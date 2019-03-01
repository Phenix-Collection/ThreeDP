package com.tdp.main.controller.newmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import com.tdp.main.R;
import com.tdp.main.activity.CreateAvatarActivity;
import com.tdp.main.controller.listener.OnCreateAvatarListener;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReadyController {

    private Context context;
    private OnCreateAvatarListener listener;

    public ReadyController(CreateAvatarActivity context, OnCreateAvatarListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /***
     * 展示准备拍照页面
     * @param view
     */
    public void show(RelativeLayout view) {
        view.removeAllViews();
        View v = LayoutInflater.from(context).inflate(R.layout.item_newmodel_ready, view);
        ButterKnife.bind(this, v);
    }

    @OnClick({R.id.ibtn_back, R.id.tv_ready_go})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                listener.onTakePhotoReadyListener(false);
                break;
            case R.id.tv_ready_go:
                listener.onTakePhotoReadyListener(true);
                break;
        }
    }
}

