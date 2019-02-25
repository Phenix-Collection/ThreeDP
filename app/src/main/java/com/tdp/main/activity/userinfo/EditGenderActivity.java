package com.tdp.main.activity.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
public class EditGenderActivity extends BaseActivity {
	
	@BindView(R.id.id_man_ok)
	public ImageView manOkIv;
	@BindView(R.id.id_female_ok)
	public ImageView femaleOkIv;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_ucenter_userinfo_gender);
		ButterKnife.bind(this);
		init();
	}
	
	private void init(){
		//
		int sex = getIntent().getIntExtra("sex", 0);
		manOkIv.setVisibility(sex != 2 ? View.VISIBLE : View.GONE);
		femaleOkIv.setVisibility(sex == 2 ? View.VISIBLE : View.GONE);
	}

	@OnClick({R.id.id_back, R.id.id_man, R.id.id_female})
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_back:
			onBack();
			break;
		case R.id.id_man: // 男
			Intent intent = new Intent();
			intent.putExtra("result", 1);
			setResult(1, intent);
			finish();
			break;
		case R.id.id_female: // 女
			intent = new Intent();
			intent.putExtra("result", 2);
			setResult(1, intent);
			finish();
			break;

		}
	}
}
