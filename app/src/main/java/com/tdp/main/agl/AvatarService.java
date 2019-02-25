package com.tdp.main.agl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;

import com.faceunity.p2a.FUP2AClient;
import com.sdk.api.entity.MirrorEntity;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.tdp.main.agl.web.OkHttpUtils;

import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AvatarService {
    private FURenderer mFURenderer;
    private CameraRenderer mCameraRenderer;

    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private int touchMode = 0;
    private AvatarP2A nowAvatarP2A;
    private boolean hadLoad=false;

    @SuppressLint("ClickableViewAccessibility")
    public AvatarService(final GLSurfaceView mGLSurfaceView, final Context context, FURenderer.OnLoadBodyListener onLoadBodyListener) {
        mGLSurfaceView.setEGLContextClientVersion(2);
        mCameraRenderer = new CameraRenderer((Activity) context, mGLSurfaceView);
        mCameraRenderer.setDrawMode(CameraRenderer.DRAW_MODE_AVATAR);
        mGLSurfaceView.setRenderer(mCameraRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
       // mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        //Log.e("ououou", "onComplete");
        mFURenderer = new FURenderer
                .Builder(context)
                .setNeedFaceBeauty(false)
                .setOnLoadBodyListener(onLoadBodyListener)
                .setNeedAvatar(FURenderer.SHOW_AVATAR_MODE_P2A)
                .build();
        //设置模型bundle数据
       setAvatar();

        mCameraRenderer.setOnCameraRendererStatusListener(new CameraRenderer.OnCameraRendererStatusListener() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                //Log.e("ououou", "onSurfaceCreated");
                mFURenderer.onSurfaceCreated();
            }

            @Override
            public void onSurfaceChanged(GL10 gl, final int width, int height) {
            }

            @Override
            public int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight) {
                //mHomeFragment.setFPSText(FPSUtil.fps());
                return mFURenderer.onDrawFrameAvatar(cameraWidth, cameraHeight);
            }

            @Override
            public void onSurfaceDestroy() {
                mFURenderer.onSurfaceDestroyed();
            }

            @Override
            public void onCameraChange(int currentCameraType, int cameraOrientation) {
                //mFURenderer.onCameraChange(currentCameraType, cameraOrientation);
            }
        });

        //手势控制
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final int screenWidth = metrics.widthPixels;
        final int screenHeight = metrics.heightPixels;
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //Log.e("ououou","onSingleTapUp");
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if(hadLoad){
                    if (touchMode != 1) {
                        touchMode = 1;
                        return false;
                    }
                    float rotDelta = -distanceX / screenWidth;
                    float translateDelta = distanceY / screenHeight;
                    mFURenderer.setRotDelta(rotDelta);
                    mFURenderer.setTranslateDelta(translateDelta);
                    return distanceX != 0 || translateDelta != 0;
                }else return false;
                //Log.e("ououou","onScroll");
            }
        });
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if(hadLoad) {
                    // Log.e("ououou","onScale");
                    if (touchMode != 2) {
                        touchMode = 2;
                        return false;
                    }
                    float scale = (detector.getScaleFactor() - 1) * 2;
                    //Log.e("ououou", "scale" + scale);
                    mFURenderer.setScaleDelta(scale);
                    return scale != 0;
                }else return false;
            }
        });

        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(hadLoad) {
                    if (event.getPointerCount() == 2) {
                        mScaleGestureDetector.onTouchEvent(event);
                    } else if (event.getPointerCount() == 1) {
                        mGestureDetector.onTouchEvent(event);
                    }
                    return true;
                }else return false;
            }
        });
    }

    public CameraRenderer getmCameraRenderer() {
        return mCameraRenderer;
    }

    public void setAvatar() {
        String url=CacheDataService.getLoginInfo().getUserInfo().getMirror().getUrl();
        if (url!=null&&url.length()!=0){
            String finalFilePathPrefix = CacheDataService.getLoginInfo().getUserInfo().getAccount()+"_"+CacheDataService.getLoginInfo().getLoginTime();//设置保存的文件名
            MirrorEntity mirrorEntity=CacheDataService.getLoginInfo().getUserInfo().getMirror();
            int sex =mirrorEntity.getSex();
            int clothesIndex=Integer.valueOf(mirrorEntity.getCloth().equals("")?"0":mirrorEntity.getCloth());
            nowAvatarP2A = new AvatarP2A(Globals.DIR_CACHE_BUNDLE + finalFilePathPrefix + "/", sex,clothesIndex);
            mFURenderer.setAvatar(nowAvatarP2A);
        }else{
            MirrorEntity mirrorEntity=CacheDataService.getLoginInfo().getUserInfo().getMirror();
            int clothesIndex=Integer.valueOf(mirrorEntity.getCloth());
            nowAvatarP2A = new AvatarP2A(clothesIndex);
            mFURenderer.setAvatar(nowAvatarP2A);
        }
    }

    public void loadAvatar() {
        String url=CacheDataService.getLoginInfo().getUserInfo().getMirror().getUrl();
        if (url!=null&&url.length()!=0){
            String finalFilePathPrefix = CacheDataService.getLoginInfo().getUserInfo().getAccount()+"_"+CacheDataService.getLoginInfo().getLoginTime();//设置保存的文件名
            MirrorEntity mirrorEntity=CacheDataService.getLoginInfo().getUserInfo().getMirror();
            int sex =mirrorEntity.getSex();
            int clothesIndex=Integer.valueOf(mirrorEntity.getCloth().equals("")?"0":mirrorEntity.getCloth());
            nowAvatarP2A = new AvatarP2A(Globals.DIR_CACHE_BUNDLE + finalFilePathPrefix + "/", sex,clothesIndex);
            mFURenderer.loadAvatar(nowAvatarP2A);
        }else{
            MirrorEntity mirrorEntity=CacheDataService.getLoginInfo().getUserInfo().getMirror();
            int clothesIndex=Integer.valueOf(mirrorEntity.getCloth());
            nowAvatarP2A = new AvatarP2A(clothesIndex);
            mFURenderer.loadAvatar(nowAvatarP2A);
        }
    }

    public void reLoadAvatar(){
        mFURenderer.loadAvatar(nowAvatarP2A);
    }

    public AvatarP2A getNowAvatarP2A() {
        return nowAvatarP2A;
    }

    public void setHadLoad(boolean hadLoad) {
        this.hadLoad = hadLoad;
    }
}
