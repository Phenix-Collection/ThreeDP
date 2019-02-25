package com.tdp.main.activity.userinfo.adapter;

import com.tdp.app.City_CN;
import com.tdp.main.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author:zlcai
 * @new date:2016-11-9
 * @last date:2016-11-9
 * @remark:
 **/

public class ChooseCityAdapter extends BaseAdapter {

	private City_CN sourceData;
	private String level;
	private String[] datas;
	private Context context;
	
	
	public ChooseCityAdapter(Context context, City_CN sourceData){
		this.context = context;
		this.sourceData = sourceData;
	}
	
	public void setDatas(String[] datas, String level){
		if(datas != null){
			this.datas = datas;
		} else {
			datas = new String[0];
		}
		this.level = level;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return datas == null ? 0 : datas.length;
	}

	@Override
	public Object getItem(int position) {
		return datas[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_view_ucenter_userinfo_choosecity, null);
			viewHolder = new ViewHolder();
			viewHolder.titleTv = (TextView) convertView.findViewById(R.id.title_tv);
			viewHolder.goIv = (ImageView) convertView.findViewById(R.id.item_view_ucenter_userinfo_choosecity_go_iv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.titleTv.setText(datas[position]);
		viewHolder.goIv.setVisibility(sourceData.hasCity(level + "_" + position) ? View.VISIBLE : View.GONE);
		
		return convertView;
	}

	static class ViewHolder {
		TextView titleTv;
		ImageView goIv;
	}
}
