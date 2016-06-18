package com.v2tech.map;

import com.v2tech.service.MessageListener;

public interface MapAPI {
	
	
	public void addMarker(Marker marker);
	
	public void removeMarker(Marker marker);
	
	public Marker buildMarker(Object obj);
	
	public void clearMarkers();
	
	public void updateMap(Updater updater);
	
	
	public Updater buildUpater(Object obj);
	
	
	public MapLocation buildLocation(Object obj);
	
	
	public void startLocate(LocationParameter parameter);
	
	
	public void stopLocate(LocationParameter parameter);
	
	public LocationParameter buildParameter(Object obj);
	
	
	public void animationSearch(String text);
	
	
	public void registerMakerListener(MarkerListener listener);
	
	
	public void getLocationName(LSLocation location, MessageListener listener);
	
	
	public LSLocation getMapCenter();
	
	
	public void addMapStatusListener(MapStatusListener listener);
	
	
	public void removeMapStatusListener(MapStatusListener listener);

}
