package com.v2tech.map.baidu;

import android.os.Bundle;

import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.v2tech.map.Marker;

import java.io.Serializable;

public class BaiduMarker extends Marker {
	
	
	private com.baidu.mapapi.map.Marker m;
	
	public OverlayOptions oo;

	public Overlay overylay;
	

	public BaiduMarker() {
		super();
	}

	public BaiduMarker(Serializable ntObj, double lat, double lng) {
		super(ntObj, lat, lng);
	}

	public BaiduMarker(com.baidu.mapapi.map.Marker m) {
		super();
		this.m = m;
	}

}
