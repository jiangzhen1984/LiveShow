package com.v2tech.net.lv;

import com.v2tech.net.pkt.IndicationPacket;

public class LiveWatchingIndPacket extends IndicationPacket {
	
	public static final int WATCHING = 1;
	public static final int CANCEL = 2;
	public static final int CLOSE = 3;
	public long uid;
	public long nid;
	public int type;
	
	public LiveWatchingIndPacket(long uid, long nid, int type) {
		super();
		this.uid = uid;
		this.nid = nid;
		this.type = type;
	}

	
	
	
	

	

	
	

}
