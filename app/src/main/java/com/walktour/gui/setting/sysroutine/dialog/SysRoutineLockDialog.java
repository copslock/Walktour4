package com.walktour.gui.setting.sysroutine.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicDialog.Builder;
import com.walktour.gui.R;
import com.walktour.gui.locknet.LockDevMachine;
import com.walktour.gui.locknet.LockMainPage;

import java.lang.ref.WeakReference;

/**
 * 锁网锁屏设置对话框
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("InflateParams")
public class SysRoutineLockDialog {

	/** 上下文 */
	private Activity context;
	/** 进度对话框 */
	private ProgressDialog progressDialog;
	/** 显示信息 */
	private final static int MSG_SHOW = 1;
	/** 隐藏信息 */
	private final static int MSG_DIMISS = -1;

	public SysRoutineLockDialog(Activity context) {
		this.context = context;
	}

	public void show() {
		Deviceinfo info = Deviceinfo.getInstance();
		String deviceModel = info.getDevicemodel();
		if (deviceModel.equals("SIM929") || deviceModel.equals("L8161")) {
			Intent intent = new Intent(this.context, LockDevMachine.class);
			context.startActivity(intent);
			context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		} else if (Deviceinfo.getInstance().getDevicemodel().equals("G19")) {
			showLockDialog();
		} else if (info.getLockInfo().hasLock()) {
			Intent intent = new Intent(this.context, LockMainPage.class);
			context.startActivity(intent);
		} else if (info.getLockCode().length() > 0) {
			new Thread(new ThreadCommand(info.getLockCode())).start();
		} else { // 系统设置界面
			Intent intentActivity = new Intent("android.intent.action.MAIN");
			ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.RadioInfo");
			intentActivity.setComponent(componentName);
			intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 清除
			this.context.startActivity(intentActivity);
			this.context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
	}

	// 2012.11.22添加SUMSUNG Galax 3手机的锁网页面跳转
	private boolean setting = false;

	private class ThreadCommand implements Runnable {
		String command = "";

		public ThreadCommand(String cmd) {
			this.command = cmd;
		}

		@Override
		public void run() {
			if (setting) {
				return;
			}
			setting = true;
			myHandler.obtainMessage(MSG_SHOW).sendToTarget();

			UtilsMethod
					.runRootCommand("am broadcast -a android.provider.Telephony.SECRET_CODE -d android_secret_code://" + command);

			myHandler.obtainMessage(MSG_DIMISS).sendToTarget();
			setting = false;
		}

	}

	/**
	 * 自定义消息处理
	 * 
	 * @author jianchao.wang
	 *
	 */
	private static class MyHandler extends Handler {
		private WeakReference<SysRoutineLockDialog> reference;

		public MyHandler(SysRoutineLockDialog dialog) {
			this.reference = new WeakReference<SysRoutineLockDialog>(dialog);
		}

		@Override
		public void handleMessage(android.os.Message msg) {
			SysRoutineLockDialog dialog = this.reference.get();
			switch (msg.what) {
			case MSG_SHOW:
				dialog.showProgressDialog("", true);
				break;

			case MSG_DIMISS:
				if (dialog.progressDialog != null) {
					if (dialog.progressDialog.isShowing()) {
						dialog.progressDialog.dismiss();
					}
				}
				break;
			}
		}

	}

	private Handler myHandler = new MyHandler(this);

	/**
	 * 显示进度对话框
	 * 
	 * @param message
	 *          信息
	 * @param cancleable
	 *          是否可关闭
	 */
	private void showProgressDialog(String message, boolean cancleable) {
		progressDialog = new ProgressDialog(this.context);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(cancleable);
		progressDialog.show();
	}

	/**
	 * 显示锁网对话框
	 */
	private void showLockDialog() {
		new BasicDialog.Builder(this.context)
				.setItems(this.context.getResources().getStringArray(R.array.lock), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentActivity = new Intent("android.intent.action.MAIN");
							ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.RadioInfo");
							intentActivity.setComponent(componentName);
							intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 清除
							context.startActivity(intentActivity);
							context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
							break;

						case 1:
							showLockPointDialog();
							break;
						}
					}
				}).show();
	}

	/**
	 * 显示锁网点对话框
	 */
	private void showLockPointDialog() {
		LayoutInflater f = LayoutInflater.from(this.context);
		View view = f.inflate(R.layout.alert_dialog_edittext_number, null);
		final EditText editFre = (EditText) view.findViewById(R.id.alert_textEditText);
		final TextView txt = (TextView) view.findViewById(R.id.alert_textView);
		txt.setText(this.context.getString(R.string.alert_lock_msg));
		editFre.setHint(R.string.str_lock_input);
		editFre.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		editFre.setKeyListener(new DigitsKeyListener(false, false));
		final SharedPreferences share = this.context.getSharedPreferences(this.context.getPackageName() + "_preferences",
				Context.MODE_PRIVATE);
		if (share.contains("lock_frequency")) {
			editFre.setText(share.getString("lock_frequency", ""));
		}
		Builder builder = new Builder(context);
		builder.setTitle(this.context.getResources().getStringArray(R.array.lock)[1]).setView(view)
				.setPositiveButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).setNegativeButton(R.string.str_dolock, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						int point = -1;
						try {
							point = Integer.parseInt(editFre.getText().toString().trim());
						} catch (Exception e) {
							LogUtil.w("SysSetting", e.toString());
						}

						if (point != -1) {
							Intent intent = new Intent(WalkMessage.ACTION_TRACE_SET_FREQUENCY_POINT);
							intent.putExtra(WalkMessage.KEY_FRE_POINT, point);
							context.sendBroadcast(intent);

							Editor editor = share.edit();
							editor.putString("lock_frequency", editFre.getText().toString().trim());
							editor.commit();
							// 提示要重启
							showRebootDialog();
						}

					}
				});
	}

	/**
	 * 显示重启对话框
	 */
	private void showRebootDialog() {
		new BasicDialog.Builder(this.context).setMessage(R.string.alert_lock_fre)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						UtilsMethod.rebootMachine();
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}
}
