package com.v2tech.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class Crowd implements Parcelable {

	private long id;
	private String name;
	private User creator;
	private String brief;
	private String announce;
	private int auth;

	public Crowd(CrowdGroup cg) {
		this(cg.getmGId(), cg.getOwnerUser(), cg.getName(), cg.getBrief());
	}

	public Crowd(long id, User creator, String name, String brief) {
		this.id = id;
		this.creator = creator;
		this.name = name;
		this.brief = brief;
		this.auth = 0;
	}

	public Crowd(Parcel par) {
		id = par.readLong();
		name = par.readString();
		long uid = par.readLong();
		String uname = par.readString();
		creator = new User(uid, uname);
		brief = par.readString();
		if (brief == null) {
			brief = "";
		}
		auth = par.readInt();
	}

	public static final Parcelable.Creator<Crowd> CREATOR = new Parcelable.Creator<Crowd>() {
		public Crowd createFromParcel(Parcel in) {
			return new Crowd(in);
		}

		public Crowd[] newArray(int size) {
			return new Crowd[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel par, int flag) {
		par.writeLong(id);
		par.writeString(name);
		par.writeLong(creator.getmUserId());
		par.writeString(creator.getName());
		par.writeString(brief);
		par.writeInt(auth);
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
		Crowd other = (Crowd) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAnnounce() {
		return announce;
	}

	public void setAnnounce(String announce) {
		this.announce = announce;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public long getId() {
		return this.id;
	}

	public int getAuth() {
		return auth;
	}

	public void setAuth(int auth) {
		this.auth = auth;
	}

	public String toXml() {
		StringBuffer sb = new StringBuffer();
		sb.append("<crowd id=\"" + this.id + "\" name=\"" + this.name
				+ "\" authtype='" + this.auth + "' creatoruserid='"
				+ this.creator.getmUserId() + "' summary='" + this.brief
				+ "' />");
		return sb.toString();
	}

}
