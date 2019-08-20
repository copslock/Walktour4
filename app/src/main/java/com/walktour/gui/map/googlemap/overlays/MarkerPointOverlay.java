package com.walktour.gui.map.googlemap.overlays;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ParameterSetting;
import com.walktour.control.instance.AlertManager;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.gui.R;
import com.walktour.gui.map.googlemap.GoogleMapMainActivity;
import com.walktour.gui.map.googlemap.kml.MarkPoint;
import com.walktour.gui.map.googlemap.utils.Ut;
import com.walktour.gui.map.googlemap.view.TileView;
import com.walktour.gui.map.googlemap.view.TileView.OpenStreetMapViewProjection;
import com.walktour.gui.map.googlemap.view.TileViewOverlay;
import com.walktour.model.AlarmModel;
import com.walktour.model.LocusParamInfo;
import com.walktour.model.MapEvent;
import com.walktour.model.Parameter;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 轨迹点显示层
 * 
 * @author jianchao.wang
 *
 */
public class MarkerPointOverlay extends TileViewOverlay {

	private static final String TAG = "MarkerPointOverlay";
	/** 基站管理状态：未选择基站 **/
	private final static int CONNECTION_NULL = 0;
	/** 基站管理状态：已选择基站但未关联 **/
	private final static int CONNECTION_NO = 1;
	/** 基站管理状态：已选择基站且关联 **/
	private final static int CONNECTION_YES = 2;

	private Context context;

	private ParameterSetting mParameterSet;

	private int mTapIndex = -1;

	private LinearLayout mT;

	private LinearLayout eventDesc;

	private float mDensity;

	protected OnItemTapListener<MarkPoint> mOnItemTapListener;

	/* protected OnItemLongPressListener<MarkPoint> mOnItemLongPressListener; */

	/**
	 * 轨迹点数组
	 */
	private List<MapEvent> locasList;

	protected final Point mMarkerHotSpot;

	/**
     * 
     */
	protected final int mMarkerWidth, mMarkerHeight;

	/**
	 * 画笔
	 */
	private Paint mPaint;

	/**
	 * 坐标转换对象
	 */
	private OpenStreetMapViewProjection projection;

	/**
	 * 事件数组
	 */
	// private List<MapEvent> mapEvents = new ArrayList<MapEvent>();

	/**
	 * 告警数组队列
	 */
	private List<AlarmModel> alarmList = AlertManager.getInstance(context).getMapAlarmList();

	/**
	 * 参数列表
	 */
	private List<Parameter> parameterms;

	/**
	 * 是否显示事件,-1为显示事件,<=0 为显示轨迹,且表示当前轨迹的Index
	 */
	private int showIndex = 0;

	/**
	 * 当前点击的轨迹点
	 */
	private Object clickItem;

	/**
	 * 数据集管理对象
	 */
	private DatasetManager mDatasetMgr;

	private SharedPreferences mPreferences;

	private Drawable marker;
	/** 当前的节点位置,存放上次显示的点 */
	private int currentPostion;
	/**
	 * 搜索到的基站数据,不为空则居中高亮
	 */
	private BaseStation searchBaseStation;

	public int getTapIndex() {
		return mTapIndex;
	}

	public void setTapIndex(int mTapIndex) {
		this.mTapIndex = mTapIndex;
	}

	public MarkerPointOverlay(Context ctx, List<MarkPoint> mItemList, OnItemTapListener<MarkPoint> onItemTapListener) {
		super();
		context = ctx;
		mDatasetMgr = DatasetManager.getInstance(ctx.getApplicationContext());
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		mParameterSet = ParameterSetting.getInstance();
		mParameterSet.initMapLocusShape(ctx.getApplicationContext());

		mTapIndex = -1;
		marker = ctx.getResources().getDrawable(R.drawable.iconmarker);
		this.mMarkerWidth = marker.getIntrinsicWidth();
		this.mMarkerHeight = marker.getIntrinsicHeight();

		this.mMarkerHotSpot = new Point(0, mMarkerHeight);

		this.mOnItemTapListener = onItemTapListener;
		// this.mItemList = mItemList;
		locasList = TraceInfoInterface.traceData.getGpsLocas();

		this.mT = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.poi_descr, null);

		this.mT.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		this.eventDesc = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.even_descr, null);
		this.eventDesc
				.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) ctx).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mDensity = metrics.density;

		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeWidth(3);
		// 添加外圈
		mPaint.setAntiAlias(true);
	}

	@Override
	public void onDraw(Canvas canvas, TileView mapView) {
		projection = mapView.getProjection();
		Point curScreenCoords = new Point();
		if (this.locasList == null || locasList.isEmpty()) {
			// mapEvents.clear();
			return;
		}

		this.checkParamerChange();
		float radius = getLocusRadius();
		boolean isNavigation = mPreferences.getInt(GoogleMapMainActivity.AUTO_FOLLOW_MODE, 0) == 2;

		filterGPSList();
		Point markPoint = null;
		// 绘制轨迹点
		for (int i = locasList.size() - 1; i >= 0; i--) {
			Point point = drawPoint(canvas, i, radius, curScreenCoords, isNavigation);
			if (point != null)
				markPoint = point;
		}

		// 绘制告警事件
		alarmList = new ArrayList<AlarmModel>(AlertManager.getInstance(context).getMapAlarmList());
		for (int j = alarmList.size() - 1; j >= 0; j--) {
			drawAlarm(canvas, alarmList.get(j), curScreenCoords);
		}

		// 绘制点击事件Pop
		if (clickItem != null) {
			drawSelectItem(canvas, clickItem, curScreenCoords);
		}
		if (markPoint != null) {
			marker.setBounds(markPoint.x - mMarkerWidth / 2, markPoint.y - mMarkerHeight, markPoint.x + mMarkerWidth / 2,
					markPoint.y);
			marker.draw(canvas);
		}
		// mapEvents.clear();

	}

	/**
	 * 绘制轨迹点<BR>
	 * [功能详细描述]
	 * 
	 * @param c
	 *          画布
	 * @param postion
	 *          点序号
	 * @param radius
	 *          半径
	 * @param curScreenCoords
	 *          点屏幕像素
	 * @param isNavigation
	 *          是否为导航模式
	 */
	private Point drawPoint(Canvas c, int postion, float radius, Point curScreenCoords, boolean isNavigation) {
		MapEvent mapEvent = locasList.get(postion);
		projection.toPixels(new GeoPoint((int) (mapEvent.getLatitude() * 1e6), (int) (mapEvent.getLongitude() * 1e6)),
				curScreenCoords);
		mPaint.setStyle(Paint.Style.FILL);
		Point markPoint = null;
		int connectStation = this.getConnectStation(mapEvent);
		if (this.currentPostion > 0 && this.currentPostion == postion) {
			markPoint = new Point(curScreenCoords.x, curScreenCoords.y);
		}
		// 如果是测试打点状态
		if (isNavigation) {
			mPaint.setColor(Color.GRAY);
			switch (connectStation) {
			// case CONNECTION_YES:
			// mPaint.setColor(Color.RED);
			// break;
			case CONNECTION_NO:
				mPaint.setColor(Color.GRAY);
				break;
			}
			c.drawCircle((float) curScreenCoords.x, (float) curScreenCoords.y, radius, mPaint);
		} else {
			if (parameterms == null || parameterms.isEmpty()) {
				mPaint.setColor(this.mParameterSet.getGpsColor());
				switch (connectStation) {
				// case CONNECTION_YES:
				// mPaint.setColor(Color.RED);
				// break;
				case CONNECTION_NO:
					mPaint.setColor(Color.GRAY);
					break;
				}
				if (mParameterSet.getLocusShape() == 0) {
					c.drawCircle((float) curScreenCoords.x, (float) curScreenCoords.y, radius, mPaint);
				} else {
					c.drawRect(curScreenCoords.x - radius, curScreenCoords.y - radius, curScreenCoords.x + radius,
							curScreenCoords.y + radius, mPaint);
				}
			} else {
				for (int k = 0; k < parameterms.size(); k++) {
					mPaint.setColor(mapEvent.getParamInfoMap().get(parameterms.get(k).getShowName()) == null ? Color.GRAY
							: mapEvent.getParamInfoMap().get(parameterms.get(k).getShowName()).color);
					switch (connectStation) {
					// case CONNECTION_YES:
					// mPaint.setColor(Color.RED);
					// break;
					case CONNECTION_NO:
						mPaint.setColor(Color.GRAY);
						break;
					}
					if (mParameterSet.getLocusShape() == 0) {
						c.drawCircle((float) curScreenCoords.x + k * radius * 3, (float) curScreenCoords.y, radius, mPaint);
					} else {
						c.drawRect(curScreenCoords.x - radius + k * radius * 3, curScreenCoords.y - radius, curScreenCoords.x
								+ radius + k * radius * 3, curScreenCoords.y + radius, mPaint);
					}
				}
			}
		}

		// 正常测试状态,也即是非回放状态下,将最后一个点置为当前点
		if (!DatasetManager.isPlayback && postion == locasList.size() - 1) {
			marker.setBounds(curScreenCoords.x - mMarkerWidth / 2, curScreenCoords.y - mMarkerHeight, curScreenCoords.x
					+ mMarkerWidth / 2, curScreenCoords.y);
			marker.draw(c);
			// 否则回放状态下
		} else if (DatasetManager.isPlayback) {
			if (mDatasetMgr.currentIndex >= mapEvent.getBeginPointIndex()
					&& mDatasetMgr.currentIndex <= mapEvent.getEndPointIndex()) {
				System.out.println("X:" + curScreenCoords.x + "    Y:" + curScreenCoords.y);
				this.currentPostion = postion;
				markPoint = new Point(curScreenCoords.x, curScreenCoords.y);
			}
		}
		return markPoint;
	}

	/**
	 * 过滤GPS轨迹算法<BR>
	 * 非回放时,过滤GPS,回放则不进行过滤
	 */
	private void filterGPSList() {
		// 计算步长,控制数量再500以内
		if (!DatasetManager.isPlayback) {
			int step = locasList.size() / 500 > 1 ? locasList.size() / 500 : 1;
			int location = step - 1;
			while (step >= 2 && locasList.size() > 500) {
				locasList.remove(location);
				location++;
				if (location >= locasList.size() - 1) {
					break;
				}
			}
		}
	}

	/**
	 * 绘制告警事件<BR>
	 * [功能详细描述]
	 * 
	 * @param canvas
	 * @param alarmModel
	 * @param point
	 */
	private void drawAlarm(Canvas canvas, AlarmModel alarmModel, Point point) {
		MapEvent mapEvent = alarmModel.getMapEvent();
		if (mapEvent == null)
			return;
		projection.toPixels(new GeoPoint((int) (mapEvent.getLatitude() * 1e6), (int) (mapEvent.getLongitude() * 1e6)),
				point);
		mapEvent.setX(point.x);
		mapEvent.setY(point.y);
		Drawable eventDrawable = getCallbm(alarmModel);
		int left = (int) (alarmModel.getMapEvent().getX() - 6 * mDensity);
		int right = (int) (alarmModel.getMapEvent().getX() + 6 * mDensity);
		int top = (int) (alarmModel.getMapEvent().getY() - 6 * mDensity);
		int bottom = (int) (alarmModel.getMapEvent().getY() + 6 * mDensity);
		eventDrawable.setBounds(left, top, right, bottom);
		eventDrawable.draw(canvas);
	}

	/**
	 * 绘制选中的轨迹点或告警事件
	 * 
	 * @param canvas
	 *          画布
	 * @param itemObject
	 *          选择的对象
	 * @param point
	 *          坐标点
	 */
	protected void drawSelectItem(Canvas canvas, Object itemObject, Point point) {
		if (showIndex == -1) {
			if (itemObject instanceof AlarmModel) {
				projection.toPixels(new GeoPoint((int) (((AlarmModel) clickItem).getMapEvent().getLatitude() * 1e6),
						(int) (((AlarmModel) clickItem).getMapEvent().getLongitude() * 1e6)), point);
				AlarmModel alarmModel = (AlarmModel) itemObject;
				final TextView title = (TextView) eventDesc.findViewById(R.id.event_title);
				final TextView content = (TextView) eventDesc.findViewById(R.id.even_content);
				if (alarmModel != null && alarmModel.getAlarm() != null) {

					Drawable eventDrawable = getCallbm(alarmModel);
					int left = (int) (point.x - 6 * mDensity);
					int right = (int) (point.x + 6 * mDensity);
					int top = (int) (point.y - 6 * mDensity);
					int bottom = (int) (point.y + 6 * mDensity);
					eventDrawable.setBounds(left, top, right, bottom);
					eventDrawable.draw(canvas);

					title.setText(alarmModel.getDescription(context));
					content.setText(alarmModel.getMapPopInfo());

					eventDesc.measure(0, 0);
					eventDesc.layout(0, 0, eventDesc.getMeasuredWidth(), eventDesc.getMeasuredHeight());

					canvas.save();
					canvas.translate(point.x - eventDesc.getMeasuredWidth() / 2, point.y - eventDesc.getMeasuredHeight()
							- eventDesc.getTop() - 6 * mDensity);

					eventDesc.draw(canvas);
					canvas.restore();
				}
			}
		} else if (showIndex >= 0) {
			if (itemObject instanceof MapEvent) {
				MapEvent markpoint = (MapEvent) itemObject;
				projection.toPixels(
						new GeoPoint((int) (markpoint.getLatitude() * 1e6), (int) (markpoint.getLongitude() * 1e6)), point);

				float radius = this.getLocusRadius();
				mPaint.setColor(markpoint.getColor());
				mPaint.setStyle(Paint.Style.FILL);
				canvas.drawCircle((float) point.x + showIndex * radius * 3, (float) point.y, radius, mPaint);

				if (OpenStreetMapViewConstants.DEBUGMODE) {
					final int pxUp = 5;
					final int left2 = (int) (point.x - mDensity * (pxUp) + showIndex * radius * 3);
					final int right2 = (int) (point.x + mDensity * (pxUp) + showIndex * radius * 3);
					final int top2 = (int) (point.y - mDensity * (pxUp));
					final int bottom2 = (int) (top2 + mDensity * (10));
					Paint p = new Paint();
					p.setColor(Color.WHITE);
					p.setStrokeWidth(mDensity * 2);
					canvas.drawLine(left2, top2, right2, bottom2, p);
					canvas.drawLine(right2, top2, left2, bottom2, p);

					mPaint.setColor(markpoint.getColor());
					mPaint.setStyle(Paint.Style.STROKE);
					mPaint.setColor(Color.parseColor("#FF7F00"));
					canvas.drawCircle((float) point.x + showIndex * radius * 3, (float) point.y, radius, mPaint);

				}
				final TextView title = (TextView) mT.findViewById(R.id.poi_title);
				final TextView descr = (TextView) mT.findViewById(R.id.descr);
				final TextView coord = (TextView) mT.findViewById(R.id.coord);

				title.setText("Location");
				descr.setText(markpoint.getMapPopInfo());
				if (parameterms == null || parameterms.isEmpty()) {
					coord.setVisibility(View.GONE);
				} else {
					LocusParamInfo locusParamInfo = markpoint.getParamInfoMap().get(parameterms.get(showIndex).getShowName());

					if (locusParamInfo == null || locusParamInfo.paramName == null || locusParamInfo.value == -9999) {
						coord.setVisibility(View.GONE);
					} else {
						coord.setVisibility(View.VISIBLE);
						coord.setText(locusParamInfo.paramName + ":" + locusParamInfo.value);
					}
				}
				mT.measure(0, 0);
				mT.layout(0, 0, mT.getMeasuredWidth(), mT.getMeasuredHeight());

				canvas.save();
				canvas.translate(point.x - mT.getMeasuredWidth() / 2 + showIndex * radius * 3,
						point.y - 4 * mDensity - mT.getMeasuredHeight()/*
																														 * pic.
																														 * getMeasuredHeight
																														 * ( ) - pic .
																														 * getTop ( )
																														 */);
				mT.draw(canvas);
				canvas.restore();

			}
		}
	}

	/**
	 * 判断当前参数是否有更改
	 * 
	 * @return
	 */
	private void checkParamerChange() {
		List<Parameter> params = mParameterSet.getCheckedParamertersByNet(MyPhoneState.getInstance().getCurrentNetForParam(
				this.context));
		if (params == null || params.size() == 0 || this.parameterms == null || this.parameterms.size() != params.size()) {
			this.clickItem = null;
			this.showIndex = 0;
			this.parameterms = params;
			return;
		} else {
			int count = 0;
			for (Parameter param : params) {
				for (Parameter param1 : this.parameterms) {
					if (param.getId().equals(param1.getId())) {
						count++;
					}
				}
			}
			if (params.size() != count) {
				this.clickItem = null;
				this.showIndex = 0;
				this.parameterms = params;
				return;
			}
		}
	}

	/**
	 * 获取轨迹的半径大小<BR>
	 * [功能详细描述]
	 */
	private float getLocusRadius() {
		float radius = 8 * mDensity;
		switch (mParameterSet.getLocusSize()) {
		case 0:
			radius = 12 * mDensity;
			break;
		case 1:
			radius = 8 * mDensity;
			break;
		case 2:
			radius = 4 * mDensity;
			break;
		default:
			break;
		}
		return radius;
	}

	public MapEvent getMarkPoint(final int index) {
		return this.locasList.get(index);
	}

	public int getMarkerAtPoint(final int eventX, final int eventY, TileView mapView) {
		if (this.locasList != null) {
			showIndex = 0;
			float radius = this.getLocusRadius();
			final com.walktour.gui.map.googlemap.view.TileView.OpenStreetMapViewProjection pj = mapView.getProjection();

			final Rect curMarkerBounds = new Rect();

			final Point mCurScreenCoords = new Point();

			int left, right, top, bottom;
			for (int i = 0; i < this.locasList.size(); i++) {
				final MapEvent mItem = this.locasList.get(i);
				pj.toPixels(new GeoPoint((int) (mItem.getLatitude() * 1e6), (int) (mItem.getLongitude() * 1e6)),
						mapView.getBearing(), mCurScreenCoords);
				for (int j = 0; j < parameterms.size(); j++) {
					left = (int) (mCurScreenCoords.x - radius + j * radius * 3);
					right = (int) (mCurScreenCoords.x + radius + j * radius * 3);
					top = (int) (mCurScreenCoords.y - radius);
					bottom = (int) (mCurScreenCoords.y + radius);
					curMarkerBounds.set(left, top, right, bottom);
					Ut.d("event " + eventX + " " + eventY);
					Ut.d("bounds " + left + "-" + right + " " + top + "-" + bottom);
					if (curMarkerBounds.contains(eventX, eventY)) {
						showIndex = j;
						if (DatasetManager.isPlayback) {
							mDatasetMgr.getPlaybackManager().setSkipIndex(mItem.getBeginPointIndex());
						}
						return i;
					}
				}
			}
		}
		int left, right, top, bottom;
		final Rect curMarkerBounds = new Rect();
		for (int i = 0; i < alarmList.size(); i++) {
			AlarmModel alarmModel = alarmList.get(i);
			if (alarmModel != null && alarmModel.getMapEvent() != null) {
				left = (int) (alarmModel.getMapEvent().getX() - 6 * mDensity);
				right = (int) (alarmModel.getMapEvent().getX() + 6 * mDensity);
				top = (int) (alarmModel.getMapEvent().getY() - 6 * mDensity);
				bottom = (int) (alarmModel.getMapEvent().getY() + 6 * mDensity);

				curMarkerBounds.set(left, top, right, bottom);
				if (curMarkerBounds.contains(eventX, eventY)) {
					showIndex = -1;
					return i;
				}
			}
		}

		return -1;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event, TileView mapView) {
		final int index = getMarkerAtPoint((int) event.getX(), (int) event.getY(), mapView);
		if (index >= 0) {
			if (onTap(index)) {
				return true;
			}
		} else {
			clickItem = null;
			mTapIndex = -1;
		}

		return super.onSingleTapUp(event, mapView);
	}

	@Override
	public boolean onLongPress(MotionEvent event, TileView mapView) {
		final int index = getMarkerAtPoint((int) event.getX(), (int) event.getY(), mapView);
		mapView.mPoiMenuInfo.MarkerIndex = index;
		mapView.mPoiMenuInfo.EventGeoPoint = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY(),
				mapView.getBearing());
		if (index >= 0)
			if (onLongLongPress(index))
				return true;

		return super.onLongPress(event, mapView);
	}

	private boolean onLongLongPress(int index) {
		return false;
	}

	protected boolean onTap(int index) {
		if (mTapIndex == index) {
			mTapIndex = -1;
			clickItem = null;
			LogUtil.i(TAG, "mTapIndex--" + mTapIndex + "--index" + index);
		} else {
			if (showIndex >= 0 && index < this.locasList.size()) {
				clickItem = locasList.get(index);
			} else if (showIndex == -1 && index < this.alarmList.size()) {
				clickItem = this.alarmList.get(index);
			}
			mTapIndex = index;
			return true;
		}

		if (this.mOnItemTapListener != null) {
			return this.mOnItemTapListener.onItemTap(index, this.locasList.get(index));
		} else {
			return false;
		}
	}

	@SuppressWarnings("hiding")
	public static interface OnItemTapListener<MarkPoint> {
		public boolean onItemTap(final int aIndex, final MapEvent aItem);
	}

	/**
	 * 根据不同的事件类型，获取不同的图标
	 * 
	 * @param eventType
	 * @return
	 */
	private Drawable getCallbm(AlarmModel alarm) {
		Drawable drawable = null;
		if (alarm != null) {
			drawable = alarm.getIconDrawable(context);
		}
		return drawable;
	}

	/**
	 * @param searchBaseData
	 *          the searchBaseData to set
	 */
	public void setSearchBaseData(BaseStation searchBaseData) {
		this.searchBaseStation = searchBaseData;
	}

	/**
	 * 判断当前的轨迹点是否和选择的基站相关联
	 * 
	 * @param mapEvent
	 *          轨迹点
	 * @return 0 为选择基站，1选择了未关联，2选择了已关联
	 */
	private int getConnectStation(MapEvent mapEvent) {
		if (this.searchBaseStation == null)
			return CONNECTION_NULL;
		int index = this.searchBaseStation.detailIndex;
		if (index == -1)
			return CONNECTION_NULL;
		BaseStationDetail detail = this.searchBaseStation.details.get(index);
		switch (this.searchBaseStation.netType) {
		case BaseStation.NETTYPE_GSM:
			if (mapEvent.getStationParamMap().containsKey("BS_BCCH") && mapEvent.getStationParamMap().containsKey("BS_BSIC")) {
				String bcch = mapEvent.getStationParamMap().get("BS_BCCH");
				String bsic = mapEvent.getStationParamMap().get("BS_BSIC");
				if (detail.bcch.equals(bcch) && detail.bsic.equals(bsic))
					return CONNECTION_YES;
				else
					return CONNECTION_NO;
			}
			break;
		case BaseStation.NETTYPE_WCDMA:
			if (mapEvent.getStationParamMap().containsKey("BS_PSC") && mapEvent.getStationParamMap().containsKey("BS_UARFCN")) {
				String psc = mapEvent.getStationParamMap().get("BS_PSC");
				String uarfcn = mapEvent.getStationParamMap().get("BS_UARFCN");
				if (detail.psc.equals(psc) && detail.uarfcn.equals(uarfcn))
					return CONNECTION_YES;
				else
					return CONNECTION_NO;
			}
			break;
		case BaseStation.NETTYPE_CDMA:
			if (mapEvent.getStationParamMap().containsKey("BS_PN") && mapEvent.getStationParamMap().containsKey("BS_FREQ")) {
				String pn = mapEvent.getStationParamMap().get("BS_PN");
				String freq = mapEvent.getStationParamMap().get("BS_FREQ");
				if (detail.pn.equals(pn) && detail.frequency.equals(freq))
					return CONNECTION_YES;
				else
					return CONNECTION_NO;
			}
			break;
		case BaseStation.NETTYPE_TDSCDMA:
			if (mapEvent.getStationParamMap().containsKey("BS_UARFCN") && mapEvent.getStationParamMap().containsKey("BS_CPI")) {
				String uarfcn = mapEvent.getStationParamMap().get("BS_UARFCN");
				String cpi = mapEvent.getStationParamMap().get("BS_CPI");
				if (detail.uarfcn.equals(uarfcn) && detail.cpi.equals(cpi))
					return CONNECTION_YES;
				else
					return CONNECTION_NO;
			}
			break;
		case BaseStation.NETTYPE_LTE:
			if (mapEvent.getStationParamMap().containsKey("BS_PCI") && mapEvent.getStationParamMap().containsKey("BS_EARFCN")) {
				String pci = mapEvent.getStationParamMap().get("BS_PCI");
				String earfcn = mapEvent.getStationParamMap().get("BS_EARFCN");
				if (detail.pci.equals(pci) && detail.earfcn.equals(earfcn))
					return CONNECTION_YES;
				else
					return CONNECTION_NO;
			}
			break;
		}
		return CONNECTION_NO;
	}

	@Override
	protected void onDrawFinished(Canvas c, TileView osmv) {

	}

}
