package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.V2.jni.callback.WBRequestCallback;

public class WBRequest {
	private static WBRequest mWBRequest;
	private List<WeakReference<WBRequestCallback>> mCallBacks = new ArrayList<WeakReference<WBRequestCallback>>();

	private WBRequest() {
		mCallBacks = new ArrayList<WeakReference<WBRequestCallback>>();
	}

	public static synchronized WBRequest getInstance() {
		if (mWBRequest == null) {
			synchronized (WBRequest.class) {
				if (mWBRequest == null) {
					mWBRequest = new WBRequest();
					if (!mWBRequest.initialize(mWBRequest)) {
						throw new RuntimeException("can't initilaize WBRequest");
					}
				}
			}
		}
		return mWBRequest;
	}

	/**
	 * 添加自定义的回调，监听接收到的服务信令
	 * 
	 * @param callback
	 */
	public void addCallbacks(WBRequestCallback callback) {
		mCallBacks.add(new WeakReference<WBRequestCallback>(callback));
	}

	/**
	 * 移除自定义添加的回调
	 * 
	 * @param callback
	 */
	public void removeCallback(WBRequestCallback callback) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<WBRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				if (wf.get() == callback) {
					mCallBacks.remove(wf);
					return;
				}
			}
		}
	}

	public native boolean initialize(WBRequest request);

	public native void unInitialize();

	/**
	 * 白板激活一页
	 * 
	 * @param nUserId
	 * @param szWBoardID
	 *            白板ID
	 * @param nPageID
	 *            激页ID
	 * @param nIndex
	 * @param bNotify
	 *            激活页是否通知其它成员
	 */
	public native void DocShareActivePage(long nUserId, String szWBoardID, int nPageId, int nIndex, boolean bNotify);

	/**
	 * 白板添加一页
	 * 
	 * @param szWBoardID
	 *            ID白板ID
	 * @param nActive
	 *            是否激活所添中的页
	 */
	// public native void AddPage(String szWBoardID, boolean nActive);

	// public native void downLoadPageDoc(String bowardid, int pageid);

	/**
	 * 取消文档打印
	 * 
	 * @param szWBoardID
	 *            ID白板ID
	 */
	// public native void CanclePrint(String szWBoardID);

	// private void OnRecvAddWBoardData(String szWBoardID, int nPageID, String
	// szDataID, String szData) {
	// for (int i = 0; i < mCallBacks.size(); i++) {
	// WeakReference<WBRequestCallback> wf = mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnRecvAddWBoardDataCallback(szWBoardID, nPageID, szDataID,
	// szData);
	// }
	// }
	// }
	//
	// private void OnRecvAppendWBoardData(String szWBoardID, int nPageID,
	// String szDataID, String szData) {
	// for (int i = 0; i < mCallBacks.size(); i++) {
	// WeakReference<WBRequestCallback> wf = mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnRecvAppendWBoardDataCallback(szWBoardID, nPageID, szDataID,
	// szData);
	// }
	// }
	// }
	//
	// private void OnRecvChangeWBoardData(String szWBoardID, int nPageID,
	// String szDataID, String szData) {
	// for (int i = 0; i < mCallBacks.size(); i++) {
	// WeakReference<WBRequestCallback> wf = mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnRecvChangeWBoardData(szWBoardID, nPageID, szDataID, szData);
	// }
	// }
	// }
	//
	// private void OnWBoardDataRemoved(String szWBoardID, int nPageID, String
	// szDataID) {
	// for (int i = 0; i < mCallBacks.size(); i++) {
	// WeakReference<WBRequestCallback> wf = mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnWBoardDataRemoved(szWBoardID, nPageID, szDataID);
	// }
	// }
	// }

	private void OnWBoardPageAdded(String szWBoardID, int nPageID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<WBRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnWBoardAddPageCallback(szWBoardID, nPageID);
			}
		}
	}

	// private void OnWBoardDeletePage(String szWBoardID, int nPageID) {
	// for (int i = 0; i < mCallBacks.size(); i++) {
	// WeakReference<WBRequestCallback> wf = mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnWBoardDeletePage(szWBoardID, nPageID);
	// }
	// }
	// }

	private void OnWBoardPageActive(long nUserID, String szWBoardID, int nPageID, int index) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<WBRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnWBoardActivePageCallback(nUserID, szWBoardID, nPageID);
			}
		}
	}

	private void OnWBoardPageListReport(String szWBoardID, String szPageData, int nPageID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<WBRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnWBoardPageListCallback(szWBoardID, szPageData, nPageID);
			}
		}
	}

	private void OnWBoardPageBackground(String szWBoardID, int nPageID, String szFileName, int result) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<WBRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnWBoardDocDisplayCallback(szWBoardID, nPageID, szFileName, result);
			}
		}
	}

	// private void OnGetPersonalSpaceDocDesc(long id, String xml) {
	// for (int i = 0; i < mCallBacks.size(); i++) {
	// WeakReference<WBRequestCallback> wf = mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnGetPersonalSpaceDocDesc(id, xml);
	// }
	// }
	// }
	//
	// private void OnDataBegin(String szWBoardID) {
	// for (int i = 0; i < mCallBacks.size(); i++) {
	// WeakReference<WBRequestCallback> wf = mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnDataBegin(szWBoardID);
	// }
	// }
	// }
	//
	// private void OnDataEnd(String szWBoardID) {
	// for (int i = 0; i < mCallBacks.size(); i++) {
	// WeakReference<WBRequestCallback> wf = mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnDataEnd(szWBoardID);
	// }
	// }
	// }
}
