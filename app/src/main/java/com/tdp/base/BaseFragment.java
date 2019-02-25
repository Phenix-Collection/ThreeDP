package com.tdp.base;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.sdk.utils.ScreenUtils;
import com.sdk.views.dialog.Loading;
import com.tdp.main.R;


/**
 * @author:zlcai
 * @createrDate:2017/9/2 15:22
 * @lastTime:2017/9/2 15:22
 * @detail:
 **/

public class BaseFragment extends Fragment {


	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);

	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		toImmersion();
	}

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        onVisibilityChanged(hidden);
    }

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent,requestCode, ActivityOptions.makeSceneTransitionAnimation(this.getActivity()).toBundle());
	}

	@Override
	public void startActivity(Intent intent, @Nullable Bundle options) {
		super.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this.getActivity()).toBundle());
	}

	/***
	 *
	 */
	protected void toImmersion(){
//
//		Log.v("app","VERSION:"+Build.VERSION.SDK_INT);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 沉浸式
//			Window window = this.getActivity().getWindow();
//			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//			View v = this.getView().findViewById(R.id.id_menu_top);
//			if (v != null) {
//				int statusHeight = ScreenUtils.getStatusHeight(this.getContext());
//				ViewGroup.LayoutParams lp = v.getLayoutParams();
//				lp.height = statusHeight;
//				//v.setBackgroundColor(getContext().getResources().getColor(R.color.colorBlack));
//				Log.v("app","Height:"+statusHeight);
//				v.setLayoutParams(lp);
//				v.setVisibility(View.VISIBLE);
//			}
//		}
	}



    public void onVisibilityChanged(boolean hidden) {
        //mParentActivityVisible = visible;
    }

	@Override
	public void onStop() {
		super.onStop();
		Loading.stop();
	}

	/**
	 * 获取跟视图
	 */
	public View getRootView() {
		return this.getActivity().getWindow().getDecorView();
	}

}
