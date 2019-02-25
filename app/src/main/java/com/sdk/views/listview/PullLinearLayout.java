package com.sdk.views.listview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sdk.views.listview.pulllistview.Pullable;

public class PullLinearLayout extends LinearLayout implements Pullable {

    public PullLinearLayout(Context context) {
        super(context);
    }

    public PullLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PullLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PullLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean canPullDown() {
        return getScrollY() == 0;
    }

    @Override
    public boolean canPullUp() {
        return this.getChildAt(0).getHeight() - this.getHeight() == this.getScrollY();
    }
}

