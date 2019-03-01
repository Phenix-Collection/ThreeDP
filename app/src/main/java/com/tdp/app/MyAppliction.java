package com.tdp.app;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.chat.HMSPushHelper;
import com.chat.easeui.EaseUI;
import com.faceunity.p2a.FUP2AClient;
import com.sdk.core.CacheImageControl;
import com.sdk.core.Fup2aController;
import com.sdk.core.Globals;
import com.sdk.core.HuanxinControl;
import com.sdk.core.JPushControl;
import com.sdk.core.UserControl;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.listener.OnWebExceptionListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.utils.SystemUtil;
import com.sdk.views.dialog.AlertDialog;
import com.sdk.views.dialog.Toast;
import com.sdk.views.dialog.listener.OnDialogClickListener;

import java.io.InputStream;


/**
 * @author:zlcai
 * @createrDate:2017/7/27 14:30
 * @lastTime:2017/7/27 14:30
 * @detail:
 **/

public class MyAppliction extends Application implements OnWebExceptionListener, OnResultListener {

	private Context topContext = this;

	@Override
	public void onCreate() {
		super.onCreate();
		MultiDex.install(this);
		Globals.init(this, this, true); // 初始化全局类
        init();
		Log.v("---", "helloworld!");

	}


	

	private void init(){
//		OkHttpUtils.initOkHttpUtils(OkHttpUtils.initOkHttpClient(this));
//		FURenderer.initFURenderer(this);
//		try {
//			InputStream clientBin = getAssets().open("p2a_client.bin");
//			byte[] clientBinData = new byte[clientBin.available()];
//			clientBin.read(clientBinData);
//			clientBin.close();
//			FUP2AClient.getFUP2AClient().setupData(clientBinData);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		initChatConfig();
		HuanxinControl.init(this); // 环信
		JPushControl.init(this); // 极光推送控制类
		CacheImageControl.init(this);
		Fup2aController.init(this);

		if (EaseUI.getInstance().isMainProcess(this)) {
			// 初始化华为 HMS 推送服务, 需要在SDK初始化后执行
			HMSPushHelper.getInstance().initHMSAgent(this);
		}
	}


	public void setTopContext(Activity context){
		this.topContext = context;
	}

	@Override
	public void onNetError(WebMsg webMsg) {

	}

	@Override
	public void onServiceError(WebMsg webMsg) {
		switch (webMsg.getErrorCode()){
			case WebMsg.STATUS_USER_NOTLOGIN: // 没有登录
			case WebMsg.STATUS_USER_OUTMODED: // 登录超时

				JPushControl.getInstance().unregisterAlias(this); // 删除极光推送

				AlertDialog.getInstance(topContext).setMessage(AlertDialog.TIP, "您的登录已经失效，是否重新登录？").setButton("重新登录", "不登录").setTitle("登录失效提醒！").setOnDialogClickListener(new OnDialogClickListener() {
					@Override
					public void click(int index, Dialog view) {
					switch (index){
						case AlertDialog.ACTION_OK:
							UserControl.getInstance().relist(MyAppliction.this);
							break;

						case AlertDialog.ACTION_CANCEL:
							SystemUtil.loginOut(topContext);
							break;
					}
					}
				}).show();
				break;
		}
	}

	/***
	 * 退出登录
	 */
	public void loginOut(){
		AlertDialog.getInstance(topContext).setMessage(AlertDialog.TIP, "选择退出用户将删除用户数据，选择注销APP将删除用户数据与初始化数据。").setButton("退出用户", "注销APP", "取消").setTitle("注销账号提醒！").setOnDialogClickListener(new OnDialogClickListener() {
				@Override
				public void click(int index, Dialog view) {
			switch (index){
				case AlertDialog.ACTION_OK:
					SystemUtil.loginOut(MyAppliction.this);
					Toast.show(topContext, "用户退出成功！", Toast.LENGTH_LONG);
					break;
				case AlertDialog.ACTION_CANCEL:
					SystemUtil.loginOutAndClearAll(MyAppliction.this);
					Toast.show(topContext, "APP注销成功！", Toast.LENGTH_LONG);
					break;
				case AlertDialog.ACTION_OTHER:
					Toast.show(topContext, "操作已取消", Toast.LENGTH_SHORT);
					break;
			}
			}
		}).show();
	}

	public void appExit(){
		AlertDialog.getInstance(topContext).setMessage(AlertDialog.TIP, "确定要退出当前账号吗？").setButton("退出", "取消").setTitle("退出当前账号提醒！").setOnDialogClickListener(new OnDialogClickListener() {
			@Override
			public void click(int index, Dialog view) {
				switch (index){
					case AlertDialog.ACTION_OK:
						HuanxinControl.getInstance().loginOut();
						SystemUtil.loginOutAndClearAll(MyAppliction.this);
						//Toast.show(topContext, "APP注销成功！", Toast.LENGTH_LONG);
						break;
					case AlertDialog.ACTION_CANCEL:
						//Toast.show(topContext, "操作已取消", Toast.LENGTH_SHORT);
						break;
				}
			}
		}).show();
	}

	private boolean isBusying = false;
	@Override
	public void onWebUiResult(WebMsg webMsg) {

	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}


}
