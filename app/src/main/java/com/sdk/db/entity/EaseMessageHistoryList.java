package com.sdk.db.entity;

/**
 * @author:zlcai
 * @new date:2017-4-26
 * @last date:2017-4-26
 * @remark:
 **/
public class EaseMessageHistoryList {
	private String toId; // 对象ID
	private String toName; // 聊天对象（群或人）
	private long lastTime; // 最后聊天时间
	private String toLogo; // 群或人的头像
	private String lastUserId; // 最后发言人的id
	private String lastMsg; // 最后一条消息
	private String lastName; // 最后发言人
	private int messageCount = 1; // 消息数量

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public String getToLogo() {
		return toLogo;
	}

	public void setToLogo(String toLogo) {
		this.toLogo = toLogo;
	}

	public String getLastMsg() {
		return lastMsg;
	}

	public void setLastMsg(String lastMsg) {
		this.lastMsg = lastMsg;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}

	public String getToId() {
		return toId;
	}

	public void setToId(String toId) {
		this.toId = toId;
	}

	public String getLastUserId() {
		return lastUserId;
	}

	public void setLastUserId(String lastUserId) {
		this.lastUserId = lastUserId;
	}

}
