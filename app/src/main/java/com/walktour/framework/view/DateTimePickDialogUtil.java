package com.walktour.framework.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 自定义时间选择器
 * zhihui.lian
 */
@SuppressLint("InflateParams")
public class DateTimePickDialogUtil implements OnDateChangedListener,
		OnTimeChangedListener {
	private DatePicker datePicker;
	private TimePicker timePicker;
	private BasicDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;

	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DateTimePickDialogUtil(Activity activity, String initDateTime) {
		this.activity = activity;
		this.initDateTime = initDateTime;

	}

	public void init(DatePicker datePicker, TimePicker timePicker) {
		timePicker.setIs24HourView(true);
		Calendar calendar = Calendar.getInstance();
		if (!(null == initDateTime || "".equals(initDateTime))) {
			calendar = this.getCalendarByInintData(initDateTime);
		} else {
			initDateTime = calendar.get(Calendar.YEAR) + "年"
					+ calendar.get(Calendar.MONTH) + "月"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "日 "
					+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
					+ calendar.get(Calendar.MINUTE);
		}

		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), this);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
	}

	/**
	 * 弹出日期时间选择框方法
	 * 
	 * @param inputDate
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	public BasicDialog dateTimePicKDialog(final Object inputDate) {
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.common_datetime, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
		init(datePicker, timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);
		
		ad = new BasicDialog.Builder(activity)
        .setIcon( R.drawable.icon_info )
        .setView(dateTimeLayout)
        .setTitle(R.string.data_manager_select_date_time)
        .setNegativeButton(R.string.str_cancle)
		.setPositiveButton(R.string.str_ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(inputDate instanceof EditText){
					EditText   editText = (EditText)inputDate;
					editText.setText(dateTime);
				}else{
					TextView   textView = (TextView)inputDate;
					textView.setText(dateTime); 
					callBackI.onPositiveButton(textView);
				}
			}
		})
		.show();
		onDateChanged(null, 0, 0, 0);
		return ad;
	}
	
	
	public  CallBackI callBackI;

	public void setOnClickListener(CallBackI callBackI) {
		this.callBackI = callBackI;
	}

	public interface CallBackI {
		void onPositiveButton(View v);
	}
	
	
	

	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// 获得日历实例
		Calendar calendar = Calendar.getInstance();

		calendar.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
				timePicker.getCurrentMinute());
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.getDefault());

		dateTime = sdf.format(calendar.getTime());
		ad.setTitle(dateTime);
	}

	/**
	 * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
	 * 
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String initDateTime) {
		Calendar calendar = Calendar.getInstance();
		initDateTime = "1970Y1M1D" + initDateTime;
		// 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
		String date = spliteString(initDateTime, "D", "index", "front"); // 日期
		String time = spliteString(initDateTime, "D", "index", "back"); // 时间

		String yearStr = spliteString(date, "Y", "index", "front"); // 年份
		String monthAndDay = spliteString(date, "Y", "index", "back"); // 月日

		String monthStr = spliteString(monthAndDay, "M", "index", "front"); // 月
		String dayStr = spliteString(monthAndDay, "M", "index", "back"); // 日

		String hourStr = spliteString(time, ":", "index", "front"); // 时
		String minuteStr = spliteString(time, ":", "index", "back"); // 分
//		String sStr = "00";

		int currentYear = Integer.valueOf(yearStr.trim()).intValue();
		int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
		int currentDay = Integer.valueOf(dayStr.trim()).intValue();
		int currentHour = Integer.valueOf(hourStr.trim()).intValue();
		int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();
//		int current = Integer.valueOf(sStr).intValue();

		calendar.set(currentYear, currentMonth, currentDay, currentHour,
				currentMinute);
		return calendar;
	}

	/**
	 * 截取子串
	 * 
	 * @param srcStr
	 *            源串
	 * @param pattern
	 *            匹配模式
	 * @param indexOrLast
	 * @param frontOrBack
	 * @return
	 */
	public static String spliteString(String srcStr, String pattern,
			String indexOrLast, String frontOrBack) {
		String result = "";
		int loc = -1;
		if (indexOrLast.equalsIgnoreCase("index")) {
			loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
		} else {
			loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
		}
		if (frontOrBack.equalsIgnoreCase("front")) {
			if (loc != -1)
				result = srcStr.substring(0, loc); // 截取子串
		} else {
			if (loc != -1)
				result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
		}
		return result;
	}

}
