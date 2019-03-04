package com.javaMail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;

public class DateTimeHelper {
	private static final DateTime EPOCH_WIN32 = new DateTime(1601,1,1,0,0); // Windows Time starts from 1/1/1601
	private static final long SECONDS_MILI = 10000L; // LDAP time is count in mili second 
	
	// List of Date format
	public final static String DATE_FORMAT_ddMMyyyy_HHmm = "dd-MM-yyyy HH:mm";
	public final static String DATE_FORMAT_ddMMyyyy = "dd-MM-yyyy";
	
	public final static String DATE_FORMAT_yyyyMMdd_HHmmss = "yyyy-MM-dd HH:mm:ss";
	public final static String DATE_FORMAT_yyyMMdd_HHmm = "yyyy-MM-dd HH:mm";
	public final static String DATE_FORMAT_yyyyMMdd = "yyyy-MM-dd";
	public final static String DATE_FORMAT_yyyMM = "yyyy-MM";
	
	public final static String DATE_FORMAT_HHmmss = "HH:mm:ss";
	public final static String DATE_FORMAT_HHmm = "HH:mm";
	public final static String DATE_FORMAT_HH = "HH";
	public final static String DATE_FORMAT_mm = "mm";

	//Convert from LDAP Time stamp to Date 
	public static Date ldapTimestampToDate( long ldapTime ) {
		return new Date( new DateTime(ldapTime).getMillis() / SECONDS_MILI + EPOCH_WIN32.getMillis());
	}
	
	//Check the Date Different
	public static int daysBetween( Date start, Date end) {
		return Days.daysBetween( new DateTime(start) , new DateTime(end) ).getDays();
	}
	public static int hoursBetween( Date start, Date end) {
		return Hours.hoursBetween( new DateTime(start) , new DateTime(end) ).getHours();
	}
	public static int minutesBetween( Date start, Date end) {
		return Minutes.minutesBetween( new DateTime(start) , new DateTime(end) ).getMinutes();
	}
	
	
	// Add the Date Range
	public static Date addYears( Date date, int unmberOfDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, unmberOfDays);
		return calendar.getTime();
	}
	
	public static Date addMonths( Date date, int unmberOfDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, unmberOfDays);
		return calendar.getTime();
	}
	
	
	public static Date addDays( Date date, int unmberOfDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, unmberOfDays);
		return calendar.getTime();
	}
	
	public static Date addHours( Date date, int unmberOfDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR, unmberOfDays);
		return calendar.getTime();
	}

	public static Date addMinutes( Date date, int unmberOfDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, unmberOfDays);
		return calendar.getTime();
	}
	
	// Method to Parse Date to specific Format
	public static Date parseDate( String date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		try {
			return df.parse(date);
		}catch ( ParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	// Method to format a java date object to a user readable string representation. 
	public static String formatDate( Date date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		try {
			return df.format(date);
		}catch ( Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	// Format Date
	public static String formatDate( Date date) {
		return DateTimeHelper.formatDate(date, DATE_FORMAT_yyyyMMdd_HHmmss ); 
		
	}
	
	// Truncate Date
	public Date truncateTime( Date date ) {
		return DateTimeHelper.parseDate(DateTimeHelper.formatDate(date, DateTimeHelper.DATE_FORMAT_yyyyMMdd), DateTimeHelper.DATE_FORMAT_yyyyMMdd);
	}
	
	// get Date 
	public static Integer getYearMonthDay( Date date, String type) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int result = 0;
		switch ( type.toUpperCase()) {
		case "YEAR":
			result = cal.get(Calendar.YEAR);
			break;
		case "MONTH":
			result = cal.get( Calendar.MONTH) + 1;
			break;
		case "DAY":
			result = cal.get(Calendar.DAY_OF_MONTH);
		}
		return result;
	}

}
