package com.walktour.gui.setting.sysroutine;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.AssetsWriter;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.ConfigDebugModel;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicDialog.Builder;
import com.walktour.gui.R;
import com.walktour.gui.locknet.ForceControler.ForceNet;
import com.walktour.gui.locknet.ForceManager;
import com.walktour.gui.locknet.dialog.LockNetworkProgress;
import com.walktour.gui.locknet.dialog.OnDialogChangeListener;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.service.app.Killer;
import com.walktour.service.app.LogService;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * ??????????????????????????????????????????
 *
 * @author jianchao.wang
 */
@SuppressLint("InflateParams")
public class SysRoutineAdvencedActivity extends BasicActivity implements OnClickListener, OnCheckedChangeListener {
    private static final String TAG = "SysRoutineAdvencedActivity";
    /**
     * ????????????????????????
     */
    private ConfigRoutine configRoutine;
    /**
     * ?????????
     */
    private Context mContext;
    /**
     * ????????????(???????????????(PPP???WiFi))
     */
    private TextView txtNif;
    /**
     * ????????????(???????????????NB??????)
     */
    private TextView txtModel;
    /**
     * ??????????????????
     */
    private TextView replaySpeed;
    /**
     * ????????????
     */
    private CheckBox checkAccept;
    /**
     * ??????????????????
     */
    private CheckBox checkPuaseKeepTest;
    /**
     * ?????????????????????
     */
    private CheckBox checkPuaseNoData;
    /**
     * ?????????????????????
     */
    private CheckBox checkPuaseSaveData;
    /***
     * ??????spped test Url?????????
     */
    private Button updateSpeedTestUrl;
    /**
     * ??????????????????????????????
     */
    private CheckBox checkSwitchAirplan;

    /**
     * ????????????????????????
     */
    private LinearLayout systeSetLayout;
    /** ????????????GMCC */
    // private RelativeLayout isGmccLayout;
    /**
     * ??????????????????
     */
    private RelativeLayout isOutOfServiceLayout;
    /**
     * ???????????????????????????
     */
    private RelativeLayout isOpenDataSetLayout;
    /**
     * ?????????????????????????????????
     */
    private RelativeLayout runSaveOrgDataLayout;
    /**
     * ?????????WIFI????????????IPAD,????????????
     */
    private RelativeLayout isConnectIpackByWifiLayout;
    /**
     * ??????w???????????????
     */
    private RelativeLayout isHsExternalGps;

    private RelativeLayout logcatAllLogLayout;

    /**
     * ?????????????????????
     */
    private RelativeLayout mRlUseScanner;
    /**
     * ???????????????
     */
    private CheckBox mCbUseScanner;
    private CheckBox mCbUseTSMAScanner;

    /** ????????????GMCC */
    // private CheckBox isGmccProject;
    /**
     * ??????????????????
     */
    private CheckBox isOutOfService;
    /**
     * ???????????????????????????
     */
    private CheckBox isOpenDataSetLog;
    /**
     * ?????????????????????????????????
     */
    private CheckBox runSaveOrgData;
    /**
     * ?????????WIFI???????????????????????????
     */
    private CheckBox isIpackModelByWifi;
    /**
     * ?????????????????????????????????RCU?????????
     */
    private CheckBox isMemoryMapped;
    /**
     * ????????????????????????
     */
    private CheckBox toSaveAllLog;
    /**
     * ????????????????????????
     */
    private CheckBox autoUpgrade;
    /**
     * ?????????????????????
     */
    private CheckBox cbHsExternalGPS;
    /**
     * ?????????????????????????????????
     */
    private Spinner comprehensive_test_environment;
    private ArrayAdapter comprehensive_test_environmentadapter;
    /**
     * ????????????L1,L2??????
     **/
    private CheckBox show_L1_L2_signal;
    /**
     * ????????????L1,L2????????????
     **/
    private CheckBox show_L1_L2_signal_buffer;
    /**
     * ??????????????????
     */
    private CheckBox sharepushck;
    // ????????????
    private ForceManager mForceMgr;
    private TextView setting_superuser_onCheck;
    private Boolean isShowLayout;

    private final static String SHOW_SUPER_LAYOUT = "SHOW_SUPER_LAYOUT";
    private boolean isFromMultitest = false;
    /**
     * ??????????????????
     **/
    private LinearLayout superlayout;

    /**
     * ?????????
     */
    ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getParent();
        if (null == mContext) {
            mContext = this;
        }
        Bundle bundle = this.getIntent().getExtras();
        if (null != bundle) {
            isFromMultitest = bundle.getBoolean("isFromMultitest");
        }
        LogUtil.w(TAG, "--onCreate--");
        setContentView(R.layout.sys_routine_setting_advenced);
        configRoutine = ConfigRoutine.getInstance();
        findView();
        mForceMgr = ForceManager.getInstance();
        mForceMgr.init();
        this.initData();
        regisBroadcast();
    }

    /**
     * ?????????????????????<BR>
     * [??????????????????]
     */
    private void findView() {
        txtNif = initTextView(R.id.txt_nif);
        txtModel = initTextView(R.id.txt_model);
        View layoutNif = findViewById(R.id.nif_layout);
        layoutNif.setOnClickListener(this);
        View layoutExchangeModel = findViewById(R.id.exchange_layout);
        layoutExchangeModel.setOnClickListener(this);
        if (android.os.Build.MODEL.toUpperCase().contains("G9300") && ApplicationModel.getInstance().isNBTest() && FileUtil.checkFile(AppFilePathUtil.getInstance().getSDCardFile("Walktour", "rom", "boot_nb.img")) && FileUtil.checkFile(AppFilePathUtil.getInstance().getSDCardFile("Walktour", "rom", "boot_normal.img"))) {
            layoutExchangeModel.setVisibility(View.VISIBLE);
        } else {
            layoutExchangeModel.setVisibility(View.GONE);
        }
        replaySpeed = initTextView(R.id.txt_advenced_replay_speed_multiple);
        View layoutSpeed = findViewById(R.id.advenced_replay_speed_multiple);
        layoutSpeed.setOnClickListener(this);
        checkAccept = (CheckBox) findViewById(R.id.toggle_accept_checkbox);
        checkAccept.setOnCheckedChangeListener(this);
        checkPuaseKeepTest = (CheckBox) findViewById(R.id.toggle_puase_keeptest_checkbox);
        checkPuaseKeepTest.setOnCheckedChangeListener(this);
        checkPuaseNoData = (CheckBox) findViewById(R.id.toggle_puase_nodata_checkbox);
        checkPuaseNoData.setOnCheckedChangeListener(this);
        checkPuaseSaveData = (CheckBox) findViewById(R.id.toggle_puase_savedata_checkbox);
        checkPuaseSaveData.setOnCheckedChangeListener(this);
        updateSpeedTestUrl = (Button) findViewById(R.id.update_speedtest_server);
        updateSpeedTestUrl.setOnClickListener(this);

        checkSwitchAirplan = (CheckBox) findViewById(R.id.toggle_switchaiplan_bytest_checkbox);
        checkSwitchAirplan.setOnCheckedChangeListener(this);
        mRlUseScanner = initRelativeLayout(R.id.rl_use_scanner);
        mCbUseScanner = initCheckBox(R.id.cb_use_scanner);
        mCbUseScanner.setOnCheckedChangeListener(this);
        mCbUseTSMAScanner = initCheckBox(R.id.cb_use_scanner_tsma);
        mCbUseTSMAScanner.setOnCheckedChangeListener(this);
        isShowLayout = SharePreferencesUtil.getInstance(mContext).getBoolean(SHOW_SUPER_LAYOUT, false); // ??????????????????
        superlayout = initLinearLayout(R.id.show_superuser_layout);
        superlayout.setVisibility(isShowLayout ? View.VISIBLE : View.GONE);
        setting_superuser_onCheck = initTextView(R.id.setting_superuser_txt);
        // ????????????????????????5???,??????????????????,??????????????????
        setting_superuser_onCheck.setOnClickListener(new OnClickListener() {
            long[] mHints = new long[5];

            @Override
            public void onClick(View v) {
                if (!isShowLayout) {
                    System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
                    mHints[mHints.length - 1] = SystemClock.uptimeMillis(); // ??????????????????5???????????????3????????????
                    if (SystemClock.uptimeMillis() - mHints[0] <= 3000) {
                        SharePreferencesUtil.getInstance(mContext).saveBoolean(SHOW_SUPER_LAYOUT, true);
                        superlayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // ?????????????????????????????????,?????????????????????
        systeSetLayout = initLinearLayout(R.id.setting_superuser_layout);
        // isGmccLayout = (RelativeLayout)
        // findViewById(R.id.setting_isgmcc_layout);
        isOutOfServiceLayout = initRelativeLayout(R.id.setting_checkofservice_layout);
        isOpenDataSetLayout = initRelativeLayout(R.id.setting_datasetlog_layout);
        logcatAllLogLayout = initRelativeLayout(R.id.setting_save_all_log_layout);
        runSaveOrgDataLayout = initRelativeLayout(R.id.setting_saveorgdata_layout);
        isConnectIpackByWifiLayout = initRelativeLayout(R.id.setting_ipackmodel_layout);
        isHsExternalGps = initRelativeLayout(R.id.setting_hs_external_gps);

        // isGmccProject = (CheckBox) findViewById(R.id.supper_user_isGmcc);
        isOutOfService = (CheckBox) findViewById(R.id.supper_user_checkoutofservice);
        isOpenDataSetLog = (CheckBox) findViewById(R.id.supper_user_opendatasetlog);
        runSaveOrgData = (CheckBox) findViewById(R.id.supper_user_saveorgdata);
        isIpackModelByWifi = (CheckBox) findViewById(R.id.supper_user_connectipackbywifi);
        isMemoryMapped = (CheckBox) findViewById(R.id.supper_user_memory_mapped);
        toSaveAllLog = (CheckBox) findViewById(R.id.supper_user_save_all_log);
        autoUpgrade = (CheckBox) findViewById(R.id.supper_user_auto_upgrade);
        cbHsExternalGPS = (CheckBox) findViewById(R.id.cb_hs_external_gps);

        comprehensive_test_environment = (Spinner) findViewById(R.id.comprehensive_test_environment);
        show_L1_L2_signal = (CheckBox) findViewById(R.id.show_l1_l2_command);
        show_L1_L2_signal_buffer = (CheckBox) findViewById(R.id.show_l1_l2_command_buffer);
        isMemoryMapped.setChecked(configRoutine.isMemoryMepped(this.mContext.getApplicationContext()));
        isMemoryMapped.setOnCheckedChangeListener(this);
        toSaveAllLog.setChecked(configRoutine.isSaveAllLog(this.mContext.getApplicationContext()));
        toSaveAllLog.setOnCheckedChangeListener(this);
        autoUpgrade.setChecked(ServerManager.getInstance(mContext).hasAutoUpgrade());
        autoUpgrade.setOnCheckedChangeListener(this);

        // ??????????????????ArrayAdapter????????????
        comprehensive_test_environmentadapter = ArrayAdapter.createFromResource(this, R.array.comprehensivetestinstrument, android.R.layout.simple_spinner_item);

        // ???????????????????????????
        comprehensive_test_environmentadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // ???adapter2 ?????????spinner???
        comprehensive_test_environment.setAdapter(comprehensive_test_environmentadapter);
        comprehensive_test_environment.setSelection(ServerManager.getInstance(mContext).getComprehensiveTestEnvironment());
        // ????????????Spinner????????????
        comprehensive_test_environment.setOnItemSelectedListener(new SpinnerXMLSelectedListener());

        show_L1_L2_signal.setChecked(ServerManager.getInstance(mContext).hasShowL1L2Command());
        show_L1_L2_signal.setOnCheckedChangeListener(this);

        show_L1_L2_signal_buffer.setChecked(ServerManager.getInstance(mContext).hasShowL1L2CommandBuffer());
        show_L1_L2_signal_buffer.setOnCheckedChangeListener(this);
        // GMCC?????????????????????,????????????????????????,????????????,????????????????????????????????????
        /*
         * if(ApplicationModel.getInstance().getNetList().contains(WalkStruct. ShowInfoType.GMCCProj)){
         * systeSetLayout.setVisibility(View.VISIBLE); isGmcc_layout.setVisibility(View.VISIBLE);
         * isGmccProject.setChecked(configRoutine.isGmccVersion()); isGmccProject.setOnCheckedChangeListener(new
         * OnCheckedChangeListener() {
         *
         * @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
         * configRoutine.setIsGmccVersion(isChecked ? "1" : "0"); } }); }
         */

        // ???????????????????????????????????????????????????5?????????
        if (ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.CheckOutOfService)) {
            // systeSetLayout.setVisibility(View.VISIBLE);
            isOutOfServiceLayout.setVisibility(View.VISIBLE);
            // isOutOfService.setChecked(configRoutine.checkOutOfService());
            // isOutOfService.setOnCheckedChangeListener(this);
        }
        if (ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.OpenDataSetLog)) {
            // systeSetLayout.setVisibility(View.VISIBLE);
            isOpenDataSetLayout.setVisibility(View.VISIBLE);
            // isOpenDataSetLog.setChecked(configRoutine.isOpenDataSetLog(this.mContext
            // .getApplicationContext()));
            // isOpenDataSetLog.setOnCheckedChangeListener(this);
        }
        if (ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.LogcatAllLog)) {
            logcatAllLogLayout.setVisibility(View.VISIBLE);
        }
        if (ApplicationModel.getInstance().getAppList().contains(WalkStruct.AppType.MutilyTester)) {
            // systeSetLayout.setVisibility(View.VISIBLE);
            isConnectIpackByWifiLayout.setVisibility(View.VISIBLE);
            // isIpackModelByWifi.setChecked(configRoutine.isWifiModel(this.mContext
            // .getApplicationContext()));
            // isIpackModelByWifi.setOnCheckedChangeListener(this);
        }
        // ???????????????????????????????????????????????????
        mRlUseScanner.setVisibility(ApplicationModel.getInstance().hasScannerTest() ? View.VISIBLE : View.GONE);
        isOutOfService.setChecked(configRoutine.checkOutOfService());
        isOutOfService.setOnCheckedChangeListener(this);
        isOpenDataSetLog.setChecked(configRoutine.isOpenDataSetLog(this.mContext.getApplicationContext()));
        isOpenDataSetLog.setOnCheckedChangeListener(this);
        runSaveOrgData.setChecked(configRoutine.isRunSaveOrgData(this.mContext.getApplicationContext()));
        runSaveOrgData.setOnCheckedChangeListener(this);
        isIpackModelByWifi.setChecked(configRoutine.isWifiModel(this.mContext.getApplicationContext()));
        isIpackModelByWifi.setOnCheckedChangeListener(this);
        RelativeLayout debugModelLayout = initRelativeLayout(R.id.setting_debugmodel_layout);
        debugModelLayout.setOnClickListener(this);
        CheckBox debugModelCheckBox = (CheckBox) findViewById(R.id.supper_user_debugmodel);
        debugModelCheckBox.setChecked(ConfigDebugModel.getInstance(mContext).isDebugModel());
        debugModelCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigDebugModel.getInstance(mContext).setDebugModel(mContext, isChecked);
            }
        });

        findViewById(R.id.ims_encrypted_layout).setOnClickListener(this);
        findViewById(R.id.telecom_setting_layout).setOnClickListener(this);
        findViewById(R.id.dual_Network_layout).setOnClickListener(this);

        // ??????????????????
        if (ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.TelecomSwitch)) {
            findViewById(R.id.telecom_setting_layout).setVisibility(View.VISIBLE);
        }

        // ???????????????????????????,??????????????????,?????????GSM,LTE AUTO????????????????????????,????????????
        if (Deviceinfo.getInstance().isDualMode()) {
            systeSetLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.dual_Network_layout).setVisibility(View.VISIBLE);
        }

        TextView shat = (TextView) this.findViewById(R.id.setting_sharepush_txt);
        shat.setText(getString(R.string.share_project_weifuwu) + getString(R.string.setting));
        sharepushck = (CheckBox) findViewById(R.id.setting_sharepush_ck);
        sharepushck.setChecked(SharePreferencesUtil.getInstance(mContext).getBoolean(ShareCommons.SHARE_DATA_KEY, false));
        sharepushck.setOnCheckedChangeListener(this);
        initRelativeLayout(R.id.exceptionfeedback).setOnClickListener(this);

        // ?????????????????????
        if (ApplicationModel.getInstance().getSelectScene() == WalkStruct.SceneType.HighSpeedRail || ApplicationModel.getInstance().getSelectScene() == WalkStruct.SceneType.Metro) {
            isHsExternalGps.setVisibility(View.VISIBLE);
            cbHsExternalGPS.setChecked(configRoutine.isHsExternalGPS(mContext));
            cbHsExternalGPS.setOnCheckedChangeListener(this);
        }

    }

    // ??????XML????????????
    private class SpinnerXMLSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // agr2?????????
            ServerManager.getInstance(mContext).setComprehensiveTestEnvironment(arg2);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }

    }

    /**
     * ?????????????????????
     */
    private void initData() {
        checkAccept.setChecked(configRoutine.isAcceptCall(mContext));
        checkPuaseKeepTest.setChecked(configRoutine.isPuaseKeepTest(mContext));
        checkPuaseNoData.setChecked(configRoutine.isPuaseNoData(mContext));
        checkPuaseSaveData.setChecked(configRoutine.isPuaseSaveData(mContext));
        checkSwitchAirplan.setChecked(configRoutine.isSwitchAirplan(mContext));
        mCbUseScanner.setChecked(configRoutine.isUseScanner(mContext));
        mCbUseTSMAScanner.setChecked(configRoutine.isUseTSMAScanner(mContext));
        this.initNif();
        this.initExchangeModel();
        replaySpeed.setText(configRoutine.getReplaySpeed());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.nif_layout:
            this.showDialog(R.id.nif_layout);
            break;
        case R.id.exchange_layout:
            this.showDialog(R.id.exchange_layout);
            break;
        case R.id.setting_debugmodel_layout:
            if (ConfigDebugModel.getInstance(mContext).isDebugModel()) {
                Intent intent = new Intent(SysRoutineActivity.SHOW_DEBUG_MODEL_TAB);
                this.sendBroadcast(intent);
            } else {
                Toast.makeText(mContext, R.string.sys_setting_toast_toOpenDebug, Toast.LENGTH_SHORT).show();
            }
            break;
        case R.id.ims_encrypted_layout:
            BasicDialog.Builder pBuilder = new BasicDialog.Builder(getParent());
            pBuilder.setTitle(R.string.sys_setting_ims_encrypted).setSingleChoiceItems(R.array.ims_encrypted_array, configRoutine.getIMSEncrypted(mContext), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                    case 1:
                        showDataPrintDialog(which);
                        break;
                    }
                    dialog.dismiss();
                }
            });
            pBuilder.show();
            break;
        case R.id.dual_Network_layout:
            // showDialog(R.id.dual_Network_layout);

            BasicDialog.Builder builder = new BasicDialog.Builder(getParent());
            builder.setTitle(R.string.sys_setting_Dual_Network).setSingleChoiceItems(R.array.dual_network_array, configRoutine.isDual_Network(mContext), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                    case 1:
                    case 2:
                        showDialogToast(which);
                        break;
                    }
                    dialog.dismiss();
                }
            });
            builder.show();

            break;
        case R.id.advenced_replay_speed_multiple:
            this.showDialog(R.id.advenced_replay_speed_multiple);
            break;
        case R.id.telecom_setting_layout:
            startActivity(new Intent(this, SysRoutineTelecomActivity.class));
            break;
        case R.id.exceptionfeedback:// ??????????????????
            jumpActivity(SysExceptionCommitActivity.class);
            break;
        case R.id.update_speedtest_server:
            new DownLoadSpeedTestUrl().execute();
            break;
        }
    }

    /***
     * ??????speed test url?????????
     *
     * @author weirong.fan
     *
     */
    private class DownLoadSpeedTestUrl extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            openDialog(getString(R.string.exe_info));
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            closeDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // url????????????
            String speedTestUrl = "http://c.speedtest.net/speedtest-servers-static.php";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println("???????????????" + sdf.format(System.currentTimeMillis()));
                String filePath = AppFilePathUtil.getInstance().getAppConfigDirectory() + "speedtest_server.xml";
                downloadFile(speedTestUrl, filePath);
                System.out.println("???????????????" + sdf.format(System.currentTimeMillis()));
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {

            }
            return null;
        }

        private boolean downloadFile(String httpUrl, String savePath) {
            int byteread = 0;
            HttpURLConnection conn = null;
            InputStream inStream = null;
            FileOutputStream fs = null;
            try {
                URL url = new URL(httpUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK) {
                    FileUtil.deleteFile(savePath);//???????????????
                    inStream = conn.getInputStream();
                    fs = new FileOutputStream(savePath);
                    byte[] buffer = new byte[1024 * 5];
                    while ((byteread = inStream.read(buffer)) != -1) {
                        fs.write(buffer, 0, byteread);
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (null != inStream) {
                    try {
                        inStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    inStream = null;
                }

                if (null != fs) {
                    try {
                        fs.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    fs = null;
                }

                if (null != conn) {
                    try {
                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn = null;
                }
            }
            return false;
        }
    }

    private void showDataPrintDialog(final int which) {
        BasicDialog.Builder builder = new BasicDialog.Builder(getParent());
        builder.setTitle(R.string.str_tip).setMessage(R.string.seting_ims_encrypted_str).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                configRoutine.setIMSEncrypted(getApplicationContext(), which);
                ((TextView) findViewById(R.id.ims_encrypted_text)).setText(getResources().getStringArray(R.array.ims_encrypted_array)[which]);

                if (which == 1) {
                    new AssetsWriter(mContext, "logmask/Diag_IMS_Encrypt.cfg", AppFilePathUtil.getInstance().getAppFilesFile("Diag" + ".cfg"), true).writeBinFile();

                    // new UnlockAllProgress(mContext,lockListener).execute();
                    new LockNetworkProgress(mContext, ForceNet.NET_AUTO, lockListener).execute();
                } else {
                    new AssetsWriter(mContext, "logmask/Diag_Lte.cfg", AppFilePathUtil.getInstance().getAppFilesFile("Diag" + ".cfg"), true).writeBinFile();

                    new LockNetworkProgress(mContext, ForceNet.NET_AUTO, lockListener).execute();
                }
            }
        }).setNegativeButton(R.string.str_cancle);
        builder.show();
    }

    private void showDialogToast(final int which) {
        BasicDialog.Builder builder = new BasicDialog.Builder(getParent());
        builder.setTitle(R.string.str_tip).setMessage(R.string.seting_dual_network_str).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                configRoutine.setDual_Network(SysRoutineAdvencedActivity.this, which);
                (initTextView(R.id.dual_Network_text)).setText(getResources().getStringArray(R.array.dual_network_array)[which]);

                if (which == 0) {

                    new AssetsWriter(mContext, "logmask/Diag_GSM_Only_9008v.cfg", AppFilePathUtil.getInstance().getAppFilesFile("Diag" + ".cfg"), true).writeBinFile();

                    new LockNetworkProgress(mContext, ForceNet.NET_GSM, lockListener).execute();

                } else if (which == 1) {

                    new AssetsWriter(mContext, "logmask/Diag_Lte_9008v.cfg", AppFilePathUtil.getInstance().getAppFilesFile("Diag" + ".cfg"), true).writeBinFile();

                    // new UnlockAllProgress(mContext,lockListener).execute();
                    new LockNetworkProgress(mContext, ForceNet.NET_AUTO, lockListener).execute();
                } else {
                    new AssetsWriter(mContext, "logmask/Diag_Lte.cfg", AppFilePathUtil.getInstance().getAppFilesFile("Diag" + ".cfg"), true).writeBinFile();

                    new LockNetworkProgress(mContext, ForceNet.NET_AUTO, lockListener).execute();
                }
            }
        }).setNegativeButton(R.string.str_cancle);
        builder.show();
    }

    OnDialogChangeListener lockListener = new OnDialogChangeListener() {
        @Override
        public void onPositive() {
            startService(new Intent(getApplicationContext(), Killer.class));
        }

        @Override
        public void onLockPositive(String lockType) {
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isFromMultitest) {
                this.finish();
            } else {
                Intent intent = new Intent(SysRoutineActivity.SHOW_MAIN_TAB);
                this.sendBroadcast(intent);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        BasicDialog.Builder builder = new BasicDialog.Builder(getParent());
        switch (id) {
        case R.id.nif_layout:
            builder.setTitle(R.string.str_bind_net).setSingleChoiceItems(R.array.net_interface, configRoutine.getNetInterface(mContext), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    configRoutine.setNetInterface(mContext, which);
                    initNif();
                    dialog.dismiss();
                }
            });

            break;
        case R.id.exchange_layout:
            builder.setTitle(getString(R.string.exchange_model)).setSingleChoiceItems(R.array.exchange_model, initExchangeModel(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // ????????????????????????
                    // ??????:dd if=/sdcard/new.img of=/dev/block/bootdevice/by-name/boot
                    String cmd = "";
                    switch (which) {
                    case 0:
                        // cmd="dd if="+AppFilePathUtil.getInstance().getAppFilesFile
                        // ("boot_normal.img")+" of=/dev/block/bootdevice/by-name/boot";
                        cmd = "dd if=" + AppFilePathUtil.getInstance().getSDCardFile("Walktour", "rom", "boot_normal.img") + " of=/dev/block/bootdevice/by-name/boot";
                        LogUtil.w(TAG, "result=" + cmd);
                        cmd = UtilsMethod.execRootCmdx(cmd);
                        break;
                    case 1:
                        // cmd="dd if="+AppFilePathUtil.getInstance().getAppFilesFile
                        // ("boot_nb.img")+" of=/dev/block/bootdevice/by-name/boot";
                        cmd = "dd if=" + AppFilePathUtil.getInstance().getSDCardFile("Walktour", "rom", "boot_nb.img") + " of=/dev/block/bootdevice/by-name/boot";
                        LogUtil.w(TAG, "result=" + cmd);
                        cmd = UtilsMethod.execRootCmdx(cmd);
                        break;
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    UtilsMethod.rebootMachine();
                    dialog.dismiss();
                    LogUtil.w(TAG, "result=" + cmd);
                }
            });
            break;
        case R.id.advenced_replay_speed_multiple:
            createReplaySpeedMultiple(builder);
            break;
        }
        return builder.create();
    }

    /**
     * ??????????????????????????????
     *
     * @param builder
     */
    private void createReplaySpeedMultiple(Builder builder) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.alert_dialog_edittext, null);
        EditText alert_EditText = (EditText) view.findViewById(R.id.alert_textEditText);
        final ConfigRoutine configRoutine = ConfigRoutine.getInstance();
        alert_EditText.setText(configRoutine.getReplaySpeed());
        alert_EditText.setSelectAllOnFocus(true);
        alert_EditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.sys_setting_advenced_replay_speed).setView(view).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText alert_EditText = (EditText) view.findViewById(R.id.alert_textEditText);
                String tag = alert_EditText.getText().toString().trim();
                if (Verify.isInteger(tag)) {
                    configRoutine.setReplaySpeed(tag);
                    replaySpeed.setText(configRoutine.getReplaySpeed());
                } else {
                    alert_EditText.setText(configRoutine.getReplaySpeed());
                    Toast.makeText(getParent(), getString(R.string.monitor_inputPosition), Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton(R.string.str_cancle);

    }

    /**
     * ????????????
     */
    private void initNif() {
        String[] interfaces = getResources().getStringArray(R.array.net_interface);
        txtNif.setText(interfaces[configRoutine.getNetInterface(mContext)]);
    }

    /**
     * ????????????
     */
    private int initExchangeModel() {
        int which = 0;
        String valx = UtilsMethod.execRootCmdx("cat /proc/version");
        if (null != valx && valx.contains("cxp")) {
            which = 1;
        }
        String[] interfaces = getResources().getStringArray(R.array.exchange_model);
        txtModel.setText(interfaces[which]);
        return which;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
        case R.id.toggle_accept_checkbox:
            configRoutine.setAcceptCall(mContext, isChecked);
            break;
        case R.id.toggle_puase_keeptest_checkbox:
            configRoutine.setPuaseKeepTest(mContext, isChecked);
            break;
        case R.id.toggle_puase_nodata_checkbox:
            if (isChecked) {
                checkPuaseSaveData.setChecked(false);
            }
            configRoutine.setPuaseNoData(mContext, isChecked);
            break;
        case R.id.toggle_puase_savedata_checkbox:
            if (isChecked) {
                checkPuaseNoData.setChecked(false);
            }
            configRoutine.setPuaseSaveData(mContext, isChecked);
            break;
        case R.id.toggle_switchaiplan_bytest_checkbox:
            configRoutine.setSwitchAirplan(mContext, isChecked);
            break;
        case R.id.cb_use_scanner:
            configRoutine.setUseScanner(mContext, isChecked);
            break;
        case R.id.cb_use_scanner_tsma:
            configRoutine.setUseTSMAScanner(mContext, isChecked);
            break;
        case R.id.supper_user_checkoutofservice:
            configRoutine.setCheckOutOfService(isChecked ? "1" : "0");
            break;
        case R.id.supper_user_opendatasetlog:
            configRoutine.setDataSetLog(this.mContext.getApplicationContext(), isChecked);
            break;
        case R.id.supper_user_saveorgdata:
            configRoutine.setRunSaveOrgData(this.mContext.getApplicationContext(), isChecked);
            break;
        case R.id.supper_user_connectipackbywifi:// ??????wifi??????ipack
            configRoutine.setIpackWifiModel(this.mContext.getApplicationContext(), isChecked);
            break;
        case R.id.supper_user_memory_mapped:
            configRoutine.setMemoryMappedModel(this.mContext.getApplicationContext(), isChecked);
            break;
        case R.id.supper_user_save_all_log:
            configRoutine.setSaveAllLog(this.mContext.getApplicationContext(), isChecked);
            sendBroadcast(new Intent(LogService.SWITCH_LOG_FILE_ACTION));
            break;
        case R.id.show_l1_l2_command:
            ServerManager.getInstance(mContext).setShowL1L2Command(isChecked);
            break;
        case R.id.show_l1_l2_command_buffer:
            dialog = new ProgressDialog(this.getParent());
            dialog.setMessage(getString(R.string.exe_info));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            new Thread(new RebootDataset(isChecked)).start();
            break;
        case R.id.supper_user_auto_upgrade:
            ServerManager.getInstance(mContext).setAutoUpgrade(isChecked);
            break;
        case R.id.setting_sharepush_ck:

            SharePreferencesUtil.getInstance(mContext).saveBoolean(ShareCommons.SHARE_DATA_KEY, isChecked);
            break;
        case R.id.cb_hs_external_gps:
            configRoutine.setHsExternalGPS(mContext, isChecked);
            break;
        }

    }

    /**
     * ???????????????
     */
    private class RebootDataset implements Runnable {
        boolean isChecked = false;

        public RebootDataset(boolean isChecked) {
            this.isChecked = isChecked;
        }

        @Override
        public void run() {
            ServerManager.getInstance(mContext).setShowL1L2CommandBuffer(isChecked);
            DatasetManager.getInstance(mContext).setConfigPropertyKey_DecodeOutputMS_MsgDetailBuffer();
            rebootTrace();
        }
    }

    /**
     * ????????????????????????
     */
    private void rebootTrace() {
        Intent intent = new Intent();
        intent.setAction(ServerMessage.ACTION_REBOOT_TRACE);
        sendOrderedBroadcast(intent, null);
    }

    /**
     * ?????????????????????
     */
    private void regisBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.ACTION_STARTDATASET_INIT_FINISH);
        registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WalkMessage.ACTION_STARTDATASET_INIT_FINISH)) {
                if (null != dialog) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        }
    };

    /**
     * ???????????????
     *
     * @param txt
     */
    protected void openDialog(String txt) {
        dialog = new ProgressDialog(this.getParent());
        dialog.setMessage(txt);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
    /**
     * ???????????????
     */
    protected void closeDialog() {
        dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mForceMgr.release();
        unregisterReceiver(myReceiver);
    }

}
