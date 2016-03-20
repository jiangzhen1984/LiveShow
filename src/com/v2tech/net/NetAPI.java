package com.v2tech.net;

public interface NetAPI {

	
	public boolean connect(String host, int port);
	
	public ResponsePacket request(RequestPacket packet);
	
	public void requestAsync(RequestPacket packet);
	
	public void disconnect();
}
