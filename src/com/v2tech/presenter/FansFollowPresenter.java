package com.v2tech.presenter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.v2tech.view.P2PMessageActivity;
import com.v2tech.widget.LiverInteractionLayout.InterfactionBtnClickListener;

public class FansFollowPresenter extends BasePresenter implements InterfactionBtnClickListener {

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
		i.setClass(context, P2PMessageActivity.class);
		context.startActivity(i);
		
	}


	public void onFollowBtnClick(View v) {
		
	}
	

}
