package com.dinglicom.totalreport;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinglicom.dataset.DataCenter;
import com.dinglicom.dataset.TotalInterface;
import com.dinglicom.totalreport.GroupUtil.GroupBy;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.MD5Util;
import com.walktour.control.bean.FileOperater;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.config.GoOrNogoSetting;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.CircleProgress;
import com.walktour.framework.view.FlashingView;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.data.FileManagerFragmentActivity;
import com.walktour.gui.data.FileManagerFragmentActivity2;
import com.walktour.gui.setting.GoOrNogoSettingActivity;
import com.walktour.gui.setting.Sys;
import com.walktour.model.Business;
import com.walktour.model.GoOrNogoParameter;

import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 统计报表详细界面显示
 * 
 * @author zhihui.lian
 */
public class TotalDetailActivity extends BasicActivity {

	private String TAG="TotalDetailActivity";
	public static final int TEST_TYPE_DT = 1;
	public static final int TEST_TYPE_CQT = 0;
	public static final int REQUESTCODE = 1001;

	private WebView webView1;
	private XWalkView webView2;
	private LinearLayout circleProgress_layout;
	private CircleProgress circleProgress;
	private FlashingView flashingView;
	private ControlBar bar;
	private Context mContext;
	private final static int Msg_ReportHTML_Progress = 200;
	private final static int Msg_ReportHTML_ONE_LOAD = 202;
	private final static int Msg_ReportHTML_TOTAL_FAIL = 203; // 统计失败消息提醒
	private final static int Msg_ReportHTML_TOTAL_SUCCESS = 204; // 统计失败消息提醒

	private final static int REFRESH_HTML = 100890; // 刷新界面

	private TotalInterface totalInterface = null;

	public String configPathName; // 配置文件包名

	private TextView title;
	private ImageView pointer;
	private ImageView settingBtn;
	private Button btn_count;
	private Button btnExpReport;
	private Button btnExpGrap;
	private Button btnExpAuto;
	private Button btnReport;
	private ApplicationModel appModel;
	public boolean isExit = false;
	private Button exportBtn;
	private Button filesMgrbtn;
	/** 服务器管理类 */
	private ServerManager mServer;
	/** 进度提示 */
	private ProgressDialog progressDialog;
	private Context context=TotalDetailActivity.this;
	/**
	 * 句柄接收消息
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_HTML:
				Callfunction(msg.obj.toString());
				break;
			case Msg_ReportHTML_Progress:
				mHandler.removeCallbacks(runnable);
				circleProgress_layout.setVisibility(View.GONE);
				circleProgress.setProgress(0);
				break;
			case Msg_ReportHTML_TOTAL_SUCCESS:
				showView(1);
				break;
			case Msg_ReportHTML_ONE_LOAD:
				circleProgress_layout.setVisibility(View.VISIBLE);
				circleProgress.setProgress(0);
				flashingView.setText("0%");
				break;
			case Msg_ReportHTML_TOTAL_FAIL:
				circleProgress_layout.setVisibility(View.GONE);
				exportBtn.setEnabled(false);
				mHandler.removeCallbacks(runnable);
				totalInterface.stopTotal();
				Toast.makeText(mContext, getString(R.string.total_export_fail_str), Toast.LENGTH_SHORT).show();
				break;
			case REQUESTCODE:
				isReporting = false;
				exportBtn.setEnabled(true);
				Toast.makeText(mContext, getString(R.string.total_export_success_str), Toast.LENGTH_SHORT).show();
				break;
			case 1002:
				ToastUtil.showToastShort(getApplicationContext(),R.string.main_result_main_stop);
				break;
			case 10086:
				setTextStr(msg.obj.toString());
				break;
			case 10087:
				setTextStr("");
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.total_report_detail_ui);
		genToolBar();
		title = initTextView(R.id.title_txt);
		settingBtn = initImageView(R.id.setting_btn);
		settingBtn.setOnClickListener(this);
		title.setEllipsize(TextUtils.TruncateAt.valueOf("START"));
		title.setText(getString(R.string.total_info_str));
		pointer = initImageView(R.id.pointer);
		pointer.setOnClickListener(this);
		appModel = ApplicationModel.getInstance();
		mServer = ServerManager.getInstance(this);
		mContext = this;
		configPathName = Environment.getExternalStorageDirectory() + "/Walktour/TotalConfig";
		totalInterface = TotalInterface.getInstance(getApplicationContext());
		initView();
		Intent data=getIntent();
		if (data.getBooleanExtra(FileManagerFragmentActivity.KEY_FILEMANAGER_TO_TOTALDETAIL,false)){
			addDateIntoWebview(getIntent());
		}else {
			if (SharePreferencesUtil.getInstance(mContext).getInteger(FileManagerFragmentActivity.IS_TOTAL, -1) != -1) {
			new Thread(new LoadData()).start();
			} else {
				if(Deviceinfo.getInstance().isVivoX23()){
					webView2.loadUrl("file:///android_asset/html/" + (getResources().getConfiguration().locale.getCountry().equals("CN") ? "" : "en/") + "warning.html");
				}else{
					webView1.loadUrl("file:///android_asset/html/" + (getResources().getConfiguration().locale.getCountry().equals("CN") ? "" : "en/") + "warning.html");
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void genToolBar() {
		bar = (ControlBar) findViewById(R.id.ControlBar);
		bar.setBackgroundResource(R.drawable.toolbar_bg);
		// get button from bar
		btn_count = bar.getButton(0);
		btnExpReport = bar.getButton(1);
		btnExpGrap = bar.getButton(2);
		btnExpAuto = bar.getButton(3);
		btnReport = bar.getButton(4);

		// set text
		btn_count.setText(R.string.total_total_file);
		btnExpReport.setText(R.string.total_export_report);
		btnExpReport.setVisibility(View.INVISIBLE);
		btnExpGrap.setText(R.string.fleet_remote_report);
//		btnExpGrap.setVisibility(View.INVISIBLE);
		btnExpAuto.setText(R.string.total_auto_report);
		btnExpAuto.setVisibility(View.INVISIBLE);
		btnReport.setText(R.string.fleet_local_report);

		// set icon
		btn_count.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_load), null, null);
		btnExpGrap.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_muilt), null, null);
		btnReport.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_new), null, null);

		btn_count.setOnClickListener(this);
		btnExpGrap.setOnClickListener(this);
		btnReport.setOnClickListener(this);
	}

	/**
	 * 模板对应业务枚举类
	 */
	public static enum TempLEnum {
		CS("Voice 23G", "CS"), CSFB("Voice CSFB", "CSFB"), HTTP("HTTP", "HTTP"), PingTraceroute("Ping-Traceroute", "Traceroute", "Ping"), VideoPBMSpeedtest("Video-PBM-Speedtest", "Video", "PBM", "Speedtest"), FTP("FTP", "FTP");

		private String tempLStr[]; // 模板业务名字
		private String tempLName; // 模板包名

		TempLEnum(String tempLName, String... tempLStr) {
			this.tempLStr = tempLStr;
			this.tempLName = tempLName;
		}

		public String[] getTempLStr() {
			return tempLStr;
		}

		public String getTempLName() {
			return tempLName;
		}

	}

	boolean isReporting = false;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.Button01://统计文件
				jumpActivityForResult(SelectTemplateListActivity.class, REQUESTCODE);
				break;
			case R.id.Button03://FLEET报表
//				String ip = this.mServer.getUploadFleetIp();
//				if (!this.mServer.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
//					ToastUtil.showToastShort(this,R.string.work_order_fleet_ip_null);
//					jump2SettingActivity();
//					return;
//				}
//				int port = this.mServer.getUploadFleetPort();
//				if (!this.mServer.getFleetServerType() || !Verify.isPort(String.valueOf(port))) {
//					ToastUtil.showToastShort(this,R.string.work_order_fleet_port_invalid);
//					jump2SettingActivity();
//					return;
//				}


				//如果服务器设置错误,也跳转到设置界面
				new CheckUser().execute();
				break;
			case R.id.Button05: //报表
				jumpActivity(ReportTemplateListActivity.class);
				break;
			case R.id.pointer://返回
				backKey();
				break;
			case R.id.setting_btn: //设置
				jumpActivity(GoOrNogoSettingActivity.class);
				break;
			default:
				break;
		}
	}

	/**
	 * 跳转到服务器设置界面
	 */
	private void jump2SettingActivity() {
		Bundle bundle = new Bundle();
		bundle.putInt(Sys.CURRENTTAB, 0);
		Intent intent = new Intent(this,Sys.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}


	private class CheckUser extends AsyncTask<Void, Void, Boolean> {
		private CheckUser() {
			super();
		}
		@Override
		protected void onPostExecute(Boolean value) {
			super.onPostExecute(value);
			closeDialog();
			if(value){
				jumpActivity(FleetReportActivity.class);
			}else{
				ToastUtil.showToastShort(context,R.string.mutilytester_loginServer_Faild);
				jump2SettingActivity();
			}

		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			openDialog(getString(R.string.share_project_server_doing));
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				StringBuffer url = new StringBuffer();
				url.append("http://");
				url.append(mServer.getDownloadFleetIp());
				url.append(":");
				url.append(mServer.getDownloadFleetPort());
				url.append("/Services/SSVService.svc/Authorize?");
				url.append("Account=" + mServer.getFleetAccount());
				url.append("&Password=" + MD5Util.md5Password(mServer.getFleetPassword()));
				LogUtil.w(TAG, "get url is=" + url.toString());
				OkHttpClient okHttpClient_get = new OkHttpClient();
				Request request = new Request.Builder()
						.get()
						.url(url.toString())
						.build();
				Response response = okHttpClient_get.newCall(request).execute();
				if (response.isSuccessful()) {
					Gson gson=new Gson();
					ResultValue resultValue=gson.fromJson(response.body().string(),ResultValue.class);
					return resultValue.isSuccess();
				}
			}catch(Exception ex){
				ex.printStackTrace();;
			}
			return false;
		}

		/**
		 * 返回的结果值		//{"Message":null,"Success":true}
		 */
		private class ResultValue{
			@SerializedName("Message")
			private String message="";
			@SerializedName("Success")
			private boolean success=false;

			public String getMessage() {
				return message;
			}

			public void setMessage(String message) {
				this.message = message;
			}

			public boolean isSuccess() {
				return success;
			}

			public void setSuccess(boolean success) {
				this.success = success;
			}
		}
	}
	/**
	 * 打开进度条
	 *
	 * @param txt
	 */
	protected void openDialog(String txt) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(txt);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	/**
	 * 关闭进度条
	 */
	protected void closeDialog() {
		progressDialog.dismiss();
	}

	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	private void initView() {
		webView1 = (WebView) this.findViewById(R.id.webView1);
		webView2 = (XWalkView) this.findViewById(R.id.webView2);
		if(Deviceinfo.getInstance().isVivoX23()){

			XWalkSettings xWalkSettings = webView2.getSettings();
			//webView.getSettings().setRenderPriority(RenderPriority.HIGH);
			xWalkSettings.setSupportZoom(true); //支持缩放
			xWalkSettings.setBuiltInZoomControls(true); //支持任意缩放
			xWalkSettings.setLoadWithOverviewMode(true);
			xWalkSettings.setUseWideViewPort(true); //图片调整

			xWalkSettings.setJavaScriptEnabled(true);
			xWalkSettings.setAllowFileAccess(true);

			xWalkSettings.setAllowFileAccessFromFileURLs(true);
			xWalkSettings.setAllowUniversalAccessFromFileURLs(true);
		}else{

			webView1.getSettings().setRenderPriority(RenderPriority.HIGH);
			webView1.getSettings().setJavaScriptEnabled(true);
			webView1.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					return true;
				}
			});
		}
		if(Deviceinfo.getInstance().isVivoX23()){
			webView1.setVisibility(View.GONE);
			webView2.setVisibility(View.VISIBLE);
		}else{
			webView1.setVisibility(View.VISIBLE);
			webView2.setVisibility(View.GONE);
		}
		circleProgress_layout = initLinearLayout(R.id.circleProgress_layout);
		circleProgress_layout.setOnClickListener(this);
		circleProgress = (CircleProgress) findViewById(R.id.circleProgress);
		flashingView = (FlashingView) findViewById(R.id.flashView);
		exportBtn = initButton(R.id.button2);
		exportBtn.setOnClickListener(this);
		filesMgrbtn = initButton(R.id.button3);
		filesMgrbtn.setOnClickListener(this);
	}

	/**
	 * 读取Json文件
	 */

	private String readJsonFile(String jsonPath) {
		File jsonFile = new File(jsonPath);
		String textStr = FileOperater.getTxtFromFile(jsonFile).toString();
		return textStr;
	}

	/**
	 * html请求显示操作
	 */
	int index = 0;
	int indexSub;

	private void showView(int page1) {
		int totalType = SharePreferencesUtil.getInstance(mContext).getInteger(FileManagerFragmentActivity2.IS_TOTAL, -1);
		String name = (totalType != TEST_TYPE_CQT) ? "DTListView.html" : "CQTListView.html";
		if (page1 == 1) {
			if(Deviceinfo.getInstance().isVivoX23()){
				webView2.loadUrl("file:///android_asset/html/" + (getResources().getConfiguration().locale.getCountry().equals("CN") ? "" : "en/") + name);
			}else {
				webView1.loadUrl("file:///android_asset/html/" + (getResources().getConfiguration().locale.getCountry().equals("CN") ? "" : "en/") + name);
			}
		}
		if(Deviceinfo.getInstance().isVivoX23()){
			XWalkUIClient uiClient = new XWalkUIClient(webView2) {
				@Override
				public void onPageLoadStarted(XWalkView view, String url) {
					super.onPageLoadStarted(view, url);
					indexSub = 0;
				}

				@Override
				public boolean onJsAlert(XWalkView view, String url, String message, XWalkJavascriptResult result) {
					//boolean bResult = super.onJsAlert(view, url, message, result);


					//LogUtil.w(TAG, "onJsAlert " + Boolean.toString(bResult));

					//return bResult;
					result.cancel();

					return false;
				}

				public void onPageLoadStopped(XWalkView view, String url, XWalkUIClient.LoadStatus status) {
					super.onPageLoadStopped(view, url, status);

					indexSub++;
					if (index < 2 || url.contains("?")) {
						if (indexSub <= 1) {
							new runReport(url).start();
						}
					}
					index++;
				}
			};
			webView2.setUIClient(uiClient);
		}else{
			WebViewClient client = new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					indexSub++;
					if (index < 2 || url.contains("?")) {
						if (indexSub <= 1) {
							new runReport(url).start();
						}
					}
					index++;
				}

				@Override
				public void onLoadResource(WebView view, String url) {
					super.onLoadResource(view, url);
				}

				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					indexSub = 0;
				}

			};
			webView1.setWebViewClient(client);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isExit = true;
		// totalInterface.freeLib();
	}

	/**
	 * 报表请求线程
	 */
	class runReport extends Thread {
		private String url;

		public runReport(String urlStr) {
			this.url = urlStr;
		}

		@Override
		public void run() {
			if (url.contains("?")) {
				mHandler.sendEmptyMessage(Msg_ReportHTML_ONE_LOAD);
				String[] requestArray = url.substring(url.indexOf("?") + 1, url.length()).split("&");
				String biz = "";
				String sence = "";
				for (int i = 0; i < requestArray.length; i++) {
					if (requestArray[i].startsWith("biz=")) {
						biz = requestArray[i].replaceAll("biz=", "");
					}
					if (requestArray[i].startsWith("sen=")) {
						sence = requestArray[i].replaceAll("sen=", "");
					}
				}
				Message msg = Message.obtain();
				msg.what = 10086;
				msg.obj = biz;
				mHandler.sendMessage(msg);
				createL2Xml(biz, sence);
				isFinish = false;
				mHandler.post(runnable);
				// isOne = false;
				totalInterface.getTotalL2Result(configPathName + "/sceneXmlTwo.xml");
				isFinish = true;
				String oneJson = readFile(configPathName + "/resultJson/resultJsonOne.json");
				String twoJson = readFile(configPathName + "/resultJson/resultJsonTwo.json");
				mHandler.sendMessage(Message.obtain(mHandler, REFRESH_HTML, oneJson.substring(0, (oneJson.length() - 1)) + "," + twoJson.trim().substring(twoJson.indexOf("{") + 1, twoJson.length())));
			} else {
				mHandler.sendMessage(Message.obtain(mHandler, REFRESH_HTML, readJsonFile(configPathName + "/resultJson/resultJsonOne.json")));
				mHandler.sendEmptyMessage(10087);
			}
		}
	}

	private void setTextStr(String titleStr) {
		if (titleStr != null && titleStr.trim().length() != 0) {
			title.setText(titleStr);
		} else {
			title.setText(getString(R.string.total_info_str));
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

	/**
	 * 构造二级请求XML
	 */

	private void createL2Xml(String biz, String scene) {
		RequestSceneXMl instance = RequestSceneXMl.getInstance();
		DLMessageModel dlMessageModel = instance.xmlParser();
		if (biz.contains("HTTPPage_")) {
			biz = "HTTPPage";
		} else if (biz.contains("CS")) {
			biz = "Call";
		} else if (biz.contains("CSFB")) {
			biz = "Call";
		}
		if (scene != null && scene.trim().length() != 0) {
			for (int i = 0; i < dlMessageModel.getSubDlMessageItems().size(); i++) {
				if (dlMessageModel.getSubDlMessageItems().get(i).getSceneName().equals(scene)) {
					List<SubDlMessageItem> subDlMessageItems = new ArrayList<>();
					subDlMessageItems.add(dlMessageModel.getSubDlMessageItems().get(i));
					dlMessageModel.setSubDlMessageItems(subDlMessageItems);
					break;
				}
			}
		}
		dlMessageModel.setBusiness(biz);
		instance.xmlFileCreator(dlMessageModel);
	}

	private boolean isFinish;

	/**
	 * 请求数据
	 * 
	 *            需要请求数据的格式json
	 */

	class LoadData implements Runnable {
		@Override
		public void run() {
			try {
				if (!isFinish) {
//				    Thread.sleep(5000);
					boolean isTotalTraceInitSuc = false;
					// isOne = true;
					mHandler.sendEmptyMessage(Msg_ReportHTML_ONE_LOAD);
					while (!isTotalTraceInitSuc) {
						Log.i(this.getClass().getName(), "TraceInitSucc--- " + appModel.isTraceInitSucc() + "---isTotalTraceInitSuc---" + isTotalTraceInitSuc);
						if (appModel.isTraceInitSucc()) {
							// if (!TotalInterface.isInit) {
							// Log.i(this.getClass().getName(), "isInit--- " +
							// TotalInterface.isInit);
							// totalInterface.initLib();
							// Log.i(this.getClass().getName(), "init finish");
							// }
							mHandler.post(runnable);
							int responseCode = totalInterface.excuteTotal();
							Log.i(this.getClass().getName(), "excuteTotal");
							isFinish = true;
							if (responseCode != 1) {
								mHandler.sendEmptyMessage(Msg_ReportHTML_TOTAL_FAIL);
							} else {
								mHandler.sendEmptyMessage(Msg_ReportHTML_TOTAL_SUCCESS);
							}
							isTotalTraceInitSuc = true;
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};

	/**
	 * 每500毫秒实时查询进度
	 */
	// private boolean isOne = true;

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			int progress = (int) (totalInterface.queryProgress() * 100);
			if (progress == 100 && isFinish == true) {
				mHandler.sendEmptyMessage(Msg_ReportHTML_Progress);
			}
			circleProgress.setProgress(progress);
			flashingView.setText(progress + "%");
			mHandler.postDelayed(this, 500); // 每500毫秒监听进度
		}
	};

	private String[] businessNames;
	private List<GoOrNogoParameter> goOrNogoModels = new ArrayList<GoOrNogoParameter>();

	/**
	 * 加载GoOrNogo XML配置转换为JSON
	 */
	private String loadGoOrNogoJson() {
		String jsonStr = "";
		List<Business> businessList = GoOrNogoSetting.getInstance(this).getBusinesses();
		businessNames = GoOrNogoSetting.getInstance(this).getBusinessNames();
		goOrNogoModels.clear();
		for (int i = 0; i < businessList.size(); i++) {
			for (int j = 0; j < businessList.get(i).getDefaultSettings().size(); j++) {
				GoOrNogoParameter goOrNogoModel = new GoOrNogoParameter();
				goOrNogoModel = businessList.get(i).getDefaultSettings().get(j);
				goOrNogoModel.setShowName(getName(!businessList.get(i).getName().equalsIgnoreCase("pbm") ? businessList.get(i).getName() + "_" + goOrNogoModel.getName() : goOrNogoModel.getAlias()));
				goOrNogoModel.setBusiness(businessList.get(i).getName());
				goOrNogoModels.add(goOrNogoModel);
			}

		}
		jsonStr = modelToJsonFile(goOrNogoModels);
		return jsonStr;
	}

	/**
	 * 依据HTML界面需要的JSON格式转换
	 */
	private String modelToJsonFile(List<GoOrNogoParameter> goOrNogoModels) {
		// 进行分组
		LinkedHashMap<String, List<GoOrNogoParameter>> map = GroupUtil.group(goOrNogoModels, new GroupBy<String>() {
			@Override
			public String groupby(Object obj) {
				GoOrNogoParameter d = (GoOrNogoParameter) obj;
				return d.getBusiness();
			}
		});

		// 生成Json串
		String subJsonArray = "";
		for (LinkedHashMap.Entry<String, List<GoOrNogoParameter>> entry : map.entrySet()) {
			String key = entry.getKey().toString();
			List<GoOrNogoParameter> value = entry.getValue();
			System.out.println("key=" + key + " value=" + value.size());
			try {
				subJsonArray += "\"" + key + "\"" + ":" + "[";
				subJsonArray += propertyStr(value);
				subJsonArray += "],";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "{" + (subJsonArray.length() != 0 ? subJsonArray.substring(0, subJsonArray.length() - 1) : "") + "}";

	}

	/**
	 * 属性字符串
	 * 
	 * @return
	 */
	private String propertyStr(List<GoOrNogoParameter> goOrNogoParameters) {
		String colorStr = "";
		for (int i = 0; i < goOrNogoParameters.size(); i++) {
			colorStr += "{" + "\"display\"" + ":" + "\"" + goOrNogoParameters.get(i).getShowName() + "\"" + "," + "\"paramter\"" + ":" + "\"" + goOrNogoParameters.get(i).getName() + (goOrNogoParameters.get(i).getAlias().length() != 0 ? "_" + goOrNogoParameters.get(i).getAlias() : "") + "\"" + "," + "\"condiction\"" + ":" + "\"" + goOrNogoParameters.get(i).getCondiction() + "\"" + "}" + (goOrNogoParameters.size() - 1 != i ? "," : "");
		}

		return colorStr;
	}

	/**
	 * 获取相关项的名称
	 * 
	 * @param key
	 * @return
	 */
	private String getName(String key) {
		String name = "";
		for (int i = 0; i < businessNames.length; i++) {
			String nameTmp = businessNames[i];
			if (nameTmp.contains(key)) {
				name = nameTmp.substring(nameTmp.indexOf(":") + 1);
				return name;
			}
		}
		return name;
	}

	/**
	 * 调用html页面中的方法
	 * 
	 * @param json
	 */
	public void Callfunction(String json) {
		if(Deviceinfo.getInstance().isVivoX23()){
			webView2.loadUrl("javascript: dlLoadData(" + json + "," + loadGoOrNogoJson() + ")");
		}else{
			webView1.loadUrl("javascript: dlLoadData(" + json + "," + loadGoOrNogoJson() + ")");
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
		if(Deviceinfo.getInstance().isVivoX23()) {
			if ((keyCode == KeyEvent.KEYCODE_BACK) && (webView2.getNavigationHistory().canGoBack())) {
				backKey();
				return true;
			}
		}else{
			if (keyCode == KeyEvent.KEYCODE_BACK && webView1.canGoBack()) {
				backKey();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {
		super.finish();
		if (!appModel.isGeneralMode())
			DataCenter.StopStatistic();
	}

	/**
	 * 返回按钮操作
	 */
	public void backKey() {
		if(Deviceinfo.getInstance().isVivoX23()){
			if (webView2.getNavigationHistory().canGoBack() && (!webView2.getUrl().endsWith("DTListView.html") && !webView2.getUrl().endsWith("CQTListView.html"))) {
				webView2.getNavigationHistory().navigate(XWalkNavigationHistory.Direction.BACKWARD, 1);// 返回前一个页面
				if (index != 0) {
					index = 0;
				}
			} else {
				finish();
			}
		}else {
			if (webView1.canGoBack() && (!webView1.getUrl().endsWith("DTListView.html") && !webView1.getUrl().endsWith("CQTListView.html"))) {
				webView1.goBack();// 返回前一个页面
				if (index != 0) {
					index = 0;
				}
			} else {
				finish();
			}
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUESTCODE:
				addDateIntoWebview(data);
				break;
			default:
				break;
			}

		}
	}
	void addDateIntoWebview(Intent data){
		if (data.getBooleanExtra(FileManagerFragmentActivity.KEY_TOTAL_RUN, false)) {
			// 重置状态,保存数据等操作
			SharePreferencesUtil.getInstance(mContext).saveInteger(FileManagerFragmentActivity.IS_TOTAL, data.getIntExtra(FileManagerFragmentActivity.IS_TOTAL, -1));
			List<String> myFiles = RequestSceneXMl.getInstance().getFilePathList();
			String ddibStrs = "";
			for (int i = 0; i < myFiles.size(); i++) {
				ddibStrs += myFiles.get(i) + "*";
			}
			SharePreferencesUtil.getInstance(mContext).saveString(FileManagerFragmentActivity.TOTAL_DDIB_PATH, ddibStrs);
			if(Deviceinfo.getInstance().isVivoX23()){
				webView2.getNavigationHistory().clear();
				webView2.clearCache(true);
			}else{
				webView1.clearHistory();
				webView1.clearView();
			}
			isFinish = false;
			index = 0;
			new Thread(new LoadData()).start();
		}
	}
}
