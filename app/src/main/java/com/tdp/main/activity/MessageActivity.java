package com.tdp.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sdk.api.WebFriendApi;
import com.sdk.api.entity.AddFriendEntity;
import com.sdk.db.BaseDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.views.Menu.TopMenuView;
import com.sdk.views.dialog.Loading;
import com.sdk.views.dialog.Toast;
import com.sdk.views.listview.PullListView;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.adapter.MessageAdapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ahtor: super_link
 * date: 2018/9/28 17:45
 * remark:
 */
public class MessageActivity extends BaseActivity {

    private MessageAdapter adapter;
    @BindView(R.id.id_content)
    PullListView contentPlv;
    @BindView(R.id.id_menu)
    TopMenuView menu;

    private List<AddFriendEntity> datas;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        init();

        loadMessage();
    }

    private void init(){
        // view

        // data
        datas = new Gson().fromJson(BaseDataService.getValueByString(BaseDataService.DATA_MESSAGE_CACHE, ""), new TypeToken<List<AddFriendEntity>>(){}.getType());
        if(datas == null){
            datas = new ArrayList<>();
        }

        adapter = new MessageAdapter(this, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isBusying) return;
                isBusying = true;
                currentIndex = position;
                Loading.start(MessageActivity.this, "正在加载中，请稍后...", true);
                HttpRequest.instance().doPost(HttpRequest.create(WebFriendApi.class).agreeOrRefuseFriend(datas.get(position).getAccount(), "", 1), new OnResultListener() {
                    @Override
                    public void onWebUiResult(WebMsg webMsg) {
                        Loading.stop();
                        isBusying = false;
                        if(webMsg.isSuccess()){
                            Toast.show(MessageActivity.this, "您已同意对方好友请求！", Toast.LENGTH_LONG);
                            datas.get(currentIndex).setStatus(1);
                            adapter.setDatas(datas);
                            BaseDataService.saveValueToSharePerference(BaseDataService.DATA_MESSAGE_TIME, System.currentTimeMillis());
                        } else {
                            webMsg.showMsg(MessageActivity.this);
                        }
                    }
                });
            }
        });
        adapter.setDatas(datas);
        contentPlv.setAdapter(adapter);
        BaseDataService.saveValueToSharePerference(BaseDataService.DATA_FRIEND_REQUEST, 0); // 将未读消息条数设置为0

        // listener
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case TopMenuView.CLICK_LEFT:
                        finish();
                        break;
                }
            }
        });

        contentPlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentIndex = position;
                AddFriendEntity data = (AddFriendEntity) adapter.getItem(position);
                if(data != null && data.getStatus() == 0){
                    Intent intent = new Intent();
                    intent.putExtra("account", data.getAccount());
                    //intent.putExtra("nickName", data.getNick_name());
                    intent.setClass(MessageActivity.this, UserInfoActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        switch (requestCode){
            case 1:
                datas.get(currentIndex).setStatus(1);
                adapter.setDatas(datas);
                break;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        BaseDataService.saveValueToSharePerference(BaseDataService.DATA_MESSAGE_CACHE, new Gson().toJson(datas));
    }

    private boolean isBusying = false;
    private void loadMessage(){
        if(isBusying)return;
        isBusying = true;
        long datetime = BaseDataService.getValueByLong(BaseDataService.DATA_MESSAGE_TIME, 0);
       // Log.e("ououou"," "+datetime);
        final long nowTime = System.currentTimeMillis();
        HttpRequest.instance().doPost(HttpRequest.create(WebFriendApi.class).applyList(datetime), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
                isBusying = false;
                if(webMsg.isSuccess()){
                   // BaseDataService.saveValueToSharePerference(BaseDataService.DATA_MESSAGE_TIME, nowTime);
                   // Log.e("ououou",webMsg.getData());
                    datas = new Gson().fromJson(webMsg.getData(), new TypeToken<List<AddFriendEntity>>(){}.getType());
                    if(datas != null){
//                        datas.addAll(0, tempDatas);
                        adapter.setDatas(datas);
                        BaseDataService.saveValueToSharePerference(BaseDataService.DATA_MESSAGE_CACHE, new Gson().toJson(datas));
                    }
                } else {
                    webMsg.showMsg(MessageActivity.this);
                }
            }
        });
    }
}
