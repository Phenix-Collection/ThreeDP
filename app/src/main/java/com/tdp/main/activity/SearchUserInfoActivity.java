package com.tdp.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sdk.api.WebFriendApi;
import com.sdk.api.entity.UserInfoEntity;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.views.dialog.Loading;
import com.sdk.views.dialog.Toast;
import com.sdk.views.listview.PullListView;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.adapter.SearchUserAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ahtor: super_link
 * date: 2018/10/26 15:54
 * remark: 查找用户信息
 */
public class SearchUserInfoActivity extends BaseActivity {

    private List<UserInfoEntity> datas;
    private SearchUserAdapter adapter;
    @BindView(R.id.id_content)
    PullListView contentLv;
    @BindView(R.id.id_account)
    EditText accountEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serach_user);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        adapter = new SearchUserAdapter(this);
        contentLv.setAdapter(adapter);

        contentLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 准备跳用户详情
                UserInfoEntity data = (UserInfoEntity) adapter.getItem(position);
                Intent intent = new Intent();
                intent.setClass(SearchUserInfoActivity.this, UserInfoActivity.class);
                intent.putExtra("account", data.getAccount());
                intent.putExtra("nickname", data.getNickName());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                SearchUserInfoActivity.this.startActivity(intent);
            }
        });
    }

    private boolean isBusying = false;

    @OnClick({R.id.id_ok, R.id.id_back})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.id_back:
                onBack();
                break;
            case R.id.id_ok: // 搜索

                if(isBusying) return;

                String account = accountEdt.getText().toString();
                if(account.length() < 1){
                    Toast.show(this, "输入内容不能为空！", Toast.LENGTH_SHORT);
                    return;
                }

                isBusying = true;
                Loading.start(this, "正在搜索中...", false);
                HttpRequest.instance().doPost(HttpRequest.create(WebFriendApi.class).searchUser(account), new OnResultListener() {
                    @Override
                    public void onWebUiResult(WebMsg webMsg) {
                        Loading.stop();
                        isBusying = false;
                        if(webMsg.isSuccess()){
                            datas = new Gson().fromJson(webMsg.getData(), new TypeToken<List<UserInfoEntity>>(){}.getType());
                            adapter.setDatas(datas);
                        } else {
                            webMsg.showMsg(SearchUserInfoActivity.this);
                        }
                    }
                });
                break;
        }
    }

}
