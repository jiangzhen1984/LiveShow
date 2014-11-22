package com.v2tech.vo;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class SearchedResult implements Parcelable {

	
	private List<SearchedResultItem> mList;
	
	public SearchedResult() {
		mList  = new ArrayList<SearchedResultItem>();
	}
	
	public SearchedResult(Parcel in) {
		mList  = new ArrayList<SearchedResultItem>();
		if (in != null) {
			in.readList(mList, this.getClass().getClassLoader());
		}
	}
	
	
	
	@Override
	public int describeContents() {
		return 0;
	}




	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(mList);
	}

	
	public static final Parcelable.Creator<SearchedResult> CREATOR = new Parcelable.Creator<SearchedResult>() {
		public SearchedResult createFromParcel(Parcel in) {
			return new SearchedResult(in);
		}

		public SearchedResult[] newArray(int size) {
			return new SearchedResult[size];
		}
	};



	public void addItem(Type type, long id, String name) {
		if (id < 0 || TextUtils.isEmpty(name)) {
			return;
		}
		mList.add(new SearchedResultItem(type, id, name));
	}
	
	
	public void addCrowdItem(long id, String name, User creator, String brief, int authType) {
		if (id < 0 || TextUtils.isEmpty(name) || creator == null) {
			return;
		}
		SearchedResultItem item = new SearchedResultItem(Type.CROWD, id, name);
		item.creator = creator.getmUserId();
		item.creatorName= creator.getName();
		item.brief = brief;
		item.authType = authType;
		mList.add(item);
	}
	
	
	public List<SearchedResultItem> getList() {
		return mList;
	}
	
	public static class SearchedResultItem  implements Parcelable {
		public Type mType;
		public long id;
		public String name;
		public long creator;
		public String creatorName;
		public String brief;
		public int authType;
		
		public SearchedResultItem(Type mType, long id, String name) {
			super();
			this.mType = mType;
			this.id = id;
			this.name = name;
		}
		
		
		public SearchedResultItem(Parcel in) {
			if (in != null) {
				int t = in.readInt();
				id = in.readLong();
				this.name = in.readString();
				this.creator = in.readLong();
				this.creatorName = in.readString();
				this.brief = in.readString();
				this.authType = in.readInt();
				if (t == Type.CROWD.ordinal()) {
					mType  = Type.CROWD;
				} else if (t == Type.USER.ordinal()) {
					mType  = Type.USER;
				}
			}
		}
		
		
		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(mType.ordinal());
			dest.writeLong(id);
			dest.writeString(name);
			dest.writeLong(creator);
			dest.writeString(creatorName);
			dest.writeString(brief);
			dest.writeInt(authType);
		}
		
		
		public static final Parcelable.Creator<SearchedResultItem> CREATOR = new Parcelable.Creator<SearchedResultItem>() {
			public SearchedResultItem createFromParcel(Parcel in) {
				return new SearchedResultItem(in);
			}

			public SearchedResultItem[] newArray(int size) {
				return new SearchedResultItem[size];
			}
		};
		
		
		
	}
	
	
	public enum Type {
		USER,CROWD;
	}
}
