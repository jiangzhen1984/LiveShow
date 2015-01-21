package com.V2.jni;

import com.V2.jni.util.V2Log;

public class VideoBCRequest {

	private static VideoBCRequest instance;

	private VideoBCRequest() {

	}

	public static VideoBCRequest getInstance() {
		if (instance == null) {
			instance = new VideoBCRequest();
		}
		return instance;
	}

	//public native boolean Initialize(VideoBCRequest instance);

	public native void UnInitialize();

	public native void StartLive();

	public native void StopLive();

	public native void UpdateGpsRequest(String gpsxml);

	public native void GetNeiborhood(int meters);

	private void OnStartLive(long nUserID, String szUrl) {

	}

	void OnStopLive(long nUserID) {

	}

	void OnGPSUpdated() {

	}

	void OnGetNeiborhood(String szXml) {
		V2Log.e(szXml);
	}
}
