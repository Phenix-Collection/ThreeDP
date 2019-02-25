package com.sdk.views.dialog;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import com.sdk.views.dialog.adapter.NormalSpinerAdapter;
import com.tdp.main.R;

public class SpinerPopWindow extends PopupWindow implements OnItemClickListener {

	private Activity mContext;
	private ListView mListView;
	private NormalSpinerAdapter mAdapter;
	private NormalSpinerAdapter.IOnItemSelectListener mItemSelectListener;


	private View contentView; 

	public SpinerPopWindow(Activity context) {
		super(context);
		mContext = context;
		init();
	}

	public void setItemListener(NormalSpinerAdapter.IOnItemSelectListener listener) {
		mItemSelectListener = listener;
	}

	private void init() {
		contentView = LayoutInflater.from(mContext).inflate(R.layout.zl_dialog_spiner, null);
		setContentView(contentView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);

		setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x00);
		setBackgroundDrawable(dw);

		mListView = contentView.findViewById(R.id.listview);
		mListView.setOnItemClickListener(this);


	}



	public void setAdatper(NormalSpinerAdapter adapter) {
		mAdapter = adapter;
		mListView.setAdapter(mAdapter);
	}
	
	public void refreshData(List<String> list, List<Integer> icons) {
		mAdapter.setDatas(list, icons);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		dismiss();
		if (mItemSelectListener != null) {
			mItemSelectListener.onItemClick(pos);
		}
	}


}
