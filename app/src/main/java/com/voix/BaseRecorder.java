package com.voix;

import android.media.MediaRecorder;

import java.io.File;

/**
 * 录音接口类
 * 
 * @author jianchao.wang
 *
 */
public abstract class BaseRecorder {
	/** 当前是否正在录音 */
	protected boolean isRecording = false;
	/** 声音来源 */
	protected int mAudioSource = MediaRecorder.AudioSource.VOICE_CALL;
	/** 采样率 */
	protected int mSampleRate = 8000;
	/** 声道数 */
	protected int mChannels = 1;
	/** 采样位数 */
	protected int mSampleBits = 16;
	/** 录音存放文件 */
	protected File mRecordFile;
	/** 音频格式 */
	protected int mAudioFormat;
	/** 当前录音线程 */
	private RecordThread mRecordThread;
	/** 是否停止录音 */
	protected boolean isStop = false;

	/**
	 * 
	 * @param recordFile
	 *          录音存放文件
	 * @param audioFormat
	 *          音频格式
	 */
	public BaseRecorder(File recordFile, int audioFormat) {
		this.mRecordFile = recordFile;
		this.mAudioFormat = audioFormat;
	}

	/**
	 * 
	 * 
	 * @param recordFile
	 *          录音存放文件
	 * @param audioFormat
	 *          音频格式
	 * @param audioSource
	 *          音源
	 * @param sampleRate
	 *          采样频率
	 * @param channels
	 *          声道数
	 * @param samplebits
	 *          采样位数
	 */
	public BaseRecorder(File recordFile, int audioFormat, int audioSource, int sampleRate, int channels, int samplebits) {
		this(recordFile, audioFormat);
		this.mAudioSource = audioSource;
		this.mSampleRate = sampleRate;
		this.mChannels = channels;
		this.mSampleBits = samplebits;
	}

	/**
	 * 开始录音
	 * 
	 */
	public void start() {
		this.mRecordThread = new RecordThread();
		this.mRecordThread.start();
	}

	/**
	 * 停止录音
	 */
	public void stop() {
		this.isStop = true;
		this.isRecording = false;
	}

	/**
	 * 是否正在录音
	 * 
	 * @return
	 */
	public boolean isRecording() {
		return this.isRecording;
	}

	/**
	 * 执行录音
	 */
	public abstract void runRecord();

	/**
	 * 录音执行线程
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class RecordThread extends Thread {

		@Override
		public void run() {
			isRecording = true;
			runRecord();
			isRecording = false;
		}

	}
	
	/**
	 * 初始化设备
	 */
	public abstract boolean init();

}
