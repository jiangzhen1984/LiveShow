package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class LogoutReqPacket extends RequestPacket {

	// set true if from anonymous 
	public boolean isTrans;

	public LogoutReqPacket(boolean isTrans) {
		super();
		this.isTrans = isTrans;
	}
	
	
		
}
