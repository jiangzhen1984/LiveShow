package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.V2.jni.callback.SipRequestCallback;

public class SipRequest {
	private static SipRequest mSipRequest;
	private List<WeakReference<SipRequestCallback>> mCallBacks;

	private SipRequest() {
		mCallBacks = new ArrayList<WeakReference<SipRequestCallback>>();
	};

	public static synchronized SipRequest getInstance() {
		if (mSipRequest == null) {
			synchronized (SipRequest.class) {
				if (mSipRequest == null) {
					mSipRequest = new SipRequest();
					if (!mSipRequest.Initialize(mSipRequest)) {
						throw new RuntimeException("can't initilaize SipRequest");
					}
				}
			}
		}
		return mSipRequest;
	}

	public void addCallback(SipRequestCallback callback) {
		this.mCallBacks.add(new WeakReference<SipRequestCallback>(callback));
	}

	public void removeCallback(SipRequestCallback callback) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<SipRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				if (wf.get() == callback) {
					mCallBacks.remove(wf);
					return;
				}
			}
		}
	}

	public native boolean Initialize(SipRequest request);

	public native void UnInitialize();

	/**
	 * @brief SIP呼叫
	 *
	 * @param szURI
	 *            呼叫的URI (1:注册sip服务器成功直接呼叫对方的注册号码, 未注册直接呼叫ip)
	 * @param isVideo
	 *            是否是视频通话
	 * @return None
	 */
	public native void InviteSipCall(String szURI, boolean isVideo);

	/**
	 * @brief 接受SIP呼叫
	 *
	 * @param szURI
	 *            呼叫的URI
	 * @param isVideoCall
	 *            是否是视频呼叫
	 * @return None
	 */
	public native void AcceptSipCall(String szURI, boolean isVideoCall);

	/**
	 * @brief 挂断SIP
	 *
	 * @param szURI
	 *            呼叫的URI
	 *
	 * @return None
	 */
	public native void CloseSipCall(String szURI);

	/**
	 * @brief 二次拨号
	 *
	 * @param szURI
	 *            呼叫的URI
	 * @param szDialNum
	 *            二次拨号的按键
	 *
	 * @return None
	 */
	public native void SipSecondDial(String szURI, String szDialNum);

	/**
	 * @brief 设置SIP麦克风的最大音量
	 *
	 * @param nVolume
	 *            最大音量值
	 *
	 * @return None
	 */
	public native void MicSetMaxValue(int nVolume);

	/**
	 * @brief 设置SIP扬声器的最大音量
	 *
	 * @param nVolume
	 *            最大音量值
	 *
	 * @return None
	 */
	public native void SpeakerSetMaxValue(int nVolume);

	/**
	 * @brief 设置SIP扬声器是否静音
	 *
	 * @param bMute
	 *            True表示静音 False表示不静音
	 *
	 * @return None
	 */
	public native void SetSpkeakerMuted(boolean bMute);

	/**
	 * @brief 设置SIP麦克风是否静音
	 *
	 * @param bMute
	 *            True表示静音 False表示不静音
	 *
	 * @return None
	 */
	public native void SetMicMuted(boolean bMute);

	/**
	 * @brief 对方接受呼叫邀请
	 * 
	 * @param szURI
	 *            呼叫时候的号码
	 * @param isVideoCall
	 *            是否是视频通话
	 * @return None
	 */
	public void OnInviteSipCall(String szURI, boolean isVideoCall) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<SipRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnInviteSipCall(szURI);
			}
		}
	}

	/**
	 * @brief 收到sip呼叫邀请
	 * 
	 * @param szURI
	 *            来自呼叫号码
	 * @param isVideoCall
	 *            是否是视频邀请
	 * @return None
	 */
	public void OnAcceptSipCall(String szURI, boolean isVideoCall) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<SipRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnAcceptSipCall(szURI, isVideoCall);
			}
		}
	}

	/**
	 * @brief 对方拒绝呼叫邀请
	 * 
	 * @param szURI
	 *            呼叫时候的号码
	 * @param nErrorCode
	 *            见错误码
	 * 
	 * @return None
	 */
	public void OnFailureSipCall(String szURI, int nErrorCode) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<SipRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnFailureSipCall(szURI, nErrorCode);
			}
		}
	}

	/**
	 * @brief 对方挂断呼叫
	 * 
	 * @param szURI
	 *            呼叫时候的号码
	 * 
	 * @return None
	 */
	public void OnCloseSipCall(String szURI) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<SipRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnCloseSipCall(szURI);
			}
		}
	}

	/**
	 * @brief Mic设置的最大值
	 * 
	 * @param nVolume
	 *            数值
	 * 
	 * @return None
	 */
	public void OnSipMicMaxVolume(int nVolume) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<SipRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnSipMicMaxVolume(nVolume);
			}
		}
	}

	/**
	 * @brief Speaker设置的最大值
	 * 
	 * @param nVolume
	 *            数值
	 * 
	 * @return None
	 */
	public void OnSipSpeakerVolum(int nVolume) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<SipRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnSipSpeakerVolum(nVolume);
			}
		}
	}

	/**
	 * @brief Mic状态
	 * 
	 * @param bMute
	 *            是否静音
	 * 
	 * @return None
	 */
	public void OnSipMuteMic(boolean bMute) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<SipRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnSipMuteMic(bMute);
			}
		}
	}

	/**
	 * @brief Speaker状态
	 * 
	 * @param bMute
	 *            是否静音
	 * 
	 * @return None
	 */
	public void OnSipMuteSpeaker(boolean bMute) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<SipRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnSipMuteSpeaker(bMute);
			}
		}
	}

	/**
	 * @brief 当前Speaker和Mic大小
	 * 
	 * @param nSpeakerLevel
	 *            Speaker大小
	 * @param nMicLevel
	 *            Mic大小
	 * 
	 * @return None
	 */
	public void OnSipSipCurrentLevel(int nSpeakerLevel, int nMicLevel) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<SipRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnSipSipCurrentLevel(nSpeakerLevel, nMicLevel);
			}
		}
	}
}
