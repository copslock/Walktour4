package com.walktour.mapextention.ibwave;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import com.jhlabs.map.java.Point2D;
import com.jhlabs.map.java.Rectangle2D;
import com.jhlabs.map.proj.Projection;
import com.walktour.base.util.LogUtil;

import java.util.ArrayList;

public class ViewPort {
	private static final String TAG = "ViewPort";
	private Rectangle2D.Double mViewArea;
	private RectF mClientRect = new RectF();
	private double mScale;

	public RTree mLabelTree;
	public ArrayList<BoundedObject> mResults;

	private static ViewPort mViewPort = null;

	private Projection mProj;

	public Point mDownPoint;
	public Point2D.Double mDownPoint2D = new Point2D.Double();
	public Point mMovePoint;

	public Projection getProj() {
		return mProj;
	}

	public void setProj(Projection mProj) {
		this.mProj = mProj;
	}

	public static ViewPort getInstance() {
		if (mViewPort == null) {
			mViewPort = new ViewPort();
		}
		return mViewPort;
	}

	public ViewPort() {
		mViewArea = new Rectangle2D.Double();
		this.mScale = 1;
		mClientRect.left = 0;
		mClientRect.right = 300;
		mClientRect.top = 0;
		mClientRect.bottom = 400;
		mViewArea.x = 0;
		mViewArea.y = 0;
		mViewArea.width = mClientRect.width();
		mViewArea.height = mClientRect.height();
	}

	public void BeforeDraw() {
		mLabelTree = new RTree();
		mResults = new ArrayList<BoundedObject>();
	}

	public void AfterDraw() {
		mLabelTree = null;
		mResults = null;
	}

	public Rectangle2D.Double getViewArea() {
		return mViewArea;
	}

	public void setViewArea(Rectangle2D.Double viewArea) {
		mViewArea.setRect(viewArea);
		calcScale();
	}

	public float getWidth() {
		return mClientRect.width();
	}

	public void setWidth(float mWidth) {
		mClientRect.right = mClientRect.left + mWidth;
	}

	public float getHeight() {
		return mClientRect.height();
	}

	public void setHeight(float mHeight) {
		mClientRect.bottom = mClientRect.top + mHeight;
	}

	public void resize(float ix, float iy, float iWidth, float iHeight) {
		if (mClientRect.left == ix && mClientRect.top == iy && mClientRect.width() == iWidth
				&& mClientRect.height() == iHeight) {
			return;
		}
		mClientRect.left = ix;
		mClientRect.top = iy;
		mClientRect.right = mClientRect.left + iWidth;
		mClientRect.bottom = mClientRect.top + iHeight;
		calcScale();
	}

	public float getx() {
		return mClientRect.left;
	}

	public void setx(float mx) {
		mClientRect.left = mx;
	}

	public float gety() {
		return mClientRect.top;
	}

	public void sety(float my) {
		mClientRect.top = my;
	}

	public void DPToVP(Point2D.Double dp, PointF vp) {
		this.DPToVP(dp, vp, false);
	}

	public void DPToVP(Point2D.Double dp, PointF vp, boolean showLog) {
		vp.x = (float) Math.floor((dp.x - mViewArea.x) / this.mScale);
		vp.y = (float) Math.floor((mViewArea.y - dp.y) / this.mScale);
		if (showLog) {
			LogUtil.d(TAG, "---------DPToVP--------");
			LogUtil.d(TAG, "mViewArea x = " + mViewArea.x + " y = " + mViewArea.y);
			LogUtil.d(TAG, "mScale = " + mScale);
			LogUtil.d(TAG, "dp x = " + dp.x + " y = " + dp.y);
			LogUtil.d(TAG, "vp x = " + vp.x + " y = " + vp.y);
		}
	}

	public void VPToDP(PointF vp, Point2D.Double dp) {
		dp.x = vp.x * this.mScale + mViewArea.x;
		dp.y = mViewArea.y - vp.y * this.mScale;
		// LogUtil.d(TAG, "---------VPToDP--------");
		// LogUtil.d(TAG, "mViewArea x = " + mViewArea.x + " y = " + mViewArea.y);
		// LogUtil.d(TAG, "mScale = " + mScale);
		// LogUtil.d(TAG, "vp x = " + vp.x + " y = " + vp.y);
		// LogUtil.d(TAG, "dp x = " + dp.x + " y = " + dp.y);
	}

	/**
	 * 计算缩放比例
	 */
	private void calcScale() {
		if (mClientRect.width() == 0 || mClientRect.height() == 0) {
			mScale = 1;
			return;
		}
		double xScale = mViewArea.width / mClientRect.width();
		double yScale = mViewArea.height / mClientRect.height();
		mScale = Math.max(xScale, yScale);
		mViewArea.width = mClientRect.width() * mScale;
		mViewArea.height = mClientRect.height() * mScale;
	}

	/**
	 * 移动图像
	 * 
	 * @param downPoint2D
	 * @param pt
	 */
	public void move(Point2D.Double downPoint2D, Point pt) {
		Point2D.Double pt2d = new Point2D.Double();
		pt2d.x = pt.x * mScale;
		pt2d.y = pt.y * mScale;
		mViewArea.x = downPoint2D.x - pt2d.x;
		mViewArea.y = downPoint2D.y + pt2d.y;
	}

	/**
	 * 缩放图片
	 * 
	 * @param downViewArea2D
	 * @param dScale
	 */
	public void zoom(Rectangle2D.Double downViewArea2D, double dScale) {
		double dWidth = downViewArea2D.width / dScale;
		double dHeight = downViewArea2D.height / dScale;
		mViewArea.x = downViewArea2D.x + (downViewArea2D.width - dWidth) / 2;
		mViewArea.y = downViewArea2D.y - (downViewArea2D.height - dHeight) / 2;
		/*
		 * mViewArea.x = DownViewArea2D.x - dWidth; mViewArea.y = DownViewArea2D.y +
		 * dHeight;
		 */
		mViewArea.width = dWidth;
		mViewArea.height = dHeight;
		calcScale();
	}

	public RectF getClientRect() {
		return mClientRect;
	}
}
