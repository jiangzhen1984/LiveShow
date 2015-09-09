package com.v2tech.service.jni;

public class RequestPublishResponse extends JNIResponse {
	
	public String url;
	public long uid;

	public RequestPublishResponse(Result res) {
		super(res);
	}

	public RequestPublishResponse(Result res, String url, long uid) {
		super(res);
		this.url = url;
		this.uid = uid;
	}
	
	

}
