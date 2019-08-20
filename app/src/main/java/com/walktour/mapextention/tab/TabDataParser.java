package com.walktour.mapextention.tab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.walktour.Utils.TypeConver;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TabDataParser {
	public TabData tab = null;
	private final String tag = "TabDataParser";
	// private double para = 120;
	// private double CrGeogCoordRatio = 9/(double)1000000;
	private static final int IMG_MAX_PIXELS = 2600 * 2000;
	// private static int viewWidth;
	// private static int viewHeight;
	/** JPEG原图缩放倍数，大于1时表示原图缩小的倍数 */
	private float scale;
	/** Tab描述文件中宽与JPEG图片宽的缩放比例 */
	private double tabToJpegWidthScale;
	/** Tab描述文件中高与JPEG图片高的缩放比例 */
	private double tabToJpegHeightScale;
	// private GPSData tlGps; //Top Left Gps Point
	// private GPSData blGps; //Bottom Left Gps Point
	// private CoordData tlCoord; //Top Left Coord
	// private CoordData trCoord; //Top Right Coord
	// private GPSData trGps; //Right Top Gps Point
	// private GPSData brGps; //Right Bottom Gps Point
	// private CoordData blCoord; //left Bottom Coord
	private CoordData brCoord; // Right Bottom Coord
	// private double centerlot;
	// private double centerlat;
	// private double lotscale;
	// private double falseeast;
	// private double falsenorth;

	public TabDataParser() {
	}

	/**
	 * 将收到的GPS经纬度转换成在平面上显示的X，Y轴值
	 * 
	 * @author tangwq
	 * @param x
	 * @param y
	 * @return
	 */
	// private Point ConvertCoordToGPS(double x,double y){
	// Ellipsoid e = Ellipsoid.WGS_1984;
	// LogUtil.w(tag,centerlot +":" + centerlat + ":" + lotscale + ":" + falseeast
	// +":" +falsenorth+ ":" + x +":" +y);
	// TransverseMercatorProjection project = new
	// TransverseMercatorProjection(e,centerlot*Math.PI/180.0,centerlat,lotscale,falseeast,falsenorth);
	// Point ptTest = new Point();
	// ptTest.x = x;
	// ptTest.y = y;
	// Point ptDest = new Point();
	//
	// Point p = project.inverseTransform(ptTest, ptDest);
	// LogUtil.w(tag,"inverseTransform" + p.x +"," + p.y);
	// return p;
	// }

	/**
	 * 设置界面宽高
	 * 
	 * @author tangwq
	 * @param width
	 * @param height
	 */
	// public void setviewSize(int width,int height){
	// viewWidth = width;
	// viewHeight = height;
	// }

	// 根据图片坐标返回gps值注意 coord为相对于图片的坐标
	/**
	 * 将屏蔽中打点的X，Y值转换成GPS源始数据存储于RCU文件中
	 */
	// public GPSData getGPSDataoftheMapbyCoodData(CoordData coord){
	// LogUtil.w(tag,"--befor x :"+coord.x + "--- befor
	// y"+coord.y+"---scale:"+scale);
	// coord.x = (coord.x * scale ) ;
	// coord.y = (coord.y * scale ) ;
	// LogUtil.w(tag,"--end x :"+coord.x + "--- end y"+coord.y);
	// GPSData data = new GPSData(0,0);
	// //LogUtil.w("ttt",(gps.lot - tlGps.lot) + "," + (brCoord.x - tlCoord.x) +
	// "," + (brGps.lot - tlGps.lot));
	// //LogUtil.w("ttt1",(tlGps.lat - gps.lat) + "," + (brCoord.y -tlCoord.y) +
	// "," + (tlGps.lat -brGps.lat));
	// data.lot =(coord.x - tlCoord.x)*(brGps.lot - tlGps.lot)/(brCoord.x -
	// tlCoord.x) + tlGps.lot;
	// data.lat =(tlCoord.y - coord.y)*(brGps.lat -tlGps.lat)/(tlCoord.y
	// -brCoord.y) + tlGps.lat;
	// return data;
	// }

	/**
	 * @author huanguanfu 通过传入两点的像素值经纬度及比例计算某个比例点的经纬度<BR>
	 *         [功能详细描述]
	 * @param widthSscale
	 *          宽度比例 如20% 则只需传20
	 * @param heightScale
	 *          高度比例 如20% 则只需传20
	 */
	// public GPSData getLatitudeAndLongitude(CoordData coord) {
	// double longitudeX = 0;//经度
	// double latitudeY = 0; //纬度
	// GPSData data = new GPSData(0,0);
	// /**占矩阵图宽的百分比*/
	// float widthSscale = (float)((coord.x * scale * tabToJpegWidthScale) /
	// brCoord.x);
	// /**占矩阵图高的百分比*/
	// float heightScale = (float)((coord.y * scale * tabToJpegHeightScale) /
	// brCoord.y);
	//
	// LogUtil.w(tag,"---xper:"+widthSscale +
	// "---yper:"+heightScale+"--btx:"+brCoord.x+"--bty:"+brCoord.y);
	// LogUtil.w(tag,"--toplat:"+tlGps.lat+"---buttomrlat:"+brGps.lat+"--toplot:"+tlGps.lot+"---
	// btlot:"+brGps.lot);
	// /*if(tlGps.lot < brGps.lot){
	// longitudeX = brGps.lot - tlGps.lot;
	// data.lot = tlGps.lot + longitudeX * (widthSscale/ scale) / 100.0f;
	// LogUtil.w(tag,"---longitude+:"+data.lot);
	// }else{
	// longitudeX = tlGps.lot - brGps.lot;
	// data.lot = tlGps.lot - longitudeX * (widthSscale/ scale) / 100.0f;
	// LogUtil.w(tag,"---longitude-:"+data.lot);
	// }
	// //正向纬度,反向
	// if(tlGps.lat < brGps.lat){
	// latitudeY = brGps.lat - tlGps.lat;
	// data.lat = tlGps.lat + latitudeY * (heightScale/ scale) / 100.0f ;
	// LogUtil.w(tag,"---latitude+:"+data.lat);
	// }else{
	// latitudeY = tlGps.lat - brGps.lat;
	// data.lat = tlGps.lat - latitudeY * (heightScale/ scale) / 100.0f ;
	// LogUtil.w(tag,"---latitude-:"+data.lat);
	// }*/
	// double lxBminusT = blGps.lot - tlGps.lot; //左经度即X轴两点底部减上部差值
	// double rxBminusT = brGps.lot - trGps.lot; //右经度即X轴两点底部减上部差值
	// double tyRminusL = trGps.lat - tlGps.lat; //上纬度即Y轴两点右减左差值
	// double byRminusL = brGps.lat - blGps.lat; //下纬度即Y轴两点右减左差值
	// LogUtil.w(tag,"--lxBminusT:"+lxBminusT+"--rxBminusT:"+rxBminusT+"--tyRminusL:"+tyRminusL+"--byRminusL:"+byRminusL);
	//
	// double lxPoint = tlGps.lot + lxBminusT * heightScale;
	// double rxpoint = brGps.lot + rxBminusT * heightScale;
	// double tyPoint = tlGps.lat + tyRminusL * widthSscale;
	// double byPoint = blGps.lat + byRminusL * widthSscale;
	// LogUtil.w(tag,"--lxPoint:"+lxPoint+"--rxpoint:"+rxpoint+"--tyPoint:"+tyPoint+"--byPoint:"+byPoint);
	//
	// double xPoint = tlGps.lot + (rxpoint - lxPoint) * widthSscale;
	// double yPoint = tlGps.lat + (byPoint - tyPoint) * heightScale;
	// LogUtil.w(tag,"--xPoint:"+xPoint+"--yPoint:"+yPoint);
	// data.lot = xPoint;
	// data.lat = yPoint;
	//
	// /*longitudeX = brGps.lot - tlGps.lot;
	// data.lot = tlGps.lot + longitudeX * (widthSscale);
	// LogUtil.w(tag,"---longitude+:"+data.lot+"--lx:"+longitudeX+"--pp:"+(longitudeX
	// * widthSscale));
	//
	// latitudeY = brGps.lat - tlGps.lat;
	// data.lat = tlGps.lat + latitudeY * (heightScale) ;
	// LogUtil.w(tag,"---latitude+:"+data.lat+"--ly:"+latitudeY+"--pp:"+
	// (latitudeY * heightScale));*/
	//
	//
	// return data;
	// }

	// 根据gps值返回图片坐标
	// public CoordData getCoordDataoftheMapbyGPSdata(GPSData gps){
	/*
	 * data.lot = (gps.lot - tlGps.lot)/scale; data.lat = (gps.lat -
	 * tlGps.lat)/scale; CoordData data1 = new CoordData(0,0); data1.x = (data.lot
	 * - tlGps.lot)*(brCoord.x - tlCoord.x)/(brGps.lot - tlGps.lot); data1.y =
	 * (data.lat - tlGps.lat)*(brCoord.y - tlCoord.y)/(brGps.lat - tlGps.lat);
	 */
	/*
	 * data.lot = (gps.lot - tlGps.lot)/(double)scale; data.lat = (tlGps.lat -
	 * gps.lat)/(double)scale; LogUtil.w("data.lot",data.lot+"");
	 * LogUtil.w("data.lat",data.lat+""); CoordData data1 = new CoordData(0,0);
	 * LogUtil.w("ttt",(data.lot + tlGps.lot) + "," + (brCoord.x - tlCoord.x) +
	 * "," + (brGps.lot - tlGps.lot)); data1.x = (data.lot + tlGps.lot)*(brCoord.x
	 * - tlCoord.x)/(brGps.lot - tlGps.lot); data1.y = (tlGps.lat +
	 * data.lat)*(brCoord.y - tlCoord.y)/(tlGps.lat - brGps.lat);
	 * LogUtil.w("data1.x",data1.x+""); LogUtil.w("data1.y",data1.y+"");
	 */
	// CoordData data = new CoordData(0,0);
	// LogUtil.w("ttt",(gps.lot - tlGps.lot) + "," + (brCoord.x - tlCoord.x) + ","
	// + (brGps.lot - tlGps.lot));
	// LogUtil.w("ttt1",(tlGps.lat - gps.lat) + "," + (brCoord.y -tlCoord.y) + ","
	// + (tlGps.lat -brGps.lat));
	// data.x =(gps.lot - tlGps.lot)*(brCoord.x - tlCoord.x)/(brGps.lot -
	// tlGps.lot) + tlCoord.x;
	// data.y =(tlGps.lat - gps.lat)*(brCoord.y -tlCoord.y)/(tlGps.lat -brGps.lat)
	// + tlCoord.y;
	// /*data.x = data.x * scale + tlCoord.x;
	// data.y = data.y * scale + tlCoord.y;*/
	// return data;
	// }

	/**
	 * 根据TAB文件的相关信息计算左上解右下解的区域范围
	 * 
	 * @author tangwq
	 */
	public void GetDataByTab() {
		/*
		 * Iterator it = tab.points.entrySet().iterator(); int i =0; while
		 * (it.hasNext()){ Entry entry = (java.util.Map.Entry) it.next(); i++;
		 * if(i==1){ tlGps = (GPSData) entry.getKey(); if (tlGps.lat>180 ||
		 * tlGps.lat <-180){ Point p =ConvertCoordToGPS(tlGps.lot,tlGps.lat);
		 * tlGps.lot = p.x; tlGps.lat = p.y; } tlCoord =
		 * (CoordData)entry.getValue(); LogUtil.w(tag,"--getdata1:"+tlGps.lot + ","
		 * +tlGps.lat + "," + tlCoord.x + "," + tlCoord.y); }
		 * 
		 * if(i==2) { blGps = (GPSData) entry.getKey(); if (blGps.lat>180 ||
		 * blGps.lat <-180){ Point p =ConvertCoordToGPS(blGps.lot,blGps.lat);
		 * blGps.lot = p.x; blGps.lat = p.y; } blCoord =
		 * (CoordData)entry.getValue(); LogUtil.w(tag,"--getdata2:"+blGps.lot + ","
		 * +blGps.lat + "," + blCoord.x + "," + blCoord.y); } if(i==3) { trGps =
		 * (GPSData) entry.getKey(); if (trGps.lat>180 || trGps.lat <-180){ Point p
		 * =ConvertCoordToGPS(trGps.lot,trGps.lat); trGps.lot = p.x; trGps.lat =
		 * p.y; } trCoord = (CoordData)entry.getValue();
		 * LogUtil.w(tag,"--getdata3:"+trGps.lot + "," +trGps.lat + "," + trCoord.x
		 * + "," + trCoord.y); } if(i==4){ brGps = (GPSData) entry.getKey(); if
		 * (brGps.lot>180 ||brGps.lot < -180){ Point p
		 * =ConvertCoordToGPS(brGps.lot,brGps.lat); brGps.lot = p.x; brGps.lat =
		 * p.y; } brCoord = (CoordData)entry.getValue();
		 * LogUtil.w(tag,"--getdata4:"+brGps.lot + "," +brGps.lat + "," + brCoord.x
		 * + "," + brCoord.y); } }
		 */
	}

	/**
	 * 创建bitmap地图
	 * 
	 * @author tangwq
	 * @param filepath
	 * @return
	 */
	public Bitmap createBitmap(String filepath) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设定后不会给图片分配内存，可以获取到原图的宽高
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filepath, opts);
		double w = opts.outWidth;
		double h = opts.outHeight;
		tabToJpegWidthScale = (brCoord == null ? 1 : brCoord.x / w);
		tabToJpegHeightScale = (brCoord == null ? 1 : brCoord.y / h);
		LogUtil.w(tag, "--w:" + w + "--ws:" + tabToJpegWidthScale + "--h:" + h + "--hs:" + tabToJpegHeightScale);
		/*
		 * double v = w * h / IMG_MAX_PIXELS; int samplesize = (int)
		 * Math.ceil(Math.sqrt(v)); LogUtil.w(tag,"samplesize:" +
		 * samplesize+"---v:"+v); if (samplesize>1){ scale
		 * =(float)(1/(float)samplesize); }else{ scale =1; }
		 */
		// scale = samplesize>1?(1/samplesize * 1.0f):1;
		// LogUtil.w("create bitmap","scale:" + scale);
		scale = UtilsMethod.computeSuitedSampleSize(opts, IMG_MAX_PIXELS);
		opts.inSampleSize = (int) scale;
		opts.inJustDecodeBounds = false;
		// 如果原图大于设定的最大值给出提示
		/*
		 * if(opts.inSampleSize>1){ Toast.makeText(context,
		 * R.string.main_indoor_loadMaptip, Toast.LENGTH_LONG).show(); }
		 */
		Bitmap bitmap = BitmapFactory.decodeFile(filepath, opts);
		if (bitmap != null) {
			LogUtil.i("TabDataParser",
					"---mbmpTest width=" + bitmap.getWidth() + "---mbmpTest height=" + bitmap.getHeight() + "---scale:" + scale);
		} else {
			LogUtil.w(tag, "--tab file null--");
		}
		return bitmap;
	}

	/**
	 * TAB地图加载及相关信息比例信息解析 解析方法需重新整理
	 * 
	 * @author tangwq
	 * @param FilePath
	 */
	public void Parse(String FilePath) {
		tab = new TabData();
		BufferedReader br = null;
		String LineStr;
		String gpsData;
		String coordData;
		double lot;
		double lat;
		double x;
		double y;
		try {
			FileInputStream fis = new FileInputStream(FilePath);
			br = new BufferedReader(new InputStreamReader(fis, "GB2312"));
			while ((LineStr = br.readLine()) != null) {
				LogUtil.w("Tab Parser", LineStr);
				if (LineStr.startsWith(" ")) {
					LogUtil.w("Tab Parser", "Start Parse");
					LineStr = LineStr.substring(2);
					LogUtil.w("Tab Parser", LineStr);
					if (LineStr.substring(0, 1).equals("F")) {
						tab.filename = LineStr.split("\"").length > 1 ? LineStr.split("\"")[1] : "";
						LogUtil.w("Tab Parser get file", tab.filename);
					} else if (LineStr.substring(0, 1).equals("(")) {
						LogUtil.w("Tab Parser", "GetGPS");
						gpsData = LineStr.split(" ")[0];
						gpsData = gpsData.replace("(", "");
						gpsData = gpsData.replace(")", "");
						// gpsData = gpsData.trim();
						LogUtil.w("Tab Parser gpsData", gpsData);
						lot = TypeConver.StringToDouble(gpsData.split(",")[0]);

						lat = TypeConver.StringToDouble(gpsData.split(",")[1]);
						LogUtil.w("Tab Parser lot", lot + "");
						LogUtil.w("Tab Parser lat", lat + "");

						coordData = LineStr.split(" ")[1];
						coordData = coordData.replace("(", "");
						coordData = coordData.replace(")", "");
						x = TypeConver.StringToDouble(coordData.split(",")[0]);
						y = TypeConver.StringToDouble(coordData.split(",")[1]);
						// tab.points = new HashMap<GPSData,CoordData>();
						tab.points.put(new GPSData(lot, lat), new CoordData(x, y));

						/*
						 * LogUtil.w("Tab Parser lot",lot+"");
						 * LogUtil.w("Tab Parser lat",lat+"");
						 */
					} else if (LineStr.substring(0, 8).equals("CoordSys") && LineStr.split(",").length >= 8) {
						// centerlot = TypeConver.StringToDouble(LineStr.split(",")[3]);
						// centerlat = TypeConver.StringToDouble(LineStr.split(",")[4]);
						// lotscale = TypeConver.StringToDouble(LineStr.split(",")[5]);
						// falseeast = TypeConver.StringToDouble(LineStr.split(",")[6]);
						// falsenorth = TypeConver.StringToDouble(LineStr.split(",")[7]);
					}
				}
			}
			GetDataByTab();
		} catch (IOException e) {
			e.printStackTrace();
			LogUtil.w("Walktour_TabDataParser_Parse_ReadLine_Error:", e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
