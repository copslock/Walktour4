package com.dingli.droidwall;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Looper;

import com.dingli.droidwall.Api.DroidApp;

import java.util.ArrayList;

public class DroidWallUtil {
	/** 需要过滤的应用名称，多个以逗号隔开*/
	private static final String APP_NAME_NEED_TO_FILTER = "Walktour";

	/**
	 * 禁止其他应用联网
	 * @param context
	 */
	public static void enable(final Context context) {
		new Thread(){
			public void run() {
				Looper.prepare();
				Api.applyIptablesRules(context, false);
				Looper.loop();
			};
		}.start();
		
	}
	
	/**
	 *允许 其他应用联网
	 * @param context
	 */
	public static void disable(final Context context) {
		new Thread(){
			public void run() {
				Looper.prepare();
				Api.purgeIptables(context, false);
				Looper.loop();
			};
		}.start();
		
	}
	
	/**
	 * 过滤应用
	 * @param apps
	 * @return
	 */
	public static DroidApp[] appFilter(DroidApp[] apps) {
		DroidApp[] appArray = null;
		ArrayList<DroidApp> appList = new ArrayList<Api.DroidApp>();
		for (int i = 0; i < apps.length; i++) {
			String name = apps[i].toString().trim();
			if (!APP_NAME_NEED_TO_FILTER.contains(name)) {
				appList.add(apps[i]);
			}
		}
		appArray = new DroidApp[appList.size()];
		for (int i = 0; i < appArray.length; i++) {
			appArray[i] = appList.get(i);
		}
		return appArray;
	}
	
	/**
	 * 保存修改，每次设置选择改变后调用此方法
	 * @param apps
	 */
	public static void saveChange(Context ctx, DroidApp[] apps) {
		final SharedPreferences prefs = ctx.getSharedPreferences(Api.PREFS_NAME, 0);
		// Builds a pipe-separated list of names
		final StringBuilder newuids_wifi = new StringBuilder();
		final StringBuilder newuids_3g = new StringBuilder();
		for (int i=0; i<apps.length; i++) {
			if (apps[i].selected_wifi) {
				if (newuids_wifi.length() != 0) newuids_wifi.append('|');
				newuids_wifi.append(apps[i].uid);
			}
			if (apps[i].selected_3g) {
				if (newuids_3g.length() != 0) newuids_3g.append('|');
				newuids_3g.append(apps[i].uid);
			}
		}
		// save the new list of UIDs
		final Editor edit = prefs.edit();
		edit.putString(Api.PREF_WIFI_UIDS, newuids_wifi.toString());
		edit.putString(Api.PREF_3G_UIDS, newuids_3g.toString());
		edit.commit();
	}
}
