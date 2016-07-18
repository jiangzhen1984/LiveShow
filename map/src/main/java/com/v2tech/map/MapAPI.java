package com.v2tech.map;


import java.io.Serializable;

public interface MapAPI {
	
	
	public void addMarker(Marker marker);
	
	public void removeMarker(Marker marker);
	
	public Marker buildMarker(Serializable obj, double lat, double lng, int resId);
	
	public void clearMarkers();
	
	public void updateMap(Updater updater);
	
	
	public Updater buildUpater(Serializable obj);
	
	
	public MapLocation buildLocation(double lat, double lng);
	
	public void startLocate(LocationParameter parameter);
	
	
	public void stopLocate(LocationParameter parameter);
	
	public LocationParameter buildParameter(Object obj);
	
	
	public void animationSearch(String text);
	
	
	public void registerMakerListener(MarkerListener listener);
	
	
	public void getLocationName(MapLocation location, LocationNameQueryResponseListener listener);
	
	
	public MapLocation getMapCenter();
	
	
	public void showRoadMap(MapLocation from, MapLocation to);
	
	
	public void showRoadMap(Marker sm, MapLocation from, Marker tm, MapLocation to);
	
	
	public void addMapStatusListener(MapStatusListener listener);
	
	
	public void removeMapStatusListener(MapStatusListener listener);

}
