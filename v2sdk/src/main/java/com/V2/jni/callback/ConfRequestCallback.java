package com.V2.jni.callback;

public interface ConfRequestCallback {

	/**
	 * 我进入会议的回调
	 * 
	 * @param nConfID
	 * @param nTime
	 * @param szConfData
	 * @param nJoinResult
	 *            result of join. 0 success 200 Confence is'nt exist 205 资源不足
	 */
	public void OnEnterConfCallback(long nConfID, long nTime,
			String szConfData, int nJoinResult);



	/**
	 * 会议有用户进入的回调
	 *
	 * @param nConfID
	 * @param nUserID
	 * @param szUserInfos
	 */
	public void OnConfMemberEnter(long nConfID, long nUserID,
								  String szUserInfos);

	/**
	 * 会议有用户退出的回调
	 * 
	 * @param nConfID
	 * @param nTime
	 * @param nUserID
	 */
	public void OnConfMemberExitCallback(long nConfID, long nTime, long nUserID);

	/**
	 * 我被请出会议的回调
	 * 
	 * @param nReason
	 *            204 user deleted group 203 current user is kicked by chairman
	 */
	public void OnKickConfCallback(int nReason);

	/**
	 * 收到会议通知
	 * 
	 * @param confXml
	 * @param creatorXml
	 */
	public void OnConfNotify(String confXml, String creatorXml);

	/**
	 * 通知主席某人申请控制权回调
	 * 
	 * @param userid
	 * @param type
	 */
	public void OnNotifyChair(long userid, int type);

	/**
	 * 通知会议成员某人获得某种权限
	 * 
	 * @param userid
	 * @param type
	 * @param status
	 */
	public void OnGrantPermissionCallback(long userid, int type, int status);

	/**
	 * 同步打开某人视频
	 * 
	 * @param xml
	 */
	public void OnConfSyncOpenVideo(String str);

	/**
	 * 同步关闭某人视频
	 * 
	 * @param gid
	 * @param str
	 */
	public void OnConfSyncCloseVideo(long gid, String str);

	/**
	 * 同步取消某人视频给移动端
	 * 
	 * @param nDstUserID
	 * @param sDstMediaID
	 */
	public void OnConfSyncCloseVideoToMobile(long nDstUserID, String sDstMediaID);

	/**
	 * 同步打开某人视频给移动端
	 * 
	 * @param sSyncVideoMsgXML
	 */
	public void OnConfSyncOpenVideoToMobile(String sSyncVideoMsgXML);

	/**
	 * 会议主席更改
	 * 
	 * @param nConfID
	 * @param nChairID
	 */
	public void OnConfChairChanged(long nConfID, long nChairID);

	/**
	 * 改变同步视频位置
	 * 
	 * @param nDstUserID
	 * @param szDeviceID
	 * @param sPos
	 */
	public void OnChangeSyncConfOpenVideoPos(long nDstUserID,
			String szDeviceID, String sPos);

	public void OnConfMute();

	public void OnGetConfVodList(long nGroupID, String sVodXmlList);

	public void OnConfNotify(long nSrcUserID, String srcNickName, long nConfID,
			String subject, long nTime);

	public void OnConfNotifyEnd(long nConfID);

	public void OnConfSyncOpenVideo(long nDstUserID, String sDstMediaID,
			int nPos);

}
