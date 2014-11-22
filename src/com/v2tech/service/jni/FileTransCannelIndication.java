package com.v2tech.service.jni;

public class FileTransCannelIndication extends FileTransStatusIndication {

	public FileTransCannelIndication(String uuid) {
		super(IND_TYPE_TRANS_CANNEL, 1, uuid);
	}
}
