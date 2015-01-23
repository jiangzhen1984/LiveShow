package com.v2tech.vo;

import java.util.Set;

public class Live {

	
	private User publisher;
	
	private String url;
	
	private Set<User> followers;
	
	private double lat;
	
	private double lan;
	
	
	
	public Live(User pu, String url) {
		this.publisher = pu;
		this.url = url;
	}



	public User getPublisher() {
		return publisher;
	}



	public void setPublisher(User publisher) {
		this.publisher = publisher;
	}



	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}



	public double getLat() {
		return lat;
	}



	public void setLat(double lat) {
		this.lat = lat;
	}



	public double getLan() {
		return lan;
	}



	public void setLan(double lan) {
		this.lan = lan;
	}
	
	
	
}

