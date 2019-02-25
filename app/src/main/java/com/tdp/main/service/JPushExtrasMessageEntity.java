package com.tdp.main.service;

/**
 * ahtor: super_link
 * date: 2018/11/7 09:29
 * remark: 推送的附加消息
 */
public class JPushExtrasMessageEntity {
    private int type;
    private String account;
    private String remark;
    private Object data;
    private long dateTime;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}
