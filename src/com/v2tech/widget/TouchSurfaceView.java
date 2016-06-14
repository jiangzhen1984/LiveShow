package com.v2tech.widget;

import v2av.VideoPlayer;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class TouchSurfaceView extends SurfaceView {
	
	
	private VideoPlayer playerController;

	public TouchSurfaceView(Context context) {
		super(context);
		playerController = new VideoPlayer(6);
	}

	public TouchSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		playerController = new VideoPlayer(6);
	}

	public TouchSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		playerController = new VideoPlayer(6);
	}


	public VideoPlayer getPlayerController() {
		playerController.SetSurface(getHolder());
		return playerController;
	}
	

	
	
}
