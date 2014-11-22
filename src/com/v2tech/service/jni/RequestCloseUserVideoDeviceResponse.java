package com.v2tech.service.jni;

/**
 * Used to wrap response data from JNI when receive call from JNI
 * @author 28851274
 *
 */
public class RequestCloseUserVideoDeviceResponse extends JNIResponse {

	
	
	
	
	
	long nTime;
	Result er;

	/**
	 * This class is wrapper that wrap response of request to close user video device
	 * @param nTime
	 * @param result {@link Result}
	 */
	public RequestCloseUserVideoDeviceResponse(long nTime,
			Result result) {
		super(result);
		this.nTime = nTime;
		er = result;
	}

}
