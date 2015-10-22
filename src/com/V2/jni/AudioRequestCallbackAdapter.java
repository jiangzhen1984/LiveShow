package com.V2.jni;

import com.V2.jni.callback.AudioRequestCallback;
import com.V2.jni.ind.AudioJNIObjectInd;

public abstract class AudioRequestCallbackAdapter implements
		AudioRequestCallback {

	@Override
	public void OnAudioChatInvite(String szSessionID, long nUserID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAudioChatAccepted(String szSessionID, long nUserID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAudioChatRefused(String szSessionID, long nUserID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAudioChatClosed(String szSessionID, long nUserID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnRecordStart(String RecordID, int nResult) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnRecordStop(String szRecordID, String szFileName, int nResult) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAudioMicCurrentLevel(int nValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAudioChating(String szSessionID, long nUserID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAudioGroupEnableAudio(int eGroupType, long nGroupID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAudioGroupOpenAudio(int eGroupType, long nGroupID,
			long nUserID, boolean bSpeaker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAudioGroupCloseAudio(int eGroupType, long nGroupID,
			long nUserID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAudioGroupUserSpeaker(int eGroupType, long nGroupID,
			long nUserID, boolean bSpeaker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAudioGroupMuteSpeaker(int eGroupType, long nGroupID,
			String sExecptUserIDXml) {
		// TODO Auto-generated method stub
		
	}


	
}
