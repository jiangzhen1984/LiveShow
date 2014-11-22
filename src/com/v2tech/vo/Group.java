package com.v2tech.vo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;


import com.V2.jni.V2GlobalEnum;
import com.V2.jni.util.V2Log;
import com.v2tech.service.GlobalHolder;
import com.v2tech.util.GlobalConfig;


/**
 * Group information
 * 
 * @author 28851274
 * 
 */
public abstract class Group implements Comparable<Group> {

	protected long mGId;
	protected GroupType mGroupType;
	protected String mName;

	protected long mOwner;
	protected User mOwnerUser;
	protected Date mCreateDate;
	protected Group mParent;
	protected List<Group> mChild;
	protected Set<User> users;
	protected int level;
	private Object mLock = new Object();

	public enum GroupType {
		ORG(V2GlobalEnum.GROUP_TYPE_DEPARTMENT), CONTACT(
				V2GlobalEnum.GROUP_TYPE_CONTACT), CHATING(
				V2GlobalEnum.GROUP_TYPE_CROWD), CONFERENCE(
				V2GlobalEnum.GROUP_TYPE_CONFERENCE), UNKNOWN(-1);

		private int type;

		private GroupType(int type) {
			this.type = type;
		}

		public static GroupType fromInt(int code) {
			switch (code) {
			case V2GlobalEnum.GROUP_TYPE_DEPARTMENT:
				return ORG;
			case V2GlobalEnum.GROUP_TYPE_CONTACT:
				return CONTACT;
			case V2GlobalEnum.GROUP_TYPE_CROWD:
				return CHATING;
			case V2GlobalEnum.GROUP_TYPE_CONFERENCE:
				return CONFERENCE;
			default:
				return UNKNOWN;

			}
		}

		public int intValue() {
			return type;
		}
	}

	/**
	 * 
	 * @param gId
	 * @param groupType
	 * @param name
	 * @param owner
	 * @param createDate
	 */
	protected Group(long gId, GroupType groupType, String name, User owner,
			Date createDate) {

		this.mGId = gId;
		this.mGroupType = groupType;
		this.mName = name;
		this.mOwnerUser = owner;
		this.mCreateDate = createDate;
		users = new CopyOnWriteArraySet<User>();
		mChild = new CopyOnWriteArrayList<Group>();
		level = 1;

	}

	/**
	 * 
	 * @param gId
	 * @param groupType
	 * @param name
	 * @param owner
	 * @param createDate
	 */
	protected Group(long gId, GroupType groupType, String name, User owner) {
		this(gId, groupType, name, owner, new Date());

	}

	public long getmGId() {
		return mGId;
	}

	public void setGId(long mGId) {
		this.mGId = mGId;
	}

	public GroupType getGroupType() {
		return mGroupType;
	}

	public void setGroupType(GroupType mGroupType) {
		this.mGroupType = mGroupType;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public Date getCreateDate() {
		return mCreateDate;
	}

	public String getStrCreateDate() {
		if (this.mCreateDate != null) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm",
					Locale.getDefault());
			return df.format(this.mCreateDate);
		} else {
			return null;
		}

	}

	public void setCreateDate(Date createDate) {
		this.mCreateDate = createDate;
	}

	public User getOwnerUser() {
		return mOwnerUser;
	}

	public void setOwnerUser(User mOwnerUser) {
		this.mOwnerUser = mOwnerUser;
	}

	public Group getParent() {
		return mParent;
	}

	public void setmParent(Group parent) {
		this.mParent = parent;
		level = this.getParent().getLevel() + 1;
	}

	public void addUserToGroup(Collection<User> u) {
		if (u == null) {
			V2Log.e(" Invalid user data");
			return;
		}
		synchronized (mLock) {
			this.users.addAll(u);
		}
	}

	public void addUserToGroup(User u) {
		if (u == null) {
			V2Log.e(" Invalid user data");
			return;
		}
		synchronized (mLock) {
			this.users.add(u);
			u.addUserToGroup(this);
		}
	}

	public void removeUserFromGroup(User u) {
		synchronized (mLock) {
			this.users.remove(u);
		}
	}

	public void removeUserFromGroup(long uid) {
		synchronized (mLock) {
			// User object use id as identification
			User tmpUser = new User(uid);
			users.remove(tmpUser);
		}
	}

	/**
	 * return copy collection
	 * 
	 * @return
	 */
	public List<User> getUsers() {
		return new ArrayList<User>(this.users);
	}

	public List<Group> getChildGroup() {
		return this.mChild;
	}

	/**
	 * Get sub group and user sum
	 * 
	 * @return
	 */
	public int getSubSize() {
		return this.mChild.size() + this.users.size();
	}

	public void addGroupToGroup(Group g) {
		if (g == null) {
			V2Log.e(" Invalid group data");
			return;
		}
		synchronized (mLock) {
			this.mChild.add(g);
			g.setmParent(this);
		}
	}

	/**
	 * Find use in current group and childs group.<br>
	 * If find and return first belongs group.
	 * @param u
	 * @return
	 */
	public Group findUser(User u) {
		if (u == null) {
			return null;
		}
		return internalSearchUser(null, u);
	}
	
	public Group internalSearchUser(Group g, User user) {
		if (g == null) {
			g = this;
		}
		List<User> list = this.getUsers();
		for (User tu : list) {
			if (tu.getmUserId() == user.getmUserId()) {
				return g;
			}
		}
		List<Group> subGroups = g.getChildGroup();
		for (int i = 0; i < subGroups.size(); i++) {
			Group subG = subGroups.get(i);
			Group gg = internalSearchUser(subG, user);
			if (gg != null) {
				return gg;
			}
		}
		return null;
		
	}

	public List<User> searchUser(String text) {
		List<User> l = new ArrayList<User>();
		Group.searchUser(text, l, this);
		return l;
	}

	public static void searchUser(String text, List<User> l, Group g) {
		if (l == null || g == null) {
			return;
		}
		List<User> list = g.getUsers();
		for (User u : list) {
			if ((u != null && u.getName() != null && u.getName().contains(text))
					|| (u.getArra().equals(text))) {
				l.add(u);
			}
		}
		for (Group subG : g.getChildGroup()) {
			searchUser(text, l, subG);
		}
	}

	public int getOnlineUserCount() {
		//FIXME should optimze data structure
		Set<User> counter = new HashSet<User>();
		this.populateUser(this, counter);

		int c = 0;
		for (User u : counter) {
			if (u.getmStatus() == User.Status.ONLINE
					|| u.getmStatus() == User.Status.BUSY
					|| u.getmStatus() == User.Status.DO_NOT_DISTURB
					|| u.getmStatus() == User.Status.LEAVE) {
				c++;
			}
		}
		return c;
	}
	
	
	public Set<User> getOnlineUserSet() {
		Set<User> counter = new HashSet<User>();
		this.populateUser(this, counter);
		return counter;
	}

	public int getUserCount() {
		Set<User> counter = new HashSet<User>();
		populateUser(this, counter);
		int count = counter.size();
		counter.clear();
		return count;
	}

	private void populateUser(Group g, Set<User> counter) {
		List<User> lu = g.getUsers();
		for (int i = 0; i < lu.size(); i++) {
			counter.add(lu.get(i));
		}
		List<Group> sGs = g.getChildGroup();
		for (int i = 0; i < sGs.size(); i++) {
			Group subG = sGs.get(i);
			populateUser(subG, counter);
		}
	}

	public void addUserToGroup(List<User> l) {
		synchronized (mLock) {
			for (User u : l) {
				this.users.add(u);
				u.addUserToGroup(this);
			}
		}
	}

	public int getLevel() {
		return level;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (mGId ^ (mGId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (mGId != other.mGId)
			return false;
		return true;
	}

	@Override
	public int compareTo(Group arg0) {
		return 0;
	}

	public abstract String toXml();
}
