package com.tdp.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.chat.activity.ToChatActivity;
import com.chat.easeui.EaseConstant;
import com.chat.easeui.widget.EaseConversationList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.sdk.db.BaseDataService;
import com.tdp.base.BaseFragment;
import com.tdp.main.R;
import com.tdp.main.entity.FriendInfoEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * 聊天记录
 */
public class FriendChatsFragment extends BaseFragment implements EMMessageListener {

    @BindView(R.id.id_content)
    EaseConversationList conversationListView;
    protected List<EMConversation> conversationList = new ArrayList<EMConversation>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
    }

    private void init(){
        // data


        // listener
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            EMConversation data = conversationList.get(i);
            if(data != null){
                toChat(data.conversationId());
            }
            }
        });
    }

    private void toChat(String account){
        List<FriendInfoEntity> datas = new Gson().fromJson(BaseDataService.getValueByString(BaseDataService.DATA_FRIEND_DATA), new TypeToken<List<FriendInfoEntity>>(){}.getType());
        for (FriendInfoEntity data : datas){
            if(data.getAccount().equals(account)){
                Intent intent = new Intent();
                intent.setClass(FriendChatsFragment.this.getContext(), ToChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, 1);
                intent.putExtra(EaseConstant.EXTRA_USER_DATA, new Gson().toJson(data));
                FriendChatsFragment.this.startActivity(intent);
                return;
            }
        }
    }




    protected List<EMConversation> loadConversationList(){
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {
                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        conversationList = loadConversationList();
        conversationListView.init(conversationList);
//        conversationListView.getAdapter().notifyDataSetChanged();
        hasRegister = true;
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    private boolean hasRegister = false;
    @Override
    public void onVisibilityChanged(boolean hidden) {
        super.onVisibilityChanged(hidden);

        if(hidden && hasRegister){
            hasRegister = false;
            EMClient.getInstance().chatManager().removeMessageListener(this);
        } else if(hasRegister){
            hasRegister = true;
            EMClient.getInstance().chatManager().addMessageListener(this);
        }

    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {
        conversationList = loadConversationList();
        conversationListView.refresh();
//        conversationListView.init(conversationList);
//        conversationListView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

    }

    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }
}
