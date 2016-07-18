package com.v2tech.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;

public class SPUtil {

	private static final String CONFIG_NAME = "config";

	public SPUtil() {
	}

	public static String getConfigStrValue(Context context, String key) {
		if (context == null) {
			throw new NullPointerException(" context is null");
		}
		SharedPreferences sf = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		return sf.getString(key, null);
	}

	public static int getConfigIntValue(Context context, String key,
			int defaultVal) {
		SharedPreferences sf = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		return sf.getInt(key, defaultVal);
	}

	public static boolean putConfigStrValue(Context context, String key,
			String value) {
		SharedPreferences sf = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		Editor e = sf.edit();
		e.putString(key, value);
		return e.commit();
	}

	public static boolean putConfigIntValue(Context context, String key,
			int value) {
		SharedPreferences sf = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		Editor e = sf.edit();
		e.putInt(key, value);
		return e.commit();
	}

	public static boolean putConfigStrValue(Context context, String[] keys,
			String[] values) {
		if (keys == null || values == null) {
			return false;
		}
		if (keys.length != values.length) {
			throw new RuntimeException(
					" keys's length is different with values's length");
		}
		SharedPreferences sf = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		Editor e = sf.edit();
		for (int index = 0; index < keys.length; index++) {
			e.putString(keys[index], values[index]);
		}
		return e.commit();
	}

	public static String getPath(Context context, Uri uri) {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };

			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection,
						null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {

			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;

	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkCurrentAviNetwork(Context context) {
		if (context == null) {
			throw new NullPointerException("Invalid context object");
		}
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		//need to check mobile !=null, because no mobile network data in PAD
		if (wifi.isConnected() || (mobile != null && mobile.isConnected())) {
			return true;
		} else {
			return false;
		}
	}
	
}
