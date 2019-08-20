package com.walktour.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dingli.droidwall.DroidWallUtil;
import com.dingli.ott.util.OttUtil;
import com.dinglicom.wifi.WifiAutoConnectManager;
import com.walktour.Utils.AppVersionControl;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.BuildPower;
import com.walktour.Utils.JudgeProcess;
import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalktourConst;
import com.walktour.Utils.excel.manger.CountTimeManger;
import com.walktour.base.util.LogUtil;
import com.walktour.control.adapter.BaseDisplayAdapter;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigNBModuleInfo;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.config.PageManager;
import com.walktour.control.config.ServerManager;
import com.walktour.control.instance.AlertManager;
import com.walktour.customView.ListViewForScrollView;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.about.AboutActivity;
import com.walktour.gui.applet.LicenseExplorer;
import com.walktour.gui.fleet.Fleet;
import com.walktour.gui.indoor.IndoorTask;
import com.walktour.gui.main.adapter.GridViewAdapter;
import com.walktour.gui.main.adapter.ViewPagerAdapter;
import com.walktour.gui.main.model.TestType;
import com.walktour.gui.mutilytester.MutilyTester;
import com.walktour.gui.setting.SysMap;
import com.walktour.gui.setting.sysroutine.NBSelectActivity;
import com.walktour.gui.task.activity.scannertsma.ennnnum.WifiType;
import com.walktour.service.ApplicationInitService;
import com.walktour.service.OppoCustomService;
import com.walktour.service.app.Killer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/****
 * 主界面
 */

public class Main extends BasicActivity implements OnInitListener, AdapterView.OnItemSelectedListener {
    private static final String tag = "Main";
    private static final int REQ_TTS_STATUS_CHECK = 0;
    private Button btnExit = null;
    private boolean hasCheckTTS = false;
    /**上下文***/
    private Context mContext=Main.this;
    //关于按钮
    private Button aboutBtn;
    //验证权限成功
    private final int powerSucc = 0;
    private final int finishApp = 1;
    private final int killApp = 2;
    private final int alert = 3;
    /**打开wifi窗口**/
    private final int openWifiDialog=4;
    private final int openGPSDialog = 5;
    private boolean isLoadLicenseFromLoal = false;
    private boolean isShowAbouting = false;
    private ApplicationModel appModel = ApplicationModel.getInstance();
    private ProgressDialog progressDialog;
    private static final String UYou_Trace = "com.dingli.uyou.TraceInfo";
    private int uyou_stop = 100; // U-You已经停止
    private final int ACTIVI_TIME = 7;

    private static final int SHOW_AUTHENTICATION_DIALOG = 1001;
    private static final int SHOW_TTS_DIALOG = 1002;
    private static final int SHOW_LICENCE_DIALOG = 1003;

    private ViewPager mPager;
    private List<View> mPagerList;
    private List<TestType> mDatas;
    private LinearLayout mLlDot;
    private LayoutInflater inflater;

    SharePreferencesUtil sharePreferencesUtil;
    private BasicDialog dialog;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_wifi_type:
                sharePreferencesUtil.saveInteger(WalktourConst.WifiType.WIFI_TYPE,position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }
    /**
     * 总的页数
     */
    private int pageCount;
    /**
     * 每一页显示的个数
     */
    private int pageSize = 6;
    /**
     * 当前显示的是第几页
     */
    private int curIndex = 0;
    /**是否启动了NB检测,控制多次启动**/
    private boolean isStartNB=false;
    private boolean isStartInitService=false;
    private int GPS_REQUEST_CODE = 110;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.base_progress);
		regisBroadcast();
		DroidWallUtil.enable(this);// 开启禁止其他应用联网
		if(AppVersionControl.getInstance().isTelecomInspection() && !OttUtil.hasServicePermission(this,OppoCustomService.class)){
			OttUtil.openServicePermissonCompat(this,OppoCustomService.class);
		}
        CountTimeManger.getInstance(this).setStartTime();
        sharePreferencesUtil=SharePreferencesUtil.getInstance(this);
	}


    @SuppressWarnings("deprecation")
    @Override
    public void onResume() {
        super.onResume();
        LogUtil.w(tag, "---onResume checkP:" + appModel.isCheckPowerSuccess() + "--powerByNet:" +
                appModel.isPowerByNet());
        if (appModel.isCheckPowerSuccess()) { // 已经通过鉴权
            Message msg = mHandler.obtainMessage(powerSucc);
            mHandler.sendMessage(msg);
            if (appModel.getActiveTime() <= ACTIVI_TIME && !appModel.isShowActive()) {
                showDialog(SHOW_LICENCE_DIALOG);
                appModel.setShowActive(true);
            }


        } else {
            if (!appModel.isCheckPowerRunning()) {
                appModel.setCheckPowerRunning(true);
                new RunEnvironment(this).start();
            }
        }

    }

    /**
     * 线程一直等待，直到U-You停止后启动Walktour
     *
     * @author Administrator
     */
    private class WaitThread extends Thread {
        @Override
        public void run() {
            JudgeProcess process = new JudgeProcess();
            boolean uyouIsRun = true;
            int waitKillUyou = 0;
            while (uyouIsRun && waitKillUyou < 180) {
                ThreadWait(1000);
                waitKillUyou += 1;
                LogUtil.w(tag, "--wait kill uyou:" + waitKillUyou);
                uyouIsRun = process.isRunning(UYou_Trace);
                if (!uyouIsRun) {
                    LogUtil.w(tag, "--uyou kill success wait 2S-");
                    ThreadWait(2000);
                    uyouIsRun = process.isRunning(UYou_Trace);

                    // 如果这里uyou进程还存在，重发停杀UYOU进程消息
                    if (uyouIsRun) {
                        LogUtil.w(tag, "--resend kill uyou message");
                        sendBroadcast(new Intent(WalkMessage.START_WALKTOUR));
                    }
                }
            }

            Message msg = mHandler.obtainMessage(uyou_stop, waitKillUyou < 180);
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 等待时间
     */
    private void ThreadWait(int second) {
        try {
            Thread.sleep(second);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册广播接收器
     */
    private void regisBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.ACTION_MAIN_INIT_SUCCESS);
        registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.w(tag, "---action=" + action);
            // U-You停止
            if (action.equals(WalkMessage.KILL_UYOU)) {
                new WaitThread().start();
            } else if (action.equals(WalkMessage.ACTION_MAIN_INIT_SUCCESS)) {
                if (Main.this.findViewById(R.id.progressBar1) == null) {
                    setContentView(R.layout.main);
                    findView();
                    initViewPage();
                }
                Main.this.findViewById(R.id.progressBar1).setVisibility(View.GONE);

            }
        }
    };

    /**
     * 初始化ViewPage
     */
    private void initViewPage() {
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mLlDot = (LinearLayout) findViewById(R.id.ll_dot);
        //初始化数据源
        initDatas();
        inflater = LayoutInflater.from(this);
        //总的页数=总数/每页数量，并取整
        pageCount = (int) Math.ceil(mDatas.size() * 1.0 / pageSize);
        mPagerList = new ArrayList<View>();
        for (int i = 0; i < pageCount; i++) {
            // 每个页面都是inflate出一个新实例
            GridView gridView = (GridView) inflater.inflate(R.layout.gridview, mPager, false);
            gridView.setAdapter(new GridViewAdapter(this, mDatas, i, pageSize));
            mPagerList.add(gridView);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TestType test=mDatas.get((int) id);
                    if (test.getType()!=null){
                        appModel.setSelectScene(test.getType());
                    }
                    if (test.getIntent()!=null){
                        startActivity(test.getIntent());
                        overridePendingTransition(R.anim.open_next, R.anim.close_main);
                    }
                }
            });
        }
        if (pageCount<=1){
            mLlDot.setVisibility(View.GONE);
        }
        //设置适配器
        mPager.setAdapter(new ViewPagerAdapter(mPagerList));
        //设置圆点
        setOvalLayout();
    }

    /**
     * 初始化数据源
     */
    private void initDatas() {
        mDatas = new ArrayList<TestType>();
        Intent intent;
        Bundle bundle = new Bundle();
        TestType test;
        if (appModel.getAppList().contains(WalkStruct.AppType.AutomatismTest)) {
            intent = new Intent(Main.this, Fleet.class);
            test = new TestType();
            test.setIconRes(R.drawable.main_auto);
            test.setIntent(intent);
            test.setName(getString(R.string.main_autotest));
            test.setType(WalkStruct.SceneType.Auto);
            mDatas.add(test);
        }
        if (appModel.getAppList().contains(WalkStruct.AppType.OperationTest)) {
            intent = new Intent(Main.this, WalkTour.class);
            bundle.putBoolean("indoor_special", false);
            intent.putExtras(bundle);
            test = new TestType();
            test.setIconRes(R.drawable.main_manual);
            test.setIntent(intent);
            test.setName(getString(R.string.main_manualtest));
            test.setType(WalkStruct.SceneType.Manual);
            mDatas.add(test);
        }
        if (appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
            intent = new Intent(Main.this, IndoorTask.class);
            test = new TestType();
            test.setIconRes(R.drawable.main_in_door);
            test.setIntent(intent);
            test.setName(getString(R.string.main_indoortest));
            test.setType(null);
            mDatas.add(test);
        }
        if (appModel.getAppList().contains(WalkStruct.AppType.MutilyTester)) {
            intent = new Intent(Main.this, MutilyTester.class);
            test = new TestType();
            test.setIconRes(R.drawable.main_mutily);
            test.setIntent(intent);
            test.setName(getString(R.string.main_mutilytester));
            test.setType(WalkStruct.SceneType.MultiTest);
            mDatas.add(test);
        }
        //单站验证
        if (appModel.isSingleStationTest()) {
            intent = new Intent(Main.this, WalkTour.class);
            bundle.putBoolean("indoor_special", false);
            intent.putExtras(bundle);
            test = new TestType();
            test.setIconRes(R.drawable.main_single_station);
            test.setIntent(intent);
            test.setName(getString(R.string.str_scene_type_name_danzhan));
            test.setType(WalkStruct.SceneType.SingleSite);
            mDatas.add(test);
        }
        //感知测试
        if (AppVersionControl.getInstance().isPerceptionTest()) {
            intent = new Intent(Main.this, WalkTour.class);
            bundle.putBoolean("indoor_special", false);
            intent.putExtras(bundle);
            test = new TestType();
            test.setIconRes(R.drawable.perception_test);
            test.setIntent(intent);
            test.setName(getString(R.string.str_scene_type_name_perception));
            test.setType(WalkStruct.SceneType.Perception);
            mDatas.add(test);
        }
        //高铁权限
        if (appModel.getAppList().contains(WalkStruct.AppType.HighSpeedRail)) {
            intent = new Intent(Main.this, WalkTour.class);
            bundle.putBoolean("indoor_special", false);
            intent.putExtras(bundle);
            test = new TestType();
            test.setIconRes(R.drawable.main_gaotie);
            test.setIntent(intent);
            test.setName(getString(R.string.gaotie_project_test));
            test.setType(WalkStruct.SceneType.HighSpeedRail);
            mDatas.add(test);
        }
        //地铁权限
        if (appModel.getAppList().contains(WalkStruct.AppType.Metro)) {
            intent = new Intent(Main.this, WalkTour.class);
            bundle.putBoolean("indoor_special", false);
            intent.putExtras(bundle);
            test = new TestType();
            test.setIconRes(R.drawable.main_ditie);
            test.setIntent(intent);
            test.setName(getString(R.string.metro_test));
            test.setType(WalkStruct.SceneType.Metro);
            mDatas.add(test);
        }
    }

    //	/**
//	 * 设置圆点
//	 */
    public void setOvalLayout() {
        for (int i = 0; i < pageCount; i++) {
            mLlDot.addView(inflater.inflate(R.layout.dot, null));
        }
        // 默认显示第一页
        mLlDot.getChildAt(0).findViewById(R.id.v_dot)
                .setBackgroundResource(R.drawable.dot_selected);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int position) {
                // 取消圆点选中
                mLlDot.getChildAt(curIndex)
                        .findViewById(R.id.v_dot)
                        .setBackgroundResource(R.drawable.dot_normal);
                // 圆点选中
                mLlDot.getChildAt(position)
                        .findViewById(R.id.v_dot)
                        .setBackgroundResource(R.drawable.dot_selected);
                curIndex = position;
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private class RunEnvironment extends Thread {
        private String deviceId = "";

        public RunEnvironment(Context context) {
            deviceId = MyPhoneState.getInstance().getMyDeviceId(context);
        }

        public void run() {
            if (appModel.isCheckPowerSuccess()) { // 已经通过鉴权
                Message msg = mHandler.obtainMessage(powerSucc);
                mHandler.sendMessage(msg);
            } else {

                boolean hasPower = false;

                int checkPower = new BuildPower().checkUserPower(getApplicationContext(), deviceId);
                LogUtil.w(tag, "--checkPower:" + checkPower + "--" + deviceId);
                Message msg;
                if (checkPower == 0) {
                    // 鉴定成功
                    hasPower = true;
                }
                if (hasPower) {
                    // 鉴定成功
                    msg = mHandler.obtainMessage(powerSucc);
                    mHandler.sendMessage(msg);
                } else {
                    // 提示用户手动加载License
                    msg = mHandler.obtainMessage(alert, String.valueOf(checkPower));
                    msg.sendToTarget();
                }
            }

            appModel.setCheckPowerRunning(false);
        }

    }

    private void showProgressDialog(String message, boolean cancleable) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(cancleable);
        progressDialog.show();
    }

    private class MyHandler extends Handler {
        private WeakReference<Main> refenence;

        public MyHandler(Main main) {
            this.refenence = new WeakReference<Main>(main);
        }

        @Override
        public void handleMessage(Message msg) {
            final Main main = this.refenence.get();
            if (msg.what == powerSucc) { // 鉴权成功
                boolean uyouIsRun = new JudgeProcess().isRunning(UYou_Trace);
                LogUtil.w(tag, "----isUYouRun=" + uyouIsRun);
                new PageManager(main.getApplicationContext(), true);
                // 如果启动是UYou,那么会强制walktour回到首页，此时就不能弹出"停止UYou"的进度框，只有是在启动Walktour时才能弹出对话框
                if (!WalkBroadcastReceiver.uyouReadyStart) {
                    // 如果UYou正在运行
                    if (uyouIsRun) {
                        new BasicDialog.Builder(main).setTitle(R.string.str_tip)
                                .setMessage(R.string.str_startwalktour)
                                .setPositiveButton(R.string.str_ok, new DialogInterface
                                        .OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        LogUtil.w(tag, "----notify walktour ready to start");
                                        main.sendBroadcast(new Intent(WalkMessage.START_WALKTOUR));
                                        main.showProgressDialog(main.getString(R.string
                                                .str_stopuyou), false);
                                        main.new WaitThread().start();
                                    }
                                }).setNegativeButton(R.string.str_cancle, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                main.finish();
                            }
                        }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent
                                    event) {
                                dialog.dismiss();
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    main.finish();
                                }
                                return true;
                            }
                        }).create().show();
                    }
                    // 如果UYou没有运行
                    else {
                        main.checkPowerSuccess();
                    }
                }
                // 如果是要启动uyou，在walktour这端显示停止的进度对话框
                else {
                    main.showProgressDialog(main.getString(R.string.main_stopwalktour), false);
                }
            } else if (msg.what == finishApp) { // 提示鉴权失败
                int recheck = (Integer) msg.obj;
                String powerFaild = "";
                switch (recheck) {
                    case WalkCommonPara.POWER_LICENSE_TIME_OUT:
                        powerFaild = main.getString(R.string.main_license_timelimit_out);
                        break;
                    default:
                        powerFaild = main.getString(R.string.main_license_check_faild);
                        break;
                }
                Toast.makeText(main, powerFaild, Toast.LENGTH_LONG).show();
                main.finish();
            } else if (msg.what == killApp) { // 关闭当前应用
                Intent intent = new Intent(main, Killer.class);
                main.startService(intent);
                main.finish();
            } else if (msg.what == alert) {
                main.startActivity(new Intent(main, AboutActivity.class));
            } else if (msg.what == uyou_stop) {
                if (main.progressDialog != null) {
                    main.progressDialog.dismiss();
                }

                LogUtil.w(tag, "--kill uyou result:" + msg.obj.toString());
                if (msg.obj.toString().equals("true")) {
                    main.checkPowerSuccess();
                } else {
                    main.finish();
                }
            } else if(msg.what==openWifiDialog){
                WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                if (null != wifiManager) {
                    List<ScanResult> list = wifiManager.getScanResults();
                    if (null != list && list.size() > 0) {
                        ApSelectAdapter adapter = new ApSelectAdapter(list);
                        openDialog(adapter, list, -1);
                    } else {
                        ToastUtil.showToastShort(mContext, getString(R.string.sys_wifi_aplist) + "");
                        doStartInitService();
                    }
                }else{
                    doStartInitService();
                }
            }
        }

        /**
         * 热点选择界面
         */
        private class ApSelectAdapter extends BaseAdapter {
            private List<ScanResult> listSR;

            public ApSelectAdapter(List<ScanResult> listSR) {
                super();
                this.listSR = listSR;
            }

            @Override
            public int getCount() {
                return listSR == null ? 0 : listSR.size();
            }

            @Override
            public Object getItem(int position) {
                return listSR.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ScanResult model = listSR.get(position);
                ApItem itemView = null;
                if (convertView == null) {
                    itemView = new ApItem();
                    convertView = inflater.inflate(R.layout.task_ap_select_item, parent, false);
                    itemView.wifiAP = (TextView) convertView.findViewById(R.id.wlanapname);
                    itemView.wifiStrength = (TextView) convertView.findViewById(R.id.wlanapsinglestrength);
                    convertView.setTag(itemView);
                } else {
                    itemView = (ApItem) convertView.getTag();
                }
                itemView.wifiAP.setText(model.SSID);
                itemView.wifiStrength.setText(model.level + "dbm");
                return convertView;
            }
        }

        /**
         * 热点信息
         */
        private final class ApItem {
            public TextView wifiAP;
            public TextView wifiStrength;
        }
    }

    private Handler mHandler = new MyHandler(this);

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        BasicDialog.Builder dialog = new BasicDialog.Builder(Main.this);
        switch (id) {
            case SHOW_AUTHENTICATION_DIALOG:
                dialog.setIcon(android.R.drawable.ic_input_get).setTitle("License")
                        .setMessage(R.string.main_menu_license_fail)
                        .setPositiveButton(R.string.main_menu_license_load, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                installWalkSetting();
                                isLoadLicenseFromLoal = true;
                                appModel.setCheckPowerSuccess(false);
                                if (isLoadLicenseFromLoal) {
                                    new LicenseExplorer(Main.this, new String[]{"bin"}).start();
                                }
                            }
                        }).setNeutralButton(R.string.main_menu_license_online, new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                installWalkSetting();
                                appModel.setCheckPowerSuccess(false);
                                startDownloadLicense();
                            }
                        }).setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Message msg = mHandler.obtainMessage(killApp);
                        mHandler.sendMessage(msg);
                    }
                });
                break;

            case SHOW_TTS_DIALOG:
                dialog.setMessage(R.string.sys_alarm_tts_data)
                        .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent dataIntent = new Intent();
                                dataIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                                startActivity(dataIntent);
                            }
                        }).setNegativeButton(R.string.str_cancle);
                break;

            case SHOW_LICENCE_DIALOG:
                dialog.setTitle(R.string.str_tip)
                        .setMessage(String.format(getString(R.string
                                .main_license_timelimit_activetime), appModel.getActiveTime()))
                        .setNegativeButton(R.string.str_cancle);
                break;

            default:
                break;
        }

        return dialog.create();
    }

    // 用户监权成功，进入主界面，如果用户只拥有监控，业务，自动中的一项时，直接进入相应功能的首页
    private void checkPowerSuccess() {

        if (appModel.isNB()) {//有NB权限直接启动服务,测试本机
            new ThreadNB().start();
        }else{
            doStartInitService();
        }
        setContentView(R.layout.main);
        findView();// 生成界面
        initViewPage();

        if (!hasCheckTTS) {
            try {
                // 检查TTS数据是否已经安装并且可用
                Intent checkIntent = new Intent();
                checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                startActivityForResult(checkIntent, REQ_TTS_STATUS_CHECK);
            } catch (Exception e) {
                e.printStackTrace();
            }

            hasCheckTTS = true;
        }
    }

    private class ThreadNB extends Thread {
        @Override
        public void run() {
            super.run();
            if(isStartNB)
                return;
            isStartNB=true;
            //有NB模块权限的话连续检查5次,防止没有检查到的情况
            if (appModel.isNB()&&appModel.getWifiType(Main.this)==WifiType.NB) {//NB权限
                WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()) {//开启了wifi则走wifi通道
                    //关闭数据开关
                    UtilsMethod.runRootCommand(String.format("svc data %s&", "disable"));
                    if (checkGPSIsOpen()) {//定位服务是否开启,开启直接选择wifi
                        selectWifi();
                    } else {
                        if(UtilsMethod.openGPSService(mContext)){
                            selectWifi();
                        }else {
                            toOpenGpsSetting();
                        }
                    }
                } else {//否则走USB连接通道
                    checkNBUSB();
                }
            }
        }
    }

    /***
     * 选择wifi
     */
    private void selectWifi()
    {
        Message message = new Message();
        message.what = openWifiDialog;
        mHandler.sendMessage(message);
    }

    private void toOpenGpsSetting(){
        Message message = new Message();
        message.what = openGPSDialog;
        mHandler.sendMessage(message);
    }
    /**
     * 检测GPS是否打开
     *
     * @return
     */
    private boolean checkGPSIsOpen()
    {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    /**
     * 跳转GPS设置
     */
    private void openGPSSettings()
    {
        //没有打开则弹出对话框
        AlertDialog.Builder dialogx=new AlertDialog.Builder(mContext)
                .setTitle(R.string.str_tip)
                .setMessage(R.string.gpsNotifyMsg)
                // 拒绝, 退出应用
                .setNegativeButton(R.string.str_cancle,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                checkNBUSB();
                            }
                        })

                .setPositiveButton(R.string.setting,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //跳转GPS设置界面
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                try {
                                    startActivityForResult(intent, GPS_REQUEST_CODE);
                                } catch (Exception ex) {
                                    LogUtil.e(tag, ex.getMessage());
                                    intent.setAction(Settings.ACTION_SETTINGS);
                                    try {
                                        startActivityForResult(intent, GPS_REQUEST_CODE);
                                    } catch (Exception e) {
                                        LogUtil.e(tag, e.getMessage());
                                    }
                                }
                            }
                        })

                .setCancelable(false);
        dialogx.show();


    }


    /***
     * 打开wifi选择窗口
     * @param adapter
     * @param listSR
     * @param checkedItem
     */
    private void openDialog(final BaseAdapter adapter, final List<ScanResult> listSR, final int checkedItem)
    {

        View rootview=LayoutInflater.from(this).inflate(R.layout.wifi_select_layout,null);
        BasicSpinner wifiTypesp= (BasicSpinner) rootview.findViewById(R.id.sp_wifi_type);
        ListViewForScrollView lvWifi = (ListViewForScrollView) rootview.findViewById(R.id.lv_wifi);
        RelativeLayout wifiLayout= (RelativeLayout) rootview.findViewById(R.id.wifi_layout);
        if (appModel.isNB()&&appModel.isScannerTSMATest()){
            setSpinner(wifiTypesp,WalktourConst.WifiType.wifiType);
            wifiTypesp.setSelection(sharePreferencesUtil.getInteger(WalktourConst.WifiType.WIFI_TYPE));
        }else if (appModel.isNB() &&!appModel.isScannerTSMATest()){
            wifiLayout.setVisibility(View.GONE);
            sharePreferencesUtil.saveInteger(WalktourConst.WifiType.WIFI_TYPE,0);
        }else if (!appModel.isNB() &&appModel.isScannerTSMATest()) {
            wifiLayout.setVisibility(View.GONE);
            sharePreferencesUtil.saveInteger(WalktourConst.WifiType.WIFI_TYPE,1);
        }else {
            return;
        }
        lvWifi.setDivider(getResources().getDrawable(R.drawable.list_divider));
        // listView.setBackgroundColor(getResources().getColor(R.color.base_list_item_bg_nomal));
        lvWifi.setBackgroundColor(getResources().getColor(R.color.app_main_bg_color));
        lvWifi.setAdapter(adapter);
        BasicDialog.Builder builder;
        lvWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                try {
                    ScanResult sr = listSR.get(position);
                    WifiManager wifiManager = (WifiManager) mContext.getApplicationContext()
                            .getSystemService(Context.WIFI_SERVICE);
                    Main.WifiAutoConnectManager wac = new Main.WifiAutoConnectManager(wifiManager);
                    wac.connect(sr.SSID, "12345678", WifiCipherType.WIFICIPHER_WPA);//WifiCipherType.WIFICIPHER_NOPASS
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally{
                    UtilsMethod.closeGPSService(mContext);
                }
            }
        });
        builder = new BasicDialog.Builder(mContext);
        builder.setView(rootview);
        builder.setTitle(getString(R.string.sys_wifi_selectap) + "");
        builder.setOnCancelListener(new AlertDialog.OnCancelListener(){
            @Override
            public void onCancel(DialogInterface dialog)
            {
                dialog.dismiss();
                if (appModel.getWifiType(Main.this)== WifiType.NB){
                    try {
                        checkNBUSB();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }finally {
                        UtilsMethod.closeGPSService(mContext);
                    }
                }else if (appModel.getWifiType(Main.this)==WifiType.SCAN_TSMA){
                    //TODO 这里是Scan连接了Wifi
                }
            }
        });
        dialog=  builder.show();
    }

    void setSpinner(Spinner spinner, String[] objects) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
                objects);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }
    /**
     * 检查NB USB测试
     */
    private void checkNBUSB()
    {
        Deviceinfo.getInstance().setHasNBUsbTestModel(false);
        ConfigNBModuleInfo.getInstance(mContext).setHasNBWifiTestModel(false);
        if (!Deviceinfo.getInstance().isHasNBUsbTestModel()) {//没检测到NB usb,再检测三次
            int max = 5;
            while (!Deviceinfo.getInstance().isHasNBUsbTestModel() && max > 0) {
                Deviceinfo.getInstance().setHasNBUsbTestModel(ConfigNBModuleInfo.getInstance(Main
                        .this).checkNBModule());
                max -= 1;
            }
        }
        if (Deviceinfo.getInstance().isHasNBUsbTestModel()) {
            SharePreferencesUtil preferences = SharePreferencesUtil.getInstance(this.getApplicationContext());
            //设备名
            String msg=preferences.getString(WalktourConst.SYS_SETTING_nbmoduele_devicename_control, "")+"";
            if (null == msg|| msg.trim().length()<= 0) {
                jumpActivity(NBSelectActivity.class);
            } else {
                doStartInitService();
            }
        } else {
            doStartInitService();
        }
    }
    /**
     * 显示正在退出的进度条
     */
    @SuppressWarnings("deprecation")
    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        // progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setButton(getString(R.string.main_alert_exit_now), new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 启动退出 服务
                Intent intent = new Intent(Main.this, Killer.class);
                startService(intent);
                finish();
            }
        });
        progressDialog.setButton2(getString(R.string.hide), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        progressDialog.show();
    }

    private void findView() {
        ImageView logoImage = initImageView(R.id.logoImage);
        boolean isHuawei = appModel.isHuaWeiTest();
        TextView communications = initTextView(R.id.communications);
        if (isHuawei) {
            logoImage.setBackgroundResource(R.drawable.logo_hw);
            communications.setText(R.string.communications_corp_hw);
        }else if(AppVersionControl.getInstance().isPerceptionTest()){//感知测试,改为特殊图标
            logoImage.setBackgroundResource(R.drawable.changjia);
            communications.setText(R.string.communications_corp_changjia);
        }

        aboutBtn = initButton(R.id.about_btn);
        btnExit = initButton(R.id.exit_btn);
        aboutBtn.setOnClickListener(listener);
        btnExit.setOnClickListener(listener);

        if (appModel.isInit()) {
            this.findViewById(R.id.progressBar1).setVisibility(View.GONE);
        } else {
            this.findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
            this.findViewById(R.id.progressBar1).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 屏蔽下层view点击事件
                }
            });
        }
    }


    private OnClickListener listener = new OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.about_btn:// About
                    LogUtil.w(tag, "----isShowAbout:" + isShowAbouting);
                    startActivity(new Intent(Main.this, AboutActivity.class));
                    overridePendingTransition(R.anim.open_next, R.anim.close_main);
                    break;

                case R.id.exit_btn:// 退出

                    doExit();

                    break;
            }
        }
    };

    private void doExit()
    {
        DroidWallUtil.disable(getApplicationContext());// 关闭第三方应用联网控制
        // 如果当前测试未完成
        if (appModel.isTestJobIsRun() || appModel.isTestStoping() || appModel
                .isTestPause()) {
            // 需要完全关闭进程
            new BasicDialog.Builder(Main.this).setTitle(R.string.exit).setIcon(R
                    .drawable.icon_info)
                    .setMessage(R.string.main_menu_exit_alert_testing)
                    .setPositiveButton(R.string.str_return, new DialogInterface
                            .OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        } else {
            new BasicDialog.Builder(Main.this).setTitle(R.string.exit).setIcon(R
                    .drawable.icon_info)
                    .setMessage(R.string.main_menu_exit_alert)
                    .setPositiveButton(R.string.str_ok, new DialogInterface
                            .OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // 先通知Fleet
                            // sendBroadcast( new Intent( FleetMessage
                            // .ACTION_FLEET_LOGOUT
                            // ) );
                            if (ApplicationModel.getInstance().isTestJobIsRun() ||
                                    ApplicationModel.getInstance().isTestStoping()
                                    || ApplicationModel.getInstance().isTestPause()) {
                                // do nothing
                            } else {

                                boolean exitHere = false;
                                // twq20120328当用户权限只有多网测试时不执行退出上报动作，直接启动杀进程服务
                                if (ApplicationModel.getInstance().getAppList().size
                                        () == 1
                                        && ApplicationModel.getInstance().getAppList
                                        ().contains(WalkStruct.AppType.MutilyTester)) {
                                    exitHere = true;
                                } else {
                                    if (!ServerManager.getInstance(Main.this)
                                            .hasSetFleetServer()) {
                                        exitHere = true;
                                    } else {
                                        ServerManager server = ServerManager
                                                .getInstance(Main.this);
                                        if (server.hasSetFleetServer() && server
                                                .hasFleetEvent(Main.this)) {

                                            showProgressDialog(getString(R.string
                                                    .main_alert_logouting));
                                            // 先通知Fleet
                                            sendBroadcast(new Intent(ServerMessage
                                                    .ACTION_FLEET_LOGOUT));
                                        } else {
                                            exitHere = true;
                                        }
                                    }
                                }

                                if (exitHere) {
                                    Intent intent = new Intent(Main.this, Killer.class);
                                    startService(intent);
                                    try {
//                                        Thread.sleep(2000);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }finally {
                                        dialog.dismiss();
                                        finish();
                                    }


                                }

                            }

                        }
                    }).setNegativeButton(R.string.str_cancle).show();
        }
    }

    private void installWalkSetting() {
        if (UtilsMethod.getSDKVersionNumber() < 15) {
            LogUtil.v(tag, "--sdk version less than 15 can't install walktoursetting--");
        } else {
            LogUtil.v(tag, "--sdk version more than 15 need install walktoursetting--");
        }
    }

    /**
     * 打开网络下载权限文件activity
     */
    private void startDownloadLicense() {
        startActivity(new Intent(Main.this, GetPowByNet.class).putExtra("fromMain", true));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle
            persistentState)
    {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.w(tag, "---onDestroy local:" + isLoadLicenseFromLoal);
        AlertManager.getInstance(this).setTTS(null);
        /**
         * 按返回键退出的时候，报释放语音设备异常。此处要处理的话，需要把检查TTS设备的动作放在Service,或者捕捉HOME键
         * AlertManager.getInstance(this).getTTS().shutdown();
         */
        try {
            WalktourApplication.refreshFile();
            unregisterReceiver(myReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onBackPressed() {
//        doExit();
//    }


    @Override
    public void onInit(int status) {
        LogUtil.i(tag, "onInit:" + status);
        if (status == TextToSpeech.SUCCESS) {
            try {
                int result = AlertManager.getInstance(this).getTTS().setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech
                        .LANG_NOT_SUPPORTED) {
                    // 判断语言是否可用
                    Toast.makeText(this, "TTS Error", Toast.LENGTH_LONG).show();
                } else {
                    if (AlertManager.getInstance(this).getPrefs(AlertManager.KEY_NETWORK_SOUND)
                            || AlertManager.getInstance(this).getPrefs(AlertManager.KEY_TEST_SOUND)) {

                    }
                }
            } catch (Exception ex) {
                LogUtil.w(tag, ex.getMessage());
            }

        }

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.w(tag, "---resultCode=" + requestCode);
        try {
            if (requestCode == REQ_TTS_STATUS_CHECK) {
                switch (resultCode) {
                    case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
                    case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA:// 缺少需要语言的语音数据
                        // 这个返回结果表明TTS Engine可以用
                        LogUtil.w(tag, "check voice data pass");
                        if (AlertManager.getInstance(this).getTTS() == null) {
                            TextToSpeech tts = new TextToSpeech(this, this);
                            AlertManager.getInstance(this).setTTS(tts);
                        }
                        break;
                    case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME:// 缺少需要语言的发音数据
                    case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:// 需要的语音数据已损坏
                        LogUtil.w(tag, "tts is not available:" + resultCode);
                        showDialog(SHOW_TTS_DIALOG);
                        break;
                    case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:// 检查失败
                        LogUtil.w(tag, "tts CHECK_VOICE_DATA_FAIL");
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LogUtil.w(tag, "", e);
        }
    }

    /**
     * 返回键时自定义home键实现
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            doExit();
        } else {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    private class WifiAutoConnectManager {
        WifiManager wifiManager;
        // 构造函数
        public WifiAutoConnectManager(WifiManager wifiManager) {
            this.wifiManager = wifiManager;
        }

        // 提供一个外部接口，传入要连接的无线网
        public void connect(String ssid, String password, WifiCipherType type) {
            Thread thread = new Thread(new ConnectRunnable(ssid, password, type));
            thread.start();
        }

        // 查看以前是否也配置过这个网络
        private List<WifiConfiguration> isExsits(String SSID) {
            List<WifiConfiguration> configurations=new LinkedList<>();
            List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")||existingConfig.SSID.toLowerCase().startsWith("\"liteprobe\"")) {
                    configurations.add(existingConfig);
                }
            }
            return configurations;
        }

        private WifiConfiguration createWifiInfo(String SSID, String Password,
                                                 WifiCipherType Type) {
            WifiConfiguration config = new WifiConfiguration();
            config.allowedAuthAlgorithms.clear();
            config.allowedGroupCiphers.clear();
            config.allowedKeyManagement.clear();
            config.allowedPairwiseCiphers.clear();
            config.allowedProtocols.clear();
            config.SSID = "\"" + SSID + "\"";
            // nopass
            if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
                // config.wepKeys[0] = "";
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                // config.wepTxKeyIndex = 0;
            }
            // wep
            if (Type == WifiCipherType.WIFICIPHER_WEP) {
                if (!TextUtils.isEmpty(Password)) {
                    if (isHexWepKey(Password)) {
                        config.wepKeys[0] = Password;
                    } else {
                        config.wepKeys[0] = "\"" + Password + "\"";
                    }
                }
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
            }
            // wpa
            if (Type == WifiCipherType.WIFICIPHER_WPA) {
                config.preSharedKey = "\"" + Password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms
                        .set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.TKIP);
                // 此处需要修改否则不能自动重联
                // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.CCMP);
                config.status = WifiConfiguration.Status.ENABLED;
            }
            return config;
        }

        // 打开wifi功能
        private boolean openWifi() {
            boolean bRet = true;
            if (!wifiManager.isWifiEnabled()) {
                bRet = wifiManager.setWifiEnabled(true);
            }
            return bRet;
        }

        private class ConnectRunnable implements Runnable {
            private String ssid;

            private String password;

            private WifiCipherType type;

            public ConnectRunnable(String ssid, String password, WifiCipherType type) {
                this.ssid = ssid;
                this.password = password;
                this.type = type;
            }

            @Override
            public void run() {
                try {
                    // 打开wifi
//                    openWifi();
//                    Thread.sleep(200);
                    while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                        }
                    }

                    WifiConfiguration wifiConfig = createWifiInfo(ssid, password,
                            type);
                    if (wifiConfig == null) {
                        return;
                    }

                    List<WifiConfiguration> configurations = isExsits(ssid);
                    for(WifiConfiguration configuration:configurations){
                        wifiManager.removeNetwork(configuration.networkId);
                    }

                    int netID = wifiManager.addNetwork(wifiConfig);
                    boolean enabled = wifiManager.enableNetwork(netID, true);
                    boolean connected = wifiManager.reconnect();
                    LogUtil.w(tag,"nb wifi connected="+connected);
                    if(connected){
                        String serverIP=getServerIp(wifiManager);
                        String clientIP=getClientIp(wifiManager);
                        LogUtil.w(tag,"nb select wifi serverIP="+serverIP);
                        LogUtil.w(tag,"nb select wifi clientIP="+clientIP);

                        ConfigNBModuleInfo.getInstance(mContext).setNbSelectWifiClientIP(clientIP);
                        ConfigNBModuleInfo.getInstance(mContext).setNbSelectWifiServerIP(serverIP);

                        ConfigNBModuleInfo.getInstance(mContext).setHasNBWifiTestModel(true);
                        jumpActivity(NBSelectActivity.class);
                    }else{
                        ConfigNBModuleInfo.getInstance(mContext).setNbSelectWifiClientIP(null);
                        ConfigNBModuleInfo.getInstance(mContext).setNbSelectWifiServerIP(null);
                        ConfigNBModuleInfo.getInstance(mContext).setHasNBWifiTestModel(false);
                        ToastUtil.showToastShort(mContext,R.string.mutilytester_wificonnect_faild);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private  boolean isHexWepKey(String wepKey) {
            final int len = wepKey.length();
            if (len != 10 && len != 26 && len != 58) {
                return false;
            }

            return isHex(wepKey);
        }

        private  boolean isHex(String key) {
            for (int i = key.length() - 1; i >= 0; i--) {
                final char c = key.charAt(i);
                if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                        && c <= 'f')) {
                    return false;
                }
            }

            return true;
        }

        /***
         * 整数转为IP
         * @param i
         * @return
         */
        private String intToIp(int i) {
            return (i & 0xFF ) + "." +
                    ((i >> 8 ) & 0xFF) + "." +
                    ((i >> 16 ) & 0xFF) + "." +
                    ( i >> 24 & 0xFF) ;
        }
        /***
         * 获取Wifi 客户端ip
         * @return
         */
        private String getClientIp(WifiManager wifiManager){
            WifiInfo wi=wifiManager.getConnectionInfo();
            int ipAdd=wi.getIpAddress();
            return intToIp(ipAdd);
        }

        /***
         * 获取Wifi 服务端ip
         * @return
         */
        private String getServerIp(WifiManager wifiManager){
            return intToIp(wifiManager.getDhcpInfo().serverAddress);
        }

    }

    /***
     * 启动初始化服务
     */
    private void doStartInitService(){
        if(!isStartInitService) {
            isStartInitService=true;
            Intent i = new Intent(Main.this, ApplicationInitService.class);
            startService(i);
            i = null;
        }
    }
}