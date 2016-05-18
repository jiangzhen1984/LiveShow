package com.v2tech.map;

public interface MapAPI {
	
	
	public void addMarker(Marker marker);
	
	
	public Marker buildMarker(Object obj);
	
	
	public void updateMap(Updater updater);
	
	
	public Updater buildUpater(Object obj);
	
	
	public MapLocation buildLocation(Object obj);
	
	
	public void startLocate(LocationParameter parameter);
	
	
	public void stopLocate(LocationParameter parameter);
	
	public LocationParameter buildParameter(Object obj);
	
	
	public void animationSearch(String text);
	
	
	public void registerMakerListener(MarkerListener listener);

}
