package com.walktour.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.service.app.Killer;

public class TraceService extends Service {
	private static final String tag = "TraceService";
	private Intent traceInfo;

	/** 设置指定信令的过滤频率，置认为1秒钟 */
	// private int filterFrequency = 1000;
	/** 过滤频率设置表，对需要过滤的信令进行配置,在服务onCreate的时候加载过滤ID */
	// private HashMap<Integer, Long> filterSetting = null;

	private final static int TIMER_TASK = 101;

	private boolean isStart=false;
	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case TIMER_TASK:
				applicationTimeChange();
				break;

			default:
				break;
			}

			return true;
		}
	});

	/**
	 * 开始时间计时器<BR>
	 * [功能详细描述]
	 */
	private void applicationTimeChange() {
		mHandler.sendEmptyMessageDelayed(TIMER_TASK, 1000);
		RefreshEventManager.notifyRefreshEvent(RefreshType.ACTION_WALKTOUR_TIMER_CHANGED, null);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.w(tag, "--onStartCommand--" + (intent == null));
		if (intent == null) {
			startService(new Intent(getApplicationContext(), Killer.class));
		}

		LogUtil.w(tag, "--OpenTraceDev--");
		if(!isStart) {
				isStart=true;
				traceInfo = new Intent(getApplicationContext(), DatasetRecordService.class);
				startService(traceInfo);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.w(tag, "trace onCreate");

		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.mapParaChanged);
		filter.addAction(WalkMessage.chartLineChanged);
		this.registerReceiver(paraChangedReceiver, filter);
		TraceInfoInterface.traceData.initChartLine(ParameterSetting.getInstance().getChartLineParameterNames());

		/** 串口服务启动后，打开计时器消息，每秒钟发送该消息一次，Walktour过程中所有用到计时器的都接受该消息 */
		applicationTimeChange();


	}

	@Override
	public void onDestroy() {
		LogUtil.w(tag, "trace onDestroy");
		super.onDestroy();
		this.unregisterReceiver(paraChangedReceiver);

		LogUtil.w(tag, "---stop traceinfo service");
		if(traceInfo == null){
			traceInfo = new Intent(getApplicationContext(), DatasetRecordService.class);
		}
		stopService(traceInfo);
		traceInfo=null;
	}

	private final BroadcastReceiver paraChangedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(WalkMessage.mapParaChanged)) {
				TraceInfoInterface.traceData.paraChnaged();
			} else if (intent.getAction().equals(WalkMessage.chartLineChanged)) {
				TraceInfoInterface.traceData.chartLineChanged(ParameterSetting.getInstance().getChartLineParameterNames());
			} 
		}
	};
}
