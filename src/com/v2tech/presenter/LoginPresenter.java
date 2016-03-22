package com.v2tech.presenter;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.v2tech.service.MessageListener;
import com.v2tech.service.UserService;

public class LoginPresenter {

	
	private static final int INIT = 1;
	private static final int LOGIN_ACTION = 2;
	private static final int GET_CODE_ACTION = 3;
	private static final int LOGIN_CALLBACK = 4;
	
	
	public interface LoginPresenterUI {
		
		public String getUserNameText();
		
		public String getCodeText();
		
		public void updateStartButton(boolean enable);
		
		public void appendBlankSpace();
		
		public void showLogingInProgress();
		
		public void doLogedIn();
		
		public void doReturned();
		
		public void setPhoneNumberError();
	}
	
	
	private LoginPresenterUI ui;
	private UserService us;

	public LoginPresenter(LoginPresenterUI ui) {
		super();
		this.ui = ui;
	}
	
	
	public void verificationCodeButtonClicked() {
		Message.obtain(h, GET_CODE_ACTION, null).sendToTarget();;
	}
	
	public void startButtonClicked() {
		Message.obtain(h, LOGIN_ACTION, null).sendToTarget();;
	}
	
	public void returnButtonClicked() {
		ui.doReturned();
	}
	
	public void userNameTextChanged() {
		int len = ui.getUserNameText().length();
		if (len == 4 || len == 8) {
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
	
	
	
	public void onUICreate() {
		if (th != null) {
			th.quit();
		}
		th = new HandlerThread("LoginPresenterBackEnd");
		th.start();
		
		h = new LocalHandler(th.getLooper());
		Message.obtain(h, INIT).sendToTarget();;
	}
	
	
	public void onUIDestroy() {
		us.clearCalledBack();
		th.quit();
		
	}
	
	
	
	private void doLoginInBack() {
		String username = ui.getUserNameText();
		String code = ui.getCodeText();
		us.login(username, code, new MessageListener(h, LOGIN_CALLBACK, null));
		ui.showLogingInProgress();
	}
	
	
	public void doGetCodeInBack() {
		String number = ui.getUserNameText();
		if (TextUtils.isEmpty(number)) {
			ui.setPhoneNumberError();
			return;
		}
		us.sendVaidationCode(number);
	}
	
	HandlerThread th;
	private Handler h;
	
	class LocalHandler  extends Handler {
		

		public LocalHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			int w = msg.what;
			switch (w) {
			case INIT:
				us = new UserService();
				break;
			case LOGIN_ACTION:
				doLoginInBack();
				break;
			case GET_CODE_ACTION:
				doGetCodeInBack();
				break;
			case LOGIN_CALLBACK:
				ui.doLogedIn();
				break;
			}
		}
		
	}
	

}
