package com.tdp.main.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.faceunity.constant.Constant;
import com.faceunity.entity.AvatarP2A;
import com.google.gson.Gson;
import com.sdk.api.WebUcenterApi;
import com.sdk.db.BaseDataService;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnProgressListener;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.tdp.main.utils.MiscUtil;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import static com.sdk.core.Globals.BASE_API;

/**
 * ahtor: super_link
 * date: 2019/3/10 10:48
 * remark:
 */
public class SyncAvatarService extends Service {

    public static final String TAG = "SyncAvatarService";

    /** 化身目录 */
    private String asyncDir;

    private boolean isBusying = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String dir = intent.getStringExtra("dir");

        // 获取任务栈中的任务目录
        String asyncDir = BaseDataService.getValueByString("Async-Bundle-Cache");


        Log.v("ououou", "isBusying && dir.equals(asyncDir) = " + String.valueOf(isBusying && dir.equals(asyncDir)));
        // 如果重复提交任务，将撤销这次操作
        if(isBusying && dir.equals(asyncDir)) return super.onStartCommand(intent, flags, startId);


        File file = new File(dir);

        Log.v("ououou", "file.isDirectory() = " + file.isDirectory());
        if(file.isDirectory()){
            /** 更新待同步的缓存文件夹，这样可以保持下次检测有最新需要同步的文件 */
            BaseDataService.saveValueToSharePerference("Async-Bundle-Cache", asyncDir);
        }

        Log.v("ououou", "isBusying = " + isBusying);
        if(isBusying) return super.onStartCommand(intent, flags, startId);

        /** 暂存当前处理的目录路径 */
        this.asyncDir = dir;

        /** 标记状态为忙碌中 */
        isBusying = true;

        new AsyncTask<String, String, String>(){
            @Override
            protected String doInBackground(String... voids) {
                // 压缩文件,并得到文件地址
                String zipPath = zipDir(dir);
                return zipPath;
            }

            @Override
            protected void onPostExecute(String zipPath) {
                // 上传zip文件到服务器
                if(zipPath != null){
                    zipToServer(zipPath);
                } else {
                    isBusying = false;
                }
                super.onPostExecute(zipPath);
            }
        }.execute();

        return super.onStartCommand(intent, flags, startId);
    }

    /***
     * 压缩目录
     * @param dir 目录名称
     * @return 压缩文件的路径
     */
    private String zipDir(String dir){
        // 压缩文件，并保存到当前目录上
        String zipPath = Constant.filePath + "bundle.zip";
        try {
            // 休眠3秒， 因为太快会导致文件夹读取的数量不对

            File file = null;
            while (true){
                file = new File(dir);
                File[] entries = file.listFiles();

                Log.v("ououou", "entries.length = " + entries.length);
                if(entries.length == 16){
                    break;
                }
                Thread.sleep(2000);
            }

            MiscUtil.zip(dir, zipPath);
            return zipPath;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 上传zip文件到服务器上
     * @param zipPath zip文件路径
     */
    private void zipToServer(final String zipPath){

        final AvatarP2A avatarP2A = CacheDataService.getAvatarP2A();

        // 保存到服务器
        Map<String, String> map = new HashMap<>();
        map.put("file", "uploadFile");
        HttpRequest.instance().upload(BASE_API + "file/upload", new OnProgressListener() {
            @Override
            public void onProgress(long currentBytes, long contentLength) {
                int progress = (int) (currentBytes * 100 / contentLength);

                Log.v("ououou", String.valueOf(progress));
            }

            @Override
            public void onFinished(WebMsg webMsg) {
                if (webMsg.isSuccess()) {
                    final String url = new Gson().fromJson(webMsg.getData(), String.class);
                    avatarP2A.setServer_url(url);

                    // 更新化身数据到服务器
                    saveMirrorToServer(avatarP2A);
                } else {
                    isBusying = false;
                    webMsg.showMsg(SyncAvatarService.this);

                    Log.v("ououou", new Gson().toJson(webMsg));
                }
            }
        }, map, new File(zipPath));
    }

    /***
     * 保存化身数据到服务器
     * @param avatarP2A
     */
    private void saveMirrorToServer(final AvatarP2A avatarP2A){

        final String avatarP2sStr = new Gson().toJson(avatarP2A);
        //
        HttpRequest.instance().doPost(HttpRequest.create(WebUcenterApi.class).editMirror(avatarP2sStr), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {

                isBusying = false;
                if(webMsg.isSuccess()){

                    String dir = BaseDataService.getValueByString("Async-Bundle-Cache");

                    // 判断当前同步后的数据是否最新的，如果是，则删除任务，防止下次重复同步
                    if(SyncAvatarService.this.asyncDir.equals(dir)){
                        BaseDataService.remove("Async-Bundle-Cache");
                    }

                    // 关闭服务
                    stopSelf();
                } else {
//                    Toast.show(SyncAvatarService.this, "数据同步失败！", android.widget.Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
