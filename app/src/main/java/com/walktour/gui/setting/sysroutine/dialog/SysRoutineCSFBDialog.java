package com.walktour.gui.setting.sysroutine.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

/**
 * CSFB设置对话框
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("InflateParams")
public class SysRoutineCSFBDialog extends BasicDialog implements OnCheckedChangeListener {

	/** 建设类 */
	private Builder builder;
	/** 上下文 */
	private Context mContext;
	/** 服务器管理类 */
	private ServerManager mServer;

	public SysRoutineCSFBDialog(Context context, Builder builder, ServerManager mServer) {
		super(context);
		this.mContext = context;
		this.builder = builder;
		this.mServer = mServer;
		this.init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.sys_routine_setting_csfb, null);
		builder.setTitle(R.string.csfb_faild_analysis_main);
		builder.setView(layout);
		CheckBox check = (CheckBox) layout.findViewById(R.id.csfb_analysis_checkbox);
		check.setChecked(mServer.getCsfbAnalysis());
		check.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mServer.setCsfbAnalysis(isChecked);
		if (isChecked && mServer.getAutoTip()) {
			mServer.setAutoTip(false);
			Toast.makeText(this.mContext, R.string.sys_setting_csfbt_autof, Toast.LENGTH_LONG).show();
		}
	}

}
