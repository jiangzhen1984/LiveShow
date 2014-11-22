package com.v2tech.vo;

import java.util.Date;


public class CrowdGroup extends Group {
	
	private AuthType mAuthType;
	private int mCapcity;
	private String mAnnouncement;
	private String mBrief;
	private int mNewFileCount;
	

	public CrowdGroup(long mGId, String mName, User mOwner, Date createDate) {
		super(mGId, GroupType.CHATING, mName, mOwner, createDate);
		mAuthType = AuthType.ALLOW_ALL;
	}

	public CrowdGroup(long mGId, String mName, User mOwner) {
		super(mGId, GroupType.CHATING, mName, mOwner);
		mAuthType = AuthType.ALLOW_ALL;
	}

	

	public AuthType getAuthType() {
		return mAuthType;
	}

	public void setAuthType(AuthType authType) {
		this.mAuthType = authType;
	}

	public int getCapcity() {
		return mCapcity;
	}

	public void setCapcity(int mCapcity) {
		this.mCapcity = mCapcity;
	}

	public String getAnnouncement() {
		return mAnnouncement;
	}

	public void setAnnouncement(String mAnnouncement) {
		this.mAnnouncement = mAnnouncement;
	}

	public String getBrief() {
		return mBrief;
	}

	public void setBrief(String brief) {
		this.mBrief = brief;
	}

	public String toGroupUserListXml() {
		StringBuffer sb = new StringBuffer();
		sb.append("<userlist>");
		for (User u : this.users) {
			sb.append(" <user id=\"" + u.getmUserId() + "\" />");
		}
		sb.append("</userlist>");
		return sb.toString();
	}
	
	public int getNewFileCount() {
		return mNewFileCount;
	}
	
	public void resetNewFileCount() {
		mNewFileCount = 0;
	}
	
	public void addNewFileNum() {
		mNewFileCount ++;
	}
	
	public void addNewFileNum(int num) {
		mNewFileCount+= num;
	}

	@Override
	public String toXml() {
		StringBuffer sb = new StringBuffer();
		sb.append("<crowd id=\""+this.mGId+"\" name=\"" + this.mName + "\" authtype=\""
				+ mAuthType.ordinal() + "\" size=\"" + mCapcity
				+ "\" announcement=\""+(mAnnouncement == null? "" : mAnnouncement)+"\" summary=\""+(mBrief == null? "" : mBrief)+"\" creatoruserid=\""+(mOwnerUser == null? "" : mOwnerUser.getmUserId())+"\"/>");
		return sb.toString();
	}
	
	public enum AuthType {
		ALLOW_ALL(0),QULIFICATION(1),NEVER(2);
		
		private int type;
		private AuthType(int type){
			this.type = type;
		}
		public static AuthType fromInt(int code) {
			switch (code) {
				case 0:
					return ALLOW_ALL;
				case 1:
					return QULIFICATION;
				case 2:
					return NEVER;
			}
			return null;
		}
		
		public int intValue() {
			return type;
		}
	}
	
}
