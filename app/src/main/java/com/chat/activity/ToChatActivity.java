package com.chat.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.chat.ChatHelper;
import com.chat.Constant;
import com.chat.activity.control.ToChatInputControl;
import com.chat.easeui.EaseConstant;
import com.chat.easeui.EaseUI;
import com.chat.easeui.model.EaseDingMessageHelper;
import com.chat.easeui.ui.EaseDingMsgSendActivity;
import com.chat.easeui.utils.EaseCommonUtils;
import com.chat.easeui.widget.EaseAlertDialog;
import com.chat.easeui.widget.EaseChatMessageList;
import com.chat.easeui.widget.EaseTitleBar;
import com.chat.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.chat.easeui.widget.presenter.EaseChatRowPresenter;
import com.chat.entity.RobotUser;
import com.chat.ui.ChatRoomDetailsActivity;
import com.chat.ui.ContextMenuActivity;
import com.chat.ui.ForwardMessageActivity;
import com.chat.ui.GroupDetailsActivity;
import com.chat.ui.UserProfileActivity;
import com.chat.widget.ChatRowConferenceInvitePresenter;
import com.chat.widget.ChatRowLivePresenter;
import com.chat.widget.EaseChatRecallPresenter;
import com.chat.widget.EaseChatVoiceCallPresenter;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.sdk.api.entity.UserInfoEntity;
import com.sdk.views.listview.pulllistview.PullToRefreshLayout;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.activity.UserInfoActivity;
import com.tdp.main.entity.FriendInfoEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * ahtor: super_link
 * date: 2018/10/30 15:27
 * remark:
 */
public class ToChatActivity extends BaseActivity implements EMMessageListener {

    @BindView(R.id.message_list)
    EaseChatMessageList messageList; // 消息控件（）
    @BindView(R.id.title_bar)
    protected EaseTitleBar titleBar;
    protected PullToRefreshLayout swipeRefreshLayout;
    protected boolean isRoaming = false;
    protected Handler handler = new Handler();
    private ToChatInputControl inputControl; // 输入控件
    private EMCallBack emCallBack;
    protected ClipboardManager clipboard;
    private EMMessage contextMenuMessage;

    private ListView messageLv;
    protected EMConversation conversation;
    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pagesize = 20;
    private ExecutorService fetchQueue;
    private boolean isRobot;

    private boolean isMessageListInited;

    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_DING_MSG = 4;

    private static final int REQUEST_CODE_SELECT_VIDEO = 11;
    private static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final int REQUEST_CODE_GROUP_DETAIL = 13;
    private static final int REQUEST_CODE_CONTEXT_MENU = 14;
    private static final int REQUEST_CODE_SELECT_AT_USER = 15;

    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;
    private static final int MESSAGE_TYPE_CONFERENCE_INVITE = 5;
    private static final int MESSAGE_TYPE_LIVE_INVITE = 6;
    private static final int MESSAGE_TYPE_RECALL = 9;


    FriendInfoEntity userInfo;
//    private String toChatUsername;
    private int chatType; // 聊天类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ease_fragment_chat);
        ButterKnife.bind(this);
        init();
//        hideSoftKeyboard();

    }



    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(this);
        if (isMessageListInited) {
            messageList.refresh();
        }
        inputControl.hide();
    }
    @Override
    public void onPause() {
        super.onPause();
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }


    private void init(){

        // data
        // --- 获取初始值

        userInfo = new Gson().fromJson(getIntent().getStringExtra(EaseConstant.EXTRA_USER_DATA), FriendInfoEntity.class);
        chatType = getIntent().getIntExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE); // 聊天类型，默认单人
        titleBar.setTitle(userInfo.getRemark().equals("")?userInfo.getNick_name():userInfo.getRemark());
        if (chatType != EaseConstant.CHATTYPE_SINGLE) // 显示昵称
            messageList.setShowUserNick(true);

        if (chatType == Constant.CHATTYPE_SINGLE) {
            Map<String,RobotUser> robotMap = ChatHelper.getInstance().getRobotList();
            if(robotMap!=null && robotMap.containsKey(userInfo.getAccount())){
                isRobot = true;
            }
        }

        messageLv = messageList.getListView();
        inputControl = new ToChatInputControl(this, userInfo.getAccount(), chatType);
        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        // 为了判断是否是群聊，这很重要，因为取决于要不要从漫游中取数据的关键所在。
        isRoaming = chatType != EaseConstant.CHATTYPE_CHATROOM;
        if (isRoaming) {
            fetchQueue = Executors.newSingleThreadExecutor();
        }
        // -------- 加载消息

        conversation = EMClient.getInstance().chatManager().getConversation(userInfo.getAccount(), EaseCommonUtils.getConversationType(chatType), true);

        conversation.markAllMessagesAsRead();
        if (!isRoaming) {
            final List<EMMessage> msgs = conversation.getAllMessages();
            int msgCount = msgs != null ? msgs.size() : 0;
            if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                String msgId = null;
                if (msgs != null && msgs.size() > 0) {
                    msgId = msgs.get(0).getMsgId();
                }
                conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
            }
        } else {
            fetchQueue.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().chatManager().fetchHistoryMessages(userInfo.getAccount(), EaseCommonUtils.getConversationType(chatType), pagesize, "");
                        final List<EMMessage> msgs = conversation.getAllMessages();
                        int msgCount = msgs != null ? msgs.size() : 0;
                        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                            String msgId = null;
                            if (msgs != null && msgs.size() > 0) {
                                msgId = msgs.get(0).getMsgId();
                            }
                            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
                        }
                        messageList.refreshSelectLast();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        // ------ 加载消息到显示控件中
        messageList.init(userInfo.getAccount(), chatType, userInfo, new CustomChatRowProvider()); // 初始化消息列表控件
        isMessageListInited = true;


        // listener

        messageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {
            @Override
            public void onUserAvatarClick(String username) {
                ToChatActivity.this.onAvatarClick(username);
            }

            @Override
            public boolean onResendClick(final EMMessage message) {
                EMLog.i("ToChatActivity", "onResendClick::onResendClick");
                new EaseAlertDialog(ToChatActivity.this, R.string.resend, R.string.confirm_resend, null, new EaseAlertDialog.AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (!confirmed) {
                            return;
                        }
                        message.setStatus(EMMessage.Status.CREATE);
                        inputControl.sendMessage(message);
                    }
                }, true).show();
                return true;
            }

            @Override
            public void onUserAvatarLongClick(String username) {
                inputControl.inputAtUsername(username, true);
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                contextMenuMessage = message;
                onMessageBubbleLongClick(message);
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                return onMessageBubbleClick(message);
            }

            @Override
            public void onMessageInProgress(EMMessage message) {
                message.setMessageStatusCallback(emCallBack);
            }
        });

        messageList.getListView().setOnTouchListener(new View.OnTouchListener() { // 触摸非输入区域时，将隐藏输入控件
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                inputControl.hide();
                return false;
            }
        });

        emCallBack = new EMCallBack() {
            @Override
            public void onSuccess() {
                if (isMessageListInited) {
                    messageList.refresh();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.i("EaseChatRowPresenter", "onError: " + code + ", error: " + error);
                if (isMessageListInited) {
                    messageList.refresh();
                }
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.i("ToChatActivity", "EMCallBack::onProgress: " + progress);
                if (isMessageListInited) {
                    messageList.refresh();
                }
            }
        };

        inputControl.setMessageStatusCallback(emCallBack);

        // 下拉刷新时加载本地或漫游消息
        swipeRefreshLayout.setRefreshIsEnable(true);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isRoaming) {
                            loadMoreLocalMessage();
                        } else {
                            loadMoreRoamingMessages();
                        }
                    }
                }, 600);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });

        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(ToChatActivity.this, UserInfoActivity.class);
                intent.putExtra("account", userInfo.getAccount());
                ToChatActivity.this.startActivityForResult(intent, 1);

//                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
//                    emptyHistory();
//                } else {
//                    toGroupDetails();
//                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        inputControl.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case ContextMenuActivity.RESULT_CODE_COPY: // copy
                    clipboard.setPrimaryClip(ClipData.newPlainText(null,
                            ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage()));
                    break;
                case ContextMenuActivity.RESULT_CODE_DELETE: // delete
                    conversation.removeMessage(contextMenuMessage.getMsgId());
                    messageList.refresh();
                    // To delete the ding-type message native stored acked users.
                    EaseDingMessageHelper.get().delete(contextMenuMessage);
                    break;

                case ContextMenuActivity.RESULT_CODE_FORWARD: // forward
                    Intent intent = new Intent(this, ForwardMessageActivity.class);
                    intent.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
                    startActivity(intent);
                    break;
                case ContextMenuActivity.RESULT_CODE_RECALL://recall
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMMessage msgNotification = EMMessage.createTxtSendMessage(" ",contextMenuMessage.getTo());
                                EMTextMessageBody txtBody = new EMTextMessageBody(getResources().getString(R.string.msg_recall_by_self));
                                msgNotification.addBody(txtBody);
                                msgNotification.setMsgTime(contextMenuMessage.getMsgTime());
                                msgNotification.setLocalTime(contextMenuMessage.getMsgTime());
                                msgNotification.setAttribute(Constant.MESSAGE_TYPE_RECALL, true);
                                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                                EMClient.getInstance().chatManager().recallMessage(contextMenuMessage);
                                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                                messageList.refresh();
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                ToChatActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        android.widget.Toast.makeText(ToChatActivity.this, e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();

                    // Delete group-ack data according to this message.
                    EaseDingMessageHelper.get().delete(contextMenuMessage);
                    break;

                default:
                    break;
            }
        }
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case REQUEST_CODE_CAMERA: // 本地拍照后发送
                    if (inputControl.cameraFile != null && inputControl.cameraFile.exists()){
                        inputControl.sendImageMessage(inputControl.cameraFile.getAbsolutePath());
                        if (isMessageListInited) {
                            messageList.refreshSelectLast();
                        }
                    }



                    break;
                case REQUEST_CODE_LOCAL: // 本地选择照片后发送
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            inputControl.sendPicByUri(selectedImage);

                            if (isMessageListInited) {
                                messageList.refreshSelectLast();
                            }
                        }
                    }
                    break;
                case REQUEST_CODE_MAP:
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);
                    String locationAddress = data.getStringExtra("address");
                    if (locationAddress != null && !locationAddress.equals("")) {
                        inputControl.sendLocationMessage(latitude, longitude, locationAddress);
                        if (isMessageListInited) {
                            messageList.refreshSelectLast();
                        }
                    } else {
                        Toast.makeText(this, R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case REQUEST_CODE_SELECT_VIDEO: //send the video
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");
                        File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                            ThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();
                            inputControl.sendVideoMessage(videoPath, file.getAbsolutePath(), duration);

                            if (isMessageListInited) {
                                messageList.refreshSelectLast();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_FILE: //send the file
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            inputControl.sendFileByUri(uri);

                            if (isMessageListInited) {
                                messageList.refreshSelectLast();
                            }
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_AT_USER:
                    if(data != null){
                        String username = data.getStringExtra("username");
                         inputControl.inputAtUsername(username, false);

                        if (isMessageListInited) {
                            messageList.refreshSelectLast();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
            switch (resultCode) {
                case GroupDetailsActivity.RESULT_CODE_SEND_GROUP_NOTIFICATION:
                    // Start the ding-type msg send ui.
//                    EMLog.i("ToChatActivity", "onActivityResult::Intent to the ding-msg send activity.");
//                    Intent intent = new Intent(this, EaseDingMsgSendActivity.class);
//                    intent.putExtra(EaseConstant.EXTRA_USER_ID, toChatUsername);
//                    startActivityForResult(intent, REQUEST_CODE_DING_MSG);
                    break;
            }
        }
    }

    public void onAvatarClick(String account) {
        //handling when user click avatar
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra("account", account);
        startActivity(intent);
    }

    public void onSetMessageAttributes(EMMessage message) {
        if(isRobot){
            //set message extension
            message.setAttribute("em_robot_message", isRobot);
        }
    }

    public void onMessageBubbleLongClick(EMMessage message) {
        // no message forward when in chat room
//        startActivityForResult((new Intent(this, ContextMenuActivity.class)).putExtra("message",message).putExtra("ischatroom", chatType == EaseConstant.CHATTYPE_CHATROOM),
//                REQUEST_CODE_CONTEXT_MENU);
    }

    public boolean onMessageBubbleClick(EMMessage message) {
        //消息框点击事件，demo这里不做覆盖，如需覆盖，return true
        return false;
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            String username = null;
            // group message
            if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                // single chat message
                username = message.getFrom();
            }

            // if the message is for current conversation
            if (username.equals(userInfo.getAccount()) || message.getTo().equals(userInfo.getAccount()) || message.conversationId().equals(userInfo.getAccount())) {
                messageList.refreshSelectLast();
                conversation.markMessageAsRead(message.getMsgId());
            }
            EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
        }
    }

    protected static final String ACTION_TYPING_BEGIN = "TypingBegin";
    protected static final String ACTION_TYPING_END = "TypingEnd";
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        for (final EMMessage msg : messages) {
            final EMCmdMessageBody body = (EMCmdMessageBody) msg.getBody();
//            EMLog.i(TAG, "Receive cmd message: " + body.action() + " - " + body.isDeliverOnlineOnly());
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ACTION_TYPING_BEGIN.equals(body.action()) && msg.getFrom().equals(userInfo.getAccount())) {
                        titleBar.setTitle(getString(R.string.alert_during_typing));
                    } else if (ACTION_TYPING_END.equals(body.action()) && msg.getFrom().equals(userInfo.getAccount())) {
                        titleBar.setTitle(userInfo.getAccount());
                    }
                }
            });
        }
    }

    @Override
    public void onMessageRead(List<EMMessage> list) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }

    // =================


    /***
     * 消息提供者回调， 主要用于定制一些显示上的功能。
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //here the number is the message type in EMMessage::Type
            //which is used to count the number of different chat row
            return 14;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if(message.getType() == EMMessage.Type.TXT){
                //voice call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)){
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
                }else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
                    //video call
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
                }
                //messagee recall
                else if(message.getBooleanAttribute(Constant.MESSAGE_TYPE_RECALL, false)){
                    return MESSAGE_TYPE_RECALL;
                } else if (!"".equals(message.getStringAttribute(Constant.MSG_ATTR_CONF_ID,""))) {
                    return MESSAGE_TYPE_CONFERENCE_INVITE;
                } else if (Constant.OP_INVITE.equals(message.getStringAttribute(Constant.EM_CONFERENCE_OP, ""))) {
                    return MESSAGE_TYPE_LIVE_INVITE;
                }
            }
            return 0;
        }

        @Override
        public EaseChatRowPresenter getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
            if(message.getType() == EMMessage.Type.TXT){
                // voice call or video call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
                        message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
                    EaseChatRowPresenter presenter = new EaseChatVoiceCallPresenter();
                    return presenter;
                }
                //recall message
                else if(message.getBooleanAttribute(Constant.MESSAGE_TYPE_RECALL, false)){
                    EaseChatRowPresenter presenter = new EaseChatRecallPresenter();
                    return presenter;
                } else if (!"".equals(message.getStringAttribute(Constant.MSG_ATTR_CONF_ID,""))) {
                    return new ChatRowConferenceInvitePresenter();
                } else if (Constant.OP_INVITE.equals(message.getStringAttribute(Constant.EM_CONFERENCE_OP, ""))) {
                    return new ChatRowLivePresenter();
                }
            }
            return null;
        }

    }

    /***
     * 加载更多本地消息
     */
    private void loadMoreLocalMessage() {
        if (messageLv.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
            List<EMMessage> messages;
            try {
                messages = conversation.loadMoreMsgFromDB(conversation.getAllMessages().size() == 0 ? "" : conversation.getAllMessages().get(0).getMsgId(), pagesize);
            } catch (Exception e1) {
                // swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.refreshFinish(true);
                return;
            }
            if (messages.size() > 0) {
                messageList.refreshSeekTo(messages.size() - 1);
                if (messages.size() != pagesize) {
                    haveMoreData = false;
                }
            } else {
                haveMoreData = false;
            }

            isloading = false;
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_more_messages),
                    Toast.LENGTH_SHORT).show();
        }
        // swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.refreshFinish(true);
    }

    /***
     * 加载漫游消息
     */
    private void loadMoreRoamingMessages() {
        if (!haveMoreData) {
            Toast.makeText(this, getResources().getString(R.string.no_more_messages),
                    Toast.LENGTH_SHORT).show();
            //swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.refreshFinish(true);
            return;
        }
        if (fetchQueue != null) {
            fetchQueue.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<EMMessage> messages = conversation.getAllMessages();
                        EMClient.getInstance().chatManager().fetchHistoryMessages(userInfo.getAccount(), EaseCommonUtils.getConversationType(chatType), pagesize,(messages != null && messages.size() > 0) ? messages.get(0).getMsgId() : "");
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    } finally {
                        ToChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadMoreLocalMessage();
                            }
                        });
                    }
                }
            });
        }
    }

    protected void emptyHistory() {
        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(this, null, msg, null, new EaseAlertDialog.AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    if (conversation != null) {
                        conversation.clearAllMessages();
                    }
                    messageList.refresh();
                    haveMoreData = true;
                }
            }
        }, true).show();
    }

    protected void toGroupDetails() {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(userInfo.getAccount());
            if (group == null) {
                Toast.makeText(this, R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            if (chatType == Constant.CHATTYPE_GROUP) {
                group = EMClient.getInstance().groupManager().getGroup(userInfo.getAccount());
                if (group == null) {
                    Toast.makeText(ToChatActivity.this, R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivityForResult(
                        (new Intent(ToChatActivity.this, GroupDetailsActivity.class).putExtra("groupId", userInfo.getAccount())),
                        REQUEST_CODE_GROUP_DETAIL);
            }else if(chatType == Constant.CHATTYPE_CHATROOM){
                startActivityForResult(new Intent(ToChatActivity.this, ChatRoomDetailsActivity.class).putExtra("roomId", userInfo.getAccount()), REQUEST_CODE_GROUP_DETAIL);
            }
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            if (chatType == Constant.CHATTYPE_GROUP) {
                EMGroup group = EMClient.getInstance().groupManager().getGroup(userInfo.getAccount());
                if (group == null) {
                    Toast.makeText(ToChatActivity.this, R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivityForResult(
                        (new Intent(ToChatActivity.this, GroupDetailsActivity.class).putExtra("groupId", userInfo.getAccount())),
                        REQUEST_CODE_GROUP_DETAIL);
            }else if(chatType == Constant.CHATTYPE_CHATROOM){
                startActivityForResult(new Intent(ToChatActivity.this, ChatRoomDetailsActivity.class).putExtra("roomId", userInfo.getAccount()), REQUEST_CODE_GROUP_DETAIL);
            }
        }
    }
}
