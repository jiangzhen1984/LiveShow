package com.v2tech.vo;

public class Watcher extends User {
	
	
	public double lat;
	
	public double lng;

	public Watcher(long mUserId) {
		super(mUserId);
	}

	public Watcher(long mUserId, String name) {
		super(mUserId, name);
	}



	public Watcher(long mUserId, String name, String email, String signature) {
		super(mUserId, name, email, signature);
	}

}
