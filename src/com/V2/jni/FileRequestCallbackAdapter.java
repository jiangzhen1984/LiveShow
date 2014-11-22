package com.V2.jni;

import com.V2.jni.ind.FileJNIObject;

public abstract class FileRequestCallbackAdapter implements FileRequestCallback {

	@Override
	public void OnFileTransInvite(FileJNIObject file) {

	}

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
	public void OnFileDownloadError(String sFileID, int errorCode,
			int nTransType) {
		
	}

	@Override
	public void OnFileDeleted(FileJNIObject file) {
		
	}

	
	
}
