package com.V2.jni.ind;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupJoinErrorJNIObject extends JNIObjectInd implements Parcelable{

	private int groupType;
	private long groupID;
	private int errorCode;
	
	public GroupJoinErrorJNIObject(Parcel in) {
		if (in != null) {
			mType = JNIIndType.values()[in.readInt()];  
			groupType = in.readInt();
			groupID = in.readLong();
			errorCode = in.readInt();
		}
	}
	
	public GroupJoinErrorJNIObject(int groupType , long groupID , int errorCode){
		super();
		mType = JNIIndType.GROUP;
		this.groupType = groupType;
		this.groupID = groupID;
		this.errorCode = errorCode;
	}
	
	public int getGroupType() {
		return groupType;
	}

	public long getGroupID() {
		return groupID;
	}

	public int getErrorCode() {
		return errorCode;
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
		dest.writeInt(errorCode);
	}
	
	public static final Parcelable.Creator<GroupJoinErrorJNIObject> CREATOR = new Parcelable.Creator<GroupJoinErrorJNIObject>() {
		public GroupJoinErrorJNIObject createFromParcel(Parcel in) {
			return new GroupJoinErrorJNIObject(in);
		}

		public GroupJoinErrorJNIObject[] newArray(int size) {
			return new GroupJoinErrorJNIObject[size];
		}
	};
}
