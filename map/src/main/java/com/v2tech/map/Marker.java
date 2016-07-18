package com.v2tech.map;

import java.io.Serializable;

public class Marker {

	protected double lat;

	protected double lng;

	protected int resId;
	
	protected Serializable ntObj;
	
	public Marker() {
	}


	public Marker(Serializable ntObj, double lat, double lng) {
		super();
		this.ntObj = ntObj;
		this.lat = lat;
		this.lng = lng;
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public Serializable getNtObj() {
		return ntObj;
	}

	public void setNtObj(Serializable ntObj) {
		this.ntObj = ntObj;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}
