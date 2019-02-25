package com.sdk.views.dialog;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.sdk.views.dialog.adapter.BasePopWindowAdapter;
import com.sdk.views.dialog.adapter.NormalSpinerAdapter;
import com.tdp.main.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PopWindow extends PopupWindow {

	private Activity mContext;
	@BindView(R.id.id_content)
	RelativeLayout contentRl;

	private float alpha = 1f;

	public PopWindow(Activity context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		View contentView = LayoutInflater.from(mContext).inflate(R.layout.zl_dialog_pop, null);
		setContentView(contentView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);

		setFocusable(true);
		setBackgroundDrawable(new ColorDrawable(-000000));


		ButterKnife.bind(this, this.getContentView());

		this.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				new Thread(new Runnable(){
					@Override
					public void run() {
						//此处while的条件alpha不能<= 否则会出现黑屏
						while(alpha<1f){
							try {
								Thread.sleep(4);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							Message msg =mHandler.obtainMessage();
							msg.what = 1;
							alpha+=0.01f;
							msg.obj =alpha ;
							mHandler.sendMessage(msg);
						}
					}

				}).start();
			}
		});
	}

	public PopWindow addView(BasePopWindowAdapter adapter){
		contentRl.removeAllViews();
		contentRl.addView(adapter.getView(mContext));
		return this;
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(alpha>0.6f){
					try {
						//4是根据弹出动画时间和减少的透明度计算
						Thread.sleep(4);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Message msg =mHandler.obtainMessage();
					msg.what = 1;
					//每次减少0.01，精度越高，变暗的效果越流畅
					alpha-=0.01f;
					msg.obj =alpha ;
					mHandler.sendMessage(msg);
				}
			}

		}).start();

	}

	/**
	 * 设置添加屏幕的背景透明度
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha)
	{
		WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
		lp.alpha = bgAlpha; //0.0-1.0
		mContext.getWindow().setAttributes(lp);
		mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}

	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 1:
					backgroundAlpha((float)msg.obj);
					break;
			}
		}
	};

}
