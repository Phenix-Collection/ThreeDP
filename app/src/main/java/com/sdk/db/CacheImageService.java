package com.sdk.db;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.sdk.core.Globals;
import com.sdk.utils.VolleySingleton;
import com.tdp.main.R;
import java.net.URLEncoder;

/**
 * @author:zlcai
 * @new date:2016-12-7
 * @last date:2016-12-7
 * @remark: 图片缓存工具
 **/

public class CacheImageService {
	
//	private static LinkedHashMap<String, Bitmap> cacheBitmap1 = new LinkedHashMap<String, Bitmap>();
	
	/***
	 * 返回一个图片对象
	 * @param url 图片路径
	 * @param isCache 是否需要缓存
	 * @return
	 */
	protected static Bitmap getImage(Context context, String url, boolean isCache){
		
		Log.v("", ""+ Globals.DIR_CACHE);
		return null;
	}
	
	/***
	 * 设置图片到ImageView对象中
	 * @param iv ImageView对象
	 * @param url 图片地址
	 * @param isCache 是否需要缓存
	 */
	public static void setImageView(final ImageView iv, final String url, final boolean isCache){
		Context context = Globals.getContext();
		setImageView(iv,url,context.getResources().getDrawable(R.drawable.icon_empty), context.getResources().getDrawable(R.drawable.icon_error),isCache, true, false);
	}
	
	public static void setImageView(final ImageView iv, final String url, int iconDefault, final boolean isCache){
		Context context = Globals.getContext();
		setImageView(iv,url,context.getResources().getDrawable(iconDefault),context.getResources().getDrawable(iconDefault),isCache, true, false);
	}
	
	public static void setImageView(final ImageView iv, final String url, int iconLoading, int iconDefault, final boolean isCache){
		Context context = Globals.getContext();
		setImageView(iv,url,context.getResources().getDrawable(iconLoading),context.getResources().getDrawable(iconDefault),isCache, true, false);
	}
	
	public static void setImageView(final ImageView iv, final String url, Drawable iconDefault, final boolean isCache){
		setImageView(iv,url,iconDefault,iconDefault,isCache, true, false);
	}
	
	public static void setImageView(final ImageView iv, final String url, Drawable iconLoading,Drawable iconDefault, final boolean isCache){
		setImageView(iv,url,iconLoading, iconDefault,isCache, true, false);
	}
	
	public static void setImageViewB(final ImageView iv, final String url, final boolean isCache){
		Context context = Globals.getContext();
		setImageView(iv,url,context.getResources().getDrawable(R.drawable.icon_empty), context.getResources().getDrawable(R.drawable.icon_error),isCache, true, true);
	}
	
	public static void setImageViewB(final ImageView iv, final String url, int iconDefault, final boolean isCache){
		Context context = Globals.getContext();
		setImageView(iv,url,context.getResources().getDrawable(iconDefault),context.getResources().getDrawable(iconDefault),isCache, true, true);
	}
	
	public static void setImageViewB(final ImageView iv, final String url,int iconLoading, int iconDefault, final boolean isCache){
		Context context = Globals.getContext();
		setImageView(iv,url,context.getResources().getDrawable(iconLoading),context.getResources().getDrawable(iconDefault),isCache, true, true);
	}
	
	/***
	 * 
	 * @param iv 图片控件
	 * @param url 图片地址
	 * @param iconLoading 默认图片
	 * @param iconDefault 加载出错图片
	 */
	public static void setImageViewB(final ImageView iv, final String url, int iconLoading, int iconDefault){
		Context context = Globals.getContext();
		setImageView(iv,url,context.getResources().getDrawable(iconLoading),context.getResources().getDrawable(iconDefault),false, true, true);
	}
	
	public static void setImageViewB(final ImageView iv, final String url, Drawable iconDefault, final boolean isCache){
		setImageView(iv,url,iconDefault,iconDefault,isCache, true, true);
	}
	
	
	/***
	 * 设置图片到ImageView对象中
	 * @param iv ImageView对象
	 * @param url 图片地址
	 * @param isCache 是否需要缓存
	 */
	private static void setImageView(final ImageView iv, String url, final Drawable iconLoading, final Drawable iconDefault, final boolean isCache, final boolean isFirst, boolean isBig){
//		final String name = url;
//		if(url == null)return;
		
		if(url == null || url.length() < 6 || url.indexOf("http://") < 0){
			if(iv != null){
				iv.setImageDrawable(iconDefault);
			}
			return;
		}
		
		if(iv == null) return;
		
		url = url.trim().replace(" ","%20");
		
		if(isBig && url.indexOf(".") > 0 && url.indexOf("_b") < 0){
			url = url.replace(url.substring(url.lastIndexOf(".")), "_b" + url.substring(url.lastIndexOf(".")));
		}
		
		final String tempUrl = url;
		
		Log.v("", "....." + url);
		
//		if(isCache){ // 如果有缓存到本地，则在本地中
		Bitmap img = VolleySingleton.getVolleySingleton(Globals.getContext()).getChacheBitmap(url);
		if(img != null){
			iv.setImageBitmap(img);
			return;
		}
//		}
		
		iv.setImageDrawable(iconLoading); // 设置加载图
		
		VolleySingleton.getVolleySingleton(Globals.getContext()).getImageLoader().get(url==null?"":url, new ImageListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				if(!isFirst){
					iv.setImageDrawable(iconDefault);
					return; 
				}
				
				if(tempUrl.lastIndexOf("/") > 0){ // 如果按照原始的地址情况都出错，尝试将url转义重试一次
					String convert = tempUrl.substring(tempUrl.lastIndexOf("/"));
					try {
						String url = tempUrl.replace(convert, URLEncoder.encode(convert, "utf-8"));
						setImageView(iv, url, iconDefault, iconLoading, isCache, false, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onResponse(final ImageContainer response, boolean isImmediate) {
				if (response.getBitmap() != null) {
					iv.setImageBitmap(response.getBitmap());
                } 
//				else if (R.drawable.icon_error != 0) {
//                	iv.setImageDrawable(iconDefault);
//                }
			}
		});
//		} else {
//			iv.setImageBitmap(ImageUtils.readBitMap(filePath));
//		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
