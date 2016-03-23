package com.v2tech.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import v2av.VideoRecorder;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.V2.jni.ind.VideoCommentInd;
import com.V2.jni.util.V2Log;
import com.baidu.location.BDLocation;
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
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.camera.CameraView;
import com.v2tech.presenter.MainPresenter;
import com.v2tech.service.DeviceService;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.service.jni.LiveNotification;
import com.v2tech.service.jni.RequestConfCreateResponse;
import com.v2tech.service.jni.RequestLogInResponse;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.Conference;
import com.v2tech.vo.Live;
import com.v2tech.vo.User;
import com.v2tech.vo.UserDeviceConfig;
import com.v2tech.widget.VideoShowFragment;

public class MainActivity extends FragmentActivity implements
		View.OnClickListener, MainPresenter.MainPresenterUI {

	private static final int REQUEST_KEYBOARD_ACTIVITY = 100;
	private static final int REQUEST_LOGIN_ACTIVITY_CODE = 101;
	private static final int REQUEST_LOGIN_ACTIVITY_CODE_FOR_SHARE = 102;
	private static final int REQUEST_PERSONAL_ACTIVITY = 103;
	
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
	private boolean showCurrentLocation = true;// 是否首次定位
	private boolean isSuspended;

	private CameraView cv;
	private boolean isRecording = false;
	private boolean isInCameraView = false;

	private DisplayMetrics mDisplay;

	private Map<VideoOpt, VideoItem> videoMaps = new HashMap<VideoOpt, VideoItem>();
	private Map<Live, Overlay> currentOverlay = new HashMap<Live, Overlay>();
	private VideoOpt mCurrentVideoFragment;

	private LatLng selfLocation;
	private LatLng currentVideoLocation;

	private LocalHandler mLocalHandler;
	private HandlerThread mHandlerThread;
	private ImageView mPersonalButton;
	private String phone;
	Conference currentLive;

	
	MainPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (presenter == null) {
			presenter = new MainPresenter(this, this);
		}
		setContentView(R.layout.main_activity);
		mMainLayout = (FrameLayout) findViewById(R.id.main);

		mDisplay = getResources().getDisplayMetrics();

		initMapviewLayout();
		initVideoShareLayout();
		initBottomButtonLayout();
		initTitleBarButtonLayout();
		initResetOrder();

		presenter.uicreated();

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
		
		mCurrentVideoFragment = mMapVideoLayout.getCurrentVideoFragment();
		VideoItem item = videoMaps.get(mCurrentVideoFragment);
		if (item == null) {
			videoMaps.put(mCurrentVideoFragment, new VideoItem(
					mCurrentVideoFragment));
		}
		
		mVideoController = mMapVideoLayout;
	}
	
	
	private void initTitleBarButtonLayout() {
//		 View titleBar = findViewById(R.id.title_bar);
//		 titleBar.bringToFront();
		 this.mPersonalButton = (ImageView)findViewById(R.id.title_bar_left_btn);
		 this.mPersonalButton.setImageResource(R.drawable.user_icon);
		 mPersonalButton.setOnClickListener(this);

	}

	private void initBottomButtonLayout() {
		mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);

		View button = findViewById(R.id.map_button);
		button.setTag(BUTTON_TAG_MAP);
		button.setOnClickListener(this);

		button = findViewById(R.id.msg_button);
		button.setTag(BUTTON_TAG_WORD);
		button.setOnClickListener(this);

		mEditText = (EditText) findViewById(R.id.edit_text);
		mEditText.setInputType(InputType.TYPE_NULL);
		mEditText.setFocusable(true);
		mEditText.setOnClickListener(this);

		mLocateButton = findViewById(R.id.map_locate_button);
		mLocateButton.setOnClickListener(this);
		

	}

	
	private boolean initVideoShareLayoutFlag = true;
	private SurfaceView localSurfaceView;
	private void initVideoShareLayout() {
		
		videoShareLayout = (FrameLayout)findViewById(R.id.video_share_ly);
		mShareVideoButton = (Button)findViewById(R.id.video_share_button);
		mShareVideoButton.setOnClickListener(this);
		localSurfaceView = (SurfaceView)videoShareLayout.findViewById(R.id.local_camera_view);
		VideoRecorder.VideoPreviewSurfaceHolder = localSurfaceView.getHolder();
		VideoRecorder.VideoPreviewSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
	}
	
	
	private void initResetOrder() {
		mBottomLayout.bringToFront();
		mPersonalButton.bringToFront();
		mLocateButton.bringToFront();
	}


	@Override
	protected void onStart() {
		super.onStart();
		presenter.onStart();
	
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
		super.onStop();
		presenter.onStop();
	}
	
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	//	mLocalHandler.removeMessages(MARKER_ANIMATION);
		// 退出时销毁定位
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();

		CloudManager.getInstance().destroy();

		mLocalHandler = null;
		presenter.onUIDestroy();
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_KEYBOARD_ACTIVITY) {
			presenter.onKeyboardChildUIFinished(requestCode, data);
		} else if (requestCode == REQUEST_LOGIN_ACTIVITY_CODE) {
			presenter.onLoginChildUIFinished(requestCode, data);
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

	DeviceService ds = new DeviceService();
	private MapVideoLayout.LayoutPositionChangedListener posChangedListener = new MapVideoLayout.LayoutPositionChangedListener() {

		@Override
		public void onFlyingOut() {
			if (!initVideoShareLayoutFlag) {
				initVideoShareLayout();
				initVideoShareLayoutFlag = true;
			}
			if (!isRecording) {
				UserDeviceConfig duc = new UserDeviceConfig(0, 0, GlobalHolder.getInstance().getCurrentUserId(), "", null);
				duc.setSVHolder(localSurfaceView);
				ds.requestOpenVideoDevice(duc, null);
				//cv.startPreView();
			}
			isInCameraView = true;
			updateCurrentVideoState(mCurrentVideoFragment, false);
			mLocateButton.setVisibility(View.GONE);
			mBottomLayout.setVisibility(View.GONE);
		}

		@Override
		public void onFlyingIn() {
			if (initVideoShareLayoutFlag) {
				//cv.stopPreView();
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
	};

	private MapVideoLayout.OnVideoFragmentChangedListener videoFragmentChangedListener = new MapVideoLayout.OnVideoFragmentChangedListener() {

		@Override
		public void onChanged(VideoShowFragment videoFrag) {
			if (mCurrentVideoFragment != null && mCurrentVideoFragment.getCurrentLive()!= null) {
				animationMaker(mCurrentVideoFragment.getCurrentLive(), false);
			}
			updateCurrentVideoState(mCurrentVideoFragment, false);
			mCurrentVideoFragment = videoFrag;
			updateCurrentVideoState(videoFrag, true);

			VideoItem item = videoMaps.get(videoFrag);
			if (item == null) {
				videoMaps.put(videoFrag, new VideoItem(videoFrag));
				autoPlayNecessary();
			} else {
				Live cl = videoFrag.getCurrentLive();
				if (cl != null) {
					updateMapLocation(cl);
				}
			}
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






	
	
	private void updateGPS() {
//		mLocalHandler.sendMessageDelayed(Message.obtain(mLocalHandler,
//				UPDATE_GPS, new Double[] { mCacheLocation.getLatitude(),
//				mCacheLocation.getLongitude() }), 2000);
	}

	private LocalState mSearchState = LocalState.DONE;


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
//		for (int i = 0; i < neiborhoodList.size(); i++) {
//			Live live = neiborhoodList.get(i);
//			boolean inUsed = false;
//			for (VideoItem item : videoMaps.values()) {
//				if (item.live == null) {
//					continue;
//				}
//				if (item.live.equals(live)) {
//					inUsed = true;
//					break;
//				}
//			}
//			if (inUsed) {
//				continue;
//			} else {
//				if (!TextUtils.isEmpty(live.getUrl())) {
//					playLive(live);
//					return true;
//				}
//			}
//		}
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



	private BaiduMap.OnMarkerClickListener mMarkerClickerListener = new BaiduMap.OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker marker) {
			if (marker.getExtraInfo() != null) {
				Live l = (Live) marker.getExtraInfo().getSerializable("live");
				if (l != null && !TextUtils.isEmpty(l.getUrl())) {
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
			//mLocalHandler.sendEmptyMessageDelayed(GET_MAP_SNAPSHOT, 1000);
		}

		@Override
		public void onMapStatusChangeStart(MapStatus arg0) {

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
	
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	


	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id) {
		case R.id.video_share_button:
			presenter.videoShareButtonClicked();
			break;
		case R.id.edit_text:
			presenter.textClicked();
			break;
		case R.id.map_button:
			presenter.mapSearchButtonClicked();
			break;
		case R.id.msg_button:
			presenter.sendMessageButtonClicked();
			break;
		case R.id.title_bar_left_btn:
			presenter.personelButtonClicked();
			break;
		case R.id.map_locate_button:
			presenter.mapLocationButtonClicked();
		}
	}

	@Override
	public void resetUIDisplayOrder() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showTextKeyboard(boolean flag) {
		if (flag) {
			mBottomLayout.setVisibility(View.INVISIBLE);
			Intent i = new Intent();
			i.setClass(mEditText.getContext(), BottomButtonLayoutActivity.class);
			startActivityForResult(i, REQUEST_KEYBOARD_ACTIVITY);
		} else {
			mBottomLayout.setVisibility(View.VISIBLE);
			mLocateButton.setVisibility(View.VISIBLE);
		}
		
	}

	@Override
	public void showVideoScreentItem(int tag, boolean showFlag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetMapCenter(double lat, double lng, int zoom) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showLoginUI() {
		Intent i = new Intent();
		i.setClass(getApplicationContext(), LoginActivity.class);
		this.startActivityForResult(i, REQUEST_LOGIN_ACTIVITY_CODE);
	}

	@Override
	public void showPersonelUI() {
		Intent i = new Intent();
		i.setClass(getApplicationContext(), PersonalActivity.class);
		this.startActivityForResult(i, REQUEST_PERSONAL_ACTIVITY);
	}
	
	

	@Override
	public String getTextString() {
		return mEditText.getEditableText().toString();
	}

	@Override
	public BaiduMap getMapInstance() {
		return this.mBaiduMap;
	}

	private Toast last;
	@Override
	public void showSearchErrorToast() {
		if (last != null) {
			last.cancel();
		} else {
			last = Toast.makeText(this, R.string.main_search_no_element_found, Toast.LENGTH_SHORT);
		}
		
		last.setText(R.string.main_search_no_element_found);
		last.show();
	}
	
	


	@Override
	public boolean getRecommandationButtonState() {
		// TODO Auto-generated method stub
		return false;
	}

	
	/////////////////////////////////////////////////////////////
	

	
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
				if (!isSuspended) {
					mLocalHandler.sendEmptyMessageDelayed(
							INTERVAL_GET_NEIBERHOOD, 5000);
				}
				break;
				
			case UPDATE_GPS:
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
				break;
			case REQUEST_PUBLISH_CALLBACK:
				handleRequestPublishCallback(msg);
				break;
			case STOP_PUBLISH:
				//VideoBCRequest.getInstance().stopLive();
//				liveService.requestFinishPublish(null);
//				cv.stopPublish();
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
