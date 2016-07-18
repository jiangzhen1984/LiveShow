package com.v2tech.service.jni;


public class RequestPermissionResponse extends JNIResponse {

	
	

	/**
	 * This class is wrapper that wrap response of request open user video device
	 * @param nConfID
	 * @param nTime
	 * @param nJoinResult {@link Result}
	 */
	public RequestPermissionResponse(
			Result result) {
		super(result);
	}

}
