package com.walktour.gui.newmap.basestation;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.newmap.basestation.service.BaseStationService;
import com.walktour.gui.newmap.basestation.util.BaseStationImportFactory;

import java.util.List;
import java.util.Vector;

/**
 * 基站数据解析类
 * 
 * @author Zhengmin Create Time:2010/4/16
 */
public class BaseDataParser {
	private static final String tag = "BaseDataParser";
	/** 从文件加载到的基站列表 */
	private static Vector<BaseStationDetail> dataLoad;
	/** 要显示的基站列表 */
	private static Vector<BaseStationDetail> dataDisplay;

	private double minX = 0;

	private double minY = 0;

	private double maxX = 0;

	private double maxY = 0;
	/** 是否追加基站地图 */
	private static boolean isAppendBase = false;
	// TODO ?????什么值
	private static float xValue = 0;
	private static double ydX = 0;

	private static double ydY = 0;

	private static float yValue = 0;

	private static int viewWidth;

	private static int viewHeight;

	private static BaseStationDetail firstDetail = null;

	private static double height;

	// private static final double MAX_SIZE = 1600 * 1400;

	public BaseDataParser() {

	}

	public static boolean isAppendBase() {
		return isAppendBase;
	}

	public static void setAppendBase(boolean isAppendBase) {
		BaseDataParser.isAppendBase = isAppendBase;
	}

	/** 已经从文件加载的所有基站 */
	public static Vector<BaseStationDetail> getDataLoad() {
		return dataLoad;
	}

	/** 要显示的基站 */
	public static Vector<BaseStationDetail> getDataDisplay() {
		return dataDisplay;
	}

	public static float getXValue() {
		return xValue;
	}

	public static float getYValue() {
		return yValue;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void setCoordinate(float x, float y) {
		xValue = x;
		yValue = y;
		// LogUtil.w(tag, "---xvalue="+xvalue+" yvalue="+yvalue);
	}

	public void setViewSize(int width, int height) {
		viewWidth = width;
		viewHeight = height;
	}

	/**
	 * 角度转弧度
	 * 
	 * @param degree
	 * @return
	 */
	static double deg2rad(double degree) {
		return degree / 180 * Math.PI;
	}

	public static BaseStationDetail getFirstDetail() {
		return firstDetail;
	}

	public void setMinX(double minX) {
		this.minX = minX;
	}

	public void setMinY(double minY) {
		this.minY = minY;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	public double getYdX() {
		return ydX;
	}

	public double getYdY() {
		return ydY;
	}

	public static double getHeight() {
		return height;
	}

	/**
	 * 根据缩放比例创建位图
	 * 
	 * @param scale
	 * @return 输出位图
	 */
	public Bitmap createBitmap(double scale) {
		ConfigRoutine config = ConfigRoutine.getInstance();
		double width = (maxX - minX) * scale + 16;
		height = (maxY - minY) * scale + 16;
		LogUtil.w(tag, "---width = " + width + " height=" + height);
		Bitmap bm = Bitmap.createBitmap(viewWidth, viewHeight, Config.ARGB_8888);

		Canvas canvas = new Canvas(bm);
		Paint paint = new Paint();
		BaseStationDetail curDetail = null;
		double centerX = 0;
		double centerY = 0;
		int size = 15;
		double textX = 0;
		double textY = 0;
		float len = 0;
		double lineLen = 20;
		double endX = 0;
		double endY = 0;
		if (dataDisplay != null) {
			LogUtil.w(tag, "---data_vector.size=" + dataDisplay.size());
		}
		for (BaseStationDetail detail : dataDisplay) {
			if (curDetail == null) {
				curDetail = detail;
				firstDetail = curDetail;
				centerX = (maxX + minX) / 2 - firstDetail.main.longitude;
				centerY = (maxY + minY) / 2 - firstDetail.main.latitude;
				ydX = -(centerX * scale) + width / 2;
				LogUtil.w(tag, "---yd_x=" + ydX);
				ydY = -(centerY * scale) + height / 2;
				LogUtil.w(tag, "---yd_y=" + ydY);
				canvas.translate(0, (float) (height - 16));
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(Color.BLUE);
				paint.setTypeface(null);
				paint.setTextSize(size);
				detail.xScreen = (float) ydX + xValue;
				detail.yScreen = (float) (height - ydY) + yValue;
				canvas.drawCircle((float) ydX + xValue, (float) (height - ydY) + yValue, 6, paint);
				len = curDetail.main.name.length() * size;
				endX = ydX + Math.sin(deg2rad(curDetail.bearing)) * lineLen + xValue;
				endY = height - ydY - Math.cos(deg2rad(curDetail.bearing)) * lineLen + yValue;
				paint.setColor(Color.RED);
				canvas.drawLine((float) ydX + xValue, (float) (height - ydY) + yValue, (float) endX, (float) endY, paint);
				float offsety = 10;
				if (Math.cos(deg2rad(curDetail.bearing)) > 0) {
					offsety = -10;
				}
				textX = endX - len;
				textY = endY + offsety;
				canvas.drawText(curDetail.cellName, (float) textX, (float) textY, paint);
				String s1 = "", s2 = "", s3 = "";
				if (config.getMapHeight().equals("1")) {
					s1 = "ANTENNA HEIGHT:" + curDetail.antennaHeight;
				}
				if (config.getMapAzimuth().equals("1")) {
					textY = endY + offsety + 15;
					s2 = "AZIMUTH:" + curDetail.bearing;
				}
				if (config.getMapPn().equals("1")) {
					textY = endY + offsety + 15;
					s3 = "PN:" + curDetail.pn;
				}
				String show = s1 + " " + s2 + " " + s3;
				canvas.drawText(show, (float) textX, (float) textY, paint);
			} else {
				curDetail = detail;
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(Color.BLUE);
				paint.setTypeface(null);
				paint.setTextSize(size);
				canvas.drawCircle((float) (ydX + (curDetail.main.longitude - firstDetail.main.longitude) * scale) + xValue,
						(float) (height - ydY + (curDetail.main.latitude - firstDetail.main.latitude) * scale) + yValue, 6, paint);
				detail.xScreen = (float) (ydX + (curDetail.main.longitude - firstDetail.main.longitude) * scale) + xValue;
				detail.yScreen = (float) (height - ydY + (curDetail.main.latitude - firstDetail.main.latitude) * scale)
						+ yValue;
				len = curDetail.main.name.length() * size;
				endX = (float) (ydX + (curDetail.main.longitude - firstDetail.main.longitude) * scale)
						+ Math.sin(deg2rad(curDetail.bearing)) * lineLen + xValue;
				endY = (float) (height - ydY + (curDetail.main.latitude - firstDetail.main.latitude) * scale)
						- Math.cos(deg2rad(curDetail.bearing)) * lineLen + yValue;
				paint.setColor(Color.RED);
				canvas.drawLine((float) (ydX + (curDetail.main.longitude - firstDetail.main.longitude) * scale) + xValue,
						(float) (height - ydY + (curDetail.main.latitude - firstDetail.main.latitude) * scale) + yValue,
						(float) endX, (float) endY, paint);
				float offsety = 10;
				if (Math.cos(deg2rad(curDetail.bearing)) > 0) {
					offsety = -10;
				}
				textX = endX - len;
				textY = endY + offsety;
				canvas.drawText(curDetail.cellName, (float) textX, (float) textY, paint);
				// 偏移
				int lineHeight = 15;

				// 经纬度
				if (config.getLatitude().equals("1")) {
					textY = endY + offsety + lineHeight;
					String txtGeo = curDetail.main.longitude + "," + curDetail.main.latitude;
					canvas.drawText(txtGeo, (float) textX, (float) textY, paint);
					lineHeight += 15;
				}
				// 方向角、高度、PN
				String s1 = "", s2 = "", s3 = "";
				textY = endY + offsety + lineHeight;
				if (config.getMapHeight().equals("1")) {
					s1 = "ANTENNA HEIGHT:" + curDetail.antennaHeight;
				}
				if (config.getMapAzimuth().equals("1")) {
					s2 = "AZIMUTH:" + curDetail.bearing;
				}
				if (config.getMapPn().equals("1")) {
					s3 = "PN:" + curDetail.pn;
				}
				String show = s1 + " " + s2 + " " + s3;
				canvas.drawText(show, (float) textX, (float) textY, paint);
			}
		}
		canvas.save();
		canvas.restore();
		return bm;

	}

	/**
	 * 解析基站数据文件<BR>
	 * 返回基站数据
	 * 
	 * @param filePath
	 *          文件路径
	 * @param mHandler
	 *          句柄
	 * @return 基站数组
	 */
	public List<BaseStation> parse(String filePath, Handler mHandler) {
		if (!MapFactory.isLoadBase() || !isAppendBase) {
			dataLoad = new Vector<BaseStationDetail>();
			dataDisplay = new Vector<BaseStationDetail>();
		}
		List<BaseStation> baselist = null;
		try {
			baselist = BaseStationImportFactory.getInstance().importFile(filePath);
			for (BaseStation base : baselist) {
				if (!MapFactory.isLoadBase() || !isAppendBase) {
					getBaseScope(base);
				}
				for (BaseStationDetail detail : base.details) {
					dataLoad.add(detail);
					dataDisplay.add(detail);
				}
			}
			TraceInfoInterface.traceData.cleanCellIDHmKey();
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.w("Walktour_BaseDataParser_Parse_ReadLine_Error:", e.toString());
			if (mHandler != null) {
				mHandler.sendEmptyMessage(BaseStationService.BASEDATA_FILE_ERROR);
			}
		}
		return baselist;

	}

	/**
	 * 获取基站的经纬度范围
	 */
	private void getBaseScope(BaseStation base) {
		if (minX != 0) {
			minX = base.longitude < minX ? base.longitude : minX;
			minY = base.latitude < minY ? base.latitude : minY;
			maxX = base.longitude > maxX ? base.longitude : maxX;
			maxY = base.latitude > maxY ? base.latitude : maxY;
		} else {
			minX = base.longitude;
			minY = base.latitude;
			maxX = base.longitude;
			maxY = base.latitude;
		}
	}

}
