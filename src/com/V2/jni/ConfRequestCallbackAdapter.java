package com.V2.jni;

import java.util.List;

import com.V2.jni.ind.V2Conference;
import com.V2.jni.ind.V2User;
import com.v2tech.vo.User;

public abstract class ConfRequestCallbackAdapter implements ConfRequestCallback {

	@Override
	public void OnEnterConfCallback(long nConfID, long nTime,
			String szConfData, int nJoinResult) {

	}

	@Override
	public void OnConfMemberEnterCallback(long nConfID, long nTime, V2User user) {

	}

	@Override
	public void OnConfMemberExitCallback(long nConfID, long nTime, long nUserID) {

	}

	@Override
	public void OnKickConfCallback(int nReason) {

	}

	@Override
	public void OnGrantPermissionCallback(long userid, int type, int status) {

	}

	@Override
	public void OnConfNotify(V2Conference v2conf,  V2User user) {

	}
	
	
	public void onUserListNotify(int type, List<V2User> list) {
		
	}

}
