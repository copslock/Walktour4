package com.walktour.service.bluetoothmos.command;

import android.app.Service;
import android.media.AudioManager;
import android.media.MediaRecorder;

import com.voix.RVoixSrv;
import com.voix.VoiceRecorder;
import com.walktour.base.util.LogUtil;
import com.walktour.service.bluetoothmos.BluetoothMOSFactory;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

import java.io.File;

/**
 * 执行mos头放音命令
 * 
 * @author jianchao.wang
 *
 */
public class PlaybackCommand extends BaseCommand {

	/** 播放文件类型 */
	private final FileType mFileType;
	/** 是否在mos头放音的过程中，终端执行录音操作 */
	private boolean isRecord = false;
	/** 录音执行线程 */
	private RecordThread mRecordThread;
	/** 读取到的录音文件 */
	private String mRecordFile;

	public PlaybackCommand(BluetoothMOSDevice device, FileType fileType, boolean isRecord) {
		super(CommandType.playback, device);
		this.mFileType = fileType;
		this.isRecord = isRecord;
		super.mTimeout = (fileType.getRecordTime() + RESPONSE_TIMEOUT) * 1000;
	}

	@Override
	protected void dealRespACK(byte[] ack) {
		super.resolveRespACK(ack);
		if (super.isACKOK) {
			if (super.mACKType.equals(ACK_TYPE_DATA)) {
				if (this.isRecord) {
					this.mRecordThread.stopThread();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				super.finish();
			} else if (this.isRecord) {
				this.mRecordThread = new RecordThread();
				this.mRecordThread.start();
			}
		} else {
			super.finish();
		}
	}

	@Override
	protected void dealRespData(byte[] data) {
		// 无需实现

	}

	/**
	 * 初始化音量管理
	 */
	private void initVoice() {
		// 音频管理器
		AudioManager audioManager = null;
		if(super.mDevice.getDeviceType() == BluetoothMOSDevice.DEVICE_TYPE_MOC){
			audioManager = (AudioManager) super.mDevice.getService().getSystemService(Service.AUDIO_SERVICE);
		}else {
			audioManager = (AudioManager) super.mDevice.getMTCService().getSystemService(Service.AUDIO_SERVICE);
		}
		// 调节媒体播放器的音量至5(实验证明此时音质最好)
		audioManager.setMode(AudioManager.MODE_NORMAL);
		audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
				audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FLAG_SHOW_UI);
		audioManager.setSpeakerphoneOn(false);
//		audioManager.setMicrophoneMute(false);
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
	public void runCommand() {
		if (this.isRecord) {
			this.initVoice();
			this.mRecordFile = super.mDevice.createRecordFile("playback", "wav");
		}
		byte[] cmd = super.createRequestCmd(this.mFileType.getName().getBytes());
		super.sendRequestCmd(cmd);
	}

	/**
	 * 手机录音线程,一个线程只录音一次
	 */
	private class RecordThread extends Thread {
		/** 是否暂停 */
		private boolean isStop = false;

		@Override
		public void run() {
			VoiceRecorder recorder = new VoiceRecorder();
			String fileDir = BluetoothMOSFactory.getStorgePath(mDevice.getDeviceType() == BluetoothMOSDevice.DEVICE_TYPE_MOC? mDevice.getService():mDevice.getMTCService());
			if (fileDir != null) {
				LogUtil.d(mTag, "-----start mos recorder-----");
				LogUtil.d(mTag, "----file:" + mRecordFile + "----");
				int sampleRate = mFileType.getSampleRate() * 1000;
				LogUtil.d(mTag, "----sampleRate:" + sampleRate + "----");
				recorder.init(new File(mRecordFile), RVoixSrv.MODE_RECORD_WAV, sampleRate, MediaRecorder.AudioSource.MIC, 1, 16,
						true);
				recorder.startRecord();
				while (!isStop) {
					// 录音计时
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
				}
				recorder.stopRecord();
				muteSystemOrNotification(false);
				LogUtil.d(mTag, ">>>---stop mos recorder");
			}
		}

		public void stopThread() {
			this.isStop = true;
			this.interrupt();
		}
	}

	public String getRecordFile() {
		return this.mRecordFile;
	}

}
