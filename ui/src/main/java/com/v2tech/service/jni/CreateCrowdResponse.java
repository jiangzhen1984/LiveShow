package com.v2tech.service.jni;

/**
 * Used to wrap response data from JNI when receive call from JNI
 * @author 28851274
 *
 */
public class CreateCrowdResponse extends JNIResponse {

	
	
	long nGroupId;

	/**
	 * This class is wrapper that wrap response of create crowd
	 * @param nGroupId returned crowd id
	 * @param result {@link Result}
	 */
	public CreateCrowdResponse(long nGroupId,
			Result result) {
		super(result);
		this.nGroupId = nGroupId;
	}
	
	
	public long getGroupId() {
		return nGroupId;
	}

}
