package com.sdk.utils;

import android.util.Log;

import com.sdk.core.Globals;


/**
 * @author:zlcai
 * @createrDate:2017/7/28 10:36
 * @lastTime:2017/7/28 10:36
 * @detail:
 **/

public class APLog {

	public static void e(String tag, String msg) {
		if(Globals.isDebug()) {
			Log.e(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if(Globals.isDebug()) {
			Log.v(tag, msg);
		}
	}


	public static void d(String tag, String msg) {
		if(Globals.isDebug()) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if(Globals.isDebug()) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if(Globals.isDebug()) {
			Log.w(tag, msg);
		}
	}
}
