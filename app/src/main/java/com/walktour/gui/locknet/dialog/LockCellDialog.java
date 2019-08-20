package com.walktour.gui.locknet.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.locknet.ForceControler.Band;
import com.walktour.gui.locknet.ForceControler.ForceNet;
import com.walktour.gui.locknet.ForceManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 锁定小区对话框
 * 
 * @author jianchao.wang
 *
 */
@SuppressLint("InflateParams")
public class LockCellDialog {
	/** 上下文 */
	private Activity context;
	/** 强制信息管理类 */
	private ForceManager mForceMgr;
	/** 回调方法 */
	private OnDialogChangeListener callback;

	public LockCellDialog(Activity context, OnDialogChangeListener diagListener) {
		this.context = context;
		this.callback = diagListener;
		mForceMgr = ForceManager.getInstance();

	}

	public void show() {
		Deviceinfo deviceinfo = Deviceinfo.getInstance();
		if (ApplicationModel.getInstance().isNBTest()) {
			showNbIot();
		} else {
			showNormal();
		}
	}

	private void showNormal() {
		// 设备配置文件
		Deviceinfo device = Deviceinfo.getInstance();
		Set<String> types = device.getNettypes();
		Iterator<String> interator = types.iterator();

		// 增加可以锁频的网络类型
		List<String> netList = new ArrayList<>();
		while (interator.hasNext()) {
			String network = interator.next();
			if (network.equals(Deviceinfo.NET_TYPES_WCDMA) || network.equals(Deviceinfo.NET_TYPES_LTE)) {
				netList.add(network);
			}
		}
		final String[] networks = new String[netList.size()];
		netList.toArray(networks);

		if (!Deviceinfo.getInstance().isSamsungCustomRom())
		{
			for (int i = 0; i < networks.length; i++) {
				if (networks[i].equals(Deviceinfo.NET_TYPES_WCDMA)) {
					networks[i] = this.context.getString(R.string.lock_device_cell_wcdma);
				}
			}
		}

		new BasicDialog.Builder(context).setTitle(R.string.locl_lock_area)
				.setItems(networks, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						if (networks[which].startsWith(Deviceinfo.NET_TYPES_WCDMA)) {
							if (Deviceinfo.getInstance().isSamsungCustomRom()) {
								showCellSetDialog(networks[which]);
							} else {
								mForceMgr.saveLockCell(context, new String[] { Deviceinfo.NET_TYPES_WCDMA });
								if (callback != null)
									callback.onPositive();
							}
						} else
							showCellSetDialog(networks[which]);
					}
				}).show();
	}

	/**
	 * 显示小区对话框
	 * 
	 * @param netType
	 *          网络类型
	 */
	protected void showCellSetDialog(final String netType) {
		LayoutInflater factory = LayoutInflater.from(this.context);

		final View view = factory.inflate(R.layout.lock_dev_cell_lte_edit, null);
		final EditText earfcnText = (EditText) view.findViewById(R.id.earfcn_edit);
		final EditText pciText = (EditText) view.findViewById(R.id.pci_edit);
		final Spinner bandSpinner = (Spinner) view.findViewById(R.id.band_select_edit);

		ArrayList<Band> bands = Band.getBandsByNetType(netType);
		if (bands != null) {
			ArrayAdapter<String> bandList = new ArrayAdapter<>(context, R.layout.simple_spinner_custom_layout,
					Band.bandArrayToNames(bands));

			bandList.setDropDownViewResource(R.layout.spinner_dropdown_item);
			bandSpinner.setAdapter(bandList);
		}

		String cellParams = mForceMgr.getLockCell(context);
		if (cellParams.trim().length() > 0) {
			String[] params = cellParams.split(",");
			if (params.length == 4 && netType.equals(params[0])) {
				if (bands != null) {
					for (int i = 0; i < bands.size(); i++) {
						if (bands.get(i).toString().equals(params[1])) {
							bandSpinner.setSelection(i);
							break;
						}
					}
				}
				earfcnText.setText(params[2]);
				pciText.setText(params[3]);
			}
		}

		new BasicDialog.Builder(context).setTitle(netType).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String earfcn = earfcnText.getText().toString().trim();
						String pci = pciText.getText().toString().trim();
						String band = String.valueOf(bandSpinner.getSelectedItem().toString());

						if (earfcn.length() > 0 && Verify.checknum(earfcn) && Integer.parseInt(earfcn) < 65535 && pci.length() > 0
								&& Verify.checknum(pci) && Integer.parseInt(pci) < 504) {
							new LockCellProgress(context,
									netType.equals(Deviceinfo.NET_TYPES_WCDMA) ? ForceNet.NET_WCDMA : ForceNet.NET_LTE, callback, band,
									earfcn, pci).execute();
						} else {
							String[] cellParams = mForceMgr.getLockCell(context).split(",");
							if (cellParams.length == 3 && cellParams[0].equals(netType)) {
								earfcnText.setText(cellParams[1]);
								pciText.setText(cellParams[2]);
							} else {
								earfcnText.setText("");
								pciText.setText("");
							}
							Toast.makeText(context, context.getString(R.string.sc_channels_Correct), Toast.LENGTH_LONG).show();
						}
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

	private void showNbIot() {
		LayoutInflater factory = LayoutInflater.from(this.context);

		final View view = factory.inflate(R.layout.lock_dev_cell_lte_edit, null);
		RelativeLayout relativeLayoutBand = (RelativeLayout )view.findViewById(R.id.lock_dev_cell_lte_band_select_layout);
		relativeLayoutBand.setVisibility(View.GONE);

		final EditText earfcnText = (EditText) view.findViewById(R.id.earfcn_edit);
		final EditText pciText = (EditText) view.findViewById(R.id.pci_edit);

		new BasicDialog.Builder(context).setTitle(R.string.locl_lock_area).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String earfcn = earfcnText.getText().toString().trim();
						String pci = pciText.getText().toString().trim();

						if (earfcn.length() > 0 && Verify.checknum(earfcn) && Integer.parseInt(earfcn) < 65535 && pci.length() > 0
								&& Verify.checknum(pci) && Integer.parseInt(pci) < 504) {
							new LockCellProgress(context, ForceNet.NET_LTE, callback, earfcn, pci).execute();
						} else {
							Toast.makeText(context, context.getString(R.string.sc_channels_Correct), Toast.LENGTH_LONG).show();
						}
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

}
