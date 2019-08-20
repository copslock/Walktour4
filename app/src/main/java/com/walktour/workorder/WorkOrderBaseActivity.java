package com.walktour.workorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.dinglicom.UnicomInterface;
import com.walktour.gui.R;
import com.walktour.gui.task.TestDialog;
import com.walktour.workorder.bll.InitServer;
import com.walktour.workorder.bll.InitServer.OnInitServerListener;

/**
 * 工单的Activity的父类，定义工单初始化操作、工单初始化超时处理、显示和隐藏进度对话框
 * Author: ZhengLei
 *   Date: 2013-6-23 下午12:56:23
 */
public abstract class WorkOrderBaseActivity extends WorkOrderBasic implements OnInitServerListener {
	public static final int UNICOM_SERVER_SETTING_EMPTY = 1000;
	public static final int NETWORK_NOT_CONNECTED = 1001;
	public static final int INIT_SERVER_FINISH = 1002;
	public static final int INIT_TIMEOUT = 1003;
	public static final int DEFAULT_INIT_SERVER_TIMEOUT = 60; // 默认初始化服务器的超时时间为60秒
	
	private ProgressDialog progressDialog;
	private InitServer mInitServer = null;
	protected boolean isInitFinished = false; // 是否初始化结束，子类中会赋值

	/**
	 * 初始化服务器的线程
	 */
	protected void initServer() {
		showProgressDialog(getString(R.string.connecting_server), false);
		
		mInitServer = new InitServer(this);
		mInitServer.addListener(this);
		new Thread(mInitServer).start(); // 相关的回调，在子类（如工单字典、工单列表）中处理
		new Thread(new MonitorInitServerTimeout(DEFAULT_INIT_SERVER_TIMEOUT)).start(); // 启动监视线程
	}
	
	/**
	 * 初始化服务器结束后的处理，在子类中实现
	 */
	protected abstract void handleInitOk();
	
	// 回调函数
	@Override
	public void onValid(boolean isValid) {
		if(!isValid) {
			Message msg = mHandler.obtainMessage(UNICOM_SERVER_SETTING_EMPTY);
			msg.sendToTarget();
		}
	}

	@Override
	public void onCheckNetwork(boolean isConnectNetwork) {
		if(!isConnectNetwork) {
			Message msg = mHandler.obtainMessage(NETWORK_NOT_CONNECTED);
			msg.sendToTarget();
		}
	}
	
	@Override
	public void onInitFinish(boolean isInit) {
//		this.isInit = isInit; // 这里的回调的isInit不要用了，因为可以用hasInit的方法，保持一个口来获取是否初始化
		isInitFinished = true;
		Message msg = mHandler.obtainMessage(INIT_SERVER_FINISH);
		msg.sendToTarget();
	}

	/**
	 * 监视初始化服务器超时的线程
	 * @author ZhengLei
	 *
	 */
	private class MonitorInitServerTimeout implements Runnable {
		private int timeout;

		public MonitorInitServerTimeout(int timeout) {
			this.timeout = timeout;
		}

		@Override
		public void run() {
			int counter = timeout * 1000 / 50;
			for(int i=1; i<=counter&&!isInitFinished; i++) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// 如果超时了还没执行完，则断开连接
			if(!isInitFinished) {
				UnicomInterface.disconnect();
				Message msg = mHandler.obtainMessage(INIT_TIMEOUT);
				msg.sendToTarget();
			}
			isInitFinished = false;
		}
		
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 先dismiss掉进度对话框
			dismissProgressDialog();
			switch (msg.what) {
				case UNICOM_SERVER_SETTING_EMPTY:
					Intent intent = new Intent(WorkOrderBaseActivity.this, TestDialog.class);
					intent.putExtra(TestDialog.EXTRA_FROM, TestDialog.UNICOM_SERVER_EMPTY);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					break;
				case NETWORK_NOT_CONNECTED:
					Toast.makeText(WorkOrderBaseActivity.this, R.string.network_not_connected, Toast.LENGTH_LONG).show();
					break;
				case INIT_SERVER_FINISH:
					handleInitOk();
					break;
				case INIT_TIMEOUT:
					Toast.makeText(WorkOrderBaseActivity.this, R.string.init_server_timeout, Toast.LENGTH_LONG).show();
					break;
				default:
					break;
			}
		}
		
	};

	protected void showProgressDialog(String message, boolean cancleable) {
		progressDialog = new ProgressDialog(WorkOrderBaseActivity.this);
		progressDialog.setMessage(message);
		// progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(cancleable);
		progressDialog.show();
	}
	
	protected void dismissProgressDialog() {
		if(progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
}
