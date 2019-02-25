package com.sdk.views.dialog.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tdp.main.R;

public class NormalSpinerAdapter extends BaseAdapter {

	private Context context;
	private List<String> vals = new ArrayList<String>();
	private List<Integer> icons = new ArrayList<>();
	private LayoutInflater mInflater;

	public NormalSpinerAdapter(Context context) {
		this.context = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setDatas(List<String> vals, List<Integer> icons) {

		if (vals == null) {
			vals = new ArrayList<>();
		}

		this.icons = icons;
		this.vals = vals;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return vals.size();
	}

	@Override
	public Object getItem(int position) {
		return vals.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

	     if (convertView == null) {
	    	 convertView = mInflater.inflate(R.layout.zl_dialog_spiner_item, null);
	         viewHolder = new ViewHolder();
	         viewHolder.mTextView = (TextView) convertView.findViewById(R.id.textView);
	         viewHolder.iconIv = (ImageView) convertView.findViewById(R.id.views_spiner_item_layout_icon_iv);
	         convertView.setTag(viewHolder);
	     } else {
	         viewHolder = (ViewHolder) convertView.getTag();
	     }

	     String item = (String) getItem(position);
		 viewHolder.mTextView.setText(item);

//		 LayoutParams lp = viewHolder.mTextView.getLayoutParams();
//		 lp.width = context.getResources().getDimensionPixelOffset(R.dimen.x16) * item.length();
//		 viewHolder.mTextView.setLayoutParams(lp);

		 if(icons!= null && icons.size() > position){
			 viewHolder.iconIv.setImageResource(icons.get(position));
		 } else {
			 viewHolder.iconIv.setVisibility(View.GONE);
		 }

		// convertView.findViewById(R.id.id_item_solid).setVisibility(vals.size() - 1 > position ? View.VISIBLE : View.GONE);

	     return convertView;
	}

	private static class ViewHolder{
		TextView mTextView;
		ImageView iconIv;
	}

	public static interface IOnItemSelectListener{
		public void onItemClick(int pos);
	};
}
