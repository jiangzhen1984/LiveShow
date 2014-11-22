package com.v2tech.vo;

/**
 * Use to wrap camera parameters
 * 
 * @author jiangzhen
 * 
 */
public class CameraConfiguration {

	public static final int DEFAULT_CAMERA_INDEX = 1;

	public static final int DEFAULT_FRAME_RATE = 24;

	public static final int DEFAULT_BIT_RATE = 256000;

	private String mDeviceId;

	private int mCameraIndex;

	private int mFrameRate;

	private int mBitRate;

	/**
	 * Use default mCameraIndex({@value #DEFAULT_CAMERA_INDEX}) mFrameRate(
	 * {@value #DEFAULT_FRAME_RATE}) mBitRate({@value #DEFAULT_BIT_RATE}) to construct this object
	 * 
	 * @param mDeviceId
	 * @see DEFAULT_CAMERA_INDEX
	 * @see DEFAULT_FRAME_RATE
	 * @see DEFAULT_BIT_RATE
	 */
	public CameraConfiguration(String mDeviceId) {
		this(mDeviceId, DEFAULT_CAMERA_INDEX, DEFAULT_FRAME_RATE,
				DEFAULT_BIT_RATE);
	}

	public CameraConfiguration(String mDeviceId, int mCameraIndex) {
		this(mDeviceId, mCameraIndex, DEFAULT_FRAME_RATE, DEFAULT_BIT_RATE);
	}

	public CameraConfiguration(String mDeviceId, int mCameraIndex,
			int mFrameRate) {
		this(mDeviceId, mCameraIndex, DEFAULT_FRAME_RATE, mFrameRate);
	}

	public CameraConfiguration(String mDeviceId, int mCameraIndex,
			int mFrameRate, int mBitRate) {
		this.mDeviceId = mDeviceId;
		this.mCameraIndex = mCameraIndex;
		this.mFrameRate = mFrameRate;
		this.mBitRate = mBitRate;
	}

	public String getDeviceId() {
		return mDeviceId;
	}

	public void setDeviceId(String deviceId) {
		this.mDeviceId = deviceId;
	}

	public int getCameraIndex() {
		return mCameraIndex;
	}

	public void setCameraIndex(int cameraIndex) {
		this.mCameraIndex = cameraIndex;
	}

	public int getFrameRate() {
		return mFrameRate;
	}

	public void setFrameRate(int frameRate) {
		this.mFrameRate = frameRate;
	}

	public int getBitRate() {
		return mBitRate;
	}

	public void setBitRate(int bitRate) {
		this.mBitRate = bitRate;
	}

}
