package com.sdk.core;

import android.content.Context;
import android.os.Handler;

import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.api.entity.UserInfoEntity;
import com.sdk.db.CacheDataService;
import cn.jpush.android.api.JPushInterface;

/**
 * ahtor: super_link
 * date: 2018/10/27 09:37
 * remark:
 */
public class JPushControl {

    private static JPushControl instance;
    private Handler handler;

    private JPushControl(final Context context) {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(context);
    }

    /***
     * 友盟初始化
     * @param context
     */
    public static void init(final Context context) {

        if (instance == null) instance = new JPushControl(context);
    }

    public static JPushControl getInstance() {
        return instance;
    }

    // 添加别名（根据用户标识新增别名到推送代理后，才能收到对应的消息）
    public void registerAlias(Context context) {
        LoginInfoEntity data = CacheDataService.getLoginInfo();
        if(data != null){
            JPushInterface.setAlias(context, 1, data.getLoginTime());
        }

    }

    //
//    // 移除别名（移除别名后，才能防止在未登陆情况下收到消息）
    public void unregisterAlias(Context context) {
        JPushInterface.deleteAlias(context, 1);
    }

}
