package com.v2tech.vo;

import v2av.VideoPlayer;

public class UserChattingObject {

	public static final int VOICE_CALL = 0x01;
	public static final int VIDEO_CALL = 0x02;
	public static final int INCOMING_CALL = 0x10;
	public static final int OUTING_CALL = 0x00;
	public static final int SPEAKING = 0x100;
	public static final int CONNECTED = 0x200;
	private String szSessionID;
	private int flag;
	private User mUser;
	private long groupdId;
	UserDeviceConfig udc;

	public UserChattingObject(User user, int flag) {
		this(null, user, flag, "", null);
	}

	public UserChattingObject(User user, int flag, String deviceId) {
		this(null, user, flag, deviceId, null);
	}

	public UserChattingObject(long groupdId, User user, int flag,
			String deviceId, VideoPlayer vp) {
		if (user == null) {
			throw new RuntimeException(
					" UserChattingObject user can not be null");
		}
		this.groupdId = groupdId;

		this.flag = flag;
		this.flag |= SPEAKING;
		this.mUser = user;
		this.udc = new UserDeviceConfig(0 , 0 , user.getmUserId(), deviceId, vp,
				V2GlobalConstants.REQUEST_TYPE_IM,
				UserDeviceConfig.UserDeviceConfigType.EVIDEODEVTYPE_CAMERA);
	}
	
	public UserChattingObject(String szSessionID, User user, int flag,
			String deviceId, VideoPlayer vp) {
		if (user == null) {
			throw new RuntimeException(
					" UserChattingObject user can not be null");
		}
		this.szSessionID = szSessionID;
		this.flag = flag;
		this.flag |= SPEAKING;
		this.mUser = user;
		this.udc = new UserDeviceConfig(0 , 0 , user.getmUserId(), deviceId, vp,
				V2GlobalConstants.REQUEST_TYPE_IM,
				UserDeviceConfig.UserDeviceConfigType.EVIDEODEVTYPE_CAMERA);
	}

	public User getUser() {
		return this.mUser;
	}
	
	public String getSzSessionID() {
		return szSessionID;
	}

	public void setSzSessionID(String szSessionID) {
		this.szSessionID = szSessionID;
	}

	public long getGroupdId() {
		return groupdId;
	}

	public String getDeviceId() {
		return udc.getDeviceID();
	}

	public void setDeviceId(String devId) {
		this.udc.setDeviceID(devId);
	}

	public VideoPlayer getVp() {
		return udc.getVp();
	}

	public void setVp(VideoPlayer vp) {
		this.udc.setVp(vp);
	}

	public UserDeviceConfig getUdc() {
		return this.udc;
	}

	public void setMute(boolean b) {
		if (b) {
			this.flag &= (~SPEAKING);
		} else {
			this.flag |= SPEAKING;
		}
	}

	public boolean isMute() {
		return !((this.flag & SPEAKING) == SPEAKING);
	}

	public boolean isAudioType() {
		return (flag & VOICE_CALL) == VOICE_CALL;
	}

	public boolean isVideoType() {
		return (flag & VIDEO_CALL) == VIDEO_CALL;
	}

	public boolean isIncoming() {
		return (flag & INCOMING_CALL) == INCOMING_CALL;
	}

	public void updateAudioType() {
		// Clear video call flag
		flag &= ~VIDEO_CALL;
		flag |= VOICE_CALL;
	}

	public boolean isConnected() {
		return (flag & CONNECTED) == CONNECTED ? true : false;
	}

	public void setConnected(boolean cFlag) {
		if (cFlag) {
			flag |= CONNECTED;
		} else {
			flag &= (~CONNECTED);
		}
	}
}
