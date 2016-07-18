package com.v2tech.service.jni;

import com.v2tech.vo.UserDeviceConfig;
import com.v2tech.vo.conference.Conference;

public class OpenVideoRequest extends JNIRequest {
	
	Conference conf;
	UserDeviceConfig userDevice;

	public OpenVideoRequest(Conference conf, UserDeviceConfig userDevice) {
		super();
		this.conf = conf;
		this.userDevice = userDevice;
	}

}
