package com.sdk.core;

import android.content.Context;

import com.sdk.db.DBManager;

/**
 * ahtor: super_link
 * date: 2018/10/27 13:47
 * remark:
 */
public class SdkSetting {

    private static SdkSetting instance;
    private Context context;
    private DBManager dbManager;

    private SdkSetting(Context context){
        this.context = context;
        initDB();
    }

    public static void init(Context context){
        if(instance ==null){
            instance = new SdkSetting(context);
        }
    }

    public static SdkSetting getInstance() {
        return instance;
    }


    // 初始化数据库
    private void initDB(){
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
    }


    //---------------------------------------------------------
    public DBManager getDbManager() {
        return dbManager;
    }

}
