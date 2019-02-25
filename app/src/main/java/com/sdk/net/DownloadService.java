package com.sdk.net;

import retrofit2.Call;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/***
 * 上传下载服务
 */
public interface DownloadService {

    @POST("updatepackage")
    Call<ResponseBody> uploadFile(@Body RequestBody partMap);

    @Streaming //大文件时要加不然会OOM
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
