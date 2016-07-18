package com.V2.jni;

import com.V2.jni.callback.ChatRequestCallback;
import com.V2.jni.ind.SendingResultJNIObjectInd;

public abstract class ChatRequestCallbackAdapter implements ChatRequestCallback {

	@Override
	public void OnRecvChatTextCallback(int eGroupType, long nGroupID,
			long nFromUserID, long nToUserID, long nTime, String szSeqID,
			String szXmlText) {
		
	}

	@Override
	public void OnRecvChatBinaryCallback(int eGroupType, long nGroupID,
			long nFromUserID, long nToUserID, long nTime, int binaryType,
			String messageId, String binaryPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnSendTextResultCallback(int eGroupType, long nGroupID,
			long nFromUserID, long nToUserID, String sSeqID, int nResult) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnSendBinaryResultCallback(int eGroupType, long nGroupID,
			long nFromUserID, long nToUserID, int mediaType, String sSeqID,
			int nResult) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnMonitorRecv(int eGroupType, String sSeqID, int nResult) {
		// TODO Auto-generated method stub
		
	}
	
}
