package com.v2tech.service.jni;


/**
 * Used to wrap response data from JNI when receive call from JNI
 * @author 28851274
 *
 */
public class RequestConfCreateResponse extends JNIResponse {

	
	
	
	long nConfID;
	long nTime;
	String data;

	/**
	 * This class is wrapper that wrap response of create conference
	 * @param nConfID
	 * @param nTime
	 * @param result {@link Result}
	 */
	public RequestConfCreateResponse(long nConfID, long nTime, String data,
			Result result) {
		super(result);
		this.nConfID = nConfID;
		this.nTime = nTime;
		this.data = data;
	}
	
	
	public long getConfId() {
		return this.nConfID;
	}

	public String getData() {
		return data;
	}

}
