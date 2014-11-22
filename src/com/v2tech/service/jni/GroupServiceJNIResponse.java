package com.v2tech.service.jni;

import com.v2tech.vo.Group;

/**
 * JNI call back data wrapper
 * 
 * @author 28851274
 * 
 */
public class GroupServiceJNIResponse extends JNIResponse {

	
	
	protected Result res;
	public Group g;
	
	public GroupServiceJNIResponse(Result res) {
		super(res);
	}

	
	public GroupServiceJNIResponse(Result res, Group g) {
		super(res);
		this.g = g;
	}
}
