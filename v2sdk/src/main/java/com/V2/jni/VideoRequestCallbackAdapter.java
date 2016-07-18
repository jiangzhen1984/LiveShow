package com.V2.jni;

import com.V2.jni.callback.VideoRequestCallback;
import com.V2.jni.ind.VideoJNIObjectInd;

public abstract class VideoRequestCallbackAdapter implements
VideoRequestCallback {

	@Override
	public void OnRemoteUserVideoDevice(long uid, String szXmlData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnVideoChatInviteCallback(String szSessionID, long nFromUserID,
			String szDeviceID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnSetCapParamDone(String szDevID, int nSizeIndex,
			int nFrameRate, int nBitRate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnVideoChatAccepted(String szSessionID, long nFromUserID,
			String szDeviceID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnVideoChatRefused(String szSessionID, long nFromUserID,
			String szDeviceID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnVideoChatClosed(String szSessionID, long nFromUserID,
			String szDeviceID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnVideoChating(String szSessionID, long nFromUserID,
			String szDeviceID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnVideoBitRate(Object hwnd, int bps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnGetVideoDevice(String xml, long l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnVideoCaptureError(String szDevID, int nErr) {
		// TODO Auto-generated method stub
		
	}




}
