package com.v2tech.widget;

import com.V2.jni.util.V2Log;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class TouchSurfaceView extends SurfaceView {
	
	

	public TouchSurfaceView(Context context) {
		super(context);
	}

	public TouchSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TouchSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		V2Log.i("====get event "+ event);
		return true;
	}

	
	
	
}
