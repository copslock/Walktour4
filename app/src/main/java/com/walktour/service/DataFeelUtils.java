package com.walktour.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.service.test.IPesqCalculator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 由于算分的库不稳定，所以放在个这个独立进程的服务里进行
 */
public class DataFeelUtils extends Service {
	private static final String tag = "DataFeelUtils";

	// public static final String DegFilePath = "datafeelutils.degfilepath";
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
	private File preferFile ; // 参考文件路径
	// private boolean isInitialhandle = false;
	private int handle;

	static {
		System.loadLibrary("pesqso");
		// System.loadLibrary("p563so");
	}

	/**
	 * 初始化
	 *
	 * @param type
	 *          1
	 * @param pRefName
	 *          参考文件的文件名
	 * @param degSample
	 *          衰减文件的大小
	 * @return
	 */
	public native int mainit(int type, String pRefName, long degSample);

	/**
	 * 开始计算
	 *
	 * @param handle
	 *          句柄
	 * @param type
	 *          1
	 * @param pDegName
	 *          衰减文件名
	 * @param LV_Model
	 *          0
	 */
	public native String mastart(int handle, int type, String pDegName, long LV_Model);

	/**
	 * 释放句柄
	 *
	 * @param handle
	 *          句柄
	 */
	public native void mauninit(int handle);

	/**
	 *
	 * @param path
	 */
	public native String mastartp563(String path);

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final IPesqCalculator.Stub mBinder = new IPesqCalculator.Stub() {
		@Override
		public Map<String, Object> calculate(int rawId, String filePath) throws RemoteException {
			HashMap<String, Object> map = calculatePESQ(rawId, filePath);
			return map;
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.w(tag, "-----DataFeelUtils onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			// twq20150211 在4.2以前的版本中，调用当前方法有崩掉。修平说这个方法本身也没有释放要释放的东西，所以屏了。
			// mauninit(handle);

			// 将初始化句柄状态置为falses
			// isInitialhandle = false;
			// 此命令只会杀死符合本用户号的进程
			android.os.Process.sendSignal(android.os.Process.myPid(), android.os.Process.SIGNAL_KILL);
		} catch (Exception e) {

		}
	}

	/**
	 * 生成样本文件
	 */
	@SuppressLint("SdCardPath")
	private void createPreferFile(int rawId) {
		String fileName = this.getResources().getResourceName(rawId);
		if(fileName.contains("/")){
			fileName = fileName.substring(fileName.lastIndexOf("/")+1);
		}
		preferFile = AppFilePathUtil.getInstance().getSDCardBaseFile("mos",fileName + ".wav");
		LogUtil.d(tag, "-----preferFile=" + preferFile);
		if (!preferFile.exists())
			UtilsMethod.writeRawResource(DataFeelUtils.this, rawId, preferFile);
	}

	/**
	 * 计算分值
	 *
	 * @param rawId
	 *          资源文件ID
	 * @param filePath
	 *          目标文件
	 * @return
	 */
	private HashMap<String, Object> calculatePESQ(int rawId, String filePath) {
		LogUtil.d(tag,"----calculatePESQ,rawId: " + rawId + " , filePath:" + filePath + "----");
		this.createPreferFile(rawId);
		HashMap<String, Object> map = new HashMap<String, Object>();

		// 初始化句柄
		// if (!isInitialhandle) {
		// LogUtil.i(tag, "-----isInitialhandle=" + isInitialhandle);
		File deg = new File(filePath); // 待算分文件
		LogUtil.d(tag, "deg:" + deg.getAbsolutePath());
		LogUtil.e(tag,"deg.exists():" + deg.exists() +" , deg.isFile():"  + deg.isFile()+
				" , preferFile.exists()：" + preferFile.exists()+" ,  preferFile.isFile()" +  preferFile.isFile());
		if (deg.exists() && deg.isFile() && preferFile.exists() && preferFile.isFile()) {
			handle = mainit(1, preferFile.getAbsolutePath(), deg.length() / 2);
		}
		// isInitialhandle = true;
		// }

		// 算分
		String result = mastart(handle, 1, filePath, 0);
		LogUtil.i(tag, "-----result=" + result);
		// 取分值，发送广播，显示分值
		String[] strs = result.split(",");
		if (strs != null && strs.length > 0) {
			try {
				LogUtil.i(tag, "----strs[0]=" + strs[0] + "str[1]" + strs[1] + "str[2]" + strs[2] + "str[3]" + strs[3]);
				double pesqScore = 0;
				double pesqLQ = 0;
				double pesq = 0;
				if (strs[0].startsWith("pesqscore")) {
					String score = strs[0].substring("pesqscore:".length());
					pesqScore = Double.parseDouble(score) * 1000;
				}

				if (strs[2].startsWith("pesqLq")) {
					String lq = strs[2].split(":")[1];
					pesqLQ = Double.parseDouble(lq) * 1000;
				}

				if (strs[3].startsWith("pesq")) {
					String mos = strs[3].split(":")[1];
					pesq = Double.parseDouble(mos) * 1000;
				}

				map.put("pesqscore", pesqScore);
				map.put("pesqLq", pesqLQ);
				map.put("pesq", pesq);
				map.put("file", filePath);
			} catch (Exception e) {
				LogUtil.w(tag, e.toString());
			}

		}
		return map;
	}

}
