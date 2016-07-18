package com.V2.jni.ind;


/**
 * This class wrapped audio JNI request returned result
 * @see com.V2.jni.AudioRequest
 * @author jiangzhen
 *
 */
public class AudioJNIObjectInd extends JNIObjectInd {

	public AudioJNIObjectInd() {
		this(null, 0, -1);
	}

	private String szSessionID;
	private long mGroupId;
	private long mFromUserId;
	
	public AudioJNIObjectInd(String szSessionID, long fromUserId, int requestType) {
		this.szSessionID = szSessionID;
		this.mFromUserId = fromUserId;
		this.mRequestType = requestType;
		this.mType = JNIIndType.AUDIO;
	}
	
	public AudioJNIObjectInd(String szSessionID, long fromUserId) {
		this.szSessionID = szSessionID;
		this.mFromUserId = fromUserId;
		this.mType = JNIIndType.AUDIO;
	}

//	public long getGroupId() {
//		return mGroupId;
//	}
//
//	public void setGroupId(long groupId) {
//		this.mGroupId = groupId;
//	}
	
	public String getSzSessionID() {
		return szSessionID;
	}

	public void setSzSessionID(String szSessionID) {
		this.szSessionID = szSessionID;
	}

	public long getFromUserId() {
		return mFromUserId;
	}

	public void setFromUserId(long fromUserId) {
		this.mFromUserId = fromUserId;
	}
	
	
}
