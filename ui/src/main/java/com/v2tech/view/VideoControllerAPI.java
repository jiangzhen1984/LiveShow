package com.v2tech.view;

import com.v2tech.vo.Live;

public interface VideoControllerAPI {


	
	
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
