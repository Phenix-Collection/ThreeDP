package com.chat.easeui.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chat.easeui.EaseUI;
import com.chat.easeui.domain.EaseUser;
import com.sdk.api.entity.UserInfoEntity;
import com.sdk.core.Globals;
import com.sdk.db.CacheImageService;
import com.tdp.main.R;
import com.tdp.main.entity.FriendInfoEntity;

import cn.jpush.im.android.api.model.UserInfo;

public class EaseUserUtils {
    
    static EaseUI.EaseUserProfileProvider userProvider;
    
    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }
    
    /**
     * get EaseUser according username
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        if(userProvider != null)
            return userProvider.getUser(username);

        return null;
    }
    
    /**
     * set user avatar
     * @param
     */
    public static void setUserAvatar(Context context, FriendInfoEntity userInfo, ImageView imageView){
//    	EaseUser user = getUserInfo(username);
        if(userInfo != null && userInfo.getProfile_photo() != null){

            CacheImageService.setImageView(imageView, Globals.BASE_API + userInfo.getProfile_photo(), true);

//            try {
//                int avatarResId = Integer.parseInt(user.getAvatar());
//                Glide.with(context).load(avatarResId).into(imageView);
//            } catch (Exception e) {
//                //use default avatar
//                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.login_head).into(imageView);
//            }
        }else{
            Glide.with(context).load(R.drawable.icon_empty).into(imageView);
        }
    }
    public static void setUserAvatar(Context context, UserInfoEntity userInfo, ImageView imageView){
//    	EaseUser user = getUserInfo(username);
        if(userInfo != null && userInfo.getProfilePhoto() != null){

            CacheImageService.setImageView(imageView, Globals.BASE_API + userInfo.getProfilePhoto(), true);

//            try {
//                int avatarResId = Integer.parseInt(user.getAvatar());
//                Glide.with(context).load(avatarResId).into(imageView);
//            } catch (Exception e) {
//                //use default avatar
//                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.login_head).into(imageView);
//            }
        }else{
            Glide.with(context).load(R.drawable.icon_empty).into(imageView);
        }
    }

    
    /**
     * set user's nickname
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	EaseUser user = getUserInfo(username);
        	if(user != null && user.getNick() != null){
        		textView.setText(user.getNick());
        	}else{
        		textView.setText(username);
        	}
        }
    }
    
}
