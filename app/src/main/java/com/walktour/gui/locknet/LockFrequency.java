package com.walktour.gui.locknet;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.MyKeyListener;
import com.walktour.gui.locknet.ForceControler.ForceNet;

/**
 *
 * LockFrequency 锁定频点页面 2013-11-6 下午2:33:47
 *
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class LockFrequency extends LockBasicActivity {

	private String[] lteBandValues;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lteBandValues = getBaseContext().getResources().getStringArray(R.array.lock_lte_band_value);
	}

	private void showLockPointView(final int id) {
		BasicDialog.Builder builder = new BasicDialog.Builder(this);
		final Context mContext = this;
		final View viewLayout = inflater.inflate(R.layout.alert_dialog_band_edittext, null);
		TextView tv = (TextView) viewLayout.findViewById(R.id.alert_textView);
		final LinearLayout lteBand = (LinearLayout) viewLayout.findViewById(R.id.lte_band);

		if (id == R.id.setting_layout_2g) {
			tv.setText("BCCH");
		} else if (id == R.id.setting_layout_3G) {
			tv.setText("UARFCN");
		} else if (id == R.id.setting_layout_lte) {
			tv.setText("EARFCN");
			lteBand.setVisibility(View.VISIBLE);
		}

		final EditText keepdataET = (EditText) viewLayout.findViewById(R.id.alert_textEditText);
		keepdataET.setSelectAllOnFocus(true);
		keepdataET.setKeyListener(new MyKeyListener().getIntegerKeyListener());
		builder.setTitle(R.string.locl_lock_point).setView(viewLayout)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 找到对话框的EditText ,注意这里必须是
						// textEntryView.findViewById
						boolean hasError = false;
						int index = 0;
						String error;
						String selected = "0";
						if (keepdataET.getText() != null && keepdataET.getText().length() > 0) {
							int value = Integer.parseInt(keepdataET.getText().toString());
							if (id == R.id.setting_layout_lte) {
								index = ((Spinner) viewLayout.findViewById(R.id.edit_lte_band)).getSelectedItemPosition();
								selected = lteBandValues[index];
								error = getBaseContext().getString(R.string.lock_lte_band_error);
								if (selected.equals("1") && !(value >= 0 && value <= 599)) {
									keepdataET.setError(error);
									hasError = true;
								} else if (selected.equals("28") && !(value >= 37750 && value <= 38249)) {
									keepdataET.setError(error);
									hasError = true;
								} else if (selected.equals("29") && !(value >= 38250 && value <= 38649)) {
									keepdataET.setError(error);
									hasError = true;
								} else if (selected.equals("30") && !(value >= 38650 && value <= 39649)) {
									keepdataET.setError(error);
									hasError = true;
								} else {
									keepdataET.setError(null);
								}
							} else {
								keepdataET.setError(null);
							}
						} else {
							hasError = true;
							error = getBaseContext().getString(R.string.str_inputerror);
							keepdataET.setError(error);
						}

						if (!hasError) {
							EditText alert_EditText = (EditText) viewLayout.findViewById(R.id.alert_textEditText);
							String[] arg = new String[2];
							if (id == R.id.setting_layout_lte) {
								arg[0] = selected;
								arg[1] = alert_EditText.getText().toString();
							} else {
								arg[1] = alert_EditText.getText().toString();
							}

							ForceNet net = net2G;
							if (id == R.id.setting_layout_2g) {
								net = net2G;
							} else if (id == R.id.setting_layout_3G) {
								net = net3G;
							} else if (id == R.id.setting_layout_lte) {
								net = net4G;
							}
							mForceMgr.lockFrequency(mContext, net, arg);
							showProgressDialog(getString(R.string.exe_info));

						}
						dismissDialog(dialog, !hasError);
					}
				}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dismissDialog(dialog, true);
				dialog.dismiss();
			}
		}).setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dismissDialog(dialog, true);
				dialog.dismiss();
			}
		}).show();
	}

	/**
	 * @see com.walktour.gui.locknet.LockBasicActivity#titleStringId()
	 */
	@Override
	protected int titleStringId() {
		return R.string.locl_lock_point;
	}

	/**
	 * @see com.walktour.gui.locknet.LockBasicActivity#setViewByDeviceInfo()
	 */
	@Override
	protected void setViewByDeviceInfo() {
		// 先查询是否有锁定当前频点功能
		Deviceinfo.LockInfo info = Deviceinfo.getInstance().getLockInfo();
		layoutCurrent.setVisibility(info.hasLockCurrentFreq() ? View.VISIBLE : View.GONE);
		boolean canLockCurrentCell = mForceMgr.queryFrequency(ForceNet.NET_AUTO);
		txtCurrent.setText(canLockCurrentCell ? getString(R.string.locl_lock_none) : getString(R.string.lock_current_freq));
	}

	/**
	 * @see com.walktour.gui.locknet.LockBasicActivity#showLockDialog(int)
	 */
	@Override
	protected void showLockDialog(int layoutViewId) {
		showLockPointView(layoutViewId);
	}

}
