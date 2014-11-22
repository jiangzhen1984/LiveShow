package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.V2.jni.util.V2Log;

public class VideoMixerRequest {

	private List<WeakReference<VideoMixerRequestCallback>> mCallbacks;

	private static VideoMixerRequest mVideoMixerRequest = null;

	private VideoMixerRequest() {
		mCallbacks = new ArrayList<WeakReference<VideoMixerRequestCallback>>();
	}

	public static synchronized VideoMixerRequest getInstance() {
		if (mVideoMixerRequest == null) {
			mVideoMixerRequest = new VideoMixerRequest();
			if (!mVideoMixerRequest.initialize(mVideoMixerRequest)) {
				V2Log.e(" VideoMixerRequest initialize failed");
			}
		}
		return mVideoMixerRequest;
	}
	
	public void addCallbacks(VideoMixerRequestCallback cb) {
		if (cb == null) {
			throw new NullPointerException(" cb is null");
		}
		mCallbacks.add(new WeakReference<VideoMixerRequestCallback>(cb));
	}
	
	public void removeCallback(VideoMixerRequestCallback callback) {
		for (int i = 0; i < mCallbacks.size(); i++) {
			if (mCallbacks.get(i).get() == callback) {
				mCallbacks.remove(i);
				break;
			}
		}
	}
	

	public native boolean initialize(VideoMixerRequest instance);

	public native void unInitialize();

	public native void createVideoMixer(String szMediaId, int layout,
			int width, int height);

	public native void destroyVideoMixer(String szMediaId);

	public native void addVideoMixerDevID(String szMediaId, long dstUserId,
			String dstDevId, int pos);

	public native void delVideoMixerDevID(String szMediaId, long dstUserId,
			String dstDevId);

	private void OnCreateVideoMixer(String sMediaId, int layout, int width,
			int height) {
		V2Log.d("OnCreateVideoMixer--> sMediaId :" + sMediaId + "   " + layout + "  "
				+ width + "  " + height);

		for (WeakReference<VideoMixerRequestCallback> we : mCallbacks) {
			Object obj = we.get();
			if (obj != null) {
				VideoMixerRequestCallback cb = (VideoMixerRequestCallback) obj;
				cb.OnCreateVideoMixerCallback(sMediaId, layout, width, height);
			}
		}
	}

	private void OnDestroyVideoMixer(String sMediaId) {
		V2Log.d("OnDestroyVideoMixer-->" + sMediaId);
		for (WeakReference<VideoMixerRequestCallback> we : mCallbacks) {
			Object obj = we.get();
			if (obj != null) {
				VideoMixerRequestCallback cb = (VideoMixerRequestCallback) obj;
				cb.OnDestroyVideoMixerCallback(sMediaId);
			}
		}
	}

	private void OnAddVideoMixer(String sMediaId, long nDstUserId,
			String sDstDevId, int pos) {
		V2Log.d("OnAddVideoMixer-->" + sMediaId + "  " + nDstUserId + "  "
				+ sDstDevId + "  " + pos);
		for (WeakReference<VideoMixerRequestCallback> we : mCallbacks) {
			Object obj = we.get();
			if (obj != null) {
				VideoMixerRequestCallback cb = (VideoMixerRequestCallback) obj;
				cb.OnAddVideoMixerCallback(sMediaId, nDstUserId, sDstDevId, pos);
			}
		}
	}

	private void OnDelVideoMixer(String sMediaId, long nDstUserId,
			String sDstDevId) {
		V2Log.d("OnDelVideoMixer-->" + sMediaId + "   " + nDstUserId + "   "
				+ sDstDevId);
		for (WeakReference<VideoMixerRequestCallback> we : mCallbacks) {
			Object obj = we.get();
			if (obj != null) {
				VideoMixerRequestCallback cb = (VideoMixerRequestCallback) obj;
				cb.OnDelVideoMixerCallback(sMediaId, nDstUserId, sDstDevId);
			}
		}
	}

}
