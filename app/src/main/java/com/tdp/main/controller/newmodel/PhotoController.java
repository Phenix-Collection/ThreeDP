package com.tdp.main.controller.newmodel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdk.core.Globals;
import com.sdk.utils.StatusBarUtil;
import com.tdp.main.R;
import com.tdp.main.activity.NewModelActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static android.hardware.Camera.Parameters.ANTIBANDING_50HZ;
import static android.hardware.Camera.Parameters.ANTIBANDING_OFF;

public class PhotoController {
    @BindView(R.id.img_photo_back)
    ImageView imgPhotoBack;
    @BindView(R.id.img_photo_flash)
    ImageView imgPhotoFlash;
    @BindView(R.id.img_photo_flip)
    ImageView imgPhotoFlip;
    @BindView(R.id.preview_view)
    View previewView;
    @BindView(R.id.tv_photo_go)
    TextView tvPhotoGo;
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    private NewModelActivity context;
    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private Boolean ifFlash = false;
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头
    private Camera.Parameters parameters;//摄像头属性
    private int picture_w, picture_h;
    private String TAG = "PhotoController";

    public PhotoController(NewModelActivity context) {
        this.context = context;
        Log.e("ououou",context.TAG+TAG+"这里是拍照步骤！");;
    }

    public void initView(RelativeLayout group) {
        group.removeAllViews();
        View v = LayoutInflater.from(context).inflate(R.layout.item_newmodel_photo, group);
        ButterKnife.bind(this, v);
        init();
    }

    private void init() {
        StatusBarUtil.setStatusBarColor(context, R.color.colorWhite);
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.e("ououou", TAG + "surfaceCreated");
                mCamera = getCamera();
                if (mCamera != null) {
                    try {
                        setCamera();
                        //通过SurfaceView显示取景画面
                        mCamera.setPreviewDisplay(mSurfaceHolder);
                        //开始预览
                        mCamera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.e("ououou", TAG + "surfaceChanged");
                if(mCamera!=null) {
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (success) {
                                camera.cancelAutoFocus();// 只有加上了这一句，才会自动对焦
                                doAutoFocus();
                            }
                        }
                    });
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.e("ououou", TAG + "surfaceDestroyed");
                releaseCamera();
            }
        });

    }

    //得到相机
    private Camera getCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                return mCamera;

            } catch (Exception ex) {
                return null;
            }
        }
        return mCamera;
    }

    //设置相机的属性
    private void setCamera() {
        chooseSize();
        //因为是竖屏拍照，所以让预览画面旋转90度
        mCamera.setDisplayOrientation(getPreviewDegree());// 设置相机的方向
    }

    private void chooseSize() {
        parameters = mCamera.getParameters(); // 获取各项参数
        parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
        if (parameters.getAntibanding().equals(ANTIBANDING_OFF))
            parameters.setAntibanding(ANTIBANDING_50HZ);//抗菌设置设置为50hz
        Log.e("ououou", "抗菌设置:" + parameters.getAntibanding() + "自动曝光锁定的状态:" + parameters.getAutoExposureLock() + "自动白平衡锁的状态:" +
                parameters.getAutoWhiteBalanceLock() + "当前的颜色效果设置:" + parameters.getColorEffect() + "当前的曝光补偿指数:" + parameters.getExposureCompensation()
                + "当前的闪光模式设置:" + parameters.getFlashMode());
        List<Camera.Size> SupportedPreviewSizes = parameters.getSupportedPreviewSizes();// 获取支持预览照片的尺寸
        int max_width = 0;//最大宽度
        int tag = 0;//最大分辨率的位置
        for (int i = 0; i < SupportedPreviewSizes.size(); i++) {
            Camera.Size previewSize = SupportedPreviewSizes.get(i);// 从List取出Size
            if (previewSize.width * 3 / 4 == previewSize.height) {
                if (previewSize.width > max_width) {
                    max_width = previewSize.width;
                    tag = i;
                }
            }
        }
        Camera.Size previewSize = SupportedPreviewSizes.get(tag);// 从List取出Size
        parameters.setPreviewSize(previewSize.width, previewSize.height);// 设置预览的大小
        Log.e("ououou", TAG + "预览的宽高：" + previewSize.width + " " + previewSize.height);

        int width_lack = 10000;
        List<Camera.Size> SupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();
        for (int i = 0; i < SupportedPictureSizes.size(); i++) {
            Camera.Size pictureSize = SupportedPictureSizes.get(i);// 从List取出Size
            if (pictureSize.width * 3 / 4 == pictureSize.height) {
                if (pictureSize.width > max_width && (pictureSize.width - max_width) < width_lack) {//选出最靠近预览的尺寸
                    width_lack = pictureSize.width - max_width;
                    tag = i;
                }
            }
        }
        Camera.Size pictureSize = SupportedPictureSizes.get(tag);// 从List取出Size
        parameters.setPictureSize(pictureSize.width, pictureSize.height);// 设置照片的大小
        picture_h = pictureSize.height;
        picture_w = pictureSize.width;
        Log.e("ououou", TAG + "照片的宽高：" + pictureSize.width + " " + pictureSize.height);
        mCamera.setParameters(parameters);
    }

    private int getPreviewDegree() {
        int degree = 0;
        // 获得手机的方向
        int rotation = context.getWindowManager().getDefaultDisplay().getRotation();
        //Log.d(TAG, "rotation : " + rotation);
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    //释放相机
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @OnClick({R.id.img_photo_back, R.id.img_photo_flash, R.id.img_photo_flip, R.id.preview_view, R.id.tv_photo_go})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_photo_back:
                context.step2();
                break;
            case R.id.img_photo_flash:
                flash();
                break;
            case R.id.img_photo_flip:
                flip();
                break;
            case R.id.preview_view:
                if (cameraPosition == 1) {
                    Log.e("ououou", TAG + "自动对焦！");
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (success) {
                                camera.cancelAutoFocus();// 只有加上了这一句，才会自动对焦
                                doAutoFocus();
                            }
                        }
                    });
                }
                break;
            case R.id.tv_photo_go:
                takePhoto();
                break;
        }
    }

    //开关闪光灯
    public void flash() {
        Camera.Parameters parameters = mCamera.getParameters();
        if (ifFlash) {
            ifFlash = false;
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
        } else {
            if (cameraPosition == 1) {
                ifFlash = true;
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
            }

        }
    }

    //切换前后摄像头
    public void flip() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        for (int i = 0; i < cameraCount; i++) {
            // Log.i("藕？","藕？"+cameraCount);
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraPosition == 1) {
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        setCamera();
                        mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    cameraPosition = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        setCamera();
                        mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    cameraPosition = 1;
                    break;
                }
            }

        }
    }

    //自动对焦
    private void doAutoFocus() {
        parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(parameters);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    camera.cancelAutoFocus();// 只有加上了这一句，才会自动对焦。
                    if (!Build.MODEL.equals("KORIDY H30")) {
                        parameters = camera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
                        camera.setParameters(parameters);
                    } else {
                        parameters = camera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        camera.setParameters(parameters);
                    }
                }
            }
        });
    }

    //拍照函数
    private void takePhoto() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //保存
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                //后置摄像头旋转图片90度
                if (cameraPosition == 1) {
                    // Log.e("ou","摄像头"+cameraPosition);
                    matrix.setRotate(getPreviewDegree());
                    bmp = Bitmap
                            .createBitmap(bmp, 0, 0, bmp.getWidth(),
                                    bmp.getHeight(), matrix, true);
                } else {
                    //前置摄像头旋转图片270度并水平翻转
                    // Log.e("ou","摄像头"+cameraPosition);
                    bmp = turnCurrentLayer(bmp, -1, 1);
                }
                //裁剪图片
                int x = (int) (picture_h * 4.33 / 125);
                int y = (int) (picture_w * 21.33 / 223.14);
                int width = (int) (picture_h * 116.67 / 125);
                int height = picture_w * previewView.getHeight() / surfaceView.getHeight();
                //Log.e("ououou","ou?"+x+" "+y+" "+width+" "+height);
                bmp = Bitmap.createBitmap(bmp, x, y, width, height);
                //保存图片
                BufferedOutputStream bos = null;
                try {
                    File dir = new File(Globals.DIR_CACHE_BUNDLE);
                    if(!dir.exists()) dir.mkdirs();
                    String filename = "/HET_" + System.currentTimeMillis() + ".jpg";
                    String path = dir + filename;
                    bos = new BufferedOutputStream(new FileOutputStream(new File(path)));
                    //压缩图片
                    bmp.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                    context.step4(path);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (bos != null) {
                        try {
                            bos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * 旋转图片270度并水平翻转
     * 翻转bitmap (-1,1)左右翻转  (1,-1)上下翻转
     *
     * @param srcBitmap
     * @param sx
     * @param sy
     * @return
     */
    private Bitmap turnCurrentLayer(Bitmap srcBitmap, float sx, float sy) {
        Bitmap cacheBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);// 创建缓存像素的位图
        int w = cacheBitmap.getWidth();
        int h = cacheBitmap.getHeight();

        Canvas cv = new Canvas(cacheBitmap);//使用canvas在bitmap上面画像素

        Matrix mMatrix = new Matrix();//使用矩阵 完成图像变换
        mMatrix.setRotate(270);

        mMatrix.postScale(sx, sy);//重点代码，记住就ok

        Bitmap resultBimtap = Bitmap.createBitmap(srcBitmap, 0, 0, w, h, mMatrix, true);
        cv.drawBitmap(resultBimtap,
                new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight()),
                new Rect(0, 0, w, h), null);
        return resultBimtap;
    }




}
