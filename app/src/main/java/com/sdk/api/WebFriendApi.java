package com.sdk.api;

import com.sdk.net.msg.WebMsg;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author:zlcai
 * @createrDate:2017/9/14 16:59
 * @lastTime:2017/9/14 16:59
 * @detail:
 **/

public interface WebFriendApi {

	// 查找用户
	@FormUrlEncoded
	@POST("friend/selectFriend")
	public Observable<WebMsg> searchUser(@Field("username") String username);

	//
	@FormUrlEncoded
	@POST("friend/userInfo")
	public Observable<WebMsg> userInfo(@Field("account") String userId);

	//@FormUrlEncoded
	@POST("friend/friendList")
	public Observable<WebMsg> friendList();

	// 获取好友请求数据
	@FormUrlEncoded
	@POST("friend/applyList")
	public Observable<WebMsg> applyList(@Field("applyDate") long applyDate);

	// 发送好友请求数据
	@FormUrlEncoded
	@POST("friend/friendApplication")
	public Observable<WebMsg> friendApplication(@Field("account") String account, @Field("recommend") String recommend);

	// 同意或拒绝好友请求
	@FormUrlEncoded
	@POST("friend/agreeOrRefuseFriend")
	public Observable<WebMsg> agreeOrRefuseFriend(@Field("account") String account, @Field("remark") String remark, @Field("isapplication") int isapplication);

	// 同意或拒绝好友请求
	@FormUrlEncoded
	@POST("friend/deleteFriend")
	public Observable<WebMsg> deleteFriend(@Field("account") String account);

	// 同意或拒绝好友请求
	@FormUrlEncoded
	@POST("friend/modifyAlias")
	public Observable<WebMsg> modifyAlias(@Field("remark") String remark, @Field("account") String account);

}
