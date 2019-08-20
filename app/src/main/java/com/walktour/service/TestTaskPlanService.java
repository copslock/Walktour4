package com.walktour.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ServerManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/***
 * 平台交互监控--》测试任务计划扫描
 * 
 * @author weirong.fan
 *
 */
public class TestTaskPlanService extends Service {
	private static final String TAG = "TestTaskPlanService";
	/** 上下文 **/
	private Context context;
	/** 服务器管理类 */
	private ServerManager mServer;
	/** 设置的服务器的IP **/
	private String serverIp;
	/** 设置的服务器的端口 **/
	private int serverPort;
	/** 参数存储 */
	private SharedPreferences preferences;
	// 休眠15秒
	private int sleepTime = 15 * 1000;
	private Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
				LogUtil.d(TAG, "-----------DownLoad TestPlan---------------");
				String guid = "{" + MyPhoneState.getInstance().getGUID(context) + "}";
				new DownloadTestPlan(guid).execute();
				if(null!=handler)
					handler.postDelayed(this, sleepTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		context = this.getApplicationContext();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if(null!=handler)
			handler.postDelayed(runnable, sleepTime);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (null != handler)
			if (null != runnable)
				handler.removeCallbacks(runnable);
	}

	private class DownloadTestPlan extends AsyncTask<Void, Void, Integer> {
		private String guid;

		public DownloadTestPlan(String guid) {
			super();
			this.guid = guid;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			String msg = getReportRequest(guid);
			// System.out.println(TAG + "获取测试计划更新时间:" + msg);
			if (null == msg || msg.trim().equals("")) {
				// System.out.println(TAG + "无测试计划可更新");
				return null;
			}
			String value = preferences.getString(TAG + "-" + guid, null);
			if (null == value || !msg.equals(value)) {
				// System.out.println(TAG + "获取任务并刷新任务.");
				ServerManager sm = ServerManager.getInstance(context);
				if (sm.hasDownloadServerSet()) {
					sm.downloadTestTask(true);
				}
			} else {
				// System.out.println(TAG + "已经更新过,无新测试计划可更新.");
			}

			preferences.edit().putString(TAG + "-" + guid, msg).commit();
			return null;
		}

		/***
		 * 获取报表的数据
		 * 
		 * @param siteInfoModel
		 * @return
		 */
		private String getReportRequest(String guid) {
			try {
				mServer = ServerManager.getInstance(context);
				serverIp = mServer.getDownloadFleetIp();
				serverPort = mServer.getDownloadFleetPort();
				StringBuilder http = new StringBuilder();
				http.append("http://").append(serverIp).append(":").append(serverPort);
				http.append("/services/TestPlanService.svc/GetTestPlanUpdateDT");
				// guid="{01346900-1367-2850-0000-000000000000}";
				http.append("?Guid=").append(URLEncoder.encode(guid, "UTF-8"));
				http.append("&DeviceType=2");
				// System.out.println(TAG + http.toString() + "");
				URL url = new URL(http.toString());// 构造一个url对象
				HttpURLConnection urlConnection;
				urlConnection = (HttpURLConnection) url.openConnection();
				InputStream stream = urlConnection.getInputStream();
				InputStreamReader in = new InputStreamReader(stream);
				BufferedReader buffere = new BufferedReader(in);
				String resultData = "";
				String line = null;
				while ((line = buffere.readLine()) != null) {
					resultData += line + "\n";
				}
				in.close();
				urlConnection.disconnect();
				return resultData;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
