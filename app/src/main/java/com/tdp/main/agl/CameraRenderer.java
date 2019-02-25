package com.tdp.main.agl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.tdp.main.agl.gles.ProgramLandmarks;
import com.tdp.main.agl.gles.ProgramTexture2d;
import com.tdp.main.agl.gles.ProgramTextureOES;
import com.tdp.main.agl.gles.core.GlUtil;
import com.tdp.main.agl.utils.BitmapUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Camera相关处理
 * Camera.PreviewCallback camera数据回调
 * GLSurfaceView.Renderer GLSurfaceView相应的创建销毁与绘制回调
 * <p>
 * Created by tujh on 2018/3/2.
 */

public class CameraRenderer implements Camera.PreviewCallback, GLSurfaceView.Renderer {
    public final static String TAG = CameraRenderer.class.getSimpleName();

    private Activity mActivity;
    private GLSurfaceView mGLSurfaceView;

    public interface OnCameraRendererStatusListener {
        void onSurfaceCreated(GL10 gl, EGLConfig config);

        void onSurfaceChanged(GL10 gl, int width, int height);

        int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight);

        void onSurfaceDestroy();

        void onCameraChange(int currentCameraType, int cameraOrientation);
    }

    private OnCameraRendererStatusListener mOnCameraRendererStatusListener;

    public void setOnCameraRendererStatusListener(OnCameraRendererStatusListener onCameraRendererStatusListener) {
        mOnCameraRendererStatusListener = onCameraRendererStatusListener;
    }

    private int mViewWidth = 1280;
    private int mViewHeight = 720;

    public static final int DRAW_MODE_AVATAR = 0;
    public static final int DRAW_MODE_CAMERA = 1;
    private int drawMode = DRAW_MODE_CAMERA;
    private final Object mCameraLock = new Object();
    private Camera mCamera;
    private static final int PREVIEW_BUFFER_COUNT = 3;
    private byte[][] previewCallbackBuffer;
    private int mCameraOrientation;
    private int mCurrentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mCameraWidth = 1280;
    private int mCameraHeight = 720;

    private byte[] mCameraNV21Byte;
    private SurfaceTexture mSurfaceTexture;
    private int mCameraTextureId;

    private int mFuTextureId;
    private final float[] mtxAvatar = {0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F};
    private final float[] mtx = {0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F};
    private float[] mvp = new float[16];

    private ProgramTexture2d mFullFrameRectTexture2D;
    private ProgramTextureOES mFullFrameRectTextureOES;
    private ProgramLandmarks mProgramLandmarks;
    private boolean isShowCamera = false;
    private boolean isNeedStopDrawFrame = false;
    private boolean isNeedFocus = true;

    public CameraRenderer(Activity activity, GLSurfaceView GLSurfaceView) {
        mActivity = activity;
        mGLSurfaceView = GLSurfaceView;
    }

    public void onCreate() {
        mGLSurfaceView.onResume();
    }

    public void onDestroy() {
        final CountDownLatch count = new CountDownLatch(1);
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                onSurfaceDestroy();
                count.countDown();
            }
        });
        try {
            count.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mGLSurfaceView.onPause();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mCameraNV21Byte = data;
        mCamera.addCallbackBuffer(data);
        if (!isNeedStopDrawFrame)
            mGLSurfaceView.requestRender();
        if (!isShowCamera)
            mGLSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    isShowCamera = true;
                }
            });
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFullFrameRectTexture2D = new ProgramTexture2d();
        mFullFrameRectTextureOES = new ProgramTextureOES();
        mProgramLandmarks = new ProgramLandmarks();
        mCameraTextureId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        //cameraStartPreview();

        mOnCameraRendererStatusListener.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, mViewWidth = width, mViewHeight = height);
        mvp = GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mCameraHeight, mCameraWidth);
        mOnCameraRendererStatusListener.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mFullFrameRectTexture2D == null) return;
        if (mCameraNV21Byte == null && drawMode == DRAW_MODE_CAMERA) {
            mFullFrameRectTexture2D.drawFrame(mFuTextureId, drawMode == DRAW_MODE_CAMERA ? mtx : mtxAvatar, mvp);
            return;
        } else if (mCameraNV21Byte != null) {
            try {
                mSurfaceTexture.updateTexImage();
                mSurfaceTexture.getTransformMatrix(mtx);
            } catch (Exception e) {
                mFullFrameRectTexture2D.drawFrame(mFuTextureId, drawMode == DRAW_MODE_CAMERA ? mtx : mtxAvatar, mvp);
                return;
            }
        }

        if (!isNeedStopDrawFrame)
            mFuTextureId = mOnCameraRendererStatusListener.onDrawFrame(mCameraNV21Byte, mCameraTextureId, mCameraWidth, mCameraHeight);
        mFullFrameRectTexture2D.drawFrame(mFuTextureId, drawMode == DRAW_MODE_CAMERA ? mtx : mtxAvatar, mvp);
        if (isShowCamera && drawMode == DRAW_MODE_AVATAR) {
            mFullFrameRectTextureOES.drawFrame(mCameraTextureId, mtx, mvp, 0, mViewHeight * 2 / 3, mViewWidth / 3, mViewHeight / 3);
            mProgramLandmarks.drawFrame(0, mViewHeight * 2 / 3, mViewWidth / 3, mViewHeight / 3);
        }
        checkPic(mFuTextureId, drawMode == DRAW_MODE_CAMERA ? mtx : mtxAvatar, mCameraHeight, mCameraWidth);
        if (!isNeedStopDrawFrame)
            mGLSurfaceView.requestRender();
    }

    private void onSurfaceDestroy() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }

        if (mCameraTextureId != 0) {
            int[] textures = new int[]{mCameraTextureId};
            GLES20.glDeleteTextures(1, textures, 0);
            mCameraTextureId = 0;
        }

        if (mFullFrameRectTexture2D != null) {
            mFullFrameRectTexture2D.release();
            mFullFrameRectTexture2D = null;
        }

        if (mFullFrameRectTextureOES != null) {
            mFullFrameRectTextureOES.release();
            mFullFrameRectTextureOES = null;
        }

        if (mProgramLandmarks != null) {
            mProgramLandmarks.release();
            mProgramLandmarks = null;
        }
        mOnCameraRendererStatusListener.onSurfaceDestroy();
    }

/*    public void openCamera() {
        openCamera(mCurrentCameraType);
    }

    @SuppressWarnings("deprecation")
    public void openCamera(final int cameraType) {
        try {
            synchronized (mCameraLock) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                int cameraId = 0;
                int numCameras = Camera.getNumberOfCameras();
                for (int i = 0; i < numCameras; i++) {
                    Camera.getCameraInfo(i, info);
                    if (info.facing == cameraType) {
                        cameraId = i;
                        mCamera = Camera.open(i);
                        mCurrentCameraType = cameraType;
                        break;
                    }
                }

                mCameraOrientation = CameraUtil.getCameraOrientation(cameraId);
                CameraUtil.setCameraDisplayOrientation(mActivity, cameraId, mCamera);

                Camera.Parameters parameters = mCamera.getParameters();

                if (isNeedFocus)
                    CameraUtil.setFocusModes(parameters);

                int[] size = CameraUtil.choosePreviewSize(parameters, mCameraWidth, mCameraHeight);
                mCameraWidth = size[0];
                mCameraHeight = size[1];
                mvp = GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mCameraHeight, mCameraWidth);

                mCamera.setParameters(parameters);

                cameraStartPreview();
            }

            mOnCameraRendererStatusListener.onCameraChange(mCurrentCameraType, mCameraOrientation);
        } catch (Exception e) {
            e.printStackTrace();
            releaseCamera();
            new AlertDialog.Builder(mActivity)
                    .setTitle("警告")
                    .setMessage("相机权限被禁用或者相机被别的应用占用！")
                    .setNegativeButton("重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            openCamera(cameraType);
                        }
                    })
                    .setNeutralButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mActivity.onBackPressed();
                        }
                    })
                    .show();
        }
    }*/
/*
    private void cameraStartPreview() {
        try {
            if (mCameraTextureId == 0 || mCamera == null) {
                return;
            }
            synchronized (mCameraLock) {
                if (previewCallbackBuffer == null) {
                    previewCallbackBuffer = new byte[PREVIEW_BUFFER_COUNT][mCameraWidth * mCameraHeight * 3 / 2];
                }
                mCamera.setPreviewCallbackWithBuffer(this);
                for (int i = 0; i < PREVIEW_BUFFER_COUNT; i++)
                    mCamera.addCallbackBuffer(previewCallbackBuffer[i]);
                if (mSurfaceTexture != null) {
                    mSurfaceTexture.release();
                }
                mCamera.setPreviewTexture(mSurfaceTexture = new SurfaceTexture(mCameraTextureId));
                mCamera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void releaseCamera() {
        try {
            synchronized (mCameraLock) {
                isShowCamera = false;
                mCameraNV21Byte = null;
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.setPreviewTexture(null);
                    mCamera.setPreviewCallbackWithBuffer(null);
                    mCamera.release();
                    mCamera = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeCamera() {
        if (mCameraNV21Byte == null) {
            return;
        }
        releaseCamera();
        openCamera(mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT);
    }*/

    public int getCameraWidth() {
        return mCameraWidth;
    }

    public int getCameraHeight() {
        return mCameraHeight;
    }

    public void setDrawMode(int mode) {
        drawMode = mode;
    }

    public void setNeedStopDrawFrame(boolean needStopDrawFrame) {
        isNeedStopDrawFrame = needStopDrawFrame;
    }

    public boolean isNeedStopDrawFrame() {
        return isNeedStopDrawFrame;
    }

    public void setNeedFocus(boolean needFocus) {
        isNeedFocus = needFocus;
    }

    private boolean mTakePicing = false;
    private boolean mIsNeedTakePic = false;
    private TakePhotoCallBack mTakePhotoCallBack;

    public void takePic(TakePhotoCallBack takePhotoCallBack) {
        if (mTakePicing) {
            return;
        }
        mTakePhotoCallBack = takePhotoCallBack;
        mIsNeedTakePic = true;
        mTakePicing = true;
    }

    private void checkPic(int textureId, float[] mtx, final int texWidth, final int texHeight) {
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
        setNeedStopDrawFrame(true);
        BitmapUtil.glReadBitmap(textureId, mtx, GlUtil.IDENTITY_MATRIX, texWidth, texHeight, new BitmapUtil.OnReadBitmapListener() {
            @Override
            public void onReadBitmapListener(Bitmap bitmap) {
                if (mTakePhotoCallBack != null) {
                    mTakePhotoCallBack.takePhotoCallBack(bitmap);
                }
                mTakePicing = false;
            }
        });
    }

    public interface TakePhotoCallBack {
        void takePhotoCallBack(Bitmap bmp);
    }

    public void refreshLandmarks(float[] landmarks) {
        mProgramLandmarks.refresh(landmarks, mCameraWidth, mCameraHeight, mCameraOrientation, mCurrentCameraType);
    }
}
