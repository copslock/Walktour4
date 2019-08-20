package com.voix;

import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;

import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * 录音功能
 * 
 * @author jianchao.wang
 *
 */
public class VoiceRecorder {
	/** 日志标识 */
	private static final String TAG = "VoiceRecorder";
	/** 录音类 */
	private BaseRecorder mRecorder = null;
	/** 是否已初始化 */
	private boolean mInit = false;

	/**
	 * 初始化
	 * 
	 * @param recordFile
	 *          录音存放文件
	 * @param audioFormat
	 *          音频格式
	 * @param sampleRate
	 *          采样频率
	 * @param audioSource
	 *          音源
	 * @param channels
	 *          声道数
	 * @param samplebits
	 *          采样位数
	 * @param useAPI
	 *          是否启用API
	 * @return
	 */
	public boolean init(File recordFile, int audioFormat, int sampleRate, int audioSource, int channels, int samplebits,
			boolean useAPI) {
		if (!useAPI) {
			if (!checkSetDevicePermissions()) {
				useAPI = true;
				LogUtil.w(TAG, "devicePermission check fail, use standard api");
			}
		}
		if (useAPI) {
			// 不同手机用了不同的链路
			mRecorder = new APIRecorder(recordFile, audioFormat, audioSource, sampleRate, channels, samplebits);
		} else {
			mRecorder = new RVoixSrv(recordFile, audioFormat);
		}
		this.mInit = this.mRecorder.init();
		return this.mInit;
	}

	/**
	 * 开始录音
	 */
	public void startRecord() {
		if (!mInit)
			return;
		LogUtil.d(TAG, "---------startRecord-----------");
		try {
			mRecorder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止录音
	 */
	public void stopRecord() {
		LogUtil.d(TAG, "---------stopRecord-----------");
		if (mRecorder != null) {
			mRecorder.stop();
		}
	}

	public boolean isRecording() {
		return mRecorder.isRecording();
	}

	/**
	 * 根据模版中的选择返回音频文件的采样率 样本文件类型:0 NB 8k;1 WB 16k;2 SWB 48k;
	 * 
	 * @return
	 */
	public static int getSampleRateByCheck(int check) {
		switch (check) {
		case 2:
			return 48000;
		case 1:
			return 16000;
		default:
			return 8000;
		}
	}

	public static final boolean checkSetDevicePermissions() {

		final String std_devz[] = { "/dev/msm_pcm_in", "/dev/msm_amrnb_in", "/dev/msm_amr_in", "/dev/msm_audio_dev_ctrl" };
		ArrayList<String> cmds = new ArrayList<String>();
		File f = new File(std_devz[0]);

		if (!f.exists())
			return false;
		if (f.canRead())
			return true;

		for (String s : std_devz) {
			File ff = new File(s);
			if (ff.exists()) {
				if (!ff.canRead())
					cmds.add("chmod 0664 " + s + "\n");
			}
		}
		if (cmds.isEmpty()) {
			return true;
		}
		if (!sudo(cmds))
			return false;
		return f.canRead();
	}

	private static boolean sudo(ArrayList<String> cmds) {
		java.lang.Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec(Deviceinfo.getInstance().getSuOrShCommand());
			os = new DataOutputStream(process.getOutputStream());
			os.flush();
			for (int i = 0; i < cmds.size(); i++) {
				os.writeBytes(cmds.get(i));
				os.flush();
			}
			os.writeBytes("exit\n");
			os.flush();
			os.close();
			os = null;
			process.waitFor();
		} catch (Exception e) {
			LogUtil.w(TAG, "exception in sudo()");
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (os != null)
					os.close();
				if (process != null)
					process.destroy();
			} catch (Exception e) {
				LogUtil.w(TAG, "exception on exit of sudo()");
				e.printStackTrace();
			}
		}
		return true;
	}

}
