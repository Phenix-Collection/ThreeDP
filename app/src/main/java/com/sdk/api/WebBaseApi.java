package com.sdk.api;

import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.sdk.utils.MD5;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:zlcai
 * @createrDate:2017/8/16 11:44
 * @lastTime:2017/8/16 11:44
 * @detail:
 **/

public abstract class WebBaseApi {

	/***
	 * 获取具有TOKEN验证的参数
	 * @return
	 */
	protected static Map<String, Object> getTokenParams(){

		LoginInfoEntity loginInfo = CacheDataService.getLoginInfo();
		Map<String,Object> params = new HashMap<String,Object>();
		if(loginInfo != null){
//			params.put("SECURITY-CODE", MD5.md5(loginInfo.getPersonId() + loginInfo.getSecurityCode() + Globals.APP_KEY).toUpperCase());
//			params.put("ACCOUNT-ID", loginInfo.getPersonId());
//			params.put("LOGIN-TIME", loginInfo.getLoginTime());
		}

		return params;
	}

	public static String getSecurity(String... strs){
		String securityStr = "";
		for(String str : strs){
			securityStr += str;
		}
		return MD5.md5(MD5.md5(securityStr + getSecurityTime()).toUpperCase()+ Globals.APP_KEY).toUpperCase();
	}

	protected static long getSecurityTime(){
		return System.currentTimeMillis() / 60000;
	}


}
