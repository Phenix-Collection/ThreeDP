package com.tdp.main.entity;

/***
 * 好友信息实体
 */
public class FriendInfoEntity {

    private String birthday; //":"十月 19, 2018",
    private String address; //":"中华广场",
    private String educational; //":"博士前",
    private String occupation; //":"软件行业",
    private String role; //":1,
    private String profile_photo; //":"upload/测试.jpg",
    private String sex; //":2,
    private String remark; //":"老板",
    private String real_name; //":"︿(￣︶￣)︿",
    private String friend_group; //":"1",
    private String security; //":1,
    private String nick_name; //":"死肥宅的朋友",
    private String phone_number; //":"83838383",
    private String id; //":2,
    private String register_ip; //":"158",
    private String account; //":"13826070778",
    private String email; //":"278344333@qq.com"


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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRemark() {
        return remark == null ? "" : remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getFriend_group() {
        return friend_group;
    }

    public void setFriend_group(String friend_group) {
        this.friend_group = friend_group;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
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

    public String getRegister_ip() {
        return register_ip;
    }

    public void setRegister_ip(String register_ip) {
        this.register_ip = register_ip;
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
}
