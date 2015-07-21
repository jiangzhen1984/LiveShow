package com.v2tech.vo;

import java.io.Serializable;
import java.util.Set;

public class Live implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5215649680130458839L;

	
	private User publisher;
	
	private String url;
	
	private Set<User> followers;
	
	private double lat;
	
	private double lng;
	
	private boolean canRemove;
	
	
	
	
	
	public Live(User publisher, String url, double lat, double lng) {
		super();
		this.publisher = publisher;
		this.url = url;
		this.lat = lat;
		this.lng = lng;
		this.canRemove = false;
	}

	
	public Live(User publisher, String url, double lat, double lng, boolean canRemove) {
		super();
		this.publisher = publisher;
		this.url = url;
		this.lat = lat;
		this.lng = lng;
		this.canRemove = canRemove;
	}


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



	public double getLng() {
		return lng;
	}



	public void setLng(double lng) {
		this.lng = lng;
	}
	
	



	public boolean isCanRemove() {
		return canRemove;
	}



	public void setCanRemove(boolean canRemove) {
		this.canRemove = canRemove;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Live other = (Live) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "Live [publisher=" + publisher + ", url=" + url + ", lat=" + lat
				+ ", lng=" + lng + "]";
	}
	
	
	
}

