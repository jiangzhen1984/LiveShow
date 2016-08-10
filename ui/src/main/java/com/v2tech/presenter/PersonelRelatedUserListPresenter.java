package com.v2tech.presenter;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.v2tech.frag.UserListFragment.UserListFragmentClickListener;
import com.v2tech.net.DeamonWorker;
import com.v2tech.net.lv.FansQueryReqPacket;
import com.v2tech.net.lv.FansQueryRespPacket;
import com.v2tech.net.lv.FollowsQueryReqPacket;
import com.v2tech.net.lv.FollowsQueryRespPacket;
import com.v2tech.net.pkt.RequestPacket;
import com.v2tech.net.pkt.ResponsePacket;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.P2PMessageService;
import com.v2tech.service.UserService;
import com.v2tech.R;
import com.v2tech.view.FansFollowActivity;
import com.v2tech.view.InquiryActionActivity;
import com.v2tech.vo.User;
import com.v2tech.vo.inquiry.InquiryData;
import com.v2tech.vo.msg.VMessageSession;

public class PersonelRelatedUserListPresenter extends BasePresenter implements UserListFragmentClickListener {
	
	public static final int TYPE_FANS = 1;
	public static final int TYPE_FRIENDS = 2;
	public static final int TYPE_FOLLOWS = 3;
	public static final int TYPE_FRIEND_INVITATION = 4;
	public static final int TYPE_MESSAGE = 5;
	
	
	private static final int MSG_GET_LIST = 1;
	
	private Context context;
	private UserService us;
	private P2PMessageService messageService;
	
	private PersonelRelatedUserListPresenterUI ui;
	private int type;
	
	private List<Item> itemList;
	private List<User> userList;
	private List<VMessageSession> msessList;
	private LocalBackendHandler local;
	
	public interface PersonelRelatedUserListPresenterUI {
		public void updateSearchBarHint(String text);
		public void updateItemAvatar(View parent, Bitmap bm);
		public void updateItemName(View parent, CharSequence name);
		public void updateItemText(View parent, CharSequence txt);
		public void updateItemSn(View parent, CharSequence sn);
		public void updateItemBtnCancel(View parent);
		public void updateItemBtnFollow(View parent);
		public void updateItemBtnTag(View parent, Object tag);
		public void updateItemGender(View parent, boolean showFlag, boolean male);
		public void updateItemUserTag(View parent, Object tag);
		public void updateItemTime(View parent, CharSequence timeStr);
		public void showFansTitle();
		public void showFriendsTitle();
		public void showFollowTitle();
		public void showFrinedInvitationTitle();
		public void showMessageTitle();
		public void doFinish();
		public void refreshDataSet();
		
		public void showTimeView(boolean flag);
		public void showRightBtmView(boolean flag);
		public void showFansBtnIcon();
		public void showFollowsBtnIcon();
		public void showFriendsBtnIcon();
		
	}
	
	
	
	
	public PersonelRelatedUserListPresenter(Context context, int type, PersonelRelatedUserListPresenterUI ui) {
		this.type = type;
		this.ui = ui;
		this.context = context;
		local = new LocalBackendHandler(this, backendThread.getLooper());
		us = new UserService();
	}
	
	
	public void onTextChanged(String str) {
		
	}
	
	
	public void onListItemClicked(Object tag) {
		if (tag instanceof SystemItem) {
			SystemItem si = (SystemItem)tag;
			
			try {
				double lat = si.content.getDouble("lat");
				double lng = si.content.getDouble("lng");
				Intent i = new Intent();
				i.putExtra("lat", lat);
				i.putExtra("lng", lng);
				InquiryData id = new InquiryData();
				id.targetLat = lat;
				id.targetLng = lng;
				id.id = si.content.getLong("irid");
				id.sponsor = new User (si.content.getLong("userid"));
				i.putExtra("inquiry", id);
				i.setClass(context, InquiryActionActivity.class);
				context.startActivity(i);
			} catch (JSONException e) {
				e.printStackTrace();
				//TODO notify user
			}
			
		} else {
			Item item = (Item)tag;
			Intent i = new Intent();
			i.putExtra("user", item.u);
			i.setClass(context, FansFollowActivity.class);
			i.putExtra("type", type);
			context.startActivity(i);
		}
	}
	
	
	public void onItemBtnClicked(Object tag) {
		Long id = (Long) tag;
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			if (u.nId == id) {
				if (type == TYPE_FOLLOWS) {
				  us.followUser(u, false);
				  userList.remove(i);
				  //remove from ui adpater list
				  itemList.remove(i);
				} else if (type == TYPE_FANS) {
					us.followUser(u, true);
					if (GlobalHolder.getInstance().mMyFollowers != null) {
						GlobalHolder.getInstance().mMyFollowers.add(u);
					}
				}
				break;
			}
		}
		ui.refreshDataSet();
	}
	
	
	public int getCount() {
		return itemList == null ? 0 : itemList.size();
	}

	public Object getItem(int position) {
		return itemList.get(position);
	}

	public long getItemId(int position) {
		return itemList.get(position).id;
	}

	public void update(int position, View convertView) {
		Item item = itemList.get(position);
		ui.updateItemName(convertView, item.getName());
		ui.updateItemSn(convertView, item.getSn());
//		if (u.follow) {
//			ui.updateItemBtnCancel(convertView);
//		} else {
//			ui.updateItemBtnFollow(convertView);
//		}
		ui.updateItemBtnTag(convertView, Long.valueOf(item.id));
		
		ui.updateItemGender(convertView, item.isShowGender(), item.gender);
		ui.updateItemUserTag(convertView, item);
		ui.updateItemTime(convertView, item.getTime());
	}

	
	


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onUICreated() {
		super.onUICreated();
	}

	
	

	@Override
	public void onUIStarted() {
		super.onUIStarted();
		boolean timeFlag = false;
		boolean btnFlag  = false;
		switch (type) {
		case  TYPE_FANS:
			ui.showFansTitle();
			timeFlag = false;
			btnFlag = true;
			ui.showFansBtnIcon();
			break;
		case  TYPE_FRIENDS:
			ui.showFriendsTitle();
			timeFlag = false;
			btnFlag = true;
			ui.showFriendsBtnIcon();
			break;
		case  TYPE_FOLLOWS:
			ui.showFollowTitle();
			timeFlag = false;
			btnFlag = true;
			ui.showFollowsBtnIcon();
			break;
		case  TYPE_FRIEND_INVITATION:
			ui.showFrinedInvitationTitle();
			timeFlag = false;
			btnFlag = true;
			ui.showFansBtnIcon();
			break;
		case  TYPE_MESSAGE:
			ui.showMessageTitle();
			timeFlag = true;
			btnFlag = false;
			break;
		}
		ui.showTimeView(timeFlag);
		ui.showRightBtmView(btnFlag);
		if (type == TYPE_FRIEND_INVITATION) {
			ui.updateSearchBarHint(context.getString(R.string.personal_friends_invitation_search_tips));
		}
		
		Message.obtain(local, MSG_GET_LIST).sendToTarget();
	}


	@Override
	public void onUIDestroyed() {
		super.onUIDestroyed();
		us.clearCalledBack();
	}


	@Override
	public void onReturnBtnClicked() {
		ui.doFinish();
	}
	
		
	
	
	private void doGetList() {
		RequestPacket req = null;
		List<User> tempList = null;
		ResponsePacket  resp = null;
		switch (type) {
		case TYPE_FANS: 
			if (GlobalHolder.getInstance().mMyFans == null) {
				req = new FansQueryReqPacket();
				resp =DeamonWorker.getInstance().request(req);
			} else {
				tempList = GlobalHolder.getInstance().mMyFans;
			}
			break;
		case TYPE_FRIENDS:
			tempList = GlobalHolder.getInstance().mMyFriends;
			tempList = handleFriendsList();
			break;
		case TYPE_FOLLOWS:
			if (GlobalHolder.getInstance().mMyFollowers == null) {
				req = new FollowsQueryReqPacket();
				resp =DeamonWorker.getInstance().request(req);
			} else {
				tempList = GlobalHolder.getInstance().mMyFollowers;
			}
			break;
		case TYPE_FRIEND_INVITATION:
			break;
		case TYPE_MESSAGE:
			if (messageService == null) {
				messageService = new P2PMessageService(context.getApplicationContext());
			}
			msessList = messageService.getMessageSession(0, 40);
			convertMessageSessionList(msessList);
			break;
		}
		
		
		if (resp != null) {
			if (!resp.getHeader().isError()) {
				switch (type) {
				case TYPE_FANS: 
					tempList = handleFansList((FansQueryRespPacket)resp);
					break;
				case TYPE_FOLLOWS:
					tempList = handleFollowsList((FollowsQueryRespPacket)resp);
					break;
				}
			} else {
				//TODO query error
			}
			
		}
		
		userList = tempList;
		convertUserList(userList);

		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				ui.refreshDataSet();
			}
			
		});
	}
	
	
	private List<User> handleFriendsList() {

		if (GlobalHolder.getInstance().mMyFans == null) {
			ResponsePacket resp =DeamonWorker.getInstance().request(new FansQueryReqPacket());
			if (!resp.getHeader().isError()) {
				handleFansList((FansQueryRespPacket)resp);
			}
		}
		if (GlobalHolder.getInstance().mMyFollowers == null) {
			ResponsePacket resp =DeamonWorker.getInstance().request(new FollowsQueryReqPacket());
			if (!resp.getHeader().isError()) {
				handleFollowsList((FollowsQueryRespPacket)resp);
			}
		}
		return combineFriends(GlobalHolder.getInstance().mMyFans, GlobalHolder.getInstance().mMyFollowers);
		
	}
	
	
	private List<User> combineFriends(List<User> fans, List<User> follows) {
		int fansSize = fans != null && fans.size() > 0 ? fans.size() : 0;
		int followSize = follows != null && follows.size() > 0 ?  follows.size() : 0;
		int size = Math.min(fansSize, followSize);
		
		List<User> friends = new ArrayList<User>(size);
		if (size <= 0) {
			return friends;
		}
		
		for (User u : fans) {
			for (User u1 : follows) {
				if (u1.nId == u.nId) {
					friends.add(u);
					break;
				}
			}
		}
		
		return friends;
	}
	
	
	
	private List<User> handleFansList(FansQueryRespPacket fqrp) {
		List<User> tmp;
		if (fqrp.fansList == null || fqrp.fansList.size() <= 0) {
			tmp = new ArrayList<User>(0);
			return tmp;
		}
		List<User> fans = new ArrayList<User>(fqrp.fansList.size());
		User u = null;
		for (Map<String, String> m : fqrp.fansList) {
			long id = Long.parseLong(m.get("id"));
			String v2idStr = m.get("v2id");
			long v2id = -1;
			if (v2idStr != null && !v2idStr.isEmpty()) {
			    v2id = Long.parseLong(v2idStr);
			}
			u = new User(v2id);
			u.nId = id;
			String strfansC = m.get("fansCount");
			if (!TextUtils.isEmpty(strfansC)) {
				u.fansCount =Integer.parseInt(strfansC); 
			}
			
			String strFollowCount = m.get("followCount");
			if (!TextUtils.isEmpty(strFollowCount)) {
				u.followerCount =Integer.parseInt(strFollowCount); 
			}
			
			String strvideoCount = m.get("videoCount");
			if (!TextUtils.isEmpty(strvideoCount)) {
				u.videoCount =Integer.parseInt(strvideoCount); 
			}
			fans.add(u);
		}
		
		tmp = fans;
		GlobalHolder.getInstance().mMyFans = tmp;
		return tmp;
	}
	
	
	
	private List<User>  handleFollowsList(FollowsQueryRespPacket fqrp) {
		List<User> tmp;
		if (fqrp.follows == null || fqrp.follows.size() <= 0) {
			tmp = new ArrayList<User>(0);
			return tmp;
		}
		List<User> fans = new ArrayList<User>(fqrp.follows.size());
		User u = null;
		for (Map<String, String> m : fqrp.follows) {
			long id = Long.parseLong(m.get("id"));
			String v2idStr = m.get("v2id");
			long v2id = -1;
			if (v2idStr != null && !v2idStr.isEmpty()) {
			    v2id = Long.parseLong(v2idStr);
			}
			u = new User(v2id);
			u.nId = id;
			
			String strfansC = m.get("fansCount");
			if (!TextUtils.isEmpty(strfansC)) {
				u.fansCount =Integer.parseInt(strfansC); 
			}
			
			String strFollowCount = m.get("followCount");
			if (!TextUtils.isEmpty(strFollowCount)) {
				u.followerCount =Integer.parseInt(strFollowCount); 
			}
			
			String strvideoCount = m.get("videoCount");
			if (!TextUtils.isEmpty(strvideoCount)) {
				u.videoCount =Integer.parseInt(strvideoCount); 
			}
			fans.add(u);
		}
		
		tmp = fans;
		GlobalHolder.getInstance().mMyFollowers = tmp;
		return tmp;
	}
	
	
	
	
	private static SimpleDateFormat format =  new SimpleDateFormat("HH:mm");
	private static SimpleDateFormat format1 =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private void convertMessageSessionList(List<VMessageSession> sessList) {
		if (sessList == null || sessList.size() <= 0) {
			return;
		}
		long today = System.currentTimeMillis() / 24 * 3600 * 1000;
		itemList  = new ArrayList<Item>(sessList.size());
		for (VMessageSession vs : sessList) {
			boolean todayFlag = today == (vs.timestamp.getTime()  / 24 * 3600 * 1000);
			
			if (vs.isSystem) {
				SystemItem si = new SystemItem();
				si.id = vs.id;
				si.content = vs.contentJson;
				si.time = todayFlag ? format1.format(vs.timestamp) : format.format(vs.timestamp);
				itemList.add(si);
			} else {
				Item item = new Item();
				itemList.add(item);
				item.id = vs.id;
				item.name = vs.fromName;
				item.sn = vs.content;
				item.time = todayFlag ? format1.format(vs.timestamp) : format.format(vs.timestamp);
				item.u = new User(vs.fromUid);
			}
		}
	}
	
	
	private void convertUserList(List<User> sessList) {
		if (sessList == null || sessList.size() <= 0) {
			return;
		}
		
		itemList  = new ArrayList<Item>(sessList.size());
		for (User vs : sessList) {
			Item item = new Item();
			itemList.add(item);
			item.id = vs.nId;
			item.name = vs.getName();
			item.gender = vs.isMale;
			item.u = vs;
			//item.sn = vs.content;
			//item.time = format.format(vs.timestamp);
		}
	}
	
	private Handler uiHandler = new Handler();
	
	
	class LocalBackendHandler extends Handler  {
		
		private WeakReference<PersonelRelatedUserListPresenter> wf;

		public LocalBackendHandler(PersonelRelatedUserListPresenter pre, Looper looper) {
			super(looper);
			wf = new WeakReference<PersonelRelatedUserListPresenter>(pre);
		}

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case MSG_GET_LIST:
				if (wf.get() != null) {
					wf.get().doGetList();
				}
				break;
			}
		}
		
	}
	
	
	
	class Item {
		public long id;
		public CharSequence name;
		public CharSequence sn;
		public boolean gender;
		public CharSequence time;
		public int underCount;
		public User u;
		public boolean showGender;
		
		
		public CharSequence getName() {
			return name;
		}
		
		public CharSequence getSn() {
			return sn;
		}

		public boolean isShowGender() {
			return true;
		}
		
		public CharSequence getTime() {
			return this.time;
		}
		
	}
	
	class SystemItem extends Item {
		JSONObject content;
		
		public CharSequence getSn() {
			try {
				return content.getString("desc");
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public CharSequence getName() {
			try {
				return content.getString("title");
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public boolean isShowGender() {
			return false;
		}
	}
}
