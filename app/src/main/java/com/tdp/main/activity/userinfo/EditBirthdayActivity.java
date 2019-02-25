package com.tdp.main.activity.userinfo;

import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.sdk.utils.CalendarUtil;
import com.sdk.views.dialog.Loading;
import com.sdk.views.wheelview.NumericWheelAdapter;
import com.sdk.views.wheelview.OnWheelScrollListener;
import com.sdk.views.wheelview.WheelView;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author:zlcai
 * @new date:2017-2-24
 * @last date:2017-2-24
 * @remark: 修改性别
 **/
public class EditBirthdayActivity extends BaseActivity implements OnWheelScrollListener {

//	contentTv = (TextView) this.findViewById(R.id.activity_ucenter_userinfo_birthday_content_tv);

	@BindView(R.id.id_year)
	public WheelView yearWv;
	@BindView(R.id.id_month)
	public WheelView monthWv;
	@BindView(R.id.id_day)
	public WheelView dayWv;
	@BindView(R.id.id_content)
	public TextView contentTv;
	
	private int norYear;
	private int curYear, curMonth, curDay;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_ucenter_userinfo_birthday);
		ButterKnife.bind(this);
		init();
	}

	private void init(){

		Intent intent = getIntent();
		curYear = intent.getIntExtra("year", 2000);
		curMonth = intent.getIntExtra("month", 1);
		curDay = intent.getIntExtra("day", 1);

		Calendar c = Calendar.getInstance();
		norYear = c.get(Calendar.YEAR);
		curYear = getIntent().getIntExtra("year", 2000);
		curMonth = getIntent().getIntExtra("month", 1);
		curDay = getIntent().getIntExtra("day", 1);
		yearWv.setAdapter(new NumericWheelAdapter(norYear - 120, norYear));
		yearWv.setLabel("年");
		yearWv.setCyclic(true);
		yearWv.addScrollingListener(this);
		monthWv.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
		monthWv.setLabel("月");
		monthWv.setCyclic(true);
		monthWv.addScrollingListener(this);
		initDay(curYear,curMonth);
		dayWv.setLabel("日");
		dayWv.setCyclic(true);
		dayWv.addScrollingListener(this);
		yearWv.setCurrentItem(curYear - (norYear - 120));
		monthWv.setCurrentItem(curMonth - 1);
		dayWv.setCurrentItem(curDay - 1);
		contentTv.setText("年龄             "+curYear+"-"+curMonth+"-"+curDay);
	}

	/**
	 */
	private void initDay(int arg1, int arg2) {
		
		if(dayWv.getCurrentItem() > getDay(arg1, arg2)-1){
			dayWv.setCurrentItem(getDay(arg1, arg2) - 1);
		}
		
		dayWv.setAdapter(new NumericWheelAdapter(1, getDay(arg1, arg2), "%02d"));
	}
	
	/**
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
		switch (year % 4) {
		case 0:
			flag = true;
			break;
		default:
			flag = false;
			break;
		}
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			day = 31;
			break;
		case 2:
			day = flag ? 29 : 28;
			break;
		default:
			day = 30;
			break;
		}
		return day;
	}

	@OnClick({R.id.id_back, R.id.id_ok})
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_back:
			finish();
			
			break;
		case R.id.id_ok:
//			Loading.start(this, "正在保存中...", true);
//			LoadWebUcenterApi.updateUserInfo("update", null, null, 0, ""+DateTimeUtil.toMillis(curYear, curMonth-1, curDay), null, 5, this);

			Intent intent = new Intent();
			intent.putExtra("result", curYear+"-"+(curMonth > 9 ? curMonth : "0"+curMonth)+"-"+(curDay > 9 ? curDay : "0" + curDay));
			setResult(1, intent);
			finish();
			break;
		}
	}

	@Override
	public void onScrollingStarted(WheelView wheel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollingFinished(WheelView wheel) {
		curYear = yearWv.getCurrentItem() + (norYear - 120);//
		curMonth = monthWv.getCurrentItem() + 1;//
		curDay = dayWv.getCurrentItem()+1;
		initDay(curYear,curMonth);
		
		contentTv.setText("年龄             "+curYear+"-"+curMonth+"-"+curDay);
	}
	

}
