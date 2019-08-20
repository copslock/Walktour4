package com.walktour.service.phoneinfo.logcat;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 日志过滤
 * 
 * @author jianchao.wang
 *
 */
public class LogFilter {
	public static final int APP_NAME_FILTER = 1;
	public static final int GSMMSG_FILTER = 2;
	public static final int CDMAMSG_FILTER = 3;

	public static final boolean DEFAULT = false;
	public SparseArray<ArrayList<String>> filterMap;

	Logger logger;

	public LogFilter() {
		filterMap = new SparseArray<ArrayList<String>>();
		logger = new Logger(getClass());
	}

	public synchronized void addFilterValue(int filter, String value) {
		ArrayList<String> values = null;
		if (filterMap.indexOfKey(filter) < 0) {
			values = new ArrayList<String>();
			values.add(value);

			filterMap.put(filter, values);
		} else {
			values = filterMap.get(filter);
			if (!values.contains(value)) {
				values.add(value);
			}
		}
	}

	public synchronized void addFilterValues(SparseArray<ArrayList<String>> map) {
		for (int i = 0; i < map.size(); i++) {
			ArrayList<String> values = map.valueAt(i);
			for (String value : values) {
				addFilterValue(map.keyAt(i), value);
			}
		}
	}

	public boolean needFilte() {
		if (filterMap.size() != 0) {
			for (int i = 0; i < this.filterMap.size(); i++) {
				if (!this.filterMap.valueAt(i).isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean filterLog(LogcatBean bean) {
		// logger.debug(bean.getMsg());
		if (bean.getMsg().contains("onDisconnect: cause=LOCAL")) {
			System.out.println("wsn local000000000000000000000000000000000000000");
		}

		SparseArray<ArrayList<String>> map = filterMap.clone();
		if (map.size() == 0) {
			return DEFAULT;
		}
		boolean contains = true;
		for (int i = 0; i < this.filterMap.size(); i++) {
			ArrayList<String> values = this.filterMap.valueAt(i);
			switch (this.filterMap.keyAt(i)) {
			case LogFilter.APP_NAME_FILTER:
				// filtering by the application name
				if (!values.contains(bean.getAppName())) {
					return false;
				}
				break;
			case LogFilter.GSMMSG_FILTER:

				for (String content : values) {
					if (bean.getMsg().toLowerCase(Locale.getDefault()).contains(content.toLowerCase(Locale.getDefault()))) {
						contains = true;
						break;
					}
				}
				// if (!contains) {
				// return false;
				// }
			case LogFilter.CDMAMSG_FILTER:
				// boolean contains1 = false;
				for (String content : values) {
					if (bean.getMsg().toLowerCase(Locale.getDefault()).contains(content.toLowerCase(Locale.getDefault()))) {
						contains = true;
						break;
					}
				}
				// if (!contains1) {
				// return false;
				// }
			default:
				break;
			}

			if (contains) {
				break;
			}
		}

		return contains;
	}
}
