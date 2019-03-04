package com.sdk.core;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.chat.ChatHelper;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.db.CacheDataService;
import com.sdk.db.entity.NotifyMessage;
import com.tdp.main.R;
import com.tdp.main.activity.MainActivity;

import java.util.List;

/**
 * ahtor: super_link
 * date: 2018/11/1 17:32
 * remark:
 */
public class HuanxinControl {
    private static HuanxinControl instance;
    private Context context;

    public HuanxinControl(Context context){
        this.context = context;
        init();
        login();
    }

    public static void init(Context context){
        if(instance == null){
            instance = new HuanxinControl(context);
        }
    }

    public static HuanxinControl getInstance() {
        return instance;
    }

    private void init(){
        ChatHelper.getInstance().init(context);
    }

    /***
     * 登录
     */
    public void login(){

        //loginOut();

        LoginInfoEntity data = CacheDataService.getLoginInfo();
        if(data != null){

            Log.v("HuanxinControl", "登录环信服务器，用户名："+data.getUserInfo().getAccount());
//            EMClient.getInstance().loginWithToken();
            try{
                EMClient.getInstance().login(data.getUserInfo().getAccount(),"123456",new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        EMClient.getInstance().chatManager().addMessageListener(msgListener); // 监听消息接收
                        Log.d("HuanxinControl", "login::onSuccess::登录聊天服务器成功！");
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.d("HuanxinControl", "环信用户登录失败::" + code + ":" + message);
                    }
                });
            }catch (Exception e){}
        }

    }

    /***
     * 注销
     */
    public void loginOut(){
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                EMClient.getInstance().chatManager().removeMessageListener(msgListener);
                Log.d("HuanxinControl", "loginOut::onSuccess::环信注销成功！");
            }

            @Override
            public void onError(int i, String s) {
                Log.d("HuanxinControl", "loginOut::onError::环信注销失败！");
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * 接收消息监听
     */
    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            String from = messages.get(0).getFrom();
            String msg = "";
            switch (messages.get(0).getType()){
                case TXT:
                    EMTextMessageBody msgBody = (EMTextMessageBody) messages.get(0).getBody();
                    msg = msgBody.getMessage();
                    break;
                case IMAGE:
                    break;
                case VIDEO:
                    break;
                case LOCATION:
                    break;
                case FILE:
                    break;
                case VOICE:
                    break;
                case CMD:
                    break;
            }
            Log.v("EMMessageListener", "您收到来自:"+from+"的一条消息！");
            NotifyMessage notify = new NotifyMessage();
            notify.setFNotifyTitle("您收到来自:"+from+"的一条消息！");
            notify.setFNotifyTitle(msg);
            showNotify(notify);
            //收到消息
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            Log.v("EMMessageListener", "收到透传消息:"+new Gson().toJson(messages.get(0).getBody()));
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }
        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    private void showNotify(NotifyMessage data){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("1", "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
//            channel.enableLights(false); //是否在桌面icon右上角展示小红点
//            channel.setLightColor(Color.GREEN); //小红点颜色
//            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
//            channel.enableVibration(false);
//            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, Notification.AUDIO_ATTRIBUTES_DEFAULT);
//            mNotificationManager.createNotificationChannel(channel);
//        }




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.GREEN); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            channel.enableVibration(false);
            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, Notification.AUDIO_ATTRIBUTES_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder build =new NotificationCompat.Builder(context, "1");
            build.setContentTitle(data.getFNotifyTitle())//设置通知栏标题
            .setContentText(data.getFNotifyContent()) //
            .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
            //  .setNumber(number) //设置通知集合的数量
            .setTicker(data.getFNotifyTitle()) //通知首次出现在通知栏，带上升动画效果的
            .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
            .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
            .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
            .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
            .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
            //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
            .setSmallIcon(R.drawable.icon_logo_small);//设置通知小ICON

            // 设置跳转的activity
            if(true){
                Intent intent1 = new Intent(context,MainActivity.class);
                intent1.putExtra("type", 4); // 1:跳转到个人中心
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
                build.setContentIntent(pendingIntent);
            }

            mNotificationManager.notify(1, build.build());
        } else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setContentTitle(data.getFNotifyTitle())//设置通知栏标题
            .setContentText(data.getFNotifyContent()) //
            .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
            //  .setNumber(number) //设置通知集合的数量
            .setTicker(data.getFNotifyTitle()) //通知首次出现在通知栏，带上升动画效果的
            .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
            .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
            .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
            .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
            .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
            //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
            .setSmallIcon(R.drawable.icon_logo_small);//设置通知小ICON

            // 设置跳转的activity
            if(true){
                Intent intent1 = new Intent(context,MainActivity.class);
                intent1.putExtra("type", 4); // 1:跳转到个人中心
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
                mBuilder.setContentIntent(pendingIntent);
            }

            mNotificationManager.notify(1, mBuilder.build());
        }






    }

    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(context, 1, new Intent(), flags);
        return pendingIntent;
    }
}
