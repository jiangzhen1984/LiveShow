package com.v2tech.vo;

import com.V2.jni.util.V2Log;
import com.v2tech.vo.Group.GroupType;


public class ConferenceConversation extends Conversation {

	private static final String TAG = "ConferenceConversation";
	private Group g;

	public ConferenceConversation(Group g) {
		super();
		if (g == null) {
			throw new NullPointerException(" group is null");
		}
		this.g = g;
		super.mExtId = g.getmGId();
		super.mType = TYPE_CONFERNECE;
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
		if (g != null) {
			User u = g.getOwnerUser();
			V2Log.e(TAG, "group name : " + g.getName());
			// TODO need use localization
			return u == null ? "" : "创建人:" + u.getName();
		}
		return super.getMsg();
	}

	@Override
	public String getDate() {
		if (g != null) {
			return g.getStrCreateDate();
		}
		return super.getDate();
	}

	public Group getGroup() {
		return g;
	}

}
