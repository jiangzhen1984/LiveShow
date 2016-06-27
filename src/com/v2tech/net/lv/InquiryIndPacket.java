package com.v2tech.net.lv;

import com.v2tech.net.pkt.IndicationPacket;

public class InquiryIndPacket extends IndicationPacket {

	public static final int TYPE_NEW = 1;
	public static final int TYPE_UPDATE_AWARD = 2;
	public static final int TYPE_CANCEL = 3;
	public static final int TYPE_ACCEPT = 4;
	
	
	public long inquireId;
	public int type;
	public float award;
	public long inquiryUserId;
	public double lat;
	public double lng;
	public String desc;
	
	
}
