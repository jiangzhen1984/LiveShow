package com.V2.jni;

import com.V2.jni.ind.VideoJNIObjectInd;

public abstract class VideoRequestCallbackAdapter implements
		VideoRequestCallback {

	@Override
	public void OnRemoteUserVideoDevice(long uid, String szXmlData) {
		
	}

	@Override
	public void OnVideoChatInviteCallback(VideoJNIObjectInd ind) {
		
	}

	@Override
	public void OnSetCapParamDone(String szDevID, int nSizeIndex,
			int nFrameRate, int nBitRate) {
		
	}

	@Override
	public void OnVideoChatAccepted(VideoJNIObjectInd ind) {
		
	}

	@Override
	public void OnVideoChatRefused(VideoJNIObjectInd ind) {
		
	}

	@Override
	public void OnVideoChatClosed(VideoJNIObjectInd ind) {
		
	}

	@Override
	public void OnVideoChating(VideoJNIObjectInd ind) {
		
	}



}
