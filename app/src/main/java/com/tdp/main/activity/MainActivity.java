package com.tdp.main.activity;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.faceunity.constant.Constant;
import com.faceunity.core.AvatarHandle;
import com.faceunity.core.FUP2ARenderer;
import com.faceunity.core.P2ACore;
import com.faceunity.entity.AvatarP2A;
import com.faceunity.renderer.CameraRenderer;
import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnProgressListener;
import com.sdk.net.msg.WebMsg;
import com.tdp.base.BaseFragmentActivity;
import com.tdp.main.R;
import com.tdp.main.fragment.HomeVideoFragment;
import com.tdp.main.fragment.HomeFragment;
import com.tdp.main.fragment.HomeFriendFragment;
import com.tdp.main.fragment.HomeUCenterFragment;
import com.tdp.main.utils.MiscUtil;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jmessage.support.google.gson.Gson;

/**
 * @author:zlcai
 * @createrDate:2017/9/2 10:35
 * @lastTime:2017/9/2 10:35
 * @detail: 主Activity, 所有功能的入口
 **/

public class MainActivity extends BaseFragmentActivity implements CameraRenderer.OnCameraRendererStatusListener, View.OnClickListener {

    private List<Fragment> fragments = new ArrayList<>();
    private CreateAvatarActivity newModelFragment;
    @BindViews({R.id.id_home_icon, R.id.id_reportwage_icon, R.id.id_what1_icon, R.id.id_ucenter_icon})
    List<ImageView> menuIcons;
    @BindViews({R.id.id_home_label, R.id.id_reportwage_label, R.id.id_what1_label, R.id.id_ucenter_label})
    List<TextView> menuLabels;
    private int menuNormalIcons[] = {R.drawable.icon_home_n, R.drawable.icon_video_n, R.drawable.icon_interactive_n, R.drawable.icon_my_n};
    private int menuFocusIcons[] = {R.drawable.icon_home_f, R.drawable.icon_video_f, R.drawable.icon_interactive_f, R.drawable.icon_my_f};
    @BindView(R.id.id_content)
    FrameLayout contentFl; // 内容

    public static final int FROM_FIGURE = 1;
    public static final int FROM_NEWMODEL = 2;
    public static final int FROM_LOGIN = 3;
    public static final int FROM_CHANGUSER = 4;

    private boolean ISEXIT = false;
    private Handler handler;
    public static final String TAG = "MainActivity";

    //
    @BindView(R.id.main_gl_surface)
    GLSurfaceView mainGlSurfaceView;
    @BindView(R.id.loading_v)
    TextView loadingV;

    //
    private CameraRenderer mCameraRenderer;
    private FUP2ARenderer mFUP2ARenderer;
    private P2ACore mP2ACore;
    private AvatarHandle mAvatarHandle;
//    private List<AvatarP2A> mAvatarP2As;
    private AvatarP2A mShowAvatarP2A;
    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkLogin();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
    }

    /***
     * 检查是否登录
     */
    private void checkLogin(){
        LoginInfoEntity data = CacheDataService.getLoginInfo();
        if (data != null) {
              init();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private int touchMode = 0;
    private void init() {

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ISEXIT = false;
            }
        };
        // UI
        // ====== 初始化五个功能页面
        fragments.clear();
        fragments.add(new HomeFragment());
        fragments.add(new HomeVideoFragment());
        fragments.add(new HomeFriendFragment());
        fragments.add(new HomeUCenterFragment());

        newModelFragment = new CreateAvatarActivity();

        // ====== 初始化功能图标
        showFragment(0 );

        //
        mainGlSurfaceView.setEGLContextClientVersion(3);
        mCameraRenderer = new CameraRenderer(this, mainGlSurfaceView);
        mCameraRenderer.setOnCameraRendererStatusListener(this);
        mainGlSurfaceView.setRenderer(mCameraRenderer);
        mainGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        // 初始化数据
//        mAvatarP2As = getAllAvatarP2As();
//        mShowAvatarP2A = CacheDataService.getAvatarP2A();
//        mDBHelper = new DBHelper(this);
//        mAvatarP2As = mDBHelper.getAllAvatarP2As();
//        mShowAvatarP2A = mAvatarP2As.get(mShowIndex = 0);

        mFUP2ARenderer = new FUP2ARenderer(this);
        mP2ACore = new P2ACore(this, mFUP2ARenderer);
        mFUP2ARenderer.setFUCore(mP2ACore);
        mAvatarHandle = mP2ACore.createAvatarHandle();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int screenWidth = metrics.widthPixels;
        final int screenHeight = metrics.heightPixels;
        mGestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (showIndex == 0) {
//                    return fragments.get(0).onSingleTapUp(e);
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (touchMode != 1) {
                    touchMode = 1;
                    return false;
                }
                float rotDelta = -distanceX / screenWidth;
                float translateDelta = distanceY / screenHeight;
                mAvatarHandle.setRotDelta(rotDelta);
                mAvatarHandle.setTranslateDelta(translateDelta);
                return distanceX != 0 || translateDelta != 0;
            }
        });

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (touchMode != 2) {
                    touchMode = 2;
                    return false;
                }
                float scale = detector.getScaleFactor() - 1;
                mAvatarHandle.setScaleDelta(scale);
                return scale != 0;
            }
        });

        loadAvatarP2A();
    }

    /***
     * 加载化身数据
     */
    private void loadAvatarP2A(){

        // 获取AvatarP2A对象
        mShowAvatarP2A = CacheDataService.getAvatarP2A();

        Log.v("ououou", new Gson().toJson(mShowAvatarP2A));

        // 判断化身对象中是否配置，如果有是否已下载。
        String bundleDir = mShowAvatarP2A.getBundleDir();
        String url = mShowAvatarP2A.getServer_url();
        if(bundleDir != null && url != null){
            File file = new File(bundleDir);
            if(file.exists()){ // 判断Bundle是否存在，如果不存在说明没有下载
                setMShowAvatarP2A();
            } else { // 本地文件不存在，尝试从网上下载
                //loadingV.setText("正在准备同步形象！");
                setMShowAvatarP2A();

                // 同步bundle到本地
               // downloadAvatarP2A(Globals.BASE_API + url, bundleDir);

            }
        } else { // 当Bundle目录为空时，说明用户还没有配置过
            setMShowAvatarP2A();
        }

    }

    /***
     * 下载化身数据到本地
     */
    private void downloadAvatarP2A(String url, final String bundleDir){

        loadingV.setText("正在同步信息到本地，请稍后...");
        final String zipPath = Constant.filePath + System.currentTimeMillis() + ".zip";

        Log.v("ououou", url);
        HttpRequest.instance().download(url, this, zipPath, new OnProgressListener() {
            @Override
            public void onProgress(long currentBytes, long contentLength) {
                int progress = (int) (currentBytes * 100 / contentLength);
                loadingV.setText("信息同步中，已完成" + + progress + "%");
                //tvHomeDownloading.setText("准备中 " + progress + "%");
            }

            @Override
            public void onFinished(WebMsg webMsg) {
                //tvHomeDownloading.setVisibility(View.GONE);
                if (webMsg.isSuccess()) {
                    //tvError.setVisibility(View.GONE);
                    try {
//                        ZipFile zipFile = new ZipFile(zipPath);//ZipFile是用来解压文件的工具
//                        zipFile.extractAll(bundleDir);//解压所有的文件
//                        MiscUtil.deleteFile(new File(zipPath));
                        loadingV.setText("同步完成，正在准备展示形象。");
                        MiscUtil.unzip(zipPath, bundleDir);
//                        // 创建化身服务
//                        createAvatarService();
                        setMShowAvatarP2A();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //tvError.setVisibility(View.VISIBLE);
                    webMsg.showMsg(MainActivity.this);
                    loadingV.setText("同步失败，将展示默认形象。");
                    setMShowAvatarP2A();
                }
            }
        });
    }

    /***
     * 设置并显示化身数据
     */
    private void setMShowAvatarP2A(){

        mAvatarHandle.setAvatar(getShowAvatarP2A(), new Runnable() {
            @Override
            public void run() {
                if (loadingV.getVisibility() == View.VISIBLE) {
                    loadingV.post(new Runnable() {
                        @Override
                        public void run() {
                            loadingV.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loadAvatarP2A();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (showIndex == 0) {
            if (event.getPointerCount() == 2) {
                mScaleGestureDetector.onTouchEvent(event);
            } else if (event.getPointerCount() == 1)
                mGestureDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @OnClick({R.id.id_home, R.id.ll_video, R.id.ll_friend, R.id.id_ucenter})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_home:
                showFragment(0);
                break;
            case R.id.ll_video:
                showFragment(1);
                break;
            case R.id.ll_friend:
                showFragment(2);
                break;
            case R.id.id_ucenter:
                showFragment(3);
                break;
        }
    }

    private Integer showIndex;
    private Fragment showFragment;

    /***
     * 展示界面
     * @param index 界面下标
     */
    public void showFragment(int index){
        // 当前已经展示该界面，无需重复展示
        if(showFragment != null && showIndex == index){
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment tempFramgnet = fragments.get(index);

        if(!tempFramgnet.isAdded()){
            transaction.add(contentFl.getId(), tempFramgnet);
        }
        if(showFragment != null){
            transaction.hide(showFragment);
        }

        // 还原未选中菜单按钮状态
        if(showIndex != null){
            menuLabels.get(showIndex).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryBlack, null));
            menuIcons.get(showIndex).setImageResource(menuNormalIcons[showIndex]);
        }
        // 改变选中菜单按钮状态
        menuLabels.get(index).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, null));
        menuIcons.get(index).setImageResource(menuFocusIcons[index]);

        // 展示当前选中页面并提交
        transaction.show(tempFramgnet).commitAllowingStateLoss();
        showFragment = tempFramgnet;
        showIndex = index;
    }



    //重写返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!ISEXIT) {
            ISEXIT = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息,2秒之内再按一次退出程序
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            super.exitApp();
        }
    }

    public AvatarP2A getShowAvatarP2A() {
        return mShowAvatarP2A;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFUP2ARenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight) {
        mCameraRenderer.refreshLandmarks(mP2ACore.getLandmarksData());
        return mFUP2ARenderer.onDrawFrame(cameraNV21Byte, cameraTextureId, cameraWidth, cameraHeight);
    }

    @Override
    public void onSurfaceDestroy() {
        mFUP2ARenderer.onSurfaceDestroyed();
    }

    @Override
    public void onCameraChange(int currentCameraType, int cameraOrientation) {
        mFUP2ARenderer.onCameraChange(currentCameraType, cameraOrientation);
    }

    /***
     * 放大人偶
     * @param isMin
     */
    public void setGLSurfaceViewSize(boolean isMin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainGlSurfaceView.getLayoutParams();
        params.width = isMin ? getResources().getDimensionPixelSize(R.dimen.x208) : RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = isMin ? getResources().getDimensionPixelSize(R.dimen.x290) : RelativeLayout.LayoutParams.MATCH_PARENT;
        params.topMargin = isMin ? getResources().getDimensionPixelSize(R.dimen.x158) : 0;
        mainGlSurfaceView.setLayoutParams(params);
        //mGroupPhotoRound.setVisibility(isMin ? View.VISIBLE : View.GONE);
    }
}
