package com.v2tech.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.v2tech.presenter.LoginPresenter.LocalHandler;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.UserService;
import com.v2tech.util.SPUtil;
import com.v2tech.vo.User;

public class MainPresenter implements OnGetGeoCoderResultListener,  BDLocationListener {
	
	private static final int INIT = 1;
	private static final int LOGIN_CALLBACK = 2;
	
	private static final int RECOMMENDATION_BUTTON_SHOW_FLAG = 1;
	private static final int RECOMMENDATION_COUNT_SHOW_FLAG = 1 << 1;
	private static final int FOLLOW_BUTTON_SHOW_FLAG = 1 << 2;
	private static final int FOLLOW_COUNT_SHOW_FLAG = 1 << 3;
	private static final int LIVER_SHOW_FLAG = 1 << 4;
	private static final int PUBLISHING_FLAG = 1 << 5;
	private static final int WATCHING_FLAG = 1 << 6;
	
	
	private Context context;
	private MainPresenterUI ui;
	private UserService us;
	
	
	private int videoScreenState; 
	private boolean keyboardState = false;
	private boolean loginState = false;
	
	private LocationWrapper currentLocation;
	private LocationWrapper currentMapCenter;
	private static float mCurrentZoomLevel = 12F;
	
	/////////////////////////////////
	private GeoCoder mSearch;
	private BaiduMap  mapInstance;
	private LocationClient locationClient;
	
	/////////////////////////////////////////
	
	public MainPresenter(Context context, MainPresenterUI ui) {
		super();
		this.ui = ui;
		this.context = context;
		videoScreenState = (RECOMMENDATION_BUTTON_SHOW_FLAG
				| RECOMMENDATION_COUNT_SHOW_FLAG | FOLLOW_BUTTON_SHOW_FLAG
				| FOLLOW_COUNT_SHOW_FLAG );
		
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
	}

	public interface MainPresenterUI {
		
		public void resetUIDisplayOrder();
		
		public void showTextKeyboard(boolean flag);
		
		public void showVideoScreentItem(int tag, boolean showFlag);
		
		public void resetMapCenter(double lat, double lng, int zoom);
		
		public void showLoginUI();
		
		public void showPersonelUI();
		
		public BaiduMap getMapInstance();
		
		public void showSearchErrorToast();
		
		public String getTextString();
		
	}
	
	public void uicreated() {
		if (th != null) {
			th.quit();
		}
		th = new HandlerThread("LoginPresenterBackEnd");
		th.start();
		
		h = new LocalHandler(th.getLooper());
		Message.obtain(h, INIT).sendToTarget();
	}
	
	public void mapLocationButtonClicked() {
		
	}
	
	
	public void sendMessageButtonClicked() {
		
	}
	
	public void mapMarkerClicked() {
		
	}
	
	public void mapSearchButtonClicked() {
		String text = ui.getTextString();
		if (TextUtils.isEmpty(text)) {
			return;
		}
		searchMap(text);
	}
	
	
	public void videoScreenClicked() {
		videoScreenState &=  ~(RECOMMENDATION_BUTTON_SHOW_FLAG
				| RECOMMENDATION_COUNT_SHOW_FLAG | FOLLOW_BUTTON_SHOW_FLAG
				| FOLLOW_COUNT_SHOW_FLAG | LIVER_SHOW_FLAG);
		
		if ((videoScreenState & RECOMMENDATION_BUTTON_SHOW_FLAG) == RECOMMENDATION_BUTTON_SHOW_FLAG) {
			ui.showVideoScreentItem(RECOMMENDATION_BUTTON_SHOW_FLAG, true);
		} else {
			ui.showVideoScreentItem(RECOMMENDATION_BUTTON_SHOW_FLAG, false);
		}
		
		if ((videoScreenState & RECOMMENDATION_COUNT_SHOW_FLAG) == RECOMMENDATION_COUNT_SHOW_FLAG) {
			ui.showVideoScreentItem(RECOMMENDATION_COUNT_SHOW_FLAG, true);
		}else {
			ui.showVideoScreentItem(RECOMMENDATION_COUNT_SHOW_FLAG, false);
		}

		if ((videoScreenState & FOLLOW_BUTTON_SHOW_FLAG) == FOLLOW_BUTTON_SHOW_FLAG) {
			ui.showVideoScreentItem(FOLLOW_BUTTON_SHOW_FLAG, true);
		}else {
			ui.showVideoScreentItem(FOLLOW_BUTTON_SHOW_FLAG, false);
		}

		if ((videoScreenState & FOLLOW_COUNT_SHOW_FLAG) == FOLLOW_COUNT_SHOW_FLAG) {
			ui.showVideoScreentItem(FOLLOW_COUNT_SHOW_FLAG, true);
		}else {
			ui.showVideoScreentItem(FOLLOW_COUNT_SHOW_FLAG, false);
		}

		if ((videoScreenState & LIVER_SHOW_FLAG) == LIVER_SHOW_FLAG) {
			ui.showVideoScreentItem(LIVER_SHOW_FLAG, true);
		}else {
			ui.showVideoScreentItem(LIVER_SHOW_FLAG, false);
		}
	}
	
	public void recommendationButtonClicked() {
		
	}
	
	public void followButtonClicked() {
		
	}
	
	public void liverButtonClicked() {
		
	}
	
	
	public void videoShareButtonClicked() {
		
	}
	
	public void personelButtonClicked() {
		User user = GlobalHolder.getInstance().getCurrentUser();
		if (user == null || user.isNY) {
			ui.showLoginUI();
		} else {
			ui.showPersonelUI();
		}
	}
	
	public void mapCenterMoved(double lat, double lng) {
		currentMapCenter.ll = new LatLng(lat, lng);
	}
	
	public void textClicked() {
		keyboardState = true;
		ui.showTextKeyboard(true);
	}
	
	
	public void onKeyboardChildUIFinished(int ret,Intent data) {
		keyboardState = false;
		ui.showTextKeyboard(false);
		if (ret != Activity.RESULT_OK) {
			int action = data.getExtras().getInt("action");
			String text = data.getExtras().getString("text");
			if (TextUtils.isEmpty(text)) {
				return;
			}
			if (action == 1) {
				searchMap(text);
			} else {
				
			}
		}
	}
	
	public void onLoginChildUIFinished(int ret,Intent data) {
		
	}
	
	

	
	
	public void onUIDestroy() {
		us.clearCalledBack();
		stopLocationScan(locationClient);
	}
	
	
	
	private void searchMap(String text) {
		mSearch.geocode(new GeoCodeOption().city("北京").address(text));
	}
	
	
	
	private LocationClient startLocationScan() {
		LocationClient lc = new LocationClient(context);
		lc.registerLocationListener(this);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		lc.setLocOption(option);
		lc.start();
		return lc;
	}
	
	private void stopLocationScan(LocationClient lc) {
		lc.stop();
	}
	
	
	private boolean prepreUpdateMap() {
		if (isOpenedLiveScreen() || isPublishing()) {
			return false;
		}
		return true;
	}
	
	
	private boolean isOpenedLiveScreen() {
		return (videoScreenState & LIVER_SHOW_FLAG) == LIVER_SHOW_FLAG;
	}
	
	private boolean isPublishing() {
		return (videoScreenState & PUBLISHING_FLAG) == PUBLISHING_FLAG;
	}
	
	
	private boolean isWatchingLive() {
		return (videoScreenState & WATCHING_FLAG) == WATCHING_FLAG;
	}
	
	private void updateMapCenter(LocationWrapper lw, float level) {
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(
				lw.ll, level);
		this.ui.getMapInstance().animateMapStatus(u);
	}
	
	
	
	
	
	//////////////////////// OnGetGeoCoderResultListener
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			ui.showSearchErrorToast();
			return;
		}
		mapInstance.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		
		//
		if (currentMapCenter == null) {
			currentMapCenter = new LocationWrapper();
		}
		currentMapCenter.ll = result.getLocation();
		
		updateMapCenter(currentMapCenter , mCurrentZoomLevel);
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult res) {
		// TODO Auto-generated method stub
		
	}

	
	///////////////////BDLocationListener
	@Override
	public void onReceiveLocation(BDLocation location) {
		LatLng ll = new LatLng(location.getLatitude(),
				location.getLongitude());
		
		if (currentLocation == null) {
			currentLocation = new LocationWrapper();
		}
		currentLocation.ll= ll;
		
		if (prepreUpdateMap()) {
			updateMapCenter(currentLocation, mCurrentZoomLevel);
		}
		
		//TODO out of area search from server
	}
	
	
	
	private void doInitInBack() {

		TelephonyManager tl = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String phone = SPUtil.getConfigStrValue(context, "cellphone");
		String code = SPUtil.getConfigStrValue(context, "code");
		String lp = tl.getLine1Number();
		us = new UserService();
		if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code)) {
			if (TextUtils.isEmpty(lp)) {
				lp = System.currentTimeMillis()+"";
			}
			us.login(lp, "", true, null);
		} else {
			us.login(phone, code, false, null);
		}
		
		mapInstance = ui.getMapInstance();
		locationClient = startLocationScan();
	}
	
	HandlerThread th;
	private Handler h;
	
	class LocalHandler  extends Handler {
		

		public LocalHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			int w = msg.what;
			switch (w) {
			case INIT:
				doInitInBack();
				break;
			}
		}
		
	}
	
	
	
	final class LocationWrapper {
		LatLng ll;
		
	}

}
