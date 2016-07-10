package com.v2tech.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import v2av.VideoPlayer;
import v2av.VideoPlayer.ViewItemListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
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
import com.v2tech.vo.live.LiveConnectionUser;
import com.v2tech.vo.msg.VMessage;
import com.v2tech.vo.msg.VMessageAudioVideoRequestItem;
import com.v2tech.vo.msg.VMessageTextItem;
import com.v2tech.widget.BottomButtonLayout.BottomButtonLayoutListener;
import com.v2tech.widget.InquiryBidWidget.InquiryBidWidgetListener;
import com.v2tech.widget.LiveInformationLayout.LiveInformationLayoutListener;
import com.v2tech.widget.LiverInteractionLayout.InterfactionBtnClickListener;
import com.v2tech.widget.MessageMarqueeLinearLayout.MessageMarqueeLayoutListener;
import com.v2tech.widget.P2PAudioLiverLayout.P2PAudioLiverLayoutListener;
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
		P2PVideoMainLayoutListener, 
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
	private static final int LOCAL_CAMERA_OPENING = 1 << 7;
	private static final int BOTTOM_LAYOUT_SHOW = 1 << 8;
	private static final int KEYBOARD_SHOW = 1 << 9;
	private static final int MAP_CENTER_UPDATE = 1 << 10;
	private static final int LIVER_INTERACTION_LAY_SHOW = 1 << 11;
	private static final int MESSAGE_MARQUEE_ENABLE = 1 << 12;
	private static final int AUDIO_CALL_REQUEST_SHOW = 1 << 13;
	private static final int VIDEO_CALL_REQUEST_SHOW = 1 << 14;
	private static final int AUDIO_P2P_SHOW = 1 << 16;
	private static final int PROGRESS_DIALOG_SOWN = 1 << 17;
	public static final int MESSAGE_MARQUEE_LY_SHOW = 1 << 18;
	public static final int INQUIRY_WIDGET_SHOW = 1 << 19;
	public static final int INQUIRY_BIDER_PERSONLE_WIDGET_SHOW = 1 << 20;
	
	
	private static final int B_WATCHING_FLAG = 0X0001;
	private static final int B_PREPARE_PUBLISH_FLAG = 0X0002;
	private static final int B_PUBLISHING_FLAG = 0X0004;
	private static final int B_WATCHING_AUDIO_REQUEST_FLAG = 0X0010;
	private static final int B_WATCHING_VIDEO_REQUEST_FLAG = 0X0020;
	private static final int B_PUBLISHING_AUDIO_REQUEST_FLAG = 0X0040;
	private static final int B_PUBLISHING_VIDEO_REQUEST_FLAG = 0X0080;
	private static final int B_WATCHING_AUDIO_CONNECTED_FLAG = 0X0100;
	private static final int B_WATCHING_VIDEO_CONNECTED_FLAG = 0X0200;
	private static final int B_PUBLISHING_AUDIO_CONNECTED_FLAG = 0X0400;
	private static final int B_PUBLISHING_VIDEO_CONNECTED_FLAG = 0X0800;
	

	private Context context;
	private MainPresenterUI ui;
	private UserService us;
	private ConferenceService vs;
	private LiveService ls;
	private Handler h;
	private InquiryService is;

	//for UI window state
	private int videoScreenState;
	//for  business state
	private int bState;


	// ///////////////////////////////
	private MapAPI mapInstance;

	private MapLocation currentLocation;
	
	private Handler uiHandler;

	// ///////////////////////////////////////
	private Map<Live, Marker> cacheMarker;
	
	
	private VideoPlayer  vpController;
	private VideoPlayer  vpP2pController;
	
	private List<ViewLive> viewLiveList;
	private List<LiveConnectionUser>  liveConnectionUserList;
	
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
		
		liveConnectionUserList =  new ArrayList<LiveConnectionUser>(5);
	}

	public void videoShareButtonClicked() {
		if (isBState(B_PUBLISHING_FLAG)) {
			unsetBState(B_PUBLISHING_FLAG);
			setBState(B_PREPARE_PUBLISH_FLAG);
			ui.updateVideShareButtonText(false);
			vs.quitConference(publishingLive, new MessageListener(h,
					CANCEL_PUBLISHING_REQUEST_CALLBACK, null));
		} else {
			setBState(B_PUBLISHING_FLAG);
			unsetBState(B_PREPARE_PUBLISH_FLAG);
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
			ui.showConnectRequestLayout(false, null);
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
		
		if (vpP2pController == null) {
			vpP2pController = ui.getP2PVideoPlayer();
			vpP2pController.setItemListener(this);
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
		if (isBState(B_PUBLISHING_FLAG) || isBState(B_PREPARE_PUBLISH_FLAG)) {
			throw new RuntimeException("ilegal state: "+ bState);
		}
		if (isBState(B_WATCHING_FLAG)) {
			// quit from old
			currentViewLive.playing = false;
			currentViewLive.showing = false;
			currentViewLive.surfaveViewIdx = -1;
			
			UserDeviceConfig duc = new UserDeviceConfig(4,
					currentViewLive.live.getLid(), GlobalHolder.getInstance()
							.getCurrentUserId(), "", null);
			vs.requestCloseVideoDevice(duc, null);
			vs.requestExitConference(currentViewLive.live, null);
			unsetBState(B_WATCHING_FLAG);
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
			setBState(B_WATCHING_FLAG);
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
	public void onRequestConnectLeftBtnClicked(View v, Object widgetTag) {
		if (!isState(AUDIO_CALL_REQUEST_SHOW)
				&& !isState(VIDEO_CALL_REQUEST_SHOW)) {
			return;
		}
		ui.showConnectRequestLayout(false, null);

		unsetState(AUDIO_CALL_REQUEST_SHOW);
		unsetState(VIDEO_CALL_REQUEST_SHOW);
	}


	@Override
	public void onRequestConnectRightBtnClicked(View v, Object widgetTag) {
		if (!isBState(B_PUBLISHING_VIDEO_REQUEST_FLAG)
				&& !isBState(B_PUBLISHING_AUDIO_REQUEST_FLAG)) {
			return;
		}
		
		LiveConnectionUser lcu = (LiveConnectionUser) widgetTag;
		ui.showConnectRequestLayout(false, null);
		if (isBState(B_PUBLISHING_VIDEO_REQUEST_FLAG)) {
			setBState(B_PUBLISHING_VIDEO_CONNECTED_FLAG);
			unsetBState(B_PUBLISHING_VIDEO_REQUEST_FLAG);
			unsetBState(B_PUBLISHING_AUDIO_REQUEST_FLAG);
			ui.showP2PVideoLayout(true);
			lcu.udc.setVp(vpP2pController);
			lcu.index = 0;
			lcu.showing = true;
			vs.requestOpenVideoDevice(publishingLive.group, lcu.udc, null);

			this.requestConnection(this.publishingLive.getLid(),
					VMessageAudioVideoRequestItem.TYPE_VIDEO,
					VMessageAudioVideoRequestItem.ACTION_ACCEPT);

		} else if (isBState(B_PUBLISHING_AUDIO_REQUEST_FLAG)) {
			setBState(B_PUBLISHING_AUDIO_CONNECTED_FLAG);
			unsetBState(B_PUBLISHING_AUDIO_REQUEST_FLAG);
			unsetBState(B_PUBLISHING_VIDEO_REQUEST_FLAG);
			ui.showMap(true);
			this.requestConnection(this.publishingLive.getLid(),
					VMessageAudioVideoRequestItem.TYPE_AUDIO,
					VMessageAudioVideoRequestItem.ACTION_ACCEPT);
		}
		
	}

	// ///////////RequestConnectLayoutListener

	// ///////////InterfactionBtnClickListener

	@Override
	public void onChattingBtnClicked(View v) {
		if (!isBState(B_WATCHING_FLAG)) {
			throw new RuntimeException("ilegal state: "+ bState);
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
		setBState(B_WATCHING_AUDIO_REQUEST_FLAG);
	}

	@Override
	public void onVideoCallBtnClicked(View v) {
		if (!isBState(B_WATCHING_FLAG)) {
			throw new RuntimeException("ilegal state: "+ bState);
		}
		requestConnection(this.currentViewLive.live.getLid(),
				VMessageAudioVideoRequestItem.TYPE_VIDEO,
				VMessageAudioVideoRequestItem.ACTION_REQUEST);
		setBState(B_WATCHING_VIDEO_REQUEST_FLAG);
	}

	@Override
	public void onMsgBtnClicked(View v) {
		if (!isBState(B_WATCHING_FLAG)) {
			throw new RuntimeException("ilegal state: "+ bState);
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
		if (!isBState(B_PUBLISHING_VIDEO_CONNECTED_FLAG) && !isBState(B_WATCHING_VIDEO_CONNECTED_FLAG)) {
			throw new RuntimeException("ilegal state: "+ bState);
		}
		
		// If liver is not self, than close local camera device

		int type = VMessageAudioVideoRequestItem.TYPE_AUDIO;
		if (isBState(B_WATCHING_VIDEO_CONNECTED_FLAG)) {
			type = VMessageAudioVideoRequestItem.TYPE_VIDEO;
		} else if (isState(AUDIO_P2P_SHOW)) {
			type = VMessageAudioVideoRequestItem.TYPE_AUDIO;
		}
		
		if (isBState(B_PUBLISHING_FLAG)) {
			ui.showP2PVideoLayout(false);
			requestConnection(this.publishingLive.getLid(), type,
					VMessageAudioVideoRequestItem.ACTION_HANG_OFF);
			//close all remote device
			int len = liveConnectionUserList.size();
			for(int i =0; i < len; i++) {
				LiveConnectionUser lcu = liveConnectionUserList.get(i);
				vs.requestCloseVideoDevice(lcu.udc, null);
			}
			
			//clear connection state
			unsetBState(B_PUBLISHING_VIDEO_CONNECTED_FLAG);
			unsetBState(B_PUBLISHING_VIDEO_REQUEST_FLAG);
			
		} else if(isState(B_WATCHING_FLAG)) {
			ui.showWatcherP2PVideoLayout(false);
			requestConnection(this.currentViewLive.live.getLid(), type,
					VMessageAudioVideoRequestItem.ACTION_HANG_OFF);
			//close local camera
			UserDeviceConfig duc = new UserDeviceConfig(4,
					this.currentViewLive.live.getLid(), GlobalHolder.getInstance()
							.getCurrentUserId(), "", null);
			vs.requestCloseVideoDevice(duc, null);
			//clear connection state
			unsetBState(B_WATCHING_VIDEO_CONNECTED_FLAG);
		}
		liveConnectionUserList.clear();

	}

	@Override
	public void onP2PVideoMainRightBtnClicked(View v) {
		if (!isBState(B_WATCHING_VIDEO_CONNECTED_FLAG)) {
			throw new RuntimeException("ilegal state: "+ bState);
		}
		
		UserDeviceConfig duc = new UserDeviceConfig(0, 0, GlobalHolder
				.getInstance().getCurrentUserId(), "", null);
		vs.switchCamera(duc);
		
	}

	// ///////////P2PVideoMainLayoutListener


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

	@Override
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
	
	@Override
	public void onWechatShareBtnClicked(View v) {
		this.onVideoMessage(12, 123, VMessageAudioVideoRequestItem.ACTION_REQUEST);
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
			LiveConnectionUser lcu = addLiveConnctionUser(uid);
			this.videoScreenState |= AUDIO_CALL_REQUEST_SHOW;
			ui.updateConnectLayoutBtnType(AUDIO_CALL_REQUEST_SHOW);
			ui.showConnectRequestLayout(true, lcu);
			addLiveConnctionUser(uid);
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_ACCEPT) {
			uiHandler.removeMessages(UI_HANDLE_AUDIO_CALL_TIMEOUT);
			ui.showProgressDialog(false, null);
			ui.showWatcherP2PAudioLayout(true);
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_HANG_OFF) {
			ui.showWatcherP2PAudioLayout(false);
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_DECLINE) {

		}

	}

	public void onVideoMessage(long liveId, long uid, int opt) {
		if (isState(AUDIO_CALL_REQUEST_SHOW)) {
			return;
		}
		if (opt == VMessageAudioVideoRequestItem.ACTION_REQUEST) {
			if (isBState(B_PUBLISHING_FLAG) && liveId == this.publishingLive.getLid()) {
				LiveConnectionUser lcu = addLiveConnctionUser(uid);
				if (isBState(B_PUBLISHING_VIDEO_CONNECTED_FLAG)) {
					int idx = vpP2pController.getCurrentItemIdx();
					for(int i = 0; i < liveConnectionUserList.size(); i++) {
						LiveConnectionUser tmp = liveConnectionUserList.get(i);
						if (tmp.showing == true || tmp.index == idx) {
							vs.requestCloseVideoDevice(publishingLive.group, tmp.udc, null);
							tmp.showing = false;
						}
					}
					
					lcu.udc.setVp(vpP2pController);
					lcu.showing = true;
					vpP2pController.setItemIndex(lcu.index);
					vs.requestOpenVideoDevice(publishingLive.group, lcu.udc, null);
	
					this.requestConnection(this.publishingLive.getLid(),
							VMessageAudioVideoRequestItem.TYPE_VIDEO,
							VMessageAudioVideoRequestItem.ACTION_ACCEPT);
					
				} else {
					setBState(B_PUBLISHING_VIDEO_REQUEST_FLAG);
					ui.updateConnectLayoutBtnType(VIDEO_CALL_REQUEST_SHOW);
					ui.showConnectRequestLayout(true, lcu);
				}
			}
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_ACCEPT) {
			if (isBState(B_WATCHING_FLAG) && isBState(B_WATCHING_VIDEO_REQUEST_FLAG)) {
				ui.updateLocalCameraType(MainPresenterUI.LOCAL_CAMERA_TYPE_P2P_CONNECTION);
				ui.showWatcherP2PVideoLayout(true);
				UserDeviceConfig duc = new UserDeviceConfig(4,
						this.currentViewLive.live.getLid(), GlobalHolder.getInstance()
								.getCurrentUserId(), "", null);
				ui.showBottomLayout(false);
				vs.requestOpenVideoDevice(new ConferenceGroup(currentViewLive.live.getLid(), null, null, null, null) , duc, null);
				
				setBState(B_WATCHING_VIDEO_CONNECTED_FLAG);
				unsetBState(B_WATCHING_VIDEO_REQUEST_FLAG);
				unsetState(LIVER_INTERACTION_LAY_SHOW);
				unsetState(BOTTOM_LAYOUT_SHOW);
				
			}
			
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_HANG_OFF) {
			
			if (isBState(B_PUBLISHING_FLAG) && isBState(B_PUBLISHING_VIDEO_CONNECTED_FLAG)) {
				
				
				LiveConnectionUser lcu = removeLiveConnectionUser(uid);
				if (lcu != null) {
					// close remote device
					UserDeviceConfig duc = new UserDeviceConfig(0,
							publishingLive.getLid(), uid, lcu.udc.getDeviceID(), null);
					vs.requestCloseVideoDevice(duc, null);
				} else {
					throw new RuntimeException("Ilegal statue uid not found:"+ uid);
				}
				//TODO if all users connection are hanged off, should hide device
			} else if (isBState(B_WATCHING_FLAG)  && isBState(B_WATCHING_VIDEO_CONNECTED_FLAG)) {
				
				UserDeviceConfig duc = new UserDeviceConfig(4,
						this.currentViewLive.live.getLid(), GlobalHolder.getInstance()
								.getCurrentUserId(), "", null);
				vs.requestCloseVideoDevice(duc, null);
				ui.showWatcherP2PVideoLayout(false);
			}
			ui.updateLocalCameraType(MainPresenterUI.LOCAL_CAMERA_TYPE_SHARE);
			
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_DECLINE) {
			if (isBState(B_WATCHING_FLAG)  && isBState(B_WATCHING_VIDEO_REQUEST_FLAG)) {
				//TODO notify user
			}
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
//		Watcher w = new Watcher(user.getmUserId());
//		if (isState(WATCHING_FLAG)) {
//			ui.addWatcher(WATCHER_FLAG_WATCHER, w);
//		} else {
//			ui.addWatcher(WATCHER_FLAG_PUBLISHER, w);
//		}
		
	}

	@Override
	public void onWatcherLeaved(Live l, User user) {
//		Watcher w = new Watcher(user.getmUserId());
//		if (isState(WATCHING_FLAG)) {
//			ui.removeWatcher(WATCHER_FLAG_WATCHER, w);
//		} else {
//			ui.removeWatcher(WATCHER_FLAG_PUBLISHER, w);
//		}
		
	}
	
	
///////////LiveWathcingHandler///////////////////////////////////////////////////
	
	
	///////////UITypeStatusChangedListener///////////////////////////////////////////////////	
	@Override
	public void onUITypeChanged(ScreenType screenType) {
		switch (screenType) {
		case INQUIRE_BIDING:
			ui.showBottomLayout(false);
			unsetState(BOTTOM_LAYOUT_SHOW);
			//Update title to back
			ui.updateTitleBarBtn(MainPresenterUI.TITLE_BAR_BTN_TYPE_BACK);
			mapInstance.getLocationName(mapInstance.getMapCenter(), new MessageListener(uiHandler, QUERY_MAP_LOCATION_CALL_BACK, null));
			setState(INQUIRY_WIDGET_SHOW);
			ui.updateInputMode(MainPresenterUI.INPUT_MODE_PAN);
			break;
		case VIDEO_MAP:
			if (isState(LOCAL_CAMERA_OPENING)) {
				UserDeviceConfig duc = new UserDeviceConfig(0, 0, GlobalHolder
						.getInstance().getCurrentUserId(), "", null);
				vs.requestCloseVideoDevice(duc, null);
				unsetState(LOCAL_CAMERA_OPENING);
						
			}
			if (isBState(B_PUBLISHING_FLAG)) {
				videoShareButtonClicked();
			}
			setState(BOTTOM_LAYOUT_SHOW);
			ui.showBottomLayout(true);
			ui.updateInputMode(MainPresenterUI.INPUT_MODE_NOTHING);
			ui.updateTitleBarBtn(MainPresenterUI.TITLE_BAR_BTN_TYPE_PERSONEL);
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
			setBState(B_PREPARE_PUBLISH_FLAG);
			this.unsetState(BOTTOM_LAYOUT_SHOW);
			break;
		case VIDEO_SHARE_CONNECTION_REQUESTING:
			setBState(B_PUBLISHING_VIDEO_REQUEST_FLAG);
			setBState(B_PUBLISHING_AUDIO_REQUEST_FLAG);
			break;
		case VIDEO_SHARE_MAP:
			this.unsetState(VIDEO_SHARE_BTN_SHOW);
			break;
		case VIDEO_SHARE_P2P_VIDEO_CONNECTION:
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
		if (!(isState(INQUIRY_WIDGET_SHOW) || isState(INQUIRY_BIDER_PERSONLE_WIDGET_SHOW))) {
			this.updateMapCenter(ml, ml.getParameter());
		}
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
	public void onCurrentItemChanged(VideoPlayer vp, int current, int newIdx) {
		if (vp == vpP2pController) {
			//DO 2P2 video change
			int len = liveConnectionUserList.size();
			for (int i = 0; i < len; i++) {
				LiveConnectionUser lcu = liveConnectionUserList.get(i);
				if (lcu.index == current) {
					V2Log.e("====  close idx:" + lcu.index);
					vs.requestCloseVideoDevice(publishingLive.group, lcu.udc, null);
				}
				if (lcu.index == newIdx) {
					V2Log.e("====  open idx:" + lcu.index);
					vs.requestOpenVideoDevice(publishingLive.group, lcu.udc, null);
				}
			}
			return;
		}
		
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
		setState(INQUIRY_BIDER_PERSONLE_WIDGET_SHOW);
		unsetState(INQUIRY_WIDGET_SHOW);
		
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
	
	private void setBState(int flag) {
		this.bState |= flag;
	}

	private void unsetBState(int flag) {
		this.bState &= (~flag);
	}

	private boolean isBState(int flag) {
		return (this.bState & flag) == flag;
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
		if (currentLocation == null) {
			//FIXME notify when locating
			return;
		}
		publishingLive = new PublishingLive(GlobalHolder.getInstance().getCurrentUser(), 0,
				currentLocation.getLat(), currentLocation.getLng());
		publishingLive.group = new ConferenceGroup(this.publishingLive.getLid(), "", null,
				null, null);
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
		if (inquiryData != null && inquiryData.id > 0) {
			is.cancelInquiry(inquiryData.id);
			//reset inquiryId
			inquiryData.id = -1;
			
			ui.setInquiryStateToWaiting(false);
		}
		unsetState(INQUIRY_WIDGET_SHOW);
	}
	// waiting for chair man device information
	private boolean pending = false;

	private void handWatchRequestCallback(JNIResponse resp) {
		if (resp.getResult() == JNIResponse.Result.SUCCESS) {
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
				vs.requestOpenVideoDevice(new ConferenceGroup(this.currentViewLive.live.getLid(), "", null,
						null, null) ,  udc, null);
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
	
	
	private LiveConnectionUser addLiveConnctionUser(long uid) {
		User u = GlobalHolder.getInstance().getUser(uid);
		if (u.ll == null || u.ll.size() <= 0) {
			return null;
		}
		UserDeviceConfig udc = u.ll.iterator().next();
		udc.setGroupID(this.publishingLive.getLid());
		udc.setGroupType(4);
		
		LiveConnectionUser lcu = new LiveConnectionUser();
		lcu.user = u;
		lcu.udc = udc;
		lcu.showing = false;
		if (liveConnectionUserList.size() > 0) {
			lcu.index = vpP2pController.appendWindow() - 1;
		} else {
			lcu.index = 0;
		}
		liveConnectionUserList.add(lcu);
		return lcu;
	}
	
	
	private LiveConnectionUser removeLiveConnectionUser(long uid) {
		int size = liveConnectionUserList.size();
		for (int i  = 0; i < size; i++) {
			if (liveConnectionUserList.get(i).user.getmUserId() == uid) {
				return liveConnectionUserList.remove(i);
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
