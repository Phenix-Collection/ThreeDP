package com.sdk.db.entity;

/**
 * @author:zlcai
 * @new date:2017-5-22
 * @last date:2017-5-22
 * @remark:
 **/
public class NotifyMessage {
	private int PNotifyId;
	private String FNotifyTitle;
	private String FNotifyContent;
	private int FType;
	private int KReceiverId;
	private long FAddTime;
	private int FVersionCode;
	private String FExtContent;
	private int FState = 0; // 状态，处理后的状态码， （FType->0：未处理， 1：已通过，2：已拒绝）

	public int getPNotifyId() {
		return PNotifyId;
	}

	public void setPNotifyId(int pNotifyId) {
		PNotifyId = pNotifyId;
	}

	public String getFNotifyTitle() {
		return FNotifyTitle;
	}

	public void setFNotifyTitle(String fNotifyTitle) {
		FNotifyTitle = fNotifyTitle;
	}

	public String getFNotifyContent() {
		return FNotifyContent;
	}

	public void setFNotifyContent(String fNotifyContent) {
		FNotifyContent = fNotifyContent;
	}

	public int getFType() {
		return FType;
	}

	public void setFType(int fType) {
		FType = fType;
	}

	public int getKReceiverId() {
		return KReceiverId;
	}

	public void setKReceiverId(int kReceiverId) {
		KReceiverId = kReceiverId;
	}

	public long getFAddTime() {
		return FAddTime;
	}

	public void setFAddTime(long fAddTime) {
		FAddTime = fAddTime;
	}

	public int getFVersionCode() {
		return FVersionCode;
	}

	public void setFVersionCode(int fVersionCode) {
		FVersionCode = fVersionCode;
	}

	public String getFExtContent() {
		return FExtContent;
	}

	public void setFExtContent(String fExtContent) {
		FExtContent = fExtContent;
	}

	public int getFState() {
		return FState;
	}

	public void setFState(int fState) {
		FState = fState;
	}

}
