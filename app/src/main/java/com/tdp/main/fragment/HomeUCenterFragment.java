package com.tdp.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sdk.api.entity.UserInfoEntity;
import com.sdk.core.CacheImageControl;
import com.sdk.core.Globals;
import com.sdk.db.BaseDataService;
import com.sdk.db.CacheDataService;
import com.sdk.views.dialog.Loading;
import com.tdp.base.BaseFragment;
import com.tdp.main.R;
import com.tdp.main.activity.AbountActivity;
import com.tdp.main.activity.EditPasswordActivity;
import com.tdp.main.activity.ChangeUserActivity;
import com.tdp.main.activity.HelpActivity;
import com.tdp.main.activity.UserInfoActivity;
import com.tdp.main.activity.LoginActivity;
import com.tdp.main.activity.MessageActivity;
import com.tdp.main.activity.EditUserInfoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author:zlcai
 * @createrDate:2017/9/2 15:27
 * @lastTime:2017/9/2 15:27
 * @detail: 个人中心
 **/

public class HomeUCenterFragment extends BaseFragment {

    public static final String TAG = "FRAGMENT_HOME_UCENTER";

    @BindView(R.id.img_head)
    ImageView imgHead;
    @BindView(R.id.id_uname)
    TextView unameTv;
    @BindView(R.id.id_account)
    TextView accountTv;
    @BindView(R.id.id_message_number)
    TextView messageNumberTv;

    // ui
    private UserInfoEntity userInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ucenter, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshUI();
    }

    //选文件响应函数
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            refreshUI();
        }
    }

    public void refreshUI() {
        userInfo = CacheDataService.getLoginInfo().getUserInfo();
//        ImageLoader.getInstance().displayImage(Globals.BASE_API + userInfo.getProfilePhoto(), imgHead, ImageLoaderUtils.getHeaderOptions());
        CacheImageControl.getInstance().setImageView(imgHead, Globals.BASE_API + userInfo.getProfilePhoto(), true);
        unameTv.setText(userInfo.getNickName() == null || userInfo.getNickName().length() == 0 ? "未设置昵称" : userInfo.getNickName());
        accountTv.setText(userInfo.getAccount());

        int count = BaseDataService.getValueByInt(BaseDataService.DATA_FRIEND_REQUEST, 0);
        messageNumberTv.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        messageNumberTv.setText(String.valueOf(count));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.rl_head, R.id.tx_change_user, R.id.rl_add, R.id.rl_setInformation, R.id.rl_changepw, R.id.rl_message, R.id.rl_abount, R.id.rl_help})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_head:
                Intent intent=new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtra("account",CacheDataService.getLoginInfo().getUserInfo().getAccount());
                startActivity(intent);
                break;
            case R.id.tx_change_user:
                startActivity(new Intent(getActivity(), ChangeUserActivity.class));
                break;
            case R.id.rl_add:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.rl_setInformation:
                startActivityForResult(new Intent(getActivity(), EditUserInfoActivity.class),1);
                break;
            case R.id.rl_changepw:
                startActivity(new Intent(getActivity(), EditPasswordActivity.class));
                break;
            case R.id.rl_message:
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.rl_abount:
                startActivity(new Intent(getActivity(), AbountActivity.class));
                break;
            case R.id.rl_help:
                startActivity(new Intent(getActivity(), HelpActivity.class));
                break;
        }
    }
}
