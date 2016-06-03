package com.v2tech.presenter;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import v2av.VideoPlayer;
import v2av.VideoRecorder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.V2.jni.ind.MessageInd;
import com.V2.jni.util.V2Log;
import com.v2tech.map.LocationParameter;
import com.v2tech.map.MapAPI;
import com.v2tech.map.MapLocation;
import com.v2tech.map.Marker;
import com.v2tech.map.MarkerListener;
import com.v2tech.service.AsyncResult;
import com.v2tech.service.ConferenceService;
import com.v2tech.service.GlobalHolder;
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
import com.v2tech.view.MapVideoLayout;
import com.v2tech.view.P2PMessageActivity;
import com.v2tech.vo.AttendDeviceIndication;
import com.v2tech.vo.Live;
import com.v2tech.vo.User;
import com.v2tech.vo.UserDeviceConfig;
import com.v2tech.vo.Watcher;
import com.v2tech.vo.conference.ConferenceGroup;
import com.v2tech.vo.msg.VMessage;
import com.v2tech.vo.msg.VMessageAudioVideoRequestItem;
import com.v2tech.vo.msg.VMessageTextItem;
import com.v2tech.widget.BottomButtonLayout.BottomButtonLayoutListener;
import com.v2tech.widget.LiveInformationLayout.LiveInformationLayoutListener;
import com.v2tech.widget.LiverInteractionLayout.InterfactionBtnClickListener;
import com.v2tech.widget.MessageMarqueeLinearLayout.MessageMarqueeLayoutListener;
import com.v2tech.widget.P2PAudioLiverLayout.P2PAudioLiverLayoutListener;
import com.v2tech.widget.P2PAudioWatcherLayout.P2PAudioWatcherLayoutListener;
import com.v2tech.widget.P2PVideoMainLayout.P2PVideoMainLayoutListener;
import com.v2tech.widget.RequestConnectLayout.RequestConnectLayoutListener;
import com.v2tech.widget.VideoShareBtnLayout.VideoShareBtnLayoutListener;
import com.v2tech.widget.VideoShowFragment;
import com.v2tech.widget.VideoWatcherListLayout.VideoWatcherListLayoutListener;

public class MainPresenter extends BasePresenter implements

MapVideoLayout.LayoutPositionChangedListener, MarkerListener,
		BottomButtonLayoutListener,
		MapVideoLayout.OnVideoFragmentChangedListener,
		LiveInformationLayoutListener, RequestConnectLayoutListener,
		InterfactionBtnClickListener, VideoWatcherListLayoutListener,
		P2PVideoMainLayoutListener, P2PAudioWatcherLayoutListener,
		P2PAudioLiverLayoutListener, VideoShareBtnLayoutListener,
		MessageMarqueeLayoutListener, LiveStatusHandler, LiveMessageHandler,
		LiveWathcingHandler {

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

	private Context context;
	private MainPresenterUI ui;
	private UserService us;
	private ConferenceService vs;
	private LiveService ls;
	private Handler h;
	private Live currentLive;

	private int videoScreenState;
	private boolean cameraSurfaceViewMeasure = false;

	private MapLocation currentLocation;

	// ///////////////////////////////
	private MapAPI mapInstance;

	private Handler uiHandler;

	// ///////////////////////////////////////
	private Map<Live, Marker> cacheMarker;

	// /////

	public MainPresenter(Context context, MainPresenterUI ui) {
		super();
		this.ui = ui;
		this.context = context;
		videoScreenState = (VIDEO_SCREEN_BTN_FLAG | VIDEO_BOTTOM_LY_FLAG
				| FOLLOW_COUNT_SHOW_FLAG | BOTTOM_LAYOUT_SHOW
				| MESSAGE_MARQUEE_ENABLE | VIDEO_SHARE_BTN_SHOW | MESSAGE_MARQUEE_LY_SHOW);

		uiHandler = new UiHandler(this, ui);
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

		public void videoShareLayoutFlyout();

		public SurfaceView getCameraSurfaceView();

		public SurfaceView getCurrentSurface();

		public SurfaceView getP2PMainSurface();

		public SurfaceView getP2PMainWatherSurface();

		public void showBottomLayout(boolean flag);

		public void resizeCameraSurfaceSize();

		public void showError(int flag);

		public void showDebugMsg(String msg);

		public void queuedWatchingMessage(CharSequence msg);
		
		public void queuedPublisingMessage(CharSequence msg);

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

		// 1 for audio 2 for video
		public void updateConnectLayoutBtnType(int type);

		public void showP2PVideoLayout(boolean flag);

		public void showWatcherP2PVideoLayout(boolean flag);

		public void showWatcherP2PAudioLayout(boolean flag);

		public void showProgressDialog(boolean flag, String text);

		public void showP2PLiverLayout(boolean flag);

		public void showVideoshareBtnLayout(boolean flag);

		public void mapVideoLayoutFlyingIn();

		public void updateVideoLayoutOffset(int x);

		public void updateInterfactionFollowBtn(boolean followed);

		public MapAPI getWatcherMapInstance();
		
		public void addWatcher(int flag, Watcher watcher);
		
		public void removeWatcher(int flag, Watcher watcher);
	}

	public void videoShareButtonClicked() {
		if (isState(PUBLISHING_FLAG)) {
			unsetState(PUBLISHING_FLAG);
			ui.updateVideShareButtonText(false);
			ui.videoShareLayoutFlyout();
			vs.quitConference(currentLive, new MessageListener(h,
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
		startLocationScan();
	}

	@Override
	public void onUIStopped() {
		stopLocate();
	}

	@Override
	public void onUIDestroyed() {
		us.clearCalledBack();
		vs.clearCalledBack();
		ls.clearCalledBack();
		this.h.removeMessages(REPORT_LOCATION);
		cacheMarker.clear();
		super.onUIDestroyed();
	}

	// /////////////////////////////////////////////////////////////////////////////////////////

	private void searchMap(String text) {
		mapInstance.animationSearch(text);
	}

	private void startLocationScan() {
		if (mapInstance == null) {
			h.postDelayed(new Runnable() {

				@Override
				public void run() {
					mapInstance.startLocate(mapInstance.buildParameter(context));

				}

			}, 500);
			return;
		} else {
			mapInstance.startLocate(mapInstance.buildParameter(context));
		}

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
			// TODO check window count

			// quit from old
			vs.requestExitConference(currentLive, null);
			unsetState(WATCHING_FLAG);
		}

		// join new one
		Live l = (Live) m.getLive();
		if (l != null) {
			vs.requestEnterConference(l, new MessageListener(h,
					WATCHING_REQUEST_CALLBACK, null));
			currentLive = l;
			ui.showDebugMsg(currentLive.getLid() + "");
			updateLiveScreen(l);
		}
		return true;
	}

	@Override
	public void onLocated(MapLocation lo) {
		this.currentLocation = lo;
		this.updateMapCenter(lo, lo.getParameter());
		reportLocation();
	}

	// /////////////MarkerListener

	// //////////////////////////////////LayoutPositionChangedListener////////////////////////

	@Override
	public void onPreparedFlyingIn() {
	}

	@Override
	public void onFlyingIn() {
		if (isState(LOCAL_CAMERA_OPENING)) {
			this.unsetState(LOCAL_CAMERA_OPENING);
			UserDeviceConfig duc = new UserDeviceConfig(0, 0, GlobalHolder
					.getInstance().getCurrentUserId(), "", null);
			duc.setSVHolder(ui.getCameraSurfaceView());
			vs.requestCloseVideoDevice(duc, null);

		}

		if (isState(BOTTOM_LAYOUT_SHOW)) {
			ui.showBottomLayout(true);
			this.setState(BOTTOM_LAYOUT_SHOW);
		}
		this.setState(LIVER_SHOW_FLAG);
	}

	@Override
	public void onPreparedFlyingOut() {
	}

	@Override
	public void onFlyingOut() {
		this.unsetState(LIVER_SHOW_FLAG);
	}

	@Override
	public void onDrag() {
		if (!cameraSurfaceViewMeasure) {
			cameraSurfaceViewMeasure = true;
			ui.resizeCameraSurfaceSize();
		}
		if (!isState(LOCAL_CAMERA_OPENING)) {

			UserDeviceConfig duc = new UserDeviceConfig(0, 0, GlobalHolder
					.getInstance().getCurrentUserId(), "", null);
			duc.setSVHolder(ui.getCameraSurfaceView());
			VideoRecorder.VideoPreviewSurfaceHolder = ui.getCameraSurfaceView()
					.getHolder();
			vs.requestOpenVideoDevice(duc, null);
			ui.showBottomLayout(false);
			this.setState(LOCAL_CAMERA_OPENING);
			this.unsetState(BOTTOM_LAYOUT_SHOW);
		}

	}

	@Override
	public void onVideoScreenClick() {
		if (isState(VIDEO_SCREEN_BTN_FLAG)) {
			unsetState(VIDEO_SCREEN_BTN_FLAG);
		} else {
			setState(VIDEO_SCREEN_BTN_FLAG);
		}
		if (isState(VIDEO_BOTTOM_LY_FLAG)) {
			unsetState(VIDEO_BOTTOM_LY_FLAG);
		} else {
			setState(VIDEO_BOTTOM_LY_FLAG);
		}
		if (isState(FOLLOW_COUNT_SHOW_FLAG)) {
			unsetState(FOLLOW_COUNT_SHOW_FLAG);
		} else {
			setState(FOLLOW_COUNT_SHOW_FLAG);
		}

		if (isState(LIVER_SHOW_FLAG)) {
			unsetState(LIVER_SHOW_FLAG);
		} else {
			setState(LIVER_SHOW_FLAG);
		}
		if (isState(MESSAGE_MARQUEE_LY_SHOW)) {
			unsetState(MESSAGE_MARQUEE_LY_SHOW);
		} else {
			setState(MESSAGE_MARQUEE_LY_SHOW);
		}
		ui.showVideoScreentItem(VIDEO_SCREEN_BTN_FLAG,
				isState(VIDEO_SCREEN_BTN_FLAG));
		ui.showVideoScreentItem(VIDEO_BOTTOM_LY_FLAG,
				isState(VIDEO_BOTTOM_LY_FLAG));
		ui.showVideoScreentItem(FOLLOW_COUNT_SHOW_FLAG,
				isState(FOLLOW_COUNT_SHOW_FLAG));
		ui.showVideoScreentItem(LIVER_SHOW_FLAG, isState(LIVER_SHOW_FLAG));
		ui.showVideoScreentItem(MESSAGE_MARQUEE_LY_SHOW,
				isState(MESSAGE_MARQUEE_LY_SHOW));

	}

	// ////////////////////////////////LayoutPositionChangedListener/////////////////

	// //////////////VideoWatcherListLayoutListener

	@Override
	public void onCloseBtnClicked(View v) {
		ui.closeVideo(true);
		// FIXME close video btn
	}

	@Override
	public void onLiveInfoTipsBtnClicked(View v) {
		if (currentLive == null) {
			return;
		}

		// TODO add tips call
		currentLive.isInchr = !currentLive.isInchr;
		updateLiveScreen(currentLive);
	}

	@Override
	public void onLiveInfoRecommandBtnClicked(View v) {
		if (currentLive == null) {
			// TODO show incorrect UI
			return;
		}
		ls.recommend(currentLive, currentLive.isRend());
		currentLive.setRend(!currentLive.isRend());
		currentLive.rendCount += (currentLive.isRend() ? 1 : -1);
		updateLiveScreen(currentLive);
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

	// ///////////OnVideoFragmentChangedListener

	@Override
	public void onChanged(VideoShowFragment videoFrag) {
		if (currentLive != null) {
			vs.requestExitConference(currentLive, null);
			currentLive = null;
		}

		unsetState(WATCHING_FLAG);
		// TODO reset status

		// TODO update show new live
		Live l = (Live) videoFrag.getTag1();
		if (l == null) {
			// TODO request new one

			// TODO update tag;

			// TODO update screen
		} else {
			// join new one
			vs.requestEnterConference(l, new MessageListener(h,
					WATCHING_REQUEST_CALLBACK, null));
			currentLive = l;
			updateLiveScreen(l);
		}

	}

	// ///////////OnVideoFragmentChangedListener

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
			udc.setGroupID(this.currentLive.getLid());
			udc.setGroupType(4);
			VideoPlayer vp = new VideoPlayer();
			vp.SetSurface(ui.getP2PMainSurface().getHolder());
			udc.setVp(vp);
			vs.requestOpenVideoDevice(
					new ConferenceGroup(this.currentLive.getLid(), "", null,
							null, null), udc, null);

			this.requestConnection(this.currentLive.getLid(),
					VMessageAudioVideoRequestItem.TYPE_VIDEO,
					VMessageAudioVideoRequestItem.ACTION_ACCEPT);

		} else if (isState(AUDIO_CALL_REQUEST_SHOW)) {
			setState(AUDIO_P2P_SHOW);
			unsetState(AUDIO_CALL_REQUEST_SHOW);
			ui.showP2PLiverLayout(true);
			this.requestConnection(this.currentLive.getLid(),
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

		requestConnection(this.currentLive.getLid(),
				VMessageAudioVideoRequestItem.TYPE_AUDIO,
				VMessageAudioVideoRequestItem.ACTION_REQUEST);
	}

	@Override
	public void onVideoCallBtnClicked(View v) {
		if (!isState(WATCHING_FLAG)) {
			// TODO show incorrect UI
			return;
		}
		requestConnection(this.currentLive.getLid(),
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
		i.putExtra("chatuserid", this.currentLive.getPublisher().getmUserId());
		i.setClass(context, P2PMessageActivity.class);
		context.startActivity(i);
	}

	@Override
	public void onFollowBtnClick(View v) {
		if (currentLive == null) {
			return;
		}
		// TODO check friend status
		us.followUser(currentLive.getPublisher(), true);
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
		requestConnection(this.currentLive.getLid(), type,
				VMessageAudioVideoRequestItem.ACTION_HANG_OFF);
		if (currentLive.getPublisher().getmUserId() != GlobalHolder
				.getInstance().getCurrentUserId()) {

			UserDeviceConfig duc = new UserDeviceConfig(4,
					this.currentLive.getLid(), GlobalHolder.getInstance()
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
		ui.showP2PLiverLayout(false);
		this.requestConnection(this.currentLive.getLid(),
				VMessageAudioVideoRequestItem.TYPE_AUDIO,
				VMessageAudioVideoRequestItem.ACTION_HANG_OFF);
	}

	public void onMapReturnBtn(View view) {
		if (!isState(VIDEO_SHARE_BTN_SHOW)) {
			ui.showVideoshareBtnLayout(true);
			setState(VIDEO_SHARE_BTN_SHOW);
			// show map view
			ui.showP2PLiverLayout(false);
		}
	}

	// ///////////P2PAudioLiverLayoutListener///////////////////////////////////////////////////

	// ///////////VideoShareBtnLayoutListener///////////////////////////////////////////////////

	@Override
	public void onVideoSharedBtnClicked(View v) {
		videoShareButtonClicked();
	}

	public void requestStartDrag() {

	}

	public void requestUpdateOffset(int dy) {
		ui.updateVideoLayoutOffset(dy);
	}

	public void requestFlyingOut() {
		ui.mapVideoLayoutFlyingIn();
	}

	public void onMapShareBtnClicked(View v) {
		if (isState(VIDEO_SHARE_BTN_SHOW)) {
			ui.showVideoshareBtnLayout(false);
			unsetState(VIDEO_SHARE_BTN_SHOW);
			// show map view
			ui.showP2PLiverLayout(true);

			// TODO add watcher list marker to map
			if (currentLive != null) {
				ls.getWatcherList(currentLive, new MessageListener(h,
						WATCHER_LIST, currentLive));
			}
		}
	}

	// ///////////VideoShareBtnLayoutListener///////////////////////////////////////////////////

	// ///////////LiveStatusHandler///////////////////////////////////////////////////

	public void handleNewLivePushlishment(Live l) {
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
					this.currentLive.getLid(), GlobalHolder.getInstance()
							.getCurrentUserId(), "", null);
			ui.getP2PMainWatherSurface().setZOrderMediaOverlay(true);
			VideoRecorder.VideoPreviewSurfaceHolder = ui
					.getP2PMainWatherSurface().getHolder();
			VideoRecorder.VideoPreviewSurfaceHolder
					.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			ui.showBottomLayout(false);
			unsetState(BOTTOM_LAYOUT_SHOW);
			vs.requestOpenVideoDevice(duc, null);
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_HANG_OFF) {
			ui.showWatcherP2PVideoLayout(false);
			if (uid != currentLive.getPublisher().getmUserId()) {
				// close local device
				UserDeviceConfig duc = new UserDeviceConfig(4,
						this.currentLive.getLid(), GlobalHolder.getInstance()
								.getCurrentUserId(), "", null);
				vs.requestCloseVideoDevice(duc, null);
			} else {
				// close remote device
				VideoPlayer vp = new VideoPlayer();
				vp.SetSurface(ui.getP2PMainSurface().getHolder());
				UserDeviceConfig duc = new UserDeviceConfig(0,
						this.currentLive.getLid(), uid, GlobalHolder
								.getInstance().getUser(uid).ll.iterator()
								.next().getDeviceID(), vp);
				vs.requestCloseVideoDevice(duc, null);

			}
		} else if (opt == VMessageAudioVideoRequestItem.ACTION_DECLINE) {

		}
	}

	public void onLiveMessage(long liveId, long uid, VMessage vm) {
		if (isState(WATCHING_FLAG) ) {
			ui.queuedWatchingMessage(MessageUtil.buildContent(context, vm));
			return;
		} else if (isState(PUBLISHING_FLAG)) {
			ui.queuedPublisingMessage(MessageUtil.buildContent(context, vm));
		}
		
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

		mapInstance = ui.getMainMap();
		mapInstance.registerMakerListener(this);

	}

	private void doRecommendationInBack() {

	}

	private void handleCreateVideoShareBack(JNIResponse resp) {
		V2Log.e("==> CREATE VIDEO SHARE:" + resp.getResult());
		if (resp.getResult() == JNIResponse.Result.SUCCESS) {
			RequestConfCreateResponse rcr = (RequestConfCreateResponse) resp;
			currentLive.setLid(rcr.getConfId());
			ui.showDebugMsg(this.currentLive.getLid() + "");
			videoScreenState |= PUBLISHING_FLAG;
			ls.reportLiveStatus(this.currentLive, null);
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
		currentLive = new Live(GlobalHolder.getInstance().getCurrentUser(), 0,
				currentLocation.getLat(), currentLocation.getLng());
		vs.createConference(currentLive, new MessageListener(h,
				CREATE_VIDEO_SHARE_CALL_BACK, null));
	}

	private void sendMessage(String text) {
		if (currentLive == null) {
			ui.showError(1);
			return;
		}
		// 4 for group type
		VMessage vmsg = new VMessage(4, currentLive.getLid(), GlobalHolder
				.getInstance().getCurrentUser(), new Date(
				System.currentTimeMillis()));
		new VMessageTextItem(vmsg, text);
		vs.sendMessage(vmsg);
		ui.queuedWatchingMessage(text);
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

		this.uiHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				handleLiveFinished(live);

			}

		}, 2000);
	}

	private void addWatcherMarker(Watcher watcher) {
		mapInstance.addMarker(mapInstance.buildMarker(watcher));
	}

	// waiting for chair man device information
	private boolean pending = true;

	private void handWatchRequestCallback(JNIResponse resp) {
		if (resp.getResult() == JNIResponse.Result.SUCCESS) {
			setState(WATCHING_FLAG);
			RequestEnterConfResponse rer = (RequestEnterConfResponse) resp;
			if (this.currentLive.getPublisher() == null) {
				this.currentLive.setPublisher(new User(rer.getConf()
						.getChairman()));
			} else {
				this.currentLive.getPublisher().setmUserId(
						(rer.getConf().getChairman()));
			}

			Message.obtain(uiHandler, UI_HANDLE_UPDATE_VIDEO_SCREEN,
					currentLive).sendToTarget();
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
				V2Log.e("=====got remote user id : " + uid
						+ "   chair man userid:"
						+ this.currentLive.getPublisher().getmUserId());
			}

		}

	}

	private void handleWatcherListRespone(AsyncResult ar) {
		if (ar.getUserObject() != currentLive) {
			V2Log.e("==== liver changed state " + currentLive + "====> origin"
					+ ar.getUserObject());
			return;
		}
		List<Watcher> watcherList = (List<Watcher>) ar.getResult();
		for (Watcher w : watcherList) {
			addWatcherMarker(w);
		}
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
			}
		}
	}

}
