/**
 * com.walktour.gui.locknet.dialog
 * LockBandProgress.java
 * 类功能：
 * 2014-6-30-下午2:45:23
 * 2014鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.gui.locknet.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.gui.locknet.ForceControler.Band;
import com.walktour.gui.locknet.ForceControler.ForceNet;
import com.walktour.gui.locknet.ForceManager;

/**
 * LockBandProgress
 *
 * 2014-6-30 下午2:45:23
 *
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class LockBandProgress  extends AsyncTask<Void,Void,Boolean>{
	private Context context;
	private ProgressDialog progressDialog;
	private Band[] lockBands;
	private ForceNet netType;
	private OnDialogChangeListener callback;
	public LockBandProgress(Context context ,ForceNet netType,Band[] lockBands,
							OnDialogChangeListener callback){
		this.context = context;
		this.lockBands = lockBands;
		this.netType = netType;
		this.callback = callback;
	}

	@Override
	protected void onPreExecute() {
		showProgressDialog(context.getString(R.string.exe_info),false);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		//Deviceinfo deviceinfo = Deviceinfo.getInstance();
		ForceManager.getInstance().lockBand(context,netType, lockBands);
		/*
		if (deviceinfo.isHasNBModule()) {
			ForceManager.getInstance().lockBand(context,netType, lockBands);
		} else if (deviceinfo.isSamsungCustomRom()) {
			ForceManager.getInstance().lockBand(context,netType, lockBands);
		} else {
			String strDeviceModel = deviceinfo.getDevicemodel();
			if ((strDeviceModel.startsWith("vivo")) || (strDeviceModel.startsWith("OPPO"))) {
				ForceManager.getInstance().lockBand(context, netType, lockBands);
			} else{
				switch (strDeviceModel) {
					case "ZTENX569J":
					case "SM-G9600": {
						ForceManager.getInstance().lockBand(context, netType, lockBands);
						break;
					}
					default:
						ForceManager.getInstance().lockBand(netType, lockBands);
						break;
				}
			}
		}
		*/
		ForceManager.getInstance().saveLockNet(context, netType);
		ForceManager.getInstance().saveLockBand(context, lockBands);
		return true;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		dismissProgress();
		if( callback!=null ){
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
