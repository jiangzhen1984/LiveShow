package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class WatcherListQueryReqPacket extends RequestPacket {
	
	public long lid;

	public int start = 1;
	
	public int count;

	public WatcherListQueryReqPacket() {
		super();
		count = 20;
	}

	public WatcherListQueryReqPacket(long lid, int start, int count) {
		super();
		this.lid = lid;
		this.start = start;
		this.count = count;
	}
	
	
	
	
}
