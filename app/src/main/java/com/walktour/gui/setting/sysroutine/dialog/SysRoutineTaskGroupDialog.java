package com.walktour.gui.setting.sysroutine.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.WalktourConst;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;

/**
 * 任务组设置对话框
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("InflateParams")
public class SysRoutineTaskGroupDialog  extends BasicDialog implements OnCheckedChangeListener {

	/** 建设类 */
	private Builder builder;
	/** 上下文 */
	private Context mContext;

	/** 参数存储 */
	private SharedPreferences preferences;
	public SysRoutineTaskGroupDialog(Context context, Builder builder) {
		super(context);
		this.mContext = context;
		this.builder = builder;
		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		this.init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.sys_routine_setting_taskgroup, null);
		builder.setTitle(R.string.droidwall_platform_task_group_short);
		builder.setView(layout);
		CheckBox check = (CheckBox) layout.findViewById(R.id.task_group_checkbox);
		check.setChecked(WalktourApplication.isExitGroup());
		check.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		SharePreferencesUtil.getInstance(mContext).saveBoolean(WalktourConst.SYS_SETTING_taskgroup_control, isChecked);
	}

}
