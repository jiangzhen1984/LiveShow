package com.v2tech.service;

import android.os.Message;

import com.V2.jni.VideoRequest;
import com.V2.jni.util.V2Log;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.service.jni.RequestCloseUserVideoDeviceResponse;
import com.v2tech.service.jni.RequestConfCreateResponse;
import com.v2tech.service.jni.RequestOpenUserVideoDeviceResponse;
import com.v2tech.service.jni.RequestUpdateCameraParametersResponse;
import com.v2tech.vo.CameraConfiguration;
import com.v2tech.vo.Group;
import com.v2tech.vo.UserDeviceConfig;

/**
 * Device function class.
 * <ul>
 * </ul>
 * 
 * @author 28851274
 * 
 */
public class DeviceService extends AbstractHandler {

	private static final int JNI_REQUEST_OPEN_VIDEO = 0x00100001;
	private static final int JNI_REQUEST_CLOSE_VIDEO = 0x00100002;
	private static final int JNI_UPDATE_CAMERA_PAR = 0x00100003;

	/**
	 * User request to open video device.
	 * 
	 * @param userDevice
	 *            {@link UserDeviceConfig} if want to open local video,
	 *            {@link UserDeviceConfig#getVp()} should be null and
	 *            {@link UserDeviceConfig#getDeviceID()} should be ""
	 * @param caller
	 *            if input is null, ignore response Message.object is
	 *            {@link com.v2tech.service.jni.RequestOpenUserVideoDeviceResponse}
	 * 
	 * @see UserDeviceConfig
	 */
	public void requestOpenVideoDevice(UserDeviceConfig userDevice,
			MessageListener caller) {
		if (userDevice == null) {
			JNIResponse jniRes = new RequestOpenUserVideoDeviceResponse(0,
					RequestConfCreateResponse.Result.INCORRECT_PAR);
			sendResult(caller, jniRes);
			return;
		}
		initTimeoutMessage(JNI_REQUEST_OPEN_VIDEO, DEFAULT_TIME_OUT_SECS,
				caller);
		V2Log.i(" request open video   UID:" + userDevice.getUserID()
				+ " deviceid:" + userDevice.getDeviceID() + "   videoplayer:"
				+ userDevice.getVp());
		VideoRequest.getInstance().openVideoDevice(0, 0,
				userDevice.getType().ordinal(), userDevice.getUserID(),
				userDevice.getDeviceID(), userDevice.getVp());
		JNIResponse jniRes = new RequestOpenUserVideoDeviceResponse(
				System.currentTimeMillis() / 1000,
				RequestOpenUserVideoDeviceResponse.Result.SUCCESS);

		// send delayed message for that make sure send response after JNI
		Message res = Message.obtain(this, JNI_REQUEST_OPEN_VIDEO, jniRes);
		this.sendMessageDelayed(res, 300);

	}

	/**
	 * User request to close video device.
	 * 
	 * @param userDevice
	 *            {@link UserDeviceConfig} if want to open local video,
	 *            {@link UserDeviceConfig#getVp()} should be null and
	 *            {@link UserDeviceConfig#getDeviceID()} should be ""
	 * @param caller
	 *            if input is null, ignore response Message.object is
	 *            {@link com.v2tech.service.jni.RequestCloseUserVideoDeviceResponse}
	 * 
	 * @see UserDeviceConfig
	 */
	public void requestCloseVideoDevice(UserDeviceConfig userDevice,
			MessageListener caller) {
		if (userDevice == null) {
			JNIResponse jniRes = new RequestCloseUserVideoDeviceResponse(
					System.currentTimeMillis() / 1000,
					RequestCloseUserVideoDeviceResponse.Result.INCORRECT_PAR);
			sendResult(caller, jniRes);
			return;
		}
		initTimeoutMessage(JNI_REQUEST_CLOSE_VIDEO, DEFAULT_TIME_OUT_SECS,
				caller);

		VideoRequest.getInstance().closeVideoDevice(0, 0, 
				userDevice.getType().ordinal(), userDevice.getUserID(),
				userDevice.getDeviceID(), userDevice.getVp());
		JNIResponse jniRes = new RequestCloseUserVideoDeviceResponse(
				System.currentTimeMillis() / 1000,
				RequestCloseUserVideoDeviceResponse.Result.SUCCESS);

		// send delayed message for that make sure send response after JNI
		Message res = Message.obtain(this, JNI_REQUEST_CLOSE_VIDEO, jniRes);
		this.sendMessageDelayed(res, 300);
	}
	
	
	
	/**
	 * User request to open video device.
	 * 
	 * @param group If user not IM, use this
	 * @param userDevice
	 *            {@link UserDeviceConfig} if want to open local video,
	 *            {@link UserDeviceConfig#getVp()} should be null and
	 *            {@link UserDeviceConfig#getDeviceID()} should be ""
	 * @param caller
	 *            if input is null, ignore response Message.object is
	 *            {@link com.v2tech.service.jni.RequestOpenUserVideoDeviceResponse}
	 * 
	 * @see UserDeviceConfig
	 */
	public void requestOpenVideoDevice(Group group, UserDeviceConfig userDevice,
			MessageListener caller) {
		if (userDevice == null || group == null) {
			JNIResponse jniRes = new RequestOpenUserVideoDeviceResponse(0,
					RequestConfCreateResponse.Result.INCORRECT_PAR);
			sendResult(caller, jniRes);
			return;
		}
		initTimeoutMessage(JNI_REQUEST_OPEN_VIDEO, DEFAULT_TIME_OUT_SECS,
				caller);
		V2Log.i(" request open video   UID:" + userDevice.getUserID()
				+ " deviceid:" + userDevice.getDeviceID() + "   videoplayer:"
				+ userDevice.getVp());
		VideoRequest.getInstance().openVideoDevice(group.getGroupType().intValue(), group.getmGId(), 
				userDevice.getType().ordinal(), userDevice.getUserID(),
				userDevice.getDeviceID(), userDevice.getVp());
		JNIResponse jniRes = new RequestOpenUserVideoDeviceResponse(
				System.currentTimeMillis() / 1000,
				RequestOpenUserVideoDeviceResponse.Result.SUCCESS);

		// send delayed message for that make sure send response after JNI
		Message res = Message.obtain(this, JNI_REQUEST_OPEN_VIDEO, jniRes);
		this.sendMessageDelayed(res, 300);

	}

	/**
	 * User request to close video device.
	 * 
	 * @param group if user not for IM, use this API
	 * 
	 * @param userDevice
	 *            {@link UserDeviceConfig} if want to open local video,
	 *            {@link UserDeviceConfig#getVp()} should be null and
	 *            {@link UserDeviceConfig#getDeviceID()} should be ""
	 * @param caller
	 *            if input is null, ignore response Message.object is
	 *            {@link com.v2tech.service.jni.RequestCloseUserVideoDeviceResponse}
	 * 
	 * @see UserDeviceConfig
	 */
	public void requestCloseVideoDevice(Group group, UserDeviceConfig userDevice,
			MessageListener caller) {
		if (userDevice == null || group == null) {
			JNIResponse jniRes = new RequestCloseUserVideoDeviceResponse(
					System.currentTimeMillis() / 1000,
					RequestCloseUserVideoDeviceResponse.Result.INCORRECT_PAR);
			sendResult(caller, jniRes);
			return;
		}
		initTimeoutMessage(JNI_REQUEST_CLOSE_VIDEO, DEFAULT_TIME_OUT_SECS,
				caller);

		VideoRequest.getInstance().closeVideoDevice(group.getGroupType().intValue(), group.getmGId(), 
				userDevice.getType().ordinal(), userDevice.getUserID(),
				userDevice.getDeviceID(), userDevice.getVp());
		JNIResponse jniRes = new RequestCloseUserVideoDeviceResponse(
				System.currentTimeMillis() / 1000,
				RequestCloseUserVideoDeviceResponse.Result.SUCCESS);

		// send delayed message for that make sure send response after JNI
		Message res = Message.obtain(this, JNI_REQUEST_CLOSE_VIDEO, jniRes);
		this.sendMessageDelayed(res, 300);
	}
	
	

	/**
	 * Update current user's camera. Including front-side or back-side camera
	 * switch.
	 * 
	 * @param cc
	 *            {@link CameraConfiguration}
	 * @param caller
	 *            if input is null, ignore response Message.object is
	 *            {@link com.v2tech.service.jni.RequestUpdateCameraParametersResponse}
	 */
	public void updateCameraParameters(CameraConfiguration cc, MessageListener caller) {
		if (cc == null) {
			JNIResponse jniRes = new RequestUpdateCameraParametersResponse(
					null,
					RequestCloseUserVideoDeviceResponse.Result.INCORRECT_PAR);
			sendResult(caller, jniRes);
			return;
		}
		initTimeoutMessage(JNI_UPDATE_CAMERA_PAR, DEFAULT_TIME_OUT_SECS, caller);
		VideoRequest.getInstance().setCapParam(cc.getDeviceId(),
				cc.getCameraIndex(), cc.getFrameRate(), cc.getBitRate());
	}

	@Override
	public void clearCalledBack() {

	}

}
