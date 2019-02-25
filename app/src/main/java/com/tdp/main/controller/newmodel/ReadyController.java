package com.tdp.main.controller.newmodel;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdk.views.Menu.TopMenuView;
import com.tdp.main.R;
import com.tdp.main.activity.NewModelActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReadyController {
    @BindView(R.id.tv_ready_go)
    TextView tvReadyGo;
    private NewModelActivity context;
    private String TAG="SelectSexController";

    public ReadyController(NewModelActivity context) {
        this.context = context;
    }

    public void initView(RelativeLayout group) {
        group.removeAllViews();
        View v = LayoutInflater.from(context).inflate(R.layout.item_newmodel_ready, group);
        ButterKnife.bind(this, v);
        Log.e("ououou",context.TAG+TAG+"这里是拍照指引步骤！");
    }

    @OnClick({TopMenuView.CLICK_LEFT, R.id.tv_ready_go})
    public void onClick(View view) {
        switch (view.getId()) {
            case TopMenuView.CLICK_LEFT:
                context.step1();
                break;
            case R.id.tv_ready_go:
                context.step3();
                break;
        }
    }
}

