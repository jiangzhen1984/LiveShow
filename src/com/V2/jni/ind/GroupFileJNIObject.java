package com.V2.jni.ind;

public class GroupFileJNIObject extends FileJNIObject {


	public V2Group group;

	public GroupFileJNIObject(V2Group group, String szFileID) {
		super(null, szFileID, "", 0, 0);
		this.group = group;
	}
	
	
}
