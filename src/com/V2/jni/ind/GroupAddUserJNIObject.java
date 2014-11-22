package com.V2.jni.ind;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupAddUserJNIObject extends JNIObjectInd implements Parcelable {

	private int groupType;
	private long groupID;
	private long userID;

	public GroupAddUserJNIObject(Parcel in) {
		if (in != null) {
			mType = JNIIndType.values()[in.readInt()];
			groupType = in.readInt();
			groupID = in.readLong();
			userID = in.readLong();
		}
	}

	public GroupAddUserJNIObject(int groupType, long groupID, long userID , String userInfos) {
		super();
		mType = JNIIndType.GROUP;
		this.groupType = groupType;
		this.groupID = groupID;
		this.userID = userID;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(JNIIndType.GROUP.ordinal());
		dest.writeInt(groupType);
		dest.writeLong(groupID);
		dest.writeLong(userID);
	}

	public static final Parcelable.Creator<GroupAddUserJNIObject> CREATOR = new Parcelable.Creator<GroupAddUserJNIObject>() {
		public GroupAddUserJNIObject createFromParcel(Parcel in) {
			return new GroupAddUserJNIObject(in);
		}

		public GroupAddUserJNIObject[] newArray(int size) {
			return new GroupAddUserJNIObject[size];
		}
	};

	public int getGroupType() {
		return groupType;
	}

	public void setGroupType(int groupType) {
		this.groupType = groupType;
	}

	public long getGroupID() {
		return groupID;
	}

	public void setGroupID(long groupID) {
		this.groupID = groupID;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}
	
}
