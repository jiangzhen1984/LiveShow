package com.v2tech.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.format.DateUtils;

public class DateUtil {

	public static String getStringDate(long longDate) {
			Date dates = new Date(longDate);
			SimpleDateFormat format = null;
			if(DateUtils.isToday(longDate)){
				format = new SimpleDateFormat("HH:mm:ss");
				return format.format(dates.getTime());
			}
			
			Calendar cale = Calendar.getInstance();
			cale.setTime(dates);
			Calendar currentCale = Calendar.getInstance();
			int days = cale.get(Calendar.DAY_OF_MONTH);
			int currentCaleDays = currentCale.get(Calendar.DAY_OF_MONTH);
			if(currentCaleDays - 1 == days){
				format = new SimpleDateFormat("HH:mm:ss");
				return "昨天  " + format.format(dates);
			}
			
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.format(dates.getTime());
	}
	
	public static String getDates(long mTimeLine) {
		
		mTimeLine = mTimeLine / 1000;
		
		int hour = (int) mTimeLine / 3600;

		int minute = (int) (mTimeLine - (hour * 3600)) / 60;

		int second = (int) mTimeLine - (hour * 3600 + minute * 60);
		return (hour < 10 ? "0" + hour : hour) + ":"
				+ (minute < 10 ? "0" + minute : minute) + ":"
				+ (second < 10 ? "0" + second : second);
	}
	
	/**
	 * get standard date time , like 2014-09-01 14:20:22
	 * @return
	 */
	public static String getStandardDate(Date date){
		
		if(date == null)
			throw new RuntimeException("Given date object is null...");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date.getTime());
	} 
}
