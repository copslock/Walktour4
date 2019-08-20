package com.walktour.service.bluetoothmos;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Environment;

import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 蓝牙MOS头工厂类
 * 
 * @author jianchao.wang
 *
 */
public class BluetoothMOSFactory {
	/** 日志标识 */
	private static final String TAG = "BluetoothMOSFactory";
	/** 唯一实例 */
	private static BluetoothMOSFactory sInstance;
	/** 当前连接的设备名称 */
	/*private BluetoothMOSDevice mCurrDevice;*/
	/**当前连接的主叫设备对象*/
	private BluetoothMOSDevice mCurrMOCDevice;
	/**当前连接的被叫设备对象*/
	private BluetoothMOSDevice mCurrMTCDevice;
	/** 日期格式化 */
	private static SimpleDateFormat mDF = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
	/** 当前终端的蓝牙地址 */
	private String mMyAddress;

	private BluetoothMOSFactory() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		this.mMyAddress = adapter.getAddress();
	}

	/**
	 * 返回唯一实例
	 * 
	 * @return
	 */
	public static BluetoothMOSFactory get() {
		if (sInstance == null) {
			sInstance = new BluetoothMOSFactory();
		}
		return sInstance;
	}

	public BluetoothMOSDevice getCurrMOCDevice() {
		return mCurrMOCDevice;
	}

	public void setCurrMOCDevice(BluetoothMOSDevice currMOCDevice) {
		mCurrMOCDevice = currMOCDevice;
	}

	public BluetoothMOSDevice getCurrMTCDevice() {
		return mCurrMTCDevice;
	}

	public void setCurrMTCDevice(BluetoothMOSDevice currMTCDevice) {
		mCurrMTCDevice = currMTCDevice;
	}

	/**
	 * 生成要保存的文件
	 * 
	 * @param context
	 *          上下文
	 * @param filePrefix
	 *          文件名前缀
	 * @param fileSuffix
	 *          文件名后缀
	 */
	public static String createRecordFile(Context context, String filePrefix, String fileSuffix) {
		return createRecordFile(context, "", "", filePrefix, fileSuffix);
	}

	/**
	 * 生成要保存的文件 \语音文件存放目录\yyyy-MM-dd\测试文件名\IMEI_POQAL|PESQ_Default
	 * ?POQAL(_8K|16K|48K_NB|SWB)
	 * 
	 * @param context
	 *          上下文
	 * @param fileName
	 *          当前测试文件名
	 * @param fileExtends
	 *          文个把扩展属性 PESQ_Default ?POQAL(_48K_SWB)
	 * @param filePrefix
	 *          文件名前缀 IMEI
	 * @param fileSuffix
	 *          文件名后缀 .wav
	 */
	public static String createRecordFile(Context context, String fileName, String fileExtends, String filePrefix,
			String fileSuffix) {
		String filePath = getStorgePath(context, fileName) + File.separator + filePrefix + "_" + mDF.format(new Date())
				+ (fileExtends.equals("") ? "" : "_" + fileExtends) + "." + fileSuffix;
		File recordFile = new File(filePath);
		if (!recordFile.exists()) {
			try {
				recordFile.createNewFile();
			} catch (IOException e) {
				LogUtil.e(TAG, e.getMessage(), e);
			}
		}
		return recordFile.getAbsolutePath();
	}

	public String getMyAddress() {
		return mMyAddress;
	}

	/**
	 * 获取语音文件的存放路径
	 * 
	 * @return
	 */
	public static String getStorgePath(Context context) {
		return getStorgePath(context, "");
	}

	/**
	 * 获取语音文件的存放路径 \sdcard目录\yyyy-MM-dd\fileName
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	private static String getStorgePath(Context context, String fileName) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return AppFilePathUtil.getInstance().createSDCardBaseDirectory(context.getString(R.string.path_voice),
					UtilsMethod.getSimpleDateFormat6(System.currentTimeMillis()),(fileName.equals("") ? "" : fileName));
		}
		// 当SD卡不可用时，返回null
		return null;
	}
}
