package com.V2.jni.ind;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupFileJNIObject extends FileJNIObject {


	public V2Group group;

	public GroupFileJNIObject(V2Group group, String szFileID) {
		super(null, szFileID, "", 0, 0);
		this.group = group;
	}


	public GroupFileJNIObject(String szFileID) {
		super(null, szFileID, "", 0, 0);
	}

	@Override
	public int describeContents() {
		return super.describeContents();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
	}


	public static final Parcelable.Creator<GroupFileJNIObject> CREATOR = new Creator<GroupFileJNIObject>() {

		@Override
		public GroupFileJNIObject[] newArray(int i) {
			return new GroupFileJNIObject[i];
		}

		@Override
		public GroupFileJNIObject createFromParcel(Parcel parcel) {
			return new GroupFileJNIObject( parcel.readString());
		}
	};
}
