package com.walktour.gui.setting;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.framework.view.colorpicker.ColorPanelView;
import com.walktour.framework.view.colorpicker.ColorPickerDialog;
import com.walktour.gui.R;
import com.walktour.model.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 基本信息的图表显示参数设置
 * 
 * @author jianchao.wang
 *
 */
@SuppressLint("InflateParams")
public class SysChart extends BasicActivity implements OnClickListener {
	public static final String tag = "Walktour.SysChart";

	public static final String KEY_PARA = "parameter";

	public static final String KEY_COLOR = "color";

	private List<BasicSpinner> lines = new ArrayList<BasicSpinner>();;

	private List<ColorPanelView> colors = new ArrayList<ColorPanelView>();

	/**
	 * 曲线颜色一
	 */
	// private View chatLineColorOne;

	/**
	 * 设置自定义参数
	 */
	private RelativeLayout settingCustomParam;

	private ListView list;

	/**
	 * 曲线参数下拉Spinner显示的参数（第一个为"无"）
	 */
	private String[] parametersInSpinner;

	static ArrayList<HashMap<String, Object>> listdata;

	static SimpleAdapter adapter;

	private BasicDialog dialog;

	private BasicDialog dialog1;
	/** 显示的曲线最大值 */
	private static int curveSize = 10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sys_chart);
		findView();
		initData();
		initValue();
	}

	private void findView() {
		LinearLayout curveLayout = initLinearLayout(R.id.curve_set_list);
		for (int i = 0; i < curveSize; i++) {
			View view = LayoutInflater.from(this).inflate(R.layout.sys_chart_row, null);
			curveLayout.addView(view);
			BasicSpinner para = (BasicSpinner) view.findViewById(R.id.SpinnerPara);
			this.lines.add(para);
			ColorPanelView color = (ColorPanelView) view.findViewById(R.id.SpinnerColor);
			color.setTag(i);
			color.setOnClickListener(this);
			TextView paraText = (TextView) view.findViewById(R.id.sys_chart_para_text);
//			paraText.setText(this.getResources().getString(R.string.sys_chart_line_text) + (i + 1));//曲线1,2
			paraText.setText("");//
			this.colors.add(color);
		}
		settingCustomParam = initRelativeLayout(R.id.setting_chart_custom_param);
	}

	/**
	 * 初始化数据<BR>
	 * 初始化控件原始数据
	 */
	private void initData() {
		ParameterSetting setting = ParameterSetting.getInstance();

		// Spinner of Parameter
		String[] parameterNames = setting.getParameterNames();// 真实参数名
		String[] parameterShortNames = setting.getParameterShortNames();// 显示的参数名简称
		String[] display = new String[parameterNames.length + 1];
		parametersInSpinner = new String[parameterNames.length + 1];

		// 第0项为"无"
		display[0] = getString(R.string.none);
		parametersInSpinner[0] = "null";
		// 从第一项开始添加
		for (int i = 1; i < display.length; i++) {
			display[i] = parameterShortNames[i - 1];
			parametersInSpinner[i] = parameterNames[i - 1];
			// if(display[i].equals("EcIo"))
			// {
			// ecioPos = i;
			// }
			// if(display[i].equals("FFER"))
			// {
			// fferPos = i;
			// }
			// if(display[i].equals("RxAgc"))
			// {
			// rxAgcPos = i;
			// }
			// if(display[i].equals("TxAgc"))
			// {
			// txAgcPos = i;
			// }
		}
		ArrayAdapter<String> adapter_para = new ArrayAdapter<String>(SysChart.this, R.layout.simple_spinner_custom_layout,
				display);
		adapter_para.setDropDownViewResource(R.layout.spinner_dropdown_item);
		for (int i = 0; i < this.lines.size(); i++) {
			BasicSpinner para = this.lines.get(i);
			para.setAdapter(adapter_para);
			if (i + 1 < adapter_para.getCount())
				para.setSelection(i + 1);
			para.setOnItemSelectedListener(this.paraSelectListener);
		}

		settingCustomParam.setOnClickListener(this);

	}// end method setView

	private void initValue() {
		Parameter[] chartParameters = ParameterSetting.getInstance().getChartLineParemeters();
		for (int i = 0; i < lines.size(); i++) {
			try {
				lines.get(i)
						.setSelection(ParameterSetting.getInstance().getPositionOfParameter(chartParameters[i].getId()) + 1);
				colors.get(i).setColor(chartParameters[i].getColor());
			} catch (Exception e) {
				lines.get(i).setSelection(0);
			}
		}
	}

	/**
	 * 根据View生成记录参数名和颜色的HashMap列表
	 * */
	private ArrayList<HashMap<String, Object>> genParametersFromView() {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < lines.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			// 获得所有曲线选择的参数
			int p1 = lines.get(i).getSelectedItemPosition();
			if (p1 != 0) {
				LogUtil.w("parameterName:", parametersInSpinner[p1]);
				map.put(SysChart.KEY_PARA, parametersInSpinner[p1]);
				map.put(SysChart.KEY_COLOR, colors.get(i).getColor());
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 参数选择监听：仅供para1,para2,para3,para4使用
	 * */
	private OnItemSelectedListener paraSelectListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// int selected[] = new int []{0,0,0,0};
			LogUtil.w(tag, "select parameter:" + parametersInSpinner[arg2]);

			for (int i = 0; i < lines.size(); i++) {
				if (lines.get(i).getId() != arg0.getId()) {
					if (lines.get(i).getSelectedItemPosition() == arg2) {
						lines.get(i).setSelection(0);

						Toast.makeText(getApplicationContext(), getString(R.string.sys_chart_paraToLine), Toast.LENGTH_LONG).show();
					}
				}
			}

			for (int i = 0; i < lines.size(); i++) {
				if (lines.get(i).getSelectedItemPosition() == 0) {
					colors.get(i).setVisibility(View.INVISIBLE);
				} else {
					colors.get(i).setVisibility(View.VISIBLE);
				}

			}

			// 修改配置文件中的曲线
			ParameterSetting setting = ParameterSetting.getInstance();
			setting.setChartLineParameters(SysChart.this.genParametersFromView());

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	};

	@Override
	// 添加菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.revert_default, menu);
		return true;
	}

	@Override
	// 菜单点击事件
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {

		case R.id.menu_revert_default:
			dialog = new BasicDialog.Builder(SysChart.this).setTitle(R.string.str_revert_default)
					.setMessage(R.string.str_revert_default_makesure)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// 根据不同手机类型写入不同的配置文件,目标文件名一样为config_map_chart.xml
							ParameterSetting.resetToDefaultFromFile(SysChart.this, true);
							ParameterSetting.getInstance().initialParameter();
							// findView();
							// initData();
							initValue();
						}
					}).setNegativeButton(R.string.str_cancle).show();
		}

		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_chart_custom_param:
			setCustomParams();
			break;
		case R.id.SpinnerColor:
			int index = Integer.parseInt(String.valueOf(v.getTag()));
			onClickColorPickerDialog(this.colors.get(index));
			break;
		default:
			break;
		}
	}

	/**
	 * 选择自定义参数
	 */
	@SuppressWarnings("deprecation")
	private void setCustomParams() {
		ParameterSetting setting = ParameterSetting.getInstance();
		final boolean[] checked = setting.getTableBooleans();
		// String[] parameterNames = setting.getParameterNames();
		String[] parameterShortNames = setting.getParameterShortNames();
		listdata = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < parameterShortNames.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemText", parameterShortNames[i]);
			map.put("ItemIcon", checked[i] ? R.drawable.btn_check_on : R.drawable.btn_check_off);
			map.put("ItemChecked", checked[i]);
			listdata.add(map);
		}
		LayoutInflater inflater = LayoutInflater.from(SysChart.this);
		View view = inflater.inflate(R.layout.list_chart, null);
		list = (ListView) view.findViewById(R.id.list);

		adapter = new SimpleAdapter(SysChart.this, listdata, R.layout.list_chart_item, new String[] { "ItemText",
				"ItemIcon" }, new int[] { R.id.ItemText, R.id.ItemIcon });
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, Object> map = listdata.get(position);
				boolean c = !Boolean.valueOf(map.get("ItemChecked").toString());

				if (c) {
					map.put("ItemIcon", R.drawable.btn_check_on);
				} else {
					map.put("ItemIcon", R.drawable.btn_check_off);
				}
				map.put("ItemChecked", c);
				// checked[position] = c;
				adapter.notifyDataSetChanged();
			}
		});

		DisplayMetrics metric = new DisplayMetrics();
		SysChart.this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		dialog1 = new BasicDialog.Builder(SysChart.this).setTitle(R.string.sys_chart_custom)
				.setView(view, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, (int) (350 * metric.density)))
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (int i = 0; i < listdata.size(); i++) {
							HashMap<String, Object> map = listdata.get(i);
							boolean c = Boolean.valueOf(map.get("ItemChecked").toString());
							checked[i] = c;
						}
						ParameterSetting.getInstance().setTableBooleans(checked);
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

	/**
	 * 创建颜色选择器对话框<BR>
	 * [功能详细描述]
	 * 
	 * @param colorPanelView
	 */
	public void onClickColorPickerDialog(final ColorPanelView colorPanelView) {
		int initialValue = colorPanelView.getColor();
		Log.d("mColorPicker", "initial value:" + initialValue);
		final ColorPickerDialog colorDialog = new ColorPickerDialog(this, initialValue);
		colorDialog.setAlphaSliderVisible(false);
		colorDialog.builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				colorPanelView.setColor(colorDialog.getColor());
			}
		});
		colorDialog.builder.setNegativeButton(android.R.string.cancel);
		colorDialog.builder.show();
	}

	/**
	 * 切换Spinner背景颜色<BR>
	 * [功能详细描述]
	 * 
	 * @param colorSpinner
	 * @param postion
	 */
	public void switchColor(Spinner colorSpinner, int postion) {
		switch (postion) {
		case 0:
			colorSpinner.setBackgroundColor(getResources().getColor(R.color.gray));
			break;
		case 1:
			colorSpinner.setBackgroundColor(getResources().getColor(R.color.white));
			break;
		case 2:
			colorSpinner.setBackgroundColor(getResources().getColor(R.color.black));
			break;
		case 3:
			colorSpinner.setBackgroundColor(getResources().getColor(R.color.red));
			break;
		case 4:
			colorSpinner.setBackgroundColor(getResources().getColor(R.color.yellow));
			break;
		case 5:
			colorSpinner.setBackgroundColor(getResources().getColor(R.color.green));
			break;
		case 6:
			colorSpinner.setBackgroundColor(Color.CYAN);
			break;
		case 7:
			colorSpinner.setBackgroundColor(getResources().getColor(R.color.blue));
			break;
		case 8:
			colorSpinner.setBackgroundColor(getResources().getColor(R.color.light_purple));
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dialog1 != null && dialog1.isShowing()) {
			dialog1.dismiss();
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		ParameterSetting setting = ParameterSetting.getInstance();
		setting.setChartLineParameters(SysChart.this.genParametersFromView());
		// 通知图表的界面更新
		Intent intent = new Intent();
		intent.setAction(WalkMessage.chartLineChanged);
		sendBroadcast(intent);
	}

}