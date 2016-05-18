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

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	
	
}
