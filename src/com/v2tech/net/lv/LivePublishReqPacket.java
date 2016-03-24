package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class LivePublishReqPacket extends RequestPacket {
	
	long lid;

	double lat;
	double lng;
	
	public LivePublishReqPacket(long lid, double lat, double lng) {
		super();
		this.lid = lid;
		this.lat = lat;
		this.lng = lng;
	}

	

	
	
	
	

	

	
	

}
