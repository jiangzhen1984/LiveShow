package com.v2tech.service.jni;

import com.v2tech.vo.User;

public class RequestUserUpdateResponse extends JNIResponse {

	User u;

	public RequestUserUpdateResponse(User u, Result res) {
		super(res);
		this.u = u;
		this.res = res;
	}

	public RequestUserUpdateResponse(User u, Result res, Object originObject) {
		super(res);
		this.u = u;
		this.res = res;
		this.callerObject = originObject;
	}

	public User getUser() {
		return u;
	}

}
