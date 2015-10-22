package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.V2.jni.callback.AudioRequestCallback;

public class AudioRequest {
	private List<WeakReference<AudioRequestCallback>> mCallBacks;
	private static AudioRequest mAudioRequest;

	private AudioRequest() {
		mCallBacks = new ArrayList<WeakReference<AudioRequestCallback>>();
	};

	public static synchronized AudioRequest getInstance() {
		if (mAudioRequest == null) {
			synchronized (AudioRequest.class) {
				if (mAudioRequest == null) {
					mAudioRequest = new AudioRequest();
					if (!mAudioRequest.initialize(mAudioRequest)) {
						throw new RuntimeException("can't initilaize AudioRequest");
					}
				}
			}
		}
		return mAudioRequest;
	}

	/**
	 * 添加自定义的回调，监听接收到的服务信令
	 * 
	 * @param callback
	 */
	public void addCallback(AudioRequestCallback callback) {
		mCallBacks.add(new WeakReference<AudioRequestCallback>(callback));
	}

	/**
	 * 移除自定义添加的回调
	 * 
	 * @param callback
	 */
	public void removeCallback(AudioRequestCallback callback) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				if (wr.get() == callback) {
					mCallBacks.remove(wr);
					return;
				}
			}
		}
	}

	public native boolean initialize(AudioRequest request);

	public native void unInitialize();

	/**
	 * @brief 音频邀请
	 *
	 * @param szSessionID
	 *            语音通话ID
	 * @param nUserID
	 *            被邀请用户ID
	 *
	 * @return None
	 */
	public native void AudioInviteChat(String szSessionID, long nUserID);

	/**
	 * @brief 接受音频邀请
	 *
	 * @param szSessionID
	 *            语音通话ID
	 * @param nUserID
	 *            邀请用户ID
	 *
	 * @return None
	 */
	public native void AudioAcceptChat(String szSessionID, long nUserID);

	/**
	 * @brief 拒绝音频邀请
	 *
	 * @param szSessionID
	 *            语音通话ID
	 * @param nUserID
	 *            邀请用户ID
	 *
	 * @return None
	 */
	public native void AudioRefuseChat(String szSessionID, long nUserID);

	/**
	 * @brief 取消尚未被对方接受的音频通话
	 *
	 * @param szSessionID
	 *            语音通话ID
	 * @param nUserID
	 *            被取消音频通话用户ID
	 *
	 * @return None
	 */
	public native void AudioCancelChat(String szSessionID, long nUserID);

	/**
	 * @brief 关闭进行中的音频通话
	 *
	 * @param szSessionID
	 *            语音通话ID
	 * @param nUserID
	 *            被关闭音频通话用户ID
	 *
	 * @return None
	 */
	public native void AudioCloseChat(String szSessionID, long nUserID);

	/**
	 * @brief 静音扬声器(静音)
	 *
	 * @param nGroupID
	 *            组ID
	 * @param nUserID
	 *            被静音用户ID
	 * @param bMute
	 *            是否静音(true/false)
	 *
	 * @return None
	 */
	public native void AudioMuteMic(long nGroupID, long nUserID, boolean bMute);

	/**
	 * 让底层开始录音的函数接口
	 * 
	 * @param fileID
	 */
	public native void AudioStartRecord(String fileID);

	/**
	 * 让底层停止录音的函数接口
	 */
	public native void AudioStopRecord(String fileID);

	/**
	 * 让底层开始播放录音文件的函数接口
	 * 
	 * @param fileID
	 */
	public native void AudioStartPlay(String fileID);

	/**
	 * 让底层停止播放录音文件的函数接口
	 */
	public native void AudioStopPlay();

	/**
	 * @brief 启动组中语音(只支持讨论组和群组)
	 * 
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 */
	public native void AudioGroupEnableAudio(int eGroupType, long nGroupID);

	/**
	 * @brief 打开组中语音
	 * 
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param bSpeaker
	 *            是否发言
	 *
	 */
	public native void AudioGroupOpenAudio(int eGroupType, long nGroupID);

	/**
	 * @brief 关闭组中语音
	 * 
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 */
	public native void AudioGroupCloseAudio(int eGroupType, long nGroupID);

	/**
	 * @brief 组中申请发言
	 * 
	 * @param eGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 */
	public native void AudioGroupApplySpeaker(int eGroupType, long nGroupID);

	/**
	 * @brief 组中释放发言
	 * 
	 * @param eGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 */
	public native void AudioGroupReleaseSpeaker(int eGroupType, long nGroupID);

	/**
	 * @brief 除了指定用户全体静音
	 * 
	 * @param eGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param sExecptUserIDXml
	 *            除了指定用户 <userlist><user id='11'/></userlist>
	 */
	public native void AudioGroupMuteSpeaker(int eGroupType, long nGroupID, String sExecptUserIDXml);

	public native void ResumePlayout();

	public native void PausePlayout();

	private void OnAudioRecordStart(String RecordID, int nResult) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				wr.get().OnRecordStart(RecordID, nResult);
			}
		}
	};

	private void OnAudioRecordStop(String szRecordID, String szFileName, int nResult) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				wr.get().OnRecordStop(szRecordID, szFileName, nResult);
			}
		}
	};

	private void OnAudioChatInvite(String szSessionID, long nUserID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				wr.get().OnAudioChatInvite(szSessionID, nUserID);
			}
		}
	}

	private void OnAudioChatAccepted(String szSessionID, long nUserID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				wr.get().OnAudioChatAccepted(szSessionID, nUserID);
			}
		}
	}

	private void OnAudioChatRefused(String szSessionID, long nUserID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				wr.get().OnAudioChatRefused(szSessionID, nUserID);
			}
		}
	}

	private void OnAudioChatClosed(String szSessionID, long nUserID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				wr.get().OnAudioChatClosed(szSessionID, nUserID);
			}
		}
	}

	private void OnAudioChating(String szSessionID, long nUserID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				wr.get().OnAudioChating(szSessionID, nUserID);
			}
		}
	}

	private void OnAudioMicCurrentLevel(int nValue) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				wr.get().OnAudioMicCurrentLevel(nValue);
			}
		}
	}

	private void OnAudioGroupEnableAudio(int eGroupType, long nGroupID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				wr.get().OnAudioGroupEnableAudio(eGroupType, nGroupID);
			}
		}
	};

	private void OnAudioGroupOpenAudio(int eGroupType, long nGroupID, long nUserID, boolean bSpeaker) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				wr.get().OnAudioGroupOpenAudio(eGroupType, nGroupID , nUserID, bSpeaker);
			}
		}
	};

	private void OnAudioGroupCloseAudio(int eGroupType, long nGroupID, long nUserID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				wr.get().OnAudioGroupCloseAudio(eGroupType, nGroupID , nUserID);
			}
		}
	};

	private void OnAudioGroupUserSpeaker(int eGroupType, long nGroupID, long nUserID, boolean bSpeaker) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				wr.get().OnAudioGroupUserSpeaker(eGroupType, nGroupID , nUserID , bSpeaker);
			}
		}
	};

	private void OnAudioGroupMuteSpeaker(int eGroupType, long nGroupID, String sExecptUserIDXml) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = mCallBacks.get(i);
			if (wr != null && wr.get() != null) {
				wr.get().OnAudioGroupMuteSpeaker(eGroupType, nGroupID , sExecptUserIDXml);
			}
		}
	};
}
