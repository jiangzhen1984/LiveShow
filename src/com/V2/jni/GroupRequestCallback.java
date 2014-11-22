package com.V2.jni;

import java.util.List;

import com.V2.jni.ind.FileJNIObject;
import com.V2.jni.ind.GroupQualicationJNIObject;
import com.V2.jni.ind.V2Document;
import com.V2.jni.ind.V2Group;
import com.V2.jni.ind.V2User;

/**
 * 
 * @author 28851274
 * 
 */
public interface GroupRequestCallback {

	/**
	 * When log in successfully, this function will be called by JNI.<br>
	 * To indicate group information current user belongs and owns.
	 * 
	 * @param groupType
	 *            1:org 2: contacts group 3: crowd type 4: conference type
	 * @param list
	 *            group list
	 * 
	 * @see com.v2tech.vo.Group#GroupType
	 */
	public void OnGetGroupInfoCallback(int groupType, List<V2Group> list);

	/**
	 * When log in successfully, this function will be call by JNI.<br>
	 * To indicate users information who users belong group
	 * <ul>
	 * XML:<br>
	 * {@code <xml><user accounttype='1' address='沈阳' birthday='1981-10-27' email='' fax='02422523280' id='174' mobile='13998298300' needauth='0' nickname='朱  江' privacy='0' sex='1' sign='13998298300' telephone='02422523280'/></xml>}
	 * 
	 * </ul>
	 * 
	 * @param groupType
	 *            <ul>
	 *            <br>
	 *            <li>1: contact type
	 *            {@link com.v2tech.vo.Group.GroupType#FRIGROUP}<br>
	 *            </li>
	 *            <li>4: conference type
	 *            {@link com.v2tech.vo.Group.GroupType#CONFERENCE}<br>
	 *            </li>
	 *            </ul>
	 * @param nGroupID
	 * @param sXml
	 */
	public void OnGetGroupUserInfoCallback(int groupType, long nGroupID,
			String sXml);


	/**
	 * Callback of group information updated
	 * @param group
	 */
	public void OnModifyGroupInfoCallback(V2Group group);
	

	/**
	 * Invite user join conference or crowd. 
	 * @param group 
	 */
	public void OnInviteJoinGroupCallback(V2Group group);
	
	
	/**
	 * Add contact relation request.
	 * @param user 
	 * @param additInfo 
	 */
	public void OnRequestCreateRelationCallback(V2User user,
			String additInfo);

	/**
	 * Callback of delete group
	 * @param groupType
	 * @param nGroupID
	 * @param bMovetoRoot
	 */
	public void OnDelGroupCallback(int groupType, long nGroupID,
			boolean bMovetoRoot);

	/**
	 * TODO add comment
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param nUserID
	 */
	public void OnDelGroupUserCallback(int groupType, long nGroupID,
			long nUserID);

	/**
	 * The function called when a new friend coming 增加好友成功时的回调
	 * @param obj
	 */
	public void OnAddGroupUserInfoCallback(int groupType, long nGroupID, V2User user);

	/**
	 * new group created call back
	 * 
	 * @param group
	 */
	public void onAddGroupInfo(V2Group group);

	/**
	 * update contact group callback
	 * 
	 * @param groupType
	 * @param srcGroup
	 * @param desGroup
	 * @param u
	 */
	public void OnMoveUserToGroup(int groupType, V2Group srcGroup,
			V2Group desGroup, V2User u);

	/**
	 * Callback of accept join crowd invitation
	 * 
	 * @param groupType
	 * @param groupId
	 * @param nUserID
	 */
	public void OnAcceptInviteJoinGroup(int groupType, long groupId,
			long nUserID);
	
	
	/**
	 * Callback of accept apply join crowd invitation
	 * 
	 * @param group
	 */
	public void OnAcceptApplyJoinGroup(V2Group group);
	
	
	/**
	 * Callback of refuse apply join crowd invitation
	 * @param parseSingleCrowd
	 * @param reason
	 */
	public void OnRefuseApplyJoinGroup(V2Group parseSingleCrowd, String reason);
	/**
	 * Callback of  apply join crowd 
	 * 
	 * @param group
	 */
	public void OnApplyJoinGroup(V2Group group, V2User user, String reason);

	
	/**
	 * this funcation was called when be invited user refused to join group
	 * @param groupType
	 * @param nGroupID
	 * @param nUserID
	 * @param reason
	 */
	public void OnRefuseInviteJoinGroup(GroupQualicationJNIObject qualicationObject);
	
	/**
	 * call back for get group file list
	 * @param group
	 * @param list
	 * 
	 * @see GroupRequest#getGroupFileInfo(int, long)
	 */
	public void OnGetGroupFileInfo(V2Group group, List<FileJNIObject> list);
	
	
	
	/**
	 * call back for removed group file list
	 * @param group
	 * @param list
	 * 
	 * @see GroupRequest#delGroupFile(int, long, String)
	 */
	public void OnDelGroupFile(V2Group group, List<FileJNIObject> list);
	
	/**
	 * call back when other user upload new group file 
	 * @param group
	 * @param list
	 */
	public void OnAddGroupFile(V2Group group, List<FileJNIObject> list);
	
	

	
	/**
	 * Call back function which search group
	 * @param list
	 * @see GroupRequest#searchGroup(int, String, int, int)
	 */
	public void OnSearchCrowdCallback(List<V2Group> list);
	
	
	/**
	 * Callback function which current user was kicked by crowd owner
	 * @param groupType
	 * @param groupId
	 * @param nUserId should always same with current logged in user
	 */
	public void OnKickGroupUser(int groupType, long groupId, long nUserId);
	
	
	/**
	 * 
	 * @param doc
	 */
	public void OnGroupWBoardNotification(V2Document doc, DocOpt opt);
	
	
	public enum DocOpt {
		CREATE, RENAME, DESTROY
	}

	/**
	 * Callback function which current user apply a crowd group , hanppen a mistake 
	 * @param eGroupType
	 * @param nGroupID
	 * @param nErrorNo
	 */
	public void OnJoinGroupError(int eGroupType, long nGroupID, int nErrorNo);
}
