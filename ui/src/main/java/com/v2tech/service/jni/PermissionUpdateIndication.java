package com.v2tech.service.jni;

public class PermissionUpdateIndication extends JNIIndication {

	long uid;
	int type;
	int state;
	
	public PermissionUpdateIndication(long uid, int type, int state) {
		super(Result.SUCCESS);
		this.uid = uid;
		this.type = type;
		this.state = state;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	
	
}
