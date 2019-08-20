package com.walktour.workorder;

import android.app.Activity;

import com.walktour.base.util.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Class Description
 *
 * @author Kelvin Van
 * @version 1.0
 */
public class ActivityManager {

    private static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static final List<Activity> getActivities() {
        return activities;
    }

	public static void finishAll() {
		for (int i = activities.size() - 1; i >= 0; i--) {
			LogUtil.w("ActivityManager",
					"--kill:" + activities.get(i).getLocalClassName());
			Activity activity = activities.get(i);
			if (!(activity.isChild() || activity.isFinishing()))
				activity.finish();

		}

	}

}
