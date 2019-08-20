package com.walktour.base.util;

import android.content.Context;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 *
 * @author yi.lin
 */
public class DateUtil {
    public final static String FORMAT_DATE_FULL_TIME = "yyyy-MM-dd HH:mm:ss.SSS";
    public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_DATE_TIME2 = "yyyy-MM-dd_HH_mm_ss";
    public final static String FORMAT_DATE_MINUTE = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_DATE = "yyyy-MM-dd";
    public final static String FORMAT_DATE2 = "yyyy年MM月dd日";
    public final static String FORMAT_DATE3 = "yyyy.MM.dd";
    public final static String FORMAT_MONTH_TIME = "MM-dd HH:mm";
    public final static String FORMAT_HOUR_MINUTE = "HH:mm";
    public final static String FORMAT_MONTH_DAY = "MM月dd日";
    public final static String FORMAT_MONTH_DAY1 = "MM/dd";
    public final static String FORMAT_YEAR_MONTH = "MM/yyyy";
    public final static String FORMAT_YEAR_MONTH1 = "yyyy-MM";
    private static final String[] WEEK_STRING_ARR = new String[]{"周日","周一","周二","周三","周四","周五","周六"};

    /**
     * 获取如“01月31日 星期天”格式字符串
     *
     * @param date
     * @return
     */
    public static String getMonthDayWeek(Date date,Context context) {
        String dateStr = "";
        if (null != date) {
            dateStr = DateUtil.formatDate(FORMAT_MONTH_DAY, date)+" "+getDayOfWeekString(context,date);
        }
        return dateStr;
    }


    /**
     * 日期格式化 成 字符串
     *
     * @param format
     * @param date
     * @return
     */
    public static String formatDate(String format, Date date) {
        if (null != date) {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            return formatter.format(date);
        }
        return "";

    }

    public static List<String> getMonthBetween(long minDate, long maxDate,String format) throws ParseException {
        ArrayList<String> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(new Date(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(new Date(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }

        return result;
    }

    /**
     * 解析 日期字符串为Date
     *
     * @param format
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String format, String dateString) throws ParseException {
        if (!TextUtils.isEmpty(dateString)) {
            SimpleDateFormat formater = new SimpleDateFormat(format);
            return formater.parse(dateString);
        }
        return new Date();

    }

    /**
     * 获取星期几
     *
     * @param date
     * @return
     */
    public static int getDayOfWeek(Date date) {
        if (null != date) {
            Calendar ca = getCalendar(date);
            return ca.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return 0;

    }

    /**
     * 获取星期几的文字描述</br>
     *
     * @param date
     * @return
     */
    public static String getDayOfWeekString(Context context, Date date) {
        if (null != date) {
            int dayofweek = getDayOfWeek(date);
            return WEEK_STRING_ARR[dayofweek];
        }
        return "";
    }

    public static Calendar getCalendar(Date date) {
        Calendar ca = Calendar.getInstance();
        if (null != date)
            ca.setTime(date);
        return ca;
    }

    public static int getYear(Date date) {
        if (date != null) {
            Calendar ca = getCalendar(date);
            return ca.get(Calendar.YEAR);
        }
        return 1;

    }

    /**
     * 获取月份  0~11
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        if (null != date) {
            Calendar ca = getCalendar(date);
            return ca.get(Calendar.MONTH);
        }
        return 0;
    }

    /**
     * 获取日期
     *
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date) {
        if (null != date) {
            Calendar ca = getCalendar(date);
            return ca.get(Calendar.DAY_OF_MONTH);
        }
        return 0;
    }

    /**
     * 根据参数 生成date
     *
     * @param year
     * @param monthOfYear 0~11
     * @return
     */
    public Date getDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.YEAR, year);
        ca.set(Calendar.MONTH, monthOfYear);
        ca.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return ca.getTime();
    }

}



