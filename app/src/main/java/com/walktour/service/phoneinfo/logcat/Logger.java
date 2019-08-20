package com.walktour.service.phoneinfo.logcat;

import android.util.Log;

/**
 * 日志对象
 * 
 * @author jianchao.wang
 *
 */
public class Logger {

	public static final String STRING_VERBOSE = "verbose";

	public static final String STRING_DEBUG = "debug";

	public static final String STRING_INFO = "info";

	public static final String STRING_WARN = "warn";

	public static final String STRING_ERROR = "error";

	public static final int LEVEL_VERBOSE = 0x0;

	public static final int LEVEL_DEBUG = 0x1;

	public static final int LEVEL_INFO = 0x2;

	public static final int LEVEL_WARN = 0x3;

	public static final int LEVEL_ERROR = 0x4;

	public static final int LEVEL_NONE = 0x5;

	private int level;

	private String tag;

	private boolean debug;

	public Logger(Class<?> clazz) {
		this(clazz.getSimpleName());
	}

	public Logger(String tag) {
		this(LEVEL_VERBOSE, tag);
	}

	// public Logger(Context context) {
	// this(context, context.getClass().getSimpleName());
	// }

	// public Logger(Context context, String tag) {
	// this(context.getResources().getString(R.string.log_level), tag);
	// }

	public Logger(String logLevel, String tag) {
		this.debug = true;
		this.tag = tag;
		this.setLevel(logLevel);
	}

	public Logger(int level, String tag) {
		this.debug = true;
		this.level = level;
		this.tag = tag;
	}

	public void verbose(String msg) {
		if (msg == null)
			return;
		if (level <= LEVEL_VERBOSE && debug) {
			Log.v(tag, msg);
		}
	}

	public void debug(String msg) {
		if (msg == null)
			return;
		if (level <= LEVEL_DEBUG && debug) {
			Log.d(tag, msg);
		}
	}

	public void info(String msg) {
		if (msg == null)
			return;
		if (level <= LEVEL_INFO && debug) {
			Log.i(tag, msg);
		}
	}

	public void warn(String msg) {
		if (msg == null)
			return;
		if (level <= LEVEL_WARN && debug) {
			Log.w(tag, msg);
		}
	}

	public void warn(String msg, Throwable e) {
		if (msg == null)
			return;
		if (level <= LEVEL_WARN && debug) {
			Log.w(tag, msg, e);
		}
	}

	public void error(String msg) {
		if (msg == null)
			return;
		if (level <= LEVEL_ERROR && debug) {
			Log.e(tag, msg);
		}
	}

	public void error(String msg, Throwable e) {
		if (msg == null)
			return;
		if (level <= LEVEL_ERROR && debug) {
			Log.e(tag, msg, e);
		}
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setLevel(String logLevel) {
		if (logLevel.equalsIgnoreCase(STRING_VERBOSE)) {
			this.level = LEVEL_VERBOSE;
		} else if (logLevel.equalsIgnoreCase(STRING_DEBUG)) {
			this.level = LEVEL_DEBUG;
		} else if (logLevel.equalsIgnoreCase(STRING_INFO)) {
			this.level = LEVEL_INFO;
		} else if (logLevel.equalsIgnoreCase(STRING_WARN)) {
			this.level = LEVEL_WARN;
		} else if (logLevel.equalsIgnoreCase(STRING_ERROR)) {
			this.level = LEVEL_ERROR;
		} else {
			this.level = LEVEL_NONE;
		}
	}

}
