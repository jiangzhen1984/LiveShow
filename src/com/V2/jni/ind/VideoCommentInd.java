package com.V2.jni.ind;

public class VideoCommentInd extends JNIObjectInd {

	public long userId;
	public String msg;

	public VideoCommentInd(long userId, String msg) {
		super();
		this.userId = userId;
		this.msg = msg;
	}
	
	
}
