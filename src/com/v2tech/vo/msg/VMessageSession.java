package com.v2tech.vo.msg;

import java.util.Date;

public class VMessageSession {

	public long id;
	
	public int read;
	
	public int unreadCount;
	
	public int type;
	
	public long fromUid;
	
	public String fromName;
	
	public CharSequence content;
	
	public Date timestamp;
}
