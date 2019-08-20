package com.walktour.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 日期工具类
 * 
 * @author weirong.fan
 * 
 */
public class DateUtils {
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT = "yyyyMMdd";
	public static final String DATE_FORMAT_2 = "yyyy-MM-dd";
	public static final String DATE_DAY_FORMAT = "dd";
	public static final String DATE_HOUR_FORMAT = "HH";
	public static final String DATE_FORMAT_LOCAL = "MM月dd日";
	public static final DateFormat DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
	public static final DateFormat DATETIME_FORMATTER2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
	public static final DateFormat DATETIME_FORMATTER3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.CHINESE);
	public static final DateFormat DATETIME_FORMATTER_DOT = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss", Locale.CHINESE);
	public static final DateFormat DATE_FORMATTER_NO = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE);
	public static final DateFormat DATE_FORMATTER_DOT = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINESE);
	public static final DateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss", Locale.CHINESE);
	
	public static Date parseDate(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	public static Date parseDate(String dateStr, String parseStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(parseStr);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	public static String formatDate(Date date, String formateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(formateStr);
		return sdf.format(date);
	}
	
	public static String getDay(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_DAY_FORMAT);
		return sdf.format(date);
	}
	
	public static String getHour(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_HOUR_FORMAT);
		return sdf.format(date);
	}
	
	public static String parseToLocalStr(String dayInfo, String formateStr) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(formateStr);
			Date day = sdf.parse(dayInfo);
			sdf.applyPattern(DATE_FORMAT_LOCAL);
			return sdf.format(day);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 取得本周一日期
	 * 
	 * @return
	 */
	public static Date getCurrWeekMonday() {
		Calendar calendar = Calendar.getInstance();
		int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		calendar.add(Calendar.DATE, -day_of_week + 1);
		return calendar.getTime();
	}
	
	/**
	 * 取得本周日日期
	 * 
	 * @return
	 */
	public static Date getCurrWeekSunday() {
		Calendar calendar = Calendar.getInstance();
		int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		calendar.add(Calendar.DATE, -day_of_week + 7);
		return calendar.getTime();
	}
	
	/**
	 * 获取本月的重置日
	 * 
	 * @return
	 */
	public static Date getCurrMonthResetDay(int resetDay) {
		Calendar calendar = Calendar.getInstance();
		int nowDay = calendar.get(Calendar.DAY_OF_MONTH);
		if (resetDay > nowDay) {
			if (resetDay > getLastMonthMaxDay()) {
				resetDay = getLastMonthMaxDay() + 1;
			}
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DAY_OF_MONTH, resetDay);
		} else {
			calendar.set(Calendar.DAY_OF_MONTH, resetDay);
		}
		return calendar.getTime();
	}
	
	/**
	 * 获取下个月的重置日
	 * 
	 * @return
	 */
	public static Date getNextMonthResetDay(int resetDay) {
		Calendar calendar = Calendar.getInstance();
		int nowDay = calendar.get(Calendar.DAY_OF_MONTH);
		if (resetDay > nowDay) {
			if (resetDay > getCurrMonthMaxDay()) {
				resetDay = getCurrMonthMaxDay() + 1;
			}
			calendar.set(Calendar.DAY_OF_MONTH, resetDay);
		} else {
			if (resetDay > getNextMonthMaxDay()) {
				resetDay = getNextMonthMaxDay() + 1;
			}
			calendar.add(Calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH, resetDay);
		}
		return calendar.getTime();
	}
	
	public static int getLastMonthMaxDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getCurrMonthMaxDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getNextMonthMaxDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public static String[] getTodayAndTomorrow() {
		String[] days = new String[2];
		Calendar now = Calendar.getInstance();
		days[0] = formatDate(now.getTime(), DATE_FORMAT);
		
		now.add(Calendar.DAY_OF_MONTH, 1);
		days[1] = formatDate(now.getTime(), DATE_FORMAT);
		return days;
	}
	
	/**
	 * 取当前日期时间
	 * 
	 * @return
	 */
	public static String getCurrentDateTime() {
		try {
			return DATETIME_FORMATTER.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 取当前日期时间
	 * 
	 * @return
	 */
	public static String getCurrentDateTime2() {
		try {
			return DATETIME_FORMATTER2.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 取当前日期时间
	 * 
	 * @return
	 */
	public static String getCurrentDateTime3() {
		try {
			return DATETIME_FORMATTER3.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 取当前日期时间：时-分-秒
	 * 
	 * @return
	 */
	public static String getCurrentTimes() {
		try {
			return TIME_FORMATTER.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取当前日期的毫秒数
	 * 
	 * @return
	 */
	public static long getTimeByNow() {
		Date lastupdate = new Date();
		return lastupdate.getTime();
	}
	
	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public static String getDateTimeToString() {
		try {
			return DATE_FORMATTER_NO.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static long getDayStartTime() {
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int mon = ca.get(Calendar.MONTH);
		int day = ca.get(Calendar.DAY_OF_MONTH);
		
		Calendar ca1 = Calendar.getInstance();
		ca1.set(year, mon, day, 0, 0, 0);
		
		String timeMsStr = String.valueOf(ca1.getTimeInMillis());
		int length = timeMsStr.length();
		timeMsStr = timeMsStr.substring(0, length - 3);
		timeMsStr = timeMsStr + "000";
		long startTimeOfday = Long.parseLong(timeMsStr);
		return startTimeOfday;
	}
	 /**
     * 当月第一天 包含时分秒
     * @return
     */
	public static String getFirstDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();
        
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(day_first).append(" 00:00:00");
        return str.toString();

    }
    public static String getFirstDay2() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();
        
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first = df.format(gcLast.getTime()); 
        return day_first.toString();

    }
    /**
     * 当月最后一天 包含时分秒
     * @return
     */
    public static String getLastDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();
        String s = df.format(theDate);
        StringBuffer str = new StringBuffer().append(s).append(" 23:59:59");
        return str.toString();

    }
    public static String getLastDay2() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();
        String s = df.format(theDate);
        return s.toString();

    }
    public static  String transferLongToDate(String dateFormat,Long millSec){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date= new Date(millSec);
               return sdf.format(date);
       }

}
