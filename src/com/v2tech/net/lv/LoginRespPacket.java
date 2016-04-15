package com.v2tech.net.lv;

import java.util.List;

import com.v2tech.net.pkt.ResponsePacket;

public class LoginRespPacket extends ResponsePacket {

	public long uid;

	public String sign;

	public String url;
	
	public List<Fans> fansList;

	public class Fans {
		public long id;
		public String phone;
		public String name;
		public String headurl;
		public String signText;
		public String type;
	}

}
