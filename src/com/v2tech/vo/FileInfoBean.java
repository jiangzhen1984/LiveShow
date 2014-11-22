package com.v2tech.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class FileInfoBean implements Parcelable {

	public String fileName;
	// public String fileDate;
	// public String fileSize;
	// public String fileItmes;
	public String filePath;
	public long fileSize;
	public boolean isDir;
	public int isCheck;
	public int fileType;
	public String fileUUID;

	public FileInfoBean() {
		super();
	}

	// public FileInfoBean(String fileName, String fileDate, String fileSize,
	// String fileItmes, String filePath) {
	// this.fileName = fileName;
	// this.filePath = filePath;
	// this.fileDate = fileDate;
	// this.fileSize = fileSize;
	// this.fileItmes = fileItmes;
	// }

	public FileInfoBean(String fileName, String filePath, long fileSize,
			int isCheck, int fileType , String fileUUID) {
		super();
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileSize = fileSize;
		this.isCheck = isCheck;
		this.fileType = fileType;
		this.fileUUID = fileUUID;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(fileName);
		dest.writeString(filePath);
		dest.writeLong(fileSize);
		dest.writeInt(isCheck);
		dest.writeInt(fileType);
		dest.writeString(fileUUID);
		// dest.writeString(fileDate);
		// dest.writeString(fileSize);
		// dest.writeString(fileItmes);
	}

	public static final Parcelable.Creator<FileInfoBean> CREATOR = new Creator<FileInfoBean>() {

		@Override
		public FileInfoBean[] newArray(int i) {
			return new FileInfoBean[i];
		}

		@Override
		public FileInfoBean createFromParcel(Parcel parcel) {
			return new FileInfoBean(parcel.readString(), parcel.readString(),
					parcel.readLong(), parcel.readInt(), parcel.readInt() , parcel.readString());
		}
	};
}
