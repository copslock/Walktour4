package com.walktour.gui.locknet.dialog;

import android.app.Activity;
import android.content.DialogInterface;

import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

/**
 * 
 * UnlockAllDialog
 * 
 * 2014-6-30 下午4:37:06
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class UnlockAllDialog {
	/** 上下文 */
	private Activity context;
	private OnDialogChangeListener callback;
	private boolean isUnlockCell = false;

	public UnlockAllDialog(Activity context, boolean isUnlockCell, OnDialogChangeListener diagListener) {
		callback = diagListener;
		this.context = context;
		this.isUnlockCell = isUnlockCell;
	}

	public void show() {
		new BasicDialog.Builder(context).setTitle(R.string.lock).setMessage(R.string.locl_lock_none)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new UnlockAllProgress(context, isUnlockCell, callback).execute();
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

}
