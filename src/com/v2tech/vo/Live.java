package com.v2tech.vo;

import java.util.Set;

public class Live {

	
	private User publisher;
	
	private String url;
	
	private Set<User> followers;
	
	
	
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
	
	
	
}

