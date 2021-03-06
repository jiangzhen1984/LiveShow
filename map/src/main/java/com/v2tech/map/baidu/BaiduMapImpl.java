package com.v2tech.map.baidu;

import android.content.Context;
import android.os.Bundle;
import android.util.LongSparseArray;

import com.V2.jni.util.V2Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.v2tech.map.LocationNameQueryResponseListener;
import com.v2tech.map.LocationParameter;
import com.v2tech.map.MapAPI;
import com.v2tech.map.MapLocation;
import com.v2tech.map.MapStatusListener;
import com.v2tech.map.Marker;
import com.v2tech.map.MarkerListener;
import com.v2tech.map.Updater;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class BaiduMapImpl implements MapAPI,
		BaiduMap.OnMapStatusChangeListener, BDLocationListener,
		OnGetGeoCoderResultListener, BaiduMap.OnMarkerClickListener, OnGetRoutePlanResultListener {
	
	private static final int GET_ADDS_NAME = 1;
	
	private LongSparseArray<WeakReference<LocationNameQueryResponseListener>> pendingListener = new LongSparseArray<WeakReference<LocationNameQueryResponseListener>>();
	
	private List<WeakReference<MapStatusListener>> statusListener = new ArrayList<WeakReference<MapStatusListener>>();
	
	//private WeakReference<MapView> mapView;

	public BaiduMap mapImpl;

	private LocationClient locationClient;
	private GeoCoder mSearchAPI;
	private RoutePlanSearch roadMapQueryAPI;
	
	private WalkingRouteOverlay  lastOverlay;
	private Marker startMarker;
	private Marker terminalMarker;
	

	// ///listener///
	private MarkerListener markerListener;

	public BaiduMapImpl(BaiduMap mapImpl, MapView mv) {
		super();
		this.mapImpl = mapImpl;
		mSearchAPI = GeoCoder.newInstance();
		mSearchAPI.setOnGetGeoCodeResultListener(this);
	//	mapView = new WeakReference<MapView>(mv);
		this.mapImpl.setOnMapStatusChangeListener(this);

		roadMapQueryAPI = RoutePlanSearch.newInstance();
		roadMapQueryAPI.setOnGetRoutePlanResultListener(this);
	}

	public BaiduMap getMapImpl() {
		return mapImpl;
	}

	public void setMapImpl(BaiduMap mapImpl) {
		this.mapImpl = mapImpl;
	}

	@Override
	public void addMarker(Marker marker) {
		MarkerOptions oo = null;
		BitmapDescriptor online = BitmapDescriptorFactory
				.fromResource(marker.getResId());

		Bundle bundle = new Bundle();
		bundle.putSerializable("ntobj", marker.getNtObj());
		oo = new MarkerOptions()
				.icon(online)
				.position(
						new LatLng(marker.getLat(), marker.getLng())).extraInfo(bundle);
		Overlay ol = mapImpl.addOverlay(oo);
		((BaiduMarker)marker).oo = oo;
		((BaiduMarker)marker).overylay = ol;
	}
	
	public void removeMarker(Marker marker) {
		((BaiduMarker)marker).overylay.remove();
	}

	
	public void clearMarkers() {
		mapImpl.clear();
	}
	
	
	@Override
	public void updateMap(Updater updater) {
		if (updater instanceof BaiduUpdater) {
			mapImpl.animateMapStatus(((BaiduUpdater) updater).u);
		} else {
			throw new RuntimeException(" updater is not BaiduUpdater instance");
		}
	}

	@Override
	public Marker buildMarker(Serializable obj, double lat, double lng, int resId) {
		return new BaiduMarker(obj, lat, lng, resId);
	}

	public Updater buildUpater(Serializable obj) {
		if (obj instanceof BaiduLocation) {
			BaiduLocation bl = ((BaiduLocation) obj);
			return new BaiduUpdater(MapStatusUpdateFactory.newLatLng(bl.ll));
		}

		return null;
	}


	public MapLocation buildLocation(double lat, double lng) {
		return new BaiduLocation(new LatLng(lat, lng));
	}

	@Override
	public void animationSearch(String text) {
		// FIXME update city first
		mSearchAPI.geocode(new GeoCodeOption().city("北京").address(text));
	}

	@Override
	public void startLocate(LocationParameter parameter) {
		if (!(parameter instanceof BaiduLocationParameter)) {
			throw new RuntimeException(
					" parameter is not BaiduLocationParameter instance");
		}
		if (locationClient != null && locationClient.isStarted()) {
			return;
		}
		BaiduLocationParameter bp = (BaiduLocationParameter) parameter;
		mapImpl.setMyLocationEnabled(bp.isEnableSelfLocation());
		if (mapImpl.isMyLocationEnabled()) {
			mapImpl.setMyLocationConfigeration(new MyLocationConfiguration(
					LocationMode.NORMAL, true, null));
		}
		locationClient = new LocationClient(bp.getContext());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(bp.isEnableGps());
		option.setCoorType(bp.getCoorType());
		option.setScanSpan(bp.getInterval());
		locationClient.setLocOption(option);
		locationClient.registerLocationListener(this);
		locationClient.start();
	}

	public void stopLocate(LocationParameter parameter) {
		if (locationClient != null) {
			locationClient.stop();
			locationClient.unRegisterLocationListener(this);
		}
		mapImpl.setMyLocationEnabled(false);
	}

	public LocationParameter buildParameter(Object obj) {
		if (obj instanceof Context) {
			return new BaiduLocationParameter((Context) obj);
		} else {
			return new BaiduLocationParameter();
		}
	}

	public void registerMakerListener(MarkerListener listener) {
		markerListener = listener;
		mapImpl.setOnMarkerClickListener(this);
	}
	
	
	public MapLocation getMapCenter() {
		LatLng tar = mapImpl.getMapStatus().target;
		return new BaiduLocation(tar); 
	}
	
	
	public void addMapStatusListener(MapStatusListener listener) {
		statusListener.add(new WeakReference<MapStatusListener>(listener));
	}
	
	
	public void removeMapStatusListener(MapStatusListener listener) {
		for (int i = 0; i < statusListener.size(); i++) {
			WeakReference<MapStatusListener> wr = statusListener.get(i);
			if (wr == null) {
				continue;
			}
			if (wr.get() == null) {
				statusListener.remove(i);
				i--;
				continue;
			}
			
			if (wr.get() == listener) {
				statusListener.remove(i);
				break;
			}
		}
	}
	

	class BaiduUpdater extends Updater {
		MapStatusUpdate u;

		public BaiduUpdater(MapStatusUpdate u) {
			super();
			this.u = u;
		}

	}

	@Override
	public boolean onMarkerClick(com.baidu.mapapi.map.Marker maker) {
		if (markerListener != null) {
			return markerListener
					.onMarkerClickedListener(new BaiduMarker(maker));
		}
		return false;
	}
	
	
	@Override
	public void showRoadMap(MapLocation from, MapLocation to) {
		showRoadMap(null, from, null, to);
	}
	
	@Override
	public void showRoadMap(Marker sm, MapLocation from, Marker tm, MapLocation to) {
		if (lastOverlay != null) {
			lastOverlay.removeFromMap();
		}
		PlanNode stNode = PlanNode.withLocation(new LatLng(from.getLat(), from.getLng()));
	    PlanNode enNode = PlanNode.withLocation(new LatLng(to.getLat(), to.getLng()));
		roadMapQueryAPI.walkingSearch((new WalkingRoutePlanOption())
                .from(stNode)
                .to(enNode));
		if (sm != null) {
			this.startMarker = sm;
		}
		if (tm != null) {
			this.terminalMarker = tm;
		}
	}
	

	// /////////////////////BaiduMap.OnMapStatusChangeListener

	@Override
	public void onMapStatusChange(MapStatus status) {
	}

	@Override
	public void onMapStatusChangeFinish(MapStatus status) {
		V2Log.i("map status changed from ===>>>>>" + status.target+"  to ===>"+ status.targetScreen);
		BaiduMapStatus bms = new BaiduMapStatus(status);
		for (int i = 0; i < statusListener.size(); i++) {
			WeakReference<MapStatusListener> wr = statusListener.get(i);
			if (wr.get() != null) {
				wr.get().onMapStatusUpdated(bms);
			}
			
		}
	}

	@Override
	public void onMapStatusChangeStart(MapStatus status) {
	}
	
	
	public void getLocationName(MapLocation location, LocationNameQueryResponseListener listener) {
		if (listener != null) {
			pendingListener.put(GET_ADDS_NAME, new WeakReference<LocationNameQueryResponseListener>(listener));
		}
		mSearchAPI.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(location.getLat(), location.getLng())));
	}

	// //////////////////BaiduMap.OnMapStatusChangeListener

	// /////////////////BDLocationListener
	@Override
	public void onReceiveLocation(BDLocation location) {
		LatLng ll = new LatLng(location.getLatitude(),
				location.getLongitude());
		MapLocation ml = new BaiduLocation(ll);
		if(mapImpl.isMyLocationEnabled()) {
			MyLocationData locData = new MyLocationData.Builder()
			.accuracy(location.getRadius())
			// 此处设置开发者获取到的方向信息，顺时针0-360
			.direction(100).latitude(location.getLatitude())
			.longitude(location.getLongitude()).build();
			mapImpl.setMyLocationData(locData);
		}
		
		for (int i = 0; i < this.statusListener.size(); i++) {
			WeakReference<MapStatusListener> wr = statusListener.get(i);
			if (wr.get() == null) {
				continue;
			}
			wr.get().onSelfLocationUpdated(ml);
		}
	}

	// /////////////////BDLocationListener

	// ////////////////////// OnGetGeoCoderResultListener
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			return;
		}
		mapImpl.animateMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult res) {
		WeakReference<LocationNameQueryResponseListener> wf = pendingListener.get(GET_ADDS_NAME);
		if (wf.get() == null) {
			V2Log.e("[ERROR] no listener for geo reverse search: " + res.getAddress());
			return;
		}
		wf.get().doNotification(res.getAddress());
		
	}
	// ////////////////////// OnGetGeoCoderResultListener
	
	// ////////////////////// OnGetRoutePlanResultListener
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
        	WalkingRouteLine route = result.getRouteLines().get(0);
        	RoadMapOverlay overlay = new RoadMapOverlay(mapImpl);
        	if (startMarker != null) {
        		overlay.startMarkerRes = startMarker.getResId();
        	}
        	if (terminalMarker != null) {
        		overlay.terminalMarkerRes = terminalMarker.getResId();
        	}
            lastOverlay = overlay;
            overlay.setData(route);
            overlay.addToMap();
            overlay.zoomToSpan();
        }
	}
	
	@Override
	public void onGetTransitRouteResult(
			com.baidu.mapapi.search.route.TransitRouteResult arg0) {

	}

	@Override
	public void onGetDrivingRouteResult(
			com.baidu.mapapi.search.route.DrivingRouteResult arg0) {

	}
	// ////////////////////// OnGetRoutePlanResultListener
}
