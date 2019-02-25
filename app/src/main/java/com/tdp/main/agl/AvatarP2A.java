package com.tdp.main.agl;

import android.text.TextUtils;
import android.util.Log;


import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by tujh on 2018/6/20.
 */
public class AvatarP2A implements Serializable {
    private static final long serialVersionUID = -2062781401904016738L;
    public static final String TAG = AvatarP2A.class.getSimpleName();

    public static final String FILE_NAME_CLIENT_DATA_THUMB_NAIL = "thumbNail.jpg";
    public static final String FILE_NAME_CLIENT_DATA_ORIGIN_PHOTO = "originPhoto.jpg";
    public static final String FILE_NAME_CLIENT_DATA_ZIP = "data.zip";

    public static final String FILE_NAME_CLIENT_DATA_JSON = "client_data.json";
    public static final String FILE_NAME_Q_FINAL_BUNDLE = "q_final.bundle";
    public static final String FILE_NAME_Q_HAIR_BUNDLE = "q_hair.bundle";

    private String animFile;
    private String bundleDir;
    private int originPhotoRes;
    private String originPhoto;
    private String originPhotoThumbNail;
    private String headFile;
    private String bodyFile;
    private int gender = 1;//识别性别, gender 1 is man 2 is woman
    private int hairIndex = 0;
    private String[] hairFileList = new String[1];
    private int glassesIndex = 0;
    private int clothesIndex = 1;
    private int expressionIndex = 1;

    private boolean isDelete = false;

     AvatarP2A(int clothesIndex) {
        originPhotoRes = 2131099777;
        originPhoto = null;
        originPhotoThumbNail = null;
        headFile = "head_1_male.bundle";
        bodyFile = "male_body_basic.bundle";
        hairFileList = new String[]{"male_hair_basic.bundle", "female_hair_basic.bundle"};
        hairIndex = 0;
        animFile = "dongzuo8_debug.bundle";
        this.clothesIndex=clothesIndex;
    }

     AvatarP2A(int originPhotoRes, int gender, String headFile, String[] hairFileList) {
        this.originPhotoRes = originPhotoRes;
        this.gender = gender;
        this.headFile = headFile;
        this.bodyFile = AvatarConstant.bodyBundle(gender);
        this.hairFileList = hairFileList;
        this.hairIndex = gender == 1 ? 0 : 0;
    }

     AvatarP2A(String bundleDir, int gender, String headFile, String[] hairFileList) {
        this.bundleDir = bundleDir;
        this.originPhoto = bundleDir + FILE_NAME_CLIENT_DATA_ORIGIN_PHOTO;
        this.originPhotoThumbNail = bundleDir + FILE_NAME_CLIENT_DATA_THUMB_NAIL;
        this.headFile = bundleDir + FILE_NAME_Q_FINAL_BUNDLE;

        this.headFile = headFile;
        this.bodyFile = AvatarConstant.bodyBundle(gender);
        this.gender = gender;
        this.hairFileList = hairFileList;
        this.hairIndex = gender == 1 ? 0 : 0;
    }

     AvatarP2A(String bundleDir1, int gender1,int clothesIndex) {
        gender = gender1;
        bundleDir = bundleDir1;
        originPhoto = bundleDir + FILE_NAME_CLIENT_DATA_ORIGIN_PHOTO;
        originPhotoThumbNail = bundleDir + FILE_NAME_CLIENT_DATA_THUMB_NAIL;
        headFile = bundleDir + FILE_NAME_Q_FINAL_BUNDLE;
        hairFileList = new String[]{"male_hair_basic.bundle", "female_hair_basic.bundle"};
        bodyFile = AvatarConstant.bodyBundle(gender);
        hairIndex = gender == 2 ? 1 : 0;
        animFile = "dongzuo8_debug.bundle";
        Log.e("ououou","clothesIndex"+clothesIndex);
        this.clothesIndex=clothesIndex;
    }

    public String getAnimFile() {
        return animFile;
    }

    public void setAnimFile(String animFile) {
        this.animFile = animFile;
    }

    public String getBundleDir() {
        return bundleDir;
    }

    public int getOriginPhotoRes() {
        return originPhotoRes;
    }

    public String getOriginPhoto() {
        return originPhoto;
    }

    public void setOriginPhoto(String originPhoto) {
        this.originPhoto = originPhoto;
    }

    public String getOriginPhotoThumbNail() {
        return originPhotoThumbNail;
    }

    public void setOriginPhotoThumbNail(String originPhotoThumbNail) {
        this.originPhotoThumbNail = originPhotoThumbNail;
    }

    public String getHeadFile() {
        return headFile;
    }


    public String getBodyFile() {
        return bodyFile;
    }

    public String[] getHairFileList() {
        return hairFileList;
    }

    public void setHairFileList(String[] hairFileList) {
        this.hairFileList = hairFileList;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getHairIndex() {
        return hairIndex;
    }

    public void setHairIndex(int hairIndex) {
        this.hairIndex = hairIndex;
    }

    public int getGlassesIndex() {
        return glassesIndex;
    }

    public void setGlassesIndex(int glassesIndex) {
        this.glassesIndex = glassesIndex;
    }

    public int getClothesIndex() {
        return clothesIndex;
    }

    public void setClothesIndex(int clothesIndex) {
        this.clothesIndex = clothesIndex;
    }

    public int getExpressionIndex() {
        return expressionIndex;
    }

    public void setExpressionIndex(int expressionIndex) {
        this.expressionIndex = expressionIndex;
    }

    public String getHairFile() {
        return hairFileList == null || hairFileList.length == 0 ? "" : hairFileList[hairIndex];
    }

    public String getGlassesFile() {
        return AvatarConstant.glassesBundle(gender)[glassesIndex];
    }

    public String getClothesFile() {
        return  AvatarConstant.clothesBundle(gender)[clothesIndex];
    }

    public String getExpressionFile() {
        return AvatarConstant.expressionBundle(gender)[expressionIndex];
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    @Override
    public String toString() {
        return " bundleDir " + bundleDir + "\n"
                + " originPhotoRes " + originPhotoRes + "\n"
                + " originPhoto " + originPhoto + "\n"
                + " originPhotoThumbNail " + originPhotoThumbNail + "\n"
                + " headFile " + headFile + "\n"
                + " bodyFile " + bodyFile + "\n"
                + " gender " + gender + "\n"
                + " hair " + getHairFile() + "\n"
                + " hairFileList " + Arrays.toString(hairFileList) + "\n"
                + " glassesIndex " + glassesIndex + "\n"
                + " clothes " + getClothesFile() + "\n"
                + " expressionIndex " + expressionIndex + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvatarP2A avatarP2A = (AvatarP2A) o;
        return !TextUtils.isEmpty(headFile) && headFile.equals(avatarP2A.getHeadFile());
    }


    @Override
    public AvatarP2A clone() {
        AvatarP2A avatarP2A = new AvatarP2A(this.clothesIndex);
        avatarP2A.bundleDir = this.bundleDir;
        avatarP2A.originPhotoRes = this.originPhotoRes;
        avatarP2A.originPhoto = this.originPhoto;
        avatarP2A.originPhotoThumbNail = this.originPhotoThumbNail;
        avatarP2A.headFile = this.headFile;
        avatarP2A.bodyFile = this.bodyFile;
        avatarP2A.gender = this.gender;
        avatarP2A.hairIndex = this.hairIndex;
        avatarP2A.hairFileList = Arrays.copyOf(this.hairFileList, this.hairFileList.length);
        avatarP2A.glassesIndex = this.glassesIndex;
        avatarP2A.clothesIndex = this.clothesIndex;
        avatarP2A.expressionIndex = this.expressionIndex;
        return avatarP2A;
    }
}

