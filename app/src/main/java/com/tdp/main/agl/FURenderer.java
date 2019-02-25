package com.tdp.main.agl;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.wrapper.faceunity;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 一个基于Faceunity Nama SDK的简单封装，方便简单集成，理论上简单需求的步骤：
 * <p>
 * 1.通过OnEffectSelectedListener在UI上进行交互
 * 2.合理调用FURenderer构造函数
 * 3.对应的时机调用onSurfaceCreated和onSurfaceDestroyed
 * 4.处理图像时调用onDrawFrame
 * <p>
 * 如果您有更高级的定制需求，Nama API文档请参考http://www.faceunity.com/technical/android-api.html
 */
public class FURenderer {
    private static final String TAG = FURenderer.class.getSimpleName();

    private Context mContext;

    /**
     * 目录assets下的 *.bundle为程序的数据文件。
     * 其中 v3.bundle：人脸识别数据文件，缺少该文件会导致系统初始化失败；
     * face_beautification.bundle：美颜和美型相关的数据文件；
     * anim_model.bundle：优化表情跟踪功能所需要加载的动画数据文件；适用于使用Animoji和avatar功能的用户，如果不是，可不加载
     * ardata_ex.bundle：高精度模式的三维张量数据文件。适用于换脸功能，如果没用该功能可不加载
     * fxaa.bundle：3D绘制抗锯齿数据文件。加载后，会使得3D绘制效果更加平滑。
     * 目录effects下是我们打包签名好的道具
     */
    public static final String BUNDLE_v3 = "v3.bundle";
    public static final String BUNDLE_anim_model = "anim_model.bundle";
    public static final String BUNDLE_ardata_ex = "ardata_ex.bundle";
    public static final String BUNDLE_face_beautification = "face_beautification.bundle";
    public static final String BUNDLE_fxaa = "fxaa.bundle";
    public static final String BUNDLE_controller = "controller.bundle";
    public static final String BUNDLE_default_bg = "default_bg.bundle";

    //美颜和滤镜的默认参数
    private boolean isNeedUpdateFaceBeauty = true;
    private float mFilterLevel = 1.0f;//滤镜强度
    private String mFilterName = "origin";

    private float mSkinDetect = 1.0f;//精准磨皮
    private float mHeavyBlur = 0.0f;//美肤类型
    private float mBlurLevel = 0.0f;//磨皮
    private float mColorLevel = 0.0f;//美白
    private float mRedLevel = 0.0f;//红润
    private float mEyeBright = 0.0f;//亮眼
    private float mToothWhiten = 0.0f;//美牙

    private float mFaceShape = 4.0f;//脸型
    private float mFaceShapeLevel = 1.0f;//程度
    private float mEyeEnlarge = 0.0f;//大眼
    private float mCheekThinning = 0.0f;//瘦脸
    private float mIntensityChin = 0.5f;//下巴
    private float mIntensityForehead = 0.5f;//额头
    private float mIntensityNose = 0.0f;//瘦鼻
    private float mIntensityMouth = 0.5f;//嘴形

    private int mFrameId = 0;

    private static final int ITEM_ARRAYS_FACE_BEAUTY_INDEX = 0;
    private static final int ITEM_ARRAYS_EFFECT = 1;
    private static final int ITEM_ARRAYS_CONTROLLER = 2;
    private static final int ITEM_ARRAYS_FXAA = 3;
    private static final int ITEM_ARRAYS_COUNT = 4;
    //美颜和其他道具的handle数组
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];
    //用于和异步加载道具的线程交互
    private HandlerThread mFuItemHandlerThread;
    private Handler mFuItemHandler;

    private boolean isNeedFaceBeauty = false;

    public static final int SHOW_AVATAR_MODE_NONE = 0;
    public static final int SHOW_AVATAR_MODE_P2A = 1;
    public static final int SHOW_AVATAR_MODE_P2A_TRACK = 2;
    public static final int SHOW_AVATAR_MODE_AR = 3;
    public static final int SHOW_AVATAR_MODE_FACE = 4;
    private int showAvatarMode = SHOW_AVATAR_MODE_NONE;

    private int mInputImageOrientation = 0;
    private int mCurrentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private boolean isNeedTrackFace = false;
    private int mIsTracking = 0;
    private float[] landmarksData = new float[150];
    private float[] expressionData = new float[46];
    private float[] rotationData = new float[4];
    private float[] pupilPosData = new float[2];
    private float[] rotationModeData = new float[1];
    private float[] faceRectData = new float[4];

    private List<Runnable> mEventQueue;

    private int headItem, bodyItem, hairItem, glassItem, expressionItem, clothesItem, animItem;

    private AvatarP2A mAvatarP2A;
    private AvatarP2A mShowNowAvatarP2A;
    private String mARFilterBundle = "";

    /**
     * 全局加载相应的底层数据包
     */
    public static void initFURenderer(Context ct) {

        Context context = ct.getApplicationContext();
        try {
            //获取faceunity SDK版本信息
            Log.e(TAG, "fu sdk version " + faceunity.fuGetVersion());

            /**
             * fuSetup faceunity初始化
             * 其中 v3.bundle：人脸识别数据文件，缺少该文件会导致系统初始化失败；
             *      authpack：用于鉴权证书内存数组。若没有,请咨询support@faceunity.com
             * 首先调用完成后再调用其他FU API
             */
            InputStream v3 = context.getAssets().open(BUNDLE_v3);
            byte[] v3Data = new byte[v3.available()];
            v3.read(v3Data);
            v3.close();
            faceunity.fuSetup(v3Data, null, authpack.A());

            /**
             * 加载优化表情跟踪功能所需要加载的动画数据文件anim_model.bundle；
             * 启用该功能可以使表情系数及avatar驱动表情更加自然，减少异常表情、模型缺陷的出现。该功能对性能的影响较小。
             * 启用该功能时，通过 fuLoadAnimModel 加载动画模型数据，加载成功即可启动。该功能会影响通过fuGetFaceInfo获取的expression表情系数，以及通过表情驱动的avatar模型。
             * 适用于使用Animoji和avatar功能的用户，如果不是，可不加载
             */
/*            InputStream animModel = context.getAssets().open(BUNDLE_anim_model);
            byte[] animModelData = new byte[animModel.available()];
            animModel.read(animModelData);
            animModel.close();
            faceunity.fuLoadAnimModel(animModelData);*/

            /**
             * 加载高精度模式的三维张量数据文件ardata_ex.bundle。
             * 适用于换脸功能，如果没用该功能可不加载；如果使用了换脸功能，必须加载，否则会报错
             */
     /*       InputStream ar = context.getAssets().open(BUNDLE_ardata_ex);
            byte[] arDate = new byte[ar.available()];
            ar.read(arDate);
            ar.close();
            faceunity.fuLoadExtendedARData(arDate);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取faceunity sdk 版本库
     */
    public static String getVersion() {
        return faceunity.fuGetVersion();
    }

    /**
     * FURenderer构造函数
     */
    private FURenderer(Context context) {
        this.mContext = context.getApplicationContext();

/*        FaceBeautyModel faceBeautyModel = FaceBeautyModel.getInstance(context);

        mBlurLevel = faceBeautyModel.getFaceBeautyBlurLevel();
        mColorLevel = faceBeautyModel.getFaceBeautyColorLevel();
        mRedLevel = faceBeautyModel.getFaceBeautyRedLevel();
        mEyeEnlarge = faceBeautyModel.getFaceBeautyEnlargeEye();
        mCheekThinning = faceBeautyModel.getFaceBeautyCheekThin();*/
    }

    /**
     * 创建及初始化faceunity相应的资源
     */
    public void onSurfaceCreated() {
        Log.e(TAG, "onSurfaceCreated");
        onSurfaceDestroyed();

        mEventQueue = Collections.synchronizedList(new ArrayList<Runnable>());

        mFuItemHandlerThread = new HandlerThread("FUItemHandlerThread");
        mFuItemHandlerThread.start();
        mFuItemHandler = new FUItemHandler(mFuItemHandlerThread.getLooper());

        faceunity.fuSetExpressionCalibration(2);
        faceunity.fuSetMaxFaces(1);//设置多脸，目前最多支持8人。

        if (isNeedFaceBeauty) {
            mFuItemHandler.sendEmptyMessage(FUItemHandler.HANDLE_CREATE_BEAUTY_ITEM);
        }
        if (showAvatarMode == SHOW_AVATAR_MODE_P2A || showAvatarMode == SHOW_AVATAR_MODE_P2A_TRACK || showAvatarMode == SHOW_AVATAR_MODE_FACE) {
            loadEffect(BUNDLE_default_bg);
            mFuItemHandler.sendEmptyMessage(FUItemHandler.HANDLE_CREATE_PREPARE);
            loadAvatar(mAvatarP2A);
        } else if (showAvatarMode == SHOW_AVATAR_MODE_AR) {
            mFuItemHandler.sendEmptyMessage(FUItemHandler.HANDLE_CREATE_PREPARE);
            loadEffect(mARFilterBundle);
            loadAvatar(mAvatarP2A);
        }
        if (showAvatarMode == SHOW_AVATAR_MODE_FACE) {
            mFuItemHandler.post(new Runnable() {
                @Override
                public void run() {
                    enterFacepupMode();
                }
            });
        }
    }

    /**
     * 双输入接口(fuDualInputToTexture)(处理后的画面数据并不会回写到数组)，由于省去相应的数据拷贝性能相对最优，推荐使用。
     *
     * @param img NV21数据
     * @param tex 纹理ID
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrame(byte[] img, int tex, int w, int h) {
        if (tex <= 0 || img == null || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrame date null");
            return 0;
        }
        prepareDrawFrame();

        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuDualInputToTexture(img, tex, faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE, w, h, mFrameId++, mItemsArray);
        if (mNeedBenchmark) mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    public void trackFace(byte[] img, int w, int h) {
        if (img == null || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrameAvatar date null");
            return;
        }
        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        faceunity.fuTrackFace(img, 0, w, h);
    }

    /**
     * 使用 fuTrackFace + fuAvatarToTexture 的方法组合绘制画面，该组合没有camera画面绘制，适用于animoji等相关道具的绘制。
     * fuTrackFace 获取识别到的人脸信息
     * fuAvatarToTexture 依据人脸信息绘制道具
     *
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrameAvatar(int w, int h) {

        Arrays.fill(landmarksData, 0.0f);
        Arrays.fill(rotationData, 0.0f);
        Arrays.fill(expressionData, 0.0f);
        Arrays.fill(pupilPosData, 0.0f);
        Arrays.fill(rotationModeData, 0.0f);

        mIsTracking = faceunity.fuIsTracking();

        if (mIsTracking > 0 && isNeedTrackFace) {
            /**
             * landmarks 2D人脸特征点，返回值为75个二维坐标，长度75*2
             */
            faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
            /**
             *rotation 人脸三维旋转，返回值为旋转四元数，长度4
             */
            faceunity.fuGetFaceInfo(0, "rotation", rotationData);
            /**
             * expression  表情系数，长度46
             */
            faceunity.fuGetFaceInfo(0, "expression", expressionData);
            /**
             * pupil pos 人脸朝向，0-3分别对应手机四种朝向，长度1
             */
            faceunity.fuGetFaceInfo(0, "pupil_pos", pupilPosData);
            /**
             * rotation mode
             */
            faceunity.fuGetFaceInfo(0, "rotation_mode", rotationModeData);
        } else {
            rotationData[3] = 1.0f;
        }
        rotationModeData[0] = (360 - mInputImageOrientation) / 90;

        prepareDrawFrame();
        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        int tex = faceunity.fuAvatarToTexture(pupilPosData, expressionData, rotationData, rotationModeData,
                0, w, h, mFrameId++, mItemsArray, mIsTracking);
        if (mNeedBenchmark) mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return tex;
    }

    /**
     * 销毁faceunity相关的资源
     */
    public void onSurfaceDestroyed() {
        if (mFuItemHandlerThread != null) {
            mFuItemHandlerThread.quitSafely();
            mFuItemHandlerThread = null;
            mFuItemHandler = null;
        }
        if (showAvatarMode == SHOW_AVATAR_MODE_AR && mItemsArray[ITEM_ARRAYS_CONTROLLER] > 0) {
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "quit_ar_mode", 1);
        }
        faceunity.fuUnBindItems(mItemsArray[ITEM_ARRAYS_CONTROLLER], new int[]{headItem, bodyItem, hairItem, glassItem, expressionItem, clothesItem,animItem});
        mFrameId = 0;
        isNeedUpdateFaceBeauty = true;
        Arrays.fill(mItemsArray, 0);
        headItem = 0;
        bodyItem = 0;
        hairItem = 0;
        glassItem = 0;
        clothesItem = 0;
        expressionItem = 0;
        animItem=0;
        mShowNowAvatarP2A = null;
        faceunity.fuDestroyAllItems();
        faceunity.fuOnDeviceLost();
        if (mEventQueue != null) {
            mEventQueue.clear();
            mEventQueue = null;
        }
    }

    /**
     * 每帧处理画面时被调用
     */
    private void prepareDrawFrame() {
        //计算FPS等数据
       // benchmarkFPS();

        //获取人脸是否识别，并调用回调接口
        int isTracking = faceunity.fuIsTracking();
        if (mOnTrackingStatusChangedListener != null && mTrackingStatus != isTracking) {
            mOnTrackingStatusChangedListener.onTrackingStatusChanged(mTrackingStatus = isTracking);
        }

        //获取faceunity错误信息，并调用回调接口
        int error = faceunity.fuGetSystemError();
        if (error != 0)
            Log.e(TAG, "fuGetSystemErrorString " + faceunity.fuGetSystemErrorString(error));
        if (mOnSystemErrorListener != null && error != 0) {
            mOnSystemErrorListener.onSystemError(error == 0 ? "" : faceunity.fuGetSystemErrorString(error));
        }

        //获取是否正在表情校准，并调用回调接口
        final float[] isCalibratingTmp = new float[1];
        faceunity.fuGetFaceInfo(0, "is_calibrating", isCalibratingTmp);
        if (mOnCalibratingListener != null && isCalibratingTmp[0] != mIsCalibrating) {
            mOnCalibratingListener.OnCalibrating(mIsCalibrating = isCalibratingTmp[0]);
        }
        //Log.e("ououou","prepareDrawFrame");
        //修改美颜参数
        if (isNeedUpdateFaceBeauty && mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] != 0) {
            //Log.e("ououou","isNeedUpdateFaceBeauty");
            //filter_level 滤镜强度 范围0~1 SDK默认为 1
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "filter_level", mFilterLevel);
            //filter_name 滤镜
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "filter_name", mFilterName);

            //skin_detect 精准美肤 0:关闭 1:开启 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "skin_detect", mSkinDetect);
            //heavy_blur 美肤类型 0:清晰美肤 1:朦胧美肤 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "heavy_blur", mHeavyBlur);
            //blur_level 磨皮 范围0~6 SDK默认为 6
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "blur_level", 6 * mBlurLevel);

            //color_level 美白 范围0~1 SDK默认为 1
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "color_level", mColorLevel);
            //red_level 红润 范围0~1 SDK默认为 1
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "red_level", mRedLevel);
            //eye_bright 亮眼 范围0~1 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "eye_bright", mEyeBright);
            //tooth_whiten 美牙 范围0~1 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "tooth_whiten", mToothWhiten);


            //face_shape_level 美型程度 范围0~1 SDK默认为1
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "face_shape_level", mFaceShapeLevel);
            //face_shape 脸型 0：女神 1：网红 2：自然 3：默认 4：自定义（新版美型） SDK默认为 3
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "face_shape", mFaceShape);
            //eye_enlarging 大眼 范围0~1 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "eye_enlarging", mEyeEnlarge);
            //cheek_thinning 瘦脸 范围0~1 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "cheek_thinning", mCheekThinning);
            //intensity_chin 下巴 范围0~1 SDK默认为 0.5    大于0.5变大，小于0.5变小
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "intensity_chin", mIntensityChin);
            //intensity_forehead 额头 范围0~1 SDK默认为 0.5    大于0.5变大，小于0.5变小
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "intensity_forehead", mIntensityForehead);
            //intensity_nose 鼻子 范围0~1 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "intensity_nose", mIntensityNose);
            //intensity_mouth 嘴型 范围0~1 SDK默认为 0.5   大于0.5变大，小于0.5变小
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "intensity_mouth", mIntensityMouth);
            //isNeedUpdateFaceBeauty = false;
        }

        //queueEvent的Runnable在此处被调用
        while (mEventQueue != null && !mEventQueue.isEmpty()) {
            Runnable r = mEventQueue.remove(0);
            if (r != null)
                r.run();
        }
    }

    //--------------------------------------对外可使用的接口----------------------------------------

    /**
     * 类似GLSurfaceView的queueEvent机制
     */
    public void queueEvent(@NonNull Runnable r) {
        if (mEventQueue != null)
            mEventQueue.add(r);
    }

    /**
     * camera切换时需要调用
     *
     * @param currentCameraType     前后置摄像头ID
     * @param inputImageOrientation
     */
    public void onCameraChange(final int currentCameraType, final int inputImageOrientation) {
        if (mCurrentCameraType == currentCameraType && mInputImageOrientation == inputImageOrientation)
            return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCurrentCameraType = currentCameraType;
                mInputImageOrientation = inputImageOrientation;
                faceunity.fuOnCameraChange();
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "is3DFlipH", Camera.CameraInfo.CAMERA_FACING_BACK == mCurrentCameraType ? 1 : 0);
            }
        });
    }

    private int mDefaultOrientation;

    public void setTrackOrientation(final int rotation) {
        if (mTrackingStatus == 0 && mDefaultOrientation != rotation) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mDefaultOrientation = rotation;
                    faceunity.fuSetDefaultOrientation(rotation / 90);//设置识别人脸默认方向，能够提高首次识别的速度
                }
            });
        }
    }

    public int isTracking() {
        return faceunity.fuIsTracking();
    }

    public void setNeedTrackFace(boolean needTrackFace) {
        isNeedTrackFace = needTrackFace;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuOnCameraChange();
            }
        });
    }

    public float[] getLandmarksData() {
        Arrays.fill(landmarksData, 0.0f);
        faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
        return landmarksData;
    }

    public float[] getRotationData() {
        Arrays.fill(rotationData, 0.0f);
        faceunity.fuGetFaceInfo(0, "rotation", rotationData);
        return rotationData;
    }

    public float[] getFaceRectData() {
        Arrays.fill(faceRectData, 0.0f);
        faceunity.fuGetFaceInfo(0, "face_rect", faceRectData);
        return faceRectData;
    }

    public float[] getExpressionData() {
        Arrays.fill(expressionData, 0.0f);
        faceunity.fuGetFaceInfo(0, "expression", expressionData);
        return expressionData;
    }

    public void setRotDelta(final float rotDelta) {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "rot_delta", rotDelta);
            }
        });
    }

    public void setTranslateDelta(final float translateDelta) {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "translate_delta", translateDelta);
            }
        });
    }

    public void setScaleDelta(final float scaleDelta) {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "scale_delta", scaleDelta);
            }
        });
    }

//    public void resetAll(final int gender) {
//        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
//        queueEvent(new Runnable() {
//            @Override
//            public void run() {
//                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "target_scale", gender == 1 ? 0 : 20);
//                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "target_trans", gender == 1 ? 0 : -10);
//                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "target_angle", 0);
//                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "reset_all", 30);
//            }
//        });
//    }
//
//    public void resetAllMin(final int gender) {
//        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
//        queueEvent(new Runnable() {
//            @Override
//            public void run() {
//                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "target_scale", 140);
//                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "target_trans", gender == 1 ? 70 : 60);
//                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "target_angle", 0);
//                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "reset_all", 30);
//            }
//        });
//    }

    public void enterFacepupMode() {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "enter_facepup_mode", 1);
                showAvatarMode = SHOW_AVATAR_MODE_FACE;
            }
        });
    }

    public void quitFacepupMode() {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "quit_facepup_mode", 1);
                showAvatarMode = SHOW_AVATAR_MODE_P2A;
            }
        });
    }

    public void enterTrackMode() {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
//                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "enter_track_rotation_mode", 1);
                showAvatarMode = SHOW_AVATAR_MODE_P2A_TRACK;
            }
        });
    }

    public void quitTrackMode() {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
//                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "quit_track_rotation_mode", 1);
                showAvatarMode = SHOW_AVATAR_MODE_P2A;
            }
        });
    }


    public void setLargeEye(final float large_eye) {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "large_eye", large_eye);
            }
        });
    }

    public void setThinJaw(final float thin_jaw) {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "thin_jaw", thin_jaw);
            }
        });
    }

    public void setThinFace(final float thin_face) {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "thin_face", thin_face);
            }
        });
    }

    public void setThinNose(final float thin_nose) {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "thin_nose", thin_nose);
            }
        });
    }

    public void setLowForehead(final float low_forehead) {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "low_forehead", low_forehead);
            }
        });
    }

    public float getColorIndex() {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return 0;
        return (float) faceunity.fuItemGetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "color_index");
    }

    public void setColorIndex(final float color_index) {
        if (mItemsArray[ITEM_ARRAYS_CONTROLLER] <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "color_index", color_index);
            }
        });
    }
//--------------------------------------美颜参数与道具回调----------------------------------------

/*    @Override
    public void onFilterLevelSelected(float progress) {
        isNeedUpdateFaceBeauty = true;
        mFilterLevel = progress;
    }

    @Override
    public void onSkinDetectSelected(float isOpen) {
        isNeedUpdateFaceBeauty = true;
        mSkinDetect = isOpen;
    }

    @Override
    public void onHeavyBlurSelected(float isOpen) {
        isNeedUpdateFaceBeauty = true;
        mHeavyBlur = isOpen;
    }

    @Override
    public void onBlurLevelSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mBlurLevel = level;
    }

    @Override
    public void onColorLevelSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mColorLevel = level;
    }


    @Override
    public void onRedLevelSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mRedLevel = level;
    }

    @Override
    public void onEyeBrightSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mEyeBright = level;
    }

    @Override
    public void onToothWhitenSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mToothWhiten = level;
    }

    @Override
    public void onFaceShapeSelected(float faceShape) {
        isNeedUpdateFaceBeauty = true;
        this.mFaceShape = faceShape;
    }

    @Override
    public void onEyeEnlargeSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mEyeEnlarge = level;
    }

    @Override
    public void onCheekThinningSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mCheekThinning = level;
    }

    @Override
    public void onIntensityChinSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mIntensityChin = level;
    }

    @Override
    public void onIntensityForeheadSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mIntensityForehead = level;
    }

    @Override
    public void onIntensityNoseSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mIntensityNose = level;
    }

    @Override
    public void onIntensityMouthSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mIntensityMouth = level;
    }*/

//--------------------------------------IsTracking（人脸识别回调相关定义）----------------------------------------

    private int mTrackingStatus = 0;

    public interface OnTrackingStatusChangedListener {
        void onTrackingStatusChanged(int status);
    }

    private OnTrackingStatusChangedListener mOnTrackingStatusChangedListener;

    //--------------------------------------FaceUnitySystemError（faceunity错误信息回调相关定义）----------------------------------------

    public interface OnSystemErrorListener {
        void onSystemError(String error);
    }

    private OnSystemErrorListener mOnSystemErrorListener;

    //--------------------------------------mIsCalibrating（表情校准回调相关定义）----------------------------------------

    private float mIsCalibrating = 0;

    public interface OnCalibratingListener {
        void OnCalibrating(float isCalibrating);
    }

    private OnCalibratingListener mOnCalibratingListener;

    //--------------------------------------加载完成身体Bundle的回调----------------------------------------

    public interface OnLoadBodyListener {
        void onLoadBodyCompleteListener();
    }

    private OnLoadBodyListener mOnLoadBodyListener;

    //--------------------------------------FPS（FPS相关定义）----------------------------------------

    private static final float NANO_IN_ONE_MILLI_SECOND = 1000000.0f;
    private static final float TIME = 5f;
    private int mCurrentFrameCnt = 0;
    private long mLastOneHundredFrameTimeStamp = 0;
    private long mOneHundredFrameFUTime = 0;
    private boolean mNeedBenchmark = true;
    private long mFuCallStartTime = 0;

    private OnFUDebugListener mOnFUDebugListener;

    public interface OnFUDebugListener {
        void onFpsChange(double fps, double renderTime);
    }

    private void benchmarkFPS() {
        if (!mNeedBenchmark) return;
        if (++mCurrentFrameCnt == TIME) {
            mCurrentFrameCnt = 0;
            long tmp = System.nanoTime();
            double fps = (1000.0f * NANO_IN_ONE_MILLI_SECOND / ((tmp - mLastOneHundredFrameTimeStamp) / TIME));
            mLastOneHundredFrameTimeStamp = tmp;
            double renderTime = mOneHundredFrameFUTime / TIME / NANO_IN_ONE_MILLI_SECOND;
            mOneHundredFrameFUTime = 0;

            if (mOnFUDebugListener != null) {
                mOnFUDebugListener.onFpsChange(fps, renderTime);
            }
        }
    }

    //--------------------------------------道具（异步加载道具）----------------------------------------

    class FUItemHandler extends Handler {

        static final int HANDLE_CREATE_BEAUTY_ITEM = 1;
        static final int HANDLE_CREATE_PREPARE = 2;
        static final int HANDLE_CREATE_EFFECT = 3;
        static final int HANDLE_CREATE_AVATAR = 4;

        FUItemHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //加载美颜bundle
                case HANDLE_CREATE_BEAUTY_ITEM:
                    Log.e(TAG, "FUItemHandler HANDLE_CREATE_BEAUTY_ITEM");
                    final int itemBeauty = loadItem(BUNDLE_face_beautification);
                    queueEventItemHandler(new Runnable() {
                        @Override
                        public void run() {
                            mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] = itemBeauty;
                            isNeedUpdateFaceBeauty = true;
                        }
                    });
                    break;
                //加载CONTROLLER
                case HANDLE_CREATE_PREPARE:
                    Log.e(TAG, "FUItemHandler HANDLE_CREATE_PREPARE");
                    final int itemController = loadItem(BUNDLE_controller);
                    final int itemFXAA = loadItem(BUNDLE_fxaa);
                    queueEventItemHandler(new Runnable() {
                        @Override
                        public void run() {
                            mItemsArray[ITEM_ARRAYS_CONTROLLER] = itemController;
                            mItemsArray[ITEM_ARRAYS_FXAA] = itemFXAA;
                        }
                    });
                    break;
                //加载道具
                case HANDLE_CREATE_EFFECT:
                    Log.e(TAG, "FUItemHandler HANDLE_CREATE_EFFECT");
                    mARFilterBundle = (String) msg.obj;
                    final int itemBundle = loadItem(mARFilterBundle);
                    queueEventItemHandler(new Runnable() {
                        @Override
                        public void run() {
                            mItemsArray[ITEM_ARRAYS_EFFECT] = itemBundle;
                        }
                    });
                    break;
                case HANDLE_CREATE_AVATAR: {
                    AvatarP2A avatarP2A = (AvatarP2A) msg.obj;
                    final int controlIndex = msg.arg1;
                    if (showAvatarMode == SHOW_AVATAR_MODE_AR) {
                        int oldHeadItem = headItem;
                        int oldHairItem = hairItem;
                        headItem = mShowNowAvatarP2A != null && mShowNowAvatarP2A.getHeadFile() != null && mShowNowAvatarP2A.getHeadFile().equals(avatarP2A.getHeadFile()) ? oldHeadItem : loadItem(avatarP2A.getHeadFile());
                        hairItem = mShowNowAvatarP2A != null && mShowNowAvatarP2A.getHairFile() != null && mShowNowAvatarP2A.getHairFile().equals(avatarP2A.getHairFile()) ? oldHairItem : loadItem(avatarP2A.getHairFile());
                        avatarBindItem(controlIndex, oldHeadItem, headItem);
                        avatarBindItem(controlIndex, oldHairItem, hairItem);
                        queueEventItemHandler(new Runnable() {
                            @Override
                            public void run() {
                                faceunity.fuItemSetParam(mItemsArray[controlIndex], "enter_ar_mode", 1);
                            }
                        });
                    } else {



                        int oldHeadItem = headItem;
                        int oldBodyItem = bodyItem;
                        int oldHairItem = hairItem;
                        int oldGlassItem = glassItem;
                        int oldClothesItem = clothesItem;
                        int oldExpressionItem = expressionItem;
                        int oldAnimItem = animItem;

                        headItem = loadItem(avatarP2A.getHeadFile());
                        bodyItem = mShowNowAvatarP2A != null && mShowNowAvatarP2A.getBodyFile() != null && mShowNowAvatarP2A.getBodyFile().equals(avatarP2A.getBodyFile()) ? oldBodyItem : loadItem(avatarP2A.getBodyFile());
                        hairItem = mShowNowAvatarP2A != null && mShowNowAvatarP2A.getHairFile() != null && mShowNowAvatarP2A.getHairFile().equals(avatarP2A.getHairFile()) ? oldHairItem : loadItem(avatarP2A.getHairFile());
                        glassItem = mShowNowAvatarP2A != null && mShowNowAvatarP2A.getGlassesFile() != null && mShowNowAvatarP2A.getGlassesFile().equals(avatarP2A.getGlassesFile()) ? oldGlassItem : loadItem(avatarP2A.getGlassesFile());
                        clothesItem = mShowNowAvatarP2A != null && mShowNowAvatarP2A.getClothesFile() != null && mShowNowAvatarP2A.getClothesFile().equals(avatarP2A.getClothesFile()) ? oldClothesItem : loadItem(avatarP2A.getClothesFile());
                        expressionItem = mShowNowAvatarP2A != null && mShowNowAvatarP2A.getExpressionFile() != null && mShowNowAvatarP2A.getExpressionFile().equals(avatarP2A.getExpressionFile()) ? oldExpressionItem : loadItem(avatarP2A.getExpressionFile());
                        animItem = mShowNowAvatarP2A != null && mShowNowAvatarP2A.getAnimFile() != null && mShowNowAvatarP2A.getAnimFile().equals(avatarP2A.getAnimFile()) ? oldAnimItem : loadItem(avatarP2A.getAnimFile());

                        Log.v("ououou", String.valueOf(clothesItem) + ":::" + avatarP2A.getClothesFile());

                        avatarBindItem(controlIndex, oldHeadItem, headItem);
                        avatarBindItem(controlIndex, oldBodyItem, bodyItem);
                        avatarBindItem(controlIndex, oldHairItem, hairItem);
                        avatarBindItem(controlIndex, oldGlassItem, glassItem);
                        avatarBindItem(controlIndex, oldClothesItem, clothesItem);
                        avatarBindItem(controlIndex, oldExpressionItem, expressionItem);
                       // avatarBindItem(controlIndex, oldAnimItem ,animItem);


                        queueEventItemHandler(new Runnable() {
                            @Override
                            public void run() {
                                if (showAvatarMode == SHOW_AVATAR_MODE_P2A_TRACK)
                                    enterTrackMode();
                                if (mOnLoadBodyListener != null)
                                    mOnLoadBodyListener.onLoadBodyCompleteListener();
                            }
                        });
                    }
                    mShowNowAvatarP2A = avatarP2A.clone();
                }
                break;
            }
        }

        private void avatarBindItem(final int controlIndex, final int oldItem, final int newItem) {
            if (oldItem == newItem) return;
            queueEventItemHandler(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "controlItem " + mItemsArray[controlIndex] + " oldItem " + oldItem + " newItem " + newItem);
                    if (oldItem > 0) {
                        faceunity.fuUnBindItems(mItemsArray[controlIndex], new int[]{oldItem});
                        faceunity.fuDestroyItem(oldItem);
                    }
                    if (newItem > 0) {
                        faceunity.fuBindItems(mItemsArray[controlIndex], new int[]{newItem});
                    }
                }
            });
        }

        private int loadItem(String bundle) {
            int item = 0;
            int isDebug = 0;
            try {
                if (TextUtils.isEmpty(bundle)) {
                    item = 0;
                } else {
                    InputStream is;
                    if (bundle.startsWith(Constant.filePath)) {
                        is = new FileInputStream(new File(bundle));
                    } else {
                        File testBundle = new File(Constant.TestFilePath + bundle);
                        if (testBundle.exists()) {
                            is = new FileInputStream(testBundle);
                            Log.e(TAG, "~~~~~~~~~~~~~~~~~~使用本地测试bundle : " + Constant.TestFilePath + bundle);
                        } else {
                            is = mContext.getAssets().open(bundle);
                        }
                    }
                    byte[] itemData = new byte[is.available()];
                    int len = is.read(itemData);
                    is.close();
                    item = faceunity.fuCreateItemFromPackage(itemData);
                    isDebug = faceunity.fuCheckDebugItem(itemData);
                }
                Log.e(TAG, "loadItem " + bundle + " item " + item + " isDebug " + isDebug);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return item;
        }

        private void queueEventItemHandler(Runnable runnable) {
            if (mFuItemHandlerThread == null || Thread.currentThread().getId() != mFuItemHandlerThread.getId())
                return;
            queueEvent(runnable);
        }
    }

    public void loadEffect(String effect) {
        mFuItemHandler.removeMessages(FUItemHandler.HANDLE_CREATE_EFFECT);
        mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, FUItemHandler.HANDLE_CREATE_EFFECT, effect));
    }

    public void setAvatar(AvatarP2A avatar) {
        Log.e(TAG, "setAvatar " + avatar.toString());
        mAvatarP2A = avatar.clone();
    }

    public void loadAvatar() {
        loadAvatar(mAvatarP2A);
    }

    public void loadAvatar(AvatarP2A avatar) {
        if(avatar==null){
            return;
        }
        if(mFuItemHandler==null){
            mAvatarP2A = avatar.clone();
            return;
        }
        Log.e("ououou", "loadAvatar " + avatar.toString());
        mAvatarP2A = avatar.clone();
        mFuItemHandler.removeMessages(FUItemHandler.HANDLE_CREATE_AVATAR);
        Message message = Message.obtain(mFuItemHandler, FUItemHandler.HANDLE_CREATE_AVATAR, avatar);
        message.arg1 = ITEM_ARRAYS_CONTROLLER;
        mFuItemHandler.sendMessage(message);
    }

    public AvatarP2A getmAvatarP2A() {
        return mAvatarP2A;
    }

    //--------------------------------------Builder----------------------------------------

    /**
     * FURenderer Builder
     */
    public static class Builder {

        private Context context;
        private int inputImageRotation = 90;
        private boolean isNeedFaceBeauty = false;
        private int showAvatarMode = SHOW_AVATAR_MODE_NONE;

        private OnFUDebugListener onFUDebugListener;
        private OnTrackingStatusChangedListener onTrackingStatusChangedListener;
        private OnCalibratingListener onCalibratingListener;
        private OnSystemErrorListener onSystemErrorListener;
        private OnLoadBodyListener mOnLoadBodyListener;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public Builder inputImageOrientation(int inputImageRotation) {
            this.inputImageRotation = inputImageRotation;
            return this;
        }

        public Builder setNeedFaceBeauty(boolean needFaceBeauty) {
            isNeedFaceBeauty = needFaceBeauty;
            return this;
        }

        public Builder setNeedAvatar(int showAvatarMode) {
            this.showAvatarMode = showAvatarMode;
            return this;
        }

        public Builder setOnFUDebugListener(OnFUDebugListener onFUDebugListener) {
            this.onFUDebugListener = onFUDebugListener;
            return this;
        }

        public Builder setOnTrackingStatusChangedListener(OnTrackingStatusChangedListener onTrackingStatusChangedListener) {
            this.onTrackingStatusChangedListener = onTrackingStatusChangedListener;
            return this;
        }

        public Builder setOnCalibratingListener(OnCalibratingListener onCalibratingListener) {
            this.onCalibratingListener = onCalibratingListener;
            return this;
        }

        public Builder setOnSystemErrorListener(OnSystemErrorListener onSystemErrorListener) {
            this.onSystemErrorListener = onSystemErrorListener;
            return this;
        }

        public Builder setOnLoadBodyListener(OnLoadBodyListener onLoadBodyListener) {
            this.mOnLoadBodyListener = onLoadBodyListener;
            return this;
        }

        public FURenderer build() {
            FURenderer fuRenderer = new FURenderer(context);
            fuRenderer.mInputImageOrientation = inputImageRotation;
            fuRenderer.isNeedFaceBeauty = isNeedFaceBeauty;
            fuRenderer.showAvatarMode = showAvatarMode;

            fuRenderer.mOnFUDebugListener = onFUDebugListener;
            fuRenderer.mOnTrackingStatusChangedListener = onTrackingStatusChangedListener;
            fuRenderer.mOnCalibratingListener = onCalibratingListener;
            fuRenderer.mOnSystemErrorListener = onSystemErrorListener;
            fuRenderer.mOnLoadBodyListener = mOnLoadBodyListener;
            return fuRenderer;
        }
    }
}
