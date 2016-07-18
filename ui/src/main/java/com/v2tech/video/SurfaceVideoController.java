package com.v2tech.video;

import v2av.VideoPlayer;
import android.view.SurfaceView;
import android.view.View;

public class SurfaceVideoController implements VideoController {
	
	private SurfaceView surfaceView;
	
	private VideoPlayer player;
	
	

	public SurfaceVideoController(SurfaceView surfaceView, VideoPlayer player) {
		super();
		this.surfaceView = surfaceView;
		this.player = player;
	}

	@Override
	public View getVideoView() {
		return surfaceView;
	}

	@Override
	public VideoPlayer getVideoPlayer() {
		return player;
	}

}
