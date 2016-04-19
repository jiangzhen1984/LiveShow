package com.v2tech.presenter;

import android.content.Context;

public class PersonelShowPresenter extends BasePresenter {

	public interface PersonelShowPresenterUI {
		public void finishMainUI();
		
		public void updateTitleBar();
	}

	private Context context;
	private PersonelShowPresenterUI ui;

	public PersonelShowPresenter(Context context, PersonelShowPresenterUI ui) {
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


	

}
