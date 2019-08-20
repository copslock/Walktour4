package com.dinglicom.totalreport;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.dinglicom.dataset.TotalInterface;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.control.bean.FileOperater;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.CircleProgress;
import com.walktour.framework.view.FlashingView;
import com.walktour.gui.R;
import com.walktour.gui.report.ReportFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * 自定义报表统计详情显示
 * 
 * @author weirong.fan
 *
 */
public class CustomReportActivity extends BasicActivity {
	private TextView title;
	private WebView webView;
	/** 配置文件包名 */
	public String configPathName;
	private String jsonPath = "";
	private TotalInterface totalInterface = null;
	private CircleProgress circleProgress;
	private AlertDialog dialog;
	private FlashingView flashingView;
	private boolean isReportFinish = false; // 报表是否执行完
	private boolean stopTotal = false;
	private HashMap<String, String> tempLMap = new HashMap<String, String>(); // 模板对象集合
	/**
	 * 使用线程池
	 */
	private ExecutorService mExecutorService = Executors.newFixedThreadPool(5);

	private ArrayList<String> ddibLists=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.total_report_detail_ui_customreport);
		configPathName = Environment.getExternalStorageDirectory() + "/Walktour/TotalConfig";
		jsonPath = getIntent().getStringExtra("jsonPath");
		ddibLists=getIntent().getStringArrayListExtra("selectDDIBFiles");
		totalInterface = TotalInterface.getInstance(getApplicationContext());

		File file = new File(jsonPath);
		tempLMap.clear();
		if (file.listFiles().length > 1) {
			String fileMangeStr = "";
			for (int i = 0; i < file.listFiles().length; i++) {
				if (!file.listFiles()[i].getName().endsWith(".json")) {
					fileMangeStr += file.listFiles()[i].getAbsolutePath() + "|";
				}
			}
			tempLMap.put(file.getName(), fileMangeStr);
		}

		

		initViews();
	}

	private void initViews() {
		this.initImageButton(R.id.pointer).setOnClickListener(this);
		title = initTextView(R.id.title_txt);
		title.setText(R.string.total_reportlist_export_str1);
		title.setOnClickListener(this);
		webView = (WebView) this.findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
		WebViewClient client = new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				showProgressDialog();
				mHandler.post(runnable);
				mExecutorService.execute(new CreateReportThread(ddibLists));
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

		};
		webView.setWebViewClient(client);
		webView.loadUrl("file:///android_asset/html/customreport.html");
	}

	class CreateReportThread implements Runnable {

		public ArrayList<String> ddibLists;

		public CreateReportThread(ArrayList<String> ddibLists) {
			this.ddibLists = ddibLists;
		}

		@Override
		public void run() {
			if (null!=ddibLists&&ddibLists.size() > 0) {
				createReport(tempLMap, ddibLists);
			} else {
				mHandler.sendEmptyMessage(10000);
			}
		}

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
		dlMessageModel.setTemplateFile(getPathValue(entry.getValue().toString(), ".xml"));
		subDlMessageItem.setDataFileName(ddibPathList);
		subDlMessageItems.add(subDlMessageItem);
		dlMessageModel.setSubDlMessageItems(subDlMessageItems);
		RequestSceneXMl.getInstance().xmlXlsCreator(dlMessageModel);
	}

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

			String fileNameStr = UtilsMethod.sdfyMdhms.format(System.currentTimeMillis());
			String reportFileName = String.format("%s%s%s", fileNameStr, "_" + entry.getKey(), ".xls");
			if (getPathValue(entry.getValue().toString(), ".xls").length() <= 1) {
				System.out.println("xls no 2003 or xls is null");
			}
			ReportFactory.getInstance(getApplicationContext()).createReport(getApplicationContext(), readJsonFile(configPathName + "/resultJson/resultJsonXls.json"), getPathValue(entry.getValue().toString(), ".xls"), true, reportFileName, mHandler);

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
	 * 读取Json文件
	 */

	private String readJsonFile(String jsonPath) {
		File jsonFile = new File(jsonPath);
		String textStr = FileOperater.getTxtFromFile(jsonFile).toString();
//		try {
//			String xx=new String(textStr.getBytes("iso-8859-1"),"UTF-8");
//			System.out.println("9900=0"+xx);
//			xx=new String(textStr.getBytes("UTF-8"),"GB2312");
//			System.out.println("9900=1"+xx);
//			xx=new String(textStr.getBytes("GBK"),"UTF-8");
//			System.out.println("9900=2"+xx);
//		} catch (UnsupportedEncodingException e) { 
//			e.printStackTrace();
//		}
		return textStr;
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
				
			}
			mHandler.postDelayed(this, 500); // 每500毫秒监听进度
		}
	};
	/**
	 * 句柄接收消息
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1001: 
				dialog.dismiss();
				mHandler.removeCallbacks(runnable);
				circleProgress.setProgress(0);
				flashingView.setText("0%"); 
				String resultJsonPath = configPathName + "/resultJson/resultJsonXls.json";
				File file = new File(jsonPath + "/" + ReportCommons.CUSTOMREPORT_JSONNAME);
				String str1 = FileUtil.getStringFromFile(file);
				file = null;
				file = new File(resultJsonPath);
				String str2 = FileUtil.getStringFromFile(file);
				file = null; 
				webView.loadUrl("javascript: viewDidLoadData(" + str2 + "," + str1 + ",'"+StringUtil.getLanguage()+"')");
				Toast.makeText(getApplicationContext(), getString(R.string.total_export_success_str), Toast.LENGTH_SHORT).show();
				break;
			case 1002:
				ToastUtil.showToastShort(getApplicationContext(), R.string.main_result_main_stop);
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

	class StopThread implements Runnable {
		@Override
		public void run() {
			stopTotal = true;
			totalInterface.stopTotal();
			mHandler.sendEmptyMessage(1002); // 停止导出
		}

	}

	/**
	 * 返回键处理
	 * 
	 * @param keyCode
	 * @param event
	 * @return
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.pointer:// 返回
			finish();
			break;

		default:
			break;
		}
	}
}
