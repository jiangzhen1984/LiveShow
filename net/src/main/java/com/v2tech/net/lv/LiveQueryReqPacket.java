package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class LiveQueryReqPacket extends RequestPacket {
	
	long uid;
	int type;
	double lat;
	double lng;
	int radius;
	public LiveQueryReqPacket(long uid, double lat, double lng, int radius) {
		super();
		this.uid = uid;
		this.lat = lat;
		this.lng = lng;
		this.radius = radius;
	}
	
	

	

	
	

}
