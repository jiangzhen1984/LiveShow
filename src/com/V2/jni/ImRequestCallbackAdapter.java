package com.V2.jni;

import java.util.List;

import com.V2.jni.ind.V2User;

public abstract class ImRequestCallbackAdapter implements ImRequestCallback {

	@Override
	public void OnLoginCallback(long nUserID, int nStatus, int nResult , long serverTime) {

	}

	@Override
	public void OnLogoutCallback(int nType) {

	}

	@Override
	public void OnConnectResponseCallback(int nResult) {

	}

	@Override
	public void OnUpdateBaseInfoCallback(V2User user) {

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


	@Override
	public void OnSearchUserCallback(List<V2User> list) {
		
	}

	
	
}
