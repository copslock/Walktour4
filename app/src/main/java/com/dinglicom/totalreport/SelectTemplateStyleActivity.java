package com.dinglicom.totalreport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.report.template.BusinessTemplateModel;
import com.walktour.gui.report.template.GroupTemplateModel;
import com.walktour.gui.report.template.ReportXmlTools;
import com.walktour.gui.report.template.ResultTemplateModel;
import com.walktour.gui.report.template.StyleTemplateModel;
import com.walktour.gui.report.template.TagTemplateModel;
import com.walktour.gui.report.template.jsonmodel.SheetInfoModel;
import com.walktour.gui.report.template.jsonmodel.SheetsModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * 选择报表样式
 * 
 * @author weirong.fan
 *
 */
@SuppressLint("InflateParams")
public class SelectTemplateStyleActivity extends BasicActivity {
	/** 上下文 **/
	private Context context = SelectTemplateStyleActivity.this;
	/** 所有的样式信息 **/
	private List<GroupTemplateModel> allGroups = new LinkedList<GroupTemplateModel>();
	/** 选择或搜索的样式信息 **/
	private List<GroupTemplateModel> selectSheets = new LinkedList<GroupTemplateModel>();
	/** 选中样式后的样式信息 **/
	private List<GroupTemplateModel> curentSheet = new LinkedList<GroupTemplateModel>();
	private List<String> data_list = new LinkedList<String>();
	private ArrayAdapter<String> arr_adapter;
	/** 选择样式类型 **/
	private Spinner spinner;
	/** 同步更新服务器最新样式 **/
	private Button downBtn;
	/** 搜索样式 **/
	private Button searchBtn;
	/** 搜索输入框 **/
	private EditText searchET;
	/** 底部工具栏 **/
	private ControlBar bar;
	/** 关闭按钮 **/
	private Button button1;
	/** 是否是搜索模式 **/
	private boolean isSearch = false;
	/** 右边的布局 **/
	private LinearLayout titleRight;

	/** 报表路径 **/
	private File jsonFile = null;
	private SheetsModel sheetsModel = null;
	/** 所有历史已选择了的styles **/
	private List<StyleTemplateModel> allHistoryStyles = new LinkedList<StyleTemplateModel>();
	private String currentSheet = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.selete_report_style);
		initValue();
		initViews();
	}

	private void initValue() {
		Intent intent = this.getIntent();
		currentSheet = intent.getStringExtra("currentSheet");
		String jsonFilePath = intent.getStringExtra("jsonFilePath");
		jsonFile = new File(jsonFilePath);
		parseSheetJson();
	}

	private void initViews() {
		parseXmlStyle();
		initImageView(R.id.pointer).setOnClickListener(this);
		// 同步按钮
		downBtn = initButton(R.id.push);
		downBtn.setBackgroundResource(R.drawable.controlbar_download);
		downBtn.setOnClickListener(this);
		searchBtn = initButton(R.id.share);
		searchBtn.setBackgroundResource(R.drawable.search);
		searchBtn.setOnClickListener(this);
		// 搜索按钮
		spinner = initSpinner(R.id.seletetypesheet);
		// 适配器
		arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
		// 设置样式
		arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 加载适配器
		spinner.setAdapter(arr_adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 == 0) {
					refreshSheet(null);
				} else {
					refreshSheet(data_list.get(arg2));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		searchET = initEditText(R.id.searchbox);
		// 监听文本框输入变化
		searchET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				refreshSheet(s + "");
			}
		});

		bar = (ControlBar) findViewById(R.id.ControlBar);
		bar.setBackgroundResource(R.drawable.toolbar_bg);

		button1 = bar.getButton(0);
		button1.setText(R.string.off);
		button1.setOnClickListener(this);
		button1.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_delete), null, null);
		if (isSearch) {
			bar.setVisibility(View.VISIBLE);
		} else {
			bar.setVisibility(View.GONE);
		}

		titleRight = initLinearLayout(R.id.title_right);
		refreshSheet(null);

	}

	/***
	 * 刷新样式信息
	 * 
	 * @param styleName
	 *            isSearch=false匹配样式类型,isSearch=true匹配是否包含这个信息
	 * @param isSearch
	 *            是否是搜索
	 */
	private void refreshSheet(String styleName) {
		selectSheets.clear();
		if (isSearch) {// 搜索
			if (styleName == null || styleName.length() <= 0) {
				for (GroupTemplateModel group : allGroups) {
					selectSheets.add(group);
				}
			} else {
				for (GroupTemplateModel st : allGroups) {
					GroupTemplateModel newGTemp = null;
					for (BusinessTemplateModel buniessTemp : st.getBusinesses()) {
						if (buniessTemp.getShowBusinessName().contains(styleName)) {
							if (newGTemp == null) {
								newGTemp = new GroupTemplateModel();
								newGTemp.setGroupNameCN(st.getGroupNameCN());
								newGTemp.setGroupNameEN(st.getGroupNameEN());

							}
							newGTemp.getBusinesses().add(buniessTemp);
						} else {
							BusinessTemplateModel newBTemp = null;
							for (StyleTemplateModel styleTemp : buniessTemp.getStyles()) {
								if (styleTemp.getShowStyleName().contains(styleName)) {
									if (newGTemp == null) {
										newGTemp = new GroupTemplateModel();
										newGTemp.setGroupNameCN(st.getGroupNameCN());
										newGTemp.setGroupNameEN(st.getGroupNameEN());
									}
									if (newBTemp == null) {
										newBTemp = new BusinessTemplateModel();
										newBTemp.setBusinessNameCN(buniessTemp.getBusinessNameCN());
										newBTemp.setBusinessNameEN(buniessTemp.getBusinessNameEN());
										newGTemp.getBusinesses().add(newBTemp);
									}
									newBTemp.getStyles().add(styleTemp);
								}
							}
						}
					}
					if (null != newGTemp) {
						selectSheets.add(newGTemp);
					}
				}
			}
		} else {// 选择
			if (styleName == null || styleName.length() <= 0) {
				for (GroupTemplateModel style : allGroups) {
					selectSheets.add(style);
				}
			} else {
				for (GroupTemplateModel st : allGroups) {
					if (st.getShowGroupName().equals(styleName)) {
						selectSheets.add(st);
					}
				}
			}
		}
		final LinearLayout layoutSheet = initLinearLayout(R.id.createsheetlayout);
		layoutSheet.removeAllViews();
		for (final GroupTemplateModel group : selectSheets) {
			final LinearLayout layoutone = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.selete_report_style_item1, null);
			((CheckBox) layoutone.findViewById(R.id.selecttype)).setVisibility(View.GONE);
			((ImageView) layoutone.findViewById(R.id.changeselect)).setVisibility(View.GONE);
			((TextView) layoutone.findViewById(R.id.stylename)).setText(group.getShowGroupName());
			layoutSheet.addView(layoutone);
			for (final BusinessTemplateModel businessTemp : group.getBusinesses()) {
				final LinearLayout layout1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.selete_report_style_item1, null);
				((CheckBox) layout1.findViewById(R.id.selecttype)).setVisibility(View.GONE);
				final ImageView img1 = (ImageView) layout1.findViewById(R.id.changeselect);
				final TextView tv1 = (TextView) layout1.findViewById(R.id.stylename);
				final LinearLayout sublayout = (LinearLayout) layout1.findViewById(R.id.sublayout);
				final List<LinearLayout> layouts = new LinkedList<LinearLayout>();
				/** 子复选框集合 **/
				final List<CheckBox> checkBoxs = new LinkedList<CheckBox>();
				tv1.setText(businessTemp.getShowBusinessName());
				tv1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (sublayout.getChildCount() <= 0) {// 需要展开
							img1.setBackgroundResource(R.drawable.expander_ic_maximized_black);
							for (LinearLayout sublay : layouts) {
								sublayout.addView(sublay);
							}
						} else {// 需要收缩
							img1.setBackgroundResource(R.drawable.expander_ic_minimized_black);
							for (LinearLayout sublay : layouts) {
								sublayout.removeView(sublay);
							}
						}
					}
				});
				// 点击展开收缩按钮
				img1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (sublayout.getChildCount() <= 0) {// 需要展开
							img1.setBackgroundResource(R.drawable.expander_ic_maximized_black);
							for (LinearLayout sublay : layouts) {
								sublayout.addView(sublay);
							}
						} else {// 需要收缩
							img1.setBackgroundResource(R.drawable.expander_ic_minimized_black);
							for (LinearLayout sublay : layouts) {
								sublayout.removeView(sublay);
							}
						}
					}
				});
				layoutSheet.addView(layout1);

				for (final StyleTemplateModel styleTemp : businessTemp.getStyles()) {
					LinearLayout layout2 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.selete_report_style_item2, null);
					final CheckBox cb2 = (CheckBox) layout2.findViewById(R.id.selecttype);
					final TextView tv2 = (TextView) layout2.findViewById(R.id.stylename);
					final TextView tv2des = (TextView) layout2.findViewById(R.id.stylenamedescripe);
					tv2.setText(styleTemp.getShowStyleName());
					StringBuffer sb = new StringBuffer();
					for (TagTemplateModel tag : styleTemp.getTags()) {
						sb.append(tag.getShowTagNameSum() + "," + tag.getShowTagNamebylog() + "\n");
						for (ResultTemplateModel result : tag.getResults()) {
							sb.append("\t" + result.getShowExcelTitle() + "," + result.getShowDescripton() + "\n");
						}
					}
					tv2des.setText(sb.toString());
					tv2des.setVisibility(View.GONE);
					cb2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if (!cb2.isEnabled())// 不可编辑则不处理业务
								return;
							if (isChecked) {// 添加样式
								cb2.setBackgroundResource(R.drawable.btn_check_on);
								int selectIndex = -1;
								for (int index = 0; index < checkBoxs.size(); index++) {
									if (cb2 == checkBoxs.get(index)) {
										if (index == 0) {
											selectIndex = 0;
											break;
										}
									}
								}
								if (selectIndex == 0) {// 勾选第一个,下面的默认勾选不可编辑
									for (int index = 1; index < checkBoxs.size(); index++) {
										if (checkBoxs.get(index).isEnabled()) {
											checkBoxs.get(index).setBackgroundResource(R.drawable.check_enable);
											checkBoxs.get(index).setEnabled(false);
										}
									}
								} else {// 勾选了其他,则第一个不能编辑了
									if (checkBoxs.size() > 0) {
										checkBoxs.get(0).setEnabled(false);
										checkBoxs.get(0).setBackgroundResource(R.drawable.check_enable);
									}
								}
								// 加入指定的样式

								// 是否存在选择的样式
								GroupTemplateModel groupx = null;
								BusinessTemplateModel businessx = null;
								StyleTemplateModel stylex = null;
								for (GroupTemplateModel groupTemps : curentSheet) {
									if (groupTemps.getShowGroupName().equals(group.getShowGroupName())) {
										groupx = groupTemps;
										for (BusinessTemplateModel bm : groupTemps.getBusinesses()) {
											if (bm.getShowBusinessName().equals(businessTemp.getShowBusinessName())) {
												businessx = bm;
												for (StyleTemplateModel st : bm.getStyles()) {
													if (st.getShowStyleName().equals(styleTemp.getShowStyleName())) {
														stylex = st;
														break;
													}
												}
											}
										}
									}
								}
								if (null == groupx) {// 完全没有
									groupx = new GroupTemplateModel();
									groupx.setLevel(group.getLevel());
									groupx.setCode(group.getCode());
									groupx.setGroupNameCN(group.getGroupNameCN());
									groupx.setGroupNameEN(group.getGroupNameEN());
									BusinessTemplateModel bm = new BusinessTemplateModel();
									bm.setLevel(businessTemp.getLevel());
									bm.setCode(businessTemp.getCode());
									bm.setBusinessNameCN(businessTemp.getBusinessNameCN());
									bm.setBusinessNameEN(businessTemp.getBusinessNameEN());
									bm.getStyles().add(styleTemp);
									groupx.getBusinesses().add(bm);
									curentSheet.add(groupx);

								} else if (null == businessx) {// 有group没business
									BusinessTemplateModel bm = new BusinessTemplateModel();
									bm.setLevel(businessTemp.getLevel());
									bm.setCode(businessTemp.getCode());
									bm.setBusinessNameCN(businessTemp.getBusinessNameCN());
									bm.setBusinessNameEN(businessTemp.getBusinessNameEN());
									bm.getStyles().add(styleTemp);
									groupx.getBusinesses().add(bm);
								} else if (null == stylex) {// 有group,有business,不存在这个样式则添加
									businessx.getStyles().add(styleTemp);
								}

							} else {// 删除样式
								cb2.setBackgroundResource(R.drawable.btn_check_off);

								int selectIndex = -1;
								for (int index = 0; index < checkBoxs.size(); index++) {
									if (cb2 == checkBoxs.get(index)) {
										if (index == 0) {
											selectIndex = 0;
											break;
										}
									}
								}
								if (selectIndex == 0) {// 反勾选第一个,下面的默认勾选均可编辑
									for (int index = 1; index < checkBoxs.size(); index++) {
										checkBoxs.get(index).setEnabled(true);
										checkBoxs.get(index).setBackgroundResource(R.drawable.btn_check_off);
									}
								} else {// 反勾选了其他,则第一个不能编辑了
									boolean isFlag = false;
									for (int index = 1; index < checkBoxs.size(); index++) {
										if (checkBoxs.get(index).isChecked()) {
											isFlag = true;
											break;
										}
									}
									if (isFlag) {
										checkBoxs.get(0).setEnabled(false);
										checkBoxs.get(0).setBackgroundResource(R.drawable.check_enable);
									} else {
										checkBoxs.get(0).setEnabled(true);
										checkBoxs.get(0).setBackgroundResource(R.drawable.btn_check_off);
									}

								}
								// 删除指定的央视

								// 是否存在选择的样式
								GroupTemplateModel groupx = null;
								BusinessTemplateModel businessx = null;
								StyleTemplateModel stylex = null;
								for (GroupTemplateModel groupTemps : curentSheet) {
									if (groupTemps.getShowGroupName().equals(group.getShowGroupName())) {
										groupx = groupTemps;
										for (BusinessTemplateModel bm : groupTemps.getBusinesses()) {
											if (bm.getShowBusinessName().equals(businessTemp.getShowBusinessName())) {
												businessx = bm;
												for (StyleTemplateModel st : bm.getStyles()) {
													if (st.getShowStyleName().equals(styleTemp.getShowStyleName())) {
														stylex = st;
														break;
													}
												}
											}
										}
									}
								}
								if (null != stylex) {
									businessx.getStyles().remove(styleTemp);
									// 删除不包含任何style的business
									if (businessx.getStyles().size() <= 0) {
										groupx.getBusinesses().remove(businessx);
										// 删除不包含任何business的group
										if (groupx.getBusinesses().size() <= 0) {
											curentSheet.remove(groupx);
										}
									}
								}
							}
						}
					});
					tv2.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if (tv2des.getVisibility() == View.GONE)
								tv2des.setVisibility(View.VISIBLE);
							else {
								tv2des.setVisibility(View.GONE);
							}
						}
					});

					// 处理是否可以勾选当前样式
					for (StyleTemplateModel stylx : allHistoryStyles) {
						if (stylx.equals(styleTemp)) {
							boolean isExist = false;
							for (GroupTemplateModel g : curentSheet) {
								for (BusinessTemplateModel b : g.getBusinesses()) {
									for (StyleTemplateModel stylx2 : b.getStyles()) {
										if (stylx2.equals(styleTemp)) {
											cb2.setChecked(true);
											isExist = true;
											break;
										}
									}
								}
							}
							if (!isExist) {
								cb2.setEnabled(false);
								cb2.setBackgroundResource(R.drawable.check_enable);
							}
						}
					}
					sublayout.addView(layout2);
					checkBoxs.add(cb2);
					layouts.add(layout2);
				}
				if (checkBoxs.size() >= 1) {
					boolean isFlag = false;
					for (int index = 1; index < checkBoxs.size(); index++) {
						if (!checkBoxs.get(index).isEnabled()) {
							isFlag = true;
							break;
						}
					}
					if (isFlag) {
						checkBoxs.get(0).setEnabled(false);
						checkBoxs.get(0).setBackgroundResource(R.drawable.check_enable);
					}

					if (checkBoxs.get(0).isChecked() && checkBoxs.get(0).isEnabled()) {
						for (int index = 1; index < checkBoxs.size(); index++) {
							checkBoxs.get(index).setEnabled(false);
							checkBoxs.get(index).setBackgroundResource(R.drawable.check_enable);

						}
					}

				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		// 关闭
		case R.id.pointer:
			saveSheetJson();
			finish();
		case R.id.push:
			ToastUtil.showToastShort(context, "服务器样式同步完成.");
			break;
		// 搜索
		case R.id.share:
			titleRight.setVisibility(View.GONE);
			isSearch = !isSearch;
			if (isSearch) {// 是搜索模式
				spinner.setVisibility(View.GONE);
				searchET.setVisibility(View.VISIBLE);
				bar.setVisibility(View.VISIBLE);
			} else {
				spinner.setVisibility(View.VISIBLE);
				searchET.setVisibility(View.GONE);
				bar.setVisibility(View.GONE);
			}
			break;
		case R.id.Button01:
			titleRight.setVisibility(View.VISIBLE);
			isSearch = false;
			searchET.setText("");
			spinner.setVisibility(View.VISIBLE);
			searchET.setVisibility(View.GONE);
			bar.setVisibility(View.GONE);
			refreshSheet(null);
			break;
		default:
			break;
		}
	}

	/****
	 * 解析模板描述信息
	 */
	private void parseXmlStyle() {
		try {
			allGroups.clear();
			this.getResources().openRawResource(R.raw.stylesheet);
			StringBuffer sb = new StringBuffer();
			InputStream inputStream = getResources().openRawResource(R.raw.stylesheet);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String info = "";
			while ((info = bufferedReader.readLine()) != null) {
				sb.append(info);
			}
			allGroups = ReportXmlTools.getInstance().parseXml(sb.toString());
			data_list.clear();
			data_list.add(getResources().getString(R.string.building_all));
			for (GroupTemplateModel group : allGroups) {
				data_list.add(group.getShowGroupName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			saveSheetJson();
			this.finish();

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	private void saveSheetJson() {
		Gson gson2 = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
		String obj2 = gson2.toJson(sheetsModel);
		FileUtil.writeToFile(jsonFile, obj2);
		this.setResult(RESULT_OK);
	}

	/***
	 * 解析json文件
	 */
	private void parseSheetJson() {
		try {
			if (jsonFile.exists()) {
				String json = FileUtil.getStringFromFile(jsonFile);
				Gson gson2 = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
				sheetsModel = gson2.fromJson(json, SheetsModel.class);
				for (SheetInfoModel sheet : sheetsModel.getSheets()) {
					if (sheet.getSheetName().equals(currentSheet)) {
						this.curentSheet = sheet.getGroups();
						break;
					}
				}
			} else {
				sheetsModel = new SheetsModel();
			}

			// 所有已选择了的样式
			for (SheetInfoModel sheet : sheetsModel.getSheets()) {
				for (GroupTemplateModel group : sheet.getGroups()) {
					for (BusinessTemplateModel business : group.getBusinesses()) {
						allHistoryStyles.addAll(business.getStyles());
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
