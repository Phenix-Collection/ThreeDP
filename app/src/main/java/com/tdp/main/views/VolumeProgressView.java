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
import android.util.Log;
import android.view.View;

import com.tdp.main.R;

public class VolumeProgressView extends View {
    private Paint mPaint = new Paint();
    private int width,height,percent;

    public VolumeProgressView(Context context, @Nullable AttributeSet attrs) {
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
        width = w;
        height = h;
        Log.e("ououou","s"+width+" "+height+" "+oldw+" "+oldh);
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.progress_full);
        // 指定图片绘制区域(左上角的四分之一)
        Rect src = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        // 指定图片在屏幕上显示的区域
        Rect dst = new Rect(0,0,width,height);
        // 绘制图片
        canvas.drawBitmap(bitmap,src,dst,null);
        bitmap=BitmapFactory.decodeResource(getContext().getResources(), R.drawable.progress_);
        src=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        dst = new Rect(width*percent/100,0,width,height);
        canvas.drawBitmap(bitmap,src,dst,null);
    }

    //设置进度
    public void setCurrentPercent(int currentPercent) {
        percent = currentPercent;
        invalidate();
    }

    public int getCurrentPercent(){
        return percent;
    }
}

