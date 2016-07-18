package com.v2tech.vo;

import java.util.List;

import com.v2tech.service.jni.JNIIndication;

public class AttendDeviceIndication extends JNIIndication {
	
	public long uid;
	
	public List<UserDeviceConfig> ll;
	
	public AttendDeviceIndication(Result res) {
		super(res);
	}

}
