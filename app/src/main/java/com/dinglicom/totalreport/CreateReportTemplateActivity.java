package com.dinglicom.totalreport;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.report.ReportFactory;
import com.walktour.gui.report.template.BusinessTemplateModel;
import com.walktour.gui.report.template.GroupTemplateModel;
import com.walktour.gui.report.template.ResultTemplateModel;
import com.walktour.gui.report.template.StyleTemplateModel;
import com.walktour.gui.report.template.TagTemplateModel;
import com.walktour.gui.report.template.jsonmodel.SheetInfoModel;
import com.walktour.gui.report.template.jsonmodel.SheetsModel;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 创建报表模板
 * 
 * @author weirong.fan
 *
 */
public class CreateReportTemplateActivity extends BasicActivity {
	public final static int REQUESTCODE = 3888;
	private Context context = CreateReportTemplateActivity.this;
	/** 报表路径 **/
	private String reportPath = "";
	/** 是否是删除模式 **/
	private boolean isDelete = false;

	private ControlBar bar;
	private Button button1, button2;

	private LinearLayout layoutSheet;

	private SheetsModel sheetsModel = null;

	private File jsonFile = null;

	/** 表示当前选择的sheet **/
	private LinearLayout currentSheetLinearLayout = null;
	private String currentSheetLinearLayoutTitle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.create_report_manger);
		initViews();
	}

	@SuppressWarnings("deprecation")
	private void initViews() {
		initValue();

		initImageView(R.id.pointer).setOnClickListener(this);

		bar = (ControlBar) findViewById(R.id.ControlBar);
		bar.setBackgroundResource(R.drawable.toolbar_bg);

		button1 = bar.getButton(0);
		button1.setText(R.string.total_reportlist_export_str7);
		button1.setOnClickListener(this);
		button1.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_addto), null, null);

		button2 = bar.getButton(1);
		button2.setText(R.string.delete);
		button2.setOnClickListener(this);
		button2.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_clear), null, null);

		layoutSheet = initLinearLayout(R.id.createsheetlayout);
		refreshSheets(layoutSheet);

	}

	private void initValue() {
		Intent intent = this.getIntent();
		reportPath = intent.getStringExtra("reportPath");
		jsonFile = new File(reportPath + ReportCommons.CUSTOMREPORT_JSONNAME);
		parseJson();
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {

		// 新增表格
		case R.id.Button01:
			if(null!=sheetsModel){
				boolean isFlag=false;
				for(SheetInfoModel sheet:sheetsModel.getSheets())
				{
					if(sheet.getGroups().size()<=0){
						isFlag=true;
						break;
					}
				}
				if(isFlag){
					ToastUtil.showToastShort(context, R.string.total_reportlist_export_str14);
					return;
				}
			}
			
			
			createSheetDialog();
			break;

		// 删除表格样式
		case R.id.Button02:
			isDelete = !isDelete;
			this.refreshSheets(layoutSheet);
			if (isDelete) {
				button1.setVisibility(View.GONE);
				button2.setText(R.string.str_cancle);
				button2.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_backward), null, null);
			} else {
				button1.setVisibility(View.VISIBLE);
				button2.setText(R.string.delete);
				button2.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_clear), null, null);
			}

			break;

		// 关闭
		case R.id.pointer:
			if(checkNullSheet()){
				ToastUtil.showToastShort(context, R.string.total_reportlist_export_str15);
				return;
			}
			saveJson(true);
			this.setResult(RESULT_OK);
			finish();
		default:
			break;
		}
	}

	private void createSheetDialog() {
		final EditText et = new EditText(this);
		et.setHint(R.string.total_reportlist_export_str10);
		new AlertDialog.Builder(this).setTitle(R.string.total_reportlist_export_str13).setView(et).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {// 确定
					if (null == et.getText() || et.getText().toString().length() <= 0) {
						ToastUtil.showToastShort(context, R.string.total_reportlist_export_str11);
						return;
					}
					for (SheetInfoModel m : sheetsModel.getSheets()) {
						if (m.getSheetName().equals(et.getText() + "")) {
							ToastUtil.showToastShort(context, R.string.total_reportlist_export_str12);
							return;
						}
					}
					createSheet(et.getText() + "");
				}
			}

		}).setNegativeButton(R.string.str_cancle, null).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if(checkNullSheet()){
				ToastUtil.showToastShort(context, R.string.total_reportlist_export_str15);
				return false;
			}
			saveJson(true);
			this.setResult(RESULT_OK);
			this.finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	private void refreshSheets(final LinearLayout layoutSheetx) {
		layoutSheetx.removeAllViews();
		final Iterator<SheetInfoModel> it = sheetsModel.getSheets().iterator();
		while (it.hasNext()) {
			final SheetInfoModel sheet = it.next();
			final LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.report_manger_item2, null);
			ImageView delBtn = (ImageView) layout.findViewById(R.id.Itemdelete);
			final TextView title = (TextView) layout.findViewById(R.id.ItemTitle);
			title.setText(sheet.getSheetName());
			ImageView editBtn = (ImageView) layout.findViewById(R.id.Itemedit);
			if (isDelete) {
				delBtn.setVisibility(View.VISIBLE);
				editBtn.setVisibility(View.GONE);
			} else {
				delBtn.setVisibility(View.GONE);
				editBtn.setVisibility(View.VISIBLE);
			}
			final LinearLayout layout_1 = (LinearLayout) layout.findViewById(R.id.sublayoutx);
			delBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(context);
					builder.setMessage(R.string.str_delete_makesure);
					builder.setTitle(R.string.str_tip);
					builder.setPositiveButton(R.string.str_ok, new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							sheetsModel.getSheets().remove(sheet);
							refreshSheets(layoutSheet);
							dialog.dismiss();
						}
					});
					builder.setNegativeButton(R.string.str_cancle, new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();

				}
			});
			editBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					saveJson(false);
					currentSheetLinearLayout = layout_1;
					currentSheetLinearLayoutTitle = title.getText() + "";
					Bundle bundle = new Bundle();
					bundle.putString("currentSheet", currentSheetLinearLayoutTitle);
					bundle.putString("jsonFilePath", jsonFile.getAbsolutePath());
					bundle.putString("reportPath", reportPath);
					jumpActivityForResult(SelectTemplateStyleActivity.class, bundle, REQUESTCODE);
				}
			});

			for (GroupTemplateModel group : sheet.getGroups()) {
				for (final BusinessTemplateModel businessTemp : group.getBusinesses()) {
					final LinearLayout layout1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.selete_report_style_item1, null);
					((CheckBox) layout1.findViewById(R.id.selecttype)).setVisibility(View.GONE);
					final ImageView img1 = ((ImageView) layout1.findViewById(R.id.changeselect));
					final TextView tv1 = (TextView) layout1.findViewById(R.id.stylename);
					final LinearLayout sublayout = (LinearLayout) layout1.findViewById(R.id.sublayout);
					final List<LinearLayout> layouts = new LinkedList<LinearLayout>();
					tv1.setText(businessTemp.getShowBusinessName());
					for (final StyleTemplateModel styleTemp : businessTemp.getStyles()) {
						LinearLayout layout2 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.selete_report_style_item2, null);
						((CheckBox) layout2.findViewById(R.id.selecttype)).setVisibility(View.GONE);
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
						sublayout.addView(layout2);
						layouts.add(layout2);
					}
					layout_1.addView(layout1);
				}

			}
			layoutSheetx.addView(layout);
		}
	}

	/***
	 * 新增表格
	 * 
	 * @param name
	 *            新增表格名称
	 */
	private void createSheet(String name) {
		final LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.report_manger_item2, null);
		ImageView delBtn = (ImageView) layout.findViewById(R.id.Itemdelete);
		final TextView title = (TextView) layout.findViewById(R.id.ItemTitle);
		if (null != name) {
			title.setText(name);
		}
		ImageView editBtn = (ImageView) layout.findViewById(R.id.Itemedit);
		if (isDelete) {
			delBtn.setVisibility(View.VISIBLE);
		} else {
			delBtn.setVisibility(View.GONE);
		}
		final LinearLayout layout_1 = (LinearLayout) layout.findViewById(R.id.sublayoutx);
		final SheetInfoModel sheetModel = new SheetInfoModel();
		sheetModel.setSheetName(title.getText() + "");
		sheetsModel.getSheets().add(sheetModel);
		delBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(context);
				builder.setMessage(R.string.str_delete_makesure);
				builder.setTitle(R.string.str_tip);
				builder.setPositiveButton(R.string.str_ok, new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sheetsModel.getSheets().remove(sheetModel);
						refreshSheets(layoutSheet);
						dialog.dismiss();
					}
				});
				builder.setNegativeButton(R.string.str_cancle, new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
		});
		editBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				saveJson(false);
				currentSheetLinearLayout = layout_1;
				currentSheetLinearLayoutTitle = title.getText() + "";
				Bundle bundle = new Bundle();
				bundle.putString("currentSheet", currentSheetLinearLayoutTitle);
				bundle.putString("jsonFilePath", jsonFile.getAbsolutePath());
				bundle.putString("reportPath", reportPath);
				jumpActivityForResult(SelectTemplateStyleActivity.class, bundle, REQUESTCODE);
			}
		});
		layoutSheet.addView(layout);
		// 将新增的sheet信息写入json文件中,放在文件夹根目录下

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUESTCODE:
				parseJson();
				for (SheetInfoModel sheet : sheetsModel.getSheets()) {
					if (sheet.getSheetName().equals(currentSheetLinearLayoutTitle)) {
						refreshSheet(currentSheetLinearLayout, sheet);
						break;
					}
				}

				break;
			}
		}

	}

	private void refreshSheet(LinearLayout currentL, SheetInfoModel sheet) {
		currentL.removeAllViews();
		for (final GroupTemplateModel group : sheet.getGroups()) {
			for (final BusinessTemplateModel businessTemp : group.getBusinesses()) {
				final LinearLayout layout1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.selete_report_style_item1, null);
				((CheckBox) layout1.findViewById(R.id.selecttype)).setVisibility(View.GONE);
				final ImageView img1 = ((ImageView) layout1.findViewById(R.id.changeselect));
				final TextView tv1 = (TextView) layout1.findViewById(R.id.stylename);
				final LinearLayout sublayout = (LinearLayout) layout1.findViewById(R.id.sublayout);
				final List<LinearLayout> layouts = new LinkedList<LinearLayout>();
				tv1.setText(businessTemp.getShowBusinessName());
				currentL.addView(layout1);
				for (final StyleTemplateModel styleTemp : businessTemp.getStyles()) {
					LinearLayout layout2 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.selete_report_style_item2, null);
					((CheckBox) layout2.findViewById(R.id.selecttype)).setVisibility(View.GONE);
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
					sublayout.addView(layout2);
					layouts.add(layout2);
				}
			}
		}
	}

	/***
	 * 解析json文件
	 */
	private void parseJson() {
		try {
			if (jsonFile.exists()) {
				String json = FileUtil.getStringFromFile(jsonFile);
				Gson gson2 = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
				sheetsModel = gson2.fromJson(json, SheetsModel.class);
			} else {
				sheetsModel = new SheetsModel();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 保存json文件
	 * 
	 * @param saveXml 是否保存请求xml
	 */
	private void saveJson(boolean saveXml) {
		if (sheetsModel.getSheets().size() >= 0) { 
			if(sheetsModel.getSheets().size()==0){
				if(jsonFile.exists()){
					jsonFile.delete();
					jsonFile=null; 
				}
				jsonFile=new File(reportPath + ReportCommons.CUSTOMREPORT_XLSNAME);
				if(jsonFile.exists()){
					jsonFile.delete();
					jsonFile=null; 
				}
				jsonFile=new File(reportPath + ReportCommons.CUSTOMREPORT_XMLNAME);
				if(jsonFile.exists()){
					jsonFile.delete();
					jsonFile=null; 
				}
				return;
			}
			
			Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
			String obj2 = gson.toJson(sheetsModel);
			FileUtil.writeToFile(jsonFile, obj2);
			// 生成请求的xml
			List<String> styles = new LinkedList<String>();
			String tempPath = "";
			for (SheetInfoModel sheet : sheetsModel.getSheets()) {
				for (GroupTemplateModel group : sheet.getGroups()) {
					tempPath = group.getShowGroupName() + "/";
					for (BusinessTemplateModel business : group.getBusinesses()) {
						tempPath += business.getShowBusinessName() + "/";
						for (StyleTemplateModel style : business.getStyles()) {
							String stt=tempPath + style.getShowStyleName() + "/" + style.getShowStyleName() + ".xml";
							styles.add(ReportCommons.CUSTOMREPORT_TEMPLATE_PATH + "styletemplate/" + stt);
							stt=null;
						}
					}
				}
			}

			if (saveXml) { 
				StringBuffer businessStr = new StringBuffer();
				StringBuffer ParamStr = new StringBuffer();
				for (String style : styles) {
					String xmlStr = FileUtil.getStringFromFile(new File(style));
					int startIndex = xmlStr.indexOf(("<Businesses>"));
					int endIndex = xmlStr.indexOf(("</Businesses>"));
					if (startIndex > 0 && endIndex > 0 && startIndex < endIndex) {
						businessStr.append(xmlStr.substring(startIndex + "<Businesses>".length(), endIndex));
					}

					startIndex = xmlStr.indexOf(("<Parameters>"));
					endIndex = xmlStr.indexOf(("</Parameters>"));
					if (startIndex > 0 && endIndex > 0 && startIndex < endIndex) {
						ParamStr.append(xmlStr.substring(startIndex + "<Parameters>".length(), endIndex));
					}
				}

				StringBuffer xmlStr = new StringBuffer();
				xmlStr.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				xmlStr.append("<Report Id=\"-9999\" Version=\"2.0.0.0\">");
				xmlStr.append("<CheckNo>DFEFAED38BCBDF22DFEFAED38BCBDF22F4261267386D5A100572C9BA5B9494E9B0A164591B2841DDD50E459111A4772CDFEFAED38BCBDF22</CheckNo>");
				xmlStr.append("<ReportElement>");
				if (businessStr.length() > 0) {
					xmlStr.append("<Businesses>");
					xmlStr.append(businessStr);
					xmlStr.append("</Businesses>");
				}
				if (ParamStr.length() > 0) {
					xmlStr.append("<Parameters>");
					xmlStr.append(ParamStr);
					xmlStr.append("</Parameters>");
				}
				xmlStr.append("</ReportElement>");
				xmlStr.append("</Report>");
				FileUtil.writeToFile(new File(reportPath + ReportCommons.CUSTOMREPORT_XMLNAME), xmlStr.toString());
				//同时生成excel模板
				ReportFactory reportFactory=ReportFactory.getInstance(context); 
				reportFactory.createExcelReportTemplate(obj2, reportPath + ReportCommons.CUSTOMREPORT_XLSNAME);
			}
		}

	}
	
	/***
	 * 是否存在空的sheet
	 * @return
	 */
	private boolean checkNullSheet(){
		boolean isFlag=false;
		if(null!=sheetsModel){
			for(SheetInfoModel sheet:sheetsModel.getSheets())
			{
				if(sheet.getGroups().size()<=0){
					isFlag=true;
					break;
				}
			}
		}
		return isFlag;
	}

}
