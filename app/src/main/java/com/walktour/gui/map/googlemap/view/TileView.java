package com.walktour.gui.map.googlemap.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.walktour.gui.map.googlemap.kml.Track.TrackPoint;
import com.walktour.gui.map.googlemap.reflection.OnExGestureListener;
import com.walktour.gui.map.googlemap.reflection.VerGestureDetector;
import com.walktour.gui.map.googlemap.reflection.VerScaleGestureDetector;
import com.walktour.gui.map.googlemap.tileprovider.MessageHandlerConstants;
import com.walktour.gui.map.googlemap.tileprovider.TileSource;
import com.walktour.gui.map.googlemap.utils.Ut;

import org.andnav.osm.util.BoundingBoxE6;
import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.util.MyMath;
import org.andnav.osm.views.util.Util;
import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;

import java.util.ArrayList;
import java.util.List;

public class TileView extends View {
	private static final int LATITUDE = 0;
	private static final int LONGITUDE = 1;

	private int mLatitudeE6 = 0, mLongitudeE6 = 0;
	private int mZoom = 0;
	private float mBearing = 0;
	final Paint mPaint = new Paint();
	final Matrix mMatrixBearing = new Matrix();
	final Rect mRectDraw = new Rect();

	private boolean mStopProcessing;

	public double mTouchScale = 1;

	private TileSource mTileSource;
	private TileMapHandler mTileMapHandler = new TileMapHandler();
	protected final List<TileViewOverlay> mOverlays = new ArrayList<TileViewOverlay>();
	OnExGestureListener sdf;

	private GestureDetector mDetector = VerGestureDetector.newInstance().getGestureDetector(getContext(),
			new TouchListener());
	private VerScaleGestureDetector mScaleDetector = VerScaleGestureDetector.newInstance(getContext(),
			new ScaleListener());

	private class ScaleListener implements VerScaleGestureDetector.OnGestureListener {

		public void onScale(double aScaleFactor) {
			mTouchScale = aScaleFactor;
			if (mMoveListener != null)
				mMoveListener.onZoomDetected();

			postInvalidate();
		}

		public void onScaleEnd() {
			int zoom = 0;
			if (mTouchScale > 1)
				zoom = getZoomLevel() + (int) Math.round(mTouchScale) - 1;
			else
				zoom = getZoomLevel() - (int) Math.round(1 / mTouchScale) + 1;

			mTouchScale = 1;
			setZoomLevel(zoom);
		}

	}

	private class TouchListener implements OnExGestureListener {
		public boolean onDown(MotionEvent e) {
			Ut.d("onDown");
			for (TileViewOverlay osmvo : mOverlays) {
				if (osmvo.onDown(e, TileView.this))
					break;
			}

			return true;
		}

		public boolean onSingleTapUp(MotionEvent e) {
			Ut.d("onSingleTapUp");
			// GeoPoint geoPoint = getProjection().fromPixels(e.getX(), e.getY());
			// LogUtil.w("><<><<<><<><><<><>", "x:"+e.getX()+"   y:"+ e.getY()
			// +"---------lon:"+geoPoint.getLongitude()+"----lat:"+geoPoint.getLatitude());
			return false;
		}

		public boolean onSingleTapConfirmed(MotionEvent e) {
			Ut.d("onSingleTapConfirmed");
			/*
			 * for (TileViewOverlay osmvo : mOverlays){ if (osmvo.onSingleTapUp(e,
			 * TileView.this)) { invalidate(); return true; } }
			 */
			for (int i = mOverlays.size() - 1; i >= 0; i--) {
				if (mOverlays.get(i).onSingleTapUp(e, TileView.this)) {
					invalidate();
					return true;
				}
			}
			invalidate();
			return false;
		}

		public void onLongPress(MotionEvent e) {
			for (TileViewOverlay osmvo : mOverlays) {
				osmvo.onLongPress(e, TileView.this);
			}

			showContextMenu();
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			for (TileViewOverlay osmvo : mOverlays) {
				if (osmvo.onScroll(e1, e2, distanceX, distanceY, TileView.this))
					return false;
			}

			final float aRotateToAngle = 360 - mBearing;
			final int viewWidth_2 = TileView.this.getWidth() / 2;
			final int viewHeight_2 = TileView.this.getHeight() / 2;
			final int TouchMapOffsetX = (int) (Math.sin(Math.toRadians(aRotateToAngle)) * (distanceY / mTouchScale))
					+ (int) (Math.cos(Math.toRadians(aRotateToAngle)) * (distanceX / mTouchScale));
			final int TouchMapOffsetY = (int) (Math.cos(Math.toRadians(aRotateToAngle)) * (distanceY / mTouchScale))
					- (int) (Math.sin(Math.toRadians(aRotateToAngle)) * (distanceX / mTouchScale));
			final GeoPoint newCenter = TileView.this.getProjection().fromPixels(viewWidth_2 + TouchMapOffsetX,
					viewHeight_2 + TouchMapOffsetY);
			TileView.this.setMapCenter(newCenter);

			if (mMoveListener != null)
				mMoveListener.onMoveDetected();

			return false;
		}

		public boolean onDoubleTap(MotionEvent e) {
			if (mBearing != 0) {
				mBearing = 0;
			} else {
				final GeoPoint newCenter = TileView.this.getProjection().fromPixels(e.getX(), e.getY());
				setMapCenter(newCenter);

				setZoomLevel(getZoomLevel() + 1);
			}

			return true;
		}

		public void onShowPress(MotionEvent e) {
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}

		public void onUp(MotionEvent e) {
			Ut.d("onUp");
			for (TileViewOverlay osmvo : mOverlays) {
				osmvo.onUp(e, TileView.this);
			}
		}

		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}
	}

	private class TileMapHandler extends Handler {

		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case MessageHandlerConstants.MAPTILEFSLOADER_SUCCESS_ID:
				invalidate();
				break;
			case MessageHandlerConstants.MAPTILEFSLOADER_INDEXIND_SUCCESS_ID:
				mTileSource.postIndex();
				setZoomLevel(getZoomLevel());
				if (mMoveListener != null)
					mMoveListener.onZoomDetected();
				break;
			// case OpenStreetMapTileFilesystemProvider.ERROR_MESSAGE:
			// Message.obtain(mMainActivityCallbackHandler,
			// OpenStreetMapTileFilesystemProvider.ERROR_MESSAGE, msg.obj)
			// .sendToTarget();
			// break;
			// case OpenStreetMapTileFilesystemProvider.INDEXIND_SUCCESS_ID:
			// if (mZoomLevel > mRendererInfo.ZOOM_MAXLEVEL)
			// mZoomLevel = mRendererInfo.ZOOM_MAXLEVEL;
			// if (mZoomLevel < mRendererInfo.ZOOM_MINLEVEL)
			// mZoomLevel = mRendererInfo.ZOOM_MINLEVEL;
			//
			// Message.obtain(mMainActivityCallbackHandler,
			// R.id.set_title).sendToTarget();
			//
			// invalidate();
			// break;
			}
		}
	}

	public TileView(Context context) {
		super(context);

		mPaint.setAntiAlias(true);

		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	public PoiMenuInfo mPoiMenuInfo = new PoiMenuInfo(-1);

	public class PoiMenuInfo implements ContextMenuInfo {
		public int MarkerIndex;
		public GeoPoint EventGeoPoint;

		public PoiMenuInfo(int markerIndex) {
			super();
			MarkerIndex = markerIndex;
		}
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		mScaleDetector.onTouchEvent(event);

		boolean result = mDetector.onTouchEvent(event);
		if (!result) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		for (TileViewOverlay osmvo : this.mOverlays)
			if (osmvo.onKeyDown(keyCode, event, this))
				return true;

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDraw(Canvas c) {
		c.save();

		final float aRotateToAngle = 360 - mBearing;
		c.rotate(aRotateToAngle, this.getWidth() / 2, this.getHeight() / 2);

		c.drawRGB(255, 255, 255);

		if (mTileSource != null) {
			final int tileSizePxNotScale = mTileSource.getTileSizePx(mZoom);
			final int tileSizePx = (int) (tileSizePxNotScale * mTouchScale);
			final int[] centerMapTileCoords = Util.getMapTileFromCoordinates(this.mLatitudeE6, this.mLongitudeE6, mZoom,
					null, mTileSource.PROJECTION);

			/*
			 * Calculate the Latitude/Longitude on the left-upper ScreenCoords of the
			 * center MapTile. So in the end we can determine which MapTiles we
			 * additionally need next to the centerMapTile.
			 */
			// final Point upperLeftCornerOfCenterMapTileNotScale =
			// getUpperLeftCornerOfCenterMapTileInScreen(
			// centerMapTileCoords, tileSizePxNotScale, null);
			//
			// final int centerMapTileScreenLeftNotScale =
			// upperLeftCornerOfCenterMapTileNotScale.x;
			// final int centerMapTileScreenTopNotScale =
			// upperLeftCornerOfCenterMapTileNotScale.y;
			// final int centerMapTileScreenRightNotScale =
			// centerMapTileScreenLeftNotScale
			// + tileSizePxNotScale;
			// final int centerMapTileScreenBottomNotScale =
			// centerMapTileScreenTopNotScale
			// + tileSizePxNotScale;

			final Point upperLeftCornerOfCenterMapTile = getUpperLeftCornerOfCenterMapTileInScreen(centerMapTileCoords,
					tileSizePx, null);
			final int centerMapTileScreenLeft = upperLeftCornerOfCenterMapTile.x;
			final int centerMapTileScreenTop = upperLeftCornerOfCenterMapTile.y;

			final int mapTileUpperBound = mTileSource.getTileUpperBound(mZoom);
			final int[] mapTileCoords = new int[] { centerMapTileCoords[LATITUDE], centerMapTileCoords[LONGITUDE] };

			// mTileSource.getTileProvider().ResizeCashe((additionalTilesNeededToTopOfCenter+additionalTilesNeededToBottomOfCenter+1)*(additionalTilesNeededToLeftOfCenter+additionalTilesNeededToRightOfCenter+1));
			// Region reg = new Region(getLeft()+20, getTop()+20,
			// getLeft()+getWidth()-20, getTop()+getHeight()-20);
			// Path p = new Path();
			// p.addRect(getLeft()+20, getTop()+20, getLeft()+getWidth()-20,
			// getTop()+getHeight()-20, Direction.CW);

			boolean tileIn = true;
			int x = 0, y = 0, radius = 0, tilecnt = 0;
			mMatrixBearing.reset();
			mMatrixBearing.setRotate(360 - mBearing, this.getWidth() / 2, this.getHeight() / 2);

			while (tileIn) {
				tileIn = false;

				for (x = -radius; x <= radius; x++) {
					for (y = -radius; y <= radius; y++) {
						if (x != -radius && x != radius && y != -radius && y != radius)
							continue;

						mapTileCoords[LATITUDE] = MyMath.mod(centerMapTileCoords[LATITUDE] + y, mapTileUpperBound);
						mapTileCoords[LONGITUDE] = MyMath.mod(centerMapTileCoords[LONGITUDE] + x, mapTileUpperBound);

						final int tileLeft = centerMapTileScreenLeft + (x * tileSizePx);
						final int tileTop = centerMapTileScreenTop + (y * tileSizePx);
						mRectDraw.set(tileLeft, tileTop, tileLeft + tileSizePx, tileTop + tileSizePx);

						// if (!reg.quickReject(r)) {
						float arr[] = { mRectDraw.left, mRectDraw.top, mRectDraw.right, mRectDraw.top, mRectDraw.right,
								mRectDraw.bottom, mRectDraw.left, mRectDraw.bottom, mRectDraw.left, mRectDraw.top };
						mMatrixBearing.mapPoints(arr);

						if (Ut.Algorithm.isIntersected((int) (getWidth() * (1 - mTouchScale) / 2), (int) (getHeight()
								* (1 - mTouchScale) / 2), (int) (getWidth() * (1 + mTouchScale) / 2), (int) (getHeight()
								* (1 + mTouchScale) / 2), arr)) {
							tileIn = true;
							tilecnt++;

							final Bitmap currentMapTile = this.mTileSource.getTile(mapTileCoords[LONGITUDE], mapTileCoords[LATITUDE],
									mZoom);
							if (currentMapTile != null) {
								if (!currentMapTile.isRecycled())
									c.drawBitmap(currentMapTile, null, mRectDraw, mPaint);

								if (!OpenStreetMapViewConstants.DEBUGMODE) {
									c.drawLine(tileLeft, tileTop, tileLeft + tileSizePx, tileTop, mPaint);
									c.drawLine(tileLeft, tileTop, tileLeft, tileTop + tileSizePx, mPaint);
									c.drawText("y x = " + mapTileCoords[LATITUDE] + " " + mapTileCoords[LONGITUDE] + " zoom " + mZoom
											+ " ", tileLeft + 5, tileTop + 15, mPaint);
								}

							}
						}
					}
				}

				radius++;
			}

			mTileSource.getTileProvider().ResizeCashe(tilecnt);

			/* Draw all Overlays. */
			for (TileViewOverlay osmvo : this.mOverlays)
				osmvo.onManagedDraw(c, this);
		}

		c.restore();

		super.onDraw(c);
	}

	public List<TileViewOverlay> getOverlays() {
		return mOverlays;
	}

	public void setTileSource(TileSource tilesource) {
		if (mTileSource != null)
			mTileSource.Free();
		mTileSource = tilesource;
		mTileSource.setHandler(mTileMapHandler);
		setZoomLevel(getZoomLevel());
		invalidate();
	}

	public TileSource getTileSource() {
		return mTileSource;
	}

	public void setMapCenter(final GeoPoint aCenter) {
		this.mLatitudeE6 = aCenter.getLatitudeE6();
		this.mLongitudeE6 = aCenter.getLongitudeE6();

		this.postInvalidate();
	}

	public GeoPoint getMapCenter() {
		return new GeoPoint(this.mLatitudeE6, this.mLongitudeE6);
	}

	public int getZoomLevel() {
		return mZoom;
	}

	public double getZoomLevelScaled() {
		if (mTouchScale == 1)
			return getZoomLevel();
		else if (mTouchScale > 1)
			return getZoomLevel() + Math.round(mTouchScale) - 1;
		else
			return getZoomLevel() - Math.round(1 / mTouchScale) + 1;
	}

	/**
	 * ??????????????????<BR>
	 * [??????????????????]
	 * 
	 * @param zoom
	 */
	public void setZoomLevel(final int zoom) {
		if (mTileSource == null)
			mZoom = zoom;
		else
			mZoom = Math.max(mTileSource.ZOOM_MINLEVEL, Math.min(mTileSource.ZOOM_MAXLEVEL, zoom));

		if (mMoveListener != null)
			mMoveListener.onZoomDetected();

		this.postInvalidate();
	}

	public void setBearing(final float aBearing) {
		this.mBearing = aBearing;
	}

	public float getBearing() {
		return this.mBearing;
	}

	/**
	 * ????????????????????????<BR>
	 * [??????????????????]
	 * 
	 * @return
	 */
	public OpenStreetMapViewProjection getProjection() {
		return new OpenStreetMapViewProjection();
	}

	public class OpenStreetMapViewProjection {

		final int viewWidth;
		final int viewHeight;
		final BoundingBoxE6 bb;
		final int zoomLevel;
		final int tileSizePx;
		final int[] centerMapTileCoords;
		final Point upperLeftCornerOfCenterMapTile;

		public OpenStreetMapViewProjection() {
			viewWidth = getWidth();
			viewHeight = getHeight();

			/*
			 * Do some calculations and drag attributes to local variables to save
			 * some performance.
			 */
			zoomLevel = mZoom; // LATER Draw to
													// attributes and so
													// make it only
													// 'valid' for a
													// short time.
			tileSizePx = (int) (mTileSource.getTileSizePx(zoomLevel) * mTouchScale);

			/*
			 * Get the center MapTile which is above this.mLatitudeE6 and
			 * this.mLongitudeE6 .
			 */
			centerMapTileCoords = Util.getMapTileFromCoordinates(mLatitudeE6, mLongitudeE6, zoomLevel, null,
					mTileSource.PROJECTION);
			upperLeftCornerOfCenterMapTile = getUpperLeftCornerOfCenterMapTileInScreen(centerMapTileCoords, tileSizePx, null);

			bb = getDrawnBoundingBoxE6();
		}

		/**
		 * Converts x/y ScreenCoordinates to the underlying GeoPoint.
		 *
		 * @param x
		 * @param y
		 * @return GeoPoint under x/y.
		 */
		public GeoPoint fromPixels(float x, float y) {
			/* Subtract the offset caused by touch. */
			// LogUtil.d(DEBUGTAG,
			// "x = "+x+" mTouchMapOffsetX = "+mTouchMapOffsetX+"   ");

			x -= 0;
			y -= 0;

			// int xx =
			// centerMapTileCoords[0]*tileSizePx+(int)x-upperLeftCornerOfCenterMapTile.x;
			// int asd = Util.x2lon(xx, zoomLevel, tileSizePx);
			GeoPoint p = bb.getGeoPointOfRelativePositionWithLinearInterpolation(x / viewWidth, y / viewHeight);

			// LogUtil.d(DEBUGTAG,
			// "lon "+p.getLongitudeE6()+" "+xx+" "+asd+" OffsetX = "+mTouchMapOffsetX);
			// LogUtil.d(DEBUGTAG,
			// "	"+centerMapTileCoords[0]+" "+tileSizePx+" "+x+" "+upperLeftCornerOfCenterMapTile.x);
			// p.setLongitudeE6(asd);

			// for(int i =0; i<=tileSizePx*(1<<zoomLevel); i++){int Q = Util.x2lon(i,
			// zoomLevel, tileSizePx);LogUtil.d(DEBUGTAG, "lon "+i+" "+Q);}

			return p;
		}

		/**
		 * ????????????????????????????????????<BR>
		 * [??????????????????]
		 * 
		 * @param x
		 *          X??????
		 * @param y
		 *          Y??????
		 * @param bearing
		 *          ?????????
		 * @return ???????????????
		 */
		public GeoPoint fromPixels(float x, float y, double bearing) {
			final int x1 = (int) (x - getWidth() / 2);
			final int y1 = (int) (y - getHeight() / 2);
			final double hypot = Math.hypot(x1, y1);
			final double angle = -1 * Math.signum(y1) * Math.toDegrees(Math.acos(x1 / hypot));
			final double angle2 = angle - bearing;
			final int x2 = (int) (Math.cos(Math.toRadians(angle2)) * hypot);
			final int y2 = (int) (Math.sin(Math.toRadians(angle2 - 180)) * hypot);

			return fromPixels((float) (getWidth() / 2 + x2), (float) (getHeight() / 2 + y2));
		}

		private static final int EQUATORCIRCUMFENCE = 40075676; // 40075004;

		public float metersToEquatorPixels(final float aMeters) {
			return aMeters / EQUATORCIRCUMFENCE * mTileSource.getTileSizePx(mZoom);
		}

		/**
		 * Converts a GeoPoint to its ScreenCoordinates. <br/>
		 * <br/>
		 * <b>CAUTION</b> ! Conversion currently has a large error on
		 * <code>zoomLevels <= 7</code>.<br/>
		 * The Error on ZoomLevels higher than 7, the error is below
		 * <code>1px</code>.<br/>
		 * LATER: Add a linear interpolation to minimize this error.
		 *
		 * <PRE>
		 * Zoom 	Error(m) 	Error(px)
		 * 11 	6m 	1/12px
		 * 10 	24m 	1/6px
		 * 8 	384m 	1/2px
		 * 6 	6144m 	3px
		 * 4 	98304m 	10px
		 * </PRE>
		 *
		 * @param in
		 *          the GeoPoint you want the onScreenCoordinates of.
		 * @param reuse
		 *          just pass null if you do not have a Point to be 'recycled'.
		 * @return the Point containing the approximated ScreenCoordinates of the
		 *         GeoPoint passed.
		 */
		public Point toPixels(final GeoPoint in, final Point reuse) {
			return toPixels(in, reuse, true);
		}

		public Point toPixels(final GeoPoint in, final double bearing, final Point reuse) {
			final Point point = toPixels(in, reuse, true);
			final Point out = (reuse != null) ? reuse : new Point();

			final int x1 = point.x - getWidth() / 2;
			final int y1 = point.y - getHeight() / 2;
			final double hypot = Math.hypot(x1, y1);
			final double angle = -1 * Math.signum(y1) * Math.toDegrees(Math.acos(x1 / hypot));
			final double angle2 = angle + bearing;
			final int x2 = (int) (Math.cos(Math.toRadians(angle2)) * hypot);
			final int y2 = (int) (Math.sin(Math.toRadians(angle2 - 180)) * hypot);

			out.set(getWidth() / 2 + x2, getHeight() / 2 + y2);
			return out;
		}

		protected Point toPixels(final GeoPoint in, final Point reuse, final boolean doGudermann) {

			final Point out = (reuse != null) ? reuse : new Point();

			final int[] underGeopointTileCoords = Util.getMapTileFromCoordinates(in.getLatitudeE6(), in.getLongitudeE6(),
					zoomLevel, null, mTileSource.PROJECTION);

			/*
			 * Calculate the Latitude/Longitude on the left-upper ScreenCoords of the
			 * MapTile.
			 */
			final BoundingBoxE6 bb = Util.getBoundingBoxFromMapTile(underGeopointTileCoords, zoomLevel,
					mTileSource.PROJECTION);

			final float[] relativePositionInCenterMapTile;
			if (doGudermann && zoomLevel < 7)
				relativePositionInCenterMapTile = bb.getRelativePositionOfGeoPointInBoundingBoxWithExactGudermannInterpolation(
						in.getLatitudeE6(), in.getLongitudeE6(), null);
			else
				relativePositionInCenterMapTile = bb.getRelativePositionOfGeoPointInBoundingBoxWithLinearInterpolation(
						in.getLatitudeE6(), in.getLongitudeE6(), null);

			final int tileDiffX = centerMapTileCoords[LONGITUDE] - underGeopointTileCoords[LONGITUDE];
			final int tileDiffY = centerMapTileCoords[LATITUDE] - underGeopointTileCoords[LATITUDE];
			final int underGeopointTileScreenLeft = upperLeftCornerOfCenterMapTile.x - (tileSizePx * tileDiffX);
			final int underGeopointTileScreenTop = upperLeftCornerOfCenterMapTile.y - (tileSizePx * tileDiffY);

			final int x = underGeopointTileScreenLeft + (int) (relativePositionInCenterMapTile[LONGITUDE] * tileSizePx);
			final int y = underGeopointTileScreenTop + (int) (relativePositionInCenterMapTile[LATITUDE] * tileSizePx);

			/* Add up the offset caused by touch. */
			out.set(x + 0, y + 0);
			return out;
		}

		public void StopProcessing() {
			mStopProcessing = true;
		}

		private boolean Stop() {
			if (mStopProcessing) {
				mStopProcessing = false;
				return true;
			}
			return false;
		}

		public Path toPixelsTrackPoints(List<TrackPoint> in, Point baseCoord, GeoPoint baseLocation)
				throws IllegalArgumentException {
			if (in.size() < 2)
				return null;
			// throw new
			// IllegalArgumentException("List of GeoPoints needs to be at least 2.");

			mStopProcessing = false;
			final Path out = new Path();
			final boolean doGudermann = true;

			int i = 0;
			int lastX = 0, lastY = 0;
			for (TrackPoint tp : in) {
				if (Stop()) {
					return null;
				}
				final int[] underGeopointTileCoords = Util.getMapTileFromCoordinates(tp.getLatitudeE6(), tp.getLongitudeE6(),
						zoomLevel, null, mTileSource.PROJECTION);

				/*
				 * Calculate the Latitude/Longitude on the left-upper ScreenCoords of
				 * the MapTile.
				 */
				final BoundingBoxE6 bb = Util.getBoundingBoxFromMapTile(underGeopointTileCoords, zoomLevel,
						mTileSource.PROJECTION);

				final float[] relativePositionInCenterMapTile;
				if (doGudermann && zoomLevel < 7)
					relativePositionInCenterMapTile = bb
							.getRelativePositionOfGeoPointInBoundingBoxWithExactGudermannInterpolation(tp.getLatitudeE6(),
									tp.getLongitudeE6(), null);
				else
					relativePositionInCenterMapTile = bb.getRelativePositionOfGeoPointInBoundingBoxWithLinearInterpolation(
							tp.getLatitudeE6(), tp.getLongitudeE6(), null);

				final int tileDiffX = centerMapTileCoords[LONGITUDE] - underGeopointTileCoords[LONGITUDE];
				final int tileDiffY = centerMapTileCoords[LATITUDE] - underGeopointTileCoords[LATITUDE];
				final int underGeopointTileScreenLeft = upperLeftCornerOfCenterMapTile.x - (tileSizePx * tileDiffX);
				final int underGeopointTileScreenTop = upperLeftCornerOfCenterMapTile.y - (tileSizePx * tileDiffY);

				final int x = underGeopointTileScreenLeft + (int) (relativePositionInCenterMapTile[LONGITUDE] * tileSizePx);
				final int y = underGeopointTileScreenTop + (int) (relativePositionInCenterMapTile[LATITUDE] * tileSizePx);

				/* Add up the offset caused by touch. */
				if (i == 0) {
					out.setLastPoint(x, y);
					lastX = x;
					lastY = y;
					baseCoord.x = x;
					baseCoord.y = y;
					baseLocation.setCoordsE6(tp.getLatitudeE6(), tp.getLongitudeE6());
					i++;
				} else {
					if (Math.abs(lastX - x) > 5 || Math.abs(lastY - y) > 5) {
						out.lineTo(x, y);
						lastX = x;
						lastY = y;
						i++;
					}
				}
			}

			return out;
		}
	}

	private IMoveListener mMoveListener;

	private Point getUpperLeftCornerOfCenterMapTileInScreen(final int[] centerMapTileCoords, final int tileSizePx,
			final Point reuse) {
		final Point out = (reuse != null) ? reuse : new Point();

		final int viewWidth = this.getWidth();
		final int viewWidth_2 = viewWidth / 2;
		final int viewHeight = this.getHeight();
		final int viewHeight_2 = viewHeight / 2;

		/*
		 * Calculate the Latitude/Longitude on the left-upper ScreenCoords of the
		 * center MapTile. So in the end we can determine which MapTiles we
		 * additionally need next to the centerMapTile.
		 */
		final BoundingBoxE6 bb = Util.getBoundingBoxFromMapTile(centerMapTileCoords, this.mZoom, mTileSource.PROJECTION);
		final float[] relativePositionInCenterMapTile = bb
				.getRelativePositionOfGeoPointInBoundingBoxWithLinearInterpolation(this.mLatitudeE6, this.mLongitudeE6, null);

		final int centerMapTileScreenLeft = viewWidth_2
				- (int) (0.5f + (relativePositionInCenterMapTile[LONGITUDE] * tileSizePx));
		final int centerMapTileScreenTop = viewHeight_2
				- (int) (0.5f + (relativePositionInCenterMapTile[LATITUDE] * tileSizePx));

		out.set(centerMapTileScreenLeft, centerMapTileScreenTop);
		return out;
	}

	private BoundingBoxE6 getBoundingBox(final int pViewWidth, final int pViewHeight) {
		/*
		 * Get the center MapTile which is above this.mLatitudeE6 and
		 * this.mLongitudeE6 .
		 */
		final int[] centerMapTileCoords = Util.getMapTileFromCoordinates(this.mLatitudeE6, this.mLongitudeE6, this.mZoom,
				null, this.mTileSource.PROJECTION);

		final BoundingBoxE6 tmp = Util.getBoundingBoxFromMapTile(centerMapTileCoords, this.mZoom, mTileSource.PROJECTION);

		final int mLatitudeSpan_2 = (int) (1.0f * tmp.getLatitudeSpanE6() * pViewHeight / this.mTileSource
				.getTileSizePx(this.mZoom)) / 2;
		final int mLongitudeSpan_2 = (int) (1.0f * tmp.getLongitudeSpanE6() * pViewWidth / this.mTileSource
				.getTileSizePx(this.mZoom)) / 2;

		final int north = this.mLatitudeE6 + mLatitudeSpan_2;
		final int south = this.mLatitudeE6 - mLatitudeSpan_2;
		final int west = this.mLongitudeE6 - mLongitudeSpan_2;
		final int east = this.mLongitudeE6 + mLongitudeSpan_2;

		return new BoundingBoxE6(north, east, south, west);
	}

	public BoundingBoxE6 getDrawnBoundingBoxE6() {
		return getBoundingBox(this.getWidth(), this.getHeight());
	}

	public void setMoveListener(IMoveListener moveListener) {
		mMoveListener = moveListener;
	}

}
