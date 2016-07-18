package com.v2tech.service.jni;

import com.v2tech.vo.CameraConfiguration;


public class RequestUpdateCameraParametersResponse extends JNIResponse {

	CameraConfiguration config;

	/**
	 * This class is wrapper that wrap response of request to update camera
	 * @param config
	 * @param result {@link Result}
	 */
	public RequestUpdateCameraParametersResponse(CameraConfiguration config,
			Result result) {
		super(result);
		this.config = config;
	}

}
