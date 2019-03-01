package com.tdp.main.controller.newmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import com.tdp.main.R;
import com.tdp.main.constant.SexEnum;
import com.tdp.main.controller.listener.OnCreateAvatarListener;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectSexController {

    private Context context;
    private OnCreateAvatarListener listener;

    public SelectSexController(Context context, OnCreateAvatarListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /***
     * 展示界面
     * @param view
     */
    public void show(RelativeLayout view){
        View v = LayoutInflater.from(context).inflate(R.layout.item_newmodel_selectsex, view);
        ButterKnife.bind(this, v);
    }

    @OnClick({R.id.tv_exit, R.id.tv_male, R.id.tv_femal})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_male: // 男
                listener.onSexResult(SexEnum.MALE.getIndex());
                break;
            case R.id.tv_femal: // 女
                listener.onSexResult(SexEnum.FEMALE.getIndex());
                break;
        }
    }

}
