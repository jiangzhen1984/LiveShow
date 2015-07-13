package com.v2tech.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.V2.jni.ImRequest;
import com.V2.jni.V2ClientType;
import com.V2.jni.V2GlobalEnum;
import com.V2.jni.VideoBCRequest;
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
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.Live;
import com.v2tech.widget.BottomButtonLayout;
import com.v2tech.widget.FloatEditText;
import com.v2tech.widget.FloatEditText.OnBackkeyClickedListener;
import com.v2tech.widget.VideoShowFragment;

public class MainActivity extends FragmentActivity implements
		OnGetGeoCoderResultListener {

	private static final String BUTTON_TAG_MAP = "map";
	private static final String BUTTON_TAG_WORD = "word";

	private static final int SEARCH = 1;
	private static final int AUTO_PLAY_LIVE = 2;
	private static final int PLAY_LIVE = 3;
	private static final int INTERVAL_GET_NEIBERHOOD = 4;
	private static final int UPDATE_LIVE_MARK = 5;
	private static final int RECORDING = 6;
	private static final int START_PUBLISH = 8;
	private static final int STOP_PUBLISH = 9;
	private static final int GET_MAP_SNAPSHOT = 10;
	private static final int MARKER_ANIMATION = 12;

	private int keyboardHeight = 0;
	private WindowManager mWindowManager;
	private InputMethodManager mIMM;

	private static float mCurrentZoomLevel = 14F;

	private RelativeLayout mBottomLayout;
	// private BottomButtonLayout mBottomButtonLayout;
	private View mBottomButtonLayout;
	private ViewGroup.LayoutParams mBottomButtonLayoutParmeters;
	private EditText mEditText;
	private FrameLayout mMainLayout;
	private View mLocateButton;
	private Button mShareVideoButton;
	private FrameLayout videoShareLayout;

	private MapVideoLayout mMapVideoLayout;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocClient;
	private GeoCoder mSearch;
	private MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = true;// 是否首次定位
	private boolean isSuspended;

	private CameraView cv;
	private boolean isRecording = false;
	private boolean isInCameraView = false;
	private boolean keyboardShow = false;

	private DisplayMetrics mDisplay;

	private Map<VideoOpt, VideoItem> videoMaps = new HashMap<VideoOpt, VideoItem>();
	private Map<Live, Overlay> currentOverlay = new HashMap<Live, Overlay>();
	private VideoOpt mCurrentVideoFragment;
	private ImageView mapSnapshot;

	private LatLng selfLocation;
	private LatLng currentVideoLocation;

	private LocalHandler mLocalHandler;
	private HandlerThread mHandlerThread;
	private Handler uiThreadHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mIMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		setContentView(R.layout.main_activity);
		mMainLayout = (FrameLayout) findViewById(R.id.main);

		mDisplay = getResources().getDisplayMetrics();

		initMapviewLayout();
		initVideoShareLayout();
		initBottomButtonLayout();
		initLocation();

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

		TelephonyManager tl = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		ImRequest.getInstance().login(
				tl.getLine1Number() == null ? System.currentTimeMillis() + ""
						: tl.getLine1Number(), "111111",
				V2GlobalEnum.USER_STATUS_ONLINE, V2ClientType.ANDROID, true);

		final Window mRootWindow = getWindow();
		View mRootView = mRootWindow.getDecorView().findViewById(
				android.R.id.content);
		mRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					public void onGlobalLayout() {
						if (keyboardHeight <= 0) {
							Rect r = new Rect();
							View view = mRootWindow.getDecorView();
							view.getWindowVisibleDisplayFrame(r);
							int kh = mDisplay.heightPixels
									- (r.bottom - r.top);
							if (kh > 100) {
								keyboardHeight = kh;
							}
						}
					}
				});

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
		mBaiduMap = mMapVideoLayout.getMap();
		mMapView = mMapVideoLayout.getMapView();

		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		mMainLayout.addView(mMapVideoLayout, fl);

		mBaiduMap.setOnMapStatusChangeListener(mMapStatusChangeListener);
		mBaiduMap.setOnMarkerClickListener(mMarkerClickerListener);
		mCurrentVideoFragment = mMapVideoLayout.getCurrentVideoFragment();
		VideoItem item = videoMaps.get(mCurrentVideoFragment);
		if (item == null) {
			videoMaps.put(mCurrentVideoFragment, new VideoItem(
					mCurrentVideoFragment));
		}
	}

	private void initBottomButtonLayout() {
		mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
		// mBottomButtonLayout =
		// (BottomButtonLayout)findViewById(R.id.bottom_button_ly);
		// mBottomButtonLayout.setButtonListener(mButtonClickedListener);

		mBottomButtonLayout = findViewById(R.id.bottom_button_ly);
		View button = mBottomButtonLayout.findViewById(R.id.map_button);
		button.setTag(BUTTON_TAG_MAP);
		button.setOnClickListener(mBottomButtonClickedListener);

		button = mBottomButtonLayout.findViewById(R.id.msg_button);
		button.setTag(BUTTON_TAG_WORD);
		button.setOnClickListener(mBottomButtonClickedListener);

		mEditText = (EditText) findViewById(R.id.edit_text);
		mEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!keyboardShow) {
					updateBottomLayoutToWindowManager(true);
				}

			}

		});

		((FloatEditText) mEditText)
				.setOnBackKeyClickedListener(new OnBackkeyClickedListener() {

					@Override
					public void OnBackkeyClicked(View v) {
						updateBottomLayoutToWindowManager(false);
					}

				});

		mLocateButton = findViewById(R.id.location);
		mLocateButton.setOnClickListener(mLocateClickListener);
		mBottomLayout.bringToFront();

	}

	private void initVideoShareLayout() {
		videoShareLayout = (FrameLayout) findViewById(R.id.video_share_ly);
		videoShareLayout.setBackgroundColor(Color.BLACK);
		int width = getPreWidth();
		int height = getPreHeight(width);
		cv = new CameraView(this);
		cv.setZOrderOnTop(false);
		cv.setZOrderMediaOverlay(false);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(width,
				height);
		fl.leftMargin = (mDisplay.widthPixels - width) / 2;
		videoShareLayout.addView(cv, fl);

		mShareVideoButton = new Button(this);
		mShareVideoButton.setText("分享视频");
		mShareVideoButton.setPadding(12, 12, 12, 12);
		mShareVideoButton.setTextColor(Color.GREEN);
		mShareVideoButton
				.setBackgroundResource(R.drawable.video_share_button_bg);
		mShareVideoButton.setTextSize(18);
		mShareVideoButton.setTag("none");
		mShareVideoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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

		mMainLayout.bringToFront();
	}

	private int getPreWidth() {
		return mDisplay.widthPixels - mDisplay.widthPixels % 16;
	}

	private int getPreHeight(int width) {
		return width / 4 * 3;
	}

	@Override
	protected void onStart() {
		super.onStart();
		isSuspended = false;
		if (isRecording) {

		} else {
			if (mCurrentVideoFragment != null) {
				mCurrentVideoFragment.resume();
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
		super.onStop();
		isSuspended = true;
		cv.stopPreView();
		if (isRecording) {
			Message.obtain(mLocalHandler, STOP_PUBLISH).sendToTarget();
		} else {
			if (mCurrentVideoFragment != null) {
				mCurrentVideoFragment.pause();
			}
		}
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
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (keyboardShow) {
			updateBottomLayoutToWindowManager(false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void stopCamera() {
		cv.stopPreView();
	}

	PopupWindow pw;

	private void updateBottomLayoutToWindowManager(boolean flag) {
		if (flag) {
			uiThreadHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mBottomButtonLayoutParmeters = (RelativeLayout.LayoutParams) mBottomButtonLayout
							.getLayoutParams();
					((ViewGroup) mBottomButtonLayout.getParent())
							.removeView(mBottomButtonLayout);

					pw = new PopupWindow(mBottomButtonLayout,
							mBottomButtonLayout.getWidth(), mBottomButtonLayout
									.getHeight());
					
					pw.setFocusable(true);
					pw.setOutsideTouchable(false);
					
					if (keyboardHeight > 0) {
						int[] location = new int[2];
						mMapVideoLayout.getLocationInWindow(location);
						int offset = location[1] + ((mMapVideoLayout.getHeight() - location[1])- keyboardHeight);
						pw.showAtLocation(mMapVideoLayout, Gravity.TOP, 0, offset);
						
						keyboardShow = true;
					} else {
						uiThreadHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								int[] location = new int[2];
								mMapVideoLayout.getLocationInWindow(location);
								int offset = location[1] + ((mMapVideoLayout.getHeight() - location[1])- keyboardHeight);
								pw.showAtLocation(mMapVideoLayout, Gravity.TOP, 0, offset);
								
								keyboardShow = true;
							}
							
						}, 50);
					}

				}

			}, 30);

		} else {

			mIMM.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
			uiThreadHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (pw != null) {
						pw.dismiss();
						mBottomLayout.addView(mBottomButtonLayout,
								mBottomButtonLayoutParmeters);
						pw = null;
						keyboardShow = false;
					}
				}

			}, 300);
			//

		}
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
			if (!isRecording) {
				cv.startPreView();
			}
			isInCameraView = true;
			updateCurrentVideoState(mCurrentVideoFragment, false);
			mBottomLayout.setVisibility(View.GONE);
		}

		@Override
		public void onFlyingIn() {
			cv.stopPreView();
			isInCameraView = false;
			updateCurrentVideoState(mCurrentVideoFragment, true);
			mBottomLayout.setVisibility(View.VISIBLE);
			mMapVideoLayout.pauseDrawState(false);
		}
	};

	private MapVideoLayout.OnVideoFragmentChangedListener videoFragmentChangedListener = new MapVideoLayout.OnVideoFragmentChangedListener() {

		@Override
		public void onChanged(VideoShowFragment videoFrag) {
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
			Toast.makeText(MainActivity.this, "抱歉，未能找到位置", Toast.LENGTH_LONG)
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
			if (isFirstLoc) {
				isFirstLoc = false;
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(
						selfLocation, mCurrentZoomLevel);
				mBaiduMap.animateMapStatus(u);
				mLocateButton.setTag(new LocationItem(LocationItemType.SELF,
						selfLocation));
			}

			if (mCacheLocation == null
					|| (mCacheLocation.getLongitude() != location
							.getLongitude() || mCacheLocation.getLatitude() != location
							.getLatitude())) {
				mCacheLocation = location;
				VideoBCRequest.getInstance().updateGpsRequest(
						"<gps lon=\"" + location.getLongitude() + "\" lat=\""
								+ location.getLatitude() + "\"></gps>");

				lat = location.getLatitude();
				lng = location.getLongitude();
				mLocalHandler.sendEmptyMessageDelayed(INTERVAL_GET_NEIBERHOOD,
						1000);

			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
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
				.fromResource(R.drawable.marker_online);
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
				mBaiduMap.addOverlay(oo);
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
		mCurrentVideoFragment.play(l);

		videoMaps.get(mCurrentVideoFragment).live = l;

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
		if (mCurrentVideoFragment.isPlaying()) {
			if (mCurrentVideoFragment.isPause()) {
				return false;
			}
		}
		List<Live> list = VideoBCRequest.getInstance().lives;
		for (int i = 0; i < list.size(); i++) {
			boolean inUsed = false;
			for (VideoItem item : videoMaps.values()) {
				if (item.live == null) {
					continue;
				}
				if (item.live.equals(list.get(i))) {
					inUsed = true;
					break;
				}
			}
			if (inUsed) {
				continue;
			} else {
				playLive(list.get(i));
				return true;
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
				if (!TextUtils.isEmpty(l.getUrl())) {
					Message.obtain(mLocalHandler, PLAY_LIVE, l).sendToTarget();
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
				mMapVideoLayout.addNewMessage(mEditText.getText().toString());
			}

			mEditText.setText("");

			updateBottomLayoutToWindowManager(false);
		}

	};

	private BottomButtonLayout.ButtonClickedListener mButtonClickedListener = new BottomButtonLayout.ButtonClickedListener() {

		@Override
		public void onButtonClicked(View v, EditText et, int flag) {
			if (flag == BottomButtonLayout.WORD_BUTTON) {
				mMapVideoLayout.addNewMessage(et.getText().toString());
			} else if (flag == BottomButtonLayout.MAP_BUTTON) {
				mLocalHandler.removeMessages(SEARCH);
				Message msg = Message.obtain(mLocalHandler, SEARCH, et
						.getText().toString());
				mLocalHandler.sendMessage(msg);
			}
			et.setText("");
			et.clearFocus();
			mIMM.hideSoftInputFromWindow(et.getWindowToken(), 0);
		}
	};

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
				VideoBCRequest.getInstance().GetNeiborhood_Region(
						"<gps lon=\"" + lng + "\" lat=\"" + lat
								+ "\" distance=\"1000\" ></gps>");
				if (!isSuspended) {
					mLocalHandler.sendEmptyMessageDelayed(
							INTERVAL_GET_NEIBERHOOD, 10000);
					mLocalHandler.sendEmptyMessageDelayed(UPDATE_LIVE_MARK,
							1000);

					mLocalHandler.sendEmptyMessageDelayed(AUTO_PLAY_LIVE, 1000);
				}
				break;
			case UPDATE_LIVE_MARK:
				if (isSuspended) {
					break;
				}
				List<Live> liveList = VideoBCRequest.getInstance().lives;
				updateLiveMarkOnMap(liveList);
				break;
			case RECORDING:
				isRecording = true;
				if (VideoBCRequest.getInstance().url == null) {
					Message dm = obtainMessage(RECORDING);
					this.sendMessageDelayed(dm, 300);
				} else {
					String uuid = null;
					int ind = VideoBCRequest.getInstance().url.indexOf("file=");
					if (ind != -1) {
						uuid = VideoBCRequest.getInstance().url
								.substring(ind + 5);
					}
					cv.publishUrl = "rtmp://" + Constants.SERVER + "/vod/"
							+ uuid;
					cv.startPublish();
				}
				break;
			case START_PUBLISH:
				VideoBCRequest.getInstance().startLive();
				Message m = Message.obtain(this, RECORDING);
				this.sendMessageDelayed(m, 300);
				break;
			case STOP_PUBLISH:
				VideoBCRequest.getInstance().stopLive();
				cv.stopPublish();
				isRecording = false;
				break;
			case GET_MAP_SNAPSHOT:
				getMapSnapshot();
				break;
			case MARKER_ANIMATION:
				if (mCurrentVideoFragment.getCurrentLive() != null) {
					animationMaker(mCurrentVideoFragment.getCurrentLive(),
							msg.arg1 == 0 ? false : true);
					Message delayMessage = Message.obtain(mLocalHandler,
							MARKER_ANIMATION, msg.arg1 == 0 ? 1 : 0, 0);
					mLocalHandler.sendMessageDelayed(delayMessage, 200);
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
