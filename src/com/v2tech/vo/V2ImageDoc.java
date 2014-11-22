package com.v2tech.vo;


public class V2ImageDoc extends V2Doc {

	public V2ImageDoc(String id, String docName, Group mGroup, int mBType, User mSharedUser) {
		super(id, docName, mGroup, mBType, mSharedUser);
		this.mDocType = DOC_TYPE_IMAGE;
	}

}
