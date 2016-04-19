package com.v2tech.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.SparseArray;

import com.V2.jni.ConfigRequest;
import com.v2tech.vo.User;

public class GlobalHolder {

	
	
	public ConfigRequest mCR ;
	public UserService us;
	public ConferenceService cs;
	
	private static GlobalHolder holder;

	private User mCurrentUser;

	private Map<Long, User> mUserHolder = new HashMap<Long, User>();
	private Map<Long, String> mAvatarHolder = new HashMap<Long, String>();
	
	private Map<Long, Bitmap> mAvatarBmHolder = new HashMap<Long, Bitmap>();

	/**
	 * May some people would add you become friend when user logined successfully , and need to give user hint. 
	 */
	private SparseArray<Long> addFriendToShow = new SparseArray<Long>(); 
	
	
	public List<User> mMyFans;
	
	public List<User> mMyFollowers;
	
	public List<User> mMyFriends;

	public static synchronized GlobalHolder getInstance() {
		if (holder == null) {
			holder = new GlobalHolder();
		}
		return holder;
	}

	private GlobalHolder() {
	}

	public User getCurrentUser() {
		return mCurrentUser;
	}

	public long getCurrentUserId() {
		if (mCurrentUser == null) {
			return 0;
		} else {
			return mCurrentUser.getmUserId();
		}
	}
	
	public long nyUserId = 0;

	public void setCurrentUser(User u) {
		this.mCurrentUser = u;
		this.mCurrentUser.setCurrentLoggedInUser(true);
		this.mCurrentUser.updateStatus(User.Status.ONLINE);
		User mU = getUser(u.getmUserId());
		if (mU != null) {
			mU.updateStatus(User.Status.ONLINE);
		} else {
			// putUser(u.getmUserId(), u);
		}
	}

	private Object mUserLock = new Object();

	public User putUser(long id, User u) {
		if (id <= 0  || u == null) {
			return null;
		}
		synchronized (mUserLock) {
			Long key = Long.valueOf(id);
			User cu = mUserHolder.get(key);
			if (cu != null) {
				//Update user property for received user information
				cu.updateUser(false);
				
				if (u.getAddress() != null) {
					cu.setAddress(u.getAddress());
				}
				if (u.getAccount() != null) {
					cu.setAccount(u.getAccount());
				}
				cu.setAuthtype(u.getAuthtype());
				if (u.getBirthday() != null) {
					cu.setBirthday(u.getBirthday());
				}
				if (u.getmStringBirthday() != null) {
					cu.setmStringBirthday(u.getmStringBirthday());
				}
				if(u.getmEmail()!=null){
					cu.setEmail(u.getmEmail());
				}
				if(u.getFax()!=null){
					cu.setFax(u.getFax());
				}
				if(u.getJob()!=null){
					cu.setJob(u.getJob());
				}
				if (u.getMobile() != null) {
					cu.setMobile(u.getMobile());
				}
				if (u.getNickName() != null) {
					cu.setNickName(u.getNickName());
				}
				if (u.getSex() != null) {
					cu.setSex(u.getSex());
				}
				if (u.getSignature() != null) {
					cu.setSignature(u.getSignature());
				}
				if (u.getTelephone() != null) {
					cu.setTelephone(u.getTelephone());
				}
				if (u.getName() != null) {
					cu.setName(u.getName());
				}
				cu.updateUser(false);
				return cu;
			}
			//Update user property for received user information
			u.updateUser(false);
			mUserHolder.put(key, u);
			
			Bitmap avatar = mAvatarBmHolder.get(key);
			if (avatar != null) {
				u.setAvatarBitmap(avatar);
			}
		}

		return u;
	}

	/**
	 * Get user object according user ID<br>
	 * If id is negative, will return null.<br>
	 * Otherwise user never return null. If application doesn't receive user information from server.<br>
	 * User property is dirty {@link User#isDirty()}
	 * @param id
	 * @return
	 */
	public User getUser(long id) {
		if (id <= 0) {
			return null;
		}
		Long key = Long.valueOf(id);
		synchronized (key) {
			User tmp = mUserHolder.get(key);
			if (tmp == null) {
				tmp = new User(id);
				mUserHolder.put(key, tmp);
			}
			return tmp;
		}

	}



	
	public List<User> getFansList() {
		return mMyFans;
	}
	
	
	public List<User> getFollowerList() {
		return mMyFollowers;
	}
	
	
	public void updateUserList(int type, List<User> list) {
		if (type == 1) {
			this.mMyFans = list;
		} else if (type ==2 ) {
			this.mMyFollowers = list;
		}
	}





	public String getAvatarPath(long uid) {
		Long key = Long.valueOf(uid);
		return this.mAvatarHolder.get(key);
	}

	public void putAvatar(long uid, String path) {
		Long key = Long.valueOf(uid);
		this.mAvatarHolder.put(key, path);
	}


	


	
	public Bitmap getUserAvatar(long id) {
		Long key = Long.valueOf(id);
		return mAvatarBmHolder.get(key);
	}
	
	public SparseArray<Long> getAddFriendToShow() {
		return addFriendToShow;
	}

}
