package com.sdk.core;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.sdk.api.WebUcenterApi;
import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.utils.MD5;
import com.sdk.views.dialog.Loading;
import com.sdk.views.dialog.Toast;
import com.tdp.main.activity.LoginActivity;
import com.tdp.main.activity.WelcomeActivity;

/**
 * ahtor: super_link
 * date: 2018/10/27 09:41
 * remark: 用户控制器
 */
public class UserControl {

    private static UserControl instance;

    private UserControl(){}

    public static UserControl getInstance(){
        if(instance == null)
            instance = new UserControl();
        return instance;
    }

    /***
     * 用户登录（用户登录后会自动保存用户登录信息，调用方无需处理，只需判断是否成功即可！）
     * @param account 账号
     * @param password 密码
     * @param listener 监听器
     */
    public void login(String account, final String password, final OnResultListener listener){
        String secretKey = MD5.md5(account+password+ Globals.APP_KEY);
        HttpRequest.instance().doPost(HttpRequest.create(WebUcenterApi.class).login(account, password, "0", secretKey), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
                if (webMsg.isSuccess()) {
                    LoginInfoEntity data = new Gson().fromJson(webMsg.getData(), LoginInfoEntity.class);
                    if(data != null){
                        data.setPassword(password);
                        CacheDataService.saveLoginInfo(data);
                        JPushControl.getInstance().registerAlias(Globals.getContext());
                        HuanxinControl.getInstance().login();
                    } else {
                        webMsg.setErrorCode(-1);
                    }
                }
                listener.onWebUiResult(webMsg);
            }
        });
    }

    private boolean relistIsBusying = false;
    /***
     * 重新登录
     * @param
     * @param
     * @param
     */
    public void relist(final Context context){
        if(relistIsBusying) return;
        relistIsBusying = true;
        String account = CacheDataService.getLoginInfo().getUserInfo().getAccount();
        final String password = CacheDataService.getLoginInfo().getPassword();
        String secretKey = MD5.md5(account+password+ Globals.APP_KEY);
        HttpRequest.instance().doPost(HttpRequest.create(WebUcenterApi.class).login(account, password, "0", secretKey), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
            relistIsBusying = false;
            if (webMsg.isSuccess()) {
                LoginInfoEntity data = new Gson().fromJson(webMsg.getData(), LoginInfoEntity.class);
                if(data != null){
                    data.setPassword(password);
                    CacheDataService.saveLoginInfo(data);
                    JPushControl.getInstance().registerAlias(Globals.getContext()); // 添加友盟监听
                    HuanxinControl.getInstance().login();
                    Toast.show(context, "恭喜，登录成功！", Toast.LENGTH_LONG);
                    return;
                } else {
                    webMsg.setErrorCode(-1);
                }
            }
            webMsg.showMsg(context);
            }
        });
    }

    /***
     * 注销用户
     * @param context
     */
    public synchronized void loginOut(Context context){
        Intent intent = new Intent();
        // 通知关闭所有界面
        Loading.stop(); // 这句代码用于停止activity对应的show，防止下一个activity使用时报错
        JPushControl.getInstance().unregisterAlias(context); // 移除友盟监听器
        HuanxinControl.getInstance().loginOut();
        intent.setAction(Globals.EXIT_APP);
        context.sendBroadcast(intent);

        // 启动登录界面
        intent = new Intent();
        CacheDataService.clearUserInfo();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public synchronized void loginOutAndClearAll(Context context){
        Intent intent = new Intent();
        // 通知关闭所有界面
        Loading.stop(); // 这句代码用于停止activity对应的show，防止下一个activity使用时报错
        intent.setAction(Globals.EXIT_APP);
        context.sendBroadcast(intent);
        //Toast.makeText(context, "退出登录成功！", Toast.LENGTH_LONG).show();

        // 启动登录界面
        intent = new Intent();
        CacheDataService.clearAll();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, WelcomeActivity.class);
        context.startActivity(intent);
    }

}
