package com.V2.jni;

import java.util.List;

import com.V2.jni.ind.FileJNIObject;
import com.V2.jni.ind.GroupQualicationJNIObject;
import com.V2.jni.ind.V2Document;
import com.V2.jni.ind.V2Group;
import com.V2.jni.ind.V2User;

public abstract class GroupRequestCallbackAdapter implements
		GroupRequestCallback {

	@Override
	public void OnGetGroupInfoCallback(int groupType, List<V2Group> list) {

	}

	@Override
	public void OnGetGroupUserInfoCallback(int groupType, long nGroupID,
			String sXml) {

	}

	@Override
	public void OnModifyGroupInfoCallback(V2Group group) {

	}
	
	
	
	public void OnInviteJoinGroupCallback(V2Group group){
		
	}
	
	

	public void OnRequestCreateRelationCallback(V2User user,
			String additInfo){
		
	}


	@Override
	public void OnDelGroupCallback(int groupType, long nGroupID,
			boolean bMovetoRoot) {

	}

	@Override
	public void OnDelGroupUserCallback(int groupType, long nGroupID,
			long nUserID) {

	}

	
	@Override
	public void OnAddGroupUserInfoCallback(int groupType, long nGroupID,
			V2User user) {
		
	}

	@Override
	public void onAddGroupInfo(V2Group group) {
		
	}

	@Override
	public void OnMoveUserToGroup(int groupType, V2Group srcGroup,
			V2Group desGroup, V2User u) {
		
	}

	@Override
	public void OnAcceptInviteJoinGroup(int groupType, long groupId,
			long nUserID) {
		
	}

	@Override
	public void OnRefuseInviteJoinGroup(GroupQualicationJNIObject obj) {
		
	}

	@Override
	public void OnGetGroupFileInfo(V2Group group, List<FileJNIObject> list) {
		
	}

	@Override
	public void OnDelGroupFile(V2Group group, List<FileJNIObject> list) {
		
	}

	@Override
	public void OnAcceptApplyJoinGroup(V2Group group) {
		
	}
	

	public void OnApplyJoinGroup(V2Group group, V2User user, String reason) {
		
	}
	
	
	public void OnSearchCrowdCallback(List<V2Group> list) {
		
	}

	@Override
	public void OnAddGroupFile(V2Group group, List<FileJNIObject> list) {
		
	}

	@Override
	public void OnKickGroupUser(int groupType, long groupId, long nUserId) {
		
	}

	@Override
	public void OnGroupWBoardNotification(V2Document doc, DocOpt opt) {
		
	}

	

	@Override
	public void OnRefuseApplyJoinGroup(V2Group parseSingleCrowd,
			String reason) {
		
	}

	
	@Override
	public void OnJoinGroupError(int eGroupType, long nGroupID, int nErrorNo) {
		
	}

}
