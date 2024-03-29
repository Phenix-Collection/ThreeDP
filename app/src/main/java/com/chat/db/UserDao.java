package com.chat.db;

import android.content.Context;

import com.chat.easeui.domain.EaseUser;
import com.chat.db.ChatDBManager;
import com.chat.entity.RobotUser;
import java.util.List;
import java.util.Map;

/**
 * ahtor: super_link
 * date: 2018/9/18 09:39
 * remark: 好友信息
 */
public class UserDao {
    public static final String TABLE_NAME = "uers";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";

    public static final String PREF_TABLE_NAME = "pref";
    public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
    public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

    public static final String ROBOT_TABLE_NAME = "robots";
    public static final String ROBOT_COLUMN_NAME_ID = "username";
    public static final String ROBOT_COLUMN_NAME_NICK = "nick";
    public static final String ROBOT_COLUMN_NAME_AVATAR = "avatar";


    public UserDao(Context context) {
    }

    /**
     * save contact list
     *
     * @param contactList
     */
    public void saveContactList(List<EaseUser> contactList) {
        ChatDBManager.getInstance().saveContactList(contactList);
    }

    /**
     * get contact list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {

        return ChatDBManager.getInstance().getContactList();
    }

    /**
     * delete a contact
     * @param username
     */
    public void deleteContact(String username){
        ChatDBManager.getInstance().deleteContact(username);
    }

    /**
     * save a contact
     * @param user
     */
    public void saveContact(EaseUser user){
        ChatDBManager.getInstance().saveContact(user);
    }

    public void setDisabledGroups(List<String> groups){
        ChatDBManager.getInstance().setDisabledGroups(groups);
    }

    public List<String>  getDisabledGroups(){
        return ChatDBManager.getInstance().getDisabledGroups();
    }

    public void setDisabledIds(List<String> ids){
        ChatDBManager.getInstance().setDisabledIds(ids);
    }

    public List<String> getDisabledIds(){
        return ChatDBManager.getInstance().getDisabledIds();
    }

    public Map<String, RobotUser> getRobotUser(){
        return ChatDBManager.getInstance().getRobotList();
    }

    public void saveRobotUser(List<RobotUser> robotList){
        ChatDBManager.getInstance().saveRobotList(robotList);
    }
}
