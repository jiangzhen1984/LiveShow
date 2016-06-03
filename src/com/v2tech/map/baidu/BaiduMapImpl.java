package com.v2tech.map.baidu;

import android.content.Context;
import android.os.Bundle;

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
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.v2tech.map.LocationParameter;
import com.v2tech.map.MapAPI;
import com.v2tech.map.MapLocation;
import com.v2tech.map.Marker;
import com.v2tech.map.MarkerListener;
import com.v2tech.map.Updater;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.Live;
import com.v2tech.vo.Watcher;

public class BaiduMapImpl implements MapAPI,
		BaiduMap.OnMapStatusChangeListener, BDLocationListener,
		OnGetGeoCoderResultListener, BaiduMap.OnMarkerClickListener {
	
	//private WeakReference<MapView> mapView;

	public BaiduMap mapImpl;

	private LocationClient locationClient;
	private GeoCoder mSearchAPI;

	// ///listener///
	private MarkerListener markerListener;

	public BaiduMapImpl(BaiduMap mapImpl, MapView mv) {
		super();
		this.mapImpl = mapImpl;
		mSearchAPI = GeoCoder.newInstance();
		mSearchAPI.setOnGetGeoCodeResultListener(this);
	//	mapView = new WeakReference<MapView>(mv);
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
		if (marker.getLive() != null) {
			BitmapDescriptor online = BitmapDescriptorFactory
					.fromResource(R.drawable.marker_live);

			Bundle bundle = new Bundle();
			bundle.putSerializable("live", marker.getLive());
			oo = new MarkerOptions()
					.icon(online)
					.position(
							new LatLng(marker.getLive().getLat(), marker
									.getLive().getLng())).extraInfo(bundle);
			mapImpl.addOverlay(oo);
		} else if (marker.getWatcher() != null) {
			BitmapDescriptor online = BitmapDescriptorFactory
					.fromResource(R.drawable.watcher_location);

			Bundle bundle = new Bundle();
			bundle.putSerializable("watcher", marker.getWatcher());
			oo = new MarkerOptions()
					.icon(online)
					.position(
							new LatLng(marker.getWatcher().getLat(), marker
									.getWatcher().getLng())).extraInfo(bundle);
			mapImpl.addOverlay(oo);

		}
		((BaiduMaker)marker).oo = oo;
	}
	
	public void removeMarker(Marker marker) {
//		OverlayOptions oo = ((BaiduMaker)marker).oo;
//		MapView mv = mapView.get();
//		if (mv != null) {
//			ViewGroupOverlay gp = mv.getOverlay();
//		}
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

	public Marker buildMarker(Object obj) {
		if (obj instanceof Live) {
			return new BaiduMaker((Live) obj);
		} else if (obj instanceof Watcher) {
			return new BaiduMaker((Watcher) obj);
		}
		return null;
	}

	public Updater buildUpater(Object obj) {
		if (obj instanceof BaiduLocation) {
			BaiduLocation bl = ((BaiduLocation) obj);
			return new BaiduUpdater(MapStatusUpdateFactory.newLatLngZoom(bl.ll,
					bl.level));
		}

		return null;
	}

	public MapLocation buildLocation(Object obj) {
		if (obj instanceof LatLng) {
			return new BaiduLocation((LatLng) obj);
		} else {
			throw new RuntimeException(" obj is not latlng instance");
		}
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
					.onMarkerClickedListener(new BaiduMaker(maker));
		}
		return false;
	}

	// /////////////////////BaiduMap.OnMapStatusChangeListener

	@Override
	public void onMapStatusChange(MapStatus status) {
	}

	@Override
	public void onMapStatusChangeFinish(MapStatus status) {
	}

	@Override
	public void onMapStatusChangeStart(MapStatus status) {
	}

	// //////////////////BaiduMap.OnMapStatusChangeListener

	// /////////////////BDLocationListener
	@Override
	public void onReceiveLocation(BDLocation location) {
		if (markerListener != null) {
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			markerListener.onLocated(new BaiduLocation(ll));
			if(mapImpl.isMyLocationEnabled()) {
//				MyLocationData locData = new MyLocationData.Builder()
//				.accuracy(location.getRadius())
//				// 此处设置开发者获取到的方向信息，顺时针0-360
//				.direction(100).latitude(location.getLatitude())
//				.longitude(location.getLongitude()).build();
//				mapImpl.setMyLocationData(locData);
			}
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

	}
	// ////////////////////// OnGetGeoCoderResultListener

}
