package com.walktour.service.phoneinfo.logcat;

import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallLog extends BaseLogScan implements LogObserver {

	private static final String FILTER_GSM_CONN = "[GSMConn]";
	private static final String FILTER_CDMA_CONN = "[CDMAConn]";
	private static final String FILTER_CALL_FAIL_CAUSE = "LAST_CALL_FAIL_CAUSE";
	// opposite side ring
	private static final String FILTER_CALL_ALERTING = "parent=ALERTING";
	private long lastCallAlering_time = 0;
	private boolean isCallAlerting = false;

	public boolean isCallAlerting() {
		return isCallAlerting;
	}

	public void setCallAlerting(boolean isCallAlerting) {
		this.isCallAlerting = isCallAlerting;
	}

	// ringing
	private static final String FILTER_CALL_RINGING = "EVENT_CALL_RING Received state=RINGING";
	private long lastCallRinging_time = 0;
	private boolean isCallRinging = false;

	private static final String FILTER_CALL_ACTIVE = "parent=ACTIVE";
	private long lastCallActive_time = 0;
	private boolean isCallActive = false;

	private static final String FILTER_CALL_DISCONNECT = "onDisconnect";

	private static final String FILTER_DISCONNECT_BUSY1 = "onDisconnect: cause=BUSY";
	private static final String FILTER_DISCONNECT_BUSY2 = "onDisconnect: rawCause=17";
	private long lastDisconnectBusy_time = 0;

	private static final String FILTER_DISCONNECT_LOCAL = "onDisconnect: cause=LOCAL";
	private long lastDisconnectLocal_time = 0;

	private static final String FILTER_DISCONNECT_REMOTE1 = "onDisconnect: cause=REMOTE";
	private static final String FILTER_DISCONNECT_REMOTE2 = "onDisconnect: rawCause=16";
	private long lastDisconnectRemote_time = 0;

	private static final String FILTER_DISCONNECT_MISSED = "onDisconnect: cause=INCOMING_MISSED";
	private long lastDisconnectMissed_time = 0;

	private static final String FILTER_DISCONNECT_REJECTED = "onDisconnect: cause=INCOMING_REJECTED";
	private long lastDisconnectRejected_time = 0;

	private static final String FILTER_LAST_CALL_FAIL_CAUSE = "LAST_CALL_FAIL_CAUSE";
	// private static final String VOICE_END = "onVoiceCallEnded";
	private long lastLastCallFailCause_time = 0;
	// private long lastLastCallFailNoResponse_time = 0;
	// private long lastLastCallFailNoSignal_time = 0;

	private EventListener eventListerner = null;

	public EventListener getEventListerner() {
		return eventListerner;
	}

	public void setEventListerner(EventListener eventListerner) {
		this.eventListerner = eventListerner;
	}

	Context context;
	// Logger logger;

	private LastCallFailCause failCause;

	public CallLog(Context context) {
		super();
		this.context = context;
		// logger = new Logger(getClass());
		failCause = new LastCallFailCause();
		this.filter.addFilterValue(LogFilter.GSMMSG_FILTER, FILTER_GSM_CONN);
		this.filter.addFilterValue(LogFilter.CDMAMSG_FILTER, FILTER_CDMA_CONN);
		this.filter.addFilterValue(LogFilter.GSMMSG_FILTER, FILTER_CALL_FAIL_CAUSE);
		this.filter.addFilterValue(LogFilter.CDMAMSG_FILTER, FILTER_CALL_FAIL_CAUSE);

	}

	@Override
	public void update(LogSubject subject, LogcatBean bean) {
		if (!filter.filterLog(bean)) {
			return;
		}
		String newLog = bean.getMsg();
		long nowTime = System.currentTimeMillis();
		// opposite side ring
		if (newLog.contains(FILTER_CALL_ALERTING) && (nowTime - lastCallAlering_time) > 1000 && !isCallAlerting) {
			lastCallAlering_time = nowTime;
			isCallAlerting = true;
			if (eventListerner != null) {
				eventListerner.comeEvent(EventCode.FLAG_CALL_ALERTING, LastCallFailCause.UNKOWN_CAUSE, bean.getLogTime());
			}
		} else if (newLog.contains(FILTER_CALL_RINGING) && (nowTime - lastCallRinging_time) > 1000 && !isCallRinging) {
			lastCallRinging_time = nowTime;
			isCallRinging = true;
			/*
			 * if(eventListerner != null){
			 * eventListerner.comeEvent(EventCode.FLAG_CALL_RINGING,
			 * LastCallFailCause.UNKOWN_CAUSE,bean.getLogTime()); }
			 */
		} else if (newLog.contains(FILTER_CALL_DISCONNECT)) {
			// busy
			if ((newLog.contains(FILTER_DISCONNECT_BUSY1) || newLog.contains(FILTER_DISCONNECT_BUSY2))
					&& (nowTime - lastDisconnectBusy_time) > 1000) {
				lastDisconnectBusy_time = nowTime;
				if (eventListerner != null) {
					eventListerner.comeEvent(EventCode.FLAG_CALL_DIS_BUSY, LastCallFailCause.USER_BUSY, bean.getLogTime());
				}
			} else if (newLog.contains(FILTER_DISCONNECT_LOCAL) && (nowTime - lastDisconnectLocal_time) > 1000) {
				lastDisconnectLocal_time = nowTime;
				if (eventListerner != null) {
					eventListerner.comeEvent(EventCode.FLAG_CALL_DIS_LOCAL, LastCallFailCause.UNKOWN_CAUSE, bean.getLogTime());
				}
			} else if ((newLog.contains(FILTER_DISCONNECT_REMOTE1) || newLog.contains(FILTER_DISCONNECT_REMOTE2))
					&& (nowTime - lastDisconnectRemote_time) > 1000) {
				lastDisconnectRemote_time = nowTime;
				if (eventListerner != null) {
					eventListerner.comeEvent(EventCode.FLAG_CALL_DIS_REMOTE, LastCallFailCause.UNKOWN_CAUSE, bean.getLogTime());
				}
			} else if (newLog.contains(FILTER_DISCONNECT_MISSED) && (nowTime - lastDisconnectMissed_time) > 1000) {
				lastDisconnectMissed_time = nowTime;
				if (eventListerner != null) {
					eventListerner.comeEvent(EventCode.FLAG_CALL_MISSED, LastCallFailCause.UNKOWN_CAUSE, bean.getLogTime());
				}
			} else if (newLog.contains(FILTER_DISCONNECT_REJECTED) && (nowTime - lastDisconnectRejected_time) > 1000) {
				lastDisconnectRejected_time = nowTime;
				if (eventListerner != null) {
					eventListerner.comeEvent(EventCode.FLAG_CALL_REJECTED, LastCallFailCause.UNKOWN_CAUSE, bean.getLogTime());
				}
			}

			isCallActive = false;
			isCallAlerting = false;
			isCallRinging = false;
		} else if (newLog.contains(FILTER_CALL_ACTIVE) && (nowTime - lastCallActive_time) > 1000) {
			lastCallActive_time = nowTime;
			isCallActive = true;
			if (eventListerner != null) {
				eventListerner.comeEvent(EventCode.FLAG_CALL_ACTIVE, LastCallFailCause.UNKOWN_CAUSE, bean.getLogTime());
			}
		} else if (newLog.contains(FILTER_LAST_CALL_FAIL_CAUSE)) {
			anayseCallFailCause(newLog, bean);
		}
	}

	private void anayseCallFailCause(String log, LogcatBean bean) {
		failCause.anayseLastCallFailCause(log);
		long nowTime = System.currentTimeMillis();
		if (failCause.isDropCall() && (nowTime - lastLastCallFailCause_time) > 1000) {
			lastLastCallFailCause_time = nowTime;
			if (isCallActive) {
				if (eventListerner != null) {
					eventListerner.comeEvent(EventCode.FLAG_CALL_DIS_DROP, failCause.getErrorCode(), bean.getLogTime());
				}
			} else if (isCallAlerting) {
				if (eventListerner != null) {
					eventListerner.comeEvent(EventCode.FLAG_CALL_NORESPONSE, failCause.getErrorCode(), bean.getLogTime());
				}
			} else if (!isCallAlerting) {
				if (eventListerner != null) {
					eventListerner.comeEvent(EventCode.FLAG_CALL_SETUP_FAILURE, failCause.getErrorCode(), bean.getLogTime());
				}
			}

			isCallActive = false;
			isCallAlerting = false;
			isCallRinging = false;
		}
	}

	/**
	 * radio log export LAST_CALL_FAIL_CAUSE{xxx,yyy} or LAST_CALL_FAIL_CAUSE{xxx}
	 * CDMA drop value is 1001, GSM normal value is 16, other is drop
	 * 
	 * @author Administrator
	 * 
	 */
	private class LastCallFailCause {
		public static final int UNKOWN_CAUSE = -1;
		public static final int ERROR_UNSPECIFIED = 0xffff;

		static final int NORMAL_CLEARING1 = 16;
		static final int NORMAL_CLEARING2 = 34;
		static final int USER_BUSY = 17;
		static final int CDMA_DROP = 1001;

		public final Pattern patternDropCall = Pattern.compile("LAST_CALL_FAIL_CAUSE.*\\{.*\\}");
		public final Pattern patternDropCallTD9308 = Pattern
				.compile("EVENT_GET_LAST_CALL_FAIL_CAUSE\\s-\\s.*\\s-\\scauseCode\\s=\\s(.*)");

		public final Pattern patternCause = Pattern.compile("\\{.*\\}");

		/**
		 * {cause1}
		 */
		public int cause1 = UNKOWN_CAUSE;
		/**
		 * {cause1,cause2}
		 */
		public int cause2 = UNKOWN_CAUSE;

		public void init() {
			cause1 = UNKOWN_CAUSE;
			cause2 = UNKOWN_CAUSE;
		}

		public int getErrorCode() {
			if (cause2 != UNKOWN_CAUSE && cause2 != 0) {
				return cause2;
			}
			if (cause1 != UNKOWN_CAUSE) {
				return cause1;
			}
			return UNKOWN_CAUSE;
		}

		public void anayseLastCallFailCause(String log) {
			if (!commonLastCallFailCause(log)) {
				td9308LastCallFailCause(log);
			}
		}

		public boolean commonLastCallFailCause(String log) {
			init();
			Matcher matcher = patternDropCall.matcher(log);
			if (matcher.find()) {
				String values = matcher.group();
				matcher = patternCause.matcher(values);
				if (matcher.find()) {
					String item = matcher.group().replace("{", "").replace("}", "").trim();
					if (item.indexOf(",") != -1) {
						String[] items = item.split(",");
						if (items.length == 2) {
							try {
								cause1 = Integer.parseInt(items[0].trim());
								cause2 = Integer.parseInt(items[1].trim());
							} catch (NumberFormatException e) {
								return false;
							}
						}
					} else {
						try {
							cause1 = Integer.parseInt(item.trim());
						} catch (NumberFormatException e) {
							return false;
						}
					}
				}
				return true;
			}
			return false;
		}

		public boolean td9308LastCallFailCause(String log) {
			init();
			Matcher matcher = patternDropCallTD9308.matcher(log);
			if (matcher.find()) {
				String item = matcher.group(1).replace("{", "").replace("}", "").trim();
				if (item.indexOf(",") != -1) {
					String[] items = item.split(",");
					if (items.length == 2) {
						try {
							cause1 = Integer.parseInt(items[0].trim());
							cause2 = Integer.parseInt(items[1].trim());
						} catch (NumberFormatException e) {
							return false;
						}
					}
				} else {
					try {
						cause1 = Integer.parseInt(item.trim());
					} catch (NumberFormatException e) {
						return false;
					}
				}
				return true;
			}
			return false;
		}

		public boolean isDropCall() {
			if (cause1 == -1) {
				return false;
			}
			if (cause1 == ERROR_UNSPECIFIED) {
				return true;
			}
			if (cause1 >= 1000) {
				// CDMA
				if ((cause1 == CDMA_DROP || cause2 == CDMA_DROP)) {
					return true;
				}
				return false;
			}
			// GSM
			if (cause1 == NORMAL_CLEARING1) {
				if (cause2 != UNKOWN_CAUSE && cause2 != NORMAL_CLEARING1) {
					return true;
				} else if (cause2 != UNKOWN_CAUSE && cause2 == NORMAL_CLEARING2) {
					return true;
				} else {
					return false;
				}
			} else if (cause1 == USER_BUSY) {
				return false;
			} else {
				return false;
			}
		}

	}
}
