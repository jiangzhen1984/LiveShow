package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class V2AccountReprotReqPacket extends RequestPacket {

	public String account;
	
	public String password;

	public V2AccountReprotReqPacket(String account, String password) {
		super();
		this.account = account;
		this.password = password;
	}

	public V2AccountReprotReqPacket() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
