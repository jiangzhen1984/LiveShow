package com.v2tech.presenter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.v2tech.service.GlobalHolder;
import com.v2tech.service.UserService;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.service.jni.RequestLogInResponse;
import com.v2tech.vo.User;

public class LoginPresenter extends BasePresenter {

	
	private static final int INIT = 1;
	private static final int LOGIN_ACTION = 2;
	private static final int GET_CODE_ACTION = 3;
	private static final int LOGIN_CALLBACK = 4;
	private static final int LOGOUT_AS_CALLBACK = 5;
	
	
	public interface LoginPresenterUI {
		
		public String getUserNameText();
		
		public String getCodeText();
		
		public void updateStartButton(boolean enable);
		
		public void appendBlankSpace();
		
		public void showLogingInProgress();
		
		public void doLogedIn();
		
		public void doReturned();
		
		public void setPhoneNumberError();
		
		public void showKeyboard();
		
		public void doLogonFailed();
	}
	
	
	private LoginPresenterUI ui;
	private UserService us;

	public LoginPresenter(LoginPresenterUI ui) {
		super();
		this.ui = ui;
	}
	
	
	
	
	
	@Override
	public void onUICreated() {
		h = new LocalHandler(backendThread.getLooper());
		Message.obtain(h, INIT).sendToTarget();
		ui.showKeyboard();
	}



	@Override
	public void onUIDestroyed() {
		us.clearCalledBack();
		super.destroyBackendThread();
		
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
	
	
	
	private void doLoginInBack() {
//		User u = GlobalHolder.getInstance().getCurrentUser();
//		if (u!= null && u.isNY) {
//			us.logout(new MessageListener(h, LOGOUT_AS_CALLBACK, null));
//		}
//		String username = ui.getUserNameText();
//		String code = ui.getCodeText();
//		us.login(username, code, new MessageListener(h, LOGIN_CALLBACK, null));
//		ui.showLogingInProgress();
		
		handleLoginCallback(new RequestLogInResponse(new User(111), JNIResponse.Result.SUCCESS));
	}
	
	
	public void doGetCodeInBack() {
		String number = ui.getUserNameText();
		if (TextUtils.isEmpty(number)) {
			ui.setPhoneNumberError();
			return;
		}
		us.sendVaidationCode(number);
	}
	
	
	private void handleLoginCallback(RequestLogInResponse resp) {
		if (resp.getResult() == JNIResponse.Result.SUCCESS) {
			GlobalHolder.getInstance().getCurrentUser().isNY = false;
			ui.doLogedIn();
		} else {
			ui.doLogonFailed();
		}
	}
	
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
				handleLoginCallback((RequestLogInResponse)msg.obj);
				break;
			}
		}
		
	}
	

}
