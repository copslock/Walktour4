package com.voix;

import com.walktour.base.util.LogUtil;

import java.io.File;

/**
 * 调用录音库
 * 
 * @author jianchao.wang
 *
 */
public class RVoixSrv extends BaseRecorder {
	/** 日志标识 */
	private static final String TAG = "RVoixSrv";
	/** 补丁版本 */
	private static int patchVersion = 0;
	/** 应用版本 */
	private static int procVersion = 0;
	/** 句柄 */
	private int mHandle = 0;
	/** 音频格式：wav */
	public static final int MODE_RECORD_WAV = 0;
	/** 音频格式:mp3 */
	public static final int MODE_RECORD_MP3 = 1;
	/** 音频格式:amr */
	public static final int MODE_RECORD_AMR = 2;

	static {
		System.loadLibrary("lame");
		System.loadLibrary("voix");
	}

	public RVoixSrv(File recordFile, int audioFormat) {
		super(recordFile, audioFormat);
		int devtype = getDeviceType();
		LogUtil.w(TAG, "device type = " + devtype);
		int i = getKernelPatchInfo();
		procVersion = i >> 16;
		patchVersion = 0xFFFF & i;
	}

	public static native int startRecord(String dir, String file, int codec, int boost_up, int boost_down);

	public static native void stopRecord(int context);

	public static native void killRecord(int context);

	public static native void answerCall(String file, String ofile, int bd);

	public static native int getDeviceType();

	public static native int convertToMp3(String s1, String s2);

	public static native int getKernelPatchInfo();

	@Override
	public void runRecord() {
		this.mHandle = startRecord(super.mRecordFile.getParentFile().getAbsolutePath(), super.mRecordFile.getName(),
				super.mAudioFormat, 0, 0);
		if (this.mHandle == 0) {
			LogUtil.w(TAG, "Failed to start recording at user request,StartRecord failed");
			return;
		}
	}

	@Override
	public void stop() {
		super.stop();
		stopRecord(this.mHandle);
	}

	// Interaction with native code: it calls us.
	public static void onCallAnswered() {
		LogUtil.d(TAG, "onCallAnswered");
	}

	public static void onStartRecording(int xx) {
		LogUtil.d(TAG, "onRecordingStarted(" + xx + ")");
	}

	public static void onRecordingComplete(int xx) {
		LogUtil.d(TAG, "onRecordingComplete(" + xx + ")");
	}

	public static void onErrorRecording(int xx, int err) {
		LogUtil.d(TAG, "onErrorRecording(" + xx + "," + err + ")");
	}

	public static void onAutoanswerRecordingStarted() {
		LogUtil.d(TAG, "onAutoanswerRecordingStarted()");
	}

	public static void onStartEncoding(int xx) {
		LogUtil.d(TAG, "onEncodingStarted(" + xx + ")");
	}

	public static void onErrorEncoding(int xx, int err) {
		LogUtil.d(TAG, "onErrorRecording(" + xx + "," + err + ")");
	}

//	public static void onEncodingComplete(int xx) {
//		LogUtil.d(TAG, "onEncodingComplete");
//	}

	@Override
	public boolean init() {
		return true;
	}

}
