package com.sdk.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.sdk.views.dialog.listener.OnDialogClickListener;
import com.tdp.main.R;

public class AlertDialog extends Dialog implements View.OnClickListener, OnDialogClickListener {
	private Context context;
	private Button okBtn, cancelBtn, otherBtn;
	public final static int ACTION_OK = R.id.id_left, ACTION_CANCEL = R.id.id_center, ACTION_OTHER = R.id.id_right;
	private TextView titleTv, messageTv;
	private EditText messageZedt;
	// private String message, title;
	// private static int layout = R.layout.yyd_dialog;
	// private boolean showOther = false;
	// private String okStr = "确定", cancelStr = "放弃", otherStr = "其他";

	private OnDialogClickListener listener;
	public static final int TIP = 0, INPUT = 1, CUSTOM = 2;

	public AlertDialog setOnDialogClickListener(OnDialogClickListener listener) {
		this.listener = listener;
		return this;
	}

	public static AlertDialog getInstance(Context context){
		return new AlertDialog(context);
	}

	private AlertDialog(Context context) {
		super(context, R.style.ViewsDialog);
		getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION);
		initUI(context);
	}

	public AlertDialog(Context context, int theme) {
		super(context, theme);
		initUI(context);
	}

	/***
	 * @detail init UI
	 */
	private void initUI(Context context) {
		this.context = context;
		setContentView(R.layout.zl_dialog_alert);

		okBtn = (Button) findViewById(R.id.id_left);
		cancelBtn = (Button) findViewById(R.id.id_center);
		otherBtn = (Button) findViewById(R.id.id_right);
		titleTv = (TextView) findViewById(R.id.dialog_title_tv);
		messageTv = (TextView) findViewById(R.id.dialog_message_tv);
		messageZedt = (EditText) findViewById(R.id.dialog_message_zedt);
		okBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		otherBtn.setOnClickListener(this);
		this.setCanceledOnTouchOutside(false);
	}

	/***
	 * @detail set title
	 * @param title
	 */
	public AlertDialog setTitle(String title) {
		titleTv.setText(title);
		return this;
	}

	/***
	 * @detail set message
	 * @param message
	 */
	public AlertDialog setMessage(int model, String message) {
		switch(model){
		case TIP:
			messageTv.setVisibility(View.VISIBLE);
			messageZedt.setVisibility(View.GONE);
			messageTv.setText(message);
			break;
		case INPUT:
			messageZedt.setVisibility(View.VISIBLE);
			messageTv.setVisibility(View.GONE);
			messageZedt.setText(message);
			break;
		case CUSTOM:
			break;
		}
		
		messageTv.setText(message);
		return this;
	}

	/***
	 * @detail set show btn
	 * @param submitStr,cancelStr,otherStr
	 */
	public AlertDialog setButton(String submitStr, String cancelStr, String otherStr) {
		okBtn.setText(submitStr);
		cancelBtn.setText(cancelStr);
		otherBtn.setText(otherStr);
		okBtn.setVisibility(View.VISIBLE);
		cancelBtn.setVisibility(View.VISIBLE);
		otherBtn.setVisibility(View.VISIBLE);
		return this;
	}

	/***
	 * @detail set show btn
	 * @param submitStr,cancelStr
	 */
	public AlertDialog setButton(String submitStr, String cancelStr) {
		okBtn.setText(submitStr);
		cancelBtn.setText(cancelStr);
		okBtn.setVisibility(View.VISIBLE);
		cancelBtn.setVisibility(View.VISIBLE);
		otherBtn.setVisibility(View.GONE);
		
		return this;
	}
	
	/**  */
	public String getSimpleInput(){
		return messageZedt.getText().toString();		
	}

	@Override
	public void click(int index, Dialog view) {
		dismiss();
		listener.click(index, view);
	}

	@Override
	public void onClick(View view) {
		click(view.getId(), this);
	}

	@Override
	public void show() {
		super.show();
	}

}
