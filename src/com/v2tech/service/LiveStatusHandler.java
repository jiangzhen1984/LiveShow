package com.v2tech.service;

import com.v2tech.vo.Live;

public interface LiveStatusHandler {
	
	
	public void handleNewLivePushlishment(Live l);
	
	
	public void handleLiveFinished(Live l);

}
