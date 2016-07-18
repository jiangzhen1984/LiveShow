package com.v2tech.service.jni;

/**
 * Used to wrap response data from JNI when receive call from JNI
 * 
 * @author 28851274
 * 
 */
public class RequestChatServiceResponse extends JNIResponse {

	public static final int UNKNOWN = 0;
	public static final int ACCEPTED = 1;
	public static final int REJCTED = 2;
	public static final int CANCELED = 3;
	public static final int HANGUP = 4;

	int code;

	private long uid;
	private String uuid;
	private long groupId;
	private String deviceID;
	private long fromUserID;

	/**
	 * This class is wrapper that wrap response of chat service
	 * 
	 * @param result
	 *            {@link Result}
	 */
	public RequestChatServiceResponse(Result result) {
		super(result);
	}

	public RequestChatServiceResponse(int code, Result result) {
		super(result);
		this.code = code;
	}
	
	public RequestChatServiceResponse(int code, Result result , long fromUserID) {
		super(result);
		this.code = code;
		this.fromUserID = fromUserID;
	}

	public RequestChatServiceResponse(int code, long uid, long groupId,
			String deviceID, Result result) {
		super(result);
		this.uid = uid;
		this.groupId = groupId;
		this.deviceID = deviceID;
		this.code = code;
	}
	
	public long getFromUserID() {
		return fromUserID;
	}

	public void setFromUserID(long fromUserID) {
		this.fromUserID = fromUserID;
	}

	public int getCode() {
		return code;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
