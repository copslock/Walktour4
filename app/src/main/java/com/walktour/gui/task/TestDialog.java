package com.walktour.gui.task;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;

import com.walktour.Utils.ApplicationModel;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.setting.Sys;
import com.walktour.gui.setting.sysroutine.SysRoutineAPNActivity;

public class TestDialog extends BasicActivity {

	public static final int DialogByAlarm = 1;
	public static final int UNICOM_SERVER_EMPTY = 2;
	public static final String EXTRA_FROM = "from";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		showDialog(getIntent().getIntExtra(EXTRA_FROM, 0));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		TestDialog.this.finish();
	}

	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DialogByAlarm:
			return new BasicDialog.Builder(TestDialog.this).setTitle(R.string.sys_alarm)
					.setMessage(R.string.Sys_Intent_APN_Null)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (ApplicationModel.getInstance().isGeneralMode()) {
								Intent intent = new Intent(Settings.ACTION_APN_SETTINGS);
								startActivity(intent);
								finish();
							} else {
								/** 转到设置界面 */
								startActivity(new Intent(TestDialog.this, SysRoutineAPNActivity.class));
								finish();
							}
						}
					}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							TestDialog.this.finish();
						}
					}).create();

		case UNICOM_SERVER_EMPTY:
			return new BasicDialog.Builder(TestDialog.this).setTitle(R.string.sys_alarm)
					.setMessage(R.string.Sys_Check_Unicom_Server_fail)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							/** 转到设置界面 */
							startActivity(new Intent(TestDialog.this, Sys.class));
							finish();
						}
					}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							TestDialog.this.finish();
						}
					}).create();
		default:
			break;
		}
		return super.onCreateDialog(id);

	}
}
