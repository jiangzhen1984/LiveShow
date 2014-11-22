package com.v2tech.vo;

public class V2GlobalConstants {
	
	/**
	 * Global request type for conference
	 */
	public static final int REQUEST_TYPE_CONF = 1;
	
	/**
	 * Global request type for IM
	 */
	public static final int REQUEST_TYPE_IM = 2;
	
	
	/**
	 * User state for on line
	 */
	public static final int USER_STATUS_ONLINE = 1;

	/**
	 * User state for leaved
	 */
	public static final int USER_STATUS_LEAVING = 2;

	/**
	 * User state for busy
	 */
	public static final int USER_STATUS_BUSY = 3;

	/**
	 * User state for do not disturb
	 */
	public static final int USER_STATUS_DO_NOT_DISTURB = 4;
	
	/**
	 * User state for hidden
	 */
	public static final int USER_STATUS_HIDDEN = 5;

	/**
	 * User state for off line
	 */
	public static final int USER_STATUS_OFFLINE = 0;
	
	
	/**
	 * error conference code for user deleted conference  
	 */
	public static final int CONF_CODE_DELETED = 204;
	
	
	
	/**
	 * Indicate send on line file
	 */
	public static final int FILE_TYPE_ONLINE = 1;
	
	/**
	 * Indicate send off line file
	 */
	public static final int FILE_TYPE_OFFLINE = 2;
	
}
