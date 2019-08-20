package com.walktour.control.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.dinglicom.dataset.PlaybackManager;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.BuildPower;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.TelecomSetting;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.AssetsWriter;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.bean.MyXMLWriter;
import com.walktour.gui.R;
import com.walktour.license.Base64;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Locale;


/***
 * 系统设置环境信息
 *
 * @author weirong.fan
 *
 */
public class ConfigRoutine {
    private final String tag = "ConfigRoutine";

    public static final String STATICTISTC_A = "A";
    public static final String STATICTISTC_B = "B";
    public static final long limitLTime = 1356969600000L;
    private static final long limitLicDate = 1000 * 3600 * 24 * 7;

    /**
     * 版本类型：GMCC版本
     */
    private final int VERSION_TYPE_GMCC = 1;
    /**
     * 版本类型: 常规版本
     */
//	private final int VERSION_TYPE_NORMAL = 0;

    /**
     * 不绑定网卡
     */
    public static final int NET_INTERFACE_UNBIND = 0;
    /**
     * 绑定网卡
     */
    public static final int NET_INTERFACE_BIND = 1;

    /**
     * 业务过程中需要检测脱网
     */
    private final int CHECK_OUTOFSERVICE_TRUE = 1;
    /**
     * 业务过程中不检测脱网
     */
//	private final int CHECK_OUTOFSERVICE_FALSE 	= 0;


    private static final String KEY_ENV_INFO_UTIME = "env_info_utime";

    private final String KEY_OPEN_DATASETLOG = "open_datasetlog";
    private final String KEY_RUN_SAVEORG = "run_saveorgdata";
    private final String KEY_AUTO_DELETE = "auto_delete";
    private final String KEY_AUTO_DELETE_DAY = "auto_delete_dau";
    private final String KEY_NET_INTERFACE = "net_interface";
    //    private final String KEY_EXCHANGE_MODEL = "key_exchange_model";
    private final String KEY_INITPW = "initpw";
    private final String KEY_ACCEPT_CALL = "accept_call";
    private final String KEY_PUASE_KEEP_TEST = "puase_keep_test";
    private final String KEY_PUASE_NO_DATA = "puase_no_data";
    private final String KEY_PUASE_SAVE_DATA = "puase_save_data";
    private final String KEY_SWITCHAIRPLAN = "swithc_airplan";
    private final String KEY_USE_SCANNER = "use_scanner";
    private final String KEY_USE_SCANNER_TSMA = "use_scanner_tsma";
    private final String KEY_GEN_RCU = "rcu";
    private final String KEY_GEN_DTLOG = "dtlog";
    private final String KEY_GEN_DCF = "dcf";
    private final String KEY_GEN_CU = "cu";
    private final String KEY_GEN_ORG_RCU = "org_rcu";
    private final String KEY_GEN_ORG_ECTI = "org_ecti";
    private final String KEY_GEN_OTS = "otsparam";
    private final String KEY_GEN_AUTODELLOG = "auto_goonging";
    private final String KEY_GEN_AUTOVALUE = "auto_valuestr";
    private final String KEY_GEN_AUTO2VALUE = "auto2_valuestr";
    private final String KEY_GEN_AUTHVALUE = "auth_valuestr";
    private final String KEY_GEN_AUTH2VALUE = "auth2_valuestr";
    private final String KEY_GEN_AUTOTIMES = "auto_valuestim";
    private final String KEY_EGN_AUTO_CHE_LIN = "auto_check_lic";
    private final String KEY_BLUETOOTH_SYNC = "bluetoothsync";
    private final String KEY_NETTIME_SETTING = "nt_over";
    private final String KEY_IPACK_BYWIFIMODEL = "ipad_wifimodel";
    private final String KEY_MEMORY_MAPPED = "writeby_memorymapped";
    private final String KEY_SAVE_ALL_LOG = "save_all_log";
    private final String KEY_VOLTE_QCIINFO = "volte_qci_info";
    private final String KEY_CHECK_SMS_INFO = "check_sms_info";
    private final String KEY_ALERT_NETFAILD = "NET_T2G_CS_HANDOVER_FAILED";
    private final String KEY_ALERT_ABNORMAL = "NET_W2G_CS_ABNORMAL_SUCCESS";

    /**
     * TCP/IP采集模式
     */
    private final String KEY_TCP_IP_COLLECT = "tcp_ip_collect";
    private final String KEY_TCPIP_NAME = "tcpip_name";

    private final String KEY_Dual_Network = "Dual_Network";
    private static final String KEY_IMSEncrypt = "IMS_Encrypt";
    /**
     * 电信专项测试数据业务发起网络设置
     */
    private final String KEY_TELECOM_SETTING = "Telecom_Setting";
    /**
     * 电信专项测试语音业务发起网络设置
     */
    private final String KEY_TELECOM_VOICE_SETTING = "Telecom_Voice_Setting";

    private final String KEY_RUNTIME = "Run_Time";

    private final String KEY_MOS_BOX_CHANNEL = "key_mos_box_channel";
    private final String KEY_MOS_BOX_VREC = "key_mos_vrec";        //录音音量
    private final String KEY_MOS_BOX_VPLAY = "key_mos_vplay";        //放音音量
    private final String KEY_MOS_BOX_VFAZHI = "key_mos_vfazhi";        //阈值
    private final String KEY_IS_EXTERNAL_GPS = "key_is_external_gps";        //外置GPS

    private enum TcpIpMode {
        TCPIP_ALL(0),
        TCPIP_HEADONLY(1),
        TCPIP_OPTIMIZED(2);

        private int index;

        private TcpIpMode(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }


    /**
     * 软件卸载后还能记录的文件
     */
    private static final String FILE_LOG = "/data/local/log.txt";


    //XML reader and writer
    private MyXMLWriter writer = null;
    private Document doc = null;
    private Context mContext = null;
    private ApplicationModel mApplicationModel = ApplicationModel.getInstance();

    private ConfigRoutine() {
        writer = new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile("config_setting.xml"));
        doc = writer.getDocument();

        getReplaySpeed();
        LogUtil.w(tag, "--deviceTag:" + getDeviceTag());
    }

    public void setContext(Context context) {
        mContext = context;
    }

    private static ConfigRoutine instance = null;

    public synchronized static ConfigRoutine getInstance() {
        if (instance == null) {
            instance = new ConfigRoutine();
        }
        return instance;
    }

    /**
     * 重新从系统中加载配置文件
     */
    private void reloadConfigFile() {
        LogUtil.w(tag, "--reloadConfigFile--ctxnull:" + (mContext == null));
        if (mContext != null) {
            new AssetsWriter(mContext,
                    "config/config_setting.xml", "config_setting.xml", false).writeToConfigDir();

            writer = new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile("config_setting.xml"));
            doc = writer.getDocument();
        }
    }

    //获得当前设置文件的对象
    private Document getDoc() {
        if (!doc.hasChildNodes()) {
            doc = writer.getDocument();
        }

        return doc;
    }

    private String getValueFromXML(String item_name) {
        String result = "N/A";
        Element el = (Element) getDoc().getElementsByTagName("routine").item(0);
        if (el != null) {
            NodeList nl = el.getElementsByTagName("item");
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                if (node.getAttributes().getNamedItem("name").getNodeValue().equals(item_name)) {
                    result = node.getAttributes().getNamedItem("value").getNodeValue();
                    break;
                }
            }
        }
        return result;
    }

    private String getValueFromXML(String item_name, Object theDefault) {
        String value = getValueFromXML(item_name);

        if (value.equals("N/A")) {
            LogUtil.w(tag, "--getValueFromXML name:" + item_name + "--value:" + value + "--default:" + theDefault);
            reloadConfigFile();
            return String.valueOf(theDefault);
        }
        return value;
    }

    private void setValueIntoXML(String item_name, Object value) {
        Element el = (Element) getDoc().getElementsByTagName("routine").item(0);
        if (el != null && !value.equals("N/A")) {
            NodeList nl = el.getElementsByTagName("item");
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                if (node.getAttributes().getNamedItem("name").getNodeValue().equals(item_name)) {
                    node.getAttributes().getNamedItem("value").setNodeValue(String.valueOf(value));
                    break;
                }
            }
            writer.writeToFile(getDoc());
        }
    }


    /**
     * @return 版本类型: 0,常规版本  1,GMCC
     */
    public boolean isGmccVersion() {
        try {
            return Integer.parseInt(getValueFromXML("VersionType", 0)) == VERSION_TYPE_GMCC;
        } catch (Exception e) {
            LogUtil.w(tag, "isGmccVersion", e);
            return false;
        }
    }

    /**
     * 修改版本类型
     *
     * @param type
     */
    public void setIsGmccVersion(String type) {
        setValueIntoXML("VersionType", type);
    }

    /**
     * 是否显示WCDMA中扩展有参数信息
     *
     * @return
     */
    public boolean showWcdmaExtendParam() {
        try {
            return Integer.parseInt(getValueFromXML("wcdmaExtendParam", 0)) == 1;
        } catch (Exception e) {
            LogUtil.w(tag, "wcdmaExtendParam", e);
            return false;
        }
    }

    /**
     * 是否显示自动打点
     *
     * @return
     */
    public boolean autoMarkPoint() {
        try {
            return Integer.parseInt(getValueFromXML("autoMarkPoint", 0)) == 1;
        } catch (Exception e) {
            LogUtil.w(tag, "autoMarkPoint", e);
            return false;
        }
    }

    /**
     * 是否Walktour启动时，启动HTTP服务
     * 该服务用于响应Open TestTing System工具的生成RCU文件，关闭RCU文件等动作
     *
     * @return
     */
    public boolean toRunHttpServer(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(WalktourConst.SYS_SETTING_OTS_TEST, false);
    }


    /**
     * 非第一次
     *
     * @return
     */
    private boolean notFirstTime() {
        return getValueFromXML("notfirst", 0).equals("1");
    }

    /**
     * 第一次状态
     *
     * @param first
     */
    private void setNotFirstTime(String first) {
        setValueIntoXML("notfirst", first);
    }

    /**
     * 业务过程中是否检测脱网，0，不检测脱网，1检测脱网；默认：1
     */
    public boolean checkOutOfService() {
        try {
            return isWoneOutLicense();
        } catch (Exception e) {
            LogUtil.w(tag, "checkOutOfService", e);
            return true;
        }
    }


    /**
     * 判断是否wone权限
     *
     * @return
     */
    private boolean isWoneOutLicense() {

        return Integer.parseInt(getValueFromXML("checkOutOfService", 1)) == CHECK_OUTOFSERVICE_TRUE;
    }


    /**
     * 是否修改脱网状态写入配置文件
     *
     * @param checkType
     */
    public void setCheckOutOfService(String checkType) {
        setValueIntoXML("checkOutOfService", checkType);
    }

    /**
     * 获得系统设置中回放采样点速度系数
     *
     * @return
     */
    public String getReplaySpeed() {
        try {
            PlaybackManager.PLAYBACK_PSEED_MULTIPLE = Integer.parseInt(getValueFromXML("replaySpeed", 100));
        } catch (Exception e) {
            LogUtil.d(tag, "getReplaySpeed", e);
        }
        return String.valueOf(PlaybackManager.PLAYBACK_PSEED_MULTIPLE);
    }

    /**
     * 设置回放采样点速度系数
     *
     * @param speed
     */
    public void setReplaySpeed(String speed) {
        try {
            PlaybackManager.PLAYBACK_PSEED_MULTIPLE = Integer.parseInt(speed);
            setValueIntoXML("replaySpeed", speed);
        } catch (Exception e) {
            LogUtil.d(tag, "setReplaySpeed", e);
        }
    }

    /**
     * @return 设备名
     */
    public String getDeviceTag() {
        return getValueFromXML("device", "Android");
    }

    /**
     * 设置设备名
     */
    public void setDeviceTag(String tag) {
        setValueIntoXML("device", tag);
    }


    /**
     * @return SIM卡的手机号码
     **/
    public String getTelNum() {
        return getValueFromXML("tel", "0000");
    }

    /**
     * 设置手机号码
     */
    public void setTelNum(String num) {
        setValueIntoXML("tel", num);
    }

    /**
     * 返回文件最终存储路径
     * 根据当前室内室外测试，是否自定义路径，返回最终文件的存储路径
     * 当前返回结果如是默认路径的话是在/data/下
     */
    private String getStorgePathData(String extPath) {
        String saveDir = "";

        //如果是室内测试，则创建文件于已经加载的楼层地图相应的目录
        if (ApplicationModel.getInstance().isIndoorTest()) {
            //设定保存路径
            saveDir = ApplicationModel.getInstance().getFloorModel().getDirPath();
        } else {
             saveDir = AppFilePathUtil.getInstance().getSDCardBaseDirectory(mContext.getString(R.string.path_data), extPath);
        }

        saveDir += (saveDir.endsWith("/") ? "" : "/");
        //目录不存在则重新创建
        File file = new File(saveDir);
        if (!file.exists()) {
            file.mkdirs();
        }

        return saveDir;
    }

    /**
     * 获得task存储目录
     *
     * @return
     */
    public String getStorgePathTask() {
        return getStorgePathData(mContext.getString(R.string.path_task));
    }


    /**
     * 获得ddib存储目录
     *
     * @return 目录文件
     */
    public String getStorgePathDdib() {
        //2013.12.18 由于部分手机内存映射问题导致数据集写在/sdcard/上出错，全部默认放在手机
        return AppFilePathUtil.getInstance().getAppFilesDirectory("data", "ddib");
    }


    /**
     * @return 文件存储绝对路径，/data/data/com.walktour.gui/files/config/或者/sdcard/walktour/
     **/
    public String getStorgePath() {
        return AppFilePathUtil.getInstance().getSDCardBaseDirectory();
     }


    /**
     * 根据联通招标需要返回当前文件的序号
     * 该序号从软件安装开始从0递增
     *
     * @return
     */
    public int getFileNum() {
        int num = -1;

        FileInputStream inStream = null;
        File file = new File(FILE_LOG);
        if (!file.exists()) {
            //创建文件
            UtilsMethod.runRootCommand(String.format(Locale.getDefault(), "echo %d >%s", 1, FILE_LOG));
            UtilsMethod.runRootCommand(String.format(Locale.getDefault(), "chmod 666 %s", FILE_LOG));
        }


        try {
            inStream = new FileInputStream(file);
            String line = null;
            try {
                line = new BufferedReader(new InputStreamReader(inStream)).readLine();
                if (line != null) {
                    return Integer.parseInt(line);
                }
            } catch (Exception e) {
                LogUtil.w(tag, "getFileNum", e);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            setFileNum(1);
            return 1;
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return num;
    }

    /**
     * 是否可执行像锁网等AutoTest脚本
     *
     * @return
     */
    public boolean canRunScript() {
        try {
            return getValueFromXML("canrunscript", 0).equals("1");
        } catch (Exception e) {
            LogUtil.w(tag, "canRunScript", e);
            return false;
        }
    }

    /**
     * 文件分割类型 0 ，按大小分割，1按时长分割
     *
     * @return
     * @author tangwq
     */
    public int getSplitType() {
        try {
            return Integer.parseInt(getValueFromXML("SplitType", 1));
        } catch (Exception e) {
            LogUtil.w(tag, "getSplitType", e);
            return 0;
        }
    }

    /**
     * 返回数据上传途径
     *
     * @return
     * @author tangwq
     */
    public int getDataUploadWay() {
        try {
            return Integer.parseInt(getValueFromXML("DataUploadWay", 0));
        } catch (Exception e) {
            LogUtil.w(tag, "getDataUploadWay", e);
            return 0;
        }
    }

    /**
     * 获得RCU文件分割大小
     * 当值为0时表示不分割
     *
     * @return
     * @author tangwq
     */
    public String getFileSize() {
        try {
            return getValueFromXML("filesize", 3600);
        } catch (Exception e) {
            LogUtil.w(tag, "getFileSize", e);
            return "";
        }
    }

    /**
     * @param value 设置文件存储的绝对路径,STORGE_IN_PHONE或者STORGE_IN_CARD
     */
    public void setStorgePath(String value) {
        setValueIntoXML("storge", value);
    }

    /**
     * 设置文件生成顺序号，当前序号可直接用并且将新的序号写入
     *
     * @param num
     */
    public void setFileNum(int num) {
//		setValueIntoXML("filenamenum", String.valueOf(num));
        //创建文件
        UtilsMethod.runRootCommand(String.format(Locale.getDefault(), "echo %d >%s", num, FILE_LOG));
    }

    /**
     * 设置是否可执行锁网脚本,目前只有ME860有效
     *
     * @param canRunScript
     * @author tangwq
     */
    public void setCanRunScript(boolean canRunScript) {
        setValueIntoXML("canrunscript", canRunScript ? "1" : "0");
    }

    /**
     * 文件分割类型 0，按大小分割，1按时长分割
     *
     * @param type
     * @author tangwq
     */
    public void setSplitType(String type) {
        setValueIntoXML("SplitType", type);
    }

    /**
     * 设置文件分割大小,0时表示不分割
     *
     * @param filesize
     * @author tangwq
     */
    public void setFileSize(String filesize) {
        setValueIntoXML("filesize", filesize);
    }

    /**
     * 返回WAP测试任务的接入点名称,
     */
    public String getMapHeight() {
        Element el = (Element) getDoc().getElementsByTagName("basemap").item(0);
        if(null != el){
            NodeList nl = el.getElementsByTagName("item");
            for (int i = 0; i < nl.getLength(); i++) {
                Node nd = nl.item(i);
                if (nd.getAttributes().getNamedItem("name").getNodeValue().equals("mapHeight")) {
                    return nd.getAttributes().getNamedItem("value").getNodeValue();
                }
            }
        }
        return null;
    }


    /**
     * 返回数据业务的接入点名称
     */
    public String getMapAzimuth() {
        Element el = (Element) getDoc().getElementsByTagName("basemap").item(0);
        if(null != el){
            NodeList nl = el.getElementsByTagName("item");
            for (int i = 0; i < nl.getLength(); i++) {
                Node nd = nl.item(i);
                if (nd.getAttributes().getNamedItem("name").getNodeValue().equals("mapAzimuth")) {
                    return nd.getAttributes().getNamedItem("value").getNodeValue();
                }
            }
        }
        return null;
    }


    public String getLatitude() {
        Element el = (Element) getDoc().getElementsByTagName("basemap").item(0);
        if(null != el){
            NodeList nl = el.getElementsByTagName("item");
            for (int i = 0; i < nl.getLength(); i++) {
                Node nd = nl.item(i);
                if (nd.getAttributes().getNamedItem("name").getNodeValue().equals("latitude")) {
                    return nd.getAttributes().getNamedItem("value").getNodeValue();
                }
            }
        }
        return null;
    }

    public void setLatitude(String value) {
        Element el = (Element) getDoc().getElementsByTagName("basemap").item(0);
        if(null != el){
            NodeList nl = el.getElementsByTagName("item");
            for (int i = 0; i < nl.getLength(); i++) {
                Node nd = nl.item(i);
                if (nd.getAttributes().getNamedItem("name").getNodeValue().equals("latitude")) {
                    nd.getAttributes().getNamedItem("value").setNodeValue(value);
                }
            }
            writer.writeToFile(getDoc());
        }
    }


    /**
     * 返回数据业务的接入点名称
     */
    public String getMapPn() {
        Element el = (Element) getDoc().getElementsByTagName("basemap").item(0);
        if(null != el){
            NodeList nl = el.getElementsByTagName("item");
            for (int i = 0; i < nl.getLength(); i++) {
                Node nd = nl.item(i);
                if (nd.getAttributes().getNamedItem("name").getNodeValue().equals("mapPn")) {
                    return nd.getAttributes().getNamedItem("value").getNodeValue();
                }
            }
        }
        return null;
    }


    /**
     * 是否初始化系统
     *
     * @param context
     * @return
     */
    public boolean isInitPW(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_INITPW)) {
            Editor editor = share.edit();
            //常规版本默认为允许数据语音串行
            editor.putBoolean(KEY_INITPW, false);
            editor.apply();
        }
        return share.getBoolean(KEY_INITPW, Boolean.parseBoolean(getValueFromXML(KEY_INITPW, false))) && notFirstTime();
    }


    public boolean isAbnormal(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(context.getPackageName() + "_alarm",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_ALERT_ABNORMAL)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_ALERT_ABNORMAL, false);
            editor.apply();
        }

        return share.getBoolean(KEY_ALERT_ABNORMAL, false);
    }

    public void setAbnormal(Context context, boolean enable) {
        SharedPreferences share = context
                .getSharedPreferences(context.getPackageName() + "_alarm",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_ALERT_ABNORMAL)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_ALERT_ABNORMAL, enable);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_ALERT_ABNORMAL, enable);
        editor.apply();
    }

    private boolean getNetAlert(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(context.getPackageName() + "_alarm",
                        Context.MODE_PRIVATE);
        if (share.contains(KEY_ALERT_NETFAILD)) {
            return share.getBoolean(KEY_ALERT_NETFAILD, Boolean.parseBoolean(getValueFromXML(KEY_ALERT_NETFAILD, false)));
        }
        return Boolean.parseBoolean(getValueFromXML(KEY_ALERT_NETFAILD, false));
    }

    private void setAlertNet(Context context, boolean enable) {
        SharedPreferences share = context
                .getSharedPreferences(context.getPackageName() + "_alarm",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_ALERT_NETFAILD)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_ALERT_NETFAILD, enable);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_ALERT_NETFAILD, enable);
        editor.apply();

        setValueIntoXML(KEY_ALERT_NETFAILD, String.valueOf(enable));
    }

    /**
     * 检查当前时间是否需要在线判断权限文件是否存在
     *
     * @param context
     * @return
     */
    public boolean checkLineCheckLic(Context context) {
        boolean needLineCheck = false;
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);

        long checkTime = 0;
        if (share.contains(KEY_EGN_AUTO_CHE_LIN)) {
            checkTime = share.getLong(KEY_EGN_AUTO_CHE_LIN, 0);
        } else {
            checkTime = Long.parseLong(getValueFromXML(KEY_EGN_AUTO_CHE_LIN, 0));
        }

        if (checkTime > 0) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - checkTime > limitLicDate) {
                needLineCheck = true;
            }
        } else {
            needLineCheck = true;
        }
        return needLineCheck;
    }

    //设置当前强制访问网络时间
    public void setLineCheckLi(Context context) {
        long currentTime = System.currentTimeMillis();
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_EGN_AUTO_CHE_LIN)) {
            Editor editor = share.edit();
            editor.putLong(KEY_EGN_AUTO_CHE_LIN, currentTime);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putLong(KEY_EGN_AUTO_CHE_LIN, currentTime);
        editor.apply();

        setValueIntoXML(KEY_EGN_AUTO_CHE_LIN, currentTime);
    }

    private String getMyString(Context context) {
        String times = String.valueOf(MyPhoneState.getFisrtInstallTime(context));
        String dev = MyPhoneState.getInstance().getDeviceId(context);
        String md5Source = dev.substring(dev.length() / 3 * 2)
                + times.substring(0, times.length() / 2)
                + dev.substring(0, dev.length() / 3)
                + times.substring(times.length() / 2)
                + dev.substring(dev.length() / 3, dev.length() / 3 * 2);
        String entest = UtilsMethod.getMD5(md5Source);
        LogUtil.w(tag, "--getMyString:" + md5Source + "--entest:" + entest);
        return entest;
    }

    /**
     * 设置当前在线校验成功
     *
     * @param context
     */
    public void setGoOnType(Context context) {
        setGoOnType(context, true);
    }

    public boolean checkGoOnType(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (share.contains(KEY_GEN_AUTODELLOG)) {
            String goonStr = share.getString(KEY_GEN_AUTODELLOG, null);
            String entest = getMyString(context);
            if (goonStr != null && entest != null && entest.equals(goonStr)) {
                return getNetAlert(context);
            }
        }

        LogUtil.w(tag, "--preferences auto_goonging null--");
        return Boolean.parseBoolean(getValueFromXML(KEY_GEN_AUTODELLOG, false));
    }

    public void setGoOnType(Context context, boolean enable) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        String entest = getMyString(context);
        if (!enable) {
            String errMsg = UtilsMethod.getMD5("walktour");
            entest = entest.substring(0, entest.length() / 2) + errMsg.substring(errMsg.length() / 2)
                    + entest.substring(entest.length() / 2) + errMsg.substring(0, errMsg.length() / 2);
        }
        if (!share.contains(KEY_GEN_AUTODELLOG)) {
            Editor editor = share.edit();
            editor.putString(KEY_GEN_AUTODELLOG, entest);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putString(KEY_GEN_AUTODELLOG, entest);
        editor.apply();

        setValueIntoXML(KEY_GEN_AUTODELLOG, entest);
        setAlertNet(context, enable);
    }

    public String getAutoTim(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_AUTOTIMES)) {
            Editor editor = share.edit();
            editor.putString(KEY_GEN_AUTOTIMES, "");
            editor.apply();
        }
        return share.getString(KEY_GEN_AUTOTIMES, "");
    }

    public void setAutoTim(Context context, String valueStr) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_AUTOTIMES)) {
            Editor editor = share.edit();
            editor.putString(KEY_GEN_AUTOTIMES, valueStr);
            editor.apply();
        }

        Editor editor = share.edit();
        editor.putString(KEY_GEN_AUTOTIMES, valueStr);
        editor.apply();
    }

    public String getAuthValueStr(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_AUTHVALUE)) {
            Editor editor = share.edit();
            editor.putString(KEY_GEN_AUTHVALUE, "");
            editor.apply();
        }
        return share.getString(KEY_GEN_AUTHVALUE, "");
    }

    public void setAuthValueStr(Context context, String valueStr) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_AUTHVALUE)) {
            Editor editor = share.edit();
            editor.putString(KEY_GEN_AUTHVALUE, valueStr);
            editor.apply();
        }

        Editor editor = share.edit();
        editor.putString(KEY_GEN_AUTHVALUE, valueStr);
        editor.apply();
    }

    public String getAuth2ValueStr(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_AUTH2VALUE)) {
            Editor editor = share.edit();
            editor.putString(KEY_GEN_AUTH2VALUE, "");
            editor.apply();
        }
        return share.getString(KEY_GEN_AUTH2VALUE, "");
    }

    private void setAuth2ValueStr(Context context, String valueStr) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_AUTH2VALUE)) {
            Editor editor = share.edit();
            editor.putString(KEY_GEN_AUTH2VALUE, valueStr);
            editor.apply();
        }

        Editor editor = share.edit();
        editor.putString(KEY_GEN_AUTH2VALUE, valueStr);
        editor.apply();
    }

    public byte[] getAutoValue(Context context) {
        String l1 = getAutoValueStr(context);
        String l2 = getAuto2ValueStr(context);
        String l3 = getAuth2ValueStr(context);
        String autoStr = l1 + UtilsMethod.jem(l2) + l3;

        return autoStr.getBytes();
    }

    public String getAutoValueStr(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_AUTOVALUE)) {
            Editor editor = share.edit();
            editor.putString(KEY_GEN_AUTOVALUE, "");
            editor.apply();
        }
        return share.getString(KEY_GEN_AUTOVALUE, "");
    }

    public void setAutoValue(Context context, String powerStr) {
        int midLen = 30;
        String l1 = "", l2 = "", l3 = "";
        if (powerStr.length() > midLen + 1) {
            int subint = (powerStr.length() - midLen) / 2;
            l1 = powerStr.substring(0, subint);
            l2 = powerStr.substring(subint, subint + midLen);
            l3 = powerStr.substring(subint + midLen);
        } else {
            l1 = powerStr;
        }

        setAutoValueStr(context, l1);
        setAuto2ValueStr(context, UtilsMethod.jam(l2));
        setAuth2ValueStr(context, l3);
    }

    private void setAutoValueStr(Context context, String valueStr) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_AUTOVALUE)) {
            Editor editor = share.edit();
            editor.putString(KEY_GEN_AUTOVALUE, valueStr);
            editor.apply();
        }

        Editor editor = share.edit();
        editor.putString(KEY_GEN_AUTOVALUE, valueStr);
        editor.apply();
    }

    public String getAuto2ValueStr(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(context.getPackageName(),
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_AUTO2VALUE)) {
            Editor editor = share.edit();
            editor.putString(KEY_GEN_AUTO2VALUE, "");
            editor.apply();
        }
        return share.getString(KEY_GEN_AUTO2VALUE, "");
    }

    private void setAuto2ValueStr(Context context, String valueStr) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName(),
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_AUTO2VALUE)) {
            Editor editor = share.edit();
            editor.putString(KEY_GEN_AUTO2VALUE, valueStr);
            editor.apply();
        }

        Editor editor = share.edit();
        editor.putString(KEY_GEN_AUTO2VALUE, valueStr);
        editor.apply();
    }

    //设置当前登陆时间
    public void setNetTimes(Context context, long nettime) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        String str = UtilsMethod.jaem2(UtilsMethod.jam(String.valueOf(nettime)));
        if (!share.contains(KEY_NETTIME_SETTING)) {
            Editor editor = share.edit();
            editor.putString(KEY_NETTIME_SETTING, str);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putString(KEY_NETTIME_SETTING, str);
        editor.apply();

        setValueIntoXML(KEY_NETTIME_SETTING, str);

        UtilsMethod.WriteFile(Environment.getExternalStorageDirectory().getPath() + UtilsMethod.jem(UtilsMethod.jaem2(BuildPower.TP)),
                UtilsMethod.jem(UtilsMethod.jaem2(BuildPower.TN)), Base64.encodeToString(str.getBytes()));
    }

    //获得保存的上次登陆时间
    public long getNetTimes(Context context) {
        try {
            SharedPreferences share = context
                    .getSharedPreferences(context.getPackageName() + "_preferences",
                            Context.MODE_PRIVATE);

            String binValue = UtilsMethod.ReadFile(Environment.getExternalStorageDirectory().getPath() + UtilsMethod.jem(UtilsMethod.jaem2(BuildPower.TP))
                    + UtilsMethod.jem(UtilsMethod.jaem2(BuildPower.TN)));

            if (share.contains(KEY_NETTIME_SETTING)) {
                String str = share.getString(KEY_NETTIME_SETTING, null);
                if (str != null) {
                    if (binValue != null) {
                        if ((new String(Base64.decode(binValue))).equals(str)) {
                            return Long.parseLong(UtilsMethod.jem(UtilsMethod.jaem2(str)));
                        }
                    } else {
                        return Long.parseLong(UtilsMethod.jem(UtilsMethod.jaem2(str)));
                    }
                }
            } else if (binValue != null) {
                return Long.parseLong(UtilsMethod.jem(UtilsMethod.jaem2(new String(Base64.decode(binValue)))));
            } else {
                return Long.parseLong(getValueFromXML(KEY_NETTIME_SETTING, -1));
            }
        } catch (Exception e) {
            LogUtil.w(tag, "getNetTimes", e);
        }
        return -1;
    }

    /**
     * 设置绑定网卡,,0不绑定，1绑定 (具体绑定wifi还是ppp是由模板决定的)
     */
    public void setNetInterface(Context context, int netInterface) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_NET_INTERFACE)) {
            Editor editor = share.edit();
            editor.putInt(KEY_NET_INTERFACE, netInterface);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putInt(KEY_NET_INTERFACE, netInterface);
        editor.apply();

        setValueIntoXML(KEY_NET_INTERFACE, netInterface);
    }


    /**
     * 是否绑定网卡,0不绑定，1绑定 (具体绑定wifi还是ppp是由模板决定的)
     */
    public int getNetInterface(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_NET_INTERFACE)) {
            Editor editor = share.edit();
            editor.putInt(KEY_NET_INTERFACE, Integer.parseInt(getValueFromXML(KEY_NET_INTERFACE, NET_INTERFACE_UNBIND)));
            editor.apply();
        }
        return share.getInt(KEY_NET_INTERFACE, Integer.parseInt(getValueFromXML(KEY_NET_INTERFACE, NET_INTERFACE_UNBIND)));
    }


    /**
     * 设置是否自动清理数据
     */
    public void setAutoDelete(Context context, boolean delete) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_AUTO_DELETE)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_AUTO_DELETE, delete);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_AUTO_DELETE, delete);
        editor.apply();

        setValueIntoXML(KEY_AUTO_DELETE, delete);
    }

    /**
     * 是否自动清理数据
     */
    public boolean isAutoDelete(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_AUTO_DELETE)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_AUTO_DELETE, Boolean.valueOf(getValueFromXML(KEY_AUTO_DELETE, getDefaultAutoDel())));
            editor.apply();
        }
        return share.getBoolean(KEY_AUTO_DELETE, Boolean.valueOf(getValueFromXML(KEY_AUTO_DELETE, getDefaultAutoDel())));
    }

    private boolean getDefaultAutoDel() {
        return !ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.WOnePro);
    }

    /**
     * 设置是否生成RCU
     */
    public void setGenRCU(Context context, boolean accept) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_RCU)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_GEN_RCU, accept);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_GEN_RCU, accept);
        editor.apply();

        setValueIntoXML(KEY_GEN_RCU, accept);
    }

    /**
     * 是否生成RCU
     */
    public boolean isGenRCU(Context context) {
        SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences",
                Context.MODE_PRIVATE);
        if (ApplicationModel.getInstance().isGeneralMode())
            return false;
        if (!share.contains(KEY_GEN_RCU)) {
            Editor editor = share.edit();
            boolean defaultValue = ApplicationModel.getInstance().isHuaWeiTest();
            editor.putBoolean(KEY_GEN_RCU, Boolean.parseBoolean(getValueFromXML(KEY_GEN_RCU, defaultValue)));
            editor.apply();
        }
        return share.getBoolean(KEY_GEN_RCU, Boolean.parseBoolean(getValueFromXML(KEY_GEN_RCU, true)));
    }

    /**
     * 设置是否生成DTLog
     */
    public void setGenDtLog(Context context, boolean accept) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_DTLOG)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_GEN_DTLOG, accept);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_GEN_DTLOG, accept);
        editor.apply();

        setValueIntoXML(KEY_GEN_DTLOG, accept);
    }

    /**
     * 是否生成DTLog
     */
    public boolean isGenDTLog(Context context) {
        //加上权限判断，如果无DTLog权限，则不生成DTLog
        if ((mApplicationModel.isAtu() || mApplicationModel.isBtu())) {
            SharedPreferences share = context
                    .getSharedPreferences(
                            context.getPackageName() + "_preferences",
                            Context.MODE_PRIVATE);
            if (!share.contains(KEY_GEN_DTLOG)) {
                Editor editor = share.edit();
                editor.putBoolean(KEY_GEN_DTLOG, Boolean.parseBoolean(getValueFromXML(KEY_GEN_DTLOG, returnDefaultDtlog())));
                editor.apply();
            }
            return share.getBoolean(KEY_GEN_DTLOG, Boolean.parseBoolean(getValueFromXML(KEY_GEN_DTLOG, returnDefaultDtlog())));

        } else {
            return false;
        }
    }

    /**
     * 根据当前是否有BTU权限决定返回默认值为
     *
     * @return
     */
    private boolean returnDefaultDtlog() {
        LogUtil.w(tag, "--getdefault dtlog--"
                + ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.Btu));
        return ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.Btu) ? true : false;
    }

    /**
     * 设置是否生成DCF
     */
    public void setGenDCF(Context context, boolean accept) {
        SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_DCF)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_GEN_DCF, accept);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_GEN_DCF, accept);
        editor.apply();

        setValueIntoXML(KEY_GEN_DCF, accept);
    }

    /**
     * 是否生成DCF
     *
     * @param context
     * @return
     */
    public boolean isGenDCF(Context context) {
        //加上权限判断，如果无DCF权限，则不生成DCF
        if (mApplicationModel.hasDcf()) {
            SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences",
                    Context.MODE_PRIVATE);
            if (ApplicationModel.getInstance().isGeneralMode())
                return true;
            if (!share.contains(KEY_GEN_DCF)) {
                Editor editor = share.edit();
                editor.putBoolean(KEY_GEN_DCF, Boolean.parseBoolean(getValueFromXML(KEY_GEN_DCF, getDefaultDCF())));
                editor.apply();
            }
            return share.getBoolean(KEY_GEN_DCF, Boolean.parseBoolean(getValueFromXML(KEY_GEN_DCF, getDefaultDCF())));
        } else {
            return false;
        }

    }

    //默认都生成DCF文件
    private boolean getDefaultDCF() {
        return true;//(ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.WOnePro) ? true : false);
    }

    /**
     * 设置是否生成DCF
     */
    public void setGenCU(Context context, boolean accept) {
        SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_CU)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_GEN_CU, accept);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_GEN_CU, accept);
        editor.apply();

        setValueIntoXML(KEY_GEN_CU, accept);
    }

    /**
     * 是否生成CU
     *
     * @param context
     * @return
     */
    public boolean isGenCU(Context context) {
        //如无CU权限，则不生成CU数据文件
        if (mApplicationModel.showInfoTypeCu()) {
            SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
            if (!share.contains(KEY_GEN_CU)) {
                Editor editor = share.edit();
                editor.putBoolean(KEY_GEN_CU, Boolean.parseBoolean(getValueFromXML(KEY_GEN_CU, getDefaultCU())));
                editor.apply();
            }
            return share.getBoolean(KEY_GEN_CU, Boolean.parseBoolean(getValueFromXML(KEY_GEN_CU, getDefaultCU())));
        } else {
            return false;
        }
    }

    private boolean getDefaultCU() {
        return false;
    }

    /**
     * 设置是否生成ECTI
     */
    public void setGenECTI(Context context, boolean accept) {
        SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_ORG_ECTI)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_GEN_ORG_ECTI, accept);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_GEN_ORG_ECTI, accept);
        editor.apply();

        setValueIntoXML(KEY_GEN_ORG_ECTI, accept);
    }

    /**
     * 是否生成ECTI
     *
     * @param context
     * @return
     */
    public boolean isGenECTI(Context context) {
        //如无ECTI权限，则不生成ECTI文件
        if (mApplicationModel.showInfoTypeEcti()) {
            SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
            if (!share.contains(KEY_GEN_ORG_ECTI)) {
                Editor editor = share.edit();
                editor.putBoolean(KEY_GEN_ORG_ECTI, Boolean.parseBoolean(getValueFromXML(KEY_GEN_ORG_ECTI, getDefaultECTI())));
                editor.apply();
            }
            return share.getBoolean(KEY_GEN_ORG_ECTI, Boolean.parseBoolean(getValueFromXML(KEY_GEN_ORG_ECTI, getDefaultECTI())));
        } else {
            return false;
        }
    }

    /**
     * 设置是否生成ECTI
     */
    public void setGenOTS(Context context, boolean accept) {
        SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_OTS)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_GEN_OTS, accept);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_GEN_OTS, accept);
        editor.apply();

        setValueIntoXML(KEY_GEN_OTS, accept);
    }

    /**
     * 是否生成ECTI
     *
     * @param context
     * @return
     */
    public boolean isGenOTS(Context context) {
        //如无ECTI权限，则不生成ECTI文件
//        if (mApplicationModel.showInfoTypeOTS()) {//OTS默认无权限，直接显示
            SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
            if (!share.contains(KEY_GEN_OTS)) {
                Editor editor = share.edit();
                editor.putBoolean(KEY_GEN_OTS, Boolean.parseBoolean(getValueFromXML(KEY_GEN_OTS, false)));
                editor.apply();
            }
            return share.getBoolean(KEY_GEN_OTS, Boolean.parseBoolean(getValueFromXML(KEY_GEN_OTS, false)));
//        } else {
//            return false;
//        }
    }

    private boolean getDefaultECTI() {
        return false;
    }

    /**
     * 设置是否生成Org rcu
     */
    public void setGenOrgRcu(Context context, boolean accept) {
        SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        if (!share.contains(KEY_GEN_ORG_RCU)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_GEN_ORG_RCU, accept);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_GEN_ORG_RCU, accept);
        editor.apply();

        setValueIntoXML(KEY_GEN_ORG_RCU, accept);
    }

    /**
     * 是否生成org rcu
     *
     * @param context
     * @return
     */
    public boolean isGenOrgRcu(Context context) {
        //如无OrgRcu权限，则不生成OrgRcu文件
        if (mApplicationModel.hasOrgRcu()) {
            SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
            if (!share.contains(KEY_GEN_ORG_RCU)) {
                Editor editor = share.edit();
                editor.putBoolean(KEY_GEN_ORG_RCU, Boolean.parseBoolean(getValueFromXML(KEY_GEN_ORG_RCU, false)));
                editor.apply();
            }
            return share.getBoolean(KEY_GEN_ORG_RCU, Boolean.parseBoolean(getValueFromXML(KEY_GEN_ORG_RCU, false)));
        } else {
            return false;
        }

    }

    /**
     * 设置TCP/IP采集模式
     *
     * @param context
     * @param type
     */
    public void setTcpIpCollect(Context context, int type) {
        SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        if (!share.contains(KEY_TCP_IP_COLLECT)) {
            Editor editor = share.edit();
            editor.putInt(KEY_TCP_IP_COLLECT, type);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putInt(KEY_TCP_IP_COLLECT, type);
        editor.apply();

        setValueIntoXML(KEY_TCP_IP_COLLECT, type);
    }

    /**
     * @param context
     * @return
     */
    public int getTcpIpCollect(Context context) {
        SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences",
                Context.MODE_PRIVATE);
        int defaultValue = TcpIpMode.TCPIP_OPTIMIZED.getIndex();
        if (!share.contains(KEY_TCP_IP_COLLECT)) {
            Editor editor = share.edit();
            editor.putInt(KEY_TCP_IP_COLLECT, Integer.parseInt(getValueFromXML(KEY_TCP_IP_COLLECT, defaultValue)));
            editor.apply();
        }
        return share.getInt(KEY_TCP_IP_COLLECT, Integer.parseInt(getValueFromXML(KEY_TCP_IP_COLLECT, defaultValue)));
    }

    /**
     * 设置锁网模式
     */
    public void setIMSEncrypted(Context context, int netWorkType) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_IMSEncrypt)) {
            Editor editor = share.edit();
            editor.putInt(KEY_IMSEncrypt, netWorkType);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putInt(KEY_IMSEncrypt, netWorkType);
        editor.apply();
    }

    /**
     * 获取锁网模式
     */
    public int getIMSEncrypted(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_IMSEncrypt)) {
            Editor editor = share.edit();
            editor.putInt(KEY_IMSEncrypt, 0);
            editor.apply();
        }
        return share.getInt(KEY_IMSEncrypt, 0);
    }

    /**
     * 设置锁网模式
     */
    public void setDual_Network(Context context, int netWorkType) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_Dual_Network)) {
            Editor editor = share.edit();
            editor.putInt(KEY_Dual_Network, netWorkType);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putInt(KEY_Dual_Network, netWorkType);
        editor.apply();

        setValueIntoXML(KEY_Dual_Network, netWorkType);
    }

    /**
     * 获取锁网模式
     */
    public int isDual_Network(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_Dual_Network)) {
            Editor editor = share.edit();
            editor.putInt(KEY_Dual_Network, Integer.parseInt(getValueFromXML(KEY_Dual_Network, 1)));
            editor.apply();
        }
        return share.getInt(KEY_Dual_Network, Integer.parseInt(getValueFromXML(KEY_Dual_Network, 1)));
    }

    /**
     * 电信下切数据网络设置
     */
    private TelecomSetting telecomDataSet = null;
    /**
     * 电信下切语音发起网络设置
     */
    private TelecomSetting telecomVoiceSet = null;

    /**
     * 获得当前电信数据业务设置信息枚举项
     *
     * @param context
     * @return
     */
    public TelecomSetting getTelecomDataNetSet(Context context) {
        if (telecomDataSet == null) {
            telecomDataSet = TelecomSetting.getTelecomSetByIndex(getTelecomDataNetSetting(context));
        }

        return telecomDataSet;
    }

    /**
     * 设置电信数据业务设置配置信息
     */
    public void setTelecomDataNetSetting(Context context, int cuccSet) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_TELECOM_SETTING)) {
            Editor editor = share.edit();
            editor.putInt(KEY_TELECOM_SETTING, cuccSet);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putInt(KEY_TELECOM_SETTING, cuccSet);
        editor.apply();

        telecomDataSet = TelecomSetting.getTelecomSetByIndex(cuccSet);
    }

    /**
     * 获得当前电信数据业务设置配置信息
     *
     * @param context
     * @return
     */
    public int getTelecomDataNetSetting(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_TELECOM_SETTING)) {
            Editor editor = share.edit();
            editor.putInt(KEY_TELECOM_SETTING, 0);
            editor.apply();
        }
        return share.getInt(KEY_TELECOM_SETTING, 0);
    }


    /**
     * 获得当前电信语音业务设置信息枚举项
     *
     * @param context
     * @return
     */
    public TelecomSetting getTelecomVoiceNetSet(Context context) {
        /*if (telecomVoiceSet == null) {
            telecomVoiceSet = TelecomSetting.getTelecomSetByIndex(getTelecomVoiceNetSetting(context));
        }

        return telecomVoiceSet;*/
        return TelecomSetting.Normal;
    }

    /**
     * 设置电信语音业务设置配置信息
     */
    public void setTelecomVoiceNetSetting(Context context, int cuccSet) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_TELECOM_VOICE_SETTING)) {
            Editor editor = share.edit();
            editor.putInt(KEY_TELECOM_VOICE_SETTING, cuccSet);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putInt(KEY_TELECOM_VOICE_SETTING, cuccSet);
        editor.apply();

        telecomVoiceSet = TelecomSetting.getTelecomSetByIndex(cuccSet);
    }

    /**
     * 获得当前电信语音业务设置配置信息
     *
     * @param context
     * @return
     */
    public int getTelecomVoiceNetSetting(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_TELECOM_VOICE_SETTING)) {
            Editor editor = share.edit();
            editor.putInt(KEY_TELECOM_VOICE_SETTING, 0);
            editor.apply();
        }
        return share.getInt(KEY_TELECOM_VOICE_SETTING, 0);
    }



    /**
     * 设置运行总时长
     */
    public synchronized void setRunTime(Context context, long runTime) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_RUNTIME)) {
            Editor editor = share.edit();
            editor.putLong(KEY_RUNTIME, 0l);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putLong(KEY_RUNTIME, runTime);
        editor.apply();

        setValueIntoXML(KEY_RUNTIME, runTime);
    }

    /**
     * 获取运行总时长
     */
    public synchronized long getRunTime(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_RUNTIME)) {
            Editor editor = share.edit();
            editor.putLong(KEY_RUNTIME, Long.parseLong(getValueFromXML(KEY_RUNTIME, 0l)));
            editor.apply();
        }
        return share.getLong(KEY_RUNTIME, Long.parseLong(getValueFromXML(KEY_RUNTIME, 0l)));
    }


    /**
     * 设置是否自动接听来电
     */
    public void setAcceptCall(Context context, boolean accept) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_ACCEPT_CALL)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_ACCEPT_CALL, accept);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_ACCEPT_CALL, accept);
        editor.apply();

        setValueIntoXML(KEY_ACCEPT_CALL, accept);
    }

    /**
     * 是否自动接听来电
     */
    public boolean isAcceptCall(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_ACCEPT_CALL)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_ACCEPT_CALL, Boolean.parseBoolean(getValueFromXML(KEY_ACCEPT_CALL, false)));
            editor.apply();
        }
        return share.getBoolean(KEY_ACCEPT_CALL, Boolean.parseBoolean(getValueFromXML(KEY_ACCEPT_CALL, false)));
    }

    /**
     * 设置暂停时是否继续测试
     */
    public void setPuaseKeepTest(Context context, boolean keepTest) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_PUASE_KEEP_TEST)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_PUASE_KEEP_TEST, keepTest);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_PUASE_KEEP_TEST, keepTest);
        editor.apply();

        setValueIntoXML(KEY_PUASE_KEEP_TEST, keepTest);
    }

    /**
     * 是否暂停时是否继续测试
     */
    public boolean isPuaseKeepTest(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_PUASE_KEEP_TEST)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_PUASE_KEEP_TEST, Boolean.parseBoolean(getValueFromXML(KEY_PUASE_KEEP_TEST, false)));
            editor.apply();
        }
        return share.getBoolean(KEY_PUASE_KEEP_TEST, Boolean.parseBoolean(getValueFromXML(KEY_PUASE_KEEP_TEST, false)));
    }

    /**
     * 设置暂停时是否不保存数据
     */
    public void setPuaseNoData(Context context, boolean noData) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_PUASE_NO_DATA)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_PUASE_NO_DATA, noData);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_PUASE_NO_DATA, noData);
        editor.apply();

        setValueIntoXML(KEY_PUASE_NO_DATA, noData);
    }

    /**
     * 是否暂停时不保存数据
     */
    public boolean isPuaseNoData(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_PUASE_NO_DATA)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_PUASE_NO_DATA, Boolean.parseBoolean(getValueFromXML(KEY_PUASE_NO_DATA, false)));
            editor.apply();
        }
        return share.getBoolean(KEY_PUASE_NO_DATA, Boolean.parseBoolean(getValueFromXML(KEY_PUASE_NO_DATA, false)));
    }


    /**
     * 设置暂停时是否不保存数据
     */
    public void setPuaseSaveData(Context context, boolean noData) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_PUASE_SAVE_DATA)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_PUASE_SAVE_DATA, noData);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_PUASE_SAVE_DATA, noData);
        editor.apply();

        setValueIntoXML(KEY_PUASE_SAVE_DATA, noData);
    }

    /**
     * 是否暂停时不保存数据
     */
    public boolean isPuaseSaveData(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_PUASE_SAVE_DATA)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_PUASE_SAVE_DATA, Boolean.parseBoolean(getValueFromXML(KEY_PUASE_SAVE_DATA, false)));
            editor.apply();
        }
        return share.getBoolean(KEY_PUASE_SAVE_DATA, Boolean.parseBoolean(getValueFromXML(KEY_PUASE_SAVE_DATA, false)));
    }

    /**
     * 设置测试是否切换飞行模式
     */
    public void setSwitchAirplan(Context context, boolean airplan) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_SWITCHAIRPLAN)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_SWITCHAIRPLAN, airplan);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_SWITCHAIRPLAN, airplan);
        editor.apply();

        setValueIntoXML(KEY_SWITCHAIRPLAN, airplan);
    }

    /**
     * 是否测试开始时切换飞行模式
     */
    public boolean isSwitchAirplan(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_SWITCHAIRPLAN)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_SWITCHAIRPLAN, false);
            editor.apply();
        }
        return share.getBoolean(KEY_SWITCHAIRPLAN, false);
    }

    /**
     * 设置是否使用扫频仪测试
     */
    public void setUseScanner(Context context, boolean useScanner) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_USE_SCANNER)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_USE_SCANNER, useScanner);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_USE_SCANNER, useScanner);
        editor.apply();

//        setValueIntoXML(KEY_USE_SCANNER, useScanner);
    }

    /**
     * 是否使用扫频仪测试
     */
    public boolean isUseScanner(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_USE_SCANNER)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_USE_SCANNER, false);
            editor.apply();
        }
        return share.getBoolean(KEY_USE_SCANNER, false);
    }

    /**
     * 设置是否使用TSMA扫频仪测试
     */
    public void setUseTSMAScanner(Context context, boolean useScanner) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_USE_SCANNER_TSMA)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_USE_SCANNER_TSMA, useScanner);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_USE_SCANNER_TSMA, useScanner);
        editor.apply();

//        setValueIntoXML(KEY_USE_SCANNER, useScanner);
    }

    /**
     * 是否使用TSMA扫频仪测试
     */
    public boolean isUseTSMAScanner(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_USE_SCANNER_TSMA)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_USE_SCANNER_TSMA, false);
            editor.apply();
        }
        return share.getBoolean(KEY_USE_SCANNER_TSMA, false);
    }

    /**
     * 设置是否自动清理数据
     */
    public void setAutoDeleteDay(Context context, int day) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_AUTO_DELETE_DAY)) {
            Editor editor = share.edit();
            editor.putInt(KEY_AUTO_DELETE_DAY, day);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putInt(KEY_AUTO_DELETE_DAY, day);
        editor.apply();

        setValueIntoXML(KEY_AUTO_DELETE_DAY, day);
    }

    /**
     * 是否自动清理数据
     */
    public int getAutoDeleteDay(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_AUTO_DELETE_DAY)) {
            Editor editor = share.edit();
            editor.putInt(KEY_AUTO_DELETE_DAY, Integer.parseInt(getValueFromXML(KEY_AUTO_DELETE_DAY, 15)));
            editor.apply();
        }
        return share.getInt(KEY_AUTO_DELETE_DAY, Integer.parseInt(getValueFromXML(KEY_AUTO_DELETE_DAY, 15)));
    }

    /**
     * 设置是否开启Walktour自动打开蓝牙共享
     */
    public void setBluetoothSync(Context context, boolean accept) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_BLUETOOTH_SYNC)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_BLUETOOTH_SYNC, accept);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_BLUETOOTH_SYNC, accept);
        editor.apply();

        setValueIntoXML(KEY_BLUETOOTH_SYNC, accept);
    }


    /**
     * 业务测试过程中是否需要蓝牙同步
     *
     * @return
     */
    public boolean isBluetoothSync(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);

        if (!share.contains(KEY_BLUETOOTH_SYNC)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_BLUETOOTH_SYNC, Boolean.parseBoolean(getValueFromXML(KEY_BLUETOOTH_SYNC, false)));
            editor.apply();
        }
        return share.getBoolean(KEY_BLUETOOTH_SYNC, Boolean.parseBoolean(getValueFromXML(KEY_BLUETOOTH_SYNC, false)));
    }

    private boolean bluetoothServerOpen = false;
    private boolean bluetoothClientOpen = false;

    /**
     * 蓝牙同步服务端连接打开
     *
     * @return
     */
    public boolean isBluetoothServerOpen() {
        return bluetoothServerOpen;
    }

    public void setBluetoothServerOpen(boolean bluetoothServerOpen) {
        this.bluetoothServerOpen = bluetoothServerOpen;
    }

    /**
     * 蓝牙同步服务客房端连接打开
     *
     * @return
     */
    public boolean isBluetoothClientOpen() {
        return bluetoothClientOpen;
    }

    public void setBluetoothClientOpen(boolean bluetoothClientOpen) {
        this.bluetoothClientOpen = bluetoothClientOpen;
    }


    /**
     * 设置是否打开数据集日志
     */
    public void setDataSetLog(Context context, boolean openLog) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_OPEN_DATASETLOG)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_OPEN_DATASETLOG, openLog);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_OPEN_DATASETLOG, openLog);
        editor.apply();

        setValueIntoXML(KEY_OPEN_DATASETLOG, openLog);
    }

    /**
     * 是否打开数据集日志
     */
    public boolean isOpenDataSetLog(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_OPEN_DATASETLOG)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_OPEN_DATASETLOG, Boolean.parseBoolean(getValueFromXML(KEY_OPEN_DATASETLOG, true)));
            editor.apply();
        }
        return share.getBoolean(KEY_OPEN_DATASETLOG, Boolean.parseBoolean(getValueFromXML(KEY_OPEN_DATASETLOG, true)));
    }

    /**
     * 设置是否运行时生成原始数据
     */
    public void setRunSaveOrgData(Context context, boolean saveOrg) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_RUN_SAVEORG)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_RUN_SAVEORG, saveOrg);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_RUN_SAVEORG, saveOrg);
        editor.apply();

        setValueIntoXML(KEY_RUN_SAVEORG, saveOrg);
    }

    /**
     * 是否运行时生成原始数据
     */
    public boolean isRunSaveOrgData(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_RUN_SAVEORG)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_RUN_SAVEORG, false);
            editor.apply();
        }
        return share.getBoolean(KEY_RUN_SAVEORG, false);
    }

    /**
     * 设置是否以WIFI模式连接IPAD
     */
    public void setIpackWifiModel(Context context, boolean wifiModel) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_IPACK_BYWIFIMODEL)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_IPACK_BYWIFIMODEL, wifiModel);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_IPACK_BYWIFIMODEL, wifiModel);
        editor.apply();

        setValueIntoXML(KEY_IPACK_BYWIFIMODEL, wifiModel);
    }

    /**
     * 是否WIFI模式连接IPACK
     */
    public boolean isWifiModel(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_IPACK_BYWIFIMODEL)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_IPACK_BYWIFIMODEL, Boolean.parseBoolean(getValueFromXML(KEY_IPACK_BYWIFIMODEL, false)));
            editor.apply();
        }
        return share.getBoolean(KEY_IPACK_BYWIFIMODEL, Boolean.parseBoolean(getValueFromXML(KEY_IPACK_BYWIFIMODEL, false)));
    }

    /**
     * 设置是否以内存映射的方式生成RCU等文件
     */
    public void setMemoryMappedModel(Context context, boolean memoryMapped) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_MEMORY_MAPPED)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_MEMORY_MAPPED, memoryMapped);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_MEMORY_MAPPED, memoryMapped);
        editor.apply();

        setValueIntoXML(KEY_MEMORY_MAPPED, memoryMapped);
    }

    /**
     * 是否以内存映射方放生成RCU等文件
     */
    public boolean isMemoryMepped(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_MEMORY_MAPPED)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_MEMORY_MAPPED, Boolean.parseBoolean(getValueFromXML(KEY_MEMORY_MAPPED, false)));
            editor.apply();
        }
        return share.getBoolean(KEY_MEMORY_MAPPED, Boolean.parseBoolean(getValueFromXML(KEY_MEMORY_MAPPED, false)));
    }

    /**
     * 设置是否保存全部日志文件
     */
    public void setSaveAllLog(Context context, boolean saveAllLog) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_SAVE_ALL_LOG)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_SAVE_ALL_LOG, saveAllLog);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_SAVE_ALL_LOG, saveAllLog);
        editor.apply();

        setValueIntoXML(KEY_SAVE_ALL_LOG, saveAllLog);

        //日志记录是否保存仅当前应用还是全部日志项已有，设置时修改该静态变量，创建日志前通过当前共享值重置该变量
        LogUtil.ONLYAPPLOG = (!saveAllLog);
    }

    /**
     * 是否保存全部日志
     */
    public boolean isSaveAllLog(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_SAVE_ALL_LOG)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_SAVE_ALL_LOG, Boolean.parseBoolean(getValueFromXML(KEY_SAVE_ALL_LOG, true)));
            editor.apply();
        }
        return share.getBoolean(KEY_SAVE_ALL_LOG, Boolean.parseBoolean(getValueFromXML(KEY_SAVE_ALL_LOG, true)));
    }

    /**
     * 设置是否采集VoLTE QCI数据
     */
    public void setVoLTEQCIInfo(Context context, boolean qciInfo) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_VOLTE_QCIINFO)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_VOLTE_QCIINFO, qciInfo);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_VOLTE_QCIINFO, qciInfo);
        editor.apply();
    }

    /**
     * 是否采集VoLTE QCI数据
     */
    public boolean isVoLTEQCIInfo(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_VOLTE_QCIINFO)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_VOLTE_QCIINFO, false);
            editor.apply();
        }
        return share.getBoolean(KEY_VOLTE_QCIINFO, false);
    }

    /**
     * 设置是否采集VoLTE QCI数据
     */
    public void setSMSInfo(Context context, boolean qciInfo) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_CHECK_SMS_INFO)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_CHECK_SMS_INFO, qciInfo);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_CHECK_SMS_INFO, qciInfo);
        editor.apply();
    }

    /**
     * 是否采集VoLTE QCI数据
     */
    public boolean isSMSInfo(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_CHECK_SMS_INFO)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_CHECK_SMS_INFO, false);
            editor.apply();
        }
        return share.getBoolean(KEY_CHECK_SMS_INFO, false);
    }

    /**
     * Tcp抓包精简模式
     */
    public int getTcpIpSetting(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        int server = share.getInt(KEY_TCPIP_NAME, Integer.parseInt(getValueFromXML(KEY_TCPIP_NAME, 0)));
        return server;
    }

    public boolean setTcpIpSetting(Context context, int type) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);


        setValueIntoXML(KEY_TCPIP_NAME, type);
        return share.edit().putInt(KEY_TCPIP_NAME, type).commit();
    }

    /**
     * 返回WAP测试任务的接入点名称,
     */
    public String getWapAPN() {
        Element el = (Element) getDoc().getElementsByTagName("apn").item(0);
        if(null != el){
            NodeList nl = el.getElementsByTagName("item");
            for (int i = 0; i < nl.getLength(); i++) {
                Node nd = nl.item(i);
                if (nd.getAttributes().getNamedItem("name").getNodeValue().equals("wap")) {
                    return nd.getAttributes().getNamedItem("value").getNodeValue();
                }
            }
        }
        return null;
    }

    /**
     * 返回数据业务的接入点名称
     */
    public String getDataAPN() {
        Element el = (Element) getDoc().getElementsByTagName("apn").item(0);
        if(null != el){
            NodeList nl = el.getElementsByTagName("item");
            for (int i = 0; i < nl.getLength(); i++) {
                Node nd = nl.item(i);
                if (nd.getAttributes().getNamedItem("name").getNodeValue().equals("data")) {
                    return nd.getAttributes().getNamedItem("value").getNodeValue();
                }
            }
        }
        return null;
    }

    public void setWapAPN(String value) {
        Element el = (Element) getDoc().getElementsByTagName("apn").item(0);
        if (el != null) {
            NodeList nl = el.getElementsByTagName("item");
            for (int i = 0; nl != null && i < nl.getLength(); i++) {
                Node nd = nl.item(i);
                if (nd.getAttributes().getNamedItem("name").getNodeValue().equals("wap")) {
                    nd.getAttributes().getNamedItem("value").setNodeValue(value);
                }
            }
            writer.writeToFile(getDoc());
        }
    }

    public void setDataAPN(String value) {
        Element el = (Element) getDoc().getElementsByTagName("apn").item(0);
        if (el != null) {
            NodeList nl = el.getElementsByTagName("item");
            for (int i = 0; nl != null && i < nl.getLength(); i++) {
                Node nd = nl.item(i);
                if (nd.getAttributes().getNamedItem("name").getNodeValue().equals("data")) {
                    nd.getAttributes().getNamedItem("value").setNodeValue(value);
                }
            }
            writer.writeToFile(getDoc());
        }
    }

    /**
     * 设置MOS盒通道信息
     */
    public void setMosBoxChannel(Context context, int channel) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_MOS_BOX_CHANNEL)) {
            Editor editor = share.edit();
            editor.putInt(KEY_MOS_BOX_CHANNEL, channel);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putInt(KEY_MOS_BOX_CHANNEL, channel);
        editor.apply();
    }

    /**
     * 获得MOS盒通道信息
     */
    public int getMosBoxChannel(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_MOS_BOX_CHANNEL)) {
            Editor editor = share.edit();
            editor.putInt(KEY_MOS_BOX_CHANNEL, 0);
            editor.apply();
        }
        return share.getInt(KEY_MOS_BOX_CHANNEL, 0);
    }

    /**
     * 设置是否拷贝过环境文件
     */
    public static void setUEnvInfoTime(Context context, String times) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_ENV_INFO_UTIME)) {
            Editor editor = share.edit();
            editor.putString(KEY_ENV_INFO_UTIME, times);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putString(KEY_ENV_INFO_UTIME, times);
        editor.apply();
    }

    /**
     * 是否环境文件初始化成功
     */
    public static String getUEnvInfoTime(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_ENV_INFO_UTIME)) {
            Editor editor = share.edit();
            editor.putString(KEY_ENV_INFO_UTIME, "");
            editor.apply();
        }
        return share.getString(KEY_ENV_INFO_UTIME, "");
    }

    /**
     * 设置MOS盒录音音量
     */
    public void setMosBoxVRec(Context context, int vRec) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_MOS_BOX_VREC)) {
            Editor editor = share.edit();
            editor.putInt(KEY_MOS_BOX_VREC, vRec);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putInt(KEY_MOS_BOX_VREC, vRec);
        editor.apply();
    }

    /**
     * 获得MOS盒录音音量
     */
    public int getMosBoxVRec(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_MOS_BOX_VREC)) {
            Editor editor = share.edit();
            editor.putInt(KEY_MOS_BOX_VREC, Deviceinfo.getInstance().getMosBoxVRec());
            editor.apply();
        }
        return share.getInt(KEY_MOS_BOX_VREC, Deviceinfo.getInstance().getMosBoxVRec());
    }

    /**
     * 设置MOS盒放音音量
     */
    public void setMosBoxVPlay(Context context, int vPlay) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_MOS_BOX_VPLAY)) {
            Editor editor = share.edit();
            editor.putInt(KEY_MOS_BOX_VPLAY, vPlay);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putInt(KEY_MOS_BOX_VPLAY, vPlay);
        editor.apply();
    }

    /**
     * 获得MOS盒放音音量
     */
    public int getMosBoxVPlay(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_MOS_BOX_VPLAY)) {
            Editor editor = share.edit();
            editor.putInt(KEY_MOS_BOX_VPLAY, Deviceinfo.getInstance().getMosBoxVPlay());
            editor.apply();
        }
        return share.getInt(KEY_MOS_BOX_VPLAY, Deviceinfo.getInstance().getMosBoxVPlay());
    }

    /**
     * 设置MOS阈值
     */
    public void setCallMosBoxLowMos(Context context, float vFazhi) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_MOS_BOX_VFAZHI)) {
            Editor editor = share.edit();
            editor.putFloat(KEY_MOS_BOX_VFAZHI, vFazhi);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putFloat(KEY_MOS_BOX_VFAZHI, vFazhi);
        editor.apply();
    }

    /**
     * 获得MOS阈值
     */
    public float getCallMosBoxLowMos(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_MOS_BOX_VFAZHI)) {
            Editor editor = share.edit();
            editor.putFloat(KEY_MOS_BOX_VFAZHI, Deviceinfo.getInstance().getCallMosBoxLowMos());
            editor.apply();
        }
        return share.getFloat(KEY_MOS_BOX_VFAZHI, Deviceinfo.getInstance().getCallMosBoxLowMos());
    }

    /**
     * 设置是否外置陀螺仪
     */
    public void setHsExternalGPS(Context context, boolean isExternalGPS) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_IS_EXTERNAL_GPS)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_IS_EXTERNAL_GPS, isExternalGPS);
            editor.apply();
        }
        Editor editor = share.edit();
        editor.putBoolean(KEY_IS_EXTERNAL_GPS, isExternalGPS);
        editor.apply();
    }

    /**
     * 获得是否外置陀螺仪
     */
    public boolean isHsExternalGPS(Context context) {
        SharedPreferences share = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        if (!share.contains(KEY_IS_EXTERNAL_GPS)) {
            Editor editor = share.edit();
            editor.putBoolean(KEY_IS_EXTERNAL_GPS, true);
            editor.apply();
        }
        return share.getBoolean(KEY_IS_EXTERNAL_GPS,true);
    }

    /***
     * 复位所有的文件上传类型,用于多网测试同步上传日志类型
     */
    public void resetFileUploadTypes(Context context) {
        this.setGenDCF(context, false);
        this.setGenOrgRcu(context, false);
        this.setGenDtLog(context, false);
        this.setGenCU(context, false);
        this.setGenRCU(context, false);
        this.setGenECTI(context, false);
    }
}