package com.v2tech.vo;

/**
 * It's enumeration definition. Used to delegate permission of conference.
 * 
 * @author 28851274
 * 
 */
public enum ConferencePermission {
	//
	UNKOWN(0),
	// 1 mean user want to control in meeting
	CONTROL(1),
	// 3 means speaking permission, this number is defined by server side.
	SPEAKING(3);

	private int code;

	private ConferencePermission(int code) {
		this.code = code;
	}

	public int intValue() {
		return code;
	}

	public static ConferencePermission fromInt(int i) {
		switch (i) {
		case 1:
			return CONTROL;
		case 3:
			return SPEAKING;
		default:
			return UNKOWN;

		}
	}
}
