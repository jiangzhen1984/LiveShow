package com.V2.jni.callback;

public interface ServerRecordRequestCallBack {

	public void OnAddRecordResponse(long nGroupID, String sVodXml);

	public void OnDelRecordResponse(long nGroupID, String sRecordID);

	public void OnNotifyVodShapshotResponse(String sRecordID, String sLocalDir);

	public void OnStartLive(long nGroupID, String sLiveBroadcastXml);

	public void OnStopLive(long nGroupID, long nUserID, String sRecordID);

}
