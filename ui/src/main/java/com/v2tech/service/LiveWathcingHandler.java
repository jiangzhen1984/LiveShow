package com.v2tech.service;

import com.v2tech.vo.Live;
import com.v2tech.vo.User;

public interface LiveWathcingHandler {
	
	
	public void onUserWatched(Live l, User user);
	
	
	public void onWatcherLeaved(Live l, User user);

}
