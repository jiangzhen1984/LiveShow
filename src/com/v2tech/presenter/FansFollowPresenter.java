package com.v2tech.presenter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.v2tech.view.P2PMessageActivity;
import com.v2tech.vo.User;
import com.v2tech.widget.LiverInteractionLayout.InterfactionBtnClickListener;

public class FansFollowPresenter extends BasePresenter implements InterfactionBtnClickListener {

	public interface FansFollowPresenterUI {
		public void finishMainUI();
		
		public void updateTitleBar();
		
		public void showBox();
		
		public Object getIntentData(String key);
	}

	private Context context;
	private FansFollowPresenterUI ui;
	private User u;

	public FansFollowPresenter(Context context, FansFollowPresenterUI ui) {
		super();
		this.context = context;
		this.ui = ui;
		u = (User)ui.getIntentData("user");
	}
	
	
	

	@Override
	public void onUICreated() {
		super.onUICreated();
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




	@Override
	public void onChattingBtnClicked(View v) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void onVideoCallBtnClicked(View v) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void onMsgBtnClicked(View v) {
		Intent i = new Intent();
		i.putExtra("chatuserid", u.getmUserId());
		i.setClass(context, P2PMessageActivity.class);
		context.startActivity(i);
		
	}


	public void onFollowBtnClick(View v) {
		
	}
	

}
