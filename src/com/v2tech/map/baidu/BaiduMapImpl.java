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
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
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
		OnGetGeoCoderResultListener,BaiduMap.OnMarkerClickListener {

	public BaiduMap mapImpl;

	private LocationClient locationClient;
	private GeoCoder mSearchAPI;
	
	// ///listener///
	private MarkerListener markerListener;

	public BaiduMapImpl(BaiduMap mapImpl) {
		super();
		this.mapImpl = mapImpl;
		mSearchAPI = GeoCoder.newInstance();
		mSearchAPI.setOnGetGeoCodeResultListener(this);
		
		
	}

	public BaiduMap getMapImpl() {
		return mapImpl;
	}

	public void setMapImpl(BaiduMap mapImpl) {
		this.mapImpl = mapImpl;
	}

	@Override
	public void addMarker(Marker marker) {
		if (marker.getLive() != null) {
			BitmapDescriptor online = BitmapDescriptorFactory
					.fromResource(R.drawable.marker_live);

			Bundle bundle = new Bundle();
			bundle.putSerializable("live", marker.getLive());
			OverlayOptions oo = new MarkerOptions()
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
			OverlayOptions oo = new MarkerOptions()
					.icon(online)
					.position(
							new LatLng(marker.getWatcher().getLat(), marker
									.getWatcher().getLng())).extraInfo(bundle);
			mapImpl.addOverlay(oo);

		}
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
		//FIXME update city first
		mSearchAPI.geocode(new GeoCodeOption().city("北京").address(text));
	}
	
	
	
	

	@Override
	public void startLocate(LocationParameter parameter) {
		if (!(parameter instanceof BaiduLocationParameter)) {
			throw new RuntimeException(" parameter is not BaiduLocationParameter instance");
		}
		if (locationClient != null && locationClient.isStarted()) {
			return;
		}
		BaiduLocationParameter bp = (BaiduLocationParameter)parameter;
		mapImpl.setMyLocationEnabled(bp.isEnableSelfLocation());
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
		}
	}
	
	public LocationParameter buildParameter(Object obj) {
		if (obj instanceof Context) {
			return new BaiduLocationParameter((Context)obj);
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
		// if (!isState(MAP_CENTER_UPDATE)) {
		// this.setState(MAP_CENTER_UPDATE);
		// }
		//
		// if ((this.locationStatus & REQUEST_SELF_LOCATION) ==
		// REQUEST_SELF_LOCATION) {
		// this.locationStatus = 0;
		// this.locationStatus |= SELF_LOCATION;
		// } else if ((this.locationStatus & REQUEST_LIVER_LOCATION) ==
		// REQUEST_LIVER_LOCATION) {
		// this.locationStatus = 0;
		// this.locationStatus |= LIVER_LOCATION;
		// } else if ((this.locationStatus & REQUEST_RANDOM_LOCATION) ==
		// REQUEST_RANDOM_LOCATION) {
		// this.locationStatus = 0;
		// this.locationStatus |= RANDOM_LOCATION;
		// }
		//
		// currentMapCenter.ll = status.target;
		// V2Log.i("new map location status : " + this.locationStatus
		// + "  center:" + this.currentMapCenter.ll);
		// if (!h.hasMessages(SEARCH_LIVE)) {
		// V2Log.i("send delay message for search live ");
		// Message m = Message.obtain(h, SEARCH_LIVE);
		// h.sendMessageDelayed(m, 200);
		// }

	}

	@Override
	public void onMapStatusChangeStart(MapStatus status) {
		// if ((this.locationStatus & REQUEST_SELF_LOCATION) ==
		// REQUEST_SELF_LOCATION) {
		// return;
		// } else if ((this.locationStatus & REQUEST_LIVER_LOCATION) ==
		// REQUEST_LIVER_LOCATION) {
		// return;
		// } else {
		// // For handle when user drag map directly
		// this.locationStatus |= REQUEST_RANDOM_LOCATION;
		// }
	}

	// //////////////////BaiduMap.OnMapStatusChangeListener

	
	// /////////////////BDLocationListener
	@Override
	public void onReceiveLocation(BDLocation location) {
		if (markerListener != null) {
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			markerListener.onLocated(new BaiduLocation(ll));
		}
	}
	// /////////////////BDLocationListener
	

	// ////////////////////// OnGetGeoCoderResultListener
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			return;
		}
		mapImpl.animateMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult res) {

	}
	// ////////////////////// OnGetGeoCoderResultListener
	
	
	
	
	
}
