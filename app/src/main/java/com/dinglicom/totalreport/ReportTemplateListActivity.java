package com.dinglicom.totalreport;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinglicom.dataset.TotalInterface;
import com.dinglicom.totalreport.TotalDetailActivity.TempLEnum;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.control.bean.FileOperater;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.CircleProgress;
import com.walktour.framework.view.FlashingView;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.data.FileManagerFragmentActivity;
import com.walktour.gui.report.ReportFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 报表选模板管理列表
 * 
 * @author zhihui.lian
 * 
 */
@SuppressLint("InflateParams")
public class ReportTemplateListActivity extends BasicActivity {

	public final static int REQUESTCODE = 1888;
	public final static int REQUESTCODE_2 = 1999;
	private Context context = ReportTemplateListActivity.this;
	private ControlBar bar;
	private Button button1, button2, button3, button4;

	private String configPathName;

	private String fileListDir; // 中英文路径

	private ArrayList<File> filePathList = new ArrayList<File>();;

	private HashMap<String, String> tempLMap = new HashMap<String, String>(); // 模板对象集合

	private HashMap<String, Object[][]> hashMap = new HashMap<String, Object[][]>(); // json对象集合

	private TotalInterface totalInterface = null;

	private boolean isReportFinish = false; // 报表是否执行完

	private LinearLayout layout1;
	private LinearLayout layout2;

	private boolean isDelete = false;
	/**
	 * 使用线程池
	 */
	private ExecutorService mExecutorService = Executors.newFixedThreadPool(5);

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listview_report_manger);
		configPathName = Environment.getExternalStorageDirectory() + "/Walktour/TotalConfig";
		fileListDir = configPathName + "/ReportExlTempLate/" + (getResources().getConfiguration().locale.getCountry().equals("CN") ? "CN" : "EN") + File.separator;

		initTextView(R.id.title_txt).setText(R.string.total_reportlist_title_str);
		initImageView(R.id.pointer).setOnClickListener(this);

		initTextView(R.id.exportcurrentreport).setOnClickListener(this);
//		initTextView(R.id.createreport).setOnClickListener(this);
		layout1 = initLinearLayout(R.id.layout11);
		layout2 = initLinearLayout(R.id.layout22);
		layout1.setVisibility(View.GONE);
		bar = (ControlBar) findViewById(R.id.ControlBar);
		bar.setBackgroundResource(R.drawable.toolbar_bg);

		button1 = bar.getButton(0);
		button1.setText(R.string.str_help);
		button1.setOnClickListener(this);
		button1.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_more), null, null);

		button2 = bar.getButton(1);
		button2.setText(R.string.total_reportlist_refresh_str);
		button2.setOnClickListener(this);
		button2.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_refresh), null, null);

		button3 = bar.getButton(2);
		button3.setText(R.string.delete);
		button3.setOnClickListener(this);
		button3.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_clear), null, null);

		button4 = bar.getButton(3);
		button4.setText(R.string.total_reportfile_str);
		button4.setOnClickListener(this);
		button4.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_new), null, null);
		totalInterface = TotalInterface.getInstance(getApplicationContext());

		refreshData();
	}

	private void refreshData() {
		fileLists();
		layout1.removeAllViews();
		layout2.removeAllViews();
		File ff = new File(ReportCommons.CUSTOMREPORT_PATH);
		if (ff.exists()) {
			File[] files = ff.listFiles();
			if (null != files && files.length > 0) {
				for (final File file : files) {
					LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.report_manger_item, null);
					ImageView delBtn = (ImageView) layout.findViewById(R.id.Itemdelete);
					final TextView tv = (TextView) layout.findViewById(R.id.ItemTitle);
					ImageView editBtn = (ImageView) layout.findViewById(R.id.Itemedit);
					if (isDelete) {
						delBtn.setVisibility(View.VISIBLE);
						editBtn.setVisibility(View.GONE);
					} else {
						delBtn.setVisibility(View.GONE);
						editBtn.setVisibility(View.VISIBLE);
					}
					tv.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if(isDelete)
								return;
							if (file.exists()) {
								tempLMap.clear();
								if (file.listFiles().length >=3) {//模板文件都生成好 
									String fileMangeStr = "";
									for (int i = 0; i < file.listFiles().length; i++) {
										if(!file.listFiles()[i].getName().endsWith(".json")){
											fileMangeStr += file.listFiles()[i].getAbsolutePath() + "|";
										}
									}
									tempLMap.put(file.getName(), fileMangeStr);
									Bundle bundle = new Bundle();
									bundle.putBoolean(FileManagerFragmentActivity.KEY_TOTAL_MODE, true);
									bundle.putBoolean(FileManagerFragmentActivity.KEY_ISREPORT, true); // 区分是否报表跳转
									jumpActivityForResult(FileManagerFragmentActivity.class, bundle, REQUESTCODE);
								} else {
									ToastUtil.showToastShort(context, R.string.server_file_notfound); 
								}
							}
						}
					});
					delBtn.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							AlertDialog.Builder builder = new AlertDialog.Builder(ReportTemplateListActivity.this);

							builder.setMessage(R.string.str_delete_makesure);

							builder.setTitle(R.string.str_tip);

							builder.setPositiveButton(R.string.str_ok, new AlertDialog.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									FileUtil.deleteDirectory(ReportCommons.CUSTOMREPORT_PATH + file.getName());
									refreshData();
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
							Bundle bundle = new Bundle();
							bundle.putBoolean("isEdit", true);
							bundle.putString("reportPath", ReportCommons.CUSTOMREPORT_PATH + file.getName() + "/");
							jumpActivity(CreateReportTemplateActivity.class, bundle);
						}
					});
					tv.setText(file.getName());
					layout1.addView(layout);
				}

			}
		}

		for (final File fileDir : filePathList) {
			LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.report_manger_item, null);
			ImageView delBtn = (ImageView) layout.findViewById(R.id.Itemdelete);
			TextView tv = (TextView) layout.findViewById(R.id.ItemTitle);
			ImageView editBtn = (ImageView) layout.findViewById(R.id.Itemedit);
			if (isDelete) {
				delBtn.setVisibility(View.VISIBLE);
			} else {
				delBtn.setVisibility(View.GONE);
			}
			delBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ToastUtil.showToastLong(context, "delBtn");
				}
			});
			tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(isDelete)
						return;
					if (fileDir.exists()) {
						tempLMap.clear();
						if (fileDir.listFiles().length > 1) {
							String fileMangeStr = "";
							for (int i = 0; i < fileDir.listFiles().length; i++) {
								fileMangeStr += fileDir.listFiles()[i].getAbsolutePath() + "|";
							}
							tempLMap.put(fileDir.getName(), fileMangeStr);
							Bundle bundle = new Bundle();
							bundle.putBoolean(FileManagerFragmentActivity.KEY_TOTAL_MODE, true);
							bundle.putBoolean(FileManagerFragmentActivity.KEY_ISREPORT, true); // 区分是否报表跳转
							jumpActivityForResult(FileManagerFragmentActivity.class, bundle, REQUESTCODE);
						} else {
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_file_notfound), Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
			editBtn.setVisibility(View.GONE);
			tv.setText(fileDir.getName());
			layout2.addView(layout);
		}
	}

	/**
	 * 句柄接收消息
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1001:
				// isReporting = false;
				Toast.makeText(getApplicationContext(), getString(R.string.total_export_success_str), Toast.LENGTH_SHORT).show();
				dialog.dismiss();
				mHandler.removeCallbacks(runnable);
				circleProgress.setProgress(0);
				flashingView.setText("0%");
				break;
			case 1002:
				ToastUtil.showToastShort(getApplicationContext(),R.string.main_result_main_stop);
				break;
			case 10000:
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.total_report_not_file), Toast.LENGTH_SHORT).show();
				break;
			case Msg_ReportHTML_ONE:

				break;
			default:
				break;
			}
		};
	};

	/**
	 * 获取模板路径文件list 遍历目录
	 */
	private void fileLists() {
		File root = new File(fileListDir);
		File[] files = root.listFiles();
		filePathList.clear();
		for (File file : files) {
			if (file.isDirectory()) {
				if (isXlsOrXMl(file.listFiles())) {
					filePathList.add(file);
				}
			}
		}
	}

	/**
	 * 判断excel模板与xml模板是否符合规范
	 */
	private boolean isXlsOrXMl(File[] fileList) {
		boolean isXls = false;
		boolean isXml = false;
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].toString().toLowerCase(Locale.getDefault()).endsWith(".xls")) {
				isXls = true;
			}
			if (fileList[i].toString().toLowerCase(Locale.getDefault()).endsWith(".xml")) {
				isXml = true;
			}
		}
		return isXls && isXml;
	}

	/**
	 * 解析JSON，主要是提取数据里头的业务类型
	 */
	@SuppressWarnings("rawtypes")
	private void jsonParser() {
		tempLMap.clear();
		String resultJsonOne = readFile(configPathName + "/resultJson/resultJsonOne.json");
		JSONTokener parser = new JSONTokener(resultJsonOne);
		try {
			JSONObject parent = (JSONObject) parser.nextValue();
			for (Iterator iter = parent.keys(); iter.hasNext();) {
				String key = (String) iter.next();
				JSONArray arr = parent.getJSONArray(key);
				Object[][] arrValues = new Object[arr.length()][];
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					arrValues[i] = new Object[obj.length()];
					int pos = 0;
					for (Iterator iter1 = obj.keys(); iter1.hasNext();) {
						String key1 = (String) iter1.next();
						arrValues[i][pos++] = key1 + ":" + obj.get(key1);
					}
				}
				this.hashMap.put(key, arrValues);
			}
			extraTask();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提取JSON里包含的业务
	 */
	@SuppressWarnings("rawtypes")
	private void extraTask() {
		Iterator iter = hashMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			for (int j = 0; j < TempLEnum.values().length; j++) {
				String[] tempLStrArray = TempLEnum.values()[j].getTempLStr();
				for (int k = 0; k < tempLStrArray.length; k++) {
					if (entry.getKey().toString().indexOf(TempLEnum.values()[j].getTempLStr()[k]) != -1) {
						if (!(TempLEnum.values()[j].toString().equals("CSFB") || TempLEnum.values()[j].toString().equals("CS"))) {
							tempLMap.put(TempLEnum.values()[j].toString(), "");
						} else {
							difCSorCSFB(entry);
						}
					}
				}
			}
		}
	}

	/**
	 * 区分CS与CSFB
	 * 
	 * @param entry
	 */
	private void difCSorCSFB(@SuppressWarnings("rawtypes") Map.Entry entry) {
		Object[][] value = (Object[][]) entry.getValue();
		for (int i = 0; i < value.length; i++) {
			if (value[0][i].toString().indexOf("BUSINESSCOUNT_ATTEMPTS") != -1) { // 这里判断CS与CSFB
				if (Double.valueOf(value[0][i].toString().split(":")[1].toString()) > 0) {
					if (entry.getKey().toString().indexOf("CSFB") != -1) {
						tempLMap.put("CSFB", "");
					} else {
						tempLMap.put("CS", "");
					}
					break;
				}
			}
		}
	}

	/**
	 * 读取Json文件
	 * 
	 * @param path
	 * @return
	 */
	private String readFile(String path) {
		String str = "";
		File file = new File(path);
		try {

			FileInputStream in = new FileInputStream(file);
			int size = in.available();
			byte[] buffer = new byte[size];
			in.read(buffer);
			in.close();
			str = new String(buffer, "utf-8");

		} catch (IOException e) {
			return null;
		}
		return str;
	}

	private boolean stopTotal = false;

	// private BasicDialog alert;

	private AlertDialog dialog;

	private CircleProgress circleProgress;

	private FlashingView flashingView;

	/**
	 * 请求报表生成报表
	 */
	private void createReport(HashMap<String, String> templMaps, List<String> ddibPathList) {
		stopTotal = false;
		isReportFinish = false;
		Iterator<?> iter = templMaps.entrySet().iterator();
		int i = 0;
		while (iter.hasNext() && !stopTotal) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			createRequestXML(entry, ddibPathList);
			totalInterface.ExportReportJson();
			try {
				TempLEnum.valueOf(entry.getKey().toString());
				String fileNameStr = UtilsMethod.sdfyMdhms.format(System.currentTimeMillis());
				String reportFileName = String.format("%s%s%s", fileNameStr, "_" + TempLEnum.valueOf(entry.getKey().toString()).getTempLName(), ".xls");
				ReportFactory.getInstance(getApplicationContext()).createReport(getApplicationContext(), readJsonFile(configPathName + "/resultJson/resultJsonXls.json"), fileListDir + TempLEnum.valueOf(entry.getKey().toString()).getTempLName() + File.separator + TempLEnum.valueOf(entry.getKey().toString()).getTempLName() + ".xls", true, reportFileName, mHandler);
			} catch (Exception e) {
				String fileNameStr = UtilsMethod.sdfyMdhms.format(System.currentTimeMillis());
				String reportFileName = String.format("%s%s%s", fileNameStr, "_" + entry.getKey(), ".xls");
				if (getPathValue(entry.getValue().toString(), ".xls").length() <= 1) {
					System.out.println("xls no 2003 or xls is null");
				}
				ReportFactory.getInstance(getApplicationContext()).createReport(getApplicationContext(), readJsonFile(configPathName + "/resultJson/resultJsonXls.json"), getPathValue(entry.getValue().toString(), ".xls"), true, reportFileName, mHandler);
			}
			i++;
		}
		if (templMaps.size() == i) {
			isReportFinish = true;
			mHandler.sendEmptyMessage(1001); // 导出成功
		}

	}

	/**
	 * 取值xls或xml
	 */

	private String getPathValue(String merger, String suffix) {
		String[] mergerArray = merger.split("\\|");
		for (int i = 0; i < mergerArray.length; i++) {
			if (mergerArray[i].endsWith(suffix)) {
				return mergerArray[i];
			}
		}
		return "";
	}

	/**
	 * 创建报表请求XML
	 * 
	 * @param entry
	 */
	@SuppressWarnings("rawtypes")
	private void createRequestXML(Map.Entry entry, List<String> ddibPathList) {
		ReportXlsConfigModel dlMessageModel = new ReportXlsConfigModel();
		List<SubDlMessageItem> subDlMessageItems = new ArrayList<SubDlMessageItem>();
		SubDlMessageItem subDlMessageItem = new SubDlMessageItem();
		dlMessageModel.setSendTime(UtilsMethod.sdFormatss.format(System.currentTimeMillis()));
		subDlMessageItem.setSceneName("");
		try {
			TempLEnum.valueOf(entry.getKey().toString());
			String str=fileListDir + TempLEnum.valueOf(entry.getKey().toString()).getTempLName() + File.separator + TempLEnum.valueOf(entry.getKey().toString()).getTempLName() + ".xml";
			dlMessageModel.setTemplateFile(str);
			str=null;
		} catch (Exception e) {
			dlMessageModel.setTemplateFile(getPathValue(entry.getValue().toString(), ".xml"));
		}
		subDlMessageItem.setDataFileName(ddibPathList);
		subDlMessageItems.add(subDlMessageItem);
		dlMessageModel.setSubDlMessageItems(subDlMessageItems);
		RequestSceneXMl.getInstance().xmlXlsCreator(dlMessageModel);
	}

	/**
	 * 读取Json文件
	 */

	private String readJsonFile(String jsonPath) {
		File jsonFile = new File(jsonPath);
		String textStr = FileOperater.getTxtFromFile(jsonFile).toString();
		return textStr;
	}

	class CreateReportThread implements Runnable {

		public List<String> ddibLists;

		public CreateReportThread(List<String> ddibLists) {
			this.ddibLists = ddibLists;
		}

		@Override
		public void run() { 
			if (ddibLists.size() > 0) {
				createReport(tempLMap, ddibLists);
			} else {
				mHandler.sendEmptyMessage(10000);
			}
		}

	}

	class StopThread implements Runnable {
		@Override
		public void run() {
			stopTotal = true;
			totalInterface.stopTotal();
			mHandler.sendEmptyMessage(1002); // 停止导出
		}

	} 
	/**
	 * 显示导出进度dialog
	 */
	@SuppressWarnings("deprecation")
	private void showProgressDialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.show_dialog_progress, null);
		dialog = new AlertDialog.Builder(this).create();
		dialog.show();
		dialog.setContentView(view);
		Window window = dialog.getWindow();
		WindowManager m = window.getWindowManager();
		Display d = m.getDefaultDisplay();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.height = (int) (d.getHeight() * 0.35);
		lp.width = (int) (d.getWidth() * 0.6);
		window.setLayout(lp.width, ViewGroup.LayoutParams.WRAP_CONTENT);
		circleProgress = (CircleProgress) view.findViewById(R.id.circleProgress);
		flashingView = (FlashingView) view.findViewById(R.id.flashView);

		TextView cancelBtn = (TextView) view.findViewById(R.id.cancel_btn);
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (!isReportFinish) {
					new Thread(new StopThread()).start();
				}
			}
		});

	}

	/**
	 * 每500毫秒实时查询进度
	 */

	private final int Msg_ReportHTML_ONE = 101;

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			int progress = (int) (totalInterface.queryProgress() * 100);
			circleProgress.setProgress(progress);
			flashingView.setText(progress + "%");
			if (progress == 100) {
				// mHandler.sendEmptyMessage(Msg_ReportHTML_ONE);
			}
			mHandler.postDelayed(this, 500); // 每500毫秒监听进度
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		// 导出当前报表
		case R.id.exportcurrentreport:
			String ddibPathStr = SharePreferencesUtil.getInstance(getApplicationContext()).getString(FileManagerFragmentActivity.TOTAL_DDIB_PATH, "");
			String[] ddibPathArray = ddibPathStr.split("\\*");
			List<String> ddibPathList = new ArrayList<String>();
			if (ddibPathArray.length != 0) {
				ddibPathList = Arrays.asList(ddibPathArray);
			}
			showProgressDialog();
			mHandler.post(runnable);
			jsonParser();
			mExecutorService.execute(new CreateReportThread(ddibPathList));
			break;
		// 创建自定义报表模板
//		case R.id.createreport:
//			createReport();
//			break;
		// 帮助
		case R.id.Button01:
			showHelpDialog();
			break;

		// 刷新模板
		case R.id.Button02:
			fileLists();
			// customadapter.notifyDataSetChanged();
			break;
		// 报表管理
		case R.id.Button03:
			isDelete = !isDelete;
			if (isDelete) {
				button3.setText(R.string.str_cancle);
				
				button1.setVisibility(View.GONE);
				button2.setVisibility(View.GONE);
				button4.setVisibility(View.GONE);
			} else {
				button3.setText(R.string.delete);
				
				button1.setVisibility(View.VISIBLE);
				button2.setVisibility(View.VISIBLE);
				button4.setVisibility(View.VISIBLE);
			}
			this.refreshData();
			break;
		// 报表管理
		case R.id.Button04:
			jumpActivity(TotalReportXlsActivity.class);
			break;
		// 关闭
		case R.id.pointer:
			finish();
		default:
			break;
		}
	}

	private void createReport() {
		final EditText et = new EditText(this);
		et.setHint(R.string.total_reportlist_export_str6);
		new AlertDialog.Builder(this).setTitle(R.string.total_reportlist_export_str5).setView(et).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {// 确定
					if (null == et.getText() || et.getText().toString().length() <= 0) {
						ToastUtil.showToastShort(context, R.string.total_reportlist_export_str9);
						return;
					}
					String reportPath = ReportCommons.CUSTOMREPORT_PATH + et.getText() + "/";
					File fid = new File(reportPath);
					if (fid.exists()) {
						ToastUtil.showToastShort(context, R.string.total_reportlist_export_str8);
						return;
					}
					fid.mkdir();
					Bundle bundle = new Bundle();
					bundle.putBoolean("isEdit", false);
					bundle.putString("reportPath", reportPath);
					jumpActivityForResult(CreateReportTemplateActivity.class, bundle, REQUESTCODE_2);
				}
			}

		}).setNegativeButton(R.string.str_cancle, null).show();
	}

	/**
	 * 帮助弹框
	 */
	private void showHelpDialog() {
		LayoutInflater fac = LayoutInflater.from(getApplicationContext());
		View view = fac.inflate(R.layout.total_help_layout, null);
		BasicDialog alert = new BasicDialog.Builder(ReportTemplateListActivity.this).setIcon(R.drawable.icon_info).setTitle(R.string.str_help).setView(view).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		}).create();
		alert.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUESTCODE:
				showProgressDialog();
				mHandler.post(runnable);
				mExecutorService.execute(new CreateReportThread(data.getStringArrayListExtra(FileManagerFragmentActivity.DDIBLIST)));
				break;
			case REQUESTCODE_2:
				this.refreshData();
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mExecutorService.shutdown();
	}

}
