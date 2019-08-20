package com.walktour.gui.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.DateUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.CustomWheelView;
import com.walktour.framework.view.calendarcard.CalendarCellDecorator;
import com.walktour.framework.view.calendarcard.CalendarPickerView;
import com.walktour.framework.view.calendarcard.CalendarPickerView.SelectionMode;
import com.walktour.gui.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

@SuppressLint({ "InflateParams", "HandlerLeak" })
public class DateSelector  extends BasicActivity {
	
	private CalendarPickerView start_calendar;
	private CalendarPickerView end_calendar;
	
	private CustomWheelView wvHourStart;
	private CustomWheelView wvMinStart;
	private CustomWheelView wvHourEnd;
	private CustomWheelView wvMinEnd;
	private String startHour = "00";
	private String startMin = "00";
	private String endHour = "00";
	private String endMin = "00";
	private String startTime = "";
	private String endTime = "";
	private SharedPreferences mPreferences;
	private String flag = "";
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			addContentView();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.date_selector_activity);
		mPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE );
		flag = getIntent().getStringExtra("flag");
		initView();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer://返回
			mPreferences.edit().putString(FilterKey.KEY_TIME_RANGE + flag, "").commit();
			finish();
			break;
		case R.id.btn_summit:
			save();
			break;
		case R.id.btn_cancel:
			mPreferences.edit().putString(FilterKey.KEY_TIME_RANGE + flag, "").commit();
			finish();
			break;

		default:
			break;
		}
	}

	private void initView() {
		TextView tvTitle = initTextView(R.id.title_txt);
		tvTitle.setText(getResources().getString(R.string.data_manager_select_date_time));
		findViewById(R.id.pointer).setOnClickListener(this);
		
		waitAsecond();
	    
	}
	
	private void save() {
		startTime = DateUtil.Y_M_D.format(start_calendar.getSelectedDate().getTime());
		endTime = DateUtil.Y_M_D.format(end_calendar.getSelectedDate().getTime());
		String timeRange = startTime + " " + startHour + ":" + startMin + "~" + endTime + " " + endHour + ":" + endMin;
		String[] timeRangeArray = timeRange.split("~");
		long[] times = DateUtil.getTimeRange(3, timeRangeArray);
		if (times[1] - times[0] >= 0) {
			mPreferences.edit().putString(FilterKey.KEY_TIME_RANGE + flag, timeRange).commit();
			finish();
		} else {
			Toast.makeText(getApplicationContext(), R.string.data_manager_time_setting, Toast.LENGTH_SHORT).show();
			return;
		}
	}
	
	private void addContentView() {
		View v = getLayoutInflater().inflate(R.layout.date_selector_content_activity, null);
		LinearLayout content = (LinearLayout)findViewById(R.id.content);
		content.addView(v);
		final Calendar nextYear = Calendar.getInstance();
	    nextYear.add(Calendar.YEAR, 1);

	    final Calendar lastYear = Calendar.getInstance();
	    lastYear.add(Calendar.YEAR, -1);
		start_calendar = (CalendarPickerView)findViewById(R.id.calendar_view_start);
		end_calendar = (CalendarPickerView)findViewById(R.id.calendar_view_end);
		
		start_calendar.init(lastYear.getTime(), nextYear.getTime()) //
        .inMode(SelectionMode.SINGLE) //
        .withSelectedDate(new Date());
		end_calendar.init(lastYear.getTime(), nextYear.getTime()) //
        .inMode(SelectionMode.SINGLE) //
        .withSelectedDate(new Date());
		
		start_calendar.setDecorators(Collections.<CalendarCellDecorator>emptyList());
		end_calendar.setDecorators(Collections.<CalendarCellDecorator>emptyList());
		
		findViewById(R.id.btn_summit).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		wvHourStart = (CustomWheelView) findViewById(R.id.wv_hour_start);
		wvMinStart = (CustomWheelView) findViewById(R.id.wv_min_start);

		wvHourEnd = (CustomWheelView) findViewById(R.id.wv_hour_end);
		wvMinEnd = (CustomWheelView) findViewById(R.id.wv_min_end);
		
		initTimeData();
	}
	
	private void initTimeData() {
		String[] hours = generateArray(24);
		String[] mins = generateArray(60);
		// 开始时
		wvHourStart.setOffset(1);
		wvHourStart.setItems(Arrays.asList(hours));
		wvHourStart
				.setOnWheelViewListener(new CustomWheelView.OnWheelViewListener() {
					@Override
					public void onSelected(int selectedIndex, String item) {
						startHour = item;
					}
				});
		// 开始分
		wvMinStart.setOffset(1);
		wvMinStart.setItems(Arrays.asList(mins));
		wvMinStart
				.setOnWheelViewListener(new CustomWheelView.OnWheelViewListener() {
					@Override
					public void onSelected(int selectedIndex, String item) {
						startMin = item;
					}
				});

		// 结束时
		wvHourEnd.setOffset(1);
		wvHourEnd.setItems(Arrays.asList(hours));
		wvHourEnd
				.setOnWheelViewListener(new CustomWheelView.OnWheelViewListener() {
					@Override
					public void onSelected(int selectedIndex, String item) {
						endHour = item;
					}
				});
		// 结束分
		wvMinEnd.setOffset(1);
		wvMinEnd.setItems(Arrays.asList(mins));
		wvMinEnd.setOnWheelViewListener(new CustomWheelView.OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
				endMin = item;
			}
		});
	}

	private String[] generateArray(int max) {
		String[] tmp = new String[max];
		for (int i = 0; i < tmp.length; i++) {
			String str = "";
			if (i < 10) {
				str = "0" + i;
			} else {
				str = "" + i;
			}
			tmp[i] = str;
		}
		return tmp;
	}
	
	/**
	 * 延时100毫秒
	 */
	private void waitAsecond() {
		new Thread(){
			public void run() {
				try {
					sleep(100);
					mHandler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
}
