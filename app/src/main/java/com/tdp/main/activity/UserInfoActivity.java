package com.tdp.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.easeui.utils.EaseCommonUtils;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.sdk.api.WebFriendApi;
import com.sdk.api.entity.UserDetailInfo;
import com.sdk.core.CacheImageControl;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.utils.CalendarUtil;
import com.sdk.utils.StatusBarUtil;
import com.sdk.views.dialog.AlertDialog;
import com.sdk.views.dialog.Loading;
import com.sdk.views.dialog.Toast;
import com.sdk.views.dialog.listener.OnDialogClickListener;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.activity.userinfo.EditRemarkActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.model.UserInfo;
import okhttp3.Cache;

public class UserInfoActivity extends BaseActivity {
    @BindView(R.id.id_nick_name)
    TextView nickNameTv;
    @BindView(R.id.id_account)
    TextView accountTv;
    @BindView(R.id.id_logo)
    ImageView ulogoIv;
//    @BindView(R.id.id_realname)
//    TextView relaNameTv;
    @BindView(R.id.id_sex)
    ImageView sexIv;
    @BindView(R.id.id_date)
    TextView dateTv; // 地址年龄
    @BindView(R.id.tv_address)
    TextView addressTv;
    //    @BindView(R.id.id_phone)
//    TextView phoneTv; // 手机
//    @BindView(R.id.id_email)
//    TextView emailTv;
//    @BindView(R.id.id_occupation)
//    TextView occupationTv; // 行业
//    @BindView(R.id.id_educational)
//    TextView educationalTv; // 教育
    @BindView(R.id.id_add_friend)
    TextView addFriendTv;
    @BindView(R.id.id_remark)
    TextView remarkTv;

    @BindView(R.id.id_remark_rl)
    RelativeLayout remarkRl;
    @BindView(R.id.id_clear)
    Button clearMsgBtn;

    private UserDetailInfo data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        ButterKnife.bind(this);
        init();
    }

    private void init(){

        Intent intent = getIntent();
        String account = intent.getStringExtra("account");
        accountTv.setText("账号："+ account);
//        String nickName = intent.getStringExtra("nickname");
//        relaNameTv.setText("" + nickName);
//        phoneTv.setText(account);
        //data
        Loading.start(this, "数据加载中...", true);
        HttpRequest.instance().doPost(HttpRequest.create(WebFriendApi.class).userInfo(account), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
                Loading.stop();
                if(webMsg.isSuccess()){
                    data = new Gson().fromJson(webMsg.getData(), UserDetailInfo.class);
                    if(data != null){
                        addFriendTv.setVisibility(data.getIsStatus() == -1 ? View.GONE : View.VISIBLE);
                        clearMsgBtn.setVisibility(data.getIsStatus() == 2 ? View.VISIBLE : View.GONE);
                        remarkRl.setVisibility(data.getIsStatus() == 2 ? View.VISIBLE : View.GONE);
                        switch (data.getIsStatus()){
                            case 0: // 陌生人
                                addFriendTv.setText("加为好友");
                                break;
                            case 1: // 对方正在请求我为好友
                                addFriendTv.setText("通过验证");
                                break;
                            case 2:
                                remarkTv.setText(data.getRemark() == null ? data.getNick_name() : data.getRemark());
                                addFriendTv.setText("删除好友");
                                break;
                        }

//                        ImageLoader.getInstance().displayImage(Globals.BASE_API + data.getProfile_photo(), ulogoIv, ImageLoaderUtils.getHeaderOptions());
                        CacheImageControl.getInstance().setImageView(ulogoIv, Globals.BASE_API + data.getProfile_photo(), false);
                        nickNameTv.setText(data.getNick_name());
                        dateTv.setText(data.getBirthday() == null ? "0岁" : CalendarUtil.getAgeByBirth(data.getBirthday()) + "岁 ");
                        sexIv.setImageResource(data.getSex() == 1 ? R.drawable.man : R.drawable.icon_woman_pink);
                        addressTv.setText(data.getAddress());
//                        emailTv.setText(data.getEmail()== null ? "未设置" : data.getEmail());
//                        occupationTv.setText(data.getOccupation()== null ? "未设置" : data.getOccupation());
//                        educationalTv.setText(data.getEducational()== null ? "未设置" : data.getEducational());
                    }
                } else {
                    webMsg.showMsg(UserInfoActivity.this);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1: // 添加好友请求
                if(resultCode == 1){ // 结果集为1时代表添加请求成功
                    onBack();
                }
                break;
            case 2:
                if(resultCode == 1){ // 修改用户别名
                    String remark = data.getStringExtra("remark");
                    remarkTv.setText(remark);
                }
                break;
        }
    }

//    @Override
//    public void toImmersion() {
//        StatusBarUtil.transparencyBar(this);
//    }


    private boolean isBusying = false;
    @OnClick({R.id.id_back, R.id.id_add_friend, R.id.id_clear, R.id.id_remark_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_back:
                onBack();
                break;
            case R.id.id_add_friend:
                if(data != null){
                    if(data.getIsStatus() == 0){ // 加为好友
                        Intent intent = new Intent();
                        intent.putExtra("account", data.getAccount());
                        //  intent.putExtra("nickName", data.getNick_name());
                        intent.setClass(this, AddFriendActivity.class);
                        startActivityForResult(intent, 1);
                    } else if(data.getIsStatus() == 1){ // 通过验证

                        if(isBusying) return;
                        isBusying = true;
                        Loading.start(this, "正在加载中，请稍后...", true);
                        HttpRequest.instance().doPost(HttpRequest.create(WebFriendApi.class).agreeOrRefuseFriend(data.getAccount(), "", 1), new OnResultListener() {
                            @Override
                            public void onWebUiResult(WebMsg webMsg) {
                                Loading.stop();
                                isBusying = false;
                                if(webMsg.isSuccess()){
                                    Toast.show(UserInfoActivity.this, "您已同意对方好友请求！", Toast.LENGTH_LONG);
                                    setResult(1);
                                    finish();
                                } else {
                                    webMsg.showMsg(UserInfoActivity.this);
                                }
                            }
                        });
                    }else if(data.getIsStatus() == 2) { // 通过验证


                        AlertDialog.getInstance(this).setMessage(AlertDialog.TIP, "移除该好友后，将在双方列表中同时移除，确定要移除吗？").setTitle("删除好友提示!").setButton("我要删除", "放弃").setOnDialogClickListener(new OnDialogClickListener() {
                            @Override
                            public void click(int index, Dialog view) {
                                switch (index){
                                    case AlertDialog.ACTION_OK:
                                        if(isBusying) return;
                                        isBusying = true;
                                        Loading.start(UserInfoActivity.this, "正在删除，请稍后...", true);
                                        HttpRequest.instance().doPost(HttpRequest.create(WebFriendApi.class).deleteFriend(data.getAccount()), new OnResultListener() {
                                            @Override
                                            public void onWebUiResult(WebMsg webMsg) {
                                                Loading.stop();
                                                isBusying = false;
                                                if(webMsg.isSuccess()){
                                                    CacheDataService.removeFriendInfo(data.getAccount());
                                                    EMClient.getInstance().chatManager().deleteConversation(data.getAccount(), true);
                                                    setResult(2, null);
                                                    finish();
                                                } else {
                                                    webMsg.showMsg(UserInfoActivity.this);
                                                }
                                            }
                                        });
                                        break;
                                    case AlertDialog.ACTION_CANCEL:

                                        break;
                                }
                            }
                        }).show();
                    }
                }

                //startActivity(new Intent(this, EditUserInfoActivity.class));
                break;
            case R.id.id_clear: // 清空与该用户的聊天记录
                String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
                AlertDialog.getInstance(this).setButton("确定", "取消").setTitle("删除与好友的聊天记录").setMessage(AlertDialog.TIP, msg).setOnDialogClickListener(new OnDialogClickListener() {
                    @Override
                    public void click(int index, Dialog view) {
                        switch (index){
                            case AlertDialog.ACTION_OK:
                                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(data.getAccount(), EaseCommonUtils.getConversationType(1), true);
                                conversation.clearAllMessages();
                                finish();
                                break;
                            case AlertDialog.ACTION_CANCEL:
                                Toast.show(UserInfoActivity.this, "操作已取消！", Toast.LENGTH_LONG);
                                break;
                        }
                    }
                }).show();
                break;
            case R.id.id_remark_rl:
                Intent intent = new Intent();
                intent.setClass(this, EditRemarkActivity.class);
                intent.putExtra("account", data.getAccount());
                intent.putExtra("remark", data.getRemark() == null ? data.getNick_name() : data.getRemark());
                this.startActivityForResult(intent, 2);
                break;
        }
    }
}
