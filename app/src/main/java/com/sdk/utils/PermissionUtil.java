package com.sdk.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by super_link on 2017/12/2.
 */

public class PermissionUtil {

    /**
     * 返回是否同意
     * @param context
     * @return
     */
    public static boolean getHasReadExternalStorage(Activity context){
       if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
           applyReadExternalStorage(context);
           return false;
       }
       return true;
    }

    public static boolean getHasCamera(Activity context){
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            applyCamera(context);
            return false;
        }
        return true;
    }

    public static void applyCamera(Activity context){
        ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.CAMERA}, 0);
    }

    public static void applyReadExternalStorage(Activity context){
        ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
    }



}
