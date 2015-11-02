package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.V2.jni.callback.VideoMixerRequestCallback;

public class VideoMixerRequest {
	private List<WeakReference<VideoMixerRequestCallback>> mCallBacks;
	private static VideoMixerRequest mVideoMixerRequest = null;

	private VideoMixerRequest() {
		mCallBacks = new ArrayList<WeakReference<VideoMixerRequestCallback>>();
	}

	public static synchronized VideoMixerRequest getInstance() {
		if (mVideoMixerRequest == null) {
			synchronized (VideoMixerRequest.class) {
				if (mVideoMixerRequest == null) {
					mVideoMixerRequest = new VideoMixerRequest();
					if (!mVideoMixerRequest.initialize(mVideoMixerRequest)) {
						throw new RuntimeException("can't initilaize VideoMixerRequest");
					}
				}
			}
		}
		return mVideoMixerRequest;
	}

	/**
	 * 添加自定义的回调，监听接收到的服务信令
	 * 
	 * @param callback
	 */
	public void addCallbacks(VideoMixerRequestCallback callback) {
		mCallBacks.add(new WeakReference<VideoMixerRequestCallback>(callback));
	}

	/**
	 * 移除自定义添加的回调
	 * 
	 * @param callback
	 */
	public void removeCallback(VideoMixerRequestCallback callback) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoMixerRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				if (wf.get() == callback) {
					mCallBacks.remove(wf);
					return;
				}
			}
		}
	}

	public native boolean initialize(VideoMixerRequest instance);

	public native void unInitialize();
	
	
	public native void delVideoMixerDevID(String str, long id, String str1);


	// public native void createVideoMixer(String szMediaId, int layout, int
	// width, int height);

	// public native void destroyVideoMixer(String szMediaId);

	// public native void addVideoMixerDevID(String szMediaId, long dstUserId,
	// String dstDevId, int pos);

	// public native void delVideoMixerDevID(String szMediaId, long dstUserId,
	// String dstDevId);

	private void OnVideoMixerCreate(String sMediaId, int layout, int width, int height) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoMixerRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnCreateVideoMixerCallback(sMediaId, layout, width, height);
			}
		}
	}

	private void OnVideoMixerDestroy(String sMediaId) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoMixerRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnDestroyVideoMixerCallback(sMediaId);
			}
		}
	}

	private void OnVideoMixerAddDevID(String sMediaId, long nDstUserId, String sDstDevId, int pos) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoMixerRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnAddVideoMixerCallback(sMediaId, nDstUserId, sDstDevId, pos);
			}
		}
	}

	private void OnVideoMixerDelDevID(String sMediaId, long nDstUserId, String sDstDevId) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VideoMixerRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnDelVideoMixerCallback(sMediaId, nDstUserId, sDstDevId);
			}
		}
	}
}
