package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class LivePublishReqPacket extends RequestPacket {
	
	
	OptType  ot = OptType.PUBLISH;
	public long lid;
	long uid;
	long nid;
	double lat;
	double lng;
	public String pwd;
	
	public LivePublishReqPacket(long uid, long lid, double lat, double lng) {
		super();
		this.uid = uid;
		this.lid = lid;
		this.lat = lat;
		this.lng = lng;
		this.pwd = "";
	}

	
	public LivePublishReqPacket(long uid, long lid, long nid, double lat, double lng, String pwd) {
		super();
		this.uid = uid;
		this.nid = nid;
		this.lid = lid;
		this.lat = lat;
		this.lng = lng;
		this.pwd = pwd;
		this.ot = OptType.UPDATE_PWD;
	}
	
	
	public enum OptType{
		PUBLISH,UPDATE_PWD
	}
	
	
	

	

	
	

}
