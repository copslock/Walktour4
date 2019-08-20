package com.walktour.gui.map.googlemap.overlays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.gui.R;
import com.walktour.gui.map.googlemap.location.GoogleCorrectUtil;
import com.walktour.gui.map.googlemap.utils.Ut;
import com.walktour.gui.map.googlemap.view.TileView;
import com.walktour.gui.map.googlemap.view.TileView.OpenStreetMapViewProjection;
import com.walktour.gui.map.googlemap.view.TileViewOverlay;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.setting.SysMap;

import org.andnav.osm.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基站数据覆盖层<BR>
 * [功能详细描述]
 *
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-10-10]
 */
@SuppressLint("UseSparseArrays")
public class BaseDataOverlay extends TileViewOverlay {

    private final static String TAG = "BaseDataOverlay";

    private Context mContext;

    private SharedPreferences mPreferences;

    private int defaultColor = 0xFFFF0000/* 0xFF000000 */;

    /**
     * 基站数据
     */
    private List<BaseStation> simplifyBaseDatas;

    private Paint mPaint;

    protected OnItemTapListener<BaseStation> mOnItemTapListener;

    private int mTapIndex;

    private GeoPoint mLastMapCenter;

    private boolean mCanUpdateList = true;

    private int mLastZoom;

    private boolean mNeedUpdateList = false;

    private float systemScale;

    private int overlayRadius;

    private HashMap<String, BaseStationDetail> baseDetailMap = new HashMap<String, BaseStationDetail>();

    /**
     * 地球半径
     */
    private double EARTH_RADIUS = 6378.137;

    /**
     * 搜索到的基站数据,不为空则居中高亮
     */
    private BaseStation searchBaseStation;

    private DisplayMetrics metric;

    /**
     * 是否选择基站操作
     */
    private boolean isSelectStation = false;
    /**
     * 基站扇区区域映射
     */
    private Map<Integer, Region> stationSectorMap = new HashMap<Integer, Region>();
    /**
     * 弹出基站扇区缩放比例
     */
    private final int stationPopScale = 3;
    /** */
    private OpenStreetMapViewProjection projection;
    /**
     * 视图
     */
    private TileView tileView;
    /**
     * 是否正在测距
     */
    private boolean isRanging = false;
    /**
     * 测距的点列表
     */
    protected List<MyLatLng> rangingList = new ArrayList<MyLatLng>();
    /**
     * 测距点图标
     */
    private Drawable marker;
    /**
     * 测距点图标宽度
     */
    private int mMarkerWidth;
    /**
     * 测距点图标高度
     */
    private int mMarkerHeight;

    /**
     * [构造简要说明]
     *
     * @param baseStations 基站数据
     */
    public BaseDataOverlay(Context context, List<BaseStation> simplifyBaseDatas,
                           OnItemTapListener<BaseStation> onItemTapListener) {
        super();
        this.mContext = context;
        this.simplifyBaseDatas = simplifyBaseDatas;
        this.mOnItemTapListener = onItemTapListener;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED); // 设置画笔颜色
        mPaint.setStrokeWidth((float) 2.0); // 线宽

        this.mLastMapCenter = null;
        this.mLastZoom = -1;

        metric = context.getResources().getDisplayMetrics();
        systemScale = metric.densityDpi / 240.f;

        overlayRadius = (int) (25 * systemScale);
        marker = context.getResources().getDrawable(R.drawable.iconmarker);
        this.mMarkerWidth = marker.getIntrinsicWidth();
        this.mMarkerHeight = marker.getIntrinsicHeight();
    }

    /**
     * 设置测距操作
     *
     * @param isRanging
     */
    public void setRanging(boolean isRanging) {
        this.isRanging = isRanging;
        if (!this.isRanging)
            this.rangingList.clear();
    }

    /**
     * 绘制测距连线
     *
     * @param canvas
     */
    private void drawRangingLink(Canvas canvas) {
        if (this.rangingList.isEmpty())
            return;
        // 绘制测距点
        int count = 0;
        for (MyLatLng latlng : this.rangingList) {
            Point point = this.convertLatlngToPoint(latlng);
            if (point.x < 0 || point.y < 0)
                continue;
            marker.setBounds(point.x - mMarkerWidth / 2, point.y - mMarkerHeight, point.x + mMarkerWidth / 2, point.y);
            marker.draw(canvas);
            count++;
        }
        // 绘制测距连线
        if (count == 2) {
            Point point1 = this.convertLatlngToPoint(this.rangingList.get(0));
            Point point2 = this.convertLatlngToPoint(this.rangingList.get(1));
            canvas.drawLine(point1.x, point1.y, point2.x, point2.y, mPaint);
            mPaint.setTextSize(systemScale * 14);
            mPaint.setColor(this.defaultColor);
            mPaint.setStrokeWidth(2);
            double distance = this.calculateDistance(this.rangingList.get(0), this.rangingList.get(1));
            canvas.drawText(distanceConversion(distance), (point1.x + point2.x) / 2, (point1.y + point2.y) / 2, mPaint);
        }
    }

    /**
     * 转换经纬度为界面坐标
     *
     * @param latlng 经纬度
     * @return 界面坐标
     */
    private Point convertLatlngToPoint(MyLatLng latlng) {
        Point point = new Point();
        projection.toPixels(new GeoPoint((int) (latlng.latitude * 1e6), (int) (latlng.longitude * 1e6)), point);
        return point;
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     *
     * @param canvas
     * @param tileView
     * @see com.walktour.gui.map.googlemap.view.TileViewOverlay#onDraw(android.graphics.Canvas,
     * com.walktour.gui.map.googlemap.view.TileView)
     */
    @Override
    protected void onDraw(Canvas canvas, TileView tileView) {
        projection = tileView.getProjection();
        this.tileView = tileView;
        final Point curScreenPoint = new Point();
        GeoPoint leftGeoPoint = projection.fromPixels(0.0f, 0.0f);
        GeoPoint bottomGeoPoint = projection.fromPixels((float) tileView.getWidth(), (float) tileView.getHeight());
        /*
         * Logger.d(TAG, "leftGeoPoint:"+ "____lat:" + leftGeoPoint.getLatitude() +
         * "______log:"+ leftGeoPoint.getLongitude()); Logger.d(TAG,
         * "BottomGeoPoint:"+ "____lat:" + bottomGeoPoint.getLatitude() +
         * "______log:"+ bottomGeoPoint.getLongitude());
         */

        if (mCanUpdateList) {
            boolean looseCenter = false;
            GeoPoint center = tileView.getMapCenter();
            GeoPoint lefttop = projection.fromPixels(0, 0);
            double deltaX = Math.abs(center.getLongitude() - lefttop.getLongitude());
            double deltaY = Math.abs(center.getLatitude() - lefttop.getLatitude());

            if (mLastMapCenter == null || mLastZoom != tileView.getZoomLevel())
                looseCenter = true;
            else if (0.7 * deltaX < Math.abs(center.getLongitude() - mLastMapCenter.getLongitude())
                    || 0.7 * deltaY < Math.abs(center.getLatitude() - mLastMapCenter.getLatitude()))
                looseCenter = true;

            if (looseCenter || mNeedUpdateList) {
                mLastMapCenter = center;
                mLastZoom = tileView.getZoomLevel();
                mNeedUpdateList = false;

                simplifyBaseDatas = BaseStationDBHelper.getInstance(mContext.getApplicationContext()).queryBaseStation(
                        leftGeoPoint.getLongitude() - deltaY, leftGeoPoint.getLatitude() + deltaX,

                        bottomGeoPoint.getLongitude() + deltaY, bottomGeoPoint.getLatitude() - deltaX, getNetTypes(), 1,
                        BaseStation.MAPTYPE_OUTDOOR, 50);
                LogUtil.d(TAG, "SimplifyBaseDatas size:" + simplifyBaseDatas.size());
                /*
                 * mThread.setParams(1.5*deltaX, 1.5*deltaY); mThread.run();
                 */
            }
        }

        /*
         * projection.fromPixels(tileView.getWidth() * 1.5, y) if (centerGeoPoint ==
         * null) { centerGeoPoint = tileView.getMapCenter(); } else {
         *
         * }
         */
        for (int i = 0; i < simplifyBaseDatas.size(); i++) {
            BaseStation baseStation = simplifyBaseDatas.get(i);
            GeoPoint baseDateGeoPoint = new GeoPoint((int) (baseStation.latitude * 1e6), (int) (baseStation.longitude * 1e6));
            projection.toPixels(baseDateGeoPoint, curScreenPoint);
            boolean isSelected = false;
            if (searchBaseStation != null && baseStation.latitude == searchBaseStation.latitude
                    && baseStation.longitude == searchBaseStation.longitude) {
                mPaint.setColor(Color.parseColor("#FF7F00"));
                canvas.drawCircle(curScreenPoint.x, curScreenPoint.y, overlayRadius, mPaint);
                isSelected = true;
            }

            String bearings[] = new String[baseStation.details.size()];
            for (int j = 0; j < baseStation.details.size(); j++) {
                bearings[j] = String.valueOf(baseStation.details.get(j).bearing);
            }

            if (isSelectStation && isSelected)
                this.drawSelectSectorStation(canvas, curScreenPoint, bearings);
            else
                this.drawStation(canvas, curScreenPoint, bearings, isSelected);
            GeoPoint lastGeoPoint = GpsInfo.getInstance().getLastGeoPoint();
            GeoPoint adjustedGeoPoint = GoogleCorrectUtil.adjustLatLng(mContext, lastGeoPoint);
            if (adjustedGeoPoint != null) {
                double distance = calculateDistance(adjustedGeoPoint, new GeoPoint((int) (baseStation.latitude * 1e6),
                        (int) (baseStation.longitude * 1e6)));
                switch (baseStation.netType) {
                    case BaseStation.NETTYPE_GSM:
                        gsmBcchBsic(baseStation, distance);
                        break;
                    case BaseStation.NETTYPE_WCDMA:
                        wcdmaPscUarfcn(baseStation, distance);
                        break;
                    case BaseStation.NETTYPE_CDMA:
                        cdmaPNFreq(baseStation, distance);
                        break;
                    case BaseStation.NETTYPE_TDSCDMA:
                        tdscdmaCPIFreq(baseStation, distance);
                        break;
                    case BaseStation.NETTYPE_LTE:
                        ltePCIEafrcn(baseStation, distance);
                        break;
                }
            }

            if (simplifyBaseDatas.size() - 1 == i) {
                // GeoPoint adjustedGeoPoint =
                // GpsInfo.getInstance().getAdjustedGeoPoint()/* new
                // GeoPoint((int)(22.39325 * 1e6), (int)(113.55448 * 1e6))*/;
                mPaint.clearShadowLayer();
                if (adjustedGeoPoint != null) {
                    for (String key : this.baseDetailMap.keySet()) {
                        BaseStationDetail detail = baseDetailMap.get(key);
                        int angle = detail.bearing - 90;
                        Point currentLocationPoint = new Point();
                        projection.toPixels(adjustedGeoPoint, currentLocationPoint);
                        Point basePoint = new Point();
                        projection.toPixels(new GeoPoint((int) (detail.main.latitude * 1e6), (int) (detail.main.longitude * 1e6)),
                                basePoint);
                        switch (detail.setType) {
                            case BaseStationDetail.SETTYPE_ACTIVESET:
                                if (mPreferences.getBoolean(WalktourConst.CellLink.ACTIVE_SET_ENABLE, true)) {
                                    mPaint.setColor(mPreferences.getInt(WalktourConst.CellLink.ACTIVE_SET_COLOR, defaultColor));
                                    mPaint.setStrokeWidth(mPreferences.getInt(WalktourConst.CellLink.ACTIVE_SET_WIDTH, 2) * metric.density);
                                    drawCellLink(canvas, detail, currentLocationPoint, basePoint, angle);
                                }
                                break;
                            case BaseStationDetail.SETTYPE_MONITORSET:
                                if (mPreferences.getBoolean(WalktourConst.CellLink.MONITOR_CANDIDATE_ENABLE, true)) {
                                    mPaint.setColor(mPreferences.getInt(WalktourConst.CellLink.MONITOR_CANDIDATE_COLOR, defaultColor));
                                    mPaint.setStrokeWidth(mPreferences.getInt(WalktourConst.CellLink.MONITOR_CANDIDATE_WIDTH, 2)
                                            * metric.density);
                                    drawCellLink(canvas, detail, currentLocationPoint, basePoint, angle);
                                }
                                break;
                            case BaseStationDetail.SETTYPE_NEIGHBORSET:
                                if (mPreferences.getBoolean(WalktourConst.CellLink.NEIGHBOR_ENABLE, true)) {
                                    mPaint.setColor(mPreferences.getInt(WalktourConst.CellLink.NEIGHBOR_COLOR, defaultColor));
                                    mPaint.setStrokeWidth(mPreferences.getInt(WalktourConst.CellLink.NEIGHBOR_WIDTH, 2) * metric.density);
                                    drawCellLink(canvas, detail, currentLocationPoint, basePoint, angle);
                                }
                                break;
                            case BaseStationDetail.SETTYPE_SERVINGSET:
                                if (mPreferences.getBoolean(WalktourConst.CellLink.SERVING_REFERENCE_ENABLE, true)) {
                                    mPaint.setColor(mPreferences.getInt(WalktourConst.CellLink.SERVING_REFERENCE_COLOR, defaultColor));
                                    mPaint.setStrokeWidth(mPreferences.getInt(WalktourConst.CellLink.SERVING_REFERENCE_WIDTH, 2)
                                            * metric.density);
                                    drawCellLink(canvas, detail, currentLocationPoint, basePoint, angle);
                                }
                                break;

                            default:
                                break;
                        }
                    }
                }
                baseDetailMap.clear();
            }
        }
        this.drawRangingLink(canvas);
    }

    /**
     * 绘制基站图片
     *
     * @param canvas     画布
     * @param point      坐标点
     * @param bearings   朝向
     * @param isSelected 是否被选中
     * @return
     */
    private void drawStation(Canvas canvas, Point point, String bearings[], boolean isSelected) {
        if (bearings == null || bearings.length == 0)
            return;
        if (isSelected) {
            mPaint.setColor(Color.parseColor("#FF7F00"));
            canvas.drawCircle(point.x, point.y, overlayRadius, mPaint);
        }
        RectF oval = new RectF();
        oval.top = point.y - overlayRadius;
        oval.bottom = point.y + overlayRadius;
        oval.left = point.x - overlayRadius;
        oval.right = point.x + overlayRadius;
        RectF selectOval = new RectF();
        selectOval.top = point.y - overlayRadius * 2;
        selectOval.bottom = point.y + overlayRadius * 2;
        selectOval.left = point.x - overlayRadius * 2;
        selectOval.right = point.x + overlayRadius * 2;
        mPaint.setShadowLayer(1f, 1, 1f, Color.BLACK);
        for (int i = 0; i < bearings.length; i++) {
            float startAngle = Integer.valueOf(bearings[i]) - 90 - 30;
            if (Integer.valueOf(bearings[i]) <= 120) {
                mPaint.setColor(Color.RED);
            } else if (Integer.valueOf(bearings[i]) <= 240) {
                mPaint.setColor(Color.BLUE);
            } else if (Integer.valueOf(bearings[i]) <= 360) {
                mPaint.setColor(Color.GREEN);
            }
            if (isSelected && this.searchBaseStation.detailIndex == i)
                canvas.drawArc(selectOval, startAngle, 60, true, mPaint);
            else
                canvas.drawArc(oval, startAngle, 60, true, mPaint);
        }
    }

    /**
     * 绘制要选择扇区的基站
     *
     * @param canvas   画布
     * @param point    坐标点
     * @param bearings 朝向
     */
    private void drawSelectSectorStation(Canvas canvas, Point point, String bearings[]) {
        if (bearings == null || bearings.length == 0)
            return;
        mPaint.setColor(Color.parseColor("#FF7F00"));
        canvas.drawCircle(point.x, point.y, overlayRadius, mPaint);
        this.stationSectorMap.clear();
        RectF oval = new RectF();
        oval.top = point.y - overlayRadius * this.stationPopScale;
        oval.bottom = point.y + overlayRadius * this.stationPopScale;
        oval.left = point.x - overlayRadius * this.stationPopScale;
        oval.right = point.x + overlayRadius * this.stationPopScale;
        mPaint.setShadowLayer(1f, 1, 1f, Color.BLACK);
        Region base = new Region();
        base.set((int) oval.left, (int) oval.top, (int) oval.right, (int) oval.bottom);
        Point pathPoint;
        int startAngle;
        int r = overlayRadius * this.stationPopScale;
        for (int i = 0; i < bearings.length; i++) {
            startAngle = Integer.valueOf(bearings[i]) - 90 - 30;
            if (Integer.valueOf(bearings[i]) <= 120) {
                mPaint.setColor(Color.RED);
            } else if (Integer.valueOf(bearings[i]) <= 240) {
                mPaint.setColor(Color.BLUE);
            } else if (Integer.valueOf(bearings[i]) <= 360) {
                mPaint.setColor(Color.GREEN);
            }
            canvas.drawArc(oval, startAngle, 60, true, mPaint);
            Region region = new Region();
            Path path = new Path();
            path.moveTo((float) point.x, (float) point.y);
            pathPoint = this.calculateCriclePoint(point, r, startAngle);
            path.lineTo(pathPoint.x, pathPoint.y);
            pathPoint = this.calculateCriclePoint(point, r, startAngle + 60);
            path.lineTo(pathPoint.x, pathPoint.y);
            path.close();
            region.setPath(path, base);
            this.stationSectorMap.put(i, region);
        }
    }

    /**
     * 绘制小区连线<BR>
     * [功能详细描述]
     *
     * @param canvas
     * @param detail
     * @param currentLocationPoint
     * @param basePoint
     * @param angle
     */
    private void drawCellLink(Canvas canvas, BaseStationDetail detail, Point currentLocationPoint, Point basePoint,
                              int angle) {
        float overLayX = (float) (basePoint.x + overlayRadius * Math.cos(((angle)) * Math.PI / 180));
        float overLayY = (float) (basePoint.y + overlayRadius * Math.sin(((angle)) * Math.PI / 180));
        canvas.drawLine(currentLocationPoint.x, currentLocationPoint.y, overLayX, overLayY, mPaint);
        mPaint.setTextSize(systemScale * 14);
        mPaint.setColor(Color.BLACK);
        // 计算两点之间的中间点 公式：x = (x1+x2)/2 y = (y1+y2)/2
        canvas.drawText(distanceConversion(detail.distance), (currentLocationPoint.x + overLayX) / 2,
                (currentLocationPoint.y + overLayY) / 2, mPaint);
        /*
         * canvas.drawLine(currentLocationPoint.x, currentLocationPoint.y, (float)
         * (basePoint.x + overlayRadius * Math.cos(((baseStation.getBearing() - 90)
         * ) Math.PI / 180)),(float) (basePoint.y + overlayRadius *
         * Math.sin(((baseStation.getBearing() - 90) )* Math.PI / 180)), mPaint);
         */
    }

    /**
     * WCDMA相同频点基站集合
     *
     * @param base     当前判断的基站
     * @param distance 基站距离
     */
    private void wcdmaPscUarfcn(BaseStation base, double distance) {
        String servingNeighbor[] = getParaValue(UnifyParaID.W_TUMTSCellInfoV2).split(";");
        for (int i = 0; servingNeighbor != null && i < servingNeighbor.length - 1; i++) {
            String[] neighbor = servingNeighbor[i + 1].split(",");
            for (BaseStationDetail detail : base.details) {
                if (detail.psc.equals(neighbor[2]) && detail.uarfcn.equals(neighbor[1])) {
                    LogUtil.i(TAG, "  ___>>>>>>>>Uarfcn:" + neighbor[1] + "   >>>>>PSC:" + neighbor[2]);
                    if (neighbor[0].equals("0")) {
                        detail.setType = BaseStationDetail.SETTYPE_ACTIVESET;
                    } else if (neighbor[0].equals("1")) {
                        detail.setType = BaseStationDetail.SETTYPE_MONITORSET;
                    } else /* if(neighbor[0].equals("2")) */ {
                        detail.setType = BaseStationDetail.SETTYPE_NEIGHBORSET;
                    }
                    String key = detail.psc + "_" + detail.uarfcn;
                    setBaseSatationDistance(key, detail, distance);
                    return;
                }
            }
        }

    }

    /**
     * GSM相同频点基站集合
     *
     * @param base     当前判断的基站
     * @param distance 距离
     */
    public void gsmBcchBsic(BaseStation base, double distance) {
        String[] datas = new String[]{getParaValue(UnifyParaID.G_Ser_BCCH), getParaValue(UnifyParaID.G_NCell_N1_BCCH),
                getParaValue(UnifyParaID.G_NCell_N2_BCCH), getParaValue(UnifyParaID.G_NCell_N3_BCCH),
                getParaValue(UnifyParaID.G_NCell_N4_BCCH), getParaValue(UnifyParaID.G_NCell_N5_BCCH),
                getParaValue(UnifyParaID.G_NCell_N6_BCCH), getParaValue(UnifyParaID.G_Ser_BSIC),
                getParaValue(UnifyParaID.G_NCell_N1_BSIC), getParaValue(UnifyParaID.G_NCell_N2_BSIC),
                getParaValue(UnifyParaID.G_NCell_N3_BSIC), getParaValue(UnifyParaID.G_NCell_N4_BSIC),
                getParaValue(UnifyParaID.G_NCell_N5_BSIC), getParaValue(UnifyParaID.G_NCell_N6_BSIC),};
        for (int i = 0, j = 7; i < (datas.length / 2); i++, j++) {
            for (BaseStationDetail detail : base.details) {
                if (detail.bcch.equals(datas[i]) && detail.bsic.equals(datas[j])) {
                    if (i == 0) {
                        detail.setType = BaseStationDetail.SETTYPE_SERVINGSET;
                    } else {
                        detail.setType = BaseStationDetail.SETTYPE_NEIGHBORSET;
                    }
                    String key = detail.bcch + "_" + detail.bsic;
                    setBaseSatationDistance(key, detail, distance);
                    return;
                }
            }
        }
    }

    /**
     * TDSCDMA相同频点基站集合
     *
     * @param base     当前判断的基站
     * @param distance 基站距离
     */
    public void tdscdmaCPIFreq(BaseStation base, double distance) {
        String[] datas = new String[]{getParaValue(UnifyParaID.TD_Ser_UARFCN),
                getParaValue(UnifyParaID.T_NCell_N1_UARFCN), getParaValue(UnifyParaID.T_NCell_N2_UARFCN),
                getParaValue(UnifyParaID.T_NCell_N3_UARFCN), getParaValue(UnifyParaID.T_NCell_N4_UARFCN),
                getParaValue(UnifyParaID.T_NCell_N5_UARFCN), getParaValue(UnifyParaID.T_NCell_N6_UARFCN),
                getParaValue(UnifyParaID.TD_Ser_CPI), getParaValue(UnifyParaID.T_NCell_N1_CPI),
                getParaValue(UnifyParaID.T_NCell_N2_CPI), getParaValue(UnifyParaID.T_NCell_N3_CPI),
                getParaValue(UnifyParaID.T_NCell_N4_CPI), getParaValue(UnifyParaID.T_NCell_N5_CPI),
                getParaValue(UnifyParaID.T_NCell_N6_CPI),};
        for (int i = 0, j = 7; i < (datas.length / 2); i++, j++) {
            for (BaseStationDetail detail : base.details) {
                if (detail.cpi.equals(datas[j]) && detail.uarfcn.equals(datas[i])) {
                    if (i == 0) {
                        detail.setType = BaseStationDetail.SETTYPE_SERVINGSET;
                    } else {
                        detail.setType = BaseStationDetail.SETTYPE_NEIGHBORSET;
                    }
                    String key = detail.cpi + "_" + detail.uarfcn;
                    setBaseSatationDistance(key, detail, distance);
                    return;
                }
            }
        }
    }

    /**
     * CDMA相同频点基站集合
     *
     * @param base     当前判断的基站
     * @param distance 基站距离
     */
    public void cdmaPNFreq(BaseStation base, double distance) {
        String[] servingNeighbor = TraceInfoInterface.getParaValue(UnifyParaID.C_cdmaServingNeighbor).split(";");

        for (int i = 0; i < servingNeighbor.length - 1; i++) {
            String[] neighbor = servingNeighbor[i + 1].split(",");
            for (BaseStationDetail detail : base.details) {
                if (detail.pn.equals(neighbor[2]) && detail.frequency.equals(neighbor[1])) {
                    if (neighbor[0].equals("0")) {
                        detail.setType = BaseStationDetail.SETTYPE_ACTIVESET;
                    } else if (neighbor[0].equals("1")) {
                        detail.setType = BaseStationDetail.SETTYPE_MONITORSET;
                    } else /* if(neighbor[0].equals("2")) */ {
                        detail.setType = BaseStationDetail.SETTYPE_NEIGHBORSET;
                    }
                    String key = detail.pn + "_" + detail.frequency;
                    setBaseSatationDistance(key, detail, distance);
                    return;
                }
            }
        }
    }

    /**
     * LTE相同频点基站集合
     *
     * @param base     当前判断的基站
     * @param distance 基站距离
     */
    public void ltePCIEafrcn(BaseStation base, double distance) {
        String[] servingNeighbor = getParaValue(UnifyParaID.LTE_CELL_LIST).split(";");
        String sevPCI = getParaValue(UnifyParaID.L_SRV_PCI);
        String sevEARFCN = getParaValue(UnifyParaID.L_SRV_EARFCN);
        for (int i = 0; i < servingNeighbor.length - 1; i++) {
            String[] neighbor = servingNeighbor[i + 1].split(",");
            for (BaseStationDetail detail : base.details) {
                if (detail.pci.equals(neighbor[1]) && detail.earfcn.equals(neighbor[0])) {
                    String key = detail.pci + "_" + detail.earfcn;
                    detail.setType = BaseStationDetail.SETTYPE_NEIGHBORSET;
                    setBaseSatationDistance(key, detail, distance);
                    return;
                } else if (detail.pci.equals(sevPCI) && detail.earfcn.equals(sevEARFCN)) {
                    String key = detail.pci + "_" + detail.earfcn;
                    detail.setType = BaseStationDetail.SETTYPE_SERVINGSET;
                    setBaseSatationDistance(key, detail, distance);
                    return;
                }
            }
        }
    }

    /**
     * 设置集合映射
     *
     * @param key      关键字
     * @param detail   基站明细对象
     * @param distance 距离
     * @return
     */
    private boolean setBaseSatationDistance(String key, BaseStationDetail detail, double distance) {
        if (baseDetailMap.get(key) != null) {
            LogUtil.e(TAG, "baseDetailMap distance:" + baseDetailMap.get(key).distance + "____distance:" + distance);
            if (baseDetailMap.get(key).distance > distance) {
                detail.distance = distance;
                baseDetailMap.put(key, detail);
                return true;
            }
            return false;
        } else {
            detail.distance = distance;
            baseDetailMap.put(key, detail);
            return true;

        }
    }

    /**
     * 计算总距离<BR>
     * [功能详细描述]
     */
    private double calculateDistance(MyLatLng geoPoint1, MyLatLng geoPoint2) {
        double distance = 0.0;
        double lat1 = geoPoint1.latitude;
        double lat2 = geoPoint2.latitude;
        double lon1 = geoPoint1.longitude;
        double lon2 = geoPoint2.longitude;
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180)
                * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * EARTH_RADIUS;
        return distance;
    }

    /**
     * 计算总距离<BR>
     * [功能详细描述]
     */
    public double calculateDistance(GeoPoint geoPoint1, GeoPoint geoPoint2) {
        double distance = 0.0;
        double lat1 = geoPoint1.getLatitudeE6() / 1e6;
        double lat2 = geoPoint2.getLatitudeE6() / 1e6;
        double lon1 = geoPoint1.getLongitudeE6() / 1e6;
        double lon2 = geoPoint2.getLongitudeE6() / 1e6;
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180)
                * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * EARTH_RADIUS;
        return distance;
    }

    /**
     * 距离换算<BR>
     * [功能详细描述]
     *
     * @param distance
     * @return
     */
    private String distanceConversion(double distance) {
        String resultString = "";
        if (distance < 1) {
            resultString = ((double) ((int) Math.round(distance * 1000 * 10)) / 10.0) + "M";
        } else {
            resultString = ((double) (((int) Math.round(distance * 10))) / 10.0) + "KM";
        }
        return resultString;
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     *
     * @param c
     * @param tileView
     * @see com.walktour.gui.map.googlemap.view.TileViewOverlay#onDrawFinished(android.graphics.Canvas,
     * com.walktour.gui.map.googlemap.view.TileView)
     */

    @Override
    protected void onDrawFinished(Canvas c, TileView tileView) {

    }

    /**
     * 判断数字是否在区间内<BR>
     * [功能详细描述]
     *
     * @param scppe 区间数组
     * @param value 需判断的数值
     * @return 是否在区间内
     */
    public static boolean isInScope(double[] scppe, double value) {
        double max;
        double min;
        if (scppe[0] > scppe[1]) {
            max = scppe[0];
            min = scppe[1];
        } else {
            min = scppe[0];
            max = scppe[1];
        }
        if (value > max) {
            return false;
        }
        if (value < min) {
            return false;
        }
        return true;
    }

    private String getNetTypes() {
        StringBuffer netTypeSB = new StringBuffer();
        if (SharePreferencesUtil.getInstance(mContext.getApplicationContext()).getInteger(SysMap.BASE_DISPLAY_TYPE, 0) == 1) {
            if (SharePreferencesUtil.getInstance(mContext.getApplicationContext()).getBoolean(SysMap.BASE_GSM, false)) {
                netTypeSB.append(WalktourConst.NetWork.GSM + ",");
            }
            if (SharePreferencesUtil.getInstance(mContext.getApplicationContext()).getBoolean(SysMap.BASE_WCDMA, false)) {
                netTypeSB.append(WalktourConst.NetWork.WCDMA + ",");
            }
            if (SharePreferencesUtil.getInstance(mContext.getApplicationContext()).getBoolean(SysMap.BASE_CDMA, false)) {
                netTypeSB.append(WalktourConst.NetWork.CDMA + ",");
            }
            if (SharePreferencesUtil.getInstance(mContext.getApplicationContext()).getBoolean(SysMap.BASE_TDSCDMA, false)) {
                netTypeSB.append(WalktourConst.NetWork.TDSDCDMA + ",");
            }
            if (SharePreferencesUtil.getInstance(mContext.getApplicationContext()).getBoolean(SysMap.BASE_LTE, false)) {
                netTypeSB.append(WalktourConst.NetWork.LTE + ",");
            }
            if (SharePreferencesUtil.getInstance(mContext.getApplicationContext()).getBoolean(SysMap.BASE_NB_IoT, false)) {
                netTypeSB.append(WalktourConst.NetWork.NB_IoT + ",");
            }
            if (!StringUtil.isNullOrEmpty(netTypeSB.toString())) {
                return netTypeSB.toString().substring(0, netTypeSB.toString().length() - 1);
            } else {
                return "";
            }
        } else {
            MyPhoneState state = MyPhoneState.getInstance();
            NetType nettype = state.getCurrentNetType(mContext);
            LogUtil.d(TAG, nettype == null ? "" : nettype.toString());
            switch (nettype) {
                case GSM:
                    netTypeSB.append(WalktourConst.NetWork.GSM);
                    break;
                case WCDMA:
                    netTypeSB.append(WalktourConst.NetWork.WCDMA);
                    break;
                case EVDO:
                case CDMA:
                    netTypeSB.append(WalktourConst.NetWork.CDMA);
                    break;
                case TDSCDMA:
                    netTypeSB.append(WalktourConst.NetWork.TDSDCDMA);
                    break;
                case LTE:
                    netTypeSB.append(WalktourConst.NetWork.LTE);
                    break;
                case NBIoT:
                    netTypeSB.append(WalktourConst.NetWork.NB_IoT);
                    break;
                default:
                    break;
            }
            return netTypeSB.toString();
        }

    }

    /**
     * 获得当前点击的是哪个基站<BR>
     * [功能详细描述]
     *
     * @param eventX
     * @param eventY
     * @param mapView
     * @return 数组下标
     */
    public int getMarkerAtPoint(final int eventX, final int eventY, TileView mapView) {
        if (this.simplifyBaseDatas != null) {
            final com.walktour.gui.map.googlemap.view.TileView.OpenStreetMapViewProjection pj = mapView.getProjection();

            final Rect curMarkerBounds = new Rect();
            final Point mCurScreenCoords = new Point();

            for (int i = 0; i < this.simplifyBaseDatas.size(); i++) {
                final BaseStation base = this.simplifyBaseDatas.get(i);
                pj.toPixels(new GeoPoint((int) (base.latitude * 1e6), (int) (base.longitude * 1e6)), mapView.getBearing(),
                        mCurScreenCoords);

                final int top = mCurScreenCoords.y - overlayRadius;
                final int bottom = mCurScreenCoords.y + overlayRadius;
                final int left = mCurScreenCoords.x - overlayRadius;
                final int right = mCurScreenCoords.x + overlayRadius;

                Ut.d("event " + eventX + " " + eventY);
                Ut.d("bounds " + left + "-" + right + " " + top + "-" + bottom);

                curMarkerBounds.set(left, top, right, bottom);
                if (curMarkerBounds.contains(eventX, eventY))
                    return i;
            }
        }

        return -1;
    }

    protected boolean onTap(int index) {
        if (mTapIndex == index)
            mTapIndex = -1;
        else
            mTapIndex = index;
        this.searchBaseStation = this.simplifyBaseDatas.get(index);
        if (this.mOnItemTapListener != null)
            return this.mOnItemTapListener.onItemTap(index, this.simplifyBaseDatas.get(index));
        else
            return false;
    }

    /**
     * 获得得参数队列中指定ID的值<BR>
     * [功能详细描述]
     *
     * @param paraId
     * @return
     */
    private String getParaValue(int paraId) {
        return TraceInfoInterface.getParaValue(paraId);
    }

    /**
     * @param simplifyBaseDatas the simplifyBaseDatas to set
     */
    public void clearSimplifyBaseDatas() {
        searchBaseStation = null;
        this.simplifyBaseDatas.clear();
    }

    /**
     * @param mNeedUpdateList the mNeedUpdateList to set
     */
    public void setmNeedUpdateList(boolean mNeedUpdateList) {
        this.mNeedUpdateList = mNeedUpdateList;
    }

    /**
     * 添加测距的经纬度点
     *
     * @param latlng 测距的经纬度点
     */
    private void addRangingPoint(MyLatLng latlng) {
        if (this.rangingList.size() == 2)
            this.rangingList.remove(0);
        this.rangingList.add(latlng);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event, TileView mapView) {
        if (this.isRanging) {
            Point point = new Point((int) event.getX(), (int) event.getY());
            MyLatLng latlng = this.convertPointToLatlng(point);
            this.addRangingPoint(latlng);
            return true;
        }
        if (this.isSelectStation) {
            Point point = new Point((int) event.getX(), (int) event.getY());
            for (int index : this.stationSectorMap.keySet()) {
                Region region = this.stationSectorMap.get(index);
                if (region.contains(point.x, point.y)) {
                    this.isSelectStation = false;
                    this.searchBaseStation.detailIndex = index;
                    if (this.mOnItemTapListener != null)
                        this.mOnItemTapListener.onBaseStationSelect(searchBaseStation);
                    this.tileView.invalidate();
                    return true;
                }
            }
            this.isSelectStation = false;
        }
        final int index = getMarkerAtPoint((int) event.getX(), (int) event.getY(), mapView);
        if (index >= 0) {
            if (this.searchBaseStation != null)
                this.searchBaseStation.detailIndex = -1;
            if (onTap(index)) {
                return true;
            }
        } else if (this.searchBaseStation != null) {
            this.isSelectStation = false;
            this.searchBaseStation.detailIndex = -1;
            this.searchBaseStation = null;
            if (this.mOnItemTapListener != null)
                this.mOnItemTapListener.onBaseStationSelect(searchBaseStation);
        }
        return super.onSingleTapUp(event, mapView);
    }

    /**
     * 转换界面坐标为经纬度
     *
     * @param point 界面坐标
     * @return 经纬度
     */
    private MyLatLng convertPointToLatlng(Point point) {
        GeoPoint geo = this.projection.fromPixels(point.x, point.y);
        return new MyLatLng(geo.getLatitude(), geo.getLongitude());
    }

    /**
     * 计算指定半径的园上的指定角度的坐标
     *
     * @param center 中心点坐标
     * @param r      园半径
     * @param angle  指定角度
     * @return 坐标
     */
    private Point calculateCriclePoint(Point center, int r, int angle) {
        Point point = new Point();
        while (angle < 0)
            angle += 360;
        if (angle >= 360)
            angle = angle % 360;
        int x = 0;
        int y = 0;
        if (angle == 0) {
            x = r;
        } else if (angle == 90) {
            y = r;
        } else if (angle == 180) {
            x = -r;
        } else if (angle == 270) {
            y = -r;
        } else if (angle > 0 && angle < 90) {
            y = (int) (Math.sin(Math.toRadians(angle)) * r);
            x = (int) (Math.cos(Math.toRadians(angle)) * r);
        } else if (angle > 90 && angle < 180) {
            x -= (int) (Math.sin(Math.toRadians(angle - 90)) * r);
            y = (int) (Math.cos(Math.toRadians(angle - 90)) * r);
        } else if (angle > 180 && angle < 270) {
            y -= (int) (Math.sin(Math.toRadians(angle - 180)) * r);
            x -= (int) (Math.cos(Math.toRadians(angle - 180)) * r);
        } else {
            x = (int) (Math.sin(Math.toRadians(angle - 270)) * r);
            y -= (int) (Math.cos(Math.toRadians(angle - 270)) * r);
        }
        point.x = center.x + x;
        point.y = center.y + y;
        return point;
    }

    public static interface OnItemTapListener<SimplifyBaseData> {
        public boolean onItemTap(final int aIndex, final SimplifyBaseData aItem);

        public boolean onBaseStationSelect(final SimplifyBaseData aItem);
    }

    /**
     * @param searchBaseData the searchBaseData to set
     */
    public void setSearchBaseData(BaseStation searchBaseData) {
        this.searchBaseStation = searchBaseData;
    }

    @Override
    public boolean onLongPress(MotionEvent event, TileView mapView) {
        final int index = getMarkerAtPoint((int) event.getX(), (int) event.getY(), mapView);
        if (index >= 0) {
            this.searchBaseStation = this.simplifyBaseDatas.get(index);
            this.isSelectStation = true;
            return true;
        } else if (this.searchBaseStation != null) {
            this.isSelectStation = false;
            this.searchBaseStation.detailIndex = -1;
            this.searchBaseStation = null;
        }
        return super.onLongPress(event, mapView);
    }

}
