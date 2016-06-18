package com.v2tech.map.baidu;

import java.lang.ref.WeakReference;

import android.content.Context;

import com.v2tech.map.LocationParameter;

public class BaiduLocationParameter extends LocationParameter {
	
	public static final boolean DEFAULT_GPS_ENABLE = true;
	
	public static final String DEFAULT_COOR_TYPE = "bd09ll";
	
	public static final int DEFAULT_INTERVAL = 15000;
	
	public static final boolean DEFAULT_ENABLE_SELF_LOCATION = true;
	
	public static final int DEFAULT_ZOOM_LEVEL = 12;
	
	private WeakReference<Context> ctx;
	
	private boolean enableGps;
	
	private String coorType;
	
	private int interval;
	
	private boolean enableSelfLocation;
	
	private int level;
	
	public BaiduLocationParameter(Context ctx) {
		this.ctx = new WeakReference<Context>(ctx);
		enableGps = DEFAULT_GPS_ENABLE;
		coorType = DEFAULT_COOR_TYPE;
		interval = DEFAULT_INTERVAL;
		enableSelfLocation = DEFAULT_ENABLE_SELF_LOCATION;
		this.level = DEFAULT_ZOOM_LEVEL;
	}
	
	

	public BaiduLocationParameter() {
		this(null);
	}

	public boolean isEnableGps() {
		return enableGps;
	}

	public void setEnableGps(boolean enableGps) {
		this.enableGps = enableGps;
	}

	public String getCoorType() {
		return coorType;
	}

	public void setCoorType(String coorType) {
		this.coorType = coorType;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public Context getContext() {
		if (this.ctx != null) {
			return ctx.get();
		}
		return null;
	}



	public boolean isEnableSelfLocation() {
		return enableSelfLocation;
	}


	public  void enableMyLococation(boolean enable) {
		this.enableSelfLocation = enable;
	}



	public int getLevel() {
		return level;
	}



	public void setLevel(int level) {
		this.level = level;
	}
	

	
}
