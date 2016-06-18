package com.v2tech.map.baidu;

import com.baidu.mapapi.model.LatLng;
import com.v2tech.map.LocationParameter;
import com.v2tech.map.MapLocation;

public class BaiduLocation extends MapLocation {
	
	private static BaiduLocationParameter parameter;
	
	LatLng ll;
	int level;
	
	

	public BaiduLocation(LatLng ll) {
		super();
		this.ll = ll;
		this.level = ((BaiduLocationParameter)getParameter()).getLevel();
	}

	public BaiduLocation(LatLng ll, int level) {
		super();
		this.ll = ll;
		this.level = level;
	}
	

	public  double getLat() {
		return ll.latitude;
	}
	
	public  double getLng() {
		return ll.longitude;
	}
	
	
	public LocationParameter getParameter() {
		if (parameter == null) {
			parameter = new BaiduLocationParameter();
		}
		return parameter;
	}
}
