package com.walktour.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dingli.ott.task.service.WalktourAutoService;
import com.dingli.ott.util.OttUtil;
import com.dinglicom.data.control.DataTableStruct.RecordInfoKey;
import com.dinglicom.data.model.RecordTestInfo;
import com.walktour.Utils.AlertWakeLock;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DataSetFileUtil;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.FileOperater;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.ConfigIndoor;
import com.walktour.control.config.ConfigProNum;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.highspeedrail.GService;
import com.walktour.gui.highspeedrail.HighSpeedRailActivity;
import com.walktour.gui.highspeedrail.HsSelectNoActivity;
import com.walktour.gui.highspeedrail.model.HighSpeedLineModel;
import com.walktour.gui.highspeedrail.model.HighSpeedNoModel;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.map.MapView;
import com.walktour.gui.map.PointStatus;
import com.walktour.gui.metro.MetroSelectRouteActivity;
import com.walktour.gui.metro.MetroSettingCityActivity;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.innsmap.InnsmapSelectActivity;
import com.walktour.gui.newmap2.NewInfoTabActivity;
import com.walktour.gui.setting.SysBuildingManager;
import com.walktour.gui.share.UpDownService;
import com.walktour.gui.share.download.DownloadManager;
import com.walktour.gui.share.upload.UploadManager;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.xml.btu.TaskConverter;
import com.walktour.gui.task.parsedata.xml.btu.model.TestScheme;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.model.BuildingModel;
import com.walktour.model.FloorModel;
import com.walktour.service.XiaoMi8CustomService;
import com.walktour.service.automark.constant.AutoMarkConstant;
import com.walktour.service.automark.constant.MarkScene;
import com.walktour.service.innsmap.InnsmapFactory;
import com.walktour.service.innsmap.model.InnsmapModel;
import com.walktour.service.innsmap.model.InnsmapModel.Type;
import com.walktour.service.metro.MetroFactory;
import com.walktour.service.metro.model.MetroCity;
import com.walktour.service.metro.model.MetroRoute;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;

//import com.walktour.control.config.ConfigCityInfo;

/**
 * ??????????????????????????????????????????
 *
 * @author jianchao.wang
 */
@SuppressLint("InflateParams")
@SuppressWarnings("deprecation")
public class StartDialog {
    /**
     * ????????????
     */
    private static final String TAG = "StartDialog";
    /**
     * ??????????????????????????????
     */
    public static final int requestRailRouteCode = 10101;
    /**
     * ??????????????????????????????
     */
    public static final int requestMetroRouteCode = 10102;
    /**
     * ????????????????????????
     */
    public static final int requestCityCode = 10103;
    /**
     * ????????????????????????
     */
    public static final int requestInnsmapCode = 10104;
    /**
     * ????????????????????????
     */
    public static final int requestHsCode = 10105;
    /**
     * ????????????
     */
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String[] DEV_NAMES = new String[]{"FootSensor","HC-06","Dingli-Walk"};
    /**
     * DT???????????????
     */
    private RadioButton checkDT;
    /**
     * CQT???????????????
     */
    private RadioButton checkCQT;
    /**
     * ?????????????????????
     */
    private EditText editDeviceTag;
    /**
     * ?????????????????????????????????
     */
    private RelativeLayout previouslyLayout;
    /**
     * ????????????????????????
     */
    private CheckBox checkPreviously;
    /**
     * GPS????????????
     */
    private TextView gpsTip = null;
    /**
     * ????????????
     */
    private TextView editLongitude = null;
    /**
     * ????????????
     */
    private TextView editLatitude = null;
    /**
     * ??????????????????
     */
    private ImageView imageOutView = null;
    /**
     * ??????????????????
     */
    private FloorModel floorModel = null;
    /**
     * ??????????????????????????????
     */
    private SysBuildingManager mSysBuildingManager;
    /**
     * ??????????????????
     */
    private TextView imgTip = null;
    /**
     * ????????????
     */
    private String tester = "";
    /**
     * ????????????
     */
    private String testAddress = "";
    /**
     * ???????????????????????????
     */
    private Spinner spinnerTestPorNum;
    /**
     * ????????????
     */
    private Activity mActivity;
    /**
     * ???????????????
     */
    private View dialogView;
    /**
     * CQT????????????
     */
    private LinearLayout cqtTestLayout;
    // /** ?????????????????????*/
    // private RelativeLayout addressLayout;
    /**
     * DT??????CQT?????????
     */
    private RelativeLayout dtOrCqtCheckLayout;
    /**
     * ?????????????????????
     */
    private LinearLayout mTestCityLayout;
    /**
     * ?????????????????????
     */
    private LinearLayout mTestWayLayout;
    /**
     * ????????????????????????????????????
     */
    private RelativeLayout mBuildingTestLayout;
    /**
     * CQT????????????????????????
     */
    private RelativeLayout mCQTTestBuildingLayout;
    /**
     * GPS??????
     */
    private LinearLayout gpsLayout;
    /**
     * ?????????????????????
     */
    private LinearLayout floorOutviewLayout;
    /**
     * CU????????????
     */
    private LinearLayout mCULayout;
    /**
     * ????????????????????????
     */
    private LinearLayout mMetroMessageLayout;
    /**
     * ????????????
     */
    private TextView mTestCityTxt;
    /**
     * ????????????
     */
    private TextView mTestWayTxt;
    /**
     * ????????????????????????????????????
     */
    private TextView textBuild;
    /**
     * ?????????????????????
     */
    private Spinner spinnerFloor;
    /**
     * ????????????????????????
     */
    private Spinner spinnerBuild;
    /**
     * ????????????????????????
     */
    private EditText editLooptimes;

    /**
     * ??????????????????????????????
     */
    private EditText mEtOutloopInterval;

    /**
     * ?????????????????????????????????????????????????????????
     */
    private EditText mEtDifferentTaskInterval;

    /**
     * ?????????????????????
     */
    private EditText editTester;
    /**
     * ?????????????????????
     */
    private EditText editAddress;
    /**
     * ??????????????????????????????
     */
    private RelativeLayout looptimePPPDisableLayout;
    /**
     * ????????????????????????
     */
    private RelativeLayout mLoopTimeslayout;

    /**
     * ??????????????????????????????
     */
    private RelativeLayout mOutloopIntervallayout;
    /**
     * ??????????????????????????????
     */
    private CheckBox loopTimePPPDisable;
    /**
     * ??????????????????
     */
    private Button btnCamera;
    /**
     * ??????????????????
     */
    private ImageView floorImage;
    /**
     * ??????????????????
     */
    private TextView floorImageTip;
    /**
     * ????????????????????????
     */
    private Gallery gallery;
    /**
     * ????????????
     */
    private ConfigIndoor configIndoor;
    /**
     * ???????????????????????????
     */
    private boolean isIndoorTest = false;
    /**
     * ????????????????????????
     */
    private int itemGallaryPosition = 0;
    /**
     * ?????????????????????
     */
    private RelativeLayout netsnifferCheckLayout;
    /**
     * ?????????????????????
     */
    private RelativeLayout automarkCheckLayout;
    /**
     * ????????????????????????
     */
    private RelativeLayout dontSaveFilesLayout;
    /**
     * ???????????????????????????
     */
    private RelativeLayout testPorNumLayout;
    /**
     * ????????????
     */
    private ApplicationModel appModel = ApplicationModel.getInstance();
    /**
     * ?????????????????????
     */
    private CheckBox netsnifferCheckBox;
    /**
     * ??????VoLTE QCI??????
     */
    private CheckBox voteQCIInfoCheckBox;
    /**
     * ???????????????????????????
     */
    private CheckBox autoMarkCheckBox;
    /**
     * ????????????????????????
     */
    private CheckBox dontSaveDataCBox;
    /**
     * ??????????????????????????????
     */
    private final String defaultLonLatStr = "N/A";
    /**
     * ?????????????????????
     */
    private static int itemPostionBuilding;
    /**
     * gps?????????
     */
    private GpsInfo gpsInfo;
    /**
     * ????????????
     */
    private List<FloorModel> floorList = null;
    /**
     * gps?????????????????????
     */
    private TimerTask gpsTask = null;
    /**
     * ??????????????????
     */
    private Handler mHandler;
    /**
     * ??????????????????
     */
    private boolean isNetSniffer = false;
    /**
     * ????????????CU??????,????????????????????????????????????CU????????????
     */
    private boolean isCreateCUFile = false;
    /**
     * ??????????????????
     */
    private int mFromType;
    /**
     * ????????????ID
     */
    private SceneType mSceneType = SceneType.Manual;
    /**
     * ???????????????????????????
     */
    private MetroFactory mMetroFactory;
    /**
     * ???????????????????????????
     */
    private InnsmapFactory mInnsmapFactory;
    /**
     * ????????????????????????
     */
    private LinearLayout mInnsmapLayout;
    /**
     * CQT??????????????????????????????????????????????????????
     */
    private RelativeLayout mIndoorTestAutoMarkTypeLayout;
    /**
     * CQT?????????????????????????????????
     */
    private Spinner mAutoMarkTypeSpinner;
    /**
     * ????????????
     */
    private RelativeLayout mAutoMarkSceneLayout;
    /**
     * ?????????????????????
     */
    private BasicSpinner mAutoMarkSceneSpinner;
    /**
     * ???????????????
     */
    private RelativeLayout mGlonavinScetedLayout;
    /**
     * ????????????????????????
     */
    private BasicSpinner mGlonavinSpinner;
    /**
     * ???????????????????????????
     */
    private LinearLayout mInnsmapCityLayout;
    /**
     * ??????????????????????????????
     */
    private LinearLayout mInnsmapBuildingLayout;
    /**
     * ???????????????????????????
     */
    private LinearLayout mInnsmapFloorLayout;
    /**
     * ????????????????????????
     */
    private TextView mInnsmapCityText;
    /**
     * ???????????????????????????
     */
    private TextView mInnsmapBuildingText;
    /**
     * ????????????????????????
     */
    private TextView mInnsmapFloorText;
    /**
     * ????????????
     */
    private LinearLayout mTestNoLayout;
    /**
     * ????????????
     */
    private TextView mTestNoText;


    /**
     * ??????????????????????????????
     */
    private String mSingleStationName;

    /**
     * ???????????????????????????????????????
     */
    private LinearLayout mGlonavinLayout;

    /***
     * ????????????????????????
     */
    private CheckBox checkSMS;
    private EditText mEtConnectedModule;


    /**
     * ???????????????
     */
    private ProgressDialog mProgressDialog;
    /**
     * ???????????????
     */
    private BluetoothAdapter mBluetoothAdapter;
    /**
     * ???????????????
     */
    BluetoothManager mBluetoothManager;
    /**
     * ??????????????????????????????
     */
    private ArrayList<BluetoothDevice> mFoundModules;
    /**
     * ??????????????????????????????
     */
    private String mSelectedModuleAddress = null;
    /**
     * ?????????????????????????????????mShowing????????????
     */
    private Field mGlonavinDialogField;
    private RelativeLayout mCurrentFloorLayout;
    private RelativeLayout mFirstFloorHeightLayout;
    private RelativeLayout mFloorHeightLayout;
    /**
     * ????????????
     */
    private EditText mCurrentFloorEt;
    /**
     * ???????????????
     */
    private EditText mFirstFloorHeightEt;
    /**
     * ????????????
     */
    private EditText mFloorHeightEt;
    /*???????????????
    * */
    private EditText mEditScale;
    /*????????????
    * */
    private ImageAdapter imageAdapter;
    /*???????????????????????????
     * */
    private String imgPath;


    public StartDialog(final Activity activity, Handler handler, boolean isNetSniffer, String deviceTag,
                       int fromType, SceneType sceneType, String singleStationName) {
        this(activity, handler, isNetSniffer, deviceTag, fromType, sceneType);
        this.mSingleStationName = singleStationName;
    }

    public StartDialog(final Activity activity, Handler handler, boolean isNetSniffer, String deviceTag,
                       int fromType, SceneType sceneType) {
        this.mFromType = fromType;
        this.mSceneType = sceneType;
        this.mMetroFactory = MetroFactory.getInstance(activity);
        this.mInnsmapFactory = InnsmapFactory.getInstance(activity);
        gpsInfo = GpsInfo.getInstance();
        this.isNetSniffer = isNetSniffer;
        this.mHandler = handler;
        this.mActivity = activity;
        this.mSysBuildingManager = SysBuildingManager.getInstance(activity);
        isCreateCUFile = ConfigRoutine.getInstance().isGenCU(activity)
                && appModel.showInfoTypeCu();

        configIndoor = ConfigIndoor.getInstance(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        dialogView = inflater.inflate(R.layout.alert_dialog_starttest, null);
        this.findView();
        if ("".equals(editDeviceTag.getText().toString())){
            if (deviceTag != null) {
                editDeviceTag.setText(deviceTag);
                editDeviceTag.setEnabled(false);
            } else {
                editDeviceTag.setText(ConfigRoutine.getInstance().getDeviceTag());
            }
        }

    }
    boolean isPreciseSearch =false;//???????????????????????????????????????
    /**
     * ??????????????????????????????
     * 1.????????????????????????????????????????????????????????????????????????????????????
     * 2.????????????????????????????????????getName???NUll???????????????????????????
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//            final BleAdvertisedData badata = BleUtil.parseAdertisedData(scanRecord);
            foundModules(device);
        }
    };

    /**
     * ????????????
     */
    private void findView() {
        mCurrentFloorLayout = (RelativeLayout) dialogView.findViewById(R.id.rl_current_floor);
        mFirstFloorHeightLayout = (RelativeLayout) dialogView.findViewById(R.id.rl_first_floor_height);
        mFloorHeightLayout = (RelativeLayout) dialogView.findViewById(R.id.rl_floor_height);
        mCurrentFloorEt = (EditText) dialogView.findViewById(R.id.et_current_floor);
        mFirstFloorHeightEt = (EditText) dialogView.findViewById(R.id.et_first_floor_height);
        mFloorHeightEt = (EditText) dialogView.findViewById(R.id.et_floor_height);
        mEditScale=(EditText)dialogView.findViewById(R.id.EditScale);
        mEditScale.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        cqtTestLayout = (LinearLayout) dialogView.findViewById(R.id.cqt_test_layout);
        mLoopTimeslayout = (RelativeLayout) dialogView.findViewById(R.id.outloop_time_layout);
        mOutloopIntervallayout = (RelativeLayout) dialogView.findViewById(R.id.outloop_interval_layout);
        if (WalktourApplication.isExitGroup()) {
            mLoopTimeslayout.setVisibility(View.GONE);
            mOutloopIntervallayout.setVisibility(View.GONE);
        }
        editLooptimes = (EditText) dialogView.findViewById(R.id.editLooptimes);
        editLooptimes.setText(String.valueOf(appModel.getOutLooptimes()));
        mEtOutloopInterval = (EditText) dialogView.findViewById(R.id.et_outloop_interval);
        mEtDifferentTaskInterval = (EditText) dialogView.findViewById(R.id.et_different_task_interval);
        if (ServerManager.getInstance(mActivity).getUploadServer() == ServerManager.SERVER_ATU
                || ServerManager.getInstance(mActivity).getUploadServer() == ServerManager.SERVER_BTU) {
            File file = new File(ServerManager.getInstance(this.mActivity).getUploadServer() == ServerManager.SERVER_BTU
                    ? ServerManager.FILE_BTU_TASK : ServerManager.FILE_ATU_TASK);
            TaskConverter converter = new TaskConverter(mActivity, file);
            List<TestScheme> schemeList = converter.convertTestScheme();
            if (null != schemeList && schemeList.size() > 0) {
                editLooptimes.setText(schemeList.get(0).getCommandListRepeat() + "");
            } else {
                editLooptimes.setText("1");
            }
        }

        editTester = (EditText) dialogView.findViewById(R.id.EditTester);
        editAddress = (EditText) dialogView.findViewById(R.id.EditAddress);
        editDeviceTag = (EditText) dialogView.findViewById(R.id.EditDeviceTag);

        looptimePPPDisableLayout = (RelativeLayout) dialogView.findViewById(R.id.outLooptimes_ppp_disable_layout);
        looptimePPPDisableLayout.setVisibility(View.VISIBLE);
        loopTimePPPDisable = (CheckBox) dialogView.findViewById(R.id.outLooptimes_ppp_disable_check);
        loopTimePPPDisable.setChecked(appModel.isOutLoopDisconnetNetwork());

        // addressLayout= (RelativeLayout)
        // dialogView.findViewById(R.id.addressLayout);
        dtOrCqtCheckLayout = (RelativeLayout) dialogView.findViewById(R.id.dt_cqt_check_layout);
        mTestCityLayout = (LinearLayout) dialogView.findViewById(R.id.test_city_layout);
        mTestWayLayout = (LinearLayout) dialogView.findViewById(R.id.test_way_layout);
        this.mMetroMessageLayout = (LinearLayout) dialogView.findViewById(R.id.metroMessageLayout);
        gallery = (Gallery) dialogView.findViewById(R.id.gallery);
        mBuildingTestLayout = (RelativeLayout) dialogView.findViewById(R.id.reTextBuild);
        // GPS???????????????????????????
        gpsLayout = (LinearLayout) dialogView.findViewById(R.id.gpsLinearlayout);
        mCQTTestBuildingLayout = (RelativeLayout) dialogView.findViewById(R.id.reSpinnerBuild);
        floorOutviewLayout = (LinearLayout) dialogView.findViewById(R.id.floorOutviewlayout);
        mCULayout = (LinearLayout) dialogView.findViewById(R.id.LinearLayoutCU);
        testPorNumLayout = (RelativeLayout) dialogView.findViewById(R.id.testPorNumRly);

        if (isCreateCUFile) {
            ArrayAdapter<String> networkAdapter = new ArrayAdapter<String>(mActivity, R.layout.simple_spinner_custom_layout,
                    mActivity.getResources().getStringArray(R.array.cu_Test_Network));
            networkAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            ((Spinner) dialogView.findViewById(R.id.SpinnerNetwork)).setAdapter(networkAdapter);

            mCULayout.setVisibility(View.VISIBLE); // ??????CU??????????????????
            ((TextView) dialogView.findViewById(R.id.TextviewAddress)).setText(R.string.main_city); // ??????????????????????????????
            ((RelativeLayout) dialogView.findViewById(R.id.RL_DeviceTag)).setVisibility(View.GONE); // ????????????????????????
            indoorShow(View.GONE); // ?????????????????????
            ((EditText) dialogView.findViewById(R.id.EditTestMobilePhone))
                    .setText(MyPhoneState.getInstance().getSimNumber(mActivity));
        }
        if (ConfigProNum.getInstance(mActivity).getProNoList().size() > 0) {
            spinnerTestPorNum = (Spinner) dialogView.findViewById(R.id.SpinnerTestPorNum);

            final ArrayAdapter<String> adptProNo = new ArrayAdapter<String>(mActivity, R.layout.simple_spinner_custom_layout,
                    ConfigProNum.getInstance(mActivity).getProNoList());
            adptProNo.setDropDownViewResource(R.layout.spinner_dropdown_item);

            spinnerTestPorNum.setAdapter(adptProNo);
            testPorNumLayout.setVisibility(View.VISIBLE);
        }
        mTestCityTxt = (TextView) dialogView.findViewById(R.id.testcitytxt);
        mTestWayTxt = (TextView) dialogView.findViewById(R.id.testwaytxt);
        if (this.mSceneType == SceneType.Metro)
            this.updateMetroCityAndRoute();
        floorImage = (ImageView) dialogView.findViewById(R.id.floor_image);
        floorImageTip = (TextView) dialogView.findViewById(R.id.floor_image_tip);
        imgTip = (TextView) dialogView.findViewById(R.id.imgTip);
        gpsTip = (TextView) dialogView.findViewById(R.id.gpsTip);
        editLongitude = (TextView) dialogView.findViewById(R.id.editLongitude);
        editLatitude = (TextView) dialogView.findViewById(R.id.editLatitude);

        checkCQT = (RadioButton) dialogView.findViewById(R.id.CheckCQT);
        automarkCheckLayout = (RelativeLayout) dialogView.findViewById(R.id.automark_check);
        if (this.mSceneType == SceneType.Metro) {
            automarkCheckLayout = (RelativeLayout) dialogView.findViewById(R.id.metro_automark_check);
        }
        netsnifferCheckLayout = (RelativeLayout) dialogView.findViewById(R.id.netsniffer_check);
        if (appModel.getNetList().contains(WalkStruct.ShowInfoType.TCPIPCapture)) {
            netsnifferCheckLayout.setVisibility(View.VISIBLE);
        } else {
            this.isNetSniffer = false;
        }
        dontSaveFilesLayout = (RelativeLayout) dialogView.findViewById(R.id.layout_dontsaveFile);
        if (appModel.getNetList().contains(WalkStruct.ShowInfoType.DontSaveFile)) {
            dontSaveFilesLayout.setVisibility(View.VISIBLE);
        }
        netsnifferCheckBox = (CheckBox) dialogView.findViewById(R.id.start_netsniffer);
        netsnifferCheckBox.setChecked(this.isNetSniffer);
        voteQCIInfoCheckBox = (CheckBox) dialogView.findViewById(R.id.start_volte_qciinfo);
        voteQCIInfoCheckBox.setChecked(ConfigRoutine.getInstance().isVoLTEQCIInfo(this.mActivity));
        voteQCIInfoCheckBox.setOnCheckedChangeListener(this.mCheckListener);
        mTestNoLayout = (LinearLayout) dialogView.findViewById(R.id.test_no_layout);
        mTestNoText = (TextView) dialogView.findViewById(R.id.test_no_txt);
        checkSMS = (CheckBox) dialogView.findViewById(R.id.check_sms);
        checkSMS.setChecked(ConfigRoutine.getInstance().isSMSInfo(this.mActivity));
        checkSMS.setOnCheckedChangeListener(this.mCheckListener);
        dontSaveDataCBox = (CheckBox) dialogView.findViewById(R.id.start_dongSaveFile);
        autoMarkCheckBox = (CheckBox) dialogView.findViewById(R.id.start_automark);
        if (this.mSceneType == SceneType.Metro) {
            autoMarkCheckBox = (CheckBox) dialogView.findViewById(R.id.metro_start_automark);
            if (!MyPhoneState.isZhForLanguage(mActivity)) {
                autoMarkCheckBox.setButtonDrawable(R.drawable.metro_switch_button_bg_en);
            }
            autoMarkCheckBox.setChecked(true);
        }
        autoMarkCheckBox.setOnCheckedChangeListener(mCheckListener);
        appModel.setInnsmapTest(false);
        this.mInnsmapLayout = (LinearLayout) dialogView.findViewById(R.id.innsmap_layout);
        this.mIndoorTestAutoMarkTypeLayout = (RelativeLayout) dialogView.findViewById(R.id.automark_type_check_layout);
        mGlonavinLayout = (LinearLayout) dialogView.findViewById(R.id.ll_glonavin);
        mAutoMarkSceneSpinner = (BasicSpinner) dialogView.findViewById(R.id.start_automark_scene);
        mAutoMarkSceneLayout = (RelativeLayout) dialogView.findViewById(R.id.automark_scene_check_layout);
        mGlonavinSpinner= (BasicSpinner) dialogView.findViewById(R.id.glonavin_type);
        mGlonavinScetedLayout= (RelativeLayout) dialogView.findViewById(R.id.glonavin_check_layout);
        if (ConfigRoutine.getInstance().isHsExternalGPS(mActivity)&&
                (appModel.getSelectScene()==SceneType.HighSpeedRail||appModel.getSelectScene()==SceneType.Metro)){
            mGlonavinScetedLayout.setVisibility(View.VISIBLE);
        }
        mEtConnectedModule = (EditText) mGlonavinLayout.findViewById(R.id.et_connected_module);
        dialogView.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(mActivity);
                mProgressDialog.setMessage(mActivity.getString(R.string.glonavin_auto_mark_scanning_module));
                mProgressDialog.show();
                if (null == mBluetoothManager) {
                    mBluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
                }
                if (null == mBluetoothAdapter) {
                    mBluetoothAdapter = mBluetoothManager.getAdapter();
                }
                mBluetoothAdapter.enable();
                mFoundModules = new ArrayList<>();

                 UUID uuid=null;

                if ((appModel.getSelectScene()==SceneType.Metro||appModel.getSelectScene()==SceneType.HighSpeedRail)){
                    String glonavinType = String.valueOf(mGlonavinSpinner.getSelectedItem());
                    if("????????????".equals(glonavinType)||"????????????".equals(glonavinType)){
                        uuid   = null;
                    }else {
                        uuid=UUIDSManager.GNV_SERVICE_UUID;
                    }
                }else {
                    uuid=UUIDSManager.GNV_SERVICE_UUID;
                }
                if (uuid==null){
                    mBluetoothAdapter.startDiscovery();
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                }else {
                    UUID[] uuids={uuid};
                    mBluetoothAdapter.startLeScan(uuids,mLeScanCallback);
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //10????????????????????????
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        BlueToothScanFisher();
                    }
                }, 6 * 1000);

            }
        });
        if (!autoMarkCheckBox.isChecked())
            this.mIndoorTestAutoMarkTypeLayout.setVisibility(View.GONE);
        this.mAutoMarkTypeSpinner = (Spinner) dialogView.findViewById(R.id.start_automark_type);
        if (autoMarkCheckBox.isChecked() && this.mSceneType == SceneType.Manual && this.appModel.hasInnsmapTest())
            this.mIndoorTestAutoMarkTypeLayout.setVisibility(View.VISIBLE);
        this.mInnsmapCityLayout = (LinearLayout) dialogView.findViewById(R.id.innsmap_city_layout);
        this.mInnsmapCityLayout.setOnClickListener(mClickListener);
        this.mInnsmapBuildingLayout = (LinearLayout) dialogView.findViewById(R.id.innsmap_building_layout);
        this.mInnsmapBuildingLayout.setOnClickListener(mClickListener);
        this.mInnsmapFloorLayout = (LinearLayout) dialogView.findViewById(R.id.innsmap_floor_layout);
        this.mInnsmapFloorLayout.setOnClickListener(mClickListener);
        this.mInnsmapCityText = (TextView) dialogView.findViewById(R.id.innsmap_city);
        this.mInnsmapBuildingText = (TextView) dialogView.findViewById(R.id.innsmap_building);
        this.mInnsmapFloorText = (TextView) dialogView.findViewById(R.id.innsmap_floor);
        this.checkPreviously = (CheckBox) dialogView.findViewById(R.id.checkPreviously);
        this.previouslyLayout = (RelativeLayout) dialogView.findViewById(R.id.previously_layout);
        /******* ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? ************/
        // ???????????????
        boolean isExistPrevious = false;
        LogUtil.d(TAG,
                "----MapFactory.getMapData().pointStatusStack.Size=" + MapFactory.getMapData().getPointStatusStack().size());
        if (!MapFactory.getMapData().getPointStatusStack().isEmpty()) {
            for (int i = 0; i < MapFactory.getMapData().getPointStatusStack().size(); i++) {
                int status = MapFactory.getMapData().getPointStatusStack().elementAt(i).getStatus();
                LogUtil.d(TAG, "---s=" + status);
                if (status == PointStatus.POINT_STATUS_PREVIOUS) {
                    isExistPrevious = true;
                    break;
                }
            }
        }
        if (isExistPrevious) {
            checkPreviously.setEnabled(true);
            checkPreviously.setChecked(true);
            appModel.setPreviouslyTest(true);
            LogUtil.w(TAG, "---is exist vitual point");
        } else {
            checkPreviously.setChecked(false);
            checkPreviously.setEnabled(false);
            appModel.setPreviouslyTest(false);
        }
        checkDT = (RadioButton) dialogView.findViewById(R.id.CheckDT);
        imageOutView = (ImageView) dialogView.findViewById(R.id.imageCamera);
        btnCamera = (Button) dialogView.findViewById(R.id.btnCamera);

        btnCamera.setOnClickListener(mClickListener);
        textBuild = (TextView) dialogView.findViewById(R.id.textBuild);
        spinnerFloor = (Spinner) dialogView.findViewById(R.id.SpinnerFloor);
        spinnerBuild = (Spinner) dialogView.findViewById(R.id.SpinnerBuild);
        gpsTip.setText(R.string.main_indoor_gpsnosearch);
        // ????????????????????????
        setPositon(defaultLonLatStr, defaultLonLatStr);
        if (appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
            if (itemPostionBuilding >= 0) {
                dtOrCqtCheckLayout.setVisibility(View.GONE);
                mCQTTestBuildingLayout.setVisibility(View.GONE);
                gpsLayout.setVisibility(View.GONE);
            } else {
                gpsLayout.setVisibility(View.VISIBLE);
                indoorShow(View.GONE);
            }
            if (ConfigRoutine.getInstance().autoMarkPoint()) {
                automarkCheckLayout.setVisibility(View.VISIBLE);
            }
        } else {
            mBuildingTestLayout.setVisibility(View.GONE);
            checkCQT.setVisibility(View.VISIBLE);
            indoorShow(checkCQT.isChecked() ? View.VISIBLE : View.GONE);
            automarkCheckLayout.setVisibility(
                    checkCQT.isChecked() && ConfigRoutine.getInstance().autoMarkPoint() ? View.VISIBLE : View.GONE);
        }
        // ?????????????????????
        appModel.setIndoorTest(false);

        // ????????????????????????
        checkCQT.setOnCheckedChangeListener(mCheckListener);
        // Gps??????
        checkDT.setOnCheckedChangeListener(mCheckListener);

        // Gps??????
        checkPreviously.setOnCheckedChangeListener(mCheckListener);
        // checkPreviously.setChecked(false);
        mTestCityLayout.setOnClickListener(mClickListener);
        mTestWayLayout.setOnClickListener(mClickListener);
        // ???GPS????????????
        checkDT.setChecked(gpsInfo.isJobTestGpsOpen());

        init();

        // ??????????????????????????????
        IntentFilter broadCaseIntent = new IntentFilter();
        broadCaseIntent.addAction(GpsInfo.gpsLocationChanged);
        broadCaseIntent.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        broadCaseIntent.addAction(BluetoothDevice.ACTION_FOUND);
        broadCaseIntent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mActivity.registerReceiver(testJobDoneReceiver, broadCaseIntent);
    }// end constructor

    /**
     * @date on
     * @describe ??????????????????
     * @author jinfeng.xie
     */
    private void BlueToothScanFisher() {
        mProgressDialog.dismiss();
        if (null != mFoundModules && !mFoundModules.isEmpty()) {
            BasicDialog.Builder builder = new BasicDialog.Builder(mActivity);
            /*
            * ????????????*/
            ArrayList<String> macAddress=new ArrayList<>();
            for (int i = 0; i < mFoundModules.size(); i++) {
                if (!macAddress.contains(mFoundModules.get(i).getAddress())){
                    macAddress.add(mFoundModules.get(i).getAddress());
                }
            }
            final String[] arr = new String[macAddress.size()];
            for (int k=0;k<arr.length;k++){
                arr[k] = macAddress.get(k);
            }
            mSelectedModuleAddress = mFoundModules.get(0).getAddress();
            BasicDialog dialog = builder.setTitle(mActivity.getString(R.string.glonavin_auto_mark_select_module)).setSingleChoiceItems(arr, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        mGlonavinDialogField = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        mGlonavinDialogField.setAccessible(true);// ???????????????????????????
                        mGlonavinDialogField.set(dialog, false);
                        mSelectedModuleAddress = arr[which];
                        LogUtil.i(TAG, "selectedAddress:" + mSelectedModuleAddress);
                    } catch (Exception e) {
                        LogUtil.e(TAG, e.getMessage());
                    }

                }
            }).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        mGlonavinDialogField = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        mGlonavinDialogField.setAccessible(true);// ???????????????????????????
                        mGlonavinDialogField.set(dialog, true);
                        dialog.dismiss();
                    } catch (Exception e) {
                        LogUtil.e(TAG, e.getMessage());
                    }
                }
            }).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        mGlonavinDialogField = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        mGlonavinDialogField.setAccessible(true);// ???????????????????????????
                        mGlonavinDialogField.set(dialog, true);
                        mEtConnectedModule.setText(mSelectedModuleAddress);
                        //?????????????????????????????????
                        if (appModel.getSelectScene()==SceneType.Metro||appModel.getSelectScene()==SceneType.HighSpeedRail){
                            GService.blueToothAddress = mSelectedModuleAddress;
                        }else {
                            WalktourConst.connectBlueToothAddress=mSelectedModuleAddress;
                        }
                        dialog.dismiss();
                    } catch (Exception e) {
                        LogUtil.e(TAG, e.getMessage());
                    }
                }
            }).show();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        } else {
            //??????????????????
            ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_no_module));
        }
    }

    /**
     * ??????????????????
     */
    private void selectTestRoute() {
        if (this.mSceneType == SceneType.HighSpeedRail) {
            Intent intent = new Intent(mActivity, HighSpeedRailActivity.class);
            intent.putExtra(WalkMessage.KEY_IS_FROM_STARTDIALOG, true);
            mActivity.startActivityForResult(intent, requestRailRouteCode);
            mActivity.overridePendingTransition(R.anim.open_next, R.anim.close_main);
        } else if (this.mSceneType == SceneType.Metro) {
            if (this.mMetroFactory.getCurrentCity(false) == null) {
                ToastUtil.showToastShort(mActivity, R.string.metro_no_select_city);
            } else {
                Intent intent = new Intent(mActivity, MetroSelectRouteActivity.class);
                mActivity.startActivityForResult(intent, requestMetroRouteCode);
                mActivity.overridePendingTransition(R.anim.open_next, R.anim.close_main);
            }
//            Intent intent = new Intent(this.mActivity, MetroSelectRouteActivity.class);
//            this.mActivity.startActivityForResult(intent, requestMetroRouteCode);
//            this.mActivity.overridePendingTransition(R.anim.open_next, R.anim.close_main);
        }
    }

    /**
     * ??????????????????
     */
    protected void selectTestCity() {
        Intent intent = new Intent(mActivity, MetroSettingCityActivity.class);
        intent.putExtra("is_select", true);
        mActivity.startActivityForResult(intent, requestCityCode);
        mActivity.overridePendingTransition(R.anim.open_next, R.anim.close_main);
    }

    /**
     * ????????????????????????
     */
    public void updateInnsmap() {
        InnsmapModel city = this.mInnsmapFactory.getCurrentCity();
        if (city != null) {
            this.mInnsmapCityText.setText(city.getName());
            this.mInnsmapCityText.setTextColor(mActivity.getResources().getColor(R.color.light_blue));
        } else {
            this.mInnsmapCityText.setText(R.string.work_order_fj_select_action);
            this.mInnsmapCityText.setTextColor(mActivity.getResources().getColor(R.color.gray));
        }
        InnsmapModel building = this.mInnsmapFactory.getCurrentBuilding();
        if (building != null) {
            this.mInnsmapBuildingText.setText(building.getName());
            this.mInnsmapBuildingText.setTextColor(mActivity.getResources().getColor(R.color.light_blue));
        } else {
            this.mInnsmapBuildingText.setText(R.string.work_order_fj_select_action);
            this.mInnsmapBuildingText.setTextColor(mActivity.getResources().getColor(R.color.gray));
        }
        InnsmapModel floor = this.mInnsmapFactory.getCurrentFloor(false);
        if (floor != null) {
            this.mInnsmapFloorText.setText(floor.getName());
            this.mInnsmapFloorText.setTextColor(mActivity.getResources().getColor(R.color.light_blue));
        } else {
            this.mInnsmapFloorText.setText(R.string.work_order_fj_select_action);
            this.mInnsmapFloorText.setTextColor(mActivity.getResources().getColor(R.color.gray));
        }
    }

    /**
     * ??????????????????????????????????????????
     */
    public void updateMetroCityAndRoute() {
        MetroCity city = this.mMetroFactory.getCurrentCity(false);
        if (city != null) {
            this.mTestCityTxt.setText(city.getName());
            this.mTestCityTxt.setTextColor(mActivity.getResources().getColor(R.color.light_blue));
        } else {
            this.mTestCityTxt.setText(R.string.work_order_fj_select_action);
            this.mTestCityTxt.setTextColor(mActivity.getResources().getColor(R.color.gray));
        }
        MetroRoute route = this.mMetroFactory.getCurrentRoute(false);
        if (route == null) {
            this.mTestWayTxt.setText(R.string.work_order_fj_select_action);
            this.mTestWayTxt.setTextColor(mActivity.getResources().getColor(R.color.gray));
            if (city != null)
                this.editDeviceTag.setText(city.getName());
            else
                this.editDeviceTag.setText("");
        } else {
            this.mTestWayTxt.setText(route.getName() + "," + route.getRouteSelectDesc());
            this.mTestWayTxt.setTextColor(mActivity.getResources().getColor(R.color.light_blue));
            if (city != null)
                this.editDeviceTag.setText(city.getName() + "_" + route.getName());
            else
                this.editDeviceTag.setText("");
        }
    }

    /**
     * ??????????????????
     */
    public void updateRailWay() {
        if (mSceneType == SceneType.HighSpeedRail) {
            HighSpeedLineModel hsModel =  SharePreferencesUtil.getInstance(mActivity).getObjectFromShare(WalktourConst.CURRENT_HS, HighSpeedLineModel.class);
            HighSpeedNoModel hsNo = SharePreferencesUtil.getInstance(mActivity).getObjectFromShare(WalktourConst.CURRENT_HS_NO, HighSpeedNoModel.class);
            if (hsModel == null) {
                mTestWayTxt.setTextColor(mActivity.getResources().getColor(R.color.gray));
                mTestWayTxt.setText(R.string.work_order_fj_select_action);
            } else {
                mTestWayTxt.setTextColor(mActivity.getResources().getColor(R.color.light_blue));
                mTestWayTxt.setText(hsModel.hsname);
            }
            if (hsNo != null && hsNo.routes != null) {
                mTestNoText.setTextColor(mActivity.getResources().getColor(R.color.light_blue));
                mTestNoText.setText(hsNo.getRoutes().getRouteSelectDesc());
                this.editDeviceTag.setText(hsNo.getRoutes().getRouteSelectDesc());
            } else {
                mTestNoText.setTextColor(mActivity.getResources().getColor(R.color.gray));
                mTestNoText.setText(R.string.work_order_fj_select_action);
                this.editDeviceTag.setText("");
            }

        }
    }

    /**
     * ????????????DT????????????CQT??????
     *
     * @param isCheckDT ????????????DT?????????????????????CQT??????
     */
    public void checkDTOrCQT(boolean isCheckDT) {
        if (isCheckDT)
            checkDT.setChecked(true);
        else
            checkCQT.setChecked(true);
    }

    /*
     * ??????????????????????????? private void initialCity() { final List<String> provinceList =
     * ConfigCityInfo.getInstance(this.context).getProvinceList();
     *
     * final ArrayAdapter<String> adptProv = new ArrayAdapter<String>(context,
     * R.layout.simple_spinner_custom_layout, provinceList);
     * adptProv.setDropDownViewResource(R.layout.spinner_dropdown_item);
     *
     * spinnerPrivoic.setAdapter(adptProv); // ?????????????????????????????????????????????????????????????????????
     * spinnerPrivoic.setOnItemSelectedListener(new OnItemSelectedListener() {
     *
     * @Override public void onItemSelected(AdapterView<?> parent, View view, int
     * position, long id) {
     *
     * LogUtil.w(tag, "--prov:" + parent.getSelectedItem().toString()); final
     * ArrayList<String> cityList =
     * cityInfos.getCityListByprov(provinceList.get(position));
     *
     * if (cityList != null) { final ArrayAdapter<String> adptCity = new
     * ArrayAdapter<String>(context, R.layout.simple_spinner_custom_layout,
     * cityList);
     * adptCity.setDropDownViewResource(R.layout.spinner_dropdown_item);
     * spinnerCity.setAdapter(adptCity); } }
     *
     * @Override public void onNothingSelected(AdapterView<?> parent) {
     *
     * } }); }
     */

    /**
     * ????????????????????????
     */
    private final BroadcastReceiver testJobDoneReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(GpsInfo.gpsLocationChanged) && gpsInfo.getLocation() != null) {
                clearGpstimer();
                setPositon(StringUtil.formatStr(gpsInfo.getLocation().getLongitude() + ""),
                        StringUtil.formatStr(gpsInfo.getLocation().getLatitude() + ""));
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                foundModules(device);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                BlueToothScanFisher();
            }
        }
    };
    void foundModules( BluetoothDevice device) {
        String deviceName = device.getName();
        String devAddress = device.getAddress();
        Log.e(TAG, "deviceName:" + deviceName + ",devAddress" + devAddress);
//        if ((appModel.getSelectScene() == SceneType.HighSpeedRail || appModel.getSelectScene() == SceneType.Metro)
//                && ConfigRoutine.getInstance().isHsExternalGPS(mActivity)){
        if (!TextUtils.isEmpty(deviceName) && isHave(DEV_NAMES,deviceName)) {
                if (!isPreciseSearch){
                    mFoundModules.clear();
                }
                if (!mFoundModules.contains(device)){
                    mFoundModules.add(device);
                }
                isPreciseSearch=true;
        }else if (!isPreciseSearch && TextUtils.isEmpty(deviceName)&& !mFoundModules.contains(device)){
                mFoundModules.add(device);
        }
//        }else{
////            if (!TextUtils.isEmpty(deviceName) && deviceName.contains(DEV_NAME_PREFIX) ) {
////                if (!isPreciseSearch){
////                    mFoundModules.clear();
////                }
//                if (!mFoundModules.contains(device)){
//                    mFoundModules.add(device);
//                }
////                isPreciseSearch=true;
////            }else if (!isPreciseSearch && TextUtils.isEmpty(deviceName)&& !mFoundModules.contains(device)){
////                mFoundModules.add(device);
////            }
//        }
    }

    public   boolean isHave(String[] strs,String s){
        if (strs==null||s==null){
            return false;
        }
        /*???????????????????????????????????????????????????????????????????????????????????????????????????????????????*/
        for (int i=0;i<strs.length;i++){
            if (strs[i].toLowerCase().equals(s.toLowerCase())){
                return true;
            }
        }
        return false;
    }
    /**
     * ?????????????????????
     *
     * @param longitude
     * @param latitude
     */
    private void setPositon(String longitude, String latitude) {
        if (!this.defaultLonLatStr.equals(longitude) && gpsTip != null) {
            gpsTip.setText(R.string.main_indoor_gpssucces);
        }
        if (editLatitude != null) {
            editLongitude.setText(longitude);
            editLatitude.setText(latitude);
        }
    }

    /**
     * ???????????????
     */
    private void init() {
        boolean isAHworkorder = appModel.isAnHuiTest();
        // ?????? ??????????????????????????????
        final List<BuildingModel> buildingList = configIndoor.getBuildings(mActivity, isAHworkorder);
        // ??????????????????????????????
        if (appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
            configIndoor.getBuildings(mActivity, isAHworkorder);
            if (itemPostionBuilding >= 0) {
                floorList = new ArrayList<FloorModel>();
                final List<String> floorNames = new ArrayList<String>();
                if (buildingList.size() > 0) {
                    btnCamera.setEnabled(true);
                    itemPostionBuilding = buildingList.size() > itemPostionBuilding ? itemPostionBuilding : 0;
                    floorList.addAll(buildingList.get(itemPostionBuilding).getFloors());
                    appModel.setBuildModel(buildingList.get(itemPostionBuilding));
                } else {
                    btnCamera.setEnabled(false);
                    floorImageTip.setText(R.string.main_buildnotfound);
                    floorList.clear();
                }
                for (int i = 0; i < floorList.size(); i++) {
                    floorNames.add(floorList.get(i).getName());
                }
                if (floorList.size() == 0) {
                    floorNames.add(this.mActivity.getString(R.string.none));
                    isIndoorTest = false;
                    if (!buildingList.isEmpty()) {
                        floorImageTip.setText(R.string.main_floornotfound);
                    }
                    floorImage.setImageDrawable(this.mActivity.getResources().getDrawable(R.drawable.safe_img));
                }
                final ArrayAdapter<String> adptFloor = new ArrayAdapter<String>(this.mActivity,
                        R.layout.simple_spinner_custom_layout, floorNames);
                adptFloor.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerFloor.setAdapter(adptFloor);
                if (buildingList.size() > 0) {
                    // ????????????????????????????????????????????????
                    itemPostionBuilding = buildingList.size() > itemPostionBuilding ? itemPostionBuilding : 0;
                    textBuild.setText(buildingList.get(itemPostionBuilding).getName());
                    // ???????????????????????????????????????????????????????????????????????????
                    spinnerFloor.setOnItemSelectedListener(new MyFloorListener(floorList));
                } else {
                    textBuild.setText(R.string.none);
                }
            }
        } else {// ?????????????????????????????????
            // ?????? ??????????????????????????????
            List<String> buildingNames = new ArrayList<String>();
            for (int i = 0; i < buildingList.size(); i++) {
                buildingNames.add(buildingList.get(i).getName());
            }
            String[] names = new String[buildingNames.size() + 1];
            names[0] = mActivity.getString(R.string.none); // ?????????
            int i = 1;
            for (String name : buildingNames) {
                names[i++] = name;
            }
            buildingNames = Arrays.asList(names);
            final ArrayAdapter<String> adptBuild = new ArrayAdapter<String>(mActivity, R.layout.simple_spinner_custom_layout,
                    buildingNames);
            adptBuild.setDropDownViewResource(R.layout.spinner_dropdown_item);

            spinnerBuild.setAdapter(adptBuild);
            // ?????????????????????????????????????????????????????????????????????
            spinnerBuild.setOnItemSelectedListener(new MyBuilderListener(buildingList));
        }
        if (mSceneType == SceneType.Manual/* && appModel.hasInnsmapTest()*/) {
            String[] automarkTypes = mActivity.getResources().getStringArray(R.array.auto_mark_type);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.simple_spinner_custom_layout,
                    automarkTypes);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            this.mAutoMarkTypeSpinner.setAdapter(adapter);
            this.mAutoMarkTypeSpinner.setOnItemSelectedListener(new MyAutoMarkTypeListener());
            String[] automarkNotCommonTypes = mActivity.getResources().getStringArray(R.array.auto_mark_scene);
            final ArrayAdapter<String> notCommonAdapter = new ArrayAdapter<String>(mActivity, R.layout.simple_spinner_custom_layout,
                    automarkNotCommonTypes);
            notCommonAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            this.mAutoMarkSceneSpinner.setAdapter(notCommonAdapter);
            this.mAutoMarkSceneSpinner.setOnItemSelectedListener(new MyAutoMarkSceneListener());
            if (!"".equals(WalktourConst.connectBlueToothAddress)) {
                mEtConnectedModule.setText(WalktourConst.connectBlueToothAddress);
            }
        }
        if (mSceneType == SceneType.HighSpeedRail) {// ??????????????????
            // ????????????
            dtOrCqtCheckLayout.setVisibility(View.GONE);
            mTestWayLayout.setVisibility(View.VISIBLE);

            HighSpeedNoModel hsNo = SharePreferencesUtil.getInstance(mActivity).getObjectFromShare(WalktourConst.CURRENT_HS_NO, HighSpeedNoModel.class);
            if (hsNo != null && hsNo.routes != null) {
                mTestNoText.setText("" + hsNo.getRoutes().getRouteSelectDesc());
            }
            checkCQT.setChecked(false);
            checkDT.setChecked(true);
            checkPreviously.setChecked(false);
            // ???????????????
            automarkCheckLayout.setVisibility(View.GONE);
            this.previouslyLayout.setVisibility(View.GONE);
            appModel.setGpsTest(true);
            appModel.setPreviouslyTest(false);
            setPositon(defaultLonLatStr, defaultLonLatStr);
            // ?????????????????????????????????????????????GPS??????
            gpsInfo.openGps(mActivity, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
            // GPS????????????
            if (!appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
                gpsLayout.setVisibility(View.VISIBLE);
            }
            if (ConfigRoutine.getInstance().isHsExternalGPS(mActivity)) {
                mGlonavinLayout.setVisibility(View.VISIBLE);
                mTestNoLayout.setVisibility(View.VISIBLE);
                mTestWayLayout.setVisibility(View.GONE);
                mTestNoLayout.setOnClickListener(mClickListener);
                if (!"".equals(GService.blueToothAddress)) {
                    mEtConnectedModule.setText(GService.blueToothAddress);
                }
            }



            // ??????????????????
            gpsTip.setText(R.string.main_indoor_waitgps);
            updateRailWay();

            String[] automarkNotCommonTypes = mActivity.getResources().getStringArray(R.array.glonavin_type_gaotie);
            final ArrayAdapter<String> notCommonAdapter = new ArrayAdapter<String>(mActivity, R.layout.simple_spinner_custom_layout,
                    automarkNotCommonTypes);
            notCommonAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            this.mGlonavinSpinner.setAdapter(notCommonAdapter);
            this.mGlonavinSpinner.setOnItemSelectedListener(new MyGlonavintypeListener());
            initGlonavinSpinner();
        } else if (mSceneType == SceneType.Metro) {// ??????????????????
            // ????????????
            dtOrCqtCheckLayout.setVisibility(View.GONE);
            mTestCityLayout.setVisibility(View.VISIBLE);
            mTestWayLayout.setVisibility(View.VISIBLE);
            mMetroMessageLayout.setVisibility(View.VISIBLE);
            checkCQT.setChecked(false);
            checkDT.setChecked(false);
            checkPreviously.setChecked(false);
            // ???????????????
            automarkCheckLayout.setVisibility(View.VISIBLE);
            this.previouslyLayout.setVisibility(View.GONE);
            appModel.setGpsTest(true);
            appModel.setPreviouslyTest(false);

            String[] automarkNotCommonTypes = mActivity.getResources().getStringArray(R.array.glonavin_type_ditie);
            final ArrayAdapter<String> notCommonAdapter = new ArrayAdapter<String>(mActivity, R.layout.simple_spinner_custom_layout,
                    automarkNotCommonTypes);
            notCommonAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            this.mGlonavinSpinner.setAdapter(notCommonAdapter);
            this.mGlonavinSpinner.setOnItemSelectedListener(new MyGlonavintypeListener());
            initGlonavinSpinner();
            if (ConfigRoutine.getInstance().isHsExternalGPS(mActivity)&&autoMarkCheckBox.isChecked()) {
                mGlonavinLayout.setVisibility(View.VISIBLE);
                if (!"".equals(GService.blueToothAddress)) {
                    mEtConnectedModule.setText(GService.blueToothAddress);
                }
            }
        }

    }

    private void initGlonavinSpinner(){
//        if (appModel.isGlonavinTest()){
            if (mGlonavinSpinner.getAdapter().getCount()-1<=appModel.getGlonavinType()){
                mGlonavinSpinner.setSelection(appModel.getGlonavinType());
            }
//        }
    }
    /**
     * ????????????????????????
     *
     * @author jianchao.wang
     */
    private class MyAutoMarkTypeListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                //????????????????????????
                cqtTestLayout.setVisibility(View.VISIBLE);
                mInnsmapLayout.setVisibility(View.GONE);
                mGlonavinLayout.setVisibility(View.GONE);
                mAutoMarkSceneLayout.setVisibility(View.GONE);
                appModel.setInnsmapTest(false);
                appModel.setGlonavinTest(false);
            } else if (position == 1) {
                //????????????????????????
                cqtTestLayout.setVisibility(View.GONE);
                mInnsmapLayout.setVisibility(View.VISIBLE);
                mGlonavinLayout.setVisibility(View.GONE);
                mAutoMarkSceneLayout.setVisibility(View.GONE);
                appModel.setInnsmapTest(true);
                appModel.setGlonavinTest(false);
                updateInnsmap();
            } else if (position == 2) {
                //??????????????????????????????????????????????????????L1,L2???
                mGlonavinLayout.setVisibility(View.VISIBLE);
                cqtTestLayout.setVisibility(View.VISIBLE);
                mInnsmapLayout.setVisibility(View.GONE);
                mAutoMarkSceneLayout.setVisibility(View.VISIBLE);
                appModel.setInnsmapTest(false);
                appModel.setGlonavinTest(true);
                appModel.setGlonavinType(0);
            }else if (position==3){
                //??????????????????????????????????????????????????????L3????????????
                mGlonavinLayout.setVisibility(View.VISIBLE);
                cqtTestLayout.setVisibility(View.VISIBLE);
                mInnsmapLayout.setVisibility(View.GONE);
                mAutoMarkSceneLayout.setVisibility(View.VISIBLE);
                appModel.setInnsmapTest(false);
                appModel.setGlonavinTest(true);
                appModel.setGlonavinType(1);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    /**
     * ????????????????????????
     */
    private class MyAutoMarkSceneListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                //??????
                mCurrentFloorLayout.setVisibility(View.GONE);
                mFirstFloorHeightLayout.setVisibility(View.GONE);
                mFloorHeightLayout.setVisibility(View.GONE);
                AutoMarkConstant.markScene = MarkScene.COMMON;
            } else if (position == 1) {
                //??????
                mCurrentFloorLayout.setVisibility(View.VISIBLE);
                mFirstFloorHeightLayout.setVisibility(View.VISIBLE);
                mFloorHeightLayout.setVisibility(View.VISIBLE);
                AutoMarkConstant.markScene = MarkScene.STAIRS;
            } else if (position == 2) {
                //??????
                mCurrentFloorLayout.setVisibility(View.VISIBLE);
                mFirstFloorHeightLayout.setVisibility(View.VISIBLE);
                mFloorHeightLayout.setVisibility(View.VISIBLE);
                AutoMarkConstant.markScene = MarkScene.LIFT;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mCurrentFloorLayout.setVisibility(View.GONE);
            mFirstFloorHeightLayout.setVisibility(View.GONE);
            mFloorHeightLayout.setVisibility(View.GONE);
        }
    }

    /**
     * ????????????????????????
     */
    private class MyGlonavintypeListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String text= (String)((TextView)view).getText();
            if ("????????????".equals(text)||"????????????".equals(text)) {
                appModel.setGlonavinTest(true);
                appModel.setGlonavinType(0);
            }else if ("L3".equals(text)) {
                //L3
                appModel.setGlonavinTest(true);
                appModel.setGlonavinType(1);
            }else {
                appModel.setGlonavinTest(true);
                appModel.setGlonavinType(0);

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    /**
     * ?????????????????????
     *
     * @author jianchao.wang
     */
    private class MyBuilderListener implements OnItemSelectedListener {
        private List<BuildingModel> buildingList;

        public MyBuilderListener(List<BuildingModel> buildingList) {
            this.buildingList = buildingList;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // ??????????????????Spinner?????????
            floorList = new ArrayList<FloorModel>();
            final ArrayList<String> floorNames = new ArrayList<String>();
            if (buildingList.size() > 0 && position > 0) {
                btnCamera.setEnabled(true);
                floorList.addAll(buildingList.get(position - 1).getFloors());
                appModel.setBuildModel(buildingList.get(position - 1));
                appModel.setBuildNodeId(
                        SysBuildingManager.getInstance(mActivity).getNodeId(buildingList.get(position - 1).getName(), "0"));
            } else {
                btnCamera.setEnabled(false);
                floorImageTip.setText(R.string.main_buildnotfound);
                imageOutView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.safe_img));
                floorList.clear();
            }
            for (int i = 0; i < floorList.size(); i++) {
                floorNames.add(floorList.get(i).getName());
            }
            if (floorList.size() == 0) {
                floorNames.add(mActivity.getString(R.string.none));
                isIndoorTest = false;
                if (!buildingList.isEmpty()) {
                    floorImageTip.setText(R.string.main_floornotfound);
                }
                floorImage.setVisibility(View.VISIBLE);
                floorImage.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.safe_img));
            }
            final ArrayAdapter<String> adptFloor = new ArrayAdapter<String>(mActivity, R.layout.simple_spinner_custom_layout,
                    floorNames);
            adptFloor.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerFloor.setAdapter(adptFloor);
            spinnerFloor.setOnItemSelectedListener(new MyFloorListener(floorList));

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    /**
     * ?????????????????? ???????????????????????? ????????????????????????
     *
     * @author Administrator
     */
    private class MyFloorListener implements OnItemSelectedListener {
        private List<FloorModel> floorList;

        public MyFloorListener(List<FloorModel> floorList) {
            this.floorList = floorList;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if (floorList.size() > 0) {
                btnCamera.setEnabled(true);
                floorModel = floorList.get(position);
                appModel.setFloorNodeId(
                        SysBuildingManager.getInstance(mActivity).getNodeId(floorModel.getName(), appModel.getBuildNodeId()));
                gallery.setVisibility(View.VISIBLE);
                List<String> mappaths = floorModel.getAllMapPaths();
                List<String> outsideMaps = floorModel.getOutViewMapPaths();
                // ??????????????????
                if (!outsideMaps.isEmpty()) {
                    imgTip.setVisibility(View.GONE);
                    imgTip.setText("");
                    showImage(outsideMaps.get(0));
                } else {// ?????????????????????
                    imgTip.setVisibility(View.VISIBLE);
                    imageOutView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.safe_img));
                    imgTip.setText(R.string.main_indoor_buildNomap);
                }
                 imageAdapter = new ImageAdapter(mActivity, mappaths);
                gallery.setAdapter(imageAdapter);
                gallery.setOnItemSelectedListener(new GallerySelectListener());
                if (!imageAdapter.isEmpty()) {
                    isIndoorTest = true; // ??????????????????????????????????????????
                    floorImageTip.setText("");
                    floorImage.setVisibility(View.GONE);
                } else {
                    isIndoorTest = false;
                    floorImageTip.setText(R.string.main_notfound);
                    floorImage.setVisibility(View.VISIBLE);
                    floorImage.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.safe_img));
                }

            } else {
                btnCamera.setEnabled(false);
                gallery.setVisibility(View.GONE);
                imgTip.setVisibility(View.VISIBLE);
                imageOutView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.safe_img));
                imgTip.setText(R.string.main_indoor_buildNomap);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    ;

    /**
     * ???????????????
     *
     * @author jianchao.wang
     */
    private class GallerySelectListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            itemGallaryPosition = position;
            if (position>=imageAdapter.getCount()){
                return;
            }
            /*???????????????????????????????????????????????????????????????
             * */
            imgPath= (String) imageAdapter.getItem(position);
            if(!TextUtils.isEmpty(imgPath)) {
                //??????????????????????????????????????????SharedPreference
                SharePreferencesUtil sharePreferencesUtil = SharePreferencesUtil.getInstance(mActivity);
                sharePreferencesUtil.saveString(MapView.SP_INDOOR_MAP_PATH, imgPath);
                float plottingScale = sharePreferencesUtil.getFloat(imgPath, 1);
                MapFactory.getMapData().setPlottingScale(plottingScale);
                mEditScale.setText(""+plottingScale);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    /**
     * ?????????????????????
     *
     * @param imgPath ????????????
     */
    private void showImage(String imgPath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, opts);
        opts.inSampleSize = UtilsMethod.computeSuitedSampleSize(opts, 250 * 250);
        opts.inJustDecodeBounds = false;
        File file = new File(imgPath);

        if (file.exists() && file.isFile()) {
            Bitmap bmp = BitmapFactory.decodeFile(imgPath, opts);
            imageOutView.setImageBitmap(bmp);
        }
    }

    /**
     * ???????????????
     *
     * @author jianchao.wang
     */
    public class ImageAdapter extends BaseAdapter {
        // private int mGalleryItemBackground;
        private Context mContext;
        private List<String> floorMaps;

        public ImageAdapter(Context context, List<String> floorMaps) {
            mContext = context;
            this.floorMaps = floorMaps;
            TypedArray a = context.obtainStyledAttributes(R.styleable.GalleryTheme);
            // mGalleryItemBackground = a.getResourceId(
            // R.styleable.GalleryTheme_android_galleryItemBackground, 0);
            a.recycle();
        }

        @Override
        public int getCount() {
            return floorMaps.size();
        }

        @Override
        public Object getItem(int position) {
            return floorMaps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.gallery_item, null);
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            // TypedArray typedArray =
            // obtainStyledAttributes(R.styleable.Gallery);
            TextView tv = (TextView) view.findViewById(R.id.imageName);
            String mappath = floorMaps.get(position);
            floorImage.setBackgroundColor(Color.alpha(1));
            // imageView.setBackgroundResource(R.styleable.GalleryTheme_android_galleryItemBackground);
            File file = new File(mappath);
            Bitmap bmp = null;
            if (file.isFile()) {
                tv.setText(file.getName());
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mappath, opts);
                opts.inSampleSize = UtilsMethod.computeSuitedSampleSize(opts, 480 * 800);
                opts.inJustDecodeBounds = false;
                bmp = BitmapFactory.decodeFile(mappath, opts);
            }
            imageView.setImageBitmap(bmp);

            return view;
        }
    }

    /**
     * ??????GPS?????????
     */
    public void clearGpstimer() {
        // LogUtil.w(tag, "---clear timer");
        if (gpsTask != null) {
            gpsTask.cancel();
            gpsTask = null;
        }
    }

    /**
     * ??????GPS??????
     */
    private void clearGpsStatus() {
        LogUtil.w(TAG, "clear All status");
        if (gpsInfo.isJobTestGpsOpen()) {
            gpsInfo.releaseGps(mActivity, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
            clearGpstimer();
        }
    }

    /**
     * ????????????????????????
     */
    private void setTestFloorModel() {
        if (isIndoorTest) {
            File file = null;
            String path = "";
            // ????????????????????????,?????????????????????ibwave??????
            if (!StringUtil.isNullOrEmpty(floorModel.tabfilePath)) {
                file = new File(floorModel.tabfilePath);
                path = floorModel.tabfilePath;
            } else {
                file = new File(floorModel.getAllMapPaths().get(itemGallaryPosition));
                path = floorModel.getAllMapPaths().get(itemGallaryPosition);
            }
            if (file.exists()) {
                floorModel.setTestMapPath(path);
                appModel.setFloorModel(floorModel);
                LogUtil.w(TAG, "----path=" + path);
            }
        } else if ((floorList == null || floorList.size() == 0) && spinnerBuild.getSelectedItemId() > 0) {
            if (appModel.getFloorModel() != null) {
                appModel.getFloorModel().setTestMapPath("");
                LogUtil.w(TAG, "----path=");
            }
            MapFactory.getMapData().setMap(null);
        }
    }


    public void doStart(DialogInterface dialog) {
        Field field;
        try {
            field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            if (mSceneType == SceneType.HighSpeedRail) {// ????????????
                autoMarkCheckBox.setChecked(true);//????????????????????????
                if (mTestNoText.getText().length() <= 0) {
                    field.set(dialog, false);
                    ToastUtil.showToastShort(mActivity, R.string.metro_select_route);
                    return;
                }
                if (ConfigRoutine.getInstance().isHsExternalGPS(mActivity)) {
                    if (mActivity.getString(R.string.work_order_fj_select_action).equals(mTestNoText.getText().toString())) {
                        field.set(dialog, false);
                        ToastUtil.showToastShort(mActivity, R.string.metro_select_route);
                        return;
                    } else {
                        field.set(dialog, true);
                    }
                    if (!"".equals(mEtConnectedModule.getText().toString()) && mEtConnectedModule.getText().toString() != null) {
                        GService.blueToothAddress = mEtConnectedModule.getText().toString().trim();
                        field.set(dialog, true);
                    } else {
                        field.set(dialog, false);
                        ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_select_module));
                        return;
                    }
                }
            } else if (mSceneType == SceneType.Metro) {// ????????????
                if (mTestCityTxt.getText().length() <= 0) {
                    field.set(dialog, false);
                    ToastUtil.showToastShort(mActivity, R.string.metro_select_city);
                    return;
                } else if (mActivity.getString(R.string.work_order_fj_select_action).equals(mTestWayTxt.getText().toString())) {
                    field.set(dialog, false);
                    ToastUtil.showToastShort(mActivity, R.string.metro_select_route);
                    return;
                } else {
                    field.set(dialog, true);
                }
                if (ConfigRoutine.getInstance().isHsExternalGPS(mActivity)) {
                    if (!"".equals(mEtConnectedModule.getText().toString()) && mEtConnectedModule.getText().toString() != null) {
                        GService.blueToothAddress = mEtConnectedModule.getText().toString().trim();
                        field.set(dialog, true);
                    } else {
                        field.set(dialog, false);
                        ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_select_module));
                        return;
                    }
                }
            } else {
                field.set(dialog, true);
            }
            // ???????????????????????????????????????0?????????1
            boolean check = editLooptimes.getText().toString().equals("")
                    || editLooptimes.getText().toString().equals("0");
            appModel.setOutLooptimes(check ? 1 : Integer.parseInt(editLooptimes.getText().toString()));
            appModel.setOutLoopDisconnetNetwork(loopTimePPPDisable.isChecked());
            tester = editTester.getText().toString().trim();
            testAddress = editAddress.getText().toString().trim();
            String deviceTag = editDeviceTag.getText().toString().trim();
            if (deviceTag.length() > 0 && !Verify.isValidFileName(deviceTag)) {
                Toast.makeText(mActivity, mActivity.getString(R.string.monitor_inputPosition), Toast.LENGTH_LONG)
                        .show();
                return;
            }
            if (mSceneType == SceneType.Metro) {
                isIndoorTest = false;
                GpsInfo.getInstance().setJobTestGpsOpen(true);
            }
            //??????????????????????????????????????????
            if (appModel.isInnsmapTest()) {
                if (mInnsmapFactory.getCurrentFloor(false) == null) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.innsmap_no_select_floor), Toast.LENGTH_LONG)
                            .show();
                    //set(dialog,false)???????????????????????????dialog??????
                    field.set(dialog, false);
                    return;
                } else {
                    field.set(dialog, true);
                }
            }
            //???????????????????????????????????????????????????
            if (autoMarkCheckBox.isChecked() && appModel.isGlonavinTest()) {
                if (TextUtils.isEmpty(mEtConnectedModule.getText().toString().trim())) {
                    ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_select_module));
                    //set(dialog,false)???????????????????????????dialog??????
                    field.set(dialog, false);
                    return;
                }
                int currentFloor = 0;
                double firstFloorHeight = 0;
                double floorHeight = 0;
                if (AutoMarkConstant.markScene != MarkScene.COMMON) {//??????????????????????????????????????????????????????????????????
                    if (TextUtils.isEmpty(mCurrentFloorEt.getText().toString().trim())) {
                        ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_select_current_floor));
                        //set(dialog,false)???????????????????????????dialog??????
                        field.set(dialog, false);
                        return;
                    }
                    if (TextUtils.isEmpty(mFirstFloorHeightEt.getText().toString().trim())) {
                        ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_select_first_floor_height));
                        //set(dialog,false)???????????????????????????dialog??????
                        field.set(dialog, false);
                        return;
                    }
                    if (TextUtils.isEmpty(mFloorHeightEt.getText().toString().trim())) {
                        ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_select_floor_height));
                        //set(dialog,false)???????????????????????????dialog??????
                        field.set(dialog, false);
                        return;
                    }
                    currentFloor = Integer.parseInt(mCurrentFloorEt.getText().toString());
                    firstFloorHeight = Double.parseDouble(mFirstFloorHeightEt.getText().toString());
                    floorHeight = Double.parseDouble(mFloorHeightEt.getText().toString());

                    if (currentFloor > 255) {
                        ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_rule_current_floor));
                        field.set(dialog, false);
                        return;
                    }
                    if (firstFloorHeight > 25.5) {
                        ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_rule_first_floor_height));
                        field.set(dialog, false);
                        return;
                    }
                    if (floorHeight > 25.5) {
                        ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_rule_floor_height));
                        field.set(dialog, false);
                        return;
                    }
                    AutoMarkConstant.currentFloor = currentFloor;
                    AutoMarkConstant.firstFloorHeight = firstFloorHeight;
                    AutoMarkConstant.FloorHeight = floorHeight;
                }
            }
            /**
             * 2019/4/25
             * @anthur jinfeng.xie
             * ?????????????????????
             */
            if(!TextUtils.isEmpty(imgPath)) {
                //??????????????????????????????????????????SharedPreference
                SharePreferencesUtil sharePreferencesUtil = SharePreferencesUtil.getInstance(mActivity);
                float plottingScale=Float.valueOf(mEditScale.getText().toString());
                if (plottingScale==0){
                    ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.toast_scale_no_zero));
                    field.set(dialog, false);
                    return;
                }
                MapFactory.getMapData().setPlottingScale(plottingScale);
                sharePreferencesUtil.saveFloat(imgPath, plottingScale);
            }
            if (appModel.isPioneer()) {//???pioneer??????????????????????????????
                DataSetFileUtil.getInstance().sendCloseRcuFileAddToDB(mActivity.getApplicationContext());
                appModel.setPioneer(false);

            }
            // ????????????????????????
            appModel.setIndoorTest(isIndoorTest);
            LogUtil.i(TAG, "---isIndoorTest?" + isIndoorTest);
            setTestFloorModel();
            GpsInfo.getInstance().setGpsLastChangeTime(0);
            if (Deviceinfo.getInstance().isATModel()) {
                ToastUtil.showToastShort(mActivity, R.string.at_command_info);
                return;
            }
            startTest(isIndoorTest, netsnifferCheckBox.isChecked(), dontSaveDataCBox.isChecked(),
                    autoMarkCheckBox.isChecked());
            clearGpstimer();
            dialog.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        MapFactory.getMapData().setIndoorTestFirstPoint(true);
    }

    private void doClickPositiveBtn(DialogInterface dialog) {
        if (TaskListDispose.getInstance().isCurrentTaskNeedAssitPermission()
                && !OttUtil.hasServicePermission(mActivity, WalktourAutoService.class)) {
            new BasicDialog.Builder(mActivity)
                    .setTitle(mActivity.getString(R.string.str_tip))
                    .setMessage(mActivity.getString(R.string.open_auto_test_pemission_dialog_tips))
                    .setNegativeButton(R.string.control_cancel, new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dg, int which)
                        {
                            dg.dismiss();
                        }
                    }).setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    try {
                        if (OttUtil.isRoot()) {
                            OttUtil.openServicePermissonRoot(mActivity, WalktourAutoService.class);
                        }
                        if (!OttUtil.hasServicePermission(mActivity, WalktourAutoService.class)) {
                            OttUtil.openServicePermission(mActivity, WalktourAutoService.class);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!OttUtil.hasServicePermission(mActivity, WalktourAutoService.class)) {
                        ToastUtil.showToastLong(mActivity, mActivity.getString(R.string
                                .open_auto_test_permission_toast));
                        OttUtil.jumpSystemSetting(mActivity);
                    } else {
                        ToastUtil.showToastLong(mActivity, mActivity.getString(R.string
                                .auto_service_open_success));
                        doStart(dialog);
                    }
                }
            }).show();
        }else if (TaskListDispose.getInstance().isXiaoMi8VideoTest()
                && !OttUtil.hasServicePermission(mActivity, XiaoMi8CustomService.class)){
            new BasicDialog.Builder(mActivity)
                    .setTitle(mActivity.getString(R.string.str_tip))
                    .setMessage(mActivity.getString(R.string.open_xiaomi_video_test_pemission_dialog_tips))
                    .setNegativeButton(R.string.control_cancel, new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dg, int which)
                        {
                            dg.dismiss();
                        }
                    }).setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    try {
                        if (OttUtil.isRoot()) {
                            OttUtil.openServicePermissonRoot(mActivity, XiaoMi8CustomService.class);
                        }
                        if (!OttUtil.hasServicePermission(mActivity, XiaoMi8CustomService.class)) {
                            OttUtil.openServicePermission(mActivity, XiaoMi8CustomService.class);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!OttUtil.hasServicePermission(mActivity, XiaoMi8CustomService.class)) {
                        ToastUtil.showToastLong(mActivity, mActivity.getString(R.string
                                .open_auto_test_permission_toast));
                        OttUtil.jumpSystemSetting(mActivity);
                    } else {
                        ToastUtil.showToastLong(mActivity, mActivity.getString(R.string
                                .auto_service_open_success));
                        doStart(dialog);
                    }
                }
            }).show();
        }else {
            doStart(dialog);
        }
    }

    /**
     * ???????????????
     */
    public void show() {
        DisplayMetrics metric = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        ApplicationModel.getInstance().setGlonavinTest(false);
        BasicDialog dialog = new BasicDialog.Builder(mActivity).setTitle(R.string.main_start)
                .setView(dialogView, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (320 * metric.density)))
                /* .setView( dialogView ) */
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       doClickPositiveBtn(dialog);
                    }
                }).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
                    private Field field;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                            field.setAccessible(true);// ???????????????????????????
                            field.set(dialog, true);
                            // ?????????????????????
                            clearGpsStatus();
                            dialog.dismiss();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                })/*
         * .setOnKeyListener(new OnKeyListener() {
         *
         * @Override public boolean onKey(DialogInterface dialog, int keyCode,
         * KeyEvent event) { if(keyCode == KeyEvent.KEYCODE_BACK){
         * dialog.dismiss(); //????????????????????????????????? clearGpsStatus(); } return false; }
         * })
         */
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        LogUtil.w(TAG, "--diag show dismiss--");
                        diagDismissClean();
                    }
                }).create();
        dialog.show();

    }// end method show

    /**
     * ???????????????????????????????????????????????????
     */
    private void diagDismissClean() {
        mActivity.unregisterReceiver(testJobDoneReceiver);
        AlertWakeLock.release();
    }

    /***
     * ????????????
     *
     * @param isIndoorTest ?????????????????????
     * @param isNetsniffer
     *          ????????????
     * @param dontSaveData
     *          ??????????????????
     * @param isAutoMark
     *          ??????????????????
     */
    private void startTest(boolean isIndoorTest, boolean isNetsniffer, boolean dontSaveData, boolean isAutoMark) {
        try {
            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if (autoMarkCheckBox.isChecked() && appModel.isGlonavinTest() && AutoMarkConstant.markScene != MarkScene.COMMON) {
                MapFactory.getMapData().getGlonavinPointStack().clear();
            } else {
                AutoMarkConstant.markScene = MarkScene.COMMON;
            }
            // ????????????????????????????????????????????????????????????????????????????????????
            List<ShareFileModel> listFile = ShareDataBase.getInstance(mActivity).fetchAllFilesByFileStatusAndFileType(-1,
                    new int[]{ShareFileModel.FILE_STATUS_ONGOING, ShareFileModel.FILE_STATUS_WAITING});
            for (ShareFileModel sf : listFile) {
                if (sf.getSendOrReceive() == ShareFileModel.SEND_OR_RECEIVE_SEND) {
                    UploadManager um = UpDownService.getUploadManager();
                    um.stopUpload(sf);
                } else {
                    DownloadManager dm = UpDownService.getDownloadManager();
                    dm.stopDownload(sf);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ??????GPS???????????????????????????????????????????????????????????????????????????
        if (this.mSceneType != SceneType.Metro && !checkDT.isChecked()) {
            // ???????????????????????????
            appModel.setGerenalTest(true);
            // ????????????GPS??????
            appModel.setGpsTest(false);
        } else {
            appModel.setGerenalTest(false);
        }
        LogUtil.w(TAG,
                "----checkPreviously.isChecked=" + checkPreviously.isChecked() + "--gerenalTest:" + appModel.isGerenalTest());
        // 2012.04.19????????????????????????
        appModel.setNeedToCleanMap(!checkPreviously.isChecked());
        // ?????????????????????????????????????????????????????????????????????
        if (checkPreviously.isChecked()) {
            MapFactory.getMapData().getEventQueue().clear();
            // MapFactory.getMapData().getPointStatusStack().clear();
            for (int i = MapFactory.getMapData().getPointStatusStack().size() - 1; i >= 0; i--) {
                int status = MapFactory.getMapData().getPointStatusStack().get(i).getStatus();
                if (status == PointStatus.POINT_STATUS_EFFECTIVE || status == PointStatus.POINT_STATUS_PREVIOUS_EFFECTIVE) {
                    MapFactory.getMapData().getPointStatusStack().remove(i);
                }
            }
            MapFactory.getMapData().getQueueStack().clear();
            TraceInfoInterface.traceData.getGpsLocas().clear();
        } else {
            MapFactory.getMapData().getEventQueue().clear();
            MapFactory.getMapData().getPointStatusStack().clear();
            MapFactory.getMapData().getQueueStack().clear();
            TraceInfoInterface.traceData.getGpsLocas().clear();
        }
        if (checkDT.isChecked()) {
            appModel.setNeedToCleanMap(true);
            // 2012.04.19????????????????????????
            // appModel.setNeedToCleanMap(false);
        }
        if (appModel.isGyroTest()) {
            // ???????????????????????????????????????????????????
            // RecordTraceService.setAdjust(false);
            MapFactory.getMapData().getPointStatusStack().clear();
            appModel.setNeedToCleanMap(false);
        }
        MapFactory.getMapData().setZoomGrade(10);
        MapFactory.getMapData().setScale(1);
        MapFactory.getMapData().setSampleSize(1);
        MapFactory.getMapData().setAutoMark(isAutoMark);
        MapFactory.setLoadIndoor(isIndoorTest);
        if (isIndoorTest) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.OtherMap;
        }
        if (appModel.isInnsmapTest()) {
            if (createInnsmapTest()) {
                appModel.setIndoorTest(true);
                isIndoorTest = true;
            }
        }
        // MapFactory.getMapData().flag_toggle = false;
        // ????????????????????????????????????
        // ApplicationModel.getInstance().setNeedToCleanMap( true );

        Intent startTestIntent = new Intent(WalkMessage.ACTION_WALKTOUR_START_TEST);
        startTestIntent.setPackage(this.mActivity.getPackageName());
        LogUtil.w(TAG, "----looptimes=" + appModel.getOutLooptimes());
        //????????????????????????????????????
        String outLoopIntervalStr = mEtOutloopInterval.getText().toString().trim();
        if(!TextUtils.isEmpty(outLoopIntervalStr)){
            int outloopInterval = Integer.parseInt(outLoopIntervalStr);
            if(outloopInterval > 0){
                appModel.setOutLoopInterval(outloopInterval);
            }
        }
        //?????????????????????????????????????????????
        String differentTaskIntervalStr = mEtDifferentTaskInterval.getText().toString().trim();
        if(!TextUtils.isEmpty(differentTaskIntervalStr)){
            int differentTaskInterval = Integer.parseInt(differentTaskIntervalStr);
            if(differentTaskInterval > 0){
                startTestIntent.putExtra(WalkMessage.DIFFERENT_TASK_INTERVAL, differentTaskInterval);
            }
        }

        startTestIntent.putExtra(WalkMessage.Outlooptimes, appModel.getOutLooptimes());
        startTestIntent.putExtra(WalkMessage.OutloopInterval, appModel.getOutLoopInterval());
        startTestIntent.putExtra(WalkMessage.OutloopDisconnetNetwork, appModel.isOutLoopDisconnetNetwork());
        startTestIntent.putExtra(WalkMessage.RcuFileLimitType, ConfigRoutine.getInstance().getSplitType());
        startTestIntent.putExtra(WalkMessage.RucFileSizeLimit, ConfigRoutine.getInstance().getFileSize());
        startTestIntent.putExtra(WalkMessage.ISNETSNIFFER, isNetsniffer);
        startTestIntent.putExtra(WalkMessage.ISCQTAUTOMARK, isAutoMark);
        startTestIntent.putExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE, mEtConnectedModule.getText().toString().trim());
        startTestIntent.putExtra(WalkMessage.KEY_TEST_DONTSAVEDATA, dontSaveData);
        startTestIntent.putExtra(WalkMessage.KEY_TESTER, tester);
        startTestIntent.putExtra(WalkMessage.KEY_TEST_ADDRESS, testAddress);
        // ???????????????????????????
        if (mSceneType == SceneType.HighSpeedRail) {// ????????????
            startTestIntent.putExtra(WalkMessage.KEY_TEST_HIGHT_SPEED_RAIL, mTestWayTxt.getText().toString() + "");
            SysBuildingManager.getInstance(mActivity).addBuilding(mActivity, mTestWayTxt.getText().toString() + "");
        } else if (mSceneType == SceneType.Metro) {// ????????????
            MetroCity city = this.mMetroFactory.getCurrentCity(false);
            MetroRoute route = this.mMetroFactory.getCurrentRoute(false);
            NewMapFactory.getInstance().setMapType(NewMapFactory.MAP_TYPE_NONE);
            startTestIntent.putExtra(WalkMessage.KEY_TEST_CITY, city.getName());
            startTestIntent.putExtra(WalkMessage.KEY_TEST_METRO, route.getRouteFilterDesc());
            SysBuildingManager.getInstance(mActivity).addBuilding(mActivity, route.getRouteFilterDesc() + "");
        } else if (mSceneType == SceneType.SingleSite) {//????????????
            LogUtil.d(TAG, "---SceneType???" + mSceneType.name() + "---stationName:" + this.mSingleStationName);
            startTestIntent.putExtra(WalkMessage.KEY_TEST_SINGLE_STATION, this.mSingleStationName);
            SysBuildingManager.getInstance(mActivity).addBuilding(mActivity, this.mSingleStationName);
        }
        // ??????????????????????????????(????????????????????????)
        /*
         * String building = ""; String floor = ""; if (isIndoortest) { if
         * (floorModel != null) { building = floorModel.getBuildingName(); floor =
         * floorModel.getName(); } }
         */

        startTestIntent.putExtra(WalkMessage.KEY_TEST_INDOOR, isIndoorTest);
        startTestIntent.putExtra(WalkMessage.KEY_TEST_CQT_CHECK, checkCQT.isChecked());
        startTestIntent.putExtra(WalkMessage.KEY_TEST_BUILDING, appModel.getBuildNodeId());// building
        startTestIntent.putExtra(WalkMessage.KEY_TEST_FLOOR, appModel.getFloorNodeId());// floor
        startTestIntent.putExtra(WalkMessage.KEY_TEST_TAG, getTestTagStr());
        startTestIntent.putExtra(WalkMessage.KEY_FROM_TYPE, this.mFromType);
        startTestIntent.putExtra(WalkMessage.KEY_FROM_SCENE, mSceneType.getSceneTypeId());

        mActivity.sendBroadcast(startTestIntent);

        new WaitStartTypeThread().start();
        // ????????????
        TraceInfoInterface.traceData.setTestStartInfo();
        // ????????????????????????:Bundle???????????????????????????
        Intent intent = new Intent(mActivity, NewInfoTabActivity.class);
        if (TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Map
                || TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Default) {
            intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_MAP);
        } else {
            intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_OTHER);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.open_next, R.anim.close_main);

        appModel.setRuningScene(mSceneType);
        /**
         * 2018/8/23
         * @anthur jinfeng.xie
         * FIXME ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         */
        TotalDataByGSM.getInstance().initTotalDetail();


    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    private boolean createInnsmapTest() {
        InnsmapModel buildingModel = this.mInnsmapFactory.getCurrentBuilding();
        String buildingName = buildingModel.getName();
        BuildingModel building = this.configIndoor.getBuilding(mActivity, buildingName);
        if (building == null) {
            if (this.configIndoor.addBuilding(mActivity, buildingName)) {
                this.mSysBuildingManager.addBuilding(mActivity, buildingName);
                building = this.configIndoor.getBuilding(mActivity, buildingName);
            }
        }
        if (building != null) {
            appModel.setBuildModel(building);
            appModel.setBuildNodeId(this.mSysBuildingManager.getNodeId(buildingName, "0"));
            InnsmapModel floorModel = this.mInnsmapFactory.getCurrentFloor(false);
            String floorName = floorModel.getName() + "_" + floorModel.getId();
            this.floorModel = this.configIndoor.getFloor(mActivity, new File(building.getDirPath()), floorName);
            if (this.floorModel == null) {
                if (this.configIndoor.addFloor(new File(building.getDirPath()), floorName)) {
                    this.mSysBuildingManager.addFloor(buildingName, floorName);
                    this.floorModel = this.configIndoor.getFloor(mActivity, new File(building.getDirPath()), floorName);
                }
            }
            if (this.floorModel != null) {
                appModel.setFloorModel(this.floorModel);
                appModel.setFloorNodeId(this.mSysBuildingManager.getNodeId(floorModel.getName(), appModel.getBuildNodeId()));
                return true;
            }
        }
        return false;
    }

    /**
     * ?????????????????????????????????
     */
    private class WaitStartTypeThread extends Thread {
        public void run() {
            while (!appModel.isTestJobIsRun()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mHandler != null) {
                Message msg = mHandler.obtainMessage(WalkTour.REFLESH_VIEW);
                mHandler.sendMessage(msg);
            }
        }
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    private String getTestTagStr() {
        String tagStr = "";
        tagStr = editDeviceTag.getText().toString().trim();

        if (!tagStr.equals("")) {
            tagStr = String.format("%s-", tagStr);
        }
        if (spinnerTestPorNum != null && spinnerTestPorNum.getSelectedItemPosition() > 0) {
            tagStr = String.format("%s%s-", tagStr, spinnerTestPorNum.getSelectedItem().toString());
        }

        appModel.addExtendInfo("operator",new RecordTestInfo("operator", MyPhoneState.getInstance().getNetworkOperateName(WalktourApplication.getAppContext())));

        if (!editTester.getText().toString().trim().equals("")) {
            appModel.addExtendInfo(RecordInfoKey.tester.name(),
                    new RecordTestInfo(RecordInfoKey.tester.name(), editTester.getText().toString().trim()));
        }

        if (!editAddress.getText().toString().trim().equals("")) {
            appModel.addExtendInfo(RecordInfoKey.city.name(),
                    new RecordTestInfo(RecordInfoKey.city.name(), editAddress.getText().toString().trim()));
        }
        // ??????????????????????????????
        if (mSceneType == SceneType.HighSpeedRail) {// ??????????????????
            if (mTestWayTxt.getText().length() > 0) {// ??????????????????
                appModel.addExtendInfo(RecordInfoKey.high_speed_rail.name(),
                        new RecordTestInfo(RecordInfoKey.high_speed_rail.name(), mTestWayTxt.getText().toString().trim()));
            }
        } else if (mSceneType == SceneType.Metro) {// ??????????????????
            if (mTestWayTxt.getText().length() > 0) {// ??????????????????
                appModel.addExtendInfo(RecordInfoKey.metro_line.name(), new RecordTestInfo(RecordInfoKey.metro_line.name(),
                        this.mMetroFactory.getCurrentRoute(false).getRouteFilterDesc()));
            }
            if (mTestCityTxt.getText().length() > 0) {// ??????????????????
                appModel.addExtendInfo(RecordInfoKey.city.name(),
                        new RecordTestInfo(RecordInfoKey.city.name(), this.mMetroFactory.getCurrentCity(false).getName()));
            }

        } else if (mSceneType == SceneType.SingleSite) {//????????????
            if (!StringUtil.isNullOrEmpty(this.mSingleStationName)) {
                appModel.addExtendInfo(RecordInfoKey.single_station.name(), new RecordTestInfo(RecordInfoKey.single_station.name(), this.mSingleStationName));
            }
        }

        if (isCreateCUFile) {

            if ((EditText) dialogView.findViewById(R.id.EditTestScope) != null) {
                appModel.addExtendInfo(RecordInfoKey.cu_Scope.name(), new RecordTestInfo(RecordInfoKey.cu_Scope.name(),
                        ((EditText) dialogView.findViewById(R.id.EditTestScope)).getText().toString().trim()));
            }
            if ((EditText) dialogView.findViewById(R.id.EditTestCompany) != null) {
                appModel.addExtendInfo(RecordInfoKey.cu_Company.name(), new RecordTestInfo(RecordInfoKey.cu_Company.name(),
                        ((EditText) dialogView.findViewById(R.id.EditTestCompany)).getText().toString().trim()));
            }
            if ((Spinner) dialogView.findViewById(R.id.SpinnerNetwork) != null) {
                appModel.addExtendInfo(RecordInfoKey.cu_Network.name(), new RecordTestInfo(RecordInfoKey.cu_Network.name(),
                        ((Spinner) dialogView.findViewById(R.id.SpinnerNetwork)).getSelectedItem().toString()));
            }
            if ((EditText) dialogView.findViewById(R.id.EditTestMobilePhone) != null) {
                appModel.addExtendInfo(RecordInfoKey.cu_PhoneNum.name(), new RecordTestInfo(RecordInfoKey.cu_PhoneNum.name(),
                        ((EditText) dialogView.findViewById(R.id.EditTestMobilePhone)).getText().toString().trim()));
            }
            if ((EditText) dialogView.findViewById(R.id.EditTestExtendInfo) != null) {
                appModel.addExtendInfo(RecordInfoKey.extendsInfo.name(), new RecordTestInfo(RecordInfoKey.extendsInfo.name(),
                        ((EditText) dialogView.findViewById(R.id.EditTestExtendInfo)).getText().toString().trim()));
            }

        }
        LogUtil.w(TAG, "--getTestTagStr:" + tagStr);

        return tagStr;
    }

    /**
     * ???????????????
     */
    private OnClickListener mClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnCamera:
                    // sd???????????????
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(mActivity, R.string.sdcard_unmount, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    doTakePhoto();
                    break;
                case R.id.test_city_layout:
                    selectTestCity();
                    break;
                case R.id.test_way_layout:
                    selectTestRoute();
                    break;
                case R.id.innsmap_city_layout:
                    selectInnsmapCity();
                    break;
                case R.id.innsmap_building_layout:
                    selectInnsmapBuilding();
                    break;
                case R.id.innsmap_floor_layout:
                    selectInnsmapFloor();
                    break;
                case R.id.test_no_layout:
                    selectTestNo();
                    break;

            }
        }

    };

    /**
     * @date on
     * @describe ????????????
     * @author jinfeng.xie
     */
    private void selectTestNo() {
//        if (mActivity.getString(R.string.work_order_fj_select_action).equals(mTestWayTxt.getText().toString())) {
//            ToastUtil.showToastShort(mActivity, R.string.metro_select_route);
//            return;
//        }
        Intent intent = new Intent(mActivity, HsSelectNoActivity.class);
        mActivity.startActivityForResult(intent, requestHsCode);
        mActivity.overridePendingTransition(R.anim.open_next, R.anim.close_main);
    }

    /**
     * ????????????????????????
     */
    private void selectInnsmapFloor() {
        if (this.mInnsmapFactory.getCurrentCity() == null) {
            ToastUtil.showToastShort(mActivity, R.string.metro_no_select_city);
            return;
        }
        if (this.mInnsmapFactory.getCurrentBuilding() == null) {
            ToastUtil.showToastShort(mActivity, R.string.metro_no_select_city);
            return;
        }
        Intent intent = new Intent(mActivity, InnsmapSelectActivity.class);
        intent.putExtra("typeId", Type.Floor.getId());
        mActivity.startActivityForResult(intent, requestInnsmapCode);
        mActivity.overridePendingTransition(R.anim.open_next, R.anim.close_main);
    }

    /**
     * ???????????????????????????
     */
    private void selectInnsmapBuilding() {
        if (this.mInnsmapFactory.getCurrentCity() == null) {
            ToastUtil.showToastShort(mActivity, R.string.metro_no_select_city);
            return;
        }
        Intent intent = new Intent(mActivity, InnsmapSelectActivity.class);
        intent.putExtra("typeId", Type.Building.getId());
        mActivity.startActivityForResult(intent, requestInnsmapCode);
        mActivity.overridePendingTransition(R.anim.open_next, R.anim.close_main);
    }

    /**
     * ????????????????????????
     */
    private void selectInnsmapCity() {
        Intent intent = new Intent(mActivity, InnsmapSelectActivity.class);
        intent.putExtra("typeId", Type.City.getId());
        mActivity.startActivityForResult(intent, requestInnsmapCode);
        mActivity.overridePendingTransition(R.anim.open_next, R.anim.close_main);
    }

    /**
     * ????????????intent?????????????????????
     */
    private void doTakePhoto() {
        Intent intent = new Intent(mActivity, CaptureImg.class);
        mActivity.startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    /**
     * ?????????????????????
     *
     * @param latestfile
     */
    public void showPhoto(File latestfile) {
        imgTip.setVisibility(View.GONE);
        imgTip.setText("");
        showImage(latestfile.getAbsolutePath());
        if (floorModel != null) {
            FileOperater operater = new FileOperater();
            operater.overCopy(latestfile.getAbsolutePath(), String.format("%s/camera/%s%s_%s.jpg", floorModel.getDirPath(),
                    mActivity.getString(R.string.str_lczp), floorModel.getBuildingName(), floorModel.getName()));
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param show
     */
    private void indoorShow(int show) {
        // ????????????????????????????????????,???????????????CU??????,????????????????????????
        if (show == View.VISIBLE && isCreateCUFile) {
            return;
        }
        if (show == View.VISIBLE && this.mSceneType == SceneType.Metro)
            cqtTestLayout.setVisibility(View.GONE);
        else
            cqtTestLayout.setVisibility(show);
        floorOutviewLayout.setVisibility(show);
    }

    /**
     * ??????CQT??????
     *
     * @param isChecked ????????????
     */
    private void checkCQTTest(boolean isChecked) {
        this.previouslyLayout.setVisibility(View.VISIBLE);
        automarkCheckLayout
                .setVisibility(isChecked && ConfigRoutine.getInstance().autoMarkPoint() ? View.VISIBLE : View.GONE);
        // layoutIndoor.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        // floorOutviewlayout.setVisibility(isChecked ? View.VISIBLE :
        // View.GONE);
        indoorShow(isChecked ? View.VISIBLE : View.GONE);
        if (!isChecked) {
            isIndoorTest = false;
            checkDT.setChecked(isChecked);
        } else {
            checkDT.setChecked(false);
            checkCQT.setChecked(isChecked);
            gpsLayout.setVisibility(View.GONE);
        }
    }

    /**
     * ??????DT??????
     *
     * @param isChecked
     */
    private void checkDTTest(boolean isChecked) {
        if (isChecked) {
            checkCQT.setChecked(false);
            checkDT.setChecked(isChecked);
            checkPreviously.setChecked(false);
            // ???????????????
            automarkCheckLayout.setVisibility(View.GONE);
            this.previouslyLayout.setVisibility(View.GONE);
            appModel.setGpsTest(true);
            appModel.setPreviouslyTest(false);
            setPositon(defaultLonLatStr, defaultLonLatStr);
            // ??????GPS??????
            if (appModel.getSelectScene() == SceneType.HighSpeedRail&&ConfigRoutine.getInstance().isHsExternalGPS(mActivity)) {
                gpsInfo.releaseGps(mActivity, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
            }else {
                gpsInfo.openGps(mActivity, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
            }
            // GPS????????????
            if (!appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
                gpsLayout.setVisibility(View.VISIBLE);
            }
            // ??????????????????
            gpsTip.setText(R.string.main_indoor_waitgps);
        } else {
            LogUtil.w(TAG, "**********set gps to falses");
            checkCQT.setChecked(false);
            appModel.setGpsTest(false);
            this.previouslyLayout.setVisibility(View.VISIBLE);
            // ??????GPS??????
            gpsInfo.releaseGps(mActivity, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
            if (gpsInfo.getLocation() == null) {
                gpsTip.setText(R.string.main_indoor_gpsnosearch);
            }
            // ??????GPS?????????
            clearGpstimer();
            // ??????????????????GPS????????????GPS???????????????
            if (!appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
                gpsLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * ???????????????
     */
    private OnCheckedChangeListener mCheckListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.CheckCQT:
                    checkCQTTest(isChecked);
                    break;
                case R.id.CheckDT:
                    checkDTTest(isChecked);
                    break;
                case R.id.checkPreviously:
                    appModel.setPreviouslyTest(isChecked);
                    LogUtil.w(TAG, "---is exist vitual point=" + isChecked);
                    checkCQT.setEnabled(!isChecked);
                    break;
                // case R.id.checkGyro:
                // appModel.setGyroTest(isChecked);
                // break;
                case R.id.metro_start_automark:
                    mMetroMessageLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    mGlonavinLayout.setVisibility(isChecked?View.VISIBLE:View.GONE);
                    break;
                case R.id.start_automark:
                    if (isChecked) {
                        if (mSceneType == SceneType.Manual /*&& appModel.hasInnsmapTest()*/)
                            mIndoorTestAutoMarkTypeLayout.setVisibility(View.VISIBLE);
                    } else {
                        mIndoorTestAutoMarkTypeLayout.setVisibility(View.GONE);
                    }
                    cqtTestLayout.setVisibility(View.VISIBLE);
                    mInnsmapLayout.setVisibility(View.GONE);
                    mGlonavinLayout.setVisibility(View.GONE);
                    appModel.setInnsmapTest(false);
                    break;
                case R.id.start_volte_qciinfo:
                    ConfigRoutine.getInstance().setVoLTEQCIInfo(mActivity, isChecked);
                    break;
                case R.id.check_sms:
                    ConfigRoutine.getInstance().setSMSInfo(mActivity, isChecked);
                    break;
            }
        }
    };
}
