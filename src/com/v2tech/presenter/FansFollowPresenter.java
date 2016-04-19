package com.v2tech.presenter;

import android.content.Context;

public class FansFollowPresenter extends BasePresenter {

	public interface FansFollowPresenterUI {
		public void finishMainUI();
		
		public void updateTitleBar();
		
		public void showBox();
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
		ui.showBox();
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



}
