package com.v2tech.view;

import com.v2tech.vo.Live;

public interface VideoOpt {

	public void play(Live live);

	public void pause();

	public void resume();

	public void stop();
	
	public boolean isPlaying();
	
	public boolean isPause();

}
