package com.sdk.net.listener;


import com.sdk.net.msg.WebMsg;

import java.util.Observer;

/**
 * @author:zlcai
 * @new date:2016-12-5
 * @last date:2016-12-5
 * @remark:
 **/

public interface OnResultListener  {
	abstract void onWebUiResult(WebMsg webMsg);
}