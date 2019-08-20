package com.walktour.gui.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.dingli.wlan.apscan.WifiScanner;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;

import java.util.HashMap;
import java.util.List;
/**
 * AP信号强度-时间曲线的VIEW
 * */
public class APCurveView extends View {


	public APCurveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		metric = new DisplayMetrics();
		 ((Activity) context).getWindowManager()
       .getDefaultDisplay()
       .getMetrics(metric);
      reservedLeft = (50 * metric.densityDpi / 240.f);
      reservedBottom =  (40 * metric.densityDpi / 240.f);
      reservedTop = (10 * metric.densityDpi / 240.f);
      reservedRight = (10 * metric.densityDpi / 240.f);
		
		
	}
	
	private final static String tag = "APCurveView"; 
	private static float DENSITY = 0;
	private float mPixelPerMHz;
	private float mPixelPerSecond;
	private final int ALLTIME = 60;
	private float mPixelPerdBm;
	private float reservedLeft;// 左面的边距手机屏幕左沿距离
	private float reservedBottom ;// 下面的边距手机屏幕下沿距离
	private float reservedTop;// 上面的边距手机屏幕上沿距离
	private float reservedRight;// 右面的边距手机屏幕右沿距离
	private int width;// 手机屏幕的宽
	private int height;// 手机屏幕的高
	private static int spinnerHeight = 73;//View里面Spinner的高度
	private final int COLORSPACE = 256*3;
	private int colorCount = 1;
	private HashMap<String, Point[]> pointMap;//将AP名称与点阵对应
	private HashMap<String, Integer> colorMap ;//将AP名称与颜色对应
	private static String selectedAPName = "";
	public static List<String> allname = null;
	private WifiScanner scanner= WifiScanner.instance(this.getContext());
	private DisplayMetrics metric;
	private long updateTime = System.currentTimeMillis();
	
	public static void setDensity(float value) {
		DENSITY = value;
	}
	
	public static void setSpinnerHeight(int height) {
		spinnerHeight = height;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		width = getWidth();
		height = getHeight();

		mPixelPerMHz = (width - reservedLeft - reservedRight) / 94.0F;
		mPixelPerdBm = (height - reservedBottom - reservedTop) / 80.0F;
		mPixelPerSecond = (width - reservedLeft - reservedRight) / ALLTIME * 1.0f;
		
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);

		canvas.drawLine(reservedLeft, height - reservedBottom,
				reservedLeft, reservedTop, paint);// 左边的线
		canvas.drawLine(reservedLeft, height - reservedBottom, width
				- reservedRight, height - reservedBottom, paint);// 下边的线
		canvas.drawLine(reservedLeft, reservedTop, width - reservedRight,
				reservedTop, paint);// 上边的线
		canvas.drawLine(width - reservedRight, reservedTop, width
				- reservedRight, height - reservedBottom, paint);// 右边的线

		drawLevel(canvas);
		initMap();
		
		// 必须添加这行，否则过滤后不能生效
		APCurveView.allname = scanner.getAPNamesClone();
		//停止更新
		if(scanner.getApList() != null && (System.currentTimeMillis()-updateTime) > 1000) {
			updateTime = System.currentTimeMillis();
			buildPointMap();
			sortPoint();
			
			
		}
		//清除
		drawAllPoint(canvas);
	}
	/*清除所有点*/
	private void clearAllPoints() {
		if(allname == null || pointMap == null) {
			return;
		}
		for(int i = 0;i < allname.size();i++) {
			Point[] p = null;
			p = pointMap.get(allname.get(i)) ;
			if(p == null) {
				continue;
			}
			for(int j = 0;j < ALLTIME;j++) {
				p[j].x = (int)(j*mPixelPerSecond);
				p[j].y = 0;
			}
		}
	}
	//初始化
	private void initMap(){
		if(pointMap == null) {
			pointMap = new HashMap<String, Point[]>();
		}
		if(colorMap == null) {
			colorMap = new HashMap<String, Integer>();
		}
	}
	private void buildPointMap() {
		for(int i = 0;i < allname.size();i++) {
			if(!colorMap.containsKey(allname.get(i))) {
				int r = (int)(Math.random()*255);
				int g = (int)(Math.random()*255);
				int b = (int)(Math.random()*255);
				colorMap.put(allname.get(i), Color.argb(255, r, g, b));
				colorCount += 10 ;
			}
		}
		for(int i = 0;i < allname.size(); i++) {
			if(!pointMap.containsKey(allname.get(i))) {
				Point[] p = new Point[ALLTIME];
				for(int j = 0 ;j < ALLTIME; j++) {
					p[j] = new Point();
					p[j].x = (int)(j * mPixelPerSecond);
					p[j].y = 0;
				}
				pointMap.put(allname.get(i), p);
			}
		}

	}
	
	private void sortPoint() {
		Point[] p = null;
		for(int i = 0;i < allname.size() ;i++) {
			p = pointMap.get(allname.get(i));
			if( p == null) {
				continue;
			}
			for(int j = 0;j < ALLTIME - 1; j++) {
				p[j].y = p[j+1].y;
			}
			p[ALLTIME - 1].x = (int) (width - reservedLeft - reservedRight);
			/**
			 * WIFI_AP_LIST可能发生更新，使得可以扫描到的AP数量与之前的不一致，发生数组越界
			 * **/
			if(i >= scanner.getApList().size()) {
				p[ALLTIME - 1].y = 0;
			}
			else {
				p[ALLTIME - 1].y = (int) (- (110 + scanner.getApList().get(i).rssi)*mPixelPerdBm);
			}
			pointMap.put(allname.get(i), p);
			
		}
	}
	public static void setSelectAP(String apName) {
		selectedAPName = apName;
	}
	private void drawAllPoint(Canvas canvas) {
		Paint paint = new Paint();
		Point[] allPoint = null;
		canvas.translate(reservedLeft, height - reservedBottom + spinnerHeight);
		LogUtil.w(tag, "reservedLeft>>"+reservedLeft);
		LogUtil.w(tag, "height - reservedBottom + spinnerHeight>>>" + (height - reservedBottom + spinnerHeight));
		for(int i = 0;i < allname.size() ; i++) {
			/**选中的线条加粗*/
			if(allname.get(i).equals(selectedAPName)) {
				paint.setStrokeWidth(6);
			}
			else {
				paint.setStrokeWidth(2);
			}
			int color = Color.WHITE;
			if(colorMap.get(allname.get(i)) != null) {
				color = colorMap.get(allname.get(i));
			}
			paint.setColor(color);
			allPoint = pointMap.get(allname.get(i));
			if(allPoint == null) {
				continue;
			}
			for(int j = 0; j < ALLTIME - 1; j++) {
				canvas.drawLine(allPoint[j].x, allPoint[j].y, allPoint[j+1].x
								, allPoint[j+1].y, paint);
			}
		}
	}


	/**
	 * 在x坐标中画出信道刻度
	 */
	private void drawChannel(Canvas canvas) {
		float leftPointX = reservedLeft + 12 * mPixelPerMHz;
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(DENSITY * paint.getTextSize());
		paint.setColor(Color.GRAY);
		Rect rect = new Rect();
		paint.getTextBounds("14", 0, "14".length(), rect);
		// paint.setStyle(style.Theme_Black);
		for (int i = 0; i < 13; i++) {
			float pointX = leftPointX + i * 5 * mPixelPerMHz;
			canvas.drawLine(pointX, height - reservedBottom, pointX, height
					- reservedBottom - 5, paint);// height - reservedBottom
													// - 5其中五个像素为刻度线长度
			canvas.drawText(i + 1 + "", pointX - rect.width() / 4.0f,
					height - reservedBottom + 15, paint);// height -
															// reservedBottom
															// +
															// 15其中十五个像素为刻度值距下边线的距离
		}
		float pointX = leftPointX + 72 * mPixelPerMHz;
		canvas.drawLine(pointX, height - reservedBottom, pointX, height
				- reservedBottom - 5, paint);
		canvas.drawText("14", pointX - rect.width() / 4.0f, height
				- reservedBottom + 15, paint);

		String text = getResources().getString(R.string.wifi_channel);
		paint.getTextBounds(text, 0, text.length(), rect);
		float textX = reservedLeft
				+ ((width - reservedLeft - reservedRight) / 2.0f - rect
						.width() / 2.0f);
		canvas.drawText(text, textX, height - 5, paint);
	}

	/**
	 * 在Y坐标中画出信号强度刻度
	 */
	private void drawLevel(Canvas canvas) {
		float levY = height - reservedBottom - 10 * mPixelPerdBm;
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(DENSITY * paint.getTextSize());
		paint.setColor(Color.GRAY);
		Rect rect = new Rect();
		paint.getTextBounds("-100", 0, "-100".length(), rect);
		for (int i = 0; i < 7; i++) {
			float pointY = levY - i * 10 * mPixelPerdBm;
			canvas.drawLine(reservedLeft + 5 * metric.density, pointY, reservedLeft, pointY,
					paint);// reservedLeft - 5其中五个像素为刻度线长度
			canvas.drawText((i - 10) * 10 + "", reservedLeft - paint.measureText((i - 10) * 10 + "") - 2 * metric.density, pointY
					+ rect.width() / 4.0f, paint);// reservedLeft -
													// 25其中二十五个像素为刻度长度
			// 划出虚线
			for (int j = (int) reservedLeft; j <= width - reservedRight; j += 4) {
				canvas.drawPoint(j, pointY, paint);
			}
		}

		String text = getResources().getString(R.string.signal_level);
		paint.getTextBounds(text, 0, text.length(), rect);

		float textY = reservedTop
				+ ((height - reservedBottom - reservedTop) / 2.0f + rect
						.width() / 2.0f);
		// 旋转-90度使描述的字为竖着写的
		canvas.rotate(-90, rect.height(), textY);
		canvas.drawText(text, 2 * metric.density, textY, paint);
		canvas.restore();
	}
}
