package com.walktour.gui.setting.msgfilter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.colorpicker.ColorPanelView;
import com.walktour.framework.view.colorpicker.ColorPickerDialog;
import com.walktour.gui.R;
import com.walktour.gui.setting.msgfilter.model.MsgFilterSetModel;

/**
 * 信令过滤设置对话框
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("InflateParams")
public class MsgFilterDialog extends BasicDialog implements OnClickListener {

	/** 设置的对象 */
	private MsgFilterSetModel filter;
	/** 建设类 */
	private Builder builder;
	/** 显示在列表勾选框 */
	private CheckBox showInList;
	/** 显示在地图勾选框 */
	private CheckBox showInMap;
	/** 显示颜色选择框 */
	private ColorPanelView showColor;
	/** 上下文 */
	private Context context;

	public MsgFilterDialog(Context context, MsgFilterSetModel filter) {
		super(context);
		this.context = context;
		this.filter = filter;
		builder = new BasicDialog.Builder(context);
		this.init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.message_filter_dialog, null);
		builder.setView(layout);
		this.showInList = (CheckBox) layout.findViewById(R.id.show_in_list);
		this.showInList.setChecked(this.filter.isShowList());
		this.showInMap = (CheckBox) layout.findViewById(R.id.show_in_map);
		this.showInMap.setChecked(this.filter.isShowMap());
		this.showColor = (ColorPanelView) layout.findViewById(R.id.show_color);
		this.showColor.setColor(this.filter.getColor());
		this.showColor.setOnClickListener(this);
	}

	/**
	 * 创建颜色选择器对话框<BR>
	 * [功能详细描述]
	 * 
	 * @param colorPanelView
	 * @param colorKey
	 */
	private void onClickColorPickerDialog() {
		int initialValue = this.showColor.getColor();
		final ColorPickerDialog colorDialog = new ColorPickerDialog(this.context, initialValue);
		colorDialog.setAlphaSliderVisible(false);
		colorDialog.builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showColor.setColor(colorDialog.getColor());
					}
				});
		colorDialog.builder.setNegativeButton(android.R.string.cancel);
		colorDialog.builder.show();
	}

	public Builder getBuilder() {
		return builder;
	}

	/**
	 * 返回设置的结果
	 * 
	 * @return
	 */
	public MsgFilterSetModel getResult() {
		MsgFilterSetModel result = new MsgFilterSetModel("");
		result.setShowList(this.showInList.isChecked());
		result.setShowMap(this.showInMap.isChecked());
		result.setColor(this.showColor.getColor());
		return result;
	}

	@Override
	public void onClick(View v) {
		this.onClickColorPickerDialog();
	}

}
