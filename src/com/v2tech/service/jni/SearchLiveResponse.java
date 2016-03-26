package com.v2tech.service.jni;

import com.v2tech.net.lv.LiveQueryRespPacket;

public class SearchLiveResponse extends JNIResponse {

	
	LiveQueryRespPacket packet;

	public SearchLiveResponse(Result res, LiveQueryRespPacket packet) {
		super(res);
		this.packet = packet;
	}

	public LiveQueryRespPacket getPacket() {
		return packet;
	}
	
	
}
