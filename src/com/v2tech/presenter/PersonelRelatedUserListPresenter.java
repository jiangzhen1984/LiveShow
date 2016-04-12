package com.v2tech.presenter;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;

import com.v2tech.vo.User;

public class PersonelRelatedUserListPresenter extends BasePresenter {
	
	public static final int TYPE_FANS = 1;
	public static final int TYPE_FRIENDS = 2;
	public static final int TYPE_FOLLOWS = 3;
	public static final int TYPE_FRIEND_INVITATION = 4;
	
	
	private PersonelRelatedUserListPresenterUI ui;
	private int type;
	
	List<User> userList;
	
	public interface PersonelRelatedUserListPresenterUI {
		public void updateItemAvatar(View parent, Bitmap bm);
		public void updateItemName(View parent, String name);
		public void updateItemText(View parent, String txt);
		public void updateItemSn(View parent, String sn);
		public void updateItemBtnCancel(View parent);
		public void updateItemBtnFollow(View parent);
		public void updateItemBtnTag(View parent, Object tag);
		public void updateItemGender(View parent, boolean male);
		public void showFansTitle();
		public void showFriendsTitle();
		public void showFollowTitle();
		public void showFrinedInvitationTitle();
		public void doFinish();
	}
	
	
	
	
	public PersonelRelatedUserListPresenter(int type, PersonelRelatedUserListPresenterUI ui) {
		this.type = type;
		this.ui = ui;
		userList = new ArrayList<User>();
		User u = null;
		for (int i = 100; i < 150; i++) {
			u = new  User(i, "凌小小" + i, "", "夏天斯蒂芬就哦巍峨"+ i);
			u.isMale = i % 2 == 0? true: false;
			u.follow = i % 2 == 0? true: false;
			userList.add(u);
		}
	}
	
	
	public void onTextChanged(String str) {
		
	}
	
	
	public void onListItemClicked() {
		
	}
	
	
	public void onItemBtnClicked(Object tag) {
		Long id = (Long) tag;
		for (User u : userList) {
			if (u.nId == id) {
				//TODO 
				break;
			}
		}
	}
	
	
	public int getCount() {
		return userList.size();
	}

	public Object getItem(int position) {
		return userList.get(position);
	}

	public long getItemId(int position) {
		return userList.get(position).nId;
	}

	public void update(int position, View convertView) {
		User u = userList.get(position);
		ui.updateItemName(convertView, u.getName());
		ui.updateItemSn(convertView, u.getSignature());
		if (u.follow) {
			ui.updateItemBtnCancel(convertView);
		} else {
			ui.updateItemBtnFollow(convertView);
		}
		ui.updateItemBtnTag(convertView, Long.valueOf(u.nId));
		
		ui.updateItemGender(convertView, u.isMale);
	}



	@Override
	public void onUICreated() {
		super.onUICreated();
		switch (type) {
		case  TYPE_FANS:
			ui.showFansTitle();
			break;
		case  TYPE_FRIENDS:
			ui.showFriendsTitle();
			break;
		case  TYPE_FOLLOWS:
			ui.showFollowTitle();
			break;
		case  TYPE_FRIEND_INVITATION:
			ui.showFrinedInvitationTitle();
			break;
		}
		//TODO query data from server
		//And show Progress
	}


	@Override
	public void onReturnBtnClicked() {
		ui.doFinish();
	}
	
		
	
	

}
