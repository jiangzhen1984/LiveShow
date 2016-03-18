package com.v2tech.presenter;

import android.text.TextUtils;

public class LoginPresenter {

	
	
	public interface LoginPresenterUI {
		
		public String getUserNameText();
		
		public String getCodeText();
		
		public void updateStartButton(boolean enable);
	}
	
	
	private LoginPresenterUI ui;


	public LoginPresenter(LoginPresenterUI ui) {
		super();
		this.ui = ui;
	}
	
	
	
	public void verificationCodeButtonClicked() {
		
	}
	
	public void startButtonClicked() {
		
	}
	
	public void userNameTextChanged() {
		
	}
	
	public void codeTextChanged() {
		if (!TextUtils.isEmpty(ui.getUserNameText()) && !TextUtils.isEmpty(ui.getCodeText()) ) {
			ui.updateStartButton(true);
		} else {
			ui.updateStartButton(false);
		}
	}
}
