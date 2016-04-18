package com.v2tech.presenter;

import android.content.Context;

public class FansFollowPresenter extends BasePresenter {

	public interface FansFollowPresenterUI {
		public void finishMainUI();
		
		public void updateTitleBar();
	}

	private Context context;
	private FansFollowPresenterUI ui;

	public FansFollowPresenter(Context context, FansFollowPresenterUI ui) {
		super();
		this.context = context;
		this.ui = ui;
	}
	
	
	

	@Override
	public void onUICreated() {
		ui.updateTitleBar();
	}




	public void friendsBtnClicked() {

	}

	public void followBtnClicked() {

	}

	public void fansBtnClicked() {

	}

	public void settingBtnClicked() {

	}

	public void returnBtnClicked() {
		ui.finishMainUI();
	}



	@Override
	public void onUIDestroyed() {
		destroyBackendThread();
	}
	
	
	

}
