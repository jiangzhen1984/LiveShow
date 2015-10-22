package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.V2.jni.callback.VoteRequestCallBack;

/**
 * 该类还没有被使用
 * @author 
 *
 */
public class VoteRequest {
	private static VoteRequest VoteRequest;
	private List<WeakReference<VoteRequestCallBack>> mCallBacks;

	private VoteRequest() {
		mCallBacks = new ArrayList<WeakReference<VoteRequestCallBack>>();
	};

	public static synchronized VoteRequest getInstance() {
		if (VoteRequest == null) {
			VoteRequest = new VoteRequest();
			if (!VoteRequest.initialize(VoteRequest)) {
				throw new RuntimeException("can't initilaize imrequest");
			}
		}
		return VoteRequest;
	}

	/**
	 * 添加自定义的回调，监听接收到的服务信令
	 * 
	 * @param callback
	 */
	public void addCallback(VoteRequestCallBack callback) {
		this.mCallBacks.add(new WeakReference<VoteRequestCallBack>(callback));
	}

	/**
	 * 移除自定义添加的回调
	 * 
	 * @param callback
	 */
	public void removeCallback(VoteRequestCallBack callback) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VoteRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				if (wf.get() == callback) {
					mCallBacks.remove(wf);
					return;
				}
			}
		}
	}

	public native boolean initialize(VoteRequest request);

	public native void unInitialize();

	/**
	 * 创建投票
	 * 
	 * @param sVoteID
	 * @param sVoteSubject
	 * @param eVoteStatus
	 * @param nTotalUserCount
	 * @param szQuestionXml
	 * @param nLen
	 */
	public native void createVote(String sVoteID, String sVoteSubject, int eVoteStatus, int nTotalUserCount,
			String szQuestionXml, int nLen);

	/**
	 * 删除投票
	 * 
	 * @param sVoteID
	 */
	public native void destroyVote(String sVoteID);

	/**
	 * 修改投票内容
	 * 
	 * @param sVoteID
	 * @param sVoteSubject
	 * @param szQuestionXml
	 * @param nLen
	 */
	public native void modifyVoteContent(String sVoteID, String sVoteSubject, String szQuestionXml, int nLen);

	/**
	 * 修改投票状态
	 * 
	 * @param sVoteID
	 * @param eVoteStatus
	 */
	public native void modifyVoteStatus(String sVoteID, int eVoteStatus);

	/**
	 * 修改投票总人数
	 * 
	 * @param sVoteID
	 * @param nTotalUserCount
	 */
	public native void modifyVoteTotalUserCount(String sVoteID, int nTotalUserCount);

	/**
	 * 单个投票
	 * 
	 * @param sVoteID
	 * @param sAnswer
	 */
	public native void vote(String sVoteID, String sAnswer);

	private void OnCreateVote(String sVoteID, String sVoteSubject, int eVoteStatus, int nTotalUserCount,
			String szQuestionXml, int nLen) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VoteRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnCreateVote(sVoteID, sVoteSubject, eVoteStatus, nTotalUserCount, szQuestionXml, nLen);
			}
		}
	};

	private void OnDestroyVote(String sVoteID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VoteRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnDestroyVote(sVoteID);
			}
		}
	};

	private void OnModifyVote(String sVoteID, String sVoteSubject, String szQuestionXml, int nLen) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VoteRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnModifyVote(sVoteID, sVoteSubject, szQuestionXml, nLen);
			}
		}
	};

	private void OnModifyVoteStatus(String sVoteID, int eVoteStatus) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VoteRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnModifyVoteStatus(sVoteID, eVoteStatus);
			}
		}
	};

	private void OnModifyVoteTotalUserCount(String sVoteID, int nTotalUserCount) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VoteRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnModifyVoteTotalUserCount(sVoteID, nTotalUserCount);
			}
		}
	};

	private void OnVote(String sVoteID, long nUserID, String sAnswer) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<VoteRequestCallBack> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnVote(sVoteID, nUserID, sAnswer);
			}
		}
	};
}
