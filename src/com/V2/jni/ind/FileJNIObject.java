package com.V2.jni.ind;


import android.os.Parcel;
import android.os.Parcelable;

public class FileJNIObject extends JNIObjectInd implements Parcelable {

	public V2User user;
	public String fileId;
	public String fileName;
	public long fileSize;
	public int fileType;
	public int linetype;

	// For crowd file type
	public String url;

	public FileJNIObject(V2User user, String szFileID, String szFileName,
			long nFileBytes, int linetype) {
		this(user, szFileID, szFileName, nFileBytes, linetype, null);
	}

	public FileJNIObject(Parcelable user, String szFileID, String szFileName,
			long nFileBytes, int linetype, int fileType , String url) {
		this.user = (V2User) user;
		this.fileId = szFileID;
		this.fileName = szFileName;
		this.fileSize = nFileBytes;
		this.fileType = fileType;
		this.linetype = linetype;
		this.mType = JNIIndType.FILE;
		this.url = url;
	}

	/**
	 * 
	 * @param user
	 * @param fileId
	 * @param fileName
	 * @param fileSize
	 * @param linetype
	 * 			 2: offline file 1: online file
	 * @param url
	 */
	public FileJNIObject(V2User user, String fileId, String fileName,
			long fileSize, int linetype, String url) {
		super();
		this.user = user;
		this.fileId = fileId;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.linetype = linetype;
		this.url = url;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(user, 0);
		dest.writeString(fileId);
		dest.writeString(fileName);
		dest.writeLong(fileSize);
		dest.writeInt(fileType);
		dest.writeInt(linetype);
		dest.writeString(url);
	}

	public static final Parcelable.Creator<FileJNIObject> CREATOR = new Creator<FileJNIObject>() {

		@Override
		public FileJNIObject[] newArray(int i) {
			return new FileJNIObject[i];
		}

		@Override
		public FileJNIObject createFromParcel(Parcel parcel) {
			return new FileJNIObject(parcel.readParcelable(V2User.class
					.getClassLoader()), parcel.readString(),
					parcel.readString(), parcel.readLong(), parcel.readInt(),
					parcel.readInt(), parcel.readString());
		}
	};
}
