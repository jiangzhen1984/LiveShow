package com.v2tech.presenter;

import v2av.VideoPlayer;

import com.v2tech.map.MapAPI;
import com.v2tech.vo.Watcher;

public interface MainPresenterUI {
	
	
	
	
	public static final int TITLE_BAR_BTN_TYPE_BACK = 1;
	public static final int TITLE_BAR_BTN_TYPE_PERSONEL = 2;
	
	public static final int LOCAL_CAMERA_TYPE_SHARE = 1;
	public static final int LOCAL_CAMERA_TYPE_P2P_CONNECTION = 2;
	
	public static final int INPUT_MODE_NOTHING = 1;
	public static final int INPUT_MODE_PAN = 2;
	
	
	
	public static final int UI_LAYOUT_TYPE_MAP = 1;
	public static final int UI_LAYOUT_TYPE_P2P_AUDIO_REQUEST = 2;
	public static final int UI_LAYOUT_TYPE_P2P_VIDEO_REQUEST = 3;
	public static final int UI_LAYOUT_TYPE_P2P_AUDIO_CONNECTION_PUBLISHER = 4;
	public static final int UI_LAYOUT_TYPE_P2P_AUDIO_CONNECTION_WATCHER = 5;
	public static final int UI_LAYOUT_TYPE_P2P_VIDEO_CONNECTION_PUBLISHER = 6;
	public static final int UI_LAYOUT_TYPE_P2P_VIDEO_CONNECTION_WATCHER = 7;
	public static final int UI_LAYOUT_TYPE_LIVE_PUBLISHER_PERSONEL = 8;
	public static final int UI_LAYOUT_TYPE_BOTTOM_BTN = 9;
	public static final int UI_LAYOUT_TYPE_BID_PERSON_INFO = 10;
	public static final int UI_LAYOUT_TYPE_VIDEO_LOCK_SETTING_DIALOG = 11;
	public static final int UI_LAYOUT_TYPE_VIDEO_UNLOCK_SETTING_DIALOG = 12;



	public static final int REQUEST_UI_LAYOUT_VIDEO_WACHING = 1;
	public static final int REQUEST_UI_LAYOUT_VIDEO_SHARE = 2;


	MapAPI getMainMap();

	void showTextKeyboard(boolean flag);

	void showVideoScreentItem(int tag, boolean showFlag);

	void showLoginUI();

	void showPersonelUI();

	void showSearchErrorToast();

	String getTextString();

	void updateVideShareButtonText(boolean publish);

	void showError(int flag);

	void showDebugMsg(String msg);

	void queuedLiveMessage(CharSequence msg);
	
	void updateRendNum(int num);

	void updateBalanceSum(final float num);

	void showMarqueeMessage(boolean flag);

	void doFinish();

	/**
	 * @param type {@link MainPresenterUI#TITLE_BAR_BTN_TYPE_BACK}  {@link MainPresenterUI#TITLE_BAR_BTN_TYPE_PERSONEL}
	 */
	void updateTitleBarBtn(int type);

	
	void showInquiryAcceptedMsg(String msg);

	void showProgressDialog(boolean flag, String text);

	void updateInterfactionFollowBtn(boolean followed);

	MapAPI getWatcherMapInstance();
	
	void addWatcher(int flag, Watcher watcher);
	
	void removeWatcher(int flag, Watcher watcher);
	
	VideoPlayer getVideoPlayer();
	
	VideoPlayer getP2PVideoPlayer();
	
	void cancelInquireState();
	
	void updateMapAddressText(String text);
	
	String getInquiryAward();
	
	String getInquiryMessage();
	
	void showIncorrectAwardMessage(String message);
	
	void setInquiryStateToWaiting(boolean wait);
	
	void updateInquiryMessage(String msg);
	
	/**
	 * {@link MainPresenterUI#LOCAL_CAMERA_TYPE_SHARE}
	 * {@link MainPresenterUI#LOCAL_CAMERA_TYPE_P2P_CONNECTION}
	 * @param type
	 */
	void updateLocalCameraType(int type);
	
	void updateInputMode(int mode);
	
	
	void showUILayout(int type, boolean show, Object tag);


	/**
	 *
	 * @param requestType
     */
	void updateUILayout(int requestType);
}
