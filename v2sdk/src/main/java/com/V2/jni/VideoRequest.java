package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import v2av.VideoPlayer;

import com.V2.jni.callback.VideoRequestCallback;
import com.V2.jni.util.V2Log;

public class VideoRequest {
	private static VideoRequest mVideoRequest;
	private List<WeakReference<VideoRequestCallback>> mCallBacks;

	private VideoRequest() {
		mCallBacks = new ArrayList<WeakReference<VideoRequestCallback>>();
	};

	public static synchronized VideoRequest getInstance() {
		if (mVideoRequest == null) {
			synchronized (VideoRequest.class) {
				if (mVideoRequest == null) {
					mVideoRequest = new VideoRequest();
					if (!mVideoRequest.initialize(mVideoRequest)) {
						throw new RuntimeException("can't initilaize VideoRequest");
					}
				}
			}
		}
		return mVideoRequest;
	}

	/**
	 * 添加自定义的回调，监听接收到的服务信令
	 * 
	 * @param callback
	 */
	public void addCallback(VideoRequestCallback callback) {
		this.mCallBacks.add(new WeakReference<VideoRequestCallback>(callback));
	}

	/**
	 * 移除自定义添加的回调
	 * 
	 * @param callback
	 */
	public void removeCallback(VideoRequestCallback callback) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				if (wf.get() == callback) {
					mCallBacks.remove(wf);
					return;
				}
			}
		}
	}

	public native boolean initialize(VideoRequest request);

	public native void unInitialize();

	/**
	 * @brief 枚举本地视频设备列表
	 *
	 * @param nCustomData
	 *            设备数量
	 *
	 * @return
	 */
	// public native void VideoEnumDevices(int nCustomData);

	/**
	 * @brief 邀请用户视频聊天
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            被邀请用户ID
	 * @param szDeviceID
	 *            nUserID的视频设备ID
	 *
	 * @return None
	 */
	public native void VideoInviteChat(String szSessionID, long nUserID, String szDeviceID);

	/**
	 * @brief 接受用户视频聊天邀请
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            邀请用户ID
	 * @param szDeviceID
	 *            nUserID的视频设备ID
	 *
	 * @return None
	 */
	public native void VideoAcceptChat(String szSessionID, long nUserID, String szDeviceID);

	/**
	 * @brief 拒绝用户视频聊天邀请
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            邀请用户ID
	 * @param szDeviceID
	 *            nUserID的视频设备ID
	 *
	 * @return None
	 */
	public native void VideoRefuseChat(String szSessionID, long nUserID, String szDeviceID);

	/**
	 * @brief 取消尚未被对方接受的视频聊天
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            目标用户ID
	 * @param szDeviceID
	 *            nUserID的视频设备ID
	 *
	 * @return None
	 */
	public native void VideoCancelChat(String szSessionID, long nUserID, String szDeviceID);

	/**
	 * @brief 关闭进行中的视频聊天
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            目标用户ID
	 * @param szDeviceID
	 *            nUserID的视频设备ID
	 *
	 * @return None
	 */
	public native void VideoCloseChat(String szSessionID, long nToUserID, String szDeviceID);

	/**
	 * @brief 打开视频设备
	 *
	 * @param nGroupType
	 *            组类型, 0为点对点, 4为会议
	 * @param nGroupID
	 *            组ID
	 * @param nDeviceType
	 *            视频设备类型
	 * @param nUserID
	 *            目标用户ID
	 * @param szDeviceID
	 *            nUserID的视频设备ID
	 * @param VideoPlayer
	 *            需要构建一个VideoPlayer对象
	 *
	 * @return None
	 */
	public native void VideoOpenDevice(int nGroupType, long nGroupID, int nDeviceType, long nUserID, String szDeviceID,
			VideoPlayer vp);

	/**
	 * @brief 关闭视频设备
	 *
	 * @param nGroupType
	 *            组类型, 0为点对点, 4为会议
	 * @param nGroupID
	 *            组ID
	 * @param nDeviceType
	 *            视频设备类型
	 * @param nUserID
	 *            目标用户ID
	 * @param szDeviceID
	 *            nUserID的视频设备ID
	 * @param VideoPlayer
	 *            需要构建一个VideoPlayer对象
	 * @return None
	 */
	public native void VideoCloseDevice(int nGroupType, long nGroupID, int nDeviceType, long nUserID, String szDeviceID,
			VideoPlayer vp);

	/**
	 * 启用摄像头设备
	 * 
	 * @param szDeviceID
	 * @param bInuse
	 */
	public native void EnableVideoDev(String szDeviceID, int bInuse);

	/**
	 * @brief 切换摄像头
	 *
	 * @param nCustomData
	 *            程序自定义数据
	 *
	 * @return None
	 */
	public native void VideoSwitchCamera(VideoPlayer player);

	/**
	 * 视频通话本地与远端视频切换
	 * 
	 * @param szDeviceID
	 */
	public native void PausePlayout(String szDeviceID);

	/**
	 * 视频通话本地与远端视频切换
	 * 
	 * @param szDeviceID
	 */
	public native void ResumePlayout(String szDeviceID);

	/**
	 * @brief 设置默认的视频设备
	 *
	 * @param szDeviceID
	 *            设备ID
	 * @return None
	 */
	// public native void VideoSetDefaultDevice(String szDeviceID);

	/**
	 * 设置摄像头预置位
	 * 
	 * @param szDevID
	 *            设备ID
	 * @param nPresetNum
	 *            预置位（1-6）
	 * @param szPreset
	 *            预置位名称
	 */
	// public native void setCamPreset(String szDevID, int nPresetNum, String
	// szPreset);

	/**
	 * 允许别人控制摄像头
	 * 
	 * @param szDevID
	 *            设备ID
	 * @param bRemotePtz
	 *            0,禁止; 1,允许
	 */
	// public native void enableRemotePtz(String szDevID, int bRemotePtz);

	private void OnVideoUserDevices(long uid, String szXmlData) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnRemoteUserVideoDevice(uid, szXmlData);
			}
		}
	}

	/**
	 * @brief 收到他人邀请我开始视频会话邀请回调函数
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            目标用户ID
	 * @param szDeviceID
	 *            nUserID的视频设备ID
	 *
	 * @return None
	 */
	private void OnVideoChatInvite(String szSessionID, long nUserID, String szDeviceID) {
		V2Log.i("====>" + szSessionID+"   nUserID:"+nUserID+"  szDeviceID:"+ szDeviceID);
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnVideoChatInviteCallback(szSessionID, nUserID, szDeviceID);
			}
		}
	}

	/**
	 * @brief 我的视频会话邀请被对方接受回调函数
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            目标用户ID
	 * @param szDeviceID
	 *            nUserID的视频设备ID
	 *
	 * @return None
	 */
	private void OnVideoChatAccepted(String szSessionID, long nUserID, String szDeviceID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnVideoChatAccepted(szSessionID, nUserID, szDeviceID);
			}
		}
	}

	/**
	 * @brief 我的视频会话邀请被对方拒绝回调函数
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            目标用户ID
	 * @param szDeviceID
	 *            nUserID的视频设备ID
	 *
	 * @return None
	 */
	private void OnVideoChatRefused(String szSessionID, long nUserID, String szDeviceID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnVideoChatRefused(szSessionID, nUserID, szDeviceID);
			}
		}
	}

	/**
	 * @brief 收到视频会话已经建立的回调函数
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            目标用户ID
	 * @param szDeviceID
	 *            nUserID的视频设备ID
	 *
	 * @return None
	 */
	private void OnVideoChating(String szSessionID, long nUserID, String szDeviceID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnVideoChating(szSessionID, nUserID, szDeviceID);
			}
		}
	}

	/**
	 * @brief 收到视频会话被关闭的回调函数
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            目标用户ID
	 * @param szDeviceID
	 *            nUserID的视频设备ID
	 *
	 * @return None
	 */
	private void OnVideoChatClosed(String szSessionID, long nUserID, String szDeviceID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnVideoChatClosed(szSessionID, nUserID, szDeviceID);
			}
		}
	}

	private void OnSetCapParamDone(String szDevID, int nSizeIndex, int nFrameRate, int nBitRate) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnSetCapParamDone(szDevID, nSizeIndex, nFrameRate, nBitRate);
			}
		}
	}

	private void OnVideoBitRate(Object hwnd, int bps) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnVideoBitRate(hwnd, bps);
			}
		}
	}

	private void OnVideoCaptureError(String szDevID, int nErr) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnVideoCaptureError(szDevID, nErr);
			}
		}
	}

	// private void OnGetVideoDevice(String xml, long l) {
	// for (int i = 0; i < mCallBacks.size(); i++) {
	// WeakReference<VideoRequestCallback> wf = mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnGetVideoDevice(xml, l);
	// }
	// }
	// }
}
