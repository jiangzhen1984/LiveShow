package com.v2tech.service.jni;


/**
 * Used to wrap response data from JNI when receive call from JNI
 * @author 28851274
 *
 */
public class RequestSendMessageResponse extends JNIResponse {

	
	
	
	/**
	 * This class is wrapper that wrap response of chat service
	 * @param result {@link Result}
	 */
	public RequestSendMessageResponse(
			Result result) {
		super(result);
	}
	
	

}
