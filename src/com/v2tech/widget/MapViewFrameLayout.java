package com.v2tech.widget;

import com.baidu.mapapi.map.MapView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MapViewFrameLayout extends FrameLayout {

	private MapView mMapView;

	public MapViewFrameLayout(Context context) {
		super(context);
	}

	public MapViewFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MapViewFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void init() {
		this.addView(mMapView, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT));
	}

	public void setMapView(MapView mapView) {
		this.mMapView = mapView;
		init();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		mMapView.requestDisallowInterceptTouchEvent(true);
		return false;
	}
	
	

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

}
