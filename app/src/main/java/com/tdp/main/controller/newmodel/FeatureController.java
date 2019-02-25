package com.tdp.main.controller.newmodel;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.sdk.views.Menu.TopMenuView;
import com.sdk.views.dialog.Loading;
import com.tdp.main.R;
import com.tdp.main.activity.NewModelActivity;
import com.tdp.main.utils.MiscUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeatureController {
    @BindView(R.id.img_feature_feature)
    ImageView imgFeatureFeature;
    @BindView(R.id.tv_register_go)
    TextView tvRegisterGo;
    private NewModelActivity context;
    private String filepath;
    private String TAG="FeatureCotroller";
    private boolean one_time=true;

    public FeatureController(NewModelActivity context,String filepath) {
        this.filepath=filepath;
        this.context = context;
        Log.e("ououou",context.TAG+TAG+"这里是查看特征图片并确定步骤！");;
    }

    public void initView(RelativeLayout group) {
        group.removeAllViews();
        View v = LayoutInflater.from(context).inflate(R.layout.item_newmodel_feature, group);
        ButterKnife.bind(this, v);
        init();
    }

    private void init() {
        Bitmap bitmap= MiscUtil.getBitmapFromPath(filepath);
        imgFeatureFeature.setImageBitmap(bitmap);
    }

    @OnClick({TopMenuView.CLICK_LEFT, R.id.tv_register_go})
    public void onClick(View view) {
        switch (view.getId()) {
            case TopMenuView.CLICK_LEFT:
                context.deleteTempFile();
                break;
            case R.id.tv_register_go:
                uploadFile();
                break;
        }
    }

    private void uploadFile(){
        Loading.start(context, "模型生成中...", true);
        Log.e("ououou",TAG+"上传zip包函数");
        final String filename=CacheDataService.getLoginInfo().getUserInfo().getAccount()+".zip";
        File file = new File(Globals.DIR_CACHE_BUNDLE + filename);
        Map<String,String> map=new HashMap<>();
        map.put("file","regispackage");
/*        HttpRequest.instance().upload("updatepackage",new OnProgressListener() {
            @Override
            public void onProgress(long currentBytes, long contentLength, boolean isDone) {
                 if(isDone&&one_time){
                     one_time=false;
                     Log.e("ououou",TAG+"上传成功！");
                     //上传后删除压缩包
                     MiscUtil.deleteFile(new File(filePath +filename ));
                     Log.e("ououou",TAG+"删除zip包成功！");
                     //复制文件
                     MiscUtil.copyFolder(filePath+"temp/"+CacheDataService.getLoginInfo().getUserInfo().getAccount() ,filePath+ CacheDataService.getLoginInfo().getUserInfo().getAccount());
                     Log.e("ououou",TAG+"复制文件成功！");
                     MiscUtil.deleteFile(new File(filePath+"temp"));
                     Log.e("ououou",TAG+"删除临时文件成功！");
                     Loading.stop();
                     LoginInfoEntity loginInfo=CacheDataService.getLoginInfo();
                     loginInfo.getUserInfo().setSex(1);
                     CacheDataService.saveLoginInfo(loginInfo);
                     Intent intent = new Intent(context, MainActivity.class);
                     intent.putExtra("need_update_home",true);
                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                     context.startActivity(intent);
                 }
            }
        },map,file);*/
    }
}
