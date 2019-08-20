package com.walktour.gui.setting.sysroutine.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walktour.Utils.APNOperate;
import com.walktour.control.config.ConfigAPN;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

/**
 * 接入点设置对话框
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("InflateParams")
public class SysRoutineAPNDialog extends BasicDialog implements OnClickListener {

	/** 建设类 */
	private Builder builder;
	/** 互联网连接显示 */
	private TextView internetText;
	/** WAP连接显示 */
	private TextView wapText;
	/** 上下文 */
	private Context context;
	/** 配置文件 */
	private ConfigAPN configAPN;
	/** apn名称数组 */
	private String[] apnNames;
	/** apn操作 */
	private APNOperate apnOperate;

	public SysRoutineAPNDialog(Context context, Builder builder, ConfigAPN configAPN, APNOperate apnOperate) {
		super(context);
		this.context = context;
		this.builder = builder;
		this.configAPN = configAPN;
		this.apnNames = apnOperate.getAPNNameListByFirstEmpty(context);
		this.apnOperate = apnOperate;
		this.init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.sys_routine_setting_apn, null);
		builder.setTitle(R.string.sys_setting_access_point);
		builder.setView(layout);
		RelativeLayout internet = (RelativeLayout) layout.findViewById(R.id.setting_internt_layout);
		internet.setOnClickListener(this);
		this.internetText = (TextView) layout.findViewById(R.id.setting_internt_text);
		this.internetText.setText(configAPN.getDataAPN());
		RelativeLayout wap = (RelativeLayout) layout.findViewById(R.id.setting_wap_layout);
		wap.setOnClickListener(this);
		this.wapText = (TextView) layout.findViewById(R.id.setting_wap_text);
		this.wapText.setText(configAPN.getWapAPN());
	}

	@Override
	public void onClick(View v) {
		Builder builder = new Builder(this.context);
		switch (v.getId()) {
		case R.id.setting_internt_layout:
			builder.setTitle(R.string.sys_setting_internt_apn).setSingleChoiceItems(this.apnNames,
					apnOperate.getPositonFirstEmpty(this.configAPN.getDataAPN()), new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							configAPN.setDataAPN(apnNames[which]);
							internetText.setText(apnNames[which]);
							dialog.dismiss();
						}
					});
			break;
		case R.id.setting_wap_layout:
			builder.setTitle(R.string.sys_setting_internt_wapAPN).setSingleChoiceItems(
					this.apnNames, apnOperate.getPositonFirstEmpty(this.configAPN.getWapAPN()), new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							configAPN.setWapAPN(apnNames[which]);
							wapText.setText(apnNames[which]);
							dialog.dismiss();
						}
					});
			break;
		}
		builder.show();
	}

}
