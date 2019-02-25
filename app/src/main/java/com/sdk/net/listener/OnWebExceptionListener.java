package com.sdk.net.listener;


import com.sdk.net.msg.WebMsg;

/**
 *
 * @author:zlcai
 * @createrDate:2015-3-8 下午4:56:43
 * @lastTime:2015-3-8 下午4:56:43
 * @detail: 
 *
 **/

public interface OnWebExceptionListener {
	/** 访问没有到达服务器出错 */
	public void onNetError(WebMsg webMsg);
	public void onServiceError(WebMsg webMsg);

}
