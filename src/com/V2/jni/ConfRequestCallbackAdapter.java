package com.V2.jni;

import java.util.List;

import com.V2.jni.callback.ConfRequestCallback;
import com.V2.jni.ind.V2Conference;
import com.V2.jni.ind.V2User;

public abstract class ConfRequestCallbackAdapter implements ConfRequestCallback {
	
	
	
	@Override
	public void OnEnterConfCallback(long nConfID, long nTime,
			String szConfData, int nJoinResult) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnConfMemberEnter(long nConfID, long nUserID, long nTime,
			String szUserInfos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnConfMemberExitCallback(long nConfID, long nTime, long nUserID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnKickConfCallback(int nReason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnConfNotify(String confXml, String creatorXml) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnNotifyChair(long userid, int type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnGrantPermissionCallback(long userid, int type, int status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnConfSyncOpenVideo(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnConfSyncCloseVideo(long gid, String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnConfSyncCloseVideoToMobile(long nDstUserID, String sDstMediaID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnConfSyncOpenVideoToMobile(String sSyncVideoMsgXML) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnConfChairChanged(long nConfID, long nChairID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnChangeSyncConfOpenVideoPos(long nDstUserID,
			String szDeviceID, String sPos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnConfMute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnGetConfVodList(long nGroupID, String sVodXmlList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnConfNotify(long nSrcUserID, String srcNickName, long nConfID,
			String subject, long nTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnConfNotifyEnd(long nConfID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnConfSyncOpenVideo(long nDstUserID, String sDstMediaID,
			int nPos) {
		// TODO Auto-generated method stub
		
	}

	public void onUserListNotify(int type, List<V2User> list) {
		
	}

}
