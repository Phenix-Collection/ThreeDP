package com.tdp.main.utils;

import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;
import java.util.HashMap;

import static android.media.MediaPlayer.SEEK_CLOSEST;

/**
 * ahtor: super_link
 * date: 2018/9/28 12:24
 * remark:
 */
public class MediaPlayerService implements MediaPlayer.OnPreparedListener, TextureView.SurfaceTextureListener, MediaPlayer.OnBufferingUpdateListener {

    //private static MediaPlayerService mediaPlayerService;
    private MediaPlayer mediaPlayer;
    private Surface surface;
    private SurfaceTexture surfaceTexture;
    private TextureView textureView;
    private int bufferProgressPercent;
    // 播放地址
    private String url;

    private int progress = 0;
    private boolean READY = false;
    private boolean mOnScreen=true;//标识，判断播放器是否在当前页面上

    public boolean ismOnScreen() {
        return mOnScreen;
    }

    public void setmOnScreen(boolean mOnScreen) {
        this.mOnScreen = mOnScreen;
    }

    public static MediaPlayerService getInstance() {
        return new MediaPlayerService();
    }

    /***
     * 播放视频
     * @param textureView
     * @param url
     */
    public void playVideo(TextureView textureView, String url, int progress) {
        this.textureView = textureView;
        this.url = url;
        this.progress = progress;
        onStop();
        playVideo();

    }

    public int getProgress() {
        return mediaPlayer!=null&&mediaPlayer.isPlaying() ? mediaPlayer.getCurrentPosition() : -1;
    }

    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : -1;
    }

    public void seekTo(int progress) {
        if (mediaPlayer != null) {
            //SEEK_CLOSEST参数是Android8.0新出的，解决seekto定位不准确的问题
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaPlayer.seekTo(progress, SEEK_CLOSEST);
            } else {
                mediaPlayer.seekTo(progress);
            }
        }
    }

    public boolean getReady() {
        return READY;
    }

    public int getBufferProgressPercent() {
        return bufferProgressPercent;
    }


    /***
     * 恢复播放
     */
    public void onResume() {
        mOnScreen=true;
        if (textureView != null && textureView.isAvailable() ) {
//            playVideo();
           // Log.e("ououou","start");
            mediaPlayer.start();
        }
    }

    private boolean isPlaying=true; // 是否正在播放，作为下一次恢复播放的依据

    /***
     * 暂停播放
     */
    public void onPause() {
        mOnScreen=false;
        if (mediaPlayer != null) {
            //isPlaying = mediaPlayer.isPlaying();
            mediaPlayer.pause();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    /***
     * 暂停播放
     */
    public synchronized void onStop() {
        if (surfaceTexture != null) {
//            surfaceTexture.release();  //停止视频的绘制线程
            surfaceTexture = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    /***
     * 播放，内部方法
     */
    private void playVideo() {
        if (textureView.isAvailable()) {
            if (mediaPlayer == null) {
                surfaceTexture = textureView.getSurfaceTexture();
                surface = new Surface(surfaceTexture);
                initMediaPlayer(url);
            }
        } else {
            textureView.setSurfaceTextureListener(this);
        }
    }

    private void initMediaPlayer(String url) {
        this.mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setLooping(true);


            Log.v("---", String.valueOf(progress) + "sss");
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        this.bufferProgressPercent = percent;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.seekTo(progress);
            if(!mOnScreen) {
               // Log.e("ououou","pause");
                mediaPlayer.pause();
            }
        }
        Log.v("---", "MediaPlayerService:onPrepared");
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        if (this.surfaceTexture == null) {
            this.surfaceTexture = surfaceTexture;
            playVideo();
        } else {
            textureView.setSurfaceTexture(this.surfaceTexture);
        }
        READY = true;
        Log.v("---", "MediaPlayerService:onSurfaceTextureAvailable");
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        // Log.v("---", "MediaPlayerService:onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        // Log.v("---", "MediaPlayerService:onSurfaceTextureDestroyed");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        // Log.v("---", "MediaPlayerService:onSurfaceTextureUpdated");
    }
}
