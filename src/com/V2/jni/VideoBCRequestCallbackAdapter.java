package com.V2.jni;

import java.util.List;

import com.V2.jni.ind.V2Live;

public class VideoBCRequestCallbackAdapter implements VideoBCRequestCallback {

	public VideoBCRequestCallbackAdapter() {
	}

	@Override
	public void OnStartLive(long nUserID, String szUrl) {

	}

	@Override
	public void OnStopLive(long nUserID) {

	}

	@Override
	public void OnGPSUpdated() {

	}

	@Override
	public void OnCommentVideo(long id, String comments) {

	}

	@Override
	public void OnGetNeiborhood(List<V2Live> liveList) {

	}

}
