package com.v2tech.presenter;

import android.content.Context;
import android.content.Intent;

import com.v2tech.view.PersonalSettingActivity;

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
		super.onUICreated();
		
	}

	


	@Override
	public void onUIStarted() {
		super.onUIStarted();
		ui.updateTitleBar();
	}




	private static final int UI_TYPE_NICK_NAME_SETTING = 1;
	private static final int UI_TYPE_LOCATION_SETTING = 2;
	private static final int UI_TYPE_GENDER_SETTING = 3;
	private static final int UI_TYPE_AVATAR_SETTING = 4;
	private static final int UI_TYPE_SIGNATURE_SETTING = 5;

	public void locationUpdateBtnClicked() {
		startUpdateActivity(UI_TYPE_LOCATION_SETTING);
	}

	public void avatarUpdateBtnClicked() {
		startUpdateActivity(UI_TYPE_AVATAR_SETTING);
	}

	public void nickNameUpdateBtnClicked() {
		startUpdateActivity(UI_TYPE_NICK_NAME_SETTING);
	}

	public void signatureUpdateBtnClicked() {
		startUpdateActivity(UI_TYPE_SIGNATURE_SETTING);
	}

	public void genderUpdateBtnClicked() {
		startUpdateActivity(UI_TYPE_GENDER_SETTING);
	}


	private void startUpdateActivity(int type) {
		Intent i = new Intent();
		i.putExtra("type", type);
		i.setClass(context, PersonalSettingActivity.class);
		context.startActivity(i);
	}

	public void returnBtnClicked() {
		ui.finishMainUI();
	}


	

}
