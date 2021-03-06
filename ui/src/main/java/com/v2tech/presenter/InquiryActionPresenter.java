package com.v2tech.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.v2tech.map.LocationNameQueryResponseListener;
import com.v2tech.map.LocationParameter;
import com.v2tech.map.MapAPI;
import com.v2tech.map.MapLocation;
import com.v2tech.map.MapStatus;
import com.v2tech.map.MapStatusListener;
import com.v2tech.service.InquiryService;
import com.v2tech.vo.inquiry.InquiryData;

import java.lang.ref.WeakReference;

public class InquiryActionPresenter extends BasePresenter implements  MapStatusListener {
	
	
	private static final int TARGET_LOCATION_UPDATE_CALLBACK = 1;
	
	
	private InquiryActionPresenterUI ui;
	private Context context;
	private ActionState as = ActionState.WAITING;
	private MapAPI mapInstance;
	private MapLocation currentLocation;
	
	private UIHandler uiHandler;
	private InquiryService is;
	private InquiryData data;
	
	private boolean showRoadMap;
	
	public interface InquiryActionPresenterUI {
		
		public MapAPI getMap();
		
		public MapLocation getTargetLocation();
		
		public void showBtn(boolean acceptBtn, boolean audioBtn, boolean videoBtn);
		
		public void showTargetAddress(String str);
		
		public InquiryData getInquiryDataFromUI();
		
		public void showWaitingLocation();

		public void quit();
	}

	public InquiryActionPresenter(Context context, InquiryActionPresenterUI ui) {
		this.context = context;
		this.ui = ui;
		uiHandler = new UIHandler(new WeakReference<InquiryActionPresenter>(
				this), new WeakReference<InquiryActionPresenterUI>(ui));
		is = new InquiryService();
	}

	
	
	
	@Override
	public void onUICreated() {
		super.onUICreated();
	}
	
	




	@Override
	public void onUIStarted() {
		super.onUIStarted();
		ui.showBtn(true, false, false);
		mapInstance = ui.getMap();
		mapInstance.addMapStatusListener(this);
		LocationParameter lp = mapInstance.buildParameter(context);
		lp.enableMyLococation(false);
		mapInstance.startLocate(lp);
		mapInstance.updateMap(mapInstance.buildUpater(ui.getTargetLocation()));
		mapInstance.getLocationName(ui.getTargetLocation(), new LocationNameQueryResponseListener(uiHandler, TARGET_LOCATION_UPDATE_CALLBACK, null));
		//TODO show waiting location message
		
		this.data = ui.getInquiryDataFromUI();
	}




	@Override
	public void onUIDestroyed() {
		super.onUIDestroyed();
		mapInstance.stopLocate(null);
		mapInstance.removeMapStatusListener(this);
		uiHandler = null;
		is.clearCalledBack();
		is = null;
	}


	
	
	
	
	


	@Override
	public void onMapStatusUpdated(MapStatus ms) {
		
	}




	@Override
	public void onSelfLocationUpdated(MapLocation ml) {
		currentLocation = ml;
		mapInstance.showRoadMap(currentLocation, ui.getTargetLocation());
	}




	public void acceptBtnClicked(View view) {
		as = ActionState.ACCEPTED;
		ui.showBtn(false, true, true);
		
		if (currentLocation == null) {
			ui.showWaitingLocation();
		} else {
			is.takeInquiry(data.id, currentLocation.getLat(), currentLocation.getLng());
		}
		
		
	}
	
	public void audioBtnClicked(View view) {
		//TODO send audio message
	}
	
	public void videoShareBtnClicked(View view) {
		Intent i = new Intent();
		i.putExtra("inquiry", data);
		i.putExtra("from", "inquiryaction");
		i.addCategory("com.v2tech");
		i.setAction("com.v2tech.intent.request_video_record");
		i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(i);
		ui.quit();
	}

	public void returnBtnClicked(View v) {
		ui.quit();
	}
	
	
	class UIHandler extends Handler {

		private WeakReference<InquiryActionPresenter> wrPr;
		private WeakReference<InquiryActionPresenterUI> wrUI;
		
		
		
		public UIHandler(WeakReference<InquiryActionPresenter> wrPr,
				WeakReference<InquiryActionPresenterUI> wrUI) {
			super();
			this.wrPr = wrPr;
			this.wrUI = wrUI;
		}



		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case TARGET_LOCATION_UPDATE_CALLBACK:
				if (wrUI.get() != null && msg.obj != null) {
					wrUI.get().showTargetAddress(msg.obj.toString());
				}
				break;
			}
		}
		
	}
	
	
	
	enum ActionState {
		WAITING, ACCEPTED, AUDIOING;
	}
}
