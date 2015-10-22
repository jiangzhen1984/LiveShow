package com.V2.jni;

import com.V2.jni.callback.FileRequestCallback;
import com.V2.jni.ind.FileJNIObject;

public abstract class FileRequestCallbackAdapter implements FileRequestCallback {


	@Override
	public void OnFileTransProgress(String szFileID, long nBytesTransed,
			int nTransType) {

	}

	@Override
	public void OnFileTransEnd(String szFileID, String szFileName,
			long nFileSize, int nTransType) {

	}

	@Override
	public void OnFileTransError(String szFileID, int errorCode, int nTransType) {
		
	}

	@Override
	public void OnFileTransCancel(String szFileID) {
		
	}

	@Override
	public void OnFileTransInvite(long userid, String szFileID,
			String szFileName, long nFileBytes, String url, int linetype) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnFileTransAccepted(String szFileID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnFileTransRefuse(String szFileID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnFileTransBegin(String szFileID, int nTransType, long nFileSize) {
		// TODO Auto-generated method stub
		
	}


	
	
}
