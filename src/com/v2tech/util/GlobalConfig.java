package com.v2tech.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.SparseArray;


public class GlobalConfig {

	public static final String KEY_LOGGED_IN = "LoggedIn";

	public static int GLOBAL_DPI = DisplayMetrics.DENSITY_XHIGH;

	public static int GLOBAL_VERSION_CODE = 1;

	public static String GLOBAL_VERSION_NAME = "1.3.0.1";

	public static double SCREEN_INCHES = 0;

	public static boolean isConversationOpen = false;

	public static long SERVER_TIME = 0;
	public static long LOCAL_TIME = 0;

	public static String DEFAULT_GLOBLE_PATH = "";
	
	public static String SDCARD_GLOBLE_PATH = "";
	
	public static String LOGIN_USER_ID = "";

	public static HashMap<String, String> allChinese = new HashMap<String, String>();
	



	public static void saveLogoutFlag(Context context) {
		SPUtil.putConfigIntValue(context, KEY_LOGGED_IN, 0);
	}

	
	public static String getGlobalPath() {
			// --mnt/sdcard
		return SDCARD_GLOBLE_PATH + "/vl";
	}
	
	public static String getGlobalUserAvatarPath() {
		return getGlobalPath() + "/Users/"
				+ LOGIN_USER_ID + "/avatars";
	}

	public static String getGlobalPicsPath() {
		return getGlobalPath() + "/Users/"
				+ LOGIN_USER_ID + "/Images";
	}

	public static String getGlobalAudioPath() {
		return getGlobalPath() + "/Users/"
				+ LOGIN_USER_ID + "/audios";
	}

	public static String getGlobalFilePath() {
		return getGlobalPath() + "/Users/"
				+ LOGIN_USER_ID + "/files";
	}
	
	public static String getGlobalDataBasePath() {
		return getGlobalPath() + "/Users/" + LOGIN_USER_ID;
	}
	
	public static String getGlobalCrashPath() {
		return getGlobalPath() + "/cl/";
	}

	public static long getGlobalServerTime() {
		return (((System.currentTimeMillis() - GlobalConfig.LOCAL_TIME) / 1000) + GlobalConfig.SERVER_TIME) * 1000;
	}
	
	public static final String DEFAULT_CONFIG_FILE = "v2platform.cfg";
	public static final String DEFAULT_CONFIG_LOG_FILE = "log_options.xml";
	public static final String DATA_SAVE_FILE_NAME = "CommBar";

	public static String getGlobalRootPath() {
		boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
		if (!sdExist) {// 如果不存在,
			// --data/data/com.v2tech
			return DEFAULT_GLOBLE_PATH + File.separator + DATA_SAVE_FILE_NAME;
		} else {
			// --mnt/sdcard
			return SDCARD_GLOBLE_PATH + File.separator + DATA_SAVE_FILE_NAME;
		}
	}

}
