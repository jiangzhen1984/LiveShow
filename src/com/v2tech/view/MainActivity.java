package com.v2tech.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.v2tech.presenter.MainPresenter;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.Conference;
import com.v2tech.vo.Live;
import com.v2tech.vo.User;

public class MainActivity extends FragmentActivity implements
		View.OnClickListener, MainPresenter.MainPresenterUI {

	private static final int REQUEST_KEYBOARD_ACTIVITY = 100;
	private static final int REQUEST_LOGIN_ACTIVITY_CODE = 101;
	private static final int REQUEST_PERSONAL_ACTIVITY = 102;
	

	private RelativeLayout mBottomLayout;
	// private BottomButtonLayout mBottomButtonLayout;
	private EditText mEditText;
	private FrameLayout mMainLayout;
	private View mLocateButton;
	private VideoShareLayout videoShareLayout;
	private MapVideoLayout mMapVideoLayout;
	private MapView mMapView;
	private BaiduMap mBaiduMap;


	private ImageView mPersonalButton;
	Conference currentLive;

	
	MainPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((MainApplication)this.getApplication()).onMainCreate();
		
		if (presenter == null) {
			presenter = new MainPresenter(this, this);
		}
		setContentView(R.layout.main_activity);
		mMainLayout = (FrameLayout) findViewById(R.id.main);


		initMapviewLayout();
		initVideoShareLayout();
		initBottomButtonLayout();
		initTitleBarButtonLayout();
		initResetOrder();

		findViewById(R.id.title_bar_center_tv).setVisibility(View.GONE);
		findViewById(R.id.title_bar_logo).setVisibility(View.VISIBLE);
		presenter.onUICreated();

	}



	private void initMapviewLayout() {
		mMapVideoLayout = new MapVideoLayout(this);

		mMapVideoLayout.setPosInterface(presenter);
		mMapVideoLayout.setVideoChangedListener(presenter);
		mMapVideoLayout.setNotificationClickedListener(mOnNotificationClicked);
		mMapVideoLayout.setRequestConnectLayoutListener(presenter);
		mMapVideoLayout.setInterfactionBtnClickListener(presenter);
		mMapVideoLayout.setLiveInformationLayoutListener(presenter);
		mMapVideoLayout.setVideoWatcherListLayoutListener(presenter);
		mMapVideoLayout.setP2PVideoMainLayoutListener(presenter);
		mMapVideoLayout.setMessageMarqueeLayoutListener(presenter);
		
		mBaiduMap = mMapVideoLayout.getMap();
		mMapView = mMapVideoLayout.getMapView();

		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		mMainLayout.addView(mMapVideoLayout, fl);

		//mBaiduMap.setMyLocationEnabled(true);
		
	}
	
	
	private void initTitleBarButtonLayout() {
		 this.mPersonalButton = (ImageView)findViewById(R.id.title_bar_left_btn);
		 this.mPersonalButton.setImageResource(R.drawable.user_icon);
		 mPersonalButton.setOnClickListener(this);

	}

	private void initBottomButtonLayout() {
		mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);

		View button = findViewById(R.id.map_button);
		button.setOnClickListener(this);

		button = findViewById(R.id.msg_button);
		button.setOnClickListener(this);

		mEditText = (EditText) findViewById(R.id.edit_text);
		mEditText.setInputType(InputType.TYPE_NULL);
		mEditText.setFocusable(true);
		mEditText.setOnClickListener(this);

		mLocateButton = findViewById(R.id.map_locate_button);
		mLocateButton.setOnClickListener(this);
		

	}

	
	
	private void initVideoShareLayout() {
		
		videoShareLayout = (VideoShareLayout)findViewById(R.id.video_share_ly);
		videoShareLayout.setVideoShareBtnLayoutListener(presenter);
		videoShareLayout.setRequestConnectLayoutListener(presenter);
		videoShareLayout.setP2PVideoMainLayoutListener(presenter);
		videoShareLayout.setP2PAudioLiverLayoutListener(presenter);
		
	}
	
	
	private void initResetOrder() {
		mBottomLayout.bringToFront();
		mPersonalButton.bringToFront();
		mLocateButton.bringToFront();
	}


	@Override
	protected void onStart() {
		super.onStart();
		presenter.onUIStarted();
	
	}

	@Override
	protected void onPause() {
		super.onPause();
	//	mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	//	mMapView.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
		presenter.onUIStopped();
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

		presenter.onUIDestroyed();
		
		((MainApplication)this.getApplication()).requestQuit();
		
	}

	@Override
	public void onBackPressed() {
		presenter.onReturnBtnClicked();
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

//	private void stopCamera() {
//		cv.stopPreView();
//	}


//	float initY;
//	float lastY;
//	float offsetY;
//	int mActivePointerId;
//
//	private OnTouchListener dragListener = new OnTouchListener() {
//
//		@Override
//		public boolean onTouch(View v, MotionEvent event) {
//			int action = event.getAction();
//			switch (action) {
//			case MotionEvent.ACTION_DOWN:
//				stopCamera();
//				mActivePointerId = MotionEventCompat.getPointerId(event, 0);
//				initY = MotionEventCompat.getY(event, 0);
//				lastY = initY;
//				mMapVideoLayout.pauseDrawState(true);
//				break;
//			case MotionEvent.ACTION_MOVE:
//				final int pointerIndex = MotionEventCompat.findPointerIndex(
//						event, mActivePointerId);
//				final float y = MotionEventCompat.getY(event, pointerIndex);
//				final float dy = y - lastY;
//				mMapVideoLayout.updateOffset((int) dy);
//				lastY = y;
//				break;
//			case MotionEvent.ACTION_UP:
//				V2Log.d("Start translate");
//				mMapVideoLayout.requestUpFlying();
//				break;
//			}
//			return true;
//		}
//
//	};




	
	
	private MapVideoLayout.OnNotificationClickedListener mOnNotificationClicked = new MapVideoLayout.OnNotificationClickedListener() {

		@Override
		public void onNotificationClicked(View v, Live live, User u) {
			mMapVideoLayout.removeLiveNotificaiton(live);
		}
		
	};
	
	
	
	
	
	
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
	public void showTextKeyboard(boolean flag) {
		if (flag) {
		//	mBottomLayout.setVisibility(View.INVISIBLE);
			Intent i = new Intent();
			i.setClass(mEditText.getContext(), BottomButtonLayoutActivity.class);
			startActivityForResult(i, REQUEST_KEYBOARD_ACTIVITY);
		}
	}

	@Override
	public void showVideoScreentItem(int tag, boolean showFlag) {
		if (tag == MainPresenter.VIDEO_SCREEN_BTN_FLAG) {
			this.mMapVideoLayout.showVideoBtnLy(showFlag);
		} else if(tag == MainPresenter.VIDEO_BOTTOM_LY_FLAG) {
			this.mMapVideoLayout.showVideoWatcherListLy(showFlag);
		} else if (tag == MainPresenter.MESSAGE_MARQUEE_LY_SHOW) {
			this.mMapVideoLayout.showMarqueeMessageLayout(showFlag);
		}
		
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
	public void updateVideShareButtonText(boolean publish) {
		if (publish) {
			videoShareLayout.updateVideoShareBtnBackground(R.drawable.video_sharing_button_bg);
		} else {
			videoShareLayout.updateVideoShareBtnBackground(R.drawable.video_share_button_bg);
		}
	}

	
	


	@Override
	public void videoShareLayoutFlyout() {
		mMapVideoLayout.requestUpFlying();
	}


	
	@Override
	public SurfaceView getCameraSurfaceView() {
		return videoShareLayout.getLocalCameraView();
	}

	


	@Override
	public void showBottomLayout(boolean flag) {
		int vv = flag ? View.VISIBLE : View.GONE;
		mBottomLayout.setVisibility(vv);
		mLocateButton.setVisibility(vv);
		
	}

	



	@Override
	public void resizeCameraSurfaceSize() {
//		int width = localSurfaceView.getWidth();
//		int height = localSurfaceView.getHeight();
//		int r = width % 16;
//		MarginLayoutParams lp = (MarginLayoutParams)localSurfaceView.getLayoutParams();
//		if (r > 0) {
//			lp.leftMargin = r /2;
//			lp.rightMargin = r /2;
//		}
//		
//		r = height % 9;
//		if (r > 0) {
//			lp.topMargin = r;
//		}
//		localSurfaceView.setLayoutParams(lp);
	}

	
	@Override
	public void showError(int flag) {
		if (last != null) {
			last.cancel();
		}
		int res = -1;
		switch(flag) {
		case 1:
			res = R.string.error_no_any_watching_video;
			break;
		case 2:
			res = R.string.error_no_any_watching_video;
		case 3:
			res = R.string.error_no_open_remote_live_failed;
			break;
		} 
		last = Toast.makeText(this, res, Toast.LENGTH_SHORT);
		last.show();
	}
	
	

	public void showLiverInteractionLayout(boolean flag) {
		this.mMapVideoLayout.showLiverInteractionLy(flag);
		showBottomLayout(!flag);
	}
	



	@Override
	public SurfaceView getCurrentSurface() {
		return mMapVideoLayout.getCurrentVideoFragment().getSurfaceView();
	}
	
	

	@Override
	public void showDebugMsg(final String msg) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				((TextView)findViewById(R.id.title_bar_center_tv)).setText(msg);
			}
			
		});
		
	}
	
	
	public void setCurrentLive(Live l) {
		mMapVideoLayout.getCurrentVideoFragment().setTag1(l);
	}
	
	
	public void queuedMessage(final String msg) {
		mMapVideoLayout.addNewMessage(msg);
	}
	
	public void updateBalanceSum(final float num) {
		mMapVideoLayout.updateBalanceSum(num);
	}
	
	public void updateWatchNum(final int num) {
		mMapVideoLayout.updateWatcherNum(num);
		
	}
	
	public void updateRendNum(final int num) {
		mMapVideoLayout.updateRendNum(num);
	}
	
	
	
	public void showRedBtm(boolean flag) {
		mMapVideoLayout.showRedBtm(flag);
	}
	
	public void showIncharBtm(boolean flag) {
		mMapVideoLayout.showIncharBtm(flag);
	}
	
	
	
	public void showMarqueeMessage(boolean flag) {
		mMapVideoLayout.showMarqueeMessage(flag);
	}
	
	
	public void updateConnectLayoutBtnType(int type) {
		if (type ==1) {
			videoShareLayout.getConnectionRequestLayout().updateLeftBtnIcon(R.drawable.audio_call_decline_btn);
			videoShareLayout.getConnectionRequestLayout().updateRightBtnIcon(R.drawable.audio_call_accept_btn);
		} else {
			videoShareLayout.getConnectionRequestLayout().updateLeftBtnIcon(R.drawable.video_call_decline_btn);
			videoShareLayout.getConnectionRequestLayout().updateRightBtnIcon(R.drawable.video_call_accept_btn);
		}
	}
	
	
	public void showConnectRequestLayout(boolean flag) {
		videoShareLayout.showRequestConnectionLayout(flag);
	}
	
	
	public void showP2PLiverLayout(boolean flag) {
		videoShareLayout.showP2PLiverLayout(flag);
	}
	
	
	public void mapVideoLayoutFlyingIn() {
		mMapVideoLayout.requestUpFlying();
	}
	
	public void updateVideoLayoutOffset(int dy) {
		mMapVideoLayout.updateOffset(dy);
	}
	

	@Override
	public void doFinish() {
		finish();
	}
	
	@Override
	public void showP2PVideoLayout(boolean flag) {
		videoShareLayout.showP2PVideoLayout(flag);
		
	}
	
	
	public SurfaceView  getP2PMainSurface() {
		return videoShareLayout.getP2PMainSurface();
	}




	public void showVideoshareBtnLayout(boolean flag) {
		videoShareLayout.showVideoShareBtnLy(flag);
	}





	@Override
	public SurfaceView getP2PMainWatherSurface() {
		return mMapVideoLayout.getP2PWatcherSurfaceView();
	}



	@Override
	public void showWatcherP2PVideoLayout(boolean flag) {
		mMapVideoLayout.showP2PVideoLayout(flag);
	}
	
	
	ProgressDialog dialog;
	
	public void showProgressDialog(boolean flag, String text) {
		if (dialog == null && flag) {
			dialog = ProgressDialog.show(this, "", text);
			dialog.show();
		} else {
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
		}
	} 
	
	
	
	public void showWatcherP2PAudioLayout(boolean flag) {
		mMapVideoLayout.showP2PAudioWatcherLy(flag);
	}
	
	
	
	public void updateInterfactionFollowBtn(boolean followed) {
		if (followed) {
			mMapVideoLayout.updateFollowBtnImageResource(R.drawable.liver_interaction_cancel_follow_friend);
			mMapVideoLayout.updateFollowBtnTextResource(R.string.personel_item_user_cf_text);
		} else {
			mMapVideoLayout.updateFollowBtnImageResource(R.drawable.liver_interaction_follow);
			mMapVideoLayout.updateFollowBtnTextResource(R.string.personel_item_user_f_text);
		}
	}
	
	
	
	public BaiduMap getWatcherMapInstance() {
		return this.videoShareLayout.getWatcherMapInstance();
	}
	
	//FIXME close BTN
	public void closeVideo(boolean flag) {
		
	}
	
	/////////////////////////////////////////////////////////////
	





	

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
