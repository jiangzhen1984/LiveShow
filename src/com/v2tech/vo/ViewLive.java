package com.v2tech.vo;

import com.v2tech.map.Marker;

public class ViewLive {

	public Live live;
	
	public Marker marker;
	
	public boolean playing;
	
	public boolean showing;
	
	public int surfaveViewIdx;
	
	public boolean isowner;

	public ViewLive(Live live) {
		super();
		this.live = live;
	}

	public ViewLive(Live live, Marker marker) {
		super();
		this.live = live;
		this.marker = marker;
	}
	
	

}