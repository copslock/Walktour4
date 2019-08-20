package com.walktour.framework.ui;

import android.app.Activity;

import com.walktour.base.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity管理器
 */
public class ActivityManager {

	private static List<Activity> activities = new ArrayList<Activity>();

	public static void addActivity(Activity activity) {
		activities.add(activity);
	}

	public static void removeActivity(Activity activity) {
		activities.remove(activity);
	}

	/**
	 * 移除最后打开的activity
	 */
	public static void removeLast() {
		if (activities.size() > 0) {
			Activity activity = activities.remove(activities.size() - 1);
			LogUtil.w("WalktourKiller", "--kill:" + activity.getLocalClassName());
			activity.finish();
		}
	}

	public static final List<Activity> getActivities() {
		return activities;
	}

	public static void finishAll() {
		for (int i = activities.size() - 1; i >= 0; i--) {
			Activity activity = activities.get(i);
			if (!activity.isFinishing()&&!activity.isChild()) {
				LogUtil.w("WalktourKiller", "--kill:" + activity.getLocalClassName());
				activity.finish();
			}

		}

	}

}
