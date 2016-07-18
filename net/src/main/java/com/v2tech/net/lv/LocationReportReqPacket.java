package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class LocationReportReqPacket extends RequestPacket {
	
	double lat;
	double lng;
	long uid;
	
	public LocationReportReqPacket(long uid, double lat, double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.uid = uid;
	}

	
	
	
	
	
	
	

	

	
	

}
