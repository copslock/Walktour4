package com.dinglicom.dataset;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dingli.samsungvolte.HexConversion;
import com.dingli.seegull.SeeGullFlags.ScanIDShow;
import com.dinglicom.DataSetLib;
import com.dinglicom.data.control.BuildTestRecord;
import com.dinglicom.data.control.DataTableStruct.RecordDetailEnum;
import com.dinglicom.data.control.DataTableStruct.RecordInfoKey;
import com.dinglicom.dataset.logic.DatasetBuilder;
import com.dinglicom.dataset.logic.MonitorNetSwitch;
import com.dinglicom.dataset.logic.PointIndexChangeLinstener;
import com.dinglicom.dataset.logic.PushEventThread;
import com.dinglicom.dataset.model.ModuleInfo;
import com.walktour.Utils.AppVersionControl;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.BuildPower;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.HttpServer;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.StructParseUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UmpcSwitchMethod;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UnifyStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.Utils.WalkStruct.TelecomSetting;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigDataAcquisition;
import com.walktour.control.config.ConfigDebugModel;
import com.walktour.control.config.ConfigNBModuleInfo;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.config.ParameterSetting;
import com.walktour.control.config.ScannerInfo;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;
import com.walktour.gui.RebootDialog;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.setting.customevent.CustomEventFactory;
import com.walktour.gui.task.activity.scanner.model.CdmaCpichPilotModel;
import com.walktour.gui.task.activity.scanner.model.ColorCodeParseModel;
import com.walktour.gui.task.activity.scanner.model.LteCellDataPilotModel;
import com.walktour.gui.task.activity.scanner.model.LtePssPilotModel;
import com.walktour.gui.task.activity.scanner.model.LteRsPilotModel;
import com.walktour.gui.task.activity.scanner.model.LteSssPilotModel;
import com.walktour.gui.task.activity.scanner.model.RssiParseModel;
import com.walktour.gui.task.activity.scanner.model.TdScdmaPilotModel;
import com.walktour.gui.task.activity.scanner.model.WcdmaCpichPilotModel;
import com.walktour.gui.task.activity.scanner.model.WcdmaPschPilotModel;
import com.walktour.gui.task.activity.scanner.model.WcdmaSschPilotModel;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.model.BaseStructParseModel;
import com.walktour.model.DataAcquisitionModel;
import com.walktour.model.NetStateModel;
import com.walktour.model.Parameter;
import com.walktour.model.ScannerInfoModel;
import com.walktour.model.UmpcTestInfo;
import com.walktour.netsniffer.NetSnifferServiceUtil;
import com.walktour.service.NBHandlerService;
import com.walktour.service.app.Killer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.currentTimeMillis;

/**
 * 数据集管理控制类
 */
public class DatasetManager {

    public static final String TAG = "DatasetManager";

    /**
     * 数据集管理单例对象
     */
    private static DatasetManager sInstance;

    /**
     * 回放对象
     */
    private PlaybackManager mPlaybackManager;

    /**
     * 数据集建造类
     */
    private DatasetBuilder mDatasetBuilder;

    /**
     * 线程池管理
     */
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(5);

    /**
     * 参数查询定时线程池,避免睡眠时间不准问题
     */
    // private ScheduledExecutorService scheduler = Executors
    // .newScheduledThreadPool(1);

    /**
     * 采样点变化监听回调事件
     */
    private List<PointIndexChangeLinstener> pointIndexChangeLinsteners;

    /**
     * 上下文对象
     */
    private Context mContext;

    /**
     * 模块集合
     */
    private ModuleInfo moduleInfo = new ModuleInfo();

    /**
     * 句柄
     */
    private int datasetHandle = -1;

    /**
     * 是否回放
     */
    public static boolean isPlayback = false;

    /**
     * 是否回放加载中
     */

    public static boolean isPlaybackLoading = false;

    /**
     * 是否测试中
     */
    public static boolean isTesting = false;

    /**
     * 当前采样点
     */
    public int currentIndex = -1;

    /**
     * 模块端口2,事件,参数,层三,打开Trace均使用此端口
     */
    public static int PORT_2 = 2;

    /**
     * 只用于扫频仪模块
     */
    private static int PORT_3 = 3;

    /**
     * 回放端口
     */
    public static int PORT_4 = 4;

    private boolean isTraceOpen = false;
    /**
     * 是否可以写入文件
     */
    private boolean isCanWriteFile = false;

    /**
     * 创建文件时，文件名在底层库中会按指定端口拼接上端口名字
     */
    public static String Port2Name = "_Port2";
    public static String Port3Name = "_Port3";

    private Deviceinfo mDeviceInfo;

    private ConfigDebugModel debugModel;

    private PageQueryParas pageQueryParas;

    private ConfigDataAcquisition configDataAcquisition; // 数据采样频率

    private ConfigRoutine configRoutine;

    private boolean hasStarted = false;// 数据集是否启动完成

    private DataSetLib datasetLib;

    private PushEventThread mPushEventThread;

    private byte[] queryScanLock = new byte[0];

    private ParameterSetting parameterSetting;
    /**
     * NB服务是否启动
     */
    private boolean isNBServiceStart = false;

    /**
     * 查询事件线程
     */
    private QueryEventsThread mQueryEventsThread;
    /**是否是通用的logmask:true表示通用,false表示Volte*/
    private boolean isNormalLogMask = true;

    /**
     * [构造简要说明]
     */
    private DatasetManager(Context context) {
        this.mContext = context;
        datasetLib = new DataSetLib();
        parameterSetting = ParameterSetting.getInstance();
        mDeviceInfo = Deviceinfo.getInstance();
        debugModel = ConfigDebugModel.getInstance(mContext);
        pageQueryParas = new PageQueryParas();
        moduleInfo.portID = PORT_2;
        moduleInfo.deviceName = mDeviceInfo.getModuleName();
        moduleInfo.moduleType = mDeviceInfo.getChipVendor();
        configDataAcquisition = ConfigDataAcquisition.getInstance();
        configRoutine = ConfigRoutine.getInstance();
        mPushEventThread = new PushEventThread(context);
    }

    /**
     * 获得数据集管理单例<BR>
     * [功能详细描述]
     *
     * @param context 上下文
     * @return
     */
    public static synchronized DatasetManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatasetManager(context);
        }
        return sInstance;
    }

    public DataSetLib getDatasetLib() {
        return datasetLib;
    }

    /**
     * 打开数据集<BR>
     * 初始化数据集环境
     *
     * @param configPath
     * @param libPath
     * @param initHandle 是否需要重新初始化句柄
     * @return
     */
    private boolean openDataset(String configPath, String libPath, boolean initHandle) {
        if (initHandle) {
            datasetHandle = datasetLib.initHandle(configPath, libPath);
        }
        this.mDatasetBuilder = new DatasetBuilder(datasetHandle, moduleInfo, mContext);
        return datasetHandle != 0;
    }

    /**
     * 添加模块, 打开解码器<BR>
     *
     * @param logPath    数据集日志路径
     * @param port
     * @param deviceName 设备名称
     * @return >=0 成功；<0 失败，此时可以通过getLastError获取错误原因码
     */
    private int addModule(String logPath, int port, String deviceName) {
        datasetLib.setEnvironmentProperty(datasetHandle, logPath);
        return datasetLib.addMoudle(datasetHandle, port, deviceName);
    }

    /**
     * 设置模块列表<BR>
     * [功能详细描述]
     *
     * @param moudleMap
     */
    private void setModuleMap(ModuleInfo moudleMap) {
        this.moduleInfo = moudleMap;
    }

    /**
     * 向端口写数据
     *
     * @param bytes 数据
     * @return
     */
    public boolean devWritePort(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return false;
        int flag = datasetLib.devWritePort(datasetHandle, PORT_2, bytes, bytes.length);
        LogUtil.d(TAG, "devWritePort = " + flag);
        return true;
    }

    /**
     * 打开解码<BR>
     * 这个函数要在AddModule之后使用
     *
     * @param filePath       临时文件路径
     * @param orgfileFormat  输入格式
     * @param outfileFormat  输出文件格式
     * @param szGPSModelName GPS设备名，空结尾字符串，如“NMEA0183”,鼎利的用 "DingLi GPS"
     * @return 是否成功
     */
    private boolean openDecoder(String filePath, int orgfileFormat, int outfileFormat, String szGPSModelName) {
        // datasetLib.configDecoder(datasetHandle, false, false,
        // datasetLib.CPV_FileType_DCF, DataSetLib.CPV_FileType_DCF, true);
        datasetLib.configDecoder(datasetHandle, false, false, orgfileFormat, outfileFormat, true);
        int result = datasetLib.openDecoder(datasetHandle, filePath, szGPSModelName);
        return result >= 0;
    }

    /**
     * 添加模块, 打开解码器<BR>
     * TRACE口打开以后，调用此函数初始化数据集，开始解码数据
     *
     * @return >=0 成功；<0 失败，此时可以通过getLastError获取错误原因码
     */
    public boolean openTrace() {
        if(Deviceinfo.getInstance().isZTEA2020N3Pro()){
                UtilsMethod.execRootCmdx("chmod 777 /dev/diag ");
        }
        // 2014.1.10 qihang.li 转换文件时要关闭traceo
        int port = PORT_2;
        int moduleType = mDeviceInfo.getChipVendor(); // 设备芯片类型定义，如“0x9107”，参见Pilot.Chip文件
        String devicePort = mDeviceInfo.getTracepath(); // 设备端口名，默认值“/dev/ttyUSB0”
        int baudrate = mDeviceInfo.getBaudrate(); // 波特率，默认值152000
        int rxinterval = mDeviceInfo.getInterval(); // 获取数据周期，默认值20ms
        String diagPath = mDeviceInfo.getModuletype() != 8 ? // logmask路径
                AppFilePathUtil.getInstance().getAppConfigFile("Diag.cfg").getAbsolutePath() : "";
        int iTraceOffset = mDeviceInfo.getTraceOffset();
        int iNetLicense = 0; // 此值仅IPHONE有用

        TraceInfoInterface.traceData.l3MsgList.clear();

        // 当有扫频仪权限的时候，执行port3与port2，当有通用版本权限时只执行port3,两者权限都没有只执行port2。请慎重修改!!!!!
        if (ApplicationModel.getInstance().isScannerTest() || ApplicationModel.getInstance().isGeneralMode()) {
            datasetLib.configIPCDiag(datasetHandle, false);
            isTraceOpen = datasetLib.openTrace(datasetHandle, PORT_3, moduleType, "", baudrate, rxinterval, "", "", true, 0, 0xff) >= 0;
        }

        String ipcPath = mDeviceInfo.getIpcPath();
        int type = mDeviceInfo.getDevDiagType();
        Deviceinfo deviceinfo = Deviceinfo.getInstance();
        LogUtil.w(TAG, "isHasNBUsbTestModel:" + deviceinfo.isHasNBUsbTestModel());
        LogUtil.w(TAG, "isHasNBWifiTestModel:" + ConfigNBModuleInfo.getInstance(WalktourApplication.getAppContext()).isHasNBWifiTestModel());
        if (deviceinfo.isHasNBUsbTestModel()) {// NB usb 测试模式
            port = PORT_2;
            devicePort = ConfigNBModuleInfo.getInstance(mContext).getNbPort();
            if (!mDeviceInfo.getNbModuleNames().isEmpty()) {
                devicePort = ConfigNBModuleInfo.getInstance(mContext).getNbPort();
            }
            ipcPath = AppFilePathUtil.getInstance().getAppBaseDirectory() + "ipcDevDiagNB";
            type = 2;
            moduleType = Integer.valueOf(ConfigNBModuleInfo.getInstance(mContext).getChipvendor(), 16);
            LogUtil.w("DatasetManager", "parameters:port=" + port + ",type=" + type + ",ipc=" + (mDeviceInfo.getIpcDiag() == Deviceinfo.IPC_DIAG_ROOT) + ",ipcPath=" + ipcPath);
            datasetLib.configIpcValue(datasetHandle, port, type, mDeviceInfo.getIpcDiag() == Deviceinfo.IPC_DIAG_ROOT, ipcPath);
            LogUtil.w("DatasetManager", "NB-module:is open!");
            LogUtil.w("DatasetManager", "openTrace parameters:port=" + port + ",moduleType=" + moduleType + "," + "devicePort=" + devicePort + ",baudrate=" + baudrate + ",rxinterval=" + rxinterval + ",diagPath=" + diagPath + "," + "" + "," + false + ",iTraceOffset=" + iTraceOffset + ",iNetLicense=" + iNetLicense);
            isTraceOpen = datasetLib.openTrace(datasetHandle, port, moduleType, devicePort, baudrate, rxinterval, diagPath, "", false, iTraceOffset, iNetLicense) >= 0;
            LogUtil.w(TAG, "isTraceOpen=" + isTraceOpen);
            if (isTraceOpen && !isNBServiceStart) {// 打开串口,开取NB模块开始测试流程,
                Intent intentThree = new Intent(mContext, NBHandlerService.class);
                intentThree.putExtra("select", NBHandlerService.COMMAND_UPDOWN);
                mContext.startService(intentThree);
                isNBServiceStart = true;
            }
        } else if (ConfigNBModuleInfo.getInstance(mContext).isHasNBWifiTestModel()) {// NB wifi测试模式
            port = PORT_2;
            devicePort = ConfigNBModuleInfo.getInstance(mContext).getNbSelectWifiServerIP() + ":8899";
            ipcPath = AppFilePathUtil.getInstance().getAppBaseDirectory() + "ipcDevDiagNB";
            type = 2;
            moduleType = Integer.valueOf(ConfigNBModuleInfo.getInstance(mContext).getChipvendor(), 16);
            LogUtil.w("DatasetManager", "parameters:port=" + port + ",type=" + type + ",ipc=" + (mDeviceInfo.getIpcDiag() == Deviceinfo.IPC_DIAG_ROOT) + ",ipcPath=" + ipcPath);
            datasetLib.configIpcValue(datasetHandle, port, type, mDeviceInfo.getIpcDiag() == Deviceinfo.IPC_DIAG_ROOT, ipcPath);
            LogUtil.w("DatasetManager", "NB-module:is open!");
            LogUtil.w("DatasetManager", "openTrace parameters:port=" + port + ",moduleType=" + moduleType + "," + "devicePort=" + devicePort + ",baudrate=" + baudrate + ",rxinterval=" + rxinterval + ",diagPath=" + diagPath + "," + "" + "," + false + ",iTraceOffset=" + iTraceOffset + ",iNetLicense=" + iNetLicense);
            isTraceOpen = datasetLib.openTrace(datasetHandle, port, moduleType, devicePort, baudrate, rxinterval, diagPath, "", false, iTraceOffset, iNetLicense) >= 0;
            LogUtil.w(TAG, "isTraceOpen=" + isTraceOpen);
            if (isTraceOpen && !isNBServiceStart) {// 打开串口,开取NB模块开始测试流程,
                Intent intentThree = new Intent(mContext, NBHandlerService.class);
                intentThree.putExtra("select", NBHandlerService.COMMAND_UPDOWN);
                mContext.startService(intentThree);
                isNBServiceStart = true;
            }
        } else {// 单机测试模式
            LogUtil.w("DatasetManager", "parameters:port=" + port + ",type=" + type + ",ipc=" + (mDeviceInfo.getIpcDiag() == Deviceinfo.IPC_DIAG_ROOT) + ",ipcPath=" + ipcPath);
            // 新接口
            datasetLib.configIpcValue(datasetHandle, port, type, mDeviceInfo.getIpcDiag() == Deviceinfo.IPC_DIAG_ROOT, ipcPath);
            LogUtil.w("DatasetManager", "NB-module:is close!");
            LogUtil.w("DatasetManager", "openTrace parameters:port=" + port + ",moduleType=" + moduleType + "," + "devicePort=" + devicePort + ",baudrate=" + baudrate + ",rxinterval=" + rxinterval + ",diagPath=" + diagPath + "," + "" + "," + false + ",iTraceOffset=" + iTraceOffset + ",iNetLicense=" + iNetLicense);
            if (!ApplicationModel.getInstance().isGeneralMode()) {
                isTraceOpen = datasetLib.openTrace(datasetHandle, port, moduleType, devicePort, baudrate, rxinterval, diagPath, "", false, iTraceOffset, iNetLicense) >= 0;
            }
        }

        moduleInfo.lastTraceIndex = 0;
        moduleInfo.lastTraceTime = currentTimeMillis();
        LogUtil.w(TAG, "isRunSaveOrgData=" + configRoutine.isRunSaveOrgData(mContext));
        if (configRoutine.isRunSaveOrgData(mContext)) {
            long timeValue = currentTimeMillis();
            LogUtil.w(TAG, PORT_2 + "----" + true + "----" + Environment.getExternalStorageDirectory() + "----" + "/Walktour/temp/" + UtilsMethod.sdfhmsss.format(timeValue) + ".org");
            datasetLib.configSaveOrgSource(datasetHandle, PORT_2, true, Environment.getExternalStorageDirectory() + "/Walktour/temp/" + UtilsMethod.sdfhmsss.format(timeValue) + ".org");
        }
        LogUtil.d(TAG, "isTraceOpen=" + isTraceOpen);
        return isTraceOpen;
    }

    /**
     * 关闭Trace串口<BR>
     * [功能详细描述]
     *
     * @param port
     */
    public void closeTrace(int port) {
        if (isTraceOpen) {
            LogUtil.d(TAG, "closeTrace start.");
            isTraceOpen = false;
            datasetLib.closeTrace(datasetHandle, port);
            moduleInfo.lastTraceIndex = 0;
            LogUtil.d(TAG, "closeTrace end.");
            if (ApplicationModel.getInstance().isScannerTest()) {
                datasetLib.closeTrace(datasetHandle, PORT_3);
            }
        }
    }

    /**
     * 启动数据集服务<BR>
     * [功能详细描述]
     *
     * @param initHandle 是否需要初始化句柄
     * @return true or false
     */
    public boolean startDataSet(boolean initHandle) {
        String ddibPath = ConfigRoutine.getInstance().getStorgePathDdib();
        LogUtil.w(TAG, "--ddib:" + ddibPath);
        AppFilePathUtil util = AppFilePathUtil.getInstance();
        if (this.openDataset(util.getAppConfigDirectory(), util.getAppLibDirectory(), initHandle)) {

            ScannerInfoModel scannerModel = ScannerInfo.getInstance().getScannerList().get(0);
            LogUtil.w(TAG, "--scannerModel:" + scannerModel.toString());
            String logPath = AppFilePathUtil.getInstance().getSDCardBaseDirectory("liblog");
            LogUtil.d(TAG, "-----logPath:" + logPath);
            LogUtil.w(TAG, "isHasNBUsbTestModel:" + Deviceinfo.getInstance().isHasNBUsbTestModel());
            LogUtil.w(TAG, "isHasNBWifiTestModel:" + ConfigNBModuleInfo.getInstance(WalktourApplication.getAppContext()).isHasNBWifiTestModel());
            if (ApplicationModel.getInstance().isNBTest()) {
                LogUtil.d(TAG, "-----NBModuleName:" + ConfigNBModuleInfo.getInstance(mContext).getNbModuleName());
                this.addModule(logPath, PORT_2, ConfigNBModuleInfo.getInstance(mContext).getNbModuleName());
            } else {
                if (ApplicationModel.getInstance().isScannerTest() || ApplicationModel.getInstance().isGeneralMode()) {
                    this.addModule(logPath, PORT_3, "PCTEL_Android");
                }
                if (!ApplicationModel.getInstance().isGeneralMode()) {
                    this.addModule(logPath, PORT_2, mDeviceInfo.getModuleName());
                }
            }

            // 设置实时输出org文件
            // datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2,
            // DataSetLib.ConfigPropertyKey_OutputDataBufferRealTime, 1);

            String authStr = ConfigRoutine.getInstance().getAuthValueStr(mContext);
            LogUtil.w(TAG, "--authStr:" + authStr);
            datasetLib.setThirdPropertyValue(datasetHandle, authStr, MyPhoneState.getInstance().getDeviceId(mContext));

            /**是否存储5G信令，1表示存储，0表示不存储，默认为0*/
            datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_Storage5GInfo, 1);
            // 配置产品类型
            datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_ProductType, 3);
//            datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_OutputDdibFileInfo, 0);
            setConfigPropertyKey_DecodeOutputMS_MsgDetailBuffer();

            boolean flag = false;
            String szGPSModelName = "DingLi GPS";
            if (ApplicationModel.getInstance().isGeneralMode())
                flag = this.openDecoder(ddibPath, DataSetLib.FileType.CPV_FileType_DCF, DataSetLib.FileType.CPV_FileType_DCF, szGPSModelName);
            else
                flag = this.openDecoder(ddibPath, DataSetLib.FileType.CPV_FileType_RCU, DataSetLib.FileType.CPV_FileType_RCU, szGPSModelName);
            LogUtil.d(TAG, "-----flag:" + flag);
            if (flag) {
                boolean result = openTrace(); // 0xDDDD表示是从外部压入数据，后面参数无效

                LogUtil.i(TAG, String.format(Locale.getDefault(), "moduleName:%s,chipVendor:%d ,dev:%s, baudrate%d,interval:%d", mDeviceInfo.getModuleName(), mDeviceInfo.getChipVendor(), mDeviceInfo.getTracepath(), mDeviceInfo.getBaudrate(), mDeviceInfo.getInterval()));
                if (!result) {
                    return false;
                }
                isPlayback = false;
                this.setModuleMap(moduleInfo);
                if (!TotalInterface.isInit) { // 初始化统计库
                    Log.i(this.getClass().getName(), "isInit--- " + TotalInterface.isInit);
                    TotalInterface.getInstance(mContext).initLib();
                    Log.i(this.getClass().getName(), "init   finish");
                }
                // 数据集打开时创建事件查询线程,状态为暂停,只有在创建文件之后才需要查询事件
                if (mQueryEventsThread != null && !mQueryEventsThread.isInterrupted()) {
                    mQueryEventsThread.stopThread();
                    mQueryEventsThread = null;
                }

                mQueryEventsThread = new QueryEventsThread();
                mQueryEventsThread.start();
                hasStarted = true;
                return true;
                // if(initesourceCategory(datasetHandle)) {
                // mQueryEventsThread = new QueryEventsThread();
                // mQueryEventsThread.start();
                // hasStarted = true;
                // return true;
                // }else{
                // hasStarted = false;
                // return false;
                // }

            }
            LogUtil.e(TAG, "open decoder failed");
        } else {
            LogUtil.e(TAG, "open dataset failed:" + datasetHandle);
        }

        return false;
    }

    /**
     * 初始化数据集数据集资源库
     * @param datasetHandle
     */
    private boolean initesourceCategory(int datasetHandle) {
        File file = AppFilePathUtil.getInstance().getAppConfigFile("ResourceCategory.db");
        String pDbPath = file.getAbsolutePath();
        int initResult = datasetLib.initResourceCategory(datasetHandle, DataSetLib.RESOURCETYPE_EVENT, pDbPath, "");
        // LogUtil.w(TAG,"initResourceCategory="+initResult);
        // file=null;
        // if(initResult==1){//初始化数据集数据库成功
        // 初始化事件的定义
        EventManager.getInstance().initEevnts(mContext);
        // 初始化自定义事件的定义
        CustomEventFactory.getInstance().initCustomEvent();
        return true;
        // }else{
        // return false;
        // }

    }

    /**
     * 关闭数据集<BR>
     * [功能详细描述]
     */
    public void closeDataSet() {
        LogUtil.d(TAG, "closeDataSet start.");
        closeTrace(moduleInfo.portID);
        if (ApplicationModel.getInstance().isScannerTest()) {
            LogUtil.d(TAG, "delMoudle:" + PORT_3);
            datasetLib.delMoudle(datasetHandle, PORT_3);
        }
        datasetLib.finishDataToDecoder(datasetHandle);
        datasetLib.closeDecoder(datasetHandle);
        datasetLib.delMoudle(datasetHandle, moduleInfo.portID);
        datasetLib.freeHandle(datasetHandle);
        LogUtil.d(TAG, "freeHandle=" + datasetHandle);
        datasetHandle = 0;
        if (mQueryEventsThread != null && !mQueryEventsThread.isInterrupted()) {
            mQueryEventsThread.stopThread();
            mQueryEventsThread = null;
        }
        LogUtil.d(TAG, "closeDataSet end.");
    }

    private int runTimeCount = 0;

    /**
     * 查询页面参数线程<BR>
     * 需循环查询
     *
     * @author 黄广府
     * @version [WalkTour Client V100R001C03, 2013-8-29]
     */
    private int[] scanIdArray = { ScanIDShow.ScanID_RSSI, ScanIDShow.SCANID_CDMA_CPICH, ScanIDShow.SCANID_WCDMA_PSCH, ScanIDShow.SCANID_WCDMA_CPICH, ScanIDShow.SCANID_WCDMA_SSCH, ScanIDShow.SCANID_COLORCODE, ScanIDShow.SCANID_TDSCDMA_PCCPCH, ScanIDShow.SCANID_LTE_CellInfo, ScanIDShow.SCANID_LTE_PSS, ScanIDShow.SCANID_LTE_SSS, ScanIDShow.SCANID_LTE_RS, };

    /**
     * 查询页面参数线程<BR>
     * 需循环查询
     *
     * @author 黄广府
     * @version [WalkTour Client V100R001C03, 2013-8-29]
     */
    private void queryPageParas() {
        // LogUtil.d(TAG,"----queryPageParas----start----");
        if (debugModel.isQueryParam() || TraceInfoInterface.currentShowChildTab == ShowInfoType.L3Msg) {
            runTimeCount += 1;
            if (runTimeCount % 5 == 0) {
                LogUtil.d(TAG, "--runTimeCount:" + runTimeCount + "--index:" + currentIndex + "--childTab:" + TraceInfoInterface.currentShowChildTab);
            }
            if (runTimeCount >= BuildPower.TBTime) {
                runTimeCount = 0;
                long saveTime = ConfigRoutine.getInstance().getNetTimes(mContext);
                if (currentTimeMillis() < saveTime) {
                    if (!ApplicationModel.getInstance().isTestJobIsRun()) {
                        mContext.startService(new Intent(mContext, Killer.class));
                    }
                } else {
                    ConfigRoutine.getInstance().setNetTimes(mContext, currentTimeMillis());
                }
            }

            LogUtil.d(TAG, "currentIndex>>>>" + currentIndex);
            int port = isPlayback ? PORT_4 : PORT_2;
            getTotalPointCount(port, true);
            currentIndex = DatasetManager.this.getCurrentIndex();
            boolean isOn = (getCurrentNetType(port, currentIndex) != UnifyParaID.NET_NO_SERVICE);
            if (ApplicationModel.getInstance().isTestJobIsRun()) {
                if (MyPhoneState.getInstance().isServiceAlive() != isOn) {
                    MyPhoneState.getInstance().setServiceAlive(isOn);

                    // 只有脱网时需要发送消息通知测试服务停止当前测试
                    if (!isOn) {
                        mContext.sendBroadcast(new Intent(WalkMessage.SERVICE_CHANGE_BY_TRACE));
                    }
                }
            }

            // 当前正在测试中,如果当前为电信设置开关打开状态,并且当前设置的非普通模式
            if (ApplicationModel.getInstance().isTestJobIsRun() && !ApplicationModel.getInstance().isTestInterrupt() && ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.TelecomSwitch) && (configRoutine.getTelecomDataNetSet(mContext) != TelecomSetting.Normal || configRoutine.getTelecomVoiceNetSet(mContext) != TelecomSetting.Normal)) {
                // 当当前网络与电信设置中的网络不一致时,发送网络不一致消息
                if (TraceInfoInterface.currentNetType.getGeneral() != configRoutine.getTelecomDataNetSet(mContext).getNetWorkGeneral() || TraceInfoInterface.currentNetType.getGeneral() != configRoutine.getTelecomVoiceNetSet(mContext).getNetWorkGeneral()) {
                    mContext.sendBroadcast(new Intent(WalkMessage.TELECOM_SETTIMG_NETNOTMATCH));
                }
            }

            if (!isPlayback && isTraceOpen && MyPhoneState.getInstance().isServiceAlive()) {
                monitorTraceAbnormal(currentIndex, port);
            }

            notifyPointIndexChange(currentIndex, false);
            // 查询扫频仪,参数界面信息
            // startScannerQuery();
            boolean isScanLicense = ApplicationModel.getInstance().isScannerTest();

            if (isScanLicense) {
                int scanCurrentIndex = getTotalPointCount(isPlayback ? PORT_4 : PORT_3, true);
                synchronized (queryScanLock) {
                    for (int i = 0; i < scanIdArray.length; i++) {
                        queryScannerParas(isPlayback ? currentIndex : scanCurrentIndex, scanIdArray[i]);
                    }
                }
            }

            switch (TraceInfoInterface.currentShowChildTab) {
            case Chart:
                mDatasetBuilder.buildDatasetParamQuery(port, currentIndex, pageQueryParas.getPageQueryChart(mContext));
                TraceInfoInterface.traceData.setChartLineQValue(moduleInfo.lastChartIndex, currentIndex, mContext);
                moduleInfo.lastChartIndex = currentIndex;
                break;
            case Data:

                if (isPlayback && !ApplicationModel.getInstance().isFreezeScreen()) {
                    mDatasetBuilder.buildDataBroardQuery(mContext, port, currentIndex, pageQueryParas.PageQueryDataBrabord);
                }
                break;
            case VideoPlay:
                if (isPlayback) {
                    // mDatasetBuilder.buildVideoStreamQuery(port,
                    // currentIndex,
                    // pageQueryParas.PageQueryVideoStream);
                }
                break;
            case TcpIpPcap:
                if (!ApplicationModel.getInstance().isBeiJingTest()) {
                    NetSnifferServiceUtil.getInstance().buildTcpipSimpleInfo();
                }

                break;
            case OtherMap:
                break;
            case GoogleMap:
                break;
            case LTECA:
                break;
            default:
                if (!isPlayback && debugModel.isQuerySignal()) { // 如何回放状态不再请求层三消息了，只有到停止回放的时刻
                    queryL3Message();
                }
                break;
            }

            mDatasetBuilder.buildDatasetParamQuery(port, currentIndex, parameterSetting.getPageParaByNetworkType(TraceInfoInterface.currentNetType.name()));
            //查询当前速率
            mDatasetBuilder.buildDatasetParamQuery(port, currentIndex, PageQueryParas.pageDataService);
            switch (TraceInfoInterface.currentNetType) {
            case GSM:
                mDatasetBuilder.buildDatasetParamQuery(port, currentIndex, PageQueryParas.pageGSMQueryPublicId);
                mDatasetBuilder.buildSpecialStructNew(port, currentIndex, UnifyParaID.G_gsm_Struct); // 查询GSM结构体
                break;
            case TDSCDMA:
                mDatasetBuilder.buildDatasetParamQuery(port, currentIndex, PageQueryParas.pageTdQueryPublicId);
                // 查询结构体
                mDatasetBuilder.buildSpecialStruct(port, currentIndex, UnifyStruct.TDPhysChannelInfoDataV2.FLAG);
                mDatasetBuilder.buildSpecialStruct(port, currentIndex, UnifyStruct.FLAG_TD_HSDPAPhysChannelInfoData);
                mDatasetBuilder.buildSpecialStruct(port, currentIndex, UnifyStruct.FLAG_TD_Activate_PDP_Context_Accept_Win_Data);
                break;
            case CDMA:
                mDatasetBuilder.buildDatasetParamQuery(port, currentIndex, PageQueryParas.pageCDMAQueryPublicId);
                mDatasetBuilder.buildCellSetQuery(port, currentIndex, DataSetLib.EnumNetType.CDMA);
                mDatasetBuilder.buildCellSetQuery(port, currentIndex, DataSetLib.EnumNetType.EVDO);
                break;
            case WCDMA:
                mDatasetBuilder.buildDatasetParamQuery(port, currentIndex, PageQueryParas.pageWCDMAQueryPublicId);
                mDatasetBuilder.buildCellSetQuery(port, currentIndex, DataSetLib.EnumNetType.WCDMA);

                mDatasetBuilder.buildSpecialStruct2(port, currentIndex, UnifyStruct.FLAG_WCDMA_TrCH_DL_Configuration);
                mDatasetBuilder.buildSpecialStruct2(port, currentIndex, UnifyStruct.FLAG_WCDMA_RLC_DL_Entities);
                mDatasetBuilder.buildSpecialStruct2(port, currentIndex, UnifyStruct.FLAG_WCDMA_RLC_UL_Entities);
                break;
            case LTE:
                mDatasetBuilder.buildDatasetParamQuery(port, currentIndex, PageQueryParas.pageLteQueryPublicId);

                mDatasetBuilder.buildCellSetQuery(port, currentIndex, DataSetLib.EnumNetType.LTE);
                mDatasetBuilder.buildSpecialStruct(port, currentIndex, UnifyParaID.LTE_EPS_BearerContext_02C1);
                mDatasetBuilder.buildSpecialStruct(port, currentIndex, UnifyParaID.LTE_APN);
                mDatasetBuilder.buildGSMSpecialStruct(port, currentIndex, UnifyParaID.LTE_GSM_CELL_LIST);
                break;

            case NBIoT:
            case CatM:
                mDatasetBuilder.buildSpecialStruct(port, currentIndex, UnifyParaID.LTE_NB_FORMAT);

                mDatasetBuilder.buildCellSetQuery(port, currentIndex, DataSetLib.EnumNetType.LTE);

                mDatasetBuilder.buildDatasetParamQuery(port, currentIndex, PageQueryParas.pageNBIoTQueryPublicId);
                break;
                case ENDC:
                    mDatasetBuilder.buildDatasetParamQuery(port, currentIndex, PageQueryParas.pageLteQueryPublicId);
                    mDatasetBuilder.buildDatasetParamQuery(port, currentIndex, PageQueryParas.pageENDCQueryPublicId);
                    mDatasetBuilder.buildCellSetQuery(port, currentIndex, DataSetLib.EnumNetType.LTE);
                    mDatasetBuilder.buildSpecialStruct(port, currentIndex, UnifyParaID.LTE_EPS_BearerContext_02C1);
                    mDatasetBuilder.buildSpecialStruct(port, currentIndex, UnifyParaID.LTE_APN);
                    mDatasetBuilder.buildGSMSpecialStruct(port, currentIndex, UnifyParaID.LTE_GSM_CELL_LIST);
                    mDatasetBuilder.buildENDCSpecialStruct(port, currentIndex, UnifyParaID.ENDC_CELL_LIST);
                    break;
            default:
                break;
            }
        }
        // LogUtil.d(TAG,"----queryPageParas----end----");
    }

    /**
     * 根据传入的端口，采样点获得当前网络类型 当前网络GSM = 0x01,WCDMA = 0x02,TD-SCDMA = 0x04,CDMA\EVDO
     * = 0x08,LTE = 0x10,Unknown = 0x20,NoService = 0x80
     *
     * @param port
     * @param pointIndex
     * @return
     */
    public int getCurrentNetType(int port, int pointIndex) {
        // LogUtil.d(TAG,"----getCurrentNetType----start----");
        int netType = WalkStruct.CurrentNetState.Unknown.getNetType();
        try {
            String type = getRealParam(port, UnifyParaID.CURRENT_NETWORKTYPE, pointIndex, pointIndex, false, true);
            if (StringUtil.isEmpty(type))
                return netType;
            TraceInfoInterface.decodeResultUpdate(UnifyParaID.CURRENT_NETWORKTYPE, type);
            netType = Double.valueOf(type).intValue();
        } catch (Exception e) {
            LogUtil.w(TAG, "getCurrentNetType", e);
            netType = WalkStruct.CurrentNetState.Unknown.getNetType();
        }
        WalkStruct.CurrentNetState currentNetState = WalkStruct.CurrentNetState.getNetStateById(netType);
        if (currentNetState != TraceInfoInterface.currentNetType) {
            LogUtil.i(TAG, "--getCurrentNetType--pointIndex:" + pointIndex + "---NetType:" + currentNetState.getNetTypeName());
            TraceInfoInterface.currentNetType = currentNetState;
            MyPhoneState.saveNetTypeToSpf(currentNetState.getNetTypeName());
            if (ApplicationModel.getInstance().isNBTest() && Deviceinfo.getInstance().getNbPowerOnStaus() == Deviceinfo.POWN_ON_SUCCESS && !DatasetManager.isPlayback) {
                NetStateModel state = NetStateModel.getInstance();
                WalkStruct.CurrentNetState status = state.getCurrentNetTypeSync();
                TraceInfoInterface.currentNetType = status;
                MyPhoneState.saveNetTypeToSpf(status.getNetTypeName());
            }
        }
        // LogUtil.i(TAG, "----getCurrentNetType----end----" + TraceInfoInterface.currentNetType.name());
        return netType;
    }

    /**
     * 监控串口是否正常流程
     */
    private void monitorTraceAbnormal(int curIndex, int port) {
        try {
            if (curIndex > -1 && curIndex != moduleInfo.lastTraceIndex) {
                moduleInfo.lastTraceTime = currentTimeMillis();
                ApplicationModel.getInstance().setTraceInitSucc(true);
                moduleInfo.lastTraceIndex = curIndex;
            } else {
                LogUtil.w(TAG, "--MonitorTrace curIndex:" + curIndex + ",--lastTraceIndex:" + moduleInfo.lastTraceIndex);
                boolean isReboot = !ApplicationModel.getInstance().isTestPause() || ApplicationModel.getInstance().isTestPause() && !configRoutine.isPuaseNoData(mContext);
                LogUtil.w(TAG, "--MonitorTrace isPause:" + ApplicationModel.getInstance().isTestPause() + ",--notPushData:" + configRoutine.isPuaseNoData(mContext) + ",isReboot=" + isReboot);

                // 当前非暂停，或者暂停但暂停不生成文件为false
                if (isReboot) {
                    LogUtil.d(TAG, "monitorTraceAbnormal,rebootTrace.");
                    rebootTrace(port, 2);
                }
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "monitorTraceAbnormal", e);
        }
    }

    /**
     * 重启串口
     *
     * @param port
     */
    public void rebootTrace(int port, int from) {
        try {
            // 如果当前获得的采样点没有变化，判断是否超过指定时间没有变化,如果重启串口
            if (currentTimeMillis() - moduleInfo.lastTraceTime > (AppVersionControl.getInstance().isTelecomInspection() ? 2 * 60 * 1000 : 1000 * 20)) {
                ApplicationModel.getInstance().setTraceInitSucc(false);
                LogUtil.d(TAG, "--trace reboot lastTime :" + UtilsMethod.sdfhmsss.format(moduleInfo.lastTraceTime) + "--cur:" + UtilsMethod.sdfhmsss.format(currentTimeMillis()) + "--f:" + from);
                LogUtil.d(TAG, "moduleInfo.traceRebootTime=" + moduleInfo.traceRebootTime);
                if (moduleInfo.traceRebootTime < 5) {
                    LogUtil.d(TAG, "is has NB Moduel:" + ApplicationModel.getInstance().isNBTest());
                    if (ApplicationModel.getInstance().isNBTest()&&!ConfigNBModuleInfo.getInstance(mContext).isHasNBWifiTestModel()) {
                        LogUtil.w(TAG, "Check NB-Iot device.");
                        // NB模式下，提示重启串口前判断下NB-Iot是否丢失,记录下日志.
                        boolean isFlag = ConfigNBModuleInfo.getInstance(mContext).checkNBModule();
                        if (!isFlag) {
                            EventManager.getInstance().addTagEvent(mContext, System.currentTimeMillis(), "NB-Iot is lost.");
                            LogUtil.w(TAG, "NB-Iot is Lost---------------------.");
                        }
                    }
                    LogUtil.w(TAG, "--trace reboot--" + moduleInfo.traceRebootTime);
                    if(isLiErDa()){
                     //NB测试的利尔达设备不提示LOST
                    }else {
                        if(!ApplicationModel.getInstance().isNBTest()) {
                            EventBytes.Builder(mContext, RcuEventCommand.MessageLost).addInteger(20).addInteger((int) (currentTimeMillis() - moduleInfo.lastTraceTime) / 1000).writeToRcu(currentTimeMillis());
                        }
                    }
                    if (ApplicationModel.getInstance().isUmpcTest()) {
                        UmpcSwitchMethod.sendEventToUmpc(mContext, WalkStruct.UMPCEventType.Alarm.getUMPCEvnetType(), "Trace is reboot,Message is lost.", true);
                    }
                    closeTrace(port);
                    Thread.sleep(1000);
                    openTrace();
                    Thread.sleep(1000);
                    moduleInfo.traceRebootTime += 1;
                    moduleInfo.lastTraceTime = currentTimeMillis();
                } else {
                    LogUtil.d(TAG, "is has NB Moduel:" + ApplicationModel.getInstance().isNBTest());
                    if (ApplicationModel.getInstance().isNBTest()&&!ConfigNBModuleInfo.getInstance(mContext).isHasNBWifiTestModel()) {
                        LogUtil.w(TAG, "Check NB-Iot device.");
                        // NB模式下，提示重启串口前判断下NB-Iot是否丢失,记录下日志.
                        boolean isFlag = ConfigNBModuleInfo.getInstance(mContext).checkNBModule();
                        if (!isFlag) {
                            EventManager.getInstance().addTagEvent(mContext, System.currentTimeMillis(), "NB-Iot is lost.");
                            LogUtil.w(TAG, "NB-Iot is Lost---------------------.");
                        }
                    }
                    LogUtil.w(TAG, "--trace reboot device--");
                    moduleInfo.traceRebootTime = 0;

                    if (ApplicationModel.getInstance().isUmpcTest()) {
                        UmpcSwitchMethod.sendEventToUmpc(mContext, WalkStruct.UMPCEventType.Alarm.getUMPCEvnetType(), "Trace is reboot more 5 times,Device show reboot dialog.", true);
                    }
                    // 如果串口重启5次仍旧串口异常，提示重启
                    if(isLiErDa()) {
                        //NB测试的利尔达设备不提示LOST
                    }else {
                        if(!ApplicationModel.getInstance().isNBTest()) {//NB测试存在问题,不提示.
                            Intent dialIntent = new Intent(mContext, RebootDialog.class);
                            dialIntent.putExtra(RebootDialog.DIALOG_ID, RebootDialog.traceInitFaileToRboot);
                            dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(dialIntent);
                        }
                    }
                }
            } else {
                LogUtil.d(TAG, "--trace rebootTrace nothing--f:" + from + "--" + (currentTimeMillis() - moduleInfo.lastTraceTime));
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "rebootTrace", e);
        }
    }

    /***
     * 是否是利尔达设备
     * @return
     */
    private boolean isLiErDa(){
        if(ConfigNBModuleInfo.getInstance(mContext).isLierda()&&ApplicationModel.getInstance().isNBTest()&&TraceInfoInterface.traceData.getStateInfoNoQuery().getCurrentJon().equalsIgnoreCase("IdleTask")){
            return true;
        }
        return false;
    }
    /**
     * 查询Scanner参数
     */
    private void queryScannerParas(int scanCrrentIndex, int showType) {
        ArrayList<BaseStructParseModel> itemList = new ArrayList<>();
        int itemCount = mDatasetBuilder.getStructItemCount(scanCrrentIndex, showType, isPlayback ? PORT_4 : PORT_3);
        Log.d(TAG, "------Start-----" + scanCrrentIndex);
        for (int i = 0; i < itemCount; i++) {
            byte[] buffer = mDatasetBuilder.getStructItem(scanCrrentIndex, showType, i, isPlayback ? PORT_4 : PORT_3);
            try {
                switch (showType) {
                case ScanIDShow.ScanID_RSSI:
                    RssiParseModel rssiModel = new RssiParseModel();
                    StructParseUtil.parse(rssiModel, buffer);
                    itemList.add(rssiModel);
                    break;
                case ScanIDShow.SCANID_CDMA_CPICH:
                    CdmaCpichPilotModel cCpichModel = new CdmaCpichPilotModel();
                    StructParseUtil.parse(cCpichModel, buffer);
                    itemList.add(cCpichModel);
                    break;
                case ScanIDShow.SCANID_WCDMA_PSCH:
                    WcdmaPschPilotModel wPschModel = new WcdmaPschPilotModel();
                    StructParseUtil.parse(wPschModel, buffer);
                    itemList.add(wPschModel);
                    break;
                case ScanIDShow.SCANID_WCDMA_CPICH:
                    WcdmaCpichPilotModel wCpichModel = new WcdmaCpichPilotModel();
                    StructParseUtil.parse(wCpichModel, buffer);
                    itemList.add(wCpichModel);
                    break;
                case ScanIDShow.SCANID_WCDMA_SSCH:
                    WcdmaSschPilotModel wSschModel = new WcdmaSschPilotModel();
                    StructParseUtil.parse(wSschModel, buffer);
                    itemList.add(wSschModel);
                    break;
                case ScanIDShow.SCANID_COLORCODE:
                    ColorCodeParseModel colorCodeModel = new ColorCodeParseModel();
                    StructParseUtil.parse(colorCodeModel, buffer);
                    itemList.add(colorCodeModel);
                    break;
                case ScanIDShow.SCANID_TDSCDMA_PCCPCH:
                    TdScdmaPilotModel tdModel = new TdScdmaPilotModel();
                    StructParseUtil.parse(tdModel, buffer);
                    itemList.add(tdModel);
                    break;
                case ScanIDShow.SCANID_LTE_CellInfo:
                    LteCellDataPilotModel lteCellInfoModel = new LteCellDataPilotModel();
                    StructParseUtil.parse(lteCellInfoModel, buffer);
                    itemList.add(lteCellInfoModel);
                    break;
                case ScanIDShow.SCANID_LTE_PSS:
                    LtePssPilotModel ltePssPilotModel = new LtePssPilotModel();
                    StructParseUtil.parse(ltePssPilotModel, buffer);
                    itemList.add(ltePssPilotModel);
                    break;
                case ScanIDShow.SCANID_LTE_SSS:
                    LteSssPilotModel lteSssPilotModel = new LteSssPilotModel();
                    StructParseUtil.parse(lteSssPilotModel, buffer);
                    itemList.add(lteSssPilotModel);
                    break;
                case ScanIDShow.SCANID_LTE_RS:
                    LteRsPilotModel rsPilotModel = new LteRsPilotModel();
                    StructParseUtil.parse(rsPilotModel, buffer);
                    itemList.add(rsPilotModel);
                    break;
                default:
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        TraceInfoInterface.traceData.putScanResultMap(showType, itemList); // 往临时存储压数据

    }

    /**
     * 测试时查询事件，如语音通话的关键层三事件、地图窗口事件 twq20131113此线程启动后，同时执行事件统计，参数统计的查询
     * 统计执行分别为：buildEventTotalValue，buildParamTotalValue
     * 还有一部分里程覆盖统计，放在DataSetBuilder类的addGpsInfo方法中去触发调用，
     * 该方法为往文件中写入一个点时执行一次GPS点的查询，重新获得GPS点
     *
     * @author qihang.li
     */
    private class QueryEventsThread extends Thread {
        private boolean isStop = false;
        private boolean isQueryRunning = true;
        private boolean isGpsTest = false;
        // 网络监控时,需要查询的参数ID
        private int[] monitorNetParams = { UnifyParaID.L_SRV_Work_Mode, UnifyParaID.LTECA_Capacity_Packet_Capability, UnifyParaID.VOLTE_Invite_Request_Cause, UnifyParaID.CURRENT_NETWORKTYPE, UnifyParaID.LTECA_Active_SCell_Count, UnifyParaID.LTECA_UL_Active_SCell_Count };
        private final int QUERY_INTERVAL_TOTAL = 1000;
        private long mStartTime;
        private int[] monitorParams;

        private void initMonitorParams() {
            monitorParams = new int[monitorNetParams.length + PageQueryParas.OtherParamQuery.length + PageQueryParas.PBMParamQuery.length];
            int pos = 0;
            for (int i = 0; i < monitorNetParams.length; i++) {
                monitorParams[pos++] = monitorNetParams[i];
            }
            for (int i = 0; i < PageQueryParas.OtherParamQuery.length; i++) {
                monitorParams[pos++] = PageQueryParas.OtherParamQuery[i];
            }
            for (int i = 0; i < PageQueryParas.PBMParamQuery.length; i++) {
                monitorParams[pos++] = PageQueryParas.PBMParamQuery[i];
            }
        }

        @Override
        public void run() {

            /**
             * 监控FDD,TDD CA网络权限信息
             */
            initMonitorParams();
            MonitorNetSwitch netSwitch = new MonitorNetSwitch(mContext);
            BuildParamTotalValueThread thread = new BuildParamTotalValueThread();
            LogUtil.i(TAG, "start query event and l3msg");
            do {
                // 如果查询状态为假,则线程暂停
                if (!isQueryRunning || !isTraceOpen) {
                    try {
                        Thread.sleep(1000);
                        continue;
                    } catch (InterruptedException e) {
                        if (isStop) {
                            break;
                        }
                    }
                }
                mStartTime = System.currentTimeMillis();
                // LogUtil.d(TAG, "----query event and l3msg----start");
                if (isTesting && isCanWriteFile) {
                    if (!isPlayback) {
                        if (isCanWriteFile) {
                            mPushEventThread.pushData();
                        }

                    }
                    int port = isPlayback ? PORT_4 : PORT_2;
                    int totalPointCount = getTotalPointCount(port, true);
                    LogUtil.d(TAG, "----totalPointCount:" + totalPointCount);
                    // 事件查询
                    mDatasetBuilder.buildEventTotalValue(mDatasetBuilder.buildEvents(PORT_2, false, moduleInfo));
                    if (!ApplicationModel.getInstance().isUmpcRunning() && !isPlayback) {
                        isGpsTest = ApplicationModel.getInstance().isGpsTest();
                        mDatasetBuilder.buildNewGPSInfo(PORT_2, !isGpsTest);
                    }
                }
                queryPageParas();
                queryParamsSync(monitorParams);

                if (isTesting) {
                    if (!ApplicationModel.getInstance().isUmpcRunning()) {
                        LogUtil.d(TAG, "umpc is not running.");
                        new Thread(thread).start();
                    } else {
                        LogUtil.w(TAG, "umpc is running=" + TraceInfoInterface.currentNetType);
                        switch (TraceInfoInterface.currentNetType) {
                        case GSM:
                            queryParamsSync(PageQueryParas.PageQueryGsm);
                            break;
                        case WCDMA:
                            queryParamsSync(PageQueryParas.PageQueryWcdma);
                            break;
                        case CDMA:
                            queryParamsSync(PageQueryParas.PageQueryEvdo);
                            break;
                        case TDSCDMA:
                            queryParamsSync(PageQueryParas.PageQueryTDScdma);
                            break;
                        case LTE:
                            queryParamsSync(PageQueryParas.PageQueryLte);
                            break;
                        }
                    }
                    // twq20140717开始测试后,根据当前参数分布页设定参数,执行参数分布统计
                    mDatasetBuilder.buildDistributionParams(PORT_2);
                    if (!ApplicationModel.getInstance().isGeneralMode()) {
                        netSwitch.MonitorNet();
                    }
                }
                // LogUtil.d(TAG, "----query event and l3msg----end");
                long sleepTime = QUERY_INTERVAL_TOTAL - (System.currentTimeMillis() - mStartTime);
                try {
                    if (sleepTime > 0) {
                        if (isTesting && isCanWriteFile) {
                            if (!isPlayback) {
                                for (int i = 0; i < sleepTime; i += 100) {
                                    if (isCanWriteFile) {
                                        mPushEventThread.pushData();
                                    }
                                    Thread.sleep(100);
                                }
                            }
                        } else {
                            Thread.sleep(sleepTime);
                        }
                    } else {
                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    if (isStop) {
                        break;
                    }
                }
            } while (!isStop && datasetHandle != 0 && debugModel.isQueryEvent()); // isQueryRunning &&
            LogUtil.i(TAG, "stop query event and l3msg");

        }

        public void pauseThread() {
            this.isQueryRunning = false;
        }

        public void continueThread() {
            this.isQueryRunning = true;
            this.interrupt();
        }

        public void stopThread() {
            this.isStop = true;
            this.interrupt();
        }
    }

    /**
     * 生成参数统计值线程
     */
    private class BuildParamTotalValueThread implements Runnable {
        private boolean isTotal = false;

        private BuildParamTotalValueThread() {
        }

        @Override
        public void run() {
            if (this.isTotal)
                return;
            this.isTotal = true;
            mDatasetBuilder.buildParamTotalValue(PORT_2, moduleInfo);
            mContext.sendBroadcast(new Intent(TotalDataByGSM.TotalParaDataChanged));
            this.isTotal = false;
        }
    }

    /**
     * 当界面参数发生变化时，调用当前方法，重新计算分布参数
     *
     * @param param
     */
    public void rebuildDistributionParams(Parameter param) {
        mDatasetBuilder.rebuildDistributionParams(PORT_2, param);
    }

    /**
     * 查询指定参数列表值 同步查询,这个查询会阻塞
     *
     * @param param
     */
    public synchronized void queryParamsSync(int[] param) {
        if (this.mDatasetBuilder == null)
            return;
        // LogUtil.d(TAG,"----queryParamsSync----start----param:"+ Arrays.toString(param));
        int port = isPlayback ? PORT_4 : PORT_2;
        if (param.length > 0)
            mDatasetBuilder.buildDatasetParamQuery(port, currentIndex, param);
        // LogUtil.d(TAG,"----queryParamsSync----end----param:"+ Arrays.toString(param));
    }

    /**
     * 查询指定网络的邻区列表
     *
     * @param netType
     */
    public void queryCellSet(DataSetLib.EnumNetType netType) {
        int port = isPlayback ? PORT_4 : PORT_2;

        mDatasetBuilder.buildCellSetQuery(port, currentIndex, netType);
    }

    /**
     * 获得采样点总数<BR>
     * [功能详细描述]
     *
     * @param port      端口
     * @param isRefresh 是否更新采样点总数
     * @return 总数
     */
    private synchronized int getTotalPointCount(int port, boolean isRefresh) {
        if (isTraceOpen)
            return this.mDatasetBuilder.getTotalPointCount(port, isRefresh) - 1;
        return -1;
    }

    /**
     * 获得采样点总数<BR>
     * [功能详细描述]
     *
     * @param port 端口
     * @return 总数
     */
    public int getTotalPointCount(int port) {
        if (isPlayback)
            return this.getTotalPointCount(port, true);
        else
            return this.getTotalPointCount(port, false);
    }

    /**
     * 查层3信息，界面打开或者测试中调用此方法
     */
    private void queryL3Message() {
        // LogUtil.d(TAG,"----queryL3Message----start----");
        int count = this.getTotalPointCount(PORT_2);
        if (count < 0)
            return;
        int from = moduleInfo.lastMsgPointIndex;
        if (from < 0) {
            from = 0;
        }
        long current = System.currentTimeMillis();
//        LogUtil.d("DataSetLibykykyk", "count=" + count + ",from=" + from);
        while (true) {
            // 查询时间超过1秒则退出
            if (System.currentTimeMillis() - current > 1000) {
                moduleInfo.lastMsgPointIndex = from;
                break;
            }
            // 分段查询，每段100采样点
            if (from + 100 < count) {
                mDatasetBuilder.buildL3MsgQuery(isPlayback ? PORT_4 : PORT_2, from, from + 99, false);
                from += 100;
            } else if (from < count) {
                mDatasetBuilder.buildL3MsgQuery(isPlayback ? PORT_4 : PORT_2, from, count - 1, false);
                moduleInfo.lastMsgPointIndex = count;
                break;
            } else
                break;
        }

    }

    /**
     * 获取当前采样点
     *
     * @return
     */
    public int getCurrentIndex() {
        try {
            if (isPlayback) {
                if (mPlaybackManager != null) {
                    currentIndex = mPlaybackManager.getCurrentIndex();
                }
            } else if (!ApplicationModel.getInstance().isGeneralMode()) {
                currentIndex = getTotalPointCount(PORT_2);
            } else {
                currentIndex++;
            }

            if (isTraceOpen && currentIndex <= 0) {
                LogUtil.i(TAG, "--currentIndex:>>" + currentIndex + "--" + TraceInfoInterface.currentShowChildTab.name());
            }

            return currentIndex;
        } catch (Exception e) {
            LogUtil.w(TAG, "getCurrentIndex", e);
            return -1;
        }
    }

    /**
     * 返回组合值
     *
     * @param hightValue
     * @param lowerValue
     * @return
     */
    private long getPortfolioValue(long hightValue, long lowerValue) {
        long result = 0;
        if (hightValue > 0) {
            result = hightValue;
            result = result << 32 | lowerValue;
        } else {
            result = lowerValue;
        }

        return result;
    }

    /**
     * 开始测试<BR>开始执行业务测试时，调用此函数，开始采集有效数据
     *
     * @param fileFullPath   测试期间生成的ddib&rcu文件名（无后缀），如“20130309112233”
     * @param isDontSaveFile 是否不存文件模式
     * @param genDTLog       是否DTLog数据
     * @param deviceId       需要生成DTLog文件时用的是BoxID,不需要生成时用的是GUID
     * @param testVersion    DTLog文件里的测试计划版本号
     * @param umpcTestInfo   小背包测试时的相关信息
     * @param testRecord     测试记录对象,该对象在TestService中引用
     * @param rcuFileNum     Rcu文件序号,电信联通第三方招标时,防数据丢失按顺序生成的测试文件序号,没有时默认-1
     * @param entryptionKey  单机版加通指定路径的密码文件中获得的密钥串
     * @return 1=成功；0=失败，此时可以通过getLastError获取错误原因码
     */
    public int createFile(String fileFullPath, boolean isDontSaveFile, boolean genDTLog, String deviceId, int testVersion, UmpcTestInfo umpcTestInfo, BuildTestRecord testRecord, int rcuFileNum, String entryptionKey, boolean hasCall) {
        // 开始测试时，数据集的事件index会从0开始
        mDatasetBuilder.setBuildTestRecord(testRecord);
        if (mQueryEventsThread != null) {
            mQueryEventsThread.pauseThread();
            LogUtil.w(TAG, "--QueryEvents Create file Thr pause--");
        }
        isTesting = false;
        isTraceOpen = false;
        String filePath = fileFullPath.substring(0, fileFullPath.lastIndexOf("/") + 1);
        String fileName = fileFullPath.substring(fileFullPath.lastIndexOf("/") + 1, fileFullPath.lastIndexOf(".")) + Port2Name;
        /**
         * 2018/9/20  给每个测试添加tag文件夹
         */
        String fileAttachDir = Environment.getExternalStorageDirectory() + File.separator + "WalkTour/tag/" + fileFullPath.substring(fileFullPath.lastIndexOf("/") + 1, fileFullPath.lastIndexOf("."));// 附件的保存地址;
        WalktourConst.SAVE_ATTACH_DIR = fileAttachDir;
        FileUtil.createFileDir(fileAttachDir);
        LogUtil.w(TAG, "--createfile:" + fileFullPath + "--save file:" + !isDontSaveFile + "--deviceId:" + deviceId + "--testVersion:" + testVersion + "--num: rcuFile:" + rcuFileNum);

        byte[] dcfEncryptKey = ApplicationModel.getInstance().getDcfEncryptKey(mContext);
        LogUtil.w(TAG, "--dcfEncryptKey:" + dcfEncryptKey.length + "--Hex:" + HexConversion.bytes2HexString(dcfEncryptKey));

        // 添加文件生成
        if (!isDontSaveFile) {

            // twq20151216,如果当前有语音业务,测不判起呼原因码,否则都判起咱原因
            /**C网语音业务判起呼时，是否需要判断原因码，1表示需要，0表示不需要，默认为不需要*/
            datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_VoiceCDMAAttemptNeedOption, hasCall ? 0 : 1);

            /**生成rcu数据时，是否将GPS信息保存为鼎利通用GPS格式（RCU文件的0端口的ModelName为“DingLi GPS”），0表示不生效，1表示生效*/
            datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_OutputRCUFileUseDLGPSDevice, 1);
            /**是否处于综合测试仪的环境（信令流程可能会与现网不同）配置为1，就独立输出,配置不为1，维持现状,不配置就是维持现状*/
            datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_Decode_IsComprehensiveTestInstrument, ServerManager.getInstance(mContext).getComprehensiveTestEnvironment());
            /**是否存储5G信令，1表示存储，0表示不存储，默认为0*/
            datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_Storage5GInfo, 1);

            setConfigPropertyKey_DecodeOutputMS_MsgDetailBuffer();

            // RCU文件 注意：oppo电信巡检特殊功能，如果是小背包测试则默认不保存rcu文件
//            if (umpcTestInfo != null ? (!AppVersionControl.getInstance().isTelecomInspection() && (umpcTestInfo.getGenFileTypes() != null && umpcTestInfo.getGenFileTypes()[umpcTestInfo.GEN_FILE_RCU] == '1')) : configRoutine.isGenRCU(mContext)) {
//                datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_ProductType, 3);
//
//                if (rcuFileNum > -1) {
//                    datasetLib.configDecodeOrStorageComplexProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_Complex_RCUSecretContent, UtilsMethod.buildRucFileNum(rcuFileNum));
//                }
//                // 如果小背包测试要求加密文件时,将密钥信息配置到数据集中,此时会在生成RCU文件时加密包头,注意要放要添加RCU文件类型之前
//                // "DV-55YZD-FSLCP-POHR6-GWATY-N7U2N-HVBCL-E4Q7B-4ISNV-2XQEQ-TKMJR-4GYRD-ZXRYF-QVLJW-ROESV-CVVRQ-CBCGJ-LEQCS-EDMOR-WFDFO-RBFME-JJJFK-AKGDP-CFWXG-5ITDI-TEEQ2-LIFST-CQCGD-BLWOE-YRKIO-GMFLM");
//                if (umpcTestInfo != null && !umpcTestInfo.getEncryptCode().equals("")) {
//                    datasetLib.configDecodeOrStorageStringProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_String_SecretKeyContent, umpcTestInfo.getEncryptCode());
//                } else if (entryptionKey != null && !entryptionKey.equals("")) {
//                    // 通过本地路径获得密钥信息
//                    datasetLib.configDecodeOrStorageStringProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_String_SecretKeyContent, entryptionKey);
//                }
//
//                datasetLib.addSaveFileType(datasetHandle, DataSetLib.FileType.CPV_FileType_RCU);
//                testRecord.setRecordDetailMsg(FileType.RCU.getFileTypeName(), RecordDetailEnum.file_type.name(), FileType.RCU.getFileTypeId());
//                testRecord.setRecordDetailMsg(FileType.RCU.getFileTypeName(), RecordDetailEnum.file_path.name(), filePath);
//                testRecord.setRecordDetailMsg(FileType.RCU.getFileTypeName(), RecordDetailEnum.file_name.name(), fileName + FileType.RCU.getExtendName());
//                testRecord.setRecordDetailMsg(FileType.RCU.getFileTypeName(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());
//            }

            // DTLog文件
            if (umpcTestInfo != null ? umpcTestInfo.getGenFileTypes() != null && umpcTestInfo.getGenFileTypes()[umpcTestInfo.GEN_FILE_DTLOG] == '1' : genDTLog) {
                long lte = DataSetLib.FileType.dkDTLogLTEBin_Phone;
                if (TaskListDispose.getInstance().isWlanTest()) {
                    // 如果是wifi测试
                    lte = DataSetLib.FileType.dkDTLogWLANBin_Phone;
                }

                datasetLib.addSaveFileType(datasetHandle, (int) lte);
                testRecord.setRecordDetailMsg(FileType.DTLOG.getFileTypeName(), RecordDetailEnum.file_type.name(), FileType.DTLOG.getFileTypeId());
                testRecord.setRecordDetailMsg(FileType.DTLOG.getFileTypeName(), RecordDetailEnum.file_path.name(), filePath);
                testRecord.setRecordDetailMsg(FileType.DTLOG.getFileTypeName(), RecordDetailEnum.file_name.name(), fileName + (TaskListDispose.getInstance().isWlanTest() ? FileType.WLAN.getExtendName() : FileType.LTE.getExtendName()));
                testRecord.setRecordDetailMsg(FileType.DTLOG.getFileTypeName(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());

                // 配置场景，采集模式下，直接生成DTLOG而不生成临时文件
                datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_DatasetSceneValue, 0);

                datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_OutputFileIsDgzCompressed, getPortfolioValue(lte, 1));

                datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_OutputBeforeCompressDataBufferSize, getPortfolioValue(lte, 64));

                datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.PropertyKey_TestPlanID, testVersion);
                datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_StartCurDateTime, UtilsMethod.getLocalTimeByUTCTime() * 1000);

                // 设置缓存大小 （单位是KByte）
                final long Buffer = 512;// 8 * 1024;// 2014.6.19 新总说改成8M
                long value = (lte << 32) + Buffer;
                datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.PropertyKey_File_Buffer, value);
            }

            // DCF文件
            if (umpcTestInfo != null ? umpcTestInfo.getGenFileTypes() != null && umpcTestInfo.getGenFileTypes()[umpcTestInfo.GEN_FILE_DCF] == '1' : configRoutine.isGenDCF(mContext)) {

                if (dcfEncryptKey.length > 0) {
                    // 1.配置是否加密
                    datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_DecodeOutputDCFEncryption, 1);
                    // 2.配置密钥
                    datasetLib.configDecodeOrStorageComplexProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_Complex_DCFUserCertContent, dcfEncryptKey);
                }

                datasetLib.addSaveFileType(datasetHandle, DataSetLib.FileType.CPV_FileType_DCF);

                regObserverInfo(datasetHandle, PORT_2, DataSetLib.Register_Observer_Info_FileSize, DataSetLib.FileType.CPV_FileType_DCF);
                regObserverInfo(datasetHandle, PORT_2, DataSetLib.Register_Observer_Info_FileName, DataSetLib.FileType.CPV_FileType_DCF);
                testRecord.setRecordDetailMsg(FileType.DCF.getFileTypeName(), RecordDetailEnum.file_type.name(), FileType.DCF.getFileTypeId());
                testRecord.setRecordDetailMsg(FileType.DCF.getFileTypeName(), RecordDetailEnum.file_path.name(), filePath);
                testRecord.setRecordDetailMsg(FileType.DCF.getFileTypeName(), RecordDetailEnum.file_name.name(), fileName + FileType.DCF.getExtendName());
                testRecord.setRecordDetailMsg(FileType.DCF.getFileTypeName(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());

                datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_OutputBeforeCompressDataBufferSize, getPortfolioValue(DataSetLib.FileType.CPV_FileType_DCF, 4));
                datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_StartCurDateTime, UtilsMethod.getLocalTimeByUTCTime() * 1000);

                if (umpcTestInfo != null && ApplicationModel.getInstance().isPioneer()) {
                    // 设置实时输出org文件
                    datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_OutputDataBufferRealTime, 1);
                    // 设置缓存大小 （单位是KByte）
                    datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.PropertyKey_File_Buffer, 32);
                }
            }

            // CU文件,联通格式
            if ((umpcTestInfo != null ? umpcTestInfo.getGenFileTypes() != null && umpcTestInfo.getGenFileTypes()[umpcTestInfo.GEN_FILE_CU] == '1' : configRoutine.isGenCU(mContext)) && ApplicationModel.getInstance().showInfoTypeCu()) {
                datasetLib.addSaveFileType(datasetHandle, DataSetLib.FileType.CPV_FileType_CU);

                datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_CUSP_OperatorID, mDeviceInfo.getCUSPByNetType());
                // 如果GPS开启为DT测试,否则为CQT测试
                datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_CUSP_TestType, GpsInfo.getInstance().isJobTestGpsOpen() ? 1 : 2);

                datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_OutputGenerateRealFileName, 0);

                testRecord.setRecordDetailMsg(FileType.CU.getFileTypeName(), RecordDetailEnum.file_type.name(), FileType.CU.getFileTypeId());
                testRecord.setRecordDetailMsg(FileType.CU.getFileTypeName(), RecordDetailEnum.file_path.name(), filePath);
                testRecord.setRecordDetailMsg(FileType.CU.getFileTypeName(), RecordDetailEnum.file_name.name(), fileName + FileType.CU.getExtendName());
                testRecord.setRecordDetailMsg(FileType.CU.getFileTypeName(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());

                datasetLib.configDecodeOrStorageStringProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_String_ProductName, mContext.getString(R.string.cu_ProductName));

                datasetLib.configDecodeOrStorageStringProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_String_NetType, ApplicationModel.getInstance().getExtendInfoStr(RecordInfoKey.cu_Network.name()));

                datasetLib.configDecodeOrStorageStringProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_String_MSIISDN, ApplicationModel.getInstance().getExtendInfoStr(RecordInfoKey.cu_PhoneNum.name()));

                // ExtendsInfo extendsInfo = ApplicationModel.getInstance().getExtendsInfo();
                // //城市名称
                // datasetLib.configDecodeOrStorageStringProperty(datasetHandle, PORT_2,
                // DataSetLib.ConfigPropertyKey_String_TestCityName, extendsInfo.proviceNname + extendsInfo.cityName);
                // //测试范围
                // datasetLib.configDecodeOrStorageStringProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_String_TestRange,
                // extendsInfo.testRange);
                // //测试厂家
                // datasetLib.configDecodeOrStorageStringProperty(datasetHandle, PORT_2,
                // DataSetLib.ConfigPropertyKey_String_TestFactory, extendsInfo.testFactory);
                // //扩展信息
                // datasetLib.configDecodeOrStorageStringProperty(datasetHandle, PORT_2,
                // DataSetLib.ConfigPropertyKey_String_TestExtendInfo, extendsInfo.TestExtendInfo);

            }

            // ORG Rcu
            if (umpcTestInfo != null ? umpcTestInfo.getGenFileTypes() != null && umpcTestInfo.getGenFileTypes()[umpcTestInfo.GEN_FILE_ORGRCU] == '1' : configRoutine.isGenOrgRcu(mContext)) {

                datasetLib.addSaveFileType(datasetHandle, DataSetLib.FileType.CPV_FileType_SourceRCU);
                testRecord.setRecordDetailMsg(FileType.ORGRCU.getFileTypeName(), RecordDetailEnum.file_type.name(), FileType.ORGRCU.getFileTypeId());
                testRecord.setRecordDetailMsg(FileType.ORGRCU.getFileTypeName(), RecordDetailEnum.file_path.name(), filePath);
                testRecord.setRecordDetailMsg(FileType.ORGRCU.getFileTypeName(), RecordDetailEnum.file_name.name(), fileName + FileType.ORGRCU.getExtendName());
                testRecord.setRecordDetailMsg(FileType.ORGRCU.getFileTypeName(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());
            }
            // OTS数据格式
            if (configRoutine.isGenOTS(mContext)) {// 只在设置里起作用
                datasetLib.addSaveFileType(datasetHandle, DataSetLib.FileType.CPV_FileType_OTSPARAM);
                testRecord.setRecordDetailMsg(FileType.OTSPARAM.getFileTypeName(), RecordDetailEnum.file_type.name(), FileType.OTSPARAM.getFileTypeId());
                testRecord.setRecordDetailMsg(FileType.OTSPARAM.getFileTypeName(), RecordDetailEnum.file_path.name(), filePath);
                testRecord.setRecordDetailMsg(FileType.OTSPARAM.getFileTypeName(), RecordDetailEnum.file_name.name(), fileName + FileType.OTSPARAM.getExtendName());
                testRecord.setRecordDetailMsg(FileType.OTSPARAM.getFileTypeName(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());
            }

            // ECTI
            if (umpcTestInfo != null ? umpcTestInfo.getGenFileTypes() != null && umpcTestInfo.getGenFileTypes()[umpcTestInfo.GEN_FILE_ECTI] == '1' : configRoutine.isGenECTI(mContext)) {

                datasetLib.addSaveFileType(datasetHandle, DataSetLib.FileType.CPV_FileType_ECTI);
                // 只有ECTI才需要获取文件名
                int backVal = regObserverInfo(datasetHandle, PORT_2, DataSetLib.Register_Observer_Info_FileName, DataSetLib.FileType.CPV_FileType_ECTI);// 8表示目标文件的实际文件名（目前只有CTI、CU）
                // 这里不入库,因为eCTI数据格式的文件名由so库返回,停止测试是入库.

                // 福建电信需求ECTI压缩数据
                datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_OutputFileIsDgzCompressed, getPortfolioValue(DataSetLib.FileType.CPV_FileType_ECTI, 1));
            }
            // 设置是否以内存映射的方式生成RCU等文件
            datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_OutputWriterType, (configRoutine.isMemoryMepped(mContext) ? 1 : 0));

            datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_StorageModuleFlushAfterWriteFile, 0);

            // 文件头设备ID
            datasetLib.configDecodeOrStorageStringProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_String_DeviceID, deviceId);
            // OTSParam
            if (HttpServer.getInstance(mContext).isCellCheck) {
                datasetLib.addSaveFileType(datasetHandle, DataSetLib.FileType.CPV_FileType_OTSPARAM);
            }

            testRecord.setRecordDetailMsg(FileType.DDIB.getFileTypeName(), RecordDetailEnum.file_type.name(), FileType.DDIB.getFileTypeId());
            testRecord.setRecordDetailMsg(FileType.DDIB.getFileTypeName(), RecordDetailEnum.file_path.name(), ConfigRoutine.getInstance().getStorgePathDdib());
            testRecord.setRecordDetailMsg(FileType.DDIB.getFileTypeName(), RecordDetailEnum.file_name.name(), fileName + FileType.DDIB.getExtendName());
            testRecord.setRecordDetailMsg(FileType.DDIB.getFileTypeName(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());
        }

        // 数据集日志
        if (ConfigRoutine.getInstance().isOpenDataSetLog(mContext)||Deviceinfo.getInstance().isOpenDataSetLog()) {
            LogUtil.w(TAG, "--create dataset log file--");
            String logPath = AppFilePathUtil.getInstance().getSDCardBaseDirectory(mContext.getString(R.string.path_log));
            int val1 = datasetLib.configDecodeOrStorageStringProperty(datasetHandle, PORT_2, DataSetLib.PropertyKey_Log_Path, logPath);
            LogUtil.w(TAG, "--create dataset log file path is:" + logPath);
            int val2 = datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.FileType.CPV_FileType_LOG, 1);
            LogUtil.w(TAG, "--create dataset log file--val1=" + val1 + ",val2=" + val2);
        }

        // 如果调试模式开关为开关的状态,才需根据设置处理相关开关
        if (debugModel.isDebugModel()) {
            // 根据开关设置是否往数据集压数据
            if (!debugModel.isPushDataSet()) {
                configPushToDataSet(debugModel.isPushDataSet());
            } else {
                if (debugModel.isOnlyFrame()) {
                    datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.PropertyKey_Debug_Model, DataSetLib.CPV_DecodeMode_OnlyParse);
                } else if (debugModel.isOnlyDecoder()) {
                    datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.PropertyKey_Debug_Model, DataSetLib.CPV_DecodeMode_OnlyDecode);
                } else if (debugModel.isDecoderAndRead()) {
                    datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.PropertyKey_Debug_Model, DataSetLib.CPV_DecodeMode_DecodeAndReadFromDecoder);
                } else if (debugModel.isEventJudge()) {
                    datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.PropertyKey_Debug_Model, DataSetLib.CPV_DecodeMode_DoLogic);
                } else if (debugModel.isDebugNormal()) {
                    datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.PropertyKey_Debug_Model, DataSetLib.CPV_DecodeMode_Normal);
                }
            }
        } else {
            configPushToDataSet(true);
        }

        // 根据/sdcard/walktour/data/encryFile.dig文件是否存在决定是否需要对ddib进行加密
        // if (configRoutine.getEncryKey(mContext) != null) {
        // twq20151104根据小背包是否下发密钥信息,或者"/sdcard/walktour/config/dingli.wskf" 目录下的密钥文件存在则对ddib进行加密
        if (umpcTestInfo != null && !umpcTestInfo.getEncryptCode().equals("") || entryptionKey != null && !entryptionKey.equals("") || dcfEncryptKey.length > 0) {
            datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.Property_Encry_DDIBFile_Key, DataSetLib.Property_Encry_DDIBFile_Yes);
        }

        regObserverInfo(datasetHandle, PORT_2, DataSetLib.Register_Observer_Info_FileSize, DataSetLib.FileType.CPV_FileType_RCU);

        datasetLib.setThirdPropertyValue(datasetHandle, configRoutine.getAuthValueStr(mContext), MyPhoneState.getInstance().getDeviceId(mContext));

        if (configRoutine.isAbnormal(mContext)) {
            LogUtil.w(TAG, "--file is Abnormal--");
            datasetLib.setEnvironmentProperty2(datasetHandle, 1);
        }

        TraceInfoInterface.traceData.l3MsgList.clear();
        mDatasetBuilder.resetTotalInfo();
        int startResult = datasetLib.startTest(datasetHandle, fileFullPath);
        moduleInfo.resetPonintIndex();
        LogUtil.w(TAG, "--createfile result:" + startResult);
        isTesting = true;

        if (mQueryEventsThread != null) {
            mQueryEventsThread.continueThread();
            LogUtil.w(TAG, "--QueryEvents Create file Thr continue--");
        }
        moduleInfo.lastTraceTime = currentTimeMillis();
        moduleInfo.lastTraceIndex = 0;
        isTraceOpen = true;
        this.isCanWriteFile = true;
        return startResult;
    }

    /**
     * 结束测试<BR>
     * 测试完成之后，调用此函数，停止记录解码数据，并生成文件
     *
     * @return 1=成功；0=失败，此时可以通过getLastError获取错误原因码
     */
    public int closeFile() {
        mDatasetBuilder.setBuildTestRecord(null);
        if (mQueryEventsThread != null) {
            mQueryEventsThread.pauseThread();
        }
        this.isCanWriteFile = false;
        isTesting = false;
        int result = 0;
        try {
            mPushEventThread.pushData();// 将缓存中的数据全部压入数据集再停止测试
            result = datasetLib.stopTest(datasetHandle);
            WalktourApplication.refreshFile();
            // 停止测试后还原Logmask,如果没停止就不要切换了.
            if (ApplicationModel.getInstance().isTestInterrupt()) {
                changeToNormalLogMask();
            }
        } catch (Exception e) {
            LogUtil.d(TAG, e.getMessage(), e);
        }
        moduleInfo.resetPonintIndex();
        if (mQueryEventsThread != null) {
            mQueryEventsThread.continueThread();
        }
        return result;
    }

    /**
     * 函数功能： 业务中暂停保存文件//业务开始时默认是记录文件的
     *
     * @param bSave False: 暂停记录文件；True: 记录文件
     * @return 成功>=0 ; 失败<0
     */
    public int setSaveFile(boolean bSave) {
        LogUtil.w(TAG, "--setSaveFile:" + bSave);
        return datasetLib.setSaveFile(datasetHandle, PORT_2, bSave);
    }

    /**
     * 函数功能： 获取指定采样点所处的业务类型列表 仅回放中使用
     *
     * @return 返回值 ： 业务类型格式串，例：3@@01##02##03
     */
    public int configPushToDataSet(boolean bSave) {
        LogUtil.w(TAG, "--configPushToDataSet:" + bSave);
        return datasetLib.configPushToDataSet(datasetHandle, bSave);
    }

    /**
     * 暂停导出、切换文件
     *
     * 当设置为1是，参数和信令都不刷新，设置为3时恢复刷新
     *
     * @param CommandType 1--暂停导出 2--截断文件 3--恢复导出
     * @param isSaveDdib 0--对ddib不起作用(ddib仍然会保存数据)  1-对ddib起作用(ddib是否保存数据,取决于)
     * @return
     */
    public void setCommand(int CommandType, int isSaveDdib) {
        LogUtil.w(TAG, "--setCommand:" + CommandType);

//        datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_OutputDdibFileInfo, isSaveDdib);

        datasetLib.setCommand(datasetHandle, CommandType, "",0);
    }

    /**
     * 函数功能：从rcu转换到其它文件格式
     *
     * @param rcuFile 源文件,目前只是rcu文件
     * @param desFile 目标文件,dtlog ddib文件
     * @return 0, 转换成功, 1,正在测试 2,正在回放
     */
    public int converRcuFile(File rcuFile, File desFile, int fileType) {
        if (isTesting) {
            return 1;
        } else if (isPlayback) {
            return 2;
        }

        int result = datasetLib.rcuConverter(datasetHandle, rcuFile.getAbsolutePath(), desFile.getAbsolutePath(), fileType, rcuFile.getParent(), rcuFile.getParent());

        return result;
    }

    /**
     * 查询L3详细信息
     */
    public String queryL3Detail(int pointIndex) {
        int port = PORT_2;
        if (isPlayback) {
            port = PORT_4;
        } else {
            port = PORT_2;
        }
        if (debugModel.isDetailDecoder()) {
            return mDatasetBuilder.getL3Detail(port, pointIndex);
        }
        return "";
    }

    /**
     * 压数据给数据集<BR>
     *
     * @param flag   类型标识，比如'D','E'等
     * @param type   轨迹点类型 -1 Del 0 为室内ADD打点 1为 GPS打点（仅当flag为'D'时有效）
     * @param port   如果是0，代表GPS，如果多模块的话，需要传递不同的值
     * @param time   数据包时间，到1970年1月1日0：0：0秒的微秒数
     * @param buffer 数据包缓存
     * @param size   数据包缓存大小（buffer的实际长度，不包括flag，module，time）
     * @return 1=成功；0=失败，此时可以通过getLastError获取错误原因码
     * @history // 2013.11.32 不能用线程来做，否则会导致事件写入顺序不当,先改成这样看看会不会卡 // 2014.4.23
     * qihang.li 不在线程里写入数据，会导致阻塞UI线程界面卡，这里要用线程来写 // 并且执行写入加上同步锁
     */
    public void pushData(final int flag, final int type, final int port, final long time, final byte[] buffer, final int size) {
        if (!this.isCanWriteFile)
            return;
        this.pushData(flag, type, port, time, buffer, size, true);
    }

    /**
     * 压采样点给数据集<BR>
     *
     * @param port   如果是0，代表GPS，如果多模块的话，需要传递不同的值
     * @param time   数据包时间，到1970年1月1日0：0：0秒的微秒数
     * @param buffer 数据包缓存
     * @param size   数据包缓存大小（buffer的实际长度，不包括flag，module，time）
     * @return 1=成功；0=失败，此时可以通过getLastError获取错误原因码
     * @history // 2013.11.32 不能用线程来做，否则会导致事件写入顺序不当,先改成这样看看会不会卡 // 2014.4.23
     * qihang.li 不在线程里写入数据，会导致阻塞UI线程界面卡，这里要用线程来写 // 并且执行写入加上同步锁
     */
    public void pushPointData(final int port, final long time, final byte[] buffer, final int size) {
        if (!this.isCanWriteFile)
            return;
        mPushEventThread.pushPoint(mDatasetBuilder, datasetHandle, port, time, buffer, size);
    }

    /**
     * 压数据给数据集<BR>
     *
     * @param flag        类型标识，比如'D','E'等
     * @param type        轨迹点类型 -1 Del 0 为室内ADD打点 1为 GPS打点（仅当flag为'D'时有效）
     * @param port        如果是0，代表GPS，如果多模块的话，需要传递不同的值
     * @param time        数据包时间，到1970年1月1日0：0：0秒的微秒数
     * @param buffer      数据包缓存
     * @param size        数据包缓存大小（buffer的实际长度，不包括flag，module，time）
     * @param isAddNewGPS 是否根据新加的点重新生成事件
     * @return 1=成功；0=失败，此时可以通过getLastError获取错误原因码
     * @history // 2013.11.32 不能用线程来做，否则会导致事件写入顺序不当,先改成这样看看会不会卡 // 2014.4.23
     * qihang.li 不在线程里写入数据，会导致阻塞UI线程界面卡，这里要用线程来写 // 并且执行写入加上同步锁
     */
    public void pushData(final int flag, final int type, final int port, final long time, final byte[] buffer, final int size, boolean isAddNewGPS) {
        if (!this.isCanWriteFile)
            return;
        mPushEventThread.pushEvent(mDatasetBuilder, datasetHandle, flag, type, port, time, buffer, size, isAddNewGPS, currentIndex);
    }

    /**
     * 将外部数据写入数据集环境中
     *dd
     * @param buffer
     */
    public void pushExternalTraceData(byte[] buffer) {
        pushExternalTraceData(buffer, PORT_3);
    }

    /**
     * 将外部数据写入数据集环境中
     *
     * @param buffer
     */
    private void pushExternalTraceData(byte[] buffer, int port) {
        if (!this.isCanWriteFile)
            return;
        int pushScannerInt = datasetLib.pushExternalTraceData(datasetHandle, port, currentTimeMillis() * 1000, buffer, buffer.length);
        Log.i(TAG, "ExternalTraceData: " + pushScannerInt + "--port:" + port);
    }

    /**
     * 打开文件回放<BR>
     * 逻辑： 1、先打开回放文件 2、根据返回结果,成功则加载GPS轨迹 注：这边可能还有其他数据需要一次性加载的,均可以在此初始化
     *
     * @param port     端口号
     * @param filepath 目标ddib文件全路径，如“/data/local/ddib/20130309.ddib”
     * @return 1=成功；0=失败，此时可以通过getLastError获取错误原因码
     */
    public boolean openPlayback(int port, String filepath) {
        return openPlayback(port, filepath, -1, -1);
    }

    /**
     * 打开文件回放<BR>
     * 逻辑： 1、先打开回放文件 2、根据返回结果,成功则加载GPS轨迹 注：这边可能还有其他数据需要一次性加载的,均可以在此初始化
     *
     * @param port       端口号
     * @param filepath   目标ddib文件全路径，如“/data/local/ddib/20130309.ddib”
     * @param startPoint 如果有指定开始采样点,那么加载事件从指定采样点开始	未指定为-1
     * @param endPoint   如果有结束采样点,加载事件到指定位置结束,未指定为-1
     * @return 1=成功；0=失败，此时可以通过getLastError获取错误原因码
     */
    public boolean openPlayback(int port, String filepath, int startPoint, int endPoint) {
        // datasetLib.closeTrace(datasetHandle, PORT_2);
        // closeTrace(PORT_2);
        // datasetLib.closeDecoder(datasetHandle);
        // datasetLib.delMoudle(datasetHandle, PORT_2);
        isPlayback = true;
        int flag = datasetLib.openPlayback(datasetHandle, port, filepath);
        TraceInfoInterface.traceData.l3MsgList.clear();
        LogUtil.i("DataSetLibJni", "datasetLib.openPlayback:" + flag);
        if (flag == 1) {
            // 2013.10.23
            // 注意查询的次序:
            // 1.查事件(生成告警)
            // 2.查层三(生成自定义事件--->生成告警)
            // 3.最后查GPS点( 修改告警里的地图弹出信息 )
            // 最好修改一下查询方法，限制为上述次序
            mDatasetBuilder.buildEvents(isPlayback ? PORT_4 : PORT_2, true, moduleInfo, startPoint, endPoint);
            int l3Index = 0;
            // twq20161020如果指定开始采样点大于默认值,设定开始采样点
            if (startPoint > l3Index) {
                l3Index = startPoint;
            }
            int pointIndexTotal = sInstance.getTotalPointCount(PORT_4);
            // twq20161020如果结束采样点小于最大采样点,指定结束采样
            if (endPoint != -1 && endPoint < pointIndexTotal) {
                pointIndexTotal = endPoint;
            }

            // 分段查询曾三信令,目前是1000个采样点查询一次
            while (l3Index != (pointIndexTotal - 1)) {
                int from = (l3Index + 1000) >= (pointIndexTotal - 1) ? (pointIndexTotal - 1) : (l3Index + 1000);
                mDatasetBuilder.buildL3MsgQuery(port, l3Index, from, true);
                l3Index = from;
            }

            // getSpecialParam(pointIndexTotal,UnifyParaID.L_SRV_RSRP);
            // twq20161020 GPS构建不是通过采样点指定的,就不去暂无法通过指定采样点查询
            mDatasetBuilder.buildAllGPSInfoQuery(PORT_4);
        }
        return flag == 1;
    }

    /*
     * private void getSpecialParam(int toIndex,int id){ int from = 0; while(from < toIndex){ String param2 =
     * datasetLib.getRealParam(datasetHandle, PORT_4, UnifyParaID.L_SRV_RSRP, from, from + 1000, false, false);
     * LogUtil.w(TAG, "--params:" + from + "-to:" + (from + 1000) + "-v:" + param2); from += 1001; } }
     */

    public void openPlaybackData(int port, String filepath) {
        isPlayback = true;
        // datasetLib.closeTrace(datasetHandle, PORT_2);
        // closeTrace(PORT_2);
        // datasetLib.closeDecoder(datasetHandle);
        // datasetLib.delMoudle(datasetHandle, PORT_2);
        datasetLib.openPlayback(datasetHandle, port, filepath);
    }

    /**
     * 结束回放<BR>
     * [功能详细描述]
     *
     * @param port 端口号
     * @return 回放过程结束时调用此接口
     */
    public boolean closePlayback(int port) {
        isPlayback = false;
        // startDataSet(false);
        return datasetLib.closePlayback(datasetHandle, port) == 1;
    }

    public void reBuildGPSInfo(final Parameter parameter) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                mDatasetBuilder.reBuildGpsInfo(parameter, isPlayback ? PORT_4 : PORT_2);
            }
        });
    }

    /**
     * 查询参数值信息（实际值）<BR>
     * [功能详细描述]
     *
     * @param port
     * @param param      参数ID
     * @param fromIndex  起始采样点索引
     * @param toIndex    终止采样点索引
     * @param filter     非继承值（即实际解码值），false=继承值
     * @param statistics 是否过滤无效值
     * @return 字符串，格式如“value1@@value2@@value3@@value4...”
     */
    public String getRealParam(int port, int param, int fromIndex, int toIndex, boolean filter, boolean statistics) {
        // 防止因采样点数据过多导致应用无响应特设置最大采样点范围
        if (toIndex - fromIndex > 100)
            fromIndex = toIndex - 100;
        return datasetLib.getRealParam(datasetHandle, port, param, fromIndex, toIndex, filter, statistics);
    }

    /**
     * 批量查询参数值信息（实际值）
     * <p>
     * 说明 ：这个函数也适合GSM邻小区和TD邻小区的数据请求（每个小区的值都有key）
     *
     * @param pParamKeyList 参数ID列表
     * @param iListCount    参数个数
     * @param iPointIndex   采样点索引
     * @param bFilter       true=非继承值（即实际解码值），false=继承值
     * @return 字符串，格式如“value1@@value2@@value3@@value4...”
     */
    public String batchGetRealParam(int[] pParamKeyList, int iListCount, int iPointIndex, boolean bFilter) {
        return datasetLib.batchGetRealParam(datasetHandle, isPlayback ? PORT_4 : PORT_2, pParamKeyList, iListCount, iPointIndex, bFilter);
    }

    /**
     * 函数功能： 获取信令编码
     *
     * @param iFromPointIndex 起始采样点索引
     * @param iToPointIndex   终止采样点索引
     * @param bIsUTCTime      true=UTC时间，false=非UTC时间
     * @param bIsHandsetTime  true=信令时间，false=电脑时间
     * @return 字符串，格式如“PointIndex1@@time1@@code1@@direction1##PointIndex2@@time2@@code2@@direction2
     * ...”
     */
    public String getMsgCode(int iFromPointIndex, int iToPointIndex, boolean bIsUTCTime, boolean bIsHandsetTime) {
        return datasetLib.getMsgCode(datasetHandle, isPlayback ? PORT_4 : PORT_2, iFromPointIndex, iToPointIndex, bIsUTCTime, bIsHandsetTime);
    }

    /**
     * 获取参数随机点<BR>
     * 根据起始采样点与结束采样点,获取CQT Mark点之间的随机参数点
     *
     * @param param     参数ID
     * @param fromIndex 起始采样点
     * @param toIndex   结束采样点
     * @return 随机参数数组
     */
    public String[] getCQTMapEventPoint(int param, int fromIndex, int toIndex) {
        String result = this.getRealParam(isPlayback ? PORT_4 : PORT_2, param, fromIndex, toIndex, true, true);
        if (!StringUtil.isNullOrEmpty(result)) {
            String params[] = result.split("@@");
            return params;
        }
        return new String[] {};
    }

    /**
     * @return the mPlaybackManager
     */
    public PlaybackManager getPlaybackManager() {
        return mPlaybackManager;
    }

    /**
     * @param playbackManager the mPlaybackManager to set
     */
    public void setPlaybackManager(PlaybackManager playbackManager) {
        this.mPlaybackManager = playbackManager;
    }

    /**
     * @return the moduleInfo
     */
    public ModuleInfo getModuleInfo() {
        return moduleInfo;
    }

    /**
     * 添加采样点回调<BR>
     * 采样点变化通知所有注册该接口的界面
     *
     * @param linstener 采样点监听事件
     */
    public void addPointIndexChangeListener(PointIndexChangeLinstener linstener) {
        if (pointIndexChangeLinsteners == null) {
            pointIndexChangeLinsteners = new ArrayList<>();
        }
        synchronized (pointIndexChangeLinsteners) {
            if (!pointIndexChangeLinsteners.contains(linstener)) {
                pointIndexChangeLinsteners.add(linstener);
            }
        }
    }

    /**
     * 移除采样点监听事件<BR>
     * [功能详细描述]
     *
     * @param listener 采样点监听事件
     */
    public void removePointIndexChangeListener(PointIndexChangeLinstener listener) {
        if (pointIndexChangeLinsteners != null) {
            synchronized (pointIndexChangeLinsteners) {
                if (pointIndexChangeLinsteners.contains(listener)) {
                    pointIndexChangeLinsteners.remove(listener);
                }
            }
        }
    }

    /**
     * 通知界面采样点更改<BR>
     * [功能详细描述]
     *
     * @param pointIndex
     * @param isProgressSkip 是否进度条触发跳转
     */
    public void notifyPointIndexChange(int pointIndex, boolean isProgressSkip) {
        if (pointIndexChangeLinsteners != null) {
            for (PointIndexChangeLinstener linstener : pointIndexChangeLinsteners) {
                linstener.onPointIndexChange(pointIndex, isProgressSkip);
            }
        }
    }

    /**
     * 自定义导出文件
     */
    public void customExportFile(ArrayList<String> exportFileName, ArrayList<String> headMsg, int iSplitSize, ArrayList<Integer> config, int from, int to, long[] jMsgIDs, int MsgIDCount) {
        Log.i(TAG, "DatasetManager OTS export");
        this.mDatasetBuilder.customExportFile(isPlayback ? PORT_4 : PORT_2, exportFileName, headMsg, iSplitSize, config, from, to, jMsgIDs, MsgIDCount);

    }

    /**
     * 自定义导出获取采样点总数，此处不考虑是否串口打开，因为是回放<BR>
     * [功能详细描述]
     *
     * @param port
     * @return
     */
    public int getTotalExportPointCount(int port) {
        return datasetLib.getTotalPointCount(datasetHandle, port) - 1;

    }

    /**
     * 参数自定义导出文件
     */

    public void parmCustomExportFile(String exportFilePath, int config, int fromPointIndex, int toPointIndex, int splitSize, long[] msgIDs, int msgIDCount, long[] paramIDs) {
        Log.i(TAG, "File Custom export");
        this.mDatasetBuilder.parmCustomExportFile(isPlayback ? PORT_4 : PORT_2, exportFilePath, config, fromPointIndex, toPointIndex, splitSize, msgIDs, msgIDCount, paramIDs);
    }

    /**
     * 通过采样点,获取采样点时间
     */
    public long getPointTime(int pointIndex) {

        return datasetLib.getPointTime(datasetHandle, isPlayback ? PORT_4 : PORT_2, pointIndex, true, false);

    }

    /**
     * 通过时间获取采样点
     */

    public int getPointIndexFromTime(long iTime) {
        return datasetLib.getPointIndexFromTime(datasetHandle, isPlayback ? PORT_4 : PORT_2, iTime);
    }

    /**
     * 配置LTE帧结构和参数采样周期
     */
    public int configDecodeProperty(boolean isOts) {
        return configDecodeProperty(isOts, datasetHandle);
    }

    /**
     * 设置GPS漂移过滤（精度更高）
     * @param isCheckGPSDrift
     * @return
     */
    public int ConfigPropertyKeyIsCheckGPSDrift(boolean isCheckGPSDrift) {
        return datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_IsCheckGPSDrift, isCheckGPSDrift ? 1 : 0);
    }

    /**
     * 配置LTE帧结构和参数采样周期
     */
    private int configDecodeProperty(boolean isOts, int configHandle) {

        // 设置不保存复杂帧结构
        // {0x80000, 0x80001, 0x80002, 0x80003, 0x80004}
        if (!configDataAcquisition.getschInfo()) {
            datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.PropertyKey_Non_Storge, 0x80000);
        }
        if (!configDataAcquisition.getRLCPDUInfo()) {
            datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.PropertyKey_Non_Storge, 0x80005);
        }
        if (!configDataAcquisition.getMACPDUInfo()) {
            datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.PropertyKey_Non_Storge, 0x80006);
        }
        if (!configDataAcquisition.getML1Info() && !configRoutine.isVoLTEQCIInfo(mContext)) {
            datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.PropertyKey_Non_Storge, 0x80007);
        }
        if (!configDataAcquisition.getPDCPPDUInfo()) {
            datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.PropertyKey_Non_Storge, 0x80008);
        }

        if (!configDataAcquisition.getgrantInfo()) {
            datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.PropertyKey_Non_Storge, 0x80001);
        }
        if (!configDataAcquisition.getmcsStatis() && !configRoutine.isVoLTEQCIInfo(mContext)) {
            datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.PropertyKey_Non_Storge, 0x80002);
        }
        if (!configDataAcquisition.getdciInfo()) {
            datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.PropertyKey_Non_Storge, 0x80003);
        }
        if (!configDataAcquisition.getrrbInfo()) {
            datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.PropertyKey_Non_Storge, 0x80004);
        }

        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_StatisticMethod, 1);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_ServiceProvider, 3);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_MessageLostInterval, 999999);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_FTPDropMode, 1);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_UserStopMode, 0);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_TimeOutMode, 1);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_DeviceLostMode, 0);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_PPPDropMode, 0);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_VoiceIgnoreDevoceLost, 1);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_AbandonNoVoiceDialMOSP, 0);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_BlockCallTimeout, 30);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_VoiceDropByExceptionCodeOfDownDisconnectMsg, 0);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_NoRCUEventVoiceSPMode, 0);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_AbandonHaveAlertBeforeBlockSP, 0);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_AbandonSpecialReasonExceptVoiceSP, 0);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_Email70543DropMode, 0);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_VoLTECallDropWhenNoEndMessage, 1);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_VoLTECallEndCheckByeOKDirection, 0);
        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_LastCallNotInclude, 1);

        datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Storage5GInfo, 1);
        // datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_Logic_InviteBidVersion,
        // 1);
        /**
         * 地铁或高铁设置为不过滤GPS漂移
        * @anthor jinfeng.xie
         * @time 2018/9/29
        */
        if (ApplicationModel.getInstance().getSelectScene() == WalkStruct.SceneType.HighSpeedRail || ApplicationModel.getInstance().getSelectScene() == WalkStruct.SceneType.Metro) {
            datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_IsCheckGPSDrift, 0);
        } else {
            datasetLib.configDecodeOrStorageProperty(configHandle, PORT_2, DataSetLib.ConfigPropertyKey_IsCheckGPSDrift, 1);
        }
        try {
            ArrayList<DataAcquisitionModel> dataAcquisitionModels = configDataAcquisition.getDataModelList();
            DataAcquisitionModel sampleModel = null;
            if (configDataAcquisition.getisPara() && !isOts) {
                return configDecodeProperty(PORT_2, dataAcquisitionModels, DataSetLib.PropertyKey_Filter_Interval);
            } else if (PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(WalktourConst.SYS_SETTING_OTS_TEST, false) && isOts) {
                ArrayList<DataAcquisitionModel> otsDataSamples = new ArrayList<DataAcquisitionModel>();
                for (int i = 0; i < dataAcquisitionModels.size(); i++) { // 过滤掉W网参数与C网参数
                    String showName = dataAcquisitionModels.get(i).getShowName().toLowerCase(Locale.getDefault());
                    if (!showName.startsWith("w") && !showName.startsWith("c")) {
                        sampleModel = new DataAcquisitionModel();
                        sampleModel.setId(dataAcquisitionModels.get(i).getId());
                        sampleModel.setShowName(dataAcquisitionModels.get(i).getShowName());
                        sampleModel.setTimeInterval(String.valueOf(PreferenceManager.getDefaultSharedPreferences(mContext).getInt(WalktourConst.SYS_SETTING_OTS_SAMPLE_INTERVAL, 500)));
                        otsDataSamples.add(sampleModel);
                    }
                }
                Log.i(TAG, "---configLenght---" + otsDataSamples.size());
                return configDecodeProperty(PORT_2, otsDataSamples, DataSetLib.PropertyKey_Filter_Interval);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    private int configDecodeProperty(int port, ArrayList<DataAcquisitionModel> configModel, int PropertyKey) {
        long propertyValue = 0;
        int successCode = -1;
        if (PropertyKey == 1003) {
            for (int i = 0; i < configModel.size(); i++) {
                try {
                    propertyValue = (Long.parseLong(configModel.get(i).getId().replace("0x", ""), 16) << 32) + Long.parseLong(configModel.get(i).getTimeInterval(), 10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Log.i(TAG, "--------------" + propertyValue);
                successCode = datasetLib.configDecodeOrStorageProperty(datasetHandle, port, PropertyKey, propertyValue);
            }
        }
        return successCode;
    }

    /**
     * 函数功能： 获取事件 说明 ： 在特殊情况下，比如MOS的时候，算分的事件会滞后 返回的eventFlag是只有低4位的，比如
     * Ping_Start对应的值为 0x0190 所以在和本地进行比较的时候，需要将本地的eventFlag取低4位进行比较
     *
     * @param iFromEventIndex 起始事件索引
     * @param iToEventIndex   终止事件索引
     * @param bIsUTCTime      true=UTC时间，false=非UTC时间
     * @param iTimeType       0=电脑时间，1=信令时间，2=事件时间
     * @return 字符串，返回值，格式如“time1@@eventIndex1@@eventFlag1@@pointindex1##time2@@eventIndex2@@eventFlag2@@pointindex2
     * ...”
     */
    public String getEvent(int iFromEventIndex, int iToEventIndex, boolean bIsUTCTime, int iTimeType) {
        return datasetLib.getEvent(datasetHandle, isPlayback ? PORT_4 : PORT_2, iFromEventIndex, iToEventIndex, bIsUTCTime, iTimeType);
    }

    /**
     * 获得当前生成的ddib路径
     *
     * @return
     */
    public String getDecodedIndexFileName(int port) {
        return datasetLib.getDecodedIndexFileName(datasetHandle, port);
    }

    /**
     * 函数功能： 获取存在业务过程类型标识
     *
     * @param spType 0所有业务，1只包含应用层业务，2只包含信令层业务
     * @return 返回字符串 "4097@@4098" Common_SP_ID_MO = 0x1001, //主叫 Common_SP_ID_MT
     * = 0x1002, //被叫
     */
    public String getExistSPList(int spType) {
        return datasetLib.getExistSPList(datasetHandle, isPlayback ? PORT_4 : PORT_2, spType);
    }

    /**
     * 函数功能： 获取指定类型业务个数
     */
    public int getAppointSPCount(int spid) {
        return datasetLib.getAppointSPCount(datasetHandle, isPlayback ? PORT_4 : PORT_2, spid);
    }

    /**
     * 函数功能： 获取指定类型业务个数
     */
    public long getSPBaseInfoValue(int spid, int index) {
        return datasetLib.getSPBaseInfoValue(datasetHandle, isPlayback ? PORT_4 : PORT_2, spid, index, 2);
    }

    /**
     * 函数功能： 计算业务过程属性值主 此方法没有反回值,但需要执行后方可调后面的方法
     *
     * @param spid           业务ID
     * @param conditionKey   APP_SP_Sta_t为前缀定义的常量,参考《业务过程属性(按业务过程分类).xlsx》，必须指定(csfb: 84)
     *                       APP_SP_Stat_CalcCSFBInfo(84)
     * @param conditionValue 有些APP_SP_Stat_需要指定值，一般情况下为-9999
     * @return 成功返回1
     */
    public int calcSPSinglePropertyValue(int spid, int conditionKey, int conditionValue) {
        return datasetLib.calcSPSinglePropertyValue(datasetHandle, isPlayback ? PORT_4 : PORT_2, spid, conditionKey, conditionValue);
    }

    /**
     * 函数功能： 获取业务过程属性值（非字符串）
     *
     * @param index      业务ID
     * @param index
     * @param propertyId SP_Property_ID_CSFBExceptionReason(173)返回:CSFB异常分析，0：未配置CSFB，1
     *                   ：超时，2：配置网络与起呼网络不一致
     *                   SP_Property_ID_ReturnToLTEFailureReason(174)返回:Return Back To
     *                   LTE失败原因，0：TAUReject，1：TAUTimeout 当前方法要根据错误类类多次调用
     * @return -1获取失败
     */
    public double getSPPropertyDoubleValue(int spid, int index, int propertyId) {
        return datasetLib.getSPPropertyDoubleValue(datasetHandle, isPlayback ? PORT_4 : PORT_2, spid, index, propertyId);
    }

    /**
     * 函数功能： 获取业务过程属性值（字符串）
     */
    public String getSPPropertyStringValue(int spid, int index, int propertyId) {
        return datasetLib.getSPPropertyStringValue(datasetHandle, isPlayback ? PORT_4 : PORT_2, spid, index, propertyId);
    }

    /**
     * 函数功能： 业务详情类区间信息
     *
     * @param spid  业务ID
     * @param index SPID的第几个实例（下标从0开始）
     * @param bStat 标示StartPoint和EndPoint是否是按统计规则下的采样点
     *              StartPoint：按bStat的值，返出相应的采样点 EndPoint： 按bStat的值，返出相应的采样点
     * @return 返回字符串 "StartPoint@@EndPoint"
     */
    public String getSPRangeInfo(int spid, int index, boolean bStat) {
        return datasetLib.getSPRangeInfo(datasetHandle, isPlayback ? PORT_4 : PORT_2, spid, index, bStat);
    }

    /**
     * 函数功能： 一个业务详情类实例存在多少个事件
     *
     * @param spid  SPID的第几个实例（下标从0开始）
     * @param Index 事件个数
     * @return 返回 COUNT
     */
    public int getSPEventCount(int spid, int Index) {
        return datasetLib.getSPEventCount(datasetHandle, isPlayback ? PORT_4 : PORT_2, spid, Index);
    }

    /**
     * 函数功能： 一个业务详情类实例所有事件的EventIndex
     *
     * @param spid  业务ID
     * @param Index SPID的第几个实例（下标从0开始）
     *              EventIndexs：数组首地址，数组长度由GetSPEventCount()得到，调用者负责创建释放
     *              获取每个事件的EvnetIndex后，通过调用事件相关函数，可获取更多的事件信息
     * @return 成功返回1
     */
    public String getSPEventIndexList(int spid, int Index) {
        return datasetLib.getSPEventIndexList(datasetHandle, isPlayback ? PORT_4 : PORT_2, spid, Index);
    }

    /**
     * 函数功能：转换上次软件异常停止测试（崩溃，关机）的临时文件 此函数在启动数据集完成之后执行
     *
     * @param tempFilePath
     */
    public void convertTempFile(final String tempFilePath) {
        long startTime = currentTimeMillis();
        do {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (currentTimeMillis() - startTime > 30 * 1000) {
                break;
            }
        } while (!hasStarted);

        if (hasStarted) {
            datasetLib.genOtherFormatRealFileFromTempFile(datasetHandle, PORT_2, tempFilePath);
        }

    }

    /**
     * FMT文件格式转换DDIB return -1不成功
     * <p>
     * 文件格式转换流程:
     * 1.InitHandle
     * 2.fileConverterInit
     * 3.config_decoder_storage_property
     * 4.fileConverter
     * 5.FreeHandle
     */
    public int fileConverterDdib(String szSrcPath, String desPath) {
        String appPath = mContext.getFilesDir().getParentFile().getAbsolutePath();
        int initHandle = datasetLib.initHandle(appPath + "/files/config/", appPath + "/lib/"); // 1

        File file = new File(desPath);
        if (!(file.exists() && file.isDirectory())) {
            file.mkdirs();
        }

        System.out.println("=====fileConverter initHandle:" + initHandle);
        datasetLib.fileConverterInit(initHandle, szSrcPath, DataSetLib.FileType.CPV_FileType_FMT, -1, desPath, desPath); // 2
        configDecodeProperty(false, initHandle); // 配置采样配置周期 false为不是OTS采集 //3
        int isSuccess = datasetLib.fileConverter(initHandle, szSrcPath, DataSetLib.FileType.CPV_FileType_FMT, -1, desPath, desPath); // 4
        datasetLib.freeHandle(initHandle); // 5

        return isSuccess;
    }

    // /**
    // * 查询特定的ID返回网络供仪表盘使用，根据Boss给的仪表范围值得
    // * 与网络类型不一样，切勿混使用
    // */

    // public WalkStruct.CurrentNetState currentYbNetState = WalkStruct.CurrentNetState.Unknown;

    /**
     * 函数功能： 注册实时通知 这个方法目前库没有开放,如需用于需朱公子开放
     *
     * @param iDslHandle Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPortID    端口号
     * @param iInfoType  1->代表参数 2->代表信令 3 -->结构 4 --> 文件大小(后在的参数传文件类型)
     * @param lInfoID    参数ID或者信令ID
     * @return 返回值 ： 成功>=0 ; 失败<0
     */

    public int regObserverInfo(int iDslHandle, int iPortID, int iInfoType, long lInfoID) {
        return datasetLib.regObserverInfo(iDslHandle, iPortID, iInfoType, lInfoID);
    }

    /**
     * 海思锁频频网络接口
     *
     * @param type    eATNULL = 0, eATLTE_PowerON, //不带参数 eATLTE_PowerOFF, //不带参数
     *                eATLTE_Attach, //不带参数 eATLTE_Detach, //不带参数 eATLTE_GetIP,
     *                //不带参数 eATLTE_ResetDev, //不带参数 eATLTE_ReleaseIP, //不带参数
     *                eATLTE_SetAPN, //暂不做 eATLTE_SetIMSAPN, //暂不做
     *                eATLTE_SetDefualtAPN,//暂不做 eAT_LockNetwork, //带参数: Network
     *                Type=xx\r\n eATLTE_LockBand, //带参数：Band=xx\r\n
     *                eTSLTE_LockFreq, //带参数：Band=xx\r\nEARFCN=xx\r\n
     *                eTSLTE_LockCell, //带参数：Band=xx\r\nEARFCN=xx\r\nPCI=xx\r\n
     *                eTSLTE_Unlock, //不带参数 eTSLTE_HandoverReq, //暂不做
     *                eTSLTE_CselReq, //暂不做 eTSLTE_BarCellAccessReq,//暂不做
     *                eAGSM_LockBand, //带参数：Band=xx\r\n eAGSM_LockFreq,
     *                //带参数：Band=xx\r\nARFCN=xx\r\n eAGSM_UnLock, //不带参数
     *                eAWcdma_LockFreq, //暂不做 eAWcdma_LockPSC, //暂不做 eAWcdma_Unlock,
     *                //暂不做 eUEMoodifyNVParam, //暂不做
     * @param pBuffer
     * @return
     */
    public int devWritePortExt(int type, byte[] pBuffer) {
        LogUtil.w(TAG, "--devWritePortExt type:" + type);
        return datasetLib.devWritePortExt(datasetHandle, PORT_2, type, pBuffer, pBuffer.length);
    }

    /**
     * 构建GPS信息点
     */

    public int buildAllGPSInfoQuery(int port) {
        return mDatasetBuilder.buildAllGPSInfoQuery(port);
    }

    /**
     * 重新配置logmask
     *
     * @param logmaskPath logmask文件路径
     * @return
     */
    public int configLogmask(String logmaskPath) {
        try {
            File fix = new File(logmaskPath);
            if (!fix.exists() && !fix.isDirectory()) {// 判断文件是否存在
                LogUtil.w("configLogmask", "logmask is not exist:" + logmaskPath);
                EventManager.getInstance().addTagEvent(WalktourApplication.getAppContext(), System.currentTimeMillis(), "logmask is not exist,please check license.");
            }
            fix = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        LogUtil.w(TAG, "-----change logmask to configLogmask=" + logmaskPath);
        return datasetLib.configLogmask(datasetHandle, PORT_2, logmaskPath);
    }

    /***
     * 是否可以写入文件
     * @param canWriteFile
     */
    public void setCanWriteFile(boolean canWriteFile) {
        isCanWriteFile = canWriteFile;
    }

    /**
     * 配置是否输出手机信令详解
     */
    public void setConfigPropertyKey_DecodeOutputMS_MsgDetailBuffer() {
        datasetLib.configDecodeOrStorageProperty(datasetHandle, PORT_2, DataSetLib.ConfigPropertyKey_DecodeOutputMS_MsgDetailBuffer, ServerManager.getInstance(mContext).hasShowL1L2CommandBuffer() ? 1 : 0);
    }

    /**
     * 获取事件名称
     *
     * @param eventID 事件ID
     * @return
     */
    public String getEventName(int eventID) {
        return datasetLib.getEventName(datasetHandle, eventID) + "";
    }

    /***
     * 切换为普通的logmask
     */
    public void changeToNormalLogMask()
    {
        if (!isNormalLogMask) {
            String filePath = AppFilePathUtil.getInstance().getAppConfigFile("Diag.cfg").getAbsolutePath();
            LogUtil.w(TAG, "-----change logmask to Diag.cfg-----");
            configLogmask(filePath);
            isNormalLogMask = true;
        }
    }

    /***
     * 如果有三星VoLTE权限,则切换为VoLTE权限
     */
    public void changeToVoLTELogMask()
    {
        if (ApplicationModel.getInstance().hasSamsungVoLTE() && isNormalLogMask) {
            String filePath = AppFilePathUtil.getInstance().getAppConfigFile("Diag_VoLTE.cfg").getAbsolutePath();
            LogUtil.w(TAG, "-----change logmask to Diag_VoLTE.cfg-----");
            configLogmask(filePath);
            isNormalLogMask = false;
        }
    }
}
