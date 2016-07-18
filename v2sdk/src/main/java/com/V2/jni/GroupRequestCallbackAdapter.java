package com.V2.jni;

import java.util.List;

import com.V2.jni.callback.GroupRequestCallback;
import com.V2.jni.ind.V2Document;
import com.V2.jni.ind.V2Group;
import com.V2.jni.ind.V2User;

public abstract class GroupRequestCallbackAdapter implements
		GroupRequestCallback {

	
	@Override
	public void OnGetGroupUserInfoCallback(int groupType, long nGroupID,
			String sXml) {

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
	public void OnAcceptInviteJoinGroup(int groupType, long groupId,
			long nUserID) {
		
	}

	
	

	public void OnApplyJoinGroup(V2Group group, V2User user, String reason) {
		
	}
	
	
	public void OnSearchCrowdCallback(List<V2Group> list) {
		
	}

	

	@Override
	public void OnKickGroupUser(int groupType, long groupId, long nUserId) {
		
	}



	
	@Override
	public void OnJoinGroupError(int eGroupType, long nGroupID, int nErrorNo) {
		
	}





	@Override
	public void OnAddGroupUserInfoCallback(int groupType, long nGroupID,
			String sXml) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void onAddGroupInfo(int groupType, long nParentID, long nGroupID,
			String sXml) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnInviteJoinGroup(int groupType, String groupInfo,
			String userInfo, String additInfo) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnRefuseInviteJoinGroup(int groupType, long nGroupID,
			long nUserID, String reason) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnMoveUserToGroup(int groupType, long srcGroupID,
			long dstGroupID, long nUserID) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnApplyJoinGroup(int groupType, long nGroupID, String userInfo,
			String reason) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnAcceptApplyJoinGroup(int groupType, String sXml) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnRefuseApplyJoinGroup(int groupType, String sXml, String reason) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnGroupCreateWBoard(int eGroupType, long nGroupID,
			String szWBoardID, int nWhiteIndex) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnRenameGroupFile(int eGroupType, long nGroupID,
			String sFileID, String sNewName) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnWBoardDestroy(int eGroupType, long nGroupID, String szWBoardID) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnGroupCreateDocShare(int eGroupType, long nGroupID,
			String szWBoardID, String szFileName) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnSearchGroup(int eGroupType, String infoXml) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnModifyGroupInfo(int groupType, long nGroupID, String sXml) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnGetGroupInfo(int groupType, String sXml) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnDelGroupFile(int type, long nGroupId, String fileId) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnAddGroupFile(int eGroupType, long nGroupId, String sXml) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void OnGetGroupFileInfo(int groupType, long nGroupId, String sXml) {
		// TODO Auto-generated method stub
		
	}
	

	
	

}
