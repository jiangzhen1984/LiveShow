package com.V2.jni.callback;

public interface WBRequestCallback {

	/**
	 * 收到白板页列表的回调
	 * 
	 * @param szWBoardID
	 * @param szPageData
	 * @param nPageID
	 */
	public void OnWBoardPageListCallback(String szWBoardID, String szPageData,
			int nPageID);

	/**
	 * 收到白板激活页的回调
	 * 
	 * @param nUserID
	 * @param szWBoardID
	 * @param nPageID
	 */
	public void OnWBoardActivePageCallback(long nUserID, String szWBoardID,
			int nPageID);

	/**
	 * 文档共享显示的回调
	 * 
	 * @param szWBoardID
	 * @param nPageID
	 * @param szFileName
	 * @param result
	 */
	public void OnWBoardDocDisplayCallback(String szWBoardID, int nPageID,
			String szFileName, int result);

	/**
	 * 收到白板添加页的回调
	 * 
	 * @param szWBoardID
	 * @param nPageID
	 */
	public void OnWBoardAddPageCallback(String szWBoardID, int nPageID);

	/**
	 * 收到添加白板数据的回调
	 * 
	 * @param szWBoardID
	 * @param nPageID
	 * @param szDataID
	 * @param szData
	 */
	public void OnRecvAddWBoardDataCallback(String szWBoardID, int nPageID,
			String szDataID, String szData);

	/**
	 * 收到更改白板数据的回调
	 * 
	 * @param szWBoardID
	 * @param nPageID
	 * @param szDataID
	 * @param szData
	 */
	public void OnRecvChangeWBoardData(String szWBoardID, int nPageID,
			String szDataID, String szData);

	/**
	 * 白板数据被删除的回调
	 * 
	 * @param szWBoardID
	 * @param nPageID
	 * @param szDataID
	 */
	public void OnWBoardDataRemoved(String szWBoardID, int nPageID,
			String szDataID);

	/**
	 * 获得保存服务器上文档信息回调
	 * 
	 * @param id
	 * @param xml
	 */
	public void OnGetPersonalSpaceDocDesc(long id, String xml);

	public void OnDataBegin(String szWBoardID);

	public void OnDataEnd(String szWBoardID);

	public void OnRecvAppendWBoardDataCallback(String szWBoardID, int nPageID,
			String szDataID, String szData);

	public void OnWBoardDeletePage(String szWBoardID, int nPageID);

}
