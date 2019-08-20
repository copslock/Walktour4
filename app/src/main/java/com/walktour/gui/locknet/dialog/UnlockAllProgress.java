package com.walktour.gui.locknet.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.gui.locknet.ForceControler.Band;
import com.walktour.gui.locknet.ForceControler.ForceNet;
import com.walktour.gui.locknet.ForceManager;

/**
 * 解锁全部的进度条
 *
 * 2014-6-30 下午2:45:23
 *
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class UnlockAllProgress extends AsyncTask<Void, Void, Boolean> {
	//	private final static String TAG = "UnlockAllProgress";
	private Context context;
	private ProgressDialog progressDialog;
	private OnDialogChangeListener callback;
	/** 是否解锁频点 */
	private boolean isUnlockFrequency = false;
	/** 是否解锁小区 */
	private boolean isUnlockCell = false;

	public UnlockAllProgress(Context context, boolean isUnlockCell, OnDialogChangeListener callback) {
		this.context = context;
		this.callback = callback;
		this.isUnlockCell = isUnlockCell;
	}

	@Override
	protected void onPreExecute() {
		showProgressDialog(context.getString(R.string.exe_info), false);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// ForceNet net = ForceManager.getInstance().getLockNet(context);
		// ForceManager.getInstance().unlockAll( net );
		Deviceinfo deviceinfo = Deviceinfo.getInstance();

//		if (ApplicationModel.getInstance().isNBTest()) {
			if (this.isUnlockCell) {
				this.unlockCell();
			}
			else {
				this.unlockFrequency();
			}
/*		} else if(deviceinfo.isSamsungCustomRom()) {
			//ForceManager.getInstance().unlockAll(context, null);
			if (this.isUnlockCell) {
				this.unlockCell();
			}
			else {
				this.unlockFrequency();
			}

			//ForceManager.getInstance().saveLockFrequency(context, null);
			//ForceManager.getInstance().saveLockCell(context, null);

		} else	{
			String deviceModel = deviceinfo.getDevicemodel();
			if (deviceModel.startsWith("vivo"))
			{
				if (this.isUnlockCell) {
					this.unlockCell();
				}
				else {
					this.unlockFrequency();
				}
			} else {
				switch (deviceModel) {
					case "SM-G9300":
					case "SM-G9308":
					case "SM-G9500":
					case "SM-G9550": {
						if (this.isUnlockCell) {
							this.unlockCell();
						}
						else {
							this.unlockFrequency();
						}
					}
					break;
					default: {
						this.unlockNetWorkAndBand();
						this.unlockFrequency();
						if (this.isUnlockCell) {
							this.unlockCell();
						}
					}
					break;
				}
				// ForceManager.getInstance().saveLockBand(context, new Band[]{});
			}

		}
		*/
		return true;
	}

	/**
	 * 解锁网络和频段
	 */
	private void unlockNetWorkAndBand() {
		String devicemodel = Deviceinfo.getInstance().getDevicemodel();
		if(devicemodel.equals("ZTENX569J") || devicemodel.startsWith("vivo")){
			ForceManager.getInstance().lockNetwork(context,ForceNet.NET_AUTO);
			ForceManager.getInstance().lockBand(context,ForceNet.NET_AUTO, new Band[] {});
		}else
			ForceManager.getInstance().lockNetwork(ForceNet.NET_AUTO);
		ForceManager.getInstance().saveLockNet(context, ForceNet.NET_AUTO);
		ForceManager.getInstance().saveLockBand(context, new Band[] {});
	}

	/**
	 * 解锁频点
	 */
	private void unlockFrequency() {
		if (ApplicationModel.getInstance().isNBTest()) {
			ForceManager.getInstance().unlockFrequency(context, ForceNet.NET_LTE);
			ForceManager.getInstance().saveLockFrequency(context, null);
			this.isUnlockFrequency = true;

			return ;
		}

		String frequency = ForceManager.getInstance().getLockFrequency(context);
		if (StringUtil.isNullOrEmpty(frequency))
			return;
		String[] ls = frequency.split(",");
		if (ls[0].equals(Deviceinfo.NET_TYPES_WCDMA)) {
			ForceManager.getInstance().unlockFrequency(context, ForceNet.NET_WCDMA);
			ForceManager.getInstance().saveLockFrequency(context, null);
			this.isUnlockFrequency = true;
		}else if (ls[0].equals(Deviceinfo.NET_TYPES_LTE)) {
			ForceManager.getInstance().unlockFrequency(context, ForceNet.NET_LTE);
			ForceManager.getInstance().saveLockFrequency(context, null);
			this.isUnlockFrequency = true;
		}
	}

	/**
	 * 解锁小区
	 */
	private void unlockCell() {
		if (ApplicationModel.getInstance().isNBTest()) {
			ForceManager.getInstance().unlockCell(context, ForceNet.NET_LTE);
			ForceManager.getInstance().saveLockCell(context, null);

			return ;
		}
		String cellParams = ForceManager.getInstance().getLockCell(context);
		if (StringUtil.isNullOrEmpty(cellParams))
			return;
		String[] ls = cellParams.split(",");
		if (ls[0].equals(Deviceinfo.NET_TYPES_WCDMA)) {
			ForceManager.getInstance().unlockCell(context, ForceNet.NET_WCDMA);
		} else {
			ForceManager.getInstance().unlockCell(context, ForceNet.NET_LTE);
		}
		ForceManager.getInstance().saveLockCell(context, null);
	}

	@Override
	protected void onPostExecute(Boolean success) {
		dismissProgress();
		if (callback != null) {
			if (this.isUnlockCell || this.isUnlockFrequency)
				callback.onLockPositive(ForceManager.KEY_UNLOCK_FREQUENCY_CELL);
			else
				callback.onPositive();
		}
	}

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
