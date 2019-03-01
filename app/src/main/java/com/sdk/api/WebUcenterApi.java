package com.sdk.api;

import com.google.gson.Gson;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.sdk.net.msg.WebMsg;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author:zlcai
 * @createrDate:2017/9/14 16:59
 * @lastTime:2017/9/14 16:59
 * @detail:
 **/

public interface WebUcenterApi {
	// 登录
	@FormUrlEncoded
	@POST("author/login")
	public Observable<WebMsg> login(@Field("username") String username, @Field("password") String password, @Field("type") String type, @Field("secretKey") String secretKey);
	// 发送手机验证码(从参数：type, _register:注册验证码， _forget：忘记密码验证码)
	@FormUrlEncoded
	@POST("user/sendCode")
	public Observable<WebMsg> sendCode(@Field("phoneNumber") String phoneNumber, @Field("secretKey") String secretKey, @Field("type") String type);
	@FormUrlEncoded
	@POST("user/register")
	public Observable<WebMsg> register(@Field("phoneNumber") String phoneNumber, @Field("password") String password, @Field("code") String code, @Field("secretKey") String secretKey);
	@FormUrlEncoded
	@POST("user/updatePwd")
	public Observable<WebMsg> updatePwd(@Field("account") String account, @Field("password") String password, @Field("passwordNew") String passwordNew, @Field("secretKey") String secretKey);
	// 修改密码
	@FormUrlEncoded
	@POST("user/forget")
	public Observable<WebMsg> forget(@Field("phone") String phone, @Field("password") String password, @Field("code") String code, @Field("secretKey") String secretKey);

	//保存用户模型信息
	@FormUrlEncoded
	@POST("user/edit_mirror")
	Observable<WebMsg> editMirror(@Field("url") String url, @Field("name") String name,@Field("sex") String sex,@Field("skinColor") String skinColor
			,@Field("cloth") String cloth,@Field("glass") String glass,@Field("hats") String hats,@Field("cosplay") String cosplay);

	// 修改用户信息
	@FormUrlEncoded
	@POST("user/edit")
	public Observable<WebMsg> edit(@Field("nick_name") String nick_name, @Field("profile_photo") String profile_photo, @Field("real_name") String real_name,
		   @Field("sex") int sex, @Field("birthday") String birthday, @Field("address") String address, @Field("educational") String educational,
		   @Field("occupation") String occupation, @Field("email") String email, @Field("phone_number") String phone_number, @Field("security") int security);

	/***
	 * 修改用户信息
	 * @param mirrorValue 化身实体
	 * @return
	 */
	@FormUrlEncoded
	@POST("user/edit_mirror")
	public Observable<WebMsg> editMirror(@Field("mirrorValue") String mirrorValue);

}
