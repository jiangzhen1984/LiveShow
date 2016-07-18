package com.V2.jni.ind;


public class ConferenceJNIObjecctInd extends JNIObjectInd {
	
	
	private long mConfId;

	public ConferenceJNIObjecctInd(long mConfId) {
		super();
		this.mConfId = mConfId;
		this.mType = JNIIndType.CONF;
	}
	
	

}
