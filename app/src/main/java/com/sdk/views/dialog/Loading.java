package com.sdk.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tdp.main.R;

/**
 * @projectName：baibailai01
 * @className：Loading.java
 * @Description：
 * @author：蔡子良
 * @date：下午8:33:18 2013年10月21日
 */
public class Loading {
	private Dialog dialog = null;
	View view = null;
	private static Loading loading = null;

	/**
	 * 开始
	 * 
	 * @param context
	 */
	public static void start(Context context, boolean isCanCancel) {
		try {
			initUI(context);
			loading.dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					Loading.stop();
				}
			});
			loading.dialog.setCancelable(isCanCancel);
			show(context);
		} catch (Exception e) {
			Log.e("Loading->start", e.toString());
			stop();
		}
	}

	/**
	 * 开始
	 * 
	 * @param context
	 */
	public static void start(Context context) {
		try {
			initUI(context);
			show(context);
		} catch (Exception e) {
			Log.e("Loading->start", e.toString());
			stop();
		}
	}

	/**
	 * 开始
	 * 
	 * @param context
	 */
	public static void start(Context context, String message, boolean isCanCancel) {
		try {
			initUI(context);
			loading.dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					Loading.stop();
				}
			});
			loading.dialog.setCancelable(isCanCancel);
			show(context,message);
		} catch (Exception e) {
			Log.e("Loading->start", e.toString());
			stop();
		}
	}

	/****
	 * @detail 初始化所有控件
	 * @param context
	 */
	private static void initUI(Context context) {
		if (loading == null) {
			loading = new Loading();
		}
		if (loading.dialog == null) {
//			loading.dialog =new AlertDialog.Builder(context).create();

			loading.dialog = new Dialog(context,R.style.ViewsDialog);
			loading.dialog.setCanceledOnTouchOutside(false);
			loading.dialog.setCancelable(true);
		}
	}
	private static void show(Context context){
		try{
			loading.dialog.show();
			if (loading.view == null) {
				loading.view = View.inflate(context, R.layout.zl_dialog_loading,null);
				//ProgressBar pb = (ProgressBar) loading.view.findViewById(R.id.util_loading_pgb);
//				if (android.os.Build.VERSION.SDK_INT > 22) {//android 6.0替换clip的加载动画
//					final Drawable drawable = context.getApplicationContext().getResources().getDrawable(R.drawable.util_loading_progress_60);
//					pb.setIndeterminateDrawable(drawable);
//				}
				loading.dialog.getWindow().setContentView(loading.view);
			}
		}catch(Exception e){}
	}

	private static void show(Context context, String message){
		try{
			loading.dialog.show();
			if (loading.view == null) {
				loading.view = View.inflate(context, R.layout.zl_dialog_loading,null);
			//	ProgressBar pb = (ProgressBar) loading.view.findViewById(R.id.util_loading_pgb);
//				if (android.os.Build.VERSION.SDK_INT > 22) {//android 6.0替换clip的加载动画
//				    final Drawable drawable = context.getApplicationContext().getResources().getDrawable(R.drawable.util_loading_progress_60);
//				    pb.setIndeterminateDrawable(drawable);
//				}
				loading.view.setBackgroundColor(Color.TRANSPARENT);
				View view = loading.view.findViewById(R.id.dialog_loading_message_tv);
				((TextView) view).setText(message);

				loading.dialog.getWindow().setContentView(loading.view);
			}
		}catch(Exception e){}
	}
	
	/**
	 * 关闭
	 */
	public static void stop() {

//		StackTraceElement[] stackElements = new Throwable().getStackTrace();//java 中打印调用栈（判断哪个类调用了此方法）
//		for(StackTraceElement stackTraceElement:stackElements){
//			Log.e("Loading","stack:::"+ stackTraceElement.getClassName() + "->" + stackTraceElement.getMethodName());
//		}
		try{
			if (loading != null && loading.dialog != null)
				loading.dialog.dismiss();
			loading = null;
		}catch(Exception e){}
	}

}
