package com.v2tech.net.lv;

import com.v2tech.net.pkt.ResponsePacket;

public class FollowRespPacket extends ResponsePacket {
	
	long uid;
	boolean add;

	public FollowRespPacket(long uid) {
		super();
		this.uid = uid;
	}

	public FollowRespPacket(long uid, boolean add) {
		super();
		this.uid = uid;
		this.add = add;
	}

	
	

}
