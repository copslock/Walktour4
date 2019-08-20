package com.voix;

import android.media.AudioFormat;
import android.media.AudioRecord;

import com.walktour.base.util.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 调用系统API录音功能
 * 
 * @author jianchao.wang
 *
 */
public class APIRecorder extends BaseRecorder {
	/** 日志标识 */
	private final static String TAG = "APIRecorder";
	/** 语音录音 */
	private AudioRecord mAudioRecorder;

	/**
	 * API录音
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
	public APIRecorder(File recordFile, int audioFormat, int audioSource, int sampleRate, int channels, int samplebits) {
		super(recordFile, audioFormat, audioSource, sampleRate, channels, samplebits);
	}

	/**
	 * 获取wave文件的头
	 * 
	 * @return
	 */
	public byte[] getWaveFileHeaderBuf() {
		long byteRate = 16 * super.mSampleRate * super.mChannels / 8;

		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = 0x00;
		header[5] = 0x00;
		header[6] = 0x00;
		header[7] = 0x00;
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) super.mChannels;
		header[23] = 0;
		header[24] = (byte) (super.mSampleRate & 0xff);
		header[25] = (byte) ((super.mSampleRate >> 8) & 0xff);
		header[26] = (byte) ((super.mSampleRate >> 16) & 0xff);
		header[27] = (byte) ((super.mSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = 0x02; // (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = 16; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = 0x00;
		header[41] = 0x00;
		header[42] = 0x00;
		header[43] = 0x00;

		return header;
	}

	@Override
	public void runRecord() {
		/*
		 * int bitsFormat = AudioFormat.ENCODING_PCM_16BIT; if (mSampleBits == 8)
		 * bitsFormat = AudioFormat.ENCODING_PCM_8BIT; int channelFormat =
		 * AudioFormat.CHANNEL_IN_MONO; if (mChannels == 2) channelFormat =
		 * AudioFormat.CHANNEL_IN_STEREO;
		 */
		try {
			mAudioRecorder.startRecording();
			int audioRecordingState = mAudioRecorder.getRecordingState();
			if (audioRecordingState != AudioRecord.RECORDSTATE_RECORDING) {
				LogUtil.w(TAG, "AudioRecord is not recording");
				return;
			}
			isRecording = true;
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.w(TAG, "start record failure,illegalstate exception");
			return;
		}

		int read = 0;
		byte[] arrayOfByte1 = new byte[2560];
		FileOutputStream fileOutput = null;

		// 先写入文件头
		try {
			fileOutput = new FileOutputStream(super.mRecordFile);
			fileOutput.write(getWaveFileHeaderBuf());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int totalread = 0;
		if (fileOutput == null) {
			LogUtil.w(TAG, "create output file failure!");
			return;
		}

		// 录音时写入音频数据
		while (!super.isStop) {
			try {
				read = mAudioRecorder.read(arrayOfByte1, 0, 320);
				fileOutput.write(arrayOfByte1, 0, read);
				totalread += read;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// release函数已经包括了stop
		mAudioRecorder.release();
		mAudioRecorder = null;
		// 修改文件头
		try {
			int data_sz = totalread;
			if (data_sz == 0) {
				LogUtil.w(TAG, "invalid size");
			}

			int riff_sz = data_sz + 36;
			byte[] buffer = new byte[4];
			buffer[0] = (byte) (riff_sz & 0xff);
			buffer[1] = (byte) ((riff_sz >> 8) & 0xff);
			buffer[2] = (byte) ((riff_sz >> 16) & 0xff);
			buffer[3] = (byte) ((riff_sz >> 24) & 0xff);

			fileOutput.getChannel().position(4);
			fileOutput.write(buffer, 0, 4);

			buffer[0] = (byte) (data_sz & 0xff);
			buffer[1] = (byte) ((data_sz >> 8) & 0xff);
			buffer[2] = (byte) ((data_sz >> 16) & 0xff);
			buffer[3] = (byte) ((data_sz >> 24) & 0xff);
			fileOutput.getChannel().position(40);
			fileOutput.write(buffer, 0, 4);

			fileOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		isRecording = false;
		LogUtil.w(TAG, "Record Stop");
	}

	@Override
	public boolean init() {
		int min = AudioRecord.getMinBufferSize(super.mSampleRate, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		LogUtil.w(TAG, "min:" + min);

		int frameSize = min;// Math.max(2560, min) * 4;
		try {
			// 有些手机用的是VOICE_DOWNLINK
			mAudioRecorder = new AudioRecord(super.mAudioSource, super.mSampleRate, AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, frameSize);
			if (mAudioRecorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
				LogUtil.w(TAG, "init Audio record failure,record device has not prepared");
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e.getMessage(), e.fillInStackTrace());
			return false;
		}
		return true;
	}
}
