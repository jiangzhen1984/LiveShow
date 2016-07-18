package com.V2.jni.callback;

public interface ImRequestCallback {

	/**
	 * 我登录的回调
	 * 
	 * @param nUserID
	 *            if succeed means user ID, otherwise 0
	 * @param nStatus
	 * @param nResult
	 *            0: succeed, 1: failed
	 * @param serverTime
	 * @param sDBID
	 *            : Server id
	 */
	public void OnLoginCallback(long nUserID, int nStatus, int nResult, long serverTime, String sDBID);

	/**
	 * 用户在其他设备上登录 called
	 * 
	 * @param nType
	 *            device type of logged
	 */
	public void OnLogoutCallback(int nType);

	/**
	 * 与服务器连接状态改变的回调
	 * 
	 * @param nResult
	 *            301 can't not connect server; 0: succeed
	 */
	public void OnConnectResponseCallback(int nResult);

	/**
	 * 更新基本信息
	 * 
	 * @param nUserID
	 * @param updatexml
	 */
	public void OnUpdateBaseInfoCallback(long nUserID, String updatexml);

	/**
	 * 用户状态更新的回调
	 * 
	 * @param nUserID
	 * @param nType
	 *            1 PC 2 cell phone
	 * @param nStatus
	 *            1 is online, 0 is offline
	 * @param szStatusDesc
	 */
	public void OnUserStatusUpdatedCallback(long nUserID, int nType, int nStatus, String szStatusDesc);

	/**
	 * 用户头像改变
	 * 
	 * @param nAvatarType
	 * @param nUserID
	 *            User ID which user's changed avatar
	 * @param AvatarName
	 *            patch of avatar
	 */
	public void OnChangeAvatarCallback(int nAvatarType, long nUserID, String AvatarName);

	/**
	 * 修改备注姓名
	 * 
	 * @param nUserId
	 * @param sCommmentName
	 */
	public void OnModifyCommentNameCallback(long nUserId, String sCommmentName);

	/**
	 * 检测到客户端有更新
	 * 
	 * @param updatefilepath
	 * @param updatetext
	 */
	public void OnHaveUpdateNotify(String updatefilepath, String updatetext);

	/**
	 * 开始下载更新文件
	 * 
	 * @param filesize
	 */
	public void OnUpdateDownloadBegin(long filesize);

	/**
	 * 正在下载更新
	 * 
	 * @param size
	 */
	public void OnUpdateDownloading(long size);

	/**
	 * 下载更新完成
	 * 
	 * @param error
	 */
	public void OnUpdateDownloadEnd(boolean error);

	/**
	 * 开始获得组, 组成员信息
	 */
	public void OnGetGroupsInfoBegin();

	/**
	 * 获得所有组, 组成员信息结束
	 */
	public void OnGroupsLoaded();

	/**
	 * 开始(消息, 文件, 好友请求....)
	 */
	public void OnOfflineStart();

	/**
	 * 离线请求(消息, 文件, 好友请求....)
	 */
	public void OnOfflineEnd();

	/**
	 * 和服务器信令通道断开链接
	 */
	public void OnSignalDisconnected();

	public void OnSearchUserCallback(String xmlinfo);

	public void OnImUserCreateValidateCode(int ret);

	public void OnImRegisterPhoneUser(int ret);

	public void OnImUpdateUserPwd(int ret);
	
	
	
	public void OnGuestRegister(String account, String password, int ret);
}
