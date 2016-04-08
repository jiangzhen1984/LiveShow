package com.V2.jni.ind;

import com.v2tech.service.jni.JNIIndication;

public class MessageInd extends JNIIndication {
	
	public long uid;
	
	public String content;

	public MessageInd(Result res) {
		super(res);
	}
	
	

}
