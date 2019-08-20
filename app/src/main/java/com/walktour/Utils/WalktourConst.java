/**
 *
 */
package com.walktour.Utils;


import android.os.Environment;

import java.io.File;

/**
 * @author jone
 */
public class WalktourConst {
    /**
     * APN设置,需要传递参数 IS_ENABLE boolean,如果IS_ENABLE为True，还需要传入APN_ID打开的APN ID
     */
    public static final String WALKTOUR_SYS_SETTING_APN = "com.dinglicom.walktour.setting.apn";

    /**
     * GPS设置，直接广播，不需要传值，当GPS处于打开时关闭，关闭时打开
     */
    public static final String WALKTOUR_SYS_SETTING_GPS = "com.dinglicom.walkgour.setting.gps";

    /**
     * 打电话
     */
    public static final String WALKTOUR_SYS_CALL = "com.dinglicom.walktour.call";
    public static final String KEY_NUMBER = "number";
    /**
     * 同意接电话，直接广播不需要传参数
     */
    public static final String WALKTOUR_SYS_SETTING_ACCEPTCALL = "com.dinglicom.walktour.setting.acceptcall";

    /**
     * 数据业务，需要传递参数 IS_ENABLE boolean用来标识数据业务是否可用
     */
    public static final String WALKTOUR_SYS_SETTING_DATA = "com.dinglicom.walktour.setting.data";

    /**
     * 选择APN进行设置，需要参入参数APN_NAME，来进行设置选中的APN名称
     */
    public static final String WALKTOUR_SYS_SETTING_APN_SELECT = "com.dinglicom.walktour.setting.selectapn";

    /**
     * 开启或关闭无线，需要传递参数 IS_ENABLE boolean用来标识业务是否可用
     */
    public static final String WALKTOUR_SYS_SETTING_RADIO = "com.dinglicom.walktour.setting.radio";

    /**
     * 是否可用
     */
    public static final String IS_ENABLE = "isEnable";

    /**
     * APN ID
     */
    public static final String APN_ID = "apnId";

    /**
     * 选中的APN名称
     */
    public static final String APN_NAME = "selectapn";

    /**
     * 执行命令action
     */
    public static final String WALKTOUR_EXEC_COMMAND = "com.dinglicom.walktour.command";

    /**
     * 命令
     */
    public static final String COMMAND = "command";

    /**
     * 更改串口权限
     */
    public static final String WALKTOUR_CHMOD_TRACE = "com.dinglicom.walktour.chmodtrace";


    /**
     * 设置系统时间
     */
    public static final String WALKTOUR_SET_SYSTEMCLOCK = "com.dingli.walktour.setsystemclock";

    public static final String WALKTOUR_SYSTEM_TIME = "walktour_system_time";

    public static final String LINE_SEPARATOR = "\r\n";
    public static String SAVE_ATTACH_DIR= Environment.getExternalStorageDirectory() + File.separator + "WalkTour/tag";

    /**
     * 网络类型定义<BR>
     * [功能详细描述]
     *
     * @author 黄广府
     * @version [WalkTour Client V100R001C03, 2013-5-17]
     */
    public interface NetWork {

        public final static int GSM = 1;

        public final static int WCDMA = 2;

        public final static int CDMA = 3;

        public final static int TDSDCDMA = 4;

        public final static int LTE = 5;

        public final static int NB_IoT = 6;
        public final static int CatM = 7;

    }

    /**
     * 小区连线常量定义<BR>
     * [功能详细描述]
     *
     * @author 黄广府
     * @version [WalkTour Client V100R001C03, 2013-10-5]
     */
    public interface CellLink {

        public final static String SERVING_REFERENCE_ENABLE = "serving_reference_enable";

        public final static String ACTIVE_SET_ENABLE = "active_set_enable";

        public final static String MONITOR_CANDIDATE_ENABLE = "monitor_candidate_enable";

        public final static String NEIGHBOR_ENABLE = "neighbor_enable";


        public final static String SERVING_REFERENCE_WIDTH = "serving_reference_width";

        public final static String ACTIVE_SET_WIDTH = "active_set_width";

        public final static String MONITOR_CANDIDATE_WIDTH = "monitor_candidate_width";

        public final static String NEIGHBOR_WIDTH = "neighbor_width";


        public final static String SERVING_REFERENCE_COLOR = "serving_reference_color";

        public final static String ACTIVE_SET_COLOR = "active_set_color";

        public final static String MONITOR_CANDIDATE_COLOR = "monitor_candidate_color";

        public final static String NEIGHBOR_COLOR = "neighbor_color";


    }

    public final static String SYS_SETTING_OTS_SAMPLE_INTERVAL = "sys_setting_ots_sample_interval";

    public final static String SYS_SETTING_OTS_TEST = "sys_setting_ots_test";

    public final static String SYS_SETTING_OTS_DATASTORAGE = "sys_setting_ots_datastorage";

    public final static String SYS_SETTING_OTS_DIVISION_SIZE = "sys_setting_ots_division_size";

    public final static String SYS_SETTING_OTS_DIVISION_ENABLE = "sys_setting_ots_division_enable";

    public final static String SYS_SETTING_OTS_PORT = "sys_setting_ots_port";                        //HTTP服务启动时端口

    public final static String SYS_SETTING_OTS_TEST_ACTIVE = "sys_setting_test_active";                //开始测试

    public final static String SYS_SETTING_OTS_REPORT_INTERVAL = "sys_setting_report_interval";        //上报间隔

    public final static String SYS_SETTING_OTS_URL_DEVICE = "sys_setting_ots_url_device";            //url(设备信息)

    public final static String SYS_SETTING_OTS_URL_PARAMETER = "sys_setting_ots_url_parameter";        //url(参数信息)


    public final static String SYS_SETTING_platform_test = "sys_setting_platform_control_test";        //测试计划扫描

    public final static String SYS_SETTING_platform_control = "sys_setting_platform_control_interactive";        //平台监控

    public final static String SYS_SETTING_taskgroup_control = "sys_setting_taskgroup_control_interactive";
    //平台监控

    public final static String SYS_SETTING_nbmoduele_devicename_control =
            "sys_setting_devicename_control_interactive";        //NB模块设备名
    public final static String SYS_SETTING_nbmoduele_deviceport_control =
            "sys_setting_deviceport_control_interactive";        //NB模块设串口
    public final static String SYS_SETTING_nbmoduele_deviceatport_control =
            "sys_setting_deviceatport_control_interactive";        //NB模块AT口
    public final static String SYS_SETTING_nbmoduele_devicechipvendor_control =
            "sys_setting_devicechipvendor_control_interactive";        //NB模块chipvendor
    public final static String SYS_SETTING_nbmodule_devicescramblestate =
            "sys_setting_devicescramblestate_control_interactive";    //NB模块扰码设置
    public final static String SYS_SETTING_nbmodule_devicesetapn =
            "sys_setting_devicesetapn_control_interactive";             //NB模块APN设置
    public final  static String SYS_SETTING_nbmodule_devicevoltesetting =
            "sys_setting_nbmodule_evicevoltesetting_control_interactive";             //NB模块APN设置
    public final  static String SYS_SETTING_nbmodule_devicesetpsm =
            "sys_setting_nbmodule_devicesetpsm_control_interactive";             //NB模块APN设置
    public final  static String SYS_SETTING_nbmodule_devicesetedrx =
            "sys_setting_nbmodule_devicesetedrx_control_interactive";             //NB模块APN设置
    public final static String SYS_SETTING_nbmodule_powerondelay =
            "sys_setting_devicesetapn_control_interactive_poweronldelay";             //NB模块上电时延，默认60s
    public final static String SYS_SETTING_nbmodule_iswifinbtest="sys_setting_devicesetapn_control_interactive_iswifinbtest";//是否是NBwifi测试
    public final static String SYS_SETTING_nbmodule_wifiserverip="sys_setting_devicesetapn_control_interactive_wifiserverip";//NB 模块选择的wifi server ip地址
    public final static String SYS_SETTING_nbmodule_wificlientip="sys_setting_devicesetapn_control_interactive_wificlientip";//NB 模块选择的wifi client ip地址
    /**
     * 统计设置存储常量
     */
    public final static String TOTAL_SETTING_REPORT_ISSHOW_REPORT = "total_setting_report_isshow_report";

    public final static String TOTAL_SETTING_REPORT_TOTALTEMPLATE = "total_setting_report_totaltemplate";

    public final static String TOTAL_SETTING_REPORT_REPORTTYPE = "total_setting_report_reportType";

    public final static String TOTAL_SETTING_REPORT_TOTALCHANNEL = "total_setting_report_totalChannel";

    public final static String TOTAL_SETTING_REPORT_TOTALNETWORK = "total_setting_report_totalNetWork";

    /**
     * 设置手机网络的工作模式
     * 0 GSM/WCDMA (WCDMA preferred)
     * 1 GSM only
     * 2 WCDMA only
     * 3 GSM/WCDMA (auto mode, according to PRL)
     * AVAILABLE Application Settings menu
     * 4 CDMA and EvDo (auto mode, according to PRL
     * AVAILABLE Application Settings menu
     * 5 CDMA only
     * 6 EvDo only
     * 7 GSM/WCDMA, CDMA, and EvDo (auto mode, according to PRL)
     * AVAILABLE Application Settings menu
     */
    public static final String WALKTOUR_SYS_SETTING_NETWORKMODEL = "com.dinglicom.walktour.setting.networkmodel";
    public static final String NETWORKMODELTYPE = "netModeltype";

    /**
     * 自动日期与时间开关设置
     */
    public static final String AUTOMATIC_DATE_TIME_SETTING = "com.dingli.walktour.automatictimeet";
    public static final String AUTOMATICTYPE = "AutomaticTimeType";

    //单站验证存储的数据
    public static final String WALKTOUR_SINGLESTATION_WORKORDERID = "com.dinglicom.walktour.singlestation.workorderid";
    public static final String WALKTOUR_SINGLESTATION_SITEINFOID = "com.dinglicom.walktour.singlestation.siteinfoid";
    public static final String WALKTOUR_SINGLESTATION_WORKITEMCODE = "com.dinglicom.walktour.singlestation" +
            ".workitemcode";
    public static final String WALKTOUR_SINGLESTATION_WORKITEMID = "com.dinglicom.walktour.singlestation.workitemid";
    public static final String WALKTOUR_SINGLESTATION_CELLID = "com.dinglicom.walktour.singlestation.cellid";
    public static final String WALKTOUR_SINGLESTATION_CELLNAME = "com.dinglicom.walktour.singlestation.cellname";
    public static final String WALKTOUR_SINGLESTATION_RSPOWER = "com.dinglicom.walktour.singlestation.rspower";
    public static final String WALKTOUR_SINGLESTATION_PA = "com.dinglicom.walktour.singlestation.pa";
    public static final String WALKTOUR_SINGLESTATION_PB = "com.dinglicom.walktour.singlestation.pb";
    public static final String WALKTOUR_SINGLESTATION_ECI = "com.dinglicom.walktour.singlestation.eci";
    public static final String WALKTOUR_SINGLESTATION_EARFCN = "com.dinglicom.walktour.singlestation.earfcn";
    public static final String WALKTOUR_SINGLESTATION_PCI = "com.dinglicom.walktour.singlestation.pci";
    public static final String WALKTOUR_SINGLESTATION_TAC = "com.dinglicom.walktour.singlestation.tci";

    /**
     * 地铁,高铁有关的
     * 地铁最新发布包MD5, 例如:7A0768E1065DDE7C4B50029159B76827
     */
    public static final String WALKTOUR_METRO_VERSION_MD5="com.dinglicom.walktour.version.metro";//
    public static final String WALKTOUR_HST_VERSION_MD5="com.dinglicom.walktour.version.highspeed.train";//
    /**
     * MIF 存储路径
     */
    public static final String MIF_MAP_DIR="com.dinglicom.walktour.mifmap";


    /**
     * 帮助文档是否已经copy
     */
    public static final String HELP_INFO="com.dinglicom.walktour.help.info.guid";
    /**
     * 统计信息是否已经copy
     */
    public static final String STATISTICS_INFO="com.dinglicom.walktour.statistics.info.guid";
    /**
     * 重启后是否需要启动WALKTOUR
     */
    public static final String IS_NEED_REBOOT="com.dinglicom.walktour.reboot";
    /**
     * 是否为最新版本
     */
    public static final String IS_NEW_VERSION="com.dinglicom.walktour.newversion";
    /**
     * 记录当前高铁线
     */
    public static final String CURRENT_HS="com.dinglicom.walktour.hs.model";
    /**
     * 记录当前高铁班次
     */
    public static final String CURRENT_HS_NO="com.dinglicom.walktour.hs.no";
    /**
     * 记录缓存连接蓝牙的地址
     */
    public static String connectBlueToothAddress="";

    public static class WifiType{
        public static String[] wifiType=new String[]{"NB","ScanTSMA"};
        /*0:NB模块，1：Scan模块
        * */
        public static String  WIFI_TYPE="com.dinglicon.walktour.wifitype";
    }
}
