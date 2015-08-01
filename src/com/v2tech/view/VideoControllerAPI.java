package com.v2tech.view;

import com.v2tech.vo.Live;

public interface VideoControllerAPI {

	
	/**
	 * Post new message to current video
	 * @param str
	 */
	public void  addNewMessage(String str);
	
	
	/**
	 * Add new window to Current Pager
	 * @param l
	 * @return null for can not add more, match max limitation
	 */
	public VideoOpt addNewVideoWindow(Live l);
	
	
	/**
	 * 
	 * @return
	 */
	public int getVideoWindowNums();
	
}
