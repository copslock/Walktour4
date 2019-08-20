package com.walktour.gui.metro.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 进度条显示对话框
 * 
 * @author jianchao.wang
 *
 */
public class ProgressFragment extends DialogFragment {
	/** 传递的参数名称 */
	public static final String PROGRESS_MESSAGE = "progress_message";
	/** 进度条对话框 */
	private ProgressDialog dialog;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle bundle = this.getArguments();
		String message = bundle.getString(PROGRESS_MESSAGE);
		dialog = new ProgressDialog(getActivity());
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(message);
		return dialog;
	}

}
