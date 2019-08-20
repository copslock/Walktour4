package com.walktour.gui.newmap.overlay.constant;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.DisplayMetrics;

import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay.OverlayType;

/**
 * 固定图层的基础类
 * 
 * @author jianchao.wang
 *
 */
public abstract class BaseConstantOverlay {

	/** 日志标识 */
	protected String TAG = "";
	/** 所属的界面类 */
	protected BaseMapActivity mActivity;
	/** 地图关联工厂类 */
	protected NewMapFactory factory;
	/** 显示工具 */
	private DisplayMetrics metric;
	/** 画笔 */
	protected Paint mPaint;
	/** 系统缩放比例 */
	protected float systemScale;
	/** 图片半径 */
	protected int overlayRadius;
	/** 地图图层 */
	protected BaseMapLayer mMapLayer;
	/** 像素密度 */
	protected float mDensity;
	/** 当前覆盖物类型 */
	protected OverlayType mType;

	public BaseConstantOverlay(BaseMapActivity activity, BaseMapLayer mapLayer, String tag, OverlayType type) {
		this.mActivity = activity;
		this.mMapLayer = mapLayer;
		this.TAG = tag;
		this.mType = type;
		this.factory = NewMapFactory.getInstance();
		this.metric = new DisplayMetrics();
		this.mActivity.getWindowManager().getDefaultDisplay().getMetrics(this.metric);
		this.mDensity = this.metric.density;
		this.systemScale = metric.densityDpi / 240.f;
		this.overlayRadius = (int) (15 * systemScale);
		this.createPaint();
	}

	/**
	 * 生成画笔
	 */
	private void createPaint() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.RED); // 设置画笔颜色
		mPaint.setStrokeWidth((float) 2.0); // 线宽
	}

	/**
	 * 把经纬度转换成界面坐标点
	 * 
	 * @param latitude
	 *          百度经度
	 * @param longitude
	 *          百度纬度
	 * 
	 * @return 界面坐标点
	 */
	protected Point convertLatlngToPoint(double latitude, double longitude) {
		return this.mMapLayer.convertLatlngToPoint(latitude, longitude);
	}

	/**
	 * 把界面坐标点转换成经纬度
	 * 
	 * @param point
	 *          界面坐标点
	 * 
	 * @return 经纬度
	 */
	protected MyLatLng convertPointToLatlng(Point point) {
		return this.mMapLayer.convertPointToLatlng(point);
	}

	/**
	 * 获取图层要绘制的数据
	 * 
	 * @param valueLeftTop
	 *          左上角经纬度坐标
	 * @param valueRightBottom
	 *          右下角经纬度坐标
	 */
	protected abstract void getOverlayItems(MyLatLng valueLeftTop, MyLatLng valueRightBottom);

	/**
	 * 绘制画板
	 * 
	 * @param canvas
	 *          画板
	 */
	protected abstract void drawCanvas(Canvas canvas);

	/**
	 * 销毁对象处理
	 */
	public abstract void onDestroy();

	/**
	 * 恢复操作
	 */
	public abstract void onResume();

	public OverlayType getType() {
		return mType;
	}
}
