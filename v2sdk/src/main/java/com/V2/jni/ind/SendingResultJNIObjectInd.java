package com.V2.jni.ind;

public class SendingResultJNIObjectInd extends JNIObjectInd {

	public enum Result {
		SUCCESS(0), FAILED(1);
		private int code;

		private Result(int code) {
			this.code = code;
		}

		public int value() {
			return this.code;
		}

		public static Result fromInt(int code) {
			switch (code) {
			case 0:
				return SUCCESS;
			case 1:
				return FAILED;
			default:
				return FAILED;
			}
		}
	}

	private String uuid;
	
	private Result mRet;

	private int mErrorCode;

	public SendingResultJNIObjectInd(String uuid, Result ret, int errorCode) {
		super();
		this.mType = JNIObjectInd.JNIIndType.CHAT;
		this.uuid = uuid;
		this.mRet = ret;
		this.mErrorCode = errorCode;
	}

	
	
	public String getUuid() {
		return uuid;
	}


	public void setUuid(String uuid) {
		this.uuid = uuid;
	}



	public Result getRet() {
		return mRet;
	}

	public void setRet(Result ret) {
		this.mRet = ret;
	}

	public int getErrorCode() {
		return mErrorCode;
	}

	public void setmErrorCode(int errorCode) {
		this.mErrorCode = errorCode;
	}

}
