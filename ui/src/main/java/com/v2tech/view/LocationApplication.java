package com.v2tech.view;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class LocationApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
	}

	
}
