package com.V2.jni.callback;

public interface InteractionRequestCallBack {

	void OnStartLive(long nUserID, String url);

	void OnStopLive(long nUserID);

	void OnGPSUpdated();

	/**
	 * 周围好友列表
	 * 
	 * @param resultXml
	 *            resultXml =
	 *            <neiborhood> <user userid = "" lon="" lat = "" distance = ""
	 *            url=""/> userid:long long; lon、lat、distance：double;url:char *;
	 *            <user userid = "" lon = "" lat = "" distance = "" url=""/> ...
	 *            </neiborhood>
	 */
	void OnGetNeiborhood(String resultXml);

	/**
	 * 视频评论
	 * 
	 * @param nUserID
	 *            谁的直播视频的评论信息
	 * @param szCommentXml
	 *            <comment> <content userid="评论者id" data=”评论信息"/> <content
	 *            userid="评论者id" data=”评论信息"/> ... </comment>
	 */
	void OnCommentVideo(long nUserID, String szCommentXml);

	/**
	 * 添加关注事件
	 * 
	 * @param nSrcUserID
	 * @param nDstUserID
	 */
	void OnAddConcern(long nSrcUserID, long nDstUserID);

	/**
	 * 取消关注事件
	 * 
	 * @param nSrcUserID
	 * @param nDstUserID
	 */
	void OnCancelConcernl(long nSrcUserID, long nDstUserID);

	/**
	 * 我的所有关注
	 * 
	 * @param szConcernsXml
	 *            <concerns> <user id = ''/> <user id = ''/> ... </concerns>
	 */
	void OnMyConcerns(String szConcernsXml);

	/**
	 * 我的粉丝
	 * 
	 * @param szFansXml
	 *            <fans> <user id = ''/> <user id = ''/> ... </fans>
	 */
	void OnMyFans(String szFansXml);

	/**
	 * 粉丝总数量
	 * 
	 * @param szFansXml
	 *            <fans> <user id = '' count='' /> <user id = '' count=''/> ...
	 *            </fans>
	 */
	void OnFansCount(String szFansXml);
}