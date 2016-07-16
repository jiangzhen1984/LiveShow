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
	

	public MapAPI getMainMap();

	public void showTextKeyboard(boolean flag);

	public void showVideoScreentItem(int tag, boolean showFlag);

	public void showLoginUI();

	public void showPersonelUI();

	public void showSearchErrorToast();

	public String getTextString();

	public void updateVideShareButtonText(boolean publish);

	public void showError(int flag);

	public void showDebugMsg(String msg);

	public void queuedLiveMessage(CharSequence msg);
	
	public void updateRendNum(int num);

	public void showRedBtm(boolean flag);

	public void showIncharBtm(boolean flag);

	public void updateBalanceSum(final float num);

	public void showMarqueeMessage(boolean flag);

	public void closeVideo(boolean flag);

	public void doFinish();
	
	/**
	 * 
	 * @param type  {@link TITLE_BAR_BTN_TYPE_BACK}  {@link TITLE_BAR_BTN_TYPE_PERSONEL}
	 */
	public void updateTitleBarBtn(int type);

	
	public void showInquiryAcceptedMsg(String msg);

	public void showProgressDialog(boolean flag, String text);

	public void updateInterfactionFollowBtn(boolean followed);

	public MapAPI getWatcherMapInstance();
	
	public void addWatcher(int flag, Watcher watcher);
	
	public void removeWatcher(int flag, Watcher watcher);
	
	public VideoPlayer getVideoPlayer();
	
	public VideoPlayer getP2PVideoPlayer();
	
	public void cancelInquireState();
	
	public void updateMapAddressText(String text);
	
	public String getInquiryAward();
	
	public String getInquiryMessage();
	
	public void showIncorrectAwardMessage(String message);
	
	public void setInquiryStateToWaiting(boolean wait);
	
	public void updateInquiryMessage(String msg);
	
	/**
	 * {@link LOCAL_CAMERA_TYPE_SHARE}
	 * {@link LOCAL_CAMERA_TYPE_P2P_CONNECTION}
	 * @param type
	 */
	public void updateLocalCameraType(int type);
	
	public void updateInputMode(int mode);
	
	
	public void showUILayout(int type, boolean show, Object tag);
}
