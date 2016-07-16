package com.v2tech.vo.live;

import com.v2tech.vo.User;
import com.v2tech.vo.UserDeviceConfig;

public class LiveConnectionUser {

	public User user;
	public UserDeviceConfig udc;
	public int index;
	public boolean showing;
	public Type type = Type.VIDEO;
	
	public enum Type {
		AUDIO, VIDEO;
	}
}
