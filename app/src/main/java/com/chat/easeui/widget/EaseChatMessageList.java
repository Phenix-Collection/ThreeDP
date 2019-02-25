package com.chat.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.chat.easeui.adapter.EaseMessageAdapter;
import com.chat.easeui.model.styles.EaseMessageListItemStyle;
import com.chat.easeui.utils.EaseCommonUtils;
import com.chat.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.sdk.views.listview.PullListView;
import com.sdk.views.listview.pulllistview.PullToRefreshLayout;
import com.sdk.views.listview.pulllistview.Pullable;
import com.tdp.main.R;
import com.tdp.main.entity.FriendInfoEntity;

public class EaseChatMessageList extends RelativeLayout implements Pullable {

    protected static final String TAG = "EaseChatMessageList";
    //protected ListView listView;
   // protected SwipeRefreshLayout swipeRefreshLayout;
    protected PullListView listView;
    protected PullToRefreshLayout swipeRefreshLayout;
    protected Context context;
    protected EMConversation conversation;
    protected int chatType;
    protected String toChatUsername;
    protected EaseMessageAdapter messageAdapter;
    protected EaseMessageListItemStyle itemStyle;

    public EaseChatMessageList(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseChatMessageList(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	parseStyle(context, attrs);
    	init(context);
    }

    public EaseChatMessageList(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){

        // find view
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.ease_chat_message_list, this);
        swipeRefreshLayout = (PullToRefreshLayout) findViewById(R.id.chat_swipe_layout);
        listView = (PullListView) findViewById(R.id.list);

        // data

        // listener
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });
    }
    
    /**
     * init widget
     * @param toChatUsername
     * @param chatType
     * @param customChatRowProvider
     */
    public void init(String toChatUsername, int chatType, FriendInfoEntity userInfo,  EaseCustomChatRowProvider customChatRowProvider) {
        this.chatType = chatType;
        this.toChatUsername = toChatUsername;
        
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);
        messageAdapter = new EaseMessageAdapter(context, userInfo, chatType, listView);
        messageAdapter.setItemStyle(itemStyle);
        messageAdapter.setCustomChatRowProvider(customChatRowProvider);
        // set message adapter
        listView.setAdapter(messageAdapter);
        
        refreshSelectLast();
    }
    
    protected void parseStyle(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatMessageList);
        EaseMessageListItemStyle.Builder builder = new EaseMessageListItemStyle.Builder();
        builder.showAvatar(ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserAvatar, true))
                .showUserNick(ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserNick, false))
                .myBubbleBg(ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground))
                .otherBuddleBg(ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground));

        itemStyle = builder.build();
        ta.recycle();
    }
    
    
    /**
     * refresh
     */
    public void refresh(){
        if (messageAdapter != null) {
            messageAdapter.refresh();
        }
    }
    
    /**
     * refresh and jump to the last
     */
    public void refreshSelectLast(){
        if (messageAdapter != null) {
            messageAdapter.refreshSelectLast();
        }
    }
    
    /**
     * refresh and jump to the position
     * @param position
     */
    public void refreshSeekTo(int position){
        if (messageAdapter != null) {
            messageAdapter.refreshSeekTo(position);
        }
    }

	public ListView getListView() {
		return listView;
	} 

//	public SwipeRefreshLayout getSwipeRefreshLayout(){
//	    return swipeRefreshLayout;
//	}


    public PullToRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public EMMessage getItem(int position){
	    return messageAdapter.getItem(position);
	}

    public void setShowUserNick(boolean showUserNick){
        itemStyle.setShowUserNick(showUserNick);
    }

    public boolean isShowUserNick(){
        return itemStyle.isShowUserNick();
    }

    @Override
    public boolean canPullDown() {
        return getScrollY() == 0;
    }

    @Override
    public boolean canPullUp() {
        return getChildAt(0).getHeight() - getHeight() == getScrollY();
    }


    public interface MessageListItemClickListener{
	    /**
	     * there is default handling when bubble is clicked, if you want handle it, return true
	     * another way is you implement in onBubbleClick() of chat row
	     * @param message
	     * @return
	     */
	    boolean onBubbleClick(EMMessage message);
	    boolean onResendClick(EMMessage message);
	    void onBubbleLongClick(EMMessage message);
	    void onUserAvatarClick(String username);
	    void onUserAvatarLongClick(String username);
	    void onMessageInProgress(EMMessage message);
	}
	
	/**
	 * set click listener
	 * @param listener
	 */
	public void setItemClickListener(MessageListItemClickListener listener){
        if (messageAdapter != null) {
            messageAdapter.setItemClickListener(listener);
        }
	}
	
	/**
	 * set chat row provider
	 * @param rowProvider
	 */
	public void setCustomChatRowProvider(EaseCustomChatRowProvider rowProvider){
        if (messageAdapter != null) {
            messageAdapter.setCustomChatRowProvider(rowProvider);
        }
    }
}
