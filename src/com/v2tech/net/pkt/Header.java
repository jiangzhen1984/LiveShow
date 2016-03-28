package com.v2tech.net.pkt;

import java.util.concurrent.atomic.AtomicLong;

public final class Header {
	
	private static final int ERROR_FLAG = 1;
	
	public static final int ERROR_FORMATION = 1;
	
	private static AtomicLong atmoic = new AtomicLong();

	public long id;
	
	public int flag;

	public int error;
	
	
	public Header() {
		super();
		id = atmoic.getAndIncrement();
	}
	
	
	public void setError(boolean b) {
		if (b) {
			flag |= ERROR_FLAG;
		} else {
			flag &= (~ERROR_FLAG);
		}
	}
	
	public boolean isError() {
		return (flag & ERROR_FLAG) == ERROR_FLAG;
	}
	
	
	public void setErrorCode(int err) {
		setError(true);
		error |= err;
	}
	
}
