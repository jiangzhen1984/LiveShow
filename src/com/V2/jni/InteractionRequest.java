package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.V2.jni.callback.InteractionRequestCallBack;

public class InteractionRequest {
	private static InteractionRequest mInteractionRequest;
	private List<WeakReference<InteractionRequestCallBack>> mCallBacks;

	private InteractionRequest() {
		mCallBacks = new ArrayList<WeakReference<InteractionRequestCallBack>>();
	};

	public static synchronized InteractionRequest getInstance() {
		if (mInteractionRequest == null) {
			mInteractionRequest = new InteractionRequest();
			if (!mInteractionRequest.initialize(mInteractionRequest)) {
				throw new RuntimeException("can't initilaize imrequest");
			}
		}
		return mInteractionRequest;
	}

	/**
	 * 添加自定义的回调，监听接收到的服务信令
	 * 
	 * @param callback
	 */
	public void addCallback(InteractionRequestCallBack callback) {
		this.mCallBacks.add(new WeakReference<InteractionRequestCallBack>(callback));
	}

	/**
	 * 移除自定义添加的回调
	 * 
	 * @param callback
	 */
	public void removeCallback(InteractionRequestCallBack callback) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<InteractionRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				if (wf.get() == callback) {
					mCallBacks.remove(wf);
					return;
				}
			}
		}
	}

	public native boolean initialize(InteractionRequest request);

	public native void unInitialize();

	public native void startLive();

	public native void stopLive();

	/**
	 * 地理位置坐标
	 * 
	 * @param gpsXml
	 *            <gps lon="经度" lat="维度“>
	 */
	public native void updateGpsRequest(String gpsXml);

	/**
	 * 周围好友查询
	 * 
	 * @param distance
	 *            距离
	 */
	public native void getNeiborhood(int distance);

	/**
	 * 特定范围的好友查询
	 * 
	 * @param stXml
	 *            strXml = <gps lon = "经度“ ,lat = "维度” distance = “范围”> //lat
	 *            lon:为double类型， distance 为int类型 单位（米）
	 */
	public native void GetNeiborhood_Region(String stXml);

	/**
	 * 更新观看状态
	 * 
	 * @param nUserID
	 * @param bwatch
	 */
	public native void UpdateWatchStatusRequest(long nUserID, boolean bwatch);

	/**
	 * 视频评论
	 * 
	 * @param nUserID
	 *            对谁的直播视频进行评论
	 * @param sData
	 *            评论信息
	 */
	public native void CommentVideo(long nUserID, String sData);

	/**
	 * 添加关注
	 * 
	 * @param nDstUserID
	 *            关注对象的ID
	 */
	public native void addConcern(long nDstUserID);

	/**
	 * 取消关注
	 * 
	 * @param nDstUserID
	 *            关注对象的ID
	 */
	public native void cancelConcern(long nDstUserID);

	/**
	 * 查看我关注了谁
	 */
	public native void getMyConcerns();

	/**
	 * 查看谁关注了我
	 */
	public native void getMyFans();

	public native void getFansCount();

	/**
	 * 粉丝数量
	 * 
	 * @param szUsersXml
	 *            szUsersXml =
	 *            <users> <user id = ''/> <user id = ''/> ... </users>
	 */
	public native void getFansCount(String szUsersXml);

	private void OnStartLive(long nUserID, String url) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<InteractionRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnStartLive(nUserID, url);
			}
		}
	};

	private void OnStopLive(long nUserID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<InteractionRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnStopLive(nUserID);
			}
		}
	};

	private void OnGPSUpdated() {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<InteractionRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnGPSUpdated();
			}
		}
	};

	private void OnGetNeiborhood(String resultXml) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<InteractionRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnGetNeiborhood(resultXml);
			}
		}
	};

	private void OnCommentVideo(long nUserID, String szCommentXml) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<InteractionRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnCommentVideo(nUserID, szCommentXml);
			}
		}
	};

	private void OnAddConcern(long nSrcUserID, long nDstUserID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<InteractionRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnAddConcern(nSrcUserID, nDstUserID);
			}
		}
	};

	private void OnCancelConcernl(long nSrcUserID, long nDstUserID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<InteractionRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnCancelConcernl(nSrcUserID, nDstUserID);
			}
		}
	};

	private void OnMyConcerns(String szConcernsXml) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<InteractionRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnMyConcerns(szConcernsXml);
			}
		}
	};

	private void OnMyFans(String szFansXml) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<InteractionRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnMyFans(szFansXml);
			}
		}
	};

	private void OnFansCount(String szFansXml) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<InteractionRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnFansCount(szFansXml);
			}
		}
	};

}
