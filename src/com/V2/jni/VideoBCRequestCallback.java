package com.V2.jni;

import java.util.List;

import com.V2.jni.ind.V2Live;

public interface VideoBCRequestCallback {

	void OnStartLive(long nUserID, String szUrl);

	void OnStopLive(long nUserID);

	void OnGPSUpdated();

	void OnCommentVideo(long id, String comments);

	void OnGetNeiborhood(List<V2Live> liveList);
}
