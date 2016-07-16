package com.v2tech.map;

import com.v2tech.vo.Live;
import com.v2tech.vo.Watcher;

public class Marker {

	protected Live live;
	
	protected Watcher watcher;
	
	protected int resId;
	
	protected Object ntObj;
	
	public Marker() {
	}

	public Marker(Live live) {
		super();
		this.live = live;
	}
	
	

	public Marker(Watcher watcher) {
		super();
		this.watcher = watcher;
	}

	public Live getLive() {
		return live;
	}

	public void setLive(Live live) {
		this.live = live;
	}

	public Watcher getWatcher() {
		return watcher;
	}

	public void setWatcher(Watcher watcher) {
		this.watcher = watcher;
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public Object getNtObj() {
		return ntObj;
	}

	public void setNtObj(Object ntObj) {
		this.ntObj = ntObj;
	}

	
	
}
