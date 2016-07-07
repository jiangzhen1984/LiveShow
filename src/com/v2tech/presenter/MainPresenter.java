package com.v2tech.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import v2av.VideoPlayer;
import v2av.VideoPlayer.ViewItemListener;
import v2av.VideoRecorder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.V2.jni.util.V2Log;
import com.v2tech.map.LocationParameter;
import com.v2tech.map.MapAPI;
import com.v2tech.map.MapLocation;
import com.v2tech.map.MapStatus;
import com.v2tech.map.MapStatusListener;
import com.v2tech.map.Marker;
import com.v2tech.map.MarkerListener;
import com.v2tech.service.AsyncResult;
import com.v2tech.service.ConferenceService;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.InquiryAcceptenceHandler;
import com.v2tech.service.InquiryService;
import com.v2tech.service.LiveMessageHandler;
import com.v2tech.service.LiveService;
import com.v2tech.service.LiveStatusHandler;
import com.v2tech.service.LiveWathcingHandler;
import com.v2tech.service.MessageListener;
import com.v2tech.service.UserService;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.service.jni.RequestConfCreateResponse;
import com.v2tech.service.jni.RequestEnterConfResponse;
import com.v2tech.service.jni.SearchLiveResponse;
import com.v2tech.util.MessageUtil;
import com.v2tech.util.SPUtil;
import com.v2tech.v2liveshow.R;
import com.v2tech.view.MainApplication;
import com.v2tech.view.MapVideoLayout.ScreenType;
import com.v2tech.view.MapVideoLayout.UITypeStatusChangedListener;
import com.v2tech.view.P2PMessageActivity;
import com.v2tech.vo.AttendDeviceIndication;
import com.v2tech.vo.Live;
import com.v2tech.vo.PublishingLive;
import com.v2tech.vo.User;
import com.v2tech.vo.UserDeviceConfig;
import com.v2tech.vo.ViewLive;
import com.v2tech.vo.Watcher;
import com.v2tech.vo.conference.ConferenceGroup;
import com.v2tech.vo.inquiry.InquiryData;
import com.v2tech.vo.msg.VMessage;
import com.v2tech.vo.msg.VMessageAudioVideoRequestItem;
import com.v2tech.vo.msg.VMessageTextItem;
import com.v2tech.widget.BottomButtonLayout.BottomButtonLayoutListener;
import com.v2tech.widget.InquiryBidWidget.InquiryBidWidgetListener;
import com.v2tech.widget.LiveInformationLayout.LiveInformationLayoutListener;
import com.v2tech.widget.LiverInteractionLayout.InterfactionBtnClickListener;
import com.v2tech.widget.MessageMarqueeLinearLayout.MessageMarqueeLayoutListener;
import com.v2tech.widget.P2PAudioLiverLayout.P2PAudioLiverLayoutListener;
import com.v2tech.widget.P2PAudioWatcherLayout.P2PAudioWatcherLayoutListener;
import com.v2tech.widget.P2PVideoMainLayout.P2PVideoMainLayoutListener;
import com.v2tech.widget.RequestConnectLayout.RequestConnectLayoutListener;
import com.v2tech.widget.VerticalSpinWidget.OnSpinVolumeChangedListener;
import com.v2tech.widget.VideoShareBtnLayout.VideoShareBtnLayoutListener;
import com.v2tech.widget.VideoShareRightWidget.VideoShareRightWidgetListener;
import com.v2tech.widget.VideoWatcherListLayout.VideoWatcherListLayoutListener;

public class MainPresenter extends BasePresenter implements
		MarkerListener,UITypeStatusChangedListener, 
		BottomButtonLayoutListener,VideoShareRightWidgetListener, 
		LiveInformationLayoutListener, RequestConnectLayoutListener,
		InterfactionBtnClickListener, VideoWatcherListLayoutListener,
		P2PVideoMainLayoutListener, P2PAudioWatcherLayoutListener,
		P2PAudioLiverLayoutListener, VideoShareBtnLayoutListener,ViewItemListener,
		MessageMarqueeLayoutListener, LiveStatusHandler, LiveMessageHandler, InquiryAcceptenceHandler, 
		LiveWathcingHandler, MapStatusListener, InquiryBidWidgetListener, OnSpinVolumeChangedListener {

	private static final int INIT = 1;
	private static final int RECOMMENDAATION = 3;
	private static final int CREATE_VIDEO_SHARE_CALL_BACK = 4;
	private static final int REPORT_LOCATION = 5;
	private static final int CREATE_VIDEO_SHARE = 6;
	private static final int SEARCH_LIVE = 7;
	private static final int SEARCH_LIVE_CALLBACK = 8;
	private static final int WATCHING_REQUEST_CALLBACK = 9;
	private static final int ATTEND_LISTENER = 11;
	private static final int CANCEL_PUBLISHING_REQUEST_CALLBACK = 12;
	private static final int WATCHER_DEVICE_LISTENER = 14;
	private static final int WATCHER_LIST = 15;
	private static final int QUERY_MAP_LOCATION_CALL_BACK = 16;

	private static final int UI_HANDLE_UPDATE_VIDEO_SCREEN = 1;
	private static final int UI_HANDLE_AUDIO_CALL_TIMEOUT = 3;

	public static final int VIDEO_SCREEN_BTN_FLAG = 1;
	public static final int VIDEO_SHARE_BTN_SHOW = 1 << 1;
	public static final int VIDEO_BOTTOM_LY_FLAG = 1 << 2;
	public static final int FOLLOW_COUNT_SHOW_FLAG = 1 << 3;
	public static final int LIVER_SHOW_FLAG = 1 << 4;
	private static final int PUBLISHING_FLAG = 1 << 5;
	private static final int WATCHING_FLAG = 1 << 6;
	private static final int LOCAL_CAMERA_OPENING = 1 << 7;
	private static final int BOTTOM_LAYOUT_SHOW = 1 << 8;
	private static final int KEYBOARD_SHOW = 1 << 9;
	private static final int MAP_CENTER_UPDATE = 1 << 10;
	private static final int LIVER_INTERACTION_LAY_SHOW = 1 << 11;
	private static final int MESSAGE_MARQUEE_ENABLE = 1 << 12;
	private static final int AUDIO_CALL_REQUEST_SHOW = 1 << 13;
	private static final int VIDEO_CALL_REQUEST_SHOW = 1 << 14;
	private static final int VIDEO_P2P_SHOW = 1 << 15;
	private static final int AUDIO_P2P_SHOW = 1 << 16;
	private static final int PROGRESS_DIALOG_SOWN = 1 << 17;
	public static final int MESSAGE_MARQUEE_LY_SHOW = 1 << 18;
	
	
	public static final int WATCHER_FLAG_PUBLISHER = 1;
	public static final int WATCHER_FLAG_WATCHER = 2;
	
	
	public static final int TITLE_BAR_BTN_TYPE_BACK = 1;
	public static final int TITLE_BAR_BTN_TYPE_PERSONEL = 2;

	private Context context;
	private MainPresenterUI ui;
	private UserService us;
	private ConferenceService vs;
	private LiveService ls;
	private Handler h;
	private InquiryService is;

	private int videoScreenState;


	// ///////////////////////////////
	private MapAPI mapInstance;

	private MapLocation currentLocation;
	
	private Handler uiHandler;

	// ///////////////////////////////////////
	private Map<Live, Marker> cacheMarker;
	
	
	private VideoPlayer  vpController;
	
	private List<ViewLive> viewLiveList;
	
	private ViewLive currentViewLive;
	private PublishingLive publishingLive;
	private InquiryData inquiryData;
	
	private AudioManager audioManager; 
	
	// /////

	public MainPresenter(Context context, MainPresenterUI ui) {
		super();
		this.ui = ui;
		this.context = context;
		videoScreenState = (VIDEO_SCREEN_BTN_FLAG | VIDEO_BOTTOM_LY_FLAG
				| FOLLOW_COUNT_SHOW_FLAG | BOTTOM_LAYOUT_SHOW
				| MESSAGE_MARQUEE_ENABLE | VIDEO_SHARE_BTN_SHOW | MESSAGE_MARQUEE_LY_SHOW);

		uiHandler = new UiHandler(this, ui);
		viewLiveList = new ArrayList<ViewLive>(20);
		audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	}

	public interface MainPresenterUI {

		public MapAPI getMainMap();

		public void showTextKeyboard(boolean flag);

		public void showVideoScreentItem(int tag, boolean showFlag);

		public void showLoginUI();

		public void showPersonelUI();

		public void showSearchErrorToast();

		public String getTextString();

		public void updateVideShareButtonText(boolean publish);

		public SurfaceView getCameraSurfaceView();

		public SurfaceView getP2PMainSurface();

		public SurfaceView getP2PMainWatherSurface();

		public void showBottomLayout(boolean flag);

		public void showError(int flag);

		public void showDebugMsg(String msg);

		public void queuedLiveMessage(CharSequence msg);
		
		public void updateWatchNum(int num);

		public void updateRendNum(int num);

		public void showRedBtm(boolean flag);

		public void showIncharBtm(boolean flag);

		public void updateBalanceSum(final float num);

		public void showLiverInteractionLayout(boolean flag);

		public void showConnectRequestLayout(boolean flag);

		public void showMarqueeMessage(boolean flag);

		public void closeVideo(boolean flag);

		public void doFinish();
		
		// 1 for back btn 2 for personel btn
		public void updateTitleBarBtn(int type);

		// 1 for audio 2 for video
		public void updateConnectLayoutBtnType(int type);

		public void showP2PVideoLayout(boolean flag);

		public void showWatcherP2PVideoLayout(boolean flag);

		public void showWatcherP2PAudioLayout(boolean flag);
		
		public void showPersonelWidgetForInquiry(boolean flag);
		
		public void showInquiryAcceptedMsg(String msg);

		public void showProgressDialog(boolean flag, String text);

		public void updateInterfactionFollowBtn(boolean followed);

		public MapAPI getWatcherMapInstance();
		
		public void addWatcher(int flag, Watcher watcher);
		
		public void removeWatcher(int flag, Watcher watcher);
		
		public VideoPlayer getVideoPlayer();
		
		public void showMap(boolean flag);
		
		public void cancelInquireState();
		
		public void updateMapAddressText(String text);
		
		public String getInquiryAward();
		
		public String getInquiryMessage();
		
		public void showIncorrectAwardMessage(String message);
		
		public void setInquiryStateToWaiting(boolean wait);
		
		public void updateInquiryMessage(String msg);
	}

	public void videoShareButtonClicked() {
		if (isState(PUBLISHING_FLAG)) {
			unsetState(PUBLISHING_FLAG);
			ui.updateVideShareButtonText(false);
			vs.quitConference(publishingLive, new MessageListener(h,
					CANCEL_PUBLISHING_REQUEST_CALLBACK, null));
		} else {
			setState(PUBLISHING_FLAG);
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
	
	
	public void titleBackButtonClicked() {
		//FIXME check UI Type first
		ui.cancelInquireState();
	}

	public void onKeyboardChildUIFinished(int ret, Intent data) {
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

	@Override
	public void onReturnBtnClicked() {
		if (isState(AUDIO_CALL_REQUEST_SHOW)
				|| isState(VIDEO_CALL_REQUEST_SHOW)) {
			this.unsetState(AUDIO_CALL_REQUEST_SHOW);
			this.unsetState(VIDEO_CALL_REQUEST_SHOW);
			ui.showConnectRequestLayout(false);
			return;
		}
		ui.doFinish();
	}

	public void onLoginChildUIFinished(int ret, Intent data) {

	}

	@Override
	public void onUICreated() {
		super.onUICreated();
		cacheMarker = new HashMap<Live, Marker>();
		h = new LocalHandler(backendThread.getLooper());
		Message.obtain(h, INIT).sendToTarget();
	}

	@Override
	public void onUIStarted() {
		if (vpController == null) {
			vpController = ui.getVideoPlayer();
			vpController.setItemListener(this);
		}
		
		if (mapInstance == null) {
			mapInstance = ui.getMainMap();
			mapInstance.registerMakerListener(this);
			mapInstance.addMapStatusListener(this);
		}
		startLocationScan();
	}

	@Override
	public void onUIStopped() {
		stopLocate();
	}

	@Override
	public void onUIDestroyed() {
		mapInstance.removeMapStatusListener(this);
		us.clearCalledBack();
		vs.clearCalledBack();
		ls.clearCalledBack();
		is.clearCalledBack();
		this.h.removeMessages(REPORT_LOCATION);
		cacheMarker.clear();
		super.onUIDestroyed();
	}

	// /////////////////////////////////////////////////////////////////////////////////////////

	private void searchMap(String text) {
		mapInstance.animationSearch(text);
	}

	private void startLocationScan() {
		mapInstance.startLocate(mapInstance.buildParameter(context));
	}

	private void stopLocate() {
		if (mapInstance != null) {
			mapInstance.stopLocate(null);
		}
	}

	private void updateMapCenter(MapLocation lw, LocationParameter param) {
		if (lw == null) {
			return;
		}

		this.mapInstance.updateMap(mapInstance.buildUpater(lw));

		if (isState(MAP_CENTER_UPDATE)) {
			setState(MAP_CENTER_UPDATE);
		}
	}

	// /////////////////MarkerListener

	@Override
	public boolean onMarkerClickedListener(Marker m) {
		if (isState(PUBLISHING_FLAG)) {
			// TODO illegal state
			return false;
		}
		if (isState(WATCHING_FLAG)) {
			// quit from old
			currentViewLive.playing = false;
			currentViewLive.showing = false;
			currentViewLive.surfaveViewIdx = -1;
			
			UserDeviceConfig duc = new UserDeviceConfig(4,
					currentViewLive.live.getLid(), GlobalHolder.getInstance()
							.getCurrentUserId(), "", null);
			vs.requestCloseVideoDevice(duc, null);
			vs.requestExitConference(currentViewLive.live, null);
			unsetState(WATCHING_FLAG);
		}

		// join new one
		Live l = (Live) m.getLive();
		if (l != null) {
			vs.requestEnterConference(l, new MessageListener(h,
					WATCHING_REQUEST_CALLBACK, null));
			ViewLive vl= findViewLive(l);
			if (vl == null) {
				throw new RuntimeException(" no viewlive :" + l);
			}
			this.currentViewLive = vl;
			currentViewLive.playing = true;
			currentViewLive.showing = true;
			currentViewLive.surfaveViewIdx = vpController.getCurrentItemIdx();
			updateLiveScreen(l);
			setState(WATCHING_FLAG);
		}
		return true;
	}


	// /////////////MarkerListener


	// //////////////VideoWatcherListLayoutListener

	@Override
	public void onCloseBtnClicked(View v) {
		ui.closeVideo(true);
		// FIXME close video btn
	}

	@Override
	public void onLiveInfoTipsBtnClicked(View v) {
		V2Log.i("====> onLiveInfoTipsBtnClicked");
	}

	@Override
	public void onLiveInfoRecommandBtnClicked(View v) {
		if (currentViewLive == null) {
			// TODO show incorrect UI
			return;
		}
		ls.recommend(currentViewLive.live, currentViewLive.live.isRend());
		currentViewLive.live.setRend(!currentViewLive.live.isRend());
		currentViewLive.live.rendCount += (currentViewLive.live.isRend() ? 1 : -1);
		updateLiveScreen(currentViewLive.live);
	}

	// //////////////////LiveInformationLayoutListener

	// //////////////////MessageMarqueeLayoutListener
	@Override
	public void onMessageSettingBtnClicked(View v) {
		if (isState(MESSAGE_MARQUEE_ENABLE)) {
			this.unsetState(MESSAGE_MARQUEE_ENABLE);
			ui.showMarqueeMessage(false);
		} else {
			this.setState(MESSAGE_MARQUEE_ENABLE);
			ui.showMarqueeMessage(true);
		}
	}

	// //////////////////MessageMarqueeLayoutListener

	// //////////////////VideoWatcherListLayoutListener
	@Override
	public void onPublisherBtnClicked(View v) {
		// ui.showWatcherP2PAudioLayout(true);
		if (isState(LIVER_INTERACTION_LAY_SHOW)) {
			this.unsetState(LIVER_INTERACTION_LAY_SHOW);
			ui.showLiverInteractionLayout(false);
		} else {
			this.setState(LIVER_INTERACTION_LAY_SHOW);
			ui.showLiverInteractionLayout(true);
		}

	}

	// //////////////////VideoWatcherListLayoutListener


	// ///////////RequestConnectLayoutListener
	@Override
	public void onRequestConnectLeftBtnClicked(View v) {
		if (!isState(AUDIO_CALL_REQUEST_SHOW)
				&& !isState(VIDEO_CALL_REQUEST_SHOW)) {
			return;
		}
		ui.showConnectRequestLayout(false);

		unsetState(AUDIO_CALL_REQUEST_SHOW);
		unsetState(VIDEO_CALL_REQUEST_SHOW);
	}

	// TODO FIXME add
	public long requestUid;

	@Override
	public void onRequestConnectRightBtnClicked(View v) {
		if (!isState(AUDIO_CALL_REQUEST_SHOW)
				&& !isState(VIDEO_CALL_REQUEST_SHOW)) {
			// TODO show UI
			return;
		}
		ui.showConnectRequestLayout(false);
		if (isState(VIDEO_CALL_REQUEST_SHOW)) {
			setState(VIDEO_P2P_SHOW);
			unsetState(VIDEO_CALL_REQUEST_SHOW);
			ui.showP2PVideoLayout(true);
			User u = GlobalHolder.getInstance().getUser(requestUid);
			if (u.ll == null || u.ll.size() <= 0) {
				// TODO
				return;
			}
			UserDeviceConfig udc = u.ll.iterator().next();
			udc.setGroupID(this.currentViewLive.live.getLid());
			udc.setGroupType(4);
			VideoPlayer vp = new VideoPlayer();
			vp.SetSurface(ui.getP2PMainSurface().getHolder());
			udc.setVp(vp);
			vs.requestOpenVideoDevice(
					new ConferenceGroup(this.currentViewLive.live.getLid(), "", null,
							null, null), udc, null);

			this.requestConnection(this.currentViewLive.live.getLid(),
					VMessageAudioVideoRequestItem.TYPE_VIDEO,
					VMessageAudioVideoRequestItem.ACTION_ACCEPT);

		} else if (isState(AUDIO_CALL_REQUEST_SHOW)) {
			setState(AUDIO_P2P_SHOW);
			unsetState(AUDIO_CALL_REQUEST_SHOW);
			ui.showMap(true);
			this.requestConnection(this.currentViewLive.live.getLid(),
					VMessageAudioVideoRequestItem.TYPE_AUDIO,
					VMessageAudioVideoRequestItem.ACTION_ACCEPT);
		}
	}

	// ///////////RequestConnectLayoutListener

	// ///////////InterfactionBtnClickListener

	@Override
	public void onChattingBtnClicked(View v) {
		if (!isState(WATCHING_FLAG)) {
			// TODO show incorrect UI
			return;
		}

		if (isState(PROGRESS_DIALOG_SOWN)) {
			return;
		}

		ui.showProgressDialog(true,
				context.getResources()
						.getString(R.string.audio_call_connecting));
		Message timout = Message
				.obtain(uiHandler, UI_HANDLE_AUDIO_CALL_TIMEOUT);
		uiHandler.sendMessageDelayed(timout, 5000);
		setState(PROGRESS_DIALOG_SOWN);

		requestConnection(this.currentViewLive.live.getLid(),
				VMessageAudioVideoRequestItem.TYPE_AUDIO,
				VMessageAudioVideoRequestItem.ACTION_REQUEST);
	}

	@Override
	public void onVideoCallBtnClicked(View v) {
		if (!isState(WATCHING_FLAG)) {
			// TODO show incorrect UI
			return;
		}
		requestConnection(this.currentViewLive.live.getLid(),
				VMessageAudioVideoRequestItem.TYPE_VIDEO,
				VMessageAudioVideoRequestItem.ACTION_REQUEST);
	}

	@Override
	public void onMsgBtnClicked(View v) {
		if (!isState(WATCHING_FLAG)) {
			// TODO show incorrect UI
			return;
		}

		Intent i = new Intent();
		i.putExtra("chatuserid", this.currentViewLive.live.getPublisher().getmUserId());
		i.setClass(context, P2PMessageActivity.class);
		context.startActivity(i);
	}

	@Override
	public void onFollowBtnClick(View v) {
		if (currentViewLive == null) {
			return;
		}
		// TODO check friend status
		us.followUser(currentViewLive.live.getPublisher(), true);
		ui.updateInterfactionFollowBtn(true);
	}

	// ///////////InterfactionBtnClickListener

	// ///////////P2PVideoMainLayoutListener
	@Override
	public void onP2PVideoMainLeftBtnClicked(View v) {
		this.unsetState(VIDEO_P2P_SHOW);
		ui.showP2PVideoLayout(false);
		ui.showWatcherP2PVideoLayout(false);
		// If liver is not self, than close local camera device

		int type = VMessageAudioVideoRequestItem.TYPE_AUDIO;
		if (isState(VIDEO_P2P_SHOW)) {
			type = VMessageAudioVideoRequestItem.TYPE_VIDEO;
		} else if (isState(AUDIO_P2P_SHOW)) {
			type = VMessageAudioVideoRequestItem.TYPE_AUDIO;
		}
		requestConnection(this.currentViewLive.live.getLid(), type,
				VMessageAudioVideoRequestItem.ACTION_HANG_OFF);
		if (currentViewLive.live.getPublisher().getmUserId() != GlobalHolder
				.getInstance().getCurrentUserId()) {

			UserDeviceConfig duc = new UserDeviceConfig(4,
					this.currentViewLive.live.getLid(), GlobalHolder.getInstance()
							.getCurrentUserId(), "", null);
			vs.requestCloseVideoDevice(duc, null);
		} else {
			// TODO close remote device
		}

	}

	@Override
	public void onP2PVideoMainRightBtnClicked(View v) {

	}

	// ///////////P2PVideoMainLayoutListener

	// ///////////P2PAudioWatcherLayoutListener
	@Override
	public void onRecordBtnClicked(View view) {

	}

	@Override
	public void onChatBtnClicked(View view) {

	}

	@Override
	public void onTipsBtnClicked(View view) {
	}

	// ///////////P2PAudioWatcherLayoutListener

	// ///////////P2PAudioLiverLayoutListener

	public void onDeclineBtn(View view) {
		unsetState(AUDIO_P2P_SHOW);
		ui.showMap(false);
		this.requestConnection(this.currentViewLive.live.getLid(),
				VMessageAudioVideoRequestItem.TYPE_AUDIO,
				VMessageAudioVideoRequestItem.ACTION_HANG_OFF);
	}

	public void onMapReturnBtn(View view) {
		if (!isState(VIDEO_SHARE_BTN_SHOW)) {
			ui.showMap(false);
			setState(VIDEO_SHARE_BTN_SHOW);
			// show map view
			ui.showMap(false);
		}
	}

	// ///////////P2PAudioLiverLayoutListener///////////////////////////////////////////////////

	// ///////////VideoShareBtnLayoutListener///////////////////////////////////////////////////

	@Override
	public void onVideoSharedBtnClicked(View v) {
		videoShareButtonClicked();
	}

	public void onMapShareBtnClicked(View v) {
		if (isState(VIDEO_SHARE_BTN_SHOW)) {
			unsetState(VIDEO_SHARE_BTN_SHOW);
			// show map view
			ui.showMap(true);

			// TODO add watcher list marker to map
			if (currentViewLive != null) {
				ls.getWatcherList(currentViewLive.live, new MessageListener(h,
						WATCHER_LIST, currentViewLive.live));
			}
		}
	}

	// ///////////VideoShareBtnLayoutListener///////////////////////////////////////////////////

	// ///////////LiveStatusHandler///////////////////////////////////////////////////

	public void handleNewLivePushlishment(Live l) {
		V2Log.i("==== get new live -->" + l.getLid());
		addLiveMarker(l);
	}

	public void handleLiveFinished(Live l) {
		Marker m = cacheMarker.get(l);
		if (m != null) {
			this.mapInstance.removeMarker(m);
		} else {
			V2Log.e("===> no marker for live id " + l);
		}
	}

	// ///////////LiveStatusHandler///////////////////////////////////////////////////

	// ///////////LiveMessageHandler///////////////////////////////////////////////////

	public void onAudioMessage(long liveId, long uid, int opt) {
		if (isState(AUDIO_CALL_REQUEST_SHOW)
				|| isState(VIDEO_CALL_REQUEST_SHOW)) {
			return;
		}

		if (opt == VMessageAudioVideoRequestItem.ACTION_REQUEST) {
			requestUid = uid;
			this.videoScreenState |= AUDIO_CALL_REQUEST_SHOW;
			ui.updateConnectLayoutBtnType(AUDIO_CALL_REQUEST_SHOW);
			ui.showConnectRequestLayout(true);
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_ACCEPT) {
			uiHandler.removeMessages(UI_HANDLE_AUDIO_CALL_TIMEOUT);
			ui.showProgressDialog(false, null);
			ui.showWatcherP2PAudioLayout(true);
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_HANG_OFF) {
			ui.showWatcherP2PAudioLayout(false);
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_DECLINE) {

		}

	}

	public void onVdideoMessage(long liveId, long uid, int opt) {
		if (isState(AUDIO_CALL_REQUEST_SHOW)
				|| isState(VIDEO_CALL_REQUEST_SHOW)) {
			return;
		}
		if (opt == VMessageAudioVideoRequestItem.ACTION_REQUEST) {
			requestUid = uid;
			this.videoScreenState |= VIDEO_CALL_REQUEST_SHOW;
			ui.updateConnectLayoutBtnType(VIDEO_CALL_REQUEST_SHOW);
			ui.showConnectRequestLayout(true);
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_ACCEPT) {
			ui.showWatcherP2PVideoLayout(true);
			UserDeviceConfig duc = new UserDeviceConfig(4,
					this.currentViewLive.live.getLid(), GlobalHolder.getInstance()
							.getCurrentUserId(), "", null);
			VideoRecorder.VideoPreviewSurfaceHolder = ui
					.getP2PMainWatherSurface().getHolder();
			VideoRecorder.VideoPreviewSurfaceHolder
					.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			ui.showBottomLayout(false);
			unsetState(BOTTOM_LAYOUT_SHOW);
			vs.requestOpenVideoDevice(duc, null);
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_HANG_OFF) {
			ui.showWatcherP2PVideoLayout(false);
			if (uid != currentViewLive.live.getPublisher().getmUserId()) {
				// close local device
				UserDeviceConfig duc = new UserDeviceConfig(4,
						this.currentViewLive.live.getLid(), GlobalHolder.getInstance()
								.getCurrentUserId(), "", null);
				vs.requestCloseVideoDevice(duc, null);
			} else {
				// close remote device
				VideoPlayer vp = new VideoPlayer();
				vp.SetSurface(ui.getP2PMainSurface().getHolder());
				UserDeviceConfig duc = new UserDeviceConfig(0,
						this.currentViewLive.live.getLid(), uid, GlobalHolder
								.getInstance().getUser(uid).ll.iterator()
								.next().getDeviceID(), vp);
				vs.requestCloseVideoDevice(duc, null);

			}
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_DECLINE) {

		}
	}

	public void onLiveMessage(long liveId, long uid, VMessage vm) {
		ui.queuedLiveMessage(MessageUtil.buildContent(context, vm));
	}

	public void onP2PMessage(VMessage vm) {

	}

	// ///////////LiveMessageHandler///////////////////////////////////////////////////

	// ///////////BottomButtonLayoutListener///////////////////////////////////////////////////

	public void onMapSearchBtnClicked(View v) {
		String text = ui.getTextString();
		if (TextUtils.isEmpty(text)) {
			return;
		}
		mapInstance.animationSearch(text);
		searchMap(text);
	}

	public void onMessageSendBtnClicked(View v) {
		String txt = ui.getTextString();
		if (TextUtils.isEmpty(txt)) {
			return;
		}
		sendMessage(txt);
	}

	public void onEditTextClicked(View v) {
		this.videoScreenState |= KEYBOARD_SHOW;
		ui.showTextKeyboard(true);
	}

	public void onLocationBtnClicked(View v) {

	}

	// ///////////BottomButtonLayoutListener///////////////////////////////////////////////////
	
	///////////LiveWathcingHandler///////////////////////////////////////////////////
	
	@Override
	public void onUserWatched(Live l, User user) {
		Watcher w = new Watcher(user.getmUserId());
		if (isState(WATCHING_FLAG)) {
			ui.addWatcher(WATCHER_FLAG_WATCHER, w);
		} else {
			ui.addWatcher(WATCHER_FLAG_PUBLISHER, w);
		}
		
	}

	@Override
	public void onWatcherLeaved(Live l, User user) {
		Watcher w = new Watcher(user.getmUserId());
		if (isState(WATCHING_FLAG)) {
			ui.removeWatcher(WATCHER_FLAG_WATCHER, w);
		} else {
			ui.removeWatcher(WATCHER_FLAG_PUBLISHER, w);
		}
		
	}
	
	
///////////LiveWathcingHandler///////////////////////////////////////////////////
	
	
	///////////UITypeStatusChangedListener///////////////////////////////////////////////////	
	@Override
	public void onUITypeChanged(ScreenType screenType) {
		switch (screenType) {
		case INQUIRE_BIDING:
			ui.showBottomLayout(false);
			this.unsetState(BOTTOM_LAYOUT_SHOW);
			//Update title to back
			ui.updateTitleBarBtn(TITLE_BAR_BTN_TYPE_BACK);
			mapInstance.getLocationName(mapInstance.getMapCenter(), new MessageListener(uiHandler, QUERY_MAP_LOCATION_CALL_BACK, null));
			break;
		case VIDEO_MAP:
			if (isState(LOCAL_CAMERA_OPENING)) {
				UserDeviceConfig duc = new UserDeviceConfig(0, 0, GlobalHolder
						.getInstance().getCurrentUserId(), "", null);
				vs.requestCloseVideoDevice(duc, null);
				unsetState(LOCAL_CAMERA_OPENING);
						
			}
			if (isState(PUBLISHING_FLAG)) {
				videoShareButtonClicked();
			}
			
			ui.updateTitleBarBtn(TITLE_BAR_BTN_TYPE_PERSONEL);
			cancelInquiry();
			break;
		case VIDEO_PUBLISHER_SHOW:
			break;
		case VIDEO_SHARE:
			UserDeviceConfig duc = new UserDeviceConfig(0, 0, GlobalHolder
			.getInstance().getCurrentUserId(), "", null);
			vs.requestOpenVideoDevice(duc, null);
			ui.showBottomLayout(false);
			this.setState(LOCAL_CAMERA_OPENING | VIDEO_SHARE_BTN_SHOW);
			this.unsetState(BOTTOM_LAYOUT_SHOW);
			break;
		case VIDEO_SHARE_CONNECTION_REQUESTING:
			break;
		case VIDEO_SHARE_MAP:
			this.unsetState(VIDEO_SHARE_BTN_SHOW);
			break;
		case VIDEO_SHARE_P2P_PUBLISHER:
			break;
		case VIDEO_SHARE_P2P_WATCHER:
			break;
		case VIDEO_WATCHING_AUDIO_CONNECTION:
			break;
		default:
			break;
		
		}
		
	}
	///////////UITypeStatusChangedListener///////////////////////////////////////////////////
	
	
	
	///////////MapStatusListener///////////////////////////////////////////////////
	
	public void onMapStatusUpdated(MapStatus ms) {
		MapLocation ml = ms.getCenter();
		mapInstance.getLocationName(ml, new MessageListener(uiHandler, QUERY_MAP_LOCATION_CALL_BACK, null));
	}
	

	@Override
	public void onSelfLocationUpdated(MapLocation ml) {
		this.currentLocation = ml;
		this.updateMapCenter(ml, ml.getParameter());
		reportLocation();
	}
	
	///////////MapStatusListener///////////////////////////////////////////////////
	
	
	///////////InquiryBidWidgetListener///////////////////////////////////////////////////
	public void onInquiryLauchBtnClicked(View v) {
		MapLocation ml = mapInstance.getMapCenter();
		String award = ui.getInquiryAward();
		String desc = ui.getInquiryMessage();
		float faward =0;
		try {
			faward = Float.parseFloat(award);
		} catch(NumberFormatException e) {
			ui.showIncorrectAwardMessage(context.getResources().getString(R.string.inquiry_error_incorrect_award));
		}
		if (inquiryData == null) {
			inquiryData = new InquiryData();
		}
		inquiryData.targetLat = ml.getLat();
		inquiryData.targetLng = ml.getLng();
		inquiryData.id = is.startInquiry(faward, ml.getLat(), ml.getLng(), desc);
		if (inquiryData.id < 0) {
			//TODO notify user
		} else {
			ui.setInquiryStateToWaiting(true);
			ui.updateInquiryMessage(context.getResources().getString(R.string.inquiry_start_notification));
		}
	}
	
	///////////InquiryBidWidgetListener///////////////////////////////////////////////////
	
	
	///////////VideoShareRightWidgetListener///////////////////////////////////////////////////
	@Override
	public void onCameraSwitchBtnClick(View v) {
		UserDeviceConfig duc = new UserDeviceConfig(0, 0, GlobalHolder
				.getInstance().getCurrentUserId(), "", null);
		vs.switchCamera(duc);
	}
	
	///////////VideoShareRightWidgetListener///////////////////////////////////////////////////
	
	
	
	
	///////////ViewItemListener///////////////////////////////////////////////////
	@Override
	public void onCurrentItemChanged(int current, int newIdx) {
			for (ViewLive vl : viewLiveList) {
				if (vl.surfaveViewIdx == current) {
					UserDeviceConfig duc = new UserDeviceConfig(4,
							vl.live.getLid(), GlobalHolder.getInstance()
									.getCurrentUserId(), "", null);
					vs.requestCloseVideoDevice(duc, null);
					
					vs.requestExitConference(vl.live, null);
					vl.showing = false;
				}
				if (vl.surfaveViewIdx == newIdx) {
					if (vl.playing) {
						vs.requestEnterConference(vl.live, new MessageListener(h,
								WATCHING_REQUEST_CALLBACK, null));
						vl.showing = true;
					}
					this.currentViewLive = vl;
				}
			}
			
			//TODO if nothing, need to push one
	}
	
	///////////ViewItemListener///////////////////////////////////////////////////
	
	
	///////////OnSpinVolumeChangedListener///////////////////////////////////////////////////
	@Override
	public void OnSpinVolumeChanged(float cent) {
		int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(cent * max), AudioManager.FLAG_PLAY_SOUND);
	}
	///////////OnSpinVolumeChangedListener///////////////////////////////////////////////////
	

	
	
	
	///////////InquiryAcceptenceHandler///////////////////////////////////////////////////
	@Override
	public void onTake(User user, InquiryData data) {
		ui.showPersonelWidgetForInquiry(true);
		ui.showInquiryAcceptedMsg(user.getName()+context.getResources().getString(R.string.inquiry_accepted));
		MapLocation target = mapInstance.buildLocation(inquiryData.targetLat, inquiryData.targetLng);
		MapLocation source = mapInstance.buildLocation(data.sourceLat, data.sourceLng);
		V2Log.i(" inquiry:  source: " + source+"  target:"+target);
		mapInstance.showRoadMap(source, target);
		
	}
	///////////InquiryAcceptenceHandler///////////////////////////////////////////////////
	
	private void requestConnection(long lid, int type, int action) {
		long uid = GlobalHolder.getInstance().getCurrentUser().getmUserId();
		// 4 for group type
		VMessage vmsg = new VMessage(4, lid, GlobalHolder.getInstance()
				.getCurrentUser(), new Date(System.currentTimeMillis()));
		new VMessageAudioVideoRequestItem(vmsg, type, uid, lid, action);
		vs.sendMessage(vmsg);
	}


	private void setState(int flag) {
		this.videoScreenState |= flag;
	}

	private void unsetState(int flag) {
		this.videoScreenState &= (~flag);
	}

	private boolean isState(int flag) {
		return (this.videoScreenState & flag) == flag;
	}

	private void updateLiveScreen(Live l) {

		ui.updateRendNum(l.rendCount);
		// ui.updateWatchNum(l.watcherCount);
		ui.showRedBtm(l.isRend());
		ui.showIncharBtm(l.isInchr);
		ui.updateBalanceSum(l.balanceSum);
	}

	private void doInitInBack() {
		vs = new ConferenceService();
		us = new UserService();
		ls = new LiveService();
		is = new InquiryService();
		vs.registerAttendeeDeviceListener(h, ATTEND_LISTENER, null);
		// vs.registerAttendeeDeviceListener(h, WATCHER_DEVICE_LISTENER, null);

		if (GlobalHolder.getInstance().getCurrentUser() == null) {
			TelephonyManager tl = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String phone = SPUtil.getConfigStrValue(context, "cellphone");
			String code = SPUtil.getConfigStrValue(context, "code");
			String lp = tl.getLine1Number();
			if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code)) {
				if (TextUtils.isEmpty(lp)) {
					lp = System.currentTimeMillis() + "";
				}
				us.login(lp, "", true, null);
			} else {
				us.login(phone, code, false, null);
			}
		} else {
			V2Log.w("Already login, no need login again"
					+ GlobalHolder.getInstance().getCurrentUser());
		}


	}

	private void doRecommendationInBack() {

	}

	private void handleCreateVideoShareBack(JNIResponse resp) {
		V2Log.e("==> CREATE VIDEO SHARE:" + resp.getResult());
		if (resp.getResult() == JNIResponse.Result.SUCCESS) {
			RequestConfCreateResponse rcr = (RequestConfCreateResponse) resp;
			publishingLive.setLid(rcr.getConfId());
			videoScreenState |= PUBLISHING_FLAG;
			ls.reportLiveStatus(publishingLive, null);
		} else {
			ui.showDebugMsg("create error");
		}
	}

	private void reportLocation() {
		if (currentLocation != null) {
			ls.updateGps(currentLocation.getLat(), currentLocation.getLng());
		}
	}

	private void createVideoShareInBack() {
		// TODO if no location how to?
		publishingLive = new PublishingLive(GlobalHolder.getInstance().getCurrentUser(), 0,
				currentLocation.getLat(), currentLocation.getLng());
		vs.createConference(publishingLive, new MessageListener(h,
				CREATE_VIDEO_SHARE_CALL_BACK, null));
	}

	private void sendMessage(String text) {
		if (currentViewLive == null) {
			ui.showError(1);
			return;
		}
		// 4 for group type
		VMessage vmsg = new VMessage(4, currentViewLive.live.getLid(), GlobalHolder
				.getInstance().getCurrentUser(), new Date(
				System.currentTimeMillis()));
		new VMessageTextItem(vmsg, text);
		vs.sendMessage(vmsg);
		ui.queuedLiveMessage(text);
	}

	private void handSearchLiveCallback(SearchLiveResponse p) {
		if (p == null || p.getPacket() == null) {
			return;
		}

		List<String[]> list = p.getPacket().getVideos();
		for (String[] d : list) {
			if (TextUtils.isEmpty(d[1]) || TextUtils.isEmpty(d[5])
					|| TextUtils.isEmpty(d[0])) {
				continue;
			}
			long uid = Long.parseLong(d[1]);
			long vid = Long.parseLong(d[5]);
			long nid = Long.parseLong(d[0]);
			double lng = Double.parseDouble(d[2]);
			double lat = Double.parseDouble(d[3]);
			Live live = new Live(new User(uid), vid, lat, lng);
			live.setNid(nid);

			if (d[4] != null && !d[4].isEmpty()) {
				live.watcherCount = Integer.parseInt(d[4]);
			}
			addLiveMarker(live);
		}

	}

	private void addLiveMarker(final Live live) {
		Marker m = mapInstance.buildMarker(live);
		mapInstance.addMarker(m);
		cacheMarker.put(live, m);
		viewLiveList.add(new ViewLive(live, m));
	}

	private void addWatcherMarker(Watcher watcher) {
		mapInstance.addMarker(mapInstance.buildMarker(watcher));
	}

	
	
	private void cancelInquiry() {
		if (inquiryData.id > 0) {
			is.cancelInquiry(inquiryData.id);
			//reset inquiryId
			inquiryData.id = -1;
			
			ui.setInquiryStateToWaiting(false);
		}
		
	}
	// waiting for chair man device information
	private boolean pending = false;

	private void handWatchRequestCallback(JNIResponse resp) {
		if (resp.getResult() == JNIResponse.Result.SUCCESS) {
			setState(WATCHING_FLAG);
			RequestEnterConfResponse rer = (RequestEnterConfResponse) resp;
			if (this.currentViewLive.live.getPublisher() == null) {
				this.currentViewLive.live.setPublisher(new User(rer.getConf()
						.getChairman()));
			} else {
				this.currentViewLive.live.getPublisher().setmUserId(
						(rer.getConf().getChairman()));
			}

			Message.obtain(uiHandler, UI_HANDLE_UPDATE_VIDEO_SCREEN,
					currentViewLive.live).sendToTarget();
			pending = true;
		} else {
			pending = false;
			ui.showError(3);
		}
	}

	private void handleAttendDevice(AsyncResult ar) {

		if (pending) {
			AttendDeviceIndication adi = (AttendDeviceIndication) ar
					.getResult();
			long uid = adi.uid;
			List<UserDeviceConfig> ll = (List<UserDeviceConfig>) adi.ll;
			if (ll == null || ll.size() < 0) {
				V2Log.e("===== chair man no device" + uid);
				pending = false;
				return;
			}
			if (uid == this.currentViewLive.live.getPublisher().getmUserId()) {
				UserDeviceConfig udc = new UserDeviceConfig(0,
						this.currentViewLive.live.getLid(), currentViewLive.live.getPublisher()
								.getmUserId(), ll.get(0).getDeviceID(), vpController);
				vs.requestOpenVideoDevice(
						new ConferenceGroup(this.currentViewLive.live.getLid(), null,
								null, null, null), udc, null);
				pending = false;

			} else {
				V2Log.e("=====got remote user id : " + uid
						+ "   chair man userid:"
						+ this.currentViewLive.live.getPublisher().getmUserId());
			}

		}

	}

	private void handleWatcherListRespone(AsyncResult ar) {
		if (ar.getUserObject() != currentViewLive.live) {
			V2Log.e("==== liver changed state " + currentViewLive.live + "====> origin"
					+ ar.getUserObject());
			return;
		}
		List<Watcher> watcherList = (List<Watcher>) ar.getResult();
		for (Watcher w : watcherList) {
			addWatcherMarker(w);
		}
	}
	
	private ViewLive findViewLive(Live l) {
		for (ViewLive vl : viewLiveList) {
			if (vl.live == l) {
				return vl;
			}
		}
		return null;
	}

	public void handleRequestTimeOut() {
		unsetState(PROGRESS_DIALOG_SOWN);
		ui.showProgressDialog(false, null);
	}

	class LocalHandler extends Handler {

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
				handleCreateVideoShareBack((JNIResponse) msg.obj);
				break;
			case REPORT_LOCATION:
				reportLocation();
				break;
			case CREATE_VIDEO_SHARE:
				createVideoShareInBack();
				break;
			case SEARCH_LIVE:
				// ls.scanNear(currentMapCenter.ll.latitude,
				// currentMapCenter.ll.longitude, 500000,
				// new MessageListener(this, SEARCH_LIVE_CALLBACK, null));
				break;
			case SEARCH_LIVE_CALLBACK:
				handSearchLiveCallback((SearchLiveResponse) msg.obj);
				break;
			case WATCHING_REQUEST_CALLBACK:
				handWatchRequestCallback((JNIResponse) msg.obj);
				break;
			case ATTEND_LISTENER:
				handleAttendDevice((AsyncResult) msg.obj);
				break;
			case WATCHER_DEVICE_LISTENER:
				break;
			case WATCHER_LIST:
				handleWatcherListRespone((AsyncResult) msg.obj);
				break;
			}
		}

	}

	static class UiHandler extends Handler {

		private WeakReference<MainPresenter> pr;

		public UiHandler(MainPresenter r, MainPresenterUI i) {
			super();
			this.pr = new WeakReference<MainPresenter>(r);
		}

		@Override
		public void handleMessage(Message msg) {
			if (pr.get() == null) {
				return;
			}
			MainPresenter mp = pr.get();
			int w = msg.what;
			switch (w) {
			case UI_HANDLE_UPDATE_VIDEO_SCREEN:
				mp.updateLiveScreen((Live) msg.obj);
				break;
			case UI_HANDLE_AUDIO_CALL_TIMEOUT:
				mp.handleRequestTimeOut();
				break;
			case QUERY_MAP_LOCATION_CALL_BACK:
				mp.ui.updateMapAddressText(((AsyncResult)msg.obj).getResult().toString());
				break;
			}
		}
	}
	
	
	

}
