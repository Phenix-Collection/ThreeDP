package com.sdk.api.entity;

/**
 * ahtor: super_link
 * date: 2018/10/31 19:32
 * remark: 用户详情信息
 */
public class UserDetailInfo {
    private String birthday; //":"十月 19, 2018",
    private String address; //":"中华广场",
    private String educational; //":"博士前",
    private String occupation; //":"软件行业",
    private String profile_photo; //":"upload/测试.jpg",
    private int isfriend; //":"1",
    private int sex; //":2,
    private String real_name; //":"︿(￣︶￣)︿",
    private int security; //":1,
    private String nick_name; //":"死肥宅的朋友",
    private String phone_number; //":"83838383",
    private String id; //":2,
    private String account; //":"13826070778",
    private String email; //":"278344333@qq.com"
    private String remark; // 备注
    private int isStatus; // 状态（0：陌生人， 1：对方正在请求我为好友， 2：互为好友）

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address == null ? "" : address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEducational() {
        return educational;
    }

    public void setEducational(String educational) {
        this.educational = educational;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public int getIsfriend() {
        return isfriend;
    }

    public void setIsfriend(int isfriend) {
        this.isfriend = isfriend;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public int getSecurity() {
        return security;
    }

    public void setSecurity(int security) {
        this.security = security;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getIsStatus() {
        return isStatus;
    }

    public void setIsStatus(int isStatus) {
        this.isStatus = isStatus;
    }
}
