package com.sdk.db;

import com.faceunity.constant.AvatarConstant;
import com.faceunity.entity.AvatarP2A;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.sdk.api.entity.LoginInfoEntity;
import com.tdp.main.R;
import com.tdp.main.entity.FriendInfoEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author:zlcai
 * @createrDate:2017/9/18 17:15
 * @lastTime:2017/9/18 17:15
 * @detail:
 **/

public class CacheDataService {

	private static CacheDataService cds;
	private static final String KEY_LOGIN_INFO = "LOGIN_INFO";
	private static final String KEY_LOGIN_INFOS = "LOGIN_INFOS";
	private static List<FriendInfoEntity> friends = new ArrayList<>();
	private static LoginInfoEntity loginInfo;
	private static AvatarP2A avatarP2A;

	/***
	 * 获取好友信息
	 * @param account
	 * @return
	 */
	public static FriendInfoEntity getFriendInfo(String account){
	    if(friends.size() == 0){
            getFriendInfos();
        }
		for(FriendInfoEntity data : friends){
			if(account.equals(data.getAccount())){
				return data;
			}
		}
		return null;
	}

	/***
	 * 获取所有好友信息
	 * @return
	 */
	public static List<FriendInfoEntity> getFriendInfos(){
	    if(friends.size() == 0){
	        friends = new Gson().fromJson(BaseDataService.getValueByString(BaseDataService.DATA_FRIEND_DATA), new TypeToken<List<FriendInfoEntity>>(){}.getType());
	        if(friends == null){
	            friends = new ArrayList<>();
            }
        }
		return friends;
	}

	/***
	 * 保存多个好友信息
	 * @param datas
	 */
	public static void saveFriendInfos(List<FriendInfoEntity> datas){
		if(datas == null){
			CacheDataService.friends.clear();
		} else {
			CacheDataService.friends = datas;
		}

		BaseDataService.saveValueToSharePerference(BaseDataService.DATA_FRIEND_DATA, new Gson().toJson(CacheDataService.friends));
	}

	public static void removeFriendInfo(String account){
		if(friends.size() == 0){
			getFriendInfos();
		}
		for(int i = 0; i < friends.size(); i ++){
			if(account.equals(friends.get(i).getAccount())){
				friends.remove(i);
				BaseDataService.saveValueToSharePerference(BaseDataService.DATA_FRIEND_DATA, new Gson().toJson(CacheDataService.friends));
				continue;
			}
		}
	}

	/***
	 * 获取用户化身表
	 * @return
	 */
	public static AvatarP2A getAvatarP2A(){
		return avatarP2A;
	}

	/***
	 * 保存登录信息
	 * @param avatarP2A
	 */
	public static void saveAvatarP2A(AvatarP2A avatarP2A){

		CacheDataService.avatarP2A = avatarP2A;
		LoginInfoEntity loginInfo = CacheDataService.getLoginInfo();
		loginInfo.getUserInfo().setMirror_value(new Gson().toJson(avatarP2A));
		CacheDataService.saveLoginInfo(loginInfo);
	}

	/***
	 * 获取登录信息
	 * @return
	 */
	public static LoginInfoEntity getLoginInfo(){
		if(loginInfo == null){
			loginInfo = new Gson().fromJson(BaseDataService.getValueByString(KEY_LOGIN_INFO), LoginInfoEntity.class);
			createAvatarForLoginInfo(loginInfo);
		}
		return loginInfo;
	}

	/***
	 * 根据用户信息获取化身
	 * @param loginInfo
	 */
	private static void createAvatarForLoginInfo(LoginInfoEntity loginInfo){
		try{
			if(loginInfo != null){
				avatarP2A = new Gson().fromJson(loginInfo.getUserInfo().getMirror_value(), AvatarP2A.class);
			}
		}catch (Exception e){}

		// 如果转换失败的话创建一个默认的
		if(avatarP2A == null){
			avatarP2A = new AvatarP2A(AvatarP2A.style_art, R.drawable.head_1_male, AvatarP2A.gender_boy, "head_1/head.bundle",
					AvatarConstant.hairBundle("head_1", AvatarP2A.gender_boy), 2, 0);
		}

	}

	/***
	 * 保存token
	 * @param loginInfo 登录实体
	 */
	public static void saveLoginInfo(LoginInfoEntity loginInfo){
		if(loginInfo != null){
//			getLoginInfos().put(loginInfo.getUserInfo().getAccount(), loginInfo);
//			BaseDataService.saveValueToSharePerference(KEY_LOGIN_INFOS, new Gson().toJson(loginInfos)); // 保存多用户数据

			CacheDataService.loginInfo = loginInfo;
			BaseDataService.saveValueToSharePerference(KEY_LOGIN_INFO, new Gson().toJson(loginInfo).toString());
		}
	}

	/** 清除用户信息缓存 */
	public static void clearUserInfo(){
		// 从多用户中删除当前登录者
//		loginInfos.remove(loginInfo.getUserInfo().getAccount());
//		BaseDataService.saveValueToSharePerference(KEY_LOGIN_INFOS, new Gson().toJson(loginInfos)); // 保存多用户数据
		// 删除当前登录用户的登录信息
		CacheDataService.loginInfo = null;
		BaseDataService.remove(KEY_LOGIN_INFO);
	}

	/** 清除所有配置信息 */
	public synchronized static void clearAll(){
		clearUserInfo();

		Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
		for (String key : conversations.keySet()){
			EMClient.getInstance().chatManager().deleteConversation(conversations.get(key).conversationId(), true);
		}
	}
}
