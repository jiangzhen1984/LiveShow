package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class GetCodeReqPacket extends RequestPacket {
	
	String phone;

	public GetCodeReqPacket(String phone) {
		super();
		this.phone = phone;
	}
	
	

}
