package com.sdk.views.Menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sdk.utils.ScreenUtils;
import com.tdp.main.R;


/**
 * @author:zlcai
 * @createrDate:2017/9/2 16:20
 * @lastTime:2017/9/2 16:20
 * @detail:
 **/

public class TopMenuView extends RelativeLayout implements View.OnClickListener {

	public static final int CLICK_LEFT = R.id.id_left, CLICK_CENTER = R.id.id_title,
			CLICK_RIGHT1 = R.id.id_menu_right1_icon, CLICK_RIGHT2 = R.id.id_menu_right2_icon,
			CLICK_RIGHT3 = R.id.id_menu_right3_icon, CLICK_RIGH_LABEL = R.id.id_menu_right_label;

	private LinearLayout leftLl, centerLl;
	private int leftIcon, centerIcon, right1Icon, right2Icon, right3Icon; // 图标
	private int leftTextId, centerTextId, rightTextId; // 文字
	private int textColor, rightTextColor; // 颜色
	private TextView leftTv, centerTv, rightTv;
	private ImageView leftIv, right1Iv, right2Iv, right3Iv;
	private OnClickListener listener;

	private Context context;


	public TopMenuView(Context context) {
		super(context);
		this.context = context;
	}

	public TopMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// UI
		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TopMenuView);
		LayoutInflater.from(context).inflate(R.layout.zl_menu_top, this, true);
		leftIv = (ImageView) this.findViewById(R.id.id_menu_left_icon);
		right1Iv = (ImageView) this.findViewById(R.id.id_menu_right1_icon);
		right2Iv = (ImageView) this.findViewById(R.id.id_menu_right2_icon);
		right3Iv = (ImageView) this.findViewById(R.id.id_menu_right3_icon);
		leftTv = (TextView) this.findViewById(R.id.id_menu_left_label);
		centerTv = (TextView) this.findViewById(R.id.id_title);
		rightTv = (TextView) this.findViewById(R.id.id_menu_right_label);

		// --- 颜色
		textColor = attributes.getColor(R.styleable.TopMenuView_textColor, Color.WHITE);
		rightTextColor = attributes.getColor(R.styleable.TopMenuView_rightTextColor, Color.WHITE);

		// --- 图标
		leftIcon = attributes.getResourceId(R.styleable.TopMenuView_leftIcon, 0);
		centerIcon = attributes.getResourceId(R.styleable.TopMenuView_centerIcon, 0);
		right1Icon = attributes.getResourceId(R.styleable.TopMenuView_right1Icon, 0);
		right2Icon = attributes.getResourceId(R.styleable.TopMenuView_right2Icon, 0);
		right3Icon = attributes.getResourceId(R.styleable.TopMenuView_right3Icon, 0);


		// -- 左边文字
		leftTextId = attributes.getResourceId(R.styleable.TopMenuView_leftText, 0);
		centerTextId = attributes.getResourceId(R.styleable.TopMenuView_centerText, 0);
		rightTextId = attributes.getResourceId(R.styleable.TopMenuView_rightText, 0);

	}

	public void setRightTextColor(int color){
		rightTv.setTextColor(color);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		leftLl = (LinearLayout) this.findViewById(R.id.id_left);
		centerLl = (LinearLayout) this.findViewById(R.id.id_center);

		// data
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 沉浸式
//			View v = this.findViewById(R.id.id_menu_top);
//			if (v != null) {
//				int statusHeight = ScreenUtils.getStatusHeight(this.getContext());
//				ViewGroup.LayoutParams lp = v.getLayoutParams();
//				lp.height = statusHeight;
//				v.setLayoutParams(lp);
//				v.setVisibility(View.VISIBLE);
//
////				SystemBarTintManager tintManager = new SystemBarTintManager((Activity) context);
////				tintManager.setStatusBarTintEnabled(true);
////				tintManager.setStatusBarTintResource(R.color.action_bar);
//
//			}
//		}

		if(leftIcon != 0){
			leftIv.setImageResource(leftIcon);
		}

		if(leftTextId != 0){
			leftTv.setText(leftTextId);
			leftTv.setTextColor(textColor);
		}

		if(centerTextId != 0){
			centerTv.setText(centerTextId);
			centerTv.setTextColor(textColor);
		}

		if(rightTextId != 0){
			rightTv.setText(rightTextId);
			rightTv.setTextColor(rightTextColor == 0 ? textColor : rightTextColor);
		}

		if(right1Icon != 0){
			right1Iv.setImageResource(right1Icon);
		}

		if(right2Icon != 0){
			right2Iv.setImageResource(right2Icon);
		}

		if(right3Icon != 0){
			right3Iv.setImageResource(right3Icon);
		}

		leftIv.setVisibility(leftIcon == 0 ? View.GONE : View.VISIBLE);
		leftTv.setVisibility(leftTextId == 0 ? View.GONE : View.VISIBLE);
		leftLl.setVisibility(leftIcon == 0 && leftTextId == 0 ? View.GONE : View.VISIBLE);
		centerLl.setVisibility(centerIcon == 0 && centerTextId == 0 ? View.GONE : View.VISIBLE);
		rightTv.setVisibility(rightTextId == 0 ? View.GONE : View.VISIBLE);
		right1Iv.setVisibility(right1Icon == 0 ? View.GONE : View.VISIBLE);
		right2Iv.setVisibility(right2Icon == 0 ? View.GONE : View.VISIBLE);
		right3Iv.setVisibility(right3Icon == 0 ? View.GONE : View.VISIBLE);

		// Listener
		leftLl.setOnClickListener(this);
		centerTv.setOnClickListener(this);
		right1Iv.setOnClickListener(this);
		right2Iv.setOnClickListener(this);
		right3Iv.setOnClickListener(this);
		rightTv.setOnClickListener(this);
	}

	public void setTitle(String text){
		centerTv.setText(text);
	}

	public void setOnClickListener(OnClickListener listener){
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		if(listener == null) return;

		listener.onClick(v);
	}


}
