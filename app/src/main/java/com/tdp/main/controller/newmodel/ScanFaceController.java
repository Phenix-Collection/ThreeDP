package com.tdp.main.controller.newmodel;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.faceunity.FUAvatarGen;
import com.faceunity.P2ACallback;
import com.faceunity.p2a.FUP2AClient;
import com.faceunity.zip4j.core.ZipFile;
import com.faceunity.zip4j.exception.ZipException;
import com.google.gson.Gson;
import com.sdk.api.WebUcenterApi;
import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.api.entity.MirrorEntity;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnProgressListener;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.views.Menu.TopMenuView;
import com.tdp.main.R;
import com.tdp.main.activity.MainActivity;
import com.tdp.main.activity.NewModelActivity;
import com.tdp.main.activity.ShareActivity;
import com.tdp.main.agl.AvatarConstant;
import com.tdp.main.agl.AvatarP2A;
import com.tdp.main.agl.utils.FileUtil;
import com.tdp.main.agl.web.OkHttpUtils;
import com.tdp.main.utils.MiscUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.Window.PROGRESS_END;
import static com.sdk.core.Globals.BASE_API;
import static com.tdp.main.agl.Constant.filePath;

public class ScanFaceController {
    @BindView(R.id.img)
    ImageView img_anim;
    @BindView(R.id.tv)
    TextView tv_note;
    private NewModelActivity context;
    private String filepath;
    private String TAG = "ScanFaceController";
    private float poisson_weight_v = 1f;
    private float poisson_basic_mag_v = 1.5f;
    private float poisson_basic_thres_v = 0.2f;
    AnimationDrawable mLoadingAnimation;
    private boolean isCancel;

    public ScanFaceController(NewModelActivity context, String filepath) {
        this.context = context;
        this.filepath = filepath;
        isCancel = false;
        Log.e("ououou", context.TAG + TAG + "这里是扫描照片步骤！" + " 文件路径：" + filepath);
    }

    public void initView(RelativeLayout group) {
        group.removeAllViews();
        View v = LayoutInflater.from(context).inflate(R.layout.item_newmodel_scanface, group);
        ButterKnife.bind(this, v);
        //init();
        init();
    }


    private void init() {
        mLoadingAnimation = (AnimationDrawable) img_anim.getDrawable();
        mLoadingAnimation.start();
        createAvatar(filepath, context.sex == 1 ? 0 : 1);
    }

    private static final String IO = "java.io.";
    private static final String NET = "java.net";

    private void createAvatar(final String dir, final int gender) {
        String paramJson = String.format("{\"is_deform\": %d,\"is_q\": %d,\"poissonWeight\": %f,\"poissonBasicMag\": %f,\"poissonBasicThres\": %f}", 0, 0, poisson_weight_v, poisson_basic_mag_v, poisson_basic_thres_v);
        Log.e("ououou", "扫描文件中");
        File file = new File(dir);
        if (file.exists()) {
            OkHttpUtils.createAvatarRequest(dir, gender, false, paramJson, new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    if (!call.isCanceled()) {
                        switch (e.toString().substring(0, 8)) {
                            case IO:
                                onError("面部识别失败，请重新尝试");
                                break;
                            case NET:
                                onError("服务器繁忙，请稍后再试");
                                break;
                            default:
                                Log.e("ououou", TAG + "response onFailure " + e);
                        }
                        //Toast.makeText(context, "面部识别失败，请重新尝试", Toast.LENGTH_SHORT).show();
                        //onFailureCreate(dir);
                    }
                    //FileUtil.deleteDirAndFile(new File(dir));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("ououou", "扫描文件成功");
                    if (response.isSuccessful() && !isCancel) {
                        byte[] bytes = response.body().bytes();
                        saveAndUploadFile(bytes, gender);
                    }
                }
            });
        } else {
            mLoadingAnimation.stop();
            tv_note.setText("照片不存在！");
        }
    }

    private void onError(final String note) {
        mLoadingAnimation.stop();
        OkHttpUtils.cancelAll();
        // FileUtil.deleteDirAndFile(new File(Globals.DIR_CACHE_BUNDLE + "temp"));
        Log.e("ououou", TAG + "删除临时文件成功！");
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_note.setText(note);
            }
        });
    }

/*    private void saveAndUploadFile(byte[] objData, int gender) {
        try {
            //if (isCancel) return null;
            final String finalFilePathPrefix = CacheDataService.getLoginInfo().getUserInfo().getAccount();//设置保存的文件名
            AvatarP2A avatarP2A = new AvatarP2A(Globals.DIR_CACHE_BUNDLE + finalFilePathPrefix + "/", gender);
            FileUtil.createFile(Globals.DIR_CACHE_BUNDLE + finalFilePathPrefix);
            FileUtil.saveDataToFile(Globals.DIR_CACHE_BUNDLE + finalFilePathPrefix + "/server.bundle", objData);
            // if (isCancel) return null;
            byte[] head = FUP2AClient.getFUP2AClient().createAvatarHeadWithData(objData);
            // if (isCancel) return null;
            FileUtil.saveDataToFile(avatarP2A.getHeadFile(), head);
            String[] hairBundles = AvatarConstant.hairBundle(gender);
            String[] hairPaths = new String[hairBundles.length];

            for (int i = 0; i < hairBundles.length; i++) {
                //if (isCancel) return null;
                String hairPath = hairBundles[i];
                if (!TextUtils.isEmpty(hairPath)) {
                    InputStream hairIS = context.getAssets().open(hairPath);
                    byte[] hairData = new byte[hairIS.available()];
                    hairIS.read(hairData);
                    hairIS.close();
                    byte[] hair = FUP2AClient.getFUP2AClient().createAvatarHairWithHeadData(head, hairData);
                    //if (isCancel) return null;
                    FileUtil.saveDataToFile(hairPaths[i] = (Globals.DIR_CACHE_BUNDLE + finalFilePathPrefix + "/" + hairPath), hair);
                } else {
                    hairPaths[i] = "";
                }
            }
            avatarP2A.setHairFileList(hairPaths);
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(MainActivity.TAG, MainActivity.FROM_NEWMODEL);
            context.startActivity(intent);

            // if (isCancel) return null;
    *//*        String[] hairBundles = AvatarConstant.hairBundle(gender);
            String[] hairPaths = new String[hairBundles.length];

            for (int i = 0; i < hairBundles.length; i++) {
                //if (isCancel) return null;
                String hairPath = hairBundles[i];
                if (!TextUtils.isEmpty(hairPath)) {
                    InputStream hairIS = getAssets().open(hairPath);
                    byte[] hairData = new byte[hairIS.available()];
                    hairIS.read(hairData);
                    hairIS.close();
                    byte[] hair = FUP2AClient.getFUP2AClient().createAvatarHairWithHeadData(head, hairData);
                    if (isCancel) return null;
                    FileUtil.saveDataToFile(hairPaths[i] = (dir + hairPath), hair);
                } else {
                    hairPaths[i] = "";
                }
            }*//*
            //avatarP2A.setHairFileList(hairPaths);
            //if (isCancel) return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    private void saveAndUploadFile(byte[] objData, int gender) {
        try {
            if (isCancel) return;
            //保存文件到本地
            final String finalFilePathPrefix = CacheDataService.getLoginInfo().getUserInfo().getAccount() + "_" + CacheDataService.getLoginInfo().getLoginTime();//设置保存的文件名
            FileUtil.createFile(Globals.DIR_CACHE_BUNDLE + "temp/" + finalFilePathPrefix);
            FileUtil.saveDataToFile(Globals.DIR_CACHE_BUNDLE + "temp/" + finalFilePathPrefix + "/server.bundle", objData);
            if (isCancel) return;
            byte[] head = FUP2AClient.getFUP2AClient().createAvatarHeadWithData(objData);
            if (isCancel) return;
            FileUtil.saveDataToFile(Globals.DIR_CACHE_BUNDLE + "temp/" + finalFilePathPrefix + "/q_final.bundle", head);
            //压缩本地文件上传服务器
            MiscUtil.zip(Globals.DIR_CACHE_BUNDLE + "temp/" + finalFilePathPrefix, Globals.DIR_CACHE_BUNDLE + "temp" + finalFilePathPrefix + ".zip");
            Map<String, String> map = new HashMap<>();
            map.put("file", "uploadFile");
            HttpRequest.instance().upload(BASE_API + "file/upload", new OnProgressListener() {
                @Override
                public void onProgress(long currentBytes, long contentLength) {
                    int progress = (int) (currentBytes * 100 / contentLength);
                    Log.e("ououou", "上传进度" + progress);
                }

                @Override
                public void onFinished(WebMsg webMsg) {
                    Log.e("ououou", new Gson().toJson(webMsg));
                    if (webMsg.isSuccess() && !isCancel) {
                        Log.e("ououou", TAG + "上传成功,更新模型数据！");
                        final String url = new Gson().fromJson(webMsg.getData(), String.class);
                        final MirrorEntity mirror = CacheDataService.getLoginInfo().getUserInfo().getMirror();
                        // final MirrorEntity mirror =new MirrorEntity();
                       /* Log.e("ououou","mirror:"+mirror.toString());
                        Log.e("ououou",url+" "+mirror.getName()+" "+String.valueOf(context.sex)+" "+String.valueOf(mirror.getSkinColor())
                                +" "+mirror.getCloth()+" "+mirror.getGlass()+" "+ mirror.getHats()+" "+mirror.getCosplay()+" ");*/
                        final String cloth;
                        if (context.sex != mirror.getSex())
                            cloth = "0";
                        else cloth = mirror.getCloth();
                        HttpRequest.instance().doPost(HttpRequest.create(WebUcenterApi.class).editMirror(
                                url,
                                mirror.getName(),
                                String.valueOf(context.sex),
                                String.valueOf(mirror.getSkinColor()),
                                cloth,
                                mirror.getGlass(),
                                mirror.getHats(),
                                mirror.getCosplay()
                        ), new OnResultListener() {
                            @Override
                            public void onWebUiResult(WebMsg webMsg) {
                                Log.e("ououou", new Gson().toJson(webMsg));
                                if (webMsg.isSuccess() && !isCancel) {
                                    Log.e("ououou", TAG + "更新成功保存到本地！");
                                    LoginInfoEntity loginInfoEntity1 = CacheDataService.getLoginInfo();
                                    MirrorEntity mirror = loginInfoEntity1.getUserInfo().getMirror();
                                    mirror.setUrl(url);
                                    mirror.setSex(context.sex);
                                    mirror.setCloth(cloth);
                                    loginInfoEntity1.getUserInfo().setMirror(mirror);
                                    CacheDataService.saveLoginInfo(loginInfoEntity1);
                                    MiscUtil.deleteFile(new File(Globals.DIR_CACHE_BUNDLE + "temp" + finalFilePathPrefix + ".zip"));
                                    Log.e("ououou", TAG + "上传完成删除zip成功！");
                                    MiscUtil.copyFolder(filePath + "temp/" + finalFilePathPrefix, filePath + finalFilePathPrefix);
                                    Log.e("ououou", TAG + "复制文件成功！");
                                    MiscUtil.deleteFile(new File(filePath + "temp"));
                                    Log.e("ououou", TAG + "删除临时文件成功！");
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.putExtra(MainActivity.TAG, MainActivity.FROM_NEWMODEL);
                                    if (!isCancel)
                                        context.startActivity(intent);
                                } else {
                                    webMsg.showMsg(context);
                                    onError("服务器连接失败");
                                }
                            }
                        });

                    } else {
                        webMsg.showMsg(context);
                        onError("服务器连接失败");
                    }
                }
            }, map, new File(Globals.DIR_CACHE_BUNDLE + "temp" + finalFilePathPrefix + ".zip"));
            // if (isCancel) return null;
    /*        String[] hairBundles = AvatarConstant.hairBundle(gender);
            String[] hairPaths = new String[hairBundles.length];

            for (int i = 0; i < hairBundles.length; i++) {
                //if (isCancel) return null;
                String hairPath = hairBundles[i];
                if (!TextUtils.isEmpty(hairPath)) {
                    InputStream hairIS = getAssets().open(hairPath);
                    byte[] hairData = new byte[hairIS.available()];
                    hairIS.read(hairData);
                    hairIS.close();
                    byte[] hair = FUP2AClient.getFUP2AClient().createAvatarHairWithHeadData(head, hairData);
                    if (isCancel) return null;
                    FileUtil.saveDataToFile(hairPaths[i] = (dir + hairPath), hair);
                } else {
                    hairPaths[i] = "";
                }
            }*/
            //avatarP2A.setHairFileList(hairPaths);
            //if (isCancel) return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(TopMenuView.CLICK_LEFT)
    public void onClick() {
        isCancel = true;
        if (context.tag == NewModelActivity.FROM_CAMARA) {
            context.step3();
        } else {
            context.step1();
        }
    }

    public void setIsCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }
}
