package com.v2tech.presenter;

import android.content.Context;

public class PersonelPresenter extends BasePresenter {

	public interface PersonelPresenterUI {
		public void finishMainUI();
	}

	private Context context;
	private PersonelPresenterUI ui;

	public PersonelPresenter(Context context, PersonelPresenterUI ui) {
		super();
		this.context = context;
		this.ui = ui;
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
	public void onUICreated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUIStarted() {
		
	}

	@Override
	public void onUIResumed() {
		
	}

	@Override
	public void onUIPaused() {
		
	}

	@Override
	public void onUIStopped() {
		
	}

	@Override
	public void onUIDestroyed() {
		
	}
	
	
	

}
