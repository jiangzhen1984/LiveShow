package com.v2tech.presenter;

import com.v2tech.service.MessageListener;
import com.v2tech.util.SPUtil;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class MainPresenter {
	
	private static final int RECOMMENDATION_BUTTON_SHOW_FLAG = 1;
	private static final int RECOMMENDATION_COUNT_SHOW_FLAG = 1 << 1;
	private static final int FOLLOW_BUTTON_SHOW_FLAG = 1 << 2;
	private static final int FOLLOW_COUNT_SHOW_FLAG = 1 << 3;
	private static final int LIVER_SHOW_FLAG = 1 << 4;
	
	
	private Context context;
	
	private MainPresenterUI ui;
	
	
	private int videoScreenState; 
	private boolean keyboardState = false;
	private boolean loginState = false;
	
	private double lat;
	private double lng;
	
	
	public MainPresenter(Context context, MainPresenterUI ui) {
		super();
		this.ui = ui;
		this.context = context;
		videoScreenState = (RECOMMENDATION_BUTTON_SHOW_FLAG
				| RECOMMENDATION_COUNT_SHOW_FLAG | FOLLOW_BUTTON_SHOW_FLAG
				| FOLLOW_COUNT_SHOW_FLAG | LIVER_SHOW_FLAG);
	}

	public interface MainPresenterUI {
		
		public void resetUIDisplayOrder();
		
		public void showTextKeyboard(boolean flag);
		
		public void showVideoScreentItem(int tag, boolean showFlag);
		
		public void resetMapCenter(double lat, double lng, int zoom);
		
	}
	
	public void uicreated() {
		
		TelephonyManager tl = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		
		String phone = SPUtil.getConfigStrValue(context, "cellphone");
		String code = SPUtil.getConfigStrValue(context, "code");
		//TODO login 
		
	}
	
	public void mapLocationButtonClicked() {
		
	}
	
	
	public void sendMessageButtonClicked() {
		
	}
	
	public void mapMarkerClicked() {
		
	}
	
	public void mapSearchButtonClicked() {
		
	}
	
	
	public void videoScreenClicked() {
		videoScreenState &=  ~(RECOMMENDATION_BUTTON_SHOW_FLAG
				| RECOMMENDATION_COUNT_SHOW_FLAG | FOLLOW_BUTTON_SHOW_FLAG
				| FOLLOW_COUNT_SHOW_FLAG | LIVER_SHOW_FLAG);
		
		if ((videoScreenState & RECOMMENDATION_BUTTON_SHOW_FLAG) == RECOMMENDATION_BUTTON_SHOW_FLAG) {
			ui.showVideoScreentItem(RECOMMENDATION_BUTTON_SHOW_FLAG, true);
		} else {
			ui.showVideoScreentItem(RECOMMENDATION_BUTTON_SHOW_FLAG, false);
		}
		
		if ((videoScreenState & RECOMMENDATION_COUNT_SHOW_FLAG) == RECOMMENDATION_COUNT_SHOW_FLAG) {
			ui.showVideoScreentItem(RECOMMENDATION_COUNT_SHOW_FLAG, true);
		}else {
			ui.showVideoScreentItem(RECOMMENDATION_COUNT_SHOW_FLAG, false);
		}

		if ((videoScreenState & FOLLOW_BUTTON_SHOW_FLAG) == FOLLOW_BUTTON_SHOW_FLAG) {
			ui.showVideoScreentItem(FOLLOW_BUTTON_SHOW_FLAG, true);
		}else {
			ui.showVideoScreentItem(FOLLOW_BUTTON_SHOW_FLAG, false);
		}

		if ((videoScreenState & FOLLOW_COUNT_SHOW_FLAG) == FOLLOW_COUNT_SHOW_FLAG) {
			ui.showVideoScreentItem(FOLLOW_COUNT_SHOW_FLAG, true);
		}else {
			ui.showVideoScreentItem(FOLLOW_COUNT_SHOW_FLAG, false);
		}

		if ((videoScreenState & LIVER_SHOW_FLAG) == LIVER_SHOW_FLAG) {
			ui.showVideoScreentItem(LIVER_SHOW_FLAG, true);
		}else {
			ui.showVideoScreentItem(LIVER_SHOW_FLAG, false);
		}
	}
	
	public void recommendationButtonClicked() {
		
	}
	
	public void followButtonClicked() {
		
	}
	
	public void liverButtonClicked() {
		
	}
	
	
	public void videoShareButtonClicked() {
		
	}
	
	public void personelButtonClicked() {
		
	}
	
	public void mapCenterMoved(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
		//TODO search from server and update marker
	}
	
	public void textClicked() {
		keyboardState = true;
		ui.showTextKeyboard(true);
	}
	
	
	public void onChildUIFinished(int ret, int requestCode) {
		keyboardState = false;
		ui.showTextKeyboard(false);
	}
	
	
	public void finished() {
		
	}

}
