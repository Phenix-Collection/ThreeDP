package com.tdp.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.chat.activity.ToChatActivity;
import com.chat.easeui.EaseConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sdk.api.WebFriendApi;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.views.listview.PullListView;
import com.sdk.views.listview.pulllistview.PullToRefreshLayout;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.adapter.InteractiveFriendAdapter;
import com.tdp.main.entity.FriendInfoEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ahtor: super_link
 * date: 2019/1/28 17:00
 * remark:
 */
public class FriendListActivity extends BaseActivity {

    @BindView(R.id.id_refresh)
    PullToRefreshLayout refreshPRl;
    @BindView(R.id.id_content)
    PullListView contentPlv;
    InteractiveFriendAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R .layout.fragment_friend_list);

        ButterKnife.bind(this, this);
        init();
        loadFriendData();
    }

    private void init() {
        // data
        adapter = new InteractiveFriendAdapter(this);
        contentPlv.setAdapter(adapter);
        refreshPRl.setRefreshIsEnable(true);

//        List<FriendInfoEntity> datas = new Gson().fromJson(BaseDataService.getValueByString(BaseDataService.DATA_FRIEND_DATA), new TypeToken<List<FriendInfoEntity>>(){}.getType());
        List<FriendInfoEntity> datas = CacheDataService.getFriendInfos();
        adapter.setDatas(datas);

        // listener
        contentPlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {
//                    Intent intent = new Intent();
//                    intent.setClass(FriendListActivity.this, SearchUserInfoActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    FriendListActivity.this.startActivity(intent);
//                } else {
                    FriendInfoEntity data = (FriendInfoEntity) adapter.getItem(i);
                    if (data != null) {
                        Intent intent = new Intent();
                        intent.setClass(FriendListActivity.this, ToChatActivity.class);
                        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, 1);
//                    intent.putExtra(EaseConstant.EXTRA_USER_ID, i - 1);
                        intent.putExtra(EaseConstant.EXTRA_USER_DATA, new Gson().toJson(data));
                        FriendListActivity.this.startActivity(intent);
                    }
                }
//            }
        });

        refreshPRl.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                loadFriendData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                loadFriendData();
            }
        });
    }

    @OnClick({R.id.ibtn_back, R.id.ibtn_add_friend})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ibtn_back: // 返回
                finish();
                break;
            case R.id.ibtn_add_friend: // 添加好友
                Intent intent = new Intent();
                intent.setClass(this, SearchUserInfoActivity.class);
                this.startActivity(intent);
                break;
        }
    }

    private void loadFriendData() {
        HttpRequest.instance().doPost(HttpRequest.create(WebFriendApi.class).friendList(), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
                refreshPRl.refreshFinish(webMsg.isSuccess());
                if (webMsg.isSuccess()) {
                    Log.e("ououou",webMsg.getData());
                    List<FriendInfoEntity> datas = new Gson().fromJson(webMsg.getData(), new TypeToken<List<FriendInfoEntity>>() {
                    }.getType());
                    adapter.setDatas(datas);
                    CacheDataService.saveFriendInfos(datas);
                } else {
                    webMsg.showMsg(FriendListActivity.this);
                }
            }
        });
    }
}
