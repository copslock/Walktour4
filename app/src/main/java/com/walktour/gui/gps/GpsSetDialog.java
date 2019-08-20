package com.walktour.gui.gps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Window;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

/**
 * GPS设置确认对话框
 * 
 * @author jianchao.wang
 *
 */
public class GpsSetDialog extends BasicActivity {
	private static final String tag = "GpsSetDialog";
	/***/
	public static final String TipDialogType = "TipDialogType";
	/** 提示是否跳到GPS设置页面 */
	public static final int DialogByGPS = 1;
	/** 提示是否跳到WIFI 打开设置页面 */
	public static final int DialogByWIFIOpen = 2;
	/** 提示是否跳到WIFI 关闭设置页面 */
	public static final int DialogByWIFIClose = 3;
	private ApplicationModel appModel;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int dialogType = getIntent().getIntExtra(TipDialogType, DialogByGPS);
		appModel=ApplicationModel.getInstance();
		LogUtil.w(tag, "--dialogType:" + dialogType);
		showDialog(dialogType);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DialogByGPS:
			/*
			 * 弹出对话框，提示用户当前GPS/A-GPS没有使能
			 */
			int msgID=R.string.sys_gps_open_content;
			if(appModel.getSelectScene()==SceneType.HighSpeedRail){
				msgID=R.string.sys_gps_open_content_highspeedrail;
			}else if(appModel.getSelectScene()==SceneType.Metro){
				msgID=R.string.sys_gps_open_content_metro;
			}
			return new BasicDialog.Builder(GpsSetDialog.this).setTitle(R.string.sys_gps_open_title)
					.setMessage(msgID)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// 转到设置界面
							Intent intent = new Intent();
							intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							try {
								startActivity(intent);
							} catch (ActivityNotFoundException ex) {
								LogUtil.e(tag, ex.getMessage());
								intent.setAction(Settings.ACTION_SETTINGS);
								try {
									startActivity(intent);
								} catch (Exception e) {
									LogUtil.e(tag, e.getMessage());
								}
							}
							finish();
						}
					}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Intent it = new Intent();
							// it.setAction(Intent.ACTION_DIAL);
							// startActivity(it);
							finish();
						}
					}).setOnDismissListener(new OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface arg0) {
							// TODO Auto-generated method stub
							finish();
						}
					}).create();
		case DialogByWIFIOpen:
		case DialogByWIFIClose:
			return new AlertDialog.Builder(GpsSetDialog.this).setTitle(R.string.sys_wifi_disabled)
					.setMessage(id == DialogByWIFIOpen ? R.string.sys_wifi_alertTosetOpen : R.string.sys_wifi_alertTosetClose)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							/*
							 * 转到设置界面
							 */
							Intent fireAlarm = new Intent("android.settings.WIFI_SETTINGS");
							fireAlarm.addCategory(Intent.CATEGORY_DEFAULT);
							startActivity(fireAlarm);
							finish();
						}
					}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							finish();
						}
					}).setOnKeyListener(new DialogInterface.OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
							if (keyCode == KeyEvent.KEYCODE_BACK) {
								finish();
								return true;
							}
							return false;
						}
					}).create();
		}
		return super.onCreateDialog(id);
	}

	/**
	 * Android中Activity的Intent大全 Api Level 3: (SDK 1.5)
	 * 1.android.intent.action.ALL_APPS 2.android.intent.action.ANSWER
	 * 3.android.intent.action.ATTACH_DATA 4.android.intent.action.BUG_REPORT
	 * 5.android.intent.action.CALL 6.android.intent.action.CALL_BUTTON
	 * 7.android.intent.action.CHOOSER 8.android.intent.action.CREATE_LIVE_FOLDER
	 * 9.android.intent.action.CREATE_SHORTCUT 10.android.intent.action.DELETE
	 * 11.android.intent.action.DIAL 12.android.intent.action.EDIT
	 * 13.android.intent.action.GET_CONTENT 14.android.intent.action.INSERT
	 * 15.android.intent.action.INSERT_OR_EDIT 16.android.intent.action.MAIN
	 * 17.android.intent.action.MEDIA_SEARCH 18.android.intent.action.PICK
	 * 19.android.intent.action.PICK_ACTIVITY
	 * 20.android.intent.action.RINGTONE_PICKER 21.android.intent.action.RUN
	 * 22.android.intent.action.SEARCH 23.android.intent.action.SEARCH_LONG_PRESS
	 * 24.android.intent.action.SEND 25.android.intent.action.SENDTO
	 * 26.android.intent.action.SET_WALLPAPER 27.android.intent.action.SYNC
	 * 28.android.intent.action.SYSTEM_TUTORIAL 29.android.intent.action.VIEW
	 * 30.android.intent.action.VOICE_COMMAND 31.android.intent.action.WEB_SEARCH
	 * 32.android.net.wifi.PICK_WIFI_NETWORK
	 * 33.android.settings.AIRPLANE_MODE_SETTINGS 34.android.settings.APN_SETTINGS
	 * 35.android.settings.APPLICATION_DEVELOPMENT_SETTINGS
	 * 36.android.settings.APPLICATION_SETTINGS
	 * 37.android.settings.BLUETOOTH_SETTINGS
	 * 38.android.settings.DATA_ROAMING_SETTINGS 39.android.settings.DATE_SETTINGS
	 * 40.android.settings.DISPLAY_SETTINGS
	 * 41.android.settings.INPUT_METHOD_SETTINGS
	 * 42.android.settings.INTERNAL_STORAGE_SETTINGS
	 * 43.android.settings.LOCALE_SETTINGS
	 * 44.android.settings.LOCATION_SOURCE_SETTINGS
	 * 45.android.settings.MANAGE_APPLICATIONS_SETTINGS
	 * 46.android.settings.MEMORY_CARD_SETTINGS
	 * 47.android.settings.NETWORK_OPERATOR_SETTINGS
	 * 48.android.settings.QUICK_LAUNCH_SETTINGS
	 * 49.android.settings.SECURITY_SETTINGS 50.android.settings.SETTINGS
	 * 51.android.settings.SOUND_SETTINGS 52.android.settings.SYNC_SETTINGS
	 * 53.android.settings.USER_DICTIONARY_SETTINGS
	 * 54.android.settings.WIFI_IP_SETTINGS 55.android.settings.WIFI_SETTINGS
	 * 56.android.settings.WIRELESS_SETTINGS
	 *
	 * Api Level 4 增加的:(SDK 1.6) 1.android.intent.action.POWER_USAGE_SUMMARY
	 * 2.android.intent.action.SEND_MULTIPLE
	 * 3.android.speech.tts.engine.CHECK_TTS_DATA
	 * 4.android.speech.tts.engine.INSTALL_TTS_DATA
	 * 
	 * Api Level 5 增加的:(SDK 2.0)
	 * 1.android.bluetooth.adapter.action.REQUEST_DISCOVERABLE
	 * 2.android.bluetooth.adapter.action.REQUEST_ENABLE
	 * 3.android.settings.ACCESSIBILITY_SETTINGS
	 * 4.android.settings.PRIVACY_SETTINGS
	 * 
	 * Api Level 8 增加的:(SDK 2.2) 1.android.app.action.ADD_DEVICE_ADMIN
	 * 2.android.app.action.SET_NEW_PASSWORD 3.android.intent.action.MUSIC_PLAYER
	 * 4.android.search.action.SEARCH_SETTINGS
	 * 5.android.settings.ADD_ACCOUNT_SETTINGS
	 * 6.android.settings.DEVICE_INFO_SETTINGS
	 */
}
