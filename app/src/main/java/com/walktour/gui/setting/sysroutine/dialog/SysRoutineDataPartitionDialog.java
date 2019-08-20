package com.walktour.gui.setting.sysroutine.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.MyKeyListener;

/**
 * 数据分割设置对话框
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("InflateParams")
public class SysRoutineDataPartitionDialog extends BasicDialog implements OnClickListener {
	/** 建设类 */
	private Builder builder;
	/** 上下文 */
	private Context mContext;
	/** 数据分割方式 */
	private TextView datacatupTypeTextView;
	/** 数据分割大小编辑 */
	private EditText cutupSizeEditText;
	/** 数据分割大小显示 */
	private TextView datacatupSizeTextView;
	/** 常规设置配置文件 */
	private ConfigRoutine configRoutine;
	/** 当前视图 */
	private View layout;

	public SysRoutineDataPartitionDialog(Context context, Builder builder, ConfigRoutine configRoutine) {
		super(context);
		this.mContext = context;
		this.builder = builder;
		this.configRoutine = configRoutine;
		this.init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.sys_routine_setting_data_partition, null);
		builder.setTitle(R.string.sys_setting_data_partition);
		builder.setView(layout);
		layout.findViewById(R.id.setting_datacatup_type_layout).setOnClickListener(this);
		datacatupTypeTextView = (TextView) layout.findViewById(R.id.setting_datacatup_type_text);
		layout.findViewById(R.id.setting_datacatup_size_layout).setOnClickListener(this);
		datacatupSizeTextView = (TextView) layout.findViewById(R.id.setting_datacatup_size_text);
		View dataCutupAlertLayout = LayoutInflater.from(this.mContext).inflate(R.layout.alert_dialog_edittext, null);
		cutupSizeEditText = (EditText) dataCutupAlertLayout.findViewById(R.id.alert_textEditText);
		datacatupTypeTextView.setText(
				this.mContext.getResources().getStringArray(R.array.sys_setting_splitType)[configRoutine.getSplitType()]);
		datacatupSizeTextView.setText(configRoutine.getFileSize() + (configRoutine.getSplitType() == 0 ? " M" : " S"));
	}

	@Override
	public void onClick(View v) {
		Builder builder = new Builder(this.mContext);
		switch (v.getId()) {
		case R.id.setting_datacatup_type_layout:
			createCatupTypeDialog(builder);
			break;
		case R.id.setting_datacatup_size_layout:
			createDatacuteDialog(builder);
			break;
		}
		builder.show();
	}

	/**
	 * 显示数据分割时长或大小对话框<BR>
	 * [功能详细描述]
	 */
	private void createDatacuteDialog(BasicDialog.Builder builder) {
		View dataCutupAlertLayout = LayoutInflater.from(this.mContext)
				.inflate(R.layout.alert_dialog_edittext_partition_number, null);
		cutupSizeEditText = (EditText) dataCutupAlertLayout.findViewById(R.id.alert_textEditText);
		cutupSizeEditText.setText(String.valueOf(configRoutine.getFileSize()));
		cutupSizeEditText.setSelectAllOnFocus(true);
		cutupSizeEditText.setKeyListener(configRoutine.getSplitType() == 0 ? new MyKeyListener().getIpKeyListener()
				: new MyKeyListener().getNumberKeyListener());
		builder.setIcon(android.R.drawable.ic_dialog_dialer)
				.setTitle(configRoutine.getSplitType() == 0 ? R.string.sys_setting_datacutup_size
						: R.string.sys_setting_datacutup_time)
				.setView(dataCutupAlertLayout).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String size = cutupSizeEditText.getText().toString();
						if (size.length() >= 6) {
							Toast.makeText(mContext, mContext.getString(R.string.alert_inputtoolong), Toast.LENGTH_SHORT).show();
							return;
						} else if (Double.valueOf(size.trim().length() == 0 ? "0" : size) < 0) {
							Toast.makeText(mContext, mContext.getString(R.string.alert_inputagain), Toast.LENGTH_SHORT).show();
							return;
						}
						configRoutine.setFileSize(size);

						if (configRoutine.getSplitType() == 0) {
							datacatupSizeTextView.setText(size + " M");
						} else if (configRoutine.getSplitType() == 1) {
							datacatupSizeTextView.setText(size + " S");
						}

					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 生成数据分割方式对话框
	 * 
	 * @param builder
	 */
	private void createCatupTypeDialog(Builder builder) {
		final String[] split = this.mContext.getResources().getStringArray(R.array.sys_setting_splitType);
		builder.setTitle(this.mContext.getResources().getStringArray(R.array.sys_setting_routine)[7])
				.setSingleChoiceItems(split, configRoutine.getSplitType(), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						configRoutine.setSplitType(String.valueOf(which));
						datacatupTypeTextView.setText(split[which]);
						datacatupSizeTextView.setText((configRoutine.getSplitType() == 0 ? "20 M" : "3600 S"));
						if (which == 0) {
							((TextView) layout.findViewById(R.id.setting_datacatup_size_left_text))
									.setText(R.string.sys_setting_datacutup_size);
							configRoutine.setFileSize("20");
							cutupSizeEditText.setText("20");
						} else if (which == 1) {
							((TextView) layout.findViewById(R.id.setting_datacatup_size_left_text))
									.setText(R.string.sys_setting_datacutup_time);
							configRoutine.setFileSize("3600");
							cutupSizeEditText.setText("3600");
						}
						dialog.dismiss();
					}
				});
	}
}
