package com.v2tech.vo;

/**
 * Crowd application message
 * @author jiangzhen
 *
 */
public class VMessageQualificationApplicationCrowd extends  VMessageQualification {
	
	private CrowdGroup mCrowdGroup;
	private User mApplicant;
	private String mApplyReason; 

	public VMessageQualificationApplicationCrowd(CrowdGroup crowdGroup, User applicant) {
		super(VMessageQualification.Type.CROWD_APPLICATION);
		this.mCrowdGroup = crowdGroup;
		this.mApplicant = applicant;
	}

	
	public CrowdGroup getCrowdGroup() {
		return mCrowdGroup;
	}

	public void setCrowdGroup(CrowdGroup crowdGroup) {
		this.mCrowdGroup = crowdGroup;
	}
	
	
	public String getApplyReason() {
		return mApplyReason;
	}


	public void setApplyReason(String applyReason) {
		this.mApplyReason = applyReason;
	}


	public User getApplicant() {
		return mApplicant;
	}


	public void setApplicant(User applicant) {
		this.mApplicant = applicant;
	}

	
	
}
