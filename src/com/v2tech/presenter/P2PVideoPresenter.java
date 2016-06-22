package com.v2tech.presenter;

import android.content.Context;

public class P2PVideoPresenter extends BasePresenter {
	
	
	private Context context;
	private P2PVideoPresenterUI ui;
	private UIType uiType;
	
	public interface P2PVideoPresenterUI {
		public int getStartType();
		
		public void showCallingLayout();
		
		public void showConnectedLayout();
		
		public void showRingingLayout();
	}

	public P2PVideoPresenter(Context context, P2PVideoPresenterUI ui) {
		super();
		this.context = context;
		this.ui = ui;
	}

	
	
	
	@Override
	public void onUICreated() {
		super.onUICreated();
		int type = ui.getStartType();
		if (type == UIType.CALLING.ordinal()) {
			uiType = UIType.CALLING;
			updateUIOnCalling();
		} else if (type == UIType.CONNECTED.ordinal()) {
			uiType = UIType.CONNECTED;
			updateUIOnConnected();
		} else if (type == UIType.RINGING.ordinal()) {
			uiType = UIType.RINGING;
			updateUIOnRinging();
		}
	}


	
	public void onHangoffBtnClicked() {
		switch (uiType) {
		case CONNECTED:
		case CALLING:
			//TODO close device
			//TODO send hangoff message
			break;
		case RINGING:
			break;
		default:
			break;
		}
	}

	public void onAcceptBtnClicked() {
		if (uiType != UIType.RINGING) {
			throw new RuntimeException(" not support ui type: " + uiType);
		}
		uiType = UIType.CONNECTED;
		updateUIOnConnected();
	}
	
	
	private void updateUIOnCalling() {
		ui.showCallingLayout();
		//TODO open local camera
	}
	
	private void updateUIOnRinging() {
		ui.showRingingLayout();
	}
	
	private void updateUIOnConnected() {
		ui.showRingingLayout();
	}

	enum UIType {
		CALLING, RINGING, CONNECTED;
	}
}
