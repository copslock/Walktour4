package com.walktour.gui.newmap.layer;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay;
import com.walktour.gui.newmap.overlay.constant.ConstantMapOverlay;
import com.walktour.gui.newmap.overlay.variable.VariableMapOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图图层接口类
 * 
 * @author jianchao.wang
 * 
 */
public abstract class BaseMapLayer {
	/** 日志标签 */
	protected String TAG;
	/** 调用的活动页面 */
	protected BaseMapActivity mActivity;
	/** 地图是否显示 */
	protected boolean isShow = true;
	/** 地图是否定位 */
	protected boolean isLocation = false;
	/** 地图界面是否初始化完成 */
	protected boolean isInit = false;
	/** 覆盖物列表 */
	protected List<BaseMapOverlay> overlayList = new ArrayList<BaseMapOverlay>();

	public BaseMapLayer(BaseMapActivity activity, String tag) {
		this.mActivity = activity;
		this.TAG = tag;
		this.fillOverlays();
		this.initZoomLevel();
		this.init();
		MyLatLng latlng = NewMapFactory.getInstance().getNowLatLng();
		if (latlng == null)
			this.setLocation();
		if (latlng != null) {
			this.isLocation = false;
			this.setCenter(latlng, NewMapFactory.getInstance().getZoomLevelNow());
		}
	}

	/**
	 * 填充图层
	 * 
	 */
	private void fillOverlays() {
		this.overlayList.add(new ConstantMapOverlay(this.mActivity, this));
		this.overlayList.add(new VariableMapOverlay(this.mActivity, this));
	}

	/**
	 * 初始化缩放等级
	 * 
	 *          当前缩放等级
	 */
	private void initZoomLevel() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.mActivity);
		NewMapFactory.getInstance().setZoomLevelMax(Integer.parseInt(pref.getString("pref_zoommaxlevel", "19")));
		NewMapFactory.getInstance().setZoomLevelMin(Integer.parseInt(pref.getString("pref_zoomminlevel", "3")));
		float zoomLevelNow = NewMapFactory.getInstance().getZoomLevelNow();
		if (zoomLevelNow <= 0)
			zoomLevelNow = 17;
		NewMapFactory.getInstance().setZoomLevelNow(zoomLevelNow);
	}

	/**
	 * 初始化图层相关的数据
	 */
	public abstract void init();

	/**
	 * 设置当前所在位置
	 */
	protected abstract void setLocation();

	/**
	 * 居中显示指定的经纬度
	 * 
	 * @param latlng
	 *          经纬度
	 * @param zoomLevelNow
	 *          当前缩放等级
	 */
	public abstract void setCenter(MyLatLng latlng, float zoomLevelNow);

	/**
	 * 放大地图操作
	 */
	public void zoomIn() {
		float zoomLevelNow = NewMapFactory.getInstance().getZoomLevelNow();
		this.setZoomLevelNow(zoomLevelNow + 1);
	}

	/**
	 * 缩小地图操作
	 */
	public void zoomOut() {
		float zoomLevelNow = NewMapFactory.getInstance().getZoomLevelNow();
		this.setZoomLevelNow(zoomLevelNow - 1);
	}

	/**
	 * 设置当前缩放等级
	 * 
	 * @param zoomLevelNow
	 *          当前缩放等级
	 */
	public abstract void setZoomLevelNow(float zoomLevelNow);

	/**
	 * 改变当前的地图类型（普通图和卫星图）
	 * 
	 */
	public abstract void changeMapType();

	/**
	 * 获取地图视图
	 * 
	 * @return
	 */
	public abstract View getMap();

	public abstract BaiduMap getMapControl();

	/**
	 * 获取当前地图的中心点经纬度
	 * 
	 * @return 经纬度
	 */
	public abstract MyLatLng getMapCenter();

	/**
	 * 暂停操作
	 */
	public abstract void onPause();

	/**
	 * 恢复操作
	 */
	public abstract void onResume();

	/**
	 * 销毁操作
	 */
	public abstract void onDestroy();

	/**
	 * 把经纬度转换成界面坐标点
	 * 
	 * @param latitude
	 *          经度
	 * @param longitude
	 *          纬度
	 * @return 坐标点
	 */
	public abstract Point convertLatlngToPoint(double latitude, double longitude);

	/**
	 * 把界面坐标点转换成经纬度
	 * 
	 * @param point
	 *          界面坐标点
	 * @return 经纬度
	 */
	public abstract MyLatLng convertPointToLatlng(Point point);

	/**
	 * 获得所有的覆盖物图层列表
	 * 
	 * @return
	 */
	public List<BaseMapOverlay> getOverlays() {
		return this.overlayList;
	}

	/**
	 * 点击覆盖物图层
	 * 
	 * @param click
	 *          点击屏幕坐标
	 */
	public void clickOverlays(Point click) {
		LogUtil.d(TAG, "--------clickOverlays------");
		for (int i = this.overlayList.size() - 1; i >= 0; i--) {
			BaseMapOverlay overlay = this.overlayList.get(i);
			if (overlay.onClick(click))
				break;
		}
	}

	/**
	 * 点击地图覆盖物
	 * 
	 * @param bundle
	 *          附加信息
	 */
	protected void clickOverlaysMarker(Bundle bundle) {
		LogUtil.d(TAG, "--------clickOverlaysMarker------");
		for (int i = this.overlayList.size() - 1; i >= 0; i--) {
			BaseMapOverlay overlay = this.overlayList.get(i);
			if (overlay.onMarkerClick(bundle))
				break;
		}
	}

	/**
	 * 缩放覆盖物图层
	 * 
	 */
	protected void zoomOverlays() {
		LogUtil.d(TAG, "------------zoomOverlays------------overlayList："+overlayList);
		for (BaseMapOverlay overlay : this.overlayList) {
			overlay.setCenter();
		}
	}

	/**
	 * 绘制线条
	 * 
	 * @param points
	 *          线上的经纬度坐标点
	 * @param lineWidth
	 *          线宽
	 * @param lineColor
	 *          线色
	 */
	public abstract Object drawLine(List<MyLatLng> points, int lineWidth, int lineColor);

	/**
	 * 绘制圆点
	 * 
	 * @param point
	 *          圆点坐标
	 * @param dotRadius
	 *          圆点半径
	 * @param dotColor
	 *          圆点颜色
	 */
	public abstract Object drawDot(MyLatLng point, int dotRadius, int dotColor);

	/**
	 * 绘制圆点
	 * 
	 * @param latlng
	 *          文字坐标
	 * @param text
	 *          文字内容
	 * @param textSize
	 *          文字大小
	 * @param textColor
	 *          文字颜色
	 */
	public abstract Object drawText(MyLatLng latlng, String text, int textSize, int textColor);

	/**
	 * 绘制位图覆盖物
	 * 
	 * @param latlng
	 *          覆盖物坐标
	 * @param icon
	 *          图片
	 * @param anchorX
	 *          X轴偏移比例
	 * @param anchorY
	 *          Y轴偏移比例
	 * @param bundle
	 *          附加信息
	 * @return
	 */
	public abstract Object drawBitmapMarker(MyLatLng latlng, Bitmap icon, float anchorX, float anchorY, Bundle bundle);

	/**
	 * 绘制资源覆盖物
	 * 
	 * @param latlng
	 *          覆盖物坐标
	 * @param rsIds
	 *          图片列表
	 * @param anchorX
	 *          X轴偏移比例
	 * @param anchorY
	 *          Y轴偏移比例
	 * @return
	 */
	public abstract Object drawResourceMarker(MyLatLng latlng, List<Integer> rsIds, float anchorX, float anchorY);

	/**
	 * 清除所有手动绘制的覆盖物
	 */
	public abstract void clearAllDraw();

	/**
	 * 长按覆盖物图层
	 * 
	 * @param click
	 *          图层坐标
	 */
	public void longClickOverlays(Point click) {
		for (int i = this.overlayList.size() - 1; i >= 0; i--) {
			BaseMapOverlay overlay = this.overlayList.get(i);
			if (overlay.onLongClick(click))
				break;
		}
	}

	/**
	 * 定位到新的经纬度
	 * 
	 * @param latitude
	 *          纬度
	 * @param longitude
	 *          经度
	 */
	public void locationNewLatlng(double latitude, double longitude) {
		if (!this.isInit)
			return;
		MyLatLng latlng = this.adjustFromGPS(latitude, longitude);
		Point point = this.convertLatlngToPoint(latlng.latitude, latlng.longitude);
		int width = this.getMap().getMeasuredWidth();
		int widthR = width / 6;
		int height = this.getMap().getMeasuredHeight();
		int heightR = height / 6;
		Rect rect = new Rect(0 + widthR, 0 + heightR, width - widthR, height - heightR);
		if (rect.contains(point.x, point.y)) {
			for (BaseMapOverlay overlay : this.overlayList) {
				overlay.locationNewLatlng(latlng);
			}
		} else {
			this.setCenter(latlng, NewMapFactory.getInstance().getZoomLevelNow());
		}
	}

	/**
	 * 初始化图层数据
	 */
	public void initOverlay() {
		for (BaseMapOverlay overlay : this.overlayList) {
			overlay.initOverlay();
		}
		this.isInit = true;
	}

	/**
	 * 设置覆盖物图层的中心点为地图的中心点
	 */
	public void setOverlaysCenter() {
		for (BaseMapOverlay overlay : this.overlayList) {
			overlay.setCenter();
		}
	}

	/**
	 * 把GPS经纬度转换成地图坐标
	 * 
	 * @param latitude
	 *          纬度
	 * @param longitude
	 *          经度
	 * @return 地图经纬度坐标
	 */
	public abstract MyLatLng adjustFromGPS(double latitude, double longitude);

	/**
	 * 关闭显示的弹出窗口
	 */
	public void closeShowPopWindow() {
		for (BaseMapOverlay overlay : this.overlayList) {
			overlay.closeShowPopWindow();
		}
	}
}
