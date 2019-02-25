package com.tdp.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.db.CacheDataService;
import com.tdp.base.BaseFragmentActivity;
import com.tdp.main.R;
import com.tdp.main.fragment.HomeVideoFragment;
import com.tdp.main.fragment.HomeFragment;
import com.tdp.main.fragment.HomeFriendFragment;
import com.tdp.main.fragment.HomeUCenterFragment;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * @author:zlcai
 * @createrDate:2017/9/2 10:35
 * @lastTime:2017/9/2 10:35
 * @detail: 主Activity, 所有功能的入口
 **/

public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {
    public ArrayList<Fragment> fragments = new ArrayList<Fragment>(); // 所有活动
    //
    @BindView(R.id.id_content)
    FrameLayout contentFl; // 内容
    @BindViews({R.id.id_home, R.id.id_reportwage, R.id.id_what1, R.id.id_ucenter})
    List<LinearLayout> tabRbs; // 底部五个功能按钮
    @BindViews({R.id.id_home_icon, R.id.id_reportwage_icon, R.id.id_what1_icon, R.id.id_ucenter_icon})
    List<ImageView> menuIcons;
    @BindViews({R.id.id_home_label, R.id.id_reportwage_label, R.id.id_what1_label, R.id.id_ucenter_label})
    List<TextView> menuLabels;
    public static final int FROM_FIGURE = 1;
    public static final int FROM_NEWMODEL = 2;
    public static final int FROM_LOGIN = 3;
    public static final int FROM_CHANGUSER = 4;

    private int menuNormalIcons[] = {R.drawable.icon_home_n, R.drawable.icon_video_n, R.drawable.icon_interactive_n, R.drawable.icon_my_n};
    private int menuFocusIcons[] = {R.drawable.icon_home_f, R.drawable.icon_video_f, R.drawable.icon_interactive_f, R.drawable.icon_my_f};
    private int currIndex = 0; // 当前页下标
    private boolean ISEXIT = false;
    private Handler handler;
    public static final String TAG = "MainActivity";
    HomeFragment homeFragment;
    HomeUCenterFragment homeUCenterFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //PermissionsUtil.checkAndRequestPermissions(this);
        ButterKnife.bind(this);
        checkLogin();
       // init();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        homeFragment = (HomeFragment) fragments.get(0);
        homeUCenterFragment = (HomeUCenterFragment) fragments.get(3);
        homeFragment.getTvHomeLoading().setVisibility(View.VISIBLE);
        switch (intent.getIntExtra(TAG, 0)) {
            case FROM_FIGURE:
                if (homeFragment.getAvatarService() != null) {
                    homeFragment.getAvatarService().setAvatar();
                    homeFragment.getAvatarService().getmCameraRenderer().onCreate();
                }
                break;
            case FROM_NEWMODEL:
                if (homeFragment.getAvatarService() != null)
                    homeFragment.getAvatarService().loadAvatar();
                break;
            case FROM_LOGIN:
            case FROM_CHANGUSER:
                if (homeFragment.getAvatarService() != null)
                    homeFragment.getAvatarService().loadAvatar();
                homeFragment.updateDollName();
                homeUCenterFragment.refreshUI();
                break;
        }

       /* if(fragments.size()==0){
            Log.e("ououou",TAG+"onNewIntent 第一次打开app从登录过来的情况");
        }else {
            Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                    Thread.sleep(200);
                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(Integer integer) {

                }

                @Override
                public void onError(Throwable e) {
                }

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onComplete() {
                    if(intent.getBooleanExtra("need_reload_modal",false)){
                        Log.e("ououou",TAG+"onNewIntent 需要重新加载home！");
                        HomeFragment homeFragment = (HomeFragment) fragments.get(0);
                        homeFragment.getAvatarService().getmCameraRenderer().onCreate();
                        //  homeFragment.onStart();
                        //homeFragment.loadModel();
                    }
                    if(intent.getBooleanExtra("need_update_home",false)){
                        Toast.makeText(MainActivity.this, "刷新模型中...", Toast.LENGTH_SHORT).show();
                        Log.e("ououou",TAG+"onNewIntent 需要更新home！");
                        HomeFragment homeFragment = (HomeFragment) fragments.get(0);
                        homeFragment.checkFileAndLoadModel(true);
                    }
//                    if(intent.getBooleanExtra("need_update_ucenter",false)){
//                        Log.e("ououou",TAG+"onNewIntent 需要更新UCenter！");
//                        HomeUCenterFragment homeUCenterFragment= (HomeUCenterFragment) fragments.get(3);
//                        homeUCenterFragment.refreshUI();
//                    }
                }
            });
        }*/
    }

    private void checkLogin(){
        LoginInfoEntity data = CacheDataService.getLoginInfo();
        if (data != null) {
              init();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void init() {

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ISEXIT = false;
            }
        };
        // UI
        // ====== 初始化五个功能页面
        fragments.clear();
        fragments.add(new HomeFragment());
        fragments.add(new HomeVideoFragment());
        fragments.add(new HomeFriendFragment());
        fragments.add(new HomeUCenterFragment());
        // ====== 初始化功能图标
        pageToIndex(currIndex);
        // listener
        for (int i = 0; i < tabRbs.size(); i++) {
            tabRbs.get(i).setTag(i);
            tabRbs.get(i).setOnClickListener(this);
        }
    }

    private void pageToIndex(int index) {
        if (fragments == null || fragments.size() <= index) return;
        menuLabels.get(currIndex).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryBlack, null));
        menuIcons.get(currIndex).setImageResource(menuNormalIcons[currIndex]);
        menuLabels.get(index).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, null));
        menuIcons.get(index).setImageResource(menuFocusIcons[index]);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //-------设置所有的按钮为正常
        if (!fragments.get(index).isAdded()) {
            if (this.currIndex == index) {
                transaction.add(contentFl.getId(), fragments.get(index)).commitAllowingStateLoss();
            } else {
                transaction.hide(fragments.get(this.currIndex)).add(contentFl.getId(), fragments.get(index)).commitAllowingStateLoss();
            }
        } else {
            transaction.hide(fragments.get(this.currIndex)).show(fragments.get(index)).commitAllowingStateLoss();
        }
        this.currIndex = index;
    }

    @Override
    public void onClick(View v) {
        int index = Integer.parseInt(v.getTag().toString());
        if (index < 4 && currIndex != index) {
            pageToIndex(index);
        }
    }


    //重写返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!ISEXIT) {
            ISEXIT = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息,2秒之内再按一次退出程序
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            super.exitApp();
        }
    }
}
