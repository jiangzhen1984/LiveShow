package com.v2tech.x;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.v2tech.v2liveshow.R;

public class TestActivity extends Activity {

	
	private FrameLayout fl;
	private MapView mMapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_activity_x);
		fl = (FrameLayout) findViewById(R.id.main_map);
		
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.compassEnabled(true);
		mapOptions.scaleControlEnabled(true);
		mapOptions.zoomControlsEnabled(false);
		mapOptions.rotateGesturesEnabled(true);
		mMapView = new MapView(this, mapOptions);
		
		FrameLayout.LayoutParams flpar = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		
		fl.addView(mMapView, flpar);
		
		findViewById(R.id.main_widget).bringToFront();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
	
}
