package com.V2.jni;


public class ServerRecordRequest {
	
	private static ServerRecordRequest instance;
	
	public static synchronized ServerRecordRequest getInstance() {
		if (instance == null) {
			instance = new ServerRecordRequest();
		}
		return instance;
	}
	
	public native boolean initialize(ServerRecordRequest request);

	public native void unInitialize();

	
	public native void startServerRecord(String name, String name1);
	
	
	public native void  stopServerRecord();

	
	
	public native void  delServerRecord(long id, String name);
	
	
	public native void  downConfVodSnapshot(long id, String str1, String str2);
	
	public native void startLive(String str1, String str2);


	public native void stopLive();
	
	public void OnAddRecordResponse(long id, String str) {
		
	}
	
	public void OnOnDelRecordResponse(long id, String str) {
		
	}

	
	public void  OnNotifyVodShapshotResponse( String str,  String str1) {
		
	}


	
	public void  OnStartLive(long str1, String str2) {
		
	}
	public void OnStopLive(long l1, long le, String str) {
		
	}
	


}
