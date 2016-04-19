package com.v2tech.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.v2tech.net.DeamonWorker;
import com.v2tech.net.lv.FansQueryReqPacket;
import com.v2tech.net.lv.FansQueryRespPacket;
import com.v2tech.net.lv.FollowsQueryReqPacket;
import com.v2tech.net.lv.FollowsQueryRespPacket;
import com.v2tech.net.pkt.RequestPacket;
import com.v2tech.net.pkt.ResponsePacket;
import com.v2tech.service.GlobalHolder;
import com.v2tech.vo.User;

public class PersonelRelatedUserListPresenter extends BasePresenter {
	
	public static final int TYPE_FANS = 1;
	public static final int TYPE_FRIENDS = 2;
	public static final int TYPE_FOLLOWS = 3;
	public static final int TYPE_FRIEND_INVITATION = 4;
	
	
	private static final int MSG_GET_LIST = 1;
	
	
	private PersonelRelatedUserListPresenterUI ui;
	private int type;
	
	List<User> userList;
	
	private LocalBackendHandler local;
	
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
		public void refreshDataSet();
	}
	
	
	
	
	public PersonelRelatedUserListPresenter(int type, PersonelRelatedUserListPresenterUI ui) {
		this.type = type;
		this.ui = ui;
		local = new LocalBackendHandler(this, backendThread.getLooper());
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
		return userList == null ? 0 : userList.size();
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
		Message.obtain(local, MSG_GET_LIST).sendToTarget();
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
			u = new User(id);
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
			u = new User(id);
			fans.add(u);
		}
		
		tmp = fans;
		GlobalHolder.getInstance().mMyFollowers = tmp;
		return tmp;
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
}
