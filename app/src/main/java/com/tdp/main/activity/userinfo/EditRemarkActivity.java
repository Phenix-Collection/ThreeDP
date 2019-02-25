package com.tdp.main.activity.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.sdk.api.WebFriendApi;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.views.dialog.Loading;
import com.sdk.views.dialog.Toast;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;

import org.apache.tools.ant.taskdefs.condition.Http;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author:zlcai
 * @new date:2017-2-24
 * @last date:2017-2-24
 * @remark: 修改性别
 **/
public class EditRemarkActivity extends BaseActivity {
	
	@BindView(R.id.id_remark)
	EditText remarkEdt;

	private String account;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_ucenter_userinfo_remark);
		ButterKnife.bind(this);
		init();
	}
	
	private void init(){
		//
		String remark = getIntent().getStringExtra("remark");
		account = getIntent().getStringExtra("account");

		remarkEdt.setText(remark);
	}

	boolean isBusying = false;
	@OnClick({R.id.id_back, R.id.id_ok})
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_back:
			onBack();
			break;
		case R.id.id_ok: // 男

			if(isBusying) return;

			final String remark = remarkEdt.getText().toString().trim();
			if(remark.length() == 0){
				Toast.show(this, "备注名不能为空！", Toast.LENGTH_SHORT);
				return;
			}

			isBusying = true;
			Loading.start(this, "正在保存信息，请稍等..", true);
			HttpRequest.instance().doPost(HttpRequest.create(WebFriendApi.class).modifyAlias(remark, account), new OnResultListener() {
				@Override
				public void onWebUiResult(WebMsg webMsg) {
					isBusying = false;
					Loading.stop();

					if(webMsg.isSuccess()){
						Intent intent = new Intent();
						intent.putExtra("remark", remark);
						setResult(1, intent);
						finish();
					} else {
						webMsg.showMsg(EditRemarkActivity.this);
					}
				}
			});

			break;
		}
	}
}
