package com.chat;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.chat.conference.LiveActivity;
import com.chat.db.ChatDBManager;
import com.chat.db.InviteMessgeDao;
import com.chat.db.UserDao;
import com.chat.easeui.EaseUI;
import com.chat.easeui.domain.EaseAvatarOptions;
import com.chat.easeui.domain.EaseUser;
import com.chat.utils.PreferenceManager;
import com.chat.entity.RobotUser;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

/**
 * ahtor: super_link
 * date: 2018/9/18 09:23
 * remark:
 */
public class ChatHelper {

    public boolean isVoiceCalling;
    public boolean isVideoCalling;
    private String TAG = "ChatHelper";
    private static ChatHelper instance = null;
    private Context context;
    private ChatModel chatModel = null;
    private LocalBroadcastManager broadcastManager;
    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;
    private EaseUI easeUI;

    private Map<String, RobotUser> robotList;

    private String username;

    public synchronized static ChatHelper getInstance() {
        if (instance == null) {
            instance = new ChatHelper();
        }
        return instance;
    }

    /***
     * 初始化
     * @param context
     */
    public void init(Context context){
        chatModel = new ChatModel(context); // 初始化配置文件
        EMOptions emOptions = loadChatOptions(); // 加载并初始化环信配置

        if(EaseUI.getInstance().init(context, emOptions)){
            this.context = context;
            EMClient.getInstance().setDebugMode(true);
            easeUI = EaseUI.getInstance(); // 获取EaseUI的全局实例

            //
            EaseAvatarOptions options = new EaseAvatarOptions();
            options.setAvatarShape(1); // 设置头像为圆形
            easeUI.setAvatarOptions(options);


            setEaseUIProviders();
            PreferenceManager.init(); //
            //	getUserProfileManager().init(context); // 这个不知道是干嘛用的，有空要研究一下
            setCallOptions();
            setGlobalListeners();
            broadcastManager = LocalBroadcastManager.getInstance(context);
            initDbDao();
        }

    }


    /***
     * 加载配置信息
     * @return
     */
    private EMOptions loadChatOptions(){
        Log.d(TAG, "init HuanXin Options");
        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(false);
        // set if you need read ack
        options.setRequireAck(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(false);

        /**
         * NOTE:你需要设置自己申请的Sender ID来使用Google推送功能，详见集成文档
         */
        options.setFCMNumber("921300338324");
        //you need apply & set your own id if you want to use Mi push notification
        options.setMipushConfig("2882303761517426801", "5381742660801");
        // 设置是否使用 fcm，有些华为设备本身带有 google 服务，
        options.setUseFCM(chatModel.isUseFCM());

        //set custom servers, commonly used in private deployment
        if(chatModel.isCustomServerEnable() && chatModel.getRestServer() != null && chatModel.getIMServer() != null) {
            options.setRestServer(chatModel.getRestServer());
            options.setIMServer(chatModel.getIMServer());
            if(chatModel.getIMServer().contains(":")) {
                options.setIMServer(chatModel.getIMServer().split(":")[0]);
                options.setImPort(Integer.valueOf(chatModel.getIMServer().split(":")[1]));
            }
        }

        if (chatModel.isCustomAppkeyEnabled() && chatModel.getCutomAppkey() != null && !chatModel.getCutomAppkey().isEmpty()) {
            options.setAppKey(chatModel.getCutomAppkey());
        }

        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
        options.setDeleteMessagesAsExitGroup(getModel().isDeleteMessagesAsExitGroup());
        options.setAutoAcceptGroupInvitation(getModel().isAutoAcceptGroupInvitation());
        // Whether the message attachment is automatically uploaded to the Hyphenate server,
        options.setAutoTransferMessageAttachments(getModel().isSetTransferFileByUser());
        // Set Whether auto download thumbnail, default value is true.
        options.setAutoDownloadThumbnail(getModel().isSetAutodownloadThumbnail());
        return options;
    }

    /***
     * 设置EaseUI的一些信息，暂时不做任何初始化，以为我想看看不设置他到底想怎样
     * 参考代码的位置：com.hxchat.DemoHelper->setEaseUIProviders()
     */
    private void setEaseUIProviders(){

    }

    /***
     * 音视频聊天的配置项，包含了回调之类的东西，暂时就不设置，后期再回来弄
     * 参考代码的位置：com.hxchat.DemoHelper->setCallOptions()
     */
    private void setCallOptions(){

    }

    /***
     * 根据介绍意思是全局的监听，反正不管他监听什么，暂时不理他，等跑起来再说
     */
    private void setGlobalListeners(){

    }

    private void initDbDao() {
        inviteMessgeDao = new InviteMessgeDao(context);
        userDao = new UserDao(context);
    }

    /***
     * 获取当前用户名
     * @return
     */
    public String getCurrentUsernName(){
        if(username == null){
            username = chatModel.getCurrentUsernName();
        }
        return username;
    }
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    public Map<String, RobotUser> getRobotList() {
        if (isLoggedIn() && robotList == null) {
            robotList = chatModel.getRobotList();
        }
        return robotList;
    }

    /**
     * 处理会议邀请
     * @param confId 会议 id
     * @param password 会议密码
     */
    public void goConference(String confId, String password, String extension) {
        if(isDuringMediaCommunication()) {
            return;
        }
        String inviter = "";
        String groupId = null;
        try {
            JSONObject jsonObj = new JSONObject(extension);
            inviter = jsonObj.optString(Constant.EXTRA_CONFERENCE_INVITER);
            groupId = jsonObj.optString(Constant.EXTRA_CONFERENCE_GROUP_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //ConferenceActivity.receiveConferenceCall(appContext, confId, password, inviter, groupId);
    }

    private boolean isDuringMediaCommunication() {
        String topClassName = easeUI.getTopActivity().getClass().getSimpleName();
        if (easeUI.hasForegroundActivies() && ("LiveActivity".equals(topClassName) || "ConferenceActivity".equals(topClassName))) {
            return true;
        }
        return false;
    }

    public void goLive(String confId, String password, String inviter) {
        if(isDuringMediaCommunication()) {
            return;
        }

        LiveActivity.watch(context, confId, password, inviter);
    }

    private Map<String, EaseUser> contactList;
    public Map<String, EaseUser> getContactList() {
        if (isLoggedIn() && contactList == null) {
            contactList = chatModel.getContactList();
        }

        // return a empty non-null object to avoid app crash
        if(contactList == null){
            return new Hashtable<String, EaseUser>();
        }

        return contactList;
    }

    void endCall() {
        try {
            EMClient.getInstance().callManager().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isSyncingGroupsWithServer = false;
    private boolean isSyncingContactsWithServer = false;
    private boolean isSyncingBlackListWithServer = false;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;
    private boolean isGroupAndContactListenerRegisted;

    synchronized void reset(){
        isSyncingGroupsWithServer = false;
        isSyncingContactsWithServer = false;
        isSyncingBlackListWithServer = false;

        chatModel.setGroupsSynced(false);
        chatModel.setContactSynced(false);
        chatModel.setBlacklistSynced(false);

        isGroupsSyncedWithServer = false;
        isContactsSyncedWithServer = false;
        isBlackListSyncedWithServer = false;

        isGroupAndContactListenerRegisted = false;

        setContactList(null);
        setRobotList(null);
        // 被我删除了
        //    getUserProfileManager().reset();
        ChatDBManager.getInstance().closeDB();
    }

    public void setRobotList(Map<String, RobotUser> robotList) {
        this.robotList = robotList;
    }

    public void setContactList(Map<String, EaseUser> aContactList) {
        if(aContactList == null){
            if (contactList != null) {
                contactList.clear();
            }
            return;
        }

        contactList = aContactList;
    }

    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        endCall();
        Log.d(TAG, "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }


    public ChatModel getModel(){
        return (ChatModel) chatModel;
    }
}
