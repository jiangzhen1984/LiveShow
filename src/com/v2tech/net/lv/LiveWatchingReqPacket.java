package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class LiveWatchingReqPacket extends RequestPacket {
	
	public static final int WATCHING = 1;
	public static final int CANCEL = 2;
	long uid;
	long nid;
	int type;
	
	public LiveWatchingReqPacket(long uid, long nid, int type) {
		super();
		this.uid = uid;
		this.nid = nid;
		this.type = type;
	}

	
	
	
	

	

	
	

}
