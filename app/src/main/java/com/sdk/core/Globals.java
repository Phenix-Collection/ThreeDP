package com.sdk.core;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.sdk.db.BaseDataService;
import com.sdk.net.listener.OnWebExceptionListener;
import com.sdk.utils.SDCardUtils;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author:zlcai
 * @createrDate:2017/7/27 14:24
 * @lastTime:2017/7/27 14:24
 * @detail:
 **/

public class Globals {

	private static Context context;
	/** 是否为调试模式 */
	public static boolean isDebug = false;
	public static String EXIT_APP = "exit_app";
	public static String APP_KEY = "7DLF3E218C10F53CB6A439D388E1B115";
	public static OnWebExceptionListener onWebExceptionListener;
	public static int SECOND_NUMBER;

	public static final String DICMFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
	// 本地路径
	public static final String DIR_CACHE_BASE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "LifeStory" + File.separator;
	public static final String DIR_CACHE_BUNDLE =  DIR_CACHE_BASE + "bundle" + File.separator;



	// 基础网址
//	public static String BASE_API = "http://47.106.180.218:8090/LifeStroy/";
	//public static String BASE_API = "http://192.168.0.134:8080/LifeStroy/";
	public static String BASE_API = "http://119.23.189.158:8080/LifeStroy/";
	public static String WEB_API = "";
	public static String TOKEN="";
	public static String APPLICTION_ID = "com.tdp.main";
	// 临时文件目录
	public static String DIR_CACHE_IMAGES = "";
	// 缓存文件目录
	public static String DIR_CACHE = "";
	// 外网网站



	public static void init(Application context, OnWebExceptionListener onWebExceptionListener, boolean isDebug){
		Globals.isDebug = isDebug;
		Globals.context = context;
		Globals.onWebExceptionListener = onWebExceptionListener;
		BaseDataService.init(context);

		if (SDCardUtils.isSDCardEnable()) {
			Globals.DIR_CACHE = Environment.getExternalStorageDirectory().toString()+"/lifeStory";

		} else {
			Globals.DIR_CACHE = context.getFilesDir().toString();
		}

		Globals.DIR_CACHE_IMAGES = Globals.DIR_CACHE + "/images/temp/";

		File file = new File(Globals.DIR_CACHE_IMAGES);
		if (!file.isDirectory()) {
			boolean isOK = file.mkdirs();
			Log.v("", "成功：" + isOK + "-" + file.getAbsolutePath());
		}
	}

	public static OnWebExceptionListener getOnWebExceptionListener() {
		return onWebExceptionListener;
	}

	public static void setOnWebExceptionListener(OnWebExceptionListener onWebExceptionListener) {
		Globals.onWebExceptionListener = onWebExceptionListener;
	}

	/***
	 * 获取上下文（使用前该类必须先初始化）
	 * @return
	 */
	public static Context getContext(){
		return Globals.context;
	}

	/***
	 * 是否为调试模式
	 * @return
	 */
	public static boolean isDebug(){
		return isDebug;
	}

	public static int GENDER_INTENT_KEY;//0是男1是女


}
