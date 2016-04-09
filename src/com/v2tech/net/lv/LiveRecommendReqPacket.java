package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class LiveRecommendReqPacket extends RequestPacket {
	

	public long nvid;
	public boolean isrecd;
	public LiveRecommendReqPacket(long nvid, boolean isrecd) {
		super();
		this.nvid = nvid;
		this.isrecd = isrecd;
	}

	

}
