package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.V2.jni.ind.AudioJNIObjectInd;
import com.V2.jni.util.V2Log;

public class AudioRequest {

	private Context context;
	private static AudioRequest mAudioRequest;

	private List<WeakReference<AudioRequestCallback>> callbacks;

	private AudioRequest(Context context) {
		this.context = context;
		callbacks = new ArrayList<WeakReference<AudioRequestCallback>>();
	};

	public void addCallback(AudioRequestCallback callback) {
		callbacks.add(new WeakReference<AudioRequestCallback>(callback));
	}

	public void removeCallback(AudioRequestCallback callback) {
		for (WeakReference<AudioRequestCallback> wr : callbacks) {
			Object obj = wr.get();
			if (obj == callback) {
				callbacks.remove(wr);
				return;
			}
		}
	}

	public static synchronized AudioRequest getInstance(Context context) {
		if (mAudioRequest == null) {
			mAudioRequest = new AudioRequest(context);
			if (!mAudioRequest.initialize(mAudioRequest)) {
				Log.e("AudioRequest", "can't initialize AudioRequest ");
			}
		}
		return mAudioRequest;
	}

	public static synchronized AudioRequest getInstance() {
		if (mAudioRequest == null) {
			mAudioRequest = new AudioRequest(null);
			if (!mAudioRequest.initialize(mAudioRequest)) {
				Log.e("AudioRequest", "can't initialize AudioRequest ");
			}
		}
		return mAudioRequest;
	}

	public native boolean initialize(AudioRequest request);

	public native void unInitialize();

	/**
	 * Invite user to join audio call
	 * 
	 * @param nGroupID
	 * @param nToUserID
	 * @param businesstype
	 *            type of request
	 * 
	 * @see V2GlobalEnum#REQUEST_TYPE_CONF
	 * @see V2GlobalEnum#REQUEST_TYPE_IM
	 */
	public native void InviteAudioChat(String szSessionID, long nToUserID);

	/**
	 * Accept audio conversation
	 * 
	 * @param nGroupID
	 * @param nToUserID
	 * @param businesstype
	 *            type of request
	 * 
	 * @see V2GlobalEnum#REQUEST_TYPE_CONF
	 * @see V2GlobalEnum#REQUEST_TYPE_IM
	 */
	public native void AcceptAudioChat(String szSessionID, long nToUserID);

	/**
	 * reject audio conversation
	 * 
	 * @param nGroupID
	 * @param nToUserID
	 * @param businesstype
	 *            type of request
	 * 
	 * @see V2GlobalEnum#REQUEST_TYPE_CONF
	 * @see V2GlobalEnum#REQUEST_TYPE_IM
	 */
	public native void RefuseAudioChat(String szSessionID, long nToUserID);

	/**
	 * 
	 * @param nGroupID
	 * @param nToUserID
	 * @param businesstype
	 *            type of request
	 * 
	 * @see V2GlobalEnum#REQUEST_TYPE_CONF
	 * @see V2GlobalEnum#REQUEST_TYPE_IM
	 */
	public native void CloseAudioChat(String szSessionID, long nToUserID);

	/**
	 * 
	 * @param nGroupID
	 * @param nUserID
	 * @param bMute
	 * @param businesstype
	 *            type of request
	 * 
	 * @see V2GlobalEnum#REQUEST_TYPE_CONF
	 * @see V2GlobalEnum#REQUEST_TYPE_IM
	 */
	public native void MuteMic(long nGroupID, long nUserID, boolean bMute);

	public native void CancelAudioChat(String szSessionID, long nToUserID);

	
	/**
	 * 
	 */
	public native void PausePlayout();
	
	public native void ResumePlayout();
	
	// �յ���Ƶͨ��������Ļص�
	private void OnAudioChatInvite(String szSessionID, long nFromUserID) {
		V2Log.d("OnAudioChaInvite " + szSessionID + ":"
				+ nFromUserID);
		for (int i = 0; i < callbacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = callbacks.get(i);
			Object obj = wr.get();
			if (obj != null) {
				((AudioRequestCallback) obj)
						.OnAudioChatInvite(new AudioJNIObjectInd(szSessionID,
								nFromUserID));
			}
		}
	}

	private void OnAudioChatAccepted(String szSessionID, long nFromUserID) {
		V2Log.d("OnAudioChatAccepted " + szSessionID + ":"
				+ nFromUserID);

		for (int i = 0; i < callbacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = callbacks.get(i);
			Object obj = wr.get();
			if (obj != null) {
				((AudioRequestCallback) obj).OnAudioChatAccepted(new AudioJNIObjectInd(szSessionID,
						nFromUserID));
			}
		}
	}

	// ��Ƶͨ�����뱻�Է��ܾ�Ļص�
	private void OnAudioChatRefused(String szSessionID, long nFromUserID) {
		V2Log.d("OnAudioChatRefused " + szSessionID  + ":"
				+ nFromUserID);
		for (int i = 0; i < callbacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = callbacks.get(i);
			Object obj = wr.get();
			if (obj != null) {
				((AudioRequestCallback) obj).OnAudioChatRefused(new AudioJNIObjectInd(szSessionID,
						nFromUserID));
			}
		}
	}

	// ��Ƶͨ�����ر���ص�
	private void OnAudioChatClosed(String szSessionID, long nFromUserID) {
		V2Log.d("OnAudioChatClosed " + szSessionID + ":"
				+ nFromUserID);
		for (int i = 0; i < callbacks.size(); i++) {
			WeakReference<AudioRequestCallback> wr = callbacks.get(i);
			Object obj = wr.get();
			if (obj != null) {
				((AudioRequestCallback) obj).OnAudioChatClosed(new AudioJNIObjectInd(szSessionID,
						nFromUserID));
			}
		}
	}

	// ��Ƶͨ��������
	private void OnAudioChating(String szSessionID, long nFromUserID) {
		Log.e("ImRequest UI", "OnAudioChating " + szSessionID + ":" + nFromUserID);
	}
}
