package com.V2.jni.ind;

import java.io.Serializable;

import com.v2tech.service.jni.JNIIndication;

public class MessageInd extends JNIIndication implements Serializable {
	
	public long uid;
	
	public long lid;
	
	public String content;

	public MessageInd(Result res) {
		super(res);
	}
	
	

}
