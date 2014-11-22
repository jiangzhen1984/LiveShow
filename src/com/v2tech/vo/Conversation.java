package com.v2tech.vo;

import android.text.TextUtils;

import com.V2.jni.V2GlobalEnum;
import com.v2tech.util.DateUtil;

public class Conversation implements Comparable<Conversation>{

	public static final int TYPE_CONFERNECE = V2GlobalEnum.GROUP_TYPE_CONFERENCE;

	public static final int TYPE_CONTACT = V2GlobalEnum.GROUP_TYPE_USER;

	public static final int TYPE_GROUP = V2GlobalEnum.GROUP_TYPE_CROWD;
	
	public static final int TYPE_DEPARTMENT = V2GlobalEnum.GROUP_TYPE_DEPARTMENT;

	public static final int TYPE_VOICE_MESSAGE = 7;

	public static final int TYPE_VERIFICATION_MESSAGE = 8;
	
	public static final int TYPE_CROWD_VERIFICATION_MESSAGE = 9;

	public static final int READ_FLAG_READ = 1;
	public static final int READ_FLAG_UNREAD = 0;

	private int mId;

	protected int mType;

	protected long mExtId;

	protected int readFlag;

	protected String date;

	protected String dateLong;
	
	protected String name;

	protected CharSequence msg;
	
	protected boolean isFirst;  //this field created for specific item voiceItem or verificationItem
	
	protected boolean isAddedItem; //this field created for specific item voiceItem or verificationItem

	public Conversation() {
	}

	public Conversation(int mId, int mType, long mExtId, int readFlag) {
		super();
		this.mId = mId;
		this.mType = mType;
		this.mExtId = mExtId;
		this.readFlag = readFlag;
	}

	public Conversation(int mId, int mType, long mExtId) {
		this(0, mType, mExtId, READ_FLAG_UNREAD);
	}

	public Conversation(int mType, long mExtId) {
		this(0, mType, mExtId);
	}

	public String getDateLong() {
		return dateLong;
	}

	public void setDateLong(String dateLong) {
		this.dateLong = dateLong;
	}

	public String getName() {
		return name;
	}

	public CharSequence getMsg() {
		return msg;
	}

	public String getDate() {
		if(dateLong != null)
			return DateUtil.getStringDate(Long.valueOf(dateLong));
		else
			return date;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setMsg(CharSequence msg) {
		this.msg = msg;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(int readFlag) {
		this.readFlag = readFlag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (mExtId ^ (mExtId >>> 32));
		// result = prime * result + ((mType == null) ? 0 : mType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Conversation other = (Conversation) obj;
		if (mExtId != other.mExtId)
			return false;
		// if (mType == null) {
		// if (other.mType != null)
		// return false;
		// }
		// else if (!mType.equals(other.mType))
		// return false;
		return true;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		this.mType = type;
	}

	public long getExtId() {
		return mExtId;
	}

	public void setExtId(long extId) {
		this.mExtId = extId;
	}
	
	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}
	
	public boolean isAddedItem() {
		return isAddedItem;
	}

	public void setAddedItem(boolean isAddedItem) {
		this.isAddedItem = isAddedItem;
	}

	@Override
	public int compareTo(Conversation another) {
		boolean localDate = TextUtils.isEmpty(dateLong);
		boolean remoteDate = TextUtils.isEmpty(another.getDateLong());
		if(localDate && remoteDate)
			return 0;
		else if(!localDate && remoteDate)
			return -1;
		else if(localDate && !remoteDate)
			return 1;
		
		if(Long.valueOf(dateLong) < Long.valueOf(another.getDateLong()))
			return 1;
		else
			return -1;
	}

}
