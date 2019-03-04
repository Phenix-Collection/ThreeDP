package com.tdp.main.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.faceunity.constant.Constant;
import com.faceunity.entity.AvatarP2A;
import com.faceunity.utils.BitmapUtil;
import com.faceunity.utils.FileUtil;
import com.faceunity.utils.ToastUtil;
import com.google.gson.Gson;
import com.sdk.api.WebUcenterApi;
import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnProgressListener;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.utils.StatusBarUtil;
import com.sdk.utils.imgeloader.ImageLoadActivity;
import com.sdk.views.dialog.Toast;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.constant.CreateAvatarTypeEnum;
import com.tdp.main.controller.listener.OnCreateAvatarListener;
import com.tdp.main.controller.newmodel.CreateAvatarController;
import com.tdp.main.controller.newmodel.PhotoController;
import com.tdp.main.controller.newmodel.ReadyController;
import com.tdp.main.controller.newmodel.SelectSexController;
import com.tdp.main.utils.MiscUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sdk.core.Globals.BASE_API;

/***
 * 创建化身
 */
public class CreateAvatarActivity extends BaseActivity implements OnCreateAvatarListener {

    @BindView(R.id.content)
    RelativeLayout contentRl;

    public String filepath;
    public int step;
    public static final  String TAG = "CreateAvatarActivity";
    SelectSexController selectSexController;
    ReadyController readyController;
    PhotoController photoController;
    CreateAvatarController scanFaceController;


    // 模型生成方式（1：摄像头， 2：文件）
    private CreateAvatarTypeEnum type;

    public int sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_model);
        ButterKnife.bind(this);
        init();
    }



    /***
     * 初始化
     */
    private void init(){
        // 获取化身创建方式
        //type = getIntent().getIntExtra("type", CreateAvatarTypeEnum.FILE.getIndex());
        type = (CreateAvatarTypeEnum) getIntent().getSerializableExtra("type");

        // 选择性别
        selectSexController = new SelectSexController(this, this);
        selectSexController.show(contentRl);
    }


    /***
     * 设置创建化身的方式（1：拍照， 2：文件选择）
     * @param type
     */
    public void setType(CreateAvatarTypeEnum type) {
        this.type = type;
    }

    //选择性别
    /*public void step1() {
        if(scanFaceController!=null){
            scanFaceController.setIsCancel(true);
        }
        step = 1;
        if(selectSexController==null) {
            selectSexController = new SelectSexController(this);
        }
        selectSexController.initView(content);
    }*/

   /* //拍照引导
    public void step2() {
        step = 2;
        if(readyController==null) {
             readyController = new ReadyController(this);
        }
        readyController.initView(content);
    }*/

    //拍照
 /*   public void step3() {
        if(scanFaceController!=null){
            scanFaceController.setIsCancel(true);
        }
        step = 3;
        if(photoController==null) {
            photoController = new PhotoController(this);
        }
        photoController.initView(content);
    }*/

/*
    //扫描照片
    public void step4(String filepath) {
        step = 4;
        if(scanFaceController==null) {
            scanFaceController = new CreateAvatarController(this, filepath);
        }
        scanFaceController.initView(content);
    }
*/



/*    public void deleteTempFile(){
        //返回时删除压缩包还有临时文件
        MiscUtil.deleteFile(new File(Globals.DIR_CACHE_BUNDLE + CacheDataService.getLoginInfo().getUserInfo().getAccount()+".zip"));
        //Log.e("ououou",TAG+"删除zip包成功！");
        MiscUtil.deleteFile(new File(Globals.DIR_CACHE_BUNDLE+"temp"));
       // Log.e("ououou",TAG+"删除临时文件成功！");
        step3();
    }*/

//    @Override
//    public void onBackPressed() {
//        switch (step) {
//            case 1:
//                finish();
//                break;
//            case 2:
//                step1();
//                break;
//            case 3:
//                step2();
//                break;
//            case 4:
//                if(tag==FROM_CAMARA){
//                    step3();
//                }else{
//                    step1();
//                }
//                break;
//            case 5:
//                deleteTempFile();
//                break;
//        }
//    }

    //选文件响应函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Uri uri;
            if (data != null) {
                uri = data.getData();
                String filePath = FileUtil.getFileAbsolutePath(this, uri);
                File file = new File(filePath);
//                if (!Constant.is_debug || !createAvatarDebug(file)) {
                    if (file.exists()) {
                        Bitmap bitmap = BitmapUtil.loadBitmap(filePath, 720);
                        String dir = BitmapUtil.saveBitmap(bitmap, null);
                        onFileResult(bitmap, dir);
                        return;
                    } else {
                        ToastUtil.showCenterToast(this, "所选图片文件不存在。");
                    }
//                }
            }
        }
    }

    @Override
    public void onSexResult(int sex) {
        this.sex = sex;

        if(type == CreateAvatarTypeEnum.FILE){ // 从相册里选择照片作为化身
            Intent intent = new Intent();
            intent.setClass(this, ImageLoadActivity.class);
            this.startActivityForResult(intent, 1);
        } else { // 拍照作为化身
            if(readyController == null){
                readyController = new ReadyController(this,this);
            }
            // 准备拍照页面
            readyController.show(contentRl);
        }

    }

    @Override
    public void onTakePhotoReadyListener(boolean hasReady) {
        if(hasReady){ // 已经准备好拍照，进入拍照
            StatusBarUtil.setStatusBarColor(this, R.color.colorWhite);
            if(photoController == null){
                photoController = new PhotoController(this, this);
            }
            // 进入拍照
            photoController.show(contentRl);
        } else { // 未准备好拍照，返回到选择性别
            selectSexController.show(contentRl);
        }
    }

    @Override
    public void onFileResult(Bitmap bitmap, String dir) {

        // 拿到选择或拍照后的图片文件路径，进入扫脸步骤
        if(scanFaceController == null){
            scanFaceController = new CreateAvatarController(this, this);
        }
        scanFaceController.show(contentRl, sex, bitmap, dir);

    }


    private AvatarP2A avatarP2A;
    private String dir;

    @Override
    public void onFinished(String dir, final AvatarP2A avatarP2A) {

        this.avatarP2A = avatarP2A;
        this.dir = dir;

        try {
            // 压缩文件，并保存到当前目录上
            String zipPath = Constant.filePath + "bundle.zip";
            MiscUtil.zip(dir, zipPath);
            // 保存到服务器
            Map<String, String> map = new HashMap<>();
            map.put("file", "uploadFile");
            HttpRequest.instance().upload(BASE_API + "file/upload", new OnProgressListener() {
                @Override
                public void onProgress(long currentBytes, long contentLength) {
                    int progress = (int) (currentBytes * 100 / contentLength);
                }

                @Override
                public void onFinished(WebMsg webMsg) {
                    if (webMsg.isSuccess()) {
                        final String url = new Gson().fromJson(webMsg.getData(), String.class);
                        avatarP2A.setServer_url(url);

                        // 更新化身数据到服务器
                        saveMirrorToServer(avatarP2A);
                    } else {
                        webMsg.showMsg(CreateAvatarActivity.this);
                        onError("服务器连接失败");
                    }
                }
            }, map, new File(zipPath));
        }catch (Exception e){
            e.printStackTrace();
        }
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
                if(webMsg.isSuccess()){
                    // 替换本地化身
                    CacheDataService.saveAvatarP2A(avatarP2A);

                    // 返回主页
                    finish();
                } else {
                    Toast.show(CreateAvatarActivity.this, "替身创建失败！", android.widget.Toast.LENGTH_SHORT);
                }
            }
        });

    }



    private void onError(final String note) {

    }
}
