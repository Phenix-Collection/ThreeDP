package com.tdp.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sdk.api.WebFriendApi;
import com.sdk.api.entity.AddFriendEntity;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.views.Menu.TopMenuView;
import com.sdk.views.dialog.Loading;
import com.sdk.views.dialog.Toast;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.utils.MiscUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ahtor: super_link
 * date: 2018/9/27 15:08
 * remark:
 */
public class AddFriendActivity extends BaseActivity {
    @BindView(R.id.id_menu)
    TopMenuView menuView;
    @BindView(R.id.id_detail)
    EditText detailEdt;

    private String account;
    private String nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        //data
        Intent intent = getIntent();
        account = intent.getStringExtra("account");
       // nickName = intent.getStringExtra("nickName");
        detailEdt.setText("我是" + CacheDataService.getLoginInfo().getUserInfo().getNickName());
        detailEdt.setSelection(detailEdt.getText().length());
        detailEdt.requestFocus();

        // listener
        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case TopMenuView.CLICK_LEFT:
                        finish();
                        break;
//                    case TopMenuView.CLICK_CENTER:
//
//                        break;
                }
            }
        });
    }


    private boolean isBusying = false;
    @OnClick({R.id.id_clear, TopMenuView.CLICK_LEFT, TopMenuView.CLICK_RIGH_LABEL})
    public void onclick(View v){
        switch (v.getId()){
            case R.id.id_clear:
                detailEdt.setText("");
                break;
            case TopMenuView.CLICK_LEFT:
                onBack();
                break;
            case TopMenuView.CLICK_RIGH_LABEL:
                if(isBusying) return;
                isBusying = true;
                Loading.start(this, "正在提交请求中...", false);
                String remark = MiscUtil.stringToUnicode(detailEdt.getText().toString());
                if(remark.length()>120)
                    android.widget.Toast.makeText(this, "留言不能太长哦~", Toast.LENGTH_SHORT).show();
                else {
                    HttpRequest.instance().doPost(HttpRequest.create(WebFriendApi.class).friendApplication(account, remark), new OnResultListener() {
                        @Override
                        public void onWebUiResult(WebMsg webMsg) {
                            isBusying = false;
                            Loading.stop();
                            if (webMsg.isSuccess()) {
                                Toast.show(AddFriendActivity.this, "请求成功，请耐心等待对方同意！", Toast.LENGTH_LONG);
                                setResult(1, null);
                                finish();
                            } else {
                                webMsg.showMsg(AddFriendActivity.this);
                            }
                        }
                    });
                }
                break;
        }
    }
}
