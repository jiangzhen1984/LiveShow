package com.V2.jni.callback;


public interface ChatRequestCallback {

	/**
	 * 收到他人发来的文字聊天信息的回调
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param nFromUserID
	 * @param nToUserID
	 * @param nTime
	 * @param szSeqID
	 * @param szXmlText
	 */
	public void OnRecvChatTextCallback(int eGroupType, long nGroupID,
			long nFromUserID, long nToUserID, long nTime, String szSeqID,
			String szXmlText);

	/**
	 * 接收图片和音频等二进制文件
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param nFromUserID
	 * @param nToUserID
	 * @param nTime
	 * @param binaryType
	 * @param messageId
	 * @param binaryPath
	 */
	public void OnRecvChatBinaryCallback(int eGroupType, long nGroupID,
			long nFromUserID, long nToUserID, long nTime, int binaryType,
			String messageId, String binaryPath);

	/**
	 * 发送/接受聊天消息成功否结果回调
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param nFromUserID
	 * @param nToUserID
	 * @param sSeqID
	 * @param nResult
	 */
	public void OnSendTextResultCallback(int eGroupType, long nGroupID,
			long nFromUserID, long nToUserID, String sSeqID, int nResult);

	/**
	 * 发送/接受二进制数据消息成功否结果回调F
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param nFromUserID
	 * @param nToUserID
	 * @param mediaType
	 * @param sSeqID
	 * @param nResult
	 */
	public void OnSendBinaryResultCallback(int eGroupType, long nGroupID,
			long nFromUserID, long nToUserID, int mediaType, String sSeqID,
			int nResult);

	/**
	 * 图片或音频接收超时的回调
	 * 
	 * @param eGroupType
	 *            类型是图片还是语音
	 * @param sSeqID
	 *            传输的ID
	 * @param nResult
	 *            接受失败原因
	 */
	public void OnMonitorRecv(int eGroupType, String sSeqID, int nResult);

}
