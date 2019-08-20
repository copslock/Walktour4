package com.walktour.control.netsniffer;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.walktour.base.util.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 内存信息<BR>
 * [功能详细描述]
 * 
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-30]
 */
public class MemInfo {

	/**
	 * 内存总量
	 */
	private long total;

	/**
	 * 可用内存
	 */
	private long avaiable;

	private static final String TAG = "MemInfo";

	public MemInfo(long total, long avaiable) {
		super();
		this.total = total;
		this.avaiable = avaiable;
	}

	/**
	 * 获取总的的内存信息，单位byte
	 * 
	 * @param total
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * 获取可用的内存信息，单位byte
	 * 
	 * @return
	 */
	public long getAvaiable() {
		return avaiable;
	}

	/**
	 * 获取RAM存储信息
	 * 
	 * @param context
	 * @return
	 */
	public static MemInfo getRAMInfo(Context context) {
		return new MemInfo(getTotalRAM(), getAvailableRAM(context));
	}

	/**
	 * 获取ROM存储信息
	 * 
	 * @return
	 */
	public static MemInfo getROMInfo() {
		return new MemInfo(getTotalROM(), getAvaiableROM());
	}

	/**
	 * 获取SD卡存储信息, 没有内存卡是则返回null
	 * 
	 * @return
	 */
	public static MemInfo getSDCardInfo() {
		MemInfo result = null;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			result = new MemInfo(getTotalSD(), getAvaiableSD());
		}
		return result;

	}

	/**
	 * 
	 * 获取手机RAM总内存,单位为字节(B)
	 */
	public static long getTotalRAM() {
		String str1 = "/proc/meminfo"; // 系统内存信息文件
		String totalMemory = "";
		long totalMemory2 = 0;// 该变量用于存储手机总内存
		FileReader fr;
		BufferedReader reader = null;
		try {
			fr = new FileReader(str1);
			reader = new BufferedReader(fr, 8192);
			try {
				totalMemory = reader.readLine();
				if (totalMemory.startsWith("MemTotal:") && totalMemory.endsWith("kB")) {
					totalMemory2 = Long.parseLong(totalMemory.replace("MemTotal:", "").replace("kB", "").trim()) * 1024;

				}
			} catch (IOException e) {

				e.printStackTrace();
			}
			LogUtil.i(TAG, "---" + totalMemory);

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return totalMemory2;
	}

	/**
	 * 
	 * 获取剩余的RAM内存 ,单位 为字节(Byte)
	 * 
	 * @param context
	 * @return
	 */
	public static long getAvailableRAM(Context context) {
		ActivityManager avaiable = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		avaiable.getMemoryInfo(mi);
		return mi.availMem;
	}

	/**
	 * 
	 * 获取ROM总内存,单位为字节(B)
	 */
	@SuppressWarnings("deprecation")
	public static long getTotalROM() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	/**
	 * 获取剩余的ROM内存，单位为字节(B)
	 */
	@SuppressWarnings("deprecation")
	public static long getAvaiableROM() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	/**
	 * 
	 * 获取SDcard卡内存总量，单位是字节(Byte)
	 */
	@SuppressWarnings("deprecation")
	public static Long getTotalSD() {
		long totalSD = 0;// SD卡总内存量
		File sdcardDir = Environment.getExternalStorageDirectory();
		// 取得sdcard文件的路径
		StatFs sf = new StatFs(sdcardDir.getPath());
		// 获取block的SIZE
		long bSize = sf.getBlockSize();
		// 获取BLOCK数量
		long bCount = sf.getBlockCount();
		totalSD = bSize * bCount;// 内存总大小
		return totalSD;
	}

	/**
	 * 
	 * 获取SDcard卡剩余内存，单位是字节(Byte)
	 */
	@SuppressWarnings("deprecation")
	public static Long getAvaiableSD() {
		File sdcardDir = Environment.getExternalStorageDirectory();
		// 取得sdcard文件路径
		StatFs sf = new StatFs(sdcardDir.getPath());
		// 获取block的SIZE
		long bSize = sf.getBlockSize();
		// 可使用的Block的数量
		long availaBlock = sf.getAvailableBlocks();
		return bSize * availaBlock;
	}

	/**
	 * 获取RAM使用率
	 * 
	 * @param context
	 * @return the used memory percentage
	 */
	public static long getUsedMemPercentage(Context context) {
		MemInfo memInfo = MemInfo.getRAMInfo(context);
		return (long) ((memInfo.getTotal() - memInfo.getAvaiable()) * 100 / (float) memInfo.getTotal());
	}
}
