package com.walktour.gui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.logic.PointIndexChangeLinstener;
import com.jhlabs.map.java.Point2D;
import com.jhlabs.map.java.Rectangle2D;
import com.jhlabs.map.proj.ProjectionFactory;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.ImageUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.Alarm;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.ToastUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.LegendColors;
import com.walktour.control.config.ParameterSetting;
import com.walktour.control.instance.AlertManager;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.framework.ui.ActivityManager;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.applet.ControlPanel;
import com.walktour.gui.newmap.basestation.BaseDataParser;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap2.util.BaiduMapUtil;
import com.walktour.gui.perceptiontest.surveytask.claiming.SurveyTaskClaimingActivity;
import com.walktour.gui.perceptiontest.surveytask.claiming.event.LocationEvent;
import com.walktour.gui.setting.SysFloorMap;
import com.walktour.mapextention.ibwave.ConvertPrj;
import com.walktour.mapextention.ibwave.Drawer;
import com.walktour.mapextention.ibwave.DrawerTabRaster;
import com.walktour.mapextention.ibwave.LayerManager;
import com.walktour.mapextention.ibwave.TabMap;
import com.walktour.mapextention.ibwave.TabMapRaster;
import com.walktour.mapextention.ibwave.ViewPort;
import com.walktour.mapextention.mif.MifCenter;
import com.walktour.mapextention.mif.MifParser;
import com.walktour.mapextention.tab.CoordData;
import com.walktour.mapextention.tab.GPSData;
import com.walktour.mapextention.tab.TabDataParser;
import com.walktour.model.AlarmModel;
import com.walktour.model.HistoryPoint;
import com.walktour.model.LocusParamInfo;
import com.walktour.model.MapEvent;
import com.walktour.model.Parameter;
import com.walktour.service.automark.AutoMarkManager;
import com.walktour.service.automark.glonavin.GlonavinDataManager;
import com.walktour.service.automark.glonavin.GlonavinUtils;
import com.walktour.service.automark.glonavin.bean.GlonavinPoint;
import com.walktour.service.automark.glonavin.eventbus.ShowConfirmStartAutoMarkDialogEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 地图视图类
 *
 * @author jianchao.wang
 */
@SuppressLint("DrawAllocation")
public class MapView extends android.support.v7.widget.AppCompatImageView implements PointIndexChangeLinstener {
    /**
     * 重绘地图
     */
    public static final String ACTION_BASE_REDRAW = "com.walktour.base.redraw";

    /**
     * 当前加载室内地图路径
     */
    public static final String SP_INDOOR_MAP_PATH = "indoor_map_path";
    /**
     * 当前是否加载了室内地图
     */
    public static final String SP_IS_LOAD_INDOOR_MAP = "is_load_indoor_map";

    /**
     * 日志标记
     */
    private static final String TAG = "MapView";
    /**
     * 设置管理对象
     */
    private ParameterSetting mParameterSet;
    /**
     * 最大地图加载大小
     */
    public static final int IMG_MAX_PIXELS = 1200 * 800;
    /**
     * Mark实预打点时画笔粗细
     */
    private final int markCirclePaint = 3;
    /**
     * 画事件虚线粗细
     */
    private final int dropEventPaintWidth = 6;
    /**
     * 根据当前窗口适配放大倍数，如果width>540则*2
     */
    private int dropPaintMultiple = 1;
    /**
     * 是否注册消息接收
     */
    private boolean isRegisterReceiver = false;
    /**
     * 画笔对象
     */
    private final Paint mPaint = new Paint();
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 隐藏对话框
     */
    private static final int DIS_DIALOG = 1000;
    /**
     * 点击屏幕的X轴坐标点
     */
    private float mTouchStartX = 0;
    /**
     * 点击屏幕的Y轴坐标点
     */
    private float mTouchStartY = 0;
    /**
     * 是否移动
     */
    private boolean isMove = false;
    /**
     * 是否居中
     */
    private boolean isCenter = true;
    /**
     * 是否切换
     */
    private boolean isSetPoint = false;
    /**
     * 是否画线
     */
    private static boolean isDrawline = false;
    /**
     * 采样点标识
     */
    private Drawable marker;
    /**
     * 密度
     */
    private float mDensity;
    /**
     * 显示信息的弹出窗口
     */
    private View popupView;
    /**
     * 中心点坐标
     */
    private Point rectCenterPoint = new Point();
    /**
     * 记录选中的事件点
     */
    private AlarmModel selectAlarm = null;
    /**
     * 值打点队列
     */
    private static Stack<Point> linePointStack = new Stack<>();
    /**
     * 当前点坐标
     */
    private Point currentPoint = new Point();
    /**
     * MIF文件解析器
     */
    private static MifParser parserMif = new MifParser();
    /**
     * 基站数据解析器
     */
    private static BaseDataParser parserBase = new BaseDataParser();
    /**
     * TAB文件解析器
     */
    private static TabDataParser parserTab = new TabDataParser();
    /**
     * 最后一个地图事件点
     */
    private static MapEvent lastEvent;
    /**
     * 图片宽
     */
    private static int viewWidth;
    /**
     * 图片中心宽
     */
    private static int viewCenterWidth;
    /**
     * 图片高
     */
    private static int viewHeight;
    /**
     * 图片中心高度
     */
    private static int viewCenterHeight;
    /**
     * 地图文件路径
     */
    private String mFilePath = "";
    /**
     * 手势监测器
     */
    private GestureDetector mGestureDetector;
    /**
     * 应用实例
     */
    private static ApplicationModel appModel = ApplicationModel.getInstance();
    /**
     * MIF文件的最大限制
     */
    private static final int MIF_MAX_SIZE = 4 * 1000 * 1000;
    /**
     * 当前打点序号
     */
    private int index = 0;
    /**
     * 网络类型
     */
    private int phoneType = 0;
    /**
     * 最后一个点的时间
     */
    private long lastPointTime = 0;
    /**
     * 告警事件列表
     */
    private List<AlarmModel> alarmList;
    /**
     * 数据管理对象
     */
    private DatasetManager mDatasetManager;
    /**
     * GPS坐标事件列表
     */
    private List<MapEvent> gpsLocal;
    /**
     * 最后的地图事件
     */
    private static MapEvent lastMapEvent;
    /**
     * 事件点画笔
     */
    private Paint pointPaint = new Paint();
    /**
     * 缩放监听事件
     */
    private ScaleGestureDetector mScaleDetector;

    /**
     * 绘制格纳微自动打点划出起始点和方向箭头画笔
     */
    private Paint mDirectionArrowPaint;

    private boolean mPerformTouchEvent;
    private boolean mPerformDraw;
    private double mLatitude;
    private double mLongitude;

    @SuppressLint("InflateParams")
    @SuppressWarnings("deprecation")
    public MapView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleGestureListener());
        mDatasetManager = DatasetManager.getInstance(this.getContext().getApplicationContext());
        mDatasetManager.addPointIndexChangeListener(this);
        mParameterSet = ParameterSetting.getInstance();
        mParameterSet.initMapLocusShape(context);
        mDensity = context.getResources().getDisplayMetrics().density;

        marker = getResources().getDrawable(R.drawable.iconmarker);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        dropPaintMultiple = display.getWidth() > 540 ? 2 : 1;
        LogUtil.w(TAG, "---dropPaintMultiple:" + dropPaintMultiple + "--width:" + display.getWidth());
        popupView = LayoutInflater.from(context).inflate(R.layout.mapview_pop, null);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        phoneType = tm.getPhoneType();
        initPointPaint();
        if (MapFactory.getMapData().getMap() != null) {
            LogUtil.d(TAG, "---SavedMapData.map have set !");
        } else {
            LogUtil.d(TAG, "---SavedMapData.map is null !");
        }
        if (!MapFactory.getMapData().getEventQueue().isEmpty()) {
            LogUtil.w(TAG, "---SavedMapData.event_Queue is not null");
        } else {
            LogUtil.w(TAG, "---SavedMapData.event_Queue is  null");
        }
        // LogUtil.w(tag, "----scale="+scale);
        LogUtil.w(TAG, "----is gps test=" + GpsInfo.getInstance().isJobTestGpsOpen());
        mGestureDetector = new GestureDetector(this.mContext, new GestureListener());

        setLongClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        // 是否需要清除地图和轨迹(运行开始测试后)
        LogUtil.w(TAG, "-----appModel.isNeedToCleanMap()=" + appModel.isNeedToCleanMap());
        if (appModel.isNeedToCleanMap()) {
            appModel.setNeedToCleanMap(false);
            clearMapData();
        }

        LogUtil.w(TAG,
                "---appModel.isPreviouslyTest=" + appModel.isPreviouslyTest() + " flag_indoor=" + MapFactory.isLoadIndoor());
        // 如果是预打点测试
        if (appModel.isPreviouslyTest()) {
            if (MapFactory.getMapData().getMap() == null) {
                LogUtil.w(TAG, "------SavedMapData.map is null");
                MapFactory.getMapData().setMap(createBlankBitmap());
            }
            invalidate();
        } else { // 如果不是预打点测试
            // 2013.3.21 增加逻辑，解决进行CQT测试时，Mif地图未被清除
            if (appModel.isGerenalTest()) {
                if (MapFactory.isLoadMIF()) {
                    MapFactory.setLoadMIF(false);
                    clearMifMap();
                }
            } else {
                if (!MapFactory.isLoadMIF() && appModel.isTestJobIsRun()) {
                    if (appModel.getFloorModel() != null)
                        appModel.getFloorModel().setTestMapPath("");
                }
            }

            // 如果是室内测试
            LogUtil.i(TAG, "---is indoor test=" + appModel.isIndoorTest());
            if (appModel.isIndoorTest()) {
                if (MapFactory.isLoadMIF()) {
                    MapFactory.setLoadMIF(false);
                    clearMifMap();
                }
                File file = new File(appModel.getFloorModel().getTestMapPath());
                if (file.isFile()) {
                    try {
                        LogUtil.i(TAG, "---flag_indoor=" + MapFactory.isLoadIndoor());
                        if (MapFactory.isLoadIndoor()) {
                            // clearmapdata();
                            // clearMap();
                            // MapFactory.setLoadIndoor(false);
                            // MapFactory.getMapData().setScale(1);
                            LogUtil.i(TAG, "---file.getAbsolutePath()=" + file.getAbsolutePath());
                            if (file.getAbsolutePath().endsWith("tab")) {
                                this.getViewCenterWidthAndHeight();
                                mFilePath = file.getAbsolutePath();
                                new ParserTabThread().start();
                            } else {
                                this.getViewCenterWidthAndHeight();
                                if (MapFactory.isLoadTAB()) {
                                    isCenter = true;
                                }
                                MapFactory.setLoadTAB(false);
                                MapFactory.getMapData().setMap(createBitmap(file.getAbsolutePath()));
                            }
                        }
                    } catch (Exception e) {
                        LogUtil.e(TAG,e.getMessage(),e);
                    }
                }
                invalidate();
            } else {
                // clearmapdata();
                if (MapFactory.getMapData().getMap() == null) {
                    LogUtil.w(TAG, "------mbmptest is null");
                    MapFactory.getMapData().setMap(createBlankBitmap());
                }
                invalidate();
            }
        }
    }

    /**
     * 获取图片中心的宽度和高度
     */
    @SuppressLint("NewApi")
    private void getViewCenterWidthAndHeight() {
        if (viewWidth > 0)
            return;
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        viewWidth = outSize.x;
        viewHeight = outSize.y;
        viewCenterWidth = viewWidth / 2;
        viewCenterHeight = viewHeight / 2;
        isCenter = false;
    }


    /**
     * 除缩放外的手势监听类
     *
     * @author jianchao.wang
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
            // 限制用户在未创建RCU文件时进行打点操作
            if (appModel.isTestJobIsRun() && !appModel.isRcuFileCreated()) {
                Toast.makeText(getContext(), R.string.main_indoor_wait, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (MapFactory.isLoadTAB() && appModel.isTestJobIsRun()) {
                if ((System.currentTimeMillis() - lastPointTime) < 2000) {
                    return true;
                }
            }
            saveGlonavinDirectionArrow(ev);
            drawCalibrationPoint((int) ev.getX(), (int) ev.getY());
            // tabOnClickListerner((int) xd,(int)yd);

            return true;
        }

        public void onShowPress(MotionEvent ev) {
        }

        public void onLongPress(MotionEvent ev) {
            deletePoint((int) ev.getX(), (int) ev.getY());

        }

        public boolean onDown(MotionEvent ev) {
            mTouchStartX = ev.getX();
            mTouchStartY = ev.getY();
            if (MapFactory.isLoadTAB()) {
                if (ViewPort.getInstance().mDownPoint == null) {
                    ViewPort.getInstance().mDownPoint = new Point();
                }
                if (ViewPort.getInstance().mMovePoint == null) {
                    ViewPort.getInstance().mMovePoint = new Point();
                }
                ViewPort.getInstance().mDownPoint.x = (int) Math.floor(ev.getX());
                ViewPort.getInstance().mDownPoint.y = (int) Math.floor(ev.getY());
                ViewPort.getInstance().mDownPoint2D.x = ViewPort.getInstance().getViewArea().x;
                ViewPort.getInstance().mDownPoint2D.y = ViewPort.getInstance().getViewArea().y;
            }
            if (isDrawline && linePointStack.empty()) {
                SavedMapData mapData = MapFactory.getMapData();
                float scale = mapData.getScale();
                Point p = new Point((int) ((ev.getX() - mapData.getMapShowX()) / scale),
                        (int) ((ev.getY() - mapData.getMapShowY()) / scale));
                linePointStack.push(p);
            }
            invalidate();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LogUtil.w(TAG, "-----onScroll------- ");
            SavedMapData mapData = MapFactory.getMapData();
            if (isDrawline) {
                if (judgePointInRect((int) e2.getX(), (int) e2.getY())) {
                    float scale = MapFactory.getMapData().getScale();
                    currentPoint.x = (int) ((e2.getX() - mapData.getMapShowX()) / scale);
                    currentPoint.y = (int) ((e2.getY() - mapData.getMapShowY()) / scale);
                    isMove = true;
                    invalidate();
                }
            } else {
                int moveX = (int) (e2.getX() - mTouchStartX);
                int moveY = (int) (e2.getY() - mTouchStartY);

                // if (MapFactory.isLoadTAB()) {
                // ViewPort.getInstance().mMovePoint.x = (int)
                // Math.floor(e2.getX());
                // ViewPort.getInstance().mMovePoint.y = (int)
                // Math.floor(e2.getY());
                // Point pt = new Point();
                // pt.x = ViewPort.getInstance().mMovePoint.x -
                // ViewPort.getInstance().mDownPoint.x;
                // pt.y = ViewPort.getInstance().mMovePoint.y -
                // ViewPort.getInstance().mDownPoint.y;
                // ViewPort.getInstance().move(ViewPort.getInstance().mDownPoint2D,
                // pt);
                // moveX = pt.x;
                // moveY = pt.y;
                // }
                mapData.setMapShowX(mapData.getMapShowX() + moveX);
                mapData.setMapShowY(mapData.getMapShowY() + moveY);
                if (!MapFactory.isLoadMIF() && !MapFactory.isLoadBase()) {
                    invalidate();
                }

                if (MapFactory.isLoadTAB()) {
                    ViewPort.getInstance().mDownPoint.x = (int) Math.floor(e2.getX());
                    ViewPort.getInstance().mDownPoint.y = (int) Math.floor(e2.getY());
                    ViewPort.getInstance().mDownPoint2D.x = ViewPort.getInstance().getViewArea().x;
                    ViewPort.getInstance().mDownPoint2D.y = ViewPort.getInstance().getViewArea().y;
                }

                mTouchStartX = e2.getX();
                mTouchStartY = e2.getY();

            }
            return true;
        }

    }

    /**
     * 缩放手势监听类
     *
     * @author jianchao.wang
     */
    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = detector.getScaleFactor();
            float origScale = MapFactory.getMapData().getScale();
            MapFactory.getMapData().setScale(origScale * mScaleFactor);
            caculateMapLeftPoint();

			if (MapFactory.isLoadMIF()) {
				MapFactory.getMapData().setMap(parserMif.CreateBitmap(1000 * MapFactory.getMapData().getScale()));
				invalidate();
			}
            return true;
        }

    }


    /**
     * 将划出的起始点和终点坐标保存起来
     * @param ev
     */
    private void saveGlonavinDirectionArrow(MotionEvent ev){
        boolean isGlonavinTest = ApplicationModel.getInstance().isGlonavinTest();
        LogUtil.d(TAG,"isGlonavinTest " + isGlonavinTest);
        if(appModel.isTestJobIsRun() && isGlonavinTest && GlonavinDataManager.getInstance().getInitialAngle() == 0){
            GlonavinDataManager instance = GlonavinDataManager.getInstance();
            if(instance.isHasStartedBubblingValidPoint()){
                //设备已经初始化完成并开始吐点，则允许用户设置起始点和方向
                GlonavinPoint sP = instance.getStartPoint();
                GlonavinPoint eP = instance.getEndPoint();
                SavedMapData mapData = MapFactory.getMapData();
                float scale = mapData.getScale();
                if (null == sP) {
                    instance.setStartPoint(new GlonavinPoint((ev.getX()- mapData.getMapShowX())/scale, (ev.getY()- mapData.getMapShowY())/scale, 0));
                } else if(null == eP){
                    instance.setEndPoint(new GlonavinPoint((ev.getX()- mapData.getMapShowX())/scale, (ev.getY()- mapData.getMapShowY())/scale, 0));
                }
                //当起始点、方向已经确定后，将是否已经设置方向标志赋为true
                if(null != instance.getStartPoint() && null != instance.getEndPoint()){
                    LogUtil.i(TAG,"起始点、方向已经确定后，将是否已经设置方向标志赋为true");
//                    instance.setHasDirectionSet(true);
                    EventBus.getDefault().post(new ShowConfirmStartAutoMarkDialogEvent());
                }
            }else{
                //反之，弹出设备未初始化对话框
                ToastUtil.showShort(getContext(),getContext().getString(R.string.glonavin_auto_mark_initializing));
            }
        }
    }

    /**
     * 初始化事件点画笔
     */
    private void initPointPaint() {
        pointPaint.setTypeface(null);
        pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pointPaint.setStrokeWidth(1);
        pointPaint.setColor(Color.RED);
        pointPaint.setAntiAlias(true);
        pointPaint.setDither(true);
        pointPaint.setPathEffect(new DashPathEffect(new float[]{1, 2, 1, 2}, 1));
    }

    /**
     * 画校准点
     *
     * @param pointX 手机屏幕的X点
     * @param pointY 手机屏幕的Y点
     */
    private void drawCalibrationPoint(int pointX, int pointY) {
        if (MapFactory.getMapData().isSetPoint()) {
            if (MapFactory.isLoadMIF()) {
                Toast.makeText(this.getContext(), R.string.unvailable_mark_point, Toast.LENGTH_LONG).show();
                return;
            }
            if (!appModel.isGpsTest()) {
                // if (judgePointInRect(pointX, pointY)) {
                // 从堆中清除校准点
                if (!MapFactory.getMapData().getPointStatusStack().isEmpty()) {
                    if (MapFactory.getMapData().getPointStatusStack().lastElement()
                            .getStatus() == PointStatus.POINT_STATUS_CALIBRATION) {
                        MapFactory.getMapData().getPointStatusStack().pop();
                    }
                }
                PointStatus ps = new PointStatus();
                SavedMapData mapData = MapFactory.getMapData();
                PointF point = new PointF((pointX - mapData.getMapShowX()),(pointY - mapData.getMapShowY()));
                LogUtil.i(TAG, "pointX:" + pointX + ",x:" + mapData.getMapShowX());
                LogUtil.i(TAG, "pointY:" + pointY + ",y:" + mapData.getMapShowY());
                LogUtil.i(TAG, "point.x:" + point.x + ",point.y:" + point.y);

                //修复未开始测试时打点在地图缩放下偏移问题
                // (当处于开始测试时，在writeMarkPoint()方法最后做了÷scale动作，因此开始测试状态下地图缩放打点不会偏移)
                //2018.5.30 打点微调出现微调控制面板和实际手动指定的地方不一致、预打点缩放时转换实打点偏移问题，现在所有点统一都在此处做/scale操作，移除writeMarkPoint()方法最后/scale操作
                float scale = mapData.getScale();
                point.set((point.x/scale),(point.y/scale));

                // 如果正在测试
                if (appModel.isTestJobIsRun()) {
                    boolean isExistPrevious = false;
                    // 如果是预打点
                    if (appModel.isPreviouslyTest()) {
                        // 判断是否还存在预打点
                        for (int j = 0; j < MapFactory.getMapData().getPointStatusStack().size(); j++) {
                            if (MapFactory.getMapData().getPointStatusStack().elementAt(j)
                                    .getStatus() == PointStatus.POINT_STATUS_PREVIOUS) {
                                index = j;
                                isExistPrevious = true;
                                break;
                            }
                        }
                    }
                    LogUtil.w(TAG, "----isExistPrevious=" + isExistPrevious);
                    /* 存在预打点时，直接进行打点的操作 */
                    if (isExistPrevious) {
                        drawPoint(point);
                        return;
                    }
                }

                // 如果不需要打点微调，直接打点
                if (ParameterSetting.getInstance().isMarkAccurately()) {
                    ps.setPoint(point);
                    ps.setStatus(PointStatus.POINT_STATUS_CALIBRATION);
                    MapFactory.getMapData().getPointStatusStack().push(ps);
                    invalidate();

                    // 通知Map显示Panel,根据点屏幕的范围决定Panel显示的位置
                    Intent intent = new Intent(ControlPanel.ACTION_MARK);
                    int marginLeft = (pointX >= viewWidth / 2)
                            ? (pointX - getResources().getDimensionPixelSize(R.dimen.panelwidth)) : (pointX + 8);
                    int marginTop = (pointY >= viewHeight - getResources().getDimensionPixelSize(R.dimen.panelheight))
                            ? (pointY - getResources().getDimensionPixelSize(R.dimen.panelheight))
                            : (pointY - getResources().getDimensionPixelSize(R.dimen.panelleft));// 在下方打点时罗盘调高一点，因为手指可能挡住该点
                    intent.putExtra(ControlPanel.KEY_LEFT, marginLeft);
                    intent.putExtra(ControlPanel.KEY_TOP, marginTop);
                    mContext.sendBroadcast(intent);
                } else {
                    drawPoint(point);
                }

                // }
            }
        } else {
            containsClickPoint(pointX, pointY);
        }
    }

    /**
     * 当前所点击点的信息
     * [功能详细描述]
     *
     * @param X 点击 X 坐标
     * @param Y 点击 Y 坐标
     */
    private void containsClickPoint(int X, int Y) {
        if (selectAlarm != null) {
            selectAlarm = null;
            invalidate();
        }
        SavedMapData mapData = MapFactory.getMapData();
        // markDrawCircleOut * dropPaintMultiple
        if (MapFactory.isLoadMIF()) {
            if (alarmList != null) {
                for (int i = 0; i < alarmList.size(); i++) {
                    MapEvent mapEvent = alarmList.get(i).getMapEvent();
                    if (mapEvent != null) {
                        float left = mapEvent.getxMIF();
                        float top = mapEvent.getyMIF();
                        float right = left + 48;
                        float bottom = top + 48;
                        RectF rectF = new RectF(left, top, right, bottom);
                        if (rectF.contains(X, Y)) {
                            selectAlarm = alarmList.get(i);
                            selectAlarm.getMapEvent().setX(left + 24);
                            selectAlarm.getMapEvent().setY(top);
                        }
                    }
                }
            }
            List<Parameter> parameterms = mParameterSet
                    .getCheckedParamertersByNet(MyPhoneState.getInstance().getCurrentNetForParam(this.mContext));
            int radius = this.getLocusRadius();

			if (gpsLocal!=null) {
				for (int i = gpsLocal.size() - 1; i >= 0; i--) {
					MapEvent mapEvent = gpsLocal.get(i);
					float px = mapEvent.getxMIF();
					float py = mapEvent.getyMIF();
					for (int j = 0; j < parameterms.size(); j++) {
						float left = px - radius + j * radius * 3;
						float right = px + radius + j * radius * 3;
						float top = py - radius;
						float bottom = py + radius;
						RectF rectF = new RectF(left, top, right, bottom);
						if (rectF.contains(X, Y)) {
							selectAlarm = new AlarmModel(System.currentTimeMillis(), Alarm.NORMAL);
							selectAlarm.setMapEvent(mapEvent);
							selectAlarm.getMapEvent().setX(px + j * radius * 3);
							selectAlarm.getMapEvent().setY(py);
							LocusParamInfo locusParamInfo = mapEvent.getParamInfoMap().get(parameterms.get(j).getShowName());
							selectAlarm
									.setMapPopInfo(mapEvent.getMapPopInfo() + "\n" + locusParamInfo.paramName + ":" + locusParamInfo.value);
							invalidate();
						}
					}
				}
			}

		} else {
            // 事件列表优先
            int alarmRect = 72;
            int paramRect = 24;
            float scale = MapFactory.getMapData().getScale();
            List<AlarmModel> alarmList = new ArrayList<>(MapFactory.getMapData().getEventQueue());
            for (AlarmModel alarmModel : alarmList) {
                MapEvent alarmMapEvent = alarmModel.getMapEvent();
                if (MapFactory.isLoadTAB() && DatasetManager.isPlayback) {
                    CoordData coorData = getCoordDataoftheMapbyGPSdata(
                            new GPSData(alarmMapEvent.getLongitude(), alarmMapEvent.getLatitude()));
                    alarmMapEvent.setX((float) coorData.x);
                    alarmMapEvent.setY((float) coorData.y);
                } else {
                    alarmMapEvent.setX((float) alarmMapEvent.getLongitude());
                    alarmMapEvent.setY((float) alarmMapEvent.getLatitude());
                }
                float px = alarmMapEvent.getX() * scale + mapData.getMapShowX();
                float py = alarmMapEvent.getY() * scale + mapData.getMapShowY();
                float left = px - (alarmModel.getAlarm() != null ? alarmRect : paramRect);
                float right = px + (alarmModel.getAlarm() != null ? alarmRect : paramRect);
                float top = py - (alarmModel.getAlarm() != null ? alarmRect : paramRect);
                float bottom = py + (alarmModel.getAlarm() != null ? alarmRect : paramRect);

                RectF rectF = new RectF(left, top, right, bottom);
                // LogUtil.w(TAG,"--alarmModel: px:" + px + "--long:" +
                // alarmMapEvent.longitude + "--py:" + py + "-lat:" +
                // alarmMapEvent.latitude
                // +"--scale:" + scale + "--l:" + left + "--r:" + right + "--t:"
                // + top + "--b:" + bottom + "--X:" + X + "--Y:" +Y
                // +"--isArm:" +(alarmModel.getAlarm() != null) + "--isIn:" +
                // (rectF.contains(X, Y)));
                if (alarmModel.getAlarm() != null) {
                    if (rectF.contains(X, Y)) {
                        alarmMapEvent.setX((float) alarmMapEvent.getLongitude());
                        alarmMapEvent.setY((float) alarmMapEvent.getLatitude());
                        selectAlarm = alarmModel;
                        if (DatasetManager.isPlayback) {
                            DatasetManager.getInstance(this.getContext()).getPlaybackManager().setSkipIndex(selectAlarm.getMsgIndex());
                        }
                        invalidate();
                    }
                } else {
                    if (rectF.contains(X, Y)) {
                        selectAlarm = new AlarmModel(System.currentTimeMillis(), Alarm.NORMAL);
                        MapEvent mapEvent = new MapEvent();
                        mapEvent.setX(alarmMapEvent.getX());
                        mapEvent.setY(alarmMapEvent.getY());
                        selectAlarm.setMapPopInfo(alarmMapEvent.getParamName() + ":"
                                + (alarmMapEvent.getValue() == -9999 ? "" : alarmMapEvent.getValue()));
                        selectAlarm.setMapEvent(mapEvent);
                        invalidate();
                    }
                }
            }
            List<PointStatus> pointList = new ArrayList<>(MapFactory.getMapData().getPointStatusStack());
            for (PointStatus pointStatus : pointList) {
                float px = (int) (pointStatus.getPoint().x * scale + mapData.getMapShowX());
                float py = (int) (pointStatus.getPoint().y * scale + mapData.getMapShowY());
                float left = px - 24;
                float right = left + 48;
                float top = py - 24;
                float bottom = top + 48;
                RectF rectF = new RectF(left, top, right, bottom);

                LogUtil.w(TAG, "--alarmModel2: px:" + px + "--py:" + py + "--scale:" + scale + "--l:" + left + "--r:" + right
                        + "--t:" + top + "--b:" + bottom + "--X:" + X + "--Y:" + Y + "--isIn:" + (rectF.contains(X, Y)));

                if (rectF.contains(X, Y)) {
                    selectAlarm = new AlarmModel(System.currentTimeMillis(), Alarm.NORMAL);
                    MapEvent mapEvent = new MapEvent();
                    mapEvent.setX(pointStatus.getPoint().x);
                    mapEvent.setY(pointStatus.getPoint().y);
                    selectAlarm.setMapPopInfo(pointStatus.getDescription());
                    selectAlarm.setMapEvent(mapEvent);
                    if (DatasetManager.isPlayback) {
                        DatasetManager.getInstance(MapView.this.getContext()).getPlaybackManager()
                                .setSkipIndex(pointStatus.getBeginPointIndex());
                    }
                    if (StringUtil.isNullOrEmpty(pointStatus.getDescription())) {
                        selectAlarm = null;
                    }
                    invalidate();
                }
            }
        }
    }

    /**
     * 随机删除预打点或者实打点
     */
    private void deletePoint(int X, int Y) {
        float scale = MapFactory.getMapData().getScale();
        List<PointStatus> pointList = new ArrayList<>(MapFactory.getMapData().getPointStatusStack());
        int pos = -1;
        SavedMapData mapData = MapFactory.getMapData();
        for (int i = 0; i < pointList.size(); i++) {
            PointStatus pointStatus = pointList.get(i);
            float px = (int) (pointStatus.getPoint().x * scale + mapData.getMapShowX());
            float py = (int) (pointStatus.getPoint().y * scale + mapData.getMapShowY());
            float left = px - 24;
            float right = px + 24;
            float top = py - 24;
            float bottom = py + 24;
            RectF rectF = new RectF(left, top, right, bottom);
            if (rectF.contains(X, Y)) {
                pos = i;
                break;
            }
        }
        if (pos == -1)
            return;
        PointStatus point = pointList.get(pos);
        LogUtil.w(TAG, "delete Point");
        MapFactory.getMapData().getPointStatusStack().remove(pos);
        // 实打点删除
        if (point.getStatus() != PointStatus.POINT_STATUS_PREVIOUS) {
            if (pos == pointList.size() - 1) {// 如果删除的点是最后一个
                if (pos > 0) {
                    MapFactory.getMapData().getEventQueue().removeAll(MapFactory.getMapData().getQueueStack().pop());
                }
                // 只有文件创建成功后，打点动作才会写到RCU文件中
                if (appModel.isRcuFileCreated()) {
                    writeMarkPoint(UtilsMethod.MARKSTATE_DEL, point, false);
                }
            } else {
                if (pos == 0) {// 如果删除的点是第一个,则同时删除第一个点和第二个点之间的事件
                    MapFactory.getMapData().getEventQueue().removeAll(MapFactory.getMapData().getQueueStack().remove(pos));
                } else {// 如果删除的点是中间的点，则把该点前后间的点之间的事件合并，同时重新计算显示的坐标
                    Queue<AlarmModel> eventQueue = MapFactory.getMapData().getQueueStack().get(pos - 1);
                    eventQueue.addAll(MapFactory.getMapData().getQueueStack().remove(pos));
                    PointStatus start = pointList.get(pos - 1);
                    PointStatus end = pointList.get(pos + 1);
                    end.setBeginPointIndex(point.getBeginPointIndex());
                    this.calculateEventPoint(eventQueue, start, end);
                }
                // 只有文件创建成功后，打点动作才会写到RCU文件中
                // 对于当前数据集的处理逻辑来看，删中间点暂时只能通过把要删除的点和后面的点全删除，然后再把后面的点添加来实现
                // TODO 这个处理逻辑要和数据集组商量一下
                if (appModel.isRcuFileCreated()) {
                    for (int i = pos; i < pointList.size(); i++) {
                        writeMarkPoint(UtilsMethod.MARKSTATE_DEL, pointList.get(i), false);
                    }
                    for (int i = pos + 1; i < pointList.size(); i++) {
                        writeMarkPoint(UtilsMethod.MARKSTATE_ADD, pointList.get(i), false);
                    }
                }
            }
        }
        invalidate();
    }

    /**
     * 重新计算打点间的事件显示坐标
     *
     * @param eventQueue 事件队列
     * @param start      开始点
     * @param end        结束点
     */
    private void calculateEventPoint(Queue<AlarmModel> eventQueue, PointStatus start, PointStatus end) {
        int count = eventQueue.size();
        float addx = 0;
        float addy = 0;
        int addIndex = 0;
        int startIndex = start.getBeginPointIndex();
        int endIndex = end.getEndPointIndex();
        for (AlarmModel alarmModel : eventQueue) {
            MapEvent event = alarmModel.getMapEvent();
            double startX = start.getPoint().x + alarmModel.getParamIndex() * 10;
            double endX = end.getPoint().x + alarmModel.getParamIndex() * 10;
            int indexOffset = (endIndex - startIndex) / (count + 1);
            addx += (float) (endX - startX) / (count + 1);
            addy += (float) (end.getPoint().y - start.getPoint().y) / (count + 1);
            addIndex += indexOffset;
            event.setLongitude(startX + addx);
            event.setLatitude(start.getPoint().y + addy);
            event.setBeginPointIndex(startIndex + addIndex - indexOffset);
            event.setEndPointIndex(startIndex + addIndex);
        }
    }

    /**
     * 操作校准点
     *
     * @param control 操作点的类型,ControlPanle.CONTROL_*中的一个
     */
    private void moveCalibrationPoint(int control) {
        // 从堆中清除校准点
        if (!MapFactory.getMapData().getPointStatusStack().isEmpty()) {
            if (MapFactory.getMapData().getPointStatusStack().lastElement()
                    .getStatus() == PointStatus.POINT_STATUS_CALIBRATION) {
                PointStatus ps = MapFactory.getMapData().getPointStatusStack().pop();
                switch (control) {
                    case ControlPanel.CONTROL_ENTER:
                        PointF p = new PointF(ps.getPoint().x, ps.getPoint().y);
                        drawPoint(p);
                        return;
                    case ControlPanel.CONTROL_LEFT:
                        ps.getPoint().x -= ControlPanel.MOVE_PIX;
                        break;
                    case ControlPanel.CONTROL_UP:
                        ps.getPoint().y -= ControlPanel.MOVE_PIX;
                        break;
                    case ControlPanel.CONTROL_RIGHT:
                        ps.getPoint().x += ControlPanel.MOVE_PIX;
                        break;
                    case ControlPanel.CONTROL_DOWN:
                        ps.getPoint().y += ControlPanel.MOVE_PIX;
                        break;
                }
                MapFactory.getMapData().getPointStatusStack().push(ps);
                invalidate();
            }
        }
    }

    /**
     * 确定好位置后的打点
     */
    private void drawPoint(PointF point) {
        if (appModel.isGpsTest())
            return;
        if (!MapFactory.getMapData().isSetPoint())
            return;
        // 如果启动自动打点，则当点击屏幕时
        if (MapFactory.getMapData().isAutoMark()) {
            Intent service = new Intent(WalkMessage.AUTOMARK_FIRST_POINT);
            service.putExtra("x", point.x);
            service.putExtra("y", point.y);
            this.mContext.sendBroadcast(service);
        }
        PointStatus lastPoint = new PointStatus();
        PointStatus newPoint = new PointStatus();
        lastPoint.setPoint(point);
        newPoint.setPoint(point);
        if (appModel.isTestJobIsRun()) {
            lastPoint.setStatus(PointStatus.POINT_STATUS_EFFECTIVE);
            newPoint.setStatus(PointStatus.POINT_STATUS_EFFECTIVE);
        } else {
            lastPoint.setStatus(PointStatus.POINT_STATUS_PREVIOUS);
            newPoint.setStatus(PointStatus.POINT_STATUS_PREVIOUS);
        }

        // 如果是室内测试把轨迹写入RCU文件
        // 2011.07.15去掉是否室内测试的判断，不管是否室内测试都打点
        // if( appModel.isIndoorTest() ){

        // 当前打点在有效区域内
        // if(JudgePointInRect(finalX, finalY)) {
        // 如果正在测试
        Stack<PointStatus> pointStatusStack = MapFactory.getMapData().getPointStatusStack();
        if (appModel.isTestJobIsRun()) {
            // 测试运行过程中，如果有打点动作，需要判断当前RC文件是否创建成功，如果不成功，不做任务动作
            if (appModel.isRcuFileCreated()) {
                boolean isExistPrevious = false;
                // 如果是预打点
                if (appModel.isPreviouslyTest()) {
                    // 判断是否还存在预打点
                    for (int i = 0; i < pointStatusStack.size(); i++) {
                        if (pointStatusStack.elementAt(i).getStatus() == PointStatus.POINT_STATUS_PREVIOUS) {
                            index = i;
                            isExistPrevious = true;
                            // if(!isFirstClear){
                            // TraceInfoInterface.traceData.getMapEvent().clear();
                            // isFirstClear = true;
                            // LogUtil.w(tag, "----clear points---");
                            // }
                            LogUtil.w(TAG, "----previousIndex=" + index);
                            break;
                        }
                    }
                }
                LogUtil.w(TAG, "----isExistPrevious=" + isExistPrevious);
                /* 存在预打点时，newPoint总是取第index个预打点开 **/
                if (isExistPrevious) {
                    if (!pointStatusStack.empty()) {
                        newPoint = pointStatusStack.elementAt(index);
                        // 如果是预打点状态，变为实打点状态
                        if (newPoint.getStatus() == PointStatus.POINT_STATUS_PREVIOUS) {
                            newPoint.setStatus(PointStatus.POINT_STATUS_PREVIOUS_EFFECTIVE);
                            LogUtil.w(TAG, "----change point status");
                        }
                        // 如果只取到第1个点或者队列长度为1，不做任何操作
                        if (index == 0 || pointStatusStack.size() == 1) {
                            LogUtil.w(TAG, "----change point is empty");
                        } else {
                            // 如果正在测试，并且至少已经取到第二个点，那么lastPoint取newPoint的上一个点
                            if (index <= pointStatusStack.size()) {
                                lastPoint = pointStatusStack.elementAt(index - 1);
                                LogUtil.w(TAG, "-----(i-1)point=" + (index - 1));
                            }
                        }
                    }
                } else {
                    // 如果不存在预打点了，重设index
                    index = -1;
                    LogUtil.w(TAG, "---index=" + index);
                    // 如果没有预打点了，那么才会加入新点
                    LogUtil.w(TAG, "---has no vitual point");
                    if (!pointStatusStack.empty()) {
                        lastPoint = pointStatusStack.lastElement();
                    }
                    TranslateGYOInfoToGPSInfo(newPoint);
                    pointStatusStack.push(newPoint);
                    LogUtil.w(TAG, "----in test push point");
                }
            }
        } else {
            // 如果没有测试，那么取上次pointStatus_Stack中最后一个点，newPoint为新打点，通过这两个点去获取事件点
            if (!pointStatusStack.isEmpty()) {
                lastPoint = pointStatusStack.lastElement();
            }
            firstPoint = lastPoint;
            // 如果没有测试，肯定会加入新点
            pointStatusStack.push(newPoint);
            LogUtil.w(TAG, "----in non test push point");
        }
        // 获取新增点说明
        if (newPoint.getStatus() == PointStatus.POINT_STATUS_EFFECTIVE
                || newPoint.getStatus() == PointStatus.POINT_STATUS_PREVIOUS_EFFECTIVE) {
            int currtentIndex = mDatasetManager.getTotalPointCount(DatasetManager.PORT_2);
            String[] radomParams = mDatasetManager.getCQTMapEventPoint(
                    Integer.valueOf(ParameterSetting.getInstance().getMapParameter().getId(), 16), lastPoint.getEndPointIndex(),
                    currtentIndex);
            String description = ParameterSetting.getInstance().getMapParameter().getShowName() + ":"
                    + ((radomParams.length > 0 && Double.valueOf(radomParams[radomParams.length - 1]) != -9999)
                    ? Double.valueOf(radomParams[radomParams.length - 1])
                    : getResources().getString(R.string.map_invalid_value));
            newPoint.setDescription(description);
        }

        if (pointStatusStack.size() == 1 || index == 0) {
            TraceInfoInterface.traceData.getMapEvent().clear();
//        } else {
            /*
			 * Queue<AlarmModel> q = GetPoint(radomParams, ps, psnew);
			 * event_Queue.addAll(q); queue_Stack.push(q);
			 */
            // GetEvents();
        }

        // 只有文件创建成功后，打点动作才会写到RCU文件中
        if (appModel.isRcuFileCreated()) {
            Bitmap map = MapFactory.getMapData().getMap();
            LogUtil.w(TAG, "----x:" + newPoint.getPoint().x + "-----y:" + newPoint.getPoint().y + "---width:"
                    + (map == null ? viewWidth : map.getWidth()) + "---height:" + (map == null ? viewHeight : map.getHeight()));
            // 如果当前加载的为TAB格式地图且不是室内地图，那么打点存到RCU文件中的为GPS点原始数据
            if (MapFactory.isLoadTAB()) {
                lastPointTime = System.currentTimeMillis();
                if (ViewPort.getInstance().getProj() != null) {
                    writeGPSMarkPoint(UtilsMethod.MARKSTATE_ADD, newPoint);
                } else {
                    writeMarkPoint(UtilsMethod.MARKSTATE_ADD, newPoint, true);
                }
            } else {
                writeMarkPoint(UtilsMethod.MARKSTATE_ADD, newPoint, true);
            }
        }
        // }
		isMove = false;
        invalidate();
    }

    public void TranslateGYOInfoToGPSInfo(PointStatus newpoint) {
        Bitmap map = MapFactory.getMapData().getMap();
        float scale = MapFactory.getMapData().getScale();
        int sampleSize = MapFactory.getMapData().getSampleSize();
        double pointX = (newpoint.getPoint().x) * sampleSize;
        double pointY = (map.getHeight() - newpoint.getPoint().y) * sampleSize;

        final SharePreferencesUtil sharePreferencesUtil = SharePreferencesUtil.getInstance(WalktourApplication.getAppContext());
        final String indoorMapPath = sharePreferencesUtil.getString(MapView.SP_INDOOR_MAP_PATH);
        float plottingScale = sharePreferencesUtil.getFloat(indoorMapPath,1.0f);//像素/米
        float northShift = (float) (pointY / plottingScale);
        float eastShift = (float) (pointX / plottingScale);


        double PI =  3.14159265;
        int CIInvalidValue=-9999;

        double lastLongitude = mLongitude;	//经度
        double LastLatitude  = mLatitude;	//维度
        double LastAltitude  = 0;	//海拔
        double NorthShift	 = northShift;	//南北偏移
        double EastShift	 = eastShift;	//东西偏移
        double HeigthShift   = 0;	//上下偏移
        double height=0;
        double lat=0;
        double lng = 0;
        if ((lastLongitude != CIInvalidValue) && (LastLatitude != CIInvalidValue) &&
                (lastLongitude != -1) && (LastLatitude != -1) && (LastAltitude != CIInvalidValue)) {
            boolean bLong, blat;
            height = (float)(LastAltitude + HeigthShift);

            lat = LastLatitude + NorthShift/(111 * 1000);  //纬度1度 = 大约111km
            if (lat > 0)
            {
                blat = true;
            }
            else
            {
                lat = -lat;
                blat = false;
            }

            if (lat < 90)
            {
                double dTemp = lat * PI / 180;
                lng = lastLongitude + EastShift/(111 * 1000*Math.cos(dTemp));  //经度1度 = 等于111km乘纬度的余弦
            }

            if (lng > 0)
            {
                bLong = true;
            }
            else
            {
                bLong = false;
                lng = - lng;
            }

            if (bLong) //东经取正
            {
                lng = Math.abs(lng);
            }
            else           //西经取负
            {
                lng = -Math.abs(lng);
            }

            if (blat) //北纬取正
            {
                lat = Math.abs(lat);
            }
            else           //南纬取负
            {
                lat = -Math.abs(lat);

            }
        }
        newpoint.setLatLng(new MyLatLng(lat,lng));
    }

    private static PointStatus firstPoint = new PointStatus();

    public static PointStatus getFirstPoint() {
        return firstPoint;
    }

    /**
     * 从文件创建Bitmap
     */
    private Bitmap createBitmap(String filepath) {
        // 图片大于10M，提示不让继续加载，并返回一张空白地图
        File f = new File(mFilePath);
        if (f.isFile()) {
            if (f.length() > 10 * 1000 * 1000) {
                Toast.makeText(mContext, R.string.main_cantnotload, Toast.LENGTH_SHORT).show();
                return createBlankBitmap();
            }
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设定后不会给图片分配内存，可以获取到原图的宽高
        opts.inJustDecodeBounds = true;
        opts.inSampleSize = UtilsMethod.computeSuitedSampleSize(opts, IMG_MAX_PIXELS);
        MapFactory.getMapData().setScale(opts.inSampleSize > 0 ? 1 / opts.inSampleSize : 1);
        MapFactory.getMapData().setSampleSize(opts.inSampleSize);
        opts.inJustDecodeBounds = false;
        LogUtil.w(TAG, "---opts.inSampleSize=" + opts.inSampleSize);
        return ImageUtil.rotaingImageView(filepath,BitmapFactory.decodeFile(filepath, opts));
    }

    private static boolean isFirstToCenter = false;

    /**
     * 清除地图方法
     */
    public static void clearMap() {
        MapFactory.getMapData().setMap(null);
        if (appModel.getFloorModel() != null) {
            appModel.getFloorModel().setTestMapPath("");
        }
    }

    /**
     * 触摸事件处理
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
		mPerformTouchEvent = true;
        // result = onTabMapTouch(event);
		int pointerCount = event.getPointerCount();
		if (pointerCount >1){
			result = mScaleDetector.onTouchEvent(event);
		}else {
			result = mGestureDetector.onTouchEvent(event);
		}

		if (result) {
			this.invalidate();
		}

        SavedMapData mapData = MapFactory.getMapData();
        // 如果是一般测试并且能够打点，如果先打点了，那么就不能够再做GPS测试
        if (appModel.isGerenalTest() && MapFactory.getMapData().isSetPoint()) {
            // appModel.setGerenalTest(false);
            appModel.setGpsTest(false);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
//				parserBase.setCoordinate(0, 0);
//				MapFactory.getMapData().setMapShowX(0);
//				MapFactory.getMapData().setMapShowY(0);
//                float scale = MapFactory.getMapData().getScale();
//                if (MapFactory.isLoadMIF()) {
//                    parserMif.setCoordinate(mapData.getMapShowX(), mapData.getMapShowY());
                    // LogUtil.w(tag,"@@@@x="+x+" yd_x="+yd_x+" y="+y+" yd_y="+yd_y);
                    // LogUtil.w(tag,"@@@@xd="+xd+" yd_x="+yd_x+" yd_y="+yd_y);
//                    MapFactory.getMapData().setMap(parserMif.CreateBitmap(1000 * scale));
//                    LogUtil.w(TAG, "----ACTION_UP");
                    // LogUtil.w(tag,"----xvalue touch="+MifParser.getXvalue()+" yvalue
                    // touch="+MifParser.getYvalue());
                    invalidate();
//                } else {
//                    if (MapFactory.isLoadBase() && !MapFactory.isLoadMIF()) {
//                        parserBase.setCoordinate(mapData.getMapShowX(), mapData.getMapShowY());
//                        MapFactory.getMapData().setMap(parserBase.createBitmap(1000 * scale));
//						Log.w("czc","ACTION_UP invalidate");
//                        invalidate();
//                    }
//                    return false;
//                }

                break;
        }
        mPerformTouchEvent = false;
        return result;
    }

    /**
     * 创建空白位图
     *
     * @return 创建位图
     */
    private Bitmap createBlankBitmap() {
        LogUtil.i(TAG, "---createBlankBitmap()");
        this.getViewCenterWidthAndHeight();
        return null;
    }

    /**
     * 绘制底层图
     *
     * @param canvas 画布
     */
    private void drawBottomMap(Canvas canvas) {
        if (MapFactory.getMapData().getMap() == null) {
            MapFactory.getMapData().setMap(createEmptyBitmap());
            return;
        }
        Matrix matrix = new Matrix();
        if (MapFactory.isLoadMIF() || MapFactory.isLoadBase()) {
            matrix.postScale(1, 1);
        } else {
            float scale = MapFactory.getMapData().getScale();
            LogUtil.d(TAG, "----drawBottomMap----" + scale);
            matrix.postScale(scale, scale);
        }
        SavedMapData mapData = MapFactory.getMapData();
//        if (isCenter) {
//			Log.w("czc","isCenter="+isCenter);
//            getRectCenter();
//            canvas.translate(rectCenterPoint.x, rectCenterPoint.y);

//		if (mMid!=null) {
//			canvas.scale(mapData.getScale(), mapData.getScale(), mMid.x, mMid.y);
//		}else {
//			canvas.scale(mapData.getScale(), mapData.getScale(), viewCenterWidth, viewCenterHeight);
//		}
		canvas.translate(mapData.getMapShowX(), mapData.getMapShowY());

            // LogUtil.w(tag,
            // "----rectCenterPoint.x="+rectCenterPoint.x+"
            // rectCenterPoint.y="+rectCenterPoint.y);
//            mapData.setMapShowX(rectCenterPoint.x);
//            mapData.setMapShowY(rectCenterPoint.y);
//        } else {
//            if (MapFactory.isLoadMIF() || MapFactory.isLoadBase()) {
//				Log.w("czc","isLoadMIF translate=0,0");
//                canvas.translate(0, 0);
//            } else {
//				Log.w("czc","translate="+mapData.getMapShowX()+","+mapData.getMapShowY());
//                canvas.translate(mapData.getMapShowX(), mapData.getMapShowY());
//                // LogUtil.e(TAG, "----yd_x + x + movex="+(yd_x + x +
//                // movex)+" yd_y + y + movey="+(yd_y + y + movey));
//            }
//        }
        if (MapFactory.isLoadTAB()) {
            canvas.drawBitmap(MapFactory.getMapData().getMap(), matrix, mPaint);
        } else {
            canvas.drawBitmap(MapFactory.getMapData().getMap(), matrix, mPaint);
        }
        if (!MapFactory.isLoadMIF() && !MapFactory.isLoadBase()) {
            canvas.translate(-mapData.getMapShowX(), -mapData.getMapShowY());
        }
    }

    /**
     * 创建空白图
     * @return
     */
    private Bitmap createEmptyBitmap() {
        LogUtil.d(TAG,"-----------createEmptyBitmap---------");
        Bitmap bitmap = Bitmap.createBitmap(viewWidth,viewHeight, Bitmap.Config.ARGB_4444);
        return bitmap;
    }

    /**
     * 绘制打点轨迹
     *
     * @param canvas 画布
     */
    private void drawPointLocus(Canvas canvas) {
        if (MapFactory.getMapData().getPointStatusStack().empty())
            return;
        float scale = MapFactory.getMapData().getScale();
        float radius = this.getLocusRadius();
        List<PointStatus> pointList = new ArrayList<>(MapFactory.getMapData().getPointStatusStack());
        float lastPointX = -1;
        float lastPointY = -1;
        DecimalFormat format = new DecimalFormat("######.##");
        SavedMapData mapData = MapFactory.getMapData();
        for (PointStatus ps : pointList) {
            PointF p = ps.getPoint();
            float pointX = p.x * scale + mapData.getMapShowX();
            float pointY = p.y * scale + mapData.getMapShowY();

//          LogUtil.i(TAG, "drawPointLocus p.x:" + p.x + ";p.y:" + p.y + ",scale:" + scale);
//          LogUtil.i(TAG, "pointX:" + pointX + ";pointY:" + pointY);
//          LogUtil.i(TAG, "drawPointLocus MapShowX:" + mapData.getMapShowX() + ";MapShowY:" + mapData.getMapShowY());

            mPaint.setColor(Color.BLUE);
            mPaint.setStrokeWidth(markCirclePaint * dropPaintMultiple);
            // 添加外圈
            mPaint.setStyle(Paint.Style.STROKE);
            if (mParameterSet.getLocusShape() == 0) {
                canvas.drawCircle(pointX, pointY, radius, mPaint);
            } else {
                canvas.drawRect(pointX - radius / 2, pointY - radius / 2, pointX + radius / 2, pointY + radius / 2, mPaint);
            }
            // 如果启动自动打点，且选择显示当前的的移动距离
            if (MapFactory.getMapData().isAutoMark() && AutoMarkManager.isShowDistance) {
                if (lastPointX > 0 && lastPointY > 0) {
                    mPaint.setColor(Color.BLACK);
                    mPaint.setTextSize(35);
                    double distance = Math.sqrt(Math.pow(Math.abs((int) pointX - (int) lastPointX), 2)
                            + Math.pow(Math.abs((int) pointY - (int) lastPointY), 2)) / scale;
                    distance = distance * AutoMarkManager.buildingScale;
                    canvas.drawText(format.format(distance) + "m", pointX - radius, pointY - radius * 2, mPaint);
                }
            }

            // 校准打点时添加十字架
            if (ps.getStatus() == PointStatus.POINT_STATUS_CALIBRATION) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(markCirclePaint * dropPaintMultiple);
                canvas.drawLine(pointX - 16, pointY, pointX + 16, pointY, mPaint);
                canvas.drawLine(pointX, pointY - 16, pointX, pointY + 16, mPaint);
            }

            // 测试时打实心点
            if (ps.getStatus() == PointStatus.POINT_STATUS_EFFECTIVE
                    || ps.getStatus() == PointStatus.POINT_STATUS_PREVIOUS_EFFECTIVE) {
                mPaint.setColor(Color.RED);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(markCirclePaint * dropPaintMultiple);
                if (mParameterSet.getLocusShape() == 0) {
                    canvas.drawCircle(pointX, pointY, radius, mPaint);
                } else {
                    canvas.drawRect(pointX - radius / 2, pointY - radius / 2, pointX + radius / 2, pointY + radius / 2, mPaint);
                }

                if (DatasetManager.isPlayback) {
                    // 设置采样点标识
                    if (mDatasetManager.currentIndex >= ps.getBeginPointIndex()
                            && mDatasetManager.currentIndex <= ps.getEndPointIndex()) {
                        marker.setBounds((int) (pointX - marker.getIntrinsicWidth() / 2),
                                (int) (pointY - marker.getIntrinsicHeight()), (int) (pointX + marker.getIntrinsicWidth() / 2),
                                (int) pointY);
                        marker.draw(canvas);
                    }
                } else {
                    if (MapFactory.getMapData().getPointStatusStack().lastElement() == ps) {
                        marker.setBounds((int) (pointX - marker.getIntrinsicWidth() / 2),
                                (int) (pointY - marker.getIntrinsicHeight()), (int) (pointX + marker.getIntrinsicWidth() / 2),
                                (int) pointY);
                        marker.draw(canvas);
                    }
                }
            }
            lastPointX = pointX;
            lastPointY = pointY;
        }
    }

    /**
     * 绘制事件点
     *
     * @param canvas 画布
     */
    private void drawEvent(Canvas canvas) {
        if (MapFactory.getMapData().getEventQueue().isEmpty())
            return;
        float scale = MapFactory.getMapData().getScale();
        // 如果有加载MIF地图，以MIF地图为参照画上GPS轨迹
        if (MapFactory.isLoadMIF()) {
            double height = parserMif.getHeight();
            MifCenter firstpoint = MifParser.getFirstpoint();
            float yd_xl = parserMif.getYd_x();
            float yd_yl = parserMif.getYd_y();
            alarmList = new ArrayList<>(AlertManager.getInstance(mContext).getMapAlarmList());
            for (AlarmModel alarmModel : alarmList) {
                MapEvent mapEvent = alarmModel.getMapEvent();
                if (mapEvent != null) {
                    float x_screen = yd_xl + (float) ((mapEvent.getLongitude() - firstpoint.x) * 1000 * scale);
                    float y_screen = (float) height - (yd_yl + (float) ((mapEvent.getLatitude() - firstpoint.y) * 1000 * scale));
                    x_screen += MifParser.getXvalue();
                    y_screen += MifParser.getYvalue();
                    mapEvent.setxMIF(x_screen);
                    mapEvent.setyMIF(y_screen);
                    Bitmap bm = alarmModel.getIconBitmap(mContext);
                    if (bm != null) {
                        canvas.drawBitmap(bm, x_screen, y_screen, this.pointPaint);
                    }
                }
            }

        } else {
            List<AlarmModel> alarmList = new ArrayList<>(MapFactory.getMapData().getEventQueue());
            boolean isDrawMark = false;
            for (AlarmModel alarmModel : alarmList) {
                MapEvent event = alarmModel.getMapEvent();
                mPaint.setColor(event.getColor());
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(dropEventPaintWidth * dropPaintMultiple);
                float pointX;
                float pointY;
                SavedMapData mapData = MapFactory.getMapData();
                if (MapFactory.isLoadTAB() && DatasetManager.isPlayback) {
                    CoordData coorData = getCoordDataoftheMapbyGPSdata(new GPSData(event.getLongitude(), event.getLatitude()));
                    pointX = (float) coorData.x / scale + mapData.getMapShowX();
                    pointY = (float) coorData.y / scale + mapData.getMapShowY();
                } else {
					pointX = (float) event.getLongitude() * scale + mapData.getMapShowX();
					pointY = (float) event.getLatitude() * scale + mapData.getMapShowY();
				}
                Bitmap bm = alarmModel.getIconBitmap(mContext);
                // LogUtil.w(tag, "----e_x 11="+e_x+" e_y="+e_y);
                if (event.getSelectType() == 1) {
                    canvas.drawCircle(pointX, pointY, 5, this.pointPaint);
                }
                // canvas.drawPoint(e_x, e_y, mPaint);
                if (bm != null) {
                    canvas.drawBitmap(bm, pointX - bm.getWidth() / 2, pointY, this.pointPaint);
                } else {
                    canvas.drawPoint(pointX, pointY, mPaint);
                }
                if (DatasetManager.isPlayback && !isDrawMark) {
                    // 设置采样点标识
                    if (mDatasetManager.currentIndex >= event.getBeginPointIndex()
                            && mDatasetManager.currentIndex <= event.getEndPointIndex()) {
                        marker.setBounds((int) (pointX - marker.getIntrinsicWidth() / 2),
                                (int) (pointY - marker.getIntrinsicHeight()), (int) (pointX + marker.getIntrinsicWidth() / 2),
                                (int) pointY);
                        marker.draw(canvas);
                        isDrawMark = true;
                    }
                }
            }
        }
    }

    /**
     * 绘制GPS点轨迹
     *
     * @param canvas 画布
     */
    private void drawGPSLocus(Canvas canvas) {
        if (gpsLocal.isEmpty())
            return;
        float scale = MapFactory.getMapData().getScale();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(dropEventPaintWidth * dropPaintMultiple);
        LogUtil.w(TAG, "---drow GPS Local---");
        paint.setPathEffect(null);
        LogUtil.w(TAG, "---gps list is not null ---mif:" + MapFactory.isLoadMIF() + "--bas:" + MapFactory.isLoadBase()
                + "--tab:" + MapFactory.isLoadTAB());
        SavedMapData mapData = MapFactory.getMapData();
        // 如果有加载MIF地图，以MIF地图为参照画上GPS轨迹
        if (MapFactory.isLoadMIF()) {
            List<Parameter> parameterms = mParameterSet
                    .getCheckedParamertersByNet(MyPhoneState.getInstance().getCurrentNetForParam(this.mContext));
            // LogUtil.w(tag,
            // "----xvalue draw="+MifParser.getXvalue()+" yvalue
            // draw="+MifParser.getYvalue());
            double height = parserMif.getHeight();
            MifCenter firstpoint = MifParser.getFirstpoint();
            float yd_xl = parserMif.getYd_x();
            float yd_yl = parserMif.getYd_y();
            // LogUtil.w(tag, "---yd_xl:"+yd_xl+" yd_yl:"+yd_yl);
            lastEvent = new MapEvent();
            int radius = this.getLocusRadius();
            for (int i = gpsLocal.size() - 1; i >= 0; i--) {
                MapEvent event = gpsLocal.get(i);
                paint.setColor(event.getColor());
                float x_screen = yd_xl + (float) ((event.getLongitude() - firstpoint.x) * 1000 * scale);
                float y_screen = (float) height - (yd_yl + (float) ((event.getLatitude() - firstpoint.y) * 1000 * scale));
                x_screen += MifParser.getXvalue();
                y_screen += MifParser.getYvalue();
                event.setxMIF(x_screen);
                event.setyMIF(y_screen);
                this.drawMIFPoint(canvas, event, parameterms, radius);
                // 记录最后一个点
                lastEvent.setxMIF(x_screen);
                lastEvent.setyMIF(y_screen);

                lastEvent.setLongitude(event.getLongitude());
                lastEvent.setLatitude(event.getLatitude());

            }
            // LogUtil.w(tag,
            // "---lastE.x_mif00000="+lastE.x_mif+" lastE.y_mif000000="+lastE.y_mif);
            Path p = new Path();
            p.moveTo(lastEvent.getxMIF() - 15, lastEvent.getyMIF() - 15);
            p.lineTo(lastEvent.getxMIF() + 15, lastEvent.getyMIF() + 15);
            p.moveTo(lastEvent.getxMIF() + 15, lastEvent.getyMIF() - 15);
            p.lineTo(lastEvent.getxMIF() - 15, lastEvent.getyMIF() + 15);
            canvas.drawPath(p, this.pointPaint);

        } else if (MapFactory.isLoadTAB()) {
            // LogUtil.w(tag,
            // "----xvalue draw="+MifParser.getXvalue()+" yvalue
            // draw="+MifParser.getYvalue());
            // LogUtil.w(tag, "---yd_xl:"+yd_xl+" yd_yl:"+yd_yl);
            lastEvent = new MapEvent();
            for (int i = gpsLocal.size() - 1; i >= 0; i--) {
                MapEvent event = gpsLocal.get(i);
                paint.setColor(event.getColor());
                // 3.25 ibwave经纬度转换为屏幕坐标换算方法
                // CoordData coorData =
                // parserTab.getCoordDataoftheMapbyGPSdata(new
                // GPSData(event.longitude, event.latitude));
                float x_screen;
                float y_screen;
                if (DatasetManager.isPlayback) {
                    if (event.getX() == 0 && event.getY() == 0) {
                        CoordData coorData = getCoordDataoftheMapbyGPSdata(new GPSData(event.getLongitude(), event.getLatitude()));
                        event.setX((float) coorData.x);
                        event.setY((float) coorData.y);
                    }
                }
                x_screen = event.getX() * scale + mapData.getMapShowX();
                y_screen = event.getY() * scale + mapData.getMapShowY();

                LogUtil.w(TAG, "--x_screen:" + x_screen + "--y_screen:" + y_screen + "--x:" + mapData.getMapShowX() + "--y:"
                        + mapData.getMapShowY() + "--lon:" + event.getLongitude() + "--lat:" + event.getLatitude());

                canvas.drawPoint(x_screen, y_screen, paint);

                if (DatasetManager.isPlayback) {
                    // 设置采样点标识
                    if (mDatasetManager.currentIndex >= event.getBeginPointIndex()
                            && mDatasetManager.currentIndex <= event.getEndPointIndex()) {
                        marker.setBounds((int) (x_screen - marker.getIntrinsicWidth() / 2),
                                (int) (y_screen - marker.getIntrinsicHeight()), (int) (x_screen + marker.getIntrinsicWidth() / 2),
                                (int) y_screen);
                        marker.draw(canvas);
                    }
                }
                lastEvent.setLongitude(event.getLongitude());
                lastEvent.setLatitude(event.getLatitude());
            }
            // LogUtil.w(tag,
            // "---lastE.x_mif00000="+lastE.x_mif+" lastE.y_mif000000="+lastE.y_mif);
            Path p = new Path();
            p.moveTo(lastEvent.getxMIF() - 15, lastEvent.getyMIF() - 15);
            p.lineTo(lastEvent.getxMIF() + 15, lastEvent.getyMIF() + 15);
            p.moveTo(lastEvent.getxMIF() + 15, lastEvent.getyMIF() - 15);
            p.lineTo(lastEvent.getxMIF() - 15, lastEvent.getyMIF() + 15);
            canvas.drawPath(p, this.pointPaint);
        } else {
            if (!MapFactory.isLoadBase()) {
                // 如果没有加载MIF地图，以一定规则转换后画在屏幕上
                lastEvent = new MapEvent();
                LogUtil.w(TAG, "---drop Mif GPS Local---");
                for (int i = gpsLocal.size() - 1; i >= 0; i--) {
                    MapEvent e = gpsLocal.get(i);
                    mPaint.setColor(e.getColor());
                    mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    mPaint.setStrokeWidth(dropEventPaintWidth * dropPaintMultiple);
                    canvas.drawPoint(e.getX() * scale + mapData.getMapShowX(), e.getY() * scale + mapData.getMapShowY(), mPaint);
                    // LogUtil.w(tag,
                    // "----select this point"+"event.eventType2="+e.eventType);
                    // TODO 暂时去掉绘制告警图标,需更改实现方式
					/*
					 * Bitmap bm = getCallbm(e); if (bm != null) { canvas.drawBitmap(bm,
					 * e.x * scale + yd_x + x, e.y scale + yd_y + y, pt); }
					 */
                    if (e.getSelectType() == 1) {
                        canvas.drawCircle(e.getX() * scale + mapData.getMapShowX(), e.getY() * scale + mapData.getMapShowY(), 5,
                                this.pointPaint);
                    }
                    lastEvent = e;
                }
                // 在队列的末尾添加X标表示当前位置
                Path p = new Path();
                float x = mapData.getMapShowX();
                float y = mapData.getMapShowY();
                p.moveTo((lastEvent.getX() * scale - 15) + x, (lastEvent.getY() * scale - 15) + y);
                p.lineTo((lastEvent.getX() * scale + 15) + x, (lastEvent.getY() * scale + 15) + y);
                p.moveTo((lastEvent.getX() * scale + 15) + x, (lastEvent.getY() * scale - 15) + y);
                p.lineTo((lastEvent.getX() * scale - 15) + x, (lastEvent.getY() * scale + 15) + y);
                canvas.drawPath(p, this.pointPaint);
            }
        }
    }

    /**
     * 绘制MIF地图轨迹点
     *
     * @param canvas      画图
     * @param mapEvent    轨迹点
     * @param parameterms 参数
     * @param radius      半径
     */
    private void drawMIFPoint(Canvas canvas, MapEvent mapEvent, List<Parameter> parameterms, int radius) {
        Point point = new Point((int) mapEvent.getxMIF(), (int) mapEvent.getyMIF());
        if (point.x <= 0 || point.y <= 0)
            return;
        if (parameterms == null || parameterms.isEmpty()) {
            mPaint.setColor(this.mParameterSet.getGpsColor());
            if (mParameterSet.getLocusShape() == 0) {
                canvas.drawCircle(point.x, point.y, radius, mPaint);
            } else {
                canvas.drawRect(point.x - radius, point.y - radius, point.x + radius, point.y + radius, mPaint);
            }
        } else {
            for (int k = 0; k < parameterms.size(); k++) {
                int color = mapEvent.getParamInfoMap().get(parameterms.get(k).getShowName()) == null ? Color.GRAY
                        : mapEvent.getParamInfoMap().get(parameterms.get(k).getShowName()).color;
                mPaint.setColor(color);
                if (mParameterSet.getLocusShape() == 0) {
                    canvas.drawCircle(point.x + k * radius * 3, point.y, radius, mPaint);
                } else {
                    canvas.drawRect(point.x - radius + k * radius * 3, point.y - radius, point.x + radius + k * radius * 3,
                            point.y + radius, mPaint);
                }
            }
        }
    }

    /**
     * 绘制
     *
     * @param canvas 画板
     */
    private void drawMIFBase(Canvas canvas) {
        double baseHeight = BaseDataParser.getHeight();
        Vector<BaseStationDetail> data_vector = BaseDataParser.getDataDisplay();
        // LogUtil.w(tag, "----data_vector.size="+data_vector.size());
        double yd_xl_base = parserBase.getYdX();
        double yd_yl_base = parserBase.getYdY();
        BaseStationDetail firstbd = BaseDataParser.getFirstDetail();
        float scale = MapFactory.getMapData().getScale();
        Path path = new Path();
        // path.moveTo(50, 50);
        // path.lineTo(200, 200);
        // canvas.drawPath(path, mPaint);
        // 如果已经加载MIF地图，那么将基站地图参照MIF地图画上去
        if (MapFactory.isLoadMIF()) {
            LogUtil.w(TAG, "---is mif");
            double height = parserMif.getHeight();
            MifCenter firstpoint = MifParser.getFirstpoint();
            float yd_xl = parserMif.getYd_x();
            float yd_yl = parserMif.getYd_y();
            // LogUtil.w(tag, "---yd_xl:"+yd_xl+" yd_yl:"+yd_yl);
            mPaint.setColor(Color.BLUE);
            mPaint.setStrokeWidth(1);
            if (data_vector != null && !data_vector.isEmpty()) {
                // LogUtil.w(tag,
                // "----data_vector.size="+data_vector.size());
				int[] mColorSets = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN};
				List<String> main = new ArrayList<>();
                for (BaseStationDetail bd : data_vector) {
                    // LogUtil.w(tag,
                    // "----bd.lot="+bd.lot+" bd.lat="+bd.lat);
					if (!main.contains(bd.main.name)) {
						float xScreen = yd_xl +MifParser.getXvalue()+ (float) ((bd.main.longitude - firstpoint.x) * 1000 * scale);
						float yScreen = MifParser.getYvalue()+ (float) height - (yd_yl + (float) ((bd.main.latitude - firstpoint.y) * 1000 * scale));
						bd.xScreen = xScreen;
						bd.yScreen = yScreen;
						float radius = 13 * scale;
						RectF r = new RectF(bd.xScreen, bd.yScreen, bd.xScreen+radius * 2 , bd.yScreen+radius * 2);
						List<Integer> angles = new ArrayList<>();
						for (int i = 0; i < bd.main.getBearings().length; i++) {
							if (!angles.contains(bd.main.getBearings()[i])) {
								float startAngle = bd.main.getBearings()[i] - 90 - 30;
								mPaint.setColor(mColorSets[i % mColorSets.length]);
								canvas.drawArc(r, startAngle, 60, true, mPaint);
								angles.add(bd.main.getBearings()[i]);
							}
						}
						main.add(bd.main.name);
					}
                    // float size = mPaint.measureText(bd.siteName);
                    // float textX = x_screen - size / 2;
                    // float textY = y_screen - 10;
                    // mPaint.setColor(Color.BLUE);
                    // canvas.drawText(bd.siteName, textX, textY, mPaint);
                    // mPaint.setColor(Color.RED);
//                    canvas.drawCircle(xScreen, yScreen, 4, mPaint);
//                    double endx1 = xScreen + Math.sin(deg2rad(bd.bearing)) * 15;
//                    double endy1 = yScreen - Math.cos(deg2rad(bd.bearing)) * 15;
//                    canvas.drawLine(xScreen, yScreen, (float) endx1, (float) endy1, mPaint);
//                    float offsety = 10;
//                    if (Math.cos(deg2rad(bd.bearing)) > 0) {
//                        offsety = -10;
//                    }
//                    double length = bd.cellName.length() * 8;
//                    double textx = endx1 - length;
//                    double texty = endy1 + offsety;
//                    canvas.drawText(bd.cellName, (float) textx, (float) texty, mPaint);

                    // 记录最后一个点
                }
            }
        }
        if (gpsLocal!=null && !gpsLocal.isEmpty()) {
            lastMapEvent = new MapEvent();
            if (MapFactory.isLoadBase() && !MapFactory.isLoadMIF()) {
                LogUtil.w(TAG, "---drop base mif GPS Local---");
                for (int i = gpsLocal.size() - 1; i >= 0; i--) {
                    MapEvent e = gpsLocal.get(i);
                    mPaint.setColor(e.getColor());
                    mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    mPaint.setStrokeWidth(dropEventPaintWidth * dropPaintMultiple);

                    float xScreen = (float) (yd_xl_base + ((e.getLongitude() - firstbd.main.longitude) * 1000 * scale));
                    float yScreen = (float) (baseHeight
                            - (yd_yl_base + (e.getLatitude() - firstbd.main.latitude) * 1000 * scale));
                    xScreen += BaseDataParser.getXValue();
                    yScreen += BaseDataParser.getYValue();
                    canvas.drawPoint(xScreen, yScreen, mPaint);
                    e.setxBase(xScreen);
                    e.setyBase(yScreen);
                    lastEvent = e;
                    lastEvent.setxBase(xScreen);
                    lastEvent.setyBase(yScreen);
                    // TODO 暂时去掉绘制告警图标,需更改实现方式
					/*
					 * Bitmap bm = getCallbm(e); if (bm != null) { canvas.drawBitmap(bm,
					 * x_screen, y_screen, pt); }
					 */
                    if (e.getSelectType() == 1) {
                        canvas.drawCircle(xScreen, yScreen, 5, this.pointPaint);
                    }
                }
            }
            Path p = new Path();
            p.moveTo(lastEvent.getxBase() - 15, lastEvent.getyBase() - 15);
            p.lineTo(lastEvent.getxBase() + 15, lastEvent.getyBase() + 15);
            p.moveTo(lastEvent.getxBase() + 15, lastEvent.getyBase() - 15);
            p.lineTo(lastEvent.getxBase() - 15, lastEvent.getyBase() + 15);
            canvas.drawPath(p, this.pointPaint);

            String freq = TraceInfoInterface.getParaValue(UnifyParaID.C_Frequency);
            String pn = TraceInfoInterface.getParaValue(UnifyParaID.C_ReferencePN);
            LogUtil.w(TAG, "----freq=" + freq + " pn=" + pn);
            double distance = -1;
            BaseStationDetail tem = null;
            switch (phoneType) {
                case 2:
                    if (data_vector != null) {
                        for (BaseStationDetail bd : data_vector) {
                            if (pn.equals(bd.pn) && freq.equals(bd.frequency)) {
                                if (distance < 0) {
                                    distance = UtilsMethod.getDistance(lastEvent.getLatitude(), lastEvent.getLongitude(), bd.main.latitude,
                                            bd.main.longitude);
                                    tem = bd;
                                } else {
                                    double ss;
                                    if ((ss = UtilsMethod.getDistance(lastEvent.getLatitude(), lastEvent.getLongitude(), bd.main.latitude,
                                            bd.main.longitude)) < distance) {
                                        distance = ss;
                                        tem = bd;
                                    }
                                }
                            }
                        }
                    }
                    Paint pp = new Paint();
                    pp.setStyle(Paint.Style.FILL_AND_STROKE);
                    pp.setStrokeWidth(1);
                    if (tem != null) {
                        pp.setColor(lastEvent.getColor());
                        path.moveTo(tem.xScreen, tem.yScreen);
                        if (MapFactory.isLoadBase() && MapFactory.isLoadMIF()) {
                            path.lineTo(lastEvent.getxMIF(), lastEvent.getyMIF());
                        }
                        if (MapFactory.isLoadBase() && !MapFactory.isLoadMIF()) {
                            LogUtil.w(TAG, "---lastE.xBase=" + lastEvent.getxBase() + " lastE.yBase=" + lastEvent.getyBase());
                            path.lineTo(lastEvent.getxBase(), lastEvent.getyBase());
                        }
                        LogUtil.w(TAG, "---tem.x_screen=" + tem.xScreen + " tem.y_screen=" + tem.yScreen);
                        // path.lineTo(tem.x_screen, tem.y_screen);
                        canvas.drawPath(path, pp);
                        String text = distance + " km";
                        float dis_screen = Math.abs(tem.yScreen - lastEvent.getyMIF());
                        mPaint.setStyle(Paint.Style.FILL);
                        mPaint.setColor(Color.RED);
                        mPaint.setStrokeWidth(markCirclePaint * dropPaintMultiple);
                        LogUtil.w(TAG, "---drow map text---");
                        mPaint.setTextSize(18);
                        float hoffset = dis_screen / 2 - 20;
                        canvas.drawTextOnPath(text, path, hoffset, -5, mPaint);
                    }
                    break;
            }
        }
    }

    /**
     * 绘制图片地图的基站点
     *
     * @param canvas 画布
     */
    private void drawPicBase(Canvas canvas) {
        List<BaseStation> baseList = MapFactory.getBaseList();
        if (baseList == null || baseList.isEmpty())
            return;
        float scale = MapFactory.getMapData().getScale();
        float pointX;
        float pointY;
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(16);
        SavedMapData mapData = MapFactory.getMapData();
        for (BaseStation base : baseList) {
            pointX = (float) base.latitude * scale + mapData.getMapShowX();
            pointY = (float) base.longitude * scale + mapData.getMapShowY();
            canvas.drawCircle(pointX, pointY, 12, mPaint);
            double endx1 = pointX + Math.sin(deg2rad(30)) * 15;
            double endy1 = pointY - Math.cos(deg2rad(30)) * 15;
            canvas.drawLine(pointX, pointY, (float) endx1, (float) endy1, mPaint);
            float offsety = 10;
            if (Math.cos(deg2rad(30)) > 0) {
                offsety = -10;
            }
            double length = base.name.length() * 8;
            double textx = endx1 - length;
            double texty = endy1 + offsety;
            canvas.drawText(base.name, (float) textx, (float) texty, mPaint);
        }
    }

    /**
     * 绘制基站点
     *
     * @param canvas 画布
     */
    private void drawBase(Canvas canvas) {
        // 如果加载基站地图
        if (MapFactory.isLoadBase()||MapFactory.getBaseList().size()>0)
            this.drawMIFBase(canvas);
        else
			this.drawPicBase(canvas);
		}

    /**
     * 绘制选中的告警事件信息
     *
     * @param canvas 画布
     */
    private void drawSelectAlarm(Canvas canvas) {
        if (selectAlarm == null)
            return;
        float scale = MapFactory.getMapData().getScale();
        ((TextView) popupView.findViewById(R.id.descr)).setText(selectAlarm.getMapPopInfo());
        LogUtil.e(TAG, selectAlarm.getMapPopInfo());
        if (selectAlarm.getAlarm().equals(Alarm.NORMAL)) {
            popupView.findViewById(R.id.title).setVisibility(View.GONE);
        } else {
            popupView.findViewById(R.id.title).setVisibility(View.VISIBLE);
            ((TextView) popupView.findViewById(R.id.title)).setText(selectAlarm.getDescription(mContext));
        }
        popupView.measure(0, 0);
        popupView.layout(0, 0, popupView.getMeasuredWidth(), popupView.getMeasuredHeight());
        float left, top;
        if (MapFactory.isLoadMIF()) {
            left = selectAlarm.getMapEvent().getX() - popupView.getMeasuredWidth() / 2;
            top = selectAlarm.getMapEvent().getY() - popupView.getMeasuredHeight();
        } else {
            float pointX;
            float pointY;
            if (MapFactory.isLoadTAB() && !selectAlarm.getAlarm().equals(Alarm.NORMAL)) {
                CoordData coorData = getCoordDataoftheMapbyGPSdata(
                        new GPSData(selectAlarm.getMapEvent().getX(), selectAlarm.getMapEvent().getY()));
                pointX = (float) coorData.x;
                pointY = (float) coorData.y;
            } else {
                pointX = selectAlarm.getMapEvent().getX();
                pointY = selectAlarm.getMapEvent().getY();
            }
            SavedMapData mapData = MapFactory.getMapData();
            left = pointX * scale + mapData.getMapShowX() - popupView.getMeasuredWidth() / 2;
            top = pointY * scale + mapData.getMapShowY() - popupView.getMeasuredHeight();
        }

        canvas.save();
        canvas.translate(left, top);
        popupView.draw(canvas);
        canvas.restore();
    }

    /**
     * 绘制线
     *
     * @param canvas 画布
     */
    private void drawLinePoint(Canvas canvas) {
        if (linePointStack.empty())
            return;
        float scale = MapFactory.getMapData().getScale();
        Queue<Point> tempQueue = new LinkedBlockingQueue<>();
        tempQueue.addAll(linePointStack);
        Point start;
        Point end = tempQueue.remove();
        LogUtil.w(TAG, "---drop tmp Queue---");
        SavedMapData mapData = MapFactory.getMapData();
        float x = mapData.getMapShowX();
        float y = mapData.getMapShowY();
        while (!tempQueue.isEmpty()) {
            start = end;
            // end = tmp_Stack.pop();
            end = tempQueue.remove();
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(dropEventPaintWidth * dropPaintMultiple);
            canvas.drawLine(start.x * scale + x, start.y * scale + y, end.x * scale + x, end.y * scale + y, mPaint);
        }
        if (isMove) {
            start = end;
            end = currentPoint;
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(dropEventPaintWidth * dropPaintMultiple);
            LogUtil.w(TAG, "---drow flag_move_1---");
            canvas.drawLine(start.x * scale + x, start.y * scale + y, end.x * scale + x, end.y * scale + y, mPaint);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
    	mPerformDraw = true;
        super.onDraw(canvas);
        this.drawBottomMap(canvas);
        this.drawPointLocus(canvas);
        // GPS轨迹图例
        gpsLocal = new ArrayList<>(TraceInfoInterface.traceData.getGpsLocas());
        this.drawGPSLocus(canvas);
        this.drawEvent(canvas);
        this.drawBase(canvas);
        // TODO 测试里程开放
		/*
		 * if(appModel.isTestJobIsRun()&&appModel.isGpsTest()){
		 * txtInfo.setText(context
		 * .getString(R.string.str_distanse)+TraceInfoInterface
		 * .traceData.getTestMileage()+"\n" +context.getString(R.string.str_times
		 * )+TraceInfoInterface.traceData.getTestTimeLength()); }
		 */
        this.drawSelectAlarm(canvas);
        this.drawLinePoint(canvas);
        isCenter = false;
        this.saveLocusImage();
        drawGlonavinDirectionArrow(canvas);
        mPerformDraw = false;
    }

    /**
     * 绘制划出的方向箭头
     * @param canvas
     */
    private void drawGlonavinDirectionArrow(Canvas canvas){
        GlonavinDataManager instance = GlonavinDataManager.getInstance();
        if(instance.isHasPointDrew()){
            //如果已经开始打点，则不绘制方向线
            return;
        }
        //初始化画笔
        if(null == mDirectionArrowPaint){
            mDirectionArrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDirectionArrowPaint.setStyle(Paint.Style.FILL);
            mDirectionArrowPaint.setStrokeWidth(3);
            mDirectionArrowPaint.setColor(Color.YELLOW);
        }
        //绘制起始点及方向
        GlonavinPoint startPoint = instance.getStartPoint();
        GlonavinPoint endPoint = instance.getEndPoint();
        SavedMapData mapData = MapFactory.getMapData();
        float scale = mapData.getScale();
        if (null != startPoint) {
            canvas.drawCircle(startPoint.getX()* scale + mapData.getMapShowX(), startPoint.getY()* scale + mapData.getMapShowY(), 10, mDirectionArrowPaint);
        }
        if (null != startPoint && null != endPoint) {
            GlonavinUtils.drawArrow(canvas, mDirectionArrowPaint, (int) (startPoint.getX()* scale + mapData.getMapShowX()), (int) (startPoint.getY()* scale + mapData.getMapShowY()), (int)( endPoint.getX()* scale + mapData.getMapShowX()), (int) (endPoint.getY()* scale + mapData.getMapShowY()));
        }
    }

    /**
     * 测试结束时保存地图轨迹图片
     */
    private void saveLocusImage() {
        if (!TraceInfoInterface.isSaveFileLocus)
            return;
        if (!StringUtil.isNullOrEmpty(TraceInfoInterface.saveFileLocusPath)) {
            File file = new File(TraceInfoInterface.saveFileLocusPath);
            String picName = file.getName();
            picName = picName.substring(0, picName.lastIndexOf(".") + 1) + "locus";
            LogUtil.d(TAG, "saveFileLocusPath" + file.getParent() + File.separator + picName);
            this.saveViewToBMP(file.getParent(), picName);
            TraceInfoInterface.saveFileLocusPath = null;
        }
    }

    /**
     * 保存视图为文件
     *
     * @param path 存放目录
     */
    private void saveViewToBMP(String path, String picName) {
        this.buildDrawingCache();
        Bitmap bitmap = this.getDrawingCache();
        if (bitmap == null) {
            return;
        }
        ImageUtil.saveBitmapToFile(path, bitmap, picName, ImageUtil.FileType.JPEG);
        Toast.makeText(this.mContext, R.string.map_export_success, Toast.LENGTH_SHORT).show();
        if (TraceInfoInterface.isSaveFileLocus) {
            TraceInfoInterface.isSaveFileLocus = false;
            ActivityManager.removeLast();
        }
    }

    /**
     * 角度转弧度
     *
     * @param degree 角度
     * @return 弧度
     */
    private double deg2rad(double degree) {
        return degree / 180 * Math.PI;
    }

    /**
     * 获取矩形中心点
     */
    private void getRectCenter() {
        float height;
        float width;

        if (MapFactory.isLoadMIF() || MapFactory.isLoadBase()) {
            height = MapFactory.getMapData().getMap().getHeight();
            width = MapFactory.getMapData().getMap().getWidth();
            // LogUtil.w(tag, "wv:"+width+"hv:"+height);
        } else {
            float scale = MapFactory.getMapData().getScale();
            height = MapFactory.getMapData().getMap().getHeight() * scale;
            width = MapFactory.getMapData().getMap().getWidth() * scale;
        }
        rectCenterPoint.x = (int) (this.getWidth() / 2 - width / 2);
        rectCenterPoint.y = (int) (this.getHeight() / 2 - height / 2);
        LogUtil.w(TAG, "---rectCenterPoint.x=" + rectCenterPoint.x + " rectCenterPoint.y=" + rectCenterPoint.y + ",MapView:"
                + this.getWidth() + "," + this.getHeight());
    }

    /**
     * 判断某点是否在矩形内
     *
     * @param inX 输入点x坐标
     * @param inY 输入点y坐标
     * @return true/false
     */
    private boolean judgePointInRect(int inX, int inY) {
        if (MapFactory.getMapData().getMap() == null) {
            return true;
        }
        float height;
        float width;
        if (MapFactory.isLoadMIF() || MapFactory.isLoadBase()) {
//            Height = MapFactory.getMapData().getMap().getHeight();
//            Width = MapFactory.getMapData().getMap().getWidth();
            // LogUtil.w(tag, "----Height="+Height);
            return true;
        }
        float scale = MapFactory.getMapData().getScale();
        height = MapFactory.getMapData().getMap().getHeight() * scale;
        width = MapFactory.getMapData().getMap().getWidth() * scale;
        SavedMapData mapData = MapFactory.getMapData();
        float x = mapData.getMapShowX();
        float y = mapData.getMapShowY();
        return ((inX > x && inX < x + width) && (inY > y && inY < y + height)) ;
    }
    //定位都要通过LocationManager这个类实现
    private LocationManager locationManager;
    private String provider;

    private void initLocation() {
//        mLocClient = new LocationClient(WalktourApplication.getAppContext());
//        mLocClient.registerLocationListener(new BDLocationListener() {
//            @Override
//            public void onReceiveLocation(BDLocation location) {
//                mLocClient.stop();
//                if (location == null) {
////                    ToastUtil.showLong(WalktourApplication.getAppContext(), "定位失败");
//                    return;
//                }
////                double[] baiduLatLng = BaiduMapUtil.baidu2Gaode(new LatLng(location.getLatitude(), location.getLongitude()));
////                mLatitude = baiduLatLng[0];
////                mLongitude = baiduLatLng[1];
//                mLatitude = location.getLatitude();
//                mLongitude = location.getLongitude();
//                LogUtil.e(TAG,"---latitude:" + mLatitude + ",longitude:" + mLongitude + "---------");
//                if (mLatitude == 0
//                        && mLongitude == 0) {
////                    ToastUtil.showLong(WalktourApplication.getAppContext(), "定位失败");
//                    return;
//                }
////                ToastUtil.showLong(WalktourApplication.getAppContext(), "定位成功");
//            }
//        });
//        LocationClientOption locationOption = new LocationClientOption();
//        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
//        locationOption.setCoorType("bd09ll");
//        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
//        locationOption.setScanSpan(1000);
//        //可选，默认false，设置是否开启Gps定位
//        locationOption.setOpenGps(true);
//        mLocClient.setLocOption(locationOption);
//获取定位服务
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        //获取当前可用的位置控制器
        List<String> list = locationManager.getProviders(true);

        if (list.contains(LocationManager.GPS_PROVIDER)) {
            //是否为GPS位置控制器
            provider = LocationManager.GPS_PROVIDER;
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            //是否为网络位置控制器
            provider = LocationManager.NETWORK_PROVIDER;

        } else {
            return;
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            //获取当前位置，这里只用到了经纬度
         mLatitude=location.getLatitude();
         mLongitude=location.getLongitude();
        }
    }
    private LocationClient mLocClient;
    @Override
    protected void onAttachedToWindow() {
        LogUtil.w(TAG, "----mapview is onAttachedToWindow");
        super.onAttachedToWindow();

        SDKInitializer.initialize(WalktourApplication.getAppContext());
        initLocation();
//        mLocClient.start();
//        mLocClient.requestLocation();
        IntentFilter filter = new IntentFilter(); // 注册一个消息过滤器
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_MAP_ZOOM);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_SHOW_LOAD_MAP_MENU);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_DRAW_POINT);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_CANCEL_POINT);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_DRAW_LINE);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_LOAD_SCAN_MAP);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_CLEAR_ALL);
//        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_LOAD_MIF_MAP);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_LOAD_BASE_DATA);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_LOCATION);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_LOAD_PAINT_MAP);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_LOAD_INDOOR_MAP);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_CLEAR_POINT);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_CLEAR_MAP);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_CLEAR_STATION);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_LOAD_BASE_DATA_APPEND);
        filter.addAction(com.walktour.gui.map.MapActivity.MOVE_TO_CENTER);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_LOAD_TAB_MAP);
        filter.addAction(ACTION_BASE_REDRAW);
        filter.addAction(WalkMessage.mapColorChanged);
        filter.addAction(WalkMessage.mapGpsColorChanged);
        filter.addAction(WalkMessage.mapParaChanged);
        filter.addAction(GpsInfo.gpsLocationChanged);
        filter.addAction(ControlPanel.ACTION_CONTROL);
        // filter.addAction(com.walktour.gui.map.SmallmapView.Register_OK);
        getContext().registerReceiver(mIntentReceiver, filter, null, null);
        isRegisterReceiver = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtil.w(TAG, "----mapview is onDetachedFromWindow");
        mDatasetManager.addPointIndexChangeListener(this);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        // 如果队列最后一个点为校准点则移除掉
        if (!MapFactory.getMapData().getPointStatusStack().isEmpty()) {
            if (MapFactory.getMapData().getPointStatusStack().lastElement()
                    .getStatus() == PointStatus.POINT_STATUS_CALIBRATION) {
                MapFactory.getMapData().getPointStatusStack().pop();
            }

        }
        try {
            if (isRegisterReceiver) {
                getContext().unregisterReceiver(mIntentReceiver); // 反注册消息过滤器
                isRegisterReceiver = false;
            }
        } catch (java.lang.IllegalArgumentException e) {
            LogUtil.w("IllegalArgumentException:", e.toString());
        }
    }

    /**
     * 清除地图数据
     */
    private void clearMapData() {
        TraceInfoInterface.traceData.getGpsLocas().clear();
        clearMifMap();
    }

    /**
     * 清除MIF地图
     */
    private void clearMifMap() {
        MapFactory.getMapData().getEventQueue().clear();
        /*如果是室内与GPS转换模式中，不需要清空
        * */
        if (!appModel.isInOutSwitchMode()){
            MapFactory.getMapData().getPointStatusStack().clear();
        }
        MapFactory.getMapData().getQueueStack().clear();
        MapFactory.getMapData().setScale(1);
        MapFactory.getMapData().setSampleSize(1);
        linePointStack.clear();
        SavedMapData mapData = MapFactory.getMapData();
        mapData.setMapShowX(0);
        mapData.setMapShowY(0);
        isCenter = true;
    }

    /**
     * 消息处理
     */
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Map)
                    || TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Default)) {
                String action = intent.getAction();
                if(action == null)
                    return;
                if (intent.hasExtra(MapActivity.Map_File_Path))
                    mFilePath = intent.getExtras().getString(MapActivity.Map_File_Path);
                else
                    mFilePath = "";
                if(!TextUtils.isEmpty(mFilePath)) {
                    //将当前加载的室内图路径保存到SharedPreference
                    SharePreferencesUtil sharePreferencesUtil = SharePreferencesUtil.getInstance(mContext);
                    sharePreferencesUtil.saveString(MapView.SP_INDOOR_MAP_PATH, mFilePath);
                    float plottingScale = sharePreferencesUtil.getFloat(mFilePath, 1);
                    MapFactory.getMapData().setPlottingScale(plottingScale);
                    sharePreferencesUtil.saveBoolean(MapView.SP_IS_LOAD_INDOOR_MAP, true);
                }
                if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_MAP_ZOOM)) { // 地图缩放
                    zoomMap(context, intent.getExtras().getFloat("scale"));
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_SHOW_LOAD_MAP_MENU)) { // 显示加载地图菜单
                    MapFactory.getMapData().setZoomGrade(10);
                    showContextMenu();
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_DRAW_POINT)) { // 打点
                    drawMapPoint();
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_CANCEL_POINT)) { // 取消打点
                    cancelOperate();
                    invalidate();
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_LOAD_SCAN_MAP)) {// 加载扫描地图
                    loadScanMap(context);
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_CLEAR_ALL)) {// 清除全部
                    clearAll();
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_CLEAR_POINT)) {// 清除轨迹
                    clearPoints();
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_CLEAR_STATION)) {// 清除基站
                    clearStationData();
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_CLEAR_MAP)) {// 清除地图
                    clearMapAction();
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_LOAD_MIF_MAP)) {// 加载MIF地图
                    loadMifMap(context);
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_LOAD_BASE_DATA)) {// 加载基站地图
                    loadBaseData(context);
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_LOAD_TAB_MAP)) {// 加载TAB地图
                    loadTabMap(context);
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_LOAD_BASE_DATA_APPEND)) {// 加载基站地图
                    appendLoadBaseData(context);
                } else if (action.equalsIgnoreCase(GpsInfo.gpsLocationChanged)) { // 更新GPS轨迹
                    if (appModel.isTestJobIsRun()) {
                        // 如果是第一次居中，并且已经加载MIF地图，并且最后一个点不为NULL，那么自动居中一次
                        if (isFirstToCenter && MapFactory.isLoadMIF() && lastEvent != null) {
                            isFirstToCenter = false;
                            moveToCenter();
                        }
                        invalidate();
                    }
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_LOAD_PAINT_MAP)) {// 加载手绘地图
                    loadPaintMap(context);
                } else if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_LOAD_INDOOR_MAP)) {// 加载室内地图
                    loadIndoorMap(context);
                } else if (action.equals(WalkMessage.mapColorChanged)) {
                    changeMapGpsLocasEventColor(context);
                } else if (action.endsWith(WalkMessage.mapGpsColorChanged)) {
                    changeMapGPSLocasColor();
                } else if (action.equals(WalkMessage.mapParaChanged)) {
                    changeMapEventColor();
                } else if (action.equals(MapActivity.MOVE_TO_CENTER)) { // 点击居中时当前GPS点回到屏幕中心位置
                    moveToCenter();
                    invalidate();
                } else if (action.equals(ACTION_BASE_REDRAW)) {// 基站地图重新绘图
                    float scale = MapFactory.getMapData().getScale();
                    MapFactory.getMapData().setMap(parserBase.createBitmap(1000 * scale));
                    invalidate();
                } else if (action.equals(ControlPanel.ACTION_CONTROL)) {// 点击校准点确定
                    int control = intent.getIntExtra(ControlPanel.KEY_CONTROL, 0);
                    moveCalibrationPoint(control);
                }
            }
        }

        /**
         * 清除地图轨迹
         */
        private void clearPoints() {
            clearMapData();
            LogUtil.w(TAG, "point****clearpoint---");
            MapFactory.setLoadBase(false);
            if (!MapFactory.isLoadTAB()) {
                isCenter = true;
            }
            invalidate();
        }

        /**
         * 绘制打点
         */
        private void drawMapPoint() {
            // flag_toggle = intent.getExtras().getBoolean("flag");
            LogUtil.w(TAG, "---flag_toggle=" + isSetPoint);
            // 当为打点状态时，隐藏信息显示的textView;
            if (isSetPoint) {
                selectAlarm = null;
            }
            // else textInfo.setVisibility(View.VISIBLE);
            // 当进行打点状态切换时，重置所有事件点和GPS点的选择状态，都置为未选择
            List<AlarmModel> alarmList = new ArrayList<>(MapFactory.getMapData().getEventQueue());
            for (AlarmModel alarmModel : alarmList) {
                alarmModel.getMapEvent().setSelectType(0);
            }
            List<MapEvent> gpsLoca = new ArrayList<>(TraceInfoInterface.traceData.getGpsLocas());
            for (MapEvent event : gpsLoca) {
                event.setSelectType(0);
            }
            invalidate();
        }

        /**
         * 改变地图事件显示颜色
         */
        private void changeMapEventColor() {
            LogUtil.w(TAG, "---receive broad111");
            // 当地图显示参数改变时,将队列中所有的事件改为有历史(status=1)事件,颜色改为轨迹颜色,或只需将改变status=0状态及相关颜色
            LegendColors.getInstance().reSetDynamicLis();
            // Intent it = new Intent();
            // it.setAction(com.walktour.gui.map.Map.MyAction12);
            // sendBroadcast(it);
            List<AlarmModel> alarmList = new ArrayList<>(MapFactory.getMapData().getEventQueue());
            for (AlarmModel alarmModel : alarmList) {
                MapEvent event = alarmModel.getMapEvent();
                if (event.getStatus() == 0) {
                    event.setColor(mParameterSet.getGpsColor());
                    event.setStatus(1);
                }
            }
            TraceInfoInterface.traceData.changeGpsLocasPara();
            invalidate();
        }

        /**
         * 改变地图GPS轨迹点颜色
         */
        private void changeMapGPSLocasColor() {
            LogUtil.w(TAG, "---receive broad000");
            // 当GPS轨迹颜色改变时,将所有历史事件(status=1)值相对应的颜色改为轨迹颜色
            List<AlarmModel> alarmList = new ArrayList<>(MapFactory.getMapData().getEventQueue());
            for (AlarmModel alarmModel : alarmList) {
                MapEvent event = alarmModel.getMapEvent();
                if (event.getStatus() == 1) {
                    event.setColor(mParameterSet.getGpsColor());
                }
            }
            TraceInfoInterface.traceData.changeGpsLocasColor();
            invalidate();
        }

        /**
         * 改变地图轨迹事件显示颜色
         *
         * @param context
         *          上下文
         */
        private void changeMapGpsLocasEventColor(Context context) {
            // 当地图参数颜色改变时,重新计算当前事件(status=0)值相对应的颜色值
            List<AlarmModel> alarmList = new ArrayList<>(MapFactory.getMapData().getEventQueue());
            for (AlarmModel alarmModel : alarmList) {
                MapEvent event = alarmModel.getMapEvent();
                if (event.getStatus() == 0) {
                    event.setColor(mParameterSet.getGpsEventColor(context, event.getValue()));
                }
            }
            TraceInfoInterface.traceData.changeGpsLocasEventColor();
            invalidate();
        }

        /**
         * 加载室内地图
         *
         * @param context
         *          上下文
         */
        private void loadIndoorMap(Context context) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.OtherMap;
            MapFactory.setLoadIndoor(true);
            clearMapData();
            clearMap();
            if (mFilePath.toLowerCase(Locale.getDefault()).endsWith("tab")) {
                new ParserTabThread().start();
            } else {
                MapFactory.getMapData().setMap(createBitmap(mFilePath));
                MapFactory.setLoadTAB(false);
            }
            setMapDefaultScale();
            MapFactory.setLoadMIF(false);
            MapFactory.setLoadBase(false);
            MapFactory
                    .setBaseList(BaseStationDBHelper.getInstance(context).queryBaseStation(-1, 0, BaseStation.MAPTYPE_INDOOR));
            isCenter = true;
            invalidate();
            //fix bug:# 211 qt测试更换室内地图偶尔无效
            SysFloorMap.isFromIndoor = false;
        }

        /**
         * 加载手绘地图
         *
         * @param context
         *          上下文
         */
        private void loadPaintMap(Context context) {
            clearMap();
            clearMapData();
            MapFactory.getMapData().setMap(BitmapFactory.decodeFile(mFilePath));
            setMapDefaultScale();
            MapFactory.setLoadMIF(false);
            MapFactory.setLoadBase(false);
            MapFactory.setLoadTAB(false);
            MapFactory
                    .setBaseList(BaseStationDBHelper.getInstance(context).queryBaseStation(-1, 0, BaseStation.MAPTYPE_INDOOR));
            isCenter = true;
            invalidate();
        }

        /**
         * 追加加载基站地图
         *
         * @param context
         *          上下文
         */
        private void appendLoadBaseData(Context context) {
            showProgressDialog(context.getString(R.string.str_importing), false);
//            if (MapFactory.isLoadMIF()) {
//            } else {
                // clearMap();
                // clearmapdata();
//            }
            // 追加基站
            BaseDataParser.setAppendBase(true);
            new ParserBaseDataThread().start();
        }

        /**
         * 加载TAB地图
         *
         * @param context
         *          上下文
         */
        private void loadTabMap(Context context) {
            TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.OtherMap;
            showProgressDialog(context.getString(R.string.str_importing), false);
            clearMap();
            clearMapData();
            new ParserTabThread().start();
        }

        /**
         * 加载基站数据
         *
         * @param context
         *          上下文
         */
        private void loadBaseData(Context context) {
            showProgressDialog(context.getString(R.string.str_importing), false);
            if (!MapFactory.isLoadMIF()) {
                clearMap();
                clearMapData();
            }
            BaseDataParser.setAppendBase(false);
            LogUtil.w(TAG, "---start load--");
            new ParserBaseDataThread().start();
        }

        /**
         * 加载MIF地图
         *
         * @param context
         *          上下文
         */
        private void loadMifMap(Context context) {
            TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.OtherMap;
            clearMap();
            LogUtil.w(TAG, "----------showmif--");
            // 如果当前正在做GPS测试，那么切换地图只清除地图，不清除原先的GPS点
            if (appModel.isGpsTest()) {
                if (!MapFactory.isLoadBase()) {
                    clearMifMap();
                }
            } else {
                if (!MapFactory.isLoadBase()) {
                    clearMapData();
                }
            }
            File f = new File(mFilePath);
            // 如果导入的地图大于3.5M
            if (f.length() > MIF_MAX_SIZE) {
                Toast.makeText(context, R.string.map_toobig, Toast.LENGTH_SHORT).show();
                return;
            }
            showProgressDialog(context.getString(R.string.str_importing), false);
            new ParserMifDataThread().start();
        }

        /**
         * 清除地图数据操作
         */
        private void clearMapAction() {
            clearMap();
            MapFactory.getMapData().setMap(createBlankBitmap());
            LogUtil.w(TAG, "map****clearmap---");
            // Intent intent1 = new Intent();
            // intent1.setAction(MyAction5);
            // sendBroadcast(intent1);
            MapFactory.setLoadMIF(false);
            MapFactory.setLoadBase(false);
            MapFactory.setLoadTAB(false);
            BaseDataParser.setAppendBase(false);
            isCenter = true;
            MapFactory.setLoadIndoor(true);
            // 如果当前是GPS测试，将缩放比例重置为1，缩放级别重置为10
            if (appModel.isGpsTest()) {
                MapFactory.getMapData().setScale(1);
                MapFactory.getMapData().setSampleSize(1);
                MapFactory.getMapData().setZoomGrade(10);
            }
            invalidate();
        }

        /**
         * 加载扫描地图
         *
         * @param context
         *          上下文
         */
        private void loadScanMap(Context context) {
            TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.OtherMap;
            clearMap();
            clearMapData();
            MapFactory.getMapData().setMap(createBitmap(mFilePath));
            // Intent intent1 = new Intent();
            // intent1.setAction(MyAction4);
            // intent1.putExtra("viewWidth", viewWidth);
            // intent1.putExtra("viewHeight", viewHeight);
            // sendBroadcast(intent1);
            setMapDefaultScale();
            MapFactory.setLoadMIF(false);
            MapFactory.setLoadBase(false);
            MapFactory.setLoadTAB(false);
            MapFactory
                    .setBaseList(BaseStationDBHelper.getInstance(context).queryBaseStation(-1, 0, BaseStation.MAPTYPE_INDOOR));
            isCenter = true;
            invalidate();
        }

        /**
         * 清除所有地图数据
         */
        private void clearAll() {
            clearMap();
            clearMapData();
            clearStationData();
            LogUtil.w(TAG, "all****clearall---");
            MapFactory.getMapData().setMap(createBlankBitmap());
            // Intent intent1 = new Intent();
            // intent1.setAction(MyAction5);
            // sendBroadcast(intent1);
            MapFactory.setLoadMIF(false);
            MapFactory.setLoadBase(false);
            MapFactory.setLoadTAB(false);
            BaseDataParser.setAppendBase(false);
            isCenter = true;
            MapFactory.setLoadIndoor(true);
            GlonavinDataManager.getInstance().reset();
            invalidate();
        }

        /**
         * 缩放当前地图
         *
         * @param context
         *          上下文
         * @param zoomScale
         *          缩放比例
         */
        private void zoomMap(Context context, float zoomScale) {
            int zoomGrade = MapFactory.getMapData().getZoomGrade();
            if (zoomScale > 1)
                zoomGrade++;
            else
                zoomGrade--;
            if (zoomGrade > 40) {
                Toast.makeText(context, R.string.zoom_max, Toast.LENGTH_SHORT).show();
                return;
            } else if (zoomGrade < 0) {
                Toast.makeText(context, R.string.zoom_min, Toast.LENGTH_SHORT).show();
                return;
            }
            MapFactory.getMapData().setZoomGrade(zoomGrade);
            float scale = MapFactory.getMapData().getScale();
            scale *= zoomScale;
            MapFactory.getMapData().setScale(scale);
            if (MapFactory.isLoadMIF()) {
                MapFactory.getMapData().setMap(parserMif.CreateBitmap(1000 * scale));
            } else if (MapFactory.isLoadBase()) {
                // LogUtil.w(TAG, "----base change big");
                // 如果是放大，需要做限制处理
                // if(BaseDataParser.getHeight()>1200&&intent.getExtras().getFloat("scale")>1){
                // scale = scale*0.8f;
                // Toast.makeText(context, R.string.base_to_max,
                // Toast.LENGTH_SHORT).show();
                // LogUtil.w(tag, "---$$$$scale="+scale);
                // return;
                // }
                // LogUtil.w(TAG, "---base create 2");
                MapFactory.getMapData().setMap(parserBase.createBitmap(1000 * scale));
            } else if (MapFactory.isLoadTAB()) {
                 LogUtil.w(TAG, "----zoomMap loadTAB");
                // mDownViewArea2D.setRect(mViewPort.getViewArea());
                // scale = intent.getExtras().getFloat("scale");
                // ViewPort.getInstance().Resize(0, 0,
                // mbmpTest.getWidth()*scale,
                // mbmpTest.getHeight()*scale);

                // Rectangle2D.Double viewArea =
                // LayerManager.getInstance().getDataScope();
                // ViewPort.getInstance().setViewArea(viewArea);
                // ViewPort.getInstance().mDownPoint2D.x =
                // ViewPort.getInstance().getViewArea().x;
                // ViewPort.getInstance().mDownPoint2D.y =
                // ViewPort.getInstance().getViewArea().y;
                // Point pt = new Point();
                // pt.x = (int) Math.floor(x);
                // pt.y = (int) Math.floor(y);
                // ViewPort.getInstance().move(ViewPort.getInstance().mDownPoint2D,
                // pt);

				/*
				 * ViewPort.getInstance().Zoom(mDownViewArea2D,
				 * intent.getExtras().getFloat("scale")); mDownViewArea2D
				 * .setRect(ViewPort.getInstance().getViewArea());
				 */
            } else {
                caculateMapLeftPoint();
            }
            invalidate();
        }

    };

    /**
     * 重新计算当前地图的左上角坐标
     */
    private void caculateMapLeftPoint() {
        SavedMapData mapData = MapFactory.getMapData();
        float changeScale = (mapData.getScale() - mapData.getLastScale()) / 2;
        float x = mapData.getMapShowX();
        float y = mapData.getMapShowY();
        x -= mapData.getMapWidth() * changeScale;
        y -= mapData.getMapHeight() * changeScale;
        mapData.setMapShowX(x);
        mapData.setMapShowY(y);
    }

    /**
     * 设置加载的地图的默认缩放比例
     */
    private void setMapDefaultScale() {
        Bitmap map = MapFactory.getMapData().getMap();
        if (map != null) {
            float width = this.getMeasuredWidth();
            float height = this.getMeasuredHeight();
            if (width > map.getWidth() && height > map.getHeight()) {
                float scale;
                if ((width / map.getWidth()) < (height / map.getHeight())) {
                    scale = width / map.getWidth();
                } else {
                    scale = height / map.getHeight();
                }
                MapFactory.getMapData().setScale(scale);
            }
        }
    }

    private ProgressDialog progressDialog;

    /**
     * 显示进度条
     *
     * @param message    显示消息
     * @param cancleable 是否可取消
     */
    private void showProgressDialog(String message, boolean cancleable) {
        progressDialog = new ProgressDialog(((Activity) this.getContext()).getParent());
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(cancleable);
        progressDialog.show();
    }

    /**
     * 清除基站数据
     */
    private void clearStationData() {
        LogUtil.w(TAG, "point****clearstation---");
        BaseStationDBHelper.getInstance(mContext).clearBaseData(BaseStation.MAPTYPE_INDOOR);
        MapFactory.getBaseList().clear();
        invalidate();
    }

    private static final int TAB_JPEGNULL = 1001;

    private MyHandler handler = new MyHandler(this);

    private static class MyHandler extends Handler {

        private WeakReference<MapView> reference;

        public MyHandler(MapView view) {
            this.reference = new WeakReference<>(view);
        }

        public void handleMessage(android.os.Message msg) {
            MapView view = this.reference.get();
            switch (msg.what) {
                case DIS_DIALOG:
                    if (view.progressDialog != null) {
                        view.progressDialog.dismiss();
                    }
                    view.invalidate();
                    break;
                case TAB_JPEGNULL:
                    // 当TAB文件指定的JPEG文件不存在的时候提示
                    Toast.makeText(view.mContext, R.string.map_tabjpeg_isnull, Toast.LENGTH_LONG).show();
				/*
				 * new AlertDialog.Builder(getContext()).setTitle(R.string.str_tip)
				 * .setMessage(R.string.map_tabjpeg_isnull)
				 * .setPositiveButton(R.string.str_ok, new
				 * DialogInterface.OnClickListener() { public void onClick(
				 * DialogInterface dialog, int which) {
				 * 
				 * } }) .show();
				 */
                    break;
            }
        }
    }

    /**
     * 加载MIF地图
     *
     * @author Administrator
     */
    private class ParserMifDataThread extends Thread {
        @Override
        public void run() {
            MifParser.setMaxx(0);
            MifParser.setMinx(0);
            MifParser.setMaxy(0);
            MifParser.setMiny(0);
            parserMif.Parse(mFilePath);
            SavedMapData mapData = MapFactory.getMapData();
            mapData.setMapShowX(0);
            mapData.setMapShowY(0);
            LogUtil.w(TAG, "---path:" + mFilePath);
            MapFactory.getMapData().setScale(0.5f);
            MapFactory.getMapData().setSampleSize(1);
            parserMif.setviewSize(viewWidth, viewHeight);
            parserMif.setCoordinate(0, 0);
            MapFactory.getMapData().setMap(parserMif.CreateBitmap(1000 * MapFactory.getMapData().getScale()));

            MapFactory.setLoadMIF(true);
            isCenter = true;
            isFirstToCenter = true;
            Message msg = handler.obtainMessage(DIS_DIALOG);
            handler.sendMessage(msg);
        }
    }

    /**
     * 加载基站地图
     *
     * @author tangwq
     */
    private class ParserBaseDataThread extends Thread {
        @Override
        public void run() {
            if (!MapFactory.isLoadBase() || !BaseDataParser.isAppendBase()) {
                parserBase.setMaxX(0);
                parserBase.setMinX(0);
                parserBase.setMaxY(0);
                parserBase.setMinY(0);
            }
            parserBase.parse(mFilePath, null);
            MapFactory.getMapData().setScale(1);
            MapFactory.getMapData().setSampleSize(1);
            parserBase.setViewSize(viewWidth, viewHeight);
            parserBase.setCoordinate(0, 0);
            if (!MapFactory.isLoadMIF()) {
                LogUtil.w(TAG, "---base create 3");
                MapFactory.getMapData().setMap(parserBase.createBitmap(1000 * MapFactory.getMapData().getScale()));
            }
            MapFactory.setLoadBase(true);
            isCenter = true;
            Message msg = handler.obtainMessage(DIS_DIALOG);
            handler.sendMessage(msg);
        }
    }

    /**
     * 加载TAB地图线程
     *
     * @author tangwq
     */
    private class ParserTabThread extends Thread {
        public void run() {
            parserTab.Parse(mFilePath);
            MapFactory.setLoadTAB(true);
            MapFactory.setLoadMIF(false);
            MapFactory.setLoadBase(false);
            addTabRasterLayer(mFilePath);
            MapFactory.getMapData().setScale(1);
            MapFactory.getMapData().setSampleSize(1);
            String mapPath = mFilePath.substring(0, mFilePath.lastIndexOf("/") + 1) + parserTab.tab.filename;
            LogUtil.w(TAG, "---start load tab map--" + mapPath);
            if ((new File(mapPath).exists())) {
                // mbmpTest = parserTab.createBitmap(mapPath);
                MapFactory.getMapData().setMap(((TabMapRaster) mMap).getDrawData());
                moveToCenter();
				/*
				 * CoordData coor = parserTab.getCoordDataoftheMapbyGPSdata(new
				 * GPSData(126.11579556, 32.6291117)); LogUtil.w(tag,"---get goon x:"
				 * +coor.x+"--y:"+coor.y); GPSData lat =
				 * parserTab.getGPSDataoftheMapbyCoodData(new CoordData(1000, 1000));
				 * LogUtil.w(tag,"---get lat:"+lat.lat+"--lot:"+lat.lot);
				 */
            } else {
                handler.sendMessage(handler.obtainMessage(TAB_JPEGNULL));
                MapFactory.getMapData().setMap(createBlankBitmap());
                ViewPort.getInstance().resize(0, 0, viewWidth, viewHeight);
            }

            Message msg = handler.obtainMessage(DIS_DIALOG);
            handler.sendMessage(msg);
        }
    }

    /**
     * DoBeforeAddLayer 与DoAfterAddLayer 使用
     */
    private Rectangle2D.Double mRectangle2D;

    private TabMap mMap;

    private Drawer mDrawerTabRaster = new DrawerTabRaster();

    private void doBeforeAddLayer() {
        mRectangle2D = LayerManager.getInstance().getDataScope();
    }

    private void doAfterAddLayer() {
        if (mRectangle2D.width == 0 || mRectangle2D.height == 0) {
            mRectangle2D = LayerManager.getInstance().getDataScope();
            ViewPort.getInstance().setViewArea(mRectangle2D);
        }
    }

    /**
     * 新增tab文件读取
     *
     * @param sFileName 文件路径
     */
    private void addTabRasterLayer(String sFileName) {
        doBeforeAddLayer();

        StringBuffer coordSysBuf = new StringBuffer("");
        mMap = new TabMapRaster(sFileName, coordSysBuf);
        ConvertPrj convertPrj = new ConvertPrj();
        String args[] = new String[]{"+proj=utm", "+zone=0", "+ellps=WGS84", "+datum=WGS84", "+units=m", "+no_defs"};
        String coordSys = convertPrj.ParseProjParas(coordSysBuf.toString(), "");
        if (coordSys != null && !StringUtil.isNullOrEmpty(coordSys)) {
            String coordSysArgs[] = coordSys.split(",")[0].split(" ");
            //fix bug #212 CQT测试导入ibwave地图开始测试后点击到地图界面软件卡死
            if (!TextUtils.isEmpty(coordSysArgs[0])) {
                args[0] = coordSysArgs[0];
            }
            args[2] = coordSysArgs[1];
            if (coordSysArgs.length == 3) {
                args[1] = "+zone="
                        + (int) (Math.floor((Integer.valueOf(coordSysArgs[2].split("=")[1]) + 180.0) / 6) + 1);
            }
            ViewPort.getInstance().setProj(ProjectionFactory.fromPROJ4Specification(args));
            mMap.recalcDataScope(ViewPort.getInstance().getProj());
        } else {
            ViewPort.getInstance().setProj(null);
        }
        mMap.setmDrawer(mDrawerTabRaster);

        LayerManager.getInstance().cleanLayer();
        LayerManager.getInstance().AddLayer(mMap);
        ViewPort.getInstance().resize(0, 0, ((TabMapRaster) mMap).getWidth(), ((TabMapRaster) mMap).getHeight());
        doAfterAddLayer();
    }

    /**
     * Tab地图坐标转换<BR>
     * 通过屏幕打点坐标转换成经纬度
     *
     * @param point 屏幕坐标点
     * @return GPSData GPS封装信息
     */
    private GPSData transformTabGpsData(PointF point) {
        float scale = MapFactory.getMapData().getScale();
        PointF pt = new PointF();
        pt.x = (float) Math.floor(point.x / scale);
        pt.y = (float) Math.floor(point.y / scale);
        Point2D.Double pt2d = new Point2D.Double(0, 0);
        ViewPort.getInstance().VPToDP(pt, pt2d);
        Point2D.Double pos2d = new Point2D.Double(0, 0);
        ViewPort.getInstance().getProj().inverseTransform(pt2d, pos2d);

        return new GPSData(new BigDecimal(pos2d.x).setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue(),
                new BigDecimal(pos2d.y).setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue());

        // String ss =
        // String.format("x %1$f.6, y %2$f.6 d-- x %3$f.6, y %4$f.6", pos2d.x,
        // pos2d.y, pt2d.x, pt2d.y); //"x: " + pos2d.x + " y: " + pos2d.y;
        // Toast.makeText(this.getContext(), ss + "--mx="+ x+ " ---my="+ y,
        // Toast.LENGTH_LONG).show();
    }

    /**
     * 将GPS点转换成屏幕坐标点
     *
     * @param gpsData gps经纬度
     * @return 屏幕坐标点
     */
    private CoordData getCoordDataoftheMapbyGPSdata(GPSData gpsData) {
        Point2D.Double src = new Point2D.Double();
        Point2D.Double dst = new Point2D.Double();
        CoordData coordData = new CoordData(0, 0);
        PointF vp = new PointF();
        src.x = gpsData.lot;
        src.y = gpsData.lat;
        if (ViewPort.getInstance().getProj() != null) {
            ViewPort.getInstance().getProj().transform(src, dst);
        }
        ViewPort.getInstance().DPToVP(dst, vp, true);
        coordData.x = vp.x;
        coordData.y = vp.y;
        return coordData;
    }

    /**
     * 实现居中的方法
     */
    private void moveToCenter() {
        if (MapFactory.getMapData().getMap() == null) {
            return;
        }
        SavedMapData mapData = MapFactory.getMapData();
        float x = mapData.getMapShowX();
        float y = mapData.getMapShowY();
        if (MapFactory.isLoadMIF()) {
            if (!TraceInfoInterface.traceData.getGpsLocas().isEmpty()) {
                if (lastEvent != null) {
                    x = viewCenterWidth - lastEvent.getxMIF();
                    y = viewCenterHeight - lastEvent.getyMIF() - 100;
                }
            } else {
                x = 0;
                y = 0;
            }
            parserMif.setCoordinate(x, y);
            float scale = MapFactory.getMapData().getScale();
            MapFactory.getMapData().setMap(parserMif.CreateBitmap(1000 * scale));
            // LogUtil.w(tag, "---parser.x="+parserMif.getXvalue());
            // LogUtil.w(tag, "---parser.y="+parserMif.getYvalue());
        } else {
            if (MapFactory.isLoadBase()) {
                if (lastMapEvent != null) {
                    x = viewCenterWidth - lastMapEvent.getxBase() - rectCenterPoint.x;
                    y = viewCenterHeight - lastMapEvent.getyBase() - 100 - rectCenterPoint.y;
                } else {
                    x = 0;
                    y = 0;
                }
                parserBase.setCoordinate(x, y);
                LogUtil.w(TAG, "---base create 4");
                float scale = MapFactory.getMapData().getScale();
                MapFactory.getMapData().setMap(parserBase.createBitmap(1000 * scale));
            } else {
                float scale = MapFactory.getMapData().getScale();
                if (appModel.isGpsTest()) {
                    if (lastEvent != null) {
                        x = viewCenterWidth - (lastEvent.getX() * scale);
                        y = viewCenterHeight - (lastEvent.getY() * scale) - 100;
                    }
                } else {
                    x = viewCenterWidth - (MapFactory.getMapData().getMap().getWidth() * scale / 2);
                    y = (viewCenterHeight - 100) - (MapFactory.getMapData().getMap().getHeight() * scale / 2);
                    LogUtil.w(TAG, "----map to center x=" + x + " y=" + y);
                }
                // if (MapFactory.isLoadTAB()) {
                // // mViewPort.Resize(0, 0, mbmpTest.getWidth(),
                // // mbmpTest.getHeight());
                // Rectangle2D.Double viewArea =
                // LayerManager.getInstance().getDataScope();
                // ViewPort.getInstance().setViewArea(viewArea);
                // ViewPort.getInstance().mDownPoint2D.x =
                // ViewPort.getInstance().getViewArea().x;
                // ViewPort.getInstance().mDownPoint2D.y =
                // ViewPort.getInstance().getViewArea().y;
                // Point pt = new Point();
                // pt.x = (int) Math.floor(x);
                // pt.y = (int) Math.floor(y);
                // ViewPort.getInstance().move(ViewPort.getInstance().mDownPoint2D,
                // pt);
                // }
            }

        }
        mapData.setMapShowX(x);
        mapData.setMapShowY(y);
    }

    /**
     * 取消打点操作<BR>
     * 业务流程 1、取出最后一个是实打点,进行移除,并写入RCU文件 2、若是最后一个点状态为PREVIOUS_EFFECTIVE_POINT
     * 则只更改状态为PREVIOUS_POINT，不做移除操作 3、预打点也就是状态为PREVIOUS_POINT在测试过程中，不可移除
     */
    private void cancelOperate() {
        if (!isDrawline) {
            if (!MapFactory.getMapData().getPointStatusStack().empty()) {
                LogUtil.w(TAG, "point_Stack size:" + MapFactory.getMapData().getPointStatusStack().size());
                PointStatus point = MapFactory.getMapData().getPointStatusStack().peek();
                if (appModel.isTestJobIsRun()) {
                    for (int i = MapFactory.getMapData().getPointStatusStack().size() - 1; i >= 0; i--) {
                        PointStatus pointStatus = MapFactory.getMapData().getPointStatusStack().get(i);
                        if (pointStatus.getStatus() == PointStatus.POINT_STATUS_EFFECTIVE
                                || pointStatus.getStatus() == PointStatus.POINT_STATUS_PREVIOUS_EFFECTIVE) {
                            point = pointStatus;
                            break;
                        }
                    }
                }

                if (appModel.isTestJobIsRun() && point.getStatus() == PointStatus.POINT_STATUS_PREVIOUS
                        || !appModel.isTestJobIsRun() && point.getStatus() == PointStatus.POINT_STATUS_EFFECTIVE
                        || !appModel.isTestJobIsRun() && point.getStatus() == PointStatus.POINT_STATUS_PREVIOUS_EFFECTIVE) {
                    return;
                }

                // 如果是预打点或校准点，直接去掉打点队列的最后点
                if (point.getStatus() == PointStatus.POINT_STATUS_PREVIOUS
                        || point.getStatus() == PointStatus.POINT_STATUS_CALIBRATION) {
                    MapFactory.getMapData().getPointStatusStack().pop();
                } else {
                    // 如果是室内测试把轨迹写入RCU文件
                    // if( appModel.isIndoorTest() ){
                    // 如果当前为tab打点，那么不支持实打点的撤销
                    if (!MapFactory.isLoadTAB()) {
                        // ps = pointStatus_Stack.pop();
                        if (point.getStatus() == PointStatus.POINT_STATUS_PREVIOUS_EFFECTIVE) {
                            point.setStatus(PointStatus.POINT_STATUS_PREVIOUS);
                        } else {
                            MapFactory.getMapData().getPointStatusStack().remove(point);
                        }
                        if (appModel.isTestJobIsRun()) {
                            writeMarkPoint(UtilsMethod.MARKSTATE_DEL, point, false);
                        }

                        Queue<AlarmModel> hisAlarmQueue = null;
                        if (!MapFactory.getMapData().getQueueStack().empty()) {
                            hisAlarmQueue = MapFactory.getMapData().getQueueStack().lastElement();
                            MapFactory.getMapData().getEventQueue().removeAll(MapFactory.getMapData().getQueueStack().pop());
                        }
                        HistoryPoint historyPoint = new HistoryPoint(point, hisAlarmQueue, HistoryPoint.HISTORY_DEL);
                        MapFactory.getMapData().getHistoryList().add(historyPoint);
                    } else {
                        if (point.getStatus() == PointStatus.POINT_STATUS_PREVIOUS_EFFECTIVE) {
                            point.setStatus(PointStatus.POINT_STATUS_PREVIOUS);
                        } else {
                            MapFactory.getMapData().getPointStatusStack().remove(point);
                        }
                        if (ViewPort.getInstance().getProj() != null) {
                            writeGPSMarkPoint(UtilsMethod.MARKSTATE_DEL, point);
                        } else {
                            writeMarkPoint(UtilsMethod.MARKSTATE_DEL, point, false);
                        }
                        if (!MapFactory.getMapData().getQueueStack().empty()) {
                            MapFactory.getMapData().getEventQueue().removeAll(MapFactory.getMapData().getQueueStack().pop());
                        }
                    }
                }
            }
        } else if (!linePointStack.empty()) {
            linePointStack.pop();
            if (linePointStack.size() == 1) {
                linePointStack.pop();
            }
        }
    }

    /**
     * 写入GPSMark点到RCU文件中
     *
     * @param status 为0时表示删除点，1表示添加点
     * @param point  点坐标
     */
    private void writeGPSMarkPoint(int status, PointStatus point) {
        // 如果当前不在文件创建成功状态，不执行加点的动作
        if (!appModel.isRcuFileCreated()) {
            return;
        }
        if (status == UtilsMethod.MARKSTATE_ADD) {
            point.setPointTime(System.currentTimeMillis() * 1000);
        }
        GPSData gps = transformTabGpsData(point.getPoint());
        StringBuffer mark = UtilsMethod.buildMarkStr(status, gps.lot, gps.lat);
        LogUtil.w(TAG, "---write GPS mark status:" + (status == UtilsMethod.MARKSTATE_DEL ? "Del" : "Add") + "---lot:"
                + gps.lot + "---lat:" + gps.lat + "--mark:" + mark);

        mDatasetManager.pushData(WalkCommonPara.MsgDataFlag_D, status == UtilsMethod.MARKSTATE_DEL ? -1 : 0, 0,
                point.getPointTime(), mark.toString().getBytes(), mark.length());
    }

    /**
     * 写入室内Mark点到RCU文件中
     *
     * @param status       为0时表示删除点，1表示添加点
     * @param point        坐标点
     * @param isAddNewMark 是否添加的是新的mark点
     * @author tangwq
     */
    public void writeMarkPoint(int status, PointStatus point, boolean isAddNewMark) {
        // 如果当前不在文件创建成功状态，不执行删点的动作
        if (!appModel.isRcuFileCreated()) {
            return;
        }

        writeGPSBeforeFirstPoint(status, isAddNewMark);

        if (status == UtilsMethod.MARKSTATE_ADD) {
            if (point.getPointTime() == 0)
                point.setPointTime(System.currentTimeMillis() * 1000);
        }
        Bitmap map = MapFactory.getMapData().getMap();
        LogUtil.d(TAG, "---map is null:" + (map == null) + " ,height:" + (map == null ? viewHeight : map.getHeight())
                + "---width:" + (map == null ? viewWidth : map.getWidth()));
        float scale = MapFactory.getMapData().getScale();
        int sampleSize = MapFactory.getMapData().getSampleSize();
        double pointX = (point.getPoint().x) * sampleSize;
        double pointY = (map.getHeight() - point.getPoint().y) * sampleSize;
//        StringBuffer mark = UtilsMethod.buildMarkStr(status,pointX,pointY);

//        mDatasetManager.pushData(WalkCommonPara.MsgDataFlag_D, status == UtilsMethod.MARKSTATE_DEL ? -1 : 0, 0,
//                point.getPointTime(), mark.toString().getBytes(), mark.length(), isAddNewMark);
        final SharePreferencesUtil sharePreferencesUtil = SharePreferencesUtil.getInstance(WalktourApplication.getAppContext());
        final String indoorMapPath = sharePreferencesUtil.getString(MapView.SP_INDOOR_MAP_PATH);
        long time = System.currentTimeMillis();
        int secondTime = (int) (time / 1000);
        int flag = 0x30007;
        float plottingScale = sharePreferencesUtil.getFloat(indoorMapPath,1.0f);//像素/米
        float northShift = (float) (pointY / plottingScale);
        float eastShift = (float) (pointX / plottingScale);

        LogUtil.d(TAG, "---write gps status:" + (status == UtilsMethod.MARKSTATE_DEL ? "Del" : "Add")
                + "---scale:" + scale + "---x:" + point.getPoint().x + "---y:" + point.getPoint().y
                +"---pointY:" + pointY + "---pointX:" + pointX
                +"---northShift:" + northShift + "---eastShift:" + eastShift
                + "---plottingScale:" + plottingScale
        );
        EventBytes.Builder(getContext()).addInteger(status == UtilsMethod.MARKSTATE_DEL ? 3 : 2)//int GPSPointType; 0：陀螺仪；2：手工打点；3：取消手工打点
                .addInteger(secondTime)//unsigned int Second;
                .addInteger((int) (time - secondTime * 1000) * 1000)//unsigned int uSecond;
                .addDouble(mLongitude)//double dLon;  定点经度（建筑物等）
                .addDouble(mLatitude)//double dLat;  定点纬度（建筑物等）
                .addSingle(0)//float Altitude;	定点海拔（建筑物等）
                .addSingle(northShift)//float Altitude;	float NorthShift; 南北偏移距离（单位：米）
                .addSingle(eastShift)//float Altitude;	float EastShift;  东西偏移距离（单位：米）
                .addSingle(0)//float Altitude;  float HeightShift; 上下偏移距离（单位：米）
                .addSingle(0)//float Altitude;	float Angle;  角度
                .writeToRcu(flag);
//        point.getPoint().set((int) (point.getPoint().x / scale), (int) (point.getPoint().y / scale));
    }

    /**
     * 新结构的插点第一个点必须插入一个X,Y,Z坐标都为0的点
     * @param status
     * @param isAddNewMark
     */
    private void writeGPSBeforeFirstPoint(int status, boolean isAddNewMark) {
        if(status == UtilsMethod.MARKSTATE_ADD && isAddNewMark && MapFactory.getMapData().isIndoorTestFirstPoint()){
            //新结构的插电第一个点必须插入一个X,Y,Z坐标都为0的点
            long time = System.currentTimeMillis();
            int secondTime = (int) (time / 1000);
            int flag = 0x30007;
            LogUtil.e(TAG,"location:" + mLongitude + "," + mLatitude);
            EventBytes.Builder(getContext()).addInteger(2)//int GPSPointType; 0：陀螺仪；2：手工打点；3：取消手工打点
                    .addInteger(secondTime)//unsigned int Second;
                    .addInteger((int) (time - secondTime * 1000) * 1000)//unsigned int uSecond;
                    .addDouble(mLongitude)//double dLon;  定点经度（建筑物等）
                    .addDouble(mLatitude)//double dLat;  定点纬度（建筑物等）
                    .addSingle(0)//float Altitude;	定点海拔（建筑物等）
                    .addSingle(0)//float NorthShift; 南北偏移距离（单位：米）
                    .addSingle(0)//float EastShift;  东西偏移距离（单位：米）
                    .addSingle(0)//float HeightShift; 上下偏移距离（单位：米）
                    .addSingle(0)//float Angle;  角度
                    .writeToRcu(flag);
            MapFactory.getMapData().setIndoorTestFirstPoint(false);
        }
    }

    /**
     * 获取轨迹的半径大小
     * [功能详细描述]
     */
    private int getLocusRadius() {
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
        return (int) radius;
    }

    /**
     * 按键处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        // mbmpTest = null;
        // mbmpTest = CreateBlankBitmap();
    }

    @Override
    public void onPointIndexChange(int pointIndex, boolean isProgressChange) {
		notifyValidate();
	}

	private int mNCount;
	private synchronized void notifyValidate() {
		if (MapFactory.isLoadMIF()) {
			// czc : Mif地图 减慢刷新时间
			if (!mPerformTouchEvent && !mPerformDraw) {
				mNCount++;
				if (mNCount==60) {
					this.postInvalidate();
				}
				if (mNCount>=60){
					mNCount = 0;
				}
			}
		}else {
			this.postInvalidate();
		}
	}

}