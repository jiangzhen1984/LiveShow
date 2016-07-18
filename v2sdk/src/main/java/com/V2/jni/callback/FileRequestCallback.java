package com.V2.jni.callback;

public interface FileRequestCallback {

	/**
	 * 收到他人的文件传输邀请的回调
	 * 
	 * @param userid
	 * @param szFileID
	 * @param szFileName
	 * @param nFileBytes
	 * @param url
	 * @param linetype
	 */
	public void OnFileTransInvite(long userid, String szFileID,
			String szFileName, long nFileBytes, String url, int linetype);

	/**
	 * 收到我的文件传输邀请被对方接受的回调
	 * 
	 * @param szFileID
	 */
	public void OnFileTransAccepted(String szFileID);

	/**
	 * 对方拒绝接收文件回调
	 * 
	 * @param szFileID
	 */
	public void OnFileTransRefuse(String szFileID);

	/**
	 * 收到文件传输开始的回调
	 * 
	 * @param szFileID
	 * @param nTransType
	 * @param nFileSize
	 */
	public void OnFileTransBegin(String szFileID, int nTransType, long nFileSize);

	/**
	 * 收到文件传输进度的回调
	 * 
	 * @param szFileID
	 * @param nBytesTransed
	 * @param nTransType
	 *            2: offline file 1: online file
	 */
	public void OnFileTransProgress(String szFileID, long nBytesTransed,
			int nTransType);

	/**
	 * 文件传输失败
	 * 
	 * @param szFileID
	 * @param errorCode
	 * @param nTransType
	 */
	public void OnFileTransError(String szFileID, int errorCode, int nTransType);

	/**
	 * 收到文件传输完成的回调
	 * 
	 * @param szFileID
	 * @param szFileName
	 * @param nFileSize
	 * @param nTransType
	 */
	public void OnFileTransEnd(String szFileID, String szFileName,
			long nFileSize, int nTransType);

	/**
	 * 收到对方取消文件传输回调
	 * 
	 * @param szFileID
	 */
	public void OnFileTransCancel(String szFileID);

}
