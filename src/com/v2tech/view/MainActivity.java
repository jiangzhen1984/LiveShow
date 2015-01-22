package com.v2tech.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.V2.jni.VideoBCRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.Live;
import com.v2tech.widget.ArrowPopupWindow;
import com.v2tech.widget.LiveVideoWidget;

public class MainActivity extends Activity implements
		LiveVideoWidget.DragListener, OnClickListener,
		LiveVideoWidget.OnWidgetClickListener, 
		OnGetGeoCoderResultListener {

	private static final int SEARCH = 1;
	private static final int PLAY_FIRST_LIVE = 2;

	private FrameLayout mMainLayout;
	private EditText mSearchEdit;
	
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	LocationClient mLocClient;
	private GeoCoder mSearch;
	
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = true;// 是否首次定位

	private LiveVideoWidget lvw;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		mMainLayout = (FrameLayout) findViewById(R.id.main);
		mSearchEdit = (EditText) findViewById(R.id.search_edit);
		mSearchEdit.addTextChangedListener(mSearchedTextWatcher);
		findViewById(R.id.main_activity_title_bar_button_plus)
				.setOnClickListener(this);

		Intent intent = getIntent();
		if (intent.hasExtra("x") && intent.hasExtra("y")) {
			// 当用intent参数时，设置中心点为指定点
			Bundle b = intent.getExtras();
			LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));
			mMapView = new MapView(this,
					new BaiduMapOptions().mapStatus(new MapStatus.Builder()
							.target(p).build()));
		} else {
			mMapView = new MapView(this, new BaiduMapOptions());
		}

		mMainLayout.addView(mMapView, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT));

		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);

		init();
		initVideoLayout();
	}

	private void init() {
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		CloudManager.getInstance().init(mLocalCloudListener);
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		
		mBaiduMap.setOnMarkerClickListener(mMarkerClickerListener);
	}

	private void initVideoLayout() {
		DisplayMetrics dis = this.getResources().getDisplayMetrics();
		int width = dis.widthPixels;
		int height = dis.heightPixels;
		lvw = new LiveVideoWidget(this);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(width / 2,
				((width / 2) - (width / 2 % 16)) / 4 * 3);
		fl.leftMargin = dis.widthPixels - fl.width - 10;
		fl.topMargin = 10;
		mMainLayout.addView(lvw, fl);
		lvw.setDragListener(this);
		lvw.setOnWidgetClickListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();

		CloudManager.getInstance().destroy();
		mSearch.destroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void startDrag() {
	}

	@Override
	public void stopDrag() {
	}

	@Override
	public void onWidgetClick(View view) {
		// FIXME stop playing
		Intent i = new Intent(this, VideoList.class);
		startActivity(i);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.main_activity_title_bar_button_plus:
			showPlusPopupWindow(v);
			break;
		}
	}

	private ArrowPopupWindow arw;

	private void showPlusPopupWindow(View anchor) {
		if (arw == null) {
			arw = new ArrowPopupWindow(this);
		}
		arw.showAsDropDown(anchor);
	}

	private BDLocation mCacheLocation;

	private void doSearch(String key) {
		VideoBCRequest.getInstance().getNeiborhood(500);
		//mSearch.geocode(new GeoCodeOption().city("北京").address(key));
		LocalHandler.removeMessages(PLAY_FIRST_LIVE);
		LocalHandler.sendEmptyMessageDelayed(PLAY_FIRST_LIVE, 1000);
	}
	
	
	
	

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MainActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_gcoding)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		String strInfo = String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);
		Toast.makeText(MainActivity.this, strInfo, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		
	}




	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				float zoomLevel = 15.0F;
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,
						zoomLevel);
				mBaiduMap.animateMapStatus(u);

			}

			
			if (mCacheLocation == null
					|| (mCacheLocation.getLongitude() != location
							.getLongitude() || mCacheLocation.getLatitude() != location
							.getLatitude())) {
				mCacheLocation = location;
				VideoBCRequest.getInstance().updateGpsRequest(
						"<gps lon=\"" + location.getLongitude() + "\" lat=\""
								+ location.getLatitude() + "\"></gps>");
				
				VideoBCRequest.getInstance().getNeiborhood(500);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private LocalState mSearchState = LocalState.DONE;

	private TextWatcher mSearchedTextWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			LocalHandler.removeMessages(SEARCH);
			Message msg = Message.obtain(LocalHandler, SEARCH, s.toString());
			LocalHandler.sendMessageDelayed(msg, 1000);
		}

	};

	private CloudListener mLocalCloudListener = new CloudListener() {

		@Override
		public void onGetDetailSearchResult(DetailSearchResult arg0, int arg1) {

		}

		@Override
		public void onGetSearchResult(CloudSearchResult result, int error) {
			if (result != null && result.poiList != null
					&& result.poiList.size() > 0) {
				mBaiduMap.clear();
				BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
				LatLng ll;
				LatLngBounds.Builder builder = new Builder();
				for (CloudPoiInfo info : result.poiList) {
					ll = new LatLng(info.latitude, info.longitude);
					OverlayOptions oo = new MarkerOptions().icon(bd).position(ll);
					mBaiduMap.addOverlay(oo);
					builder.include(ll);
				}
				LatLngBounds bounds = builder.build();
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
				mBaiduMap.animateMapStatus(u);
			}
		}

	};
	
	private BaiduMap.OnMarkerClickListener mMarkerClickerListener = new BaiduMap.OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker marker) {
			return false;
		}
		
	};

	private Handler LocalHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SEARCH:
				synchronized (mSearchState) {
					mSearchState = LocalState.DONING;
					doSearch((String) msg.obj);
				}
				break;
			case PLAY_FIRST_LIVE: 
				if (VideoBCRequest.getInstance().lives.size() > 0) {
					lvw.stop();
					lvw.startLive(new Live(null, VideoBCRequest.getInstance().lives.get(0)[0]));
				}
				break;
			
			}
		}

	};

	enum LocalState {
		DONING, DONE;
	}

}
