package com.V2.jni.callback;


public interface GroupRequestCallback {

	/**
	 * 获取组用户回调
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param sXml
	 */
	public void OnGetGroupUserInfoCallback(int groupType, long nGroupID,
			String sXml);

	/**
	 * 删除组回调
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param bMovetoRoot
	 */
	public void OnDelGroupCallback(int groupType, long nGroupID,
			boolean bMovetoRoot);

	/**
	 * 删除组用户回调
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param nUserID
	 */
	public void OnDelGroupUserCallback(int groupType, long nGroupID,
			long nUserID);

	/**
	 * 添加组用户回调
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param sXml
	 */
	public void OnAddGroupUserInfoCallback(int groupType, long nGroupID,
			String sXml);

	/**
	 * 添加组回调
	 * 
	 * @param groupType
	 * @param nParentID
	 * @param nGroupID
	 * @param sXml
	 */
	public void onAddGroupInfo(int groupType, long nParentID, long nGroupID,
			String sXml);

	/**
	 * 接受被邀请加入组回调
	 * 
	 * @param groupType
	 * @param groupId
	 * @param nUserID
	 */
	public void OnAcceptInviteJoinGroup(int groupType, long groupId,
			long nUserID);

	/**
	 * 管理员把自己从组中请出
	 * 
	 * @param groupType
	 * @param groupId
	 * @param nUserId
	 *            should always same with current logged in user
	 */
	public void OnKickGroupUser(int groupType, long groupId, long nUserId);

	/**
	 * 加入群失败（如群已经被删除等）
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param nErrorNo
	 */
	public void OnJoinGroupError(int eGroupType, long nGroupID, int nErrorNo);

	/**
	 * 邀请加入组回调
	 * 
	 * @param groupType
	 * @param groupInfo
	 * @param userInfo
	 * @param additInfo
	 */
	public void OnInviteJoinGroup(int groupType, String groupInfo,
			String userInfo, String additInfo);

	/**
	 * 拒绝被邀请加入组回调
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param nUserID
	 * @param reason
	 */
	public void OnRefuseInviteJoinGroup(int groupType, long nGroupID,
			long nUserID, String reason);

	/**
	 * 将好友移到某个组回调
	 * 
	 * @param groupType
	 * @param srcGroupID
	 * @param dstGroupID
	 * @param nUserID
	 */
	public void OnMoveUserToGroup(int groupType, long srcGroupID,
			long dstGroupID, long nUserID);

	/**
	 * 收到申请加入群回调
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param userInfo
	 * @param reason
	 */
	public void OnApplyJoinGroup(int groupType, long nGroupID, String userInfo,
			String reason);

	/**
	 * 接受申请加入群回调
	 * 
	 * @param groupType
	 * @param sXml
	 */
	public void OnAcceptApplyJoinGroup(int groupType, String sXml);

	/**
	 * 拒绝申请加入群回调
	 * 
	 * @param groupType
	 * @param sXml
	 * @param reason
	 */
	public void OnRefuseApplyJoinGroup(int groupType, String sXml, String reason);

	/**
	 * 会议中创建白板的回调
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param szWBoardID
	 * @param nWhiteIndex
	 */
	public void OnGroupCreateWBoard(int eGroupType, long nGroupID,
			String szWBoardID, int nWhiteIndex);

	/**
	 * 文件重命名
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param sFileID
	 * @param sNewName
	 */
	public void OnRenameGroupFile(int eGroupType, long nGroupID,
			String sFileID, String sNewName);

	/**
	 * 收到白板会话被关闭的回调
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param szWBoardID
	 */
	public void OnWBoardDestroy(int eGroupType, long nGroupID, String szWBoardID);

	/**
	 * 会议中创建文档共享的回调
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param szWBoardID
	 * @param szFileName
	 */
	public void OnGroupCreateDocShare(int eGroupType, long nGroupID,
			String szWBoardID, String szFileName);

	/**
	 * 搜索群组回调
	 * 
	 * @param eGroupType
	 * @param infoXml
	 */
	public void OnSearchGroup(int eGroupType, String infoXml);

	/**
	 * 更新组信息回调
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param sXml
	 */
	public void OnModifyGroupInfo(int groupType, long nGroupID, String sXml);

	/**
	 * 获得组信息回调
	 * 
	 * @param groupType
	 * @param sXml
	 */
	public void OnGetGroupInfo(int groupType, String sXml);

	/**
	 * 组中删除文件的回调
	 * 
	 * @param type
	 * @param nGroupId
	 * @param fileId
	 */
	public void OnDelGroupFile(int type, long nGroupId, String fileId);

	/**
	 * 组中添加文件的回调
	 * 
	 * @param eGroupType
	 * @param nGroupId
	 * @param sXml
	 */
	public void OnAddGroupFile(int eGroupType, long nGroupId, String sXml);

	public void OnGetGroupFileInfo(int groupType, long nGroupId, String sXml);
}
