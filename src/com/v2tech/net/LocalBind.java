package com.v2tech.net;

import com.v2tech.net.pkt.Packet;

class LocalBind implements Comparable<LocalBind> {

	long reqId;
	Packet req;
	Packet resp;
	boolean sync;
	boolean timeout;
	long sendtime;
	long queuetime;
	long respontime;
	boolean sendflag;
	
	public LocalBind(Packet req) {
		super();
		this.req = req;
		this.reqId = req.getId();
		queuetime = System.currentTimeMillis();
	}




	public LocalBind(Packet req, Packet resp) {
		super();
		this.req = req;
		this.resp = resp;
		queuetime = System.currentTimeMillis();
	}




	@Override
	public int compareTo(LocalBind another) {
		return this.req.compareTo(another.req);
	}
	
}
