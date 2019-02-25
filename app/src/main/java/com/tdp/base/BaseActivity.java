package com.tdp.base;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.sdk.core.Globals;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.utils.APLog;
import com.sdk.utils.ScreenUtils;
import com.sdk.utils.StatusBarUtil;
import com.sdk.utils.SystemUtil;
import com.sdk.views.statusbar.Utils;
import com.tdp.app.MyAppliction;
import com.tdp.main.R;


/**
 * @author:zlcai
 * @createrDate:2017/7/27 10:55
 * @lastTime:2017/7/27 10:55
 * @detail:
 **/

public class BaseActivity extends Activity implements OnResultListener {

    private BaseReceiver receiver;
    protected InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){

        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        // 沉浸式
        toImmersion();

        // 注册广播接收者
        receiver = new BaseReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Globals.EXIT_APP);
        this.registerReceiver(receiver, filter);

        //设置允许使用转场动画
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
    }


    /***
     * 模拟android的返回键（可以触发动画）
     */
    public void onBack(){
        new Thread(){
            public void run() {
                try{
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                } catch (Exception e) { }
            }
        }.start();
    }

    /**
     * 沉浸式
     */
    public void toImmersion(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            StatusBarUtil.setStatusBarColor(this, R.color.colorWhite);
        else StatusBarUtil.setStatusBarColor(this, R.color.colorGray);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (SystemUtil.toHideInput(v, event, this.getApplicationContext())) {

            }
            return super.dispatchTouchEvent(event);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(event)) {
            return true;
        }
        return onTouchEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((MyAppliction) getApplication()).setTopContext(this);
        Animal.enterAnimation(this);
        //toImmersion();
    }

    @Override
    protected void onStop() {
        super.onStop();

//		Loading.stop();
        Animal.endAnimation(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }



    public void back(View v){

    }

    @Override
    public void onWebUiResult(WebMsg webMsg) {

    }

    private class BaseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Globals.EXIT_APP)) {
                finish();
            }
        }
    }

    /**
     * 获取跟视图
     */
    public View getRootView() {
        return this.getWindow().getDecorView();
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            APLog.i("api", "版本号：" + version);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "不能找到版本号";
        }
    }

    public void hideSoftKeyboard(){

        if (this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (this.getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /***
     * 退出app
     */
    public void exitApp() {
        Intent intent = new Intent();
        intent.setAction(Globals.EXIT_APP);
        this.sendBroadcast(intent);
    }

    public static boolean isNetworkAvalible(Context context) {
        // 获得网络状态管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 建立网络数组
            NetworkInfo[] net_info = connectivityManager.getAllNetworkInfo();

            if (net_info != null) {
                for (int i = 0; i < net_info.length; i++) {
                    // 判断获得的网络状态是否是处于连接状态
                    if (net_info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
