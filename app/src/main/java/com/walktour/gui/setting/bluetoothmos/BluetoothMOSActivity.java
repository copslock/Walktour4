package com.walktour.gui.setting.bluetoothmos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.APNOperate;
import com.walktour.Utils.AlertWakeLock;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.setting.bluetoothmos.fragment.BluetoothSummaryFragment;
import com.walktour.gui.setting.bluetoothmos.fragment.ProgressFragment;
import com.walktour.service.bluetoothmos.BluetoothMOSFactory;
import com.walktour.service.bluetoothmos.BluetoothMOSService;
import com.walktour.service.bluetoothmos.IBluetoothMOSServiceBinder;
import com.walktour.service.bluetoothmos.IBluetoothMOSServiceCallback;
import com.walktour.service.bluetoothmos.command.BaseCommand.FileType;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

import java.lang.ref.WeakReference;
import java.sql.Date;
import java.util.List;

/**
 * 蓝牙MOS头设置界面
 * 
 * @author jianchao.wang
 *
 */
@SuppressLint("SdCardPath")
public class BluetoothMOSActivity extends FragmentActivity implements OnClickListener {
	/** 日志标识 */
	private final static String TAG = "BluetoothMOSActivity";
	/** 对话框类型：显示进度条 */
	private static final String DIALOG_SHOW_PROGRESS = "show_progress";
	/** 显示当前的设备信息 */
	private static final int SHOW_DEVICE_INFO = 99;
	/** 显示算分结果 */
	private static final int SHOW_CALCULATE_RESULT = 108;

	/**
	 * 弹出扫描到的设备列表
	 */
	private static final int SHOW_DISCOVERED_DEVICES = 110;

	/** 更换文件选择的文件类型 */
	private String mFileType;
	/** 蓝牙MOS头服务类 */
	private IBluetoothMOSServiceBinder mServiceBinder;
	/** 是否绑定了蓝牙MOS头服务 */
	private boolean isBindMOSService = false;
	/** 消息处理句柄 */
	private MyHandler mHandler = new MyHandler(this);
	/** 文件类型名称数组 */
	private String[] mFileTypes;
	/** 进度条对话框 */
	private ProgressFragment mProgressDialog = null;
	/** 连接设备选择按钮 */
	private CheckBox mConnectDevice;
	/** 蓝牙MOS头工厂类 */
	private BluetoothMOSFactory mFactory;
    /** 权限请求应答 */
	private final int MY_PERMISSION_REQUEST_CONSTANT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_bluetoothmos_setting);
		this.mFactory = BluetoothMOSFactory.get();
		this.findView();
		this.bindMOSService();
		this.showDeviceInfo();
		this.showRingTestResult();
		this.syncNetWorkTime();
		if (Build.VERSION.SDK_INT >= 6.0) {
			this.mConnectDevice.setEnabled(false);
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_CONSTANT);
		}else{
			this.mConnectDevice.setEnabled(true);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

		switch (requestCode) {
			case MY_PERMISSION_REQUEST_CONSTANT: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					LogUtil.d(TAG, "----ACCESS_FINE_LOCATION----Permission----Granted----");
					this.mConnectDevice.setEnabled(true);
				}
				return;
			}
		}
	}

	/**
	 * 绑定蓝牙MOS头服务
	 */
	private void bindMOSService() {
		LogUtil.d(TAG, "---------bindMOSService----------------");
		Intent intent = new Intent(this, BluetoothMOSService.class);
		intent.putExtra(BluetoothMOSService.EXTRA_KEY_CALCULATE_PESQ, true);
		if (this.mFactory.getCurrMOCDevice() != null) {
			intent.putExtra(BluetoothMOSService.EXTRA_KEY_BLUETOOTH_MOS, this.mFactory.getCurrMOCDevice());
			intent.setExtrasClassLoader(BluetoothMOSDevice.class.getClassLoader());
		}
		this.bindService(intent, conn, BIND_AUTO_CREATE);
	}

	/**
	 * 视图匹配
	 */
	private void findView() {
		TextView title = (TextView) this.findViewById(R.id.title_txt);
		title.setText(R.string.task_callMOSBluetooth);
		ImageButton pointer = (ImageButton) this.findViewById(R.id.pointer);
		pointer.setOnClickListener(this);
		Button changeFile = (Button) this.findViewById(R.id.btn_change_file);
		changeFile.setOnClickListener(this);
		Button ringTest = (Button) this.findViewById(R.id.btn_ring_test);
		ringTest.setOnClickListener(this);
		Button setting = (Button) this.findViewById(R.id.btn_setting);
		setting.setOnClickListener(this);
		mConnectDevice = (CheckBox) this.findViewById(R.id.bluetooth_mos_check);
		if (BluetoothMOSFactory.get().getCurrMOCDevice() != null)
			mConnectDevice.setChecked(true);
		mConnectDevice.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				connectDevice(isChecked);
			}
		});

	}

	/**
	 * 创建进度条对话框
	 * 
	 * @param message
	 *          进度条内容
	 */
	private void showProgressDialog(String message) {
		if (this.mProgressDialog != null)
			return;
		FragmentManager fm = this.getSupportFragmentManager();
		this.mProgressDialog = new ProgressFragment();
		Bundle bundle = new Bundle();
		bundle.putString(ProgressFragment.PROGRESS_MESSAGE, message);
		this.mProgressDialog.setArguments(bundle);
		this.mProgressDialog.show(fm, DIALOG_SHOW_PROGRESS);
		this.mProgressDialog.setCancelable(false);
	}

	/**
	 * 中断进度条显示
	 */
	private void dismissProgress() {
		if (this.mProgressDialog != null && !this.mProgressDialog.isHidden()) {
			this.mProgressDialog.dismiss();
			this.mProgressDialog = null;
		}
	}

	/**
	 * 连接设备
	 * 
	 * @param isOn
	 *          是否连接
	 */
	private void connectDevice(boolean isOn) {
		if (mServiceBinder == null) {
			return;
		}
		if (isOn) {
			AlertWakeLock.acquire(this);
			boolean flag = false;
			try {
				flag = mServiceBinder.findCorrectDevice(true);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			if (!flag) {
				Toast.makeText(BluetoothMOSActivity.this, R.string.task_callMOS_device_hadset_off, Toast.LENGTH_SHORT).show();
				this.mConnectDevice.setChecked(flag);
				AlertWakeLock.release();
			}
		} else {
			try {
				this.mServiceBinder.disconnect();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			this.mFactory.setCurrMOCDevice(null);
			this.showDeviceInfo();
			this.showRingTestResult();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			this.finish();
			break;
		case R.id.btn_change_file:
			this.showChangeFileDialog();
			break;
		case R.id.btn_ring_test:
			this.ringTest();
			break;
		case R.id.btn_setting:
			this.showMOSParamsSetDialog();
			break;
		default:
			break;
		}
	}

	/**
	 * 设置MOS头参数
	 */
	@SuppressLint("InflateParams")
	private void showMOSParamsSetDialog() {
		if (mServiceBinder == null || this.mFactory.getCurrMOCDevice() == null) {
			Toast.makeText(BluetoothMOSActivity.this, R.string.task_callMOS_device_no_connected, Toast.LENGTH_SHORT).show();
			return;
		}
		BasicDialog.Builder builder = new BasicDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_bluetoothmos_setting, null);
		builder.setTitle(R.string.task_callMOS_setting);
		builder.setView(view);
		BluetoothMOSDevice device = BluetoothMOSFactory.get().getCurrMOCDevice();
		final EditText playbackVolumeEdit = (EditText) view.findViewById(R.id.edit_playback_volume);
		playbackVolumeEdit.setText(String.valueOf(device.getPlaybackVolume()));
		final EditText recordVolumeEdit = (EditText) view.findViewById(R.id.edit_record_volume);
		recordVolumeEdit.setText(String.valueOf(device.getRecordVolume()));
		builder.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int playbackVolume = Integer.parseInt(playbackVolumeEdit.getText().toString());
				if (playbackVolume > 255)
					playbackVolume = 255;
				else if (playbackVolume < 0)
					playbackVolume = 0;
				int recordVolume = Integer.parseInt(recordVolumeEdit.getText().toString());
				if (recordVolume > 7)
					recordVolume = 7;
				else if (recordVolume < 0)
					recordVolume = 0;
				BluetoothMOSDevice device = BluetoothMOSFactory.get().getCurrMOCDevice();
				device.setPlaybackVolume(playbackVolume);
				device.setRecordVolume(recordVolume);
				setParamsToMOS();
			}
		}).setNegativeButton(R.string.str_cancle).show();
	}

	/**
	 * 设置蓝牙MOS头的参数
	 */
	private void setParamsToMOS() {
		BluetoothMOSDevice device = mFactory.getCurrMOCDevice();
		try {
			this.mServiceBinder.setDeviceParams(device.getPlaybackVolume(), device.getRecordVolume());
			this.showDeviceInfo();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 回环检测
	 */
	private void ringTest() {
		if (mServiceBinder == null || this.mFactory.getCurrMOCDevice() == null) {
			Toast.makeText(BluetoothMOSActivity.this, R.string.task_callMOS_device_no_connected, Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			this.mServiceBinder.testPlaybackAndRecord();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示更换音频文件对话框
	 */
	private void showChangeFileDialog() {
		if (mServiceBinder == null || this.mFactory.getCurrMOCDevice() == null) {
			Toast.makeText(BluetoothMOSActivity.this, R.string.task_callMOS_device_no_connected, Toast.LENGTH_SHORT).show();
			return;
		}
		BasicDialog.Builder builder = new BasicDialog.Builder(this);
		FileType[] fileTypes = FileType.values();
		this.mFileTypes = new String[fileTypes.length - 1];
		int pos = 0;
		for (int i = 0; i < fileTypes.length; i++) {
			if (fileTypes[i] == FileType.check_8k)
				continue;
			this.mFileTypes[pos++] = fileTypes[i].getName();
		}
		this.mFileType = this.mFileTypes[0];
		builder.setTitle(R.string.task_callMOS_choose_file_type)
				.setSingleChoiceItems(this.mFileTypes, 0, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mFileType = mFileTypes[which];
						writeFileToMOS();
					}
				}).setPositiveButton(R.string.task_callMOS_clear_files, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						clearAudioFiles();
					}
				}, false).setNegativeButton(R.string.str_cancle);
		builder.show();
	}

	/**
	 * 清理蓝牙MOS头里的所有样本文件
	 */
	private void clearAudioFiles() {
		if (mServiceBinder == null || this.mFactory.getCurrMOCDevice() == null) {
			Toast.makeText(BluetoothMOSActivity.this, R.string.task_callMOS_device_no_connected, Toast.LENGTH_SHORT).show();
			return;
		}
		this.showProgressDialog(this.getString(R.string.task_callMOS_clear_files_doing));
		try {
			this.mServiceBinder.clearAudioFiles();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示连接的设备信息
	 */
	private void showDeviceInfo() {
		FragmentManager fm = this.getSupportFragmentManager();
		BluetoothSummaryFragment fragment = (BluetoothSummaryFragment) fm
				.findFragmentById(R.id.activity_bluetoothmos_summary);
		if (fragment != null) {
			BluetoothMOSDevice device = this.mFactory.getCurrMOCDevice();
			fragment.setDeviceInfo(device);
			fragment.setConnectState(
					device == null ? BluetoothSummaryFragment.CONNECT_STATE_NO : BluetoothSummaryFragment.CONNECT_STATE_YES);
		}
	}

	/**
	 * 显示回环检测分值
	 */
	private void showRingTestResult() {
		FragmentManager fm = this.getSupportFragmentManager();
		BluetoothSummaryFragment fragment = (BluetoothSummaryFragment) fm
				.findFragmentById(R.id.activity_bluetoothmos_summary);
		if (fragment != null) {
			BluetoothMOSDevice device = this.mFactory.getCurrMOCDevice();
			if (device != null) {
				fragment.setRingResult(device.getPlaybackResult(), device.getRecordResult());
			}
		}
	}

	/**
	 * 消息处理句柄
	 * 
	 * @author jianchao.wang
	 *
	 */
	private static class MyHandler extends Handler {
		private WeakReference<BluetoothMOSActivity> reference;

		public MyHandler(BluetoothMOSActivity activity) {
			reference = new WeakReference<BluetoothMOSActivity>(activity);
		}

		@Override
		public void handleMessage(android.os.Message msg) {
			BluetoothMOSActivity activity = reference.get();
			BluetoothMOSDevice device = activity.mFactory.getCurrMOCDevice();
//			String msg1 = (String) msg.obj;
			switch (msg.what) {
			case BluetoothMOSService.EXTRA_SHOW_PROGRESS:
				activity.showProgressDialog((String) msg.obj);
				break;
			case BluetoothMOSService.EXTRA_SHOW_PROGRESS_DISMISS:
				AlertWakeLock.release();
				activity.dismissProgress();
				break;
			case BluetoothMOSService.EXTRA_CONNECT_INTERRUPT:
				AlertWakeLock.release();
				activity.connectInterrupt();
				break;
			case BluetoothMOSService.EXTRA_FIND_DEVICE_FAIL:
				AlertWakeLock.release();
				activity.findDeviceFail();
				break;
			case BluetoothMOSService.EXTRA_SHOW_RING_RESULT:
				AlertWakeLock.release();
				if (device != null && !StringUtil.isNullOrEmpty((String) msg.obj)) {
					String[] values = ((String) msg.obj).split(",");
					device.setPlaybackResult(Double.parseDouble(values[0]));
					device.setRecordResult(Double.parseDouble(values[1]));
					device.setRingResult(Double.parseDouble(values[2]));
				}
				activity.showRingTestResult();
				break;
			case BluetoothMOSService.EXTRA_HADSET_OFF:
				AlertWakeLock.release();
				activity.mConnectDevice.setChecked(false);
				activity.showDeviceInfo();
				activity.showRingTestResult();
				break;
			case BluetoothMOSService.EXTRA_SHOW_PARAMS:
				AlertWakeLock.release();
				if (device != null && !StringUtil.isNullOrEmpty((String) msg.obj)) {
					String[] values = ((String) msg.obj).split(",");
					device.setPower(Integer.parseInt(values[0]));
					device.setPlaybackVolume(Integer.parseInt(values[1]));
					if (values.length > 2) {
						device.setRecordVolume(Integer.parseInt(values[2]));
						device.setVersion(values[3]);
					}
				}
				activity.showDeviceInfo();
				break;
			case SHOW_DEVICE_INFO:
				AlertWakeLock.release();
				activity.dismissProgress();
				activity.showDeviceInfo();
				activity.showRingTestResult();
				break;
			case BluetoothMOSService.EXTRA_SHOW_FILES:
				AlertWakeLock.release();
				if (device != null && !StringUtil.isNullOrEmpty((String) msg.obj)) {
					device.setFileIDs((String) msg.obj);
				}
				activity.showDeviceInfo();
				break;
			case SHOW_CALCULATE_RESULT:
				Toast.makeText(activity, (String) msg.obj, Toast.LENGTH_LONG).show();
				break;
			case SHOW_DISCOVERED_DEVICES:
				List<BluetoothMOSDevice> discoveredDevices = (List<BluetoothMOSDevice>) msg.obj;
				activity.showDiscoveredBluetoothList(discoveredDevices);
			break;
			default:
				break;
			}
		}
	}

	/**
	 * 连接中断
	 */
	private void connectInterrupt() {
		Toast.makeText(BluetoothMOSActivity.this, R.string.task_callMOS_device_disconnected, Toast.LENGTH_SHORT).show();
		this.mFactory.setCurrMOCDevice(null);
		this.dismissProgress();
		this.showDeviceInfo();
		this.showRingTestResult();
		this.mConnectDevice.setChecked(false);
	}

	/**
	 * 查找设备失败操作
	 */
	private void findDeviceFail() {
		Toast.makeText(BluetoothMOSActivity.this, R.string.task_callMOS_device_no_finded, Toast.LENGTH_SHORT).show();
		this.mFactory.setCurrMOCDevice(null);
		this.dismissProgress();
		this.showDeviceInfo();
		this.showRingTestResult();
		this.mConnectDevice.setChecked(false);
	}

	/**
	 * 服务类链接类
	 */
	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			LogUtil.d(TAG, "--------onServiceDisconnected---------");
			if (mServiceBinder != null) {
				mServiceBinder = null;
			}
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			LogUtil.d(TAG, "--------onServiceConnected---------");
			mServiceBinder = IBluetoothMOSServiceBinder.Stub.asInterface(binder);
			isBindMOSService = true;
			try {
				mServiceBinder.registerCallback(mCallback);
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						LogUtil.d(TAG, "----onServiceConnected----getDeviceParams------");
						try {
							mServiceBinder.getDeviceParams();
							mServiceBinder.getHaveFileIDs();
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}, 1000);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 回调方法
	 */
	private IBluetoothMOSServiceCallback mCallback = new IBluetoothMOSServiceCallback.Stub() {

		@Override
		public void handleMessage(int what, String message) throws RemoteException {
			LogUtil.d(TAG, "--------handleMessage----what:" + what + "--message:" + message + "---");
			if (message == null) {
				mHandler.sendEmptyMessage(what);
			} else {
				mHandler.obtainMessage(what, message).sendToTarget();
			}

		}

		@Override
		public void handleDevice(BluetoothMOSDevice device) throws RemoteException {
			mFactory.setCurrMOCDevice(device);
			mHandler.sendEmptyMessage(SHOW_DEVICE_INFO);
		}

		@Override
		public void onFinishDiscoverBluetoothDevices(List<BluetoothMOSDevice> discoveredDevices) throws RemoteException {
			Message msg = new Message();
			msg.obj = discoveredDevices;
			msg.what = SHOW_DISCOVERED_DEVICES;
			mHandler.sendMessage(msg);
		}
	};

	/**
	 * 显示扫描到的蓝牙设备列表
	 */
	private void showDiscoveredBluetoothList(final List<BluetoothMOSDevice> discoveredDevices) {
		if(null != discoveredDevices && !discoveredDevices.isEmpty()){
			String[] deviceMacAddresses = new String[discoveredDevices.size()];
			for (int i = 0; i < discoveredDevices.size(); i++) {
				BluetoothMOSDevice device = discoveredDevices.get(i);
				LogUtil.e(TAG,"showDiscoveredBluetoothList name:" + device.getName() + " , mac:" +device.getAddress() );
				deviceMacAddresses[i] = device.getAddress();
			}
			BasicDialog.Builder builder = new BasicDialog.Builder(this);
			builder.setTitle("请选择插入的主叫蓝牙设备").setSingleChoiceItems(deviceMacAddresses, 0, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					BluetoothMOSDevice selectedDev = discoveredDevices.get(which);
					LogUtil.e(TAG,"选择了 " + selectedDev.getName() + " , mac:" + selectedDev.getAddress());
//					selectedDev.connect(true);
					try {
						mServiceBinder.connect(selectedDev.getAddress());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}).setNegativeButton(R.string.str_cancle).show().setCanceledOnTouchOutside(false);
		}else{
			Toast.makeText(this,"扫描不到设备",Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onDestroy() {
		LogUtil.d(TAG, "------onDestroy------");
		this.unbindMOSService();
		super.onDestroy();
	}

	/**
	 * 解除蓝牙MOS头服务绑定
	 */
	private void unbindMOSService() {
		LogUtil.d(TAG, "--------------unbindMOSService---------------");
		if (isBindMOSService) {
			isBindMOSService = false;
			this.mServiceBinder = null;
			Intent intent = new Intent(this, BluetoothMOSService.class);
			stopService(intent);
			this.unbindService(conn);
		}
	}

	/**
	 * 写文件到MOS头
	 * 
	 */
	private void writeFileToMOS() {
		if (mServiceBinder == null || this.mFactory.getCurrMOCDevice() == null) {
			Toast.makeText(BluetoothMOSActivity.this, R.string.task_callMOS_device_no_connected, Toast.LENGTH_SHORT).show();
			return;
		}
		if (this.mFileType != null) {
			this.showProgressDialog(this.getString(R.string.task_callMOS_change_file_doing));
			try {
				this.mServiceBinder.writeFileTo(this.mFileType);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/** 关闭可用接入点 */
	private synchronized void setDataEnabled(boolean enabled) {
		try {
			APNOperate apnOperate = APNOperate.getInstance(this);
			if (enabled || (!enabled && apnOperate.checkNetWorkIsConnected())) {
				apnOperate.setMobileDataEnabled(enabled, "", true, 1000 * 15);
			}
		} catch (Exception e) {
			LogUtil.e(TAG, e.getMessage());
		}
	}

	/**
	 * 获取网络时间
	 */
	private void syncNetWorkTime() {
		new Thread() {

			@Override
			public void run() {
				if (!MyPhoneState.getInstance().isNetworkAvirable(getApplicationContext())) {
					setDataEnabled(true);
				}
				long time = UtilsMethod.getNetTimeByBjtime();
				// 分别取得时间中的小时，分钟和秒，并输出
				LogUtil.d(TAG, "sync net work time:" + UtilsMethod.sdFormat.format(new Date(time)));
				LogUtil.d(TAG, "server time offset:" + (System.currentTimeMillis() - time));
				ApplicationModel.getInstance().setServerTimeOffset(System.currentTimeMillis() - time);
//				UtilsMethod.setTime(time,getApplicationContext());
			}

		}.start();
	}

}
