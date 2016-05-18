package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class V2UserIdReportReqPacket extends RequestPacket {
	
	public long v2userId;

	public V2UserIdReportReqPacket() {
	}

	public V2UserIdReportReqPacket(long v2userId) {
		super();
		this.v2userId = v2userId;
	}

	
}
