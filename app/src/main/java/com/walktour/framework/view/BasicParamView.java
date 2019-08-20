package com.walktour.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.walktour.Utils.ApplicationModel;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;

/**
 * [一句话功能简述]<BR>
 * 基本参数父类，提供基本方法及共有属性
 * 
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-20]
 */
@SuppressWarnings("deprecation")
public abstract class BasicParamView extends View implements RefreshEventListener {

	/** 参数View行数，所有View最多不超过19行 */
	protected float tableRows = 19;
	/** 行高,按照当前View的 总高度/行数 */
	// public int rowHeight = 0;
	/** 字体大小,所有参数View字体大小统一 */
	protected int textSize = 18;
	/** 字体离表格的间距 */
	protected int marginSize = 8;
	/** 字体颜色 */
	protected int fontColor = Color.WHITE;
	/** 表格线颜色 */
	protected int tableLineColor = getResources().getColor(R.color.legend);
	/** 是否初始化 */
	private boolean init = false;
	/** 字体画笔 */
	public Paint fontPaint;
	/** 线画笔 */
	public Paint linePaint;
	/** 参数画笔 */
	public Paint paramPaint;
	/** 柱状图 */
	private Drawable histogramN;
	/** 柱状图 */
	private Drawable histogramS;
	/** 柱状图 */
	private Drawable histogramLayer;
	/** 系统缩放比例 */
	public float systemScale;
	/** 显示设备 */
	private DisplayMetrics metric;

	/**
	 * [构造简要说明]
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public BasicParamView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * [构造简要说明]
	 * 
	 * @param context
	 */
	public BasicParamView(Context context) {
		super(context);
		setDrawingCacheEnabled(false);
		this.setBackgroundColor(context.getResources().getColor(R.color.app_main_bg_color));
		initPaint();
	}

	/**
	 * [构造简要说明]
	 * 
	 * @param context
	 * @param attrs
	 */
	public BasicParamView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDrawingCacheEnabled(true);
		this.setBackgroundColor(getResources().getColor(R.color.app_main_bg_color));
		// initPaint();
	}

	public void init() {
		if (!init) {
			histogramN = getResources().getDrawable(R.drawable.histogram_1);
			histogramS = getResources().getDrawable(R.drawable.histogram_2);
			histogramLayer = getResources().getDrawable(R.drawable.histogram_layer);
			metric = this.getResources().getDisplayMetrics();
			systemScale = metric.densityDpi / 240.f;
			float scaleY = getHeight() / 240.0f;
			scaleY /= systemScale;
			textSize *= systemScale;
			initPaint();

			Matrix scalYzoom = new Matrix();// 按宽度宽高缩放
			scalYzoom.postScale(1, scaleY);

			init = true;
		}
	}

	/**
	 * 初始化画笔
	 */
	public void initPaint() {
		fontPaint = new Paint();
		fontPaint.setAntiAlias(true);
		fontPaint.setStyle(Paint.Style.FILL);
		fontPaint.setColor(getResources().getColor(R.color.app_main_text_color));
		fontPaint.setTypeface(null);
		fontPaint.setTextSize(textSize);

		linePaint = new Paint();
		linePaint.setColor(getResources().getColor(R.color.legend));
		linePaint.setStrokeWidth(2f);

		paramPaint = new Paint();
		paramPaint.setAntiAlias(true);
		paramPaint.setStyle(Paint.Style.FILL);
		paramPaint.setColor(getResources().getColor(R.color.app_param_color)); //getResources().getColor(R.color.info_param_color)
		paramPaint.setTypeface(null);
		paramPaint.setTextSize(textSize);

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param canvas
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// if(!init){
		init();
		initView(canvas);
		// init = true;
		// }
	}

	/**
	 * 初始化需要绘制的控件对象 <BR>
	 * [功能详细描述]
	 */
	public abstract void initView(Canvas canvas);

//	public void switchMarker(int currentPage, int pageCount) {
//		RelativeLayout contentRelativeLayout = (RelativeLayout) findViewById(R.id.content_layout);
//		final RelativeLayout.LayoutParams scaleParams = new RelativeLayout.LayoutParams(
//				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//		switch (pageCount) {
//		case 1:
//			ImageView switchImage = new ImageView(this.getContext());
//			switchImage.setLayoutParams(scaleParams);
//			scaleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//			scaleParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//			contentRelativeLayout.addView(switchImage, scaleParams);
//			break;
//		case 2:
//			LinearLayout linearLayout = new LinearLayout(this.getContext());
//			linearLayout.setVerticalGravity(LinearLayout.HORIZONTAL);
//
//			ImageView switchImage_1 = new ImageView(this.getContext());
//			switchImage_1.setLayoutParams(scaleParams);
//			switchImage_1.setId(R.id.switch_1);
//
//			ImageView switchImage_2 = new ImageView(this.getContext());
//			switchImage_2.setLayoutParams(scaleParams);
//			switchImage_2.setId(R.id.switch_2);
//
//			linearLayout.addView(switchImage_1);
//			linearLayout.addView(switchImage_2);
//
//			scaleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//			scaleParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//			contentRelativeLayout.addView(linearLayout, scaleParams);
//
//			break;
//		case 3:
//
//			break;
//
//		default:
//			break;
//		}
//	}

	/**
	 * 创建柱状体<BR>
	 * 公共方法，提供创建Cell柱状图表能力
	 * 
	 * @param histogramName
	 *          柱状显示名称
	 * @param params
	 *          参数说明 如：N1，N2，N3等
	 * @param values
	 *          参数值：主要是采集数据的值
	 * @param percentages
	 *          百分比，用于计算柱状的高度
	 * @param Y
	 *          Y轴高度，指定绘制的位置
	 * @param canvas
	 *          画布对象
	 */
	public void createCellHistogram(String histogramName, String[] params, float[] values, float[] percentages, float Y,
			Canvas canvas) {
		int colsWidth = this.getWidth() / (values.length < 7 ? 6 : 7);
		canvas.drawText(histogramName, this.getWidth() - fontPaint.measureText(histogramName) - 10, 30, fontPaint);
		histogramLayer.setBounds((this.getWidth() - histogramLayer.getIntrinsicWidth()) / 2,
				(int) (Y - histogramLayer.getIntrinsicHeight()), (this.getWidth() - histogramLayer.getIntrinsicWidth()) / 2
						+ histogramLayer.getIntrinsicWidth(), (int) Y);
		histogramLayer.draw(canvas);
		// canvas.drawBitmap(histogramLayer,(this.getWidth() -
		// histogramLayer.getWidth()) /2, Y - histogramLayer.getHeight(),
		// fontPaint);
		int histogramfillHeight = (int) (this.getHeight() - 12 * metric.density - 30 - textSize * 3);
		if (params == null) {
			params = new String[] { "N1", "N2", "N3", "N4", "N5", "N6", "N7" };
		}
		for (int i = 0; i < (values.length > 7 ? 7 : values.length); i++) {
			// int histogramHeight = (int)Math.ceil(histogramN.getHeight() / 100.0f *
			// percentages[i]);
			int histogramHeight = (int) Math.ceil(histogramfillHeight / 100.0f * percentages[i]);
			if (histogramHeight > 0) { // histogramHeight != 0 &&
				if (params.length > 0 && params[i].equals("Srv")) {
					histogramS.setBounds(colsWidth * i + ((colsWidth - histogramN.getIntrinsicWidth()) / 2), (int) Y
							- histogramHeight,
							colsWidth * i + ((colsWidth - histogramN.getIntrinsicWidth()) / 2) + histogramN.getIntrinsicWidth(),
							(int) Y);
					histogramS.draw(canvas);
				} else {
					// signal1Bp = Bitmap.createBitmap(histogramN, 0,
					// 0,histogramN.getWidth(),histogramHeight);
					histogramN.setBounds(colsWidth * i + ((colsWidth - histogramN.getIntrinsicWidth()) / 2), (int) Y
							- histogramHeight,
							colsWidth * i + ((colsWidth - histogramN.getIntrinsicWidth()) / 2) + histogramN.getIntrinsicWidth(),
							(int) Y);
					histogramN.draw(canvas);
				}
				// LogUtil.w("BasicPaamView","--Y:"+Y+"--hh:"+histogramHeight+"--PP:"+percentages[i]);
				// canvas.drawBitmap(signal1Bp, colsWidth * i + ((colsWidth -
				// signal1Bp.getWidth()) /2), Y - signal1Bp.getHeight(), fontPaint);
				canvas.drawText(String.valueOf(values[i]),
						colsWidth * i + ((colsWidth - fontPaint.measureText(String.valueOf(values[i]))) / 2), Y - histogramHeight
								- 10, fontPaint);
				canvas.drawText(params[i], colsWidth * i + ((colsWidth - fontPaint.measureText(params[i])) / 2), Y + 12
						* metric.density, fontPaint);
			}
		}
	}


	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @see android.view.View#onAttachedToWindow()
	 */

	@Override
	protected void onAttachedToWindow() {
		RefreshEventManager.addRefreshListener(this);
		super.onAttachedToWindow();
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @see android.view.View#onDetachedFromWindow()
	 */

	@Override
	protected void onDetachedFromWindow() {
		init = false;
		RefreshEventManager.removeRefreshListener(this);
		super.onDetachedFromWindow();
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param refreshType
	 * @param object
	 * @see com.walktour.framework.view.RefreshEventManager.RefreshEventListener#onRefreshed(com.walktour.framework.view.RefreshEventManager.RefreshType,
	 *      java.lang.Object)
	 */

	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		switch (refreshType) {
		case ACTION_WALKTOUR_TIMER_CHANGED:
			if (!ApplicationModel.getInstance().isFreezeScreen()) {
				invalidate();
			}
			break;

		default:
			break;
		}
	}

}
