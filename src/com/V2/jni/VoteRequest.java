package com.V2.jni;

import android.content.Context;

import com.V2.jni.util.V2Log;

public class VoteRequest {

	private static VoteRequest VoteRequest;

//	private List<WeakReference<VoteRequestCallback>> callbacks;

	private VoteRequest(Context context) {
//		callbacks = new ArrayList<WeakReference<VoteRequestCallback>>();
	};

	public static synchronized VoteRequest getInstance(Context context) {
		if (VoteRequest == null) {
			VoteRequest = new VoteRequest(context);
			if (!VoteRequest.initialize(VoteRequest)) {
				V2Log.e(" can't  initialize imrequest ");
				throw new RuntimeException("can't initilaize imrequest");
			}
		}
		return VoteRequest;
	}

	public static synchronized VoteRequest getInstance() {
		if (VoteRequest == null) {
			VoteRequest = new VoteRequest(null);
			if (!VoteRequest.initialize(VoteRequest)) {
				V2Log.e(" can't  initialize imrequest ");
				throw new RuntimeException("can't initilaize imrequest");
			}
		}
		return VoteRequest;
	}

	/**
	 * 
	 * @param callback
	 */
//	public void addCallback(VoteRequestCallback callback) {
//		this.callbacks.add(new WeakReference<VoteRequestCallback>(callback));
//	}

	public native boolean initialize(VoteRequest request);

	public native void unInitialize();
	
	//创建投票
	public native void createVote(String sVoteID, String sVoteSubject, int eVoteStatus, 
			int nTotalUserCount, String szQuestionXml, int nLen);
	//删除投票
	public native void destroyVote(String sVoteID);
	//修改投票内容
	public native void modifyVoteContent(String sVoteID, String sVoteSubject, String szQuestionXml,
			int nLen);
	//修改投票状态
	public native void modifyVoteStatus(String sVoteID, int eVoteStatus);
	//修改投票总人数
	public native void modifyVoteTotalUserCount(String sVoteID, int nTotalUserCount);
	//单个投票
	public native void vote(String sVoteID, String sAnswer);

	
	
	//创建投票结果,状态：未开始、进行中、已结束、已发布
	private void OnCreateVote(String sVoteID, String sVoteSubject, int eVoteStatus, 
	int nTotalUserCount, String szQuestionXml, int nLen){};
		
	//删除投票结果
	private void OnDestroyVote(String sVoteID){};

	//修改投票内容结果
	private void OnModifyVote(String sVoteID, String sVoteSubject, String szQuestionXml, int nLen){};

	//修改投票状态结果
	private void OnModifyVoteStatus(String sVoteID, int eVoteStatus){};

	//修改投票总人数
	private void OnModifyVoteTotalUserCount(String sVoteID, int nTotalUserCount){};

	//单个投票结果
	private void OnVote(String sVoteID, long nUserID, String sAnswer){};
}
