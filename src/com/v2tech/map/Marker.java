package com.v2tech.map;

import com.v2tech.vo.Live;
import com.v2tech.vo.Watcher;

public class Marker {

	protected Live live;
	
	protected Watcher watcher;
	
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

	
	
}
