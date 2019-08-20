package com.walktour.gui.locknet.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.walktour.Utils.ApplicationModel;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.gui.locknet.ForceControler.ForceNet;
import com.walktour.gui.locknet.ForceManager;

/**
 * 锁小区进度显示框
 * 
 * @author jianchao.wang
 *
 */
public class LockCellProgress extends AsyncTask<Void, Void, Boolean> {
//	private final static String TAG = "LockCellProgress";
	/** 上下文 */
	private Context context;
	/** 进度条 */
	private ProgressDialog progressDialog;
	/** 参数 */
	private String[] args;
	/** 网络类型 */
	private ForceNet netType;
	/** 回调方法 */
	private OnDialogChangeListener callback;

	public LockCellProgress(Context context, ForceNet netType, OnDialogChangeListener callback, String... args) {
		this.context = context;
		this.args = args;
		this.netType = netType;
		this.callback = callback;
	}

	@Override
	protected void onPreExecute() {
		showProgressDialog(context.getString(R.string.exe_info), false);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		int iFreqParams = 0;

		if (ApplicationModel.getInstance().isNBTest()) {
			ForceManager.getInstance().saveLockCell(context, this.args);
		} else {
			String netType = "";
			if (this.netType == ForceNet.NET_WCDMA)
				netType = Deviceinfo.NET_TYPES_WCDMA;
			else
				netType = Deviceinfo.NET_TYPES_LTE;
			String[] cellParams = new String[this.args.length + 1];
			cellParams[0] = netType;
			for (int i = 0; i < this.args.length; i++) {
				cellParams[i + 1] = this.args[i];
			}
			ForceManager.getInstance().saveLockCell(context, cellParams);
		}
		ForceManager.getInstance().lockCell(this.context, this.netType, this.args);
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/*MyPhoneState.getInstance().startAirplane();
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		MyPhoneState.getInstance().closeAirplane();*/
		return true;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		dismissProgress();
		if (callback != null)
			callback.onLockPositive(ForceManager.KEY_LOCK_CELL);
	}

	/**
	 * 显示进度条对话框
	 * 
	 * @param message
	 *          信息
	 * @param cancleable
	 *          是否可以关闭
	 */
	private void showProgressDialog(String message, boolean cancleable) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(cancleable);
		progressDialog.show();
	}

	private void dismissProgress() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
}
