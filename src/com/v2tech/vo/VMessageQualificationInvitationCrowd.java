package com.v2tech.vo;

/**
 * Crowd invitation message
 * @author jiangzhen
 *
 */
public class VMessageQualificationInvitationCrowd extends  VMessageQualification {
	
	//Most time invitation user same with crowd owner
	protected User mInvitationUser;
	protected User mBeInvitatonUser;
	private CrowdGroup mCrowdGroup;

	public VMessageQualificationInvitationCrowd(CrowdGroup crowdGroup, 
			User beInvitationUser) {
		super(VMessageQualification.Type.CROWD_INVITATION);
		this.mCrowdGroup = crowdGroup;
		this.mBeInvitatonUser = beInvitationUser;
		this.mInvitationUser = this.mCrowdGroup.getOwnerUser();
	}

	
	public CrowdGroup getCrowdGroup() {
		return mCrowdGroup;
	}

	public void setCrowdGroup(CrowdGroup crowdGroup) {
		this.mCrowdGroup = crowdGroup;
	}


	public User getInvitationUser() {
		return mInvitationUser;
	}


	public void setInvitationUser(User invitationUser) {
		this.mInvitationUser = invitationUser;
	}


	public User getBeInvitatonUser() {
		return mBeInvitatonUser;
	}


	public void setBeInvitatonUser(User beInvitatonUser) {
		this.mBeInvitatonUser = beInvitatonUser;
	}
	
	
	
	
}
