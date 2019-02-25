package com.tdp.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.core.CacheImageControl;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.sdk.utils.StatusBarUtil;
import com.sdk.views.Menu.TopMenuView;
import com.tdp.app.MyAppliction;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangeUserActivity extends BaseActivity {

    @BindView(R.id.img_head)
    ImageView headIv;
    @BindView(R.id.tv_realname)
    TextView realNameTv;
    @BindView(R.id.tv_username)
    TextView userNameTv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init();
    }

    private void init() {
        StatusBarUtil.setStatusBarColor(this, R.color.colorWhite);
        // view

        // data
        LoginInfoEntity loginInfo = CacheDataService.getLoginInfo();
        userNameTv.setText(loginInfo.getUserInfo().getAccount());
        realNameTv.setText(loginInfo.getUserInfo().getNickName() == null || loginInfo.getUserInfo().getNickName().length() ==
                0 ? "未设置昵称" :loginInfo.getUserInfo().getNickName());
        CacheImageControl.getInstance().setImageView(headIv, Globals.BASE_API + loginInfo.getUserInfo().getProfilePhoto(), true);
    }


    @OnClick({TopMenuView.CLICK_LEFT, R.id.tv_changeuser_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case TopMenuView.CLICK_LEFT:
                onBack();
                break;
            case R.id.tv_changeuser_exit:
                ((MyAppliction)this.getApplication()).appExit();
                break;
        }
    }
}
