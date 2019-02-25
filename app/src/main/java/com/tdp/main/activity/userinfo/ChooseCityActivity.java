package com.tdp.main.activity.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.tdp.app.City_CN;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.activity.userinfo.adapter.ChooseCityAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author:zlcai
 * @new date:2016-11-9
 * @last date:2016-11-9
 * @remark: 选择城市
 **/

public class ChooseCityActivity extends BaseActivity {

	@BindView(R.id.id_content)
	public ListView contentLv;
	public ChooseCityAdapter provinceAdapter, cityAdapter, areaAdapter;
	private int provinceId, cityId, areaId;
	private City_CN city; // 城市数据对象
	private int currentIndex = 1;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_ucenter_userinfo_choosecity);

		ButterKnife.bind(this);
		init();
	}

	private void init(){
		//
		city = new City_CN(3);

		provinceAdapter = new ChooseCityAdapter(this, city);
		cityAdapter = new ChooseCityAdapter(this, city);
		areaAdapter = new ChooseCityAdapter(this, city);
		contentLv.setAdapter(provinceAdapter);

		provinceAdapter.setDatas(city.getCity("0"),"0"); // 设置所有省份到布局
		//
		contentLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch (currentIndex){
					case 1:
						currentIndex = 2;
						contentLv.setAdapter(cityAdapter);
						cityAdapter.setDatas(city.getCity("0_"+position),"0_"+position);
						provinceId = position;
						break;
					case 2:
						currentIndex = 3;
						if(!city.hasCity("0_"+provinceId + "_" + position)){
							String provice = (String)provinceAdapter.getItem(provinceId);
							String city = (String) cityAdapter.getItem(cityId);
							Intent intent = new Intent();
							intent.putExtra("result", provice + "-"+ city);
							setResult(1, intent);
							finish();
							return;
						}
						contentLv.setAdapter(areaAdapter);
						areaAdapter.setDatas(city.getCity("0_"+provinceId + "_" + position),"0_"+provinceId + "_" + position);
						cityId = position;
						break;
					case 3:
						areaId = position;
						String provice = (String)provinceAdapter.getItem(provinceId);
						String city = (String) cityAdapter.getItem(cityId);
						String area = (String) areaAdapter.getItem(areaId);

						Intent intent = new Intent();
						intent.putExtra("result", provice + "-"+ city + "-" + area);
						setResult(1, intent);
						finish();
						break;
				}
			}
		});
	}

	@OnClick({R.id.id_back})
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_back:
			currentIndex --;
			switch (currentIndex){
				case 0:
					onBack();
					break;
				case 1:
					contentLv.setAdapter(provinceAdapter);
					break;
				case 2:
					contentLv.setAdapter(cityAdapter);
					break;
			}
			break;
		}

	}
	

}
