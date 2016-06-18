package com.v2tech.map.baidu;

import com.v2tech.map.MapLocation;
import com.v2tech.map.MapStatus;

public class BaiduMapStatus extends MapStatus {

	com.baidu.mapapi.map.MapStatus ms;
	
	
	public BaiduMapStatus(com.baidu.mapapi.map.MapStatus ms) {
		super();
		this.ms = ms;
	}

	@Override
	public MapLocation getCenter() {
		return new BaiduLocation(ms.target);
	}

	@Override
	public int getZoom() {
		return 0;
	}


	
	
}
