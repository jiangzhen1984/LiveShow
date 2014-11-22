package com.v2tech.vo;

import com.v2tech.util.DateUtil;

import java.util.Date;

import com.V2.jni.V2GlobalEnum;
import com.v2tech.vo.Group.GroupType;

public class DepartmentConversation extends Conversation {

	private Group departmentGroup;
	private User lastSendUser;
	private boolean showContact;

	public DepartmentConversation(Group departmentGroup) {
		if (departmentGroup == null)
			throw new NullPointerException(" get department group is null");

		if (departmentGroup.getGroupType() != GroupType.ORG) {
			throw new IllegalArgumentException(
					" group type is not GroupType.ORG");
		}
		this.departmentGroup = departmentGroup;
		super.mExtId = departmentGroup.getmGId();
		super.mType = V2GlobalEnum.GROUP_TYPE_DEPARTMENT;
	}

	@Override
	public String getName() {
		if (departmentGroup != null) {
			return departmentGroup.getName();
		}
		return super.getName();
	}

	@Override
	public CharSequence getMsg() {
		if(showContact)
			return msg;
		else{
			if (departmentGroup != null) {
				User u = departmentGroup.getOwnerUser();
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
			if (departmentGroup != null && departmentGroup.getCreateDate() != null) {
				return departmentGroup.getStrCreateDate();
			}
			return super.getDate();
		}
	}

	public User getLastSendUser() {
		return lastSendUser;
	}

	public void setLastSendUser(User lastSendUser) {
		this.lastSendUser = lastSendUser;
	}

	public Group getDepartmentGroup() {
		return departmentGroup;
	}

	public void setDepartmentGroup(Group departmentGroup) {
		this.departmentGroup = departmentGroup;
	}
	
	public boolean isShowContact() {
		return showContact;
	}

	public void setShowContact(boolean showContact) {
		this.showContact = showContact;
	}
}
