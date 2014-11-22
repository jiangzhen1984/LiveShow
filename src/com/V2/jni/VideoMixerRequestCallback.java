package com.V2.jni;


public interface VideoMixerRequestCallback {

	
	/**
	 * TODO add comment
	 * @param sMediaId
	 * @param layout
	 * @param width
	 * @param height
	 */
	public void OnCreateVideoMixerCallback(String sMediaId, int layout,
			int width, int height);

	public void OnDestroyVideoMixerCallback(String sMediaId);

	public void OnAddVideoMixerCallback(String sMediaId, long nDstUserId,
			String sDstDevId, int pos);

	public void OnDelVideoMixerCallback(String sMediaId, long nDstUserId,
			String sDstDevId);

}
