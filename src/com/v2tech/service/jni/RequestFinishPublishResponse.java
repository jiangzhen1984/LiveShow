package com.v2tech.service.jni;

public class RequestFinishPublishResponse extends JNIResponse {
	
	public long uid;

	public RequestFinishPublishResponse(Result res) {
		super(res);
	}

	public RequestFinishPublishResponse(Result res,  long uid) {
		super(res);
		this.uid = uid;
	}
	
	

}
