package com.sdk.views.listview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.sdk.views.listview.pulllistview.Pullable;

public class PullScrollView extends ScrollView implements Pullable {

    public PullScrollView(Context context) {
        super(context);
    }

    public PullScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PullScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PullScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean canPullDown() {

        return  this.getScrollY() == 0;
    }

    @Override
    public boolean canPullUp() {
        return this.getChildAt(0).getHeight() - this.getMeasuredHeight() <= this.getScrollY();
    }

}
