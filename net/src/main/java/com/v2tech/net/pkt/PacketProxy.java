package com.v2tech.net.pkt;

import com.v2tech.net.NotificationListener;

public class PacketProxy extends Packet {

	
	private Packet packet;
	private NotificationListener listener;
	
	
	public PacketProxy(Packet packet, NotificationListener listener) {
		super();
		this.packet = packet;
		this.listener = listener;
	}


	public Packet getPacket() {
		return packet;
	}


	public void setPacket(Packet packet) {
		this.packet = packet;
	}


	public NotificationListener getListener() {
		return listener;
	}


	public void setListener(NotificationListener listener) {
		this.listener = listener;
	}


	@Override
	public long getId() {
		return packet.getId();
	}
	
	
	
	
}
