package com.v2tech.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Bitmap;
import android.util.SparseArray;

import com.V2.jni.V2GlobalEnum;
import com.V2.jni.ind.V2Group;
import com.V2.jni.util.V2Log;
import com.v2tech.vo.AddFriendHistorieNode;
import com.v2tech.vo.ConferenceGroup;
import com.v2tech.vo.ContactGroup;
import com.v2tech.vo.CrowdGroup;
import com.v2tech.vo.Group;
import com.v2tech.vo.Group.GroupType;
import com.v2tech.vo.OrgGroup;
import com.v2tech.vo.User;
import com.v2tech.vo.UserDeviceConfig;

public class GlobalHolder {

	private static GlobalHolder holder;

	private User mCurrentUser;

	private List<Group> mOrgGroup = new ArrayList<Group>();

	private List<Group> mConfGroup = new ArrayList<Group>();

	private List<Group> mContactsGroup = new ArrayList<Group>();

	private List<Group> mCrowdGroup = new ArrayList<Group>();

	private Map<Long, User> mUserHolder = new HashMap<Long, User>();
	private Map<Long, Group> mGroupHolder = new HashMap<Long, Group>();
	private Map<Long, String> mAvatarHolder = new HashMap<Long, String>();
	
	public List<AddFriendHistorieNode> addFriendHistorieList=new ArrayList<AddFriendHistorieNode>();

	private Map<Long, Set<UserDeviceConfig>> mUserDeviceList = new HashMap<Long, Set<UserDeviceConfig>>();

	private Map<Long, Bitmap> mAvatarBmHolder = new HashMap<Long, Bitmap>();

	private List<String> dataBaseTableCacheName = new ArrayList<String>();
	
	/**
	 * May some people would add you become friend when user logined successfully , and need to give user hint. 
	 */
	private SparseArray<Long> addFriendToShow = new SparseArray<Long>(); 

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

	/**
	 * Update group information according server's side push data
	 * 
	 * @param gType
	 * @param list
	 * 
	 */
	public void updateGroupList(int gType, List<V2Group> list) {

		for (V2Group vg : list) {
			Group cache = mGroupHolder.get(vg.id);
			if (cache != null) {
				continue;
			}
			
			if(vg.name == null)
				V2Log.e("parse the group name is wroing...the group is :" + vg.id);
				
			Group g = null;
			if (gType == V2GlobalEnum.GROUP_TYPE_CROWD) {
				boolean flag = true;
				for (Group group : mCrowdGroup) {
					if(group.getmGId() == vg.id){
						flag = false;
					}
				}
				
				if(flag){
					User owner = GlobalHolder.getInstance().getUser(vg.owner.uid);
					g = new CrowdGroup(vg.id, vg.name, owner);
					((CrowdGroup) g).setBrief(vg.brief);
					((CrowdGroup) g).setAnnouncement(vg.announce);
					mCrowdGroup.add(g);
				}
			} else if (gType == V2GlobalEnum.GROUP_TYPE_CONFERENCE) {
				User owner = GlobalHolder.getInstance().getUser(vg.owner.uid);
				User chairMan = GlobalHolder.getInstance().getUser(
						vg.chairMan.uid);
				g = new ConferenceGroup(vg.id, vg.name, owner, vg.createTime,
						chairMan);
				mConfGroup.add(g);
			} else if (gType == V2GlobalEnum.GROUP_TYPE_DEPARTMENT) {
				g = new OrgGroup(vg.id, vg.name);
				mOrgGroup.add(g);
			} else if (gType == V2GlobalEnum.GROUP_TYPE_CONTACT) {
				g = new ContactGroup(vg.id, vg.name);
				if (vg.isDefault) {
					((ContactGroup) g).setDefault(true);
				}

				mContactsGroup.add(g);
			} else {
				throw new RuntimeException(" Can not support this type");
			}


			mGroupHolder.put(g.getmGId(), g);

			populateGroup(gType, g, vg.childs);
		}

	}

	public void addGroupToList(int groupType , Group g) {
		if (groupType == V2GlobalEnum.GROUP_TYPE_DEPARTMENT) {
		} else if (groupType == V2GlobalEnum.GROUP_TYPE_CONFERENCE) {
			mConfGroup.add(g);
		} else if (groupType == V2GlobalEnum.GROUP_TYPE_CROWD) {
			this.mCrowdGroup.add(g);
		} else if (groupType == V2GlobalEnum.GROUP_TYPE_CONTACT) {
			this.mContactsGroup.add(g);
		}
		mGroupHolder.put(Long.valueOf(g.getmGId()), g);
	}

	/**
	 * 
	 * @param groupType 
	 * @param gId
	 * @return
	 * 
	 * {@see com.V2.jni.V2GlobalEnum}
	 */
	public Group getGroupById(int groupType, long gId) {
		return mGroupHolder.get(Long.valueOf(gId));
	}
	
	/**
	 * @param gId
	 * @return
	 * 
	 * {@see com.V2.jni.V2GlobalEnum}
	 */
	public Group getGroupById(long gId) {
		return mGroupHolder.get(Long.valueOf(gId));
	}

	private void populateGroup(int groupType , Group parent, Set<V2Group> list) {
		for (V2Group vg : list) {
			Group cache = mGroupHolder.get(Long.valueOf(vg.id));

			Group g = null;
			if (cache != null) {
				g = cache;
				// Update new name
				cache.setName(vg.name);
			} else {
				if (groupType == V2GlobalEnum.GROUP_TYPE_CROWD) {
					User owner = GlobalHolder.getInstance().getUser(
							vg.owner.uid);
					g = new CrowdGroup(vg.id, vg.name, owner);
				} else if (groupType == V2GlobalEnum.GROUP_TYPE_CONFERENCE) {
					User owner = GlobalHolder.getInstance().getUser(
							vg.owner.uid);
					User chairMan = GlobalHolder.getInstance().getUser(
							vg.chairMan.uid);
					g = new ConferenceGroup(vg.id, vg.name, owner,
							vg.createTime, chairMan);
				} else if (groupType == V2GlobalEnum.GROUP_TYPE_DEPARTMENT) {
					g = new OrgGroup(vg.id, vg.name);
				} else if (groupType == V2GlobalEnum.GROUP_TYPE_CONTACT) {
					g = new ContactGroup(vg.id, vg.name);
				} else {
					throw new RuntimeException(" Can not support this type");
				}
			}

			parent.addGroupToGroup(g);
			mGroupHolder.put(Long.valueOf(g.getmGId()), g);

			populateGroup(groupType, g, vg.childs);
		}
	}

	/**
	 * Group information is server active call, we can't request from server
	 * directly.<br>
	 * Only way to get group information is waiting for server call.<br>
	 * So if this function return null, means service doesn't receive any call
	 * from server. otherwise server already sent group information to service.<br>
	 * If you want to know indication, please register receiver:<br>
	 * category: {@link #JNI_BROADCAST_CATEGROY} <br>
	 * action : {@link #JNI_BROADCAST_GROUP_NOTIFICATION}<br>
	 * Notice: maybe you didn't receive broadcast forever, because this
	 * broadcast is sent before you register
	 * 
	 * @param gType
	 * @return return null means server didn't send group information to
	 *         service.
	 */
	public List<Group> getGroup(int groupType) {
		switch (groupType) {
		case V2GlobalEnum.GROUP_TYPE_DEPARTMENT:
			return this.mOrgGroup;
		case V2GlobalEnum.GROUP_TYPE_CONTACT:
			return mContactsGroup;
		case V2GlobalEnum.GROUP_TYPE_CROWD:
			List<Group> ct = new CopyOnWriteArrayList<Group>();
			ct.addAll(this.mCrowdGroup);
			return ct;
		case V2GlobalEnum.GROUP_TYPE_CONFERENCE:
			List<Group> confL = new ArrayList<Group>();
			confL.addAll(this.mConfGroup);
			Collections.sort(confL);
			List<Group> sortConfL = new CopyOnWriteArrayList<Group>(confL);
			return sortConfL;
		default:
			throw new RuntimeException("Unkonw type");
		}

	}

	/**
	 * Find all types of group information according to group ID
	 * 
	 * @param gid
	 * @return null if doesn't find group, otherwise return Group information
	 * 
	 * @see Group
	 */
	public Group findGroupById(long gid) {
		return mGroupHolder.get(Long.valueOf(gid));
	}

	/**
	 * Add user collections to group collections
	 * 
	 * @param gList
	 * @param uList
	 * @param belongGID
	 */
	public void addUserToGroup(List<Group> gList, List<User> uList,
			long belongGID) {
		for (Group g : gList) {
			if (belongGID == g.getmGId()) {
				g.addUserToGroup(uList);
				return;
			}
			addUserToGroup(g.getChildGroup(), uList, belongGID);
		}
	}

	public void removeGroupUser(long gid, long uid) {
		Group g = this.findGroupById(gid);
		if (g != null) {
			g.removeUserFromGroup(uid);
		} else {
			
		}
	}

	/**
	 * Add user collections to group collections
	 * 
	 * @param uList
	 * @param belongGID
	 */
	public void addUserToGroup(List<User> uList, long belongGID) {
		Group g = findGroupById(belongGID);
		if (g == null) {
			V2Log.e("Doesn't receive group<" + belongGID + "> information yet!");
			return;
		}
		g.addUserToGroup(uList);
	}

	public void addUserToGroup(User u, long belongGID) {
		Group g = findGroupById(belongGID);
		if (g == null) {
			V2Log.e("Doesn't receive group<" + belongGID + "> information yet!");
			return;
		}
		g.addUserToGroup(u);
	}

	public boolean removeGroup(GroupType gType, long gid) {
		List<Group> list = null;
		if (gType == GroupType.CONFERENCE) {
			list = mConfGroup;
		} else if (gType == GroupType.CONTACT) {
			list = mContactsGroup;
		} else if (gType == GroupType.CHATING) {
			list = mCrowdGroup;
		} else if (gType == GroupType.ORG) {
			list = mOrgGroup;
		}
		for (int i = 0; i < list.size(); i++) {
			Group g = list.get(i);
			if (g.getmGId() == gid) {
				list.remove(g);
				mGroupHolder.remove(Long.valueOf(gid));
				return true;
			}
		}
		return false;
	}

	public String getAvatarPath(long uid) {
		Long key = Long.valueOf(uid);
		return this.mAvatarHolder.get(key);
	}

	public void putAvatar(long uid, String path) {
		Long key = Long.valueOf(uid);
		this.mAvatarHolder.put(key, path);
	}

	/**
	 * Get user's video device according to user id.<br>
	 * This function never return null, even through we don't receive video
	 * device data from server.
	 * 
	 * @param uid
	 *            user's id
	 * @return list of user device
	 */
	public List<UserDeviceConfig> getAttendeeDevice(long uid) {
		Set<UserDeviceConfig> list = mUserDeviceList.get(Long.valueOf(uid));
		if (list == null) {
			return null;
		}

		return new ArrayList<UserDeviceConfig>(list);
	}
	
	
	public UserDeviceConfig getUserDefaultDevice(long uid) {
		Set<UserDeviceConfig> list = mUserDeviceList.get(Long.valueOf(uid));
		if (list == null) {
			return null;
		}
		for (UserDeviceConfig udc : list) {
			if (udc.isDefault()) {
				return udc;
			}
		}
		
		if (list.size() > 0) {
			V2Log.e("Not found default device, use first device !");
			return list.iterator().next();
		}
		return null;
	}

	/**
	 * Update user video device and clear existed user device first
	 * 
	 * @param id
	 * @param udcList
	 */
	public void updateUserDevice(long id, List<UserDeviceConfig> udcList) {
		Long key = Long.valueOf(id);
		Set<UserDeviceConfig> list = mUserDeviceList.get(key);
		if (list != null) {
			list.clear();
		} else {
			list = new HashSet<UserDeviceConfig>();
			mUserDeviceList.put(key, list);
		}
		list.addAll(udcList);
	}

	
	public boolean isFriend(User user){
		
		if(user == null){
			V2Log.e("GlobalHolder isFriend ---> get user is null , please check conversation user is exist");
			return false;
		}
		
		long currentUserID = user.getmUserId();
		List<Group> friendGroup = GlobalHolder.getInstance().getGroup(V2GlobalEnum.GROUP_TYPE_CONTACT);
		if(friendGroup.size() >= 0){
			for (Group friend : friendGroup) {
				List<User> users = friend.getUsers();
				for (User friendUser : users) {
					if(currentUserID == friendUser.getmUserId()){
						return true;
					}
				}
			}
		}
		return false;
	}

	
	public Bitmap getUserAvatar(long id) {
		Long key = Long.valueOf(id);
		return mAvatarBmHolder.get(key);
	}
	
	public SparseArray<Long> getAddFriendToShow() {
		return addFriendToShow;
	}
	
	public List<String> getDataBaseTableCacheName() {
		return dataBaseTableCacheName;
	}

	public void setDataBaseTableCacheName(List<String> dataBaseTableCacheName) {
		this.dataBaseTableCacheName = dataBaseTableCacheName;
	}

}
