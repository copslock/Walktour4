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

import java.util.ArrayList;

/**
 * LockBandProgress
 *
 * 2014-6-30 下午2:45:23
 *
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class LockNetworkProgress  extends AsyncTask<Void,Void,Boolean>{
	private Context context;
	private ProgressDialog progressDialog;
	private ForceNet network;
	OnDialogChangeListener callback;
	public LockNetworkProgress(Context context,ForceNet network,OnDialogChangeListener callback){
		this.context = context;
		this.network = network;
		this.callback = callback;
	}

	@Override
	protected void onPreExecute() {
		showProgressDialog(context.getString(R.string.exe_info),false);
	}

	@Override
	protected Boolean doInBackground(Void... params) {

		ForceManager.getInstance().saveLockNet(context, network);

		//锁定FDD-LTE或TDD_LTE时，是通过锁LTE特定频段实现
		if(network==ForceNet.NET_FDD_LTE){
			ArrayList<Band> fddBands = Band.getFDD_LTEBands();
			Band[] fddArray = new Band[fddBands.size()];
			fddBands.toArray(fddArray);
			String strDeviceModel = Deviceinfo.getInstance().getDevicemodel();
			if((strDeviceModel.equals("ZTENX569J")) || (strDeviceModel.startsWith("vivo")))
				ForceManager.getInstance().lockBand(context,network, fddArray);
			else
				ForceManager.getInstance().lockBand(network,fddArray );
			ForceManager.getInstance().saveLockBand(context, fddArray);
		}
		else if(network==ForceNet.NET_TDD_LTE){
			ArrayList<Band> tddBands = Band.getTDD_LTEBands();
			Band[] tddArray = new Band[tddBands.size()];
			tddBands.toArray(tddArray);
			String strDeviceModel = Deviceinfo.getInstance().getDevicemodel();
			if ((strDeviceModel.equals("ZTENX569J")) || (strDeviceModel.startsWith("vivo")))
				ForceManager.getInstance().lockBand(context,network, tddArray);
			else
				ForceManager.getInstance().lockBand(network,tddArray );
			ForceManager.getInstance().saveLockBand(context, tddArray);
		}else {
			ForceManager.getInstance().lockNetwork(context, network);
			/*
			if (Deviceinfo.getInstance().isHasNBModule())
				ForceManager.getInstance().lockNetwork(context, network);
			else {
				Deviceinfo deviceinfo = Deviceinfo.getInstance();
				if (deviceinfo.isSamsungCustomRom()) {
					ForceManager.getInstance().lockNetwork(context, network);
				}  else {
					String devicemodel = deviceinfo.getDevicemodel();
					if (devicemodel.startsWith("vivo") ||(devicemodel.startsWith("OPPO"))) {
						ForceManager.getInstance().lockNetwork(context, network);
					} else if (devicemodel.equals("HuaweiMT7") || devicemodel.equals("ZTENX569J")) {
						ForceManager.getInstance().lockNetwork(context, network);
					} else {
						ForceManager.getInstance().lockNetwork(network);
					}
				}
			}
			*/
		}

		return true;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		dismissProgress();
		if(callback!=null){
			String deviceModel = Deviceinfo.getInstance().getDevicemodel();
			if (deviceModel.startsWith("vivo")) {
				callback.onLockPositive(ForceManager.KEY_LOCK_NET);
			} else
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
