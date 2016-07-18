package com.V2.jni.callback;

public interface VideoRequestCallback {

	/**
	 * 收到远程用户上报视频设备列表的回调
	 * 
	 * @param uid
	 * @param szXmlData
	 */
	public void OnRemoteUserVideoDevice(long uid, String szXmlData);

	/**
	 * 收到他人邀请我开始视频会话的邀请
	 * 
	 * @param szSessionID
	 * @param nFromUserID
	 * @param szDeviceID
	 */
	public void OnVideoChatInviteCallback(String szSessionID, long nFromUserID, String szDeviceID);

	public void OnSetCapParamDone(String szDevID, int nSizeIndex, int nFrameRate, int nBitRate);

	/**
	 * 收到我的视频会话邀请被对方接受的回调
	 * 
	 * @param szSessionID
	 * @param nFromUserID
	 * @param szDeviceID
	 */
	public void OnVideoChatAccepted(String szSessionID, long nFromUserID, String szDeviceID);

	/**
	 * 收到我的视频会话邀请被对方拒绝的回调
	 * 
	 * @param szSessionID
	 * @param nFromUserID
	 * @param szDeviceID
	 */
	public void OnVideoChatRefused(String szSessionID, long nFromUserID, String szDeviceID);

	/**
	 * 收到视频会话被关闭的回调
	 * 
	 * @param szSessionID
	 * @param nFromUserID
	 * @param szDeviceID
	 */
	public void OnVideoChatClosed(String szSessionID, long nFromUserID, String szDeviceID);

	/**
	 * 收到视频会话已经建立的回调
	 * 
	 * @param szSessionID
	 * @param nFromUserID
	 * @param szDeviceID
	 */
	public void OnVideoChating(String szSessionID, long nFromUserID, String szDeviceID);

	/**
	 * 通知窗口视频比特率，单位Kbps
	 * 
	 * @param hwnd
	 * @param bps
	 */
	public void OnVideoBitRate(Object hwnd, int bps);

	/**
	 * 收到自已的视频设备列表的回调
	 * 
	 * @param xml
	 * @param l
	 */
	public void OnGetVideoDevice(String xml, long l);

	public void OnVideoCaptureError(String szDevID, int nErr);

}
