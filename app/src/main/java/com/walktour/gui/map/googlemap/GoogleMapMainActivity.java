package com.walktour.gui.map.googlemap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.logic.PointIndexChangeLinstener;
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
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.ui.ActivityManager;
import com.walktour.framework.ui.BasicActivity;
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
import com.walktour.gui.map.MapActivity;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.map.MapTabActivity;
import com.walktour.gui.map.ThresholdView;
import com.walktour.gui.map.googlemap.constants.PrefConstants;
import com.walktour.gui.map.googlemap.kml.PoiListActivity;
import com.walktour.gui.map.googlemap.kml.PoiManager;
import com.walktour.gui.map.googlemap.kml.XMLparser.GpxTrackParser;
import com.walktour.gui.map.googlemap.kml.XMLparser.KmlTrackParser;
import com.walktour.gui.map.googlemap.location.GoogleCorrectUtil;
import com.walktour.gui.map.googlemap.overlays.BaseDataOverlay;
import com.walktour.gui.map.googlemap.overlays.MarkerPointOverlay;
import com.walktour.gui.map.googlemap.overlays.MyLocationOverlay;
import com.walktour.gui.map.googlemap.overlays.PoiOverlay;
import com.walktour.gui.map.googlemap.overlays.TrackOverlay;
import com.walktour.gui.map.googlemap.tileprovider.TileSource;
import com.walktour.gui.map.googlemap.utils.CrashReportHandler;
import com.walktour.gui.map.googlemap.utils.RException;
import com.walktour.gui.map.googlemap.utils.SimpleThreadFactory;
import com.walktour.gui.map.googlemap.utils.Ut;
import com.walktour.gui.map.googlemap.view.IMoveListener;
import com.walktour.gui.map.googlemap.view.MapView;
import com.walktour.gui.map.googlemap.view.TileViewOverlay;
import com.walktour.gui.newmap.basestation.BaseStationDetailPopWindow;
import com.walktour.gui.newmap.basestation.BaseStationSearchPopWindow;
import com.walktour.gui.newmap.basestation.util.BaseStationExportFactory;
import com.walktour.gui.newmap.util.Util;
import com.walktour.gui.setting.SysIndoor;
import com.walktour.gui.setting.SysMap;
import com.walktour.model.Parameter;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;
import org.openintents.filemanager.util.FileUtils;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class GoogleMapMainActivity extends BasicActivity
		implements OnTabActivityResultListener, PrefConstants, PointIndexChangeLinstener, RefreshEventListener {

	private String TAG = "GoogleMapMainActivity";

	private static final String MAPNAME = "MapName";

	private static final String ACTION_SHOW_POINTS = "com.robert.maps.action.SHOW_POINTS";

	public static final int OFFILEMAP_RESULT_CODE = 1000;

	public static final int ONLINEMAP_RESULT_CODE = 1001;

	private static final int TIMER_TASK = 1200;
	/** 导出地图操作目录 */
	private final static String EXPORT_MAP_ACTION_DIR = "com.walktour.GoogleMapMainActivity.exportMap";
	/** 导出基站数据操作目录 */
	private final static String EXPORT_BASE_ACTION_DIR = "com.walktour.GoogleMapMainActivity.exportBase";
	/** 下载成功标识 */
	private static final int EXPORT_BASE_END = 12;
	/** 变量名称 */
	private final String EXTRA_DIR = "dir";
	/** 基站数据导入弹出窗口 */
	private PopupWindow stationImportPop;
	/** 配置管理 */
	private SharedPreferences mSharedPreferences;
	/** 地图对象 */
	private MapView mMap;
	/** 地图源对象 */
	private TileSource mTileSource;
	/** Poi信息管理 */
	private PoiManager mPoiManager;
	/** Handler回调对象 */
	private Handler mHandler = new MyHandler(new WeakReference<GoogleMapMainActivity>(this));
	/** 移动监听对象 */
	private MoveListener mMoveListener = new MoveListener();

	private PowerManager.WakeLock myWakeLock;

	/**
	 * 图层
	 */
	// private YandexTrafficOverlay mYandexTrafficOverlay = null;

	private BaseDataOverlay baseDataOverlay;

	private MyLocationOverlay mMyLocationOverlay;

	private PoiOverlay mPoiOverlay;

	private MarkerPointOverlay markerPointOverlay;

	// private SearchResultOverlay mSearchResultOverlay;

	private TrackOverlay mTrackOverlay;

	private String mGpsStatusName = "";

	/**
	 * 地图切换
	 */
	private Button mapBtn;

	/**
	 * GPS
	 */
	private Button moreBtn;

	/**
	 * 定位
	 */
	// private Button lacationBtn;

	/**
	 * 搜索按钮
	 */
	private Button searchBtn;

	/**
	 * 自动跟随按钮
	 */
	private ImageButton autoFollowBtn;

	/**
	 * 更多PopWindow
	 */
	private PopupWindow morePopupWindow;

	private Button clreanMarkerBtn;

	private Button undoBtn;

	private TextView testTime;

	private TextView testDistance;

	private TextView testSpeed;

	private TextView latlngText;
	/** 基站详情弹出框 */
	protected BaseStationDetailPopWindow window;

	/**
	 * 是否调用OnResume逻辑
	 */
	private boolean isOnResume = true;

	/**
	 * 获得位置是否居中显示
	 */
	private boolean isCenter = true;

	private String[] menuStr = null; // 加载地图菜单数组

	private ApplicationModel appModel = ApplicationModel.getInstance();
	/** 保存的图片类型 */
	private ImageUtil.FileType picFileType;
	/** 导出的文件类型 */
	private BaseStationExportFactory.FileType fileType;
	/** 是否正在测距 */
	private boolean isRanging = false;
	/** 基站导入按钮 */
	private Button importBtn;
	/** 导出进度条 */
	private static ProgressDialog progress;

	/**
	 * 自动跟随模式
	 */
	public static final String AUTO_FOLLOW_MODE = "auto_follow_mode";

	protected ExecutorService mThreadPool = Executors.newSingleThreadExecutor(new SimpleThreadFactory("ImportTrack"));

	private boolean mChinease;
	/** 基站信息搜索弹出窗口 */
	private BaseStationSearchPopWindow baseStationPop;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.googlemap_main_activity);

		DatasetManager.getInstance(this).addPointIndexChangeListener(this);
		RefreshEventManager.addRefreshListener(this);
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (savedInstanceState != null) {
			Ut.d("savedInstanceState != null");
			Ut.d(savedInstanceState.getDouble("MAP") + "");
		}
		if (!OpenStreetMapViewConstants.DEBUGMODE)
			CrashReportHandler.attach(this);
		createContentView();

		IntentFilter intentFilter = new IntentFilter(GpsInfo.gpsLocationChanged);
		intentFilter.addAction(MapActivity.ACTION_MAP_COLOR_CHANGE);
		intentFilter.addAction(WalkMessage.ACTION_MAP_IMPORT_KML);
		intentFilter.addAction(EXPORT_MAP_ACTION_DIR);
		intentFilter.addAction(EXPORT_BASE_ACTION_DIR);
		// 注册广播接收器
		this.registerReceiver(mReceiver, intentFilter);

		startTimer();
		mPoiManager = new PoiManager(this);
		mMap.setMoveListener(mMoveListener);

		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences uiState = getPreferences(Activity.MODE_PRIVATE);

		mMap.getController().setCenter(new GeoPoint(uiState.getInt("Latitude", 0), uiState.getInt("Longitude", 0)));

		this.mTrackOverlay = new TrackOverlay(this, mPoiManager, mHandler);
		// this.mCurrentTrackOverlay = new CurrentTrackOverlay(this,
		// mPoiManager);
		/*
		 * this.mPoiOverlay = new PoiOverlay(this, mPoiManager, null,
		 * pref.getBoolean("pref_hidepoi", false));
		 * mPoiOverlay.setTapIndex(uiState.getInt("curShowPoiId", -1));
		 */
		this.mMyLocationOverlay = new MyLocationOverlay(this);
		// this.mSearchResultOverlay = new SearchResultOverlay(this);
		// mSearchResultOverlay.fromPref(uiState);
		this.markerPointOverlay = new MarkerPointOverlay(this, null, null);
		fillOverlays();

		final boolean fullScreen = pref.getBoolean("pref_showstatusbar", true);
		if (fullScreen)
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		else
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (uiState.getString("error", "").length() > 0) {
			showDialog(R.id.error);
		}

		final Intent queryIntent = getIntent();
		final String queryAction = queryIntent.getAction();

		if (ACTION_SHOW_POINTS.equalsIgnoreCase(queryAction)) {
			ActionShowPoints(queryIntent);
		} else if (Intent.ACTION_VIEW.equalsIgnoreCase(queryAction)) {
			Uri uri = queryIntent.getData();
			if (uri.getScheme().equalsIgnoreCase("geo")) {
				final String latlon = uri.getEncodedSchemeSpecificPart().replace("?" + uri.getEncodedQuery(), "");
				if (latlon.equals("0,0")) {
					final String query = uri.getEncodedQuery().replace("q=", "");
					queryIntent.putExtra(SearchManager.QUERY, query);

				} else {
					GeoPoint point = GeoPoint.fromDoubleString(latlon);
					mPoiOverlay.setGpsStatusGeoPoint(point, "GEO", "");
					mMap.getController().setCenter(point);
				}
			}
		}
		findView();
	}

	/**
	 * 初始化所有View对象<BR>
	 * 定位成功通过调用updateToNewLocation方法更新地图打点位置
	 */
	public void findView() {
		mChinease = TextUtils.equals(Locale.getDefault().getLanguage(), "zh");
		mapBtn = initButton(R.id.map_btn);
		// lacationBtn = initButton(R.id.mylacation_btn);
		clreanMarkerBtn = initButton(R.id.clear_btn);
		undoBtn = initButton(R.id.undo_btn);
		moreBtn = initButton(R.id.more_btn);
		searchBtn = initButton(R.id.search_btn);
		this.importBtn = initButton(R.id.import_basedata);
		this.importBtn.setOnClickListener(this);
		mapBtn.setOnClickListener(this);
		// lacationBtn.setOnClickListener(this);
		searchBtn.setOnClickListener(this);
		clreanMarkerBtn.setOnClickListener(this);
		undoBtn.setOnClickListener(this);
		moreBtn.setOnClickListener(this);
		findViewById(R.id.title).setOnClickListener(this);
		autoFollowBtn = (ImageButton) findViewById(R.id.auto_follow);
		autoFollowBtn.setOnClickListener(this);
		testTime = (initTextView(R.id.test_time));
		testDistance = (initTextView(R.id.test_distance));
		testSpeed = (initTextView(R.id.test_speed));
		latlngText = (initTextView(R.id.latlng));
		initAutoFollowMode();
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
	 * 获得当前位置信息<BR>
	 * [功能详细描述]
	 */
	// private void getLocation() {
	// isCenter = true;
	// if (!GpsInfo.getInstance().isJobTestGpsOpen()) {
	// GpsInfo.getInstance().openGps(GoogleMapMainActivity.this,
	// WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
	// }
	// }

	/**
	 * 更新位置信息并刷新地图<BR>
	 * [功能详细描述]
	 *
	 * @param location
	 */
	private void locationNewLatlng(Location location) {
		if (location == null)
			return;
		GeoPoint point = GpsInfo.getInstance().getLastGeoPoint();
		if (point != null) {
			point = GoogleCorrectUtil.adjustLatLng(this, point);
			if (isCenter || mSharedPreferences.getInt(AUTO_FOLLOW_MODE, 0) == 1) {
				mMap.getController().setCenter(point);
				isCenter = false;
				mMap.postInvalidate();
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		final String queryAction = intent.getAction();
		if (ACTION_SHOW_POINTS.equalsIgnoreCase(queryAction))
			ActionShowPoints(intent);

	}

	/**
	 * 创建内容视图<BR>
	 * [功能详细描述]
	 *
	 * @return
	 */
	private View createContentView() {
		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

		final RelativeLayout rl = initRelativeLayout(R.id.map_area);
		// final int sideBottom =
		// Integer.parseInt(pref.getString("pref_zoomctrl",
		// "1"));
		final boolean showTitle = pref.getBoolean("pref_showtitle", true);

		if (!showTitle) {
			findViewById(R.id.screen).setVisibility(View.GONE);
		}
		mMap = (MapView) findViewById(R.id.main);
		/*
		 * mMap = new MapView(this, Integer.parseInt(pref.getString(
		 * "pref_zoomctrl", "1")), pref.getBoolean("pref_showscalebar", true) ? 1 :
		 * 0); mMap.setId(R.id.main); final RelativeLayout.LayoutParams pMap = new
		 * RelativeLayout.LayoutParams( LayoutParams.FILL_PARENT,
		 * LayoutParams.FILL_PARENT); rl.addView(mMap, pMap);
		 */
		return rl;
	}

	/**
	 * 填充Overlays图层<BR>
	 * 向地图中填充相应图标标记
	 */
	private void fillOverlays() {
		this.mMap.getOverlays().clear();

		/*
		 * if (mTileSource == null) { } else if (mTileSource.YANDEX_TRAFFIC_ON == 1
		 * && mYandexTrafficOverlay == null) { mYandexTrafficOverlay = new
		 * YandexTrafficOverlay(this, mMap.getTileView()); } else if
		 * (mTileSource.YANDEX_TRAFFIC_ON != 1 && mYandexTrafficOverlay != null) {
		 * mYandexTrafficOverlay.Free(); mYandexTrafficOverlay = null; }
		 *
		 * if (mYandexTrafficOverlay != null) {
		 * this.mMap.getOverlays().add(mYandexTrafficOverlay); }
		 */

		if (mPoiOverlay != null) {
			this.mMap.getOverlays().add(mPoiOverlay);
		}
		if (mTrackOverlay != null) {
			this.mMap.getOverlays().add(mTrackOverlay);
		}

		this.mMap.getOverlays().add(mMyLocationOverlay);
		// this.mMap.getOverlays().add(mSearchResultOverlay);

		baseDataOverlay = new BaseDataOverlay(this, null, new BaseDataOverlay.OnItemTapListener<BaseStation>() {
			@Override
			public boolean onItemTap(int aIndex, BaseStation aItem) {
				window = new BaseStationDetailPopWindow(mMap, GoogleMapMainActivity.this, aItem);
				window.showPopWindow();
				return true;
			}

			@Override
			public boolean onBaseStationSelect(BaseStation aItem) {
				if (aItem == null && window != null) {
					window.closePopWindow();
					window = null;
				}
				markerPointOverlay.setSearchBaseData(aItem);
				return false;
			}
		});
		this.mMap.getOverlays().add(baseDataOverlay);
		this.mMap.getOverlays().add(markerPointOverlay);
	}

	/**
	 * 设置标题文字<BR>
	 * 文字主要包含名字、GPS信息、缩放大小、地图信息等
	 */
	private void setTitle() {
		try {
			final TextView leftText = initTextView(R.id.left_text);
			if (leftText != null) {
				leftText.setText(mMap.getTileSource().NAME.replace("usermap_", "").replace("_sqlitedb", ""));
			}
			final TextView gpsText = initTextView(R.id.gps_text);
			if (gpsText != null) {
				gpsText.setText(mGpsStatusName);
			}

			final TextView rightText = initTextView(R.id.right_text);
			if (rightText != null) {
				final double zoom = mMap.getZoomLevelScaled();
				if (zoom > mMap.getTileSource().ZOOM_MAXLEVEL)
					rightText.setText("" + (mMap.getTileSource().ZOOM_MAXLEVEL + 1) + "+");
				else
					rightText.setText("" + (1 + Math.round(zoom)));
			}
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {

		if (isOnResume) {
			showThresholdLegend();
			final SharedPreferences pref = getPreferences(Activity.MODE_PRIVATE);

			if (mTileSource != null)
				mTileSource.Free();
			try {
				mTileSource = new TileSource(this, pref.getString(MAPNAME, TileSource.MAPNIK));
			} catch (RException e) {
				addMessage(e);
			}
			mMap.setTileSource(mTileSource);
			mMap.getController().setZoom(pref.getInt("ZoomLevel", 0));

			setTitle();

			fillOverlays();

			if (mTrackOverlay != null) {
				mTrackOverlay.setStopDraw(false);
			}

			// 设置屏幕是否长亮
			if (pref.getBoolean("pref_keepscreenon", false)) {
				myWakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
						.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "RMaps");
				myWakeLock.acquire();
			} else {
				myWakeLock = null;
			}
		}
		// 3.21 增加逻辑，进入Google地图，将室内地图对象清空
		MapFactory.getMapData().setMap(null);
		Ut.d("isOnResume:" + isOnResume);
		isOnResume = false;
		super.onResume();
	}

	@Override
	protected void onRestart() {
		if (mTrackOverlay != null) {
			mTrackOverlay.clearTrack();
		}
		super.onRestart();
	}

	@Override
	protected void onPause() {
		if (this.baseStationPop != null)
			this.baseStationPop.close();
		SharedPreferences uiState = getPreferences(Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = uiState.edit();
		editor.putString("MapName", mTileSource.ID);
		final GeoPoint point = mMap.getMapCenter();
		editor.putInt("Latitude", point.getLatitudeE6());
		editor.putInt("Longitude", point.getLongitudeE6());
		editor.putInt("ZoomLevel", mMap.getZoomLevel());
		editor.putString("app_version", Ut.getAppVersion(this));
		if (mPoiOverlay != null)
			editor.putInt("curShowPoiId", mPoiOverlay.getTapIndex());
		// mSearchResultOverlay.toPref(editor);
		editor.commit();

		if (myWakeLock != null)
			myWakeLock.release();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (this.baseStationPop != null)
			this.baseStationPop.close();
		for (TileViewOverlay osmvo : mMap.getOverlays())
			osmvo.Free();
		if (mTileSource != null) {
			mTileSource.Free();
			mTileSource = null;
		}
		mPoiManager.FreeDatabases();
		mMap.setMoveListener(null);
		DatasetManager.getInstance(this).addPointIndexChangeListener(this);
		RefreshEventManager.removeRefreshListener(this);
		this.unregisterReceiver(mReceiver);
		LogUtil.d(TAG, "onDestroy");
		super.onDestroy();
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * super.onCreateOptionsMenu(menu);
	 *
	 * MenuInflater inflater = getMenuInflater();
	 * inflater.inflate(R.menu.main_option_menu, menu); return true; }
	 */

	/*
	 * @Override public boolean onPrepareOptionsMenu(Menu menu) { Menu submenu =
	 * menu.findItem(R.id.mapselector).getSubMenu(); submenu.clear();
	 * SharedPreferences pref =
	 * PreferenceManager.getDefaultSharedPreferences(this);
	 *
	 * //获得地图存储目录 File folder = Ut.getRMapsMapsDir(this); if (folder.exists()) {
	 * File[] files = folder.listFiles(); if (files != null) for (int i = 0; i <
	 * files.length; i++) { if (files[i].getName().toLowerCase().endsWith(".mnm")
	 * || files[i].getName().toLowerCase().endsWith(".tar") ||
	 * files[i].getName().toLowerCase().endsWith(".sqlitedb")) { String name =
	 * Ut.FileName2ID(files[i].getName()); if (pref.getBoolean("pref_usermaps_" +
	 * name + "_enabled", false)) { MenuItem item =
	 * submenu.add(pref.getString("pref_usermaps_" + name + "_name",
	 * files[i].getName())); item.setTitleCondensed("usermap_" + name); } } } }
	 *
	 * final SAXParserFactory fac = SAXParserFactory.newInstance(); SAXParser
	 * parser = null; try { parser = fac.newSAXParser(); if(parser != null){ final
	 * InputStream in = getResources().openRawResource(R.raw.predefmaps);
	 * parser.parse(in, new PredefMapsParser(submenu, pref)); } } catch (Exception
	 * e) { e.printStackTrace(); }
	 *
	 * return super.onPrepareOptionsMenu(menu); }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		final GeoPoint point = mMap.getMapCenter();

		switch (item.getItemId()) {
			/*
			 * case (R.id.area_selector): startActivity(new Intent(this,
			 * AreaSelectorActivity.class).putExtra(MAPNAME,
			 * mTileSource.ID).putExtra("Latitude",
			 * point.getLatitudeE6()).putExtra("Longitude",
			 * point.getLongitudeE6()).putExtra("ZoomLevel", mMap.getZoomLevel()));
			 * return true;
			 */
			case (R.id.gpsstatus):
				startActivity(new Intent(GoogleMapMainActivity.this, Gps.class));
				return true;
			case (R.id.poilist):
				startActivityForResult((new Intent(this, PoiListActivity.class)).putExtra("lat", point.getLatitude())
						.putExtra("lon", point.getLongitude()).putExtra("title", "POI"), R.id.poilist);
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
				final String mapid = (String) item.getTitleCondensed();
				if (mTileSource != null)
					mTileSource.Free();
				try {
					mTileSource = new TileSource(this, mapid);
				} catch (RException e) {
					addMessage(e);
				}
				mMap.setTileSource(mTileSource);

				fillOverlays();

				setTitle();

				return true;
		}

	}

	private void addMessage(RException e) {

		LinearLayout msgbox = initLinearLayout(e.getID());
		if (msgbox == null) {
			msgbox = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.error_message_box,
					(ViewGroup) findViewById(R.id.message_list));
			msgbox.setId(e.getID());
		}
		msgbox.setVisibility(View.VISIBLE);
		((TextView) msgbox.findViewById(R.id.descr)).setText(e.getStringRes(this));
		msgbox.findViewById(R.id.message).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (v.findViewById(R.id.descr).getVisibility() == View.GONE)
					v.findViewById(R.id.descr).setVisibility(View.VISIBLE);
				else
					v.findViewById(R.id.descr).setVisibility(View.GONE);
			}
		});
		msgbox.findViewById(R.id.btn).setTag(Integer.valueOf(e.getID()));
		msgbox.findViewById(R.id.btn).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				final int id = (Integer) v.getTag();
				findViewById(id).setVisibility(View.GONE);
			}
		});
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
						menuStr = new String[] { getResources().getString(R.string.map_outdoor) };
					} else {
						menuStr = new String[]{getResources().getString(R.string.map_offline), getResources().getString(R.string.map_online)};
					}
				} else {
					menuStr = getResources().getStringArray(R.array.array_loading_map);
				}
				if (appModel.getSelectScene() == SceneType.Metro || appModel.getSelectScene() == SceneType.HighSpeedRail) {
					menuStr = new String[]{getResources().getString(R.string.map_scan), getResources().getString(R.string.map_outdoor), getResources().getString(R.string.map_offline), getResources().getString(R.string.map_online)};
				}
				if (appModel.getSelectScene() == SceneType.Manual && appModel.hasInnsmapTest()) {
					String[] menus = new String[menuStr.length + 1];
					menus[0] = getString(R.string.map_innsmap);
					for (int i = 0; i < menuStr.length; i++) {
						menus[i + 1] = menuStr[i];
					}
					menuStr = menus;
				}
				builder.setTitle(R.string.map_menu_title).setItems(menuStr, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String switchStr = menuStr[which]; // 当前选择item的名字
						if(switchStr.equals(getString(R.string.map_innsmap))
								|| switchStr.equals(getString(R.string.map_outdoor))
								|| switchStr.equals(getString(R.string.map_online))){
							SharePreferencesUtil.getInstance(WalktourApplication.getAppContext()).saveBoolean(com.walktour.gui.map.MapView.SP_IS_LOAD_INDOOR_MAP,false);
						}
						if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[0])) {
							if ((appModel.isTestJobIsRun() || appModel.isTestStoping()) && appModel.isGpsTest()) {
								Toast.makeText(GoogleMapMainActivity.this, getString(R.string.map_testRunAndGpsOpen_CantLoadScan),
										Toast.LENGTH_LONG).show();
							} else {
								((MapTabActivity) GoogleMapMainActivity.this.getParent()).swicthMap(WalkStruct.ShowInfoType.Map);
								TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.GoogleMap;
								ImageExplorer explorer = new ImageExplorer(GoogleMapMainActivity.this,
										com.walktour.gui.map.MapActivity.ACTION_LOAD_SCAN_MAP, com.walktour.gui.map.MapActivity.Map_File_Path,
										getResources().getStringArray(R.array.maptype_picture));
								explorer.start();
							}
						} else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[1])) {
							if ((appModel.isTestJobIsRun() || appModel.isTestStoping()) && appModel.isGpsTest()) {
								Toast.makeText(GoogleMapMainActivity.this, getString(R.string.map_testRunAndGpsOpen_CantLoadIndoor),
										Toast.LENGTH_LONG).show();
							} else {
								// 打开配置好的楼层地图
								Intent intent = new Intent(GoogleMapMainActivity.this, SysIndoor.class);
								intent.putExtra(SysIndoor.KEY_LOADING, true);
								getParent().startActivityForResult(intent, 10);
							}
						} else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[2])) {
							if ((appModel.isTestJobIsRun() || appModel.isTestStoping()) && appModel.isIndoorTest()) {
								Toast.makeText(GoogleMapMainActivity.this, getString(R.string.map_testRunAndIndoor_CantLoadMif),
										Toast.LENGTH_LONG).show();
							} else {
								((MapTabActivity) GoogleMapMainActivity.this.getParent()).swicthMap(WalkStruct.ShowInfoType.BaiduMap);
								TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
								new ImageExplorer(GoogleMapMainActivity.this, com.walktour.gui.map.MapActivity.ACTION_LOAD_MIF_MAP,
										com.walktour.gui.map.MapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_mif))
										.start();
							}
						} else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[3])) {
							((MapTabActivity) GoogleMapMainActivity.this.getParent()).swicthMap(WalkStruct.ShowInfoType.Map);
							TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.GoogleMap;
							new ImageExplorer(GoogleMapMainActivity.this, com.walktour.gui.map.MapActivity.ACTION_LOAD_TAB_MAP,
									com.walktour.gui.map.MapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_tab))
									.start();
						} else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[4])) {
							((MapTabActivity) getParent()).swicthMap(WalkStruct.ShowInfoType.OfflineMap);
						} else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[5])) {
							((MapTabActivity) getParent()).swicthMap(WalkStruct.ShowInfoType.OnlineMap);
						} else if (switchStr.equalsIgnoreCase(getString(R.string.map_innsmap))) {
							((MapTabActivity) getParent()).swicthMap(WalkStruct.ShowInfoType.InnsMap);
						}
					}
				});
				break;

		}
		return builder.create();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case R.id.menu_addpoi:
			case R.id.menu_editpoi:
				mPoiOverlay.UpdateList();
				mMap.postInvalidate();
				break;
			case R.id.poilist:
				/*
				 * if (resultCode == RESULT_OK) { PoiPoint point =
				 * mPoiManager.getPoiPoint(data.getIntExtra( "pointid",
				 * PoiPoint.EMPTY_ID())); if (point != null) { mPoiOverlay.UpdateList();
				 * mMap.getController().setCenter(point.GeoPoint); } } else {
				 * mPoiOverlay.UpdateList(); mMap.postInvalidate(); }
				 */
				break;
			case R.id.settings_activity_closed:
				finish();
				startActivity(new Intent(this, this.getClass()));
				break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private static class MyHandler extends Handler {
		private WeakReference<GoogleMapMainActivity> reference;

		public MyHandler(WeakReference<GoogleMapMainActivity> reference) {
			this.reference = reference;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(final Message msg) {
			GoogleMapMainActivity activity = reference.get();
			final int what = msg.what;
			switch (what) {
				case TIMER_TASK:
					activity.testTime.setText(TraceInfoInterface.traceData.getTestTimeHHmmss());
					activity.testDistance.setText(TraceInfoInterface.traceData.getTestMileageStr());
					activity.startTimer();
					break;
				case Ut.MAPTILEFSLOADER_SUCCESS_ID:
					activity.mMap.postInvalidate();
					break;
				case R.id.user_moved_map:
					// setAutoFollow(false);
					break;
				case R.id.set_title:
					activity.setTitle();
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

	private class MoveListener implements IMoveListener {

		public void onMoveDetected() {
		}

		public void onZoomDetected() {
			setTitle();
		}

	}

	private void ActionShowPoints(Intent queryIntent) {
		final ArrayList<String> locations = queryIntent.getStringArrayListExtra("locations");
		if (!locations.isEmpty()) {
			Ut.dd("Intent: " + ACTION_SHOW_POINTS + " locations: " + locations.toString());
			String[] fields = locations.get(0).split(";");
			String locns = "", title = "", descr = "";
			if (fields.length > 0)
				locns = fields[0];
			if (fields.length > 1)
				title = fields[1];
			if (fields.length > 2)
				descr = fields[2];

			GeoPoint point = GeoPoint.fromDoubleString(locns);
			mPoiOverlay.setGpsStatusGeoPoint(point, title, descr);
			mMap.getController().setCenter(point);
			saveLocusImage();
		}
	}

	/**
	 * 测试结束时保存地图轨迹图片
	 */
	private void saveLocusImage() {
		if (!TraceInfoInterface.isSaveFileLocus)
			return;
		this.mMap.getController().setZoom(10);
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!StringUtil.isNullOrEmpty(TraceInfoInterface.saveFileLocusPath)) {
					File file = new File(TraceInfoInterface.saveFileLocusPath);
					String picName = file.getName();
					picName = picName.substring(0, picName.lastIndexOf(".") + 1) + "locus";
					LogUtil.d(TAG, "saveFileLocusPath" + file.getParent() + File.separator + picName);
					saveViewToBMP(file.getParent(), picName);
					TraceInfoInterface.saveFileLocusPath = null;
				}
			}
		}.start();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.map_ranging:
				this.isRanging = !this.isRanging;
				this.baseDataOverlay.setRanging(isRanging);
				morePopupWindow.dismiss();
				break;
			case R.id.map_btn:
				showDialog(MapActivity.LOADING_MAP_DIALOG);
				break;
			case R.id.search_btn:
				// onSearchRequested();
				this.showBaseStatinPopView();
				break;
			case R.id.gps_btn:
				startActivity(new Intent(GoogleMapMainActivity.this, Gps.class));
				morePopupWindow.dismiss();
				break;
			case R.id.clear_btn:
				showClearDialog();
				break;
			case R.id.undo_btn:
				mMap.postInvalidate();
				break;
			case R.id.setting_btn:
				isOnResume = true;
				Intent intent = new Intent(GoogleMapMainActivity.this, SysMap.class);
				startActivity(intent);
				morePopupWindow.dismiss();
				break;
			case R.id.more_btn:
				showMorePopView();
				break;
			case R.id.station_import:
				this.stationImportPop.dismiss();
				importBaseData();
				break;
			case R.id.station_export:
				this.stationImportPop.dismiss();
				exportBaseData();
				break;
			case R.id.import_basedata:
				showStationImportPopView();
				break;
			case R.id.import_kml:
				intent = new Intent(new Intent(GoogleMapMainActivity.this, FileExplorer.class));
				Bundle bundle = new Bundle();
				bundle.putStringArray(FileExplorer.KEY_FILE_FILTER, new String[] { "kml" });
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
				final String[] fileTypes = new String[] { "JPEG", "BMP" };
				new BasicDialog.Builder(GoogleMapMainActivity.this.getParent()).setTitle(R.string.facebook_filetype)
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
								new ExplorerDirectory(GoogleMapMainActivity.this, fileTypes, EXPORT_MAP_ACTION_DIR, EXTRA_DIR).start();
								morePopupWindow.dismiss();
								dialog.dismiss();
							}
						}).show();
				break;
			default:
				break;
		}

	}

	private void exportBaseData() {
		final String[] fileTypes = new String[] { "TXT", "XLS", "KML", "MIF" };
		new BasicDialog.Builder(GoogleMapMainActivity.this.getParent()).setTitle(R.string.facebook_filetype)
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
						new ExplorerDirectory(GoogleMapMainActivity.this, fileTypes, EXPORT_BASE_ACTION_DIR, EXTRA_DIR).start();
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * 导入基站数据
	 */
	private void importBaseData() {
		new LicenseExplorer(GoogleMapMainActivity.this, new String[] { "txt", "xls", "kml", "mif" },
				LicenseExplorer.LOADING_BASE_DATA, BaseStation.MAPTYPE_OUTDOOR).start();
		baseDataOverlay.setmNeedUpdateList(true);
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
	 * 显示基站搜索窗
	 */
	private void showBaseStatinPopView() {
		if (this.baseStationPop == null) {
			View map = this.mMap;
			this.baseStationPop = new BaseStationSearchPopWindow(map, this.getParent(), map.getMeasuredWidth(),
					map.getMeasuredHeight() / 2);
			int[] location = new int[2];
			this.getWindow().getDecorView().getLocationOnScreen(location);
			this.baseStationPop.setLocation(location);
		}
		this.baseStationPop.show();
	}

	public void initAutoFollowMode() {
		int mode = mSharedPreferences.getInt(AUTO_FOLLOW_MODE, 0);
		switch (mode) {
			case 0:
				autoFollowBtn.setImageResource(R.drawable.location_item);
				break;
			case 1:
				autoFollowBtn.setImageResource(mChinease ? R.drawable.main_icon_follow : R.drawable.main_icon_follow_en);
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
				autoFollowBtn.setImageResource(mChinease ? R.drawable.main_icon_follow : R.drawable.main_icon_follow_en);
				break;
			case 1:
				if (appModel.isTestJobIsRun()) {
					mSharedPreferences.edit().putInt(AUTO_FOLLOW_MODE, 0).commit();
					autoFollowBtn.setImageResource(R.drawable.location_item);
				} else {
					mSharedPreferences.edit().putInt(AUTO_FOLLOW_MODE, 2).commit();
					autoFollowBtn.setImageResource(R.drawable.main_icon_mark);
				}
				break;
			case 2:
				mSharedPreferences.edit().putInt(AUTO_FOLLOW_MODE, 0).commit();
				autoFollowBtn.setImageResource(R.drawable.location_item);
				TraceInfoInterface.traceData.getGpsLocas().clear();
				break;
			default:
				break;
		}
	}

	/**
	 * 显示More Pop<BR>
	 * 点击More弹出POP展示可选项
	 */
	@SuppressLint("InflateParams")
	public void showMorePopView() {
		if (morePopupWindow == null) {
			View morePopView = LayoutInflater.from(this).inflate(R.layout.googlemap_more_pop, null);
			morePopView.findViewById(R.id.map_ranging).setOnClickListener(this);
			morePopView.findViewById(R.id.setting_btn).setOnClickListener(this);
			morePopView.findViewById(R.id.gps_btn).setOnClickListener(this);
			morePopView.findViewById(R.id.map_export).setOnClickListener(this);
			morePopView.findViewById(R.id.import_kml).setOnClickListener(this);
			float density = this.getResources().getDisplayMetrics().density;
			morePopupWindow = new PopupWindow(morePopView, (int) (150 * density), (int) (265 * density), true);
			morePopupWindow.setFocusable(true);
			morePopupWindow.setTouchable(true);
			morePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.task_more_pop_bg));
		}
		int[] location = new int[2];
		this.moreBtn.getLocationOnScreen(location);
		int height = this.morePopupWindow.getHeight();
		View mapRanging = this.morePopupWindow.getContentView().findViewById(R.id.map_ranging);
		if (this.isRanging) {
			mapRanging.setBackgroundColor(getResources().getColor(R.color.base_list_item_bg_select));
		} else {
			mapRanging.setBackgroundResource(R.drawable.base_list_toolbar_bg);
		}
		this.morePopupWindow.showAtLocation(this.moreBtn, Gravity.NO_GRAVITY, location[0], location[1] - height);
	}

	/**
	 * [功能详细描述]
	 */
	public void showClearDialog() {
		new BasicDialog.Builder(GoogleMapMainActivity.this.getParent()).setIcon(android.R.drawable.ic_menu_delete)
				.setTitle(R.string.map_clean)
				.setItems(new String[] { getResources().getString(R.string.clear_basedata),
						getResources().getString(R.string.clear_marker), getResources().getString(R.string.clear_navigation_path),
						getResources().getString(R.string.clear_all) }, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								BaseStationDBHelper.getInstance(GoogleMapMainActivity.this.getApplicationContext()).clearAllData();
								baseDataOverlay.clearSimplifyBaseDatas();
								mMap.postInvalidate();
								break;
							case 1:
								TraceInfoInterface.traceData.getGpsLocas().clear();
								mMap.postInvalidate();
								break;
							case 2:
								mPoiManager.deleteAllTrack();
								mTrackOverlay.setStopDraw(false);
								mTrackOverlay.clearTrack();
								break;
							case 3:
								BaseStationDBHelper.getInstance(GoogleMapMainActivity.this.getApplicationContext()).clearAllData();
								baseDataOverlay.clearSimplifyBaseDatas();
								TraceInfoInterface.traceData.getGpsLocas().clear();
								mPoiManager.deleteAllTrack();
								mTrackOverlay.setStopDraw(false);
								mTrackOverlay.clearTrack();
								mMap.postInvalidate();
								break;

							default:
								break;
						}

					}
				})
				.show();
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
				if (System.currentTimeMillis() - GpsInfo.getInstance().getGpsLastChangeTime() > 3000) {
					// Logger.d(TAG, "收到GPS,大于3秒，刷新界面");
					locationNewLatlng(locat);
					GpsInfo.getInstance().setGpsLastChangeTime(System.currentTimeMillis());
				} /*
				 * else { Logger.d(TAG, "收到GPS,小于3秒，不刷新界面"); }
				 */
			} else if (intent.getAction().equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_MAP_COLOR_CHANGE)) {
				LogUtil.v("MapChange", "---Change---");
				findViewById(R.id.threshold_view).invalidate();
				// setValue();
			} else if (intent.getAction().equals(WalkMessage.ACTION_MAP_IMPORT_KML)) {
				mTrackOverlay.clearTrack();
				String path = intent.getStringExtra(WalkMessage.KEY_MAP_KML_PATH);
				doImportTrack(path);
				mTrackOverlay.setStopDraw(false);
			} else if (intent.getAction().equals(EXPORT_MAP_ACTION_DIR)) {// 导出地图到指定目录
				String path = intent.getExtras().getString(EXTRA_DIR);
				saveViewToBMP(path, null);
			} else if (intent.getAction().equals(EXPORT_BASE_ACTION_DIR)) {// 导出基站数据到指定目录
				String path = intent.getExtras().getString(EXTRA_DIR);
				saveBaseToFile(path);
			}
		}
	};

	/**
	 * 保存基站数据到指定文件
	 *
	 * @param path
	 *          文件路径
	 */
	private void saveBaseToFile(final String path) {
		progress = ProgressDialog.show(this.getParent(), getString(R.string.map_export), getString(R.string.map_exporting),
				true);
		new Thread() {
			@Override
			public void run() {
				BaseStationExportFactory.getInstance().exportFile(GoogleMapMainActivity.this, path, fileType);
				mHandler.sendEmptyMessage(EXPORT_BASE_END);
			}
		}.start();
	}

	/**
	 * 生成图片名称
	 *
	 * @return
	 */
	private String getPicName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());
		return format.format(Calendar.getInstance(Locale.getDefault()).getTime());
	}

	/**
	 * 保存视图为文件
	 *
	 * @param path
	 *          存放目录
	 */
	private void saveViewToBMP(String path, String picName) {
		LinearLayout lineTool = initLinearLayout(R.id.LineraLayoutToolbar);
		lineTool.setVisibility(View.GONE);
		View view = this.getWindow().getDecorView();
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		lineTool.setVisibility(View.VISIBLE);
		if (bitmap == null) {
			return;
		}
		if (picName == null)
			picName = this.getPicName();
		Bitmap basemap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(basemap);
		canvas.drawBitmap(bitmap, 0, 0, null);
		ImageUtil.saveBitmapToFile(path, basemap, picName, this.picFileType);
		Toast.makeText(getApplicationContext(), R.string.map_export_success, Toast.LENGTH_SHORT).show();
		if (TraceInfoInterface.isSaveFileLocus) {
			TraceInfoInterface.isSaveFileLocus = false;
			ActivityManager.removeLast();
		}
	}

	/**
	 * 导入导航路径<BR>
	 * [功能详细描述]
	 *
	 * @param mFileName
	 */
	private void doImportTrack(final String mFileName) {
		File file = new File(mFileName);

		if (!file.exists()) {
			Toast.makeText(this, "No such file", Toast.LENGTH_LONG).show();
			return;
		}
		// showDialog(R.id.dialog_wait);

		this.mThreadPool.execute(new Runnable() {
			public void run() {
				File file = new File(mFileName);

				SAXParserFactory fac = SAXParserFactory.newInstance();
				SAXParser parser = null;
				try {
					parser = fac.newSAXParser();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				}
				if (parser != null) {
					mPoiManager.deleteAllTrack();
					mPoiManager.beginTransaction();
					Ut.dd("Start parsing file " + file.getName());
					try {
						if (FileUtils.getExtension(file.getName()).equalsIgnoreCase(".kml")) {
							parser.parse(file, new KmlTrackParser(mPoiManager));
						} else if (FileUtils.getExtension(file.getName()).equalsIgnoreCase(".gpx")) {
							parser.parse(file, new GpxTrackParser(mPoiManager));
						}
						mPoiManager.commitTransaction();
					} catch (SAXException e) {
						e.printStackTrace();
						mPoiManager.rollbackTransaction();
					} catch (IOException e) {
						e.printStackTrace();
						mPoiManager.rollbackTransaction();
					} catch (IllegalStateException e) {
					} catch (OutOfMemoryError e) {
						Ut.w("OutOfMemoryError");
						mPoiManager.rollbackTransaction();
					}
					Ut.dd("Pois commited");
				}
			};
		});

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @see com.walktour.framework.view.OnTabActivityResultListener#onTabActivityResult(int,
	 *      int, android.content.Intent)
	 */

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case OFFILEMAP_RESULT_CODE:
				isOnResume = false;
				/*
				 * final SharedPreferences aPref =
				 * PreferenceManager.getDefaultSharedPreferences(this); final Editor
				 * editor = aPref.edit(); editor.putString("pref_dir_maps",
				 * Ut.getExternalStorageDirectory()+"/walktour/maps/guangdong/");
				 * editor.commit();
				 */
				String mapid = data.getStringExtra("mapid");
				if (!StringUtil.isNullOrEmpty(mapid)) {
					if (mTileSource != null)
						mTileSource.Free();
					try {
						mTileSource = new TileSource(GoogleMapMainActivity.this, mapid);
						SharedPreferences uiState = getPreferences(Activity.MODE_PRIVATE);
						SharedPreferences.Editor editor = uiState.edit();
						editor.putString("MapName", mTileSource.ID);
						final GeoPoint point = mMap.getMapCenter();
						editor.putInt("Latitude", point.getLatitudeE6());
						editor.putInt("Longitude", point.getLongitudeE6());
						editor.putInt("ZoomLevel", mMap.getZoomLevel());
						editor.putString("app_version", Ut.getAppVersion(this));
						editor.commit();
					} catch (RException e) {
						addMessage(e);
					}
					mMap.setTileSource(mTileSource);

					fillOverlays();
					setTitle();
				}
				break;
			case GoogleMapMainActivity.ONLINEMAP_RESULT_CODE:
				isOnResume = false;
				try {
					if (mTileSource != null)
						mTileSource.Free();
					mTileSource = new TileSource(GoogleMapMainActivity.this, TileSource.MAPNIK);
					mMap.setTileSource(mTileSource);
					SharedPreferences pref = getPreferences(Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();
					editor.putString("MapName", mTileSource.ID);
					editor.commit();
					mMap.getController().setZoom(pref.getInt("ZoomLevel", 0));
					setTitle();
					fillOverlays();
				} catch (SQLiteException e) {
				} catch (RException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
	}

	/**
	 * 显示参数图例<BR>
	 * [功能详细描述]
	 */
	@SuppressWarnings("deprecation")
	private void showThresholdLegend() {
		ThresholdView thresholdView = (ThresholdView) findViewById(R.id.threshold_view);
		DisplayMetrics metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		if (ParameterSetting.getInstance().isDisplayLegen()) {
			List<Parameter> parameterList = ParameterSetting.getInstance()
					.getCheckedParamertersByNet(MyPhoneState.getInstance().getCurrentNetForParam(this));
			Parameter parameter = parameterList.size() > 0 ? parameterList.get(0) : null;
			if (parameter == null) {
				return;
			}
			if (parameter.getThresholdList().size() > 0) {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
						(int) (15 * metric.density));
				lp.addRule(RelativeLayout.ABOVE, R.id.LineraLayoutToolbar);
				thresholdView.setLayoutParams(lp);
				thresholdView.setVisibility(View.VISIBLE);
			} else {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
						(int) (45 * metric.density));
				lp.addRule(RelativeLayout.ABOVE, R.id.LineraLayoutToolbar);
				thresholdView.setLayoutParams(lp);
			}

		} else {
			thresholdView.setVisibility(View.GONE);
		}
		thresholdView.invalidate();
		thresholdView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
	}

	/**
	 * 开始时间计时器<BR>
	 * [功能详细描述]
	 */
	private void startTimer() {
		if (appModel.isTestJobIsRun()) {
			mHandler.sendEmptyMessageDelayed(TIMER_TASK, 1000);
			findViewById(R.id.test_info).setVisibility(View.VISIBLE);
		}
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 *
	 * @param pointIndex
	 * @see com.dinglicom.dataset.logic.PointIndexChangeLinstener#onPointIndexChange(int)
	 */
	@Override
	public void onPointIndexChange(int pointIndex, boolean isProgressChange) {
		mMap.postInvalidate();
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 *
	 * @param refreshType
	 * @param object
	 * @see com.walktour.framework.view.RefreshEventManager.RefreshEventListener#onRefreshed(com.walktour.framework.view.RefreshEventManager.RefreshType,
	 *      java.lang.Object)
	 */
	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		switch (refreshType) {
			case REFRSH_GOOGLEMAP_BASEDATA:
				BaseStation baseData = (BaseStation) object;
				this.baseDataOverlay.setSearchBaseData(baseData);
				this.markerPointOverlay.setSearchBaseData(baseData);
				mMap.getController().setZoom(16);
				mMap.getController().setCenter(new GeoPoint((int) (baseData.latitude * 1e6), (int) (baseData.longitude * 1e6)));
				break;

			default:
				break;
		}

	}
}
