package com.walktour.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct.ServerOperateType;
import com.walktour.base.util.LogUtil;
import com.walktour.service.app.DataTransService;
import com.walktour.service.paramsreport.ParamsReportFactory;

/***
 * 平台交互监控--》平台监控
 * 
 * @author weirong.fan
 *
 */
public class PlatformControlService extends Service {
	/** 日志标识 */
	private static final String TAG = "PlatformControlService";
	/** 上下文 **/
	private Context mContext;
	// 上报参数间隔时长
	private static final int REPORT_INTERVAL = 5 * 1000;
	private Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
				LogUtil.d(TAG, "-----------Upload Parameters---------------");
				reportParams();
				if (null != handler)
					handler.postDelayed(this, REPORT_INTERVAL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onCreate() {
		LogUtil.d(TAG, "-----------onCreate---------------");
		super.onCreate();
		mContext = this.getApplicationContext();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d(TAG, "-----------onStartCommand---------------");
		if (null != handler)
			handler.postDelayed(runnable, REPORT_INTERVAL);
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 上报GPS和实时参数给平台
	 */
	private void reportParams() {
		String reportJson = ParamsReportFactory.get(this).getReportJson();
		LogUtil.d(TAG, "reportJson=:" + reportJson);
		if (!StringUtil.isNullOrEmpty(reportJson)) {
			LogUtil.d(TAG, "-----reportParams json:" + reportJson + "-----");
			Intent service = new Intent(mContext, DataTransService.class);
			service.putExtra(DataTransService.EXTRA_KEY_OPERATE_TYPE_NAME, ServerOperateType.uploadParamsReport);
			service.putExtra(DataTransService.EXTRA_KEY_UPLOAD_MESSAGE, reportJson);
			mContext.startService(service);
		}
	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "-----onDestroy-----");
		super.onDestroy();
		if (null != handler)
			if (null != runnable)
				handler.removeCallbacks(runnable);
	}

}
