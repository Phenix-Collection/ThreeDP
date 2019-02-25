package com.tdp.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.db.CacheDataService;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import butterknife.ButterKnife;

public class WelcomeActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏状态栏（电量那栏）
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // init();
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                Thread.sleep(0);
//                emitter.onComplete();
//            }
//        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//            }
//
//            @Override
//            public void onNext(Integer integer) {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//            }
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public void onComplete() {
//                init();
//            }
//        });
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wellcome);
        ButterKnife.bind(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        }, 3000);

/*        startActivity(new Intent(this, MainActivity.class));
        finish();
        overridePendingTransition(0, 0);*/
    }

    private void init(){
        LoginInfoEntity data = CacheDataService.getLoginInfo();
        if (data != null) {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
//            UserControl.getInstance().login(data.getUserInfo().getAccount(), data.getPassword(), new OnResultListener() {
//                @Override
//                public void onWebUiResult(WebMsg webMsg) {
//                    // 不管登录成不成功，都跳主页
//                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }
//            });
           //finish();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
