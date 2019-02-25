package com.sdk.net;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.api.entity.UserInfoEntity;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.sdk.net.bean.ProgressModel;
import com.sdk.net.bean.ProgressRequestBody;
import com.sdk.net.listener.OnProgressListener;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author:zlcai
 * @createrDate:2017/7/27 14:57
 * @lastTime:2017/7/27 14:57
 * @detail: 该接口用于规定居于android，并遵守webmsg实体对象协议的接口说明。
 *
 **/

public class HttpRequest {

	private static HttpRequest httpRequest;
	private static OkHttpClient httpClient;
	private static Retrofit retrofit;

	public HttpRequest(){
	    if(retrofit == null){
			httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
				@Override
				public okhttp3.Response intercept(Chain chain) throws IOException {
					Request original = chain.request();
//					original.body()

					LoginInfoEntity data = CacheDataService.getLoginInfo();
					Request request = null;
					if(data != null){
						request = original.newBuilder()
								.addHeader("ACCOUNT-ID", data.getUserInfo().getAccount())
								.addHeader("SECURITY-CODE", data.getSecurityCode())
								.addHeader("LOGIN-TIME", data.getLoginTime())
								.method(original.method(), original.body())
								.build();
					} else {
						request = original.newBuilder()
								.method(original.method(), original.body())
								.build();
					}


//					Log.v("Request", "url:"+ request.url());
//					Log.v("Request", "method:"+ request.method());
//					try{
//						Log.v("Request", "body:"+ new Gson().toJson(request.body()));
//					}catch (Exception e){}


					return chain.proceed(request);
				}
			} ).build();

			retrofit = new Retrofit.Builder()
				.baseUrl(Globals.BASE_API)
				//增加返回值为String的支持
				.addConverterFactory(ScalarsConverterFactory.create())
				//增加返回值为Gson的支持(以实体类返回)
				.addConverterFactory(GsonConverterFactory.create())
				//增加返回值为Oservable<T>的支持
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.client(httpClient)
				.build();
        }
	}

	public static HttpRequest instance(){
		if(httpRequest == null){
			httpRequest = new HttpRequest();
		}
		return httpRequest;
	}


	public static <T> T create(Class<T> cls){
		return HttpRequest.retrofit.create(cls);
	}


	/***
	 * 下载文件
	 * @param url
	 * @param context
	 * @param onProgressListener
	 */
	public void download(String url, Context context, String path, OnProgressListener onProgressListener){
		new Excute().download(onProgressListener, url, path, context);
	}

	/***
	 * 上传文件
	 * @param listener
	 * @param files
	 */
	public void upload(String url, final OnProgressListener listener, Map<String,String> params, Map<String, File> files){
		new Excute().upload(url, listener, params == null ? new HashMap<String, String>() : params, files);
	}

	public void upload(String url, final OnProgressListener listener, Map<String,String> params, File file){
		Map<String,File> files = new HashMap<>();
		if(params.get("file") != null){
			files.put(params.get("file"), file);
		} else {
			files.put(file.getName(), file);
		}
		new Excute().upload(url, listener, params == null ? new HashMap<String, String>() : params, files);
	}


//	public void upload(final OnProgressListener listener, Map<String,String> params, File... files){
//		new Excute().upload(UploadService.class, listener, params == null ? new HashMap<String, String>() : params, files);
//	}

	/***
	 * post 请求
	 * @param observable
	 * @param listener
	 */
	public void doPost(Observable<WebMsg> observable, final OnResultListener listener) {
		new Excute().excute(observable, listener);
	}


	private class Excute {

		public Excute(){ }

		public void upload(String url, final OnProgressListener listener, Map<String,String> params,Map<String, File> files){
			Log.v("---","Excute->upload");
			MultipartBody.Builder muBuilder = new MultipartBody.Builder();
			for(String key: files.keySet()){
				muBuilder.addFormDataPart(key, files.get(key).getName(), RequestBody.create(MediaType.parse("multipart/form-data"),files.get(key) ));
			}
			for(String key : params.keySet()){
				muBuilder.addFormDataPart(key, params.get(key));
			}
			RequestBody originalRequestBody = muBuilder.build();
//			ProgressRequestBody progressRequestBody= new ProgressRequestBody(originalRequestBody,listener);
//			UploadService webUploadApi = retrofit.create(UploadService.class);
			final UploadService webUploadApi = retrofit.create(UploadService.class);
			webUploadApi.uploadFile(url, originalRequestBody).enqueue(new Callback<ResponseBody>() {
				@Override
				public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
					WebMsg webMsg = null;
					try{
						//Log.v("====-", new String(response.body().bytes(), "utf-8"));
						//WebMsg msg = WebMsg.getSuccessed();
						webMsg = new Gson().fromJson(new String(response.body().bytes(), "utf-8"), WebMsg.class);
						if(webMsg == null){
                            webMsg = WebMsg.getFailed(new IOException());
                        }
                        if(!webMsg.isSuccess()){
                            Globals.onWebExceptionListener.onServiceError(webMsg);
                        }
					}catch(IOException e){
						e.printStackTrace();
						//Log.e("Request",e.toString());
						webMsg = WebMsg.getFailed(e);
					} catch (Exception e){
						webMsg = WebMsg.getFailed(e);
					}

					listener.onFinished(webMsg);
				}

				@Override
				public void onFailure(Call<ResponseBody> call, Throwable t) {
					t.printStackTrace();
					WebMsg msg = WebMsg.getFailed(t);
					listener.onFinished(msg);
                    //listener.onProgress(-1,-1);
				}
			});
		}

		/***
		 * 下載
		 * @param url
		 * @param context
		 */
		public void download(final OnProgressListener listener, String url, final String path, final Context context){
			DownloadService downloadService = retrofit.create(DownloadService.class);
			Call<ResponseBody> responseBodyCall = downloadService.downloadFile(url);
			responseBodyCall.enqueue(new Callback<ResponseBody>() {
				@Override
				public void onResponse(Call<ResponseBody> carll, final Response<ResponseBody> response) {
//					Log.d("vivi",response.message()+"  length  "+response.body().contentLength()+"  type "+response.body().contentType());
					//建立一个文件
					final File file = new File(path);
					//下载文件放在子线程
					Observable.create(new ObservableOnSubscribe<ProgressModel>() {
                        @Override
                        public void subscribe(ObservableEmitter<ProgressModel> emitter) throws Exception {
							long currentLength = 0;
							OutputStream os =null;
							InputStream is = response.body().byteStream();
							long totalLength =response.body().contentLength();
							try {
								if(!file.isFile()) {
									file.createNewFile();
								}

								os = new FileOutputStream(file);
								int len ;
								byte [] buff = new byte[1024];
								while((len=is.read(buff))!=-1){
									os.write(buff,0,len);
									currentLength+=len;
									Log.d("Request","当前进度:"+currentLength);
									emitter.onNext(new ProgressModel(currentLength, totalLength, currentLength >= totalLength));

								}
								emitter.onComplete();
							} catch(FileNotFoundException e) {
								emitter.onError(e);
							} catch(IOException e) {
								emitter.onError(e);
							} finally {
								if(os!=null){
									try {
										os.close();
									} catch(IOException e) {
										emitter.onError(e);
									}
								}
								if(is!=null){
									try {
										is.close();
									} catch(IOException e) {
										emitter.onError(e);
									}
								}
							}
                        }
                    }).subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Observer<ProgressModel>() {
                        @Override
                        public void onSubscribe(Disposable d) { }
                        @Override
                        public void onNext(ProgressModel progress) {
							listener.onProgress(progress.getCurrentBytes(),progress.getContentLength());
                        }
                        @Override
                        public void onError(Throwable e) {
                        	WebMsg msg = WebMsg.getFailed(e);
                        	listener.onFinished(msg);
						}
                        @Override
                        public void onComplete() {
							WebMsg msg = WebMsg.getSuccessed();
							msg.setErrorCode(WebMsg.STATUS_OK);
							listener.onFinished(msg);
						}
                    });
				}

				@Override
				public void onFailure(Call<ResponseBody> call, Throwable t) {
					t.printStackTrace();
					WebMsg msg = WebMsg.getFailed(t);
					listener.onFinished(msg);
                    //listener.onProgress(-1, -1,false);
				}
			});
		}

		/***
		 * 执行网络访问
		 * @param observable
		 */
		public void excute(Observable<WebMsg> observable, final OnResultListener listener){
			observable.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<WebMsg>() {
				   @Override
				   public void onSubscribe(Disposable d) {

				   }

				   @Override
				   public void onNext(WebMsg webMsg) {
						Log.v("Request", "response:"+new Gson().toJson(webMsg));
				   		if(!webMsg.isSuccess()){
				   			Globals.onWebExceptionListener.onServiceError(webMsg);
						}

					   listener.onWebUiResult(webMsg);
				   }

				   @Override
				   public void onError(Throwable e) {

					   Log.v("Request:onError", e.toString());
					   e.printStackTrace();


					   WebMsg webMsg = WebMsg.getFailed(e);
					   listener.onWebUiResult(webMsg);
					   Globals.onWebExceptionListener.onNetError(webMsg);
				   }

				   @Override
				   public void onComplete() {

				   }
			   }
			);
		}
	}
}
