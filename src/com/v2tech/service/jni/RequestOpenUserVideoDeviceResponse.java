package com.v2tech.service.jni;

public class RequestOpenUserVideoDeviceResponse extends JNIResponse {

	long nTime;

	/**
	 * This class is wrapper that wrap response of request open user video
	 * device
	 * 
	 * @param nTime
	 * @param nJoinResult
	 *            {@link Result}
	 */
	public RequestOpenUserVideoDeviceResponse(long nTime,
			Result result) {
		super(result);
		this.nTime = nTime;
	}

}
