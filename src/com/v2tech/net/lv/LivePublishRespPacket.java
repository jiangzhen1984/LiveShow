package com.v2tech.net.lv;


public class LivePublishRespPacket extends GetCodeRespPacket {
	
	long lid;
	long uid;
	double lat;
	double lng;
	
	public LivePublishRespPacket(long uid, long lid, double lat, double lng) {
		super();
		this.uid = uid;
		this.lid = lid;
		this.lat = lat;
		this.lng = lng;
	}

	

	
	
	
	

	

	
	

}
