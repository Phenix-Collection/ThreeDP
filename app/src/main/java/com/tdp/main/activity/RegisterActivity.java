package com.tdp.main.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.sdk.api.WebUcenterApi;
import com.sdk.core.Globals;
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
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends BaseActivity {

    String phone = null;

    @BindView(R.id.id_account)
    public EditText accountEdt;
    @BindView(R.id.id_password)
    public EditText passwordEdt;
    @BindView(R.id.id_password_new)
    public EditText passwordNewEdt;
    @BindView(R.id.id_code)
    public EditText codeEdt;
    @BindView(R.id.id_send)
    public TextView sendTv;
    @BindView(R.id.tv_error_msg)
    public TextView erroMsgTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    private boolean sendCodeIsBusying = false;
    @OnClick({R.id.tv_exit, R.id.id_send, R.id.id_ok, R.id.login_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_tv:
            case R.id.tv_exit:
                onBack();
                break;
            case R.id.id_send: // 发送验证码
                if(sendCodeIsBusying) return;
                phone = accountEdt.getText().toString();

                if(!SystemUtil.isMobileNO(phone)){
                    Toast.show(this, "请输入正确的手机号码", Toast.LENGTH_LONG);
                    return;
                  }
                sendCodeIsBusying = true;
                String secretKey = MD5.md5(phone + Globals.APP_KEY);
                HttpRequest.instance().doPost(HttpRequest.create(WebUcenterApi.class).sendCode(phone, secretKey, "_register"), new OnResultListener() {
                    @Override
                    public void onWebUiResult(WebMsg webMsg) {
                        if(webMsg.isSuccess()){
                            Toast.show(RegisterActivity.this, "验证码已发送到您手机，请查收！", android.widget.Toast.LENGTH_LONG);
                            countDown();
                        } else {
                            sendCodeIsBusying = false;
                            webMsg.showMsg(RegisterActivity.this);
                        }
                    }
                });
                break;
            case R.id.id_ok: // 确定

                // 判断手机号
                phone = accountEdt.getText().toString();
                if(phone.length() == 0){
                    erroMsgTv.setText("请输入正确的手机号码！");
//                    Toast.show(this, "请输入正确的手机号码！", Toast.LENGTH_LONG);
                    return;
                }

                // 判断密码
                String pwd = passwordEdt.getText().toString();
                String pwdNew = passwordNewEdt.getText().toString();
                if(pwd.length() < 6){
                    erroMsgTv.setText("密码至少为6位以上字符!");
                   // Toast.show(RegisterActivity.this, "密码至少为6位以上字符！", android.widget.Toast.LENGTH_LONG);
                    return;
                }

                if(!pwd.equals(pwdNew)){
                    erroMsgTv.setText("确认密码需与密码一致！");
                  //  Toast.show(RegisterActivity.this, "确认密码需与密码一致！", android.widget.Toast.LENGTH_LONG);
                    return;
                }

                String code = codeEdt.getText().toString();
                if(code.length() != 4){
                    erroMsgTv.setText("请输入4位数的验证码！");
                   // Toast.show(RegisterActivity.this, "请输入4位数的验证码！", android.widget.Toast.LENGTH_LONG);
                    return;
                }

                erroMsgTv.setText("");
                Loading.start(this, "注册中，请稍等...", false);
                HttpRequest.instance().doPost(HttpRequest.create(WebUcenterApi.class).register(phone, pwd, code, MD5.md5(phone + pwd + Globals.APP_KEY)), new OnResultListener() {
                    @Override
                    public void onWebUiResult(WebMsg webMsg) {
                        Loading.stop();
                        if(webMsg.isSuccess()){
                            Toast.show(RegisterActivity.this, "注册成功！", Toast.LENGTH_LONG);
                            Intent intent = new Intent();
                            intent.putExtra("account", phone);
                            setResult(1, intent);
                            finish();
                        } else {
                            webMsg.showMsg(RegisterActivity.this);
                        }
                    }
                });
                break;
        }
    }

    int count = 0;
    public void countDown() {
        if (count != 0) return;
        count = 60;
        sendTv.setBackgroundResource(R.drawable.r8_gray_noc);
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                while (count-- > 0) {
                    Thread.sleep(1000);
                    emitter.onNext(count);
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Integer count) {
                sendTv.setText(String.valueOf(count + "s"));
            }

            @Override
            public void onError(Throwable e) {
            }

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onComplete() {
                sendCodeIsBusying = false;
                sendTv.setText("获取验证码");
                sendTv.setBackgroundResource(R.drawable.r8_black_btn);
            }
        });
    }
}
