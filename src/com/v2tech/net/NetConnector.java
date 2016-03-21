package com.v2tech.net;

import com.v2tech.net.pkt.Header;
import com.v2tech.net.pkt.Packet;
import com.v2tech.net.pkt.ResponsePacket;
import com.v2tech.net.pkt.Transformer;

public interface NetConnector {

	
	public boolean connect(String host, int port);
	
	public ResponsePacket request(Packet packet);
	
	public void requestAsync(Packet packet);
	
	public void disconnect();
	
	public boolean isConnected();
	
	
	public void setTransformer(Transformer<Header, String> transformer);
}
