package com.v2tech.service;

import com.v2tech.vo.msg.VMessage;

public interface LiveMessageHandler {
	
	
	public void onAudioMessage(long liveId, long uid, int opt);
	
	
	public void onVdideoMessage(long liveId, long uid, int opt);
	
	
	public void onLiveMessage(long liveId, long uid, VMessage vm);
	
	
	public void onP2PMessage(VMessage vm);
}
