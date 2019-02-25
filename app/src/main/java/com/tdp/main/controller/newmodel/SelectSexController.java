package com.tdp.main.controller.newmodel;


import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdk.utils.imgeloader.ImageLoadActivity;
import com.sdk.views.Menu.TopMenuView;
import com.tdp.main.R;
import com.tdp.main.activity.EditUserInfoActivity;
import com.tdp.main.activity.MainActivity;
import com.tdp.main.activity.NewModelActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectSexController {
    @BindView(R.id.tv_selectsex_man)
    TextView tvSelectsexMan;
    @BindView(R.id.tv_selectsex_woman)
    TextView tvSelectsexWoman;
    private NewModelActivity context;
    private String TAG="SelectSexController";

    public SelectSexController(NewModelActivity context) {
        this.context = context;
    }

    public void initView(RelativeLayout group) {
        group.removeAllViews();
        View v = LayoutInflater.from(context).inflate(R.layout.item_newmodel_selectsex, group);
        ButterKnife.bind(this, v);
        Log.e("ououou",context.TAG+TAG+"这里是选择性别步骤！");
    }

    @OnClick({R.id.tv_exit, R.id.tv_selectsex_man, R.id.tv_selectsex_woman})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_exit:
                context.finish();
                break;
            case R.id.tv_selectsex_man:
                next(1);
                break;
            case R.id.tv_selectsex_woman:
                next(2);
                break;
        }
    }

    private void next(int sex){
        context.sex=sex;
        if(context.tag==NewModelActivity.FROM_CAMARA){
            context.step2();
        }else{
            Intent intent = new Intent();
            intent.setClass(context, ImageLoadActivity.class);
//            intent.putExtra("choose_iscut", false); // 需要裁剪
//            intent.putExtra("choose_isedit", false); // 设置编辑模式
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            intent.putExtra("outputX", 800);
//            intent.putExtra("outputY", 800);
            context.startActivityForResult(intent, 1);
        }
    }
}
