package com.v2tech.map;

import java.io.Serializable;

public abstract class MapLocation implements Serializable {
	
	public abstract double getLat();
	
	public abstract double getLng();
	
	public abstract LocationParameter getParameter();

}
