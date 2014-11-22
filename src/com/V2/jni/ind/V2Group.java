package com.V2.jni.ind;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.V2.jni.V2GlobalEnum;

public class V2Group {
	
	
	
	/**
	 * Organization type
	 */
	public static final int TYPE_ORG = V2GlobalEnum.GROUP_TYPE_DEPARTMENT;
	
	/**
	 * Contacts group type
	 */
	public static final int TYPE_CONTACTS_GROUP = V2GlobalEnum.GROUP_TYPE_CONTACT;
	
	/**
	 * Crowd type
	 */
	public static final int TYPE_CROWD = V2GlobalEnum.GROUP_TYPE_CROWD;
	
	/**
	 * Conference type
	 */
	public static final int TYPE_CONF = V2GlobalEnum.GROUP_TYPE_CONFERENCE;
	
	public long id;
	public String name;
	public int type;
	public V2User owner;
	
	public V2Group parent;
	public Set<V2Group> childs = new HashSet<V2Group>();
	public List<V2User> members;
	
	//for conference
	public Date createTime;
	public V2User chairMan;
	public boolean isSync;
	
	//for crowd 
	public V2User creator;
	public int authType;
	public String brief;
	public String announce;
	
	//for contact group
	public boolean isDefault;
	
	
	public V2Group(int type) {
		super();
		this.type = type;
	}
	
	public V2Group(long id, int type) {
		super();
		this.id = id;
		this.type = type;
	}
	
	
	public V2Group(long id, String name, int type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}


	public V2Group(long id, String name, int type, V2User owner) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.owner = owner;
	}


	public V2Group(long id, String name, int type, V2User owner, Date createTime) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.owner = owner;
		this.createTime = createTime;
	}
	
	
	public V2Group(long id, String name, int type, V2User owner, Date createTime, V2User chairMan) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.owner = owner;
		this.createTime = createTime;
		this.chairMan = chairMan;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		V2Group other = (V2Group) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	
	

}
