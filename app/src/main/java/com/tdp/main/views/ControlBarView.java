package com.tdp.main.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tdp.main.R;


public class ControlBarView extends View {
    private Paint mPaint = new Paint();
    private int w_controlbar_start,w_controlbar_end,w_control_start,w_control_end,h_controlbar,h_control_start,percent,height_useful,height_control;
    //棒子x开始，x结束，y开始，y结束，棒子总高度，滑块x开始，百分比，可用的高度，滑块的高度
    public ControlBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        percent=0;
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
        w_controlbar_start=w*13/28;
        w_controlbar_end = w*15/28;
        w_control_start=w*3/14;
        w_control_end=w*11/14;
        h_controlbar = h;
        h_control_start=h/20;
        height_control=h/20;
        height_useful=h*17/20;
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icon_039);
        // 指定图片绘制区域(左上角的四分之一)
        Rect src = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        // 指定图片在屏幕上显示的区域
        Rect dst = new Rect(w_controlbar_start,0,w_controlbar_end,h_controlbar);
        // 绘制图片
        canvas.drawBitmap(bitmap,src,dst,null);//先画棒子
        bitmap=BitmapFactory.decodeResource(getContext().getResources(),R.drawable.icon_038);
        src=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        int h_control_sta=(h_control_start+height_useful*percent/100)>height_useful+h_control_start?height_useful:(h_control_start+height_useful*percent/100);//计算当前开始的高度
        dst = new Rect(w_control_start,h_control_sta,w_control_end,h_control_sta+height_control);
        //再画滑块
        canvas.drawBitmap(bitmap,src,dst,null);
    }

    //设置进度
    public void setCurrentPercent(int currentPercent) {
        percent = currentPercent;
        invalidate();
    }

}
