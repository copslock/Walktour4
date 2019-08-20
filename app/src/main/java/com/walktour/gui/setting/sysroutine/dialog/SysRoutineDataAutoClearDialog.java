package com.walktour.gui.setting.sysroutine.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
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
 * 指定清除数据设置对话框
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("InflateParams")
public class SysRoutineDataAutoClearDialog extends BasicDialog implements OnClickListener {
	/** 建设类 */
	private Builder builder;
	/** 上下文 */
	private Context mContext;
	/** 常规设置配置文件 */
	private ConfigRoutine configRoutine;
	/** 数据清理 */
	private TextView autoClearText;
	/** 保留天数 */
	private TextView keepDayText;
	/** 保留天数行 */
	private View keepDayLayout;

	public SysRoutineDataAutoClearDialog(Context context, Builder builder, ConfigRoutine configRoutine) {
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
		View layout = inflater.inflate(R.layout.sys_routine_setting_data_auto_clear, null);
		builder.setTitle(R.string.str_auto_clear);
		builder.setView(layout);
		layout.findViewById(R.id.auto_clear_layout).setOnClickListener(this);
		layout.findViewById(R.id.data_keep_layout).setOnClickListener(this);
		this.autoClearText = (TextView) layout.findViewById(R.id.auto_clear_text);
		this.autoClearText.setText(configRoutine.isAutoDelete(this.mContext) ? this.mContext.getResources().getStringArray(
				R.array.public_yn)[1] : this.mContext.getResources().getStringArray(R.array.public_yn)[0]);
		this.keepDayLayout = layout.findViewById(R.id.data_keep_layout);
		this.keepDayLayout.setVisibility(configRoutine.isAutoDelete(this.mContext) ? View.VISIBLE : View.GONE);
		this.keepDayText = (TextView) layout.findViewById(R.id.data_keep_text);
		this.keepDayText.setText(String.valueOf(configRoutine.getAutoDeleteDay(this.mContext)));
	}

	@Override
	public void onClick(View v) {
		Builder builder = new Builder(this.mContext);
		switch (v.getId()) {
		case R.id.auto_clear_layout:
			createAutoClearDialog(builder);
			break;
		case R.id.data_keep_layout:
			createDataKeepDialog(builder);
			break;
		}
		builder.show();
	}

	/**
	 * 生成保留天数对话框
	 * 
	 * @param builder
	 */
	private void createDataKeepDialog(Builder builder) {
		final View dataKeepAlertLayout = LayoutInflater.from(this.mContext).inflate(R.layout.alert_dialog_edittext, null);
		final EditText keepdataET = (EditText) dataKeepAlertLayout.findViewById(R.id.alert_textEditText);
		keepdataET.setText(String.valueOf(configRoutine.getAutoDeleteDay(this.mContext)));
		keepdataET.setSelectAllOnFocus(true);
		keepdataET.setKeyListener(new MyKeyListener().getIntegerKeyListener());
		keepdataET.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
		builder.setTitle(R.string.str_data_keep_data).setView(dataKeepAlertLayout)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 找到对话框的EditText ,注意这里必须是
						// textEntryView.findViewById
						EditText alert_EditText = (EditText) dataKeepAlertLayout.findViewById(R.id.alert_textEditText);
						try {
							int day = Integer.parseInt(alert_EditText.getText().toString());
							if (day > 0 && day < 100) {
								configRoutine.setAutoDeleteDay(mContext, day);
								keepDayText.setText(String.valueOf(day));
							} else {
								Toast.makeText(mContext, R.string.str_data_keep_input, Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							dialog.dismiss();
						}
					}
				}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						keepdataET.setText(String.valueOf(configRoutine.getAutoDeleteDay(mContext)));
						dialog.dismiss();
					}
				}).setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						keepdataET.setText(String.valueOf(configRoutine.getAutoDeleteDay(mContext)));
						dialog.dismiss();
					}
				});

	}

	/**
	 * 生成自动清理对话框
	 * 
	 * @param builder
	 */
	private void createAutoClearDialog(Builder builder) {
		builder.setTitle(R.string.str_auto_clear).setSingleChoiceItems(R.array.public_yn,
				configRoutine.isAutoDelete(this.mContext) ? 1 : 0, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							configRoutine.setAutoDelete(mContext, false);
							autoClearText.setText(mContext.getResources().getStringArray(R.array.public_yn)[0]);
							keepDayLayout.setVisibility(View.GONE);
							break;
						case 1:
							configRoutine.setAutoDelete(mContext, true);
							autoClearText.setText(mContext.getResources().getStringArray(R.array.public_yn)[1]);
							keepDayLayout.setVisibility(View.VISIBLE);
							break;
						}
						dialog.dismiss();
					}
				});
	}

}
