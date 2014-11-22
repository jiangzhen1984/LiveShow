package com.V2.jni;

import com.V2.jni.ind.FileJNIObject;

public interface FileRequestCallback {

	/**
	 * 
	 * @param file
	 */
	public void OnFileTransInvite(FileJNIObject file);

	/**
	 * 
	 * @param szFileID
	 * @param nBytesTransed
	 * @param nTransType
	 *            2: offline file 1: online file
	 */
	public void OnFileTransProgress(String szFileID, long nBytesTransed,
			int nTransType);

	/**
	 * 
	 * @param szFileID
	 * @param errorCode
	 * @param nTransType
	 */
	public void OnFileTransError(String szFileID, int errorCode, int nTransType);

	/**
	 * 
	 * @param szFileID
	 * @param szFileName
	 * @param nFileSize
	 * @param nTransType
	 */
	public void OnFileTransEnd(String szFileID, String szFileName,
			long nFileSize, int nTransType);

	/**
	 * 
	 * @param szFileID
	 */
	public void OnFileTransCancel(String szFileID);

	/**
	 * 
	 * @param sFileID
	 * @param errorCode
	 * @param nTransType
	 */
	public void OnFileDownloadError(String sFileID,int errorCode, int nTransType);
	
	
	/**
	 * File deleted 
	 * @param file
	 */
	public void OnFileDeleted(FileJNIObject file);
}
