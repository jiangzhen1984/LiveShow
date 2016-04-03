package com.v2tech.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.V2.jni.ImRequest;
import com.V2.jni.ind.V2Live;
import com.V2.jni.ind.V2Location;
import com.V2.jni.ind.V2User;
import com.V2.jni.ind.VideoCommentInd;
import com.V2.jni.util.V2Log;
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
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.camera.CameraView;
import com.v2tech.service.ConferenceService;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.LiveService;
import com.v2tech.service.MessageListener;
import com.v2tech.service.UserService;
import com.v2tech.service.jni.GetNeiborhoodResponse;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.service.jni.LiveNotification;
import com.v2tech.service.jni.RequestConfCreateResponse;
import com.v2tech.service.jni.RequestLogInResponse;
import com.v2tech.util.SPUtil;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.Conference;
import com.v2tech.vo.Live;
import com.v2tech.vo.User;
import com.v2tech.widget.VideoShowFragment;

public class CopyOfMainActivity extends FragmentActivity implements
		OnGetGeoCoderResultListener, UserControllerAPI {

	private static final int REQUEST_KEYBOARD_ACTIVITY = 100;
	private static final int REQUEST_LOGIN_ACTIVITY_CODE = 101;
	private static final int REQUEST_LOGIN_ACTIVITY_CODE_FOR_SHARE = 102;
	
	private static final String BUTTON_TAG_MAP = "map";
	private static final String BUTTON_TAG_WORD = "word";

	private static final int SEARCH = 1;
	private static final int AUTO_PLAY_LIVE = 2;
	private static final int PLAY_LIVE = 3;
	private static final int INTERVAL_GET_NEIBERHOOD = 4;
	private static final int UPDATE_LIVE_MARK = 5;
	private static final int START_PUBLISH = 8;
	private static final int STOP_PUBLISH = 9;
	private static final int GET_MAP_SNAPSHOT = 10;
	private static final int MARKER_ANIMATION = 12;
	private static final int DELAY_RESUME = 13;
	private static final int AUTO_LOGIN_CALL_BACK = 14;
	private static final int SCAN_CALL_BACK = 15;
	private static final int REQUEST_PUBLISH_CALLBACK = 16;
	private static final int REQUEST_FINISH_PUBLISH_CALLBACK = 17;
	private static final int NOTIFICATION_LIVE = 18;
	private static final int UPDATE_GPS = 19;
	private static final int ADD_FANDS_CALLBACK = 20;
	private static final int REMOVE_FANDS_CALLBACK = 21;
	private static final int VIDEO_COMMENT_IND = 22;

	private static float mCurrentZoomLevel = 12F;

	private RelativeLayout mBottomLayout;
	// private BottomButtonLayout mBottomButtonLayout;
	private EditText mEditText;
	private FrameLayout mMainLayout;
	private View mLocateButton;
	private Button mShareVideoButton;
	private FrameLayout videoShareLayout;

	private VideoControllerAPI mVideoController;
	private MapVideoLayout mMapVideoLayout;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocClient;
	private GeoCoder mSearch;
	private MyLocationListenner myListener = new MyLocationListenner();
	private boolean showCurrentLocation = true;// 是否首次定位
	private boolean isSuspended;

	private CameraView cv;
	private boolean isRecording = false;
	private boolean isInCameraView = false;

	private DisplayMetrics mDisplay;

	private Map<VideoOpt, VideoItem> videoMaps = new HashMap<VideoOpt, VideoItem>();
	private Map<Live, Overlay> currentOverlay = new HashMap<Live, Overlay>();
	private VideoOpt mCurrentVideoFragment;
	private ImageView mapSnapshot;

	private LatLng selfLocation;
	private LatLng currentVideoLocation;

	private LocalHandler mLocalHandler;
	private HandlerThread mHandlerThread;
	private View mPersonalButton;
	private UserService us;
	private String phone;
	private LiveService liveService;
	private ConferenceService confService;
	private List<Live> neiborhoodList;
	Conference currentLive;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		liveService = new LiveService();
		confService = new ConferenceService();
		neiborhoodList = new ArrayList<Live>();
		setContentView(R.layout.main_activity);
		mMainLayout = (FrameLayout) findViewById(R.id.main);

		mDisplay = getResources().getDisplayMetrics();
		us = new UserService();

		initMapviewLayout();
		initVideoShareLayout();
		initBottomButtonLayout();
		initTitleBarButtonLayout();
		initLocation();
		initResetOrder();

		mHandlerThread = new HandlerThread("back-end");
		mHandlerThread.start();
		while (!mHandlerThread.isAlive()) {
			try {
				wait(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mLocalHandler = new LocalHandler(mHandlerThread.getLooper());

		liveService.registerLiveNotification(new MessageListener(mLocalHandler, NOTIFICATION_LIVE, null));
		
		
		TelephonyManager tl = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		phone = SPUtil.getConfigStrValue(getApplicationContext(), "cellphone");
		String code = SPUtil.getConfigStrValue(getApplicationContext(), "code");
		if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(code)) {
			us.login(phone, code, new MessageListener(mLocalHandler, AUTO_LOGIN_CALL_BACK, false));
		} else {
			us.login((tl.getLine1Number() == null || tl.getLine1Number().isEmpty()) ? System.currentTimeMillis() + ""
					: tl.getLine1Number(), "111111", true, new MessageListener(mLocalHandler, AUTO_LOGIN_CALL_BACK, true));
//			ImRequest.getInstance().login(
//					tl.getLine1Number() == null ? System.currentTimeMillis() + ""
//							: tl.getLine1Number(), "111111",
//					V2GlobalEnum.USER_STATUS_ONLINE, V2ClientType.ANDROID, true);
		}
		
		liveService.setCommentsNotifier(new MessageListener(mLocalHandler, VIDEO_COMMENT_IND, null));

	}

	private void initLocation() {
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		CloudManager.getInstance().init(mLocalCloudListener);
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

	}

	private void initMapviewLayout() {
		mMapVideoLayout = new MapVideoLayout(this);

		mMapVideoLayout.setPosInterface(posChangedListener);
		mMapVideoLayout.setVideoChangedListener(videoFragmentChangedListener);
		mMapVideoLayout.setNotificationClickedListener(mOnNotificationClicked);
		mBaiduMap = mMapVideoLayout.getMap();
		mMapView = mMapVideoLayout.getMapView();

		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		mMainLayout.addView(mMapVideoLayout, fl);

		mBaiduMap.setOnMapStatusChangeListener(mMapStatusChangeListener);
		mBaiduMap.setOnMarkerClickListener(mMarkerClickerListener);
		
		mBaiduMap.setMyLocationEnabled(true);
		
		//mCurrentVideoFragment = mMapVideoLayout.getCurrentVideoFragment();
		VideoItem item = videoMaps.get(mCurrentVideoFragment);
		if (item == null) {
			videoMaps.put(mCurrentVideoFragment, new VideoItem(
					mCurrentVideoFragment));
		}
		
		mVideoController = mMapVideoLayout;
	}
	
	
	private void initTitleBarButtonLayout() {
		 View titleBar = findViewById(R.id.title_bar);
		 titleBar.bringToFront();
//		 titleBar.setBackgroundColor(Color.TRANSPARENT);
//		 titleBar.setAlpha(0.3F);
		 this.mPersonalButton =null;// findViewById(R.id.personal_button);
		 mPersonalButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (GlobalHolder.getInstance().getCurrentUser() != null) {
					Intent i = new Intent();
					i.setClass(getApplicationContext(), PersonalActivity.class);
					startActivity(i);
				} else {
					Intent i = new Intent();
					i.setClass(getApplicationContext(), LoginActivity.class);
					Intent pending = new Intent();
					pending.setClass(getApplicationContext(), PersonalActivity.class);
				    i.putExtra("pendingintent", pending);
					startActivity(i);
				}
			}
			 
		 });
		
	}

	private void initBottomButtonLayout() {
		mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);

		View button = findViewById(R.id.map_button);
		button.setTag(BUTTON_TAG_MAP);
		button.setOnClickListener(mBottomButtonClickedListener);

		button = findViewById(R.id.msg_button);
		button.setTag(BUTTON_TAG_WORD);
		button.setOnClickListener(mBottomButtonClickedListener);

		mEditText = (EditText) findViewById(R.id.edit_text);
		mEditText.setInputType(InputType.TYPE_NULL);
		mEditText.setFocusable(true);
		mEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mBottomLayout.setVisibility(View.INVISIBLE);
				Intent i = new Intent();
				i.setClass(mEditText.getContext(), BottomButtonLayoutActivity.class);
				startActivityForResult(i, REQUEST_KEYBOARD_ACTIVITY);
				
			}
			
		});


		mLocateButton = findViewById(R.id.map_locate_button);
		mLocateButton.setOnClickListener(mLocateClickListener);
		

	}

	
	private boolean initVideoShareLayoutFlag = true;
	private void initVideoShareLayout() {
		videoShareLayout = (FrameLayout) findViewById(R.id.video_share_ly);
		int width = getPreWidth();
		int height = getPreHeight(width);
		cv = new CameraView(this);
		cv.setZOrderOnTop(false);
		cv.setZOrderMediaOverlay(false);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(width, height - 80);
		fl.leftMargin = (mDisplay.widthPixels - width) / 2;
		videoShareLayout.addView(cv, fl);
		

		mShareVideoButton = new Button(this);
		mShareVideoButton.setText("分享视频");
		mShareVideoButton.setPadding(12, 12, 12, 12);
		mShareVideoButton.setTextColor(Color.GREEN);
		mShareVideoButton
				.setBackgroundResource(R.drawable.video_share_button_bg);
		mShareVideoButton.setTextSize(13);
		mShareVideoButton.setTag("none");
		mShareVideoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				if (GlobalHolder.getInstance().getCurrentUser() == null) {
//					Intent i = new Intent();
//					i.setClass(getApplicationContext(), LoginActivity.class);
//					startActivityForResult(i, REQUEST_LOGIN_ACTIVITY_CODE_FOR_SHARE);
//					return;
//				}
				if ("none".equals(mShareVideoButton.getTag())) {
					mShareVideoButton.setTag("recording");
					mShareVideoButton.setText("取消分享");
					Message.obtain(mLocalHandler, START_PUBLISH).sendToTarget();
				} else {
					mShareVideoButton.setTag("none");
					mShareVideoButton.setText("分享视频");
					Message.obtain(mLocalHandler, STOP_PUBLISH).sendToTarget();
					mMapVideoLayout.requestUpFlying();
				}
			}

		});

		mapSnapshot = new ImageView(this);
		mapSnapshot.setImageResource(R.drawable.bg);
		mapSnapshot.setScaleType(ScaleType.FIT_CENTER);
		mapSnapshot.setAlpha(0.3F);
		mapSnapshot.setOnTouchListener(dragListener);
		FrameLayout.LayoutParams flMapView = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, mDisplay.heightPixels
						- height);
		flMapView.topMargin = height;
		videoShareLayout.addView(mapSnapshot, flMapView);

		FrameLayout.LayoutParams buttonfl = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		mShareVideoButton.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		buttonfl.leftMargin = (mDisplay.widthPixels - mShareVideoButton
				.getMeasuredWidth()) / 2;
		buttonfl.topMargin = height
				+ (mDisplay.heightPixels - height - mShareVideoButton
						.getMeasuredHeight()) / 2;
		videoShareLayout.addView(mShareVideoButton, buttonfl);

		
	}
	
	
	private void initResetOrder() {
		mBottomLayout.bringToFront();
		mPersonalButton.bringToFront();
		mLocateButton.bringToFront();
	}

	private int getPreWidth() {
		return mDisplay.widthPixels - mDisplay.widthPixels % 16;
	}

	private int getPreHeight(int width) {
		int height = width / 4 * 3;
		return height - height % 16;
	}

	@Override
	protected void onStart() {
		super.onStart();
		isSuspended = false;
		if (isRecording) {

		} else {
			if (mCurrentVideoFragment != null && mCurrentVideoFragment.getCurrentLive() != null)  {
//				//FIXME send delay message since after surface view is created
//				Message m = Message.obtain(mLocalHandler, DELAY_RESUME);
//				mLocalHandler.sendMessageDelayed(m, 1000);
				
				((VideoShowFragment)mCurrentVideoFragment).setStateListener(mCurrentVideoFragmentListener);
				
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onStop() {
		isSuspended = true;
		if (cv != null) {
			cv.stopPreView();
		}
		if (isRecording) {
			Message.obtain(mLocalHandler, STOP_PUBLISH).sendToTarget();
		} else {
			if (mCurrentVideoFragment != null) {
				mCurrentVideoFragment.stop();
			}
		}
		super.onStop();
	}
	
	
	

	@Override
	public void addFans(long userId) {
		liveService.addFans(userId, new MessageListener(mLocalHandler, ADD_FANDS_CALLBACK, null));
		
	}

	@Override
	public void removeFans(long userId) {
		liveService.addFans(userId, new MessageListener(mLocalHandler, REMOVE_FANDS_CALLBACK, null));
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocalHandler.removeMessages(MARKER_ANIMATION);
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();

		CloudManager.getInstance().destroy();
		mSearch.destroy();

		mHandlerThread.quit();

		mLocalHandler = null;
		ImRequest.getInstance().ImLogout();
		liveService.clearCalledBack();
		neiborhoodList.clear();
		
		confService.clearCalledBack();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED) {
			mBottomLayout.setVisibility(View.VISIBLE);
			mLocateButton.setVisibility(View.VISIBLE);
			initResetOrder();
			return;
		}
		
		if (requestCode == 	REQUEST_LOGIN_ACTIVITY_CODE_FOR_SHARE) {
			mShareVideoButton.performClick();
			return;
		} else if (requestCode == REQUEST_KEYBOARD_ACTIVITY) {
			mBottomLayout.setVisibility(View.VISIBLE);
			mLocateButton.setVisibility(View.VISIBLE);
			initResetOrder();
			if (data == null || data.getExtras() == null) {
				return;
			}
			int action = data.getExtras().getInt("action");
			String text = data.getExtras().getString("text");
			if (TextUtils.isEmpty(text)) {
				return;
			}
			if (resultCode == Activity.RESULT_OK) {
				//click map button
				if (action == 1) {
					mLocalHandler.removeMessages(SEARCH);
					Message msg = Message.obtain(mLocalHandler, SEARCH, text);
					mLocalHandler.sendMessage(msg);
					//click word button
				} else if (action == 2) {
					mVideoController.addNewMessage(text);
					liveService.sendComments(mCurrentVideoFragment.getCurrentLive().getPublisher().getmUserId(), text);
				}
					
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void stopCamera() {
		cv.stopPreView();
	}


	float initY;
	float lastY;
	float offsetY;
	int mActivePointerId;

	private OnTouchListener dragListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				stopCamera();
				mActivePointerId = MotionEventCompat.getPointerId(event, 0);
				initY = MotionEventCompat.getY(event, 0);
				lastY = initY;
				mMapVideoLayout.pauseDrawState(true);
				break;
			case MotionEvent.ACTION_MOVE:
				final int pointerIndex = MotionEventCompat.findPointerIndex(
						event, mActivePointerId);
				final float y = MotionEventCompat.getY(event, pointerIndex);
				final float dy = y - lastY;
				mMapVideoLayout.updateOffset((int) dy);
				lastY = y;
				break;
			case MotionEvent.ACTION_UP:
				V2Log.d("Start translate");
				mMapVideoLayout.requestUpFlying();
				break;
			}
			return true;
		}

	};

	private MapVideoLayout.LayoutPositionChangedListener posChangedListener = new MapVideoLayout.LayoutPositionChangedListener() {

		@Override
		public void onFlyingOut() {
			if (!initVideoShareLayoutFlag) {
				initVideoShareLayout();
				initVideoShareLayoutFlag = true;
			}
			if (!isRecording) {
				cv.startPreView();
			}
			isInCameraView = true;
			updateCurrentVideoState(mCurrentVideoFragment, false);
			mLocateButton.setVisibility(View.GONE);
			mBottomLayout.setVisibility(View.GONE);
		}

		@Override
		public void onFlyingIn() {
			if (initVideoShareLayoutFlag) {
				cv.stopPreView();
			}
			
			isInCameraView = false;
			updateCurrentVideoState(mCurrentVideoFragment, true);
			mBottomLayout.setVisibility(View.VISIBLE);
			mLocateButton.setVisibility(View.VISIBLE);
			mMapVideoLayout.pauseDrawState(false);
			initResetOrder();
		}
		
		@Override
		public void onPreparedFlyingIn() {
			
		}
		
		
		@Override
		public void onPreparedFlyingOut() {
			onFlyingOut();
		}

		@Override
		public void onDrag() {
			// TODO Auto-generated method stub
			
		}
		
		
	};

	private MapVideoLayout.OnVideoFragmentChangedListener videoFragmentChangedListener = new MapVideoLayout.OnVideoFragmentChangedListener() {

		@Override
		public void onChanged(VideoShowFragment videoFrag) {
			if (mCurrentVideoFragment != null && mCurrentVideoFragment.getCurrentLive()!= null) {
				animationMaker(mCurrentVideoFragment.getCurrentLive(), false);
			}
			updateCurrentVideoState(mCurrentVideoFragment, false);
//			mCurrentVideoFragment = videoFrag;
//			updateCurrentVideoState(videoFrag, true);
//
//			VideoItem item = videoMaps.get(videoFrag);
//			if (item == null) {
//				videoMaps.put(videoFrag, new VideoItem(videoFrag));
//				autoPlayNecessary();
//			} else {
//				Live cl = videoFrag.getCurrentLive();
//				if (cl != null) {
//					updateMapLocation(cl);
//				}
//			}
			//bring to front, To make sure surface to show 
			mMapVideoLayout.bringToFront();

		}

	};

	private void updateCurrentVideoState(VideoOpt videoOpt, boolean play) {
		if (play) {
			videoOpt.resume();
		} else {
			videoOpt.pause();
		}
	}

	private BDLocation mCacheLocation;

	private void doSearch(String key) {
		mSearch.geocode(new GeoCodeOption().city("北京").address(key));
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(CopyOfMainActivity.this, "抱歉，未能找到位置", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));

		lat = result.getLocation().latitude;
		lng = result.getLocation().longitude;
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {

	}

	private double lat;
	private double lng;

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;

			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			selfLocation = ll;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(ll.latitude)
					.longitude(ll.longitude).build();
			mBaiduMap.setMyLocationData(locData);
			if (showCurrentLocation) {
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(
						selfLocation, mCurrentZoomLevel);
				if (u == null) {
					return;
				}
				mBaiduMap.animateMapStatus(u);
				mLocateButton.setTag(new LocationItem(LocationItemType.SELF,
						selfLocation));
				
			}

		

			if (mCacheLocation == null
					|| (mCacheLocation.getLongitude() != location
							.getLongitude() || mCacheLocation.getLatitude() != location
							.getLatitude())) {
				mCacheLocation = location;
			   
				updateGPS();
			
				mLocalHandler.sendEmptyMessageDelayed(INTERVAL_GET_NEIBERHOOD,
						1000);
				
				lat = location.getLatitude();
				lng = location.getLongitude();
				

			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	
	
	private void updateGPS() {
		mLocalHandler.sendMessageDelayed(Message.obtain(mLocalHandler,
				UPDATE_GPS, new Double[] { mCacheLocation.getLatitude(),
				mCacheLocation.getLongitude() }), 2000);
	}

	private LocalState mSearchState = LocalState.DONE;

	private CloudListener mLocalCloudListener = new CloudListener() {

		@Override
		public void onGetDetailSearchResult(DetailSearchResult arg0, int arg1) {

		}

		@Override
		public void onGetSearchResult(CloudSearchResult result, int error) {
			if (result != null && result.poiList != null
					&& result.poiList.size() > 0) {
				mBaiduMap.clear();
				BitmapDescriptor bd = BitmapDescriptorFactory
						.fromResource(R.drawable.icon_gcoding);
				LatLng ll;
				LatLngBounds.Builder builder = new Builder();
				for (CloudPoiInfo info : result.poiList) {
					ll = new LatLng(info.latitude, info.longitude);
					OverlayOptions oo = new MarkerOptions().icon(bd).position(
							ll);
					mBaiduMap.addOverlay(oo);
					builder.include(ll);
				}
				LatLngBounds bounds = builder.build();
				MapStatusUpdate u = MapStatusUpdateFactory
						.newLatLngBounds(bounds);
				mBaiduMap.animateMapStatus(u);
			}
		}

	};

	private void updateLiveMarkOnMap(List<Live> list) {
		mBaiduMap.clear();
		currentOverlay.clear();
		BitmapDescriptor online = BitmapDescriptorFactory
				.fromResource(R.drawable.marker_live);
		BitmapDescriptor live = BitmapDescriptorFactory
				.fromResource(R.drawable.marker_live);
		for (Live l : list) {
			LatLng ll = new LatLng(l.getLat(), l.getLng());
			Bundle bundle = new Bundle();
			if (l.getUrl() == null || l.getUrl().isEmpty()) {
				OverlayOptions oo = new MarkerOptions().icon(online)
						.position(ll).extraInfo(bundle);
				Overlay ol = mBaiduMap.addOverlay(oo);
				// cache overlay
				currentOverlay.put(l, ol);
			} else {
				bundle.putSerializable("live", l);
				OverlayOptions oo = new MarkerOptions().icon(live).position(ll)
						.extraInfo(bundle);
				Overlay ol = mBaiduMap.addOverlay(oo);
				currentOverlay.put(l, ol);
			}
		}

	}

	private void animationMaker(Live l, boolean show) {
		Overlay old = currentOverlay.get(l);
		if (old != null) {
			old.remove();
		}

		BitmapDescriptor online = BitmapDescriptorFactory
				.fromResource(R.drawable.marker_live);
		BitmapDescriptor onlineRed = BitmapDescriptorFactory
				.fromResource(R.drawable.marker_live_show);

		LatLng ll = new LatLng(l.getLat(), l.getLng());
		OverlayOptions oo = new MarkerOptions().icon(show ? onlineRed : online)
				.position(ll);
		Overlay ol = mBaiduMap.addOverlay(oo);
		currentOverlay.put(l, ol);
	}

	private void playLive(Live l) {
		if (mCurrentVideoFragment != null) {
			Live old = mCurrentVideoFragment.getCurrentLive();
			if (old != null) {
				animationMaker(old, false);
			}
		}
		
		showCurrentLocation = false;
		
		mCurrentVideoFragment.play(l);

		videoMaps.get(mCurrentVideoFragment).live = l;
		if (l.getLat() <= 0 || l.getLng() <=0) {
			return;
		}
		updateMapLocation(l);
		// Start new live marker animation
		animationMaker(l, true);
		//
		mLocalHandler.removeMessages(MARKER_ANIMATION);
		Message delayMessage = Message.obtain(mLocalHandler, MARKER_ANIMATION,
				0, 0);
		mLocalHandler.sendMessageDelayed(delayMessage, 200);
	}

	private void updateMapLocation(Live l) {
		currentVideoLocation = new LatLng(l.getLat(), l.getLng());
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(
				currentVideoLocation, mCurrentZoomLevel);
		mBaiduMap.animateMapStatus(u);

		mLocateButton.setTag(new LocationItem(LocationItemType.VIDEO,
				currentVideoLocation));
	}

	private boolean autoPlayNecessary() {
		if (mCurrentVideoFragment.isPlaying() || mCurrentVideoFragment.isPause()) {
				return false;
		}
		for (int i = 0; i < neiborhoodList.size(); i++) {
			Live live = neiborhoodList.get(i);
			boolean inUsed = false;
			for (VideoItem item : videoMaps.values()) {
				if (item.live == null) {
					continue;
				}
				if (item.live.equals(live)) {
					inUsed = true;
					break;
				}
			}
			if (inUsed) {
				continue;
			} else {
				if (!TextUtils.isEmpty(live.getUrl())) {
					playLive(live);
					return true;
				}
			}
		}
		return false;

	}

	private void getMapSnapshot() {
		mBaiduMap.snapshot(new SnapshotReadyCallback() {

			@Override
			public void onSnapshotReady(Bitmap bm) {
				// mapSnapshot.setImageBitmap(bm);
				mMapVideoLayout.udpateCover(bm);
			}

		});

	}

	private OnClickListener mLocateClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			LocationItem li = (LocationItem) v.getTag();
			if (li == null) {
				return;
			}
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(li.ll,
					mCurrentZoomLevel);
			mBaiduMap.animateMapStatus(u);
			if (li.type == LocationItemType.SELF) {
				if (currentVideoLocation != null) {
					li.type = LocationItemType.VIDEO;
					li.ll = currentVideoLocation;
				}
			} else {
				if (selfLocation != null) {
					li.type = LocationItemType.SELF;
					li.ll = selfLocation;
				}
			}
		}

	};

	private BaiduMap.OnMarkerClickListener mMarkerClickerListener = new BaiduMap.OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker marker) {
			if (marker.getExtraInfo() != null) {
				Live l = (Live) marker.getExtraInfo().getSerializable("live");
				if (l != null && !TextUtils.isEmpty(l.getUrl())) {
					//liveService.addFans(l);
					if (mCurrentVideoFragment != null) {
						updateCurrentVideoState(mCurrentVideoFragment, false);
					}
					
					if (mMapVideoLayout.getVideoWindowNums() < 6) {
						mCurrentVideoFragment = mVideoController.addNewVideoWindow(l);
					} else {
						//Replace current
						Message.obtain(mLocalHandler, PLAY_LIVE, l).sendToTarget();
					}
				}
			}
			return true;
		}

	};

	private OnMapStatusChangeListener mMapStatusChangeListener = new OnMapStatusChangeListener() {

		@Override
		public void onMapStatusChange(MapStatus arg0) {

		}

		@Override
		public void onMapStatusChangeFinish(MapStatus mp) {
			mLocalHandler.sendEmptyMessageDelayed(GET_MAP_SNAPSHOT, 1000);
		}

		@Override
		public void onMapStatusChangeStart(MapStatus arg0) {

		}

	};

	private OnClickListener mBottomButtonClickedListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (BUTTON_TAG_MAP.equals(v.getTag())) {
				mLocalHandler.removeMessages(SEARCH);
				Message msg = Message.obtain(mLocalHandler, SEARCH, mEditText
						.getText().toString());
				mLocalHandler.sendMessage(msg);
			} else if (BUTTON_TAG_WORD.equals(v.getTag())) {
				mVideoController.addNewMessage(mEditText.getText().toString());
				liveService.sendComments(mCurrentVideoFragment.getCurrentLive().getPublisher().getmUserId(),mEditText.getText().toString());
			}

			mEditText.setText("");

		}

	};
	
	
	private VideoShowFragment.VideoFragmentStateListener mCurrentVideoFragmentListener = new VideoShowFragment.VideoFragmentStateListener() {

		@Override
		public void onInited() {
			mCurrentVideoFragment.restart();
			((VideoShowFragment)mCurrentVideoFragment).setStateListener(null);
		}

		@Override
		public void onUnInited() {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	
	
	private MapVideoLayout.OnNotificationClickedListener mOnNotificationClicked = new MapVideoLayout.OnNotificationClickedListener() {

		@Override
		public void onNotificationClicked(View v, Live live, User u) {
			playLive(live);
			mMapVideoLayout.removeLiveNotificaiton(live);
		}
		
	};
	
	
	private void handleRequestPublishCallback(Message msg) {
		JNIResponse resp = (JNIResponse)msg.obj;
		if (resp.getResult() == JNIResponse.Result.SUCCESS) {
			RequestConfCreateResponse  hr = (RequestConfCreateResponse)resp;
			currentLive.updateConfId(hr.getConfId());
			
		} else {
			Toast.makeText(getBaseContext(), "发布视频错误:" + resp.getResult(), Toast.LENGTH_SHORT).show();
		}
	}
	
	


	private List<Live> getTestlist() {
		List<V2Live> list = new ArrayList<V2Live>();
		
		V2Live live = new V2Live();
		live.uuid = "8002";
		live.url = "http://" + Constants.SERVER + ":8090/hls/2004"+ ".m3u8";
		V2User v2user = new V2User();
		v2user.uid = 1;
		live.publisher = v2user;
		V2Location v2location = new V2Location();
		v2location.lat = 39.978437D;
		v2location.lng =116.294172D;
		live.location = v2location;
		
		list.add(live);		
		
		
		live = new V2Live();
		live.uuid = "8001";
		live.url = "http://" + Constants.SERVER + ":8090/hls/"+ live.uuid+ ".m3u8";
		v2user = new V2User();
		v2user.uid = 2;
		live.publisher = v2user;
		v2location = new V2Location();
		v2location.lat = 39.984186D;
		v2location.lng =116.449975D;
		live.location = v2location;
		list.add(live);	
		
		
		live = new V2Live();
		live.uuid = "8002";
		live.url = "http://" + Constants.SERVER + ":8090/hls/"+ live.uuid+ ".m3u8";
		v2user = new V2User();
		v2user.uid = 3;
		live.publisher = v2user;
		v2location = new V2Location();
		v2location.lat = 39.873527D;
		v2location.lng =116.308545D;
		live.location = v2location;
		list.add(live);	
		
		live = new V2Live();
		live.uuid = "8001";
		live.url = "http://" + Constants.SERVER + ":8090/hls/"+ live.uuid+ ".m3u8";
		v2user = new V2User();
		v2user.uid = 4;
		live.publisher = v2user;
		v2location = new V2Location();
		v2location.lat = 39.871312D;
		v2location.lng =116.466072D;
		live.location = v2location;
		list.add(live);	
		
		live = new V2Live();
		live.uuid = "8004";
		live.url = "http://" + Constants.SERVER + ":8090/hls/"+ live.uuid+ ".m3u8";
		v2user = new V2User();
		v2user.uid = 6;
		live.publisher = v2user;
		v2location = new V2Location();
		v2location.lat = 39.926224D;
		v2location.lng =116.361438D;
		live.location = v2location;
		list.add(live);	
		
		live = new V2Live();
		live.uuid = "8001";
		live.url = "http://" + Constants.SERVER + ":8090/hls/"+ live.uuid+ ".m3u8";
		v2user = new V2User();
		v2user.uid = 7;
		live.publisher = v2user;
		v2location = new V2Location();
		v2location.lat = 39.917813D;
		v2location.lng =116.444225D;
		live.location = v2location;
		list.add(live);	
		
		
		
		List<Live> l = new ArrayList<Live>(list.size());
		for (V2Live v2live : list) {
			l.add(new Live(new User(v2live.publisher.uid, v2live.publisher.name), v2live.url, v2live.location.lat, v2live.location.lng));
		}
		return l;
	}
		

	
	
	
	private void handleGetNeihoodrCallback(Message msg) {
		JNIResponse resp = (JNIResponse)msg.obj;
		if (resp.getResult() == JNIResponse.Result.SUCCESS) {
			GetNeiborhoodResponse hr = (GetNeiborhoodResponse)resp;
			neiborhoodList.clear();
			neiborhoodList.addAll(hr.list);
			neiborhoodList.addAll(getTestlist());
			updateLiveMarkOnMap(neiborhoodList);
			if (!isInCameraView) {
				autoPlayNecessary();
			}
			
		} else {
			//FIXME use 
			neiborhoodList.clear();
			neiborhoodList.addAll(getTestlist());
			updateLiveMarkOnMap(neiborhoodList);
			if (!isInCameraView) {
				autoPlayNecessary();
			}
	
		}
	}
	
	private void handleLiveNotification(Message msg) {
		final LiveNotification ln = (LiveNotification) msg.obj;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				
				V2Log.e("handleLiveNotification" + ln.live);
				if (ln.live != null) {
					if (ln.type == LiveNotification.TYPE_START) {
						mMapVideoLayout.addLiveNotificaiton(ln.live);
					} else if (ln.type == LiveNotification.TYPE_STOPPED) {
						mMapVideoLayout.removeLiveNotificaiton(ln.live);
					}
				}

			}

		});
			
	}


	class LocalHandler extends Handler {

		public LocalHandler(Looper looper) {
			super(looper);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SEARCH:
				synchronized (mSearchState) {
					mSearchState = LocalState.DONING;
					doSearch((String) msg.obj);
				}
				break;
			case AUTO_PLAY_LIVE:
				if (!isInCameraView) {
					autoPlayNecessary();
				}
				
				break;
			case PLAY_LIVE:
				playLive((Live) msg.obj);
				break;
			case INTERVAL_GET_NEIBERHOOD:
				liveService.scanNear(lat, lng, 1000F, new MessageListener(this, SCAN_CALL_BACK, null));
				if (!isSuspended) {
					mLocalHandler.sendEmptyMessageDelayed(
							INTERVAL_GET_NEIBERHOOD, 5000);
				}
				break;
				
			case UPDATE_GPS:
				liveService.updateGps(((Double[])msg.obj)[0],((Double[])msg.obj)[1]);
				updateGPS();
				break;
					
				
			case UPDATE_LIVE_MARK:
				break;
			case START_PUBLISH:
//				VideoBCRequest.getInstance().startLive();
//				Message m = Message.obtain(this, RECORDING);
//				this.sendMessageDelayed(m, 300);
				//liveService.requestPublish(new MessageListener(this, REQUEST_PUBLISH_CALLBACK, null));
				currentLive = new Conference(0, GlobalHolder.getInstance().getCurrentUserId());
				confService.createConference(currentLive, new MessageListener(this, REQUEST_PUBLISH_CALLBACK, null));
				break;
			case REQUEST_PUBLISH_CALLBACK:
				handleRequestPublishCallback(msg);
				break;
			case STOP_PUBLISH:
				//VideoBCRequest.getInstance().stopLive();
//				liveService.requestFinishPublish(null);
//				cv.stopPublish();
				confService.quitConference(currentLive, null);
				currentLive = null;
				isRecording = false;
				currentLive = null;
				break;
			case GET_MAP_SNAPSHOT:
				getMapSnapshot();
				break;
			case MARKER_ANIMATION:
				if (mCurrentVideoFragment.getCurrentLive() != null) {
					animationMaker(mCurrentVideoFragment.getCurrentLive(),
							msg.arg1 == 0 ? false : true);
				} else {
				}
				Message delayMessage = Message.obtain(mLocalHandler,
						MARKER_ANIMATION, msg.arg1 == 0 ? 1 : 0, 0);
				mLocalHandler.sendMessageDelayed(delayMessage, 200);
				break;
			case DELAY_RESUME:
				if (!isSuspended) {
					mCurrentVideoFragment.restart();
				}
				break;
			case AUTO_LOGIN_CALL_BACK:{
				JNIResponse resp = (JNIResponse)msg.obj;
				if (resp.getResult() == JNIResponse.Result.SUCCESS) {
					if ((Boolean)resp.callerObject == true) {
						RequestLogInResponse lir = (RequestLogInResponse)resp;
						GlobalHolder.getInstance().nyUserId = lir.getUser().getmUserId();
					} else {
						RequestLogInResponse lir = (RequestLogInResponse)resp;
						lir.getUser().setName(phone);
						GlobalHolder.getInstance().setCurrentUser(lir.getUser());
					}
				}
				break;
			}
			case SCAN_CALL_BACK:
				handleGetNeihoodrCallback(msg);
				break;
			case NOTIFICATION_LIVE:
				handleLiveNotification(msg);
				break;
			case VIDEO_COMMENT_IND:
				VideoCommentInd vci = (VideoCommentInd)msg.obj;
				if (mCurrentVideoFragment.getCurrentLive().getPublisher().getmUserId() == vci.userId) {
					mVideoController.addNewMessage(vci.msg);
				}
				break;
			}
		}

	};

	class LocationItem {
		LocationItemType type;
		LatLng ll;

		public LocationItem(LocationItemType type, LatLng ll) {
			super();
			this.type = type;
			this.ll = ll;
		}

	}

	class VideoItem {
		VideoOpt videoOpt;
		Live live;

		public VideoItem(VideoOpt videoOpt) {
			this.videoOpt = videoOpt;
		}

	}

	enum LocationItemType {
		VIDEO, SELF
	}

	enum LocalState {
		DONING, DONE;
	}

	enum DragDirection {
		NONE, VERTICAL, HORIZONTAL;
	}
}
