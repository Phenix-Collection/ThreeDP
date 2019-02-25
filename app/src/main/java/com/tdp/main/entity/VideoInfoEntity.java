package com.tdp.main.entity;

import java.io.Serializable;

public class VideoInfoEntity implements Serializable {
    private String attachment;
    private int countCommend;
    private int countEye;
    private int countLike;
    private String createTime;
    private int likeStatus;
    private String nickName;
    private int status;
    private String title;
    private int trendsId;
    private String updateTime;
    private String profilePhoto;
    private String userAccount;
    private int progress = 0; // 当前播放的节点，单位（秒）

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }

    public String getAttachment() {
        return attachment;

    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public int getCountCommend() {
        return countCommend;
    }

    public void setCountCommend(int countCommend) {
        this.countCommend = countCommend;
    }

    public int getCountEye() {
        return countEye;
    }

    public void setCountEye(int countEye) {
        this.countEye = countEye;
    }

    public int getCountLike() {
        return countLike;
    }

    public void setCountLike(int countLike) {
        this.countLike = countLike;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTrendsId() {
        return trendsId;
    }

    public void setTrendsId(int trendsid) {
        this.trendsId = trendsid;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
