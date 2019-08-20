package com.walktour.gui.setting;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.adapter.BaseDisplayAdapter;
import com.walktour.control.adapter.MutilChoiceArrayAdapter;
import com.walktour.control.config.ParameterSetting;
import com.walktour.customView.ListViewForScrollView;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.R;
import com.walktour.gui.highspeedrail.HighSpeedRailActivity;
import com.walktour.gui.metro.MetroSettingCityActivity;
import com.walktour.gui.newmap.basestation.BaseStationDownloadActivity;
import com.walktour.model.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@SuppressLint("UseSparseArrays")
public class SysMap extends BasicActivity implements OnClickListener {

	private static final String tag = "Walktour.SysMap";

	private Spinner gpsColor;
	private Spinner paraChoice;
	private boolean isInitalGps;
	private boolean isInitialParam;
	private CheckBox checkLegen;
	private CheckBox checkMark;
	/** 选择DT模式默认地图 */
	private Spinner dtDefaultMap;

	/** 轨迹形状选择 */
	private Spinner locusShapeSP;
	/** 轨迹大小 */
	private Spinner locusShapeSizeSP;

	private BasicDialog basicDialog;

	private BasicDialog basicDialog1;

	private SharePreferencesUtil mSharePreferencesUtil;

	private SharedPreferences defaultSharedPreferences;

	private ParameterSetting mParameterSet;

	public static final String BASE_DISPLAY_TYPE = "base_display_type";

	public static final String BASE_GSM = "base_gsm";
	public static final String BASE_WCDMA = "base_wcdma";
	public static final String BASE_TDSCDMA = "base_tdscdma";
	public static final String BASE_LTE = "base_lte";
	public static final String BASE_CDMA = "base_cdma";
	public static final String BASE_NB_IoT = "base_NB-IoT";

	/** 轨迹形状Key */
	public static final String LOCUS_SHARE = "locus_share";

	/** 轨迹形状大小key */
	public static final String LOCUS_SHARE_SIZE = "locus_share_size";
	private ApplicationModel appModel = ApplicationModel.getInstance();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sys_map); 
		mParameterSet = ParameterSetting.getInstance();
		mParameterSet.initMapLocusShape(this);
		defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(SysMap.this);
		mSharePreferencesUtil = SharePreferencesUtil.getInstance(SysMap.this.getApplicationContext());
		findView();
		initViews();
	}

	private void findView() {

		// findViewById
		gpsColor = (Spinner) findViewById(R.id.SpinnerGps);
		paraChoice = (Spinner) findViewById(R.id.SpinnerParameter);
		dtDefaultMap = (Spinner) findViewById(R.id.SpinnerDtMapdefault);
		locusShapeSP = initSpinner(R.id.locus_shape_spinner);
		locusShapeSizeSP = initSpinner(R.id.locus_shape_size_spinner);

		checkLegen = (CheckBox) findViewById(R.id.CheckBox01);
		checkMark = (CheckBox) findViewById(R.id.CheckBox02);
		checkLegen.setChecked(mParameterSet.isDisplayLegen());
		checkMark.setChecked(mParameterSet.isMarkAccurately());

		checkLegen.setOnCheckedChangeListener(onCheckListener);
		checkMark.setOnCheckedChangeListener(onCheckListener);

		findViewById(R.id.map_2g_param_btn).setOnClickListener(this);
		findViewById(R.id.map_3g_param_btn).setOnClickListener(this);
		findViewById(R.id.map_4g_param_btn).setOnClickListener(this);

		findViewById(R.id.cell_line_set_btn).setOnClickListener(this);
		findViewById(R.id.metro_route_download_btn).setOnClickListener(this);
		findViewById(R.id.gaotie_route_download_btn).setOnClickListener(this);
		findViewById(R.id.btn_base_params_threshold_setting).setOnClickListener(clickListener);
		findViewById(R.id.btn_base_params_go_or_nogo_setting).setOnClickListener(clickListener);
		 
		if(appModel.getSelectScene()==SceneType.HighSpeedRail){//高铁
			findViewById(R.id.dowloadgaotielayout).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.dowloadgaotielayout).setVisibility(View.GONE);
		}
		if(appModel.getSelectScene()==SceneType.Metro){//地铁
			findViewById(R.id.metro_setting_layout).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.metro_setting_layout).setVisibility(View.GONE);
		}
		
		
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_base_params_threshold_setting:
				startActivity(new Intent(SysMap.this, ParamsThresholdSettingActivity.class));
				break;
			case R.id.btn_base_params_go_or_nogo_setting:
				startActivity(new Intent(SysMap.this, GoOrNogoSettingActivity.class));
				break;
			default:
				break;
			}
		}
	};

	private OnCheckedChangeListener onCheckListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			if (arg0.getId() == R.id.CheckBox01) {
				mParameterSet.setDisplayLegen(arg1);
			} else if (arg0.getId() == R.id.CheckBox02) {
				mParameterSet.setMarkAccurately(arg1);
			}
		}
	};

	private void initViews() {
		// 获取当前的地图显示参数
		Parameter mapParemeter = mParameterSet.getMapParameter();

		// Adapter of Color Spinner
		String[] colors = getResources().getStringArray(R.array.sys_map_color);
		ArrayAdapter<String> adpt = new ArrayAdapter<String>(SysMap.this, R.layout.simple_spinner_custom_layout, colors);
		adpt.setDropDownViewResource(R.layout.spinner_dropdown_item);

		gpsColor.setAdapter(adpt);
		gpsColor.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				// 由于初始化Spinner时setSelection也会触发此listener，所以用isInitialParam来记录是否初始化
				mParameterSet.setGpsColor(mParameterSet.getColorOfPosition(arg2));
				if (isInitialParam) {
					// 发送地图参数改变的广播
					Intent intent = new Intent();
					intent.setAction(WalkMessage.mapGpsColorChanged);
					sendBroadcast(intent);
					LogUtil.v(tag, "gps has changed");
				} else {
					isInitialParam = true;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		gpsColor.setSelection(mParameterSet.getGPSColorPosition());

		// DT地图默认设置选择
		final String[] dtMapDeaults = getResources().getStringArray(R.array.sys_dtmap_default);
		int dtPosition = 0;
		ArrayAdapter<String> adpdtts = new ArrayAdapter<String>(SysMap.this, R.layout.simple_spinner_custom_layout,
				dtMapDeaults);
		adpdtts.setDropDownViewResource(R.layout.spinner_dropdown_item);
		dtDefaultMap.setAdapter(adpdtts);
		if (ApplicationModel.getInstance().isTestJobIsRun()) { // 如果正在测试，DT地图默认模式不可点击
			dtDefaultMap.setEnabled(false);
		}
		dtDefaultMap.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				mParameterSet.setDtDefaultMap(dtMapDeaults[arg2]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

		for (int i = 0; i < dtMapDeaults.length; i++) {
			if (dtMapDeaults[i].equals(mParameterSet.getDtDefaultMap())) {
				dtPosition = i;
				break;
			}
		}
		dtDefaultMap.setSelection(dtPosition);

		ArrayAdapter<String> locusShapeAdp = new ArrayAdapter<String>(SysMap.this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.locus_shape_array));
		locusShapeAdp.setDropDownViewResource(R.layout.spinner_dropdown_item);
		locusShapeSP.setAdapter(locusShapeAdp);
		locusShapeSP.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Editor editor = defaultSharedPreferences.edit();
				editor.putInt(LOCUS_SHARE, position);
				editor.commit();
				mParameterSet.setLocusShape(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
		locusShapeSP.setSelection(mParameterSet.getLocusShape());

		ArrayAdapter<String> locusShapeSizeAdp = new ArrayAdapter<String>(SysMap.this,
				R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.locus_shape_size_array));
		locusShapeSizeAdp.setDropDownViewResource(R.layout.spinner_dropdown_item);
		locusShapeSizeSP.setAdapter(locusShapeSizeAdp);
		locusShapeSizeSP.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Editor editor = defaultSharedPreferences.edit();
				editor.putInt(LOCUS_SHARE_SIZE, position);
				editor.commit();
				mParameterSet.setLocusSize(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
		locusShapeSizeSP.setSelection(mParameterSet.getLocusSize());

		// Spinner of Parameter
		final String[] parameters = mParameterSet.getParameterNames();
		final String[] shortNames = mParameterSet.getParameterShortNames();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(SysMap.this, R.layout.simple_spinner_custom_layout,
				shortNames);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		paraChoice.setAdapter(adapter);
		paraChoice.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				mParameterSet.setMapParameter(parameters[arg2]);
				// changeValue();//旧界面
				// 发送地图参数改变的广播
				if (isInitalGps) {
					Intent intent = new Intent();
					intent.setAction(WalkMessage.mapParaChanged);
					sendBroadcast(intent);
					LogUtil.v(tag, "Param has Changed");
				} else {
					isInitalGps = true;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
		LogUtil.v("ShortAAA", mapParemeter.getId());
		paraChoice.setSelection(mParameterSet.getPositionOfParameter(mapParemeter.getId()));

		if (ApplicationModel.getInstance().isTestJobIsRun()) {
			dtDefaultMap.setClickable(false);
		}

		Button baseStationDownload = (Button)this.findViewById(R.id.btn_base_station_download);
		baseStationDownload.setOnClickListener(this);
		
		BasicSpinner basicSpinner = (BasicSpinner) findViewById(R.id.basedisplay_sp);
		ArrayAdapter<String> basedisplayAdapter = new ArrayAdapter<String>(SysMap.this,
				R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_base_display_type));
		basedisplayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		basicSpinner.setAdapter(basedisplayAdapter);
		basicSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					findViewById(R.id.setting_base_custom_param).setVisibility(View.GONE);
					mSharePreferencesUtil.saveInteger(BASE_DISPLAY_TYPE, position);
					break;
				case 1:
					findViewById(R.id.setting_base_custom_param).setVisibility(View.VISIBLE);
					mSharePreferencesUtil.saveInteger(BASE_DISPLAY_TYPE, position);
					break;

				default:
					break;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		basicSpinner.setSelection(mSharePreferencesUtil.getInteger(BASE_DISPLAY_TYPE, 0));
		if (mSharePreferencesUtil.getInteger(BASE_DISPLAY_TYPE, 0) == 1) {
			findViewById(R.id.setting_base_custom_param).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.setting_base_custom_param).setVisibility(View.GONE);
		}
		setBaseCustomText();
		findViewById(R.id.setting_base_custom_param).setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				BasicDialog.Builder builder = new BasicDialog.Builder(SysMap.this);
				ListView listView = new ListView(SysMap.this);
				listView.setDivider(getResources().getDrawable(R.drawable.list_divider));
				// listView.setBackgroundColor(getResources().getColor(R.color.base_list_item_bg_nomal));
				listView.setBackgroundColor(getResources().getColor(R.color.app_main_bg_color));
				String[] params = new String[] { "GSM", "WCDMA", "CDMA", "TD-SCDMA", "LTE" ,"NB-IoT" };
				listView.setAdapter(new BaseDisplayAdapter(SysMap.this, params));
				final HashMap<Integer, Boolean> checkHm = new HashMap<Integer, Boolean>();
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						CheckedTextView checkedTextView = ((CheckedTextView) view);
						checkedTextView.setChecked(checkedTextView.isChecked() ? false : true);
						checkHm.put(position, checkedTextView.isChecked());
					}
				});
				builder.setTitle(R.string.sys_chart_custom)
						.setView(listView, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT))
						.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Iterator<Integer> iter = checkHm.keySet().iterator();
								while (iter.hasNext()) {
									int key = iter.next();
									boolean val = checkHm.get(key);
									switch (key) {
									case 0:
										mSharePreferencesUtil.saveBoolean(BASE_GSM, val);
										break;
									case 1:
										mSharePreferencesUtil.saveBoolean(BASE_WCDMA, val);
										break;
									case 2:
										mSharePreferencesUtil.saveBoolean(BASE_CDMA, val);
										break;
									case 3:
										mSharePreferencesUtil.saveBoolean(BASE_TDSCDMA, val);
										break;
									case 4:
										mSharePreferencesUtil.saveBoolean(BASE_LTE, val);
										break;
									case 5:
										mSharePreferencesUtil.saveBoolean(BASE_NB_IoT, val);
										break;

									default:
										break;
									}

									setBaseCustomText();

								}

							}
						}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								checkHm.clear();
							}
						}).show();

			}
		});

		setParamterText();
	}

	private void setParamterText() {
		final ArrayList<Parameter> parameters2g = mParameterSet.getParamerterByNetType(2);
		final ArrayList<Parameter> parameters3g = mParameterSet.getParamerterByNetType(3);
		final ArrayList<Parameter> parameters4g = mParameterSet.getParamerterByNetType(4);
		StringBuffer btn2gBuffer = new StringBuffer();
		StringBuffer btn3gBuffer = new StringBuffer();
		StringBuffer btn4gBuffer = new StringBuffer();

		buildBtnText(btn2gBuffer, parameters2g);
		buildBtnText(btn3gBuffer, parameters3g);
		buildBtnText(btn4gBuffer, parameters4g);

		(initButton(R.id.map_2g_param_btn)).setText(StringUtil.isNullOrEmpty(btn2gBuffer.toString()) ? ""
				: btn2gBuffer.toString().substring(0, btn2gBuffer.toString().length() - 1));
		(initButton(R.id.map_3g_param_btn)).setText(StringUtil.isNullOrEmpty(btn3gBuffer.toString()) ? ""
				: btn3gBuffer.toString().substring(0, btn3gBuffer.toString().length() - 1));
		(initButton(R.id.map_4g_param_btn)).setText(StringUtil.isNullOrEmpty(btn4gBuffer.toString()) ? ""
				: btn4gBuffer.toString().substring(0, btn4gBuffer.toString().length() - 1));
	}

	private void buildBtnText(StringBuffer stringBuffer, ArrayList<Parameter> parameters) {
		for (int i = 0; i < parameters.size(); i++) {
			if (parameters.get(i).isMapChecked()) {
				stringBuffer.append(parameters.get(i).getShowName()).append(",");
			}
		}
	}

	private void setBaseCustomText() {
		StringBuffer stringBuffer = new StringBuffer();
		if (mSharePreferencesUtil.getBoolean(SysMap.BASE_GSM, false)) {
			stringBuffer.append("GSM,");
		}
		if (mSharePreferencesUtil.getBoolean(SysMap.BASE_WCDMA, false)) {
			stringBuffer.append("WCDMA,");
		}
		if (mSharePreferencesUtil.getBoolean(SysMap.BASE_CDMA, false)) {
			stringBuffer.append("CDMA,");
		}
		if (mSharePreferencesUtil.getBoolean(SysMap.BASE_TDSCDMA, false)) {
			stringBuffer.append("TDSCDMA,");
		}
		if (mSharePreferencesUtil.getBoolean(SysMap.BASE_LTE, false)) {
			stringBuffer.append("LTE,");
		}
		if (mSharePreferencesUtil.getBoolean(SysMap.BASE_NB_IoT, false)) {
			stringBuffer.append("NB-IoT,");
		}
		if (!StringUtil.isNullOrEmpty(stringBuffer.toString())) {
			(initTextView(R.id.base_custom_desc))
					.setText(stringBuffer.toString().substring(0, stringBuffer.toString().length() - 1));
		} else {
			(initTextView(R.id.base_custom_desc)).setText(R.string.none);
		}
	}

	@Override // 添加菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.revert_default, menu);
		return true;
	}

	@Override // 菜单点击事件
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {

		case R.id.menu_revert_default:
			basicDialog1 = new BasicDialog.Builder(SysMap.this).setTitle(R.string.str_revert_default)
					.setMessage(R.string.str_revert_default_makesure)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							// 根据不同手机类型写入不同的配置文件,目标文件名一样为config_map_chart.xml
							ParameterSetting.resetToDefaultFromFile(SysMap.this, true);
							mParameterSet.initialParameter();
							findView();
							initViews();
						}
					}).setNegativeButton(R.string.str_cancle).show();
		}

		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		BasicDialog.Builder builder = new BasicDialog.Builder(SysMap.this);
		builder.setTitle(getString(R.string.setting));
		switch (id) {
		case R.id.map_2g_param_btn:
			ListViewForScrollView SingleChoice2g = new ListViewForScrollView(this);
			SingleChoice2g.setDivider(this.getResources().getDrawable(R.drawable.list_divider));
			SingleChoice2g.setCacheColorHint(Color.TRANSPARENT);

			final ArrayList<Parameter> parameters2g = mParameterSet.getParamerterByNetType(2);
			MutilChoiceArrayAdapter adapter2g = new MutilChoiceArrayAdapter(this, R.layout.simple_list_item_multiple_choice,
					parameters2g);
			SingleChoice2g.setAdapter(adapter2g);
			SingleChoice2g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					CheckedTextView checkedTextView = ((CheckedTextView) view);
					if (checkParamCount(2, mParameterSet.isPublicParamters(checkedTextView.getText().toString()),
							!checkedTextView.isChecked())) {
						return;
					} else {
						parameters2g.get(position).setMapChecked(!checkedTextView.isChecked());
						mParameterSet.setMapParamterChecked(parameters2g.get(position).getId(), !checkedTextView.isChecked());
						checkedTextView.setChecked(!checkedTextView.isChecked());
						if (checkedTextView.isChecked()) {
							reBuildGPSInfo(checkedTextView.getText().toString());
						}
					}
					setParamterText();
				}
			});
			builder.setView(SingleChoice2g);
			builder.show();
			break;
		case R.id.map_3g_param_btn:
			ListViewForScrollView SingleChoice3g = new ListViewForScrollView(this);
			SingleChoice3g.setDivider(this.getResources().getDrawable(R.drawable.list_divider));
			SingleChoice3g.setCacheColorHint(Color.TRANSPARENT);
			final ArrayList<Parameter> parameters3g = mParameterSet.getParamerterByNetType(3);
			MutilChoiceArrayAdapter adapter3g = new MutilChoiceArrayAdapter(this, R.layout.simple_list_item_multiple_choice,
					parameters3g);
			SingleChoice3g.setAdapter(adapter3g);
			SingleChoice3g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					CheckedTextView checkedTextView = ((CheckedTextView) view);
					if (checkParamCount(3, mParameterSet.isPublicParamters(checkedTextView.getText().toString()),
							!checkedTextView.isChecked())) {
						return;
					} else {
						parameters3g.get(position).setMapChecked(!checkedTextView.isChecked());
						mParameterSet.setMapParamterChecked(parameters3g.get(position).getId(), !checkedTextView.isChecked());
						checkedTextView.setChecked(!checkedTextView.isChecked());
						if (checkedTextView.isChecked()) {
							reBuildGPSInfo(checkedTextView.getText().toString());
						}
						setParamterText();
					}
				}
			});

			builder.setView(SingleChoice3g);
			builder.show();
			break;
		case R.id.map_4g_param_btn:
			ListViewForScrollView SingleChoice4g = new ListViewForScrollView(this);
			SingleChoice4g.setDivider(this.getResources().getDrawable(R.drawable.list_divider));
			SingleChoice4g.setCacheColorHint(Color.TRANSPARENT);

			final ArrayList<Parameter> parameters4g = mParameterSet.getParamerterByNetType(4);
			MutilChoiceArrayAdapter adapter4g = new MutilChoiceArrayAdapter(this, R.layout.simple_list_item_multiple_choice,
					parameters4g);
			SingleChoice4g.setAdapter(adapter4g);
			SingleChoice4g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					CheckedTextView checkedTextView = ((CheckedTextView) view);
					if (checkParamCount(4, mParameterSet.isPublicParamters(checkedTextView.getText().toString()),
							!checkedTextView.isChecked())) {
						return;
					} else {
						parameters4g.get(position).setMapChecked(!checkedTextView.isChecked());
						mParameterSet.setMapParamterChecked(parameters4g.get(position).getId(), !checkedTextView.isChecked());
						checkedTextView.setChecked(!checkedTextView.isChecked());
						if (checkedTextView.isChecked()) {
							reBuildGPSInfo(checkedTextView.getText().toString());
						}
					}
					setParamterText();
				}
			});
			builder.setView(SingleChoice4g);
			builder.show();
			break;
		default:
			break;
		}

		return super.onCreateDialog(id);
	}

	private void reBuildGPSInfo(String param) {
		Parameter parameter = mParameterSet.getParameterByShortName(param);
		if (parameter != null) {
			DatasetManager.getInstance(this).reBuildGPSInfo(parameter);
		}
	}

	/**
	 * 判断是否已经超出限制<BR>
	 * [功能详细描述]
	 * 
	 * @param count
	 *          参数总数,最大为3 最小为1
	 * @param isChecked
	 * @return
	 */
	private boolean isMaxParamCount(int count, boolean isChecked) {
		if ((count >= 3 && isChecked) || (count == 1 && !isChecked)) {
			return true;
		}
		return false;
	}

	/**
	 * 统计参数选择总数<BR>
	 * [功能详细描述]
	 * 
	 * @param type
	 *          网络类型
	 * @param isPublic
	 *          是否公共参数
	 * @param isChecked
	 *          是否选择
	 * @return
	 */
	private boolean checkParamCount(int type, boolean isPublic, boolean isChecked) {
		int count = 0;
		int netType = type;// 是公共参数时用于区分2G/3G/4G
		if (isPublic) {
			type = 0;
		}
		switch (type) {
		case 0:
			ArrayList<Parameter> parameters20g = mParameterSet.getParamerterByNetType(2);
			ArrayList<Parameter> parameters30g = mParameterSet.getParamerterByNetType(3);
			ArrayList<Parameter> parameters40g = mParameterSet.getParamerterByNetType(4);
			if (netType == 2) {
				for (int i = 0; i < parameters20g.size(); i++) {
					if (parameters20g.get(i).isMapChecked()) {
						count++;
					}
				}
				if (isMaxParamCount(count, isChecked)) {
					return true;
				}
			} else if (netType == 3) {
				count = 0;
				for (int i = 0; i < parameters30g.size(); i++) {
					if (parameters30g.get(i).isMapChecked()) {
						count++;
					}
				}
				if (isMaxParamCount(count, isChecked)) {
					return true;
				}
			} else if (netType == 4) {
				count = 0;
				for (int i = 0; i < parameters40g.size(); i++) {
					if (parameters40g.get(i).isMapChecked()) {
						count++;
					}
				}
			}
			break;
		case 2:
			ArrayList<Parameter> parameters2g = mParameterSet.getParamerterByNetType(2);
			for (int i = 0; i < parameters2g.size(); i++) {
				if (parameters2g.get(i).isMapChecked()) {
					count++;
				}
			}
			break;
		case 3:
			ArrayList<Parameter> parameters3g = mParameterSet.getParamerterByNetType(3);
			for (int i = 0; i < parameters3g.size(); i++) {
				if (parameters3g.get(i).isMapChecked()) {
					count++;
				}
			}
			break;
		case 4:
			ArrayList<Parameter> parameters4g = mParameterSet.getParamerterByNetType(4);
			for (int i = 0; i < parameters4g.size(); i++) {
				if (parameters4g.get(i).isMapChecked()) {
					count++;
				}
			}
			break;

		default:
			break;
		}
		return isMaxParamCount(count, isChecked);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.map_2g_param_btn:
			showDialog(R.id.map_2g_param_btn);
			break;
		case R.id.map_3g_param_btn:
			showDialog(R.id.map_3g_param_btn);
			break;
		case R.id.map_4g_param_btn:
			showDialog(R.id.map_4g_param_btn);
			break;
		case R.id.cell_line_set_btn:
			startActivity(new Intent(SysMap.this, CellLinkSetActivity.class));
			break;
		case R.id.metro_route_download_btn:
			startActivity(new Intent(SysMap.this, MetroSettingCityActivity.class));
			break;
		case R.id.gaotie_route_download_btn:
			jumpActivity(HighSpeedRailActivity.class); 
			break;
		case R.id.btn_base_station_download:
			downloadBaseData();
			break;
		default:
			break;
		}
	}

	/**
	 * 下载基站数据
	 */
	private void downloadBaseData() {
		Intent intent = new Intent(SysMap.this, BaseStationDownloadActivity.class);
		this.startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent intenter=new Intent(WalkMessage.CHANCE_DT_MAP_DEFAULT);
		sendBroadcast(intenter);//发送广播通知换地图
		if (basicDialog != null && basicDialog.isShowing()) {
			basicDialog.dismiss();
		}
		if (basicDialog1 != null && basicDialog1.isShowing()) {
			basicDialog1.dismiss();
		}
	}

}