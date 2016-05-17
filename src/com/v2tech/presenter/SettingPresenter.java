package com.v2tech.presenter;

import com.v2tech.service.UserService;


public class SettingPresenter extends BasePresenter {
	
	
	UserService us;

	public interface SettingPresenterUI {

		public void doFinish();

		public void updateTitle();
	}

	private SettingPresenterUI ui;

	public SettingPresenter(SettingPresenterUI ui) {
		super();
		this.ui = ui;
		us = new UserService();
	}

	@Override
	public void onUICreated() {
		ui.updateTitle();
	}

	@Override
	public void onUIDestroyed() {
		super.onUIDestroyed();
		us.clearCalledBack();
	}

	public void returnButtonClicked() {
		ui.doFinish();
	}
	
	public void signOutBtnClicked() {
		us.logout(null, false);
		ui.doFinish();
		//TODO application logout
	}

}
