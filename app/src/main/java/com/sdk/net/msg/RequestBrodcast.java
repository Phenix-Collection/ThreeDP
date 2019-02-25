package com.sdk.net.msg;

import android.content.Context;
import android.content.Intent;

/**
 * @author:zlcai
 * @createrDate:2017/7/28 11:09
 * @lastTime:2017/7/28 11:09
 * @detail:
 **/

public class RequestBrodcast {

	public static String ACTION = "downloading";

	private Context context;
	private String cmd;
	private int total;
	private int curr;
	private int webcode;

	public RequestBrodcast(Context context, String cmd){
		this.context = context;
		this.cmd = cmd;
	}

	private RequestBrodcast(Context context, String cmd, int total, int curr, int webcode){
		this.context = context;
		this.cmd = cmd;
	}

	public static RequestBrodcast createInstance(Context context, Intent intent){

		if(intent != null){
			String cmd = intent.getStringExtra("cmd");
			int total = intent.getIntExtra("total", -1);
			int curr = intent.getIntExtra("curr", -1);
			int webcode = intent.getIntExtra("webcode", -1);

			// cmd,curr,total,webcode参数缺一不可
			if(cmd != null && curr != -1 && total != -1 && webcode != -1){
				return new RequestBrodcast(context, cmd, total, curr, webcode);
			}
		}

		return null;
	}



	public void sendBrodcast(int total, int curr, int webcode){
		this.total = total;
		this.curr = curr;
		this.webcode = webcode;

		Intent intent = new Intent();
		intent.setAction(ACTION);
		intent.putExtra("cmd", cmd);
		intent.putExtra("total", total);
		intent.putExtra("curr", curr);
		intent.putExtra("webcode", webcode);
		this.context.sendBroadcast(intent);
	}

	public int getTotal() {
		return total;
	}

	public int getCurr() {
		return curr;
	}

	public int getWebcode() {
		return webcode;
	}

	/***
	 * 状态是否正常
	 * @return false时改cmd下的任务失败
	 */
	public boolean getStatus(){
		return webcode == 200;
	}

	/**
	 * 获取进度值
	 * @return
	 */
	public int getPercentage(){
		return curr * 100 / total;
	}
}
