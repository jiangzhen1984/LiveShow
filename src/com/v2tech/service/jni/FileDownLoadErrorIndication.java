package com.v2tech.service.jni;


public class FileDownLoadErrorIndication extends FileTransStatusIndication {

	public static final int TYPE_SEND = 1;
	public static final int TYPE_DOWNLOAD = 2;
	public FileDownLoadErrorIndication(String uuid,int errorCode, int nTransType) {
		super(IND_TYPE_DOWNLOAD_ERR, nTransType, uuid , errorCode);
	}
}
