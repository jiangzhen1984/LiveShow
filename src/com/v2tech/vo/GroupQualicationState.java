package com.v2tech.vo;

import com.v2tech.vo.VMessageQualification.QualificationState;
import com.v2tech.vo.VMessageQualification.ReadState;
import com.v2tech.vo.VMessageQualification.Type;


public class GroupQualicationState {

	public Type qualicationType;
	public QualificationState state;
	public String applyReason;
	public String refuseReason;
	public ReadState readState;
	public boolean isOwnerGroup;

	public GroupQualicationState(Type qualicationType,
			QualificationState state, String reason , ReadState readState , boolean isOwnerGroup) {
		super();
		this.qualicationType = qualicationType;
		this.state = state;
		this.readState = readState;
		this.isOwnerGroup = isOwnerGroup;
		switch (state) {
		case ACCEPTED:
		case BE_ACCEPTED:
		case BE_REJECT:
			this.applyReason = reason;
			break;
		case REJECT:
			this.refuseReason = reason;
			break;
		default:
			break;
		}
	}
}
