package com.v2tech.vo;

public class AddFriendHistorieNode {
	public long ownerUserID;
	public long ownerAuthType; // 保存当时自已的身份验证方式, 0:允许任何人加我的好友, 1:需要身份验证, 2:禁止任何人加我为好友
	public long remoteUserID; // 对方的用户ID
	public long fromUserID; // 发起人的用户ID
	public long toUserID; // 接收人的用户ID
	public String applyReason; // 申请理由
	public String refuseReason; // 拒绝理由
	public long addState; // 添加状态: 0:等待验证, 1:已同意, 2:已拒绝
	public long saveDate; // 时间戳(秒)
	public long readState; // 是否已读: 0:未读, 1:已读
}
