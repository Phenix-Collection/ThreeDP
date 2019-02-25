package com.sdk.views.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tdp.main.R;

/**
 * @author:zlcai
 * @new date:2017-3-3
 * @last date:2017-3-3
 * @remark: 
 **/
public class PageRefreshView extends RelativeLayout {
	
	private Context context;

	public PageRefreshView(Context context) {
		super(context);
		this.context = context;
		this.setGravity(RelativeLayout.CENTER_VERTICAL);
	}
	
	public PageRefreshView(Context context,AttributeSet attrs){
		 super(context, attrs);
		 this.context = context;
		 this.setGravity(RelativeLayout.CENTER_VERTICAL);
	}
	
	public void showLoading(){
		this.removeAllViews();
		View v = LayoutInflater.from(context).inflate(R.layout.zl_refresh_page, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		v.setBackgroundResource(R.color.transparent);
		v.setLayoutParams(params);
		this.addView(v);
		this.setVisibility(View.VISIBLE);
	}
	
	public void showNetError(OnClickListener onClickListener, String msg){
		this.removeAllViews();
		View v = LayoutInflater.from(context).inflate(R.layout.zl_refresh_page_neterror, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		v.setBackgroundResource(R.color.transparent);
		v.setLayoutParams(params);
		this.addView(v);
		this.setVisibility(View.VISIBLE);
		if(onClickListener != null){
			this.findViewById(R.id.item_status_neterror_retry_btn).setOnClickListener(onClickListener);
		}

		TextView msgTv = (TextView) v.findViewById(R.id.item_status_nodata_msg_tv);
		msgTv.setText(msg);
	}
	
	public void showNoData(){
		showNoData("暂无相关数据...");
	}
	
	public void showNoData(String msg){
		this.removeAllViews();
		View v = LayoutInflater.from(context).inflate(R.layout.zl_refresh_page_nodata, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		v.setBackgroundResource(R.color.transparent);
		v.setLayoutParams(params);
		this.addView(v);
		this.setVisibility(View.VISIBLE);
		
		TextView msgTv = (TextView) v.findViewById(R.id.item_status_nodata_msg_tv);
		msgTv.setText(msg);
	}
	
	
	
	public void hide(){
		this.setVisibility(View.GONE);
	}
	

}
