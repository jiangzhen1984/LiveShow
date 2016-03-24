package com.v2tech.presenter;

import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.V2.jni.util.V2Log;
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
import com.v2tech.net.DeamonWorker;
import com.v2tech.net.lv.LivePublishReqPacket;
import com.v2tech.net.lv.LiveWatchingReqPacket;
import com.v2tech.service.ConferenceService;
import com.v2tech.service.DeviceService;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.LiveService;
import com.v2tech.service.MessageListener;
import com.v2tech.service.UserService;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.util.SPUtil;
import com.v2tech.view.MapVideoLayout;
import com.v2tech.vo.Conference;
import com.v2tech.vo.User;
import com.v2tech.vo.UserDeviceConfig;
import com.v2tech.vo.VMessage;

public class MainPresenter extends BasePresenter implements OnGetGeoCoderResultListener,  BDLocationListener, MapVideoLayout.LayoutPositionChangedListener {
	
	private static final int INIT = 1;
	private static final int LOGIN_CALLBACK = 2;
	private static final int RECOMMENDAATION = 3;
	private static final int CREATE_VIDEO_SHARE_CALL_BACK = 4;
	private static final int REPORT_LOCATION = 5;
	private static final int CREATE_VIDEO_SHARE = 6;
	
	private static final int RECOMMENDATION_BUTTON_SHOW_FLAG = 1;
	private static final int RECOMMENDATION_COUNT_SHOW_FLAG = 1 << 1;
	private static final int FOLLOW_BUTTON_SHOW_FLAG = 1 << 2;
	private static final int FOLLOW_COUNT_SHOW_FLAG = 1 << 3;
	private static final int LIVER_SHOW_FLAG = 1 << 4;
	private static final int PUBLISHING_FLAG = 1 << 5;
	private static final int WATCHING_FLAG = 1 << 6;
	private static final int LOCAL_CAMERA_OPENING = 1 << 7;
	private static final int BOTTOM_LAYOUT_SHOW = 1 << 8;
	
	private Random confIdRandom;
	
	
	private Context context;
	private MainPresenterUI ui;
	private UserService us;
	private ConferenceService vs;
	private LiveService ls;
	private DeviceService ds;
	private Handler h;
	private Conference conf;
	
	private int videoScreenState;
	private boolean keyboardState = false;
	private boolean cameraSurfaceViewMeasure = false;
	
	private LocationWrapper currentLocation;
	private LocationWrapper currentMapCenter;
	private static float mCurrentZoomLevel = 12F;
	
	/////////////////////////////////
	private GeoCoder mSearch;
	private BaiduMap  mapInstance;
	private LocationClient locationClient;
	private boolean locating;
	
	/////////////////////////////////////////
	
	public MainPresenter(Context context, MainPresenterUI ui) {
		super();
		this.ui = ui;
		this.context = context;
		confIdRandom = new Random();
		videoScreenState = (RECOMMENDATION_BUTTON_SHOW_FLAG
				| RECOMMENDATION_COUNT_SHOW_FLAG | FOLLOW_BUTTON_SHOW_FLAG
				| FOLLOW_COUNT_SHOW_FLAG |BOTTOM_LAYOUT_SHOW);
		
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
		
		public boolean getRecommandationButtonState();
		
		public void updateVideShareButtonText(boolean publish);
		
		public void videoShareLayoutFlyout();
		
		public SurfaceView getCameraSurfaceView();
		
		public void showBottomLayout(boolean flag);
		
		public void resizeCameraSurfaceSize();
		
		public void showMessage(String msg);
		
		public void showError(int flag);
	}
	
	
	
	
	public void mapLocationButtonClicked() {
		
	}
	
	
	public void sendMessageButtonClicked() {
		String txt = ui.getTextString();
		if (TextUtils.isEmpty(txt)) {
			return;
		}
		sendMessage(txt);
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
		if ((videoScreenState & PUBLISHING_FLAG) == PUBLISHING_FLAG) {
			videoScreenState &= (~PUBLISHING_FLAG);
			ui.updateVideShareButtonText(false);
			ui.videoShareLayoutFlyout();
			vs.quitConference(conf, null);
			DeamonWorker.getInstance().request(new LiveWatchingReqPacket(conf.getId(), LiveWatchingReqPacket.CANCEL));
		} else {
			videoScreenState |= PUBLISHING_FLAG;
			Message.obtain(h, CREATE_VIDEO_SHARE).sendToTarget();
			ui.updateVideShareButtonText(true);
		}
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
				sendMessage(text);
			}
		}
	}
	
	public void onLoginChildUIFinished(int ret,Intent data) {
		
	}
	
	
	@Override
	public void onUICreated() {
		h = new LocalHandler(backendThread.getLooper());
		Message.obtain(h, INIT).sendToTarget();
	}


	@Override
	public void onUIStarted() {
		startLocationScan(locationClient);
	}
	


	@Override
	public void onUIStopped() {
		stopLocationScan(locationClient);
	}


	@Override
	public void onUIDestroyed() {
		us.clearCalledBack();
		vs.clearCalledBack();
		ls.clearCalledBack();
		this.h.removeMessages(REPORT_LOCATION);
		destroyBackendThread();
	}


	///////////////////////////////////////////////////////////////////////////////////////////
	
	
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
		return lc;
	}
	
	
	private void startLocationScan(LocationClient lc) {
		if (lc != null && !locating) {
			locating = true;
			lc.start();
		}
	}
	
	private void stopLocationScan(LocationClient lc) {
		if (lc != null && locating) {
			locating = false;
			lc.stop();
		}
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
		
		if (prepreUpdateMap()) {
			updateMapCenter(currentMapCenter , mCurrentZoomLevel);
		}
		
		
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult res) {
		
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
	
	
	
	////////////////////////////////////LayoutPositionChangedListener////////////////////////
	
	
	
	@Override
	public void onPreparedFlyingIn() {
		// TODO Auto-generated method stub
	}


	@Override
	public void onFlyingIn() {
		if ((this.videoScreenState & LOCAL_CAMERA_OPENING) == LOCAL_CAMERA_OPENING) {
			this.videoScreenState &= (~LOCAL_CAMERA_OPENING);
			UserDeviceConfig duc = new UserDeviceConfig(0, 0, GlobalHolder.getInstance().getCurrentUserId(), "", null);
			duc.setSVHolder(ui.getCameraSurfaceView());
			ds.requestCloseVideoDevice(duc, null);
			
		}
		
		if ((this.videoScreenState & BOTTOM_LAYOUT_SHOW) != BOTTOM_LAYOUT_SHOW) {
			ui.showBottomLayout(true);
			this.videoScreenState |= BOTTOM_LAYOUT_SHOW;
		}
		
	}


	@Override
	public void onPreparedFlyingOut() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onFlyingOut() {
	}


	@Override
	public void onDrag() {
		if (!cameraSurfaceViewMeasure) {
			cameraSurfaceViewMeasure = true;
			ui.resizeCameraSurfaceSize();
		}
		if ((this.videoScreenState & LOCAL_CAMERA_OPENING) != LOCAL_CAMERA_OPENING) {
			
			UserDeviceConfig duc = new UserDeviceConfig(0, 0, GlobalHolder.getInstance().getCurrentUserId(), "", null);
			duc.setSVHolder(ui.getCameraSurfaceView());
			ds.requestOpenVideoDevice(duc, null);
			ui.showBottomLayout(false);
			this.videoScreenState |= LOCAL_CAMERA_OPENING;
			this.videoScreenState &= (~BOTTOM_LAYOUT_SHOW);
		}
		
	}
//////////////////////////////////LayoutPositionChangedListener/////////////////

	private void doInitInBack() {
		vs = new ConferenceService();
		us = new UserService();
		ls = new LiveService();
		ds = new DeviceService();
		
		if (GlobalHolder.getInstance().getCurrentUser() == null) {
			TelephonyManager tl = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String phone = SPUtil.getConfigStrValue(context, "cellphone");
			String code = SPUtil.getConfigStrValue(context, "code");
			String lp = tl.getLine1Number();
			if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code)) {
				if (TextUtils.isEmpty(lp)) {
					lp = System.currentTimeMillis()+"";
				}
				us.login(lp, "", true, null);
			} else {
				us.login(phone, code, false, null);
			}
		} else {
			V2Log.w("Already login, no need login again" + GlobalHolder.getInstance().getCurrentUser());
		}
		mapInstance = ui.getMapInstance();
		locationClient = startLocationScan();
		startLocationScan(locationClient);
	}
	
	
	private void doRecommendationInBack() {
		
	}
	
	private void handleCreateVideoShareBack(JNIResponse resp) {
		if (resp.getResult() == JNIResponse.Result.SUCCESS) {
			DeamonWorker.getInstance().request(new LivePublishReqPacket(conf.getId(), currentLocation.ll.latitude, currentLocation.ll.longitude));
			videoScreenState |= PUBLISHING_FLAG;
		} else {
			//FIXME show error UI
		}
	}
	
	private void reportLocation() {
		if (currentLocation != null) {
			ls.updateGps(currentLocation.ll.latitude, currentLocation.ll.longitude);
			Message msg = Message.obtain();
			msg.what = REPORT_LOCATION;
			this.h.sendMessageDelayed(msg, 30000);
		}
	}
	
	
	private void createVideoShareInBack() {
		conf = new Conference(confIdRandom.nextLong());
		vs.requestEnterConference(conf, new MessageListener(h, CREATE_VIDEO_SHARE_CALL_BACK, null));
	}
	
	
	
	private void sendMessage(String text) {
		if (conf == null) {
			ui.showError(1);
			return;
		}
		VMessage vmsg = new VMessage(1, conf.getId(), GlobalHolder.getInstance().getCurrentUser(), new Date(System.currentTimeMillis()));
		vs.sendMessage(vmsg);
	}
	
	
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
			case RECOMMENDAATION:
				doRecommendationInBack();
				break;
			case CREATE_VIDEO_SHARE_CALL_BACK:
				handleCreateVideoShareBack((JNIResponse)msg.obj);
				break;
			case REPORT_LOCATION:
				reportLocation();
				break;
			case CREATE_VIDEO_SHARE:
				createVideoShareInBack();
				break;
			}
		}
		
	}
	
	
	
	final class LocationWrapper {
		LatLng ll;
		
	}

}
