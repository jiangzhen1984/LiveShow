package com.V2.jni.callback;

public interface AppShareRequestCallBack {

	/**
	 * @brief 收到创建了桌面共享的回调函数
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nHostUserID
	 *            Host端的用户ID
	 * @param szVideoDeviceID
	 *            远程桌面的视频设备ID
	 *
	 * @return None
	 */
	public void OnAppShareCreated(int nGroupType, long nGroupID, long nHostUserID, String szVideoDeviceID);

	/**
	 * @brief 收到桌面共享被销毁的回调函数
	 *
	 * @param szVideoDeviceID
	 *            远程桌面的视频设备ID
	 *
	 * @return None
	 */
	public void OnAppShareDestroyed(String szVideoDeviceID);
}
