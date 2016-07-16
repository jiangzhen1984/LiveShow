package com.v2tech.view;

import com.v2tech.map.MapAPI;
import com.v2tech.presenter.BasePresenter;
import com.v2tech.presenter.MainPresenter;
import com.v2tech.presenter.MainPresenterUI;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.Watcher;
import com.v2tech.widget.BottomButtonLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import v2av.VideoPlayer;

public class MainActivity extends BaseActivity implements View.OnClickListener, MainPresenterUI {

	private static final int REQUEST_KEYBOARD_ACTIVITY = 100;
	private static final int REQUEST_LOGIN_ACTIVITY_CODE = 101;
	private static final int REQUEST_PERSONAL_ACTIVITY = 102;

	// private BottomButtonLayout mBottomButtonLayout;
	private FrameLayout mMainLayout;
	// private VideoShareLayout videoShareLayout;
	private MapVideoLayout mMapVideoLayout;
	private BottomButtonLayout bottomButtonLayout;

	private ImageView mPersonalButton;
	private Toast inquiryToast;

	MainPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		((MainApplication) this.getApplication()).onMainCreate();
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);
		mMainLayout = (FrameLayout) findViewById(R.id.main);

		initMapviewLayout();
		initBottomButtonLayout();
		initTitleBarButtonLayout();
		initResetOrder();

		findViewById(R.id.title_bar_center_tv).setVisibility(View.GONE);
		findViewById(R.id.title_bar_logo).setVisibility(View.VISIBLE);
	}

	private void initMapviewLayout() {
		mMapVideoLayout = new MapVideoLayout(this);
		mMapVideoLayout.setUiTypeListener(presenter);
		mMapVideoLayout.setRequestConnectLayoutListener(presenter);
		mMapVideoLayout.setInterfactionBtnClickListener(presenter);
		mMapVideoLayout.setLiveInformationLayoutListener(presenter);
		mMapVideoLayout.setVideoWatcherListLayoutListener(presenter);
		mMapVideoLayout.setP2PVideoMainLayoutListener(presenter);
		mMapVideoLayout.setMessageMarqueeLayoutListener(presenter);
		mMapVideoLayout.setVideoShareBtnLayoutListener(presenter);
		mMapVideoLayout.setInquiryBidWidgetListener(presenter);
		mMapVideoLayout.setVideoShareRightWidgetListener(presenter);
		mMapVideoLayout.setOnSpinVolumeChangedListener(presenter);
		mMapVideoLayout.setP2PAudioConnectionPublisherLayoutListener(presenter);

		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		mMainLayout.addView(mMapVideoLayout, fl);
	}

	private void initTitleBarButtonLayout() {
		this.mPersonalButton = (ImageView) findViewById(R.id.title_bar_left_btn);
		mPersonalButton.setTag(MainPresenterUI.TITLE_BAR_BTN_TYPE_PERSONEL);
		this.mPersonalButton.setImageResource(R.drawable.user_icon);
		mPersonalButton.setOnClickListener(this);

	}

	private void initBottomButtonLayout() {
		bottomButtonLayout = (BottomButtonLayout) findViewById(R.id.bottom_layout);
		bottomButtonLayout.setListener(presenter);
	}

	private void initResetOrder() {
		mPersonalButton.bringToFront();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mMapVideoLayout.resetLocalCamera();

	}

	@Override
	protected void onPause() {
		super.onPause();
		// mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// mMapView.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		presenter.onUIDestroyed();
		((MainApplication) this.getApplication()).requestQuit();

	}

	@Override
	public void onBackPressed() {
		presenter.onReturnBtnClicked();
	}

	@Override
	public BasePresenter getPresenter() {

		if (presenter == null) {
			presenter = new MainPresenter(this, this);
		}
		return presenter;
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

	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.video_share_button:
			presenter.videoShareButtonClicked();
			break;
		case R.id.title_bar_left_btn:
			int tag = (Integer) v.getTag();
			if (tag == MainPresenterUI.TITLE_BAR_BTN_TYPE_BACK) {
				presenter.titleBackButtonClicked();
			} else if (tag == MainPresenterUI.TITLE_BAR_BTN_TYPE_PERSONEL) {
				presenter.personelButtonClicked();
			}
			break;
		}
	}

	@Override
	public void showTextKeyboard(boolean flag) {
		if (flag) {
			Intent i = new Intent();
			i.setClass(getBaseContext(), BottomButtonLayoutActivity.class);
			startActivityForResult(i, REQUEST_KEYBOARD_ACTIVITY);
		}
	}

	@Override
	public void showVideoScreentItem(int tag, boolean showFlag) {
		if (tag == MainPresenter.VIDEO_SCREEN_BTN_FLAG) {
			this.mMapVideoLayout.showVideoBtnLy(showFlag);
		} else if (tag == MainPresenter.VIDEO_BOTTOM_LY_FLAG) {
			this.mMapVideoLayout.showVideoWatcherListLy(showFlag);
		} else if (tag == MainPresenter.MESSAGE_MARQUEE_LY_SHOW) {
			this.mMapVideoLayout.showMarqueeMessageLayout(showFlag);
		}

	}

	public MapAPI getMainMap() {
		return mMapVideoLayout.getMap();
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
		i.setClass(getApplicationContext(), PersonelActivity.class);
		this.startActivityForResult(i, REQUEST_PERSONAL_ACTIVITY);
	}

	@Override
	public String getTextString() {
		return bottomButtonLayout.getEditText().toString();
	}

	private Toast last;

	@Override
	public void showSearchErrorToast() {
		if (last != null) {
			last.cancel();
		}
		last = Toast.makeText(this, R.string.main_search_no_element_found, Toast.LENGTH_SHORT);
		last.show();
	}

	@Override
	public void updateVideShareButtonText(boolean publish) {
		if (publish) {
			mMapVideoLayout.updateVideoShareBtnBackground(R.drawable.video_sharing_button_bg);
		} else {
			mMapVideoLayout.updateVideoShareBtnBackground(R.drawable.video_share_button_bg);
		}
	}



	@Override
	public void showError(int flag) {
		if (last != null) {
			last.cancel();
		}
		int res = -1;
		switch (flag) {
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


	@Override
	public void showDebugMsg(final String msg) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				((TextView) findViewById(R.id.title_bar_center_tv)).setText(msg);
			}

		});

	}

	@Override
	public void queuedLiveMessage(CharSequence msg) {
		mMapVideoLayout.addNewMessage(msg);
	}

	@Override
	public void updateBalanceSum(final float num) {
		mMapVideoLayout.updateBalanceSum(num);
	}

	@Override
	public void updateRendNum(final int num) {
		mMapVideoLayout.updateRendNum(num);
	}


	@Override
	public void showMarqueeMessage(boolean flag) {
		mMapVideoLayout.showMarqueeMessage(flag);
		// videoShareLayout.showMarqueeMessage(flag);
	}

	@Override
	public void doFinish() {
		finish();
	}


	ProgressDialog dialog;

	@Override
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


	@Override
	public void updateInterfactionFollowBtn(boolean followed) {
		if (followed) {
			mMapVideoLayout.updateFollowBtnImageResource(R.drawable.liver_interaction_cancel_follow_friend);
			mMapVideoLayout.updateFollowBtnTextResource(R.string.personel_item_user_cf_text);
		} else {
			mMapVideoLayout.updateFollowBtnImageResource(R.drawable.liver_interaction_follow);
			mMapVideoLayout.updateFollowBtnTextResource(R.string.personel_item_user_f_text);
		}
	}

	@Override
	public MapAPI getWatcherMapInstance() {
		// return this.videoShareLayout.getWatcherMapInstance();
		return null;
	}


	@Override
	public void addWatcher(int flag, Watcher watcher) {
		// if (flag == MainPresenter.WATCHER_FLAG_PUBLISHER) {
		// videoShareLayout.addWatcher(watcher);
		// } else if (flag == MainPresenter.WATCHER_FLAG_WATCHER) {
		// mMapVideoLayout.addWatcher(watcher);
		// }
	}

	@Override
	public void removeWatcher(int flag, Watcher watcher) {
		// if (flag == MainPresenter.WATCHER_FLAG_PUBLISHER) {
		// videoShareLayout.removeWatcher(watcher);
		// } else if (flag == MainPresenter.WATCHER_FLAG_WATCHER) {
		// mMapVideoLayout.removeWatcher(watcher);
		// }
	}

	@Override
	public VideoPlayer getVideoPlayer() {
		return mMapVideoLayout.getVideoPlayer();
	}

	@Override
	public void updateTitleBarBtn(int type) {
		mPersonalButton = (ImageView) findViewById(R.id.title_bar_left_btn);
		mPersonalButton.setTag(type);
		if (type == MainPresenterUI.TITLE_BAR_BTN_TYPE_BACK) {
			mPersonalButton.setImageResource(R.drawable.title_bar_return_btn);
		} else if (type == MainPresenterUI.TITLE_BAR_BTN_TYPE_PERSONEL) {
			mPersonalButton.setImageResource(R.drawable.user_icon);
		}
	}

	@Override
	public void cancelInquireState() {
		mMapVideoLayout.showInquiryWidget(false);
	}

	@Override
	public void updateMapAddressText(String text) {
		mMapVideoLayout.updateMapLocationAddress(text);
	}

	@Override
	public String getInquiryAward() {
		return mMapVideoLayout.getInquiryAward();
	}

	@Override
	public void setInquiryStateToWaiting(boolean wait) {
		mMapVideoLayout.disableInquiryBtn(wait);
	}

	@Override
	public void updateInquiryMessage(String msg) {
		if (this.inquiryToast != null) {
			inquiryToast.cancel();
			inquiryToast = null;
		}

		inquiryToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		inquiryToast.setText(msg);
		inquiryToast.setGravity(Gravity.CENTER, 0, 0);
		inquiryToast.show();
	}

	@Override
	public String getInquiryMessage() {
		return mMapVideoLayout.getInquiryMessage();
	}

	@Override
	public void showIncorrectAwardMessage(String message) {
		// TODO add show error
	}

	@Override
	public void showInquiryAcceptedMsg(String msg) {
		updateInquiryMessage(msg);
	}

	@Override
	public VideoPlayer getP2PVideoPlayer() {
		return mMapVideoLayout.getP2PVideoPlayer();
	}

	@Override
	public void updateLocalCameraType(int type) {
		if (MainPresenterUI.LOCAL_CAMERA_TYPE_SHARE == type) {
			mMapVideoLayout.resetLocalCamera();
		} else if (MainPresenterUI.LOCAL_CAMERA_TYPE_P2P_CONNECTION == type) {
			mMapVideoLayout.updateLocalCameraOnP2P();
		}
	}

	@Override
	public void updateInputMode(int type) {
		switch (type) {
		case MainPresenterUI.INPUT_MODE_NOTHING:
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
			break;
		case MainPresenterUI.INPUT_MODE_PAN:
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
			break;
		}

	}

	@Override
	public void showUILayout(int type, boolean show, Object tag) {
		switch (type) {
		case UI_LAYOUT_TYPE_MAP:
			mMapVideoLayout.showMap(show);
			break;
		case UI_LAYOUT_TYPE_P2P_AUDIO_REQUEST:
		case UI_LAYOUT_TYPE_P2P_VIDEO_REQUEST:
			mMapVideoLayout.showRequestingConnectionLy(show, tag);
			break;
		case UI_LAYOUT_TYPE_P2P_AUDIO_CONNECTION_PUBLISHER:
			mMapVideoLayout.showP2PAudioConnectionPublisherLy(show);
			break;
		case UI_LAYOUT_TYPE_P2P_AUDIO_CONNECTION_WATCHER:
			mMapVideoLayout.showP2PAudioConnectionWatcherLy(show);
			//TODO show toast
			break;
		case UI_LAYOUT_TYPE_P2P_VIDEO_CONNECTION_PUBLISHER:
			mMapVideoLayout.showP2PVideoConnectionPublisherLayout(show);
			break;
		case UI_LAYOUT_TYPE_P2P_VIDEO_CONNECTION_WATCHER:
			mMapVideoLayout.showP2PVideoConnectionWatcherLayout(show);
			break;
		case UI_LAYOUT_TYPE_LIVE_PUBLISHER_PERSONEL:
			this.mMapVideoLayout.showPublisherPersonelLy(show);
			break;
		case UI_LAYOUT_TYPE_BOTTOM_BTN:
			bottomButtonLayout.setVisibility(show ? View.VISIBLE : View.GONE);
			break;
		case UI_LAYOUT_TYPE_BID_PERSON_INFO:
			mMapVideoLayout.showPersonelWidgetForInquiryBider(show);
			break;
		}
	}
	/////////////////////////////////////////////////////////////

}
