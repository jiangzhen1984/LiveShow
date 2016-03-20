package com.v2tech.presenter;

import android.text.TextUtils;

public class LoginPresenter {

	
	
	public interface LoginPresenterUI {
		
		public String getUserNameText();
		
		public String getCodeText();
		
		public void updateStartButton(boolean enable);
		
		public void appendBlankSpace();
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
		int len = ui.getUserNameText().length();
		if (len == 3 || len == 8) {
			ui.appendBlankSpace();
		}
	}
	
	
	public void codeTextChanged() {
		if (!TextUtils.isEmpty(ui.getUserNameText()) && !TextUtils.isEmpty(ui.getCodeText()) ) {
			ui.updateStartButton(true);
		} else {
			ui.updateStartButton(false);
		}
	}
}
