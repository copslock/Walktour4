package com.walktour.gui.newmap;

import android.annotation.SuppressLint;
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
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.ImageUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ParameterSetting;
import com.walktour.control.instance.AlertManager;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.OnTabActivityResultListener;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.applet.ExplorerDirectory;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.applet.ImageExplorer;
import com.walktour.gui.applet.LicenseExplorer;
import com.walktour.gui.gps.Gps;
import com.walktour.gui.highspeedrail.HighSpeedGpsService;
import com.walktour.gui.map.MapActivity;
import com.walktour.gui.map.MapTabActivity;
import com.walktour.gui.map.MapView;
import com.walktour.gui.map.googlemap.MainPreferences;
import com.walktour.gui.map.googlemap.constants.PrefConstants;
import com.walktour.gui.map.googlemap.kml.PoiListActivity;
import com.walktour.gui.map.googlemap.utils.Ut;
import com.walktour.gui.newmap.basestation.BaseStationSearchPopWindow;
import com.walktour.gui.newmap.basestation.util.BaseStationExportFactory;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.layer.baidu.BaiduMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay;
import com.walktour.gui.newmap.util.Util;
import com.walktour.gui.setting.SysIndoor;
import com.walktour.gui.setting.SysMap;
import com.walktour.service.metro.MetroTestService;

import org.andnav.osm.util.GeoPoint;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 地图控件基础类，用于支持google地图，百度地图
 *
 * @author jianchao.wang
 */
@SuppressLint("NewApi")
public abstract class BaseMapActivity extends FragmentActivity
        implements OnClickListener, OnTabActivityResultListener, PrefConstants, RefreshEventListener {
    /**
     * 日志tag
     */
    protected String TAG = "";
    /**
     * 离线地图
     */
    public static final int OFFLINE_MAP_RESULT_CODE = 1000;
    /**
     * 在线地图
     */
    public static final int ONLINE_MAP_RESULT_CODE = 1001;
    /**
     * 百度地图类型
     */
    public static final int MAP_TYPE_GOOGLE = 0;
    /**
     * 百度地图类型
     */
    public static final int MAP_TYPE_BAIDU = 1;
    /**
     * 高德地图类型
     */
    public static final int MAP_TYPE_GAODE = 2;
    /**
     * 地图名称属性名
     */
    protected static final String MAPNAME = "MapName";
    /**
     * 时间计数器
     */
    protected static final int TIMER_TASK = 1200;
    /**
     * 自动跟随模式
     */
    public static final String AUTO_FOLLOW_MODE = "auto_follow_mode";
    /**
     * 加载地图菜单数组
     */
    private String[] menuStr = null;
    /**
     * 应用对象
     */
    protected ApplicationModel appModel = ApplicationModel.getInstance();
    /**
     * 配置管理
     */
    protected SharedPreferences mSharedPreferences;
    /**
     * 测试速度
     */
    private TextView testSpeed;
    /**
     * 自动跟随按钮
     */
    private ImageButton autoFollowBtn;
    /**
     * 当前显示地图类型切换按钮
     */
    private ImageButton mMapTypeChangeBtn;
    /**
     * 更多PopWindow
     */
    private PopupWindow morePopupWindow;
    /**
     * 是否中文系统
     */
    private boolean isZH;
    /**
     * 测试时长
     */
    protected TextView testTime;
    /**
     * 测试距离
     */
    protected TextView testDistance;
    /**
     * 基站导入按钮
     */
    private Button importBtn;
    /**
     * 更多按钮
     */
    private Button moreBtn;
    /**
     * Handler回调对象
     */
    protected Handler mHandler = new MyHandler(new WeakReference<>(this));
    /**
     * GPS状态名称
     */
    protected String mGpsStatusName = "";
    /**
     * 导出地图操作目录
     */
    private final static String EXPORT_MAP_ACTION_DIR = "com.walktour.BaseMapActivity.exportMap";
    /**
     * 导出基站数据操作目录
     */
    private final static String EXPORT_BASE_ACTION_DIR = "com.walktour.BaseMapActivity.exportBase";
    /**
     * 变量名称
     */
    private final static String EXTRA_DIR = "dir";
    /**
     * 下载成功标识
     */
    private static final int EXPORT_BASE_END = 12;
    /**
     * 地图图层
     */
    protected BaseMapLayer mapLayer;
    /**
     * 当前点的经纬度显示
     */
    protected TextView latlngText;
    /**
     * 地图类型
     */
    protected int mapType;
    /**
     * 基站信息搜索弹出窗口
     */
    private BaseStationSearchPopWindow baseStationPop;
    /**
     * 导出的文件类型
     */
    private BaseStationExportFactory.FileType fileType;
    /**
     * 保存的图片类型
     */
    private ImageUtil.FileType picFileType;
    /**
     * 标题显示
     */
    protected TextView titleText;
    /**
     * 基站数据导入弹出窗口
     */
    private PopupWindow stationImportPop;
    /**
     * 导出进度条
     */
    private static ProgressDialog progress;
    /**
     * 地图工厂类
     */
    protected NewMapFactory factory;
    /**
     * 当前是否显示弹出框
     */
    private boolean isShowPopWindow = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.factory = NewMapFactory.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        switch (mapType) {
            case MAP_TYPE_GOOGLE:
                setContentView(R.layout.map_google_main_activity);
                break;
            case MAP_TYPE_GAODE:
                setContentView(R.layout.map_gaode_main_activity);
                break;
            case MAP_TYPE_BAIDU:
                setContentView(R.layout.map_baidu_main_activity);
                break;
            default:
                setContentView(R.layout.map_baidu_main_activity);
                break;
        }
        switch (mapType) {
            // case MAP_TYPE_GOOGLE:
            // this.TAG = "GoogleMapMainActivity";
            // this.title = "Google.Map Online";
            // this.mapLayer = new GoogleMapLayer(savedInstanceState, this,
            // this.initLatLng);
            // break;
            case MAP_TYPE_GAODE:
                this.TAG = "GaoDeMapMainActivity";
                String mapName = "Gaode.Map";
                if (StringUtil.isNullOrEmpty(this.factory.getTitle()))
                    this.factory.setTitle(mapName + " Online");
//				this.mapLayer = new GaodeMapLayer(this);
                break;
            case MAP_TYPE_BAIDU:
                this.TAG = "BaiduMapMainActivity";
                mapName = "Baidu.Map";
                if (StringUtil.isNullOrEmpty(this.factory.getTitle()))
                    this.factory.setTitle(mapName + " Online");
                this.mapLayer = new BaiduMapLayer(this);
                break;
            default:
                this.TAG = "BaiduMapMainActivity";
                mapName = "Baidu.Map";
                String[] dtmap = getResources().getStringArray(R.array.sys_dtmap_default);
                if (dtmap.length>3&& ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[3])) {
                    mapName = "A.Map";
                }
                if (StringUtil.isNullOrEmpty(this.factory.getTitle()))
                    this.factory.setTitle(mapName + " Online");
                this.mapLayer = new BaiduMapLayer(this);
                break;
        }
        this.fillOverlays();
        this.findView();
        this.init();
    }


    /**
     * 填充图层
     */
    private void fillOverlays() {
        FrameLayout layout = (FrameLayout) this.findViewById(R.id.main);
        layout.addView(this.mapLayer.getMap());
        List<BaseMapOverlay> list = this.mapLayer.getOverlays();
        for (BaseMapOverlay overlay : list) {
            layout.addView(overlay);
        }
    }

    /**
     * 初始化
     */
    @SuppressWarnings("deprecation")
    protected void init() {
        RefreshEventManager.addRefreshListener(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        IntentFilter intentFilter = new IntentFilter(GpsInfo.gpsLocationChanged);
        intentFilter.addAction(MapActivity.ACTION_MAP_COLOR_CHANGE);
        intentFilter.addAction(WalkMessage.ACTION_MAP_IMPORT_KML);
        intentFilter.addAction(EXPORT_MAP_ACTION_DIR);
        intentFilter.addAction(EXPORT_BASE_ACTION_DIR);
        intentFilter.addAction(WalkMessage.REPLAY_CLEAR_ALL_DATA);
        intentFilter.addAction(MetroTestService.GPS_LOCATION_CHANGED);
        // 注册广播接收器
        this.registerReceiver(mReceiver, intentFilter);

        startTimer();
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences uiState = getPreferences(Activity.MODE_PRIVATE);
        final boolean fullScreen = pref.getBoolean("pref_showstatusbar", true);
        if (fullScreen)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        else
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (uiState.getString("error", "").length() > 0) {
            showDialog(R.id.error);
        }
        this.showTitleView();
        initAutoFollowMode();
    }

    /**
     * 显示标题视图
     */
    private void showTitleView() {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean showTitle = pref.getBoolean("pref_showtitle", true);
        if (!showTitle) {
            findViewById(R.id.screen).setVisibility(View.GONE);
        }
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
     * 初始化所有View对象 定位成功通过调用updateToNewLocation方法更新地图打点位置
     */
    protected void findView() {
        isZH = TextUtils.equals(Locale.getDefault().getLanguage(), "zh");
        this.importBtn = initButton(R.id.import_basedata);
        this.importBtn.setOnClickListener(this);
        findViewById(R.id.map_btn).setOnClickListener(this);
        findViewById(R.id.search_btn).setOnClickListener(this);
        findViewById(R.id.clear_btn).setOnClickListener(this);
        this.moreBtn = initButton(R.id.more_btn);
        this.moreBtn.setOnClickListener(this);
        findViewById(R.id.title).setOnClickListener(this);
        autoFollowBtn = (ImageButton) findViewById(R.id.auto_follow);
        autoFollowBtn.setOnClickListener(this);
        this.mMapTypeChangeBtn = (ImageButton) findViewById(R.id.map_type_change);
        this.setMapTypeChangeButtonImage();
        this.mMapTypeChangeBtn.setOnClickListener(this);
        testTime = ((TextView) findViewById(R.id.test_time));
        testDistance = ((TextView) findViewById(R.id.test_distance));
        testSpeed = ((TextView) findViewById(R.id.test_speed));
        this.latlngText = (TextView) findViewById(R.id.latlng);
        ImageView zoomIn = initImageView(R.id.zoom_in);
        zoomIn.setOnClickListener(this);
        ImageView zoomOut = initImageView(R.id.zoom_out);
        zoomOut.setOnClickListener(this);
        setMapTitle();
        this.initMorePopView();
        this.initStationImportPopView();
    }

    /**
     * 初始化基站导入弹出窗口
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    private void initStationImportPopView() {
        View popView = LayoutInflater.from(this).inflate(R.layout.base_station_import_pop, null);
        popView.findViewById(R.id.station_import).setOnClickListener(this);
        popView.findViewById(R.id.station_export).setOnClickListener(this);
        float density = this.getResources().getDisplayMetrics().density;
        this.stationImportPop = new PopupWindow(popView, (int) (100 * density), (int) (110 * density), true);
        stationImportPop.setTouchable(true);
        stationImportPop.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * 初始化更多弹出菜单
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    private void initMorePopView() {
        View morePopView = LayoutInflater.from(this).inflate(R.layout.googlemap_more_pop, null);
        morePopView.findViewById(R.id.map_ranging).setOnClickListener(this);
        morePopView.findViewById(R.id.map_export).setOnClickListener(this);
        if (this.mapType == MAP_TYPE_BAIDU || this.mapType == MAP_TYPE_GAODE)
            morePopView.findViewById(R.id.import_kml).setVisibility(View.GONE);
        else
            morePopView.findViewById(R.id.import_kml).setOnClickListener(this);
        morePopView.findViewById(R.id.setting_btn).setOnClickListener(this);
        morePopView.findViewById(R.id.gps_btn).setOnClickListener(this);
        float density = this.getResources().getDisplayMetrics().density;
        morePopupWindow = new PopupWindow(morePopView, (int) (150 * density),
                (this.mapType == MAP_TYPE_BAIDU || this.mapType == MAP_TYPE_GAODE) ? (int) (210 * density) : (int) (265 * density), true);
        morePopupWindow.setFocusable(true);
        morePopupWindow.setTouchable(true);
        morePopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        BasicDialog.Builder builder = new BasicDialog.Builder(this.getParent());
        switch (id) {

            case MapActivity.LOADING_MAP_DIALOG:

                if ((ApplicationModel.getInstance().isGerenalTest() || ApplicationModel.getInstance().isIndoorTest())
                        && appModel.isTestJobIsRun()) { // 室内地图
                    menuStr = getResources().getStringArray(R.array.array_indoor_map);
                } else if (!ApplicationModel.getInstance().isGerenalTest() && !ApplicationModel.getInstance().isIndoorTest()
                        && appModel.isTestJobIsRun()) { // dt地图
                    if (ParameterSetting.getInstance().getDtDefaultMap().equals(getResources().getStringArray(R.array.sys_dtmap_default)[0])) {
                        menuStr = new String[]{getResources().getString(R.string.map_outdoor)};
                    } else {
                        menuStr = new String[]{getResources().getString(R.string.map_offline), getResources().getString(R.string.map_online)};
                    }
                } else {
                    menuStr = getResources().getStringArray(R.array.array_loading_map);
                }

                if (appModel.getSelectScene().equals(SceneType.Metro)
                        || appModel.getSelectScene().equals(SceneType.HighSpeedRail)) {
                    menuStr = new String[]{getResources().getString(R.string.map_scan), getResources().getString(R.string.map_outdoor), getResources().getString(R.string.map_offline), getResources().getString(R.string.map_online)};
                }
                builder.setTitle(R.string.map_menu_title).setItems(menuStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String switchStr = menuStr[which]; // 当前选择item的名字
                        if (switchStr.equals(getString(R.string.map_innsmap))
                                || switchStr.equals(getString(R.string.map_outdoor))
                                || switchStr.equals(getString(R.string.map_online))) {
                            LogUtil.d(TAG,"是否加载室内：不是");
                            SharePreferencesUtil.getInstance(WalktourApplication.getAppContext()).saveBoolean(MapView.SP_IS_LOAD_INDOOR_MAP, false);
                        }
                        boolean isTest = (appModel.isTestJobIsRun() || appModel.isTestStoping()) && appModel.isGpsTest();
                        if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[0])) {// 扫描地图
                            if (isTest) {
                                Toast.makeText(BaseMapActivity.this, getString(R.string.map_testRunAndGpsOpen_CantLoadScan),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                ((MapTabActivity) getParent()).swicthMap(WalkStruct.ShowInfoType.Map);
                                ImageExplorer explorer = new ImageExplorer(BaseMapActivity.this, MapActivity.ACTION_LOAD_SCAN_MAP,
                                        MapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_picture));
                                explorer.start();
                            }
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[1])) {// 室内地图
                            if (isTest) {
                                Toast.makeText(BaseMapActivity.this, getString(R.string.map_testRunAndGpsOpen_CantLoadIndoor),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                // 打开配置好的楼层地图
                                Intent intent = new Intent(BaseMapActivity.this, SysIndoor.class);
                                intent.putExtra(SysIndoor.KEY_LOADING, true);
                                getParent().startActivityForResult(intent, 10);
                            }
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[2])) {// MIF地图
                            if (isTest) {
                                Toast.makeText(BaseMapActivity.this, getString(R.string.map_testRunAndIndoor_CantLoadMif),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                ((MapTabActivity) BaseMapActivity.this.getParent()).swicthMap(WalkStruct.ShowInfoType.BaiduMap);
                                TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
                                new ImageExplorer(BaseMapActivity.this, MapActivity.ACTION_LOAD_MIF_MAP, MapActivity.Map_File_Path,
                                        getResources().getStringArray(R.array.maptype_mif)).start();
                            }
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[3])) {// iBwave地图
                            ((MapTabActivity) getParent()).swicthMap(WalkStruct.ShowInfoType.Map);
                            new ImageExplorer(BaseMapActivity.this, com.walktour.gui.map.MapActivity.ACTION_LOAD_TAB_MAP,
                                    com.walktour.gui.map.MapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_tab))
                                    .start();
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[4])) {// 离线地图
                            ((MapTabActivity) getParent()).swicthMap(WalkStruct.ShowInfoType.OfflineMap);
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[5])) {// 在线地图
                            ((MapTabActivity) getParent()).swicthMap(WalkStruct.ShowInfoType.OnlineMap);
                        }
                    }
                });
                break;

        }
        return builder.create();
    }

    /**
     * 触发地图标题修改的信息
     */
    public void handleMapTitle() {
        this.mHandler.sendEmptyMessage(R.id.set_title);
    }

    /**
     * 回调类
     *
     * @author jianchao.wang
     */
    private static class MyHandler extends Handler {
        private WeakReference<BaseMapActivity> reference;

        public MyHandler(WeakReference<BaseMapActivity> reference) {
            this.reference = reference;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void handleMessage(final Message msg) {
            BaseMapActivity activity = reference.get();
            final int what = msg.what;
            switch (what) {
                case TIMER_TASK:
                    activity.testTime.setText(TraceInfoInterface.traceData.getTestTimeHHmmss());
                    activity.testDistance.setText(TraceInfoInterface.traceData.getTestMileageStr());
                    activity.startTimer();
                    break;
                case R.id.user_moved_map:
                    // setAutoFollow(false);
                    break;
                case R.id.set_title:
                    activity.setMapTitle();
                    break;
                case R.id.add_yandex_bookmark:
                    activity.showDialog(R.id.add_yandex_bookmark);
                    break;
                case Ut.ERROR_MESSAGE:
                    if (msg.obj != null)
                        Toast.makeText(activity, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
                case EXPORT_BASE_END:
                    if (progress != null) {
                        progress.dismiss();
                    }
                    break;
            }
        }
    }

    /**
     * 设置标题信息 ;主要包含名字、GPS信息、缩放大小、地图信息等
     */
    protected void setMapTitle() {
        try {
            titleText = (TextView) findViewById(R.id.left_text);
            if (titleText != null) {
                titleText.setText(this.factory.getTitle());
            }
            final TextView gpsText = (TextView) findViewById(R.id.gps_text);
            if (gpsText != null) {
                gpsText.setText(mGpsStatusName);
            }
            final TextView rightText = (TextView) findViewById(R.id.right_text);
            if (rightText != null) {
                rightText.setText("" + (int) NewMapFactory.getInstance().getZoomLevelNow());
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onRefreshed(RefreshType refreshType, Object object) {
        switch (refreshType) {
            case REFRSH_GOOGLEMAP_BASEDATA:
                BaseStation baseStation = (BaseStation) object;
                factory.setSelectBaseStation(baseStation);
                MyLatLng latlng = new MyLatLng(baseStation.latitude, baseStation.longitude);
                this.mapLayer.setCenter(latlng, 16);
                break;

            default:
                break;
        }

    }


    /**
     * 初始化自动跟随模式
     */
    public void initAutoFollowMode() {
        int mode = mSharedPreferences.getInt(AUTO_FOLLOW_MODE, 0);
        switch (mode) {
            case 0:
                autoFollowBtn.setImageResource(R.drawable.main_icon_location);
                break;
            case 1:
                autoFollowBtn.setImageResource(this.isZH ? R.drawable.main_icon_follow : R.drawable.main_icon_follow_en);
                break;
            case 2:
                autoFollowBtn.setImageResource(R.drawable.main_icon_mark);
                break;

            default:
                break;
        }
    }

    /**
     * 设置自动跟随模式<BR>
     * [功能详细描述]
     */
    private void setAutoFollowMode() {
        int mode = mSharedPreferences.getInt(AUTO_FOLLOW_MODE, 0);
        switch (mode) {
            case 0:
                mSharedPreferences.edit().putInt(AUTO_FOLLOW_MODE, 1).commit();
                autoFollowBtn.setImageResource(this.isZH ? R.drawable.main_icon_follow : R.drawable.main_icon_follow_en);
                break;
            case 1:
                if (appModel.isTestJobIsRun()) {
                    mSharedPreferences.edit().putInt(AUTO_FOLLOW_MODE, 0).commit();
                    autoFollowBtn.setImageResource(R.drawable.main_icon_location);
                } else {
                    mSharedPreferences.edit().putInt(AUTO_FOLLOW_MODE, 2).commit();
                    autoFollowBtn.setImageResource(R.drawable.main_icon_mark);
                }
                break;
            case 2:
                mSharedPreferences.edit().putInt(AUTO_FOLLOW_MODE, 0).commit();
                autoFollowBtn.setImageResource(R.drawable.main_icon_location);
                break;
            default:
                break;
        }
    }

    /**
     * 更新位置信息并刷新地图
     *
     * @param latitude  纬度
     * @param longitude 经度
     */
    protected void locationNewLatlng(double latitude, double longitude) {
        if (latitude == -9999 || longitude == -9999)
            return;
        this.mapLayer.locationNewLatlng(latitude, longitude);
    }

    /**
     * 导入基站数据
     */
    protected void importBaseData() {
        new LicenseExplorer(this, new String[]{"txt", "xls", "kml", "mif"}, LicenseExplorer.LOADING_BASE_DATA,
                BaseStation.MAPTYPE_OUTDOOR).start();
    }

    /**
     * 清理基站信息
     */
    protected void clearBaseData() {
        BaseStationDBHelper.getInstance(this.getApplicationContext()).clearAllData();
        this.mapLayer.setOverlaysCenter();
    }

    /**
     * 清理打点
     */
    protected void clearMarker() {
        TraceInfoInterface.traceData.getGpsLocas().clear();
        AlertManager.getInstance(this.getApplicationContext()).clearAlarms(true);
        this.mapLayer.setOverlaysCenter();
    }

    /**
     * 清除全部
     */
    protected void clearAll() {
        BaseStationDBHelper.getInstance(this.getApplicationContext()).clearAllData();
        TraceInfoInterface.traceData.getGpsLocas().clear();
        this.mapLayer.setOverlaysCenter();
    }

    /**
     * 接受位置更新广播
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (GpsInfo.gpsLocationChanged.equals(intent.getAction())) {
                Location locat = GpsInfo.getInstance().getLocation();
                if (locat != null && testSpeed != null) {
                    testSpeed.setText(UtilsMethod.decFormat.format(locat.getSpeed() * 3.6) + " km/h");
                }
                if (locat != null && latlngText != null) {
                    latlngText.setText(
                            Util.formatGeoPoint(new GeoPoint((int) (locat.getLatitude() * 1e6), (int) (locat.getLongitude() * 1e6))));
                } else {
                    latlngText.setText("-E,-N");
                }
                if (locat != null && System.currentTimeMillis() - GpsInfo.getInstance().getGpsLastChangeTime() > 3000) {
                    // Logger.d(TAG, "收到GPS,大于3秒，刷新界面");
                    locationNewLatlng(locat.getLatitude(), locat.getLongitude());
                    GpsInfo.getInstance().setGpsLastChangeTime(System.currentTimeMillis());
                }
            } else if (MetroTestService.GPS_LOCATION_CHANGED.equals(intent.getAction())) {
                double latitude = intent.getDoubleExtra("lat", -9999);
                double longitude = intent.getDoubleExtra("lon", -9999);
                if (latitude != -9999 && latlngText != null) {
                    latlngText.setText(Util.formatGeoPoint(new GeoPoint((int) (latitude * 1e6), (int) (longitude * 1e6))));
                } else {
                    latlngText.setText("-E,-N");
                }
//				if (System.currentTimeMillis() - GpsInfo.getInstance().getGpsLastChangeTime() > 2000) {
                // Logger.d(TAG, "收到GPS,大于3秒，刷新界面");
                locationNewLatlng(latitude, longitude);
                GpsInfo.getInstance().setGpsLastChangeTime(System.currentTimeMillis());
//				}
            } else if (HighSpeedGpsService.GPS_LOCATION_CHANGED.equals(intent.getAction())) {
                double latitude = intent.getDoubleExtra("lat", -9999);
                double longitude = intent.getDoubleExtra("lon", -9999);
                if (latitude != -9999 && latlngText != null) {
                    latlngText.setText(Util.formatGeoPoint(new GeoPoint((int) (latitude * 1e6), (int) (longitude * 1e6))));
                } else {
                    latlngText.setText("-E,-N");
                }
                //由于快到站了，会一次性给多个点，去掉时间限制
//                if (System.currentTimeMillis() - GpsInfo.getInstance().getGpsLastChangeTime() > 2000) {
                locationNewLatlng(latitude, longitude);
                GpsInfo.getInstance().setGpsLastChangeTime(System.currentTimeMillis());
//                }
            } else if (intent.getAction().equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_MAP_COLOR_CHANGE)) {
                LogUtil.v("MapChange", "---Change---");
                findViewById(R.id.threshold_view).invalidate();
                // setValue();
            } else if (intent.getAction().equals(WalkMessage.ACTION_MAP_IMPORT_KML)) {
                String path = intent.getStringExtra(WalkMessage.KEY_MAP_KML_PATH);
                doImportTrack(path);
            } else if (intent.getAction().equals(EXPORT_MAP_ACTION_DIR)) {// 导出地图到指定目录
                String path = intent.getExtras().getString(EXTRA_DIR);
                saveViewToBMP(path, null);
            } else if (intent.getAction().equals(EXPORT_BASE_ACTION_DIR)) {// 导出基站数据到指定目录
                String path = intent.getExtras().getString(EXTRA_DIR);
                saveBaseToFile(path);
            } else if (intent.getAction().equals(WalkMessage.REPLAY_CLEAR_ALL_DATA)) { // 回放时清除数据
                mapLayer.setCenter(mapLayer.getMapCenter(), factory.getZoomLevelNow());
            }
        }
    };

    /**
     * 导入导航路径
     *
     * @param mFileName 文件路径
     */
    protected abstract void doImportTrack(final String mFileName);

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
                BaseStationExportFactory.getInstance().exportFile(BaseMapActivity.this, path, fileType);
                mHandler.sendEmptyMessage(EXPORT_BASE_END);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if (this.baseStationPop != null)
            this.baseStationPop.close();
        RefreshEventManager.removeRefreshListener(this);
        this.unregisterReceiver(mReceiver);
        LogUtil.d(TAG, "-----onDestroy-----");
        this.mapLayer.onDestroy();
        //  If null, all callbacks and messages will be removed.
        mHandler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }

    /**
     * 显示基站搜索窗
     */
    private void showBaseStatinPopView() {
        if (this.baseStationPop == null) {
            View map = this.mapLayer.getMap();
            this.baseStationPop = new BaseStationSearchPopWindow(map, this.getParent(), map.getMeasuredWidth(),
                    map.getMeasuredHeight() / 2);
            int[] location = new int[2];
            this.getWindow().getDecorView().getLocationOnScreen(location);
            this.baseStationPop.setLocation(location);
        }
        this.baseStationPop.show();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.station_import:
                this.stationImportPop.dismiss();
                importBaseData();
                break;
            case R.id.station_export:
                this.stationImportPop.dismiss();
                exportBaseData();
                break;
            case R.id.map_ranging:
                factory.setRanging();
                if (!factory.isRanging())
                    this.mapLayer.setCenter(this.mapLayer.getMapCenter(), factory.getZoomLevelNow());
                morePopupWindow.dismiss();
                break;
            case R.id.map_btn:
                showDialog(MapActivity.LOADING_MAP_DIALOG);
                break;
            case R.id.search_btn:
                this.showBaseStatinPopView();
                break;
            case R.id.gps_btn:
                startActivity(new Intent(BaseMapActivity.this, Gps.class));
                morePopupWindow.dismiss();
                break;
            case R.id.clear_btn:
                showClearDialog();
                break;
            case R.id.setting_btn:
                Intent intent = new Intent(BaseMapActivity.this, SysMap.class);
                startActivity(intent);
                morePopupWindow.dismiss();
                break;
            case R.id.more_btn:
                showMorePopView();
                break;
            case R.id.import_basedata:
                showStationImportPopView();
                break;
            case R.id.import_kml:
                intent = new Intent(new Intent(BaseMapActivity.this, FileExplorer.class));
                Bundle bundle = new Bundle();
                bundle.putStringArray(FileExplorer.KEY_FILE_FILTER, new String[]{"kml"});
                bundle.putString(FileExplorer.KEY_ACTION, WalkMessage.ACTION_MAP_IMPORT_KML);
                bundle.putString(FileExplorer.KEY_EXTRA, WalkMessage.KEY_MAP_KML_PATH);
                intent.putExtras(bundle);
                startActivity(intent);
                morePopupWindow.dismiss();
                break;
            case R.id.auto_follow:
                setAutoFollowMode();
                break;
            case R.id.map_export:
                morePopupWindow.dismiss();
                exportMapImage();
                break;
            case R.id.map_type_change:
                mapLayer.changeMapType();
                this.setMapTypeChangeButtonImage();
                break;
            case R.id.zoom_in:
                mapLayer.zoomIn();
                setMapTitle();
                break;
            case R.id.zoom_out:
                mapLayer.zoomOut();
                setMapTitle();
                break;
            default:
                break;
        }

    }

    /**
     * 设置地图类型更改按钮
     */
    private void setMapTypeChangeButtonImage() {
        int mapType = NewMapFactory.getInstance().getMapType();
        System.out.println("地图类型:" + mapType);
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

    /**
     * 导出地图图片
     */
    private void exportMapImage() {
        final String[] fileTypes = new String[]{"JPEG", "BMP"};
        new BasicDialog.Builder(BaseMapActivity.this.getParent()).setTitle(R.string.facebook_filetype)
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
                        new ExplorerDirectory(BaseMapActivity.this, fileTypes, EXPORT_MAP_ACTION_DIR, EXTRA_DIR).start();
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 导出基站
     */
    private void exportBaseData() {
        final String[] fileTypes = new String[]{"TXT", "XLS", "KML", "MIF"};
        new BasicDialog.Builder(BaseMapActivity.this.getParent()).setTitle(R.string.facebook_filetype)
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
                        new ExplorerDirectory(BaseMapActivity.this, fileTypes, EXPORT_BASE_ACTION_DIR, EXTRA_DIR).start();
                        dialog.dismiss();
                    }
                }).show();
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

    /**
     * 保存视图为文件
     *
     * @param path    存放目录
     * @param picName 文件名称
     */
    public abstract void saveViewToBMP(String path, String picName);

    /**
     * 显示清除菜单Dialog<BR>
     * [功能详细描述]
     */
    public void showClearDialog() {
        new BasicDialog.Builder(BaseMapActivity.this.getParent()).setIcon(android.R.drawable.ic_menu_edit)
                .setTitle(R.string.facebook_filetype)
                .setItems(
                        new String[]{getResources().getString(R.string.clear_basedata),
                                getResources().getString(R.string.clear_marker), getResources().getString(R.string.clear_all)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:// 清除基站
                                        clearBaseData();
                                        break;
                                    case 1:// 清除打点
                                        clearMarker();
                                        break;
                                    case 2:// 清除全部
                                        clearAll();
                                        break;

                                    default:
                                        break;
                                }

                            }
                        })
                .show();
    }


    /**
     * 显示基站导入弹出可选项
     */
    private void showStationImportPopView() {
        int[] location = new int[2];
        this.importBtn.getLocationOnScreen(location);
        int height = this.stationImportPop.getHeight();
        int width = this.stationImportPop.getWidth();
        this.stationImportPop.showAtLocation(this.importBtn, Gravity.NO_GRAVITY, location[0] - width / 2,
                location[1] - height);
    }

    /**
     * 显示More Pop<BR>
     * 点击More弹出POP展示可选项
     */
    private void showMorePopView() {
        int[] location = new int[2];
        this.moreBtn.getLocationOnScreen(location);
        View mapRanging = this.morePopupWindow.getContentView().findViewById(R.id.map_ranging);
        if (this.factory.isRanging()) {
            mapRanging.setBackgroundColor(getResources().getColor(R.color.base_list_item_bg_select));
        } else {
            mapRanging.setBackgroundResource(R.drawable.base_list_item_bg);
        }
        this.morePopupWindow.showAsDropDown(this.moreBtn, 10, 10);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case (R.id.gpsstatus):
                startActivity(new Intent(BaseMapActivity.this, Gps.class));
                return true;
            case (R.id.poilist):
                setBaseStation();
                return true;
            case (R.id.search):
                super.onSearchRequested();
                return true;
            case (R.id.settings):
                startActivityForResult(new Intent(this, MainPreferences.class), R.id.settings_activity_closed);
                return true;
            case (R.id.mylocation):

                return true;
            default:
                final String mapId = (String) item.getTitleCondensed();
                this.setTileSource(mapId);
                fillOverlays();
                setMapTitle();
                return true;
        }

    }

    /**
     * 设置基站
     */
    private void setBaseStation() {
        MyLatLng latlng = this.mapLayer.getMapCenter();
        if (latlng == null)
            return;
        startActivityForResult((new Intent(this, PoiListActivity.class)).putExtra("lat", latlng.latitude)
                .putExtra("lon", latlng.longitude).putExtra("title", "POI"), R.id.poilist);
    }

    /**
     * 填充图片瓦片资源
     *
     * @param mapId 地图ID
     */
    protected abstract void setTileSource(String mapId);

    @Override
    protected void onPause() {
        if (this.baseStationPop != null)
            this.baseStationPop.close();
        this.mapLayer.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        this.mapLayer.onResume();
        super.onResume();
    }

    /**
     * 保存图像到文件中
     *
     * @param path     保存路径
     * @param bitmap   图像
     * @param fileName 文件名
     */
    protected void saveBitmapToFile(final String path, final Bitmap bitmap, final String fileName) {
        ImageUtil.saveBitmapToFile(path, bitmap, fileName, picFileType);
    }

    public int getMapType() {
        return mapType;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            this.mapLayer.initOverlay();
        }
        super.onWindowFocusChanged(hasFocus);
    }

    public void setShowPopWindow(boolean isShowPopWindow) {
        this.isShowPopWindow = isShowPopWindow;
    }

    @Override
    public void onBackPressed() {
        if (this.isShowPopWindow) {
            this.mapLayer.closeShowPopWindow();
            this.isShowPopWindow = false;
            return;
        }
        super.onBackPressed();
    }

    /**
     * 初始化View
     *
     * @param viewId view的ID
     * @return View
     */
    protected ViewFlipper initViewFlipper(int viewId) {
        return (ViewFlipper) this.findViewById(viewId);
    }

    /**
     * 初始化View
     *
     * @param viewId view的ID
     * @return View
     */
    protected View initView(int viewId) {
        return this.findViewById(viewId);
    }

    /**
     * 初始化View
     *
     * @param viewId view的ID
     * @return Button
     */
    protected Button initButton(int viewId) {
        return (Button) this.findViewById(viewId);
    }

    protected ImageButton initImageButton(int viewId) {
        return (ImageButton) this.findViewById(viewId);
    }

    /**
     * 初始化View
     *
     * @param viewId view的ID
     * @return Button
     */
    protected CheckBox initCheckBox(int viewId) {
        return (CheckBox) this.findViewById(viewId);
    }

    /**
     * 初始化View
     *
     * @param viewId view的ID
     * @return TextView
     */
    protected TextView initTextView(int viewId) {
        return (TextView) this.findViewById(viewId);
    }

    /**
     * 初始化View
     *
     * @param viewId view的ID
     * @return RadioButton
     */
    protected RadioButton initRadioButton(int viewId) {
        return (RadioButton) this.findViewById(viewId);
    }

    /**
     * 初始化View
     *
     * @param viewId view的ID
     * @return AutoCompleteTextView
     */
    protected AutoCompleteTextView initAutoCompleteTextView(int viewId) {
        return (AutoCompleteTextView) this.findViewById(viewId);
    }

    /**
     * 初始化View
     *
     * @param viewId view的ID
     * @return ToggleButton
     */
    protected ToggleButton initToggleButton(int viewId) {
        return (ToggleButton) this.findViewById(viewId);
    }

    /**
     * 初始化View
     *
     * @param viewId view的ID
     * @return EditText
     */
    protected EditText initEditText(int viewId) {
        return (EditText) this.findViewById(viewId);
    }

    /**
     * 初始化View
     *
     * @param viewId view的ID
     * @return ImageView
     */
    protected ImageView initImageView(int viewId) {
        return (ImageView) this.findViewById(viewId);
    }

    /**
     * 初始化View
     *
     * @param viewId view的ID
     * @return Spinner
     */
    protected Spinner initSpinner(int viewId) {
        return (Spinner) this.findViewById(viewId);
    }

    /**
     * 初始化View
     *
     * @param viewId view的ID
     * @return ProgressBar
     */
    protected ProgressBar initProgressBar(int viewId) {
        return (ProgressBar) this.findViewById(viewId);
    }

    /**
     * 初始化LineLayout
     *
     * @param viewId view的ID
     * @return LinearLayout
     */
    protected LinearLayout initLinearLayout(int viewId) {
        return (LinearLayout) this.findViewById(viewId);
    }

    /***
     * 初始化RelativeLayout
     *
     * @param viewId
     *          view的ID
     * @return RelativeLayout
     */
    protected RelativeLayout initRelativeLayout(int viewId) {
        return (RelativeLayout) this.findViewById(viewId);
    }

    /***
     * 初始化ListView
     *
     * @param viewId
     *          view的ID
     * @return ListView
     */
    protected ListView initListView(int viewId) {
        return (ListView) this.findViewById(viewId);
    }
}
