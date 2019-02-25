package com.tdp.base;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sdk.core.Globals;
import com.sdk.utils.StatusBarUtil;
import com.sdk.utils.SystemUtil;
import com.sdk.views.dialog.Loading;
import com.tdp.app.MyAppliction;
import com.tdp.main.R;


/**
 * @author:zlcai
 * @createrDate:2017/9/4 13:41
 * @lastTime:2017/9/4 13:41
 * @detail:
 **/

public class BaseFragmentActivity extends FragmentActivity {

	private BaseReceiver receiver;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		init();
	}

	private void init(){
		//PushAgent.getInstance(this).onAppStart();
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
			StatusBarUtil.setStatusBarColor(this, R.color.colorWhite);
		else StatusBarUtil.setStatusBarColor(this, R.color.colorGray);

		// 注册广播接收者
		if(receiver == null){
			receiver = new BaseReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Globals.EXIT_APP);
			this.registerReceiver(receiver, filter);
		}

		//设置允许使用转场动画
		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
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
	protected void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			this.unregisterReceiver(receiver);
			receiver = null;
		}
	}

	public void back(View view){

	}

	@Override
	protected void onStart() {
		super.onStart();
		((MyAppliction) getApplication()).setTopContext(this);
		Animal.enterAnimation(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Loading.stop();
	}

	private class BaseReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Globals.EXIT_APP)) {
				finish();
			}
		}
	}

	public void exitApp() {
		Intent intent = new Intent();
		intent.setAction(Globals.EXIT_APP);
		this.sendBroadcast(intent);
	}

}
