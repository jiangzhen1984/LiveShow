package com.v2tech.vo;

/**
 * It's enumeration definition.
 * 
 * @see ConferencePermission
 * @author 28851274
 * 
 */
public enum PermissionState {
	NORMAL(1), APPLYING(2), GRANTED(3), UNKNOWN(-1);

	private int code;

	private PermissionState(int code) {
		this.code = code;
	}

	public int intValue() {
		return code;
	}

	public static PermissionState fromInt(int i) {
		switch (i) {
		case 1:
			return NORMAL;
		case 2:
			return APPLYING;
		case 3:
			return GRANTED;
		default:
			return UNKNOWN;

		}
	}
}
