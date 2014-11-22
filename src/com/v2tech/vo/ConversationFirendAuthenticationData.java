package com.v2tech.vo;


public class ConversationFirendAuthenticationData extends Conversation {

	private User user;
	private Group group;
	private VerificationMessageType messageType;
	private VMessageQualification qualification;
	private AddFriendHistorieNode friendNode;

	public ConversationFirendAuthenticationData(int mType, long mExtId) {
		super(mType, mExtId);
		messageType = VerificationMessageType.CONTACT_TYPE;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public VerificationMessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(VerificationMessageType messageType) {
		this.messageType = messageType;
	}
	
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	
	public VMessageQualification getQualification() {
		return qualification;
	}

	public void setQualification(VMessageQualification qualification) {
		this.qualification = qualification;
	}
	
	public AddFriendHistorieNode getFriendNode() {
		return friendNode;
	}

	public void setFriendNode(AddFriendHistorieNode friendNode) {
		this.friendNode = friendNode;
	}
	
	public enum VerificationMessageType {
		CROWD_TYPE(0), CONTACT_TYPE(1), UNKNOWN(2);

		private int type;

		private VerificationMessageType(int type) {
			this.type = type;
		}

		public static VerificationMessageType fromInt(int code) {
			switch (code) {
			case 0:
				return CROWD_TYPE;
			case 1:
				return CONTACT_TYPE;
			default:
				return UNKNOWN;

			}
		}

		public int intValue() {
			return type;
		}
	}
}
