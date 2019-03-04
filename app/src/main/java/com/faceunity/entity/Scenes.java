package com.faceunity.entity;

/**
 * Created by tujh on 2018/12/18.
 */
public class Scenes {

    public int resId;
    public BundleRes[] bundles;

    public Scenes(int resId, BundleRes[] bundles) {
        this.resId = resId;
        this.bundles = bundles;
    }
}
