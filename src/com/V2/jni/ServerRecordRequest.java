package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import com.V2.jni.callback.ServerRecordRequestCallBack;

/**
 * 该类还没有被使用
 * @author 
 *
 */
public class ServerRecordRequest {
	private List<WeakReference<ServerRecordRequestCallBack>> mCallBacks;
	private static ServerRecordRequest mServerRecordRequest;

	private ServerRecordRequest() {
		mCallBacks = new ArrayList<WeakReference<ServerRecordRequestCallBack>>();
	}

	public static synchronized ServerRecordRequest getInstance() {
		if (mServerRecordRequest == null) {
			mServerRecordRequest = new ServerRecordRequest();
//			if (!mServerRecordRequest.initialize(mServerRecordRequest)) {
//				throw new RuntimeException("can't initilaize imrequest");
//			}
		}
		return mServerRecordRequest;
	}
	
	
	public native boolean initialize(ServerRecordRequest intance);

	/**
	 * 添加自定义的回调，监听接收到的服务信令
	 * 
	 * @param callback
	 */
	public void addCallback(ServerRecordRequestCallBack callback) {
		this.mCallBacks.add(new WeakReference<ServerRecordRequestCallBack>(callback));
	}

	/**
	 * 移除自定义添加的回调
	 * 
	 * @param callback
	 */
	public void removeCallback(ServerRecordRequestCallBack callback) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ServerRecordRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				if (wf.get() == callback) {
					mCallBacks.remove(wf);
					return;
				}
			}
		}
	}

	public native void unInitialize();

	public native void startServerRecord(String sRecordID, String sRecordName);

	public native void stopServerRecord();

	public native void delServerRecord(long nGroupID, String sRecordID);

	public native void downConfVodSnapshot(long nGroupID, String sVodID, String sVodSnapshotUrl);
	
	
	public native void startServerRecord(boolean b1, String sRecordID);

	public native void startLive(String sRecordID, String sRecordName);

	public native void stopLive();

	private void OnAddRecordResponse(long nGroupID, String sVodXml) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ServerRecordRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnAddRecordResponse(nGroupID, sVodXml);
			}
		}
	};

	private void OnDelRecordResponse(long nGroupID, String sRecordID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ServerRecordRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnDelRecordResponse(nGroupID, sRecordID);
			}
		}
	};

	private void OnNotifyVodShapshotResponse(String sRecordID, String sLocalDir) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ServerRecordRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnNotifyVodShapshotResponse(sRecordID, sLocalDir);
			}
		}
	};

	private void OnStartLive(long nGroupID, String sLiveBroadcastXml) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ServerRecordRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnStartLive(nGroupID, sLiveBroadcastXml);
			}
		}
	};

	private void OnStopLive(long nGroupID, long nUserID, String sRecordID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ServerRecordRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnStopLive(nGroupID, nUserID, sRecordID);
			}
		}
	};
}
