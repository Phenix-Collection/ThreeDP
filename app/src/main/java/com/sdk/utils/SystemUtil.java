package com.sdk.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.sdk.views.dialog.Loading;
import com.tdp.main.activity.LoginActivity;
import com.tdp.main.activity.WelcomeActivity;
import com.tdp.main.utils.MiscUtil;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author:zlcai
 * @createrDate:2017/10/31 10:43
 * @lastTime:2017/10/31 10:43
 * @detail:
 **/

public class SystemUtil {

	public static void loginOut(Context context){
		Intent intent = new Intent();
		// 通知关闭所有界面
		Loading.stop(); // 这句代码用于停止activity对应的show，防止下一个activity使用时报错
		intent.setAction(Globals.EXIT_APP);
		context.sendBroadcast(intent);
		//Toast.makeText(context, "退出登录成功！", Toast.LENGTH_LONG).show();

		// 启动登录界面
		intent = new Intent();
		CacheDataService.clearUserInfo();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(context, LoginActivity.class);
		context.startActivity(intent);
	}

	public static void loginOutAndClearAll(Context context){
		String finalFilePathPrefix = CacheDataService.getLoginInfo().getUserInfo().getAccount()+"_"+CacheDataService.getLoginInfo().getLoginTime();
		String filepath = Globals.DIR_CACHE_BUNDLE + finalFilePathPrefix;
		File file=new File(filepath);
		if(file.exists()) MiscUtil.deleteFile(file);
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
		intent.setClass(context, LoginActivity.class);
		context.startActivity(intent);
	}

	/***
	 * 判断是否在editText外，如果是则隐藏软键盘
	 * @param v
	 * @param event
	 * @return
	 */
	public static boolean toHideInput(View v, MotionEvent event, Context context) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			//获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				return true;
			}
		}
		return false;
	}

	/***
	 * 判断是否为手机号码
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
}
