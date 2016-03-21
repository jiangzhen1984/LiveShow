package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class FollowReqPacket extends RequestPacket {
	
	long uid;
	boolean add;

	public FollowReqPacket(long uid) {
		super();
		this.uid = uid;
	}

	
	

}
