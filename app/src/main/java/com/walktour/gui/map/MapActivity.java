package com.walktour.gui.map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dinglicom.dataset.DatasetManager;
import com.innsmap.InnsMap.net.http.listener.forout.NetMapLoadListener;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ImageUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.OnTabActivityResultListener;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.applet.ControlPanel;
import com.walktour.gui.applet.ExplorerDirectory;
import com.walktour.gui.applet.ImageExplorer;
import com.walktour.gui.applet.LicenseExplorer;
import com.walktour.gui.newmap.basestation.BaseStationSearchPopWindow;
import com.walktour.gui.newmap2.IMapTab;
import com.walktour.gui.setting.SysIndoor;
import com.walktour.gui.setting.SysMap;
import com.walktour.model.HistoryPoint;
import com.walktour.model.Parameter;
import com.walktour.service.automark.AutoMarkManager;
import com.walktour.service.automark.constant.AutoMarkConstant;
import com.walktour.service.automark.constant.MarkScene;
import com.walktour.service.automark.glonavin.ConfirmStartAutoPointDialogFragment;
import com.walktour.service.automark.glonavin.GlonavinAutoMarkService;
import com.walktour.service.automark.glonavin.GlonavinDataManager;
import com.walktour.service.automark.glonavin.GlonavinSetDirectionTipDialogFragment;
import com.walktour.service.automark.glonavin.eventbus.OnBubblingValidPointEvent;
import com.walktour.service.automark.glonavin.eventbus.ShowConfirmStartAutoMarkDialogEvent;
import com.walktour.service.innsmap.InnsmapFactory;
import com.walktour.service.innsmap.model.InnsmapModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * ??????Activity??? Author:Zhengmin Create Time:2010/4/16 --twq1115
 * ????????????????????????????????????????????????????????????
 */
@SuppressLint({"InflateParams", "ClickableViewAccessibility"})
public class MapActivity extends BasicActivity implements OnTabActivityResultListener {
    private final String TAG = "Map";
    /**
     * ??????????????????
     */
    public static final String Map_File_Path = "Map_File_Path";
    /**
     * ????????????
     */
    public static final String ACTION_MAP_ZOOM = "Zoom_Action";
    /**
     * ????????????
     */
    public static final String ACTION_MAP_EXPORT = "Map_Export";
    /**
     * ????????????????????????
     */
    public static final String ACTION_SHOW_LOAD_MAP_MENU = "ActionLoadMap";
    /**
     * ??????????????????
     */
    public static final String ACTION_SHOW_MORE_MENU = "ActionMore";
    /**
     * ??????
     */
    public static final String ACTION_DRAW_POINT = "DrawPoint_Action";
    /**
     * ????????????
     */
    public static final String ACTION_CANCEL_POINT = "CancelPoint_Action";
    /**
     * ??????
     */
    public static final String ACTION_DRAW_LINE = "DrawLine_Action";
    /**
     * ??????????????????
     */
    public static final String ACTION_LOAD_SCAN_MAP = "LoadScanMap_Action";
    /**
     * ????????????
     */
    public static final String ACTION_CLEAR_ALL = "ClearAll_Action";
    /**
     * ??????MIF??????
     */
    public static final String ACTION_LOAD_MIF_MAP = "LoadMifMap_Action";
    /**
     * ??????????????????
     */
    public static final String ACTION_LOAD_BASE_DATA = "LoadBaseData_Action";
    /**
     * ????????????
     */
    public static final String ACTION_LOAD_BASE_DATA_APPEND = "LoadBaseData_Action_Append";
    /**
     * ????????????
     */
    public static final String ACTION_LOCATION = "Location_Action";
    /**
     * ??????????????????
     */
    public static final String ACTION_LOAD_PAINT_MAP = "LoadPaintMap_Action";
    /**
     * ??????????????????
     */
    public static final String ACTION_LOAD_INDOOR_MAP = "LoadIndoorMap_Action";
    /**
     * ??????TAB??????
     */
    public static final String ACTION_LOAD_TAB_MAP = "LoadTabMap_Action";
    /**
     * ????????????
     */
    public static final String SWITCH_MAP = "Switch_Map";
    /**
     * ????????????
     */
    public static final String MOVE_TO_CENTER = "movecenter";
    /**
     * ????????????
     */
    public static final String ACTION_CLEAR_STATION = "ClearStation_Action";
    /**
     * ????????????
     */
    public static final String ACTION_CLEAR_POINT = "ClearPoint_Action";
    /**
     * ????????????
     */
    public static final String ACTION_CLEAR_MAP = "ClearMap_Action";
    /**
     * ??????????????????
     */
    public static final String ACTION_MAP_COLOR_CHANGE = "MapColor_Action";
    /**
     * ???????????????????????????
     */
    private static final int AUTO_MARK_ORIENGATION_CHANGE = 11;
    /**
     * ????????????????????????
     */
    private static final int AUTO_MARK_HEIGHT_CHANGE = 12;
    /**
     * ????????????????????????
     */
    private static final int AUTO_MARK_STEP_CHANGE = 13;
    /**
     * ?????????????????????
     */
    private boolean isSetPoint = false;
    /**
     * ?????????????????????
     */
    private boolean isDrawline = false;
    // private Context con = this;
    // private double nullValue = -9999;
    /**
     * ???????????????????????????
     */
    private boolean isRegisterReceiver = false;
    // private GpsInfo gpsInfo = GpsInfo.getInstance();

    // private TextView textPointInfo;
    // private LinearLayout lineTool;
    /**
     * ?????????
     */
    private ControlBar controlbar;
    // private Button btnBack ;
    /**
     * ????????????
     */
    private Button btnPoint;
    /**
     * ????????????
     */
    private Button btnMore;
    /**
     * ????????????
     */
    private MapView mapView;
    /**
     * ??????????????????
     */
    private InnsMapView innsMapView;
    /**
     * ???????????????????????????
     */
    private InnsmapFactory mInnsmapFactory;
    /**
     * ????????????
     */
    private ApplicationModel appModel;
    // ???????????????
    // private FrameLayout framLayout ;
    /**
     * ??????????????????
     */
    private ControlPanel controlPanel;
    // private TextView txtInfo;
    /**
     * ????????????ID
     */
    public final static int LOADING_MAP_DIALOG = 1000;
    /**
     * ?????????????????????
     */
    private PopupWindow morePopupWindow;
    /**
     * ???????????????????????????
     */
    private boolean isMoving = false;
    /**
     * ????????????
     */
    private static int MSG_MOVE = 1;
    /**
     * ????????????????????????
     */
    private String[] menuStr = null;
    /**
     * ????????????
     */
    private final String ACTION_DIR = "com.walktour.Map.choosePath";
    /**
     * ????????????
     */
    private final String EXTRA_DIR = "dir";
    /**
     * ?????????????????????
     */
    private ImageUtil.FileType fileType;
    /**
     * ????????????????????????
     */
    private TextView leftText;
    /**
     * ????????????????????????
     */
    private TextView centerText;
    /**
     * ????????????????????????
     */
    private TextView rightText;
    private TextView tvInitInfo;
    /**
     * ????????????????????????
     */
    private boolean isShowInnsMap = false;
    private BaseStationSearchPopWindow baseStationPop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        this.mInnsmapFactory = InnsmapFactory.getInstance(this);
//        this.isShowInnsMap = this.getIntent().getBooleanExtra("showInnsMap", false);
        this.isShowInnsMap = false;//???????????????????????????????????????
        if (this.isShowInnsMap) {
            setContentView(R.layout.activity_innsmap);
        } else
            setContentView(R.layout.mapview);
        appModel = ApplicationModel.getInstance();
        isAutoPointTest=appModel.isGlonavinTest()&& AutoMarkConstant.markScene == MarkScene.COMMON;
        if (this.isShowInnsMap) {
            this.innsMapView = (InnsMapView) this.findViewById(R.id.view_inns_map);
            this.loadInnsMap();
            registerForContextMenu(this.innsMapView);
        } else {
            this.mapView = (MapView) findViewById(R.id.view_map);
            registerForContextMenu(mapView);
        }

        LogUtil.w(TAG, "----oncreate---");
        IntentFilter filter = new IntentFilter();

        filter.addAction(com.walktour.gui.map.CustomButton.HIDE_MENU);
        filter.addAction(com.walktour.gui.map.MapActivity.ACTION_MAP_COLOR_CHANGE);
        filter.addAction(com.walktour.gui.map.MapActivity.SWITCH_MAP);
        filter.addAction(ControlPanel.ACTION_MARK);
        filter.addAction(ACTION_DIR);
        filter.addAction(WalkMessage.AUTOMARK_BUILDER_HEIGHT);
        filter.addAction(WalkMessage.AUTOMARK_ORIENTATION);
        filter.addAction(WalkMessage.AUTOMARK_TOTAL_STEPS);
        this.registerReceiver(mIntentReceiver, filter);
        isRegisterReceiver = true;
        ImageButton zoominBtn = initImageButton(R.id.zoominbtn);
        ImageButton zoomoutBtn = initImageButton(R.id.zoomoutbtn);
        ImageButton locationBtn = initImageButton(R.id.locationbtn);
        zoominBtn.setOnClickListener(myListener);
        zoomoutBtn.setOnClickListener(myListener);
        locationBtn.setOnClickListener(myListener);
        LinearLayout autoMark = (LinearLayout) this.findViewById(R.id.auto_mark_message);
        if (MapFactory.getMapData().isAutoMark())
            autoMark.setVisibility(View.VISIBLE);
        this.leftText = (TextView) this.findViewById(R.id.left_text);
        this.centerText = (TextView) this.findViewById(R.id.center_text);
        this.rightText = (TextView) this.findViewById(R.id.right_text);
        this.tvInitInfo=(TextView)this.findViewById(R.id.tv_init_info);
        genControlBar();
    }
    /**
     * ?????????????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateInfo(String msg) {
        if (appModel.isGlonavinTest()) {
            tvInitInfo.setText("" + msg);
        }
    }
    /**
     * ??????????????????
     */
    protected void loadInnsMap() {
        InnsmapModel floor = this.mInnsmapFactory.getCurrentFloor(DatasetManager.isPlayback);
        InnsmapModel building = this.mInnsmapFactory.getCurrentBuilding();
        if (floor != null) {
            this.innsMapView.loadMap(building.getId(), floor.getId(), new NetMapLoadListener() {

                @Override
                public void onSuccess() {
                    LogUtil.d(TAG, "----InnsMapView loadMap Success----");
                }

                @Override
                public void onFail(String arg0) {
                    LogUtil.d(TAG, "----InnsMapView loadMap Fail----:" + arg0);

                }
            });
        }
    }

    /**
     * ???????????????
     **/
    @SuppressWarnings("deprecation")
    public void genControlBar() {
        LogUtil.w(TAG, "--gen tab");
        controlbar = (ControlBar) findViewById(R.id.LineraLayoutToolbar);
        controlPanel = (ControlPanel) findViewById(R.id.ControlPanel);
        // framLayout = (FrameLayout) findViewById( R.id.FrameLayout01 );
        // lineTool = (LinearLayout)findViewById(R.id.linearTool);
        // lineTool.setBackgroundColor(R.color.map_bar);
        // btnBack = (Button) controlbar.getButton(0,Color.WHITE);
        // txtInfo = initTextView(R.id.textViewInfo);
        Button btnLoad = controlbar.getButton(1);
        Button btnRestore = controlbar.getButton(2);
        Button btnUndo = controlbar.getButton(3);
        btnPoint = controlbar.getButton(4);
        btnMore = controlbar.getButton(5);
        if (MapFactory.isLoadMIF()){
            btnLoad.setText(R.string.map_bar_load);
            btnRestore.setText(R.string.map_bar_clear);
            btnUndo.setText(R.string.map_base);
            btnPoint.setText(R.string.str_search);
            btnMore.setText(R.string.str_more);
        }else {
            btnLoad.setText(R.string.map_bar_load);
            btnRestore.setText(R.string.str_restore);
            btnUndo.setText(R.string.map_bar_undo);
            btnPoint.setText(R.string.map_bar_point);
            btnMore.setText(R.string.str_more);
        }

        // ??????????????????????????????
        if (DatasetManager.isPlayback) {
            btnLoad.setEnabled(false);
            btnRestore.setEnabled(false);
            btnUndo.setEnabled(false);
            btnPoint.setEnabled(false);
            btnMore.setEnabled(false);
            LogUtil.w(TAG, "-- btnMode can't click1");
        }
		/*if (this.isShowInnsMap) {
			btnRestore.setEnabled(false);
			btnUndo.setEnabled(false);
			btnPoint.setEnabled(false);
		}*/
        drawbtnPoint();
        // btnBack.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(
        // R.drawable.controlbar_backward ), null, null);
        if (MapFactory.isLoadMIF()){
            btnLoad.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.selector_controlbar_map), null,
                    null);
            btnRestore.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_clear),
                    null, null);
            btnUndo.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_station), null,
                    null);
            btnPoint.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_search), null,
                    null);
            btnMore.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_more), null,
                    null);
        }else {
            btnLoad.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.selector_controlbar_map), null,
                    null);
            btnRestore.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_station),
                    null, null);
            btnUndo.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_clear), null,
                    null);
            btnPoint.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_point), null,
                    null);
            btnMore.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_more), null,
                    null);
        }


        controlbar.setButtonsListener(btnListener);
        controlPanel.setButtonListener(btnListener);
        controlPanel.setButtonPressListener(onTouchListener);
        if (!appModel.isInOutSwitchMode()&&(appModel.isTestJobIsRun() || appModel.isTestStoping()) && (appModel.isGpsTest() || appModel.isGyroTest())) {
            btnPoint.setBackgroundDrawable(null);
            MapFactory.getMapData().setSetPoint(false);
            btnPoint.setEnabled(false);
            btnRestore.setEnabled(false);
            btnUndo.setEnabled(false);
            LogUtil.w(TAG, "here is gone");
        }
        this.initMorePopView();
    }

    /**
     * ???????????????????????????
     */
    @SuppressWarnings("deprecation")
    private void initMorePopView() {
        if (morePopupWindow == null) {
            View morePopView = LayoutInflater.from(this).inflate(R.layout.cqtmap_more_pop, null);
            morePopView.findViewById(R.id.import_basedata).setOnClickListener(this);
            morePopView.findViewById(R.id.map_clean_all).setOnClickListener(this);
            morePopView.findViewById(R.id.setting_btn).setOnClickListener(this);
            morePopView.findViewById(R.id.map_export).setOnClickListener(this);
            morePopView.findViewById(R.id.map_plotting_scale_btn).setOnClickListener(this);
            morePopView.findViewById(R.id.map_switch_point).setOnClickListener(this);
            if (!isAutoPointTest){
                morePopView.findViewById(R.id.map_switch_point).setVisibility(View.GONE);
            }
            float density = this.getResources().getDisplayMetrics().density;
            morePopupWindow = new PopupWindow(morePopView, (int) (150 * density), (int) (265 * density), true);
            morePopupWindow.setFocusable(true);
            morePopupWindow.setTouchable(true);
            morePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
    }

    /**
     * ??????More Pop<BR>
     * ??????More??????POP???????????????
     */
    private void showMorePopView() {
        int[] location = new int[2];
        this.btnMore.getLocationOnScreen(location);
        this.morePopupWindow.showAsDropDown(this.btnMore, 10, 10);
    }

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.ButtonLeft:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        isMoving = true;
                        new Thread(new ThreadMovePoint(ThreadMovePoint.DIRECTION_LEFT)).start();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        isMoving = false;
                    }
                    break;

                case R.id.ButtonRight:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        isMoving = true;
                        new Thread(new ThreadMovePoint(ThreadMovePoint.DIRECTION_RIGHT)).start();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        isMoving = false;
                    }
                    break;

                case R.id.ButtonUp:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        isMoving = true;
                        new Thread(new ThreadMovePoint(ThreadMovePoint.DIRECTION_TOP)).start();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        isMoving = false;
                    }
                    break;

                case R.id.ButtonDown:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        isMoving = true;
                        new Thread(new ThreadMovePoint(ThreadMovePoint.DIRECTION_BOTTOM)).start();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        isMoving = false;
                    }
                    break;
            }
            return false;
        }
    };

    /**
     * ?????????????????????
     *
     * @author jianchao.wang
     */
    private class ThreadMovePoint implements Runnable {
        public final static int DIRECTION_LEFT = 0;
        public final static int DIRECTION_TOP = 1;
        public final static int DIRECTION_RIGHT = 2;
        public final static int DIRECTION_BOTTOM = 3;
        private int direction = -1;

        public ThreadMovePoint(int direction) {
            this.direction = direction;
        }

        @Override
        public void run() {
            int i = 0;
            // ?????????1???
            for (int t = 0; t < 6 && isMoving; t++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            while (isMoving) {
                if (i % 2 == 0) {
                    mHandler.obtainMessage(MSG_MOVE, direction, 0).sendToTarget();
                }
                i++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<MapActivity> reference;

        public MyHandler(MapActivity activity) {
            this.reference = new WeakReference<MapActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MapActivity activity = this.reference.get();
            if (msg.what == MSG_MOVE) {
                int direction = msg.arg1;
                Intent intent;
                switch (direction) {
                    case ThreadMovePoint.DIRECTION_LEFT:
                        intent = new Intent(ControlPanel.ACTION_CONTROL);
                        intent.putExtra(ControlPanel.KEY_CONTROL, ControlPanel.CONTROL_LEFT);
                        activity.sendBroadcast(intent);
                        break;

                    case ThreadMovePoint.DIRECTION_TOP:
                        intent = new Intent(ControlPanel.ACTION_CONTROL);
                        intent.putExtra(ControlPanel.KEY_CONTROL, ControlPanel.CONTROL_UP);
                        activity.sendBroadcast(intent);
                        break;

                    case ThreadMovePoint.DIRECTION_RIGHT:
                        intent = new Intent(ControlPanel.ACTION_CONTROL);
                        intent.putExtra(ControlPanel.KEY_CONTROL, ControlPanel.CONTROL_RIGHT);
                        activity.sendBroadcast(intent);
                        break;

                    case ThreadMovePoint.DIRECTION_BOTTOM:
                        intent = new Intent(ControlPanel.ACTION_CONTROL);
                        intent.putExtra(ControlPanel.KEY_CONTROL, ControlPanel.CONTROL_DOWN);
                        activity.sendBroadcast(intent);
                        break;
                }
            } else if (msg.what == AUTO_MARK_ORIENGATION_CHANGE) {
                int oriengation = (Integer) msg.obj;
                activity.leftText.setText(activity.getString(R.string.map_base_azimuth) + ":" + oriengation); // "??"
            } else if (msg.what == AUTO_MARK_HEIGHT_CHANGE) {
                int height = (Integer) msg.obj;
                activity.centerText.setText(activity.getString(R.string.map_base_high) + ":" + height + "m");
            } else if (msg.what == AUTO_MARK_STEP_CHANGE) {
                int steps = (Integer) msg.obj;
                activity.rightText.setText(activity.getString(R.string.map_base_stepcount) + ":" + steps);
            }
        }
    }

    ;

    private OnClickListener btnListener = new OnClickListener() {
        @SuppressLint("NewApi")
        @SuppressWarnings("deprecation")
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.Button01:
                    finish();
                    break;
                case R.id.Button02: // ????????????
                    /*
                     * intent = new Intent(); intent.setAction(MyActionLoad);
                     * sendBroadcast(intent);
                     */
                    controlPanel.setVisibility(View.GONE);
                    // ???????????????????????????????????????????????????
                    if (!MapFactory.getMapData().getPointStatusStack().isEmpty()) {
                        if (MapFactory.getMapData().getPointStatusStack().lastElement()
                                .getStatus() == PointStatus.POINT_STATUS_CALIBRATION) {
                            MapFactory.getMapData().getPointStatusStack().pop();
                            mapView.invalidate();
                        }

                    }
                    MapFactory.getMapData().setZoomGrade(10);

                    showDialog(MapActivity.LOADING_MAP_DIALOG);
                    break;
                case R.id.Button03: // ????????????
                    if (MapFactory.isLoadMIF()){
                        controlPanel.setVisibility(View.GONE);
                        // ???????????????????????????????????????????????????
                        if (!MapFactory.getMapData().getPointStatusStack().isEmpty()) {
                            if (MapFactory.getMapData().getPointStatusStack().lastElement()
                                    .getStatus() == PointStatus.POINT_STATUS_CALIBRATION) {
                                MapFactory.getMapData().getPointStatusStack().pop();
                                mapView.invalidate();
                            }

                        }
                        new BasicDialog.Builder(MapActivity.this.getParent()).setIcon(android.R.drawable.ic_menu_delete)
                                .setTitle(R.string.map_clean)
                                .setItems(getResources().getStringArray(R.array.clearOpition), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = null;
                                        switch (which) {
                                            case 0:
                                                if (appModel.isTestJobIsRun()) {
                                                    Toast.makeText(getApplicationContext(), R.string.clear_marker_donebytesting, Toast.LENGTH_SHORT)
                                                            .show();
                                                } else {
                                                    intent = new Intent();
                                                    controlPanel.setVisibility(View.GONE);
                                                    intent.setAction(MapActivity.ACTION_CLEAR_POINT);
                                                    sendBroadcast(intent);
                                                }
                                                dialog.dismiss();
                                                break;
                                            case 1:
                                                intent = new Intent();
                                                intent.setAction(MapActivity.ACTION_CLEAR_STATION);
                                                sendBroadcast(intent);
                                                dialog.dismiss();
                                                break;
                                            case 2:
                                                intent = new Intent();
                                                intent.setAction(MapActivity.ACTION_CLEAR_MAP);
                                                sendBroadcast(intent);
                                                dialog.dismiss();
                                                break;
                                            case 3:
                                                intent = new Intent();
                                                intent.setAction(MapActivity.ACTION_CLEAR_ALL);
                                                sendBroadcast(intent);
                                                dialog.dismiss();
                                                break;
                                        }
                                    }
                                }).show();
                        morePopupWindow.dismiss();
                        return;
                    }
                    if (!MapFactory.isLoadTAB() && !MapFactory.getMapData().getHistoryList().isEmpty()) {
                        HistoryPoint historyPoint = MapFactory.getMapData().getHistoryList().getLast();
                        switch (historyPoint.getType()) {
                            case HistoryPoint.HISTORY_DEL:
                                if (historyPoint.getPointStatus().getStatus() == PointStatus.POINT_STATUS_PREVIOUS) {
                                    historyPoint.getPointStatus().setStatus(PointStatus.POINT_STATUS_PREVIOUS_EFFECTIVE);
                                } else {
                                    MapFactory.getMapData().getPointStatusStack().add(historyPoint.getPointStatus());
                                }
                                if (appModel.isTestJobIsRun()) {
                                    mapView.writeMarkPoint(HistoryPoint.HISTORY_ADD, historyPoint.getPointStatus(), true);
                                }
                                break;
                            case HistoryPoint.HISTORY_ADD:
                                if (historyPoint.getPointStatus().getStatus() == PointStatus.POINT_STATUS_PREVIOUS_EFFECTIVE) {
                                    historyPoint.getPointStatus().setStatus(PointStatus.POINT_STATUS_PREVIOUS);
                                } else {
                                    MapFactory.getMapData().getPointStatusStack().remove(historyPoint.getPointStatus());
                                }
                                MapFactory.getMapData().getEventQueue().removeAll(historyPoint.getAlarmQueue());
                                MapFactory.getMapData().getQueueStack().remove(historyPoint.getAlarmQueue());
                                mapView.writeMarkPoint(HistoryPoint.HISTORY_DEL, historyPoint.getPointStatus(), false);
                                MapFactory.getMapData().getHistoryList().removeLast();
                                break;

                            default:
                                break;
                        }
                    }
                    break;
                case R.id.Button04:// ????????????
                    if (MapFactory.isLoadMIF()) {
                        new LicenseExplorer(MapActivity.this, new String[]{"txt", "xls"}, LicenseExplorer.LOADING_BASE_DATA,
                                BaseStation.MAPTYPE_INDOOR).start();
                    } else {
                        controlPanel.setVisibility(View.GONE);
                        intent = new Intent();
                        intent.setAction(ACTION_CANCEL_POINT);
                        sendBroadcast(intent);
                    }
                    break;
                case R.id.Button05:// ??????
                    if (isAutoPointTest){
                        ToastUtil.showToastShort(MapActivity.this,getString(R.string.toast_autotesting));
                        return;
                    }
                    /*?????????MapActivity??????MIF????????????????????????????????????????????????MIF
                    *
                    if (MapFactory.isLoadMIF()){
                       showBaseStatinPopView();
                        return;
                   }
                   */
                    // drawpoint();
                    MapFactory.getMapData().setSetPoint(!MapFactory.getMapData().isSetPoint());
                    // ?????????????????????????????????????????????
                    if (MapFactory.getMapData().isAutoMark()) {
                        if (MapFactory.getMapData().isSetPoint())
                            showAutoMarkDialog();
                        else {
                            intent = new Intent(WalkMessage.AUTOMARK_STOP_MARK);
                            sendBroadcast(intent);
                            leftText.setText("");
                            centerText.setText("");
                            rightText.setText("");
                        }
                    }
                    intent = new Intent();
                    intent.setAction(ACTION_DRAW_POINT);
                    intent.putExtra("flag", MapFactory.getMapData().isSetPoint());
                    sendBroadcast(intent);
                    if (MapFactory.getMapData().isSetPoint()) {
                        btnPoint.setBackgroundColor(getResources().getColor(R.color.base_list_item_bg_select));
                    } else {
                        controlPanel.setVisibility(View.GONE);
                        // ???????????????????????????????????????????????????
                        if (!MapFactory.getMapData().getPointStatusStack().isEmpty()) {
                            if (MapFactory.getMapData().getPointStatusStack().lastElement()
                                    .getStatus() == PointStatus.POINT_STATUS_CALIBRATION) {
                                MapFactory.getMapData().getPointStatusStack().pop();
                                mapView.invalidate();
                            }

                        }
                        btnPoint.setBackgroundDrawable(getResources().getDrawable(R.drawable.base_list_toolbar_bg));
                    }
                    break;
                case R.id.Button06: // ??????
                    showMorePopView();
                    break;

                case R.id.ButtonLeft:// ???????????????
                    intent = new Intent(ControlPanel.ACTION_CONTROL);
                    intent.putExtra(ControlPanel.KEY_CONTROL, ControlPanel.CONTROL_LEFT);
                    sendBroadcast(intent);
                    break;
                case R.id.ButtonRight:// ???????????????
                    intent = new Intent(ControlPanel.ACTION_CONTROL);
                    intent.putExtra(ControlPanel.KEY_CONTROL, ControlPanel.CONTROL_RIGHT);
                    sendBroadcast(intent);
                    break;
                case R.id.ButtonUp: // ???????????????
                    intent = new Intent(ControlPanel.ACTION_CONTROL);
                    intent.putExtra(ControlPanel.KEY_CONTROL, ControlPanel.CONTROL_UP);
                    sendBroadcast(intent);
                    break;
                case R.id.ButtonDown: // ???????????????
                    intent = new Intent(ControlPanel.ACTION_CONTROL);
                    intent.putExtra(ControlPanel.KEY_CONTROL, ControlPanel.CONTROL_DOWN);
                    sendBroadcast(intent);
                    break;
                case R.id.ButtonEnter: // ???????????????
                    intent = new Intent(ControlPanel.ACTION_CONTROL);
                    intent.putExtra(ControlPanel.KEY_CONTROL, ControlPanel.CONTROL_ENTER);
                    sendBroadcast(intent);
                    controlPanel.setVisibility(View.GONE);
                    break;
            }
        }

    };
    /**
     * ?????????????????????
     */
    private void showBaseStatinPopView() {
        if (this.baseStationPop == null) {
            this.baseStationPop = new BaseStationSearchPopWindow(mapView, this.getParent(), mapView.getMeasuredWidth(),
                    mapView.getMeasuredHeight() / 2);
            int[] location = new int[2];
            this.getWindow().getDecorView().getLocationOnScreen(location);
            this.baseStationPop.setLocation(location);
        }
        this.baseStationPop.show();
    }
    /**
     * ?????????????????????????????????
     */
    private void showAutoMarkDialog() {
        if (!ApplicationModel.getInstance().isInnsmapTest() && !ApplicationModel.getInstance().isGlonavinTest()) {
            BasicDialog.Builder builder = new BasicDialog.Builder(this.getParent());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) this.getApplicationContext().getSystemService(inflater);
            View view = vi.inflate(R.layout.automark_setting_dialog, null, true);
            final EditText buildingOrientation = (EditText) view.findViewById(R.id.building_orientation);
            buildingOrientation.setText(String.valueOf(AutoMarkManager.buildingOrientation));
            final EditText buildingScale = (EditText) view.findViewById(R.id.building_scale);
            buildingScale.setText(String.valueOf(AutoMarkManager.buildingScale));
            final EditText standardAtm = (EditText) view.findViewById(R.id.standard_atm);
            standardAtm.setText(String.valueOf(AutoMarkManager.standardAtm));
            final CheckBox showDistance = (CheckBox) view.findViewById(R.id.show_distance);
            showDistance.setChecked(AutoMarkManager.isShowDistance);
            builder.setView(view);
            builder.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AutoMarkManager.buildingOrientation = Integer.parseInt(buildingOrientation.getText().toString());
                    AutoMarkManager.buildingScale = Double.parseDouble(buildingScale.getText().toString());
                    AutoMarkManager.standardAtm = Double.parseDouble(standardAtm.getText().toString());
                    AutoMarkManager.isShowDistance = showDistance.isChecked();
                    dialog.dismiss();
                }

            });
            builder.show();
        }
    }

    /**
     * ??????????????????????????????
     */
    @SuppressWarnings("deprecation")
    private void drawbtnPoint() {
        if (MapFactory.getMapData().isSetPoint()) {
            btnPoint.setBackgroundColor(getResources().getColor(R.color.base_list_item_bg_select));
        } else {
            btnPoint.setBackgroundDrawable(getResources().getDrawable(R.drawable.base_list_toolbar_bg));
        }
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(com.walktour.gui.map.MapActivity.ACTION_MAP_COLOR_CHANGE)) {
                LogUtil.v("MapChange", "---Change---");
                findViewById(R.id.threshold_view).invalidate();
            } else if (action.equals(ControlPanel.ACTION_MARK)) {// ???????????????????????????
                controlPanel.setVisibility(View.VISIBLE);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.panelwidth),
                        getResources().getDimensionPixelSize(R.dimen.panelheight));
                int marginLeft = intent.getIntExtra(ControlPanel.KEY_LEFT,
                        getResources().getDimensionPixelSize(R.dimen.panelleft));
                int marginTop = intent.getIntExtra(ControlPanel.KEY_TOP,
                        getResources().getDimensionPixelSize(R.dimen.paneltop));
                params.gravity = android.view.Gravity.TOP;
                params.topMargin = intent.getIntExtra(ControlPanel.KEY_TOP, 0);
                params.leftMargin = intent.getIntExtra(ControlPanel.KEY_LEFT, 0);
                LogUtil.i(TAG, "---maringLeft:" + marginLeft + ",marginTop:" + marginTop);
                controlPanel.setLayoutParams(params);
                controlPanel.invalidate();
            } else if (action.equals(MapActivity.SWITCH_MAP)) { // ??????????????????
                if (TraceInfoInterface.currentMapChildTab.equals(WalkStruct.ShowInfoType.GoogleMap)) {
                    ((MapTabActivity) MapActivity.this.getParent()).swicthMap(WalkStruct.ShowInfoType.GoogleMap);
                } else if (TraceInfoInterface.currentMapChildTab.equals(WalkStruct.ShowInfoType.BaiduMap)) {
                    ((MapTabActivity) MapActivity.this.getParent()).swicthMap(WalkStruct.ShowInfoType.BaiduMap);
                } else if (TraceInfoInterface.currentMapChildTab.equals(WalkStruct.ShowInfoType.AMap)) {
                    ((MapTabActivity) MapActivity.this.getParent()).swicthMap(WalkStruct.ShowInfoType.AMap);
                } else {
                    ((MapTabActivity) MapActivity.this.getParent()).swicthMap(WalkStruct.ShowInfoType.OtherMap);
                }
            } else if (action.equals(ACTION_DIR)) {// ???????????????????????????
                String path = intent.getExtras().getString(EXTRA_DIR);
                saveViewToBMP(path);
            } else if (action.equals(WalkMessage.AUTOMARK_BUILDER_HEIGHT)) {
                int height = intent.getIntExtra("buildingHeight", 0);
                Message msg = mHandler.obtainMessage(AUTO_MARK_HEIGHT_CHANGE);
                msg.obj = height;
                mHandler.handleMessage(msg);
            } else if (action.equals(WalkMessage.AUTOMARK_ORIENTATION)) {
                int orientation = intent.getIntExtra("orientation", 0);
                Message msg = mHandler.obtainMessage(AUTO_MARK_ORIENGATION_CHANGE);
                msg.obj = orientation;
                mHandler.handleMessage(msg);
            } else if (action.equals(WalkMessage.AUTOMARK_TOTAL_STEPS)) {
                int height = intent.getIntExtra("steps", 0);
                Message msg = mHandler.obtainMessage(AUTO_MARK_STEP_CHANGE);
                msg.obj = height;
                mHandler.handleMessage(msg);
            }

        }
    };

    private OnClickListener myListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.zoominbtn:
                    Intent intent = new Intent(ACTION_MAP_ZOOM);
                    intent.putExtra("scale", (float) 1.1);
                    sendBroadcast(intent);
                    break;
                case R.id.zoomoutbtn:
                    intent = new Intent(ACTION_MAP_ZOOM);
                    intent.putExtra("scale", (float) 0.9);
                    sendBroadcast(intent);
                    break;
                case R.id.locationbtn:
                    intent = new Intent();
                    intent.setAction(MapActivity.MOVE_TO_CENTER);
                    sendBroadcast(intent);
                    break;
            }
        }
    };

    /**
     * ?????????????????????
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // ???????????????????????????
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_offline)/* "????????????" */)) {
            ((MapTabActivity) getParent()).swicthMap(WalkStruct.ShowInfoType.OfflineMap);
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_online)/* "????????????" */)) {
            ((MapTabActivity) getParent()).swicthMap(WalkStruct.ShowInfoType.OnlineMap);
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_scan)/* "????????????" */)) {
            // if(gpsInfo.isGpsOpen() &&
            // ApplicationModel.getInstance().isTestJobIsRun())
            if ((appModel.isTestJobIsRun() || appModel.isTestStoping()) && appModel.isGpsTest()) {
                Toast.makeText(MapActivity.this, getString(R.string.map_testRunAndGpsOpen_CantLoadScan), Toast.LENGTH_LONG)
                        .show();
            } else {
                ImageExplorer explorer = new ImageExplorer(this, com.walktour.gui.map.MapActivity.ACTION_LOAD_SCAN_MAP,
                        com.walktour.gui.map.MapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_picture));
                explorer.start();
            }
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_indoor)/* "????????????" */)) {
            if ((appModel.isTestJobIsRun() || appModel.isTestStoping()) && appModel.isGpsTest()) {
                Toast.makeText(MapActivity.this, getString(R.string.map_testRunAndGpsOpen_CantLoadIndoor), Toast.LENGTH_LONG)
                        .show();
            } else {
                // ??????????????????????????????
                Intent intent = new Intent(this, SysIndoor.class);
                intent.putExtra(SysIndoor.KEY_LOADING, true);
                startActivityForResult(intent, 10);
            }
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_outdoor)/* "??????mif??????" */)) {
            if ((appModel.isTestJobIsRun() || appModel.isTestStoping()) && appModel.isIndoorTest()) {
                Toast.makeText(MapActivity.this, getString(R.string.map_testRunAndIndoor_CantLoadMif), Toast.LENGTH_LONG)
                        .show();
            } else {
                new ImageExplorer(this, com.walktour.gui.map.MapActivity.ACTION_LOAD_MIF_MAP,
                        com.walktour.gui.map.MapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_mif)).start();
            }
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_base)/* "????????????" */)) {
            new ImageExplorer(this, com.walktour.gui.map.MapActivity.ACTION_LOAD_BASE_DATA,
                    com.walktour.gui.map.MapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_tour)).start();
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_base_append)/* "????????????" */)) {
            new ImageExplorer(this, com.walktour.gui.map.MapActivity.ACTION_LOAD_BASE_DATA_APPEND,
                    com.walktour.gui.map.MapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_tour)).start();
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_selfdraw)/* "????????????" */)) {
            new ImageExplorer(this, com.walktour.gui.map.MapActivity.ACTION_LOAD_PAINT_MAP,
                    com.walktour.gui.map.MapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_picture))
                    .start();
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_clean)/* "????????????" */)) {
            Intent intent = new Intent();
            intent.setAction(ACTION_CLEAR_ALL);
            sendBroadcast(intent);
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_outtab)/* "iBwave??????" */)) {
            new ImageExplorer(this, com.walktour.gui.map.MapActivity.ACTION_LOAD_TAB_MAP,
                    com.walktour.gui.map.MapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_tab)).start();
        }
        // /*if (item.getTitle().toString().equalsIgnoreCase("??????/??????????????????"))
        // {
        // Intent intent= new Intent();
        // intent.setAction(MyAction1);
        // flag_toggle = !flag_toggle;
        // intent.putExtra("flag", flag_toggle);
        // sendBroadcast(intent);
        // //Toast.makeText(this, item.getTitle().toString(),
        // Toast.LENGTH_SHORT).show();
        // }*/
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_operate_cancle)/* "?????????????????????" */)) {
            Intent intent = new Intent();
            intent.setAction(ACTION_CANCEL_POINT);
            sendBroadcast(intent);
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_operate_drawline)/* "??????/??????????????????" */)) {
            Intent intent = new Intent();
            intent.setAction(ACTION_DRAW_POINT);
            isSetPoint = !isSetPoint;
            intent.putExtra("flag", isSetPoint);
            sendBroadcast(intent);
            Intent intent1 = new Intent();
            intent1.setAction(ACTION_DRAW_LINE);
            isDrawline = !isDrawline;
            intent1.putExtra("flag_drawline", isDrawline);
            sendBroadcast(intent1);
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_operate_setting)/* "??????" */)) {
            Intent intent = new Intent(this, SysMap.class);
            startActivity(intent);
        } else if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.map_base_edit))) {// ????????????
            Intent intent = new Intent(this, BaseEditor.class);
            startActivity(intent);
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Display display = ((WindowManager)
        // getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        // rate = (float)display.getWidth()/320;
        // setText();
        LogUtil.w(TAG, "-----onStart------");

        showThresholdLegend();
        if (appModel.isInOutSwitchMode()) {
            btnPoint.setEnabled(true);
            LogUtil.w(TAG, "here is inOutSwitchMode");
        }
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @author qihang.li
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println("Map onActivityResult");
        switch (resultCode) {
            case RESULT_OK: // ??????FileExplorer??????????????????
                // ????????????????????????
                Bundle b = data.getExtras();
                String map_file_path = b.getString(SysIndoor.KEY_RESULT);// ????????????
                LogUtil.w("Map", "mappath:" + map_file_path);
                // ??????????????????????????????????????????
                Intent intent = new Intent();
                intent.setAction(ACTION_LOAD_INDOOR_MAP);
                intent.putExtra(Map_File_Path, map_file_path);
                sendBroadcast(intent);
                break;
            default:// donothing
                break;
        }// end switch
    }// end method

    /**
     * ??????????????????<BR>
     * [??????????????????]
     */
    private void showThresholdLegend() {
        ThresholdView thresholdView = (ThresholdView) findViewById(R.id.threshold_view);
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        if (ParameterSetting.getInstance().isDisplayLegen()) {
            List<Parameter> parameterList = ParameterSetting.getInstance()
                    .getCheckedParamertersByNet(MyPhoneState.getInstance().getCurrentNetForParam(this));
            Parameter parameter = parameterList.size() > 0 ? parameterList.get(0) : null;
            // Parameter parameter =
            // ParameterSetting.getInstance().getMapParameter();
            if (parameter == null) {
                return;
            }
            if (parameter.getThresholdList().size() > 0) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        (int) (15 * metric.density));
                lp.gravity = Gravity.BOTTOM;
                thresholdView.setLayoutParams(lp);
                thresholdView.setVisibility(View.VISIBLE);
            } else {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        (int) (45 * metric.density));
                lp.gravity = Gravity.BOTTOM;
                thresholdView.setLayoutParams(lp);
            }

        } else {
            thresholdView.setVisibility(View.GONE);
        }
        thresholdView.invalidate();
        thresholdView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.w(TAG, "-----map is ondestroy");
        if (isRegisterReceiver) {
            unregisterReceiver(mIntentReceiver);
            isRegisterReceiver = !isRegisterReceiver;
        }
        if (this.isShowInnsMap && this.innsMapView != null)
            this.innsMapView.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        BasicDialog.Builder builder = new BasicDialog.Builder(this.getParent());
        switch (id) {
            case LOADING_MAP_DIALOG:
                if ((ApplicationModel.getInstance().isGerenalTest() || ApplicationModel.getInstance().isIndoorTest())
                        && appModel.isTestJobIsRun()) { // ????????????
                    menuStr = getResources().getStringArray(R.array.array_indoor_map);
                } else if (!ApplicationModel.getInstance().isGerenalTest() && !ApplicationModel.getInstance().isIndoorTest()
                        && appModel.isTestJobIsRun()) { // dt??????
                    if (ParameterSetting.getInstance().getDtDefaultMap().equals(getResources().getStringArray(R.array.sys_dtmap_default)[0])) {
                        menuStr = new String[]{getString(R.string.map_outdoor)};
                    } else {
                        menuStr = new String[]{getString(R.string.map_offline), getString(R.string.map_online)};
                    }
                } else {
                    menuStr = getResources().getStringArray(R.array.array_loading_map);
                }
                if (appModel.getSelectScene() == SceneType.Metro || appModel.getSelectScene() == SceneType.HighSpeedRail) {
                    menuStr = new String[]{getString(R.string.map_scan), getString(R.string.map_outdoor), getString(R.string.map_offline), getString(R.string.map_online)};
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
                        String switchStr = menuStr[which]; // ????????????item?????????
                        if(switchStr.equals(getString(R.string.map_innsmap))
                                || switchStr.equals(getString(R.string.map_outdoor))
                                || switchStr.equals(getString(R.string.map_online))){
                            SharePreferencesUtil.getInstance(WalktourApplication.getAppContext()).saveBoolean(MapView.SP_IS_LOAD_INDOOR_MAP,false);
                        }
                        if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[0])) {
                            if ((appModel.isTestJobIsRun() || appModel.isTestStoping()) && appModel.isGpsTest()) {
                                Toast
                                        .makeText(MapActivity.this, getString(R.string.map_testRunAndGpsOpen_CantLoadScan), Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.OtherMap;
                                ImageExplorer explorer = new ImageExplorer(MapActivity.this,
                                        com.walktour.gui.map.MapActivity.ACTION_LOAD_SCAN_MAP, com.walktour.gui.map.MapActivity.Map_File_Path,
                                        getResources().getStringArray(R.array.maptype_picture));
                                explorer.start();
                            }
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[1])) {
                            if ((appModel.isTestJobIsRun() || appModel.isTestStoping()) && appModel.isGpsTest()) {
                                Toast.makeText(MapActivity.this, getString(R.string.map_testRunAndGpsOpen_CantLoadIndoor),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                // ??????????????????????????????
                                Intent intent = new Intent(MapActivity.this, SysIndoor.class);
                                intent.putExtra(SysIndoor.KEY_LOADING, true);
                                getParent().startActivityForResult(intent, 10);
                            }
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[2])) {
                            if ((appModel.isTestJobIsRun() || appModel.isTestStoping()) && appModel.isIndoorTest()) {
                                Toast.makeText(MapActivity.this, getString(R.string.map_testRunAndIndoor_CantLoadMif), Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                ((MapTabActivity) MapActivity.this.getParent()).swicthMap(WalkStruct.ShowInfoType.BaiduMap);
                                TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
                                new ImageExplorer(MapActivity.this, com.walktour.gui.map.MapActivity.ACTION_LOAD_MIF_MAP,
                                        com.walktour.gui.map.MapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_mif))
                                        .start();
                            }
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[3])) {
                            TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.OtherMap;
                            new ImageExplorer(MapActivity.this, com.walktour.gui.map.MapActivity.ACTION_LOAD_TAB_MAP,
                                    com.walktour.gui.map.MapActivity.Map_File_Path, getResources().getStringArray(R.array.maptype_tab))
                                    .start();
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[4])) {
                            ((IMapTab) getParent()).swicthMap(WalkStruct.ShowInfoType.OfflineMap);
                        } else if (switchStr.equalsIgnoreCase(getResources().getStringArray(R.array.array_loading_map)[5])) {
                            appModel.setInOutSwitchMode(true);
                            ((IMapTab) getParent()).swicthMap(WalkStruct.ShowInfoType.OnlineMap);
                        } else if (switchStr.equalsIgnoreCase(getString(R.string.map_innsmap))) {
                            ((IMapTab) getParent()).swicthMap(WalkStruct.ShowInfoType.InnsMap);
                        }
                    }
                });
                break;

            default:
                break;
        }

        return builder.create();
    }

    @Override
    public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Map onTabActivityResult");
        switch (resultCode) {
            case RESULT_OK: // ??????FileExplorer??????????????????

                ((MapTabActivity) MapActivity.this.getParent()).swicthMap(WalkStruct.ShowInfoType.Map);
                TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.OtherMap;
                // ????????????????????????
                Bundle b = data.getExtras();
                String map_file_path = b.getString(SysIndoor.KEY_RESULT);// ????????????
                LogUtil.w("Map", "mappath:" + map_file_path);
                // ??????????????????????????????????????????
                Intent intent = new Intent();
                intent.setAction(ACTION_LOAD_INDOOR_MAP);
                intent.putExtra(Map_File_Path, map_file_path);
                sendBroadcast(intent);

                break;
            default:// donothing
                break;
        }// end switch
    }

    @Override
    protected void onPause() {
        isMoving = false;
        super.onPause();
        if (this.isShowInnsMap && this.innsMapView != null)
            this.innsMapView.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.import_basedata:
                new LicenseExplorer(this, new String[]{"txt", "xls"}, LicenseExplorer.LOADING_BASE_DATA,
                        BaseStation.MAPTYPE_INDOOR).start();
                break;
            case R.id.map_clean_all:
                controlPanel.setVisibility(View.GONE);
                // ???????????????????????????????????????????????????
                if (!MapFactory.getMapData().getPointStatusStack().isEmpty()) {
                    if (MapFactory.getMapData().getPointStatusStack().lastElement()
                            .getStatus() == PointStatus.POINT_STATUS_CALIBRATION) {
                        MapFactory.getMapData().getPointStatusStack().pop();
                        mapView.invalidate();
                    }

                }
                new BasicDialog.Builder(MapActivity.this.getParent()).setIcon(android.R.drawable.ic_menu_delete)
                        .setTitle(R.string.map_clean)
                        .setItems(getResources().getStringArray(R.array.clearOpition), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = null;
                                switch (which) {
                                    case 0:
                                        if (appModel.isTestJobIsRun()) {
                                            Toast.makeText(getApplicationContext(), R.string.clear_marker_donebytesting, Toast.LENGTH_SHORT)
                                                    .show();
                                        } else {
                                            intent = new Intent();
                                            controlPanel.setVisibility(View.GONE);
                                            intent.setAction(MapActivity.ACTION_CLEAR_POINT);
                                            sendBroadcast(intent);
                                        }
                                        dialog.dismiss();
                                        break;
                                    case 1:
                                        intent = new Intent();
                                        intent.setAction(MapActivity.ACTION_CLEAR_STATION);
                                        sendBroadcast(intent);
                                        dialog.dismiss();
                                        break;
                                    case 2:
                                        intent = new Intent();
                                        intent.setAction(MapActivity.ACTION_CLEAR_MAP);
                                        sendBroadcast(intent);
                                        dialog.dismiss();
                                        break;
                                    case 3:
                                        intent = new Intent();
                                        intent.setAction(MapActivity.ACTION_CLEAR_ALL);
                                        sendBroadcast(intent);
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        }).show();
                morePopupWindow.dismiss();
                break;
            case R.id.map_export:
                morePopupWindow.dismiss();
                final String[] fileTypes = new String[]{"JPEG", "BMP"};
                new BasicDialog.Builder(MapActivity.this.getParent()).setTitle(R.string.facebook_filetype)
                        .setSingleChoiceItems(fileTypes, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        fileType = ImageUtil.FileType.JPEG;
                                        break;
                                    case 1:
                                        fileType = ImageUtil.FileType.BMP;
                                        break;
                                }
                                // ?????????????????????
                                new ExplorerDirectory(MapActivity.this, fileTypes, ACTION_DIR, EXTRA_DIR).start();
                                morePopupWindow.dismiss();
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.setting_btn:
                controlPanel.setVisibility(View.GONE);
                // ???????????????????????????????????????????????????
                if (!MapFactory.getMapData().getPointStatusStack().isEmpty()) {
                    if (MapFactory.getMapData().getPointStatusStack().lastElement()
                            .getStatus() == PointStatus.POINT_STATUS_CALIBRATION) {
                        MapFactory.getMapData().getPointStatusStack().pop();
                        mapView.invalidate();
                    }

                }
                Intent intent1 = new Intent(MapActivity.this, SysMap.class);
                startActivity(intent1);
                morePopupWindow.dismiss();
                break;
            case R.id.map_plotting_scale_btn:
                showPlottingScaleDialog();
                morePopupWindow.dismiss();
                break;
            case R.id.map_switch_point:
                switchPointTpye();
                morePopupWindow.dismiss();
                break;
            default:
                break;
        }
    }
    boolean isAutoPointTest=true;//????????????????????????????????????
    private void switchPointTpye() {
        appModel.isTesting();
        if (isAutoPointTest){
            stopService(new Intent(this, GlonavinAutoMarkService.class));
            isAutoPointTest=false;
        }else {
            String blueToothAddress = WalktourConst.connectBlueToothAddress;
            if (TextUtils.isEmpty(blueToothAddress)){
                ToastUtil.showToastShort(this,"???????????????????????????????????????CQT????????????");
                return;
            }
            Intent glonavinIntent = new Intent(this, GlonavinAutoMarkService.class);
            glonavinIntent.putExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE,blueToothAddress);
            startService(glonavinIntent);
            isAutoPointTest=true;
        }
    }

    BasicDialog alert;
    /**
     * ????????????????????????
     */
    private void showPlottingScaleDialog() {
        final SharePreferencesUtil sharePreferencesUtil = SharePreferencesUtil.getInstance(WalktourApplication.getAppContext());
        final String indoorMapPath = sharePreferencesUtil.getString(MapView.SP_INDOOR_MAP_PATH);
        if(null == alert){
            LayoutInflater fac = LayoutInflater.from(getApplicationContext());
            View view = fac.inflate(R.layout.alert_dialog_edittext_with_button, null);
            final EditText edittext = (EditText) view.findViewById(R.id.alert_textEditText);
            view.findViewById(R.id.alert_btn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    startActivity(new Intent(MapActivity.this,MeasurePlottingScaleActivity.class));
                }
            });
            String existScale = String.valueOf(sharePreferencesUtil.getFloat(indoorMapPath, 1));
            edittext.setText(existScale);
            edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            alert = new BasicDialog.Builder(MapActivity.this.getParent()).setIcon(android.R.drawable.ic_menu_edit)
                    .setTitle(R.string.input_plotting_scale_alert_tip).setView(view)
                    .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
                            String scaleStr = edittext.getText().toString().trim();
                            if (!TextUtils.isEmpty(scaleStr)) {
                                float scale=Float.valueOf(scaleStr);
                                if (scale<=0){
                                    scale=1;
                                }
                                MapFactory.getMapData().setPlottingScale(Float.valueOf(scale));
                                if(!TextUtils.isEmpty(indoorMapPath)){
                                    sharePreferencesUtil.putFloat(indoorMapPath,Float.valueOf(scale));
                                }
                            }
                        }
                    }).setNegativeButton(R.string.str_cancle).create();
            alert.setCancelable(false);
            alert.setCanceledOnTouchOutside(false);
        }else{
            final EditText edittext = (EditText) alert.findViewById(R.id.alert_textEditText);
            String exScale = String.valueOf(sharePreferencesUtil.getFloat(indoorMapPath,1));
            edittext.setText(exScale);
        }

        alert.show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePlottingScaleMeasuredEvent(MeasurePlottingScaleActivity.OnPlottingScaleMeasuredEvent event){
        final SharePreferencesUtil sharePreferencesUtil = SharePreferencesUtil.getInstance(WalktourApplication.getAppContext());
        final String indoorMapPath = sharePreferencesUtil.getString(MapView.SP_INDOOR_MAP_PATH);
        if(null == alert){
            LayoutInflater fac = LayoutInflater.from(getApplicationContext());
            View view = fac.inflate(R.layout.alert_dialog_edittext_with_button, null);
            final EditText edittext = (EditText) view.findViewById(R.id.alert_textEditText);
            view.findViewById(R.id.alert_btn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    startActivity(new Intent(MapActivity.this,MeasurePlottingScaleActivity.class));
                }
            });
            edittext.setText(event.getPlottingScaleInput());
            edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            alert = new BasicDialog.Builder(MapActivity.this.getParent()).setIcon(android.R.drawable.ic_menu_edit)
                    .setTitle(R.string.input_plotting_scale_alert_tip).setView(view)
                    .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
                            String scaleStr = edittext.getText().toString().trim();
                            if (!TextUtils.isEmpty(scaleStr)) {
                                float scale=Float.valueOf(scaleStr);
                                if (scale<=0){
                                    scale=1;
                                }
                                MapFactory.getMapData().setPlottingScale(Float.valueOf(scale));
                                if(!TextUtils.isEmpty(indoorMapPath)){
                                    sharePreferencesUtil.putFloat(indoorMapPath,Float.valueOf(scale));
                                }
                            }
                        }
                    }).setNegativeButton(R.string.str_cancle).create();
            alert.setCancelable(false);
            alert.setCanceledOnTouchOutside(false);
        }else{
            final EditText edittext = (EditText) alert.findViewById(R.id.alert_textEditText);
            edittext.setText(event.getPlottingScaleInput());
        }
        alert.show();
    }


    /**
     * ??????????????????????????????????????????????????????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onBubblingValidPoint(OnBubblingValidPointEvent event) {
        LogUtil.i(TAG, "----------?????????????????????????????????????????????------------");
        if (!GlonavinDataManager.getInstance().isHasDirectionSet()) {
            GlonavinSetDirectionTipDialogFragment dialogFragment = new GlonavinSetDirectionTipDialogFragment();
            dialogFragment.show(getParent().getFragmentManager(), "GlonavinSetDirectionTipDialogFragment");
        }
        OnBubblingValidPointEvent stickyEvent = EventBus.getDefault().removeStickyEvent(OnBubblingValidPointEvent.class);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
    }



    /**
     * ????????????????????????????????????????????????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowConfirmStartAutoMarkDialog(ShowConfirmStartAutoMarkDialogEvent event) {
        ConfirmStartAutoPointDialogFragment dialogFragment = new ConfirmStartAutoPointDialogFragment();
        dialogFragment.show(getParent().getFragmentManager(), "ConfirmStartAutoPointDialogFragment");
    }

    /**
     * ??????????????????
     *
     * @return
     */
    private String getPicName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());
        return format.format(Calendar.getInstance(Locale.getDefault()).getTime());
    }

    /**
     * ?????????????????????
     *
     * @param path ????????????
     */
    private void saveViewToBMP(String path) {
        View view = this.findViewById(R.id.FrameLayout01);
        LinearLayout lineTool = initLinearLayout(R.id.linearTool);
        lineTool.setVisibility(View.GONE);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        lineTool.setVisibility(View.VISIBLE);
        if (bitmap == null) {
            return;
        }
        ImageUtil.saveBitmapToFile(path, bitmap, this.getPicName(), fileType);
        Toast.makeText(getApplicationContext(), R.string.map_export_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.isShowInnsMap && this.innsMapView != null)
            this.innsMapView.onResume();
    }

}
