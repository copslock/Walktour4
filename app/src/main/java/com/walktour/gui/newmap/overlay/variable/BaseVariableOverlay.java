package com.walktour.gui.newmap.overlay.variable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay.OverlayType;

/**
 * 可变图层的基础类
 * 
 * @author jianchao.wang
 *
 */
public abstract class BaseVariableOverlay {
	/** 画笔 */
	protected Paint mPaint;
	/** 日志标识 */
	protected String TAG = "";
	/** 所属的界面类 */
	protected BaseMapActivity mActivity;
	/** 地图关联工厂类 */
	protected NewMapFactory factory;
	/** 默认颜色 */
	protected int defaultColor = 0xFFFF0000;
	/** 系统缩放比例 */
	protected float systemScale;
	/** 图片半径 */
	protected int overlayRadius;
	/** 地图图层 */
	protected BaseMapLayer mMapLayer;
	/** 关联的父视图 */
	protected View parent;
	/** 地球半径 */
	private static final double EARTH_RADIUS = 6378.137;
	/** 像素密度 */
	protected float mDensity;
	/** 当前覆盖物类型 */
	protected OverlayType mType;

	public BaseVariableOverlay(BaseMapActivity activity, View parent, BaseMapLayer mapLayer, String tag,
			OverlayType type) {
		this.mActivity = activity;
		this.mMapLayer = mapLayer;
		this.TAG = tag;
		this.mType = type;
		this.parent = parent;
		this.factory = NewMapFactory.getInstance();
		DisplayMetrics metric = new DisplayMetrics();
		this.mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		this.mDensity = metric.density;
		this.systemScale = metric.densityDpi / 240.f;
		this.overlayRadius = (int) (25 * systemScale);
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
	 * 点击屏幕
	 * 
	 * @param click
	 *          屏幕坐标
	 * @return 是否当前图层有执行操作
	 */
	protected abstract boolean onClick(Point click);

	/**
	 * 点击覆盖物
	 * 
	 * @param bundle
	 *          附加信息
	 * @return 是否当前图层有执行操作
	 */
	public abstract boolean onMarkerClick(Bundle bundle);

	/**
	 * 长按屏幕
	 * 
	 * @param click
	 *          屏幕坐标
	 * @return 是否当前图层有执行操作
	 */
	protected abstract boolean onLongClick(Point click);

	/**
	 * 绘制显示的画布
	 * 
	 * @param canvas
	 *          画布
	 */
	protected abstract void drawCanvas(Canvas canvas);

	/**
	 * 距离换算<BR>
	 * [功能详细描述]
	 * 
	 * @param distance
	 * @return
	 */
	protected String distanceConversion(double distance) {
		String resultString = "";
		if (distance < 1) {
			resultString = (((int) Math.round(distance * 1000 * 10)) / 10.0) + "M";
		} else {
			resultString = ((((int) Math.round(distance * 10))) / 10.0) + "KM";
		}
		return resultString;
	}

	/**
	 * 计算两坐标点间距离
	 * 
	 * @param point1
	 *          坐标点1
	 * @param point2
	 *          坐标点2
	 * @return
	 */
	protected double calculateDistance(MyLatLng point1, MyLatLng point2) {
		double distance = 0.0;
		double lat1 = point1.latitude;
		double lat2 = point2.latitude;
		double lon1 = point1.longitude;
		double lon2 = point2.longitude;
		double dLat = (lat2 - lat1) * Math.PI / 180;
		double dLon = (lon2 - lon1) * Math.PI / 180;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * EARTH_RADIUS;
		return distance;
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
	 * 关闭显示的弹出窗口
	 */
	public abstract void closeShowPopWindow();

	/**
	 * 改变当前的地图类型（普通图和卫星图）
	 * 
	 */
	public abstract void changeMapType();

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
