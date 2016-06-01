package com.v2tech.map.baidu;

import android.os.Bundle;

import com.baidu.mapapi.map.OverlayOptions;
import com.v2tech.map.Marker;
import com.v2tech.vo.Live;
import com.v2tech.vo.Watcher;

public class BaiduMaker extends Marker {
	
	
	private com.baidu.mapapi.map.Marker m;
	
	public OverlayOptions oo;

	public BaiduMaker() {
		super();
	}

	public BaiduMaker(Live live) {
		super(live);
	}

	public BaiduMaker(Watcher watcher) {
		super(watcher);
	}

	public BaiduMaker(com.baidu.mapapi.map.Marker m) {
		super();
		this.m = m;
	}

	@Override
	public Live getLive() {
		if (this.live == null) {
			Bundle b = m.getExtraInfo();
			if (b != null) {
				return (Live)b.get("live");
			} else {
				return null;
			}
		} else {
			return live;
		}
	}
	
	
	
	
	

}
