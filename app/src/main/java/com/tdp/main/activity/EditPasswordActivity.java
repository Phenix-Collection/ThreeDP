package com.tdp.main.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sdk.api.WebUcenterApi;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.utils.MD5;
import com.sdk.utils.SystemUtil;
import com.sdk.views.Menu.TopMenuView;
import com.sdk.views.dialog.Loading;
import com.sdk.views.dialog.Toast;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditPasswordActivity extends BaseActivity {

    @BindView(R.id.id_password_old)
    EditText passwordOldEdt;
    @BindView(R.id.id_password)
    EditText passwordEdt;
    @BindView(R.id.id_password_new)
    EditText passworldNewEdt;
    @BindView(R.id.tv_account)
    TextView accountTv;

    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        setContentView(R.layout.activity_edit_password);
        ButterKnife.bind(this);
        //
        String account = CacheDataService.getLoginInfo().getUserInfo().getAccount();
        accountTv.setText(new StringBuffer().append(account.substring(0, 3)).append("****").append(account.substring(account
                .length() - 4, account.length())));

    }

    boolean isBusying = false;
   // boolean oldVisiable = false, visiable = false, newVisiable = false;
    @OnClick({R.id.ibtn_back, R.id.id_ok})
    public void onclick(View v){
        switch (v.getId()){
            case R.id.ibtn_back:
                onBack();
                break;
//            case R.id.id_password_eye:
//                visiable = !visiable;
//                passwordEdt.setInputType(visiable ? InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT :
//                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                break;
//            case R.id.id_password_new_eye:
//                newVisiable = !newVisiable;
//                passworldNewEdt.setInputType(newVisiable ? InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT :
//                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                break;
//            case R.id.id_password_old_eye:
//                oldVisiable = !oldVisiable;
//                passwordOldEdt.setInputType(oldVisiable ? InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT :
//                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                break;
            case R.id.id_ok: // 确定
                if(isBusying) return;

                String passwordOld = passwordOldEdt.getText().toString().trim();
                String password = passwordEdt.getText().toString().trim();
                String passwordNew = passworldNewEdt.getText().toString().trim();
                String secretKey = MD5.md5(account + passwordOld + Globals.APP_KEY);

                if(passwordOld.length() < 6){
                    Toast.show(this, "请输入正确的原密码！", Toast.LENGTH_SHORT);
                    return;
                }

                if(password.length() < 6){
                    Toast.show(this, "新密码至少需要6位以上字符！", Toast.LENGTH_SHORT);
                    return;
                }

                if(!password.equals(passwordNew)){
                    Toast.show(this, "新密码与重复密码不一致！", Toast.LENGTH_SHORT);
                    return;
                }

                isBusying = true;
                Loading.start(this, "正在修改中...", false);
                HttpRequest.instance().doPost(HttpRequest.create(WebUcenterApi.class).updatePwd(account, passwordOld, passwordNew,
                    secretKey), new OnResultListener() {
                    @Override
                    public void onWebUiResult(WebMsg webMsg) {
                        isBusying = false;
                        Loading.stop();
                        if(webMsg.isSuccess()){
                            Toast.show(EditPasswordActivity.this, "恭喜，修改成功！", Toast.LENGTH_SHORT);
                            SystemUtil.loginOut(EditPasswordActivity.this);
                        } else {
                            webMsg.showMsg(EditPasswordActivity.this);
                        }
                    }
                });

                break;
        }
    }
}
