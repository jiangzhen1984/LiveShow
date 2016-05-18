package com.v2tech.service;

import com.V2.jni.ind.MessageInd;

public interface LiveMessageHandler {
	
	
	public void onAudioMessage(long liveId, long uid, int opt);
	
	
	public void onVdideoMessage(long liveId, long uid, int opt);
	
	
	public void onLiveMessage(long liveId, long uid, MessageInd ind);
	
	
	public void onP2PMessage(long uid, MessageInd ind);
}
