package com.walktour.control.config;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;

import com.walktour.Utils.MobileInfoUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhone;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 设备信息类
 * @author Administrator
 *
 */
@SuppressLint("SdCardPath")
public class Deviceinfo {

    /**支持的网络制式,和config_deviceinfo文件中的nettypes对应*/
    public static final String NET_TYPES_CDMA = "CDMA";
    /**支持的网络制式,和config_deviceinfo文件中的nettypes对应*/
    public static final String NET_TYPES_EVDO = "EVDO";
    /**支持的网络制式,和config_deviceinfo文件中的nettypes对应*/
    public static final String NET_TYPES_GSM = "GSM";
    /**支持的网络制式,和config_deviceinfo文件中的nettypes对应*/
    public static final String NET_TYPES_WCDMA = "WCDMA";
    /**支持的网络制式,和config_deviceinfo文件中的nettypes对应*/
    public static final String NET_TYPES_TDSCDMA = "TDSCDMA";
    /**支持的网络制式,和config_deviceinfo文件中的nettypes对应*/
    public static final String NET_TYPES_LTE = "LTE";

    public static final int NETTYPE_WCDMA = 1;
    public static final int NETTYPE_GSM = 2;
    public static final int NETTYPE_EVDO = 3;
    public static final int NETTYPE_CDMA = 4;
    public static final int NETTYPE_TD = 5;
    public static final int NETTYPE_FDD_LTE = 6;
    public static final int NETTYPE_TDD_LTE = 7;
    //Diag_LTE logmask 文件名
    public static final String DIAG_LTE="Diag_Lte.cfg";
    //Diag_NotLte.cfg logmask 文件名
    public static final String DIAG_NOT_LTE="Diag_NotLte.cfg";
    /**
     * 获得接听电话的方法,如果当前取不到值,与取拨打一置流程
     * 0:call by trace,1:call by API 2:use radiooption,3:by Micro Phone,5:use input keyevent,
     * 6:by sys app api,7:use am command, default use dialmethod
     * Info:
     * 0:通过串口接听
     * 1:通过API接听
     * 2:使用radiooption库文件接听
     * 3:通过模拟耳机的按钮接听
     * 5:通过input keyevent命令接听
     * 6:使用置为系统应用(WalktourSetting)的API接听
     * 7:使用AM命令接听
     */
    /**使用trace设备拨打*/
    public static final int DIAL_WITH_TRACE = 0;
    /**使用android api拨打*/
    public static final int DIAL_WITH_API = 1;
    /**使用radiooption拨打,部分手机可用*/
    public static final int DIAL_WITH_RADIOOPTION = 2;
    /**使用系统应用打电话*/
    public static final int DIAL_WITH_SYSTEM_APP = 6;
    /**使用系统am命令拨打电话*/
    public static final int DIAL_WITH_SYSTEM_AM = 7;
    /**使用系统调用输入电话号码屏幕方法，再调模拟拨号键 service call phone 1 s16 "10086"*/
    public static final int DIAL_WITH_SERVICE_CALL = 8;
    /**使用系统调用输入电话号码屏幕方法2，再调模拟拨号键  service call phone 2 s16 "10086"*/
    public static final int DIAL_WITH_SERVICE_CALL2 = 9;
    /**通过系统API拨打电话4.0*/
    public static final int DIAL_WITH_SYSTEM_API_4 = 10;
    /**通过系统API拨打电话5.0*/
    public static final int DIAL_WITH_SYSTEM_API_5 = 11;

    /**通过模拟耳机的按钮接听*/
    public static final int CALL_BY_MICRO_PHONE = 3;
    /**通过input keyevent命令接听*/
    public static final int CALL_BY_INPUT_KEYEVENT = 5;

    /**GPS不打开*/
    public static final int GPS_PROVIDER_OFF = 0;
    /**GPS用户手工打开*/
    public static final int GPS_PROVIDER_MANUAL = 1;
    /**GPS强制打开*/
    public static final int GPS_PROVIDER_PROGRAMING = 2;

    /**普通方式往串口写入logmask*/
    public static final int IPC_DIAG_NORMAL = 0;
    /**ROOT方式往串口写入logmask*/
    public static final int IPC_DIAG_ROOT = 1;

    /**ppp 拨号时以svc的方式开关数据接入点*/
    public static final int PPP_MODEL_SVC = 1;
    /**不作拨号动作*/
    public static final int PPP_MODEL_NONE = 2;

    public static final String DEBUG_FILE_NAME = "debug_deviceinfo.xml";
    // 当前类实例化是设置配置文件是否存在状态
    private static boolean deviceFileExist = false;
    /**NB模块未上电**/
    public static int POWN_ON_NO = 0;
    /**NB模块上电进行中**/
    public static int POWN_ON_ING = 1;
    /**NB模块上电成功**/
    public static int POWN_ON_SUCCESS = 2;
    /**NB模块上电失败**/
    public static int POWN_ON_FAILURE = 3;
    // private String devicename;
    private String devicemodel = "";
    private String tracepath = "";
    private int moduletype;
    private int nettype;
    private String nettypes = "";
    private int allNetstate = 0; // 是否全网通设备,当配置文件中有isallnet属性且值为1时表示当前为全网通设备
    private int dialmethod; // 电话拨打的方法
    private int acceptmethod; // 接听电话的方法
    private int opengps; // 强行打开GPS
    private String extend = "";
    private int tracedisporse = 1; // 串口解码设用的解码库 0：新的统一解码库的方式；1：旧的解码库方式
    private String pppName = "";
    private String wifiDevice = "";
    private String moduleName = ""; // 设置手机的RCU头文件模块名
    private String wifiDataOnly = ""; // 是否WIFI 与数据拉入点可以共存，1是，其它不是
    private String phoneLogType = ""; // RUC头文件中自增序号手机类型ID
    private String lockCode = ""; // 部分手机锁网的暗码 ,2013.11.5扩展为一些手机强制功能页面的配置
    private int callVoicePesqM2M = 0; // PESQ通话音量(录音) Mobile to Mobile
    private int callVoicePesqM2L = 0; // PESQ通话音量(录音) Mobile to Land
    private int callVoicePolqaM2M = 0; // POLQA通话音量(录音) Mobile to Mobile
    private int callVoicePolqaM2L = 0; // POLQA通话音量(录音) Mobile to Land
    private int callMosBoxVRec = 5; // 小背包MOS盒录音音量
    private int callMosBoxVPlay = 210; // 小背包MOS盒放音音量
    private float callMosBoxLowMos = 3.5f; // 小背包MOS阈值
    /** 蓝牙MOS测试时的手机语音放音音量*/
    private int microMosVoiceVolume = 20;
    /** 蓝牙MOS测试时的手机媒体放音音量*/
    private int microMosMediaVolume = 20;
    private int mediaVoice = 5; // 放音音量
    private int traceOffset = 12; // 高通芯片自封的不定长度的信令包头信息
    private int baudrate = 115200;
    private int interval = 20; // ms
    private int chipvendor = 0;
    private int ipcdiag = 0; // 是否以进程的方式启动写logmask方式 1为是,其它为否
    private int devdiagtype = 0; // 是否以S7的方式加载动态库1表示是;默认为以谱通方式加载
    private String ipcPath = "/data/data/com.walktour.gui/ipcDevDiag";// ,文件名为ipcDevDiag
    private List<String> nbModuleNames = new LinkedList<>();// NB模块设备的名字
    /***
     * 是否可以使用volte网络,默认为0不可以,1为可以.有SAMSUNGVoLTE权限时,做语音测试切换为volte的logmask
     */
    private int voltenet = 0;
    /***
     * logmask的默认文件名,当不设置时,默认使用这个,否则使用指定的文件名
     */
    private String logmask="Diag_Lte.cfg";
    private int dualmode = 0; // 是否双模手机,默认为非双模,此设置仅用于高级设置中的双模锁GSM,OUTO两种模式
    private int pppmodel = 0; // 开关数据开关模式,1为svc模式,2为不拨号模式,其它的暂时按之前的ROM版本的选择进行
    private int apnlist = 0; // 读取APN List开关,1表示不读取,0表示读取,默认为0
    private int cmdChoke = 0; // 是否以阻塞方式执行svc 命令,默认为非阻塞方式执行,当值为1时表示阻塞
    private int pingbusybox = 0; // 是否通过busybox处理ping命令
    private int isCustom = 0; // 是否是OPPO定制机
    private LockInfo lockInfo = new LockInfo("");
    private static Deviceinfo instance = null;
    private static final String tag = "Deviceinfo";
    private static final String filename = "config_deviceinfo.xml";
    //ping业务是否使用cmpping执行
    private boolean iscmdping=false;
    /**
     * 2018/8/14 yi.lin
     * 连接小背包默认通用的控制端口号
     */
    public static final String DEFAULT_IPACK_CTRL_PORT = "55555";
    /**
     * 2018/8/14 yi.lin
     * 配置连接小背包的控制端口号，在config_deviceinfo.xml文件iPackCtrlPort节点配置，
     * 无配置默认使用55555端口
     */
    private String iPackCtrlPort = DEFAULT_IPACK_CTRL_PORT;
    /**Samsung S8定制机非root版**/
    private boolean isCustomS8 = false;
    /**Samsung S8定制机第一版非root版**/
    private boolean isCustomS8_V1 = false;
    /**Samsung S8定制机ROOT版**/
    private boolean isCustomS8Root = false;
    /**Samsung S9定制机非root版***/
    private boolean isCustomS9=false;
    /**Samsung A60定制机非root版***/
    private boolean isCustomA60=false;
    /** Android 版本号. Android 7.0.0 (700) Android 8.0.0 (800) Android 9.0.0(900)... **/
    private int[] mReleaseVersion = new int[5];
    /**当前Android系统的主版本号 **/
    private int mMainReleaseVersion;
    /** Android Version **/
    private String strReleaseVersion = "";
    /**Vivo手机***/
    private boolean isVivo = false;
    /**Oppo 定制机***/
    private boolean isCustomOppo = false;
    /**是否是ZTE A2020N3 Pro*/
    private boolean isZTEA2020N3Pro=false;
    /**
     * NB设备上电状态
     */
    private int nbPowerOnStaus = POWN_ON_NO;// 0-未上电 1-上电进行中 2-上电成功 3-上电

    /**
     * 遥远的专有设备名
     */
    public static final String DeviceName_YaoYuan = "cr1012p";
    /**
     * 遥远的实际业务测试的设备名
     */
    public static final String DeviceName_YaoYuan_Really = "Remo 1526";

    /***
     * 是否具有NB USB测试模式,启动程序就检测
     */
    private boolean hasNBUsbTestModel = false;

    private String modelName = ""; // android.os.Build.MODEL;

    /**
     * 标识是否在AT命令模式,datatests_android_devmodem在没释放前不允许新开.
     */
    private boolean isATModel = false;
    /**是否处在飞行模式 vivo开机启动串口后需要单独飞行下**/
    private boolean isAir = false;
    /**app文件路径**/
    private AppFilePathUtil appPath = AppFilePathUtil.getInstance();

    /**是否运行StartDatasetService,默认是不运行**/
    private boolean isRunStartDatasetService = false;

    /**网络激活动作 执行默认的ping地址**/
    private String pingAddress = "61.143.60.84";
    /**网络激活动作 执行默认的ping超时时间**/
    private int pingTimeOutValue = 10;

    /**NB PPP拨号DNS设置**/
    public static String CMDNB = "setprop net.dns1 218.2.2.2";
    /**是否开放数据集日志**/
    private boolean isOpenDataSetLog=false;
    private Deviceinfo() {
        try {
            String romVersion = MyPhone.getSimpleROMVersionNameByShell();
            LogUtil.w(tag, "isDeviceRooted=" + MobileInfoUtil.isDeviceRooted());
            LogUtil.w(tag, "romVersion=" + romVersion);
            modelName = android.os.Build.MODEL;
            File deviceFile = null;
            deviceFile = appPath.getSDCardFile("Walktour", "setting", filename);
            if (null == deviceFile || !deviceFile.exists()) {
                deviceFile = appPath.getAppConfigFile(filename);
            }
            deviceFileExist = deviceFile.exists();

            LogUtil.w(tag, "--deviceinfo file exist:" + deviceFileExist);

            doReleaseVersion();
            doSamsung(romVersion);

            isVivo = modelName.startsWith("vivo");
            if (!isVivo)
                isVivo = modelName.equals("V1816A")||modelName.equals("V1824A");

            if(!isZTEA2020N3Pro){
                isZTEA2020N3Pro=modelName.equals("ZTE A2020N3 Pro");
            }
            LogUtil.w(tag, "isVivo: " + isVivo);

            if (deviceFileExist) {
                DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = fac.newDocumentBuilder();
                Document doc = db.parse(deviceFile);
                Element e = doc.getDocumentElement();
                NodeList nodelist = e.getChildNodes();
                if (isCustomS8) {//S8定制机
                    modelName = "SM-G9500-C";
                }

                if(isCustomS9){//S9定制机
                    modelName = "SM-G9600-C";
                }

                if(isCustomA60){//A60定制机
                    modelName="SM-A6060-C";
                }
                if (nodelist != null) {
                    LogUtil.w(tag, "---modelName:" + modelName + "--len:" + nodelist.getLength());
                    boolean isFind = false;
                    for (int i = 0; i < nodelist.getLength(); i++) {
                        if (isFind) {
                            break;
                        }
                        Node node = nodelist.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            NamedNodeMap nodemap = node.getAttributes();
                            String model = nodemap.getNamedItem("devicename").getNodeValue();
                            String[] devicenames = model.split(",");
                            for (int k = 0; k < devicenames.length; k++) {
                                if (modelName.equalsIgnoreCase(devicenames[k])) {
                                    isFind = true;
                                    if (nodemap.getNamedItem("isallnet") != null) {
                                        allNetstate = Integer.parseInt(nodemap.getNamedItem("isallnet").getNodeValue().trim());
                                    }
                                    nettypes = nodemap.getNamedItem("nettypes").getNodeValue();
                                    nettype = Integer.parseInt(nodemap.getNamedItem("nettype").getNodeValue());
                                    moduletype = Integer.parseInt(nodemap.getNamedItem("moduletype").getNodeValue());
                                    devicemodel = nodemap.getNamedItem("devicemodel").getNodeValue();
                                    tracepath = nodemap.getNamedItem("tracepath").getNodeValue();
                                    dialmethod = Integer.parseInt(nodemap.getNamedItem("dialmethod").getNodeValue());
                                    // 如果取不到接听属性,处理为与拨打同样的操作方式
                                    if (nodemap.getNamedItem("acceptmethod") != null) {
                                        acceptmethod = Integer.parseInt(nodemap.getNamedItem("acceptmethod").getNodeValue());
                                    } else {
                                        acceptmethod = dialmethod;
                                    }
                                    opengps = Integer.parseInt(nodemap.getNamedItem("opengps").getNodeValue());
                                    tracedisporse = Integer.parseInt(nodemap.getNamedItem("tracedisporse").getNodeValue());
                                    pppName = nodemap.getNamedItem("pppname").getNodeValue();
                                    wifiDevice = nodemap.getNamedItem("wifiname").getNodeValue();
                                    if (nodemap.getNamedItem("ipcdiag") != null) {// 是否以ipc方式执行,0--false,1--true
                                        LogUtil.d(tag, "ipcdiag: " + nodemap.getNamedItem("ipcdiag").getNodeValue());
                                        ipcdiag = Integer.parseInt(nodemap.getNamedItem("ipcdiag").getNodeValue());
                                    }
                                    if (nodemap.getNamedItem("ipcPath") != null) {
                                        ipcPath = nodemap.getNamedItem("ipcPath").getNodeValue() + "";
                                    }
                                    if (nodemap.getNamedItem("nbModuleNames") != null) {
                                        String nbStr = nodemap.getNamedItem("nbModuleNames").getNodeValue() + "";
                                        if (null != nbStr && nbStr.length() > 0) {
                                            String[] names = nbStr.split(",");
                                            nbModuleNames = Arrays.asList(names);
                                        }
                                    }
                                    if (nodemap.getNamedItem("isNBModule") != null) {
                                        String val = nodemap.getNamedItem("isNBModule").getNodeValue();
                                        if (null != val && val.equals("1")) {
                                            hasNBUsbTestModel = true;
                                        }
                                    }

                                    if (nodemap.getNamedItem("devdiagtype") != null) {
                                        devdiagtype = Integer.parseInt(nodemap.getNamedItem("devdiagtype").getNodeValue());
                                    }
                                    if (nodemap.getNamedItem("voltenet") != null) {
                                        voltenet = Integer.parseInt(nodemap.getNamedItem("voltenet").getNodeValue());
                                    }
                                    if (nodemap.getNamedItem("logmask") != null) {
                                        logmask = nodemap.getNamedItem("logmask").getNodeValue();
                                    }
                                    if (nodemap.getNamedItem("dualmode") != null) {
                                        dualmode = Integer.parseInt(nodemap.getNamedItem("dualmode").getNodeValue());
                                    }
                                    if (nodemap.getNamedItem("pppmodel") != null) {
                                        pppmodel = Integer.parseInt(nodemap.getNamedItem("pppmodel").getNodeValue());
                                    }
                                    if (nodemap.getNamedItem("apnlist") != null) {
                                        apnlist = Integer.parseInt(nodemap.getNamedItem("apnlist").getNodeValue());
                                    }
                                    // 如果有值,且值为1则表示阻塞方式执行SVC
                                    if (nodemap.getNamedItem("iscmdchoke") != null) {
                                        cmdChoke = Integer.parseInt(nodemap.getNamedItem("iscmdchoke").getNodeValue());
                                    }
                                    if (nodemap.getNamedItem("pingbusybox") != null) {
                                        pingbusybox = Integer.parseInt(nodemap.getNamedItem("pingbusybox").getNodeValue());
                                    }
                                    if (nodemap.getNamedItem("isCustom") != null) {
                                        isCustom = Integer.parseInt(nodemap.getNamedItem("isCustom").getNodeValue());
                                    }
                                    if (nodemap.getNamedItem("iscmdping") != null) {
                                        iscmdping = Integer.parseInt(nodemap.getNamedItem("iscmdping").getNodeValue())==0?false:true;
                                    }
                                    if (nodemap.getNamedItem("isRunStartDatasetService") != null) {
                                        isRunStartDatasetService = Integer.parseInt(nodemap.getNamedItem("isRunStartDatasetService").getNodeValue()) == 1 ? true : false;
                                    }
                                    if (nodemap.getNamedItem("isOpenDataSetLog") != null) {
                                        isOpenDataSetLog = Integer.parseInt(nodemap.getNamedItem("isOpenDataSetLog").getNodeValue()) == 1 ? true : false;
                                    }
                                    if (nodemap.getNamedItem("pingAddress") != null) {
                                        pingAddress = nodemap.getNamedItem("pingAddress").getNodeValue();
                                    }
                                    if (nodemap.getNamedItem("pingTimeOutValue") != null) {
                                        pingTimeOutValue = Integer.parseInt(nodemap.getNamedItem("pingTimeOutValue").getNodeValue());
                                    }

                                    if (nodemap.getNamedItem("iPackCtrlPort") != null) {
                                        iPackCtrlPort = nodemap.getNamedItem("iPackCtrlPort").getNodeValue();
                                    }
                                    moduleName = nodemap.getNamedItem("modulename").getTextContent();
                                    wifiDataOnly = nodemap.getNamedItem("wifidataonly").getTextContent();
                                    phoneLogType = nodemap.getNamedItem("phonelogtype").getTextContent();

                                    lockCode = nodemap.getNamedItem("lockcode").getTextContent();
                                    lockInfo = new LockInfo(lockCode);

                                    // 数据集使用，还包括moduleName
                                    baudrate = Integer.parseInt(nodemap.getNamedItem("baudrate").getNodeValue());
                                    interval = Integer.parseInt(nodemap.getNamedItem("interval").getNodeValue());
                                    chipvendor = Integer.valueOf(nodemap.getNamedItem("chipvendor").getNodeValue(), 16);

                                    callVoicePesqM2M = Integer.parseInt(nodemap.getNamedItem("callvolumePesqM2M").getNodeValue());
                                    callVoicePesqM2L = Integer.parseInt(nodemap.getNamedItem("callvolumePesqM2L").getNodeValue());
                                    callVoicePolqaM2M = Integer.parseInt(nodemap.getNamedItem("callVolumePolqaM2M").getNodeValue());
                                    callVoicePolqaM2L = Integer.parseInt(nodemap.getNamedItem("callVolumePolqaM2L").getNodeValue());

                                    if (nodemap.getNamedItem("callMosBoxVRec") != null) {
                                        callMosBoxVRec = Integer.parseInt(nodemap.getNamedItem("callMosBoxVRec").getNodeValue());
                                    }
                                    if (nodemap.getNamedItem("callMosBoxVPlay") != null) {
                                        callMosBoxVPlay = Integer.parseInt(nodemap.getNamedItem("callMosBoxVPlay").getNodeValue());
                                    }

                                    if (nodemap.getNamedItem("callMosBoxLowMos") != null) {
                                        callMosBoxLowMos = Float.parseFloat(nodemap.getNamedItem("callMosBoxLowMos").getNodeValue());
                                    }
                                    if (nodemap.getNamedItem("microMosMediaVolume") != null) {
                                        microMosMediaVolume = Integer.parseInt(nodemap.getNamedItem("microMosMediaVolume").getNodeValue());
                                    }
                                    if (nodemap.getNamedItem("microMosVoiceVolume") != null) {
                                        microMosVoiceVolume = Integer.parseInt(nodemap.getNamedItem("microMosVoiceVolume").getNodeValue());
                                    }

                                    mediaVoice = Integer.parseInt(nodemap.getNamedItem("mediavolume").getNodeValue());
                                    traceOffset = Integer.parseInt(nodemap.getNamedItem("TraceOffset").getNodeValue());

                                    break;
                                }
                            }
                        }
                    }
                    LogUtil.w(tag, "--deviceinfo modulename:" + moduleName + "--isCustom:" + isCustom + "--wifidataonly:" + wifiDataOnly + "--accptmethod:" + acceptmethod + "--tracepath:" + tracepath + "--pppmodel:" + pppmodel + "--pppname:" + pppName + "---apnlist:" + apnlist + "--iscmdchoke:" + cmdChoke + "--iPackCtrlPort:" + iPackCtrlPort);
                }
            }
            LogUtil.w(tag, "isCustom=" + isCustom);
            if (modelName.startsWith("OPPO R11s") && (isCustom == 1)) {
                isCustomOppo = true;
                if (isCustomOppo) {
                    if (MobileInfoUtil.isDeviceRooted()) {
                        isCustomOppo = false;
                    }
                }
            }
            LogUtil.w(tag, "isCustomOppo: " + isCustomOppo);
        } catch (Exception e) {
            LogUtil.w(tag, "Exception:" + e.getMessage());
        }
    }

    public boolean isIscmdping()
    {
        return iscmdping;
    }

    /**
     * 获取设备信息单例
     * @return
     */
    public static synchronized Deviceinfo getInstance() {
        if (instance == null || !deviceFileExist) {
            instance = new Deviceinfo();
        }
        return instance;
    }

    public boolean isATModel() {
        return isATModel;
    }

    public void setATModel(boolean ATModel) {
        isATModel = ATModel;
    }

    /**
    	 * 获取设备名称
    	 * @return
    
    	public String getDevicename(){
    
    		return devicename;
    	} */

    /**
     * 获取网络类型数组
     * @return
     */
    public Set<String> getNettypes() {
        String[] types = nettypes.split(",");
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < types.length; i++) {
            set.add(types[i]);
        }
        return set;
    }

    /**
     * 获取网络类型
     * 1:WCDMA 2:GSM 3:EVDO 4:CDMA 5:TDSCDMA 6:LTE
     * @return
     */
    public int getNettype() {
        return nettype;
    }

    /***
     * 根据配置文件的网络类型返回运营商信息,此处仅联通使用,暂这样处理,不考虑国外运营商情况
     * 1:联通,2:移动,3:电信
     * @return
     */
    public int getCUSPByNetType() {
        switch (nettype) {
        case 3:
        case 4:
            return 3;
        case 5:
        case 7:
            return 2;
        case 1:
        case 6:
            return 1;
        default:
            return 1;
        }
    }

    /**
     * 获取手机模块类型
     * 1:Module_MC5210,2:Module_MC5218,3:Huawei_ec360;4:Module_me860;5:Module_motot3g G72;6:Module_huawei8500s;7:GAOTONG-LTE
     * 目前G7传2;XT800为3
     * @return
     */
    public int getModuletype() {
        return moduletype;
    }

    /**
     * 获取设备模型
     * @return
     */
    public String getDevicemodel() {
        return devicemodel;
    }

    /**
     * 获取trace名字
     * @return
     */
    public String getTracepath() {
        return tracepath;
    }

    /**
     * 获取
     * @return
     */
    public String getExtend() {
        return extend;
    }

    /**
     * 获取手机拨打使用的方法
     * 0:call by trace,1:call by API 2:use radiooption,6:by sys app api,7:use am command
     * Info:
     * 0:通过串口拨打
     * 1:通过API拨号
     * 2:使用radiooption库文件拨打
     * 6:使用置为系统应用(WalktourSetting)的API拨号
     * 7:使用AM命令拨打
     * @return
     */
    public int getDialWay() {
        return dialmethod;
    }

    /**
     * 获得接听电话的方法,如果当前取不到值,与取拨打一置流程
     * 0:call by trace,1:call by API 2:use radiooption,3:by Micro Phone,5:use input keyevent,6:by sys app api,7:use am command, default use dialmethod
     * Info:
     * 0:通过串口接听
     * 1:通过API接听
     * 2:使用radiooption库文件接听
     * 3:通过模拟耳机的按钮接听
     * 5:通过input keyevent命令接听
     * 6:使用置为系统应用(WalktourSetting)的API接听
     * 7:使用AM命令接听
     * @return
     */
    public int getAcceptWay() {
        return acceptmethod;
    }

    /**
     * GPS打开的方式
     * */
    public synchronized int getOpenGpsWay() {
        return opengps;
    }

    /**
     * 返回解码库处理方式
     * 0：统一解码库
     * 1：旧的解码库方式
     * @return
     */
    public int getTraceDisporse() {
        return tracedisporse;
    }

    /**
     * 获得当前手机拨号网络设备名
     * @return
     */
    public String getPppName() {
        return pppName;
    }

    /**
     * 获得WIFI连接设备名
     * @return
     */
    public String getWifiDevice() {
        return wifiDevice;
    }

    /**返回当前手机的模块名称
     * 如果不指定，则使用库原来的模认模块名称
     * 该值用于生成RCU头文件中的模块值
     * */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * 连接UMPC 是否WIFI 与数据共存模式
     * 值为1时表示可设 Wifi 与 Data共存
     * @return
     */
    public String getWifiDataOnly() {
        return wifiDataOnly;
    }

    /**
     * 电信第三方版本中添加文件序号，该值用于标识当前手机的类型
     * @return
     */
    public String getPhoneLogType() {
        return phoneLogType;
    }

    /**
     * @return 锁网暗码
     * */
    public String getLockCode() {
        return lockCode;
    }

    /**
     * @return 锁网页面的配置
     */
    public LockInfo getLockInfo() {
        return this.lockInfo;
    }

    /**
     * 返回高通芯片android层后续添加的不定长度表示包长的无效包信息
     * @return
     */
    public int getTraceOffset() {
        return traceOffset;
    }

    /**
     * @return MOS放音量
     */
    public int getMediaVoice() {
        if (hasDebugFile()) {
            try {
                int value = Integer.parseInt(getValueFromDebugFile("mediavolume"));
                return value;
            } catch (Exception e) {

            }
        }

        return mediaVoice;
    }

    /**
     * @return MOS录音音量(通话音量)
     */
    public int getCallVoicePesqM2M() {
        if (hasDebugFile()) {
            try {
                return Integer.parseInt(getValueFromDebugFile("callvolumePesqM2M"));
            } catch (Exception e) {

            }
        }
        return callVoicePesqM2M;
    }

    /**
     * @return MOS录音音量(通话音量)
     */
    public int getCallVoicePesqM2L() {
        if (hasDebugFile()) {
            try {
                return Integer.parseInt(getValueFromDebugFile("callvolumePesqM2L"));
            } catch (Exception e) {

            }
        }
        return callVoicePesqM2L;
    }

    /**
     * @return MOS录音音量(通话音量)
     */
    public int getCallVoicePolqaM2M() {
        if (hasDebugFile()) {
            try {
                return Integer.parseInt(getValueFromDebugFile("callVolumePolqaM2M"));
            } catch (Exception e) {

            }
        }
        return callVoicePolqaM2M;
    }

    /**
     * @return MOS录音音量(通话音量)
     */
    public int getCallVoicePolqaM2L() {
        if (hasDebugFile()) {
            try {
                return Integer.parseInt(getValueFromDebugFile("callVolumePolqaM2L"));
            } catch (Exception e) {

            }
        }
        return callVoicePolqaM2L;
    }

    private boolean hasDebugFile() {
        return appPath.hasSDCardFile(DEBUG_FILE_NAME);
    }

    /**
     * 从sdcard的调试文件中获取值
     */
    public String getValueFromDebugFile(String nodeName) {
        try {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = fac.newDocumentBuilder();
            Document doc = db.parse(appPath.getSDCardBaseFile(DEBUG_FILE_NAME));
            Element e = doc.getDocumentElement();
            NodeList nodelist = e.getChildNodes();
            if (nodelist != null) {
                LogUtil.w(tag, "---modelName:" + android.os.Build.MODEL + "--len:" + nodelist.getLength());
                for (int i = 0; i < nodelist.getLength(); i++) {
                    Node node = nodelist.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        NamedNodeMap nodemap = node.getAttributes();
                        String model = nodemap.getNamedItem("devicename").getNodeValue();
                        String[] devicenames = model.split(",");
                        for (int k = 0; k < devicenames.length; k++) {
                            if (android.os.Build.MODEL.equalsIgnoreCase(devicenames[k])) {
                                return nodemap.getNamedItem(nodeName).getNodeValue();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获得小背包MOS盒录音音量
     * @return the callMosBoxVRec
     */
    public int getMosBoxVRec() {
        return callMosBoxVRec;
    }

    /**
     * 获得小背包MOS盒放音音量
     * @return the callMosBoxVPlay
     */
    public int getMosBoxVPlay() {
        return callMosBoxVPlay;
    }

    /**
     * 获得小背包MOS阈值
     *
     * @return
     */
    public float getCallMosBoxLowMos() {
        return callMosBoxLowMos;
    }

    /**
    *串口初始化波特率
    */
    public int getBaudrate() {
        return baudrate;
    }

    /*
     * 信令读取间隔
     */
    public int getInterval() {
        return interval;
    }

    /*
     * chipvendor
     */
    public int getChipVendor() {
        return chipvendor;
    }

    /**
     * 返回当前机型是否以进程的方式启动写入logmask
     * 默认 为非进程方式启动,值为0,当前1时表示进程方式启动
     * @return
     */
    public int getIpcDiag() {
        return ipcdiag;
    }

    /**
     * 返回ipc的路径,目前默认为/data/data/com.walktour.gui/ipcDevDiag
     * @return
     */
    public String getIpcPath() {
        return ipcPath;
    }

    /**
     * 获得打开串口加载设备库方式
     * 1:S7方式加载
     * 0:变通方式加载
     * @return
     */
    public int getDevDiagType() {
        return devdiagtype;
    }

    /**
     * 当前机型是否支持VoLTE网络
     * 默认为不支持.当voltenet配置值为1时表示支持
     * @return
     */
    public boolean isVoLTENet() {
        return voltenet == 1;
    }
    /***
     * 获取logmask
     *
     * @return
     */
    public String getLogmask()
    {
        return logmask;
    }
    /**
     * 获得当前设置是否双模手机
     * 当为双模时,如果有锁网权限时,在高级中显示锁GSM,LTE OUTO两种模式
     * @return
     */
    public boolean isDualMode() {
        return dualmode == 1;
    }

    /**
     * 获得开关数据接入点开关的模式
     * 当前1表示用SVC打开,其它情况按之前ROM版本进行处理
     * 默认情况都是按ROM当前版本进行处理的
     * @return
     */
    public int getPPPMode() {
        return pppmodel;
    }

    /**
     * 是否读取APN List开关
     * 配置中1表示不读取,0表示读取,默认为0
     * @return
     */
    public boolean getApnList() {
        return apnlist == 0;
    }

    /**
     * 是否以阻塞的方式执行拨号SVC命令
     * @return
     */
    public boolean isCmdChoke() {
        return cmdChoke == 1;
    }

    /**
     * 获取所有的NB模块的名字
     *
     * @return
     */
    public List<String> getNbModuleNames() {
        return nbModuleNames;
    }

    /**
     * 是否通过busybox执行ping命令
     * @return
     */
    public boolean isBusyboxPing() {
        return pingbusybox == 1;
    }

    /**
     * 是否全网通手机,如果全网通手机,做权限校验时通过IMEI进行校验
     * @return
     */
    public boolean isAllNet() {
        return allNetstate == 1;
    }

    /**
     * LockInfo
     * 强制功能页面的配置信息
     * 2013-11-5 下午1:43:00
     * @version 1.0.0
     * @author qihang.li@dinglicom.com
     */
    public class LockInfo {

        private String lockCode = "";

        public LockInfo(String lockCode) {
            this.lockCode = lockCode;
        }

        /**
         * 函数功能：是否有强制功能
         * @return
         */
        public boolean hasLock() {
            return hasLockNet() || hasLockBand() || hasLockCell() || hasLockFreq();
        }

        /**
         * 锁定网络类型
         */
        public boolean hasLockNet() {
            return lockCode.contains("lockNet=1");
        }

        /**
         * 锁频段
         */
        public boolean hasLockBand() {
            return lockCode.contains("lockBand=1");
        }

        /**
         * 锁小区
         */
        public boolean hasLockCell() {
            return lockCode.contains("lockCell=1");
        }

        /**
         * 锁频点
         */
        public boolean hasLockFreq() {
            return lockCode.contains("lockFreq=1");
        }

        /**
         * 强制驻留小区
         */
        public boolean hasCampCell() {
            return lockCode.contains("campCell=1");
        }

        /**
         * 查询功能
         */
        public boolean hasQuery() {
            return lockCode.contains("query=1");
        }

        /**
         * 锁定当前小区
         */
        public boolean hasLockCurrentCell() {
            return lockCode.contains("lockCurrentCell=1");
        }

        /**
         * 锁定当前频点
         */
        public boolean hasLockCurrentFreq() {
            return lockCode.contains("lockCurrentFreq=1");
        }

        /**
         * 解锁当前小区
         */
        public boolean hasUnlockCurrentCell() {
            return lockCode.contains("unlockCurrentCell=1");
        }

        /**
         * 解锁当前频点
         */
        public boolean hasUnlockCurrentFreq() {
            return lockCode.contains("unlockCurrentFreq=1");
        }
    }

    /**
     *
     * @return 蓝牙MOS测试时的手机媒体放音音量
     */
    public int getMicroMosMediaVolume() {
        if (hasDebugFile()) {
            try {
                return Integer.parseInt(getValueFromDebugFile("microMosMediaVolume"));
            } catch (Exception e) {

            }
        }
        return microMosMediaVolume;
    }

    /**
     *
     * @return 蓝牙MOS测试时的手机语音放音音量
     */
    public int getMicroMosVoiceVolume() {
        if (hasDebugFile()) {
            try {
                return Integer.parseInt(getValueFromDebugFile("microMosVoiceVolume"));
            } catch (Exception e) {

            }
        }
        return microMosVoiceVolume;
    }

    public boolean isAir() {
        return isAir;
    }

    public void setAir(boolean air) {
        isAir = air;
    }

    public boolean isHasNBUsbTestModel() {
        return hasNBUsbTestModel;
    }

    public void setHasNBUsbTestModel(boolean hasNBUsbTestModel) {
        this.hasNBUsbTestModel = hasNBUsbTestModel;
    }

    public int getNbPowerOnStaus() {
        return nbPowerOnStaus;
    }

    public void setNbPowerOnStaus(int nbPowerOnStaus) {
        this.nbPowerOnStaus = nbPowerOnStaus;
    }

    /**
     * 适配Vivo手机添加的方法
     * @return
     */
    public String getSuOrShCommand() {
        if(Deviceinfo.getInstance().isZTEA2020N3Pro){
            return "sh";
        }
        if (MobileInfoUtil.isDeviceRooted())
            return "su";
        if (isUseRoot()) {
            return "su";
        }
        if(isCustomS9||isCustomA60){
            return "suzx";
        }
        return "sh";
    }

    /**
     * 是不是vivo手机
     *
     * @return
     */
    public boolean isVivo() {
        // LogUtil.w(tag, "isVivo=" + isVivo);
        return isVivo;
    }

    /**
     * 是不是OPPO定制手机
     *
     * @return
     */
    public boolean isOppoCustom() {
        // LogUtil.w(tag, "isCustomOppo=" + isCustomOppo);
        return isCustomOppo;
    }

    /**
     * 2018/8/14 yi.lin
     * 获取config_deviceinfo.xml文件配置的连接小背包控制端口号
     * @return 端口号
     */
    public String getIpackCtrlPort() {
        return TextUtils.isEmpty(iPackCtrlPort) ? DEFAULT_IPACK_CTRL_PORT : iPackCtrlPort;
    }

    /**
     * 是否使用root方式
     * @return
     */
    public boolean isUseRoot() {
        if (isVivo || isCustomOppo || isCustomS8||isCustomS9||isCustomA60) {
            if (isCustomOppo) {
                if (MobileInfoUtil.isDeviceRooted())
                    return true;
            }
            return false;
        }

        return true;
    }

    public boolean isZTEA2020N3Pro()
    {
        return isZTEA2020N3Pro;
    }

    /**
    * TODO 此判断方法需要优化
     * 是不是小米手机
     * @return
     */
    public boolean isXiaomi() {
        // return ((isCustom == 1) && devicemodel.startsWith("MI"));
        return (devicemodel.startsWith("MI"));
    }

    /**
     * 是不是S8或S8+或S7手机
     *
     * @return
     */
    public boolean isS8orS7() {
        if (isSamsungCustomRom()) {
            return false;
        }

        return (devicemodel.startsWith("SM-G9550") || devicemodel.startsWith("SM-G9500") || devicemodel.startsWith("SM-G9300") || devicemodel.startsWith("SM-G9600"))|| devicemodel.startsWith("SM-A6060");
    }

    /**
     * 是不是S8
     *
     * @return
     */
    public boolean isS8() {
        return modelName.startsWith("SM-G9500") || modelName.startsWith("SM-G9550")|| modelName.startsWith("SM-G9600")|| modelName.startsWith("SM-A6060");
    }

    /***
     * S9定制机非root版
     * @return
     */
    public boolean isCustomS9()
    {
        return isCustomS9;
    }

    /**
     * 是不是VIVO X23
     *
     * @return
     */
    public boolean isVivoX23() {
        return modelName.startsWith("V1816A");
    }

    /**
     * 是不是Vivo IQOO
     * @return
     */
    public boolean isVivoIQOO(){
        return modelName.startsWith("V1824A");
    }

    /**
     * 是否是SAMSUNG定制机非ROOT版手机
     *
     * @return
     */
    public boolean isSamsungCustomRom() {
        return isCustomS8 || isCustomS9 || isCustomA60;
    }

    /**
     * 是否是S8定制机非ROOT版手机
     *
     * @return
     */
    public boolean isS8CustomRom() {
        return isCustomS8;
    }

    /**
     * 是否是S8定制机第一版非ROOT版手机
     * 基带版本号: G9500ZCU2ARE1_B2BF
     * @return
     */
    public boolean isS8CustomRomV1() {
        return isCustomS8_V1;
    }

    /**
     * 是否是S9定制机非ROOT版手机
     *
     * @return
     */
    public boolean isS9CustomRom(){
        return isCustomS9;
    }
    /**
     * 是否是三星A60定制机非ROOT版手机
     *
     * @return
     */
    public boolean isA60Custom()
    {
        return isCustomA60;
    }

    /***
     * 是否是S8定制机ROOT版手机
     * @return
     */
    public boolean isCustomS8RomRoot() {
        return isCustomS8Root;
    }

    private void doSamsung(String strRomVerion) {
        if (isS8()) {
            LogUtil.w(tag, "romVersion=" + strRomVerion);

            if (!TextUtils.isEmpty(strRomVerion)) {
                isCustomS8 = strRomVerion.endsWith("_B2BF");
                isCustomS8_V1 = strRomVerion.equals("G9500ZCU2ARE1_B2BF");
                if (isCustomS8) {// 判断为定制机,,检测是否有root权限则为S8定制机root版
                    if (MobileInfoUtil.isDeviceRooted()) {
                        isCustomS8 = false;
                        isCustomS8Root = true;
                        isCustomS8_V1 = false;
                    }
                }

                isCustomS9= strRomVerion.startsWith("G9600");
                if (isCustomS9) {// 判断为定制机,,检测是否有root权限则为S8定制机root版
                    if (MobileInfoUtil.isDeviceRooted()) {
                        isCustomS9 = false;
                    }
                }
                isCustomA60= strRomVerion.startsWith("A6060");
                if (isCustomA60) {// 判断为定制机,,检测是否有root权限则为S8定制机root版
                    if (MobileInfoUtil.isDeviceRooted()) {
                        isCustomA60 = false;
                    }
                }
            }
            LogUtil.w(tag, "isCustomS8: " + isCustomS8);
            LogUtil.w(tag, "isCustomS9: " + isCustomS9);
            LogUtil.w(tag, "isA60Custom: " + isCustomA60);

        }
    }

    /***
     * 判断是否是三星手机
     * @return
     */
    public boolean isSAMSUNG() {
        if (null != modelName && (modelName.toLowerCase().contains("samsung") || modelName.toLowerCase().contains("sm-"))) {
            return true;
        }
        return false;
    }

    public boolean isS7() {
        return devicemodel.startsWith("SM-G9300");
    }

    public boolean isRunStartDatasetService() {
        LogUtil.w(tag, "isRunStartDatasetService=" + isRunStartDatasetService);
        return isRunStartDatasetService;
    }

    public String getPingAddress() {
        return pingAddress;
    }

    public int getPingTimeOutValue() {
        return pingTimeOutValue;
    }

    /**
     * 获取当前Android系统的主版本号
     */
    public int getMainReleaseVersion() {
        return mMainReleaseVersion;
    }

    public String getReleaseVersion() {
        return strReleaseVersion;
    }
    public boolean isOpenDataSetLog()
    {
        return isOpenDataSetLog;
    }

    /***
     * 解析手机的安卓版本号，目前最多保留5个位数;
     * 主要使用到主版本号区分命令的实现
     * @return
     */
    private void doReleaseVersion() {
        strReleaseVersion = Build.VERSION.RELEASE;
        if (TextUtils.isEmpty(strReleaseVersion))
            return;

        int iValue = 0;
        int iBeginPos = 0, iEndPos = -1, iStopPos = strReleaseVersion.length() - 1;
        int iPos = 0;

        while (iBeginPos <= iStopPos) {
            String strTemp = "";
            iEndPos = strReleaseVersion.indexOf(".", iBeginPos);
            if (iEndPos > iBeginPos) {
                strTemp = strReleaseVersion.substring(iBeginPos, iEndPos);
                iBeginPos = iEndPos + 1;
            } else if ((-1 == iEndPos) && (iBeginPos <= iStopPos)) {
                strTemp = strReleaseVersion.substring(iBeginPos);
                iBeginPos = iStopPos + 1;
            }
            iValue = Integer.parseInt(strTemp);
            mReleaseVersion[iPos++] = iValue;
            if (iPos >= mReleaseVersion.length)
                break;
        }

        mMainReleaseVersion = mReleaseVersion[0];

        return;
    }
}
