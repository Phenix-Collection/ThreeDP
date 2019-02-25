package com.sdk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.sdk.db.entity.EaseMessageHistoryList;
import com.sdk.db.entity.NotifyMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:zlcai
 * @new date:2016-11-23
 * @last date:2016-11-23
 * @remark:
 **/

public class DBManager {
	private DBHelper helper;  
    private SQLiteDatabase db;  
      
    public DBManager(Context context) {  
        helper = new DBHelper(context);  
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);  
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里  
        db = helper.getWritableDatabase();  
    }  
    
    //===================== 异步任务开始 ===========================
    
    /** 
     * 异步任务
     * @param 异步任务 
     */  
//    public void add(Async_task asyncTask) {
//        db.beginTransaction();  //开始事务
//        try {
//            db.execSQL("INSERT INTO async_task VALUES(null, ?, ?, ?, ?)", new Object[]{asyncTask.getType(), asyncTask.getUrl(), asyncTask.getParams(), asyncTask.getTime()});
//            db.setTransactionSuccessful();  //设置事务成功完成
//        } finally {
//            db.endTransaction();    //结束事务
//        }
//    }
    
    /** 
     * query all persons, return cursor 
     * @return  Cursor 
     */  
//    public List<Async_task> findAsyncTasksByType(int type) {
//    	Cursor cursor = db.query("async_task", null, "type = ? ", new String[]{String.valueOf(type)}, null, null, null);
//
//    	List<Async_task> datas = new ArrayList<>();
//        while (cursor.moveToNext()) {
//        	Async_task data = new Async_task();
//        	data.setId(cursor.getInt(cursor.getColumnIndex("id")));
//        	data.setUrl(cursor.getString(cursor.getColumnIndex("url")));
//        	data.setParams(cursor.getString(cursor.getColumnIndex("params")));
//        	data.setType(cursor.getInt(cursor.getColumnIndex("type")));
//        	data.setTime(cursor.getLong(cursor.getColumnIndex("time")));
//        	datas.add(data);
//        }
//        if (null != cursor) {
//            cursor.close();
//        }
//
//        return datas;
//    }
    
    /** 
     * delete old person 
     * @param id
     */  
//    public void delAsyncTask(int id) {
//        db.delete("async_task", "id = ?", new String[]{String.valueOf(id)});
//    }
    
    /**
     * 删除任务所有数据
     */
    public void delAllTask(){
    	db.delete("async_task", null, null);
    }
    
    //===================== 异步任务结束 ===========================
    
    //===================== 数据缓存开始 ===========================
      
    /** 
     * 新增缓存内容
     * @param  
     */  
//    public void add(CacheData cacheData) {
//        db.beginTransaction();  //开始事务
//        try {
//
//        	ContentValues cv = new ContentValues();
//            cv.put("name" , cacheData.getName());
//            cv.put("content" , cacheData.getContent());
//            cv.put("update_time", cacheData.getUpdate_time());
//
////            db.execSQL("INSERT INTO cachedata VALUES(?, ?, ?,null)", cv);
//            db.insert("cachedata", null, cv);
//            db.setTransactionSuccessful();  //设置事务成功完成
//        } finally {
//            db.endTransaction();    //结束事务
//        }
//    }
    
    /**
     * 修改缓存内容
     * @param cacheData
     */
//    public void update(CacheData cacheData){
//
////    	Log.v("", "UPDB:"+new Gson().toJson(cacheData));
//    	db.beginTransaction(); // 开始事物
//    	try{
//    		ContentValues cv = new ContentValues();
//            cv.put("content" , cacheData.getContent());
//            cv.put("update_time", cacheData.getUpdate_time());
//
//    		String where = "id="+cacheData.getId();
//
//    		db.update("cachedata", cv, where, null);
////    		db.execSQL("update cachedata set content=?,update_time=? where name=?",new Object[]{cacheData.getContent(), cacheData.getUpdate_time(),cacheData.getName()});
//    		db.setTransactionSuccessful();  //设置事务成功完成
//    	} finally {
//    		db.endTransaction(); // 结束
//    	}
//    }
    
    /***
     * 根据键值查找缓存
     * @param name
     */
//    public CacheData findCacheData(String name){
//    	Cursor cursor = db.query("cachedata", null, "name = ? ", new String[]{String.valueOf(name)}, null, null, null);
//
//    	CacheData cacheData = null;
//        while (cursor.moveToNext()) {
//        	cacheData = new CacheData();
//        	cacheData.setId(cursor.getInt(cursor.getColumnIndex("id")));
//        	cacheData.setName(cursor.getString(cursor.getColumnIndex("name")));
//        	cacheData.setUpdate_time(cursor.getLong(cursor.getColumnIndex("update_time")));
//        	cacheData.setContent(cursor.getString(cursor.getColumnIndex("content")));
//        }
//        if (null != cursor) {
//            cursor.close();
//        }
//
//
////        Log.v("", "DB:"+new Gson().toJson(cacheData));
//        return cacheData;
//    }
    
    /***
     * 删除缓存所有数据
     */
    private void clearCache(){
    	db.beginTransaction();
    	try{
	    	db.delete("cachedata", null, null);
	    	db.setTransactionSuccessful();
    	}finally {
    		db.endTransaction();
    	}
    }
    
    //===================== 数据缓存结束 ===========================
    

    //===================== 异步动态分享开始 ==============================
//    public List<ShareTrendsTask> findShareTrendsTasks(){
//    	Cursor cursor = db.query("sharetrendstask", null, "addTime < ?", new String[]{String.valueOf(System.currentTimeMillis()-1800000)}, null, null, null);
//    	List<ShareTrendsTask> datas = new ArrayList<ShareTrendsTask>();
//    	ShareTrendsTask data = null;
//        while (cursor.moveToNext()) {
//        	data = new ShareTrendsTask();
//        	data.setId(cursor.getInt(cursor.getColumnIndex("id")));
//        	data.setTitle(cursor.getString(cursor.getColumnIndex("title")));
//        	data.setContent(cursor.getString(cursor.getColumnIndex("content")));
//        	data.setTypeSys(cursor.getString(cursor.getColumnIndex("typeSys")));
//        	data.setImgPath(cursor.getString(cursor.getColumnIndex("imgPath")));
//        	data.setSystemTypes(cursor.getString(cursor.getColumnIndex("systemTypes")));
//        	data.setSystemTypes(cursor.getString(cursor.getColumnIndex("postLinkLogoPath")));
//        	data.setPostType(cursor.getInt(cursor.getColumnIndex("postType")));
//        	datas.add(data);
//        }
//        if (null != cursor) {
//            cursor.close();
//        }
//        return datas;
//    }

    /***
     * 删除动态分享任务（当任务上传成功后）
     * @param id
     */
//    public void delShareTrendsTask(int id){
//    	db.delete("sharetrendstask", "id=?", new String[]{String.valueOf(id)});
//    }
    
    /***
     * 更新我的动态分享任务
     * @param data
     */
//    public void update(ShareTrendsTask data){
//    	db.beginTransaction(); // 开始事物
//    	try{
//    		db.execSQL("update sharetrendstask set imgPath=?,postLinkLogoPath=?,tryCount=?,lastTryTime=? where id=?",new Object[]{data.getImgPath(),data.getPostLinkLogoPath(),data.getTryCount(),data.getLastTryTime(),data.getId()});
//    		db.setTransactionSuccessful();  //设置事务成功完成
//    	} finally {
//    		db.endTransaction(); // 结束
//    	}
//    }
    
    /***
     * 新增一个任务
     * @param data
     */
//    public void add(ShareTrendsTask data){
//    	db.beginTransaction();  //开始事务
//        try {
//
//        	ContentValues cv = new ContentValues();
//            cv.put("title" , data.getTitle());
//            cv.put("content" , data.getContent());
//            cv.put("typeSys", data.getTypeSys());
//            cv.put("imgPath", data.getImgPath());
//            cv.put("systemTypes", data.getSystemTypes());
//            cv.put("postLinkLogoPath", data.getPostLinkLogoPath());
//            cv.put("tryCount", data.getTryCount());
//            cv.put("lastTryTime", data.getLastTryTime());
//            cv.put("postType", data.getPostType());
//            cv.put("addTime", data.getAddTime());
////            db.execSQL("INSERT INTO cachedata VALUES(?, ?, ?,null)", cv);
//            db.insert("sharetrendstask", null, cv);
//            db.setTransactionSuccessful();  //设置事务成功完成
//        } finally {
//            db.endTransaction();    //结束事务
//        }
//    }
    
    
    //===================== 聊天历史 ============================
    
    /***
     * 新增一个任务
     * @param data
     */
    public void add(EaseMessageHistoryList data) {
    	db.beginTransaction();  //开始事务  
        try {
        	
        	ContentValues cv = new ContentValues();  
            cv.put("toId" , data.getToId());  
            cv.put("toName" , data.getToName());
            cv.put("lastTime", data.getLastTime());
            cv.put("toLogo", data.getToLogo());
            cv.put("lastUserId", data.getLastUserId());
            cv.put("lastMsg", data.getLastMsg());
            cv.put("lastName", data.getLastName());
            cv.put("messageCount", data.getMessageCount());
            db.insert("easeMessageHistoryList", null, cv);
            db.setTransactionSuccessful();  //设置事务成功完成  
        } finally {  
            db.endTransaction();    //结束事务  
        }
    }
  
    /**
     * 获取所有的历史记录，降序排序
     * @return
     */
    public List<EaseMessageHistoryList> findEaseMessageHistoryLists(){
    	Cursor cursor = db.query("easeMessageHistoryList", null, null, null, null, null, "lastUserId desc");
    	List<EaseMessageHistoryList> datas = new ArrayList<EaseMessageHistoryList>();
    	EaseMessageHistoryList data = null;
        while (cursor.moveToNext()) {
        	data = new EaseMessageHistoryList();
        	data.setToId(cursor.getString(cursor.getColumnIndex("toId")));
        	data.setToName(cursor.getString(cursor.getColumnIndex("toName")));
        	data.setLastTime(cursor.getLong(cursor.getColumnIndex("lastTime")));
        	data.setToLogo(cursor.getString(cursor.getColumnIndex("toLogo")));
        	data.setLastUserId(cursor.getString(cursor.getColumnIndex("lastUserId")));
        	data.setLastMsg(cursor.getString(cursor.getColumnIndex("lastMsg")));
        	data.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
        	data.setMessageCount(cursor.getInt(cursor.getColumnIndex("messageCount")));
        	datas.add(data);
        }  
        if (null != cursor) {  
            cursor.close();  
        }
        return datas;  
    }
    
    /***
     * 根据键值查找缓存
     * @param
     */
    public EaseMessageHistoryList findEaseMessageHistoryList(String id){ 	
    	Cursor cursor = db.query("easeMessageHistoryList", null, "toId = ? ", new String[]{String.valueOf(id)}, null, null, null);  
    	EaseMessageHistoryList data = null;
        while (cursor.moveToNext()) {
        	data = new EaseMessageHistoryList();
        	data.setToId(cursor.getString(cursor.getColumnIndex("toId")));
        	data.setToName(cursor.getString(cursor.getColumnIndex("toName")));
        	data.setLastTime(cursor.getLong(cursor.getColumnIndex("lastTime")));
        	data.setToLogo(cursor.getString(cursor.getColumnIndex("toLogo")));
        	data.setLastUserId(cursor.getString(cursor.getColumnIndex("lastUserId")));
        	data.setLastMsg(cursor.getString(cursor.getColumnIndex("lastMsg")));
        	data.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
        	data.setMessageCount(cursor.getInt(cursor.getColumnIndex("messageCount")));
        }  
        if (null != cursor) {  
            cursor.close();  
        }
        return data;  
    }
    
    /***
     * 获取未读消息的数量
     * @return
     */
    public int getEaseMessageHistoryUnreadCount(){
    	Cursor cursor = db.rawQuery("select count(*) from easeMessageHistoryList where messageCount > 0", null);
    	cursor.moveToFirst();
    	int count = cursor.getInt(0);
        return count;  
    }
    
    /***
     * 更新最后一次消息
     * @param data
     */
    public void update(EaseMessageHistoryList data) {	
    	db.beginTransaction(); // 开始事物
    	try{
    		db.execSQL("update easeMessageHistoryList set toName=?,lastTime=?,toLogo=?,lastUserId=?,lastMsg=?,lastName=?,messageCount=? where toId=?",
    				new Object[]{data.getToName(),data.getLastTime(),data.getToLogo(),data.getLastUserId(),
    				data.getLastMsg(), data.getLastName(), data.getMessageCount(), data.getToId()});
    		db.setTransactionSuccessful();  //设置事务成功完成  
    	} finally {
    		db.endTransaction(); // 结束
    	}
    }
    
    /***
     * 删除消息历史数据
     * @param id
     */
    public void delEaseMessageHistoryList(String id){
    	db.beginTransaction();
    	try{
    		db.delete("easeMessageHistoryList", "toId=?", new String[]{id});
	    	db.setTransactionSuccessful();
    	}finally {
    		db.endTransaction();
    	}
    	
    }
    
    /***
     * 清空聊天历史数据
     */
    public void clearEaseMessageHistoryList(){
    	db.beginTransaction();
    	try{
	    	db.delete("easeMessageHistoryList", null, null);
	    	db.setTransactionSuccessful();
    	}finally {
    		db.endTransaction();
    	}
    }
    
    
    //===================== 系统通知 ======================
    
    /***
     * 获取所有通知信息
     * @return
     */
    public List<NotifyMessage> getAllNotifyMessages(){
    	Cursor cursor = db.query("notifyMessage", null, null, null, null, null, "FAddTime desc");
    	List<NotifyMessage> datas = new ArrayList<NotifyMessage>();
    	NotifyMessage data = null;
        while (cursor.moveToNext()) {
        	data = new NotifyMessage();
        	data.setPNotifyId(cursor.getInt(cursor.getColumnIndex("PNotifyId")));
        	data.setFNotifyTitle(cursor.getString(cursor.getColumnIndex("FNotifyTitle")));
        	data.setFNotifyContent(cursor.getString(cursor.getColumnIndex("FNotifyContent")));
        	data.setFType(cursor.getInt(cursor.getColumnIndex("FType")));
        	data.setKReceiverId(cursor.getInt(cursor.getColumnIndex("KReceiverId")));
        	data.setFAddTime(cursor.getLong(cursor.getColumnIndex("FAddTime")));
        	data.setFVersionCode(cursor.getInt(cursor.getColumnIndex("FVersionCode")));
        	data.setFExtContent(cursor.getString(cursor.getColumnIndex("FExtContent")));
        	data.setFState(cursor.getInt(cursor.getColumnIndex("FState")));
        	datas.add(data);
        }  
        if (null != cursor) {  
            cursor.close();  
        }
        return datas;  
    }
    
    /***
     * 根据id删除通知
     * @param PNotifyId
     */
    public void delNotifyMessage(int PNotifyId){
    	db.beginTransaction();
    	try{
    		db.delete("notifyMessage", "PNotifyId=?", new String[]{String.valueOf(PNotifyId)});
	    	db.setTransactionSuccessful();
    	}finally {
    		db.endTransaction();
    	}
    }
    
    /***
     * 更新系统通知
     * @param FState 状态
     * @param PNotifyId 通知id
     */
    public void updateNotifyMessage(int FState, int PNotifyId){
    	db.beginTransaction(); // 开始事物
    	try{
    		db.execSQL("update notifyMessage set FState=? where PNotifyId=?", new Object[]{FState, PNotifyId});
    		db.setTransactionSuccessful();  //设置事务成功完成  
    	} finally {
    		db.endTransaction(); // 结束
    	}
    }
    
    public void add(List<NotifyMessage> notifyMessage){
    	
    	if(notifyMessage == null) return;
    	
    	db.beginTransaction();  //开始事务  
        try {
        	for(NotifyMessage data : notifyMessage){
        		ContentValues cv = new ContentValues();
                cv.put("PNotifyId" , data.getPNotifyId());  
                cv.put("FNotifyTitle" , data.getFNotifyTitle());
                cv.put("FNotifyContent", data.getFNotifyContent());
                cv.put("FType", data.getFType());
                cv.put("KReceiverId", data.getKReceiverId());
                cv.put("FAddTime", data.getFAddTime());
                cv.put("FVersionCode", data.getFVersionCode());
                cv.put("FExtContent", data.getFExtContent());
                cv.put("FState", data.getFState());
                db.insert("notifyMessage", null, cv);
        	}
            db.setTransactionSuccessful();  //设置事务成功完成  
        } finally {  
            db.endTransaction();    //结束事务  
        }
    }
    
    /***
     * 清空通知
     */
    public void clearNotifyMessageList(){
    	db.beginTransaction();
    	try{
	    	db.delete("notifyMessage", null, null);
	    	db.setTransactionSuccessful();
    	}finally {
    		db.endTransaction();
    	}
    }    
    
    //===================== 异步动态分享结束 ==============================
    /** 
     * close database 
     */  
    public void closeDB() {  
        db.close();  
    }
    
    
    //============ 清空数据
    public void clearData(){
    	clearCache();
    	clearEaseMessageHistoryList();
    	clearNotifyMessageList();
    }
}
