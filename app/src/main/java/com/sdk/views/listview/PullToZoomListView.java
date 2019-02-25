package com.sdk.views.listview;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import com.sdk.utils.DensityUtil;
import com.sdk.views.listview.pulllistview.Pullable;


public class PullToZoomListView extends ListView implements AbsListView.OnScrollListener,View.OnTouchListener, Pullable {

    private boolean isLoad = false;
    private static final Interpolator sInterpolator = new Interpolator() {//Interpolator 被用来修饰动画效果，定义动画的变化率，可以使存在的动画效果accelerated(加速)，decelerated(减速),repeated(重复),bounced(弹跳)等
        public float getInterpolation(float paramAnonymousFloat) {
            float f = paramAnonymousFloat - 1.0F;
            return 1.0F + f * (f * (f * (f * f)));
        }
    };
    int mActivePointerId = -1;
    private FrameLayout mHeaderContainer;
    private int mHeaderHeight;

    public int getmHeaderHeight() {
        return mHeaderHeight;
    }

    /**
     * 设置头部的高度
     *
     * @param mHeaderHeight
     */
    public void setmHeaderHeight(int mHeaderHeight) {
        this.mHeaderHeight = mHeaderHeight;
        LayoutParams lp = new LayoutParams(DensityUtil.dp2px(mContext, LayoutParams.MATCH_PARENT), mHeaderHeight);
        getHeaderContainer().setLayoutParams(lp);
    }

    private ImageView mHeaderImage;
    float mLastMotionY = -1.0F;
    float mLastScale = -1.0F;
    float mMaxScale = -1.0F;
    private OnScrollListener mOnScrollListener;
    private ScalingRunnalable mScalingRunnalable;
    private int mScreenHeight;
    private ImageView mShadow;
    
    private Context mContext;

    public PullToZoomListView(Context paramContext) {
        super(paramContext);
        init(paramContext);
        mContext = paramContext;
    }

    public PullToZoomListView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext);
        mContext = paramContext;
    }

    public PullToZoomListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext);
        mContext = paramContext;
    }

    private void endScraling() {
        if (this.mHeaderContainer.getBottom() >= this.mHeaderHeight)
        this.mScalingRunnalable.startAnimation(100L);
    }

    private void init(Context paramContext) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();//Displaymetrics 是取得手机屏幕大小的关键类
        try{
            ((Activity) paramContext).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        }catch(Exception e){}
        this.mScreenHeight = localDisplayMetrics.heightPixels;
        this.mHeaderContainer = new FrameLayout(paramContext);

        this.mHeaderImage = new ImageView(paramContext);
        mHeaderImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int i = localDisplayMetrics.widthPixels;
        setHeaderViewSize(i, (int) (9.0F * (i / 16.0F)));
        this.mShadow = new ImageView(paramContext);
        FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(-1, -2);
        localLayoutParams.gravity = Gravity.CENTER;
        this.mShadow.setLayoutParams(localLayoutParams);
        this.mHeaderContainer.addView(this.mHeaderImage);
        this.mHeaderContainer.addView(this.mShadow);
        this.mScalingRunnalable = new ScalingRunnalable();
        super.setOnScrollListener(this);
        this.setOnTouchListener(this);

    }

    private void onSecondaryPointerUp(MotionEvent paramMotionEvent) {
        int i = (paramMotionEvent.getAction()) >> 8;
        if (paramMotionEvent.getPointerId(i) == this.mActivePointerId)
            if (i != 0) {
                this.mLastMotionY = paramMotionEvent.getY(0);
                this.mActivePointerId = paramMotionEvent.getPointerId(0);
                return;
            }
    }

    private void reset() {
        this.mActivePointerId = -1;
        this.mLastMotionY = -1.0F;
        this.mMaxScale = -1.0F;
        this.mLastScale = -1.0F;
    }

    public ImageView getHeaderView() {
        return this.mHeaderImage;
    }

    public FrameLayout getHeaderContainer() {
        return mHeaderContainer;
    }

    public void setHeaderView() {
        addHeaderView(this.mHeaderContainer);
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        if (this.mHeaderHeight == 0)
            this.mHeaderHeight = this.mHeaderContainer.getHeight();
    }

    @Override
    public void onScroll(AbsListView paramAbsListView, int paramInt1, int paramInt2, int paramInt3) {

        float f = this.mHeaderHeight - this.mHeaderContainer.getBottom();
        if ((f > 0.0F) && (f < this.mHeaderHeight)) {
            int i = (int) (0.65D * f);
            this.mHeaderImage.scrollTo(0, -i);
        } else if (this.mHeaderImage.getScrollY() != 0) {
            this.mHeaderImage.scrollTo(0, 0);
        }
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScroll(paramAbsListView, paramInt1, paramInt2, paramInt3);
        }
    }

    public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScrollStateChanged(paramAbsListView, paramInt);
        }
    }

    /***
     * 设置是否下拉刷新
     * @param hasRefresh
     */
    public void setRefresh(boolean hasRefresh){
        if(hasRefresh){
            setHeaderView();
        }
    }


    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        switch (0xFF & paramMotionEvent.getAction()) {
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_DOWN:
                if (!this.mScalingRunnalable.mIsFinished) {
                    this.mScalingRunnalable.abortAnimation();
                }
                this.mLastMotionY = paramMotionEvent.getY();
                this.mActivePointerId = paramMotionEvent.getPointerId(0);
                this.mMaxScale = (this.mScreenHeight / this.mHeaderHeight);
                this.mLastScale = (this.mHeaderContainer.getBottom() / this.mHeaderHeight);
                break;
            case MotionEvent.ACTION_MOVE:
                int j = paramMotionEvent.findPointerIndex(this.mActivePointerId);
                if (j == -1) {
                } else {
                    if (this.mLastMotionY == -1.0F)
                        this.mLastMotionY = paramMotionEvent.getY(j);
                    if (this.mHeaderContainer.getBottom() >= this.mHeaderHeight) {
                        ViewGroup.LayoutParams localLayoutParams = this.mHeaderContainer.getLayoutParams();
                        float f = ((paramMotionEvent.getY(j) - this.mLastMotionY + this.mHeaderContainer.getBottom()) / this.mHeaderHeight - this.mLastScale) / 2.0F + this.mLastScale;
                        if ((this.mLastScale <= 1.0D) && (f < this.mLastScale)) {
                            localLayoutParams.height = this.mHeaderHeight;
                            this.mHeaderContainer.setLayoutParams(localLayoutParams);
                            return super.onTouchEvent(paramMotionEvent);
                        }
                        this.mLastScale = Math.min(Math.max(f, 1.0F), this.mMaxScale);
                        localLayoutParams.height = ((int) (this.mHeaderHeight * this.mLastScale));
                        if (localLayoutParams.height < this.mScreenHeight)
                            this.mHeaderContainer.setLayoutParams(localLayoutParams);
                        this.mLastMotionY = paramMotionEvent.getY(j);
                        return true;
                    }
                    this.mLastMotionY = paramMotionEvent.getY(j);
                }
                break;
            case MotionEvent.ACTION_UP:
                reset();
                endScraling();
                break;
            case MotionEvent.ACTION_CANCEL:
                int i = paramMotionEvent.getActionIndex();
                this.mLastMotionY = paramMotionEvent.getY(i);
                this.mActivePointerId = paramMotionEvent.getPointerId(i);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                onSecondaryPointerUp(paramMotionEvent);
                try {
                    this.mLastMotionY = paramMotionEvent.getY(paramMotionEvent.findPointerIndex(this.mActivePointerId));
                } catch (IllegalArgumentException e) {
                    // TODO: handle exception
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
        }

        return true;
    }


    int state = 0;
    // 重写了ontouch，拦截所有触摸，进行事件的重新分配，解决了点击事件冲突问题（以前用手滑动屏幕总是会触发listView item的点击事件）
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(state != 1){
                    state = 1;
                    changeBackgroud(view, 1, motionEvent);
                }
                break;
            case MotionEvent.ACTION_UP:
                state ++;
                changeBackgroud(view, 2, motionEvent);
                break;
            default:
                state = state == 3 ? 4 : 3;
                changeBackgroud(view, 3, motionEvent);
                break;
        }
        onTouchEvent(motionEvent);
        return true;
    }

    // 为了解决屏幕滚动与listView的点击事件冲突问题（以前用手滑动屏幕总是会触发listView item的点击事件）
    private void changeBackgroud(final View view, final int type, final MotionEvent motionEvent){
        if(state > 3) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(type == 1 && state < 3){
                            PullToZoomListView.super.onTouchEvent(motionEvent);
                        }  else if(type == 3 && state > 2){
                            motionEvent.setAction(MotionEvent.ACTION_CANCEL);
                            PullToZoomListView.super.onTouchEvent(motionEvent);
                        } else if(type == 2 && state == 2){
                            PullToZoomListView.super.onTouchEvent(motionEvent);
                        }
                    }
                }, 100);
            }
        }).start();
    }

    public void setHeaderViewSize(int paramInt1, int paramInt2) {
        Object localObject = this.mHeaderContainer.getLayoutParams();
        if (localObject == null)
            localObject = new LayoutParams(paramInt1, paramInt2);
        ((ViewGroup.LayoutParams) localObject).width = paramInt1;
        ((ViewGroup.LayoutParams) localObject).height = paramInt2;
        this.mHeaderContainer.setLayoutParams((ViewGroup.LayoutParams) localObject);
        this.mHeaderHeight = paramInt2;
    }

    public void setOnScrollListener(OnScrollListener paramOnScrollListener) {
        this.mOnScrollListener = paramOnScrollListener;
    }

    public void setShadow(int paramInt) {
        this.mShadow.setBackgroundResource(paramInt);
    }

    @Override
    public boolean canPullDown() {
        return false;
    }

    @Override
    public boolean canPullUp() {
//        if (!pullUpEnable) {
//            return false;
//        }
        if (getCount() == 0) {
            // 没有item的时候也可以上拉加载
            return true;
        } else if (getLastVisiblePosition() == (getCount() - 1)) {
            // 滑到底部了
            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
                    && getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()).getBottom()
                    <= getMeasuredHeight()) {
                return true;
            }
        }
        return false;
    }

    class ScalingRunnalable implements Runnable {
        long mDuration;
        boolean mIsFinished = true;
        float mScale;
        long mStartTime;

        ScalingRunnalable() {
        }

        public void abortAnimation() {
            this.mIsFinished = true;
        }

        public boolean isFinished() {
            return this.mIsFinished;
        }
        public void run() {
                 float f2;
                 ViewGroup.LayoutParams localLayoutParams;
                 if ((!this.mIsFinished) && (this.mScale > 1.0D)) {
                     float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) this.mStartTime)
                             / (float) this.mDuration;
                     f2 = this.mScale - (this.mScale - 1.0F) * PullToZoomListView.sInterpolator.getInterpolation(f1);
                     localLayoutParams = PullToZoomListView.this.mHeaderContainer.getLayoutParams();
                     if (f2 > 1.0F) {
                         localLayoutParams.height = PullToZoomListView.this.mHeaderHeight;
                         localLayoutParams.height = ((int) (f2 * PullToZoomListView.this.mHeaderHeight));
                         PullToZoomListView.this.mHeaderContainer.setLayoutParams(localLayoutParams);
                         PullToZoomListView.this.post(this);
                         startReload();//FIXME
                         return;
                     }
                     isLoad=false;
                     this.mIsFinished = true;
             }

        }

        public void startAnimation(long paramLong) {
            this.mStartTime = SystemClock.currentThreadTimeMillis();
            this.mDuration = paramLong;
            this.mScale = ((float) (PullToZoomListView.this.mHeaderContainer.getBottom()) / PullToZoomListView.this.mHeaderHeight);
            this.mIsFinished = false;
            PullToZoomListView.this.post(this);
            isLoad=false;
        }
    }

    /**
     * 下拉刷新
     */
    private void startReload() {
        if (mListViewListener != null && !isLoad) {
            isLoad = true;
            mListViewListener.onReload();
        }
    }

    public void setPullToZoomListViewListener(PullToZoomListViewListener l) {
        mListViewListener = l;
    }

    public interface PullToZoomListViewListener {
        void onReload();
        void onLoadMore();
    }

    private PullToZoomListViewListener mListViewListener;
}
