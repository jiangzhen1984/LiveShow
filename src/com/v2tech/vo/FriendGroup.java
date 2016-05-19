package com.v2tech.vo;

import com.v2tech.vo.group.Group;


public class FriendGroup extends Group {
	public FriendGroup(long mGId, String mName) {
		super(mGId, Group.GroupType.CONTACT, mName, null, null);
	}


	@Override
	public String toXml() {
		return null;
	}
	
	
	
	

}
