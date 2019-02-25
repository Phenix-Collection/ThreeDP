package com.chat.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Toast;
import com.chat.ChatHelper;
import com.chat.domain.EmojiconExampleGroupData;
import com.chat.easeui.EaseConstant;
import com.chat.easeui.model.EaseDingMessageHelper;
import com.chat.easeui.ui.EaseDingMsgSendActivity;
import com.chat.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.chat.easeui.widget.emojicon.EaseEmojiconMenu;
import com.chat.Constant;
import com.chat.easeui.ui.EaseChatFragment;
import com.chat.entity.RobotUser;
import com.chat.easeui.widget.presenter.EaseChatRowPresenter;
import com.chat.ui.ChatRoomDetailsActivity;
import com.chat.ui.ContextMenuActivity;
import com.chat.ui.ForwardMessageActivity;
import com.chat.ui.GroupDetailsActivity;
import com.chat.ui.ImageGridActivity;
import com.chat.ui.PickAtUserActivity;
import com.chat.ui.UserProfileActivity;
import com.chat.ui.VideoCallActivity;
import com.chat.ui.VoiceCallActivity;
import com.chat.widget.ChatRowConferenceInvitePresenter;
import com.chat.widget.ChatRowLivePresenter;
import com.chat.widget.EaseChatRecallPresenter;
import com.chat.widget.EaseChatVoiceCallPresenter;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.tdp.main.R;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class ChatFragment extends EaseChatFragment implements EaseChatFragment.EaseChatFragmentHelper {

    private static final int ITEM_VIDEO = 11;
    private static final int ITEM_FILE = 12;
    private static final int ITEM_VOICE_CALL = 13;
    private static final int ITEM_VIDEO_CALL = 14;
    private static final int ITEM_CONFERENCE_CALL = 15;
    private static final int ITEM_LIVE = 16;

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

    /**
     * if it is chatBot 
     */
    private boolean isRobot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState,
//                ChatHelper.getInstance().getModel().isMsgRoaming() && (chatType != EaseConstant.CHATTYPE_CHATROOM));
//    }

    @Override
    protected boolean turnOnTyping() {
        return ChatHelper.getInstance().getModel().isShowMsgTyping();
    }

    @Override
    protected void setUpView() {
        setChatFragmentHelper(this);
        if (chatType == Constant.CHATTYPE_SINGLE) { 
            Map<String,RobotUser> robotMap = ChatHelper.getInstance().getRobotList();
            if(robotMap!=null && robotMap.containsKey(toChatUsername)){
                isRobot = true;
            }
        }
        super.setUpView();
        // set click listener
//        titleBar.setLeftLayoutClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (EasyUtils.isSingleActivity(ChatFragment.this)) {
//                    Intent intent = new Intent(ChatFragment.this, MainActivity.class);
//                    startActivity(intent);
//                }
//                onBackPressed();
//            }
//        });
        ((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).addEmojiconGroup(EmojiconExampleGroupData.getData());
        if(chatType == EaseConstant.CHATTYPE_GROUP){
            inputMenu.getPrimaryMenu().getEditText().addTextChangedListener(new TextWatcher() {
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(count == 1 && "@".equals(String.valueOf(s.charAt(start)))){
                        startActivityForResult(new Intent(ChatFragment.this, PickAtUserActivity.class).
                                putExtra("groupId", toChatUsername), REQUEST_CODE_SELECT_AT_USER);
                    }
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    
                }
                @Override
                public void afterTextChanged(Editable s) {
                    
                } 
            });
        }
    }
    
    @Override
    protected void registerExtendMenuItem() {
        //use the menu in base class
        super.registerExtendMenuItem();
        //extend menu items
       // inputMenu.registerExtendMenuItem(R.string.attach_video, R.drawable.em_chat_video_selector, ITEM_VIDEO, extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, ITEM_FILE, extendMenuItemClickListener);
        if(chatType == Constant.CHATTYPE_SINGLE){
          //  inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, ITEM_VOICE_CALL, extendMenuItemClickListener);
          //  inputMenu.registerExtendMenuItem(R.string.attach_video_call, R.drawable.em_chat_video_call_selector, ITEM_VIDEO_CALL, extendMenuItemClickListener);
        } else if (chatType == Constant.CHATTYPE_GROUP) { // 音视频会议
          //  inputMenu.registerExtendMenuItem(R.string.voice_and_video_conference, R.drawable.em_chat_video_call_selector, ITEM_CONFERENCE_CALL, extendMenuItemClickListener);
          //  inputMenu.registerExtendMenuItem(R.string.title_live, R.drawable.em_chat_video_call_selector, ITEM_LIVE, extendMenuItemClickListener);
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                Intent intent = new Intent(ChatFragment.this, ForwardMessageActivity.class);
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
                            ChatFragment.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(ChatFragment.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            case REQUEST_CODE_SELECT_VIDEO: //send the video
                if (data != null) {
                    int duration = data.getIntExtra("dur", 0);
                    String videoPath = data.getStringExtra("path");
                    File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                        ThumbBitmap.compress(CompressFormat.JPEG, 100, fos);
                        fos.close();
                        sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_CODE_SELECT_FILE: //send the file
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        sendFileByUri(uri);
                    }
                }
                break;
            case REQUEST_CODE_SELECT_AT_USER:
                if(data != null){
                    String username = data.getStringExtra("username");
                    inputAtUsername(username, false);
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
//                    EMLog.i(TAG, "Intent to the ding-msg send activity.");
//                    Intent intent = new Intent(ChatFragment.this, EaseDingMsgSendActivity.class);
//                    intent.putExtra(EaseConstant.EXTRA_USER_ID, toChatUsername);
//                    startActivityForResult(intent, REQUEST_CODE_DING_MSG);
                    break;
            }
        }
    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        if(isRobot){
            //set message extension
            message.setAttribute("em_robot_message", isRobot);
        }
    }
    
    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return new CustomChatRowProvider();
    }
  

    @Override
    public void onEnterToChatDetails() {
        if (chatType == Constant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group == null) {
                Toast.makeText(ChatFragment.this, R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivityForResult(
                    (new Intent(ChatFragment.this, GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
                    REQUEST_CODE_GROUP_DETAIL);
        }else if(chatType == Constant.CHATTYPE_CHATROOM){
        	startActivityForResult(new Intent(ChatFragment.this, ChatRoomDetailsActivity.class).putExtra("roomId", toChatUsername), REQUEST_CODE_GROUP_DETAIL);
        }
    }

    @Override
    public void onAvatarClick(String username) {
        //handling when user click avatar
        Intent intent = new Intent(ChatFragment.this, UserProfileActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    
    @Override
    public void onAvatarLongClick(String username) {
        inputAtUsername(username);
    }
    
    
    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //消息框点击事件，demo这里不做覆盖，如需覆盖，return true
        return false;
    }
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        super.onCmdMessageReceived(messages);
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {
    	// no message forward when in chat room
        startActivityForResult((new Intent(ChatFragment.this, ContextMenuActivity.class)).putExtra("message",message)
                .putExtra("ischatroom", chatType == EaseConstant.CHATTYPE_CHATROOM),
                REQUEST_CODE_CONTEXT_MENU);
    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
        case ITEM_VIDEO:
            Intent intent = new Intent(ChatFragment.this, ImageGridActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
            break;
        case ITEM_FILE: //file
            selectFileFromLocal();
            break;
        case ITEM_VOICE_CALL:
            startVoiceCall();
            break;
        case ITEM_VIDEO_CALL:
            startVideoCall();
            break;
        case ITEM_CONFERENCE_CALL:
           // ConferenceActivity.startConferenceCall(ChatFragment.this, toChatUsername);
            break;
        case ITEM_LIVE:
           // LiveActivity.startLive(this, toChatUsername);
            break;
        default:
            break;
        }
        //keep exist extend menu
        return false;
    }
    
    /**
     * select file
     */
    protected void selectFileFromLocal() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }
    
    /**
     * make a voice call
     */
    protected void startVoiceCall() {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(ChatFragment.this, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(ChatFragment.this, VoiceCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }
    
    /**
     * make a video call
     */
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(ChatFragment.this, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(ChatFragment.this, VideoCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }
    
    /**
     * chat row provider 
     *
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
