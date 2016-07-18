package com.V2.jni.callback;

public interface VideoMixerRequestCallback {

	public void OnCreateVideoMixerCallback(String sMediaId, int layout,
			int width, int height);

	public void OnDestroyVideoMixerCallback(String sMediaId);

	public void OnAddVideoMixerCallback(String sMediaId, long nDstUserId,
			String sDstDevId, int pos);

	public void OnDelVideoMixerCallback(String sMediaId, long nDstUserId,
			String sDstDevId);

}
