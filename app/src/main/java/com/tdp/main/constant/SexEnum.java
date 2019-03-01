package com.tdp.main.constant;

/**
 * ahtor: super_link
 * date: 2019/2/27 13:53
 * remark: 性别
 */
public enum SexEnum {
    MALE("男", 1), FEMALE("女", 2);

    // 成员变量
    private String value;
    private int index;

    private SexEnum(String value, int index) {
        this.value = value;
        this.index = index;
    }

    /*
     * public static String getName(String name, int index) { for(CardTypeEnum c :
     * CardTypeEnum.values()) { if(c.getIndex() == index) { return c.name; } }
     * return null; }
     */

    public int getIndex() {
        return index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
