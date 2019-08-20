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
 * 开始或停止执行任务编辑对话框
 *
 * @author jianchao.wang
 */
@SuppressLint("InflateParams")
@SuppressWarnings("deprecation")
public class StartDialog {
    /**
     * 日志标识
     */
    private static final String TAG = "StartDialog";
    /**
     * 请求高铁测试路线代码
     */
    public static final int requestRailRouteCode = 10101;
    /**
     * 请求地铁测试路线代码
     */
    public static final int requestMetroRouteCode = 10102;
    /**
     * 请求测试城市代码
     */
    public static final int requestCityCode = 10103;
    /**
     * 请求寅时参数代码
     */
    public static final int requestInnsmapCode = 10104;
    /**
     * 请求高铁班次代码
     */
    public static final int requestHsCode = 10105;
    /**
     * 请求拍照
     */
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String[] DEV_NAMES = new String[]{"FootSensor","HC-06","Dingli-Walk"};
    /**
     * DT测试勾选框
     */
    private RadioButton checkDT;
    /**
     * CQT测试勾选框
     */
    private RadioButton checkCQT;
    /**
     * 数据命名编辑框
     */
    private EditText editDeviceTag;
    /**
     * 是否启用预打点轨迹选择
     */
    private RelativeLayout previouslyLayout;
    /**
     * 预打点轨迹勾选框
     */
    private CheckBox checkPreviously;
    /**
     * GPS提示信息
     */
    private TextView gpsTip = null;
    /**
     * 经度显示
     */
    private TextView editLongitude = null;
    /**
     * 纬度显示
     */
    private TextView editLatitude = null;
    /**
     * 拍摄照片显示
     */
    private ImageView imageOutView = null;
    /**
     * 测试楼层对象
     */
    private FloorModel floorModel = null;
    /**
     * 室内测试建筑物管理类
     */
    private SysBuildingManager mSysBuildingManager;
    /**
     * 拍摄照片说明
     */
    private TextView imgTip = null;
    /**
     * 测试人员
     */
    private String tester = "";
    /**
     * 测试地址
     */
    private String testAddress = "";
    /**
     * 项目编号下拉选择框
     */
    private Spinner spinnerTestPorNum;
    /**
     * 所属界面
     */
    private Activity mActivity;
    /**
     * 对话框视图
     */
    private View dialogView;
    /**
     * CQT测试属性
     */
    private LinearLayout cqtTestLayout;
    // /** 测试地址属性行*/
    // private RelativeLayout addressLayout;
    /**
     * DT或者CQT勾选行
     */
    private RelativeLayout dtOrCqtCheckLayout;
    /**
     * 测试城市属性行
     */
    private LinearLayout mTestCityLayout;
    /**
     * 测试线路属性行
     */
    private LinearLayout mTestWayLayout;
    /**
     * 室内专项测试建筑物属性行
     */
    private RelativeLayout mBuildingTestLayout;
    /**
     * CQT测试建筑物属性行
     */
    private RelativeLayout mCQTTestBuildingLayout;
    /**
     * GPS信息
     */
    private LinearLayout gpsLayout;
    /**
     * 拍摄照片属性行
     */
    private LinearLayout floorOutviewLayout;
    /**
     * CU格式信息
     */
    private LinearLayout mCULayout;
    /**
     * 地铁项目提示信息
     */
    private LinearLayout mMetroMessageLayout;
    /**
     * 测试城市
     */
    private TextView mTestCityTxt;
    /**
     * 测试线路
     */
    private TextView mTestWayTxt;
    /**
     * 室内专项测试建筑物编辑框
     */
    private TextView textBuild;
    /**
     * 测试楼层下拉框
     */
    private Spinner spinnerFloor;
    /**
     * 测试建筑物下拉框
     */
    private Spinner spinnerBuild;
    /**
     * 外循环次数编辑框
     */
    private EditText editLooptimes;

    /**
     * 外循环间隔时长编辑框
     */
    private EditText mEtOutloopInterval;

    /**
     * 一次循环中不同业务之间的间隔时长编辑框
     */
    private EditText mEtDifferentTaskInterval;

    /**
     * 测试人员编辑框
     */
    private EditText editTester;
    /**
     * 测试地址编辑框
     */
    private EditText editAddress;
    /**
     * 外循环断开拨号属性行
     */
    private RelativeLayout looptimePPPDisableLayout;
    /**
     * 外循环次数属性行
     */
    private RelativeLayout mLoopTimeslayout;

    /**
     * 外循环间隔时长属性行
     */
    private RelativeLayout mOutloopIntervallayout;
    /**
     * 外循环断开拨号勾选框
     */
    private CheckBox loopTimePPPDisable;
    /**
     * 拍摄照片按钮
     */
    private Button btnCamera;
    /**
     * 楼层图片显示
     */
    private ImageView floorImage;
    /**
     * 楼层图片说明
     */
    private TextView floorImageTip;
    /**
     * 楼层图片滑动显示
     */
    private Gallery gallery;
    /**
     * 室内设置
     */
    private ConfigIndoor configIndoor;
    /**
     * 是否有效的室内测试
     */
    private boolean isIndoorTest = false;
    /**
     * 楼层图片显示位置
     */
    private int itemGallaryPosition = 0;
    /**
     * 是否抓包属性行
     */
    private RelativeLayout netsnifferCheckLayout;
    /**
     * 自动打点属性行
     */
    private RelativeLayout automarkCheckLayout;
    /**
     * 不保存文件属性行
     */
    private RelativeLayout dontSaveFilesLayout;
    /**
     * 测试项目编号属性行
     */
    private RelativeLayout testPorNumLayout;
    /**
     * 应用对象
     */
    private ApplicationModel appModel = ApplicationModel.getInstance();
    /**
     * 开启抓包勾选框
     */
    private CheckBox netsnifferCheckBox;
    /**
     * 采集VoLTE QCI数据
     */
    private CheckBox voteQCIInfoCheckBox;
    /**
     * 开启自动打点勾选框
     */
    private CheckBox autoMarkCheckBox;
    /**
     * 不保存文件勾选框
     */
    private CheckBox dontSaveDataCBox;
    /**
     * 默认经纬度显示字符串
     */
    private final String defaultLonLatStr = "N/A";
    /**
     * 建筑物选择序号
     */
    private static int itemPostionBuilding;
    /**
     * gps工具类
     */
    private GpsInfo gpsInfo;
    /**
     * 楼层列表
     */
    private List<FloorModel> floorList = null;
    /**
     * gps信息获取定时器
     */
    private TimerTask gpsTask = null;
    /**
     * 消息执行句柄
     */
    private Handler mHandler;
    /**
     * 是否进行抓包
     */
    private boolean isNetSniffer = false;
    /**
     * 是否生成CU文件,根据当前设置决定是否显示CU相关属性
     */
    private boolean isCreateCUFile = false;
    /**
     * 测试任务来源
     */
    private int mFromType;
    /**
     * 任务场景ID
     */
    private SceneType mSceneType = SceneType.Manual;
    /**
     * 地铁测试项目工厂类
     */
    private MetroFactory mMetroFactory;
    /**
     * 寅时室内测试工厂类
     */
    private InnsmapFactory mInnsmapFactory;
    /**
     * 寅时室内测试选项
     */
    private LinearLayout mInnsmapLayout;
    /**
     * CQT测试自动打点模式（鼎利或寅时）勾选行
     */
    private RelativeLayout mIndoorTestAutoMarkTypeLayout;
    /**
     * CQT测试自动打点模式下拉框
     */
    private Spinner mAutoMarkTypeSpinner;
    /**
     * 场景选择
     */
    private RelativeLayout mAutoMarkSceneLayout;
    /**
     * 场景选择下拉框
     */
    private BasicSpinner mAutoMarkSceneSpinner;
    /**
     * 格纳微选择
     */
    private RelativeLayout mGlonavinScetedLayout;
    /**
     * 格纳微选择下拉框
     */
    private BasicSpinner mGlonavinSpinner;
    /**
     * 寅时测试城市选择行
     */
    private LinearLayout mInnsmapCityLayout;
    /**
     * 寅时测试建筑物选择行
     */
    private LinearLayout mInnsmapBuildingLayout;
    /**
     * 寅时测试楼层选择行
     */
    private LinearLayout mInnsmapFloorLayout;
    /**
     * 寅时测试城市显示
     */
    private TextView mInnsmapCityText;
    /**
     * 寅时测试建筑物显示
     */
    private TextView mInnsmapBuildingText;
    /**
     * 寅时测试楼层显示
     */
    private TextView mInnsmapFloorText;
    /**
     * 选择班次
     */
    private LinearLayout mTestNoLayout;
    /**
     * 选择班次
     */
    private TextView mTestNoText;


    /**
     * 单站验证测试基站名称
     */
    private String mSingleStationName;

    /**
     * 格纳微室内自动打点测试选项
     */
    private LinearLayout mGlonavinLayout;

    /***
     * 是否短信查询流量
     */
    private CheckBox checkSMS;
    private EditText mEtConnectedModule;


    /**
     * 等待进度框
     */
    private ProgressDialog mProgressDialog;
    /**
     * 蓝牙设配器
     */
    private BluetoothAdapter mBluetoothAdapter;
    /**
     * 蓝牙管理器
     */
    BluetoothManager mBluetoothManager;
    /**
     * 搜索到的蓝牙模块列表
     */
    private ArrayList<BluetoothDevice> mFoundModules;
    /**
     * 选择连接的格纳微模块
     */
    private String mSelectedModuleAddress = null;
    /**
     * 选择格纳微模块对话框的mShowing反射变量
     */
    private Field mGlonavinDialogField;
    private RelativeLayout mCurrentFloorLayout;
    private RelativeLayout mFirstFloorHeightLayout;
    private RelativeLayout mFloorHeightLayout;
    /**
     * 当前楼层
     */
    private EditText mCurrentFloorEt;
    /**
     * 首楼层高度
     */
    private EditText mFirstFloorHeightEt;
    /**
     * 楼层高度
     */
    private EditText mFloorHeightEt;
    /*室内比例尺
    * */
    private EditText mEditScale;
    /*室内图片
    * */
    private ImageAdapter imageAdapter;
    /*室内图片选中的路径
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
    boolean isPreciseSearch =false;//精准搜索，已经搜到含有指定
    /**
     * 蓝牙模块扫描回调接口
     * 1.搜索设备，如果是含有指定名字的，则为精准搜索，出现在列表
     * 2.不含有精准搜索时，只要是getName为NUll时，都会出现在列表
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//            final BleAdvertisedData badata = BleUtil.parseAdertisedData(scanRecord);
            foundModules(device);
        }
    };

    /**
     * 视图设置
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
        // GPS信息显示，默认隐藏
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

            mCULayout.setVisibility(View.VISIBLE); // 显示CU格式其它属性
            ((TextView) dialogView.findViewById(R.id.TextviewAddress)).setText(R.string.main_city); // 测试地点改名城市名称
            ((RelativeLayout) dialogView.findViewById(R.id.RL_DeviceTag)).setVisibility(View.GONE); // 隐藏设备标识信息
            indoorShow(View.GONE); // 隐藏楼层关系层
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
                    if("高铁专用".equals(glonavinType)||"地铁专用".equals(glonavinType)){
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
                        //10秒后关闭扫描蓝牙
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
        /******* 判断是否存在预打点，如果不存在，那么预打点勾选框选中，并且能使用，如果不存在，那么不选中并且禁用 ************/
        // 屏蔽预打点
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
        // 设置经纬度默认值
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
        // 室内测试的开关
        appModel.setIndoorTest(false);

        // 是否打开室内测试
        checkCQT.setOnCheckedChangeListener(mCheckListener);
        // Gps开关
        checkDT.setOnCheckedChangeListener(mCheckListener);

        // Gps开关
        checkPreviously.setOnCheckedChangeListener(mCheckListener);
        // checkPreviously.setChecked(false);
        mTestCityLayout.setOnClickListener(mClickListener);
        mTestWayLayout.setOnClickListener(mClickListener);
        // 若GPS已经打开
        checkDT.setChecked(gpsInfo.isJobTestGpsOpen());

        init();

        // 注册接收测试完成消息
        IntentFilter broadCaseIntent = new IntentFilter();
        broadCaseIntent.addAction(GpsInfo.gpsLocationChanged);
        broadCaseIntent.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        broadCaseIntent.addAction(BluetoothDevice.ACTION_FOUND);
        broadCaseIntent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mActivity.registerReceiver(testJobDoneReceiver, broadCaseIntent);
    }// end constructor

    /**
     * @date on
     * @describe 蓝牙扫描成功
     * @author jinfeng.xie
     */
    private void BlueToothScanFisher() {
        mProgressDialog.dismiss();
        if (null != mFoundModules && !mFoundModules.isEmpty()) {
            BasicDialog.Builder builder = new BasicDialog.Builder(mActivity);
            /*
            * 数组去重*/
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
                        mGlonavinDialogField.setAccessible(true);// 设置该属性可以访问
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
                        mGlonavinDialogField.setAccessible(true);// 设置该属性可以访问
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
                        mGlonavinDialogField.setAccessible(true);// 设置该属性可以访问
                        mGlonavinDialogField.set(dialog, true);
                        mEtConnectedModule.setText(mSelectedModuleAddress);
                        //地铁获取外置陀螺仪服务
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
            //未搜索到设备
            ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_no_module));
        }
    }

    /**
     * 选择测试路线
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
     * 选择测试城市
     */
    protected void selectTestCity() {
        Intent intent = new Intent(mActivity, MetroSettingCityActivity.class);
        intent.putExtra("is_select", true);
        mActivity.startActivityForResult(intent, requestCityCode);
        mActivity.overridePendingTransition(R.anim.open_next, R.anim.close_main);
    }

    /**
     * 更新寅时测试设置
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
     * 更新地铁测试的城市和线路名称
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
     * 更新测试线路
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
     * 默认开启DT测试或者CQT测试
     *
     * @param isCheckDT 是否开启DT测试，否则开启CQT测试
     */
    public void checkDTOrCQT(boolean isCheckDT) {
        if (isCheckDT)
            checkDT.setChecked(true);
        else
            checkCQT.setChecked(true);
    }

    /*
     * 初始化城市列表信息 private void initialCity() { final List<String> provinceList =
     * ConfigCityInfo.getInstance(this.context).getProvinceList();
     *
     * final ArrayAdapter<String> adptProv = new ArrayAdapter<String>(context,
     * R.layout.simple_spinner_custom_layout, provinceList);
     * adptProv.setDropDownViewResource(R.layout.spinner_dropdown_item);
     *
     * spinnerPrivoic.setAdapter(adptProv); // 此时为业务测试，在建筑选择监听中再监听楼层选择
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
     * 接收测试完成消息
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
        /*此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串*/
        for (int i=0;i<strs.length;i++){
            if (strs[i].toLowerCase().equals(s.toLowerCase())){
                return true;
            }
        }
        return false;
    }
    /**
     * 设置经度和纬度
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
     * 初始化参数
     */
    private void init() {
        boolean isAHworkorder = appModel.isAnHuiTest();
        // 设定 建筑物和楼层的下拉框
        final List<BuildingModel> buildingList = configIndoor.getBuildings(mActivity, isAHworkorder);
        // 如果包含室内专项权限
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
                    // 加入判断，避免删除建筑时导致崩溃
                    itemPostionBuilding = buildingList.size() > itemPostionBuilding ? itemPostionBuilding : 0;
                    textBuild.setText(buildingList.get(itemPostionBuilding).getName());
                    // 此时为室内专项测试，没有建筑监听，只有监听楼层选择
                    spinnerFloor.setOnItemSelectedListener(new MyFloorListener(floorList));
                } else {
                    textBuild.setText(R.string.none);
                }
            }
        } else {// 如果不包含室内专项权限
            // 设定 建筑物和楼层的下拉框
            List<String> buildingNames = new ArrayList<String>();
            for (int i = 0; i < buildingList.size(); i++) {
                buildingNames.add(buildingList.get(i).getName());
            }
            String[] names = new String[buildingNames.size() + 1];
            names[0] = mActivity.getString(R.string.none); // 增加无
            int i = 1;
            for (String name : buildingNames) {
                names[i++] = name;
            }
            buildingNames = Arrays.asList(names);
            final ArrayAdapter<String> adptBuild = new ArrayAdapter<String>(mActivity, R.layout.simple_spinner_custom_layout,
                    buildingNames);
            adptBuild.setDropDownViewResource(R.layout.spinner_dropdown_item);

            spinnerBuild.setAdapter(adptBuild);
            // 此时为业务测试，在建筑选择监听中再监听楼层选择
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
        if (mSceneType == SceneType.HighSpeedRail) {// 高铁测试权限
            // 屏蔽选项
            dtOrCqtCheckLayout.setVisibility(View.GONE);
            mTestWayLayout.setVisibility(View.VISIBLE);

            HighSpeedNoModel hsNo = SharePreferencesUtil.getInstance(mActivity).getObjectFromShare(WalktourConst.CURRENT_HS_NO, HighSpeedNoModel.class);
            if (hsNo != null && hsNo.routes != null) {
                mTestNoText.setText("" + hsNo.getRoutes().getRouteSelectDesc());
            }
            checkCQT.setChecked(false);
            checkDT.setChecked(true);
            checkPreviously.setChecked(false);
            // 屏蔽预打点
            automarkCheckLayout.setVisibility(View.GONE);
            this.previouslyLayout.setVisibility(View.GONE);
            appModel.setGpsTest(true);
            appModel.setPreviouslyTest(false);
            setPositon(defaultLonLatStr, defaultLonLatStr);
            // 因为高铁需要校准，高铁需要打开GPS服务
            gpsInfo.openGps(mActivity, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
            // GPS信息可见
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



            // 提示正在定位
            gpsTip.setText(R.string.main_indoor_waitgps);
            updateRailWay();

            String[] automarkNotCommonTypes = mActivity.getResources().getStringArray(R.array.glonavin_type_gaotie);
            final ArrayAdapter<String> notCommonAdapter = new ArrayAdapter<String>(mActivity, R.layout.simple_spinner_custom_layout,
                    automarkNotCommonTypes);
            notCommonAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            this.mGlonavinSpinner.setAdapter(notCommonAdapter);
            this.mGlonavinSpinner.setOnItemSelectedListener(new MyGlonavintypeListener());
            initGlonavinSpinner();
        } else if (mSceneType == SceneType.Metro) {// 地铁测试权限
            // 屏蔽选项
            dtOrCqtCheckLayout.setVisibility(View.GONE);
            mTestCityLayout.setVisibility(View.VISIBLE);
            mTestWayLayout.setVisibility(View.VISIBLE);
            mMetroMessageLayout.setVisibility(View.VISIBLE);
            checkCQT.setChecked(false);
            checkDT.setChecked(false);
            checkPreviously.setChecked(false);
            // 屏蔽预打点
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
     * 自动打点类型选择
     *
     * @author jianchao.wang
     */
    private class MyAutoMarkTypeListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                //鼎利自动打点模式
                cqtTestLayout.setVisibility(View.VISIBLE);
                mInnsmapLayout.setVisibility(View.GONE);
                mGlonavinLayout.setVisibility(View.GONE);
                mAutoMarkSceneLayout.setVisibility(View.GONE);
                appModel.setInnsmapTest(false);
                appModel.setGlonavinTest(false);
            } else if (position == 1) {
                //寅时自动打点模式
                cqtTestLayout.setVisibility(View.GONE);
                mInnsmapLayout.setVisibility(View.VISIBLE);
                mGlonavinLayout.setVisibility(View.GONE);
                mAutoMarkSceneLayout.setVisibility(View.GONE);
                appModel.setInnsmapTest(true);
                appModel.setGlonavinTest(false);
                updateInnsmap();
            } else if (position == 2) {
                //蓝牙自动打点模式（目前使用格纳微方案L1,L2）
                mGlonavinLayout.setVisibility(View.VISIBLE);
                cqtTestLayout.setVisibility(View.VISIBLE);
                mInnsmapLayout.setVisibility(View.GONE);
                mAutoMarkSceneLayout.setVisibility(View.VISIBLE);
                appModel.setInnsmapTest(false);
                appModel.setGlonavinTest(true);
                appModel.setGlonavinType(0);
            }else if (position==3){
                //蓝牙自动打点模式（目前使用格纳微方案L3三个一）
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
     * 自动打点类型选择
     */
    private class MyAutoMarkSceneListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                //普通
                mCurrentFloorLayout.setVisibility(View.GONE);
                mFirstFloorHeightLayout.setVisibility(View.GONE);
                mFloorHeightLayout.setVisibility(View.GONE);
                AutoMarkConstant.markScene = MarkScene.COMMON;
            } else if (position == 1) {
                //楼梯
                mCurrentFloorLayout.setVisibility(View.VISIBLE);
                mFirstFloorHeightLayout.setVisibility(View.VISIBLE);
                mFloorHeightLayout.setVisibility(View.VISIBLE);
                AutoMarkConstant.markScene = MarkScene.STAIRS;
            } else if (position == 2) {
                //电梯
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
     * 自动打点类型选择
     */
    private class MyGlonavintypeListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String text= (String)((TextView)view).getText();
            if ("高铁专用".equals(text)||"地铁专用".equals(text)) {
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
     * 监听建筑物选择
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
            // 重新生成楼层Spinner下拉项
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
     * 监听楼层选择 监听室内测试楼层 监听业务测试楼层
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
                // 楼层有外观图
                if (!outsideMaps.isEmpty()) {
                    imgTip.setVisibility(View.GONE);
                    imgTip.setText("");
                    showImage(outsideMaps.get(0));
                } else {// 楼层没有外观图
                    imgTip.setVisibility(View.VISIBLE);
                    imageOutView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.safe_img));
                    imgTip.setText(R.string.main_indoor_buildNomap);
                }
                 imageAdapter = new ImageAdapter(mActivity, mappaths);
                gallery.setAdapter(imageAdapter);
                gallery.setOnItemSelectedListener(new GallerySelectListener());
                if (!imageAdapter.isEmpty()) {
                    isIndoorTest = true; // 当楼层地图存在时设为室内测试
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
     * 选择监听类
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
            /*选择地图的时候，也有将之前设置的比列放上去
             * */
            imgPath= (String) imageAdapter.getItem(position);
            if(!TextUtils.isEmpty(imgPath)) {
                //将当前加载的室内图路径保存到SharedPreference
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
     * 显示楼层外观图
     *
     * @param imgPath 图片路径
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
     * 图片适配器
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
     * 清除GPS计时器
     */
    public void clearGpstimer() {
        // LogUtil.w(tag, "---clear timer");
        if (gpsTask != null) {
            gpsTask.cancel();
            gpsTask = null;
        }
    }

    /**
     * 清除GPS状态
     */
    private void clearGpsStatus() {
        LogUtil.w(TAG, "clear All status");
        if (gpsInfo.isJobTestGpsOpen()) {
            gpsInfo.releaseGps(mActivity, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
            clearGpstimer();
        }
    }

    /**
     * 设置测试楼层对象
     */
    private void setTestFloorModel() {
        if (isIndoorTest) {
            File file = null;
            String path = "";
            // 要加载的室内地图,首先判断是否是ibwave地图
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
            if (mSceneType == SceneType.HighSpeedRail) {// 高铁测试
                autoMarkCheckBox.setChecked(true);//高铁都是自动测试
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
            } else if (mSceneType == SceneType.Metro) {// 地铁测试
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
            // 如果外循环输入为空或者输入0，转为1
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
            //寅时自动打点进行楼层选择校验
            if (appModel.isInnsmapTest()) {
                if (mInnsmapFactory.getCurrentFloor(false) == null) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.innsmap_no_select_floor), Toast.LENGTH_LONG)
                            .show();
                    //set(dialog,false)欺骗系统达到不关闭dialog目的
                    field.set(dialog, false);
                    return;
                } else {
                    field.set(dialog, true);
                }
            }
            //格纳微自动打点进行定位模块选择校验
            if (autoMarkCheckBox.isChecked() && appModel.isGlonavinTest()) {
                if (TextUtils.isEmpty(mEtConnectedModule.getText().toString().trim())) {
                    ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_select_module));
                    //set(dialog,false)欺骗系统达到不关闭dialog目的
                    field.set(dialog, false);
                    return;
                }
                int currentFloor = 0;
                double firstFloorHeight = 0;
                double floorHeight = 0;
                if (AutoMarkConstant.markScene != MarkScene.COMMON) {//如果为电梯或者楼梯，则需要设置楼层，首层高等
                    if (TextUtils.isEmpty(mCurrentFloorEt.getText().toString().trim())) {
                        ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_select_current_floor));
                        //set(dialog,false)欺骗系统达到不关闭dialog目的
                        field.set(dialog, false);
                        return;
                    }
                    if (TextUtils.isEmpty(mFirstFloorHeightEt.getText().toString().trim())) {
                        ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_select_first_floor_height));
                        //set(dialog,false)欺骗系统达到不关闭dialog目的
                        field.set(dialog, false);
                        return;
                    }
                    if (TextUtils.isEmpty(mFloorHeightEt.getText().toString().trim())) {
                        ToastUtil.showToastShort(mActivity, mActivity.getString(R.string.glonavin_auto_mark_select_floor_height));
                        //set(dialog,false)欺骗系统达到不关闭dialog目的
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
             * 地图的缩放比列
             */
            if(!TextUtils.isEmpty(imgPath)) {
                //将当前加载的室内图路径保存到SharedPreference
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
            if (appModel.isPioneer()) {//与pioneer测试，又开始手动测试
                DataSetFileUtil.getInstance().sendCloseRcuFileAddToDB(mActivity.getApplicationContext());
                appModel.setPioneer(false);

            }
            // 设置是否室内测试
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
     * 显示对话框
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
                            field.setAccessible(true);// 设置该属性可以访问
                            field.set(dialog, true);
                            // 取消时清除状态
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
         * dialog.dismiss(); //点击返回按钮时清除状态 clearGpsStatus(); } return false; }
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
     * 开测对话框消失时，一些保持状态清除
     */
    private void diagDismissClean() {
        mActivity.unregisterReceiver(testJobDoneReceiver);
        AlertWakeLock.release();
    }

    /***
     * 开始测试
     *
     * @param isIndoorTest 是否是室内测试
     * @param isNetsniffer
     *          是否抓包
     * @param dontSaveData
     *          是否保存数据
     * @param isAutoMark
     *          是否自动打点
     */
    private void startTest(boolean isIndoorTest, boolean isNetsniffer, boolean dontSaveData, boolean isAutoMark) {
        try {
            //格纳微自动打点进行定位模块选择校验，如果为电梯或者楼梯，则需要设置楼层，首层高等
            if (autoMarkCheckBox.isChecked() && appModel.isGlonavinTest() && AutoMarkConstant.markScene != MarkScene.COMMON) {
                MapFactory.getMapData().getGlonavinPointStack().clear();
            } else {
                AutoMarkConstant.markScene = MarkScene.COMMON;
            }
            // 开始测试前自动关闭微服务中的所有上传，下载及等待中的文件
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

        // 如果GPS开关和室内开关都未选中，并且不是有效的室内专项测试
        if (this.mSceneType != SceneType.Metro && !checkDT.isChecked()) {
            // 初始设置为一般测试
            appModel.setGerenalTest(true);
            // 设置为非GPS测试
            appModel.setGpsTest(false);
        } else {
            appModel.setGerenalTest(false);
        }
        LogUtil.w(TAG,
                "----checkPreviously.isChecked=" + checkPreviously.isChecked() + "--gerenalTest:" + appModel.isGerenalTest());
        // 2012.04.19修改，不清除地图
        appModel.setNeedToCleanMap(!checkPreviously.isChecked());
        // 此逻辑解决预打点测试时，没有清空原有打点及轨迹
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
            // 2012.04.19修改，不清除地图
            // appModel.setNeedToCleanMap(false);
        }
        if (appModel.isGyroTest()) {
            // 每次测试开始时都先设置为未校准状态
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
        // 开始测试时清除地图的标志
        // ApplicationModel.getInstance().setNeedToCleanMap( true );

        Intent startTestIntent = new Intent(WalkMessage.ACTION_WALKTOUR_START_TEST);
        startTestIntent.setPackage(this.mActivity.getPackageName());
        LogUtil.w(TAG, "----looptimes=" + appModel.getOutLooptimes());
        //获取输入的外循环间隔时长
        String outLoopIntervalStr = mEtOutloopInterval.getText().toString().trim();
        if(!TextUtils.isEmpty(outLoopIntervalStr)){
            int outloopInterval = Integer.parseInt(outLoopIntervalStr);
            if(outloopInterval > 0){
                appModel.setOutLoopInterval(outloopInterval);
            }
        }
        //获取一次循环不同业务间间隔时长
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
        // 测试选择的高铁线路
        if (mSceneType == SceneType.HighSpeedRail) {// 高铁测试
            startTestIntent.putExtra(WalkMessage.KEY_TEST_HIGHT_SPEED_RAIL, mTestWayTxt.getText().toString() + "");
            SysBuildingManager.getInstance(mActivity).addBuilding(mActivity, mTestWayTxt.getText().toString() + "");
        } else if (mSceneType == SceneType.Metro) {// 地铁测试
            MetroCity city = this.mMetroFactory.getCurrentCity(false);
            MetroRoute route = this.mMetroFactory.getCurrentRoute(false);
            NewMapFactory.getInstance().setMapType(NewMapFactory.MAP_TYPE_NONE);
            startTestIntent.putExtra(WalkMessage.KEY_TEST_CITY, city.getName());
            startTestIntent.putExtra(WalkMessage.KEY_TEST_METRO, route.getRouteFilterDesc());
            SysBuildingManager.getInstance(mActivity).addBuilding(mActivity, route.getRouteFilterDesc() + "");
        } else if (mSceneType == SceneType.SingleSite) {//单站验证
            LogUtil.d(TAG, "---SceneType：" + mSceneType.name() + "---stationName:" + this.mSingleStationName);
            startTestIntent.putExtra(WalkMessage.KEY_TEST_SINGLE_STATION, this.mSingleStationName);
            SysBuildingManager.getInstance(mActivity).addBuilding(mActivity, this.mSingleStationName);
        }
        // 如果是有效的室内测试(有正确的楼层地图)
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
        // 重置时间
        TraceInfoInterface.traceData.setTestStartInfo();
        // 转到信息查看页面:Bundle内容为地图文件路径
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
         * FIXME 原本分割之后，统计次数要清空。现在已经修复不清空，但需要开始测试的时候重新清空
         */
        TotalDataByGSM.getInstance().initTotalDetail();


    }

    /**
     * 生成寅时室内测试的相关联的建筑物和楼层
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
     * 刷新主界面图标状态线程
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
     * 获取测试信息描述
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
        // 根据场景写入实际的值
        if (mSceneType == SceneType.HighSpeedRail) {// 高铁测试场景
            if (mTestWayTxt.getText().length() > 0) {// 高铁测试线路
                appModel.addExtendInfo(RecordInfoKey.high_speed_rail.name(),
                        new RecordTestInfo(RecordInfoKey.high_speed_rail.name(), mTestWayTxt.getText().toString().trim()));
            }
        } else if (mSceneType == SceneType.Metro) {// 地铁测试场景
            if (mTestWayTxt.getText().length() > 0) {// 地铁测试线路
                appModel.addExtendInfo(RecordInfoKey.metro_line.name(), new RecordTestInfo(RecordInfoKey.metro_line.name(),
                        this.mMetroFactory.getCurrentRoute(false).getRouteFilterDesc()));
            }
            if (mTestCityTxt.getText().length() > 0) {// 地铁测试线路
                appModel.addExtendInfo(RecordInfoKey.city.name(),
                        new RecordTestInfo(RecordInfoKey.city.name(), this.mMetroFactory.getCurrentCity(false).getName()));
            }

        } else if (mSceneType == SceneType.SingleSite) {//单站验证
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
     * 点击监听类
     */
    private OnClickListener mClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnCamera:
                    // sd卡没有挂载
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
     * @describe 选择班次
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
     * 选择寅时测试楼层
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
     * 选择寅时测试建筑物
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
     * 选择寅时测试城市
     */
    private void selectInnsmapCity() {
        Intent intent = new Intent(mActivity, InnsmapSelectActivity.class);
        intent.putExtra("typeId", Type.City.getId());
        mActivity.startActivityForResult(intent, requestInnsmapCode);
        mActivity.overridePendingTransition(R.anim.open_next, R.anim.close_main);
    }

    /**
     * 启动拍照intent，调用拍照程序
     */
    private void doTakePhoto() {
        Intent intent = new Intent(mActivity, CaptureImg.class);
        mActivity.startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    /**
     * 显示拍照的图片
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
     * 处理显示隐藏楼层结构
     *
     * @param show
     */
    private void indoorShow(int show) {
        // 如果当前设置显示楼层信息,且当前创建CU文件,则不处理显示动作
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
     * 勾选CQT测试
     *
     * @param isChecked 是否勾选
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
     * 勾选DT测试
     *
     * @param isChecked
     */
    private void checkDTTest(boolean isChecked) {
        if (isChecked) {
            checkCQT.setChecked(false);
            checkDT.setChecked(isChecked);
            checkPreviously.setChecked(false);
            // 屏蔽预打点
            automarkCheckLayout.setVisibility(View.GONE);
            this.previouslyLayout.setVisibility(View.GONE);
            appModel.setGpsTest(true);
            appModel.setPreviouslyTest(false);
            setPositon(defaultLonLatStr, defaultLonLatStr);
            // 打开GPS服务
            if (appModel.getSelectScene() == SceneType.HighSpeedRail&&ConfigRoutine.getInstance().isHsExternalGPS(mActivity)) {
                gpsInfo.releaseGps(mActivity, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
            }else {
                gpsInfo.openGps(mActivity, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
            }
            // GPS信息可见
            if (!appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
                gpsLayout.setVisibility(View.VISIBLE);
            }
            // 提示正在定位
            gpsTip.setText(R.string.main_indoor_waitgps);
        } else {
            LogUtil.w(TAG, "**********set gps to falses");
            checkCQT.setChecked(false);
            appModel.setGpsTest(false);
            this.previouslyLayout.setVisibility(View.VISIBLE);
            // 停止GPS服务
            gpsInfo.releaseGps(mActivity, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
            if (gpsInfo.getLocation() == null) {
                gpsTip.setText(R.string.main_indoor_gpsnosearch);
            }
            // 清除GPS计时器
            clearGpstimer();
            // 如果没有打开GPS搜索过，GPS信息不可见
            if (!appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
                gpsLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 勾选监听类
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
