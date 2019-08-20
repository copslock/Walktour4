package com.walktour.mapextention.mif;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.walktour.Utils.TypeConver;
import com.walktour.base.util.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * MIF文件解析类 Author:Zhengmin Create Time:2010/4/21 Modifier:tangwuqing,bieli
 * 2011/3/10
 */
public class MifParser {
	private static final String tag = "MifParser";
	private static Vector<GraphicObj> Graphic_Stack;
	private static List<String> roadnames;
	private static double minx = 0;

	// private static int count = 0;
	public static void setMinx(double minx) {
		MifParser.minx = minx;
	}

	public static void setMiny(double miny) {
		MifParser.miny = miny;
	}

	public static void setMaxx(double maxx) {
		MifParser.maxx = maxx;
	}

	public static void setMaxy(double maxy) {
		MifParser.maxy = maxy;
	}

	private static double miny = 0;
	private static double maxx = 0;
	private static double maxy = 0;

	public static double getMinx() {
		return minx;
	}

	public static double getMiny() {
		return miny;
	}

	public static double getMaxx() {
		return maxx;
	}

	public static double getMaxy() {
		return maxy;
	}

	private static int viewWidth;
	private static int viewHeight;
	private static float xvalue = 0;

	private double width = 0;

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	private double height = 0;

	public static float getXvalue() {
		return xvalue;
	}

	public static float getYvalue() {
		return yvalue;
	}

	private static float yvalue = 0;
	private static MifCenter firstpoint = null;

	public static MifCenter getFirstpoint() {
		return firstpoint;
	}

	static float yd_x = 0;
	static float yd_y = 0;

	public float getYd_x() {
		return yd_x;
	}

	public float getYd_y() {
		return yd_y;
	}

	// private static MifParser instence;
	// private MifParser(){}
	// public synchronized static MifParser getInstance(){
	// if(instence==null){
	// instence=new MifParser();
	// }
	// return instence;
	// }

	public static Vector<GraphicObj> getGraphic_Stack() {
		return Graphic_Stack;
	}

	public static void setGraphic_Stack(Vector<GraphicObj> graphic_Stack) {
		Graphic_Stack = graphic_Stack;
	}

	public static List<String> getRoadnames() {
		return roadnames;
	}

	public static void setRoadnames(List<String> roadnames) {
		MifParser.roadnames = roadnames;
	}

	public void setviewSize(int width, int height) {
		viewWidth = width;
		viewHeight = height;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void setCoordinate(float x, float y) {
		xvalue = x;
		yvalue = y;
		// LogUtil.w(tag, "---xvalue="+xvalue+" yvalue="+yvalue);
	}

	/**
	 * 根据缩放比例创建位图
	 * 
	 * @param scale
	 * @return 创建位图
	 */
	public Bitmap CreateBitmap(double scale) {
		// double width = (maxx - minx)*scale;
		// double height = (maxy - miny)*scale;
		width = (maxx - minx) * scale;
		height = (maxy - miny) * scale;
		// LogUtil.w(tag, "---maxx="+maxx+"minx="+minx+"maxy="+maxy+"miny="+miny);
		// LogUtil.w(tag, "----width000="+width+" heigh000t="+height);
//		if (width > viewWidth) {
//			width = viewWidth;
//		}
//		if (height > viewHeight) {
//			height = viewHeight;
//		}
		// LogUtil.w(tag, "----width111="+width+" height111="+height);
		Bitmap bm = Bitmap.createBitmap((int)width, (int)height, Config.ARGB_4444);
		Canvas cv = new Canvas(bm);
		Paint paint = new Paint();
		GraphicObj go = null;
		MifPoints points = null;
		MifPen pen = null;
		// MifBrush brush = null;
		// MifCenter center = null;
		MifCenter curpoint = null;
		Paint painttext = new Paint();
		painttext.setTextSize(18);
		// painttext.setStrokeWidth(1);
		painttext.setColor(Color.RED);
		painttext.setStyle(Paint.Style.FILL);

		double centerx = 0;
		double centery = 0;
		// 第一次取得复杂区域或者复杂线条数的标志，如果是复杂区域或者线条，那么直到把这个复杂区域或者线条绘制完毕，然后才再重新取
		boolean isFirstGetRegion = true;
		// LogUtil.w(tag, "---Graphic_Stack.size="+Graphic_Stack.size());
		int count = 0, regionNum = 1, regionNp = 1;
		for (int i = 0; i < Graphic_Stack.size(); i++) {
			go = Graphic_Stack.elementAt(i);
			if ((go.type == MifConfig.region || go.type == MifConfig.multiplePline) && isFirstGetRegion) {
				regionNp = regionNum = go.regionNumb;
				isFirstGetRegion = false;
			}
			switch (go.type) {
			case MifConfig.multiplePline:
				i++;
				points = (MifPoints) Graphic_Stack.elementAt(i);
				i++;
				pen = (MifPen) Graphic_Stack.elementAt(i);

				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(pen.width);
				paint.setColor(0xff << 24 | pen.color);
				paint.setPathEffect(null);
				Path p = new Path();
				if (curpoint == null) {
					curpoint = points.points.elementAt(0);
					firstpoint = curpoint;
					centerx = (maxx + minx) / 2 - firstpoint.x;
					centery = (maxy + miny) / 2 - firstpoint.y;
					yd_x = (float) (-(centerx * scale) + width / 2);
					yd_y = (float) (-(centery * scale) + height / 2);
					p.moveTo(xvalue + yd_x, yvalue + (float) height - yd_y);
				} else {
					curpoint = points.points.elementAt(0);
					p.moveTo(xvalue + yd_x + (float) ((curpoint.x - firstpoint.x) * scale),
							yvalue + (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale)));
				}
				for (int j = 1; j < points.points.size(); j++) {
					curpoint = points.points.elementAt(j);
					float x = yd_x + (float) ((curpoint.x - firstpoint.x) * scale);
					float y = (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale));
					x += xvalue;
					y += yvalue;
					p.lineTo(x, y);
				}
				cv.drawPath(p, paint);
				if (count < roadnames.size()) {
					String s = roadnames.get(count);
//					cv.drawTextOnPath(s, p, 5.0f, -5, painttext);
				}
				count++;
				if (regionNp >= 1) {
					regionNp--;
					// LogUtil.w(tag, "---regionNp 222="+regionNp);
				}
				if (regionNp == 0) {
					count = count - (regionNum - 1);
					isFirstGetRegion = true;
					// LogUtil.w(tag, "---isFirstGetRegion change");
				}
				break;
			case MifConfig.pline:
				i++;
				points = (MifPoints) Graphic_Stack.elementAt(i);
				i++;
				pen = (MifPen) Graphic_Stack.elementAt(i);

				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(pen.width);
				paint.setColor(0xff << 24 | pen.color);
				paint.setPathEffect(null);
				Path p8 = new Path();
				if (curpoint == null) {
					curpoint = points.points.elementAt(0);
					firstpoint = curpoint;
					centerx = (maxx + minx) / 2 - firstpoint.x;
					centery = (maxy + miny) / 2 - firstpoint.y;
					yd_x = (float) (-(centerx * scale) + width / 2);
					yd_y = (float) (-(centery * scale) + height / 2);
					p8.moveTo(xvalue + yd_x, yvalue + (float) height - yd_y);
				} else {
					curpoint = points.points.elementAt(0);
					p8.moveTo(xvalue + yd_x + (float) ((curpoint.x - firstpoint.x) * scale),
							yvalue + (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale)));
				}
				for (int j = 1; j < points.points.size(); j++) {
					curpoint = points.points.elementAt(j);
					float x = yd_x + (float) ((curpoint.x - firstpoint.x) * scale);
					float y = (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale));
					x += xvalue;
					y += yvalue;
					p8.lineTo(x, y);
				}
				cv.drawPath(p8, paint);
				if (count < roadnames.size()) {
					String s = roadnames.get(count);
//					cv.drawTextOnPath(s, p8, 5.0f, -5, painttext);
				}
				count++;
				break;
			case MifConfig.region:
				i++;
				points = (MifPoints) Graphic_Stack.elementAt(i);
				i++;
				pen = (MifPen) Graphic_Stack.elementAt(i);
				// i++;
				// brush = (MifBrush)Graphic_Stack.elementAt(i);
				// i++;
				// center = (MifCenter)Graphic_Stack.elementAt(i);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(pen.width);
				paint.setColor(0xff << 24 | pen.color);
				paint.setPathEffect(null);
				Path p1 = new Path();
				if (curpoint == null) {
					curpoint = points.points.elementAt(0);
					firstpoint = curpoint;
					centerx = (maxx + minx) / 2 - firstpoint.x;
					centery = (maxy + miny) / 2 - firstpoint.y;
					// LogUtil.w(tag, "---centerx:"+centerx+" centery:"+centery);
					yd_x = (float) (-(centerx * scale) + width / 2);
					yd_y = (float) (-(centery * scale) + height / 2);
					// LogUtil.w(tag, "---yd_xl:"+yd_x+" yd_yl:"+yd_y);
					// LogUtil.w(tag, "----firstpoint="+firstpoint.x+"
					// firstpoint="+firstpoint.y);
					p1.moveTo(xvalue + yd_x, yvalue + (float) height - yd_y);
				} else {
					curpoint = points.points.elementAt(0);
					p1.moveTo(xvalue + yd_x + (float) ((curpoint.x - firstpoint.x) * scale),
							yvalue + (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale)));
				}
				//// LogUtil.w(tag, "----xvalue="+xvalue+" yvalue="+yvalue);
				for (int j = 1; j < points.points.size(); j++) {
					curpoint = points.points.elementAt(j);
					float x = yd_x + (float) ((curpoint.x - firstpoint.x) * scale);
					float y = (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale));
					x += xvalue;
					y += yvalue;
					p1.lineTo(x, y);
				}
				cv.drawPath(p1, paint);
				if (regionNp >= 1) {
					regionNp--;
					// LogUtil.w(tag, "---regionNp 222="+regionNp);
				}
				count++;
				if (regionNp == 0) {
					count = count - (regionNum - 1);
					isFirstGetRegion = true;
					// LogUtil.w(tag, "---isFirstGetRegion change");
				}
				break;
			case MifConfig.line:
				i++;
				points = (MifPoints) Graphic_Stack.elementAt(i);
				i++;
				pen = (MifPen) Graphic_Stack.elementAt(i);
				// i++;
				// brush = (MifBrush)Graphic_Stack.elementAt(i);
				// i++;
				// center = (MifCenter)Graphic_Stack.elementAt(i);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(pen.width);
				paint.setColor(0xff << 24 | pen.color);
				paint.setPathEffect(null);
				Path p2 = new Path();
				// float x1,x2;
				// float y1,y2;
				if (curpoint == null) {
					curpoint = points.points.elementAt(0);
					firstpoint = curpoint;
					centerx = (maxx + minx) / 2 - firstpoint.x;
					centery = (maxy + miny) / 2 - firstpoint.y;
					// LogUtil.w(tag, "---centerx:"+centerx+" centery:"+centery);
					yd_x = (float) (-(centerx * scale) + width / 2);
					yd_y = (float) (-(centery * scale) + height / 2);
					// LogUtil.w(tag, "---yd_xl:"+yd_x+" yd_yl:"+yd_y);
					// LogUtil.w(tag, "----firstpoint="+firstpoint.x+"
					// firstpoint="+firstpoint.y);
					p2.moveTo(xvalue + yd_x, yvalue + (float) height - yd_y);
				} else {
					curpoint = points.points.elementAt(0);
					p2.moveTo(xvalue + yd_x + (float) ((curpoint.x - firstpoint.x) * scale),
							yvalue + (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale)));
				}
				//// LogUtil.w(tag, "----xvalue="+xvalue+" yvalue="+yvalue);
				LogUtil.w(tag, "----points.points.size=" + points.points.size());
				for (int j = 1; j < points.points.size(); j++) {
					curpoint = points.points.elementAt(j);
					// LogUtil.w(tag, "----curpoint.x="+curpoint.x+"
					// curpoint.y="+curpoint.y);
					float x = yd_x + (float) ((curpoint.x - firstpoint.x) * scale);
					float y = (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale));
					x += xvalue;
					y += yvalue;
					p2.lineTo(x, y);
				}
				cv.drawPath(p2, paint);
				if (count < roadnames.size()) {
					String st = roadnames.get(count);
//					cv.drawTextOnPath(st, p2, 5.0f, -5, painttext);
				}
				count++;
				break;
			case MifConfig.point:
				count++;
				break;
			case MifConfig.rect:
				i++;
				points = (MifPoints) Graphic_Stack.elementAt(i);
				i++;
				pen = (MifPen) Graphic_Stack.elementAt(i);
				// i++;
				// brush = (MifBrush)Graphic_Stack.elementAt(i);
				// i++;
				// center = (MifCenter)Graphic_Stack.elementAt(i);
				paint.setStyle(Paint.Style.STROKE);
				paint.setColor(0xff << 24 | pen.color);
				paint.setColor(Color.CYAN);
				paint.setPathEffect(null);
				Path prect = new Path();
				if (curpoint == null) {
					curpoint = points.points.elementAt(0);
					firstpoint = curpoint;
					centerx = (maxx + minx) / 2 - firstpoint.x;
					centery = (maxy + miny) / 2 - firstpoint.y;
					// LogUtil.w(tag, "---centerx:"+centerx+" centery:"+centery);
					yd_x = (float) (-(centerx * scale) + width / 2);
					yd_y = (float) (-(centery * scale) + height / 2);
					// LogUtil.w(tag, "---yd_xl:"+yd_x+" yd_yl:"+yd_y);
					// LogUtil.w(tag, "----firstpoint="+firstpoint.x+"
					// firstpoint="+firstpoint.y);
					prect.moveTo(xvalue + yd_x, yvalue + (float) height - yd_y);
				} else {
					curpoint = points.points.elementAt(0);
					prect.moveTo(xvalue + yd_x + (float) ((curpoint.x - firstpoint.x) * scale),
							yvalue + (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale)));
				}
				//// LogUtil.w(tag, "----xvalue="+xvalue+" yvalue="+yvalue);
				for (int j = 1; j < points.points.size(); j++) {
					curpoint = points.points.elementAt(j);
					float x = yd_x + (float) ((curpoint.x - firstpoint.x) * scale);
					float y = (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale));
					x += xvalue;
					y += yvalue;
					prect.lineTo(x, y);
				}
				count++;
				break;
			case MifConfig.roundrect:
				i++;
				points = (MifPoints) Graphic_Stack.elementAt(i);
				i++;
				pen = (MifPen) Graphic_Stack.elementAt(i);
				// i++;
				// brush = (MifBrush)Graphic_Stack.elementAt(i);
				// i++;
				// center = (MifCenter)Graphic_Stack.elementAt(i);
				paint.setStyle(Paint.Style.STROKE);
				paint.setColor(0xff << 24 | pen.color);
				paint.setPathEffect(null);
				Path proundrect = new Path();
				if (curpoint == null) {
					curpoint = points.points.elementAt(0);
					firstpoint = curpoint;
					centerx = (maxx + minx) / 2 - firstpoint.x;
					centery = (maxy + miny) / 2 - firstpoint.y;
					// LogUtil.w(tag, "---centerx:"+centerx+" centery:"+centery);
					yd_x = (float) (-(centerx * scale) + width / 2);
					yd_y = (float) (-(centery * scale) + height / 2);
					// LogUtil.w(tag, "---yd_xl:"+yd_x+" yd_yl:"+yd_y);
					// LogUtil.w(tag, "----firstpoint="+firstpoint.x+"
					// firstpoint="+firstpoint.y);
					proundrect.moveTo(xvalue + yd_x, yvalue + (float) height - yd_y);
				} else {
					curpoint = points.points.elementAt(0);
					proundrect.moveTo(xvalue + yd_x + (float) ((curpoint.x - firstpoint.x) * scale),
							yvalue + (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale)));
				}
				//// LogUtil.w(tag, "----xvalue="+xvalue+" yvalue="+yvalue);
				for (int j = 1; j < points.points.size(); j++) {
					curpoint = points.points.elementAt(j);
					float x = yd_x + (float) ((curpoint.x - firstpoint.x) * scale);
					float y = (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale));
					x += xvalue;
					y += yvalue;
					proundrect.lineTo(x, y);
				}
				count++;
				break;
			case MifConfig.arc:
				i++;
				points = (MifPoints) Graphic_Stack.elementAt(i);
				i++;
				pen = (MifPen) Graphic_Stack.elementAt(i);
				// i++;
				// brush = (MifBrush)Graphic_Stack.elementAt(i);
				// i++;
				// center = (MifCenter)Graphic_Stack.elementAt(i);
				paint.setStyle(Paint.Style.STROKE);
				paint.setColor(0xff << 24 | pen.color);
				paint.setPathEffect(null);
				Path parc = new Path();
				if (curpoint == null) {
					curpoint = points.points.elementAt(0);
					firstpoint = curpoint;
					centerx = (maxx + minx) / 2 - firstpoint.x;
					centery = (maxy + miny) / 2 - firstpoint.y;
					// LogUtil.w(tag, "---centerx:"+centerx+" centery:"+centery);
					yd_x = (float) (-(centerx * scale) + width / 2);
					yd_y = (float) (-(centery * scale) + height / 2);
					// LogUtil.w(tag, "---yd_xl:"+yd_x+" yd_yl:"+yd_y);
					// LogUtil.w(tag, "----firstpoint="+firstpoint.x+"
					// firstpoint="+firstpoint.y);
					parc.moveTo(xvalue + yd_x, yvalue + (float) height - yd_y);
				} else {
					curpoint = points.points.elementAt(0);
					parc.moveTo(xvalue + yd_x + (float) ((curpoint.x - firstpoint.x) * scale),
							yvalue + (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale)));
				}
				//// LogUtil.w(tag, "----xvalue="+xvalue+" yvalue="+yvalue);
				for (int j = 1; j < points.points.size(); j++) {
					curpoint = points.points.elementAt(j);
					float x = yd_x + (float) ((curpoint.x - firstpoint.x) * scale);
					float y = (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale));
					x += xvalue;
					y += yvalue;
					parc.lineTo(x, y);
				}
				count++;
				break;
			case MifConfig.ellipse:
				i++;
				points = (MifPoints) Graphic_Stack.elementAt(i);
				i++;
				pen = (MifPen) Graphic_Stack.elementAt(i);
				// i++;
				// brush = (MifBrush)Graphic_Stack.elementAt(i);
				// i++;
				// center = (MifCenter)Graphic_Stack.elementAt(i);
				paint.setStyle(Paint.Style.STROKE);
				paint.setColor(0xff << 24 | pen.color);
				paint.setPathEffect(null);
				Path pellipse = new Path();
				if (curpoint == null) {
					curpoint = points.points.elementAt(0);
					firstpoint = curpoint;
					centerx = (maxx + minx) / 2 - firstpoint.x;
					centery = (maxy + miny) / 2 - firstpoint.y;
					// LogUtil.w(tag, "---centerx:"+centerx+" centery:"+centery);
					yd_x = (float) (-(centerx * scale) + width / 2);
					yd_y = (float) (-(centery * scale) + height / 2);
					// LogUtil.w(tag, "---yd_xl:"+yd_x+" yd_yl:"+yd_y);
					// LogUtil.w(tag, "----firstpoint="+firstpoint.x+"
					// firstpoint="+firstpoint.y);
					pellipse.moveTo(xvalue + yd_x, yvalue + (float) height - yd_y);
				} else {
					curpoint = points.points.elementAt(0);
					pellipse.moveTo(xvalue + yd_x + (float) ((curpoint.x - firstpoint.x) * scale),
							yvalue + (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale)));
				}
				//// LogUtil.w(tag, "----xvalue="+xvalue+" yvalue="+yvalue);
				for (int j = 1; j < points.points.size(); j++) {
					curpoint = points.points.elementAt(j);
					float x = yd_x + (float) ((curpoint.x - firstpoint.x) * scale);
					float y = (float) height - (yd_y + (float) ((curpoint.y - firstpoint.y) * scale));
					x += xvalue;
					y += yvalue;
					pellipse.lineTo(x, y);
				}
				count++;
				break;

			}
		}
		// LogUtil.w(tag, "---count 333="+count);
		cv.save();
		cv.restore();
		return bm;
	}

	/**
	 * 解析MIF文件
	 * 
	 * @param context
	 */
	public void Parse(String FilePath) {
		Graphic_Stack = new Vector<GraphicObj>();
		roadnames = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			// MIF文件名字
			String miffilename = FilePath.substring(FilePath.lastIndexOf("/") + 1, FilePath.length());
			// 对应的MID文件名字
			String midfilename = miffilename.toUpperCase(Locale.getDefault()).replace("MIF", "MID");
			// 对应的MID文件路径
			String midfilepath = FilePath.substring(0, FilePath.lastIndexOf("/") + 1) + midfilename;
			LogUtil.w(tag, "---midfilepath=" + midfilepath);
			File f = new File(midfilepath);
			if (f.exists()) {
				FileInputStream fm = new FileInputStream(midfilepath);
				InputStream ss = fm;
				reader = new BufferedReader(new InputStreamReader(ss, "GBK"));
				String lineStrs = null;
				while ((lineStrs = reader.readLine()) != null) {
					String r = lineStrs.substring(lineStrs.indexOf("\"") + 1, lineStrs.lastIndexOf("\""));
					// LogUtil.w(tag, "---r="+r);
					roadnames.add(r);
					// LogUtil.w(tag, "----strs[]="+strs[1]);
				}
				reader.close();
			}
		} catch (Exception e) {
		} finally {
			try {
				if (reader != null) {
					reader.close();
					reader = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// LogUtil.w(tag, "---roadnames.size="+roadnames.size());

		int count = 0;
		FileInputStream fis = null;
		String lineStr;
		boolean flag = false;
		int curentType = 0;
		String[] arr;
		MifPen pen = null;
		MifPoints[] regionPoint = null;
		MifPoints points = null;
		try {
			fis = new FileInputStream(FilePath);
			reader = new BufferedReader(new InputStreamReader(fis));
			while ((lineStr = reader.readLine()) != null) {
				if (flag && lineStr.length() >= 4) {
					lineStr = lineStr.trim();
					if (lineStr.toLowerCase(Locale.getDefault()).startsWith("pline multiple")) { // Pline
																																												// Multiple类型
						count++;
						int multiple = Integer.parseInt(lineStr.substring(14).trim()); // 线条数
						curentType = MifConfig.multiplePline;
						MifPoints[] plineMultiple = new MifPoints[multiple];

						for (int j = 0; j < multiple; j++) {
							points = new MifPoints();
							lineStr = reader.readLine().trim();
							int plineNum = Integer.parseInt(lineStr.trim()); // pline后面跟着点数
							for (int i = 0; i < plineNum; i++) {
								lineStr = reader.readLine().trim();

								arr = Split2(" ", lineStr);
								MifCenter p = new MifCenter();
								p.x = TypeConver.StringToDouble(arr[0]);
								p.y = TypeConver.StringToDouble(arr[1]);
								points.points.add(p);

								setMaxMinXY(p);
							}
							plineMultiple[j] = points;
						}
						lineStr = reader.readLine().trim(); // 遍历完点数后紧跟着为pline的pen
						if (lineStr.toLowerCase(Locale.getDefault()).startsWith("pen")) {
							pen = getPen(lineStr.substring(3));
						} else {
							LogUtil.w(tag, "pline format error!");
							break;
						}
						for (int i = 0; i < multiple; i++) {
							addPoints(curentType, plineMultiple[i], pen, "", multiple);
						}
						plineMultiple = null;
					} else if (lineStr.length() >= 5 && lineStr.substring(0, 5).equalsIgnoreCase("Pline")) { // Pline类型
						String roadname = "";
						if (!roadnames.isEmpty()) {
							roadname = roadnames.get(count);
						}
						count++;
						curentType = MifConfig.pline;
						points = new MifPoints();
						int plineNum = Integer.parseInt(lineStr.substring(5).trim()); // pline后面跟着点数的经纬度
						for (int i = 0; i < plineNum; i++) {
							lineStr = reader.readLine().trim();

							arr = Split2(" ", lineStr);
							MifCenter p = new MifCenter();
							p.x = TypeConver.StringToDouble(arr[0]);
							p.y = TypeConver.StringToDouble(arr[1]);
							points.points.add(p);

							setMaxMinXY(p);
						}
						lineStr = reader.readLine().trim(); // 遍历完点数后紧跟着为pline的pen
						if (lineStr.toLowerCase(Locale.getDefault()).startsWith("pen")) {
							pen = getPen(lineStr.substring(3));
						} else {
							LogUtil.w(tag, "pline format error!");
							break;
						}
						addPoints(curentType, points, pen, roadname, -1);
					} else if (lineStr.length() >= 6 && lineStr.substring(0, 6).equalsIgnoreCase("Region")) { // Region类型
						// String roadname = "";
						// if (!roadnames.isEmpty()) {
						// roadname = roadnames.get(count);
						// }
						// map.put(count, roadname);
						count++;
						curentType = MifConfig.region;
						int regionNum = Integer.parseInt(lineStr.substring(6).trim()); // Region后面跟区域个数
						regionPoint = new MifPoints[regionNum];
						for (int i = 0; i < regionNum; i++) {
							points = new MifPoints();
							lineStr = reader.readLine().trim(); // 紧跟着为当前区域的经纬度点数
							int currentNum = Integer.parseInt(lineStr);
							for (int j = 0; j < currentNum; j++) {
								lineStr = reader.readLine().trim(); // 当前区域的经纬度值
								arr = Split2(" ", lineStr);
								MifCenter p = new MifCenter();
								p.x = TypeConver.StringToDouble(arr[0]);
								p.y = TypeConver.StringToDouble(arr[1]);
								points.points.add(p);

								setMaxMinXY(p);
							}
							regionPoint[i] = points;
						}
						lineStr = reader.readLine().trim(); // 遍历完点数后紧跟着为pline的pen
						if (lineStr.toLowerCase(Locale.getDefault()).startsWith("pen")) {
							pen = getPen(lineStr.substring(3));
						} else {
							LogUtil.w(tag, "pline format error!");
							break;
						}

						for (int i = 0; i < regionNum; i++) {
							addPoints(curentType, regionPoint[i], pen, "", regionNum);
						}
					} else if (lineStr.substring(0, 4).equalsIgnoreCase("Line")) { // Line类型

						curentType = MifConfig.line;
						points = new MifPoints();
						String[] sts = lineStr.split(" ");
						for (String s : sts) {
							LogUtil.i(tag, "---s=" + s);
						}
						for (int i = 1; i < 4; i += 2) {
							MifCenter p = new MifCenter();
							p.x = TypeConver.StringToDouble(sts[i]);
							p.y = TypeConver.StringToDouble(sts[i + 1]);
							// LogUtil.w(tag, " p.x="+p.x+" p.y="+p.y);
							points.points.add(p);
						}
						String roadname = "";
						if (!roadnames.isEmpty()) {
							roadname = roadnames.get(count);
						}
						count++;
						lineStr = reader.readLine().trim(); // 遍历完点数后紧跟着为pline的pen
						if (lineStr.toLowerCase(Locale.getDefault()).startsWith("pen")) {
							pen = getPen(lineStr.substring(3));
						} else {
							LogUtil.w(tag, "pline format error!");
							break;
						}
						addPoints(curentType, points, pen, roadname, -1);
					} else if (lineStr.length() >= 5 && lineStr.substring(0, 5).equalsIgnoreCase("Point")) { // Point类型
						String roadname = "";
						if (!roadnames.isEmpty()) {
							roadname = roadnames.get(count);
						}
						curentType = MifConfig.point;
						count++;
						points = new MifPoints();
						String[] sts = lineStr.split(" ");
						MifCenter p = new MifCenter();
						p.x = TypeConver.StringToDouble(sts[1]);
						p.y = TypeConver.StringToDouble(sts[2]);
						points.points.add(p);
						addPoints(curentType, points, pen, roadname, -1);
					} else if (lineStr.length() >= 7 && lineStr.substring(0, 7).equalsIgnoreCase("Ellipse")) { // Ellipse类型
						curentType = MifConfig.ellipse;
						points = new MifPoints();
						String[] sts = lineStr.split(" ");
						for (int i = 1; i < 4; i += 2) {
							MifCenter p = new MifCenter();
							p.x = TypeConver.StringToDouble(sts[i]);
							p.y = TypeConver.StringToDouble(sts[i + 1]);
							points.points.add(p);
						}
						String roadname = "";
						if (!roadnames.isEmpty()) {
							roadname = roadnames.get(count);
						}
						count++;
						lineStr = reader.readLine().trim(); // 遍历完点数后紧跟着为pline的pen
						if (lineStr.toLowerCase(Locale.getDefault()).startsWith("pen")) {
							pen = getPen(lineStr.substring(3));
						} else {
							LogUtil.w(tag, "pline format error!");
							break;
						}
						addPoints(curentType, points, pen, roadname, -1);

					} else if (lineStr.length() >= 9 && lineStr.substring(0, 9).equalsIgnoreCase("Roundrect")) { // Roundrect类型
						curentType = MifConfig.roundrect;
						points = new MifPoints();
						String[] sts = lineStr.split(" ");
						for (int i = 1; i < 4; i += 2) {
							MifCenter p = new MifCenter();
							p.x = TypeConver.StringToDouble(sts[i]);
							p.y = TypeConver.StringToDouble(sts[i + 1]);
							points.points.add(p);
						}
						String roadname = "";
						if (!roadnames.isEmpty()) {
							roadname = roadnames.get(count);
						}
						count++;
						lineStr = reader.readLine().trim(); // 遍历完点数后紧跟着为Roundrect的pen
						if (lineStr.toLowerCase(Locale.getDefault()).startsWith("pen")) {
							pen = getPen(lineStr.substring(3));
						} else {
							LogUtil.w(tag, "pline format error!");
							break;
						}
						addPoints(curentType, points, pen, roadname, -1);
					} else if (lineStr.substring(0, 3).equalsIgnoreCase("Arc")) { // Arc类型
						curentType = MifConfig.arc;
						points = new MifPoints();
						String[] sts = lineStr.split(" ");
						for (int i = 1; i < 4; i += 2) {
							MifCenter p = new MifCenter();
							p.x = TypeConver.StringToDouble(sts[i]);
							p.y = TypeConver.StringToDouble(sts[i + 1]);
							points.points.add(p);
						}
						String roadname = "";
						if (!roadnames.isEmpty()) {
							roadname = roadnames.get(count);
						}
						count++;
						lineStr = reader.readLine().trim(); // 遍历完点数后紧跟着为Arc的pen
						if (lineStr.toLowerCase(Locale.getDefault()).startsWith("pen")) {
							pen = getPen(lineStr.substring(3));
						} else {
							LogUtil.w(tag, "pline format error!");
							break;
						}
						addPoints(curentType, points, pen, roadname, -1);

					} else if (lineStr.substring(0, 4).equalsIgnoreCase("Rect")) { // Rect类型
						curentType = MifConfig.rect;
						points = new MifPoints();
						String[] sts = lineStr.split(" ");
						for (int i = 1; i < 4; i += 2) {
							MifCenter p = new MifCenter();
							p.x = TypeConver.StringToDouble(sts[i]);
							p.y = TypeConver.StringToDouble(sts[i + 1]);
							points.points.add(p);
						}
						String roadname = "";
						if (!roadnames.isEmpty()) {
							roadname = roadnames.get(count);
						}
						count++;
						lineStr = reader.readLine().trim(); // 遍历完点数后紧跟着为Rect的pen
						if (lineStr.toLowerCase(Locale.getDefault()).startsWith("pen")) {
							pen = getPen(lineStr.substring(3));
						} else {
							LogUtil.w(tag, "pline format error!");
							break;
						}
						addPoints(curentType, points, pen, roadname, -1);
					}
				} else if (lineStr.toLowerCase(Locale.getDefault()).indexOf("data") != -1) {
					flag = true;
				}
			}
			// LogUtil.w(tag, "---count="+count);
		} catch (FileNotFoundException e1) {
			LogUtil.w("FileNotFound:", e1.toString());
		} catch (IOException e) {
			LogUtil.w("Walktour_MifParser_Parse_ReadLine_Error:", e.toString());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// 解析画笔值
	private MifPen getPen(String sPen) {
		sPen = sPen.replace("(", "");
		sPen = sPen.replace(")", "");
		String[] arr1 = Split3(",", sPen);

		MifPen pen = new MifPen();
		pen.width = TypeConver.StringToInt(arr1[0].trim());
		pen.pattern = TypeConver.StringToInt(arr1[1].trim());
		pen.color = TypeConver.StringToInt(arr1[2].trim());
		return pen;
	}

	// 存储最大最小经纬度值
	private void setMaxMinXY(MifCenter p) {
		if (minx != 0) {
			minx = p.x < minx ? p.x : minx;
			miny = p.y < miny ? p.y : miny;
			maxx = p.x > maxx ? p.x : maxx;
			maxy = p.y > maxy ? p.y : maxy;
		} else {
			minx = p.x;
			miny = p.y;
			maxx = p.x;
			maxy = p.y;
		}
	}

	/**
	 * 按照类型，点阵队列，画笔属性的顺序添加到队列中
	 * 
	 * @param previousType
	 * @param points
	 * @param pen
	 * @param roadname
	 * @param regionNum
	 */
	private void addPoints(int previousType, MifPoints points, MifPen pen, String roadname, int regionNum) {
		GraphicObj gobj;
		if (previousType == MifConfig.pline) {
			gobj = new MifPline();
			gobj.roadname = roadname;
			// LogUtil.w(tag, "----roadname="+roadname);
		} else if (previousType == MifConfig.multiplePline) {
			gobj = new MifMulPline();
			gobj.regionNumb = regionNum;
			gobj.roadname = roadname;
		} else if (previousType == MifConfig.region) {
			gobj = new MifRegion();
			gobj.regionNumb = regionNum;
		} else if (previousType == MifConfig.line) {
			gobj = new MifLine();
			gobj.roadname = roadname;
			// LogUtil.w(tag, "----roadname="+roadname);
		} else if (previousType == MifConfig.arc) {
			gobj = new MifArc();
		} else if (previousType == MifConfig.rect) {
			gobj = new MifRect();
		} else if (previousType == MifConfig.ellipse) {
			gobj = new MifEllipse();
		} else if (previousType == MifConfig.roundrect) {
			gobj = new MifRoundRect();
		} else if (previousType == MifConfig.point) {
			gobj = new MifPoint();
		} else {
			gobj = new GraphicObj();
		}
		Graphic_Stack.add(gobj);
		Graphic_Stack.add(points);
		if (pen == null) {
			pen = new MifPen();
			pen.width = 1;
			pen.pattern = 2;
			pen.color = 128;
		}
		Graphic_Stack.add(pen);
	}

	/**
	 * 根据分隔符将字符串分割为2个字符串
	 * 
	 * @param token
	 *          分割符
	 * @param src
	 *          源字符串
	 * @return 字符串数组
	 */
	private String[] Split2(String token, String src) {
		int index = src.indexOf(token);
		String[] arr = new String[2];
		arr[0] = src.substring(0, index);
		arr[1] = src.substring(index + 1, src.length());
		return arr;
	}

	/**
	 * 根据分隔符将字符串分割为3个字符串
	 * 
	 * @param token
	 *          分割符
	 * @param src
	 *          源字符串
	 * @return 字符串数组
	 */
	private String[] Split3(String token, String src) {
		String tmpstr;
		int index = src.indexOf(token);
		String[] arr = new String[3];
		arr[0] = src.substring(0, index);
		tmpstr = src.substring(index + 1, src.length());
		index = tmpstr.indexOf(token);
		try {
			arr[1] = tmpstr.substring(0, index);
			arr[2] = tmpstr.substring(index + 1, tmpstr.length());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return arr;
	}
}
