package com.v2tech.presenter;

import android.content.Context;
import android.content.Intent;

import com.v2tech.service.GlobalHolder;
import com.v2tech.view.PersonelRelatedUserListActivity;
import com.v2tech.view.PersonelVideosActivity;
import com.v2tech.view.PersonelWalletActivity;
import com.v2tech.view.SettingActivity;
import com.v2tech.vo.User;

public class PersonelPresenter extends BasePresenter {

	public interface PersonelPresenterUI {
		public void finishMainUI();
		
		public void updateTitleBar();
		
		public void showPersonelDetailUI();
		
		public void updateUserUI(User user);
	}

	private Context context;
	private PersonelPresenterUI ui;

	public PersonelPresenter(Context context, PersonelPresenterUI ui) {
		super();
		this.context = context;
		this.ui = ui;
	}
	
	
	

	@Override
	public void onUICreated() {
		super.onUICreated();
		ui.updateTitleBar();
		ui.updateUserUI(GlobalHolder.getInstance().getCurrentUser());
	}

	
	public void personelBtnClicked() {
		ui.showPersonelDetailUI();
	}



	public void friendsBtnClicked() {
		showSubActivity(PersonelRelatedUserListPresenter.TYPE_FRIENDS);
	}

	public void friendsInvitationBtnClicked() {
		showSubActivity(PersonelRelatedUserListPresenter.TYPE_FRIEND_INVITATION);
	}

	
	public void myMessageBtnClicked() {
		showSubActivity(PersonelRelatedUserListPresenter.TYPE_MESSAGE);
	}

	
	public void followBtnClicked() {
		showSubActivity(PersonelRelatedUserListPresenter.TYPE_FOLLOWS);
	}

	public void fansBtnClicked() {
		showSubActivity(PersonelRelatedUserListPresenter.TYPE_FANS);
	}

	public void settingBtnClicked() {
		Intent i = new Intent();
		i.setClass(context, SettingActivity.class);
		context.startActivity(i);
	}
	
	public void walletBtnClicked() {
		Intent i = new Intent();
		i.setClass(context, PersonelWalletActivity.class);
		context.startActivity(i);
	}
	
	public void videoBtnClicked() {
		Intent i = new Intent();
		i.setClass(context, PersonelVideosActivity.class);
		context.startActivity(i);
	}
	
	

	public void returnBtnClicked() {
		ui.finishMainUI();
	}





	
	private void showSubActivity(int type) {
		Intent i = new Intent();
		i.setClass(context, PersonelRelatedUserListActivity.class);
		i.putExtra("type", type);
		context.startActivity(i);
	}
	
	

}
