package com.sdk.core;

import android.content.Context;

import com.faceunity.constant.ColorConstant;
import com.faceunity.core.FUP2ARenderer;
import com.faceunity.core.P2AClientWrapper;

/**
 * ahtor: super_link
 * date: 2019/2/27 09:39
 * remark:
 */
public class Fup2aController {

    private static Fup2aController instance;
    private Context context;

    private Fup2aController(Context context){
        this.context = context;
        FUP2ARenderer.initFURenderer(context);
        ColorConstant.init(context);
        P2AClientWrapper.setupData(context);
    }

    public static void init(final Context context) {
        if (instance == null) instance = new Fup2aController(context);
    }
}
