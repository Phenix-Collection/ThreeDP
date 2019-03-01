package com.tdp.main.controller.newmodel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.faceunity.constant.AvatarConstant;
import com.faceunity.constant.Constant;
import com.faceunity.core.P2AClientWrapper;
import com.faceunity.entity.AvatarP2A;
import com.faceunity.entity.BundleRes;
import com.faceunity.utils.FileUtil;
import com.faceunity.web.CreateFailureToast;
import com.faceunity.web.OkHttpUtils;
import com.faceunity.web.ProgressRequestBody;
import com.sdk.views.Menu.TopMenuView;
import com.tdp.main.R;
import com.tdp.main.activity.CreateAvatarActivity;
import com.tdp.main.controller.listener.OnCreateAvatarListener;
import java.io.File;
import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateAvatarController {

    @BindView(R.id.iv_loading)
    ImageView loadingIv;
    @BindView(R.id.tv)
    TextView tv_note;
    private String filepath;
    private String TAG = "CreateAvatarController";
    private float poisson_weight_v = 1f;
    private float poisson_basic_mag_v = 1.5f;
    private float poisson_basic_thres_v = 0.2f;
    AnimationDrawable mLoadingAnimation;
    private boolean isCancel;

    private Activity context;
    private OnCreateAvatarListener listener;
    private String dir;
    private Bitmap bitmap;
    private int sex;

    public CreateAvatarController(Activity context, OnCreateAvatarListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void show(RelativeLayout view, int sex, Bitmap bitmap, String dir) {
        view.removeAllViews();
        View v = LayoutInflater.from(context).inflate(R.layout.item_newmodel_scanface, view);
        ButterKnife.bind(this, v);
        this.bitmap = bitmap;
        this.dir = dir;
        this.sex = sex;
        init();
    }


    private void init() {
        mLoadingAnimation = (AnimationDrawable) loadingIv.getDrawable();
        mLoadingAnimation.start();

        // 创建化身（由于SDK的性别0为男1为女，app中的男女-1刚好能对应上）
        createAvatar(dir, sex - 1, 1);
//        createAvatar(filepath, sex == );
    }

    private static final String IO = "java.io.";
    private static final String NET = "java.net";


    private long uploadDataTime;
    private long downloadDataTime;
    private long serverDataTime;
    private long headDataTime;
    private long allTime;

    /***
     * 创建化身
     * @param dir 文件路径
     * @param gender 性别
     * @param style 风格
     */
    private void createAvatar(final String dir, final int gender, final int style) {
        final long createAvatarTime = System.nanoTime();

        // 上传图片到服务器解析
        OkHttpUtils.createAvatarRequest(dir + AvatarP2A.FILE_NAME_CLIENT_DATA_ORIGIN_PHOTO, gender, style, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!call.isCanceled()) {
                    Log.e(TAG, "response onFailure " + call.toString() + "\n IOException：\n" + e.toString());
                    CreateFailureToast.onCreateFailure(context, CreateFailureToast.CreateFailureNet);
//                    mCreateAvatarDialog.dismiss();
                    mLoadingAnimation.stop();
                }
                FileUtil.deleteDirAndFile(new File(dir));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    long onResponseTime = System.nanoTime();
                    byte[] bytes = response.body().bytes();
                    long downloadTime = System.nanoTime();
                    downloadDataTime = (downloadTime - onResponseTime) / Constant.NANO_IN_ONE_MILLI_SECOND;
                    serverDataTime = (downloadTime - createAvatarTime) / Constant.NANO_IN_ONE_MILLI_SECOND;

                    // 上传解析成功后服务器返回以bytes形式内容数据，然后进行组装
                    final AvatarP2A avatarP2A = handleP2AConvert(bytes, dir, gender, style);
                    long completeTime = System.nanoTime();
                    headDataTime = (completeTime - downloadTime) / Constant.NANO_IN_ONE_MILLI_SECOND;
                    allTime = (completeTime - createAvatarTime) / Constant.NANO_IN_ONE_MILLI_SECOND;

                    // 将化身保存到数据库中
                    if (avatarP2A != null) {
                        listener.onFinished(dir, avatarP2A);


//                        mDBHelper.insertHistory(avatarP2A);
//                        context.updateAvatarP2As();
//                        context.setShowAvatarP2A(avatarP2A);
//                        mAvatarHandle.setAvatar(avatarP2A, new Runnable() {
//                            @Override
//                            public void run() {
//                                onBackPressed();
//                            }
//                        });
                        return;
                    } else {
                        CreateFailureToast.onCreateFailure(context, CreateFailureToast.CreateFailureFile);
                    }
                } else {
                    CreateFailureToast.onCreateFailure(context, response.code() == 500 ? response.body().string() : CreateFailureToast.CreateFailureNet);
                }
                FileUtil.deleteDirAndFile(new File(dir));
                //mCreateAvatarDialog.dismiss();
            }
        }, new ProgressRequestBody.UploadProgressListener() {
            @Override
            public void onUploadRequestProgress(long byteWritten, long contentLength) {
                if (byteWritten == contentLength) {
                    uploadDataTime = (System.nanoTime() - createAvatarTime) / Constant.NANO_IN_ONE_MILLI_SECOND;
                }
            }
        });
    }


    private volatile int isCreateIndex = 2;

    /***
     * 组装化身对象
     * @param objData 服务器解析后返回的内容
     * @param dir 文件路径
     * @param gender 性别
     * @param style 风格（1）
     * @return
     */
    public AvatarP2A handleP2AConvert(final byte[] objData, final String dir, final int gender, final int style) {
        try {
            final AvatarP2A avatarP2A = P2AClientWrapper.initializeAvatarP2A(dir, gender, style);
            if (isCancel) return null;
            isCreateIndex = 2;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BundleRes[] hairBundles = AvatarConstant.hairBundleRes(gender);
                    try {
                        P2AClientWrapper.initializeAvatarP2AData(objData, avatarP2A);

                        // 保存bundle
                        P2AClientWrapper.createHair(context, objData, hairBundles[avatarP2A.getHairIndex()].path, avatarP2A.getHairFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        synchronized (avatarP2A) {
                            if (--isCreateIndex == 0)
                                avatarP2A.notify();
                            else
                                avatarP2A.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isCancel) return;
                    try {
                        for (int i = 0; i < hairBundles.length; i++) {
                            String hairPath = hairBundles[i].path;
                            if (!TextUtils.isEmpty(hairPath) && i != avatarP2A.getHairIndex()) {
                                P2AClientWrapper.createHair(context, objData, hairPath, avatarP2A.getHairFileList()[i]);
                            }
                        }
                        //handleP2AConvertDebug(dir, objData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            P2AClientWrapper.createHead(objData, avatarP2A.getHeadFile());
            synchronized (avatarP2A) {
                if (--isCreateIndex == 0)
                    avatarP2A.notify();
                else
                    avatarP2A.wait();
            }
            if (isCancel) return null;
            return avatarP2A;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @OnClick(TopMenuView.CLICK_LEFT)
    public void onClick() {
        isCancel = true;
//        if (context.tag == CreateAvatarActivity.FROM_CAMARA) {
//            context.step3();
//        } else {
//            context.step1();
//        }
    }

    public void setIsCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }
}
