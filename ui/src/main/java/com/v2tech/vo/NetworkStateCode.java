package com.v2tech.vo;

import android.os.Parcel;
import android.os.Parcelable;

public enum NetworkStateCode  implements Parcelable {
	CONNECTED(0), INCORRECT_INFO(1), TIME_OUT(-1), CONNECTED_ERROR(301), UNKNOW_CODE(-3);

	private int code;

	private NetworkStateCode(int code) {
		this.code = code;
	}
	
	public int intValue() {
		return code;
	}

	public static NetworkStateCode fromInt(int code) {
		switch (code) {
		case 0:
			return CONNECTED;
		case 1:
			return INCORRECT_INFO;
		case -1:
			return TIME_OUT;
		case 301:
			return CONNECTED_ERROR;
		default:
			return UNKNOW_CODE;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(code);
	}
	
	
	 public static final Parcelable.Creator<NetworkStateCode> CREATOR = new Parcelable.Creator<NetworkStateCode>() {

		@Override
		public NetworkStateCode createFromParcel(Parcel source) {
			return NetworkStateCode.fromInt(source.readInt());
		}

		@Override
		public NetworkStateCode[] newArray(int size) {
			return new NetworkStateCode[size];
		}
		
     };
	
	
	
	
	
}