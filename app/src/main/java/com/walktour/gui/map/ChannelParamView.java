package com.walktour.gui.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.dingli.wlan.apscan.APInfoModel;
import com.dingli.wlan.apscan.WifiScanner;
import com.dingli.wlan.apscan.WifiTools;
import com.walktour.gui.R;

import java.util.List;

public class ChannelParamView extends View {

	private DisplayMetrics metric;

	public ChannelParamView(Context context, AttributeSet attrs) {
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

	private float mPixelPerMHz;
	private float mPixelPerdBm;
	private float reservedLeft;// 左面的边距手机屏幕左沿距离
	private float reservedBottom;// 下面的边距手机屏幕下沿距离
	private float reservedTop;// 上面的边距手机屏幕上沿距离
	private float reservedRight;// 右面的边距手机屏幕右沿距离
	private int width;// 手机屏幕的宽
	private int height;// 手机屏幕的高
	private static float DENSITY = 0;
	private boolean hasClear = false;

	// 添加getter方法，用于在ChannelParamActivity中获取ChannelParamView的属性。郑磊添加于2012年04月07日
	public float getmPixelPerMHz() {
		return mPixelPerMHz;
	}
	public float getReservedLeft() {
		return reservedLeft;
	}
	public float getReservedBottom() {
		return reservedBottom;
	}
	public float getReservedTop() {
		return reservedTop;
	}
	public float getReservedRight() {
		return reservedRight;
	}
	public int getScrWidth() {
		return width;
	}
	public int getScrHeight() {
		return height;
	}
	// private Color color1 = new Color();//#4E99BD
	public void setClear(boolean value) {
		hasClear = value;
	}
	public static void setDensity(float value) {
		DENSITY = value;
	}
	private int[] colors = { Color.RED, Color.YELLOW, Color.LTGRAY,
			Color.GREEN };

	public ChannelParamView(Context context) {
		super(context);
	}

	protected void onDraw(Canvas canvas) {
		width = getWidth();
		height = getHeight();

		mPixelPerMHz = (width - reservedLeft - reservedRight) / 94.0F;
		mPixelPerdBm = (height - reservedBottom - reservedTop) / 80.0F;

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

		drawChannel(canvas);
		drawLevel(canvas);

		paint.setColor(Color.YELLOW);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);

		List<APInfoModel> scanResultList =WifiScanner.instance(this.getContext()).getApList();
		if (scanResultList == null) {
			return;
		}
		//没有设置清除，才画曲线
		if(!hasClear) {
			for (int i = 0; i < scanResultList.size(); i++) {
				paint.setColor(colors[i % 4]);
				APInfoModel result = scanResultList.get(i);
				drawParabola(result.rssi, WifiTools
						.getChannel(result.frequency), result.ssid, canvas,
						paint);
			}
		}

	}

	/**
	 * 抛物线的绘制
	 * 
	 * @param level
	 *            信号强度
	 * @param channel
	 *            信道
	 * @param ssid
	 * @param canvas
	 * @param paint
	 */
	private void drawParabola(float level, int channel, String ssid,
			Canvas canvas, Paint paint) {
		canvas.save();
		/*
		 * 求出抛物线y = ax(x-20*mPixelPerMHz)中a的值 其中20*mPixelPerMHz
		 * 中20代表一个信道频宽为20Mhz 然后代入对称轴的值得到a,maxY为最高点的y值,maxX最高点的x值
		 */
		float maxX = 10 * mPixelPerMHz;
		float maxY = (level + 110) * mPixelPerdBm;
		double a = 0.01 * maxY * (1 / Math.pow(mPixelPerMHz, 2));

		// 算出平移矩阵，即x和y轴的平移量
		float disX = reservedLeft + 2 * mPixelPerMHz + (channel - 1) * 5
				* mPixelPerMHz;
		float disY = height - reservedBottom;
		canvas.translate(disX, disY);
		// System.out.println("disX="+disX+"  disY="+disY);

		// 在最高点画出ssid的值
		Paint bsidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bsidPaint.setColor(paint.getColor());
		Rect rect = new Rect();
		bsidPaint.setTextSize(DENSITY * paint.getTextSize());
		bsidPaint.getTextBounds(ssid, 0, ssid.length(), rect);
		canvas.drawText(ssid, maxX - rect.width() / 2.0f, maxY * -1
				- rect.height(), bsidPaint);// maxY*-1,屏幕y轴与传统y轴相反

		Path p = new Path();
		for (float x = 0; x <= 20 * mPixelPerMHz; x += 2f) {
			float y = (float) (a * x * (x - 20 * mPixelPerMHz));
			if (x == 0) {
				p.moveTo(x, y);
			} else {
				p.lineTo(x, y);
			}
		}

		// 画出最后的一点，防止前面画出的抛物线少一块
		float x = 4 * 5 * mPixelPerMHz;
		float y = (float) (a * x * (x - 20 * mPixelPerMHz));
		p.lineTo(x, y);

		canvas.drawPath(p, paint);

		// 在抛物线覆盖区域图上一层透明色
		int red = Color.red(paint.getColor());
		int green = Color.green(paint.getColor());
		int blue = Color.blue(paint.getColor());
		int newColor = Color.argb(40, red, green, blue);
		Paint newPaint = new Paint();
		newPaint.setStyle(Style.FILL);
		newPaint.setColor(newColor);
		canvas.drawPath(p, newPaint);

		canvas.restore();
	}

	/**
	 * 在x坐标中画出信道刻度
	 */
	private void drawChannel(Canvas canvas) {
		float leftPointX = reservedLeft + 12 * mPixelPerMHz;
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(18 *(metric.densityDpi / 240.f));
		paint.setColor(Color.GRAY);
		Rect rect = new Rect();
		paint.getTextBounds("14", 0, "14".length(), rect);
		// paint.setStyle(style.Theme_Black);
		for (int i = 0; i < 13; i++) {
			float pointX = leftPointX + i * 5 * mPixelPerMHz;
			canvas.drawLine(pointX, height - reservedBottom, pointX, height
					- reservedBottom - 5* metric.density, paint);// height - reservedBottom
													// - 5其中五个像素为刻度线长度
			canvas.drawText(i + 1 + "", pointX - rect.width() / 4.0f,
					height - reservedBottom + rect.height() + 2 * metric.density, paint);// height -
															// reservedBottom
															// +
															// 15其中十五个像素为刻度值距下边线的距离
		}
		float pointX = leftPointX + 72 * mPixelPerMHz;
		canvas.drawLine(pointX, height - reservedBottom, pointX, height
				- reservedBottom - 5, paint);
		canvas.drawText("14", pointX - rect.width() / 4.0f, height
				- reservedBottom + rect.height() + 2 * metric.density, paint);

		String text = getResources().getString(R.string.wifi_channel);
		paint.getTextBounds(text, 0, text.length(), rect);
		float textX = reservedLeft
				+ ((width - reservedLeft - reservedRight) / 8.0f);
		canvas.drawText(text, textX, height - 2, paint);
	}

	/**
	 * 在Y坐标中画出信号强度刻度
	 */
	private void drawLevel(Canvas canvas) {
		float levY = height - reservedBottom - 10 * mPixelPerdBm;
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(18 *(metric.densityDpi / 240.f));
		paint.setColor(Color.GRAY);
		Rect rect = new Rect();
		paint.getTextBounds("-100", 0, "-100".length(), rect);
		for (int i = 0; i < 7; i++) {
			float pointY = levY - i * 10 * mPixelPerdBm;
			canvas.drawLine(reservedLeft + 5 * metric.density , pointY, reservedLeft, pointY,
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
