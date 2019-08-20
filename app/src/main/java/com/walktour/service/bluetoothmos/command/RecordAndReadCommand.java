package com.walktour.service.bluetoothmos.command;

import android.app.Service;
import android.media.AudioManager;

import com.walktour.Utils.MyAudioPlayer;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 录传同测命令
 * 
 * @author jianchao.wang
 *
 */
public class RecordAndReadCommand extends BaseCommand {
	/** 传输数据结束标识字节 */
	private final static byte[] sEndFlag = hexStringToBytes("E9FFFFFFEA");
	/** 保存音频有丢失的文件目录 */
	public static final String MOS_ERROR_DIR = "Voice-Error";
	/** 读取到的录音文件 */
	private String mRecordFile;
	/** 内存输出流 */
	private ByteArrayOutputStream mByteOut = null;
	/** 当前接收的文件长度 */
	private float mFileCurrSize = 0;
	/** 文件类型 */
	private final FileType mFileType;
	/** 是否在MOS头进行录音的同时，终端进行放音 */
	private final boolean isPlayback;
	/** 播放器 */
	private MyAudioPlayer mPlayer = null;
	/** 测速线程 */
	private SpeedTestThread mSpeedTestThread = null;
	/** 放音线程 */
	private PlaybackThread mPlaybackThread = null;
	/** 是否正在转换音频文件 */
	private boolean isConvertAudioFile = false;
	/** 每毫秒数据生成大小 */
	private long mSecondDataSize = 0;
	/** 开始录音时间 */
	private long mStartRecordTime = 0;
	/** 是否因为蓝牙传输速率过低导致数据文件丢失 */
	private boolean isCatchError = false;
	/** 蓝牙头内部缓存大小，当前的录音机制是当内部缓存写满后将丢弃后面的数据，通过当前的接收到的数据大小来判断是否缓存已被写满 */
	private long mMOSCache = 170 * 1000;

	public RecordAndReadCommand(BluetoothMOSDevice device, FileType fileType, boolean isPlayback) {
		super(CommandType.record_and_read, device);
		this.isPlayback = isPlayback;
		this.mFileType = fileType;
		this.mSecondDataSize = this.mFileType.getSampleRate() * 2;
		this.init();
	}

	/**
	 * 设置音量大小
	 */
	private void setVoice() {
		int mediaVoice = Deviceinfo.getInstance().getMediaVoice();
		LogUtil.d(mTag, "mediaVoice = " + mediaVoice);
		// 音频管理器
		AudioManager audioManager = null;
		if(super.mDevice.getDeviceType() == BluetoothMOSDevice.DEVICE_TYPE_MOC){
			audioManager = (AudioManager) super.mDevice.getService().getSystemService(Service.AUDIO_SERVICE);
		}else{
			audioManager = (AudioManager) super.mDevice.getMTCService().getSystemService(Service.AUDIO_SERVICE);
		}
		int maxValue = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		LogUtil.d(mTag, "maxValue = " + maxValue);
		audioManager.setSpeakerphoneOn(false);
		mediaVoice = maxValue;
		audioManager.setMode(AudioManager.MODE_NORMAL);
//		audioManager.setMicrophoneMute(true);
		// 调节媒体播放器的音量至5(实验证明此时音质最好)
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mediaVoice, AudioManager.FLAG_SHOW_UI);
		this.muteSystemOrNotification(true);
	}

	/**
	 * 关闭或开启系统音或提示音
	 * 
	 * @param isMute
	 *          是否关闭
	 */
	private void muteSystemOrNotification(boolean isMute) {
		AudioManager audioManager = null;
		if(super.mDevice.getDeviceType() == BluetoothMOSDevice.DEVICE_TYPE_MOC){
			audioManager = (AudioManager) super.mDevice.getService().getSystemService(Service.AUDIO_SERVICE);
		}else{
			audioManager = (AudioManager) super.mDevice.getMTCService().getSystemService(Service.AUDIO_SERVICE);
		}
		audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, isMute);
		audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, isMute);
	}

	@Override
	protected void init() {
		super.init();
		this.mFileCurrSize = 0;
		if (this.mByteOut == null) {
			try {
				this.mByteOut = new ByteArrayOutputStream();
			} catch (Exception e) {
				LogUtil.e(mTag, e.getMessage(), e.fillInStackTrace());
			}
		}
		super.isResponseACK = true;
		if (this.isPlayback) {
			try {
				this.setVoice();
				this.mPlayer = new MyAudioPlayer(super.mDevice.getDeviceType() == BluetoothMOSDevice.DEVICE_TYPE_MOC? super.mDevice.getService():super.mDevice.getMTCService(), mFileType.getRawId());
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.d(mTag, e.getMessage(), e.fillInStackTrace());
			}
		}
	}

	@Override
	protected void dealRespACK(byte[] ack) {
		super.resolveRespACK(ack);
		if (super.mACKType.equals(this.mType.getType()) && super.isACKOK) {
			this.mRecordFile = super.mDevice.createRecordFile("record", "pcm");
			super.isResponseACK = false;
			this.mStartRecordTime = System.currentTimeMillis();
			if (this.isPlayback) {
				if (this.mPlaybackThread == null)
					this.mPlaybackThread = new PlaybackThread();
				this.mPlaybackThread.start();
			}
			if (this.mSpeedTestThread == null)
				this.mSpeedTestThread = new SpeedTestThread();
			this.mSpeedTestThread.start();
		}
	}

	@Override
	protected void dealRespData(byte[] resp) {
		if (this.mFileCurrSize == 0) {
			LogUtil.d(mTag, "----start get file:" + this.mRecordFile + "----");
		}
		try {
			this.mFileCurrSize += resp.length;
			this.checkTranslateError();
			if (!this.checkFinish(resp)) {
				this.mByteOut.write(resp);
			} else {
				this.mByteOut.write(resp, 0, resp.length - sEndFlag.length);
			}
			if (this.checkFinish(resp)) {
				this.finish();
			}
		} catch (Exception e) {
			LogUtil.e(mTag, e.getMessage(), e.fillInStackTrace());
			try {
				if (mByteOut != null)
					mByteOut.close();
			} catch (IOException e1) {
				LogUtil.e(mTag, e1.getMessage());
			}
		}
	}

	/**
	 * 判断当前传输速率是否过低导致数据丢失
	 */
	private void checkTranslateError() {
		long time = System.currentTimeMillis() - this.mStartRecordTime;
		long realSize = this.mSecondDataSize * time;
		if (realSize - this.mFileCurrSize >= this.mMOSCache) {
			this.isCatchError = true;
		}
	}

	@Override
	protected void finish() {
		FileOutputStream fileOS = null;
		try {
			if (!isCatchError && !super.isTimeOutInterrupt() && !super.isForceInterrupt()) {
				fileOS = new FileOutputStream(this.mRecordFile);
				this.mByteOut.writeTo(fileOS);
				this.mByteOut.flush();
			}
		} catch (IOException e) {
			LogUtil.d(mTag, e.getMessage());
		}
		try {
			if (this.mByteOut != null) {
				this.mByteOut.close();
				this.mByteOut = null;
			}
			if (fileOS != null) {
				fileOS.close();
				fileOS = null;
			}
		} catch (IOException e) {
			LogUtil.e(mTag, e.getMessage(), e.fillInStackTrace());
		}
		if (this.mSpeedTestThread != null) {
			this.mSpeedTestThread.stopThread();
			this.mSpeedTestThread = null;
		}
		if (this.mPlaybackThread != null) {
			this.mPlaybackThread.stopThread();
			this.mPlaybackThread = null;
		}
		LogUtil.d(mTag, "----end get file:" + this.mRecordFile + "----");
		if (!isCatchError && !super.isTimeOutInterrupt() && !super.isForceInterrupt()) {
			this.isConvertAudioFile = true;
			this.mRecordFile = super.convertAudioFile(this.mRecordFile, this.mFileType.getSampleRate());
			this.isConvertAudioFile = false;
		} else if (!StringUtil.isNullOrEmpty(this.mRecordFile)) {
			File file = new File(this.mRecordFile);
			if (file.exists()) {
				if (isCatchError)
					this.copyErrorValueFile(file);
				else
					file.delete();
			}
			this.mRecordFile = "error";
		}
		super.finish();
	}

	/**
	 * 复制低速率的文件
	 * 
	 * @param fileSource
	 *          文件
	 */
	private void copyErrorValueFile(File fileSource) {
		LogUtil.d(mTag, "-----copyErrorValueFile-----file:" + fileSource.getAbsolutePath());
		String errorDir = AppFilePathUtil.getInstance().getSDCardBaseDirectory(MOS_ERROR_DIR);
		File dir = new File(errorDir);
		String file = errorDir + File.separator + fileSource.getName();
		File fileDes = new File(file);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (fileSource.exists() && fileSource.isFile()) {
			try {
				UtilsMethod.copyFile(fileSource, fileDes);
				fileSource.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 判断当前是否完成了数据传输
	 * 
	 * @return
	 */
	private boolean checkFinish(byte[] resp) {
		if (resp.length < sEndFlag.length)
			return false;
		for (int i = 0; i < sEndFlag.length; i++) {
			if (resp[resp.length - sEndFlag.length + i] != sEndFlag[i])
				return false;
		}
		return true;
	}

	/**
	 * 手机放音线程,一个线程只放音一次
	 */
	private class PlaybackThread extends Thread {

		@Override
		public void run() {
			LogUtil.d(mTag, "start play audio file");
			// 播放
			if (mPlayer != null)
				mPlayer.startPlayer();
			// 播放计时
			try {
				Thread.sleep(mFileType.getRecordTime() * 1000);
			} catch (InterruptedException e) {
			}
			// 停止播放
			LogUtil.d(mTag, "stop play audio file");
			if (mPlayer != null) {
				mPlayer.stopPlayer();
				muteSystemOrNotification(false);
			}
		}

		/**
		 * 停止当前线程
		 */
		private void stopThread() {
			this.interrupt();
		}

	}

	@Override
	public void runCommand() {
		byte[] msg = new byte[2];
		msg[0] = hexIntToBytes(this.mFileType.getSampleRate(), 1)[0];
		msg[1] = hexIntToBytes(this.mFileType.getRecordTime(), 1)[0];
		byte[] cmd = super.createRequestCmd(msg);
		super.sendRequestCmd(cmd);
	}

	public String getRecordFile() {
		return this.mRecordFile;
	}

	/**
	 * 传输速度测速线程
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class SpeedTestThread extends Thread {
		/** 计算速度的间隔时间（毫秒） */
		private final static long COUNT_TIME_INTERVAL = 500;
		/** 开始计算时间 */
		private long mStartTime = 0;
		/** 最大速度 */
		private float mMaxSpeed = 0;
		/** 最小速度 */
		private float mMinSpeed = 0;
		/** 平均速度 */
		private float mAvgSpeed = 0;
		/** 上次计算速度时的获取总大小 */
		private float mFileCountSize = 0;
		/** 中断测速线程 */
		private boolean isStop = false;

		@Override
		public void run() {
			this.mStartTime = System.currentTimeMillis();
			while (!isStop) {
				try {
					Thread.sleep(COUNT_TIME_INTERVAL);
				} catch (InterruptedException e) {
				}
				this.countSpeed();
			}
		}

		/**
		 * 计算发送速度
		 * 
		 * @return
		 */
		private void countSpeed() {
			float speed = (mFileCurrSize - this.mFileCountSize) / COUNT_TIME_INTERVAL / 1000 * 1000;
			speed = new BigDecimal(speed).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (speed > this.mMaxSpeed)
				this.mMaxSpeed = speed;
			if (this.mMinSpeed == 0 || speed < this.mMinSpeed)
				this.mMinSpeed = speed;
			long time = System.currentTimeMillis() - this.mStartTime;
			mAvgSpeed = mFileCurrSize / time / 1000 * 1000;
			mAvgSpeed = new BigDecimal(mAvgSpeed).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (speed < 76)
				LogUtil.d(mTag, "↓speed(kbps): " + speed + "(now) " + this.mMaxSpeed + "(max) " + this.mMinSpeed + "(min) "
						+ mAvgSpeed + "(avg)");
			this.mFileCountSize = mFileCurrSize;
		}

		/**
		 * 停止当前线程
		 */
		private void stopThread() {
			this.isStop = true;
			this.interrupt();
		}
	}

	@Override
	public void interrupt(boolean isSendCmd) {
		if (isSendCmd && !this.isConvertAudioFile)
			super.interrupt(true);
		else {
			if (this.mSpeedTestThread != null) {
				this.mSpeedTestThread.stopThread();
				this.mSpeedTestThread = null;
			}
			super.interrupt(false);
		}
	}

}
