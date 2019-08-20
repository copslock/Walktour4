package com.walktour.gui.newmap.overlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;

import com.walktour.base.util.LogUtil;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;

import java.lang.ref.WeakReference;

/**
 * 地图覆盖物基础类
 * 
 * @author jianchao.wang
 * 
 */
public abstract class BaseMapOverlay extends View {
	/** 刷新当前视图 */
	private final static int REFLASH_VIEW = 11;
	/** 日志标识 */
	protected String TAG = "";
	/** 所属的界面类 */
	protected BaseMapActivity mActivity;
	/** 地图关联工厂类 */
	protected NewMapFactory factory;
	/** 默认颜色 */
	protected int defaultColor = 0xFFFF0000;
	/** 显示工具 */
	protected DisplayMetrics metric;
	/** 画笔 */
	protected Paint mPaint;
	/** 系统缩放比例 */
	protected float systemScale;
	/** 图片半径 */
	protected int overlayRadius;
	/** 要获取的数据的经纬度范围的左上角经纬度 */
	protected MyLatLng valueLeftTop = null;
	/** 要获取的数据的经纬度范围的右下角经纬度 */
	protected MyLatLng valueRightBottom = null;
	/** 判断是否要刷新数据的区域范围，如果当前显示的区域超出检测区域则重新获取数据更新图层 */
	private Rect checkRect = new Rect();
	/** 拖动屏幕时起始点击屏幕点 */
	protected Point startPoint = new Point();
	/** 地图图层 */
	protected BaseMapLayer mapLayer;
	/** 是否已经做了初始化处理 */
	protected boolean isInit = false;
	/** 当前图层的宽度 */
	private int width;
	/** 当前图层的高度 */
	private int height;
	/** 消息处理句柄 */
	public MyHandler mHander = new MyHandler(this);
	/** 绘图线程 */
	private DrawThread mDrawThread;
	/** 当前是否在绘制图层 */
	private static boolean isDrawing = false;
	/** 是否正在获取覆盖物列表 */
	protected static boolean isGetOverlayItems = false;

	/** 覆盖物类型 */
	public static enum OverlayType {
		Alarm(0), BaseStation(1), LocasPoint(2), MetroRoute(3), CellLink(4), RangingLink(5);
		private int mId;

		private OverlayType(int id) {
			this.mId = id;
		}

		public static OverlayType get(int id) {
			for (OverlayType type : values()) {
				if (type.getId() == id)
					return type;
			}
			return null;
		}

		public int getId() {
			return mId;
		}
	}

	public BaseMapOverlay(BaseMapActivity activity, BaseMapLayer mapLayer, String tag) {
		super(activity);
		this.mActivity = activity;
		this.mapLayer = mapLayer;
		this.TAG = tag;
		this.factory = NewMapFactory.getInstance();
		this.metric = this.mActivity.getResources().getDisplayMetrics();
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
	 * 把界面坐标点转换成经纬度
	 * 
	 * @param point
	 *          界面坐标点
	 * 
	 * @return 经纬度
	 */
	protected MyLatLng convertPointToLatlng(Point point) {
		return this.mapLayer.convertPointToLatlng(point);
	}

	/**
	 * 点击屏幕
	 * 
	 * @param click
	 *          屏幕坐标
	 * @return 是否当前图层有执行操作
	 */
	public abstract boolean onClick(Point click);

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
	public abstract boolean onLongClick(Point click);

	/**
	 * 绘图
	 * 
	 * @param canvas
	 */
	public void onDraw(Canvas canvas) {
		if (isDrawing || isGetOverlayItems)
			return;
		isDrawing = true;
		if (!this.isInit) {
			super.onDraw(canvas);
		} else {
			this.drawCanvas(canvas);
		}
		isDrawing = false;
	}

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
	 * 设置当前图层的中心点为地图的中心点
	 * 
	 */
	public void setCenter() {
		if (!this.isInit)
			return;
		MyLatLng latlng = this.mapLayer.getMapCenter();
		if (latlng == null)
			return;
		LogUtil.d(TAG, "------setCenter---lat:" + latlng.latitude + "----lng:" + latlng.longitude);

		this.valueLeftTop = this.convertPointToLatlng(new Point(-width, -height));
		this.valueRightBottom = this.convertPointToLatlng(new Point(width * 2, height * 2));
		this.getOverlayItems();
		this.mHander.sendEmptyMessage(REFLASH_VIEW);
	}

	/**
	 * 定位到新的经纬度
	 * 
	 * @param latlng
	 *          新的经纬度
	 */
	public void locationNewLatlng(MyLatLng latlng) {
		if (!this.isInit)
			return;
		LogUtil.d(TAG, "-----locationNewLatlng-----");
		this.updateToNewLocation(latlng);
	}

	/**
	 * 更新位置信息并刷新地图
	 * 
	 * @param latlng
	 *          位置经纬度
	 */
	protected abstract void updateToNewLocation(MyLatLng latlng);

	/**
	 * 获得当前图层的显示图像
	 * 
	 * @return
	 */
	public Bitmap getBitmap() {
		this.buildDrawingCache();
		return this.getDrawingCache();
	}

	/** 绘制图层显示的图片 */
	protected abstract void getOverlayItems();

	/**
	 * 初始化图层数据
	 */
	public void initOverlay() {
		this.width = this.getMeasuredWidth();
		this.height = this.getMeasuredHeight();
		this.checkRect.left = -(width / 4 * 3);
		this.checkRect.right = width * 2 - width / 4;
		this.checkRect.top = -(height / 4 * 3);
		this.checkRect.bottom = height * 2 - height / 4;
		this.isInit = true;
	}

	private static class MyHandler extends Handler {
		private WeakReference<BaseMapOverlay> reference;

		public MyHandler(BaseMapOverlay overlay) {
			this.reference = new WeakReference<BaseMapOverlay>(overlay);
		}

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case REFLASH_VIEW:
				BaseMapOverlay overlay = reference.get();
				if (!isDrawing && !isGetOverlayItems)
					overlay.invalidate();
				break;
			}
		}

	};

	/**
	 * 关闭显示的弹出窗口
	 */
	public abstract void closeShowPopWindow();

	/**
	 * 销毁对象处理
	 */
	public abstract void onDestroy();

	/**
	 * 恢复操作
	 */
	public abstract void onResume();

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (this.mDrawThread == null) {
			this.mDrawThread = new DrawThread();
			this.mDrawThread.start();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (this.mDrawThread != null) {
			this.mDrawThread.stopThread();
			this.mDrawThread = null;
		}
	}

	/**
	 * 绘图线程
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class DrawThread extends Thread {
		/** 绘图间隔时长 */
		private static final int INTERVAL_TIME = 200;
		/** 是否暂停当前线程 */
		private boolean isStop = false;

		@Override
		public void run() {
			while (!isStop) {
				mHander.sendEmptyMessage(REFLASH_VIEW);
				try {
					Thread.sleep(INTERVAL_TIME);
				} catch (InterruptedException e) {
				}
			}
		}

		public void stopThread() {
			this.isStop = true;
			this.interrupt();
		}

	}
}
