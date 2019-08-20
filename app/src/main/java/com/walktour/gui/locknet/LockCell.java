package com.walktour.gui.locknet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.MyKeyListener;
import com.walktour.gui.locknet.ForceControler.ForceNet;

/**
 * 锁定小区界面
 * 
 * @author jianchao.wang
 *
 */
public class LockCell extends LockBasicActivity {

	@SuppressLint("InflateParams")
	private void showLockCellView(final int id, String cellValue, String pciValue) {
		BasicDialog.Builder builder = new BasicDialog.Builder(this);
		final Context mContext = this;
		final View viewLayout = inflater.inflate(R.layout.alert_dialog_cell_lock, null);
		TextView cellText = (TextView) viewLayout.findViewById(R.id.point_textView);
		TextView cpiText = (TextView) viewLayout.findViewById(R.id.cpi_textView);
		if (id == R.id.setting_layout_2g) {
			cellText.setText("BCCH");
			cpiText.setText("BISC");
		} else if (id == R.id.setting_layout_3G) {
			cellText.setText("UARFCN");
			cpiText.setText("PCI");
		} else if (id == R.id.setting_layout_lte) {
			cellText.setText("EARFCN");
			cpiText.setText("PCI");
		}

		final EditText cellEditText = (EditText) viewLayout.findViewById(R.id.alert_pointEditText);
		cellEditText.setText(cellValue);
		cellEditText.setSelectAllOnFocus(true);
		cellEditText.setKeyListener(new MyKeyListener().getIntegerKeyListener());

		final EditText pciEditText = (EditText) viewLayout.findViewById(R.id.alert_cpiEditText);
		pciEditText.setText(pciValue);
		pciEditText.setKeyListener(new MyKeyListener().getIntegerKeyListener());

		builder.setTitle(R.string.locl_lock_area).setView(viewLayout)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 找到对话框的EditText ,注意这里必须是
						// textEntryView.findViewById
						EditText cellEditText = (EditText) viewLayout.findViewById(R.id.alert_pointEditText);
						EditText pciEditText = (EditText) viewLayout.findViewById(R.id.alert_cpiEditText);

						boolean hasError = false;

						if (cellEditText.getText().length() < 1) {
							hasError = true;
							cellEditText.setError(getBaseContext().getString(R.string.str_inputerror));
						} else {
							cellEditText.setError(null);
						}

						if (pciEditText.getText().length() < 1) {
							hasError = true;
							pciEditText.setError(getBaseContext().getString(R.string.str_inputerror));
						} else {
							pciEditText.setError(null);
						}

						if (!hasError) {
							String arg1 = cellEditText.getText().toString();
							String arg2 = pciEditText.getText().toString();
							ForceNet net = net2G;
							if (id == R.id.setting_layout_2g) {
								net = net2G;
							} else if (id == R.id.setting_layout_3G) {
								net = net3G;
							} else if (id == R.id.setting_layout_lte) {
								net = net4G;
							}
							mForceMgr.lockCell(mContext, net, arg1, arg2);
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
	 * @see com.walktour.gui.locknet.LockBasicActivity#setViewByDeviceInfo()
	 */
	@Override
	protected void setViewByDeviceInfo() {
		// 先查询是否有锁定当前小区功能
		Deviceinfo.LockInfo info = Deviceinfo.getInstance().getLockInfo();
		layoutCurrent.setVisibility(info.hasLockCurrentCell() ? View.VISIBLE : View.GONE);
		boolean canLockCurrentCell = mForceMgr.queryCell(ForceNet.NET_AUTO);
		txtCurrent.setText(canLockCurrentCell ? getString(R.string.locl_lock_none) : getString(R.string.lock_current_cell));
	}

	/**
	 * @see com.walktour.gui.locknet.LockBasicActivity#showLockDialog(int)
	 */
	@Override
	protected void showLockDialog(int layoutViewId) {
		showLockCellView(layoutViewId, "", "");
	}

	/**
	 * @see com.walktour.gui.locknet.LockBasicActivity#titleStringId()
	 */
	@Override
	protected int titleStringId() {
		return R.string.locl_lock_area;
	}
}
