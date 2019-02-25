package com.sdk.views.dialog;

import android.content.Context;

/**
 * @author:zlcai
 * @new date:2016-12-6
 * @last date:2016-12-6
 * @remark:
 **/

public class Toast extends android.widget.Toast{

	private static android.widget.Toast toast;

	private Toast(Context context) {
		super(context);
	}

	public static void show(Context context, String msg, int duration) {
		if (toast == null) {
			toast = Toast.makeText(context, msg, duration);
		} else {
			toast.setText(msg);
		}
		toast.setDuration(duration);
		toast.show();
	}
}
