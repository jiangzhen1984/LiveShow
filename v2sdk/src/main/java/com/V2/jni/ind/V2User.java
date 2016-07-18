package com.V2.jni.ind;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class V2User implements Parcelable {

	public long uid;
	public String name;
	// 2 means non-registered user
	public int type;

	// Server transfer fields
	// 通过OnGetGroupUserInfo传来
	// 登录用的帐号字符串
	public String mAccount;
	public String mAddress;
	public int mAuthtype = 0;// 取值0允许任何人，1需要验证，2不允许任何人
	public Date mBirthday;
	public String mStringBirthday;
	// bsystemavatar='1'
	public String mEmail;
	public String mFax;
	// homepage='http://wenzongliang.com'
	public long mUserId;
	public String mJob;
	public String mMobile;
	// 登录后显示的昵称
	public String mNickName;
	// privacy='0'
	public String mSex;
	public String mSignature;
	public String mTelephone;
	public String mCommentname;

	// group
	public String mCompany;
	public String mDepartment;

	// end Server transfer fields

	public V2User() {

	}

	public V2User(long uid) {
		super();
		this.uid = uid;
	}

	public V2User(long uid, String name) {
		super();
		this.uid = uid;
		this.name = name;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(uid);
		dest.writeString(name);
	}

	public static final Parcelable.Creator<V2User> CREATOR = new Creator<V2User>() {

		@Override
		public V2User[] newArray(int i) {
			return new V2User[i];
		}

		@Override
		public V2User createFromParcel(Parcel parcel) {
			return new V2User(parcel.readLong(), parcel.readString());
		}
	};

	@Override
	public String toString() {
		return " [ID:"+this.uid+" name:"+this.name+"] ";
	}
	
	
	
}
