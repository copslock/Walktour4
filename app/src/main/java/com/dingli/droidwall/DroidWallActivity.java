package com.dingli.droidwall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dingli.droidwall.Api.DroidApp;
import com.walktour.gui.R;
import com.walktour.gui.setting.sysroutine.SysRoutineActivity;
/**
 * 第三方应用联网控制
 * @author haohua.xu 2015-03-19
 */
public class DroidWallActivity extends Activity implements OnCheckedChangeListener, OnClickListener {

	/** adapter */
	private ArrayAdapter<DroidApp> adapter;

	private Context mContext;
	
	private final String ALL_WIFI = "checkAllWifi";
	
	private final String ALL_MOBILE_DATA = "checkAllMobileData";
	
	private DroidApp[] appArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_other_app_network_list);
		mContext = this;
		init();
		initView();
	}

	@Override
	public void onClick(View v) {
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(SysRoutineActivity.SHOW_MAIN_TAB);
			this.sendBroadcast(intent);
			applyOrSaveRules();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 初始化
	 */
	private void init() {
		checkPreferences();
		setMode();
	}

	private void initView() {
		CheckBox cbAllWifi = ((CheckBox) findViewById(R.id.check_wifi_all));
		CheckBox cbAllMobileData = ((CheckBox) findViewById(R.id.check_data_all));
		boolean isCheckAllWifi = getSharePreferences().getBoolean(ALL_WIFI, false);
		boolean isCheckAllMobileData = getSharePreferences().getBoolean(ALL_MOBILE_DATA, false);
		cbAllWifi.setChecked(isCheckAllWifi);
		cbAllMobileData.setChecked(isCheckAllMobileData);
		cbAllWifi.setOnCheckedChangeListener(this);
		cbAllMobileData.setOnCheckedChangeListener(this);
		ListView listView = (ListView) findViewById(R.id.app_list);
		final DroidApp[] apps = Api.getApps(this);
		appArray = DroidWallUtil.appFilter(apps);
		final LayoutInflater inflater = getLayoutInflater();
		adapter = new ArrayAdapter<DroidApp>(this, R.layout.setting_other_app_network_state_listitem, R.id.name, appArray) {
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				ListEntry entry;
				if (convertView == null) {
					// Inflate a new view
					convertView = inflater.inflate(R.layout.setting_other_app_network_state_listitem, parent, false);
					Log.d("DroidWall", ">> inflate(" + convertView + ")");
					entry = new ListEntry();
					entry.box_wifi = (CheckBox) convertView.findViewById(R.id.check_wifi);
					entry.box_3g = (CheckBox) convertView.findViewById(R.id.check_data);
					entry.text = (TextView) convertView.findViewById(R.id.name);
					entry.icon = (ImageView) convertView.findViewById(R.id.itemicon);
					entry.box_wifi.setOnCheckedChangeListener(DroidWallActivity.this);
					entry.box_3g.setOnCheckedChangeListener(DroidWallActivity.this);
					convertView.setTag(entry);
				} else {
					// Convert an existing view
					entry = (ListEntry) convertView.getTag();
				}

				final DroidApp app = appArray[position];
				entry.app = app;
				entry.text.setText(app.toString());
				entry.icon.setImageDrawable(app.cached_icon);
				if (!app.icon_loaded && app.appinfo != null) {
					// this icon has not been loaded yet - load it on a separated thread
					new LoadIconTask().execute(app, getPackageManager(), convertView);
				}
				final CheckBox box_wifi = entry.box_wifi;
				box_wifi.setTag(app);
				box_wifi.setChecked(app.selected_wifi);
				final CheckBox box_3g = entry.box_3g;
				box_3g.setTag(app);
				box_3g.setChecked(app.selected_3g);
				return convertView;
			}
		};
		listView.setAdapter(adapter);
	}

	/**
	 * 设置黑名单 白名单模式，默认黑名单模式
	 */
	private void setMode() {
		String mode = Api.MODE_BLACKLIST;
		final Editor editor = getSharedPreferences(Api.PREFS_NAME, 0).edit();
		editor.putString(Api.PREF_MODE, mode);
		editor.commit();
	}

	/**
	 * Check if the stored preferences are OK
	 */
	private void checkPreferences() {
		final SharedPreferences prefs = getSharedPreferences(Api.PREFS_NAME, 0);
		final Editor editor = prefs.edit();
		boolean changed = false;
		if (prefs.getString(Api.PREF_MODE, "").length() == 0) {
			editor.putString(Api.PREF_MODE, Api.MODE_BLACKLIST);
			changed = true;
		}
		/* delete the old preference names */
		if (prefs.contains("AllowedUids")) {
			editor.remove("AllowedUids");
			changed = true;
		}
		if (prefs.contains("Interfaces")) {
			editor.remove("Interfaces");
			changed = true;
		}
		if (changed)
			editor.commit();
		Api.setEnabled(DroidWallActivity.this, false);
	}
	
	private SharedPreferences getSharePreferences() {
		return getSharedPreferences(Api.PREFS_NAME, 0);
		
	}
	
	/**
	 * 保存修改
	 */
	private void saveChange() {
		DroidWallUtil.saveChange(mContext, appArray);
	}

	/**
	 * Apply or save iptable rules, showing a visual indication
	 */
	private void applyOrSaveRules() {
		DroidWallUtil.enable(mContext);
	}

	/**
	 * Asynchronous task used to load icons in a background thread.
	 */
	private static class LoadIconTask extends AsyncTask<Object, Void, View> {
		@Override
		protected View doInBackground(Object... params) {
			try {
				final DroidApp app = (DroidApp) params[0];
				final PackageManager pkgMgr = (PackageManager) params[1];
				final View viewToUpdate = (View) params[2];
				if (!app.icon_loaded) {
					app.cached_icon = pkgMgr.getApplicationIcon(app.appinfo);
					app.icon_loaded = true;
				}
				// Return the view to update at "onPostExecute"
				// Note that we cannot be sure that this view still references "app"
				return viewToUpdate;
			} catch (Exception e) {
				Log.e("DroidWall", "Error loading icon", e);
				return null;
			}
		}

		protected void onPostExecute(View viewToUpdate) {
			try {
				// This is executed in the UI thread, so it is safe to use viewToUpdate.getTag()  and modify the UI
				final ListEntry entryToUpdate = (ListEntry) viewToUpdate.getTag();
				entryToUpdate.icon.setImageDrawable(entryToUpdate.app.cached_icon);
			} catch (Exception e) {
				Log.e("DroidWall", "Error showing icon", e);
			}
		};
	}

	/**
	 * Entry representing an application in the screen
	 */
	private static class ListEntry {
		private CheckBox box_wifi;
		private CheckBox box_3g;
		private TextView text;
		private ImageView icon;
		private DroidApp app;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		final DroidApp app = (DroidApp) buttonView.getTag();
		if (app != null) {
			switch (buttonView.getId()) {
			case R.id.check_wifi:
				if (app.selected_wifi != isChecked) {
					app.selected_wifi = isChecked;
					saveChange();
				}
				break;
			case R.id.check_data:
				if (app.selected_3g != isChecked) {
					app.selected_3g = isChecked;
					saveChange();
				}
				break;

			}
		}
		switch (buttonView.getId()) {
		case R.id.check_wifi_all:
			selectedAllWifi(isChecked);
			break;
		case R.id.check_data_all:
			selectedAllData(isChecked);
			break;

		default:
			break;
		}
	}

	/**
	 * 全选wifi
	 * 
	 * @param flag
	 */
	private void selectedAllWifi(boolean flag) {
		getSharePreferences().edit().putBoolean(ALL_WIFI, flag).commit();
		int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			adapter.getItem(i).selected_wifi = flag;
		}
		saveChange();
		adapter.notifyDataSetChanged();
	}

	/**
	 * 全选移动数据
	 * 
	 * @param flag
	 */
	private void selectedAllData(boolean flag) {
		getSharePreferences().edit().putBoolean(ALL_MOBILE_DATA, flag).commit();
		int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			adapter.getItem(i).selected_3g = flag;
		}
		saveChange();
		adapter.notifyDataSetChanged();
	}
}
