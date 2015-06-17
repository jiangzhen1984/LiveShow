package com.v2tech.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.v2tech.widget.LiveVideoWidget;
import com.v2tech.widget.LiveVideoWidget.MediaState;

public class MainActivity extends FragmentActivity implements
		LiveVideoWidget.DragListener, LiveVideoWidget.OnWidgetClickListener,
		OnGetGeoCoderResultListener {

	private static final int SEARCH = 1;
	private static final int PLAY_FIRST_LIVE = 2;
	private static final int PLAY_LIVE = 3;
	private static final int INTERVAL_GET_NEIBERHOOD = 4;
	private static final int UPDATE_LIVE_MARK = 5;
	private static final int RECORDING = 6;
	private static final int STOP_RECORDING = 7;
	private static final int START_PUBLISH = 8;
	private static final int STOP_PUBLISH = 9;
	private static final int GET_MAP_SNAPSHOT = 10;

	private View mBottomButtonLayout;
	private FrameLayout mMainLayout;
	private View mLocateButton;
	private EditText mSearchEdit;

	private MapVideoLayout mMapVideoLayout;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocClient;
	private GeoCoder mSearch;
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = true;// 是否首次定位
	private boolean isSuspended;

	private CameraView cv;
	private boolean isFirstPlayed;
	private boolean isRecording = false;

	private DisplayMetrics mDisplay;
	private VideoShowFragment mCurrentVideoShow;
	private ImageView mapSnapshot;

	private LatLng selfLocation;
	private LatLng currentVideoLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		mMainLayout = (FrameLayout) findViewById(R.id.main);

		mDisplay = getResources().getDisplayMetrics();
		// final ViewConfiguration configuration = ViewConfiguration.get(this);
		// mTouchSlop = ViewConfigurationCompat
		// .getScaledPagingTouchSlop(configuration);
		// final float density = getResources().getDisplayMetrics().density;
		// mMinimumVelocity = (int) (MIN_FLING_VELOCITY * density);
		// mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

		//
		//
		// mBaiduMap = mMapView.getMap();
		// // mBaiduMap.setMyLocationEnabled(true);
		//
		initMapviewLayout();
		initVideoShareLayout();
		initBottomButtonLayout();
		initLocation();
		// initDragLayout();
		//
		TelephonyManager tl = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		ImRequest.getInstance().login(
				tl.getLine1Number() == null ? System.currentTimeMillis() + ""
						: tl.getLine1Number(), "111111",
				V2GlobalEnum.USER_STATUS_ONLINE, V2ClientType.ANDROID, true);

		LocalHandler.sendEmptyMessageDelayed(PLAY_FIRST_LIVE, 2000);

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
		mCurrentVideoShow = mMapVideoLayout.getCurrentVideoFragment();
	}

	private void initBottomButtonLayout() {
		mBottomButtonLayout = findViewById(R.id.bottom_id);
		mLocateButton = findViewById(R.id.location);
		mLocateButton.setOnClickListener(mLocateClickListener);

		mSearchEdit = (EditText) findViewById(R.id.message_text);
		mSearchEdit.addTextChangedListener(mSearchedTextWatcher);

		mBottomButtonLayout.bringToFront();
	}

	private Button mShareVideoButton;
	private FrameLayout videoShareLayout;

	private void initVideoShareLayout() {
		videoShareLayout = (FrameLayout) findViewById(R.id.video_share_ly);

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
					Message.obtain(LocalHandler, START_PUBLISH).sendToTarget();
				} else {
					mShareVideoButton.setTag("none");
					mShareVideoButton.setText("分享视频");
					Message.obtain(LocalHandler, STOP_PUBLISH).sendToTarget();
				}
			}

		});

		mapSnapshot = new ImageView(this);
		mapSnapshot.setAlpha(0.3f);
		mapSnapshot.setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN);
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

		Message.obtain(LocalHandler, STOP_PUBLISH).sendToTarget();
		// lvw.stop();
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

	FrameLayout.LayoutParams originFl;

	@Override
	public void onWidgetClick(View view) {
		// Intent i = new Intent(this, VideoList.class);
		// startActivity(i);

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
				mMapVideoLayout.updateCoverState(true);
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
			updateCurrentVideoState(false);
			mBottomButtonLayout.setVisibility(View.GONE);
		}

		@Override
		public void onFlyingIn() {
			cv.stopPreView();
			updateCurrentVideoState(true);
			mBottomButtonLayout.setVisibility(View.VISIBLE);
		}
	};

	private MapVideoLayout.OnVideoFragmentChangedListener videoFragmentChangedListener = new MapVideoLayout.OnVideoFragmentChangedListener() {

		@Override
		public void onChanged(VideoShowFragment videoFrag) {
			mCurrentVideoShow = videoFrag;
		}

	};

	private void updateCurrentVideoState(boolean play) {
		if (mCurrentVideoShow != null) {
			if (play) {
				mCurrentVideoShow.resume();
			} else {
				mCurrentVideoShow.pause();
			}
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
		lan = result.getLocation().longitude;
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {

	}

	private double lat;
	private double lan;

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
				float zoomLevel = 15.0F;
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(
						selfLocation, zoomLevel);
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
				lan = location.getLongitude();
				LocalHandler.sendEmptyMessageDelayed(INTERVAL_GET_NEIBERHOOD,
						1000);

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
		BitmapDescriptor online = BitmapDescriptorFactory
				.fromResource(R.drawable.marker_online);
		BitmapDescriptor live = BitmapDescriptorFactory
				.fromResource(R.drawable.marker_live);
		for (Live l : list) {
			LatLng ll = new LatLng(selfLocation.latitude,
					selfLocation.longitude);
			Bundle bundle = new Bundle();
			V2Log.e(l.getLat() + "  " + l.getLan());
			if (l.getUrl() == null || l.getUrl().isEmpty()) {
				OverlayOptions oo = new MarkerOptions().icon(online)
						.position(ll).extraInfo(bundle);
				mBaiduMap.addOverlay(oo);
			} else {
				bundle.putString("url", l.getUrl());
				bundle.putDouble("lat", l.getLat());
				bundle.putDouble("lng", l.getLan());
				OverlayOptions oo = new MarkerOptions().icon(live).position(ll)
						.extraInfo(bundle);
				mBaiduMap.addOverlay(oo);
			}
		}

	}

	private OnClickListener mLocateClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			LocationItem li = (LocationItem) v.getTag();
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(li.ll, 15);
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
				String url = (String) marker.getExtraInfo().get("url");
				double lat = marker.getExtraInfo().getDouble("lat");
				double lng = marker.getExtraInfo().getDouble("lng");
				if (url != null && !url.isEmpty()) {
					Message.obtain(LocalHandler, PLAY_LIVE, new Live(null, url))
							.sendToTarget();
					// update map
					float zoomLevel = 15.0F;
					currentVideoLocation = new LatLng(lat, lng);
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(
							currentVideoLocation, zoomLevel);
					mBaiduMap.animateMapStatus(u);

					mLocateButton.setTag(new LocationItem(
							LocationItemType.VIDEO, currentVideoLocation));
				}
			}
			return true;
		}

	};

	private LiveVideoWidget.MediaStateNotification mediaStateNotificaiton = new LiveVideoWidget.MediaStateNotification() {

		@Override
		public void onPlayStateNotificaiton(MediaState state) {
			if (state == MediaState.ERROR) {
				Toast.makeText(MainActivity.this, "播放失败", Toast.LENGTH_SHORT)
						.show();
			} else if (state == MediaState.PREPARED
					|| state == MediaState.PLAYING) {
				Toast.makeText(MainActivity.this, "开始播放", Toast.LENGTH_SHORT)
						.show();
			} else if (state == MediaState.END) {
				Toast.makeText(MainActivity.this, "播放结束", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	private OnMapStatusChangeListener mMapStatusChangeListener = new OnMapStatusChangeListener() {

		@Override
		public void onMapStatusChange(MapStatus arg0) {

		}

		@Override
		public void onMapStatusChangeFinish(MapStatus arg0) {
			mBaiduMap.snapshot(new SnapshotReadyCallback() {

				@Override
				public void onSnapshotReady(Bitmap bm) {
					mapSnapshot.setImageBitmap(bm);
//					mapSnapshot.setColorFilter(Color.GRAY,
//							PorterDuff.Mode.LIGHTEN);
					mapSnapshot.setColorFilter(Color.argb(150,200,200,200));
					mapSnapshot.invalidate();
					mMapVideoLayout.udpateCover(bm);
				}

			});

		}

		@Override
		public void onMapStatusChangeStart(MapStatus arg0) {

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
//				for (int i = 0; i < VideoBCRequest.getInstance().lives.size(); i++) {
//					String url = VideoBCRequest.getInstance().lives.get(i)[0];
//					if (url != null && !url.isEmpty()) {
//						mCurrentVideoShow.play(url);
//						break;
//					}
//				}
//
//				if (!isFirstPlayed && mCurrentVideoShow != null) {
//					try {
//						mCurrentVideoShow.play(MainActivity.this.getAssets()
//								.openFd("a.mp4"));
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				isFirstPlayed = true;
				break;
			case PLAY_LIVE:
				mCurrentVideoShow.play((Live) msg.obj);
				break;
			case INTERVAL_GET_NEIBERHOOD:
				// VideoBCRequest.getInstance().getNeiborhood(1000);
				VideoBCRequest.getInstance().GetNeiborhood_Region(
						"<gps lon=\"" + lan + "\" lat=\"" + lat
								+ "\" distance=\"1000\" ></gps>");
				if (!isSuspended) {
					LocalHandler.sendEmptyMessageDelayed(
							INTERVAL_GET_NEIBERHOOD, 10000);
					LocalHandler
							.sendEmptyMessageDelayed(UPDATE_LIVE_MARK, 1000);

					if (!isFirstPlayed) {
						LocalHandler.sendEmptyMessageDelayed(PLAY_FIRST_LIVE,
								1000);
					}
				}
				break;
			case UPDATE_LIVE_MARK:
				if (isSuspended) {
					break;
				}
				List<String[]> liveList = VideoBCRequest.getInstance().lives;
				List<Live> lList = new ArrayList<Live>();
				for (String[] str : liveList) {
					Live lv = new Live(null, str[0]);
					lv.setLat(Double.parseDouble(str[1]));
					lv.setLan(Double.parseDouble(str[2]));
					lList.add(lv);
				}
				updateLiveMarkOnMap(lList);
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
			case STOP_RECORDING:
				cv.stopPublish();
				VideoBCRequest.getInstance().stopLive();
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
