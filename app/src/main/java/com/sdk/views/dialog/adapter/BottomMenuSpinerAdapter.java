package com.sdk.views.dialog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.tdp.main.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BottomMenuSpinerAdapter extends BasePopWindowAdapter {

	private View.OnClickListener listener;

	public BasePopWindowAdapter setOnclickListener(View.OnClickListener listener){
		this.listener = listener;
		return this;
	}

	@Override
	public View getView(Context context) {

		View view = LayoutInflater.from(context).inflate(R.layout.zl_dialog_bottom_menu_spiner_item, null);

		ButterKnife.bind(this, view);
		return view;
	}

	@OnClick({R.id.id_cutphoto, R.id.id_file, R.id.id_cancel})
	public void click(View view){
		listener.onClick(view);
//		switch (view.getId()){
//			case R.id.id_cutphoto:
//
//				break;
//			case R.id.id_file:
//
//				break;
//			case R.id.id_cancel:
//
//				break;
//		}
	}
}
