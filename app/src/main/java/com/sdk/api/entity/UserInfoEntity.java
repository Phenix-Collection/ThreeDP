package com.sdk.api.entity;

import com.sdk.core.Globals;

/**
 * ahtor: super_link
 * date: 2018/10/22 16:06
 * remark:
 */
public class UserInfoEntity {
    private int id;
    private String account; // 账号
    private String nickName; // 昵称
    private String profilePhoto; // 头像
    private String realName; // 真实姓名
    private int sex; // 性别（1：男， 2：女）
    private String birthday; // 生日
    private String address; // 地址
    private String educational; // 教育
    private String occupation; // 行业
    private String email; // 电子邮件
    private String phoneNumber; // 手机号码
    private String registerIp; // 注册IP
    private int security; // 是否保密
    private MirrorEntity mirror; // 用户模型

    public MirrorEntity getMirror() {
        return mirror == null ? new MirrorEntity() : mirror;
    }

    public void setMirror(MirrorEntity mirror) {
        this.mirror = mirror;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickName() {
        return nickName == null ? "" : nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getProfilePhoto() {
        return profilePhoto == null ? "" : profilePhoto.replace("%2F", "/");
    }

    public void setProfilePhoto(String profile_photo) {
        this.profilePhoto = profile_photo;
    }

    public String getReal_name() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getSecurity() {
        return security;
    }

    public void setSecurity(int security) {
        this.security = security;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }


}
