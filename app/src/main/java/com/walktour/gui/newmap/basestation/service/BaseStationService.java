package com.walktour.gui.newmap.basestation.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.map.googlemap.location.GoogleCorrectUtil;
import com.walktour.gui.newmap.basestation.BaseDataParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 基站数据服务类
 * 
 * @author jianchao.wang 2014年8月13日
 */
public class BaseStationService extends Service {
	/** 文件名称 */
	private static String FILE_NAME;
	/** 文件夹名称 */
	private static String FOLDER_PATH;
	/** 缓存大小 1K */
	private static final int BUFF_SIZE = 4096;
	/** 基站文件解析类 */
	private BaseDataParser baseDataParser = new BaseDataParser();
	/** 基站文件异常 */
	public static final int BASEDATA_FILE_ERROR = 4001;
	/** 显示当前导入进度 */
	public static final int SHOW_PROGRESS = 4002;

	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(getApplicationContext());
		FOLDER_PATH = AppFilePathUtil.getInstance().createSDCardBaseDirectory("basestation");
		FILE_NAME = AppFilePathUtil.getInstance().getSDCardBaseFile("basestation","basestation.zip").getAbsolutePath();
	}

	/**
	 * 解析基站文件包的数据流
	 * 
	 * @param bytes
	 *          数据流
	 * @param handler
	 *          消息处理句柄
	 */
	public boolean parseStationBytes(byte[] bytes, Handler handler) {
		if (bytes == null || bytes.length == 0)
			return false;
		File file = new File(FILE_NAME);
		try {
			if (file.exists())
				file.delete();
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(bytes);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.parseStationZipFile(handler);
	}

	/**
	 * 解析基站数据的zip压缩文件
	 */
	private boolean parseStationZipFile(Handler handler) {
		if (this.unzip()) {
			File desDir = new File(FOLDER_PATH);
			for (File file : desDir.listFiles()) {
				if (file.getName().equals("basestation.zip"))
					continue;
				if (file.isDirectory()) {
					for (File file1 : desDir.listFiles()) {
						this.dealFileData(file1.getAbsolutePath(), BaseStation.MAPTYPE_OUTDOOR, handler);
					}
				} else {
					this.dealFileData(file.getAbsolutePath(), BaseStation.MAPTYPE_OUTDOOR, handler);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 解析基站文件
	 * 
	 * @param filePath
	 *          文件路径
	 * @param mapType
	 *          地图类型
	 */
	public void dealFileData(String filePath, int mapType, Handler handler) {
		List<BaseStation> baseList = baseDataParser.parse(filePath, handler);
		if (mapType == BaseStation.MAPTYPE_OUTDOOR) {
			for (BaseStation base : baseList) {
				if (!this.correctLonAndLat(base))
					continue;
			}
		}
		BaseStationDBHelper.getInstance(BaseStationService.this).insertBaseData(baseList, mapType, handler);
		if (mapType == BaseStation.MAPTYPE_INDOOR)
			MapFactory.setBaseList(baseList);
	}

	/**
	 * 纠偏位置信息处理<BR>
	 * 
	 * @param baseStation
	 *          基站对象
	 * @return
	 */
	private boolean correctLonAndLat(BaseStation baseStation) {
		try {
			// 纠偏google坐标
			double[] latlng = GoogleCorrectUtil.adjustLatLng(BaseStationService.this, baseStation.latitude,
					baseStation.longitude);
			baseStation.googleLatitude = new BigDecimal(latlng[0]).setScale(8, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			baseStation.googleLongitude = new BigDecimal(latlng[1]).setScale(8, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			latlng = this.adjustLatLng(baseStation.latitude, baseStation.longitude);
			baseStation.baiduLatitude = new BigDecimal(latlng[0]).setScale(8, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			baseStation.baiduLongitude = new BigDecimal(latlng[1]).setScale(8, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 把GPS经纬度坐标转换成百度坐标
	 * 
	 * @param latitude
	 *          纬度
	 * @param longitude
	 *          经度
	 * @return
	 */
	private double[] adjustLatLng(double latitude, double longitude) {
		CoordinateConverter conver = new CoordinateConverter();
		conver.coord(new LatLng(latitude, longitude));
		conver.from(CoordinateConverter.CoordType.GPS);
		LatLng ll = conver.convert();
		return new double[] { ll.latitude, ll.longitude };
	}

	/**
	 * 删除文件或目录
	 * 
	 * @param file
	 *          文件目录
	 */
	private void deleteFile(File file) {
		if (file.isDirectory()) {
			for (File file1 : file.listFiles()) {
				this.deleteFile(file1);
			}
		}
		file.delete();
	}

	/**
	 * 解压缩文件
	 * 
	 */
	private boolean unzip() {

		// 删除目录文件下所有非zip格式的文件
		File desDir = new File(FOLDER_PATH);
		for (File file : desDir.listFiles()) {
			if (file.getName().equals("basestation.zip"))
				continue;
			this.deleteFile(file);
		}
		String strEntry; // 保存每个zip的条目名称
		BufferedOutputStream dest = null; // 缓冲输出流
		FileInputStream fis = null;
		ZipInputStream zis = null;
		try {
			fis = new FileInputStream(FILE_NAME);
			zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry; // 每个zip条目的实例

			while ((entry = zis.getNextEntry()) != null) {

				LogUtil.i("Unzip: ", "=" + entry);
				int count;
				byte data[] = new byte[BUFF_SIZE];
				strEntry = entry.getName();

				File entryFile = new File(FOLDER_PATH + File.separator + strEntry);
				File entryDir = new File(entryFile.getParent());
				if (!entryDir.exists()) {
					entryDir.mkdirs();
				}

				FileOutputStream fos = new FileOutputStream(entryFile);
				dest = new BufferedOutputStream(fos, BUFF_SIZE);
				while ((count = zis.read(data, 0, BUFF_SIZE)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
				dest = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (dest != null) {
					dest.close();
					dest = null;
				}
				if (fis != null) {
					fis.close();
					fis = null;
				}
				if (zis != null) {
					zis.close();
					zis = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new BaseStationBinder();
	}

	public class BaseStationBinder extends Binder {
		/**
		 * 获取当前Service的实例
		 * 
		 * @return
		 */
		public BaseStationService getService() {
			return BaseStationService.this;
		}
	}
}
