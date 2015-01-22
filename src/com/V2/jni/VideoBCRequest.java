package com.V2.jni;

import com.V2.jni.util.V2Log;

public class VideoBCRequest {

	private static VideoBCRequest instance;

	private VideoBCRequest() {

	}

	public static VideoBCRequest getInstance() {
		if (instance == null) {
			instance = new VideoBCRequest();
			instance.initialize(instance);
		}
		return instance;
	}

	public native boolean initialize(VideoBCRequest instance);

	public native void unInitialize();

	public native void startLive();

	public native void stopLive();

	public native void updateGpsRequest(String gpsxml);

	public native void getNeiborhood(int meters);

	private void OnStartLive(long nUserID, String szUrl) {
		V2Log.e(nUserID+"  "+szUrl);
		url = szUrl;
	}
	
	public static String url = null;
	

	void OnStopLive(long nUserID) {

		url = null;
	}

	void OnGPSUpdated() {

	}

	void OnGetNeiborhood(String szXml) {
		V2Log.e(szXml);
	}
}
