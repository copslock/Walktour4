package com.walktour.gui.setting.sysroutine.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.setting.sysroutine.SysRoutineKPISettingActivity;
import com.walktour.gui.singlestation.setting.activity.SettingActivity;

/**
 * Go/No-go设置对话框
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("InflateParams")
public class SysRoutineNogoDialog extends BasicDialog implements OnClickListener {

	/** 建设类 */
	private Builder builder;
	/** 上下文 */
	private Context context;
	/** 服务器管理类 */
	private ServerManager mServer;

	public SysRoutineNogoDialog(Context context, Builder builder) {
		super(context);
		this.context = context;
		this.builder = builder;
		this.mServer = ServerManager.getInstance(context);
		this.init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.sys_routine_setting_nogo, null);
		builder.setTitle(R.string.sys_nogo_title);
		builder.setView(layout);
		CheckBox autoTip = (CheckBox) layout.findViewById(R.id.auto_show_result_checkbox);
		autoTip.setChecked(this.mServer.getAutoTip());
		autoTip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mServer.setAutoTip(isChecked);
				if (isChecked && mServer.getCsfbAnalysis()) {
					mServer.setCsfbAnalysis(false);
					Toast.makeText(context, R.string.sys_setting_autot_csfbf, Toast.LENGTH_LONG).show();
				}
			}
		});
		Button threshold = (Button) layout.findViewById(R.id.sys_setting_threshold);
		threshold.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		ApplicationModel applicationModel = ApplicationModel.getInstance();
		if (WalkStruct.SceneType.SingleSite == applicationModel.getSelectScene())
			this.context.startActivity(new Intent(this.context, SettingActivity.class));
		else
			this.context.startActivity(new Intent(this.context, SysRoutineKPISettingActivity.class));
	}
}
