package com.walktour.gui.setting.eventfilter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.colorpicker.ColorPanelView;
import com.walktour.framework.view.colorpicker.ColorPickerDialog;
import com.walktour.gui.R;
import com.walktour.gui.setting.customevent.CustomIcomAdatper;
import com.walktour.gui.setting.eventfilter.model.EventFilterSetModel;

import java.io.File;

/**
 * 事件过滤设置对话框
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("InflateParams")
public class EventFilterDialog extends BasicDialog implements OnClickListener {

	/** 设置的对象 */
	private EventFilterSetModel filter;
	/** 建设类 */
	private Builder builder;
	/** 显示在列表勾选框 */
	private CheckBox showInList;
	/** 显示在地图勾选框 */
	private CheckBox showInMap;
	/** 显示在图表勾选框 */
	private CheckBox showInChart;
	/** 显示颜色选择框 */
	private ColorPanelView showColor;
	/** 显示在地图上的图标 */
	private ImageView mapImage;
	/** 显示在地图上的图标路径 */
	private String imagePath;
	/** 上下文 */
	private Context context;

	public EventFilterDialog(Context context, EventFilterSetModel filter) {
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
		View layout = LayoutInflater.from(this.context).inflate(R.layout.event_filter_dialog, null);
		builder.setView(layout);
		this.showInList = (CheckBox) layout.findViewById(R.id.show_in_list);
		this.showInList.setChecked(this.filter.isShowList());
		this.showInMap = (CheckBox) layout.findViewById(R.id.show_in_map);
		this.showInMap.setChecked(this.filter.isShowMap());
		this.showInChart = (CheckBox) layout.findViewById(R.id.show_in_chart);
		this.showInChart.setChecked(this.filter.isShowChart());
		this.showColor = (ColorPanelView) layout.findViewById(R.id.show_color);
		this.showColor.setColor(this.filter.getColor());
		this.showColor.setOnClickListener(this);
		this.mapImage = (ImageView) layout.findViewById(R.id.show_in_map_image);
		this.mapImage.setImageDrawable(Drawable.createFromPath(filter.getImagePath()));
		LinearLayout imageLayout = (LinearLayout) layout.findViewById(R.id.show_in_map_image_layout);
		imageLayout.setOnClickListener(this);
		if (this.filter.getType() != EventFilterSetModel.TYPE_EVENT_DETAIL)
			imageLayout.setVisibility(View.GONE);
	}

	/**
	 * 创建颜色选择器对话框<BR>
	 * [功能详细描述]
	 * 
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
	public EventFilterSetModel getResult() {
		EventFilterSetModel result = new EventFilterSetModel("");
		result.setShowList(this.showInList.isChecked());
		result.setShowMap(this.showInMap.isChecked());
		result.setShowChart(this.showInChart.isChecked());
		result.setColor(this.showColor.getColor());
		result.setImagePath(this.imagePath);
		return result;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.show_color:
			this.onClickColorPickerDialog();
			break;
		case R.id.show_in_map_image_layout:
			this.showIconDialog();
			break;
		}
	}

	/**
	 * 显示图标选择窗口 showIconDialog 函数功能：
	 * 
	 */
	private void showIconDialog() {
		String iconDir = AppFilePathUtil.getInstance().getSDCardBaseDirectory("icons");
		GridView gridView = (GridView) LayoutInflater.from(this.context).inflate(R.layout.gridview_custom_event, null);

		final CustomIcomAdatper gridAdatper = new CustomIcomAdatper(super.getContext(), new File(iconDir));
		gridView.setAdapter(gridAdatper);

		final BasicDialog dialog = new BasicDialog.Builder(super.getContext()).setView(gridView).setTitle(filter.getName())
				.create();
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				imagePath = gridAdatper.getItem(position).getAbsolutePath();
				mapImage.setImageDrawable(Drawable.createFromPath(imagePath));
				dialog.dismiss();
			}
		});

		dialog.show();
	}

}
