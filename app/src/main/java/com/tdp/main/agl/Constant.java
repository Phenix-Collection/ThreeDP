package com.tdp.main.agl;

import android.os.Environment;

import java.io.File;

public class Constant {
    public static final String APP_NAME = "FUP2ABasicDemo";
    public static final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "LifeStory" + File.separator+ "bundle" + File.separator;
    public static final String TestFilePath = filePath + "BundleTest" + File.separator;
}
