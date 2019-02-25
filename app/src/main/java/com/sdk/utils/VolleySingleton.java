package com.sdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Administrator on 2015/2/2.
 */
public class VolleySingleton {
	private final LruCache<String,Bitmap> cache = new LruCache<String ,Bitmap>(60);
    private static VolleySingleton volleySingleton;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Context mContext;
    public VolleySingleton(Context context) {
        this.mContext = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue,
        		
        		
        new ImageLoader.ImageCache(){
            @Override
            public Bitmap getBitmap(String url){
                return cache.get(url);
            }
            @Override
            public void putBitmap(String url,Bitmap bitmap){
                cache.put(url,bitmap);
            }
        });
        
    }
    
    /***
     * 获取缓存
     * @param url
     * @return
     */
    public Bitmap getChacheBitmap(String url){
    	return cache.get(url);
    }
    
    public static synchronized VolleySingleton getVolleySingleton(Context context){
        if(volleySingleton == null){
            volleySingleton = new VolleySingleton(context);
        }
        return volleySingleton;
    }
    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
