package com.tdp.main.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tdp.main.R;

public class ScanfaceProgressView extends View {
    //扇形颜色，内圆颜色，进度条颜色，当前进度，最大进度,半径,进度的宽,内圆的宽
    private int sectorColor, innerColor, progressColor, currentProgress, maxProgress, mRadius, progressWidth, innerWidth;
    private Paint mPaint = new Paint();

    public ScanfaceProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //初始化值
        sectorColor = getResources().getColor(R.color.colorBlue);
        progressColor = getResources().getColor(R.color.colorBlue);
        innerColor = getResources().getColor(R.color.colorWhite);
        currentProgress = 0;
        maxProgress = 100;
        mPaint.setAntiAlias(true);//抗锯齿
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(200, 200);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(heightMeasureSpec, heightMeasureSpec);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
        }
    }

    //在控件大小发生改变时调用。所以这里初始化会被调用一次。作用：获取控件的宽和高度
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > h) {
            mRadius = h / 2;
        } else {
            mRadius = w / 2;
        }
        progressWidth = mRadius * 3 / 5;
        innerWidth = mRadius * 4 / 5;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(sectorColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(getWidth() / 2 - mRadius,
                getHeight() / 2 - mRadius,
                getWidth() / 2 + mRadius,
                getHeight() / 2 + mRadius,
                0,
                360,
                true,
                mPaint);//先画扇形
        mPaint.setColor(innerColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(getWidth() / 2 - innerWidth,
                getHeight() / 2 - innerWidth,
                getWidth() / 2 + innerWidth,
                getHeight() / 2 + innerWidth,
                0,
                360,
                true,
                mPaint);//再画内圆
        mPaint.setColor(progressColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(getWidth() / 2 - progressWidth,
                getHeight() / 2 - progressWidth,
                getWidth() / 2 + progressWidth,
                getHeight() / 2 + progressWidth,
                -90,
                360 * (((float) currentProgress) / maxProgress),
                true,
                mPaint);//再画进度条
    }

    public void setCurrentProgress(int currentProgress) {
        if (currentProgress <= 0) {
            currentProgress = 0;
        }
        if (currentProgress >= maxProgress) {
            currentProgress = maxProgress;
        }
        this.currentProgress = currentProgress;
        invalidate();//重绘
    }
}


