package com.tdp.main.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.agl.utils.FileUtil;
import com.tdp.main.agl.web.OkHttpUtils;
import com.tdp.main.controller.newmodel.FeatureController;
import com.tdp.main.controller.newmodel.PhotoController;
import com.tdp.main.controller.newmodel.ReadyController;
import com.tdp.main.controller.newmodel.ScanFaceController;
import com.tdp.main.controller.newmodel.SelectSexController;
import com.tdp.main.utils.MiscUtil;

import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewModelActivity extends BaseActivity {
    @BindView(R.id.content)
    RelativeLayout content;
    public int sex;
    public String filepath;
    public int step,tag;
    public static final  String TAG="NewModelActivity";
    public static final int FROM_CAMARA=1;
    public static final int FROM_FILE=2;
    SelectSexController selectSexController;
    ReadyController readyController;
    PhotoController photoController;
    ScanFaceController scanFaceController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_model);
        ButterKnife.bind(this);
        tag=getIntent().getIntExtra(TAG,0);
        //Log.e("ououou",TAG+"这里是更新模型页面！");
        step1();
    }

    //选择性别
    public void step1() {
        if(scanFaceController!=null){
            scanFaceController.setIsCancel(true);
        }
        step = 1;
        if(selectSexController==null) {
            selectSexController = new SelectSexController(this);
        }
        selectSexController.initView(content);
    }

    //拍照引导
    public void step2() {
        step = 2;
        if(readyController==null) {
             readyController = new ReadyController(this);
        }
        readyController.initView(content);
    }

    //拍照
    public void step3() {
        if(scanFaceController!=null){
            scanFaceController.setIsCancel(true);
        }
        step = 3;
        if(photoController==null) {
            photoController = new PhotoController(this);
        }
        photoController.initView(content);
    }

    //扫描照片
    public void step4(String filepath) {
        step = 4;
        if(scanFaceController==null) {
            scanFaceController = new ScanFaceController(this, filepath);
        }
        scanFaceController.initView(content);
    }

    //确认特征
/*    public void step5(String filepath) {
        step = 5;
        FeatureController featureController = new FeatureController(this, filepath);
        featureController.initView(content);
    }*/


    //选文件响应函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                Log.v("NewModelActivity","onActivityResult:result:"+data.getStringArrayListExtra("result").get(0));
                step4(data.getStringArrayListExtra("result").get(0));
            }
        }
    }

    public void deleteTempFile(){
        //返回时删除压缩包还有临时文件
        MiscUtil.deleteFile(new File(Globals.DIR_CACHE_BUNDLE + CacheDataService.getLoginInfo().getUserInfo().getAccount()+".zip"));
        //Log.e("ououou",TAG+"删除zip包成功！");
        MiscUtil.deleteFile(new File(Globals.DIR_CACHE_BUNDLE+"temp"));
       // Log.e("ououou",TAG+"删除临时文件成功！");
        step3();
    }

    @Override
    public void onBackPressed() {
        switch (step) {
            case 1:
                finish();
                break;
            case 2:
                step1();
                break;
            case 3:
                step2();
                break;
            case 4:
                if(tag==FROM_CAMARA){
                    step3();
                }else{
                    step1();
                }
                break;
            case 5:
                deleteTempFile();
                break;
        }
    }
}
