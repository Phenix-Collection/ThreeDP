package com.tdp.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.google.gson.Gson;
import com.sdk.api.WebUcenterApi;
import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.core.Globals;
import com.sdk.core.UserControl;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.utils.MD5;
import com.sdk.utils.PermissionsUtil;
import com.sdk.views.Menu.TopMenuView;
import com.sdk.views.dialog.Loading;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * 用户验证活动类
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.edt_login_uname)
    EditText unameEdt;
    @BindView(R.id.edt_login_password)
    EditText passwordEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        PermissionsUtil.checkAndRequestPermissions(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null) return;
        switch (requestCode){
            case 1: // 忘记密码返回参数
            case 2: // 注册返回参数
                String account = data.getStringExtra("account");
                if(account != null){
                    unameEdt.setText(account);
                }
                break;
        }
    }

    @OnClick({R.id.tv_exit, R.id.tv_login_forget, R.id.tv_login_noaccount, R.id.tv_login_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_exit:
                finish();
                break;
            case R.id.tv_login_forget:
                startActivityForResult(new Intent(this, ForgetPwdActivity.class),1);

                //startActivity(new Intent(this, ForgetPwdActivity.class));
                break;
            case R.id.tv_login_noaccount:
                startActivityForResult(new Intent(this, RegisterActivity.class),2);
                break;
            case R.id.tv_login_login:
                String uname = unameEdt.getText().toString();
                String pwd = passwordEdt.getText().toString();
                Loading.start(this, "登录中...", true);

                UserControl.getInstance().login(uname, pwd, new OnResultListener() {
                    @Override
                    public void onWebUiResult(WebMsg webMsg) {
                        Loading.stop();
                        if (webMsg.isSuccess()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra(MainActivity.TAG,MainActivity.FROM_LOGIN);
                            startActivity(intent);
                        } else {
                            webMsg.showMsg(LoginActivity.this);
                        }
                    }
                });



//                String secretKey = MD5.md5(uname+pwd+ Globals.APP_KEY);
//                HttpRequest.instance().doPost(HttpRequest.create(WebUcenterApi.class).login(uname, pwd, "0", secretKey), new OnResultListener() {
//                    @Override
//                    public void onWebUiResult(WebMsg webMsg) {
//                    Loading.stop();
//                    if (webMsg.isSuccess()) {
//                        LoginInfoEntity data = new Gson().fromJson(webMsg.getData(), LoginInfoEntity.class);
//                        if(data != null){
//                            data.setPassword(passwordEdt.getText().toString());
//                            CacheDataService.saveLoginInfo(data);
//
//                        }
//                    } else {
//                        webMsg.showMsg(LoginActivity.this);
//                    }
//                    }
//                });
                break;
        }
    }
}
