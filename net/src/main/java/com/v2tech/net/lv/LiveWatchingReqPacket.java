package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class LiveWatchingReqPacket extends RequestPacket {
	
	public static final int WATCHING = 1;
	public static final int CANCEL = 2;
	public static final int CLOSE = 3;
	public long lid;
	public long uid;
	public long nid;
	public int type;
	
	public LiveWatchingReqPacket(long lid, long uid, long nid, int type) {
		super();
		this.lid = lid;
		this.uid = uid;
		this.nid = nid;
		this.type = type;
	}

	
	
	
	

	

	
	

}
