package com.walktour.service.bluetoothmos;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.service.DataFeelUtils;
import com.walktour.service.bluetoothmos.command.BaseCommand.FileType;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;
import com.walktour.service.bluetoothmos.util.FileUtil;
import com.walktour.service.test.IPesqCalculator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * 蓝牙被叫MOS头连接服务类
 *
 * @author jianchao.wang
 *
 */
public class BluetoothMTCService extends Service {
	/** 日志标识 */
	private static final String TAG = "BluetoothMTCService";
	/** 传递的关键字：是否要计算PESQ */
	public static final String EXTRA_KEY_CALCULATE_PESQ = "calculate_pesq";
	/** 传递的关键字：主叫还是被叫 */
	public static final String EXTRA_KEY_MOS_TYPE = "mos_type";
	/** 传递的关键字：蓝牙MOS头对象 */
	public static final String EXTRA_KEY_BLUETOOTH_MTC = "bluetooth_mtc";
	/** 传递的关键字：当前终端时间减去服务器获取到的时间的值 */
	public static final String EXTRA_KEY_SERVER_TIME_OFFSET = "server_time_offset";
	/** 反馈的消息类型：显示进度条 */
	public static final int EXTRA_SHOW_PROGRESS = 1;
	/** 反馈的消息类型：关闭进度条 */
	public static final int EXTRA_SHOW_PROGRESS_DISMISS = 2;
	/** 反馈的消息类型：连接中断 */
	public static final int EXTRA_CONNECT_INTERRUPT = 3;
	/** 反馈的消息类型：查找设备失败 */
	public static final int EXTRA_FIND_DEVICE_FAIL = 4;
	/** 反馈的消息类型：显示回环检测的分值 */
	public static final int EXTRA_SHOW_RING_RESULT = 5;
	/** 反馈的消息类型：蓝牙MOS头已拔出 */
	public static final int EXTRA_HADSET_OFF = 6;
	/** 反馈的消息类型：显示MOS头参数 */
	public static final int EXTRA_SHOW_PARAMS = 7;
	/** 反馈的消息类型：显示MOS头已有录音文件 */
	public static final int EXTRA_SHOW_FILES = 8;
	/** 当前设备设备是否插入耳机广播 */
	private static final String HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
	/** 默认连接的设备名称 */
	public static final String DEVICE_NAME_DEFAULT = "DL_MicroMOS";
	/** 蓝牙设备适配器 */
	private BluetoothAdapter mAdapter = null;
	/** 当前状态 */
	private State mState = State.init;
	/** 当前搜索到的可连接的设备列表 */
	private List<BluetoothMOSDevice> mFindDevices = new ArrayList<BluetoothMOSDevice>();
	/** 是否注册了蓝牙广播监听器 */
	private boolean isRegisterBluetoothReceiver = false;
	/** 当前终端是否已经插入了MOS头 */
	private boolean isHadsetOn = false;
	/** PESQ 算分类 */
	private IPesqCalculator pesqCalculator = null;
	/** PESQ 是否连接 */
	private boolean hasPesqConnected = false;
	/** 当前匹配成功的设备 */
	private BluetoothMOSDevice mCurrDevice = null;
	/** 回调函数集合 */
	private final RemoteCallbackList<IBluetoothMOSServiceCallback> mCallbacks = new RemoteCallbackList<IBluetoothMOSServiceCallback>();
	/** 是否在查找设备 */
	private boolean isSearchDevice = false;
	/** 当前生成的录音文件 */
	private List<String> mRecordFileList = new ArrayList<String>();

	/**
	 * mos类型
	 */
	private int mosType = BluetoothMOSDevice.DEVICE_TYPE_MOC;
	/** 状态类型 */
	private enum State {
		init, find_device_doing, find_device_fail, find_device_success, check_device;
	};

	/**
	 * 注册蓝牙监听类
	 */
	private void registerBluetoothReceiver() {
		if (this.isRegisterBluetoothReceiver)
			return;
		LogUtil.d(TAG,"----registerBluetoothReceiver----");
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mBluetoothReceiver, filter);
		this.isRegisterBluetoothReceiver = true;
	}

	/**
	 * 注销蓝牙监听类
	 */
	private void unregisterBluetoothReceiver() {
		if (!this.isRegisterBluetoothReceiver)
			return;
		LogUtil.d(TAG,"----unregisterBluetoothReceiver----");
		this.unregisterReceiver(mBluetoothReceiver);
		this.isRegisterBluetoothReceiver = false;
	}

	/**
	 * 设置当前的连接状态
	 *
	 * @param state
	 *          连接状态
	 */
	private synchronized void setState(State state) {
		LogUtil.d(TAG, "setState:" + mState + " -> " + state);
		mState = state;
		switch (state) {
		case find_device_fail:
			mAdapter.cancelDiscovery();
			this.unregisterBluetoothReceiver();
			this.findDeviceFail();
			break;
		case check_device:
			mAdapter.cancelDiscovery();
			this.unregisterBluetoothReceiver();
			this.sortDevices();
			this.onFinishDiscoverDevices();
//			this.checkNextDevice();
			break;
		default:
			break;
		}

	}

	/**
	 * 结束扫描蓝牙设备时通知界面弹出列表选择
	 */
	private void onFinishDiscoverDevices() {
		LogUtil.d(TAG,"----onFinishDiscoverDevices----");
		final int N = mCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			try {
				mCallbacks.getBroadcastItem(i).onFinishDiscoverBluetoothDevices(mFindDevices);
			} catch (RemoteException e) {
			}
		}
		mCallbacks.finishBroadcast();
	}

	/**
	 * 根据信号强度由高到低排序蓝牙设备
	 */
	private void sortDevices() {
		if (this.mFindDevices.isEmpty() || this.mFindDevices.size() == 1)
			return;
		Collections.sort(this.mFindDevices, new Comparator<BluetoothMOSDevice>() {

			@Override
			public int compare(BluetoothMOSDevice lhs, BluetoothMOSDevice rhs) {
				return lhs.getSignalLevel() - rhs.getSignalLevel();
			}

		});
	}

	/**
	 * 开始搜索蓝牙设备
	 */
	private void startDiscovery() {
		LogUtil.d(TAG, "----------startDiscovery-------------");
		if (mAdapter.isDiscovering())
			mAdapter.cancelDiscovery();
		LogUtil.d(TAG, "-----state:" + mAdapter.getState() + "------");
		boolean flag = mAdapter.startDiscovery();
		LogUtil.d(TAG, "-----startDiscovery:" + flag + "------");
	}

	/**
	 * 返回查找设备失败
	 */
	public void findDeviceFail() {
		this.mCurrDevice = null;
		this.handlerMessage(EXTRA_FIND_DEVICE_FAIL, null);
	}

	/**
	 * 显示进度条信息
	 *
	 * @param title
	 *          标题
	 */
	public void showProgressDialog(String title) {
		this.handlerMessage(EXTRA_SHOW_PROGRESS, title);
	}

	/**
	 * 返回进度条关闭
	 *
	 */
	public void handleProgressDismiss() {
		this.handlerMessage(EXTRA_SHOW_PROGRESS_DISMISS, null);
	}

	/**
	 * 返回蓝牙头拔出
	 */
	private void handleHadsetOff() {
		this.handlerMessage(EXTRA_HADSET_OFF, null);
	}

	/**
	 * 蓝牙设备发现监听类
	 */
	private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			LogUtil.d(TAG,"---BroadcastReceiver---action:"+action);
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				LogUtil.d(TAG, "-----------ACTION_STATE_CHANGED:" + mAdapter.getState() + "------------");
				if (isSearchDevice && mAdapter.getState() == BluetoothAdapter.STATE_ON) {
					mAdapter.startDiscovery();
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				LogUtil.d(TAG, "-----------ACTION_DISCOVERY_STARTED------------");
				mFindDevices.clear();
				setState(BluetoothMTCService.State.find_device_doing);
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				LogUtil.d(TAG, "-----------ACTION_DISCOVERY_FINISHED------------");
				if (mFindDevices.isEmpty())
					setState(BluetoothMTCService.State.find_device_fail);
				else
					setState(BluetoothMTCService.State.check_device);
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
				if (device == null || device.getName() == null)
					return;
				LogUtil.d(TAG, "get bluetoothDevice,name:" + device.getName() + " addr:" + device.getAddress());
				if (device.getName().startsWith(DEVICE_NAME_DEFAULT)) {
					for (BluetoothMOSDevice device1 : mFindDevices) {
						if (device1.getAddress().equals(device.getAddress()))
							return;
					}
					BluetoothMOSDevice bmd = new BluetoothMOSDevice(device, BluetoothMTCService.this, rssi);
					bmd.setDeviceType(mosType);
					mFindDevices.add(bmd);
				}
			}
		}
	};

	/**
	 * 耳机设备发现监听类
	 */
	private final BroadcastReceiver mHadsetReceiver = new BroadcastReceiver() {
		@SuppressWarnings("deprecation")
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (HEADSET_PLUG.equals(action)) {
				AudioManager localAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				if (localAudioManager.isWiredHeadsetOn()) {
					isHadsetOn = true;
				} else {
					isHadsetOn = false;
					if (mCurrDevice != null) {
						mCurrDevice.disconnect(true);
						mCurrDevice = null;
					}
					handleHadsetOff();
				}
				LogUtil.d(TAG, "isHadsetOn = " + isHadsetOn);
			}
		}
	};

	/**
	 * 把MOS头传过来的pcm格式的音频文件转成wav格式的音频文件
	 *
	 * @param recordFilePath
	 *          录音文件
	 * @param sampleRate
	 *          采样率(K)
	 */
	public String convertAudioFile(String recordFilePath, int sampleRate) {
		if (StringUtil.isNullOrEmpty(recordFilePath))
			return recordFilePath;
		File recordFile = new File(recordFilePath);
		if (!recordFile.exists() || !recordFile.getName().endsWith("pcm"))
			return recordFilePath;
		try {
			File newFile = new File(recordFile.getAbsolutePath().replaceAll(".pcm", ".wav"));
			if (!newFile.exists())
				newFile.createNewFile();
			FileUtil.convertPCMtoWAV(recordFile, newFile, sampleRate);
			recordFile.delete();
			return newFile.getAbsolutePath();
		} catch (Exception e) {
			LogUtil.e(TAG, e.getMessage(), e);
		}
		return recordFilePath;
	}

	/**
	 * 获取PESQ评分
	 *
	 * @param rawId
	 *          资源ID
	 * @param filePath
	 *          要评估文件ID
	 * @return [pesqscore,pesqLq,pesq]
	 */
	@SuppressWarnings("unchecked")
	public double[] calculatePESQ(int rawId, String filePath) {
		LogUtil.d(TAG, "-----pesq Calculator start-----");
		double[] result = new double[3];
		try {
			Map<String, Object> map = pesqCalculator.calculate(rawId, filePath);
			result[0] = (Double) map.get("pesqscore");
			result[1] = (Double) map.get("pesqLq");
			result[2] = (Double) map.get("pesq");
			LogUtil.d(TAG,
					"-----pesqScore:" + result[0] + ",pesqLQ:" + result[1] + ",pesq:" + result[2] + ",file:" + filePath);
		} catch (RemoteException e) {
			e.printStackTrace();
			LogUtil.e(TAG, "-----pesq culculator error-----", e.fillInStackTrace());
		}
		LogUtil.d(TAG, "-----pesq Calculator end-----");
		return result;
	}

	/**
	 * 将语音评估分值写入RCU文件中，并写入该RCU文件名
	 *
	 * @param rawId
	 *          资源ID
	 * @param filePath
	 *          要评估文件路径
	 */
	public double pesqCalculate(int rawId, String filePath) {
		double[] result = this.calculatePESQ(rawId, filePath);
		// 下面3个值都已经放大1000倍
		double pesq = result[2];
		return pesq / 1000f;
	}

	/**
	 * 如果当前的设备不是正确的设备，则检查下一个搜索到的设备
	 */
	public void checkNextDevice() {
		LogUtil.d(TAG, "-----checkNextDevice-----");
		if (mFindDevices.isEmpty()) {
			this.setState(State.find_device_fail);
			return;
		}
		BluetoothMOSDevice device = this.mFindDevices.remove(0);
		device.connect(true);
	}

	/**
	 * 当前找到了正确的设备
	 *
	 * @param device
	 *          正确的设备
	 */
	public void findDeviceSuccess(BluetoothMOSDevice device) {
		LogUtil.d(TAG, "-----findDeviceSuccess-----");
		this.mCurrDevice = device;
		this.mFindDevices.clear();
		final int N = mCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			try {
				mCallbacks.getBroadcastItem(i).handleDevice(device);
			} catch (RemoteException e) {
			}
		}
		mCallbacks.finishBroadcast();
		this.mCurrDevice.catchDeviceParams();
		this.mCurrDevice.catchHaveFileIDs();
	}

	/**
	 * 显示回环检测的分值
	 */
	public void showRingTestResult() {
		String value = this.mCurrDevice.getPlaybackResult() + "," + this.mCurrDevice.getRecordResult() + ","
				+ this.mCurrDevice.getRingResult();
		this.handlerMessage(EXTRA_SHOW_RING_RESULT, value);
	}

	/**
	 * 显示MOS头已有的音频文件
	 *
	 * @param fileIDs
	 *          文件ID列表
	 */
	public void showFileIDs(String fileIDs) {
		this.handlerMessage(EXTRA_SHOW_FILES, fileIDs);
	}

	/**
	 * 显示MOS头参数
	 *
	 * @param power
	 *          剩余电量
	 * @param playbackVolume
	 *          放音音量 （0~255）
	 * @param recordVolume
	 *          录音音量(0~2)
	 * @param version
	 *          软件版本
	 */
	public void showParams(int power, int playbackVolume, int recordVolume, String version) {
		this.handlerMessage(EXTRA_SHOW_PARAMS, power + "," + playbackVolume + "," + recordVolume + "," + version);
	}

	/**
	 * 查找正确的设备
	 * @param needHeadsetOn 是否需要检查蓝牙是否插入耳机口 主叫true 被叫false
	 * @return
	 */
	private boolean searchCorrectDevice(boolean needHeadsetOn) {
		this.setState(State.init);
		if (mCurrDevice != null) {
			mCurrDevice.disconnect(true);
			mCurrDevice = null;
		}
		if (needHeadsetOn && !this.isHadsetOn) {
			LogUtil.d(TAG, "hadset is Off");
			return false;
		}
		this.showProgressDialog(this.getResources().getString(R.string.task_callMOS_connect_device_doing));
		LogUtil.d(TAG, "hadset is On");
		this.registerBluetoothReceiver();
		if (!this.mAdapter.isEnabled()) {
			this.isSearchDevice = true;
			this.mAdapter.enable();
		} else {
			LogUtil.d(TAG, "---startDiscovery---");
			this.startDiscovery();
		}
		this.mCurrDevice = null;
		return true;
	}


	@Override
	public IBinder onBind(Intent intent) {
		LogUtil.d(TAG, "-----onBind-----");
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		boolean isCalculatePESQ = intent.getBooleanExtra(EXTRA_KEY_CALCULATE_PESQ, false);
		if (isCalculatePESQ)
			this.bindPesqService();
		mosType = intent.getIntExtra(EXTRA_KEY_MOS_TYPE,BluetoothMOSDevice.DEVICE_TYPE_MOC );
		this.mCurrDevice = intent.getParcelableExtra(EXTRA_KEY_BLUETOOTH_MTC);
		if (this.mCurrDevice != null) {
			this.mCurrDevice.setMTCService(this);
			this.mCurrDevice.connect(false);
		}
		this.registerReceiver(mHadsetReceiver, new IntentFilter(HEADSET_PLUG));
		return this.mBinder;
	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "-----onDestroy-----");
		super.onDestroy();
	}

	/**
	 * 调用PESQ的远程服务连接
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LogUtil.d(TAG, "------------onServiceConnected---------------");
			pesqCalculator = IPesqCalculator.Stub.asInterface(service);
			hasPesqConnected = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			hasPesqConnected = false;
		}
	};

	/**
	 * 初始化样本文件
	 *
	 * @param filePath
	 *          文件路径
	 * @param rawId
	 *          资源ID
	 * @return pcm格式的文件路径
	 */
	private String initVoiceFile(String filePath, int rawId) {
		File wavFile = new File(filePath);
		File pcmFile = new File(filePath.replaceAll(".wav", ".pcm"));
		if (!pcmFile.exists()) {
			LogUtil.d(TAG, "wrtite ref file:" + filePath);
			UtilsMethod.writeRawResource(this, rawId, wavFile);
			FileUtil.convertWAVtoPCM(wavFile, pcmFile);
			wavFile.delete();
			LogUtil.d(TAG, "wrtite ref file end");
		}
		return pcmFile.getAbsolutePath();
	}

	/**
	 * 初始化样本文件
	 *
	 * @param fileType
	 *          文件类型
	 * @return
	 */
	private String initVoiceSample(FileType fileType) {
		String voicePath = getFilesDir() + File.separator;
		int rawId = 0;
		switch (fileType) {
		case pesq_8k:
			voicePath += "pesq_8k.wav";
			rawId = R.raw.pesqvoice;
			break;
		case polqa_8k:
			voicePath += "polqa_8k.wav";
			rawId = R.raw.sample_nb_8k;
			break;
		case polqa_16k:
			voicePath += "polqa_16k.wav";
			rawId = R.raw.sample_wb_16k;
			break;
		case polqa_48k:
			voicePath += "polqa_48k.wav";
			rawId = R.raw.sample_swb_48k;
			break;
		case check_8k:
			voicePath += "check_8k.wav";
			rawId = R.raw.mos_check_8k;
			break;
		}
		return this.initVoiceFile(voicePath, rawId);
	}

	/**
	 * 取消绑定PESQ远程服务
	 */
	private void unbindPesqService() {
		LogUtil.d(TAG, "-----unbindPesqService-----");
		Intent intent = new Intent(this, DataFeelUtils.class);
		stopService(intent);
		if (hasPesqConnected) {
			hasPesqConnected = false;
			unbindService(mConnection);
			this.pesqCalculator = null;
		}
	}

	/**
	 * 绑定PESQ远程服务
	 */
	private void bindPesqService() {
		LogUtil.d(TAG, "-----bindPesqService-----");
		Intent intent = new Intent(this, DataFeelUtils.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * 反馈消息
	 *
	 * @param what
	 *          消息类型
	 * @param message
	 *          消息内容
	 */
	private void handlerMessage(int what, String message) {
		final int N = mCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			try {
				mCallbacks.getBroadcastItem(i).handleMessage(what, message);
			} catch (RemoteException e) {
			}
		}
		mCallbacks.finishBroadcast();
	}

	/**
	 * 反馈当前配对的蓝牙MOS头连接断开
	 */
	public void handlerConnectInterrupt() {
		final int N = mCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			try {
				mCallbacks.getBroadcastItem(i).handleMessage(EXTRA_CONNECT_INTERRUPT, null);
			} catch (RemoteException e) {
			}
		}
		mCallbacks.finishBroadcast();
	}

	/**
	 * 绑定类
	 */
	private final IBluetoothMOSServiceBinder.Stub mBinder = new IBluetoothMOSServiceBinder.Stub() {

		@Override
		public boolean runPlayback(String fileType) throws RemoteException {
			LogUtil.d(TAG, "-----runPlayback:" + fileType + "-----");
			if(mCurrDevice != null){
				LogUtil.d(TAG, "-----runPlayback:" + mCurrDevice.getAddress() + "-----");
			}else{
				LogUtil.d(TAG, "-----runPlayback:device null-----");
			}
			if (mCurrDevice != null)
				return mCurrDevice.runPlayback(FileType.getType(fileType));
			return false;
		}

		@Override
		public boolean runRecord(String fileType) throws RemoteException {
			LogUtil.d(TAG, "-----runRecord:" + fileType + "-----");
			if (mCurrDevice != null)
				return mCurrDevice.runRecord(FileType.getType(fileType));
			return false;
		}

		@Override
		public String getRecordFile() throws RemoteException {
			if (mRecordFileList.isEmpty())
				return null;
			return mRecordFileList.remove(0);
		}

		@Override
		public boolean findCorrectDevice(boolean needHeadsetOn) throws RemoteException {
			LogUtil.d(TAG, "-----findCorrectDevice-----");
			return searchCorrectDevice(needHeadsetOn);

		}

		@Override
		public void registerCallback(IBluetoothMOSServiceCallback callback) throws RemoteException {
			LogUtil.d(TAG, "-----registerCallback-----");
			if (callback != null) {
				mCallbacks.register(callback);
			}
		}

		@Override
		public void unregisterCallback(IBluetoothMOSServiceCallback callback) throws RemoteException {
			LogUtil.d(TAG, "-----unregisterCallback-----");
			if (callback != null) {
				mCallbacks.unregister(callback);
			}
		}

		@Override
		public void testPlaybackAndRecord() throws RemoteException {
			LogUtil.d(TAG, "-----testPlaybackAndRecord-----");
			if (mCurrDevice != null)
				mCurrDevice.testPlaybackAndRecord();
		}

		@Override
		public void writeFileTo(String fileType) throws RemoteException {
			LogUtil.d(TAG, "-----writeFileTo-----");
			if (mCurrDevice != null) {
				String filePath = initVoiceSample(FileType.getType(fileType));
				mCurrDevice.writeFileTo(filePath, FileType.getType(fileType));
			}
		}

		@Override
		public double[] getCalculatePESQ(int rawId, String filePath) throws RemoteException {
			LogUtil.d(TAG, "-----getCalculatePESQ-----");
			double[] result = new double[3];
			if (hasPesqConnected) {
				filePath = convertAudioFile(filePath, 8);
				result = calculatePESQ(rawId, filePath);
			}
			return result;
		}

		@Override
		public void getDeviceParams() throws RemoteException {
			LogUtil.d(TAG, "-----getDeviceParams-----");
			if (mCurrDevice != null) {
				mCurrDevice.catchDeviceParams();
			}
		}

		@Override
		public void setDeviceParams(int playbackVolume, int recordVolume) throws RemoteException {
			LogUtil.d(TAG, "-----setDeviceParams-----");
			if (mCurrDevice != null) {
				mCurrDevice.setDeviceParams(playbackVolume, recordVolume);
			}
		}

		@Override
		public void getHaveFileIDs() throws RemoteException {
			LogUtil.d(TAG, "-----getHaveFileIDs-----");
			if (mCurrDevice != null) {
				mCurrDevice.catchHaveFileIDs();
			}
		}

		@Override
		public void disconnect() throws RemoteException {
			LogUtil.d(TAG, "-----disconnect-----");
			if (mCurrDevice != null) {
				mCurrDevice.disconnect(true);
				mCurrDevice = null;
			}
		}

		@Override
		public void clearAudioFiles() throws RemoteException {
			LogUtil.d(TAG, "-----clearAudioFiles-----");
			if (mCurrDevice != null) {
				mCurrDevice.clearAudioFiles();
				String filePath = initVoiceSample(FileType.check_8k);
				mCurrDevice.writeFileTo(filePath, FileType.check_8k);
			}
		}

		@Override
		public boolean initMOS(String fileType, boolean isPlayer) throws RemoteException {
			LogUtil.d(TAG, "-----initMOS-----");
			if (mCurrDevice != null) {
				return mCurrDevice.initMOS(FileType.getType(fileType), isPlayer);
			}
			return false;
		}

		@Override
		public void setFileNameExtends(String rcuFileName, String fileNameExtends) throws RemoteException {
			LogUtil.d(TAG, "-----setFileNameExtends-----");
			if (mCurrDevice != null) {
				mCurrDevice.setRCUFileName(rcuFileName);
				mCurrDevice.setFileNameExtends(fileNameExtends);
			}
		}

		@Override
		public void interrupt() throws RemoteException {
			LogUtil.d(TAG, "-----interrupt-----");
			if (mCurrDevice != null)
				mCurrDevice.interrupt();
		}

		@Override
		public int getDevicePower() throws RemoteException {
			LogUtil.d(TAG, "-----getDevicePower-----");
			if (mCurrDevice != null)
				return mCurrDevice.getPower();
			return 0;
		}

		@Override
		public void connect(String mac) throws RemoteException {
			LogUtil.d(TAG,"connect nac:" + mac);
			if(mFindDevices!=null && !mFindDevices.isEmpty()){
				BluetoothMOSDevice targetDevice = null;
				for (BluetoothMOSDevice findDevice : mFindDevices) {
					if(mac.equals(findDevice.getAddress())){
						targetDevice = findDevice;
						break;
					}
				}
				if(targetDevice != null){
					targetDevice.connect(mosType == BluetoothMOSDevice.DEVICE_TYPE_MOC);
				}
			}
		}

	};

	@Override
	public boolean onUnbind(Intent intent) {
		LogUtil.d(TAG, "-----onUnbind-----");
		this.unregisterBluetoothReceiver();
		this.unregisterReceiver(mHadsetReceiver);
		this.unbindPesqService();
		if (this.mCurrDevice != null) {
			this.mCurrDevice.disconnect(true);
			this.mCurrDevice = null;
		}
		this.mCallbacks.kill();
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		LogUtil.d(TAG, "------onRebind------");
		super.onRebind(intent);
	}

	public void setRecordFile(String recordFile) {
		this.mRecordFileList.add(recordFile);
	}
}
