package com.sdk.api;

import com.sdk.net.msg.WebMsg;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WebVideoApi {
    //增加视频浏览记录
    @FormUrlEncoded
    @POST("trends/trendsEye")
    Observable<WebMsg> trendsEye(@Field("userId") String userId, @Field("trendsId") String trendsId);

    //获取视频评论列表
    @FormUrlEncoded
    @POST("trends/queryTrendsRecommendByTrendsId")
    Observable<WebMsg> queryTrendsRecommendByTrendsId(@Field("trendsId") String trendsId,@Field("pageSize") String pageSize,@Field("pageNum") String pageNum);

    //回复评论/评论
    @FormUrlEncoded
    @POST("trends/trendsRecommend")
    Observable<WebMsg> trendsRecommend(@Field("trendsId") String trendsId,@Field("toUserId") String toUserId,@Field("content") String content);

    //增加动态
    @FormUrlEncoded
    @POST("trends/addTrends")
    Observable<WebMsg> addTrends(@Field("title") String title,@Field("content") String content,@Field("attachment") String attachment,@Field("type") String type);
    //获取好友视频列表
    @FormUrlEncoded
    @POST("trends/trendsList")
    Observable<WebMsg> trendsList(@Field("pageSize") String pageSize, @Field("pageNum") String pageNum);
    //获取社区视频列表
    @FormUrlEncoded
    @POST("trends/trendsAllList")
    Observable<WebMsg> trendsAllList(@Field("pageSize") String pageSize, @Field("pageNum") String pageNum);
    //点赞视频
    @FormUrlEncoded
    @POST("trends/trendsLike")
    Observable<WebMsg> trendsLike(@Field("trendsId") String trendsId);
}
