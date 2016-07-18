package com.V2.jni.ind;


public class GroupQualicationJNIObject {

	public int groupType;
	public long groupID;
	public long userID;
	public int qualicationType;
	public int state;
	public String reason;
	
	public GroupQualicationJNIObject(int groupType, long groupID, long userID,
			int qualicationType, int state, String reason) {
		super();
		this.groupType = groupType;
		this.groupID = groupID;
		this.userID = userID;
		this.qualicationType = qualicationType;
		this.state = state;
		this.reason = reason;
	}

}
