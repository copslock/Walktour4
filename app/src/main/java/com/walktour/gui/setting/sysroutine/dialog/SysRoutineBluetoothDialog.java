package com.walktour.gui.setting.sysroutine.dialog;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.service.BlueToothControlService;

import java.lang.ref.WeakReference;
import java.util.Set;

/**
 * 蓝牙同步设置对话框
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("InflateParams")
public class SysRoutineBluetoothDialog extends BasicDialog implements OnClickListener, OnCheckedChangeListener {
	/** 日志标识 */
	private static final String TAG = "SysRoutineBluetoothDialog";
	/** 蓝牙信息标识 */
	private static final int HAND_SETBLUETOOTH = 2;
	/** 建设类 */
	private Builder builder;
	/** 上下文 */
	private Context mContext;
	/** 配置管理类 */
	private ConfigRoutine configRoutine;
	/** 是否开启蓝牙同步测试 */
	private CheckBox bluetoothSync;
	/** 创建服务端 */
	private CheckBox bluetoothcreServer;
	/** 创建客户端 */
	private CheckBox bluetoothcreClient;
	/** 配对终端显示 */
	private RelativeLayout bluetoothpairDeviceLayout;
	/** 连接状态显示 */
	private RelativeLayout bluetoothStateLayout;
	/** 创建服务端显示 */
	private RelativeLayout bluetoothCreServerLayout;
	/** 创建客户端显示 */
	private RelativeLayout bluetoothCreClientLayout;
	/** 配对终端显示 */
	private TextView bluetoothPairState;
	/** 连接状态 */
	private TextView bluetoothConState;
	/** 配对列表 */
	private BluetoothAdapter bluetoothAda = null;
	/** 绑定设备 */
	private BluetoothDevice bandDevice = null;

	public SysRoutineBluetoothDialog(Context context, Builder builder) {
		super(context);
		LogUtil.w(TAG, "--SysRoutineBluetoothDialog--");
		this.mContext = context;
		this.builder = builder;
		this.configRoutine = ConfigRoutine.getInstance();
		this.init();

		registerReceiverFilter();
	}

	private void registerReceiverFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.ACTION_BLUETOOTH_SOCKET_CHANGE);
		getContext().registerReceiver(mEventReceiver, filter);
	}

	/**
	 * 广播接收器:接收所有操作结果以更新进度框
	 */
	private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(WalkMessage.ACTION_BLUETOOTH_SOCKET_CHANGE)) {
				if (bluetoothcreClient.isChecked()) {
					bluetoothcreClient.setEnabled(true);
				}
				if (ApplicationModel.getInstance().isBluetoothConnected()) {
					bluetoothConState.setText(getContext().getString(R.string.sys_setting_bluetooth_connected));
				} else {
					bluetoothConState.setText(getContext().getString(R.string.sys_setting_bluetooth_connectting));
				}
			}
		}
	};

	/**
	 * 初始化
	 */
	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.sys_routine_setting_bluetooth, null);
		builder.setTitle(R.string.sys_setting_bluetooth_sync);
		builder.setView(layout);
		bluetoothSync = (CheckBox) layout.findViewById(R.id.bluetooth_synctest);
		bluetoothcreServer = (CheckBox) layout.findViewById(R.id.bluetooth_sync_creserver);
		bluetoothcreClient = (CheckBox) layout.findViewById(R.id.bluetooth_sync_creclient);
		bluetoothpairDeviceLayout = (RelativeLayout) layout.findViewById(R.id.setting_bluetooth_sync_pairdevice);
		bluetoothStateLayout = (RelativeLayout) layout.findViewById(R.id.setting_bluetooth_sync_cennectstate);
		bluetoothCreServerLayout = (RelativeLayout) layout.findViewById(R.id.setting_bluetooth_sync_creserver);
		bluetoothCreClientLayout = (RelativeLayout) layout.findViewById(R.id.setting_bluetooth_sync_creclient);
		bluetoothPairState = (TextView) layout.findViewById(R.id.bluetooth_sync_pairstate);
		bluetoothConState = (TextView) layout.findViewById(R.id.bluetooth_sync_curstate);

		bluetoothSync.setChecked(configRoutine.isBluetoothSync(mContext));
		bluetoothcreServer.setChecked(configRoutine.isBluetoothServerOpen());
		bluetoothcreClient.setChecked(configRoutine.isBluetoothClientOpen());

		bluetoothSync.setOnCheckedChangeListener(this);

		bluetoothpairDeviceLayout.setOnClickListener(this);
		bluetoothcreServer.setOnCheckedChangeListener(this);
		bluetoothcreClient.setOnCheckedChangeListener(this);
		bluetoothConState.setText(ApplicationModel.getInstance().isBluetoothConnected()
				? mContext.getString(R.string.sys_setting_bluetooth_connected)
				: mContext.getString(R.string.sys_setting_bluetooth_connectting));
		openBluetooth(0);
	}

	/**
	 * 如果当前蓝牙同步开关为开着的状态,处理当前配对蓝牙及创建服务(客户)端信息
	 * 
	 * @param source
	 *          检查开关开源,如果当前为开关点击(值为1),且未有配对信息,需要启个配对信息刷新线程
	 * @return 配对蓝牙个数
	 */
	private int openBluetooth(int source) {
		LogUtil.w(TAG, "--openBluetooth--" + bluetoothSync.isChecked());
		if (bluetoothSync.isChecked()) {
			if (bluetoothAda == null) {
				bluetoothAda = BluetoothAdapter.getDefaultAdapter();
			}
			if (!bluetoothAda.isEnabled()) {
				bluetoothAda.enable();
				new DelaySetBluetoothState().start();
			}
			bluetoothpairDeviceLayout.setVisibility(View.VISIBLE);
			Set<BluetoothDevice> band = bluetoothAda.getBondedDevices();
			if (band.size() == 1) {
				bluetoothCreServerLayout.setVisibility(View.VISIBLE);
				bluetoothCreClientLayout.setVisibility(View.VISIBLE);
				bluetoothStateLayout.setVisibility(View.VISIBLE);

				bandDevice = band.iterator().next();
				bluetoothPairState.setText(this.mContext.getString(R.string.sys_setting_bluetooth_pair_device)
						+ bandDevice.getName() + "(MAC:" + bandDevice.getAddress() + ")");
				if (configRoutine.isBluetoothServerOpen()) {
					bluetoothcreServer.setEnabled(true);
					bluetoothcreClient.setEnabled(false);
				} else if (configRoutine.isBluetoothClientOpen()) {
					bluetoothcreServer.setEnabled(false);
					bluetoothcreClient.setEnabled(true);
				} else {
					bluetoothcreServer.setEnabled(true);
					bluetoothcreClient.setEnabled(true);
				}
			} else {
				if (band.size() < 1) {
					bluetoothPairState.setText(mContext.getString(R.string.sys_setting_bluetooth_none_pair));
				} else if (band.size() > 1) {
					bluetoothPairState.setText(mContext.getString(R.string.sys_setting_bluetooth_more_pair));
				}
				bluetoothCreServerLayout.setVisibility(View.GONE);
				bluetoothCreClientLayout.setVisibility(View.GONE);
				bluetoothStateLayout.setVisibility(View.GONE);
				/*
				 * if(source == 1){ new pairDeviceChange().start(); }
				 */
			}

			return band.size();
		}
		if (bluetoothAda != null && bluetoothAda.isEnabled()) {
			bluetoothAda.disable();
		}
		bluetoothAda = null;

		bluetoothcreServer.setChecked(false);
		bluetoothcreClient.setChecked(false);
		bluetoothStateLayout.setVisibility(View.GONE);
		bluetoothCreServerLayout.setVisibility(View.GONE);
		bluetoothCreClientLayout.setVisibility(View.GONE);
		bluetoothpairDeviceLayout.setVisibility(View.GONE);
		return -1;
	}

	/**
	 * 句柄
	 */
	private Handler myHandler = new MyHandler(this);

	/**
	 * 自定义消息处理
	 * 
	 * @author jianchao.wang
	 *
	 */
	private static class MyHandler extends Handler {
		private WeakReference<SysRoutineBluetoothDialog> reference;

		public MyHandler(SysRoutineBluetoothDialog dialog) {
			this.reference = new WeakReference<SysRoutineBluetoothDialog>(dialog);
		}

		@Override
		public void handleMessage(android.os.Message msg) {
			SysRoutineBluetoothDialog dialog = this.reference.get();
			switch (msg.what) {
			case HAND_SETBLUETOOTH:
				dialog.openBluetooth(2);
				break;
			}
		}

	}

	/**
	 * 延时设置蓝牙状态
	 * 
	 * @author jianchao.wang
	 * 
	 */
	private class DelaySetBluetoothState extends Thread {
		public void run() {
			try {
				Thread.sleep(1000);
				myHandler.obtainMessage(HAND_SETBLUETOOTH).sendToTarget();
			} catch (Exception e) {
				LogUtil.w(TAG, "", e);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_bluetooth_sync_pairdevice:
			mContext.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.bluetooth_synctest:
			openBluetooth(1);
			configRoutine.setBluetoothSync(mContext, isChecked);
			break;
		case R.id.bluetooth_sync_creserver:
			configRoutine.setBluetoothServerOpen(isChecked);
			Intent service = new Intent(mContext, BlueToothControlService.class);
			if (isChecked) {
				bluetoothcreClient.setEnabled(false);
				// bluetoothStateLayout.setVisibility(View.VISIBLE);
				service.putExtra(BlueToothControlService.BlueToothStartType, BlueToothControlService.BlueToothStart_Server);
				mContext.startService(service);
			} else {
				bluetoothcreClient.setEnabled(true);
				// bluetoothStateLayout.setVisibility(View.GONE);
				mContext.stopService(service);
			}
			break;
		case R.id.bluetooth_sync_creclient:
			configRoutine.setBluetoothClientOpen(isChecked);
			service = new Intent(mContext, BlueToothControlService.class);
			if (isChecked) {
				bluetoothcreServer.setEnabled(false);
				bluetoothcreClient.setEnabled(false);
				// bluetoothStateLayout.setVisibility(View.VISIBLE);

				service.putExtra(BlueToothControlService.BlueToothStartType, BlueToothControlService.BlueToothStart_Client);
				mContext.startService(service);
			} else {
				bluetoothcreServer.setEnabled(true);
				// bluetoothStateLayout.setVisibility(View.GONE);

				mContext.stopService(service);
			}
			break;
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		LogUtil.w(TAG, "--onDetachedFromWindow--");
		getContext().unregisterReceiver(mEventReceiver);
	}
}
