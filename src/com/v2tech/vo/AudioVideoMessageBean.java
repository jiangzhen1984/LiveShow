package com.v2tech.vo;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

public class AudioVideoMessageBean  implements Parcelable , Comparable<AudioVideoMessageBean>{

	public static final int TYPE_AUDIO = 0;
	public static final int TYPE_VIDEO = 1;
	public static final int TYPE_ALL = 2;
	
	public static final int STATE_CALL_OUT = 3; 
	public static final int STATE_CALL_IN = 4; 
	
	public static final int STATE_ANSWER_CALL = 5; 
	public static final int STATE_NO_ANSWER_CALL = 6;
	
	public static final int STATE_READED = 7;
	public static final int STATE_UNREAD = 8;
	
	public static final int REPLY_REJECT = 9;
	public static final int REPLY_ACCEPT = 10;
	
	public AudioVideoMessageBean() {
		super();
	}
	
	public String name;
	public long holdingTime;
	public long fromUserID;
	public long toUserID;
	public long remoteUserID;
	public int callNumbers;
	public int mediaType;
	public int meidaState;
	public int readState;
	public int isCallOut; //是否是主动拨出去
	public ArrayList<ChildMessageBean> mChildBeans = new ArrayList<ChildMessageBean>();
	public boolean isCheck;
	public ImageView userIcon;
	
	public AudioVideoMessageBean(String name, long holdingTime,
			long fromUserID, long toUserID, long remoteUserID, int callNumbers,
			int mediaType, int readState , int isCallOut , int meidaState) {
		super();
		this.name = name;
		this.holdingTime = holdingTime;
		this.fromUserID = fromUserID;
		this.toUserID = toUserID;
		this.remoteUserID = remoteUserID;
		this.callNumbers = callNumbers;
		this.mediaType = mediaType;
		this.readState = readState;
		this.meidaState = meidaState;
		this.isCallOut = isCallOut;
	}

	
	public static class ChildMessageBean implements Parcelable{
		
		public int childMediaType;
		public int childReadState;
		public int childMediaState;
		public long childHoldingTime;
		public long childSaveDate;
		public int childISCallOut; //是否是主动拨出去
		
		@Override
		public int describeContents() {
			return 0;
		}
		
		public ChildMessageBean(){};

		public ChildMessageBean(int childMediaType, int childReadState, int childMediaState,
				long childHoldingTime, long childSaveDate , int childISCallOut) {
			super();
			this.childMediaType = childMediaType;
			this.childReadState = childReadState;
			this.childMediaState = childMediaState;
			this.childHoldingTime = childHoldingTime;
			this.childSaveDate = childSaveDate;
			this.childISCallOut = childISCallOut;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {

			dest.writeInt(childMediaType);
			dest.writeInt(childReadState);
			dest.writeInt(childMediaState);
			dest.writeLong(childHoldingTime);
			dest.writeLong(childSaveDate);
			dest.writeInt(childISCallOut);
		}
		
		public static final Parcelable.Creator<ChildMessageBean> CREATOR = new Creator<ChildMessageBean>() {

			@Override
			public ChildMessageBean[] newArray(int i) {
				return new ChildMessageBean[i];
			}

			@Override
			public ChildMessageBean createFromParcel(Parcel parcel) {
				return new ChildMessageBean(parcel.readInt(), parcel.readInt(), parcel.readInt(),parcel.readLong(),parcel.readLong(), parcel.readInt());
			}
		};
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(name);
		dest.writeLong(holdingTime);
		dest.writeLong(fromUserID);
		dest.writeLong(toUserID);
		dest.writeLong(remoteUserID);
		dest.writeInt(callNumbers);
		dest.writeInt(mediaType);
		dest.writeInt(readState);
		dest.writeInt(meidaState);
		dest.writeInt(isCallOut);
	}
	
	public static final Parcelable.Creator<AudioVideoMessageBean> CREATOR = new Creator<AudioVideoMessageBean>() {

		@Override
		public AudioVideoMessageBean[] newArray(int i) {
			return new AudioVideoMessageBean[i];
		}

		@Override
		public AudioVideoMessageBean createFromParcel(Parcel parcel) {
			return new AudioVideoMessageBean(parcel.readString(),
					parcel.readLong(),parcel.readLong(),parcel.readLong(),parcel.readLong(),
					parcel.readInt(), parcel.readInt(), parcel.readInt() , parcel.readInt() , parcel.readInt());
		}
	};

	@Override
	public int compareTo(AudioVideoMessageBean another) {
		if(mChildBeans.size() <=0 || another.mChildBeans.size() <= 0)
			return 0;
		
		ChildMessageBean loaclChild = mChildBeans.get(0);
		ChildMessageBean childMessageBean = another.mChildBeans.get(0);
		if (loaclChild.childSaveDate > childMessageBean.childSaveDate) 
			return -1;
		else
			return 1;
	}
}
