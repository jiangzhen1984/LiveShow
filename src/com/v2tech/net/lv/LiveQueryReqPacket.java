package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class LiveQueryReqPacket extends RequestPacket {
	

	int type;
	double lat;
	double lng;
	int radius;
	public LiveQueryReqPacket(double lat, double lng, int radius) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.radius = radius;
	}
	
	

	

	
	

}
