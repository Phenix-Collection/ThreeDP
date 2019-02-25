package com.sdk.net;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * ahtor: super_link
 * date: 2018/9/25 17:20
 * remark:
 */
public interface UploadService {

    @POST
    Call<ResponseBody> uploadFile(@Url String url, @Body RequestBody partMap);
}
