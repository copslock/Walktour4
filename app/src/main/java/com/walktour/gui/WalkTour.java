package com.walktour.gui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dingli.wlan.apscan.WifiTools;
import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.EventManager;
import com.dinglicom.totalreport.TotalDetailActivity;
import com.walktour.Utils.AlertWakeLock;
import com.walktour.Utils.AppVersionControl;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DensityUtil;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.ScreenUtils;
import com.walktour.control.adapter.WalkTourGridViewAdapter;
import com.walktour.control.config.ConfigAutoTest;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.ProjectManager;
import com.walktour.control.config.ServerManager;
import com.walktour.control.instance.AlertManager;
import com.walktour.customView.ListViewForScrollView;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.analysis.AnalysisMainActivity;
import com.walktour.gui.applet.LicenseExplorer;
import com.walktour.gui.applet.MySimpleAdapter;
import com.walktour.gui.data.FileManagerFragmentActivity;
import com.walktour.gui.fleet.Fleet;
import com.walktour.gui.gps.Gps;
import com.walktour.gui.indoor.IndoorTask;
import com.walktour.gui.newmap2.NewInfoTabActivity;
import com.walktour.gui.newmap2.gis.GisMapActivity;
import com.walktour.gui.perceptiontest.activity.BusinessOverviewActivity;
import com.walktour.gui.perceptiontest.activity.QualityOverviewActivity;
import com.walktour.gui.perceptiontest.activity.ResourceOverviewActiviity;
import com.walktour.gui.perceptiontest.notice.MessageListActivity;
import com.walktour.gui.perceptiontest.notice.service.GetNoticeService;
import com.walktour.gui.perceptiontest.surveytask.claiming.SurveyTaskClaimingActivity;
import com.walktour.gui.perceptiontest.surveytask.finishing.SurveyTaskFinishingActivity;
import com.walktour.gui.perceptiontest.surveytask.query.SurveyTaskQueryActivity;
import com.walktour.gui.replayfloatview.FloatWindowManager;
import com.walktour.gui.setting.Sys;
import com.walktour.gui.setting.sysroutine.SysRoutineActivity;
import com.walktour.gui.singlestation.report.activity.StationReportActivity;
import com.walktour.gui.singlestation.survey.activity.SurveyActivity;
import com.walktour.gui.singlestation.test.activity.StationActivity;
import com.walktour.gui.task.TaskAll;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.upgrade.UpgradeActivity;
import com.walktour.gui.weifuwu.WeiMainActivity;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.gui.weifuwu.sharepush.ShareProjectActivity;
import com.walktour.gui.weifuwu.view.BadgeView;
import com.walktour.model.AlarmModel;
import com.walktour.model.StateInfoModel;
import com.walktour.service.FleetService;
import com.walktour.service.app.Killer;
import com.walktour.workorder.WorkOrderDictActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/****************************************
 * 初始化主界面 * *
 **************************************/

public class WalkTour extends BasicActivity implements RefreshEventListener {

    /**
     * 部分手机需要添加悬浮框权限
     */
    public static int OVERLAY_PERMISSION_REQ_CODE = 0x19086;
    private final int PROJECT_SAVE = 1;
    private final int PROJECT_LOAD = 2;
    private static final int REQ_TAKE_PHOTO = 1;
    public static final int REFLESH_VIEW = 1;
    private boolean isRegisterRecev = false;
    private static boolean isWaitInitTrace = false;
    private String tag = "Walktour";
    private ApplicationModel appModel = ApplicationModel.getInstance();
    private static int item_postion_building;
    private static boolean indoor_special;
    private GpsInfo gpsInfo;
    private boolean isLoadLicense = false;
    private boolean isShowAbouting = false;
    private Button btnSave;
    private Button btnLoad;
    // 按钮上显示文字
    private BadgeView badge1;
    private ProjectManager proManager = null;
    private StartDialog dialog;
    private FileReceiver receiver = new FileReceiver();
    private Context context = WalkTour.this;
    private Button nb;
    // 按钮上显示文字
    private BadgeView badge2;
    /**
     * 服务器管理类
     */
    private ServerManager mServer;
    boolean isDoing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.walktour_main_gridview);
        ((ImageButton) findViewById(R.id.backBtn)).setOnClickListener(this);
        mServer = ServerManager.getInstance(this);
        gpsInfo = GpsInfo.getInstance();
        proManager = new ProjectManager(WalkTour.this);
        if (WifiTools.isWifiConnected()) {
            appModel.setWifiOpen(true);
        }
        // 注册接收测试完成消息
        IntentFilter broadCaseIntent = new IntentFilter();
        broadCaseIntent.addAction(WalkMessage.NOTIFY_TESTJOBDONE);
        broadCaseIntent.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
        broadCaseIntent.addAction(WalkMessage.ACTION_WALKTOUR_START_TEST);
        // broadCaseIntent.addAction(GpsInfo.gpsProviderDisabled);
        if (!isRegisterRecev) {
            registerReceiver(testJobDoneReceiver, broadCaseIntent);
            isRegisterRecev = true;
        }
        // ParamTotalInfo.getInstance();
        // 一个标志，若>-1则表示是通过室内测试而来，否则为业务测试
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            indoor_special = bundle.getBoolean("indoor_special");
            item_postion_building = bundle.getInt(IndoorTask.BUILD_POSITION, -1);
        }
        LogUtil.i(tag, "---item_postion=" + item_postion_building);
        if (!appModel.getAppList().contains(WalkStruct.AppType.OperationTest)) {
            if (appModel.isEnvironmentInit())
                Toast.makeText(WalkTour.this, R.string.main_license_operationtest_faild, Toast.LENGTH_LONG).show();
            this.finish();
            return;
        }
        TextView tvTitle = initTextView(R.id.titileTV);
        if (appModel.getSelectScene() == SceneType.HighSpeedRail) {// 高铁测试
            tvTitle.setText(R.string.gaotie_project_test);
        } else if (appModel.getSelectScene() == SceneType.Metro) {// 地铁测试
            tvTitle.setText(R.string.metro_test);
        } else if (appModel.getSelectScene() == SceneType.SingleSite) {// 单站验证
            tvTitle.setText(R.string.single_station_validation);
        } else if (appModel.getSelectScene() == SceneType.Perception) {// 感知测试
            tvTitle.setText(R.string.str_scene_type_name_perception);
        }
        setDefaultServer();
        nb = (Button) findViewById(R.id.weifuwu);
        if (appModel.getSelectScene() == SceneType.Perception) {
            nb.setBackgroundResource(R.drawable.obj_me_hui);
        }
        nb.setOnClickListener(this);
        badge2 = new BadgeView(this, nb);
        badge2.setText("1");
        badge2.setTextColor(getResources().getColor(R.color.white));
        badge2.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badge2.hide(true);
        initButtonNum2();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ShareCommons.SHARE_ACTION_1);
        filter.addAction(ShareCommons.SHARE_ACTION_2);
        filter.addAction(ShareCommons.SHARE_ACTION_3);
        filter.addAction(ShareCommons.SHARE_ACTION_4);
        filter.addAction(ShareCommons.SHARE_ACTION_5);
        filter.addAction(ShareCommons.SHARE_ACTION_6);
        filter.addAction(ShareCommons.SHARE_ACTION_7);
        filter.addAction(ShareCommons.SHARE_ACTION_8);
        filter.addAction(ShareCommons.SHARE_ACTION_9);
        registerReceiver(receiver, filter);

    }

    /**
     * 检测是否有权限
     */

    @SuppressLint("NewApi")
    private boolean checkPermission() {
        if (UtilsMethod.getSDKVersionNumber() >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            // Toast.makeText(this, "can not DrawOverlays", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            return true;
        }
        return false;
    }

    /***
     * 如果在高铁或地铁模式下,如果历史是ATU或BUT服务器则默认选择fleet
     */
    private void setDefaultServer() {
        if (appModel.getSelectScene() == SceneType.HighSpeedRail || appModel.getSelectScene() == SceneType.Metro) {// 高铁测试
            if (mServer.getUploadServer() == ServerManager.SERVER_ATU || mServer.getUploadServer() == ServerManager.SERVER_BTU) {
                mServer.setUploadServer(ServerManager.SERVER_FLEET);// 默认fleet服务器
            }
        } else {// 还原历史服务器
            mServer.setUploadServer(mServer.getHistoryServer());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TaskListDispose.getInstance().reloadFromXML();
        RefreshEventManager.addRefreshListener(this);
        findView();// 生成主界面

        updateTestStatus();
        getNotice();// 收公告
    }

    @Override
    protected void onPause() {
        super.onPause();
        RefreshEventManager.removeRefreshListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.i(tag, "---onStart--");
    }

    /*** 销毁 */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (badge1 != null) {
            badge1.clearAnimation();
        }
        if (badge2 != null) {
            badge2.clearAnimation();
        }
        // if (isRegisterRecev) {
        try {
            unregisterReceiver(receiver);
            unregisterReceiver(testJobDoneReceiver);
            isRegisterRecev = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // }
        if (isLoadLicense) {
            new LicenseExplorer(WalkTour.this, new String[] { "bin" }).start();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (appModel.getAppList().size() == 1 && appModel.getAppList().get(0) == WalkStruct.AppType.OperationTest) {
            // 显示开始测试
            menu.add(getString(R.string.main_about)).setIcon(android.R.drawable.ic_menu_help);
            menu.add(getString(R.string.main_exit)).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
            return super.onPrepareOptionsMenu(menu);
        }
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    // 菜单点击事件
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getTitle().equals(getString(R.string.main_about))) {
            LogUtil.w(tag, "----isShowAbout:" + isShowAbouting);
            LayoutInflater factory01 = LayoutInflater.from(WalkTour.this);
            View view01 = factory01.inflate(R.layout.aboutcompany_page, null);
            TextView versionId = (TextView) view01.findViewById(R.id.aboutusBuildId);
            versionId.setText("Version " + UtilsMethod.getCurrentVersionName(WalkTour.this));
            new BasicDialog.Builder(WalkTour.this).setTitle(R.string.main_menu_about)
                    // .setIcon(R.drawable.walktour38)
                    .setView(view01).setPositiveButton(R.string.main_menu_license_load, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            isLoadLicense = true;
                            isShowAbouting = false;
                            appModel.setCheckPowerSuccess(false);
                            finish();
                        }
                    }).setNeutralButton(R.string.main_menu_license_online, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            LogUtil.w(tag, "--click GetPowByNet--");
                            dialog.dismiss();
                            startActivity(new Intent(WalkTour.this, GetPowByNet.class));
                            finish();
                        }
                    }).setNegativeButton(R.string.update_ver_update, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(WalkTour.this, UpgradeActivity.class);
                            startActivity(intent);
                            isShowAbouting = false;
                        }
                    }).show();
        } else if (item.getTitle().equals(getString(R.string.main_exit))) {
            showQuiteDialog();
        }
        return true;
    }

    /**
     * 接收测试完成消息
     */
    private final BroadcastReceiver testJobDoneReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WalkMessage.NOTIFY_TESTJOBDONE) || intent.getAction().equals(WalkMessage.NOTIFY_INTERRUPTJOBDONE)) {
                LogUtil.i(tag, "testJobDoneReceiver" + appModel.isTestInterrupt());
                if (appModel.isTestInterrupt()) {
                    new WaitStopType(intent.getStringExtra(WalkMessage.NOTIFY_TESTJOBDONE_PARANAME)).start();
                } else {
                    current_tasking.setText("-");
                    current_tasking_num.setText("-/-");
                    current_circle_num.setText("-/-");
                    updateTestStatus();

                    findView();
                }
            } else if (intent.getAction().equals(WalkMessage.ACTION_SDCARD_STATUS)) {
                // sdcard状态改变
            } else if (intent.getAction().equals(WalkMessage.ACTION_WALKTOUR_START_TEST)) {
                // 如果为UMPC开始或自动测试开始做页面刷新处理
                if (intent.getBooleanExtra(WalkMessage.ACTION_ISAUTOTEST_PARA, false) || intent.getBooleanExtra(WalkMessage.ACTION_ISUMPCTEST_PARA, false)) {
                    findView();
                }
            }
        }
    };
    private TextView current_circle_num;
    private TextView current_tasking;
    private TextView current_tasking_num;
    private TextView mTaskStatus;
    private TextView mTaskAlarm;

    /********************************************
     * 生成界面所有view元素：包括GridView和widgets
     ********************************************/
    public void findView() {
        // 生成 一个GridView
        GridView gridview = (GridView) findViewById(R.id.main_gridview);
        // 把gridview和adapter(数据来源)关联起来
        // gridview.setSelector(R.color.main_gridview_bg);
        gridview.setAdapter(new WalkTourGridViewAdapter(this));
        gridview.setOnItemClickListener(new ItemClickListener());
        // 底部工程按钮
        btnSave = initButton(R.id.btn_save);
        btnLoad = initButton(R.id.btn_load);
        badge1 = new BadgeView(this, btnLoad);
        badge1.setText("1");
        badge1.setTextColor(getResources().getColor(R.color.white));
        badge1.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badge1.hide(true);
        initButtonNum1();
        LinearLayout testL = initLinearLayout(R.id.testMain);

        testL.setOnTouchListener(new View.OnTouchListener() {
            private int lastY = 0;
            private long startT;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                int tapTime = ViewConfiguration.getJumpTapTimeout();
                int minOffset = ViewConfiguration.getTouchSlop();
                switch (action) {
                case MotionEvent.ACTION_DOWN:
                    lastY = (int) event.getRawY();
                    startT = System.currentTimeMillis();
                    v.setPressed(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    v.setPressed(true);
                    int offsetY = -(int) (event.getRawY() - lastY);
                    if (Math.abs(offsetY) >= minOffset * 1.5f) {
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        lp.bottomMargin += offsetY;
                        int min = DensityUtil.dip2px(WalkTour.this, 60);
                        int max = ScreenUtils.getScreenHeight(WalkTour.this) - DensityUtil.dip2px(WalkTour.this, 120);
                        lp.bottomMargin = Math.min(Math.max(min, lp.bottomMargin), max);
                        v.setLayoutParams(lp);
                        lastY = (int) event.getRawY();
                        startT = System.currentTimeMillis() - tapTime;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    if (System.currentTimeMillis() - startT < tapTime) {
                            startActivity(new Intent(WalkTour.this, RunStatusActivity.class));
                        overridePendingTransition(R.anim.open_next, R.anim.close_main);
                    }
                    break;
                }
                return true;
            }
        });
        btnSave.setOnClickListener(btnListener);
        btnLoad.setOnClickListener(btnListener);
        current_tasking = initTextView(R.id.current_tasking);
        current_tasking_num = initTextView(R.id.current_tasking_num);
        current_circle_num = initTextView(R.id.current_circle_num);
        mTaskStatus = initTextView(R.id.tv_test_status);
        mTaskAlarm = initTextView(R.id.tv_task_alarm);
        if (appModel.isHuaWeiTest()) {
            current_circle_num.setVisibility(View.VISIBLE);
        } else {
            current_circle_num.setVisibility(View.GONE);
        }
    }// end method findView

    private OnClickListener btnListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()) {
            case R.id.btn_save:
                // 先判断SD卡是否可用
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(WalkTour.this, getString(R.string.sdcard_non), Toast.LENGTH_LONG).show();
                } else {
                    showMyDialog(PROJECT_SAVE, getString(R.string.main_project_save));
                }
                break;
            case R.id.btn_load:
                // 先判断SD卡是否可用
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(WalkTour.this, getString(R.string.sdcard_non), Toast.LENGTH_LONG).show();
                } else if (ApplicationModel.getInstance().isTestJobIsRun()) {// 当前正在测试时给提示
                    Toast.makeText(WalkTour.this, getString(R.string.main_project_testing), Toast.LENGTH_LONG).show();
                } else {
                    // showMyDialog(PROJECT_LOAD,
                    // getString(R.string.main_project_load));
                    jumpActivity(ShareProjectActivity.class);
                }
                break;
            }
        }
    };

    @SuppressLint("InflateParams")
    private void showMyDialog(int id, String title) {
        LayoutInflater factory = LayoutInflater.from(WalkTour.this);
        switch (id) {
        // 保存工程
        case PROJECT_SAVE:
            final View textEntryView = factory.inflate(R.layout.alert_dialog_edittext1, null);
            final EditText prjName = (EditText) textEntryView.findViewById(R.id.alert_textEditText);
            prjName.setHint(R.string.main_project_input);
            new BasicDialog.Builder(WalkTour.this).setView(textEntryView).setTitle(title).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 先判断SD卡是否可用
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(WalkTour.this, getString(R.string.sdcard_non), Toast.LENGTH_LONG).show();
                        return;
                    }
                    // 如果名字为空
                    if (prjName.getText().toString().trim().length() == 0) {
                        Toast.makeText(WalkTour.this, getString(R.string.main_project_input_empty), Toast.LENGTH_SHORT).show();
                        showMyDialog(PROJECT_SAVE, getString(R.string.main_project_save));
                    }
                    // 工程已经存在
                    /*
                     * else if(){
                     *
                     * }
                     */
                    else {
                        proManager.saveProject(prjName.getText().toString());
                    }
                }
            }).setNegativeButton(R.string.str_cancle).show();
            break;
        // 导入工程
        case PROJECT_LOAD:
            final View viewLoad = factory.inflate(R.layout.alert_dialog_listview, null);
            // ListView
            ListViewForScrollView listView = (ListViewForScrollView) viewLoad.findViewById(R.id.ListView);
            ArrayList<HashMap<String, Object>> itemArrayList = new ArrayList<HashMap<String, Object>>();// checkListItemAdapter的参数
            final ArrayList<File> projectFileList = proManager.getProjectList();
            for (File f : projectFileList) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemTitle", f.getName().substring(0, f.getName().lastIndexOf(".xml")));// android:background="@drawable/background_btn_delete"
                map.put("ItemImage", R.drawable.background_btn_delete);
                map.put(MySimpleAdapter.KEY_DELETE, f);
                itemArrayList.add(map);
            }
            MySimpleAdapter checkListItemAdapter = new MySimpleAdapter(this, itemArrayList, R.layout.listview_item_style13, new String[] { "ItemTitle", "ItemImage" }, new int[] { R.id.ItemTitle, R.id.ItemImage });
            listView.setAdapter(checkListItemAdapter);// listView用到的Adapter
            final BasicDialog alertDialog = new BasicDialog.Builder(WalkTour.this).setTitle(title).setView(viewLoad).setNegativeButton(R.string.str_cancle).create();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    // 先判断SD卡是否可用
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        final File fileProject = projectFileList.get(arg2);
                        new BasicDialog.Builder(WalkTour.this).setTitle(R.string.main_project_load).setMessage(getString(R.string.main_project_load_alert) + " " + fileProject.getName().substring(0, fileProject.getName().lastIndexOf(".xml"))).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (fileProject.exists()) {
                                    int resultCode = proManager.loadProject(fileProject);
                                    if (resultCode == ProjectManager.LOAD_RESULT_VERSIONEROR) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.import_file_format_str), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), String.format("%s %s", getString(R.string.main_project_load), getString(R.string.total_success)), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).setNegativeButton(R.string.str_cancle).show();
                    } else {
                        Toast.makeText(WalkTour.this, getString(R.string.sdcard_non), Toast.LENGTH_LONG).show();
                    }
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
            break;
        }
    }

    private void showSerialDialog(int strId) {
        new BasicDialog.Builder(WalkTour.this).setTitle(R.string.str_tip).setMessage(strId).setNeutralButton(R.string.str_return, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void getNotice() {
        if (appModel.getSelectScene() == SceneType.Perception && AppVersionControl.getInstance().isPerceptionTest()) {
            startService(new Intent(this, GetNoticeService.class));
        }
    }

    /************************************
     * 九宫格item按下事件类
     **********************************/
    class ItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Bundle bundle = new Bundle();
            Intent intent;
            switch (arg1.getId()) {
            case R.string.main_task:
                intent = new Intent(com.walktour.gui.WalkTour.this, TaskAll.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_main);
                break;
            case R.string.main_task_hw:
                intent = new Intent(com.walktour.gui.WalkTour.this, TaskAll.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_main);
                break;
            case R.string.main_stop:
            case R.string.main_start:
                LogUtil.w(tag, "--stoping:" + appModel.isTestStoping() + "--run:" + appModel.isTestJobIsRun());
                if (appModel.isTestStoping()) {
                    Toast.makeText(WalkTour.this, getString(R.string.main_testStoping), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (appModel.isTestJobIsRun()) {
                    LogUtil.d(tag, "用户点击了停止测试");
                    Toast.makeText(WalkTour.this, getString(R.string.main_testStoping), Toast.LENGTH_SHORT).show();
                    // if(!ConfigNBModuleInfo.getInstance(context).isHasNBWifiTestModel()) {
                    // // 停止测试,关闭wifi
                    // WifiManager wifiManager = (WifiManager) getApplicationContext()
                    // .getSystemService(Context.WIFI_SERVICE);
                    // if (wifiManager.isWifiEnabled()) {
                    // wifiManager.setWifiEnabled(false);
                    // }
                    // }
                    Intent interruptIntent = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
                    sendBroadcast(interruptIntent);
                } else {

                    if (appModel.getNetList().contains(WalkStruct.ShowInfoType.WOnePro)) {
                        Toast.makeText(WalkTour.this, getString(R.string.wone_start_toast), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 当前是否需要检查自动同步时间是否打开并且非电信手机
                    if (appModel.isTraceInitSucc() && !DatasetManager.isPlayback) {
                        // 如果有自动测试权限并且自动测试已经开启，给提示
                        if (!TaskListDispose.getInstance().hasEnabledTask() || (appModel.isScannerTest() ? !appModel.isScannerTestTask() : false)) {
                            Toast.makeText(WalkTour.this, R.string.main_testTaskEmpty, Toast.LENGTH_LONG).show();
                            return;
                        } else if (TaskListDispose.getInstance().checkSerialDataWlan()) {
                            showSerialDialog(R.string.main_hasWlanAndData);
                            return;
                        } else if (ConfigRoutine.getInstance().isBluetoothSync(getApplicationContext()) && !appModel.isBluetoothConnected()) {
                            showBlueToothSetDialg();
                            return;
                        } else if (TaskListDispose.getInstance().checkSerialCallData()) {
                            // 判断当前语音数据是否同时存在,如是检查是否有不可串行限制
                            if (ConfigRoutine.getInstance().isGmccVersion()) {
                                showSerialDialog(R.string.main_hasCallAndData);
                                return;
                            }
                        }
                        if (appModel.isScannerTest()) {
                            boolean enabled = BluetoothAdapter.getDefaultAdapter().isEnabled();
                            if (!enabled) {
                                new BasicDialog.Builder(WalkTour.this).setTitle(R.string.str_tip).setMessage(R.string.sc_enable_bluetooth_tip).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                                        startActivity(intent);
                                    }
                                }).setNegativeButton(R.string.no).show();
                                return;
                            }
                        }
                        if (ApplicationModel.getInstance().getAppList().contains(WalkStruct.AppType.AutomatismTest) && new ConfigAutoTest().isAutoTestOn()) {
                            showAutoTestOnDialog();
                        } else {
                            AlertWakeLock.acquire(getApplicationContext());
                            // 若权限列表中含有室内测试专项
                            if (appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
                                // 开始室内测试
                                LogUtil.i(tag, "----item_postion_building=" + item_postion_building);
                                appModel.setIndoorTest(indoor_special);
                            }
                            if (appModel.getSelectScene() == SceneType.HighSpeedRail) {// 高铁测试
                                dialog = new StartDialog(WalkTour.this, mHandler, false, null, TaskModel.FROM_TYPE_SELF, SceneType.HighSpeedRail);
                            } else if (appModel.getSelectScene() == SceneType.Metro) {// 地铁测试
                                dialog = new StartDialog(WalkTour.this, mHandler, false, "Metro", TaskModel.FROM_TYPE_SELF, SceneType.Metro);
                            } else {
                                int uploadServer = ServerManager.getInstance(context).getUploadServer();
                                SceneType sceneType = SceneType.Manual;
                                switch (uploadServer) {
                                case ServerManager.SERVER_BTU:
                                    sceneType = SceneType.BTU;
                                    break;
                                case ServerManager.SERVER_ATU:
                                    sceneType = SceneType.ATU;
                                    break;
                                }
                                dialog = new StartDialog(WalkTour.this, mHandler, false, null, TaskModel.FROM_TYPE_SELF, sceneType);
                            }
                            boolean isHuawei = appModel.isHuaWeiTest();
                            if (isHuawei)
                                dialog.checkDTOrCQT(true);
                            dialog.show();
                        }
                    } else {
                        if (!appModel.isTraceInitSucc()) {
                            com.walktour.base.util.ToastUtil.showShort(WalkTour.this, getString(R.string.main_traceIniting));
                            sendBroadcast(new Intent(WalkMessage.NOTIFY_TESTTING_WAITTRACEINITSUCC));
                            if (!appModel.isGeneralMode() && !isWaitInitTrace) {
                                new WaitTraceInitSuccess().start();
                            }
                        } else if (DatasetManager.isPlayback) {
                            Toast.makeText(WalkTour.this, getString(R.string.main_test_after_playback_closed), Toast.LENGTH_LONG).show();
                        }
                        return;
                    }
                }
                break;
            case R.string.preception_gis_analysis:// GIS分析
                jumpActivity(GisMapActivity.class);
                break;
            case R.string.main_info: // 5
                /*
                 * intent = new Intent(com.walktour.gui.WalkTour.this, InfoTabActivity.class); if (TraceInfoInterface.currentShowTab ==
                 * WalkStruct.ShowInfoType.Map || TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Default)
                 * intent.putExtra(InfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_MAP); else
                 * intent.putExtra(InfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_OTHER);
                 */
                // 2018/7/12 切换新地图
                intent = new Intent(com.walktour.gui.WalkTour.this, NewInfoTabActivity.class);
                if (TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Map || TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Default)
                    intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_MAP);
                else
                    intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_OTHER);
                intent.putExtra("isReplay", false);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_main);
                break;
            case R.string.main_setting:
                bundle.putInt(Sys.CURRENTTAB, 0);
                jumpActivity(Sys.class, bundle);
                break;
            case R.string.main_result_main:
                if (appModel.isTestJobIsRun()) {
                    ToastUtil.showToastShort(context, R.string.str_testing);
                    return;
                }
                jumpActivity(TotalDetailActivity.class, bundle);
                break;
            case R.string.main_gps:
                intent = new Intent(com.walktour.gui.WalkTour.this, Gps.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_main);
                break;
            case R.string.main_file:
                jumpActivity(FileManagerFragmentActivity.class, bundle);
                break;
            case R.string.data_replay:
                if (checkPermission()) {
                    ToastUtil.showToastShort(getApplicationContext(), R.string.open_auto_test_permission_toast);
                    return;
                }
                if (appModel.isTestJobIsRun()) {
                    Toast.makeText(getApplicationContext(), R.string.str_testing, Toast.LENGTH_LONG).show();
                    return;
                }

                if (FloatWindowManager.isWindowShowing()) {
                    ToastUtil.showToastShort(context, R.string.data_manage_preview_event_fail);
                    return;
                }
                intent = new Intent(com.walktour.gui.WalkTour.this, NewInfoTabActivity.class);
                if (TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Map || TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Default)
                    intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_MAP);
                else
                    intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_OTHER);
                intent.putExtra("isReplay", true);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_main);
                break;
            case R.string.main_pause:
            case R.string.main_continue:
                if (isDoing) {
                    ToastUtil.showToastShort(context, R.string.wait);
                    return;
                }
                if (appModel.isTestPause()) {// 继续
                    appModel.setTestPause(false);
                    new ContinueThread().start();
                } else {// 暂停
                    appModel.setTestPause(true);
                    new PauseThread().start();
                }
                mHandler.sendMessage(mHandler.obtainMessage(REFLESH_VIEW));
                break;
            case R.string.work_order:
                jumpActivity(WorkOrderDictActivity.class);
                break;
            case R.string.work_order_ah:
                jumpActivity(com.walktour.gui.workorder.ah.WorkOrderMainActivity.class);
                break;
            case R.string.work_order_hw:
                jumpActivity(com.walktour.gui.workorder.hw.WorkOrderMainActivity.class);
                break;
            case R.string.intelligent_analysis:
                if (appModel.isTestJobIsRun()) {
                    Toast.makeText(getApplicationContext(), R.string.csfb_analysis_disable_bytest, Toast.LENGTH_LONG).show();
                } else {
                    jumpActivity(AnalysisMainActivity.class);
                }
                break;
            case R.string.single_station_test:
                jumpActivity(StationActivity.class);
                break;
            case R.string.single_station_survey:
                jumpActivity(SurveyActivity.class);
                break;
            case R.string.single_station_report:
                jumpActivity(StationReportActivity.class);
                break;
            case R.string.single_station_test_resource:
                String ip = mServer.getDownloadFleetIp();
                if (!mServer.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.work_order_fleet_ip_null), Toast.LENGTH_SHORT).show();
                    return;
                }
                jumpActivity(ResourceOverviewActiviity.class);
                break;
            case R.string.single_station_test_quality:
                ip = mServer.getDownloadFleetIp();
                if (!mServer.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.work_order_fleet_ip_null), Toast.LENGTH_SHORT).show();
                    return;
                }
                jumpActivity(QualityOverviewActivity.class);
                break;
            case R.string.single_station_test_business:
                ip = mServer.getDownloadFleetIp();
                if (!mServer.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.work_order_fleet_ip_null), Toast.LENGTH_SHORT).show();
                    return;
                }
                jumpActivity(BusinessOverviewActivity.class);
                break;
            case R.string.title_notice:
                jumpActivity(MessageListActivity.class);
                break;
            case R.string.survey_task_manager:
                jumpActivity(SurveyTaskClaimingActivity.class);
                break;
            case R.string.survey_task_completion:
                jumpActivity(SurveyTaskFinishingActivity.class);
                break;
            case R.string.survey_task_query:
                jumpActivity(SurveyTaskQueryActivity.class);
                break;
            default:
                break;
            }

            updateTestStatus();
        }
    }

    /***
     * 暂停
     */
    private class PauseThread extends Thread {
        public PauseThread() {
        }

        @Override
        public void run() {
            isDoing = true;

            // 暂停不刷新数据
            boolean isPauseNotSaveData = ConfigRoutine.getInstance().isPuaseNoData(getApplicationContext());
            // 暂停不刷新数据
            boolean isPauseSaveData = ConfigRoutine.getInstance().isPuaseSaveData(getApplicationContext());


            if (isPauseNotSaveData) {
                EventManager.getInstance().addTagEvent(getApplicationContext(), System
                        .currentTimeMillis(), "Logging Pause.");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent interruptIntent = new Intent(WalkMessage.Action_Walktour_Test_Puase);
            sendBroadcast(interruptIntent);
            if (isPauseSaveData) {//刷新数据
                DatasetManager.getInstance(getApplicationContext()).setCommand(1, 0);
            } else if(isPauseNotSaveData){//不刷新数据
                DatasetManager.getInstance(getApplicationContext()).configPushToDataSet(false);
                DatasetManager.getInstance(getApplicationContext()).setCanWriteFile(false);
            }
            isDoing = false;
        }
    }

    /***
     * 继续
     */
    private class ContinueThread extends Thread {
        public ContinueThread() {
        }

        @Override
        public void run() {
            isDoing = true;
            Intent interruptIntent = new Intent(WalkMessage.Action_Walktour_Test_Continue);
            sendBroadcast(interruptIntent);

            // 暂停不保存数据
            boolean isPauseNotSaveData = ConfigRoutine.getInstance().isPuaseNoData(getApplicationContext());
            // 暂停保存数据
            boolean isPauseSaveData = ConfigRoutine.getInstance().isPuaseSaveData(getApplicationContext());

            if(isPauseSaveData) {//刷新数据
                DatasetManager.getInstance(getApplicationContext()).setCommand(3, 0);
            } else if(isPauseNotSaveData){//不刷新数据
                DatasetManager.getInstance(getApplicationContext()).setCanWriteFile(true);
                DatasetManager.getInstance(getApplicationContext()).configPushToDataSet(true);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                EventManager.getInstance().addTagEvent(getApplicationContext(), System
                        .currentTimeMillis(), "Logging Continue.");
            }
            isDoing = false;
        }
    }

    /**
     * 等待串口初始化成功，如果一定时间未成功则重启串口
     *
     * @author tangwq
     */
    private class WaitTraceInitSuccess extends Thread {
        public void run() {
            isWaitInitTrace = true;
            int waitTraceInitTime = 0;
            while (!appModel.isTraceInitSucc() && waitTraceInitTime < 100) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                waitTraceInitTime++;
                LogUtil.w(tag, "--waitTraceInitTime:" + waitTraceInitTime + "---" + (waitTraceInitTime % 25 == 0));
                if (waitTraceInitTime % 25 == 0) { // 20S重新初始化一次Trace 口
                    Intent reStartTraceInit = new Intent(WalkMessage.redoTraceInit);
                    sendBroadcast(reStartTraceInit);
                }
            }
            isWaitInitTrace = false;
        }
    }

    private void showAutoTestOnDialog() {
        new BasicDialog.Builder(WalkTour.this).setTitle(R.string.str_tip).setMessage(R.string.main_autoison).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(WalkTour.this, Fleet.class));
            }
        }).setNegativeButton(R.string.str_cancle).show();
    }

    /**
     * 显示蓝牙设置对话框
     */
    private void showBlueToothSetDialg() {
        new BasicDialog.Builder(WalkTour.this).setTitle(R.string.str_tip).setMessage(R.string.sys_setting_bluetooth_connect_faild).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(WalkTour.this, SysRoutineActivity.class));
            }
        }).setNegativeButton(R.string.str_cancle).show();
    }

    private void showQuiteDialog() {
        ApplicationModel appModel = ApplicationModel.getInstance();
        String alert = getString(R.string.main_menu_exit_alert);
        // 如果当前测试未完成
        if (appModel.isTestJobIsRun() || appModel.isTestStoping() || FleetService.isFleeterRunning()) {
            alert = getString(R.string.main_menu_exit_alert_testing);
        }
        new BasicDialog.Builder(WalkTour.this).setTitle(R.string.exit).setIcon(R.drawable.icon_info).setMessage(alert).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startService(new Intent(WalkTour.this, Killer.class));
                dialog.dismiss();
                finish();
            }
        }).setNegativeButton(R.string.str_cancle).show();
    }

    class WaitStopType extends Thread {
        String saveFileLocusPath;

        public WaitStopType(String saveFileLocusPath) {
            this.saveFileLocusPath = saveFileLocusPath;
        }

        public void run() {
            while (appModel.isTestStoping() || appModel.isTestJobIsRun()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.w(tag, "*****reset gps to false and close gps");
            // 中断测试的话，GPS测试状态重置，并关闭GPS
            appModel.setGpsTest(false);
            gpsInfo.releaseGps(getApplicationContext(), WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
            Message msg = mHandler.obtainMessage(REFLESH_VIEW);
            mHandler.sendMessage(msg);
            if (appModel.isFuJianTest())
                return;
            if (appModel.isSaveMapLocas()) {
                // 2015移动招标要求在数据管理里可以查看文件轨迹，特做跳转到地图界面对地图做导出处理
                TraceInfoInterface.isSaveFileLocus = true;
                TraceInfoInterface.saveFileLocusPath = this.saveFileLocusPath;
                TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Map;
                /*
                 * Intent mapIntent = new Intent(getApplicationContext(), InfoTabActivity.class);
                 * mapIntent.putExtra(InfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_MAP);
                 */
                // 2018/7/12 切换新地图
                Intent mapIntent = new Intent(getApplicationContext(), NewInfoTabActivity.class);
                mapIntent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_MAP);
                startActivity(mapIntent);
            }
        }
    }

    class WaitStartType extends Thread {
        public void run() {
            while (!appModel.isTestJobIsRun()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message msg = mHandler.obtainMessage(REFLESH_VIEW);
            mHandler.sendMessage(msg);
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<WalkTour> reference;

        public MyHandler(WalkTour wt) {
            this.reference = new WeakReference<WalkTour>(wt);
        }

        @Override
        public void handleMessage(Message msg) {
            WalkTour wt = this.reference.get();
            if (msg.what == REFLESH_VIEW) {
                wt.findView();
            }
        }
    }

    private Handler mHandler = new MyHandler(this);

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initButtonNum1();
        initButtonNum2();
        if (UtilsMethod.getSDKVersionNumber() >= Build.VERSION_CODES.M && requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                ToastUtil.showToastShort(this, R.string.check_permission_denied);
            } else {
                ToastUtil.showToastShort(this, R.string.check_permission_allowed);
            }
        } else if (requestCode == REQ_TAKE_PHOTO && resultCode == RESULT_OK && dialog != null) {
            File latestfile = new File(data.getStringExtra(CaptureImg.MAP_PATH));
            dialog.showPhoto(latestfile);
        } else if (data != null && data.getExtras() != null) {
            String result = data.getExtras().getString("resultSelectRoute");
            if (requestCode == StartDialog.requestRailRouteCode || requestCode == StartDialog.requestHsCode) {
                if (resultCode == RESULT_OK) {
                    // if (null != result && result.length() > 0) {
                    // dialog.updateRailWay(result.substring(0, result.lastIndexOf(".")));
                    dialog.updateRailWay();
                    // }
                }
            } else if (requestCode == StartDialog.requestMetroRouteCode || requestCode == StartDialog.requestCityCode) {
                dialog.updateMetroCityAndRoute();
            } else if (requestCode == StartDialog.requestInnsmapCode) {
                dialog.updateInnsmap();
            }
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public void onRefreshed(RefreshType refreshType, Object object) {
        switch (refreshType) {
            case ACTION_WALKTOUR_TIMER_CHANGED:
                if (appModel.isTestJobIsRun()) {
                    StateInfoModel stateInfo = TraceInfoInterface.traceData.getStateInfoNoQuery();
                    current_tasking.setText(stateInfo.getCurrentJon());
                    current_tasking_num.setText((stateInfo.getCurTestTime().equals("") ? "-" : stateInfo.getCurTestTime()) + "/" + (stateInfo.getCurAllTimes().equals("") ? "-" : stateInfo.getCurAllTimes()));
                    current_circle_num.setText((stateInfo.getCurTestCircles().equals("") ? "-" : stateInfo.getCurTestCircles()) + "/" + (stateInfo.getCurAllCircles().equals("") ? "-" : stateInfo.getCurAllCircles()));
                    updateTestStatus();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        case R.id.weifuwu:
            if (appModel.getSelectScene() == SceneType.Perception) {
                Bundle bundle = new Bundle();
                bundle.putInt(Sys.CURRENTTAB, 0);
                jumpActivity(Sys.class, bundle);
            } else {
                try {
                    badge2.hide();
                    ShareDataBase.getInstance(context).updateFileStatusToStart(-1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.jumpActivityForResult(WeiMainActivity.class, 0x09823);
            }
            break;
        default:
            break;
        }
    }

    /***
     * 广播接收器，更新接收按钮上的数组
     *
     * @author weirong.fan
     *
     */
    private class FileReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ShareCommons.SHARE_ACTION_1)) {
                initButtonNum1();
            }
            initButtonNum2();
        }
    }

    /**
     * 接收到广播后
     */
    private void initButtonNum2() {
        try {
            List<ShareFileModel> lists2 = ShareDataBase.getInstance(context).fetchAllFilesByFileStatusAndFileType(-1, new int[] { ShareFileModel.FILE_STATUS_INIT });
            if (lists2.size() > 0) {
                badge2.setText(lists2.size() + "");
                badge2.show(true);
            } else {
                badge2.hide(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initButtonNum1() {
        try {
            List<ShareFileModel> lists = ShareDataBase.getInstance(context).fetchAllFilesByFileStatusAndFileType(ShareFileModel.FILETYPE_PROJECT, new int[] { ShareFileModel.FILE_STATUS_INIT });
            if (lists.size() > 0) {
                badge1.setText(lists.size() + "");
                badge1.show(true);
            } else {
                badge1.hide(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * czc：更新当前测试状态
     */
    private void updateTestStatus() {
        StringBuilder sb = new StringBuilder();
        ArrayList<AlarmModel> list = AlertManager.getInstance(this).getAlarmListClone();
        if (list.size() > 0) {
            mTaskAlarm.setVisibility(View.VISIBLE);
        } else {
            mTaskAlarm.setVisibility(View.GONE);
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            AlarmModel model = list.get(i);
            String des = model.getAlarm().getDescription(this);
            if (i != 0) {
                sb.append(des + "\n");
            } else {
                sb.append(des);
            }
        }
        mTaskAlarm.setText(sb.toString());

        if (appModel.isTestJobIsRun() || appModel.isTesting()) {
            mTaskStatus.setText(R.string.testing);
            if (appModel.isTestPause()) {
                mTaskStatus.setText(R.string.test_pause);
            }
        } else if (appModel.isTestStoping()) {
            mTaskStatus.setText(R.string.test_stop);
        } else {
            mTaskStatus.setText(R.string.un_test);
            mTaskAlarm.setVisibility(View.GONE);
        }
    }
}
