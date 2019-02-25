package com.tdp.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.core.CacheImageControl;
import com.sdk.db.BaseDataService;
import com.sdk.db.CacheDataService;
import com.sdk.utils.StatusBarUtil;
import com.sdk.views.Menu.TopMenuView;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManageUserActivity extends BaseActivity {
    @BindView(R.id.id_content)
    LinearLayout idContent;
    private Map<String, LoginInfoEntity> loginInfos;
    private ViewHolder viewHolder;
    private List<String> loginInfos_delete_keys= new ArrayList<>();
    private List<String> loginInfos_keys= new ArrayList<>();
    private List<ImageView> list_img = new ArrayList<>();
    private List<RelativeLayout> list_rl = new ArrayList<>();
    private String TAG = "ManageUserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        StatusBarUtil.setStatusBarColor(this, R.color.colorWhite);
        //Log.e("ououou", TAG + "这里是编辑账号的页面！");
        // view

        // data
//        loginInfos = CacheDataService.getLoginInfos();
//        for (String key : loginInfos.keySet()) {
//            if (!loginInfos.get(key).getUserInfo().getAccount().equals(CacheDataService.getLoginInfo().getUserInfo().getAccount())) {
//                createUserView(loginInfos.get(key), key);
//                Log.e("ououou",loginInfos.get(key).getUserInfo().getAccount());
//            }
//
//        }
//        for (int i = 0; i < list_img.size(); i++) {
//            final int finalI = i;
//            list_img.get(i).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    idContent.removeView(list_rl.get(finalI));
//                    loginInfos_delete_keys.add(loginInfos_keys.get(finalI));
//                }
//            });
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("ououou","onDestroy");
    }

    private void createUserView(LoginInfoEntity loginInfo, String key) {
        View v = LayoutInflater.from(this).inflate(R.layout.item_activity_edit_user, idContent, false);
        viewHolder = new ViewHolder(v);
        v.setTag(viewHolder);
        viewHolder.tvUsername.setText(loginInfo.getUserInfo().getAccount());
        viewHolder.tvRealname.setText(loginInfo.getUserInfo().getNickName() == null || loginInfo.getUserInfo().getNickName().length() ==
                0 ? "未设置昵称" :loginInfo.getUserInfo().getNickName());
        if(loginInfo.getUserInfo().getProfilePhoto()!=null){
            CacheImageControl.getInstance().setImageView(viewHolder.imgHead, loginInfo.getUserInfo().getProfilePhoto(), true);
        }
//            ImageLoader.getInstance().displayImage(loginInfo.getUserInfo().getProfilePhoto(), viewHolder.imgHead, ImageLoaderUtils.getHeaderOptions());
        idContent.addView(v);
        list_img.add(viewHolder.imgDelete);
        list_rl.add(viewHolder.rlItem);
        loginInfos_keys.add(key);
    }

    @OnClick(TopMenuView.CLICK_RIGH_LABEL)
    public void onClick() {
        for(String key:loginInfos_delete_keys){
            loginInfos.remove(key);
        }
        BaseDataService.saveValueToSharePerference("LOGIN_INFOS", new Gson().toJson(loginInfos));
        Intent intent = new Intent(this, ChangeUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    class ViewHolder {
        @BindView(R.id.img_delete)
        ImageView imgDelete;
        @BindView(R.id.img_head)
        com.sdk.views.imageview.RoundImageView imgHead;
        @BindView(R.id.tv_realname)
        TextView tvRealname;
        @BindView(R.id.tv_username)
        TextView tvUsername;
        @BindView(R.id.rl_item)
        RelativeLayout rlItem;

        ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
