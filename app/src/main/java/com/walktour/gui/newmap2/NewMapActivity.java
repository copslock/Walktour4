package com.walktour.gui.newmap2;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.help.Tip;
import com.dinglicom.dataset.EventManager;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.ImageUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.ScreenUtils;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ParameterSetting;
import com.walktour.control.instance.AlertManager;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.ui.ActivityManager;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.OnTabActivityResultListener;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.applet.ExplorerDirectory;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.applet.ImageExplorer;
import com.walktour.gui.applet.LicenseExplorer;
import com.walktour.gui.gps.Gps;
import com.walktour.gui.highspeedrail.HighSpeedGpsService;
import com.walktour.gui.map.MapView;
import com.walktour.gui.map.ThresholdView;
import com.walktour.gui.map.googlemap.utils.Ut;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.basestation.BaseStationSearchPopWindow;
import com.walktour.gui.newmap.basestation.util.BaseStationExportFactory;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.util.Util;
import com.walktour.gui.newmap2.bean.RounteBean;
import com.walktour.gui.newmap2.bean.StationEvent;
import com.walktour.gui.newmap2.filter.ParamFilterDialog;
import com.walktour.gui.newmap2.manager.MapManager;
import com.walktour.gui.newmap2.overlay.MapCache;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.sdk.GaoDeNaviMapSdk;
import com.walktour.gui.newmap2.sdk.IMapSdk;
import com.walktour.gui.newmap2.ui.MapFrameView;
import com.walktour.gui.setting.SysIndoor;
import com.walktour.gui.setting.SysMap;
import com.walktour.model.Parameter;
import com.walktour.service.metro.MetroTestService;

import org.andnav.osm.util.GeoPoint;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 重构新地图代码
 *
 * @author zhicheng.chen
 * @date 2018/6/6
 */

public class NewMapActivity extends FragmentActivity
        implements View.OnClickListener,
        IMapSdk.MapCallBack,
        OnTabActivityResultListener {

    public static final String EXTRA_FROM = "extra_from";
    public static final String FROM_GIS_ANALYSIS = "from_gis_analysis";

    private static final String TAG = "NewMapActivity";
    private int mMapType;
    private IMapSdk mMapSdk;
    protected NewMapFactory factory;
    protected Handler mHandler = new MyHandler();

    private boolean isZH;
    protected ApplicationModel appModel;
    private PopupWindow morePopupWindow;
    private PopupWindow stationImportPop;
    private BaseStationSearchPopWindow baseStationPop;
    private ProgressDialog progress;

    /**
     * GPS状态名称
     */
    protected String mGpsStatusName = "";
    /**
     * 加载地图菜单数组
     */
    private String[] menuStr = null;
    /**
     * 加载MIF地图
     */
    public static final String ACTION_LOAD_MIF_MAP = "LoadMifMap_Action";
    /**
     * 加载基站地图
     */
    public static final String ACTION_LOAD_BASE_DATA = "LoadBaseData_Action";
    /**
     * 加载扫描地图
     */
    public static final String ACTION_LOAD_SCAN_MAP = "LoadScanMap_Action";
    /**
     * 地图文件路径
     */
    public static final String Map_File_Path = "Map_File_Path";
    /**
     * 加载TAB地图
     */
    public static final String ACTION_LOAD_TAB_MAP = "LoadTabMap_Action";

    private ImageUtil.FileType picFileType;
    private BaseStationExportFactory.FileType fileType;

    protected SharedPreferences mSharedPreferences;
    public final static int LOADING_MAP_DIALOG = 1000;
    private static final int TIMER_TASK = 1200;
    private static final int EXPORT_BASE_END = 12;
    private static final int EXPORT_IMG_SUCCESS = 101;
    private final static String EXTRA_DIR = "dir";
    public static final String AUTO_FOLLOW_MODE = "auto_follow_mode";
    public static final String ACTION_MAP_COLOR_CHANGE = "MapColor_Action";
    private final static String EXPORT_MAP_ACTION_DIR = "com.walktour.BaseMapActivity.exportMap";
    private final static String EXPORT_BASE_ACTION_DIR = "com.walktour.BaseMapActivity.exportBase";

    @BindView(R.id.test_time)
    TextView testTime;
    @BindView(R.id.test_distance)
    TextView testDistance;
    @BindView(R.id.test_speed)
    TextView testSpeed;
    @BindView(R.id.latlng)
    TextView latlngText;
    @BindView(R.id.gps_text)
    TextView gpsText;
    @BindView(R.id.location_text)
    TextView locationText;
    @BindView(R.id.left_text)
    TextView titleText;
    @BindView(R.id.right_text)
    TextView zoomText;
    @BindView(R.id.auto_follow)
    ImageButton autoFollowBtn;
    @BindView(R.id.more_btn)
    Button moreBtn;
    @BindView(R.id.map_type_change)
    ImageButton mMapTypeChangeBtn;
    @BindView(R.id.btn_frame)
    ImageButton frameBtn;
    @BindView(R.id.btn_heat)
    ImageButton heatMapBtn;
    @BindView(R.id.btn_measure)
    ImageButton measureBtn;
    @BindView(R.id.import_basedata)
    Button importBtn;
    @BindView(R.id.drag_scale_view)
    MapFrameView mapFrameView;

    private MapManager mMapManager;//地图图层管理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        String[] dtmap = getResources().getStringArray(R.array.sys_dtmap_default);
        int mapType = MapManager.BAIDU;
        if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[3])) {
            mapType = MapManager.GAODE;
        } else if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[2])) {
            mapType = MapManager.BAIDU;
        } else if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[4])) {
            mapType = MapManager.Bing;
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (savedInstanceState == null) {
                savedInstanceState = bundle;
            } else {
                savedInstanceState.putAll(bundle);
            }
        }

        mMapManager = new MapManager();
        appModel = ApplicationModel.getInstance();
        factory = NewMapFactory.getInstance();
        isZH = TextUtils.equals(getLanguage(), "zh");
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        EventBus.getDefault().register(this);
        init(mapType);
    }

    private void init() {
        String[] dtmap = getResources().getStringArray(R.array.sys_dtmap_default);
        int mapType = MapManager.BAIDU;
        if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[3])) {
            mapType = MapManager.GAODE;
        } else if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[2])) {
            mapType = MapManager.BAIDU;
        } else if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[4])) {
            mapType = MapManager.Bing;
        }
        if (mMapType != mapType) {
            init(mapType);
        }
    }

    private void init(int type) {
        if (mMapSdk != null) {
            mMapSdk.release();
        }
        mMapManager.initSdk(getApplicationContext(), type);
        mMapSdk = mMapManager.getMapByType(this, type, getIntent().getExtras());
        mMapType = mMapManager.getCurrentMapType();
        //设置地图为导航地图
        setContentView(mMapSdk.getLayoutId());
        ButterKnife.bind(this);
        initView();
        initData();
        initOfflineMap();
        requestLoactionInfo();
    }

    private void initOfflineMap() {
        mMapSdk.loadOfflineMap();
    }

    private void initView() {
        initMapView();
        isNeedFullScreen();
        initTitleView();
        initAutoFollowMode();
        initMorePopWindow();
        initStationImportPopWindow();
    }


    private void initData() {
        regReceiver();
        startTimer();
        isShowErrorDialog();
        setMapTitle();
    }

    @SuppressWarnings("deprecation")
    public void isShowErrorDialog() {
        SharedPreferences uiState = getPreferences(Activity.MODE_PRIVATE);
        if (uiState.getString("error", "").length() > 0) {
            //czc:过时方法,需要替换
            showDialog(R.id.error);
        }
    }

    private void regReceiver() {
        IntentFilter intentFilter = new IntentFilter(GpsInfo.gpsLocationChanged);
        intentFilter.addAction(ACTION_MAP_COLOR_CHANGE);
        intentFilter.addAction(WalkMessage.ACTION_MAP_IMPORT_KML);
        intentFilter.addAction(EXPORT_MAP_ACTION_DIR);
        intentFilter.addAction(EXPORT_BASE_ACTION_DIR);
        intentFilter.addAction(WalkMessage.REPLAY_CLEAR_ALL_DATA);
        intentFilter.addAction(MetroTestService.GPS_LOCATION_CHANGED);
        intentFilter.addAction(HighSpeedGpsService.GPS_LOCATION_CHANGED);
        intentFilter.addAction(NewMapActivity.ACTION_LOAD_MIF_MAP);
        intentFilter.addAction(WalkMessage.ACTION_INFO_MAP2EVENT);
        intentFilter.addAction(WalkMessage.CHANCE_DT_MAP_DEFAULT);
        intentFilter.addAction(WalkMessage.ACTION_REPLAY_FINISH);
        // 注册广播接收器
        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.station_filter_setting:
                this.stationImportPop.dismiss();
                showImportFilterDialog();
                break;
            case R.id.station_import:
                MapCache.clearRectLatLng();
                this.stationImportPop.dismiss();
                importBaseData();
                break;
            case R.id.station_export:
                this.stationImportPop.dismiss();
                exportBaseData();
                break;
            case R.id.gps_btn:
                startActivity(new Intent(NewMapActivity.this, Gps.class));
                morePopupWindow.dismiss();
                break;
            case R.id.setting_btn:
                Intent intent = new Intent(NewMapActivity.this, SysMap.class);
                startActivity(intent);
                morePopupWindow.dismiss();
                break;
            case R.id.import_kml:
                intent = new Intent(new Intent(NewMapActivity.this, FileExplorer.class));
                Bundle bundle = new Bundle();
                bundle.putStringArray(FileExplorer.KEY_FILE_FILTER, new String[]{"kml"});
                bundle.putString(FileExplorer.KEY_ACTION, WalkMessage.ACTION_MAP_IMPORT_KML);
                bundle.putString(FileExplorer.KEY_EXTRA, WalkMessage.KEY_MAP_KML_PATH);
                intent.putExtras(bundle);
                startActivity(intent);
                morePopupWindow.dismiss();
                break;
            case R.id.map_export:
                morePopupWindow.dismiss();
                exportMapImage();
                break;
            case R.id.zoom_in:
                mMapSdk.setZoomLevel(mMapSdk.getZoomLevel() + 1);
                break;
            case R.id.zoom_out:
                mMapSdk.setZoomLevel(mMapSdk.getZoomLevel() - 1);
                break;
        }
    }

    @OnClick(R.id.btn_rounte)
    public void clickRounteBtn() {
        startActivity(new Intent(getParent(), RounteActivity.class));
    }

    /**
     * 弹窗设置完路线回调该方法
     *
     * @param rounteList
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setRounte(List<RounteBean<Tip>> rounteList) {
        if (rounteList == null || rounteList.size() < 2) {
            //一条路线必须含有起点和终点两个点
            return;
        }

        //起点
        RounteBean<Tip> startPoint = rounteList.get(0);
        List<MyLatLng> start = new ArrayList<>();
        start.add(new MyLatLng(startPoint.lat, startPoint.lng));

        //终点
        RounteBean<Tip> endPoint = rounteList.get(rounteList.size() - 1);
        List<MyLatLng> end = new ArrayList<>();
        end.add(new MyLatLng(endPoint.lat, endPoint.lng));

        //途经点
        List<RounteBean<Tip>> viaPoint = rounteList.subList(1, rounteList.size() - 1);
        List<MyLatLng> via = new ArrayList<>();
        for (RounteBean<Tip> bean : viaPoint) {
            via.add(new MyLatLng(bean.lat, bean.lng));
        }

        init(MapManager.GdNav);

        //导航地图生成路线
        mMapSdk.calculateDriveRoute(start, end, via, true, false, false, false, false, true);
    }

    @SuppressWarnings("deprecation")
    @OnClick(R.id.map_btn)
    public void clickMapBtn() {
        showDialog(NewMapActivity.LOADING_MAP_DIALOG);
    }

    @OnClick(R.id.import_basedata)
    public void clickImportBaseStation() {
        showStationImportPopView();
    }

    @OnClick(R.id.search_btn)
    public void clickSearchBtn() {
        showBaseStatinPopView();
    }

    @OnClick(R.id.clear_btn)
    public void clickClearBtn() {
        showClearDialog();
    }

    @OnClick(R.id.more_btn)
    public void clickMoreBtn() {
        showMorePopView();
    }

    @OnClick(R.id.auto_follow)
    public void clickAutoFollowBtn() {
        setAutoFollowMode();
    }

    @OnClick(R.id.auto_action_center)
    public void clickSetCenterBtn() {
        setCenter();
    }

    @OnClick(R.id.map_type_change)
    public void clickMapTypeChangeBtn() {
        NewMapFactory.getInstance().changeMapType();
        int mapType = NewMapFactory.getInstance().getMapType();
        mMapSdk.switchMapByType(mapType);
        setMapTypeChangeButtonImage();
    }

    @OnClick(R.id.btn_frame)
    void clickFrame() {
        if (mapFrameView.getVisibility() == View.VISIBLE) {
            mapFrameView.setVisibility(View.GONE);
            frameBtn.setImageResource(R.drawable.ic_frame);
            frameBtn.setBackgroundColor(getResources().getColor(R.color.white));

            int left = mapFrameView.getLeft();
            int top = mapFrameView.getTop();
            int right = mapFrameView.getRight();
            int bottom = mapFrameView.getBottom();

            Point lt = new Point(left, top);
            Point rb = new Point(right, bottom);
            List<BaseStation> stationList = mMapSdk.getStationInBounds(lt, rb);
            //            GisMapDataHolder.get().setData(stationList);
            //            Intent intent = new Intent(this, GisMapListActivity.class);
            //            startActivity(intent);

            StationEvent event = new StationEvent();
            event.points = new Point[]{lt, rb};
            event.type = StationEvent.RECT_FILTER;
            EventBus.getDefault().post(event);

        } else if (mapFrameView.getVisibility() == View.GONE) {
            mapFrameView.setVisibility(View.VISIBLE);
            frameBtn.setBackgroundColor(getResources().getColor(R.color.app_main_color));
            frameBtn.setImageResource(R.drawable.ic_select);
        }
    }

    @OnClick(R.id.btn_measure)
    void clickMeasure() {
        factory.setRanging();
        mMapSdk.measureDistance(factory.isRanging());
        if (factory.isRanging()) {
            measureBtn.setBackgroundColor(getResources().getColor(R.color.app_main_color));
            measureBtn.setImageResource(R.drawable.ic_measure_press);
        } else {
            measureBtn.setBackgroundColor(getResources().getColor(R.color.white));
            measureBtn.setImageResource(R.drawable.ic_measure);
        }
    }

    @OnClick(R.id.btn_heat)
    void clickHeatMap() {
        mMapSdk.showHeatMap(!mMapSdk.getOverlayManager(OverlayType.HeatMap).isEnable());
        if (mMapSdk.getOverlayManager(OverlayType.HeatMap).isEnable()) {
            heatMapBtn.setBackgroundColor(getResources().getColor(R.color.app_main_color));
            heatMapBtn.setImageResource(R.drawable.ic_heat_press);
        } else {
            heatMapBtn.setBackgroundColor(getResources().getColor(R.color.white));
            heatMapBtn.setImageResource(R.drawable.ic_heat);
        }
    }


    private void initMapView() {
        final FrameLayout layout = (FrameLayout) this.findViewById(R.id.main);
        layout.addView(mMapSdk.getMapView());

        layout.post(new Runnable() {
            @Override
            public void run() {
                mapFrameView.setMaxBottom(layout.getHeight());
            }
        });

        //        mapFrameView.setMapFrameListener(new MapFrameView.MapFrameListener() {
        //            @Override
        //            public void doSthMapFrameViewGone(Point lt, Point rb) {
        //                StationEvent event = new StationEvent();
        //                event.points = new Point[]{lt, rb};
        //                event.type = StationEvent.CLEAR;
        //                EventBus.getDefault().post(event);
        //            }
        //
        //            @Override
        //            public void afterMapFrameViewDrag(Point lt, Point rb) {
        //                StationEvent event = new StationEvent();
        //                event.points = new Point[]{lt, rb};
        //                event.type = StationEvent.SELECT;
        //                EventBus.getDefault().post(event);
        //            }
        //        });

        NewMapFactory.getInstance().setZoomLevelMin(5);
        NewMapFactory.getInstance().setZoomLevelMax(19);
        setMapTypeChangeButtonImage();

        // 框选与热力图按钮，仅在gis分析地图呈现
        if (ApplicationModel.getInstance().getSelectScene() != WalkStruct.SceneType.Perception) {
            frameBtn.setVisibility(View.VISIBLE);
            heatMapBtn.setVisibility(View.GONE);
        } else {
            frameBtn.setVisibility(View.VISIBLE);
            heatMapBtn.setVisibility(View.VISIBLE);
        }
    }


    private void isNeedFullScreen() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean fullScreen = pref.getBoolean("pref_showstatusbar", true);
        if (fullScreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void initMorePopWindow() {
        View morePopView = LayoutInflater.from(this).inflate(R.layout.googlemap_more_pop, null);
        morePopView.findViewById(R.id.map_export).setOnClickListener(this);
        if (this.mMapType == MapManager.BAIDU || this.mMapType == MapManager.GAODE || this.mMapType == MapManager.Bing)
            morePopView.findViewById(R.id.import_kml).setVisibility(View.GONE);
        morePopView.findViewById(R.id.import_kml).setOnClickListener(this);
        morePopView.findViewById(R.id.setting_btn).setOnClickListener(this);
        morePopView.findViewById(R.id.gps_btn).setOnClickListener(this);
        morePopupWindow = new PopupWindow(morePopView, ScreenUtils.getScreenWidth(this) / 5, WindowManager.LayoutParams.WRAP_CONTENT, true);
        morePopupWindow.setFocusable(true);
        morePopupWindow.setTouchable(true);
        morePopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * 初始化基站导入弹出窗口
     */
    private void initStationImportPopWindow() {
        View popView = LayoutInflater.from(this).inflate(R.layout.base_station_import_pop, null);
        popView.findViewById(R.id.station_filter_setting).setOnClickListener(this);
        popView.findViewById(R.id.station_import).setOnClickListener(this);
        popView.findViewById(R.id.station_export).setOnClickListener(this);
        this.stationImportPop = new PopupWindow(popView, ScreenUtils.getScreenWidth(this) / 5, WindowManager.LayoutParams.WRAP_CONTENT, true);
        stationImportPop.setTouchable(true);
        stationImportPop.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * 显示基站搜索窗
     */
    private void showBaseStatinPopView() {
        if (this.baseStationPop == null) {
            View map = this.mMapSdk.getMapView();
            this.baseStationPop = new BaseStationSearchPopWindow(map, this.getParent(), map.getMeasuredWidth(),
                    map.getMeasuredHeight() / 2);
            int[] location = new int[2];
            this.getWindow().getDecorView().getLocationOnScreen(location);
            this.baseStationPop.setLocation(location);
        }
        this.baseStationPop.show();
    }

    /**
     * 显示基站导入弹出可选项
     */
    private void showStationImportPopView() {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(ScreenUtils.getScreenWidth(this) / 5, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        this.stationImportPop.getContentView().measure(widthSpec, heightSpec);

        int popHeight = this.stationImportPop.getContentView().getMeasuredHeight();
        int popWidth = this.stationImportPop.getContentView().getMeasuredWidth();

        int width = this.importBtn.getMeasuredWidth();
        int height = this.importBtn.getMeasuredHeight();
        this.stationImportPop.showAsDropDown(importBtn, Math.abs(width - popWidth) / 2, -(height + popHeight), Gravity.START);
    }

    private void showMorePopView() {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(ScreenUtils.getScreenWidth(this) / 5, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        this.morePopupWindow.getContentView().measure(widthSpec, heightSpec);

        int popHeight = this.morePopupWindow.getContentView().getMeasuredHeight();
        int popWidth = this.morePopupWindow.getContentView().getMeasuredWidth();

        int width = this.moreBtn.getMeasuredWidth();
        int height = this.moreBtn.getMeasuredHeight();
        this.morePopupWindow.showAsDropDown(moreBtn, Math.abs(width - popWidth) / 2, -(height + popHeight), Gravity.START);
    }

    /**
     * 启动计时器
     */
    private void startTimer() {
        if (appModel.isTestJobIsRun()) {
            mHandler.sendEmptyMessageDelayed(TIMER_TASK, 1000);
            findViewById(R.id.test_info).setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示标题视图
     */
    private void initTitleView() {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean showTitle = pref.getBoolean("pref_showtitle", true);
        if (!showTitle) {
            findViewById(R.id.screen).setVisibility(View.GONE);
        }
    }

    protected void setMapTitle() {
        if (titleText != null) {
            titleText.setText(mMapSdk.getMapName() + " OnLine");
        }
        if (gpsText != null) {
            gpsText.setText(mGpsStatusName);
        }
        if (zoomText != null) {
            zoomText.setText("" + (int) NewMapFactory.getInstance().getZoomLevelNow());
        }

    }

    /**
     * 初始化自动跟随模式
     */
    private void initAutoFollowMode() {
        boolean isFollow = mSharedPreferences.getInt(AUTO_FOLLOW_MODE, 0) == 1;
        if (isFollow) {
            autoFollowBtn.setImageResource(R.drawable.main_icon_follow_pressed);
        } else {
            autoFollowBtn.setImageResource(R.drawable.main_icon_follow);
        }
    }

    @Override
    public void result(Object[] objs) {
        if (mMapManager.getCurrentMapType() == MapManager.BAIDU) {
            if (objs[0] != null) {
                zoomText.setText((String) objs[0]);
            }
        } else if (mMapManager.getCurrentMapType() == MapManager.GAODE) {
            if (objs[0] != null) {
                LogUtil.d("result", "result:" + objs[0]);
                zoomText.setText("" + objs[0]);
            }
        } else if (mMapManager.getCurrentMapType() == MapManager.Bing) {
            if (objs[0] != null) {
                zoomText.setText((String) objs[0]);
            }
        }
    }

    @Override
    public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.d(TAG, "----onTabActivityResult----");
        switch (resultCode) {
            case BaseMapActivity.OFFLINE_MAP_RESULT_CODE:
                titleText.setText(mMapSdk.getMapName() + " Offline");
                break;
            case BaseMapActivity.ONLINE_MAP_RESULT_CODE:
                titleText.setText(mMapSdk.getMapName() + " OnLine");
                break;
        }
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(final Message msg) {
            final int what = msg.what;
            switch (what) {
                case TIMER_TASK:
                    testTime.setText(TraceInfoInterface.traceData.getTestTimeHHmmss());
                    testDistance.setText(TraceInfoInterface.traceData.getTestMileageStr());
                    startTimer();
                    break;
                case R.id.user_moved_map:
                    // setAutoFollow(false);
                    break;
                case R.id.add_yandex_bookmark:
                    showDialog(R.id.add_yandex_bookmark);
                    break;
                case Ut.ERROR_MESSAGE:
                    if (msg.obj != null)
                        Toast.makeText(NewMapActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
                case EXPORT_BASE_END:
                    if (progress != null) {
                        progress.dismiss();
                    }
                    break;
                case EXPORT_IMG_SUCCESS:
                    Toast.makeText(getApplicationContext(), R.string.map_export_success, Toast.LENGTH_SHORT).show();
                    if (TraceInfoInterface.isSaveFileLocus) {
                        TraceInfoInterface.isSaveFileLocus = false;
                        ActivityManager.removeLast();
                    }
                    break;
            }
        }
    }

    void tipNaviMapFinish() {
        new BasicDialog.Builder(this.getParent()).setTitle(R.string.str_tip).setMessage(getString(R.string.exit_navi))
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //不作任何处理
            }
        }).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d(TAG, "GaoDeNaviMapSdk.isNavi:" + GaoDeNaviMapSdk.isNavi);
        if (keyCode == KeyEvent.KEYCODE_BACK && GaoDeNaviMapSdk.isNavi) {
            LogUtil.d(TAG, "tipNaviMapFinish");
            tipNaviMapFinish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 接受位置更新广播
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (GpsInfo.gpsLocationChanged.equals(intent.getAction())
                    && appModel.getSelectScene() != WalkStruct.SceneType.HighSpeedRail
                    && appModel.getSelectScene() != WalkStruct.SceneType.Metro) {//高铁和地铁不需要实际定位
                requestLoactionInfo();
                Location locat = GpsInfo.getInstance().getLocation();
                LogUtil.v("MapChange", "---GPS_LOCATION_CHANGED---");
                if (locat != null && testSpeed != null) {
                    testSpeed.setText(UtilsMethod.decFormat.format(locat.getSpeed() * 3.6) + " km/h");
                }
                if (locat != null && latlngText != null) {
                    latlngText.setVisibility(View.VISIBLE);
                    latlngText.setText(Util.formatGeoPoint(new GeoPoint((int) (locat.getLatitude() * 1e6), (int) (locat.getLongitude() * 1e6))));
                } else {
                    latlngText.setText("-E,-N");
                }

                if (locat != null && System.currentTimeMillis() - GpsInfo.getInstance().getGpsLastChangeTime() > 3000) {
                    // Logger.d(TAG, "收到GPS,大于3秒，刷新界面");
                    mMapSdk.setLocation(locat.getLatitude(), locat.getLongitude());
                    MyLatLng myLatLng = new MyLatLng(locat.getLatitude(), locat.getLongitude());
                    mMapSdk.getOverlayManager(OverlayType.LocasPoint).addOverlay(myLatLng);
                    mMapSdk.getOverlayManager(OverlayType.CellLink).addOverlay(myLatLng);
                    mMapSdk.getOverlayManager(OverlayType.Alarm).addOverlay(myLatLng);
                    GpsInfo.getInstance().setGpsLastChangeTime(System.currentTimeMillis());
                }
            } else if (MetroTestService.GPS_LOCATION_CHANGED.equals(intent.getAction())) {
                double latitude = intent.getDoubleExtra("lat", -9999);
                double longitude = intent.getDoubleExtra("lon", -9999);
                if (latitude != -9999 && latlngText != null) {
                    latlngText.setVisibility(View.VISIBLE);
                    latlngText.setText(Util.formatGeoPoint(new GeoPoint((int) (latitude * 1e6), (int) (longitude * 1e6))));
                } else {
                    latlngText.setText("-E,-N");
                }
                //由于快到站了，会一次性给多个点，去掉时间限制
                //                if (System.currentTimeMillis() - GpsInfo.getInstance().getGpsLastChangeTime() > 2000) {
                MyLatLng myLatLng = new MyLatLng(latitude, longitude);
                mMapSdk.setLocation(latitude, longitude);
                mMapSdk.getOverlayManager(OverlayType.LocasPoint).addOverlay(myLatLng);
                mMapSdk.getOverlayManager(OverlayType.MetroRoute).addOverlay(myLatLng);
                LogUtil.d("MapChange", "---地铁GPS_LOCATION_CHANGED---" + myLatLng);
                GpsInfo.getInstance().setGpsLastChangeTime(System.currentTimeMillis());
                //                }
            } else if (HighSpeedGpsService.GPS_LOCATION_CHANGED.equals(intent.getAction())) {
                double latitude = intent.getDoubleExtra("lat", -9999);
                double longitude = intent.getDoubleExtra("lon", -9999);
                if (latitude != -9999 && latlngText != null) {
                    latlngText.setVisibility(View.VISIBLE);
                    latlngText.setText(Util.formatGeoPoint(new GeoPoint((int) (latitude * 1e6), (int) (longitude * 1e6))));
                } else {
                    latlngText.setText("-E,-N");
                }
                //由于快到站了，会一次性给多个点，去掉时间限制
                //                if (System.currentTimeMillis() - GpsInfo.getInstance().getGpsLastChangeTime() > 2000) {
                MyLatLng myLatLng = new MyLatLng(latitude, longitude);
                mMapSdk.setLocation(latitude, longitude);
                mMapSdk.getOverlayManager(OverlayType.LocasPoint).addOverlay(myLatLng);
                mMapSdk.getOverlayManager(OverlayType.HsRoute).addOverlay(myLatLng);
                LogUtil.d("MapChange", "---高铁GPS_LOCATION_CHANGED---" + myLatLng);
                GpsInfo.getInstance().setGpsLastChangeTime(System.currentTimeMillis());
                //                }

            } else if (intent.getAction().equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_MAP_COLOR_CHANGE)) {
                LogUtil.v("MapChange", "---Change---");
                findViewById(R.id.threshold_view).invalidate();
            } else if (intent.getAction().equals(WalkMessage.ACTION_MAP_IMPORT_KML)) {
                String path = intent.getStringExtra(WalkMessage.KEY_MAP_KML_PATH);
                doImportTrack(path);
            } else if (intent.getAction().equals(EXPORT_MAP_ACTION_DIR)) {// 导出地图到指定目录
                String path = intent.getExtras().getString(EXTRA_DIR);
                saveViewToBMP(path, getPicName());
            } else if (intent.getAction().equals(EXPORT_BASE_ACTION_DIR)) {// 导出基站数据到指定目录
                String path = intent.getExtras().getString(EXTRA_DIR);
                saveBaseToFile(path);
            } else if (intent.getAction().equals(WalkMessage.REPLAY_CLEAR_ALL_DATA)) { // 回放时清除数据

            } else if (intent.getAction().equals(NewMapActivity.ACTION_LOAD_MIF_MAP)) {//加载MIF地图广播接收
                mMapSdk.getOverlayManager(OverlayType.MifMap).addOverlay();
            } else if (intent.getAction().equals(WalkMessage.ACTION_REPLAY_FINISH)) {//回放加载完毕
                init();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMapSdk.getOverlayManager(OverlayType.LocasPoint).clearOverlay();
                        mMapSdk.getOverlayManager(OverlayType.MetroRoute).clearOverlay();
                        mMapSdk.getOverlayManager(OverlayType.LocasPoint).addOverlay();
                        mMapSdk.getOverlayManager(OverlayType.MetroRoute).addOverlay();
                    }
                }, 500);
            } else if (intent.getAction().equals(WalkMessage.ACTION_INFO_MAP2EVENT)) {//点击回放开始切换到信息页面的时候
//                init();
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mMapSdk.getOverlayManager(OverlayType.LocasPoint).addOverlay();
//                        mMapSdk.getOverlayManager(OverlayType.MetroRoute).addOverlay();
//                    }
//                }, 500);

            } else if (intent.getAction().equals(WalkMessage.CHANCE_DT_MAP_DEFAULT)) {//用户切换了DT默认地图
                init();
            }
        }
    };

    /**
     * 请求详细地址位置
     */
    private void requestLoactionInfo() {
        if (mMapSdk != null) {
            mMapSdk.requestLocation(new IMapSdk.MapCallBack<String>() {
                @Override
                public void result(String[] t) {
                    if (locationText != null && t != null && t.length > 0) {
                        String location = t[0];
                        locationText.setText(location);
                    }
                }
            });
        }
    }

    protected void doImportTrack(String mFileName) {
        // TODO 百度地图暂时不支持

    }

    public void saveViewToBMP(final String path, final String picName) {
        mMapSdk.getSnapShot(new IMapSdk.MapCallBack() {
            @Override
            public void result(Object... obj) {
                final Bitmap bitmap = (Bitmap) obj[0];
                if (bitmap != null) {
                    LinearLayout lineTool = (LinearLayout) findViewById(R.id.LineraLayoutToolbar);
                    lineTool.setVisibility(View.GONE);
                    View view = NewMapActivity.this.getWindow().getDecorView();
                    view.buildDrawingCache();
                    final Bitmap parentmap = view.getDrawingCache();
                    lineTool.setVisibility(View.VISIBLE);
                    if (parentmap == null) {
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap basemap = Bitmap.createBitmap(parentmap.getWidth(), parentmap.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(basemap);
                            canvas.drawBitmap(parentmap, 0, 0, null);
                            View thresholdView = findViewById(R.id.threshold_view);
                            Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                            float top = thresholdView.getTop() - bitmap.getHeight();
                            RectF dst = new RectF(0, top, basemap.getWidth(), thresholdView.getTop());
                            canvas.drawBitmap(bitmap, src, dst, null);
                            saveBitmapToFile(path, basemap, picName);
                            mHandler.sendEmptyMessage(EXPORT_IMG_SUCCESS);
                        }
                    }).start();
                }
            }
        });
    }

    /**
     * 生成图片名称
     *
     * @return
     */
    protected String getPicName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());
        return format.format(Calendar.getInstance(Locale.getDefault()).getTime());
    }

    protected void saveBitmapToFile(final String path, final Bitmap bitmap, final String fileName) {
        ImageUtil.saveBitmapToFile(path, bitmap, fileName, picFileType);
    }

    /**
     * 保存基站数据到指定文件
     *
     * @param path 文件路径
     */
    private void saveBaseToFile(final String path) {
        progress = ProgressDialog.show(this.getParent(), getString(R.string.map_export), getString(R.string.map_exporting),
                true);
        new Thread() {
            @Override
            public void run() {
                BaseStationExportFactory.getInstance().exportFile(NewMapActivity.this, path, fileType);
                mHandler.sendEmptyMessage(EXPORT_BASE_END);
            }
        }.start();
    }

    /**
     * 显示导入基站筛选设置对话框
     */
    private void showImportFilterDialog() {
        new ParamFilterDialog(NewMapActivity.this.getParent()).show();
    }


    /**
     * 导入基站数据
     */
    protected void importBaseData() {
        new LicenseExplorer(this, new String[]{"txt", "xls", "kml", "mif"}, LicenseExplorer.LOADING_BASE_DATA,
                BaseStation.MAPTYPE_OUTDOOR).start();
    }

    /**
     * 导出基站
     */
    private void exportBaseData() {
        final String[] fileTypes = new String[]{"TXT", "XLS", "KML", "MIF"};
        new BasicDialog.Builder(NewMapActivity.this.getParent()).setTitle(R.string.facebook_filetype)
                .setSingleChoiceItems(fileTypes, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                fileType = BaseStationExportFactory.FileType.TEXT;
                                break;
                            case 1:
                                fileType = BaseStationExportFactory.FileType.XLS;
                                break;
                            case 2:
                                fileType = BaseStationExportFactory.FileType.KML;
                                break;
                            case 3:
                                fileType = BaseStationExportFactory.FileType.MIF;
                                break;
                        }
                        // 启动文件浏览器
                        new ExplorerDirectory(NewMapActivity.this, fileTypes, EXPORT_BASE_ACTION_DIR, EXTRA_DIR).start();
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 导出地图图片
     */
    private void exportMapImage() {
        final String[] fileTypes = new String[]{"JPEG", "BMP"};
        new BasicDialog.Builder(NewMapActivity.this.getParent()).setTitle(R.string.facebook_filetype)
                .setSingleChoiceItems(fileTypes, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                picFileType = ImageUtil.FileType.JPEG;
                                break;
                            case 1:
                                picFileType = ImageUtil.FileType.BMP;
                                break;
                        }
                        // 启动文件浏览器
                        new ExplorerDirectory(NewMapActivity.this, fileTypes, EXPORT_MAP_ACTION_DIR, EXTRA_DIR).start();
                        dialog.dismiss();
                    }
                }).show();
    }

    public void showClearDialog() {
        new BasicDialog.Builder(NewMapActivity.this.getParent()).setIcon(android.R.drawable.ic_menu_edit)
                .setTitle(R.string.facebook_filetype)
                .setItems(
                        new String[]{getResources().getString(R.string.clear_basedata),
                                getResources().getString(R.string.clear_marker), getResources().getString(R.string.clear_all)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:// 清除基站
                                        //                                        clearBaseData();
                                        MapCache.clearRectLatLng();
                                        BaseStationDBHelper.getInstance(getApplicationContext()).clearAllData();
                                        mMapSdk.removeOverlay(OverlayType.BaseStation);
                                        break;
                                    case 1:// 清除打点
                                        //                                        clearMarker();
                                        TraceInfoInterface.traceData.getGpsLocas().clear();
                                        AlertManager.getInstance(getApplicationContext()).clearAlarms(true);
                                        mMapSdk.removeOverlay(OverlayType.LocasPoint);
                                        break;
                                    case 2:// 清除全部
                                        //                                        clearAll();
                                        MapCache.clearRectLatLng();
                                        BaseStationDBHelper.getInstance(getApplicationContext()).clearAllData();
                                        TraceInfoInterface.traceData.getGpsLocas().clear();
                                        mMapSdk.clearOverlay();
                                        break;

                                    default:
                                        break;
                                }

                            }
                        })
                .show();
    }

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        BasicDialog.Builder builder = new BasicDialog.Builder(this.getParent());
        switch (id) {
            case NewMapActivity.LOADING_MAP_DIALOG:
                if ((ApplicationModel.getInstance().isGerenalTest() || ApplicationModel.getInstance().isIndoorTest())
                        && appModel.isTestJobIsRun()) { // 室内地图
                    menuStr = getResources().getStringArray(R.array.array_indoor_map);
                } else if (!ApplicationModel.getInstance().isGerenalTest() && !ApplicationModel.getInstance().isIndoorTest()
                        && appModel.isTestJobIsRun()) { // dt地图
                    if (ParameterSetting.getInstance().getDtDefaultMap().equals(getResources().getStringArray(R.array.sys_dtmap_default)[0])) {
                        menuStr = new String[]{getResources().getString(R.string.map_outdoor)};
                    } else {
                        menuStr = new String[]{getResources().getString(R.string.map_offline), getResources().getString(R.string.map_online),getResources().getString(R.string.map_indoor)};
                    }
                } else {
                    menuStr = getResources().getStringArray(R.array.array_loading_map);
                }

                if (appModel.getSelectScene().equals(WalkStruct.SceneType.Metro)
                        || appModel.getSelectScene().equals(WalkStruct.SceneType.HighSpeedRail)) {
                    menuStr = new String[]{getResources().getString(R.string.map_scan), getResources().getString(R.string.map_outdoor), getResources().getString(R.string.map_offline), getResources().getString(R.string.map_online)};
                }
                builder.setTitle(R.string.map_menu_title).setItems(menuStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String switchStr = menuStr[which]; // 当前选择item的名字
                        if (switchStr.equals(getString(R.string.map_innsmap))
                                || switchStr.equals(getString(R.string.map_outdoor))
                                || switchStr.equals(getString(R.string.map_online))) {
                            LogUtil.d(TAG, "是否加载室内：不是");
                            SharePreferencesUtil.getInstance(WalktourApplication.getAppContext()).saveBoolean(MapView.SP_IS_LOAD_INDOOR_MAP, false);
                        }
                         /*2019/4/1
                    联通招标需要室内与室外的切换
                            * */
                        boolean isTest = (appModel.isTestJobIsRun() || appModel.isTestStoping()) && appModel.isGpsTest();
                        if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[0])) {// 扫描地图
                            if (isTest) {
                                Toast.makeText(NewMapActivity.this, getString(R.string.map_testRunAndGpsOpen_CantLoadScan),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                ((IMapTab) getParent()).swicthMap(WalkStruct.ShowInfoType.Map);
                                ImageExplorer explorer = new ImageExplorer(NewMapActivity.this, NewMapActivity.ACTION_LOAD_SCAN_MAP,
                                        NewMapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_picture));
                                explorer.start();
                            }
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[1])) {// 室内地图
//                            if (appModel.isTestJobIsRun() || appModel.isTestStoping()) {
//                                Toast.makeText(NewMapActivity.this, getString(R.string.map_testRunAndGpsOpen_CantLoadIndoor),
//                                        Toast.LENGTH_LONG).show();
//                            } else {
                                // 打开配置好的楼层地图
                                appModel.setInOutSwitchMode(true);
                                Intent intent = new Intent(NewMapActivity.this, SysIndoor.class);
                                intent.putExtra(SysIndoor.KEY_LOADING, true);
                                getParent().startActivityForResult(intent, 10);
//                            }
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[2])) {// MIF地图
                            ((IMapTab) NewMapActivity.this.getParent()).swicthMap(WalkStruct.ShowInfoType.BaiduMap);
                            TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
                            new ImageExplorer(NewMapActivity.this, NewMapActivity.ACTION_LOAD_MIF_MAP, NewMapActivity.Map_File_Path,
                                    getResources().getStringArray(R.array.maptype_mif)).start();
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[3])) {// iBwave地图
                            ((IMapTab) getParent()).swicthMap(WalkStruct.ShowInfoType.Map);
                            new ImageExplorer(NewMapActivity.this, NewMapActivity.ACTION_LOAD_TAB_MAP,
                                    NewMapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_tab))
                                    .start();
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[4])) {// 离线地图
                            ((IMapTab) getParent()).swicthMap(WalkStruct.ShowInfoType.OfflineMap);
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[5])) {// 在线地图
                            ((IMapTab) getParent()).swicthMap(WalkStruct.ShowInfoType.OnlineMap);
                        }
                    }
                });
                break;

        }
        return builder.create();
    }

    private void setAutoFollowMode() {
        boolean isFollow = mSharedPreferences.getInt(AUTO_FOLLOW_MODE, 0) == 1;
        if (isFollow) {
            autoFollowBtn.setImageResource(R.drawable.main_icon_follow);
            mSharedPreferences.edit().putInt(AUTO_FOLLOW_MODE, 0).commit();
        } else {
            autoFollowBtn.setImageResource(R.drawable.main_icon_follow_pressed);
            mSharedPreferences.edit().putInt(AUTO_FOLLOW_MODE, 1).commit();
        }
    }

    /**
     * 居中
     */
    void setCenter() {
        if (mMapSdk != null) {
            EventManager.getInstance().addEvent(NewMapActivity.this, "" + mMapSdk.getLocationInfo());
            mMapSdk.focuLastLatlng();
        }
    }

    /**
     * 设置地图类型更改按钮
     */
    private void setMapTypeChangeButtonImage() {
        int mapType = NewMapFactory.getInstance().getMapType();
        switch (mapType) {
            case NewMapFactory.MAP_TYPE_SATELLITE:
                String language = getLanguage();
                this.mMapTypeChangeBtn.setImageResource(
                        language.equalsIgnoreCase("zh") ? R.drawable.map_type_satellite : R.drawable.map_type_satellite_en);
                break;
            case NewMapFactory.MAP_TYPE_NORMAL_2D:
                this.mMapTypeChangeBtn.setImageResource(R.drawable.map_type_normal_2d);
                break;
            case NewMapFactory.MAP_TYPE_NORMAL_3D:
                this.mMapTypeChangeBtn.setImageResource(R.drawable.map_type_normal_3d);
                break;
            case NewMapFactory.MAP_TYPE_NONE:
                this.mMapTypeChangeBtn.setImageResource(R.drawable.map_type_none);
                break;
        }
    }

    /**
     * 语言环境
     *
     * @return
     */
    private String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapSdk.onResume();
        initThresholdLegend();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapSdk.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMapManager.destroy();

        EventBus.getDefault().unregister(this);

        if (this.baseStationPop != null)
            this.baseStationPop.close();
        this.unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        if (!mMapSdk.doSthOnBackPressed()) {
            super.onBackPressed();
        }
    }

    /**
     * 显示参数图例
     */
    private void initThresholdLegend() {
        ThresholdView thresholdView = (ThresholdView) findViewById(R.id.threshold_view);
        if (ParameterSetting.getInstance().isDisplayLegen() && thresholdView != null) {
            int net = MyPhoneState.getInstance().getCurrentNetForParam(this);
            List<Parameter> parameterList = ParameterSetting.getInstance().getCheckedParamertersByNet(net);
            Parameter parameter = parameterList.size() > 0 ? parameterList.get(0) : null;
            if (parameter == null) {
                thresholdView.setVisibility(View.GONE);
            } else {
                thresholdView.setVisibility(View.VISIBLE);
                thresholdView.invalidate();
            }
        } else {
            thresholdView.setVisibility(View.GONE);
        }
    }
}
