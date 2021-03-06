package com.walktour.gui.map.googlemap.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.walktour.gui.R;
import com.walktour.gui.map.googlemap.tileprovider.TileSource;
import com.walktour.gui.map.googlemap.utils.ScaleBarDrawable;
import com.walktour.gui.map.googlemap.utils.Ut;

import org.andnav.osm.util.GeoPoint;

import java.util.List;

/**
 * 地图View控件<BR>
 * 负责地图重绘，展现，控制
 * 
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-7]
 */
public class MapView extends RelativeLayout {
	private final TileView mTileView;
	private final MapController mController;
	private IMoveListener mMoveListener;

	@SuppressWarnings("deprecation")
	public MapView(Context context, int sideInOutButtons, int scaleBarVisible) {
		super(context);

		mController = new MapController();
		mTileView = new TileView(context);
		mMoveListener = null;
		addView(mTileView, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		displayZoomControls(sideInOutButtons);
		// displaySwitchLocationOnline();
		if (scaleBarVisible == 1) {
			final ImageView ivScaleBar = new ImageView(getContext());
			final ScaleBarDrawable dr = new ScaleBarDrawable(context, this, 0);
			ivScaleBar.setImageDrawable(dr);
			final RelativeLayout.LayoutParams scaleParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			// scaleParams.addRule(RelativeLayout.RIGHT_OF, R.id.whatsnew);
			scaleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			scaleParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			addView(ivScaleBar, scaleParams);
		}

		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	public MapView(Context context) {
		this(context, 1, 1);

	}

	@SuppressWarnings("deprecation")
	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MapView);

		mController = new MapController();
		mTileView = new TileView(context);
		addView(mTileView, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		final int sideBottom = a.getInt(R.styleable.MapView_SideInOutButtons, 0);
		displayZoomControls(sideBottom);

		if (a.getInt(R.styleable.MapView_SideInOutButtons, 0) == 1) {
			final ImageView ivScaleBar = new ImageView(getContext());
			final ScaleBarDrawable dr = new ScaleBarDrawable(context, this, 0);
			ivScaleBar.setImageDrawable(dr);
			final RelativeLayout.LayoutParams scaleParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			// scaleParams.addRule(RelativeLayout.RIGHT_OF, R.id.whatsnew);
			scaleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			scaleParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			addView(ivScaleBar, scaleParams);
		}

		a.recycle();
	}

	public TileView getTileView() {
		return mTileView;
	}

	public class MapController {
		public void setCenter(GeoPoint point) {
			mTileView.setMapCenter(point);
		}

		public void setZoom(int zoom) {
			mTileView.setZoomLevel(zoom);
		}

		public void zoomOut() {
			mTileView.setZoomLevel(mTileView.getZoomLevel() - 1);
		}

		public void zoomIn() {
			mTileView.setZoomLevel(mTileView.getZoomLevel() + 1);
		}
	}

	public MapController getController() {
		return mController;
	}

	public void setTileSource(TileSource tilesource) {
		mTileView.setTileSource(tilesource);
	}

	public TileSource getTileSource() {
		return mTileView.getTileSource();
	}

	public void displayZoomControls(final boolean takeFocus) {
		displayZoomControls(1);
	}

	/**
	 * 显示控制地图缩放按钮<BR>
	 * [功能详细描述]
	 * 
	 * @param SideInOutButtons
	 */
	public void displayZoomControls(final int SideInOutButtons) {
		if (SideInOutButtons == 0)
			return;

		final ImageView ivZoomOut = new ImageView(getContext());
		ivZoomOut.setImageResource(R.drawable.btn_zoom_down);
		ivZoomOut.setId(R.id.whatsnew);
		final RelativeLayout.LayoutParams zoomoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		zoomoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		zoomoutParams.addRule((SideInOutButtons == 2 ? false : true) ? RelativeLayout.ALIGN_PARENT_TOP
				: RelativeLayout.ALIGN_PARENT_BOTTOM);
		addView(ivZoomOut, zoomoutParams);

		final ImageView ivZoomIn = new ImageView(getContext());
		ivZoomIn.setImageResource(R.drawable.btn_zoom_up);
		final RelativeLayout.LayoutParams zoominParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		zoominParams.addRule(RelativeLayout.RIGHT_OF, R.id.whatsnew);
		zoominParams.addRule((SideInOutButtons == 2 ? false : true) ? RelativeLayout.ALIGN_PARENT_TOP
				: RelativeLayout.ALIGN_PARENT_BOTTOM);
		addView(ivZoomIn, zoominParams);
		ivZoomIn.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View v) {
				mTileView.setZoomLevel(mTileView.getZoomLevel() + 1);
				if (mMoveListener != null)
					mMoveListener.onZoomDetected();
			}
		});
		ivZoomIn.setOnLongClickListener(new OnLongClickListener() {
			// @Override
			public boolean onLongClick(View v) {
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
				final int zoom = Integer.parseInt(pref.getString("pref_zoommaxlevel", "17"));
				Ut.d("ZoomIn OnLongClick pref_zoomminlevel=" + zoom);
				if (zoom > 0) {
					mTileView.setZoomLevel(zoom - 1);
					if (mMoveListener != null)
						mMoveListener.onZoomDetected();
				}
				return true;
			}
		});

		ivZoomOut.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View v) {
				mTileView.setZoomLevel(mTileView.getZoomLevel() - 1);
				if (mMoveListener != null)
					mMoveListener.onZoomDetected();
			}
		});
		ivZoomOut.setOnLongClickListener(new OnLongClickListener() {
			// @Override
			public boolean onLongClick(View v) {
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
				final int zoom = Integer.parseInt(pref.getString("pref_zoomminlevel", "10"));
				Ut.d("ZoomOut OnLongClick pref_zoomminlevel=" + zoom);
				if (zoom > 0) {
					mTileView.setZoomLevel(zoom - 1);
					if (mMoveListener != null)
						mMoveListener.onZoomDetected();
				}
				return true;
			}
		});
	}

	/**
	 * 获取当前缩放级别<BR>
	 * [功能详细描述]
	 * 
	 * @return 缩放级别
	 */
	public int getZoomLevel() {
		return mTileView.getZoomLevel();
	}

	public double getZoomLevelScaled() {
		return mTileView.getZoomLevelScaled();
	}

	public GeoPoint getMapCenter() {
		return mTileView.getMapCenter();
	}

	public List<TileViewOverlay> getOverlays() {
		return mTileView.getOverlays();
	}

	@Override
	protected ContextMenuInfo getContextMenuInfo() {
		return mTileView.mPoiMenuInfo;
	}

	public void setBearing(float bearing) {
		mTileView.setBearing(bearing);
	}

	public void setMoveListener(IMoveListener moveListener) {
		mMoveListener = moveListener;
		mTileView.setMoveListener(moveListener);
	}

	public double getTouchScale() {
		return mTileView.mTouchScale;
	}
}
