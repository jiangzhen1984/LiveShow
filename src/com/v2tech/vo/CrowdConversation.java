package com.v2tech.vo;

import com.v2tech.util.DateUtil;
import com.v2tech.vo.Group.GroupType;

public class CrowdConversation extends Conversation {
	
	private Group g;
	private User lastSendUser;
	private boolean showContact;

	public CrowdConversation(Group g) {
		if (g == null) {
			throw new NullPointerException(" group is null");
		}
		if (g.getGroupType() != GroupType.CHATING) {
			throw new IllegalArgumentException(" group type is not GroupType.CHATING");
		}
		this.g = g;
		super.mExtId = g.getmGId();
		super.mType = TYPE_GROUP;
	}

	

	@Override
	public String getName() {
		if (g != null) {
			return g.getName();
		}
		return super.getName();
	}

	@Override
	public CharSequence getMsg() {
		if(showContact)
			return msg;
		else{
			if (g != null) {
				User u = g.getOwnerUser();
				// TODO need use localization
				return u == null ? "" : "创建人:" + u.getName();
			}
			return msg;
		}
	}

	@Override
	public String getDate() {
		if(showContact){
			if(dateLong != null){
				return DateUtil.getStringDate(Long.valueOf(dateLong));
			}
			return super.getDate();
		}
		else{
			if (g != null) {
				return g.getStrCreateDate();
			}
			return super.getDate();
		}
	}
	
	@Override
	public String getDateLong() {
		if (dateLong != null) {
			return dateLong;
		}
		return super.getDateLong();
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public void setDate(String date) {
		this.date = date;
	}



	public User getLastSendUser() {
		return lastSendUser;
	}



	public void setLastSendUser(User lastSendUser) {
		this.lastSendUser = lastSendUser;
	}
	
	public Group getGroup() {
		return g;
	}
	
	public boolean isShowContact() {
		return showContact;
	}

	public void setShowContact(boolean showContact) {
		this.showContact = showContact;
	}
	
	public Group getG() {
		return g;
	}

	public void setG(Group g) {
		this.g = g;
	}
	
}
