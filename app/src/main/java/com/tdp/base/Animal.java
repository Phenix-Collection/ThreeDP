package com.tdp.base;

import android.app.Activity;
import android.transition.Slide;
import android.view.Gravity;

import com.sdk.views.dialog.Toast;
import com.tdp.main.R;

/**
 * @author:zlcai
 * @createrDate:2017/9/21 15:20
 * @lastTime:2017/9/21 15:20
 * @detail:
 **/

public abstract class Animal {

	/**
	 * 进场动画
	 */
	public static void enterAnimation(Activity context){
	//	Toast.show(context, "test...", android.widget.Toast.LENGTH_LONG);
		//context.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
		context.getWindow().setEnterTransition(new Slide(Gravity.LEFT).setDuration(500));
		context.getWindow().setEnterTransition(new Slide(Gravity.RIGHT).setDuration(500));
	}

	/**
	 * 退场动画
	 */
	public static void endAnimation(Activity context){
		//context.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
	}
}
