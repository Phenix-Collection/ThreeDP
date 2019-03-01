package com.tdp.main.constant;

/***
 * 创建新化身方式
 */
public enum CreateAvatarTypeEnum {

	CAMARA("拍照", 1), FILE("文件", 2);

	// 成员变量
	private String value;
	private int index;

	private CreateAvatarTypeEnum(String value, int index) {
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
