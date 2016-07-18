package com.v2tech.vo.inquiry;

import java.io.Serializable;

import com.v2tech.vo.User;

public class InquiryData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1676215996687924072L;

	
	public long id;
	
	public User sponsor;
	
	public double sourceLat;
	public double sourceLng;
	
	public User answer;
	
	public double targetLat;
	public double targetLng;
}
