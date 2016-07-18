package com.V2.jni.ind;

/**
 * Class indication JNI returned result wrapper
 * @author jiangzhen
 *
 */
public abstract class JNIObjectInd {
	
	public enum JNIIndType {
		AUDIO,CHAT,APP,CONF,FILE,IM,VIDEO,WR,GROUP,VIDEO_MIXED;
	}

	
	protected JNIIndType mType;
	
	/**
	 * <p> Type of indication.  <br>
	 * com.V2.jni.V2GlobalEnum.REQUEST_TYPE_CONF<br>
	 * com.V2.jni.V2GlobalEnum.REQUEST_TYPE_IM<br>
	 * </p>
	 * 
	 */
	protected int mRequestType;
	
	
	public JNIIndType getType() {
		return this.mType;
	}
	
	public int getRequestType() {
		return this.mRequestType;
	}
}
