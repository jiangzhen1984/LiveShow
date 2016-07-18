package com.v2tech.vo.msg;

import java.util.Date;

import org.json.JSONObject;

public class VMessageSession {

	public long id;
	
	public int read;
	
	public int unreadCount;
	
	public int type;
	
	public long fromUid;
	
	public String fromName;
	
	public CharSequence content;
	
	public Date timestamp;
	
	public boolean isSystem;
	
	public int systemType;
	
	
	public JSONObject contentJson;
}
