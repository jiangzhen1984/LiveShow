package com.V2.jni.callback;

public interface AudioRequestCallback {

	/**
	 * @brief 音频通话邀请回调函数
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            邀请用户ID
	 *
	 * @return None
	 */
	public void OnAudioChatInvite(String szSessionID, long nUserID);

	/**
	 * @brief 音频通话邀请被对方接受回调函数
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            被邀请用户ID
	 *
	 * @return None
	 */
	public void OnAudioChatAccepted(String szSessionID, long nUserID);

	/**
	 * @brief 音频通话邀请被对方拒绝回调函数
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            被邀请用户ID
	 *
	 * @return None
	 */
	public void OnAudioChatRefused(String szSessionID, long nUserID);

	/**
	 * @brief 音频通话被关闭回调函数
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            关闭音频用户ID
	 *
	 * @return None
	 */
	public void OnAudioChatClosed(String szSessionID, long nUserID);

	/**
	 * @brief 录音开始
	 * @paramsz RecordID 语音的ID
	 * @param nResult
	 *            开始状态：是否成功以及异常代码，0代表成功，其他为失败
	 */
	public void OnRecordStart(String RecordID, int nResult);

	/**
	 * @brief 录音结束
	 * @param szRecordID
	 *            语音的ID
	 * @param szFileName
	 *            本地位置（全路径）
	 * @param nResult
	 *            结果：是否成功以及异常代码，0代表成功，其他为失败
	 */
	public void OnRecordStop(String szRecordID, String szFileName, int nResult);

	/**
	 * @brief 上报麦克风的当前音量回调函数
	 *
	 * @param nValue
	 *            音量值
	 *
	 * @return None
	 */
	public void OnAudioMicCurrentLevel(int nValue);

	/**
	 * @brief 音频通话进行中回调函数
	 *
	 * @param szSessionID
	 *            会话ID
	 * @param nUserID
	 *            用户ID
	 *
	 * @return None
	 */
	public void OnAudioChating(String szSessionID, long nUserID);

	/**
	 * @brief 启动组中语音
	 * 
	 * @param eGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 */
	public void OnAudioGroupEnableAudio(int eGroupType, long nGroupID);

	/**
	 * @brief 打开组中语音
	 * 
	 * @param EGROUPTYPE
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nUserID
	 *            用户ID
	 * @param bSpeaker
	 *            是否发言状态
	 */
	public void OnAudioGroupOpenAudio(int eGroupType, long nGroupID, long nUserID, boolean bSpeaker);

	/**
	 * @brief 关闭组中语音
	 * 
	 * @param EGROUPTYPE
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nUserID
	 *            用户ID
	 */
	public void OnAudioGroupCloseAudio(int eGroupType, long nGroupID, long nUserID);

	/**
	 * @brief 组中用户发言状态
	 * 
	 * @param EGROUPTYPE
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nUserID
	 *            用户ID
	 * @param bSpeaker
	 *            发言/取消发言
	 */
	public void OnAudioGroupUserSpeaker(int eGroupType, long nGroupID, long nUserID, boolean bSpeaker);

	/**
	 * @brief 除了指定用户会场静音
	 * 
	 * @param EGROUPTYPE
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param sExecptUserIDXml
	 *            除了指定用户
	 */
	public void OnAudioGroupMuteSpeaker(int eGroupType, long nGroupID, String sExecptUserIDXml);
}
