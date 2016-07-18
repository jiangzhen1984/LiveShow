package com.V2.jni.callback;

public interface VoteRequestCallBack {

	/**
	 * 创建投票结果,状态：未开始、进行中、已结束、已发布
	 * 
	 * @param sVoteID
	 * @param sVoteSubject
	 * @param eVoteStatus
	 * @param nTotalUserCount
	 * @param szQuestionXml
	 * @param nLen
	 */
	public void OnCreateVote(String sVoteID, String sVoteSubject,
			int eVoteStatus, int nTotalUserCount, String szQuestionXml, int nLen);

	/**
	 * 删除投票结果
	 * 
	 * @param sVoteID
	 */
	public void OnDestroyVote(String sVoteID);

	/**
	 * 修改投票内容结果
	 * 
	 * @param sVoteID
	 * @param sVoteSubject
	 * @param szQuestionXml
	 * @param nLen
	 */
	public void OnModifyVote(String sVoteID, String sVoteSubject,
			String szQuestionXml, int nLen);

	/**
	 * 修改投票状态结果
	 * 
	 * @param sVoteID
	 * @param eVoteStatus
	 */
	public void OnModifyVoteStatus(String sVoteID, int eVoteStatus);

	/**
	 * 修改投票总人数
	 * 
	 * @param sVoteID
	 * @param nTotalUserCount
	 */
	public void OnModifyVoteTotalUserCount(String sVoteID, int nTotalUserCount);

	/**
	 * 单个投票结果
	 * 
	 * @param sVoteID
	 * @param nUserID
	 * @param sAnswer
	 */
	public void OnVote(String sVoteID, long nUserID, String sAnswer);

}
