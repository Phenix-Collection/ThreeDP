package com.chat.activity.control;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.chat.Constant;
import com.chat.easeui.EaseConstant;
import com.chat.easeui.domain.EaseEmojicon;
import com.chat.easeui.domain.EaseUser;
import com.chat.easeui.model.EaseAtMessageHelper;
import com.chat.easeui.model.EaseCompat;
import com.chat.easeui.ui.EaseBaiduMapActivity;
import com.chat.easeui.ui.EaseChatFragment;
import com.chat.easeui.utils.EaseCommonUtils;
import com.chat.easeui.utils.EaseUserUtils;
import com.chat.easeui.widget.EaseChatExtendMenu;
import com.chat.easeui.widget.EaseChatInputMenu;
import com.chat.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.sdk.views.dialog.Toast;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ahtor: super_link
 * date: 2018/10/30 16:43
 * remark:
 */
public class ToChatInputControl {

    private BaseActivity context;
    @BindView(R.id.input_menu)
    EaseChatInputMenu inputMenu; // 输入控件
    @BindView(R.id.voice_recorder)
    EaseVoiceRecorderView voiceRecorderView; // 录音控件
    private EMCallBack messageStatusCallback;
    protected MyItemClickListener extendMenuItemClickListener;

    // 拓展菜单资源
    protected int[] itemStrings = {R.string.attach_take_pic, R.string.attach_picture, R.string.attach_location}; // 菜单名称
    protected int[] itemdrawables = {R.drawable.ease_chat_takepic_selector, R.drawable.ease_chat_image_selector,R.drawable.ease_chat_location_selector}; // 菜单图标

    // 拓展类型
    protected int[] itemIds = {ITEM_TAKE_PICTURE, ITEM_PICTURE, ITEM_LOCATION};
    static final int ITEM_TAKE_PICTURE = 1;
    static final int ITEM_PICTURE = 2;
    static final int ITEM_LOCATION = 3;


    //
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_DING_MSG = 4;

    private String toUserName;
    private int chatType; // 聊天类型


    protected static final int MSG_TYPING_BEGIN = 0;
    protected static final int MSG_TYPING_END = 1;


    public ToChatInputControl(BaseActivity context, String toUserName, int chatType){
        this.context = context;
        this.toUserName = toUserName;
        this.chatType = chatType;
        ButterKnife.bind(this, context);

        init();
    }

    private void init(){
        inputMenu.init(null);

        // listener
        inputMenu.setChatInputMenuListener(inputListener);
        for (int i = 0; i < itemStrings.length; i++) {
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], new MyItemClickListener()); // 注册拓展菜单
        }

        if(chatType == Constant.CHATTYPE_SINGLE){
            //  inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, ITEM_VOICE_CALL, extendMenuItemClickListener);
            //  inputMenu.registerExtendMenuItem(R.string.attach_video_call, R.drawable.em_chat_video_call_selector, ITEM_VIDEO_CALL, extendMenuItemClickListener);
        } else if (chatType == Constant.CHATTYPE_GROUP) { // 音视频会议
            //  inputMenu.registerExtendMenuItem(R.string.voice_and_video_conference, R.drawable.em_chat_video_call_selector, ITEM_CONFERENCE_CALL, extendMenuItemClickListener);
            //  inputMenu.registerExtendMenuItem(R.string.title_live, R.drawable.em_chat_video_call_selector, ITEM_LIVE, extendMenuItemClickListener);
        }

    }

    /**
     * 注册菜单
     */
//    protected void registerExtendMenuItem() {
//
//        for (int i = 0; i < itemStrings.length; i++) {
//            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], extendMenuItemClickListener);
//        }
//    }

    /***
     * 隐藏软键盘
     */
    public void hide(){
        context.hideSoftKeyboard();
        inputMenu.hideExtendMenuContainer();
    }

    /***
     * 这个方法因为只能在activity中回调，但封装又在这个控制类，所以需要从activity中调用。
     * @param requestCode
     * @param resultCode
     * @param data
     */
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {



//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
//                if (cameraFile != null && cameraFile.exists())
//                    sendImageMessage(cameraFile.getAbsolutePath());
//            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
//                if (data != null) {
//                    Uri selectedImage = data.getData();
//                    if (selectedImage != null) {
//                        sendPicByUri(selectedImage);
//                    }
//                }
//            } else if (requestCode == REQUEST_CODE_MAP) { // location
//                double latitude = data.getDoubleExtra("latitude", 0);
//                double longitude = data.getDoubleExtra("longitude", 0);
//                String locationAddress = data.getStringExtra("address");
//                if (locationAddress != null && !locationAddress.equals("")) {
//                    sendLocationMessage(latitude, longitude, locationAddress);
//                } else {
//                    android.widget.Toast.makeText(context, R.string.unable_to_get_loaction, android.widget.Toast.LENGTH_SHORT).show();
//                }
//
//            } else if (requestCode == REQUEST_CODE_DING_MSG) { // To send the ding-type msg.
//                String msgContent = data.getStringExtra("msg");
//                EMLog.i("Huanxin", "ToChatInputCOntrol::onActivityResult::To send the ding-type msg, content: " + msgContent);
//                // Send the ding-type msg.
//                EMMessage dingMsg = EaseDingMessageHelper.get().createDingMessage(toUserName, msgContent);
//                sendMessage(dingMsg);
//            }
//        }
//    }

    public File cameraFile;
    class MyItemClickListener implements EaseChatExtendMenu.EaseChatExtendMenuItemClickListener {

        @Override
        public void onClick(int itemId, View view) {
            if (chatFragmentHelper != null) {
                if (chatFragmentHelper.onExtendMenuItemClick(itemId, view)) {
                    return;
                }
            }
            switch (itemId) {
                case ITEM_TAKE_PICTURE: // 拍照
                    if (!EaseCommonUtils.isSdcardExist()) {
                        Toast.show(context, context.getString(R.string.sd_card_does_not_exist), Toast.LENGTH_SHORT);
                        return;
                    }
                    cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser() + System.currentTimeMillis() + ".jpg");
                    cameraFile.getParentFile().mkdirs();
                    context.startActivityForResult(
                            new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(context, cameraFile)),
                            REQUEST_CODE_CAMERA);
                    break;
                case ITEM_PICTURE: // 选择一张照片
                    Intent intent;
                    if (Build.VERSION.SDK_INT < 19) {
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");

                    } else {
                        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    }
                    context.startActivityForResult(intent, REQUEST_CODE_LOCAL);
                    break;
                case ITEM_LOCATION: // 获取位置
                    context.startActivityForResult(new Intent(context, EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
                    break;

                default:
                    break;
            }
        }
    }

    public void inputAtUsername(String username, boolean autoAddAtSymbol) {
        if (EMClient.getInstance().getCurrentUser().equals(username) ||
                chatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }
        EaseAtMessageHelper.get().addAtUser(username);
        EaseUser user = EaseUserUtils.getUserInfo(username);
        if (user != null) {
            username = user.getNick();
        }
        if (autoAddAtSymbol)
            inputMenu.insertText("@" + username + " ");
        else
            inputMenu.insertText(username + " ");
    }

    /***
     * 消息发送监听
     * @param messageStatusCallback
     */
    public void setMessageStatusCallback(EMCallBack messageStatusCallback) {
        this.messageStatusCallback = messageStatusCallback;
    }

    protected EaseChatFragment.EaseChatFragmentHelper chatFragmentHelper;

    public void setChatFragmentHelper(EaseChatFragment.EaseChatFragmentHelper chatFragmentHelper) {
        this.chatFragmentHelper = chatFragmentHelper;
    }

    /***
     * 录入回调
     */
    EaseChatInputMenu.ChatInputMenuListener inputListener = new EaseChatInputMenu.ChatInputMenuListener() {
        @Override
        public void onTyping(CharSequence s, int start, int before, int count) {
            // send action:TypingBegin cmd msg.
            typingHandler.sendEmptyMessage(MSG_TYPING_BEGIN);
        }

        @Override
        public void onSendMessage(String content) {
            sendTextMessage(content);
        }

        @Override
        public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
            return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderView.EaseVoiceRecorderCallback() {

                @Override
                public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                    sendVoiceMessage(voiceFilePath, voiceTimeLength);
                }
            });
        }

        @Override
        public void onBigExpressionClicked(EaseEmojicon emojicon) {
            sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
        }
    };

    /**
     * send image
     *
     * @param selectedImage
     */
    public void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                android.widget.Toast toast = android.widget.Toast.makeText(context, R.string.cant_find_pictures, android.widget.Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                android.widget.Toast toast = android.widget.Toast.makeText(context, R.string.cant_find_pictures, android.widget.Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    public void sendFileByUri(Uri uri) {
        String filePath = EaseCompat.getPath(context, uri);
        EMLog.i("ToChatInputControl", "sendFileByUri::sendFileByUri: " + filePath);
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            android.widget.Toast.makeText(context, R.string.File_does_not_exist, android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        sendFileMessage(filePath);
    }

    public void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, toUserName);
        sendMessage(message);
    }

    public void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toUserName);
        sendMessage(message);
    }

    /**
     * 发送位置信息
     * @param latitude
     * @param longitude
     * @param locationAddress
     */
    public void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toUserName);
        sendMessage(message);
    }

    /***
     * 发送图片消息
     * @param imagePath
     */
    public void  sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toUserName);
        sendMessage(message);
    }

    //send message
    public void sendTextMessage(String content) {
        if (EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content);
        } else {
            EMMessage message = EMMessage.createTxtSendMessage(content, toUserName);
            sendMessage(message);
        }
    }

    /**
     * 发送语音消息
     * @param filePath
     * @param length
     */
    public void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toUserName);
        sendMessage(message);
    }

    /***
     * 发送大表情消息
     * @param name
     * @param identityCode
     */
    protected void sendBigExpressionMessage(String name, String identityCode) {
        EMMessage message = EaseCommonUtils.createExpressionMessage(toUserName, name, identityCode);
        sendMessage(message);
    }

//    protected void sendMessage(EMMessage message) {
//        if (message == null) {
//            return;
//        }
//        //set extension
//        onSetMessageAttributes(message);
//        if (chatType == EaseConstant.CHATTYPE_GROUP) {
//            message.setChatType(EMMessage.ChatType.GroupChat);
//        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
//            message.setChatType(EMMessage.ChatType.ChatRoom);
//        }
//
//        message.setMessageStatusCallback(messageStatusCallback);
//
//        // Send message.
//        EMClient.getInstance().chatManager().sendMessage(message);
//        //refresh ui
//        if (isMessageListInited) {
//            messageList.refreshSelectLast();
//        }
//    }

    @SuppressWarnings("ConstantConditions")
    private void sendAtMessage(String content) {
        if (chatType != EaseConstant.CHATTYPE_GROUP) {
            EMLog.e("Huanxin", "ToChatInputControl::sendAtMessage::only support group chat message");
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(content, toUserName);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(toUserName);
        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get().containsAtAll(content)) {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
        } else {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
                    EaseAtMessageHelper.get().atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content)));
        }
        sendMessage(message);
    }

    /***
     * 发送消息（不管发送任何消息类型，都调用此方法。）
     * @param message
     */
    public void sendMessage(EMMessage message) {
        if (message == null) {
            return;
        }
        if (chatFragmentHelper != null) {
            //set extension
            chatFragmentHelper.onSetMessageAttributes(message);
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            message.setChatType(EMMessage.ChatType.ChatRoom);
        }

        message.setMessageStatusCallback(messageStatusCallback);

        // Send message.
        EMClient.getInstance().chatManager().sendMessage(message);

        //refresh ui

    }

    protected static final String ACTION_TYPING_BEGIN = "TypingBegin";
    protected static final String ACTION_TYPING_END = "TypingEnd";
    protected static final int TYPING_SHOW_TIME = 5000;
    private boolean turnOnTyping = false;

    private Handler typingHandler  = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TYPING_BEGIN: // Notify typing start

                    if (!turnOnTyping) return;

                    // Only support single-chat type conversation.
                    if (chatType != EaseConstant.CHATTYPE_SINGLE)
                        return;

                    if (hasMessages(MSG_TYPING_END)) {
                        // reset the MSG_TYPING_END handler msg.
                        removeMessages(MSG_TYPING_END);
                    } else {
                        // Send TYPING-BEGIN cmd msg
                        EMMessage beginMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
                        EMCmdMessageBody body = new EMCmdMessageBody(ACTION_TYPING_BEGIN);
                        // Only deliver this cmd msg to online users
                        body.deliverOnlineOnly(true);
                        beginMsg.addBody(body);
                        beginMsg.setTo(toUserName);
                        EMClient.getInstance().chatManager().sendMessage(beginMsg);
                    }

                    sendEmptyMessageDelayed(MSG_TYPING_END, TYPING_SHOW_TIME);
                    break;
                case MSG_TYPING_END:

                    if (!turnOnTyping) return;

                    // Only support single-chat type conversation.
                    if (chatType != EaseConstant.CHATTYPE_SINGLE)
                        return;

                    // remove all pedding msgs to avoid memory leak.
                    removeCallbacksAndMessages(null);
                    // Send TYPING-END cmd msg
                    EMMessage endMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
                    EMCmdMessageBody body = new EMCmdMessageBody(ACTION_TYPING_END);
                    // Only deliver this cmd msg to online users
                    body.deliverOnlineOnly(true);
                    endMsg.addBody(body);
                    endMsg.setTo(toUserName);
                    EMClient.getInstance().chatManager().sendMessage(endMsg);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
}
