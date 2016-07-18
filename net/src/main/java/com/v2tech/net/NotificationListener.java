package com.v2tech.net;

import com.v2tech.net.pkt.IndicationPacket;
import com.v2tech.net.pkt.ResponsePacket;

public interface NotificationListener {
	
	public void onNodification(IndicationPacket ip);
	
	public void onResponse(ResponsePacket rp);

	public void onStateChanged();
	
	public void onTimeout(ResponsePacket rp);
}
