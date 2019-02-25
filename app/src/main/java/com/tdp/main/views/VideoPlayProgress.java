package com.tdp.main.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tdp.main.R;

public class VideoPlayProgress extends View {
    private int width;
    private int height_mid;//垂直中间高度
    private int percent_play;//播放进度
    private int percent_buffer;//缓冲进度
    private int radius;//进度上的圆的圆心
    private int height_half;//一半的高度
    private Paint mPaint = new Paint();

    public VideoPlayProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        width = 0;
        height_mid = 0;
        percent_buffer = 0;
        percent_play = 0;
        mPaint.setAntiAlias(true);//抗锯齿
        radius = 0;
        height_half = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    //在控件大小发生改变时调用。所以这里初始化会被调用一次。作用：获取控件的宽和高度
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height_mid = h / 2;
        radius = w / 50;
        width = w-2*radius;
        height_half = h / 30;
        //Log.e("ououou","height_mid:"+height_mid+"radius:"+radius+"width:"+width+"height_half:"+height_half);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.parseColor("#bbbbbb"));
        mPaint.setStyle(Paint.Style.FILL);
        //先画总的进度条
        canvas.drawRect(radius, height_mid - height_half, width, height_mid + height_half, mPaint);
         mPaint.setColor(Color.parseColor("#9F9F9F"));
        //再画缓冲进度条
        canvas.drawRect(radius, height_mid - height_half, percent_buffer * width / 100, height_mid + height_half, mPaint);
        mPaint.setColor(Color.parseColor("#FF7E00"));
        //再画播放进度条
        canvas.drawRect(radius, height_mid - height_half, percent_play * width / 100, height_mid + height_half, mPaint);
        //最后画圆
        canvas.drawCircle(percent_play * width / 100+radius, height_mid, radius, mPaint);
    }

    //设置缓冲进度
    public void setCurrentPB(int currentPercent) {
        percent_buffer = currentPercent;
        invalidate();
    }

    //设置播放进度
    public void setCurrentPP(int currentPercent) {
        percent_play = currentPercent;
        invalidate();
    }

}
