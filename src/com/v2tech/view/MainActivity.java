package com.v2tech.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.camera.CameraView;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.Live;
import com.v2tech.widget.CrossLayout;
import com.v2tech.widget.LiveVideoWidget;
import com.v2tech.widget.LiveVideoWidget.MediaState;

public class MainActivity extends Activity implements
		LiveVideoWidget.DragListener, OnClickListener,
		LiveVideoWidget.OnWidgetClickListener, OnGetGeoCoderResultListener {

	private static final int SEARCH = 1;
	private static final int PLAY_FIRST_LIVE = 2;
	private static final int PLAY_LIVE = 3;
	private static final int INTERVAL_GET_NEIBERHOOD = 4;
	private static final int UPDATE_LIVE_MARK = 5;
	private static final int RECORDING = 6;
	private static final int STOP_RECORDING = 7;
	private static final int START_PUBLISH = 8;
	private static final int STOP_PUBLISH = 9;

	private FrameLayout mMainLayout;
	private EditText mSearchEdit;

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	LocationClient mLocClient;
	private GeoCoder mSearch;

	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = true;// 是否首次定位
	private boolean isSuspended;

	private MediaPlayer mp = new MediaPlayer();
	private SurfaceHolder sh;
	private CameraView cv;
	private boolean playing = false;
	private boolean isFirstPlayed;
	private boolean isRecording = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		mMainLayout = (FrameLayout) findViewById(R.id.main);
		//mMainLayout.setOnTouchListener(dragListener);
//		mMainLayout.setOnDragListener(dListener);
		// mSearchEdit = (EditText) findViewById(R.id.search_edit);
		// mSearchEdit.addTextChangedListener(mSearchedTextWatcher);

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

		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);

		init();
		initMapviewLayout();
		initVideoLayout();
		initVideoShareLayout();

		TelephonyManager tl = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		ImRequest.getInstance().login(
				tl.getLine1Number() == null ? System.currentTimeMillis() + ""
						: tl.getLine1Number(), "111111",
				V2GlobalEnum.USER_STATUS_ONLINE, V2ClientType.ANDROID, true);
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

	private void initMapviewLayout() {
		final DisplayMetrics dis = this.getResources().getDisplayMetrics();
		int width = dis.widthPixels - dis.widthPixels % 16;
		int height = width / 16 * 9;
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, dis.heightPixels
						- height);
		fl.topMargin = height;
		mMainLayout.addView(mMapView, fl);

		ImageView searchIcon = new ImageView(this);
		searchIcon.setImageResource(R.drawable.search);
		searchIcon.setPadding(5, 5, 5, 5);
		searchIcon.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		FrameLayout.LayoutParams iconFl = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		iconFl.leftMargin = 30;
		iconFl.topMargin = dis.heightPixels - 170
				- searchIcon.getMeasuredHeight();
		mMainLayout.addView(searchIcon, iconFl);
		searchIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSearchEdit == null) {
					mSearchEdit = new EditText(MainActivity.this);
					mSearchEdit.setTextSize(18);
					mSearchEdit.setBackgroundResource(R.drawable.input_bg);
					mSearchEdit.addTextChangedListener(mSearchedTextWatcher);
					mSearchEdit.setVisibility(View.GONE);
					FrameLayout.LayoutParams searchFl = new FrameLayout.LayoutParams(
							FrameLayout.LayoutParams.MATCH_PARENT,
							FrameLayout.LayoutParams.WRAP_CONTENT);

					FrameLayout.LayoutParams vfl = (FrameLayout.LayoutParams) v
							.getLayoutParams();
					searchFl.leftMargin = vfl.leftMargin + v.getWidth() + 15;
					searchFl.rightMargin = searchFl.leftMargin;
					searchFl.topMargin = vfl.topMargin;

					mMainLayout.addView(mSearchEdit, searchFl);
				}
				if (mSearchEdit.getVisibility() == View.GONE) {
					ScaleAnimation sal = new ScaleAnimation(0F, 1.0f, 1.0F,
							1.0F, 0F, 1F);
					sal.setDuration(1000);
					mSearchEdit.startAnimation(sal);
					mSearchEdit.setVisibility(View.VISIBLE);
				} else {
					ScaleAnimation sal = new ScaleAnimation(1.0F, 0F, 1.0F,
							1.0F, 0F, 1F);
					mSearchEdit.startAnimation(sal);
					sal.setDuration(1000);
					mSearchEdit.setVisibility(View.GONE);
				}

			}

		});

	}

	CrossLayout cl;
	
	private void initVideoLayout() {
		DisplayMetrics dis = this.getResources().getDisplayMetrics();
		int width = dis.widthPixels - dis.widthPixels % 16;
		int height = width / 16 * 9;

		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(width,
				height);
		fl.leftMargin = (dis.widthPixels - width) / 2;

		SurfaceView sv2 = new SurfaceView(this);
		sv2.setZOrderOnTop(true);
		sv2.getHolder().addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				sh = holder;
				mp.setDisplay(holder);
				Canvas c = holder.lockCanvas();
				drawFirstBlankFrame(c);
				holder.unlockCanvasAndPost(c);
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {

			}
		});

		mMainLayout.addView(sv2, fl);

	}
	
	private Button mShareVideoButton;
	private FrameLayout videoShareLayout;
	private void initVideoShareLayout() {
		videoShareLayout = (FrameLayout)findViewById(R.id.video_share_ly);
		videoShareLayout.setOnTouchListener(dragListener);
		DisplayMetrics dis = this.getResources().getDisplayMetrics();
		int width = dis.widthPixels - dis.widthPixels % 16;
		int height = width / 16 * 9;
		cv = new CameraView(this);
		cv.setZOrderOnTop(false);
		cv.setZOrderMediaOverlay(false);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(width, height);
		fl.leftMargin = (dis.widthPixels - width) / 2;
		videoShareLayout.addView(cv, fl);
		
		
		mShareVideoButton = new Button(this);
		mShareVideoButton.setText("分享视频");
		mShareVideoButton.setPadding(8, 4, 8, 4);
		mShareVideoButton.setBackgroundResource(R.drawable.video_share_button_bg);
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
		
		FrameLayout.LayoutParams buttonfl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		mShareVideoButton.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		buttonfl.leftMargin = (dis.widthPixels - mShareVideoButton.getMeasuredWidth()) / 2;
		buttonfl.topMargin = height + (dis.heightPixels - height - mShareVideoButton.getMeasuredHeight()) / 2; 
		videoShareLayout.addView(mShareVideoButton, buttonfl);
		mMainLayout.bringToFront();
	}

	private void drawFirstBlankFrame(Canvas c) {
		synchronized (mp) {
			if (playing) {
				return;
			}
			int width = c.getWidth();
			int height = c.getHeight();
			Bitmap bp = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_4444);
			Canvas tmp = new Canvas(bp);
			tmp.drawColor(Color.BLACK);

			c.drawBitmap(bp, 0, 0, new Paint());
			bp.recycle();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		isSuspended = false;
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
		// // FIXME stop playing
		// Intent i = new Intent(this, VideoList.class);
		// startActivity(i);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		}
	}
	
	
	
	int initY;
	int offsetY;
	int deltaY;
	int lastY;
	int mActivePointerId;
	private OnTouchListener dragListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			DisplayMetrics dis = getResources().getDisplayMetrics();
			RelativeLayout.LayoutParams fl = (RelativeLayout.LayoutParams)mMainLayout.getLayoutParams();
			int action = event.getAction();
			switch(action) {
			case MotionEvent.ACTION_DOWN:
				initY = (int)event.getY();
				lastY = initY;
			     mActivePointerId = MotionEventCompat.getPointerId(event, 0);
				break;
			case MotionEvent.ACTION_MOVE:
				
				final int pointerIndex = 
                MotionEventCompat.findPointerIndex(event, mActivePointerId);  
            
				
				offsetY = (int)MotionEventCompat.getY(event, pointerIndex) - initY;
				deltaY = (int) MotionEventCompat.getY(event, pointerIndex) - lastY;
				if (deltaY > 0 && fl.topMargin < dis.heightPixels){
					fl.topMargin += deltaY;
					mMainLayout.setLayoutParams(fl);
				} else if (deltaY < 0 && fl.topMargin > 0) {
					fl.topMargin += deltaY;
					mMainLayout.setLayoutParams(fl);
				}
				lastY = (int) MotionEventCompat.getY(event, pointerIndex);
				break;
			case MotionEvent.ACTION_UP:
				mMainLayout.post(Flying);
				break;
			}
			return true;
		}
		
	};
	
	
	private OnDragListener dListener = new OnDragListener() {

		@Override
		public boolean onDrag(View v, DragEvent event) {
			DisplayMetrics dis = getResources().getDisplayMetrics();
			RelativeLayout.LayoutParams fl = (RelativeLayout.LayoutParams)mMainLayout.getLayoutParams();
			
			boolean ret = false;
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				initY = (int)event.getY();
				lastY = initY;
				ret = true;
				break;
			case DragEvent.ACTION_DRAG_LOCATION:
				
				offsetY = (int)event.getY() - initY;
				deltaY = (int) event.getY() - lastY;
				if (deltaY > 0 && fl.topMargin < dis.heightPixels){
					fl.topMargin += deltaY;
					mMainLayout.setLayoutParams(fl);
				} else if (deltaY < 0 && fl.topMargin > 0) {
					fl.topMargin += deltaY;
					mMainLayout.setLayoutParams(fl);
				}
				lastY = (int)event.getY();
				
				ret = true;
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				mMainLayout.post(Flying);
				ret = true;
				break;
			}
			return ret;
		}
		
	};
	
	
	
	private Runnable Flying = new  Runnable() {
		
		@Override
		public void run() {
			DisplayMetrics dis = getResources().getDisplayMetrics();
			RelativeLayout.LayoutParams fl = (RelativeLayout.LayoutParams)mMainLayout.getLayoutParams();
			if (deltaY > 0 && fl.topMargin < dis.heightPixels){
				fl.topMargin += 55;
				if (fl.topMargin >  dis.heightPixels) {
					fl.topMargin =  dis.heightPixels;
					if (!isRecording) {
						cv.startPreView();
					}
				}
			} else if (deltaY < 0 && fl.topMargin > 0) {
				fl.topMargin += -55;
				if (fl.topMargin < 0) {
					fl.topMargin = 0;
					cv.stopPreView();
				}
			} else {
				return;
			}
			mMainLayout.setLayoutParams(fl);
			mMainLayout.postDelayed(Flying, 6);
		}
		
	};

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

	private LatLng selfLocation;
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
				LatLng bounds = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(
						bounds, zoomLevel);
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
				OverlayOptions oo = new MarkerOptions().icon(live).position(ll)
						.extraInfo(bundle);
				mBaiduMap.addOverlay(oo);
			}
		}

		// OverlayOptions oo = new
		// MarkerOptions().icon(online).position(selfLocation);
		// mBaiduMap.addOverlay(oo);
	}

	private void doSelect(String url, boolean force) {
		synchronized (mp) {
			playing = true;
			if (url == null) {
				return;
			}
			if (force) {
				mp.stop();
			} else {
				return;
			}

			mp.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					playing =false;
					Toast.makeText(getApplicationContext(), "视频源无法播放",
							Toast.LENGTH_SHORT).show();
					return false;
				}

			});
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					if (!mp.isPlaying()) {
						playing = false;
						Canvas c = sh.lockCanvas();
						if (c != null) {
							drawFirstBlankFrame(c);
							sh.unlockCanvasAndPost(c);
						}
					}

				}

			});

			try {
				mp.setDataSource(this, Uri.parse(url));
				mp.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mp.start();
		}
	}

	private BaiduMap.OnMarkerClickListener mMarkerClickerListener = new BaiduMap.OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker marker) {
			if (marker.getExtraInfo() != null) {
				String url = (String) marker.getExtraInfo().get("url");
				if (url != null && !url.isEmpty()) {
					Message.obtain(LocalHandler, PLAY_LIVE, new Live(null, url))
							.sendToTarget();
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
				for (int i = 0; i < VideoBCRequest.getInstance().lives.size(); i++) {
					String url = VideoBCRequest.getInstance().lives.get(i)[0];
					if (url != null && !url.isEmpty()) {
						doSelect(url, true);
					}
					isFirstPlayed = true;
					break;
				}

				break;
			case PLAY_LIVE:
				doSelect(((Live) msg.obj).getUrl(), true);
				break;
			case INTERVAL_GET_NEIBERHOOD:
				// VideoBCRequest.getInstance().getNeiborhood(1000);
				VideoBCRequest.getInstance().getNeiborhood_region(
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
			}
		}

	};

	enum LocalState {
		DONING, DONE;
	}

}
