package com.walktour.service.phoneinfo.logcat;

import android.content.Context;

public class RadioLogMonitor extends BaseLogMonitor {

	public RadioLogMonitor(Context context, EventListener eventListener) {
		super(context, eventListener);
	}

	private CallLog callLog;

	@Override
	public void init() {
		logger = new Logger(getClass());
		type = "radio";
		CMD = "logcat -v time -b radio";// logcat -b radio

		callLog = new CallLog(context);

		callLog.setEventListerner(eventListener);

		filter = new LogFilter();

		filter.addFilterValues(callLog.filter.filterMap);

		this.attach(callLog);
	}

	public CallLog getCallLog() {
		return callLog;
	}

	public void setCallLog(CallLog callLog) {
		this.callLog = callLog;
	}
}
