package com.chat.activity;

import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.View;

import com.tdp.base.BaseActivity;

/**
 * ahtor: super_link
 * date: 2018/10/9 14:18
 * remark:
 */
public class TestActivity extends BaseActivity {


    private void test(){
        SurfaceView surfaceView = new SurfaceView(this);
//        surfaceView.getHasOverlappingRendering()

        View v = this.getRootView();
        Rect rect = new Rect();
        v.getWindowVisibleDisplayFrame(rect);
        rect.height();

    }
}
