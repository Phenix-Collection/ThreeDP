package com.tdp.main.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RelativeLayout;
import com.faceunity.entity.AvatarP2A;
import com.faceunity.utils.BitmapUtil;
import com.faceunity.utils.FileUtil;
import com.faceunity.utils.ToastUtil;
import com.sdk.db.CacheDataService;
import com.sdk.utils.StatusBarUtil;
import com.sdk.utils.imgeloader.ImageLoadActivity;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.constant.CreateAvatarTypeEnum;
import com.tdp.main.controller.listener.OnCreateAvatarListener;
import com.tdp.main.controller.newmodel.CreateAvatarController;
import com.tdp.main.controller.newmodel.PhotoController;
import com.tdp.main.controller.newmodel.ReadyController;
import com.tdp.main.controller.newmodel.SelectSexController;
import com.tdp.main.service.SyncAvatarService;
import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;

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

    //选文件响应函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { // 拍照结果
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
        } else if(requestCode == 2){ // 图库选择照片结果
            String filePath = data.getStringArrayListExtra("result").get(0);
            File file = new File(filePath);
            if (file.exists()) {
                Bitmap bitmap = BitmapUtil.loadBitmap(filePath, 720);
                String dir = BitmapUtil.saveBitmap(bitmap, null);
                onFileResult(bitmap, dir);
                return;
            } else {
                ToastUtil.showCenterToast(this, "所选图片文件不存在。");
            }
        }
    }

    @Override
    public void onSexResult(int sex) {
        this.sex = sex;

        if(type == CreateAvatarTypeEnum.FILE){ // 从相册里选择照片作为化身
            Intent intent = new Intent();
            intent.setClass(this, ImageLoadActivity.class);
            this.startActivityForResult(intent, 2);
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

    @Override
    public void onFinished(final AvatarP2A avatarP2A) {

        // 保存最新化身到本地
        CacheDataService.saveAvatarP2A(avatarP2A);

        // 启动服务更新最新化身到服务器上（异步的方式）
        Intent startIntent = new Intent(this, SyncAvatarService.class);
        startIntent.putExtra("dir", avatarP2A.getBundleDir());
        startService(startIntent);

        // 退出
        finish();
    }
}
