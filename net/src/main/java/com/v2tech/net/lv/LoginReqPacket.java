package com.v2tech.net.lv;

import com.v2tech.net.pkt.RequestPacket;

public class LoginReqPacket extends RequestPacket {

	
	boolean as;
	
	String deviceId;
	
	String username;
	
	String pwd;
	
	String smscode;
	
	boolean usesms;

	public LoginReqPacket(boolean as, String deviceId) {
		super();
		this.as = as;
		this.deviceId = deviceId;
	}

	public LoginReqPacket(boolean as, String username, String pwd) {
		super();
		this.as = as;
		this.username = username;
		this.pwd = pwd;
	}
	
	
	public LoginReqPacket(boolean as, String username, String pwd, String sms, boolean usesms) {
		super();
		this.as = as;
		this.username = username;
		this.pwd = pwd;
		this.smscode = sms;
		this.usesms = usesms;
	}
	
	

	public boolean isAs() {
		return as;
	}

	public void setAs(boolean as) {
		this.as = as;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getSmscode() {
		return smscode;
	}

	public void setSmscode(String smscode) {
		this.smscode = smscode;
	}

	public boolean isUsesms() {
		return usesms;
	}

	public void setUsesms(boolean usesms) {
		this.usesms = usesms;
	}
	
	
	
}
