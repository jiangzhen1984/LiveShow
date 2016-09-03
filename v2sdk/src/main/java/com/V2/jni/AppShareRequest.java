package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.V2.jni.callback.AppShareRequestCallBack;

import v2av.VideoPlayer;

public class AppShareRequest {
	private static AppShareRequest mAppShareRequest;

	private List<WeakReference<AppShareRequestCallBack>> mCallbacks;

	private AppShareRequest() {
		mCallbacks = new ArrayList<WeakReference<AppShareRequestCallBack>>();
	}

	public static synchronized AppShareRequest getInstance() {
		if (mAppShareRequest == null) {
			synchronized (AppShareRequest.class) {
				if (mAppShareRequest == null) {
					mAppShareRequest = new AppShareRequest();
				}
			}
		}
		return mAppShareRequest;
	}

	/**
	 * 添加自定义的回调，监听接收到的服务信令
	 * 
	 * @param callback
	 */
	public void addCallbacks(AppShareRequestCallBack callback) {
		mCallbacks.add(new WeakReference<AppShareRequestCallBack>(callback));
	}

	/**
	 * 移除自定义添加的回调
	 * 
	 * @param callback
	 */
	public void removeCallback(AppShareRequestCallBack callback) {
		for (int i = 0; i < mCallbacks.size(); i++) {
			if (mCallbacks.get(i).get() == callback) {
				mCallbacks.remove(i);
				break;
			}
		}
	}

	public native boolean initialize(AppShareRequest requestObj);

	public native void unInitialize();

	/**
	 * @brief 打开桌面共享的View端
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nHostUserID
	 *            Host端的用户ID
	 * @param szVideoDeviceID
	 *            远程桌面的视频设备ID
	 * @param nCustomData
	 *            程序自定义数据
	 *
	 * @return None
	 */
	public native void AppShareStartView(int nGroupType, long nGroupID, long nHostUserID, String szVideoDeviceID,
			VideoPlayer nCustomData);

	private void OnAppShareCreated(int nGroupType, long nGroupID, long nHostUserID, String szVideoDeviceID) {
		for (WeakReference<AppShareRequestCallBack> wr : mCallbacks) {
			if (wr != null && wr.get() != null) {
				wr.get().OnAppShareCreated(nGroupType, nGroupID, nHostUserID, szVideoDeviceID);
			}
		}
	}

	private void OnAppShareDestroyed(String szVideoDeviceID) {
		for (WeakReference<AppShareRequestCallBack> wr : mCallbacks) {
			if (wr != null && wr.get() != null) {
				wr.get().OnAppShareDestroyed(szVideoDeviceID);
			}
		}
	}
}
