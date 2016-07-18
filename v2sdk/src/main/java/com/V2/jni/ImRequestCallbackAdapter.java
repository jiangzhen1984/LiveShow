package com.V2.jni;

import java.util.List;

import com.V2.jni.callback.ImRequestCallback;
import com.V2.jni.ind.V2User;

public abstract class ImRequestCallbackAdapter implements ImRequestCallback {



	@Override
	public void OnLogoutCallback(int nType) {

	}

	@Override
	public void OnConnectResponseCallback(int nResult) {

	}



	@Override
	public void OnUserStatusUpdatedCallback(long nUserID, int nType,
			int nStatus, String szStatusDesc) {

	}

	@Override
	public void OnChangeAvatarCallback(int nAvatarType, long nUserID,
			String AvatarName) {

	}

	@Override
	public void OnModifyCommentNameCallback(long nUserId, String sCommmentName) {

	}



	/**
	 * Notify all groups and users are loaded
	 * @return
	 */
	public void OnGroupsLoaded() {
		
	}

	@Override
	public void OnLoginCallback(long nUserID, int nStatus, int nResult,
			long serverTime, String sDBID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnUpdateBaseInfoCallback(long nUserID, String updatexml) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnHaveUpdateNotify(String updatefilepath, String updatetext) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnUpdateDownloadBegin(long filesize) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnUpdateDownloading(long size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnUpdateDownloadEnd(boolean error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnGetGroupsInfoBegin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnOfflineStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnOfflineEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnSignalDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnSearchUserCallback(String xmlinfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnImUserCreateValidateCode(int ret) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnImRegisterPhoneUser(int ret) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnImUpdateUserPwd(int ret) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void OnGuestRegister(String account, String password, int ret) {
		
	}
	
	
}
