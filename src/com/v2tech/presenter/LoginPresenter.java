package com.v2tech.presenter;

import android.text.TextUtils;

import com.v2tech.net.DeamonWorker;
import com.v2tech.net.NetConnector;
import com.v2tech.net.lv.GetCodeReqPacket;
import com.v2tech.net.lv.LoginReqPacket;
import com.v2tech.net.lv.PacketTransformer;

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
	
	NetConnector conn = new DeamonWorker();
	
	public void verificationCodeButtonClicked() {
		conn.setPacketTransformer(new PacketTransformer());
		String number = ui.getUserNameText();
		conn.connect("114.215.84.236", 9999);
		conn.request(new GetCodeReqPacket(number));
	}
	
	public void startButtonClicked() {
		String number = ui.getUserNameText();
		String pwd = ui.getCodeText();
		conn.request(new LoginReqPacket(false, number, pwd));
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
