package com.walktour.control.config;

import android.content.Context;
import android.util.Xml;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.LogUtil;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.walktour.Utils.WalktourConst.SYS_SETTING_nbmoduele_devicechipvendor_control;

/**
 * NB-IOT模块配置文件
 */
public class ConfigNBModuleInfo {
    private final static String TAG = ConfigNBModuleInfo.class.getSimpleName();
    private static ConfigNBModuleInfo ourInstance;
    /**
     * 参数存储
     */
    private static SharePreferencesUtil sharePreferencesUtil;
    /**
     * 所有的模块名
     */
    private Map<String, NBModule> nbmodels = new LinkedHashMap<>();

    /**默认值,当有2个选项时默认为2**/
    public static String NBPORT_DEFAULT = "/dev/ttyUSB0";

    /**AT口默认值,当有2个时,默认为1当有4个选项时默认为2**/
    public static String NBATPORT_DEFAULT = "/dev/ttyUSB2";
    /**
     * NB模块的设备名
     */
    private String nbModuleName = null;

    /**
     * NB模块的端口
     */
    private String nbPort = null;
    /**
     * NB模块的AT端口
     */
    private String nbAtPort = null;
    /**
     * NB模块的chipvendor
     */
    private String chipvendor = "";

    /***
     * NB模块的端口名集合
     */
    public Map<String, String> nbPorts = new LinkedHashMap<>();
    /**是否是NB wifi测试模式**/
    private boolean hasNBWifiTestModel = false;

    /**NB测试选择的wifi连接 的服务端ip**/
    private String nbSelectWifiServerIP = "192.168.1.1";
    /**NB测试选择的wifi连接 的客户端ip**/
    private String nbSelectWifiClientIP = null;

    /**NB module name**/
    private String nbModuleNameFile="/data/data/com.walktour.gui/files/nbmodulename.txt";
    /**NB port端口**/
    private String nbPortFile="/data/data/com.walktour.gui/files/nbportfile.txt";
    /**NB at port端口**/
    private String nbAtPortFile="/data/data/com.walktour.gui/files/nbatportfile.txt";
    /**NB wifi IP**/
    private String nbSelectWifiClientIPFile="/data/data/com.walktour.gui/files/nbSelectWifiClientIPFile.txt";
    public static synchronized ConfigNBModuleInfo getInstance(Context context) {
        sharePreferencesUtil = SharePreferencesUtil.getInstance(context);
        if (ourInstance == null) {
            ourInstance = new ConfigNBModuleInfo(context);

        }
        return ourInstance;
    }

    private ConfigNBModuleInfo(Context context) {
        try {
            this.parse(context.getAssets().open("config/config_nbmodelinfo.xml"));
        } catch (Exception e) {
            LogUtil.w(TAG, e.getMessage());
        }
    }

    private void parse(InputStream is) throws Exception {

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "UTF-8");

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
            case XmlPullParser.START_DOCUMENT:
                break;
            case XmlPullParser.START_TAG:
                if (parser.getName().equals("item")) {
                    int count = parser.getAttributeCount();
                    NBModule nbModule = new NBModule();
                    for (int i = 0; i < count; i++) {
                        if (parser.getAttributeName(i).equals("devicename")) {
                            String val = parser.getAttributeValue(i);
                            if (val != null && val.length() > 0) {
                                nbModule.setDeviceName(val);
                            }
                        }
                        if (parser.getAttributeName(i).equals("devicevalue")) {
                            String val = parser.getAttributeValue(i);
                            if (val != null && val.length() > 0) {
                                nbModule.setDeviceValue(val);
                            }
                        }
                        if (parser.getAttributeName(i).equals("chipvendor")) {
                            String val = parser.getAttributeValue(i);
                            if (val != null && val.length() > 0) {
                                nbModule.setChipvendor(val);
                            }
                        }
                    }

                    nbmodels.put(nbModule.getDeviceName(), nbModule);
                }
                break;
            case XmlPullParser.END_TAG:
                break;
            }
            eventType = parser.next();
        }
    }

    /**
     * NB模块配置信息
     */
    public class NBModule {
        private String deviceName;

        private String deviceValue;

        private String chipvendor;

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceValue() {
            return deviceValue;
        }

        public void setDeviceValue(String deviceValue) {
            this.deviceValue = deviceValue;
        }

        public String getChipvendor() {
            return chipvendor;
        }

        public void setChipvendor(String chipvendor) {
            this.chipvendor = chipvendor;
        }
    }

    /**
     * 启动app,自动检查是否具有NB模块
     */
    public boolean checkNBModule() {
        DataOutputStream os = null;
        BufferedReader in = null;
        Process process = null;
        boolean isFlag = false;
        try {
            // 获得外接USB输入设备的信息
            process = Runtime.getRuntime().exec(Deviceinfo.getInstance().getSuOrShCommand());
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("ls -l /dev/tty*\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            String matchName = "/dev/ttyUSB";
            String matchName2 = "/dev/ttyACM";
            String matchName3 = "/dev/ttyXRUSB";
            while ((line = in.readLine()) != null) {
                String deviceInfo = line.trim();
                // LogUtil.w(TAG, System.currentTimeMillis() + "checkNBModule devices is=" + deviceInfo);
                if (deviceInfo.contains(matchName)) {
                    LogUtil.w(TAG, System.currentTimeMillis() + "|usb devices is=" + deviceInfo);
                    int index = deviceInfo.lastIndexOf(matchName) + matchName.length();
                    try {
                        int val = Integer.parseInt(deviceInfo.substring(index));
                        nbPorts.put("com" + val, matchName + val);
                    } catch (Exception ex) {
                        LogUtil.w(TAG, System.currentTimeMillis() + "| exception=" + ex.getMessage());
                    }
                }
                if (!isFlag && deviceInfo.contains(matchName)) {
                    isFlag = true;
                }
                if (deviceInfo.contains(matchName2)) {
                    LogUtil.w(TAG, System.currentTimeMillis() + "|usb devices is=" + deviceInfo);
                    int index = deviceInfo.lastIndexOf(matchName2) + matchName2.length();
                    try {
                        int val = Integer.parseInt(deviceInfo.substring(index));
                        nbPorts.put("acm" + val, matchName2 + val);
                    } catch (Exception ex) {
                        LogUtil.w(TAG, System.currentTimeMillis() + "| exception=" + ex.getMessage());
                    }
                }
                if (!isFlag && deviceInfo.contains(matchName2)) {
                    isFlag = true;
                }

                if (deviceInfo.contains(matchName3)) {
                    LogUtil.w(TAG, System.currentTimeMillis() + "|usb devices is=" + deviceInfo);
                    int index = deviceInfo.lastIndexOf(matchName3) + matchName3.length();
                    try {
                        int val = Integer.parseInt(deviceInfo.substring(index));
                        nbPorts.put("xrusb" + val, matchName3 + val);
                    } catch (Exception ex) {
                        LogUtil.w(TAG, System.currentTimeMillis() + "| exception=" + ex.getMessage());
                    }
                }
                if (!isFlag && deviceInfo.contains(matchName3)) {
                    isFlag = true;
                }
            }
            if (isFlag) {
                if (nbPorts.size() <= 2) {
                    NBATPORT_DEFAULT = "/dev/ttyUSB1";
                } else if (nbPorts.size() <= 4) {
                    NBATPORT_DEFAULT = "/dev/ttyUSB2";
                } else {
                    NBATPORT_DEFAULT = "/dev/ttyUSB2";
                }
                nbAtPort = NBATPORT_DEFAULT;
                nbPort = NBPORT_DEFAULT;
                initNBModuleInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.w(TAG, e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                os = null;
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if (null != process) {
                try {
                    process.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                process = null;

            }
        }
        return isFlag;
    }

    /**
     * 初始化NB模块的测试信息
     */
    private void initNBModuleInfo() {
        nbModuleName = sharePreferencesUtil.getString(WalktourConst.SYS_SETTING_nbmoduele_devicename_control, nbModuleName);
        if (null != nbModuleName && nbModuleName.split(",").length >= 2) {
            nbModuleName = nbModuleName.split(",")[1];
        }
        setNbModuleName(nbModuleName);
        Iterator<String> iter = nbmodels.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            NBModule nbModule = nbmodels.get(key);
            if (nbModule.getDeviceValue().equals(nbModuleName)) {
                chipvendor = nbModule.getChipvendor();
            }
        }
        nbPort = sharePreferencesUtil.getString(WalktourConst.SYS_SETTING_nbmoduele_deviceport_control, nbPort);
        if (null != nbPort && nbPort.split(",").length >= 2) {
            nbPort = nbPort.split(",")[1];
        }
        setNbPort(nbPort);
        nbAtPort = sharePreferencesUtil.getString(WalktourConst.SYS_SETTING_nbmoduele_deviceatport_control, nbAtPort);
        if (null != nbAtPort && nbAtPort.split(",").length >= 2) {
            nbAtPort = nbAtPort.split(",")[1];
        }
        setNbAtPort(nbAtPort);

    }

    /**
     * 设置NB模块信息
     *
     * @param nbModuleName
     * @param nbPort
     * @param nbAtPort
     */
    public void setNBModuleInfo(String nbModuleName, String nbPort, String nbAtPort, String chipvendor) {
        this.nbModuleName = nbModuleName;
        setNbModuleName(nbModuleName);
        sharePreferencesUtil.saveString(WalktourConst.SYS_SETTING_nbmoduele_devicename_control, this.nbModuleName + "," + this.nbModuleName);

        this.nbPort = nbPort;
        setNbPort(nbPort);
        sharePreferencesUtil.saveString(WalktourConst.SYS_SETTING_nbmoduele_deviceport_control, this.nbPort + "," + this.nbPort);

        this.nbAtPort = nbAtPort;
        setNbAtPort(nbAtPort);
        sharePreferencesUtil.saveString(WalktourConst.SYS_SETTING_nbmoduele_deviceatport_control, this.nbAtPort + "," + this.nbAtPort);

        this.chipvendor = chipvendor;
        sharePreferencesUtil.saveString(SYS_SETTING_nbmoduele_devicechipvendor_control, chipvendor);

        LogUtil.w(TAG, "setNBModuleInfo");
    }

    /**
     * 获取所有的NB-IOT设备信息
     *
     * @return
     */
    public Map<String, NBModule> getNbmodels() {
        return nbmodels;
    }

    public String getNbModuleName() {
        if (nbModuleName == null || nbModuleName.equals("")) {
            nbModuleName = sharePreferencesUtil.getString(WalktourConst.SYS_SETTING_nbmoduele_devicename_control, "Remo,Remo 1526");
            if (null != nbModuleName && nbModuleName.split(",").length >= 2) {
                nbModuleName = nbModuleName.split(",")[1];
            }
        }
        LogUtil.w(TAG, "nbModuleName is=" + nbModuleName);
        return nbModuleName;
    }

    public void setNbModuleName(String nbModuleName) {
        this.nbModuleName = nbModuleName;
        FileUtil.writeToFileB(nbModuleNameFile,nbModuleName);
    }

    public String getNbPort() {

        nbPort = sharePreferencesUtil.getString(WalktourConst.SYS_SETTING_nbmoduele_deviceport_control, nbPort);
        if (null != nbPort && nbPort.split(",").length >= 2) {
            nbPort = nbPort.split(",")[1];
        }
        if (nbPort == null || nbPort.equals("")) {
            nbPort = ConfigNBModuleInfo.NBPORT_DEFAULT;
        }
        LogUtil.w(TAG, "nbPort is=" + nbPort);
        return nbPort;
    }

    public void setNbPort(String nbPort) {
        this.nbPort = nbPort;
        FileUtil.writeToFileB(nbPortFile,nbPort);
    }

    public String getNbAtPort() {

        nbAtPort = sharePreferencesUtil.getString(WalktourConst.SYS_SETTING_nbmoduele_deviceatport_control, nbAtPort);
        if (null != nbAtPort && nbAtPort.split(",").length >= 2) {
            nbAtPort = nbAtPort.split(",")[1];
        }
        if (nbAtPort == null || nbAtPort.equals("")) {
            nbAtPort = ConfigNBModuleInfo.NBATPORT_DEFAULT;
        }
        LogUtil.w(TAG, "nbAtPort is=" + nbAtPort);
        return nbAtPort;
    }

    public void setNbAtPort(String nbAtPort) {
        this.nbAtPort = nbAtPort;
        FileUtil.writeToFileB(nbAtPortFile,nbAtPort);
    }

    public String exterNBModuleName(){
        String str=FileUtil.getStringFromFile(new File(nbModuleNameFile));
        LogUtil.w(TAG,"exterNBModuleName="+str);
        return str;
    }

    public String exterNBATPort(){
        String str=FileUtil.getStringFromFile(new File(nbAtPortFile));
        LogUtil.w(TAG,"exterNBATPort="+str);
        if(null==str||str.length()<=0){
            str="/dev/ttyUSB9";
        }
        return str;
    }

    public String exterNBSelectWifiIP(){
        String str=FileUtil.getStringFromFile(new File(nbSelectWifiClientIPFile));
        LogUtil.w(TAG,"exterNBSelectWifiIP="+str);
        if(null==str||str.equalsIgnoreCase("null")||str.length()<=0){
            str="";
        }
        return str;
    }
    public Map<String, String> getNbPorts() {
        return nbPorts;
    }

    public String getChipvendor() {
        if (null == chipvendor || chipvendor.equals("")) {
            chipvendor = sharePreferencesUtil.getString(WalktourConst.SYS_SETTING_nbmoduele_devicechipvendor_control, "");
            if (null == chipvendor || chipvendor.equals("")) {
                chipvendor = "D111";
            }
        }
        LogUtil.w(TAG, "chipvendor is=" + chipvendor);
        return chipvendor;
    }

    public void setChipvendor(String chipvendor) {
        this.chipvendor = chipvendor;
        sharePreferencesUtil.saveString(SYS_SETTING_nbmoduele_devicechipvendor_control, chipvendor);
    }

    public boolean isProccessStop() {
        return ((null == getNbAtPort()) || (true == getNbAtPort().equals("")) || (true == (getNbAtPort().equals("null"))));
    }

    public boolean isHasNBWifiTestModel() {
        // if(!hasNBWifiTestModel){
        // hasNBWifiTestModel=sharePreferencesUtil.getBoolean(WalktourConst.SYS_SETTING_nbmodule_iswifinbtest, false);
        // }
        return hasNBWifiTestModel;
    }

    public void setHasNBWifiTestModel(boolean hasNBWifiTestModel) {
        this.hasNBWifiTestModel = hasNBWifiTestModel;
        // sharePreferencesUtil.saveBoolean(WalktourConst.SYS_SETTING_nbmodule_iswifinbtest,hasNBWifiTestModel);
    }

    public String getNbSelectWifiServerIP() {
        nbSelectWifiServerIP = sharePreferencesUtil.getString(WalktourConst.SYS_SETTING_nbmodule_wifiserverip, "");
        return nbSelectWifiServerIP;
    }

    public void setNbSelectWifiServerIP(String nbSelectWifiServerIP) {
        this.nbSelectWifiServerIP = nbSelectWifiServerIP;

        sharePreferencesUtil.saveString(WalktourConst.SYS_SETTING_nbmodule_wifiserverip, nbSelectWifiServerIP);

    }

    public String getNbSelectWifiClientIP() {
        nbSelectWifiClientIP = sharePreferencesUtil.getString(WalktourConst.SYS_SETTING_nbmodule_wificlientip, "");
        return nbSelectWifiClientIP;
    }

    public void setNbSelectWifiClientIP(String nbSelectWifiClientIP) {
        this.nbSelectWifiClientIP = nbSelectWifiClientIP;
        sharePreferencesUtil.saveString(WalktourConst.SYS_SETTING_nbmodule_wificlientip, nbSelectWifiServerIP);
        FileUtil.writeToFileB(nbSelectWifiClientIPFile,nbSelectWifiServerIP);
    }

    /**
     * 是否是利尔达设备
     * @return
     */
    public boolean isLierda() {
        if (ApplicationModel.getInstance().isNBTest()) {
            if (null != nbModuleName && (nbModuleName.equalsIgnoreCase("Lierda USB Dongle") || nbModuleName.equalsIgnoreCase("Lierda"))) {
                return true;
            }
        }
        return false;
    }
}
