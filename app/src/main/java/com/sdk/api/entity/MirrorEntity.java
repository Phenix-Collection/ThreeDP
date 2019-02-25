package com.sdk.api.entity;

/**
 * ahtor: super_link
 * date: 2018/10/23 12:00
 * remark: 用户模型
 */
public class MirrorEntity {
    private int id;
    private int userId;
    private String url;
    private String name;
    private int sex;
    private int skinColor;
    private String cloth = "0";
    private String glass;
    private String hats;
    private String cosplay;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(int skinColor) {
        this.skinColor = skinColor;
    }

    public String getCloth() {
        return cloth;
    }

    public void setCloth(String cloth) {
        this.cloth = cloth;
    }

    public String getGlass() {
        return glass;
    }

    public void setGlass(String glass) {
        this.glass = glass;
    }

    public String getHats() {
        return hats;
    }

    public void setHats(String hats) {
        this.hats = hats;
    }

    public String getCosplay() {
        return cosplay;
    }

    public void setCosplay(String cosplay) {
        this.cosplay = cosplay;
    }

    @Override
    public String toString() {
        return "MirrorEntity{" +
                "id=" + getId() +
                ", userId=" + getUserId() +
                ", url='" + getUrl() + '\'' +
                ", name='" + getName() + '\'' +
                ", sex=" + getSex() +
                ", skinColor=" + getSkinColor() +
                ", cloth='" + getCloth() + '\'' +
                ", glass='" + getGlass() + '\'' +
                ", hats='" + getHats() + '\'' +
                ", cosplay='" + getCosplay() + '\'' +
                '}';
    }
}

