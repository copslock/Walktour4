package com.walktour.service.bluetoothmos.model;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Parcel;
import android.os.Parcelable;

import com.walktour.Utils.StringUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.gui.R;
import com.walktour.service.bluetoothmos.BluetoothMOSFactory;
import com.walktour.service.bluetoothmos.BluetoothMOSService;
import com.walktour.service.bluetoothmos.BluetoothMTCService;
import com.walktour.service.bluetoothmos.command.BaseCommand;
import com.walktour.service.bluetoothmos.command.BaseCommand.CommandType;
import com.walktour.service.bluetoothmos.command.BaseCommand.FileType;
import com.walktour.service.bluetoothmos.command.ConnectCheckCommand;
import com.walktour.service.bluetoothmos.command.ConnectConfirmCommand;
import com.walktour.service.bluetoothmos.command.DisconnectCommand;
import com.walktour.service.bluetoothmos.command.FlashCommand;
import com.walktour.service.bluetoothmos.command.GetFileListCommand;
import com.walktour.service.bluetoothmos.command.GetParamsCommand;
import com.walktour.service.bluetoothmos.command.InitMOSCommand;
import com.walktour.service.bluetoothmos.command.PlaybackCommand;
import com.walktour.service.bluetoothmos.command.RecordAndReadCommand;
import com.walktour.service.bluetoothmos.command.SetParamsCommand;
import com.walktour.service.bluetoothmos.command.UninitMOSCommand;
import com.walktour.service.bluetoothmos.command.WriteCommand;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 蓝牙MOS头设备对象
 * 
 * @author jianchao.wang
 *
 */
public class BluetoothMOSDevice implements Parcelable {
	/** 日志标识 */
	private final static String TAG = "BluetoothMOSDevice";
	/** 设备的UUID ,当前是匿名连接的默认UUID */
	private static final UUID sUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	/** 评分结果基准 */
	private static final double RESULT_STANDARD = 2.0;
	/** 设备名称 */
	private String mName;
	/** 蓝牙对象 */
	private BluetoothDevice mDevice;
	/** 服务类 */
	private BluetoothMOSService mService;

	private BluetoothMTCService mMTCService;
	/** 当前设备状态 */
	private State mState = State.init;
	/** 客户端通道通信线程，用来做设备间通信 */
	private SocketThread mSocketThread = null;
	/** 通道维持心跳线程 */
	private HeartbeatThread mHeartbeatThread = null;
	/** 数据包处理线程 */
	private PackageDealThread mPackageDealThread = null;
	/** 当前正在执行的命令 */
	private BaseCommand mCurrCommand = null;
	/** 当前正在等待执行的命令列表 */
	private final List<BaseCommand> mWaitCommands = new ArrayList<BaseCommand>();
	/** 当前是否正在做MOS头回环测试 */
	private boolean isRingTest = false;
	/** 当前是否正在做连接测试 */
	private boolean isConnectTest = false;
	/** 当前执行的文件类型 */
	private FileType mFileType = FileType.polqa_48k;
	/** 当前执是放音还是录音 */
	private boolean isPlayer = false;
	/** 放音检测分值 */
	private double mPlaybackResult;
	/** 录音检测分值 */
	private double mRecordResult;
	/** 回环检测分值 */
	private double mRingResult;
	/** 信号强度 */
	private int mSignalLevel;
	/** 获取当前设备的剩余电量 */
	private int mPower = 100;
	/** 获取当前设备的放音音量 */
	private int mPlaybackVolume;
	/** 获取当前设备的录音音量 */
	private int mRecordVolume;
	/** 获取当前设备的软件版本号 */
	private String mVersion;
	/** 蓝牙MOS头的MAC地址 */
	private String mMACAddress;
	/** 已有文件ID数组 */
	private String mFileIDs;
	/** 当前是否在检查该设备是否是插入到当前终端且硬件正常 */
	private boolean isCheckDevice = false;
	/** 是否停止连接 */
	private boolean isStopConnect = false;
	/** 是否开启MOS芯片 */
	private boolean isInitMOS = false;
	/** 等待处理数据包队列 */
	private Queue<byte[]> mPackageQueque = new ArrayBlockingQueue<byte[]>(200);
	/** 最后一次通信时间，包括发送和接收信息 */
	private long mLastCommunicateTime = 0;
	/** 生成的录音文件名的扩展属性 */
	private String mFileNameExtends;
	/** 关联的RCU文件名 */
	private String mRCUFileName;

	public BluetoothMOSDevice(BluetoothDevice device, BluetoothMOSService service, short signalLevel) {
		this.mDevice = device;
		this.mName = device.getName();
		this.mMACAddress = device.getAddress();
		this.mService = service;
		this.mSignalLevel = signalLevel;
	}
	public BluetoothMOSDevice(BluetoothDevice device, BluetoothMTCService service, short signalLevel) {
		this.mDevice = device;
		this.mName = device.getName();
		this.mMACAddress = device.getAddress();
		this.mMTCService = service;
		this.mSignalLevel = signalLevel;
	}

	public BluetoothMOSDevice(Parcel in) {
		this.mState = State.connect_broken;
		this.mName = in.readString();
		this.mFileType = FileType.getType(in.readString());
		this.mSignalLevel = in.readInt();
		this.mMACAddress = in.readString();
		this.mDeviceType = in.readInt();
		this.initDevice();
	}

	/**
	 * 初始化蓝牙设备，由于涉及到跨进程通信，在通信的另一端需要对蓝牙设备重新初始化
	 */
	private void initDevice() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		this.mDevice = adapter.getRemoteDevice(this.mMACAddress);
	}

	/** 状态类型 */
	private  enum State {
		init("init"), connect_doing("connect doing"), connect_fail("connect_fail"), connect_success(
				"connect success"), connect_broken("connect broken"), connect_interrupt("connect interrupt"), devcie_ready(
						"device ready"), command_doing("command doing"), command_waiting("command waiting");
		/** 状态名称 */
		private String mName;

		private State(String name) {
			this.mName = name;
		}

		public String getName() {
			return this.mName;
		}

	};

	/**
	 * 设置当前的连接状态
	 * 
	 * @param state
	 *          连接状态
	 */
	private synchronized void setState(State state) {
		LogUtil.d(TAG, "-----device:" + this.mMACAddress + " setState:" + mState.getName() + " -> " + state.getName());
		mState = state;
		switch (state) {
		case connect_doing:
			this.mSocketThread = new SocketThread();
			this.mSocketThread.setPriority(Thread.MAX_PRIORITY);
			this.mSocketThread.start();
			break;
		case connect_fail:
			this.isConnectTest = false;
			this.isRingTest = false;
			if (this.isCheckDevice){
				if(mDeviceType == DEVICE_TYPE_MOC){
					this.mService.checkNextDevice();
				}else{
					this.mMTCService.checkNextDevice();
				}

			}
			else{
				if(mDeviceType == DEVICE_TYPE_MOC){
					this.mService.handlerConnectInterrupt();
				}else{
					this.mMTCService.handlerConnectInterrupt();
				}
			}

			break;
		case connect_broken:
			this.mPackageQueque.clear();
			this.disconnect(false);
			break;
		case devcie_ready:
			this.mPackageQueque.clear();
			this.mPackageDealThread = new PackageDealThread();
			this.mPackageDealThread.start();
			if (this.isCheckDevice)
				this.testConnect(true);
			else
				this.testConnect(false);
			this.mHeartbeatThread = new HeartbeatThread();
			this.mHeartbeatThread.start();
			break;
		case connect_interrupt:
			this.mPackageQueque.clear();
			this.isConnectTest = false;
			this.isRingTest = false;
			if (this.mPackageDealThread != null) {
				this.mPackageDealThread.stopThread();
				this.mPackageDealThread = null;
			}
			if (this.mCurrCommand != null) {
				this.mCurrCommand.interrupt(false);
				this.mCurrCommand = null;
				if(mDeviceType == DEVICE_TYPE_MOC){
					this.mService.setRecordFile("error");
				}else {
					this.mMTCService.setRecordFile("error");
				}

			}
			if(mDeviceType == DEVICE_TYPE_MOC){
				this.mService.handlerConnectInterrupt();
			}else {
				this.mMTCService.handlerConnectInterrupt();
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 测试连接
	 * 
	 * @param isConnectTest
	 *          是否连接测试
	 */
	private void testConnect(boolean isConnectTest) {
		this.isConnectTest = isConnectTest;
		BaseCommand cmd = new ConnectCheckCommand(this, this.mMACAddress);
		this.sendCommand(cmd);
	}

	/**
	 * 执行下一个命令
	 * 
	 */
	private void runNextCommand() {
		if (mCurrCommand != null && !mCurrCommand.isFinish()) {
			return;
		}
		if (this.mWaitCommands.isEmpty()) {
			this.mCurrCommand = null;
			return;
		}
		if (this.mSocketThread == null) {
			return;
		}
		mCurrCommand = mWaitCommands.remove(0);
		LogUtil.d(TAG, "-----runNextCommand:" + mCurrCommand.getType().getName());
		this.setState(State.command_doing);
		mCurrCommand.runCommand();
	}

	/**
	 * 完成当前执行的命令
	 */
	public void finishCurrentCommand() {
		if (this.mCurrCommand == null)
			return;
		LogUtil.d(TAG, "-----finishCurrentCommand:" + this.mCurrCommand.getType().getName());
		this.setState(State.command_waiting);
		if (this.mCurrCommand.isTimeOutInterrupt()) {
			this.mCurrCommand = null;
			if(mDeviceType == DEVICE_TYPE_MOC){
				this.mService.setRecordFile("error");
			}else{
				this.mMTCService.setRecordFile("error");
			}
			this.reconnect();
			return;
		}
		switch (this.mCurrCommand.getType()) {
		case connect_check:
			if (this.isConnectTest) {
				ConnectCheckCommand cmd = (ConnectCheckCommand) this.mCurrCommand;
				if (cmd.isCanConnect()) {
					testMOSisRight();
				} else {
					this.disconnect(false);
				}
				return;
			}
			if(mDeviceType == DEVICE_TYPE_MTC){
				this.confirmConnect(true);
			}
			break;
		case connect_confirm:
			if (this.isConnectTest) {
				ConnectConfirmCommand cmd = (ConnectConfirmCommand) this.mCurrCommand;
				if (cmd.getFlag() == ConnectConfirmCommand.FLAG_FAIL) {
					this.disconnect(false);
				} else {
					this.isConnectTest = false;
				}
			}
			break;
		case write_data:
			if(mDeviceType == DEVICE_TYPE_MOC){
				this.mService.handleProgressDismiss();
			}else{
				this.mMTCService.handleProgressDismiss();
			}
			this.catchHaveFileIDs();
			return;
		case playback:
			if (this.isRingTest) {
				PlaybackCommand cmd = (PlaybackCommand) this.mCurrCommand;
				this.mPlaybackResult = this.calculatePESQ(cmd.getRecordFile());
				this.testRecord();
				return;
			}
			break;
		case record_and_read:
			if (this.isRingTest || this.isConnectTest) {
				RecordAndReadCommand cmd = (RecordAndReadCommand) this.mCurrCommand;
				this.mRecordResult = this.calculatePESQ(cmd.getRecordFile());
				if (this.isRingTest) {
					if(mDeviceType == DEVICE_TYPE_MOC){
						this.mService.handleProgressDismiss();
						this.mService.showRingTestResult();
					}else{
						this.mMTCService.handleProgressDismiss();
						this.mMTCService.showRingTestResult();
					}
					this.isRingTest = false;
					this.sendCommand(new UninitMOSCommand(this));
					return;
				}
			} else {
				RecordAndReadCommand cmd = (RecordAndReadCommand) this.mCurrCommand;
				if(mDeviceType == DEVICE_TYPE_MOC){
					this.mService.setRecordFile(cmd.getRecordFile());
				}else{
					this.mMTCService.setRecordFile(cmd.getRecordFile());
				}
			}
			break;
		case get_params:
			GetParamsCommand cmd = (GetParamsCommand) this.mCurrCommand;
			this.mPower = cmd.getPower();
			this.mPlaybackVolume = cmd.getPlaybackVolume();
			this.mVersion = cmd.getVersion();
			if(mDeviceType == DEVICE_TYPE_MOC){
				this.mService.showParams(cmd.getPower(), cmd.getPlaybackVolume(), cmd.getRecordVolume(), cmd.getVersion());
			}else{
				this.mMTCService.showParams(cmd.getPower(), cmd.getPlaybackVolume(), cmd.getRecordVolume(), cmd.getVersion());
			}

			break;
		case get_file_ids:
			GetFileListCommand cmd1 = (GetFileListCommand) this.mCurrCommand;

			if(mDeviceType == DEVICE_TYPE_MOC){
				this.mService.showFileIDs(cmd1.getFileIDs());
			}else{
				this.mMTCService.showFileIDs(cmd1.getFileIDs());
			}
			break;
		case flash_data:
			break;
		case uninit_mos:
			this.isInitMOS = false;
			break;
		case init_mos:
			this.isInitMOS = true;
			break;
		case disconnect:
			this.mSocketThread.stopThread();
			while (this.isStopConnect) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (this.isConnectTest) {
				this.isConnectTest = false;

				if(mDeviceType == DEVICE_TYPE_MOC){
					this.mService.checkNextDevice();
				}else{
					this.mMTCService.checkNextDevice();
				}
			}
			break;
		default:
			break;
		}
		this.runNextCommand();
	}

	public static final int DEVICE_TYPE_MOC = 1;
	public static final int DEVICE_TYPE_MTC = 2;
	private int mDeviceType = DEVICE_TYPE_MOC;

	public int getDeviceType() {
		return mDeviceType;
	}

	public void setDeviceType(int deviceType) {
		mDeviceType = deviceType;
	}

	/**
	 * 计算分值
	 * 
	 * @param filePath
	 *          要计算分值的文件路径
	 * @return 返回计算的分值
	 */
	private double calculatePESQ(String filePath) {
		LogUtil.d(TAG, "-----calculatePESQ-----");
		double result = 0;
		if (filePath != null){
			if(mDeviceType == DEVICE_TYPE_MOC){
				result = this.mService.pesqCalculate(this.mFileType.getRawId(), filePath);
			}else{
				result = this.mMTCService.pesqCalculate(this.mFileType.getRawId(), filePath);
			}
		}
		if (this.isConnectTest) {
			File file = new File(filePath);
			if (file.exists() && (result < 1.5 || result > RESULT_STANDARD))
				file.delete();
			this.confirmConnect(result > RESULT_STANDARD);
		}
		return result;
	}

	/**
	 * 把MOS头传过来的pcm格式的音频文件转成wav格式的音频文件
	 * 
	 * @param recordFilePath
	 *          录音文件
	 * @param sampleRate
	 *          采样率(K)
	 */
	public String convertAudioFile(String recordFilePath, int sampleRate) {
		if(mDeviceType == DEVICE_TYPE_MOC){
			return this.mService.convertAudioFile(recordFilePath, sampleRate);
		}else{
			return this.mMTCService.convertAudioFile(recordFilePath, sampleRate);
		}
	}

	/**
	 * 确认当前MOS头是正确连接在当前终端
	 * 
	 * @param isSuccess
	 *          是否正确连接
	 */
	private void confirmConnect(boolean isSuccess) {
		LogUtil.e(TAG,"----confirmConnect " + isSuccess + "----");
		BaseCommand cmd = new ConnectConfirmCommand(this,
				isSuccess ? ConnectConfirmCommand.FLAG_SUCCESS : ConnectConfirmCommand.FLAG_FAIL);
		this.sendCommand(cmd);
		if (isSuccess) {
			if(mDeviceType == DEVICE_TYPE_MOC){
				this.mService.findDeviceSuccess(this);
			}else{
				this.mMTCService.findDeviceSuccess(this);
			}

		}
	}

	/**
	 * 初始化蓝牙MOS头芯片
	 * 
	 * @param fileType
	 *          文件类型
	 * @param isPlayer
	 *          是否放音，否则是录音
	 */
	public boolean initMOS(FileType fileType, boolean isPlayer) {
		LogUtil.d(TAG, "-----initMOS-----");
		this.mFileType = fileType;
		this.isPlayer = isPlayer;
		BaseCommand cmd = new InitMOSCommand(this, this.mFileType, this.isPlayer);
		return this.sendCommand(cmd);
	}

	/**
	 * 反初始化蓝牙MOS头芯片
	 *
	 */
	private boolean uninitMOS() {
		if (!this.isInitMOS)
			return false;
		LogUtil.d(TAG, "-----uninitMOS-----");
		BaseCommand cmd = new UninitMOSCommand(this);
		return this.sendCommand(cmd);
	}

	/**
	 * 测试当前的MOS头是否是插在当前终端上的MOS头，通过当前终端播放测试音频和MOS头录音返回的音频做评分测试
	 */
	private void testMOSisRight() {
		this.mFileType = FileType.check_8k;
		BaseCommand cmd = new InitMOSCommand(this, this.mFileType, false);
		this.sendCommand(cmd);
		cmd = new RecordAndReadCommand(this, this.mFileType, true);
		this.sendCommand(cmd);
	}

	/**
	 * 进行回环测试,先测放音,后测录音
	 */
	public void testPlaybackAndRecord() {
		this.mPlaybackResult = 0;
		this.mRecordResult = 0;
		this.mRingResult = 0;
		if(mDeviceType == DEVICE_TYPE_MOC){
			this.mService.showProgressDialog(this.mService.getResources().getString(R.string.task_callMOS_ring_test_doing));
		}else{
			this.mMTCService.showProgressDialog(this.mMTCService.getResources().getString(R.string.task_callMOS_ring_test_doing));
		}
		this.isRingTest = true;
		this.mFileType = FileType.pesq_8k;
		BaseCommand cmd = new InitMOSCommand(this, this.mFileType, true);
		this.sendCommand(cmd);
		cmd = new PlaybackCommand(this, this.mFileType, true);
		this.sendCommand(cmd);
	}

	/**
	 * 执行放音功能
	 * 
	 * @param fileType
	 *          放音的文件类型
	 */
	public boolean runPlayback(FileType fileType) {
		LogUtil.e(TAG,"----runPlayback----" + getAddress());
		this.mFileType = fileType;
		this.isPlayer = true;
		BaseCommand cmd = new PlaybackCommand(this, fileType, false);
		return this.sendCommand(cmd);
	}

	/**
	 * 中断当前蓝牙设备指令执行
	 */
	public void interrupt() {
		if (this.mState != State.command_doing)
			return;
		LogUtil.d(TAG, "-----interrupt-----");
		this.mCurrCommand.interrupt(true);
	}

	/**
	 * 删除音频文件
	 */
	public void clearAudioFiles() {
		BaseCommand cmd = new FlashCommand(this);
		this.sendCommand(cmd);
	}

	/**
	 * 执行录音功能
	 * 
	 * @param fileType
	 *          文件类型
	 */
	public boolean runRecord(FileType fileType) {
		this.mFileType = fileType;
		this.isPlayer = false;
		BaseCommand cmd = new RecordAndReadCommand(this, fileType, false);
		return this.sendCommand(cmd);
	}

	/**
	 * 测试录音功能
	 */
	private void testRecord() {
		this.mFileType = FileType.pesq_8k;
		BaseCommand cmd = new UninitMOSCommand(this);
		this.sendCommand(cmd);
		cmd = new InitMOSCommand(this, this.mFileType, false);
		this.sendCommand(cmd);
		cmd = new RecordAndReadCommand(this, this.mFileType, true);
		this.sendCommand(cmd);
	}

	/**
	 * 客户端通道通信线程
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class SocketThread extends Thread {
		/** 连接通道 */
		private BluetoothSocket mSocket;
		/** 输入流 */
		private InputStream mInStream;
		/** 输出流 */
		private OutputStream mOutStream;
		/** 是否停止线程 */
		protected boolean isStop = false;
		/** 创建通道测试尝试次数 */
		private final int mMaxConnectTimes = 3;

		/**
		 * 创建通道
		 * 
		 * @return
		 */
		@SuppressLint("NewApi")
		private boolean createSocket() {
			try {
				mSocket = mDevice.createInsecureRfcommSocketToServiceRecord(sUUID);
				mSocket.connect();
				return true;
			} catch (IOException e) {
				LogUtil.e(TAG, "-----create socket error-----" + e.getMessage());
				e.printStackTrace();
			}
			try {
				if (mSocket != null) {
					mSocket.close();
					mSocket = null;
				}
			} catch (IOException e) {
				LogUtil.e(TAG, e.getMessage(), e);
			}
			return false;
		}

		public void run() {
			LogUtil.d(TAG, "-----SocketThread Start-----");
			int times = 0;
			this.isStop = false;
			boolean flag = false;
			while (times < mMaxConnectTimes) {
				flag = this.createSocket();
				if (flag || isCheckDevice) {
					break;
				}
				times++;
			}
			LogUtil.e(TAG,"flag = " + flag);
			if (flag) {
				setState(BluetoothMOSDevice.State.connect_success);
			} else {
				setState(BluetoothMOSDevice.State.connect_fail);
				return;
			}
			try {
				mInStream = mSocket.getInputStream();
				mOutStream = mSocket.getOutputStream();
			} catch (IOException e) {
				LogUtil.e(TAG, "-----sockets not created-----");
			}
			byte[] buffer = new byte[2048];
			int readSize;
			setState(BluetoothMOSDevice.State.devcie_ready);
			byte[] newBytes = null;
			while (!isStop) {
				try {
					readSize = mInStream.read(buffer);
					if (readSize > 0) {
						newBytes = new byte[readSize];
						System.arraycopy(buffer, 0, newBytes, 0, readSize);
						if(!mPackageQueque.offer(newBytes)){
							mPackageQueque.poll();
							mPackageQueque.offer(newBytes);
						}
					}
				} catch (Exception e) {
					LogUtil.d(TAG, "-----MOS connect interrupt-----");
					break;
				}
			}
			if (!this.isStop) {
				setState(BluetoothMOSDevice.State.connect_interrupt);
				this.stopSocket();
			}
		}

		/**
		 * 停止socket链接
		 */
		private void stopSocket() {
			LogUtil.d(TAG, "-----SocketThread Stoped-----");
			try {
				if (mInStream != null) {
					mInStream.close();
					mInStream = null;
				}
				if (mOutStream != null) {
					mOutStream.close();
					mOutStream = null;
				}
				if (mSocket != null) {
					mSocket.close();
					mSocket = null;
				}
				isStopConnect = false;
				if (mPackageDealThread != null) {
					mPackageDealThread.stopThread();
					mPackageDealThread = null;
				}
				mSocketThread = null;
				mCurrCommand = null;
				if (mHeartbeatThread != null) {
					mHeartbeatThread.stopThread();
					mHeartbeatThread = null;
				}
				if (mState != BluetoothMOSDevice.State.connect_interrupt)
					mState = BluetoothMOSDevice.State.connect_broken;
			} catch (IOException e) {
				e.printStackTrace();
				LogUtil.e(TAG, e.getMessage(), e.fillInStackTrace());
			}
		}

		/**
		 * 写数据
		 * 
		 * @param buffer
		 *          数据内容
		 */
		public boolean write(byte[] buffer) {
			if (this.isStop || mOutStream == null)
				return false;
			try {
				mLastCommunicateTime = System.currentTimeMillis();
				mOutStream.write(buffer);
				return true;
			} catch (IOException e) {
				LogUtil.e(TAG, e.getMessage());
				return false;
			}
		}

		/**
		 * 停止线程
		 */
		public void stopThread() {
			LogUtil.d(TAG, "-----Stop SocketThread-----");
			isStopConnect = true;
			this.isStop = true;
			this.stopSocket();
		}
	}

	/**
	 * 数据包处理线程
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class PackageDealThread extends Thread {

		/** 是否停止线程 */
		protected boolean isStop = false;

		@Override
		public void run() {
			LogUtil.d(TAG, "-----PackageDealThread Start-----");
			while (!isStop) {
				if (mCurrCommand != null && !mPackageQueque.isEmpty()) {
					byte[] bytes = mPackageQueque.poll();
					if (bytes != null) {
						mLastCommunicateTime = System.currentTimeMillis();
						mCurrCommand.dealResponse(bytes);
					}
				}
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
				}
			}
			LogUtil.d(TAG, "-----PackageDealThread Stoped-----");
		}

		/**
		 * 停止线程
		 */
		public void stopThread() {
			LogUtil.d(TAG, "-----Stop PackageDealThread-----");
			this.isStop = true;
			this.interrupt();
		}
	}

	/**
	 * 连接当前设备
	 * 
	 * @param isCheckDevice
	 *          当前是否在检查当前的设备是否是插入到当前终端切硬件正常
	 */
	public void connect(boolean isCheckDevice) {
		while (isStopConnect) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LogUtil.d(TAG, "-----connect-----isCheckDevice:" + isCheckDevice + "-----");
		this.isCheckDevice = isCheckDevice;
		this.setState(State.connect_doing);
	}

	/**
	 * 断开当前连接
	 * 
	 * @param isWait
	 *          是否等待断开连接结束
	 */
	public void disconnect(boolean isWait) {
		if (this.mSocketThread == null)
			return;
		LogUtil.d(TAG, "-----disconnect-----device:" + this.getAddress() + "-----state:" + this.mState.getName());
		this.isRingTest = false;
		if (this.mState == State.connect_fail || this.mState == State.connect_interrupt) {
			this.isStopConnect = false;
			return;
		}
		this.isStopConnect = true;
		if (this.mCurrCommand != null) {
			this.interrupt();
		}
		this.uninitMOS();
		BaseCommand cmd = new DisconnectCommand(this);
		this.sendCommand(cmd);
		while (isWait && isStopConnect) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 重新连接
	 */
	private void reconnect() {
		LogUtil.d(TAG, "-----reconnect-----");
		this.isStopConnect = true;
		BaseCommand cmd = new DisconnectCommand(this);
		this.sendCommand(cmd);
		this.connect(false);
	}

	/**
	 * 写文件到MOS头去
	 * 
	 * @param filePath
	 *          文件路径
	 * @param fileType
	 *          文件类型
	 */
	public void writeFileTo(String filePath, FileType fileType) {
		if (this.mState != State.command_waiting)
			return;
		BaseCommand cmd = new WriteCommand(this, new File(filePath), fileType);
		this.sendCommand(cmd);
	}

	/**
	 * 发送命令
	 * 
	 * @param cmd
	 *          命令对象
	 * @return
	 */
	private boolean sendCommand(BaseCommand cmd) {
		switch (this.mState) {
		case devcie_ready:
		case command_doing:
		case command_waiting:
		case connect_doing:
		case connect_broken:
			LogUtil.d(TAG, "-----sendCommand:" + cmd.getType().getName() + ",state:" + this.mState.getName() + "-----");
			if (cmd.getType() == CommandType.disconnect) {
				if (this.mWaitCommands.size() > 0 && this.mWaitCommands.get(0).getType() == CommandType.uninit_mos) {
					this.mWaitCommands.add(1, cmd);
				} else {
					this.mWaitCommands.add(0, cmd);
				}
			} else if (cmd.getType() == CommandType.connect_check) {
				this.mWaitCommands.add(0, cmd);
			} else {
				this.mWaitCommands.add(cmd);
			}
			if (this.mState == State.command_waiting || this.mState == State.devcie_ready)
				this.runNextCommand();
			else if (this.mState == State.connect_broken) {
				this.connect(false);
			}
			return true;
		default:
			LogUtil.d(TAG, "-----sendCommand:" + cmd.getType().getName() + ",state:" + this.mState.getName() + "-----");
			return false;
		}

	}

	/**
	 * 发送消息
	 * 
	 * @param out
	 *          消息内容
	 */
	public boolean sendMsg(byte[] out) {
		if (this.mState != State.command_doing)
			return false;
		if (this.mSocketThread != null)
			return this.mSocketThread.write(out);
		return false;
	}

	/**
	 * 返回MOS头的MAC地址
	 * 
	 * @return
	 */
	public String getAddress() {
		return this.mMACAddress;
	}

	public double getPlaybackResult() {
		return mPlaybackResult;
	}

	public double getRecordResult() {
		return mRecordResult;
	}

	/**
	 * 返回MOS头的名称
	 * 
	 * @return
	 */
	public String getName() {
		return this.mName;
	}

	public double getRingResult() {
		return mRingResult;
	}

	/**
	 * 获取当前设备的参数值，包括放音音量和剩余电量
	 */
	public void catchDeviceParams() {
		LogUtil.d(TAG, "-----catchDeviceParams-----");
		GetParamsCommand cmd = new GetParamsCommand(this);
		this.sendCommand(cmd);
	}

	/**
	 * 获取当前设备的已有音频文件ID
	 */
	public void catchHaveFileIDs() {
		LogUtil.d(TAG, "-----catchHaveFileIDs-----");
		GetFileListCommand cmd = new GetFileListCommand(this);
		this.sendCommand(cmd);
	}

	/**
	 * 设置设备参数
	 * 
	 * @param playbackVolume
	 *          放音音量
	 * @param recordVolume
	 *          录音音量
	 */
	public void setDeviceParams(int playbackVolume, int recordVolume) {
		LogUtil.d(TAG, "-----setDeviceParams-----");
		SetParamsCommand cmd = new SetParamsCommand(this, playbackVolume, recordVolume);
		this.sendCommand(cmd);
	}

	public int getSignalLevel() {
		return mSignalLevel;
	}

	public void setSignalLevel(int signalLevel) {
		mSignalLevel = signalLevel;
	}

	public int getPower() {
		return mPower;
	}

	public int getPlaybackVolume() {
		return mPlaybackVolume;
	}

	@Override
	public boolean equals(Object o) {
		if (super.equals(o))
			return true;
		if (!(o instanceof BluetoothMOSDevice))
			return false;
		BluetoothMOSDevice device = (BluetoothMOSDevice) o;
		return this.mMACAddress.equals(device.mMACAddress);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.mName);
		dest.writeString(this.mFileType.getName());
		dest.writeInt(this.mSignalLevel);
		dest.writeString(this.mMACAddress);
		dest.writeInt(this.mDeviceType);
	}

	/**
	 * 必须提供一个名为CREATOR的static final属性 该属性需要实现android.os.Parcelable.Creator<T>接口
	 */
	public static final Parcelable.Creator<BluetoothMOSDevice> CREATOR = new Parcelable.Creator<BluetoothMOSDevice>() {

		@Override
		public BluetoothMOSDevice createFromParcel(Parcel in) {
			return new BluetoothMOSDevice(in);
		}

		@Override
		public BluetoothMOSDevice[] newArray(int size) {
			return new BluetoothMOSDevice[size];
		}
	};

	public void setPlaybackResult(double playbackResult) {
		mPlaybackResult = playbackResult;
	}

	public void setRecordResult(double recordResult) {
		mRecordResult = recordResult;
	}

	public void setRingResult(double ringResult) {
		mRingResult = ringResult;
	}

	public void setPower(int power) {
		mPower = power;
	}

	public void setPlaybackVolume(int playbackVolume) {
		mPlaybackVolume = playbackVolume;
	}

	public void setService(BluetoothMOSService service) {
		mService = service;
	}

	public void setMTCService(BluetoothMTCService MTCService){
		mMTCService = MTCService;
	}

	public BluetoothMOSService getService() {
		return mService;
	}
	public BluetoothMTCService getMTCService() {
		return mMTCService;
	}

	/**
	 * 生成要保存的文件
	 * 
	 * @param filePrefix
	 *          文件名前缀
	 * @param fileSuffix
	 *          文件名后缀
	 */
	public String createRecordFile(String filePrefix, String fileSuffix) {
		if (!StringUtil.isNullOrEmpty(this.mFileNameExtends) && !StringUtil.isNullOrEmpty(this.mRCUFileName)){
			if(mDeviceType == DEVICE_TYPE_MOC){
				return BluetoothMOSFactory.createRecordFile(mService, this.mRCUFileName, this.mFileNameExtends,
						MyPhoneState.getInstance().getMyDeviceId(this.mService), fileSuffix);
			}else{
				return BluetoothMOSFactory.createRecordFile(mMTCService, this.mRCUFileName, this.mFileNameExtends,
						MyPhoneState.getInstance().getMyDeviceId(this.mMTCService), fileSuffix);
			}
		}
		if(mDeviceType == DEVICE_TYPE_MOC){
			return BluetoothMOSFactory.createRecordFile(mService, filePrefix, fileSuffix);
		}else{
			return BluetoothMOSFactory.createRecordFile(mMTCService, filePrefix, fileSuffix);
		}

	}

	public int getRecordVolume() {
		return mRecordVolume;
	}

	public void setRecordVolume(int recordVolume) {
		mRecordVolume = recordVolume;
	}

	public String getVersion() {
		return mVersion;
	}

	public void setVersion(String version) {
		mVersion = version;
	}

	public void setFileIDs(String fileIDs) {
		mFileIDs = fileIDs;
	}

	public String getFileIDs() {
		return mFileIDs;
	}

	/**
	 * 心跳线程，空闲状态下每5秒发送一次查询指令
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class HeartbeatThread extends Thread {
		/** 空闲间隔时间 */
		private static final int TIME_INTERVAL = 5 * 1000;
		/** 是否停止线程 */
		private boolean isStop = false;

		@Override
		public void run() {
			mLastCommunicateTime = System.currentTimeMillis();
			while (!isStop) {
				if (mState == BluetoothMOSDevice.State.command_waiting) {
					if (System.currentTimeMillis() - mLastCommunicateTime > TIME_INTERVAL) {
						catchDeviceParams();
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			LogUtil.d(TAG, "-----HeartbeatThread Stoped-----");
		}

		/**
		 * 停止当前线程
		 */
		private void stopThread() {
			LogUtil.d(TAG, "-----Stop HeartbeatThread-----");
			this.isStop = true;
			this.interrupt();
		}
	}

	public void setFileNameExtends(String mFileNameExtends) {
		this.mFileNameExtends = mFileNameExtends;
	}

	public void setRCUFileName(String rCUFileName) {
		mRCUFileName = rCUFileName;
	}

    @Override
    public String toString() {
        return getAddress();
    }
}
