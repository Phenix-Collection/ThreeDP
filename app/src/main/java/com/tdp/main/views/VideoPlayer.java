package com.tdp.main.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdk.utils.StatusBarUtil;
import com.sdk.utils.SystemUtil;
import com.sdk.views.dialog.AlertDialog;
import com.sdk.views.dialog.listener.OnDialogClickListener;
import com.tdp.app.MyAppliction;
import com.tdp.main.R;
import com.tdp.main.utils.MediaPlayerService;
;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static android.content.Context.AUDIO_SERVICE;

public class VideoPlayer extends RelativeLayout {
    @BindView(R.id.textureView)
    TextureView textureView;
    @BindView(R.id.cl_top)
    ConstraintLayout clTop;
    @BindView(R.id.img_play)
    ImageView imgPlay;
    @BindView(R.id.tv_time_now)
    TextView tvTimeNow;
    @BindView(R.id.videoPlayProgress)
    VideoPlayProgress videoPlayProgress;
    @BindView(R.id.tv_time_all)
    TextView tvTimeAll;
    @BindView(R.id.img_full)
    ImageView imgFull;
    @BindView(R.id.cl_bottom)
    ConstraintLayout clBottom;
    @BindView(R.id.volume_progress)
    VolumeProgressView volumeProgress;
    @BindView(R.id.cl_volume)
    ConstraintLayout clVolume;
    @BindView(R.id.brightness_progress)
    VolumeProgressView brightnessProgress;
    @BindView(R.id.cl_brightness)
    ConstraintLayout clBrightness;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.img_video)
    ImageView imgVideo;
    private MediaPlayerService mediaPlayerService;
    RelativeLayout videoPlayContentFull;
    RelativeLayout videoPlayContentNoFull;
    private Activity activity;
    public Timer timer, timer1;//一个是视频控制器的几秒后消失的定时器，一个是进度条更新
    public TimerTask task, task1;//配合timer使用
    private int alltime;//总时间
    public String uri;
    public Boolean L_VISIBILITY = false;//下面LinearLayout的可见性，true为
    private Boolean PALYING = true;
    public Handler handler;
    public Context context;
    public boolean IFFULL = false;//是否全屏
    public float x;
    public float y;
    public int volume_percent_now, volume_percent_;//音量百分比，暂时的音量百分比
    public int brightness_percent_now, brightness_percent_;//亮度同上
    public int progress_percent_now, progress_percent_, duration;//进度同上，duration视频的总长度
    public boolean IFMOVE = false;//手指是否滑动
    public boolean CHANG_PEOGRESS = false;//是否改变了播放进度
    public boolean FIRST_MOVE = false;//第一次滑动
    public boolean FISET_SETDATA = true;//第一次初始化media player
    public boolean FISET_V_B_P = true;//第一次初始化声音、亮度、进度
    public int MOVE_TAG;//滑动的类别
    public Bitmap bitmap;
    //public MediaMetadataRetriever media;//获取视频某一帧的省略图
    // public NetReceiver netReceiver;
    public boolean ERROR = false;
    public int position = 0;
    public int times = 0;//检测播放的流畅度
    public boolean IFINNER = false;//滑动起始位置是否能用;
    public boolean IN_LIST = false;//是否在list中
    public boolean BY_CLICK = false;//是否通过点击改变全屏
    OrientationEventListener mScreenOrientationEventListener;
    private RotationObserver mRotationObserver;
    private boolean UPDATE_PROGRESS = true;
    private boolean haveAlertDialog = false;

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.video_player, this, true);
        ButterKnife.bind(this, view);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        activity = (Activity) context;
        handler = new Handler();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //解决手动全屏与感应全屏的冲突，监听屏幕的方向（手动全屏后需要调整手机90度才能继续使用感应全屏）
        mScreenOrientationEventListener = new OrientationEventListener(context) {
            @Override
            public void onOrientationChanged(int orientation) {
                //Log.e("ououou", "orientation" + orientation);
                if (IFFULL) {
                    if (80 < orientation && orientation < 100 || 260 < orientation && orientation < 280) {
                        setOrientationSensor();
                    }
                }
            }
        };
        mRotationObserver = new RotationObserver(new Handler());
        mRotationObserver.startObserver();
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
            //当前音量百分比
            volume_percent_now = (audioManager != null ? audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) : 0) * 100 / 15;
            int screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            //当前亮度百分比
            brightness_percent_now = screenBrightness * 100 / 255;
            //进度百分比
            progress_percent_now = 0;
            Log.e("ououou", "亮度百分比：" + brightness_percent_now + "音量百分比：" + volume_percent_now + "视频百分比：" + progress_percent_now);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    //更新进度条，一秒画一次
    public void updateProgress() {
        timer1 = new Timer();
        task1 = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayerService.getReady()) {
                            alltime = mediaPlayerService.getDuration();
                            tvTimeAll.setText(changeTime(alltime));
                            videoPlayProgress.setCurrentPB(mediaPlayerService.getBufferProgressPercent());//更新缓冲条
                            if (UPDATE_PROGRESS) {
                                if (position == mediaPlayerService.getProgress())
                                    times++;
                                else times = 0;
                                if (times >= 2) {
                                    //Log.e("ououou", "缓冲中！");
                                }
                                position = mediaPlayerService.getProgress();
                                videoPlayProgress.setCurrentPP(alltime == 0 ? 0 : (mediaPlayerService.getProgress() * 100 / alltime));//更新进度条
                                //Log.e("ououou", "" + position + " " + times);
                                tvTimeNow.setText(changeTime(mediaPlayerService.getProgress()));//设置时间
                            }
                        }
                    }
                });
            }
        };
        timer1.schedule(task1, 0, 1000);

    }

    //改变时间的格式,参数单位为毫秒
    public static String changeTime(int time) {
        int hour = time / 1000 / 60;
        int minute = time / 1000 % 60;
        String minute1;
        String hour1;
        if (hour >= 10) {
            hour1 = String.valueOf(hour);
        } else hour1 = "0" + hour;
        if (minute >= 10) {
            minute1 = String.valueOf(minute);
        } else minute1 = "0" + minute;
        return hour1 + ":" + minute1;
    }

    private void setOrientationSensor() {
        try {
            mScreenOrientationEventListener.disable();
            if (Settings.System.getInt(activity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 1) {//自动转屏已经开启
                Log.e("ououou", "SCREEN_ORIENTATION_SENSOR");
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                BY_CLICK = false;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    //观察屏幕旋转设置变化，类似于注册动态广播监听变化机制
    private class RotationObserver extends ContentObserver {
        ContentResolver mResolver;

        RotationObserver(Handler handler) {
            super(handler);
            mResolver = activity.getContentResolver();
        }

        //屏幕旋转设置改变时调用
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            try {
                if (Settings.System.getInt(activity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 1) {//自动转屏已经开启
                    mScreenOrientationEventListener.enable();
                } else {
                    if (IFFULL)
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    else activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }

        void startObserver() {
            mResolver.registerContentObserver(Settings.System
                            .getUriFor(Settings.System.ACCELEROMETER_ROTATION), false,
                    this);
        }

        void stopObserver() {
            mResolver.unregisterContentObserver(this);
        }
    }

    @OnClick({R.id.img_back, R.id.img_play, R.id.img_full})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                BY_CLICK = true;
                noFull();
                break;
            case R.id.img_play:
                play_stop();
                break;
            case R.id.img_full:
                BY_CLICK = true;
                if (!IFFULL) {
                    full();
                } else {
                    noFull();
                }
                break;
        }
    }

    private void play_stop() {
        if (PALYING) {
            mediaPlayerService.onPause();
            PALYING = false;
            imgPlay.setBackground(getResources().getDrawable(R.drawable.icon_play));
            UPDATE_PROGRESS = false;
        } else {
            mediaPlayerService.onResume();
            PALYING = true;
            imgPlay.setBackground(getResources().getDrawable(R.drawable.icon_stop));
            UPDATE_PROGRESS = true;
        }
    }

    private void full() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        hideBottomUIMenu();
        ViewGroup viewGroup = (ViewGroup) getParent();
        viewGroup.removeView(this);
        videoPlayContentFull.addView(this);
        videoPlayContentFull.setVisibility(VISIBLE);
        clTop.setVisibility(VISIBLE);
        tvTimeAll.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvTimeNow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        imgFull.setBackground(getResources().getDrawable(R.drawable.icon_nofull));
        IFFULL = true;
    }

    public void noFull() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        showBottomUIMenu();
        ViewGroup viewGroup = (ViewGroup) getParent();
        viewGroup.removeView(this);
        videoPlayContentNoFull.addView(this);
        videoPlayContentFull.setVisibility(GONE);
        clTop.setVisibility(GONE);
        tvTimeAll.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvTimeNow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        imgFull.setBackground(getResources().getDrawable(R.drawable.icon_full));
        IFFULL = false;
    }

    private void showBottomUIMenu() {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(activity, R.color.colorWhite);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else StatusBarUtil.setStatusBarColor(activity, R.color.colorGray);

    }

    private void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @OnTouch({R.id.textureView, R.id.videoPlayProgress})
    public boolean onTouch(View v, final MotionEvent event) {
        switch (v.getId()) {
            case R.id.textureView:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = event.getX();
                        y = event.getY();
                        IFINNER = getWidth() / 20 < x && x < getWidth() * 19 / 20 && getHeight() / 20 < y && y < getHeight() * 19 / 20;
                        // Log.e("ououou", "开始 " + x + " " + y+ getHeight() +" "+getWidth()+" "+IFINNER);
                        FIRST_MOVE = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Log.e("ououou"," "+IFINNER);
                        if (IFFULL && Math.abs(x - event.getX()) > 10 && IFINNER) {//发生了滑动
                            IFMOVE = true;
                            if (FIRST_MOVE) {//第一次移动决定了后面都做这件事件！
                                if (Math.abs(event.getY() - y) > Math.abs(event.getX() - x)) {//判断角度来判断是快进退还是。。
                                    if (event.getY() < y) {
                                        if (x > textureView.getRight() / 2) {
                                            MOVE_TAG = 1;//音量
                                        } else {
                                            MOVE_TAG = 2;//亮度
                                        }
                                    } else {
                                        if (x > textureView.getRight() / 2) {
                                            MOVE_TAG = 1;
                                        } else {
                                            MOVE_TAG = 2;
                                        }
                                    }
                                } else {
                                    if (event.getX() > x) {
                                        MOVE_TAG = 3;//播放进度
                                    }
                                }
                            }
                            if (Math.abs(event.getY() - y) > Math.abs(event.getX() - x)) {//判断角度来判断是快进退还是。。
                                if (event.getY() < y) {
                                    if (x > textureView.getRight() / 2) {
                                        if (MOVE_TAG == 1) {
                                            Log.e("ououou", "音量上升！");
                                            changeAudio(context, event);
                                        }
                                    } else {
                                        if (MOVE_TAG == 2) {
                                            Log.e("ououou", "亮度上升！");
                                            changeScreenBrightness(context, event);
                                        }
                                    }
                                } else {
                                    if (x > textureView.getRight() / 2) {
                                        if (MOVE_TAG == 1) {
                                            Log.e("ououou", "音量下降！");
                                            changeAudio(context, event);
                                        }
                                    } else {
                                        if (MOVE_TAG == 2) {
                                            Log.e("ououou", "亮度下降！");
                                            changeScreenBrightness(context, event);
                                        }
                                    }
                                }
                            } else {
                                CHANG_PEOGRESS = true;
                                if (event.getX() > x) {
                                    if (MOVE_TAG == 3) {
                                        Log.e("ououou", "快进！");
                                        changeProgress(event);
                                    }
                                } else {
                                    if (MOVE_TAG == 3) {
                                        Log.e("ououou", "快退！");
                                        changeProgress(event);
                                    }
                                }
                            }
                            FIRST_MOVE = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (IFFULL) {//滑动结束，更新数据，隐藏控件
                            volume_percent_now = volume_percent_;
                            brightness_percent_now = brightness_percent_;
                            progress_percent_now = (progress_percent_ == 100 ? 0 : progress_percent_);
                            if (CHANG_PEOGRESS) {
                                mediaPlayerService.seekTo(progress_percent_now * alltime / 100);
                                //updateProgress();
                            }
                            clVolume.setVisibility(View.GONE);
                            clBrightness.setVisibility(View.GONE);
                            tvProgress.setVisibility(View.GONE);
                        }
                        if (!IFMOVE) {//如果只是点击
                            if (!L_VISIBILITY) {//在隐藏就显示
                                if (IFFULL) clTop.setVisibility(View.VISIBLE);
                                clBottom.setVisibility(View.VISIBLE);
                                L_VISIBILITY = true;
                                //6秒后自动隐藏
                                timer = new Timer();
                                task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                clTop.setVisibility(View.GONE);
                                                clBottom.setVisibility(View.GONE);
                                                L_VISIBILITY = false;
                                            }
                                        });
                                    }
                                };
                                timer.schedule(task, 6000);
                            } else {//在显示就隐藏
                                clTop.setVisibility(View.GONE);
                                clBottom.setVisibility(View.GONE);
                                L_VISIBILITY = false;
                                if (timer != null) {
                                    timer.cancel();
                                    timer = null;
                                }
                            }
                        }
                        IFMOVE = false;
                        CHANG_PEOGRESS = false;
                        break;
                }
                break;
            case R.id.videoPlayProgress:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //mediaPlayerService.onPause();
                        getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        float x = event.getX();
                        //计算出百分比
                        progress_percent_now = (int) (x * 100 / (videoPlayProgress.getRight() - videoPlayProgress.getLeft()));
                        Log.e("ououou", "sdffff:" + videoPlayProgress.getLeft() + " " + videoPlayProgress.getRight() + " " + x + " " + progress_percent_now);
                        videoPlayProgress.setCurrentPP(progress_percent_now);
                        mediaPlayerService.seekTo(progress_percent_now * alltime / 100);
                        mediaPlayerService.onResume();
                        //updateProgress();
                        getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x1 = event.getX();
                        int progress1 = (int) (x1 * 100 / (videoPlayProgress.getRight() - videoPlayProgress.getLeft()));
                        progress1 = progress1 < 0 ? 0 : (progress1 > 100 ? 100 : progress1);
                        Log.e("ououou", "sdf:" + videoPlayProgress.getLeft() + " " + videoPlayProgress.getRight() + " " + x1 + " " + progress1);
                        videoPlayProgress.setCurrentPP(progress1);
                        tvTimeNow.setText(changeTime(progress1 * alltime / 100));
                        break;
                }
                break;
        }
        return true;
    }

    //加减音量
    public void changeAudio(Context context, MotionEvent event) {
        clVolume.setVisibility(View.VISIBLE);
        //根据滑动的距离改变音量百分比
        volume_percent_ = volume_percent_now + (int) ((y - event.getY()) * 200 / textureView.getBottom());
        if (volume_percent_ > 100) volume_percent_ = 100;
        if (volume_percent_ < 0) volume_percent_ = 0;
        volumeProgress.setCurrentPercent(volume_percent_);
        //同时改变系统音量，需要在activity中请求权限！
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume_percent_ * 15 / 100, AudioManager.FLAG_PLAY_SOUND);
        }
    }

    //改变亮度
    public void changeScreenBrightness(Context context, MotionEvent event) {
        //动态申请系统权限,改变亮度需要用到！
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !haveAlertDialog) {
            if (!Settings.System.canWrite(context)) {
                haveAlertDialog = true;
                AlertDialog.getInstance(context).setMessage(AlertDialog.TIP, "改变亮度需要修改系统设置权限，是否去设置权限？").setButton("去", "关闭").setTitle("权限申请！").setOnDialogClickListener(new OnDialogClickListener() {
                    @Override
                    public void click(int index, Dialog view) {
                        switch (index) {
                            case AlertDialog.ACTION_OK:
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                        Uri.parse("package:" + activity.getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                activity.startActivityForResult(intent, 1);
                                haveAlertDialog = false;
                                break;
                            case AlertDialog.ACTION_CANCEL:
                                haveAlertDialog = false;
                                break;
                        }
                    }
                }).show();

            }
        }
        clBrightness.setVisibility(View.VISIBLE);
        //根据滑动的距离改变亮度百分比
        brightness_percent_ = brightness_percent_now + (int) ((y - event.getY()) * 200 / textureView.getBottom());
        if (brightness_percent_ > 100) brightness_percent_ = 100;
        if (brightness_percent_ < 0) brightness_percent_ = 0;
        brightnessProgress.setCurrentPercent(brightness_percent_);
        //同时改变系统亮度，需要在activity中请求权限！
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness_percent_ * 255 / 100);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    //改变进度
    public void changeProgress(final MotionEvent event) {
        tvProgress.setVisibility(View.VISIBLE);
        //根据滑动的距离改变播放进度
        progress_percent_ = progress_percent_now + (int) ((event.getX() - x) * 200 / textureView.getRight());
        if (progress_percent_ > 100) progress_percent_ = 100;
        if (progress_percent_ < 0) progress_percent_ = 0;
        //获取滑动位置的省略图
        // img_progress.setImageBitmap(media.getFrameAtTime(progress_percent_ * 1000 * player.getDuration() / 100, MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
        tvProgress.setText(changeTime(progress_percent_ * alltime / 100));
        Log.e("ououou", "进度：" + changeTime(progress_percent_ * alltime / 100));
    }

    public void onBackKeyDown() {
        if (IFFULL) {
            noFull();
        } else {
            activity.finish();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        Log.e("ououou", "onConfigurationChanged:" + newConfig.orientation + " " + BY_CLICK);
        if (!BY_CLICK) {
            if (newConfig.orientation == 1) {//竖屏
                noFull();
            } else {//横屏
                full();
            }
        } else {
            mScreenOrientationEventListener.enable();
        }
    }


    public void setData(MediaPlayerService mediaPlayerService, RelativeLayout videoPlayContentFull, RelativeLayout videoPlayContentNoFull) {
        this.mediaPlayerService = mediaPlayerService;
        this.videoPlayContentFull = videoPlayContentFull;
        this.videoPlayContentNoFull = videoPlayContentNoFull;
        updateProgress();
    }

    public TextureView getTextureView() {
        return textureView;
    }

    public ImageView getImgVideo() {
        return imgVideo;
    }

    public void release() {
        if (task1 != null) {
            task1.cancel();
            task1 = null;
        }
        if (timer1 != null) {
            timer1.cancel();
            timer1 = null;
        }
        mRotationObserver.stopObserver();
    }
}
