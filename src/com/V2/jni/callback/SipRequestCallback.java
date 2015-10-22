package com.V2.jni.callback;


public interface SipRequestCallback {

	void OnAcceptSipCall(String szURI , boolean isVideoCall);

	void OnInviteSipCall(String szURI);

	void OnFailureSipCall(String szURI, int nErrorCode);

	void OnCloseSipCall(String szURI);

	void OnSipMicMaxVolume(int nVolume);

	void OnSipSpeakerVolum(int nVolume);

	void OnSipMuteMic(boolean bMute);

	void OnSipMuteSpeaker(boolean bMute);

	void OnSipSipCurrentLevel(int nSpeakerLevel, int nMicLevel);
}
