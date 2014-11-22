package com.V2.jni;

import com.V2.jni.ind.SendingResultJNIObjectInd;



public interface ChatRequestCallback {

	
	/**
	 * <ul>Use to receive message from server side.</ul>
	 * 
	 * @param nGroupID
	 * @param nBusinessType  1: conference  2: IM
	 * @param nFromUserID
	 * @param nTime
	 * @param szXmlText
	 */
	public void OnRecvChatTextCallback(int eGroupType, long nGroupID, long nFromUserID,
			long nToUserID, long nTime, String szSeqID, String szXmlText);

	/**
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param nFromUserID
	 * @param nToUserID
	 * @param nTime 发送时服务器的时间，用于确定离线消息的时间
	 * @param messageId
	 * @param binaryType
	 * @param binaryPath
	 */
	public void OnRecvChatBinaryCallback(int eGroupType, long nGroupID,
			long nFromUserID, long nToUserID, long nTime, String messageId,
			int binaryType, String binaryPath);
	
	/**
	 * <ul>Receive image data from server side.</ul>
	 * @param nGroupID
	 * @param nBusinessType  1: conference  2: IM
	 * @param nFromUserID
	 * @param nTime
	 * @param pPicData
	 */
	public void OnRecvChatPictureCallback(long nGroupID, int nBusinessType,
			long nFromUserID, long nTime, String szSeqID, byte[] pPicData);
	
	/**
	 * <ul>Receive audio data from server side.</ul>
	 * @param gid  belong group id
	 * @param businessType   1: conference  2: IM
	 * @param fromUserId  
	 * @param timeStamp
	 * @param messageId
	 * @param audioPath
	 */
	public void OnRecvChatAudio(long gid, int businessType, long fromUserId, long timeStamp, String messageId,
			String audioPath);
	
	/**
	 * <ul>Receive audio data or picture data from server side.</ul>
	 * @param eGroupType
	 * @param nGroupID
	 * @param nFromUserID
	 * @param nToUserID
	 * @param nTime
	 * @param binaryType
	 * @param messageId
	 * @param binaryPath
	 */
	public void OnRecvChatBinary(int eGroupType, long nGroupID, long nFromUserID,long nToUserID, long nTime, int binaryType, String messageId,
			String binaryPath);
	
	/**
	 * <ul>Send message result.</ul>
	 * @param ind
	 */
	public void OnSendChatResult(SendingResultJNIObjectInd ind);


}
