package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class LiveWatchingReqPacket extends RequestPacket {
	
	public static final int WATCHING = 1;
	public static final int CANCEL = 2;
	long lid;
	int type;
	
	public LiveWatchingReqPacket(long lid, int type) {
		super();
		this.lid = lid;
		this.type = type;
	}

	
	
	
	

	

	
	

}
