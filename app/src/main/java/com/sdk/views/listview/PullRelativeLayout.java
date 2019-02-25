package com.sdk.views.listview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.sdk.views.listview.pulllistview.Pullable;

public class PullRelativeLayout extends RelativeLayout implements Pullable {

    public PullRelativeLayout(Context context) {
        super(context);
    }

    public PullRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PullRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PullRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

