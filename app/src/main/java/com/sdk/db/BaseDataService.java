package com.sdk.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * author:zlcai
 * new date:2016-10-18
 * last date:2016-10-18
 * remark:
 **/

public class BaseDataService {
	
	public static Context context;
	private static SharedPreferences sharedPreferences;
	private static SharedPreferences.Editor editor;
	private static BaseDataService instance;
	private static String NAME = "APP_INFO";
	public static String DATA_FRIEND_REQUEST = "dataFriendRequest"; // 好友请求统计数
	public static String DATA_MESSAGE_TIME = "dataMessageTime"; // 消息更新时间
	public static String DATA_MESSAGE_CACHE = "dataMessageCache"; // 消息缓存
	public static String DATA_FRIEND_DATA = "dataFriendData"; // 好友数据

	private BaseDataService(Context context){
		BaseDataService.context = context;
		sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	}

	public static void init(Context context){
		if(instance == null){
			instance = new BaseDataService(context){};
		}
	}

	private synchronized static BaseDataService getInstance(){
		if(instance != null){
			return instance;
		} else {
			throw new RuntimeException("please init first!");
		}
	}

//	private static SharedPreferences getSharePerference(){
//		return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
//	}
	
	public static String getValueByString(String key){
		return sharedPreferences.getString(key, null);
	}

	public static String getValueByString(String key, String defaultVal){
		return sharedPreferences.getString(key, defaultVal);
	}
	
	public static int getValueByInt(String key){
		return sharedPreferences.getInt(key, -1);
	}

	public static int getValueByInt(String key, int defaultVal){
		return sharedPreferences.getInt(key, defaultVal);
	}
	
	public static float getValueByFloat(String key) {
		return sharedPreferences.getFloat(key, -1);
	}
	
	public static boolean getValueByBoolean(String key){
		return sharedPreferences.getBoolean(key, false);
	}

	public static boolean getValueByBoolean(String key, boolean defaultVal){
		return sharedPreferences.getBoolean(key, defaultVal);
	}
	
	public static long getValueByLong(String key){
		return sharedPreferences.getLong(key, -1);
	}

    public static long getValueByLong(String key, long defaultVal){
        return sharedPreferences.getLong(key, defaultVal);
    }
	
	public static void saveValueToSharePerference(String key, boolean value){
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static void saveValueToSharePerference(String key, int value){
		editor.putInt(key, value);
		editor.commit();
	}

    public static void saveValueToSharePerference(String key, long value){
        editor.putLong(key, value);
        editor.commit();
    }
	
	public static void saveValueToSharePerference(String key, String value){
		editor.putString(key, value);
		editor.commit();
	}

	public static void remove(String key){
		editor.remove(key);
		editor.commit();
	}
	
	public static void clear(){
		editor.clear();
		editor.commit();
	}
	
}
