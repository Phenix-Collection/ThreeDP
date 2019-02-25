package com.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.opengl.GLSurfaceView;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MEDIA_PROJECTION_SERVICE;
import static com.superrtc.mediamanager.ScreenCaptureManager.RECORD_REQUEST_CODE;

/**
 * ahtor: super_link
 * date: 2018/9/29 17:23
 * remark: 屏幕录制工具类
 */
public class ScreenRecorderUtil {


    GLSurfaceView glSurfaceView;
    private Activity context;
    private MediaProjectionManager projectionManager;
    private MediaRecorder mediaRecorder;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private int REQUEST_CODE;

    public ScreenRecorderUtil(Activity context) {
        this.context = context;
        projectionManager = (MediaProjectionManager) context.getSystemService(MEDIA_PROJECTION_SERVICE);
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
//            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
//        }
//    }

    private void init() {
        Intent captureIntent = projectionManager.createScreenCaptureIntent();
        context.startActivityForResult(captureIntent, REQUEST_CODE);
    }

    private void createVirtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay(
                "MainScreen",
                100,
                100,
                100,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                glSurfaceView.getHolder().getSurface(),
                null, null);
    }

    private void initRecorder() {
        File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".mp4");
//        mediaRecorder
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(file.getAbsolutePath());
        mediaRecorder.setVideoSize(100, 100);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder.setVideoFrameRate(30);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean running;

    public boolean startRecord() {
        if (mediaProjection == null || running) {
            return false;
        }
        initRecorder();
        createVirtualDisplay();
        mediaRecorder.start();
        running = true;
        return true;
    }

}
