package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class FollowsQueryReqPacket extends RequestPacket {

	public int start = 1;
	
	public int count;

	public FollowsQueryReqPacket() {
		super();
		count = 20;
	}

	public FollowsQueryReqPacket(int start, int count) {
		super();
		this.start = start;
		this.count = count;
	}
	
	
	
	
}
