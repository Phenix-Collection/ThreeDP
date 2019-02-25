package com.sdk.api.entity;

public class CommentEntity {
    private String addTime;
    private String content;
    private String userName;
    private int recommmentdId;
    private int toUserId;
    private String toUserName;
    private int trendsId;
    private int userId;
    private String toUserAccount;
    private String userAccount;

    public int getTrendsId() {
        return trendsId;
    }

    public void setTrendsId(int trendsId) {
        this.trendsId = trendsId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getToUserAccount() {
        return toUserAccount;
    }

    public void setToUserAccount(String toUserAccount) {
        this.toUserAccount = toUserAccount;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRecommmentdId() {
        return recommmentdId;
    }

    public void setRecommmentdId(int recommmentdId) {
        this.recommmentdId = recommmentdId;
    }

    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

}
