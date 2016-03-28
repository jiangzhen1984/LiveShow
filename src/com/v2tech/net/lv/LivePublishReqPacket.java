package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class LivePublishReqPacket extends RequestPacket {
	
	long lid;
	long uid;
	double lat;
	double lng;
	
	public LivePublishReqPacket(long uid, long lid, double lat, double lng) {
		super();
		this.uid = uid;
		this.lid = lid;
		this.lat = lat;
		this.lng = lng;
	}

	

	
	
	
	

	

	
	

}
