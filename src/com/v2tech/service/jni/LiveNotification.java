package com.v2tech.service.jni;

import com.v2tech.vo.Live;

public class LiveNotification extends JNIIndication {
	public static final int TYPE_START = 1;
	public static final int TYPE_STOPPED = 0;
	
	public Live live;
	public int type;  //0 stop 1 start
	public LiveNotification(Result res, Live live, int type) {
		super(res);
		this.live = live;
		this.type = type;
	}
	

}
