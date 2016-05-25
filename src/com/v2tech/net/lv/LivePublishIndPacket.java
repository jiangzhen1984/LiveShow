package com.v2tech.net.lv;

import com.v2tech.net.pkt.IndicationPacket;


public class LivePublishIndPacket extends IndicationPacket {
	
	public long vid;
	public long lid;
	public long uid;
	public double lat;
	public double lng;
	public long v2uid;
	
	public LivePublishIndPacket() {
		super();
	}
	
	
	public LivePublishIndPacket(long vid, long uid, long lid, double lat, double lng) {
		super();
		this.vid = vid;
		this.uid = uid;
		this.lid = lid;
		this.lat = lat;
		this.lng = lng;
	}

	

	
	
	
	

	

	
	

}
