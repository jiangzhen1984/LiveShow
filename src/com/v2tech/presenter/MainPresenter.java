package com.v2tech.presenter;

import java.util.Date;
import java.util.List;
import java.util.Random;

import v2av.VideoPlayer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.util.LongSparseArray;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.V2.jni.ind.MessageInd;
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
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.v2tech.service.AsyncResult;
import com.v2tech.service.ConferenceService;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.LiveService;
import com.v2tech.service.MessageListener;
import com.v2tech.service.UserService;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.service.jni.RequestConfCreateResponse;
import com.v2tech.service.jni.RequestEnterConfResponse;
import com.v2tech.service.jni.SearchLiveResponse;
import com.v2tech.util.SPUtil;
import com.v2tech.v2liveshow.R;
import com.v2tech.view.MapVideoLayout;
import com.v2tech.vo.Conference;
import com.v2tech.vo.ConferenceGroup;
import com.v2tech.vo.Live;
import com.v2tech.vo.User;
import com.v2tech.vo.UserDeviceConfig;
import com.v2tech.vo.VMessage;
import com.v2tech.vo.VMessageTextItem;
import com.v2tech.widget.VideoShowFragment;

public class MainPresenter extends BasePresenter implements
		OnGetGeoCoderResultListener, BDLocationListener,
		MapVideoLayout.LayoutPositionChangedListener,
		BaiduMap.OnMarkerClickListener, BaiduMap.OnMapStatusChangeListener, LiverAction, MapVideoLayout.OnVideoFragmentChangedListener {
	
	private static final int INIT = 1;
	private static final int RECOMMENDAATION = 3;
	private static final int CREATE_VIDEO_SHARE_CALL_BACK = 4;
	private static final int REPORT_LOCATION = 5;
	private static final int CREATE_VIDEO_SHARE = 6;
	private static final int SEARCH_LIVE = 7;
	private static final int SEARCH_LIVE_CALLBACK = 8;
	private static final int WATCHING_REQUEST_CALLBACK = 9;
	private static final int CANCEL_WATCHING_REQUEST_CALLBACK = 10;
	private static final int ATTEND_LISTENER = 11;
	private static final int CANCEL_PUBLISHING_REQUEST_CALLBACK = 12;
	private static final int MESSAGE_LISTENER = 13;
	
	private static final int RECOMMENDATION_BUTTON_SHOW_FLAG = 1;
	private static final int RECOMMENDATION_COUNT_SHOW_FLAG = 1 << 1;
	private static final int FOLLOW_BUTTON_SHOW_FLAG = 1 << 2;
	private static final int FOLLOW_COUNT_SHOW_FLAG = 1 << 3;
	private static final int LIVER_SHOW_FLAG = 1 << 4;
	private static final int PUBLISHING_FLAG = 1 << 5;
	private static final int WATCHING_FLAG = 1 << 6;
	private static final int LOCAL_CAMERA_OPENING = 1 << 7;
	private static final int BOTTOM_LAYOUT_SHOW = 1 << 8;
	private static final int KEYBOARD_SHOW = 1 << 9;
	private static final int MAP_CENTER_UPDATE = 1 << 10;
	
	private static final int SELF_LOCATION = 1;
	private static final int LIVER_LOCATION = 1 << 1;
	private static final int RANDOM_LOCATION = 1 << 2;
	private static final int REQUEST_SELF_LOCATION = 1 << 3;
	private static final int REQUEST_LIVER_LOCATION = 1 << 4;
	private static final int REQUEST_RANDOM_LOCATION = 1 << 5;
	
	private Random confIdRandom;
	
	
	private Context context;
	private MainPresenterUI ui;
	private UserService us;
	private ConferenceService vs;
	private LiveService ls;
	private Handler h;
    private Live currentLive;
    private LongSparseArray<Live> lives;
    
    
	private int videoScreenState;
	private int locationStatus;
	private boolean cameraSurfaceViewMeasure = false;
	
	private LocationWrapper currentLocation;
	private LocationWrapper currentMapCenter;
	private LocationWrapper currentLiveLocation;
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
		lives = new LongSparseArray<Live>();
		videoScreenState = (RECOMMENDATION_BUTTON_SHOW_FLAG
				| RECOMMENDATION_COUNT_SHOW_FLAG | FOLLOW_BUTTON_SHOW_FLAG
				| FOLLOW_COUNT_SHOW_FLAG |BOTTOM_LAYOUT_SHOW);


		currentMapCenter = new LocationWrapper();
		currentLocation = new LocationWrapper();
		
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
		
		public SurfaceView  getCurrentSurface();
		
		public void showBottomLayout(boolean flag);
		
		public void resizeCameraSurfaceSize();
		
		public void showMessage(String msg);
		
		public void showError(int flag);
		
		public void showLiverPersonelUI();
		
		public void showDebugMsg(String msg);
		
		public void setCurrentLive(Live l);
		
		public void queuedMessage(String msg);
	}
	
	
	
	
	public void mapLocationButtonClicked() {
	
		if ((this.locationStatus & SELF_LOCATION) == SELF_LOCATION) {
			this.updateMapCenter(this.currentLiveLocation, mCurrentZoomLevel);
			this.locationStatus |= REQUEST_SELF_LOCATION; 
		} else if((this.locationStatus & LIVER_LOCATION) == LIVER_LOCATION) {
			this.updateMapCenter(this.currentLocation, mCurrentZoomLevel);
			this.locationStatus |= REQUEST_LIVER_LOCATION; 
		} else if((this.locationStatus & RANDOM_LOCATION) == RANDOM_LOCATION) {
			this.updateMapCenter(this.currentLocation, mCurrentZoomLevel);
			this.locationStatus |= REQUEST_SELF_LOCATION; 
		}
		V2Log.i("===> map location clicked:" + this.locationStatus+"   "+currentLocation.ll+"  "+currentLiveLocation);
	}
	
	
	public void sendMessageButtonClicked() {
		String txt = ui.getTextString();
		if (TextUtils.isEmpty(txt)) {
			return;
		}
		sendMessage(txt);
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
		//TODO update ui
		if ((this.videoScreenState & WATCHING_FLAG) == WATCHING_FLAG) {
			ls.recommend(currentLive, !currentLive.isRend());
		} else {
			
		}
	}
	
	public void followButtonClicked() {
		if ((this.videoScreenState & WATCHING_FLAG) == WATCHING_FLAG) {
			//TODO update ui
			if (currentLive.isFollow()) {
				ls.cancelfollow(currentLive);
			} else {
				ls.follow(currentLive);
			}
		} else {
			ui.showError(2);
		}
	}
	
	public void liverButtonClicked() {
		ui.showLiverPersonelUI();
	}
	
	
	public void videoShareButtonClicked() {
		if ((videoScreenState & PUBLISHING_FLAG) == PUBLISHING_FLAG) {
			Conference conf = new Conference(currentLive.getLid());
			videoScreenState &= (~PUBLISHING_FLAG);
			ui.updateVideShareButtonText(false);
			ui.videoShareLayoutFlyout();
			vs.quitConference(conf, new MessageListener(h, CANCEL_PUBLISHING_REQUEST_CALLBACK, null));
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
	
	
	public void textClicked() {
		this.videoScreenState |= KEYBOARD_SHOW;
		ui.showTextKeyboard(true);
	}
	
	
	public void onKeyboardChildUIFinished(int ret,Intent data) {
		this.videoScreenState &= (~KEYBOARD_SHOW);
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
		V2Log.i("===> " + Process.myPid()+"  ===> uiCreate");
		h = new LocalHandler(backendThread.getLooper());
		Message.obtain(h, INIT).sendToTarget();
	}


	@Override
	public void onUIStarted() {
		locationClient = startLocationScan();
		updateLocateState(locationClient, true);
	}
	


	@Override
	public void onUIStopped() {
		updateLocateState(locationClient, false);
		locationClient = null;
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
		option.setScanSpan(15000);
		lc.setLocOption(option);
		return lc;
	}
	
	
	private void updateLocateState(LocationClient lc, boolean enable) {
		if (lc == null) {
			return;
		}
		if (enable && !locating) {
			locating = true;
			lc.start();
		} else if (!enable && locating) {
			locating = false;
			lc.stop();
		}
	}
	
	
	
	private boolean prepreUpdateMap() {
		if (isOpenedLiveScreen() || isPublishing()) {
			return false;
		}
		
		if ((this.videoScreenState & MAP_CENTER_UPDATE) == MAP_CENTER_UPDATE) {
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
		if (lw == null) {
			return;
		}
		
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(
				lw.ll, level);
		this.ui.getMapInstance().animateMapStatus(u);
		
		if ((this.videoScreenState & MAP_CENTER_UPDATE) != MAP_CENTER_UPDATE) { 
			this.videoScreenState |= MAP_CENTER_UPDATE;
		}
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
		
		if (prepreUpdateMap()) {
			this.videoScreenState |= REQUEST_RANDOM_LOCATION;
			updateMapCenter(new LocationWrapper(result.getLocation()) , mCurrentZoomLevel);
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
		
		currentLocation.ll = ll;
		if (prepreUpdateMap()) {
			this.locationStatus |= REQUEST_SELF_LOCATION;
			updateMapCenter(new LocationWrapper(ll), mCurrentZoomLevel);
		}
		
		Message.obtain(h, REPORT_LOCATION).sendToTarget();
		
	}
	
	
	///////////////////BaiduMap.OnMarkerClickListener

	@Override
	public boolean onMarkerClick(Marker marker) {
		if ((this.videoScreenState & PUBLISHING_FLAG) == PUBLISHING_FLAG) {
			//TODO illegal state
			return false;
		}
		if ((this.videoScreenState & WATCHING_FLAG) == WATCHING_FLAG) {
			//TODO check window count
			
			//quit from old
			vs.requestExitConference(currentLive, null);
		}
		
		//join new one
		Live l = (Live)marker.getExtraInfo().get("live");
		Conference  newConf = new Conference(l.getLid());
		vs.requestEnterConference(newConf, new MessageListener(h, WATCHING_REQUEST_CALLBACK, null));
		currentLive = l;
		ui.showDebugMsg(currentLive.getLid()+"");
		ui.setCurrentLive(l);
		return true;
	}
	
	
	///////////////BaiduMap.OnMarkerClickListener
	
	
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
			vs.requestCloseVideoDevice(duc, null);
			
		}
		
		if ((this.videoScreenState & BOTTOM_LAYOUT_SHOW) != BOTTOM_LAYOUT_SHOW) {
			ui.showBottomLayout(true);
			this.videoScreenState |= BOTTOM_LAYOUT_SHOW;
		}
		
		
		this.videoScreenState |= LIVER_SHOW_FLAG;
	}


	@Override
	public void onPreparedFlyingOut() {
	}


	@Override
	public void onFlyingOut() {
		this.videoScreenState &= (~LIVER_SHOW_FLAG);
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
			vs.requestOpenVideoDevice(duc, null);
			ui.showBottomLayout(false);
			this.videoScreenState |= LOCAL_CAMERA_OPENING;
			this.videoScreenState &= (~BOTTOM_LAYOUT_SHOW);
		}
		
	}
//////////////////////////////////LayoutPositionChangedListener/////////////////
	
	
	
	
	
	///////////////////////BaiduMap.OnMapStatusChangeListener
	
	
	@Override
	public void onMapStatusChange(MapStatus status) {
	}


	@Override
	public void onMapStatusChangeFinish(MapStatus status) {
		if ((this.videoScreenState & MAP_CENTER_UPDATE) != MAP_CENTER_UPDATE) { 
			this.videoScreenState |= MAP_CENTER_UPDATE;
		}
		
		if ((this.locationStatus & REQUEST_SELF_LOCATION) == REQUEST_SELF_LOCATION) {
			this.locationStatus = 0;
			this.locationStatus |= SELF_LOCATION;
		} else if ((this.locationStatus & REQUEST_LIVER_LOCATION) == REQUEST_LIVER_LOCATION) {
			this.locationStatus = 0;
			this.locationStatus |= LIVER_LOCATION;
		} else if ((this.locationStatus & REQUEST_RANDOM_LOCATION) == REQUEST_RANDOM_LOCATION) {
			this.locationStatus = 0;
			this.locationStatus |= RANDOM_LOCATION;
		}
		
		currentMapCenter.ll =status.target;
		V2Log.i("new map location status : " + this.locationStatus +"  center:" +this.currentMapCenter.ll);
		if (!h.hasMessages(SEARCH_LIVE)) {
			V2Log.i("send delay message for search live ");
			Message m = Message.obtain(h, SEARCH_LIVE);
			h.sendMessageDelayed(m, 200);
		}
		
	}


	@Override
	public void onMapStatusChangeStart(MapStatus status) {
		if ((this.locationStatus & REQUEST_SELF_LOCATION) == REQUEST_SELF_LOCATION) {
			return;
		} else if ((this.locationStatus & REQUEST_LIVER_LOCATION) == REQUEST_LIVER_LOCATION) {
			return;
		} else {
			// For handle when user drag map directly
			this.locationStatus |= REQUEST_RANDOM_LOCATION;
		}
	}
	
	
	
	////////////////////BaiduMap.OnMapStatusChangeListener
	
	
	
	////////////////LiverAction
	@Override
	public void onFavButtonClicked() {
		// TODO Auto-generated method stub
		if (currentLive == null) {
			return;
		}
		
		if (currentLive.isFollow()) {
			ls.cancelfollow(currentLive);
		} else {
			ls.follow(currentLive);
		}
		
		
	}


	@Override
	public void onRemButtonClicked() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onLiverButtonClicked() {
		// TODO Auto-generated method stub
		
	}
	
	
	////////////////////LiverAction

	
	
	/////////////OnVideoFragmentChangedListener

	@Override
	public void onChanged(VideoShowFragment videoFrag) {
		// TODO Auto-generated method stub
		if (currentLive != null) {
			vs.requestExitConference(currentLive, null);
			currentLive  = null;
		}
		
		
		this.videoScreenState &= ~WATCHING_FLAG;
		
		//TODO update show new live
		Live l = (Live)videoFrag.getTag1();
		if (l == null) {
			//TODO request new one
			
			//TODO update tag;
			
		} else {
			//join new one
			Conference  newConf = new Conference(l.getLid());
			vs.requestEnterConference(newConf, new MessageListener(h, WATCHING_REQUEST_CALLBACK, null));
			currentLive = l;
		}
		
	}

	/////////////OnVideoFragmentChangedListener	
	
	
	
	


	private void doInitInBack() {
		vs = new ConferenceService();
		us = new UserService();
		ls = new LiveService();
		vs.registerAttendeeDeviceListener(h, ATTEND_LISTENER, null);
		vs.registerMessageListener(h, MESSAGE_LISTENER, null);
		
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

		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		
		mapInstance = ui.getMapInstance();
		mapInstance.setOnMarkerClickListener(this);
		mapInstance.setOnMapStatusChangeListener(this);
		mapInstance.setOnMarkerClickListener(this);
		updateLocateState(locationClient, true);
		
	}
	


	private void doRecommendationInBack() {
		
	}
	
	private void handleCreateVideoShareBack(JNIResponse resp) {
		V2Log.e("==> CREATE VIDEO SHARE:" +resp.getResult());
		if (resp.getResult() == JNIResponse.Result.SUCCESS) {
			RequestConfCreateResponse rcr = (RequestConfCreateResponse)resp;
			currentLive.setLid(rcr.getConfId());
			ui.showDebugMsg(this.currentLive.getLid()+"");
			videoScreenState |= PUBLISHING_FLAG;
			ls.reportLiveStatus(this.currentLive, null);
		} else {
			ui.showDebugMsg("create error");
		}
	}
	
	private void reportLocation() {
		if (currentLocation != null) {
			ls.updateGps(currentLocation.ll.latitude, currentLocation.ll.longitude);
		}
	}
	
	
	private void createVideoShareInBack() {
//		long lid = confIdRandom.nextLong();
//		if (lid < 0) {
//			lid = ~lid;
//		}
		currentLive = new Live(GlobalHolder.getInstance().getCurrentUser(), 0, currentLocation.ll.latitude, currentLocation.ll.longitude);
		vs.createConference(currentLive, new MessageListener(h, CREATE_VIDEO_SHARE_CALL_BACK, null));
	}
	
	
	
	private void sendMessage(String text) {
		if (currentLive == null) {
			ui.showError(1);
			return;
		}
		//4 for group type
		VMessage vmsg = new VMessage(4, currentLive.getLid(), GlobalHolder.getInstance().getCurrentUser(), new Date(System.currentTimeMillis()));
		new VMessageTextItem(vmsg, text);
		vs.sendMessage(vmsg);
		ui.queuedMessage(text);
	}
	
	
	
	private void handSearchLiveCallback(SearchLiveResponse p) {
		if (p == null || p.getPacket() == null ) {
			V2Log.e("===> get search callback ===> No packet");
			return;
		}
		BitmapDescriptor online = BitmapDescriptorFactory
				.fromResource(R.drawable.marker_live);
		
		List<String[]> list = p.getPacket().getVideos();
		for(String[] d : list) {
			if (TextUtils.isEmpty(d[1]) || TextUtils.isEmpty(d[5]) || TextUtils.isEmpty(d[0])) {
				V2Log.e("===data formation incorrect ");
				continue;
			}
			long uid = Long.parseLong(d[1]);
			long vid = Long.parseLong(d[5]);
			long nid = Long.parseLong(d[0]);
			double lng = Double.parseDouble(d[2]);
			double lat = Double.parseDouble(d[3]);
			Live live = new Live(new User(uid), vid, lat, lng);
			live.setNid(nid);
			this.lives.append(vid, live);
			
			Bundle bundle = new Bundle();
			bundle.putSerializable("live", live);
			OverlayOptions oo = new MarkerOptions().icon(online)
					.position(new LatLng(live.getLat(), live.getLng())).extraInfo(bundle);
			Overlay ol = this.mapInstance.addOverlay(oo);
		}
		
	}
	
	boolean pending = true;
	private void handWatchRequestCallback(JNIResponse resp) {
		if (resp.getResult() == JNIResponse.Result.SUCCESS) {
			videoScreenState |= WATCHING_FLAG;
			RequestEnterConfResponse rer = (RequestEnterConfResponse)resp;
			if (this.currentLive.getPublisher() == null) {
				this.currentLive.setPublisher(new User(rer.getConf().getCreator()));
			} else {
				this.currentLive.getPublisher().setmUserId((rer.getConf().getCreator()));
			}
			pending = true;
		} else {
			pending = false;
			ui.showError(3);
		}
	}
	
	private void handleAttendDevice(AsyncResult ar) {
		if (pending) {
			// TODO waiting for chair man device;
			Object[] devices = (Object[]) ar.getResult();
			long uid = Long.parseLong(devices[0].toString());
			List<UserDeviceConfig> ll = (List<UserDeviceConfig>)devices[1];
			if (ll == null || ll.size()< 0) {
				V2Log.e("===== chair man no device" + uid);
				pending = false;
				return;
			}
			if (uid == this.currentLive.getPublisher().getmUserId()) {
				VideoPlayer vp = new VideoPlayer();
				vp.SetSurface(ui.getCurrentSurface().getHolder());
				// TODO open chairman device
				UserDeviceConfig udc = new UserDeviceConfig(0,
						this.currentLive.getLid(), currentLive.getPublisher()
								.getmUserId(), ll.get(0).getDeviceID(), vp);
				vs.requestOpenVideoDevice(
						new ConferenceGroup(this.currentLive.getLid(), null,
								null, null, null), udc, null);
				pending = false;

			} else {
				V2Log.e("=====not chair man" + uid);
			}

		}
		
	}
	
	
	private void handleNewMessage(MessageInd mi) {
		ui.queuedMessage(mi.content);
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
			case SEARCH_LIVE:
				ls.scanNear(currentMapCenter.ll.latitude,
						currentMapCenter.ll.longitude, 500000,
						new MessageListener(this, SEARCH_LIVE_CALLBACK, null));
				break;
			case SEARCH_LIVE_CALLBACK:
				handSearchLiveCallback((SearchLiveResponse)msg.obj);
				break;
			case WATCHING_REQUEST_CALLBACK:
				handWatchRequestCallback((JNIResponse)msg.obj);
				break;
			case ATTEND_LISTENER:
				handleAttendDevice((AsyncResult)msg.obj);
				break;
			case MESSAGE_LISTENER:
				handleNewMessage((MessageInd)(((AsyncResult)msg.obj).getResult()));
				break;
			}
		}
		
	}
	
	
	
	final class LocationWrapper {
		LatLng ll;

		public LocationWrapper() {
			super();
		}

		public LocationWrapper(LatLng ll) {
			super();
			this.ll = ll;
		}
		
		public LocationWrapper(LocationWrapper lw) {
			super();
			this.ll = lw.ll;
		}
		
	}

}
