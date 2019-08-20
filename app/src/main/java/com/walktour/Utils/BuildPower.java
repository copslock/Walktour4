package com.walktour.Utils;

import android.content.Context;
import android.content.Intent;

import com.dingli.https.HttpsUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.LocalInfoCheck;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.gui.ProgressActivity;
import com.walktour.gui.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BuildPower {
    private ApplicationModel appModel = ApplicationModel.getInstance();
    private static final String TAG = "BuildInfo";
    /**
     * 测试模式
     */
    private static final int Power_App = 1;
    /**
     * 测试网络类型
     */
    private static final int Power_Net = 2;
    /**
     * 外接Scanner测试
     */
    private static final int Power_Scanner = 3;
    /**
     * 测试任务
     */
    private static final int Power_Task = 4;

    public static final int TBTime = 1800; //时间允许跳变时间
    public static final String PI = "emdqTHdUUXpMallNdUVqTg==";        //61.143.60.83
    //    public static final String PI2 = "PT1BTno0a3hMakUxTVNqTHlFVE0=";    //112.91.151.34
    public static final String PN = "PVFXWWxtOXBaRkpjazVXUQ==";        //AndroidRead
    public static final String PW = "a0ZXWlNtOXBaQzVjazVXUQ==";        //Android.Read
//    public static final String UL = "PT1RWnk5R2R6RldaekpYWjI5VUxrMnhwWTI5dExtTnZiUzlCYm1SeWIybFp1bEdadWMzZDM5eUw2\nTUhjMFJIYQ==";    //https://www.dinglicom.com/Android-Overseastore
    public static final String UL = "PUlYZHZSM2FzRjJWa2wyYnlSbHVaMnhwWTI5dExtTnZiUzlCYm1HWnVjM2QzOXlMNk1IYzBSSGE=";    //https://www.dinglicom.com/AndroidWalktour
//    public static final String PD = "PWNqTHpVbWN2UjFQZG1WeWMyVmhVM0NacDltY2s1V1E=";        //Android-OverseaStore3.7
    public static final String PD = "PT13TnVNamMxQzFYWVd4cmRHOVpwOW1jazVXUQ==";        //Android-Walktour3.7---普通版本
//    public static final String PD = "PWNqTHprV1paQzFJZFdGWHA5bWNrNVdR";        //Android-HuaWei3.7---华为版本
    public static final String PL = "dWxtWXUyVnVjMlVZcHgyTA==";        // /license.bin
    public static final String PA = "MGhIZHUzSnRZV3didUpXWQ==";        // abnormal
    public static final String PO = "PT1BZDRXRnNMblJieTltYg==";        // normal
    public static final String PX = "PT1BZExuUjR1bEdl";                // xin.txt
    public static final String TP = "aGQxTA==RWIwdEdiTDJSaGRHbVl2M1Z5PT13THVs";    // /Walktour/data/bin/
    public static final String TN = "ZA==c0YyUnZkWElhWXUzdWxt";        //walktour.bin

    /**
     * 权限文件所属组
     */
    public enum PowerList {

        //----测试模式------
        OperationTest(1, Power_App, "OperationTest"),
        MutilyTester(2, Power_App, "MutilyTester"),
        PioneerTester(30, Power_App, "PioneerTester"),
        AutomatismTest(54, Power_App, "AutomatismTest"),
        ProjectTest(56, Power_App, "ProjectTest"),
        ExtDataCartTest(57, Power_App, "ExtDataCartTest"),
        ScannerTest(58, Power_App, "ScannerTest"),
        InfoMonitor(91, Power_App, "InfoMonitor"),
        BackgroundMonitor(59, Power_App, "BackgroundMonitor"),
        //----细分-单机业务测试----
        IndoorTest(8, Power_App, "IndoorTest"),        //室分专项
        DTTest(9, Power_App, "DTTest"),
        CQTTest(10, Power_App, "CQTTest"),
        CQTGPSTest(11, Power_App, "CQTGPSTest"),
        Metro(108, Power_App, "Metro"),
        HighSpeedRail(109, Power_App, "HighSpeedRail"),
        InnsMap(120, Power_App, "InnsMap"), // 寅时室内测试
        //vivo使用内置polqa算分文件权限
        PolqaType(125,Power_App,"PolqaType"),

        //----测试网络----
        WifiTest(55, Power_Net, "WLAN"),
        Gsm(3, Power_Net, "Gsm,Edge"),
        Umts(4, Power_Net, "Umts,Gsm,Edge,Hspa"),
        Cdma(5, Power_Net, "Cdma,EvDo"),
        HspaPlus(6, Power_Net, "HspaPlus"),
        LTEFDD(7, Power_Net, "LTE,LTEFDD"),
        LTETDD(51, Power_Net, "LTE,LTETDD"),
        LTECA(93, Power_Net, "LTECA"),
        LTE4T4R(124, Power_Net, "LTE4T4R"),
        FDD3CaD(104, Power_Net, "FDD3CaD"),
        TDD3CaD(105, Power_Net, "TDD3CaD"),
        FDD2CaU(106, Power_Net, "FDD2CaU"),
        TDD2CaU(107, Power_Net, "TDD2CaU"),
        FDD800M(119, Power_Net, "FDD800M"),
        TDSCDMA(52, Power_Net, "TDSCDMA"),
        TDHspaPlus(53, Power_Net, "TDHspaPlus"),
        //----测试功能----
        TCPIPCapture(28, Power_Net, "TCPIPCapture"),
        GoogleMap(66, Power_Net, "GoogleMap"),
        Playback(67, Power_Net, "Playback"),
        ShowTotal(68, Power_Net, "ShowTotal"),
        Disable1(69, Power_Net, "Default"),                //无效
        Disable2(70, Power_Net, "Default"),                //无效
        EnforceNET(71, Power_Net, "LockFrequency"),        //网络和频段强制
        EnforceCA(112, Power_Net, "LockCAFrequency"),        //CA强制
        EnforceCheckPow(72, Power_Net, "EnforceCheckPow"),
        PauseTest(76, Power_Net, "PauseTest"),            //暂停测试，预留1
        CSFBAnalysis(77, Power_Net, "CSFBAnalysis"),        //CSFB异常分析
        VoLTEAnalyse(103, Power_Net, "VoLTEAnalyse"),        //VoLTE异常分析
        LTEDataAnalysis(111, Power_Net, "LTEDataAnalysis"),        //CSFB异常分析
        GMCCProj(78, Power_Net, "GMCCProj"),            //超级用户系统设置权限
        BluetoothSync(79, Power_Net, "BluetoothSync"),        //蓝牙同步权限
        CreateBin(81, Power_Net, "CreateBin"),            //创建Bin文件
        CheckOutOfService(82, Power_Net, "CheckOutOfService"),    //检查脱网
        Atu(97, Power_Net, "Atu"),
        Btu(83, Power_Net, "Btu"),
        WOnePro(84, Power_Net, "WOnePro"),                //WONE权限
        OTS(85, Power_Net, "OTS"),                    //OTS
        DCF(95, Power_Net, "DCF"),                    //DCF开关
        CU(96, Power_Net, "CU"),                    //联通CU数据CU数据开关
        eCTI(121, Power_Net, "eCTI"),                //eCTI数据开关
        DontSaveFile(86, Power_Net, "DontSaveFile"),        //不保存文件权限
        AHWorkOrder(87, Power_Net, "AHWorkOrder"),            //安徽电信工单权限
        HWWorkOrder(88, Power_Net, "HWWorkOrder"),            //华为电信工单权限
        FJWorkOrder(92, Power_Net, "FJWorkOrder"),            //福建投诉电信工单权限
        SingleStation(94, Power_Net, "SingleStation"),            //单站验证
        BaiduSpecialCount(89, Power_Net, "BaiduSpecialCount"),    //百度云转用帐号
        OpenDataSetLog(90, Power_Net, "OpenDataSetLog"),        //是否有设置数据集生成日志开关权限
        LogcatAllLog(118, Power_Net, "LogcatAllLog"),        //Logcat全日志开关
        VoLTE(42, Power_Net, "VoLTE"),                //VoLTE测试业务可用，国内单指移动
        UnicomVoLTE(126, Power_Net, "UnicomVoLTE"),            //联通Volte权限
        TelecomVoLTE(127, Power_Net, "TelecomVoLTE"),          //电信Volte权限
        SAMSUNGVoLTE(110, Power_Net, "SAMSUNGVoLTE"),        //三星VoLTE权限
        VoLTEVideo(102, Power_Net, "VoLTEVideo"),            //VoLTE视频测试业务可用
        TelecomSwitch(101, Power_Net, "TelecomSwitch"),            //WlanEteAuth
        NBIoT(122, Power_Net, "NBIoT"),            //NB-IoT
        eMTC(130, Power_Net, "CatM"),//注意eMTC就是CatM网络
        //----细分-外接Scanner测试----
        ScannerLTE(60, Power_Scanner, "ScannerLTE"),
        ScannerWCDMA(61, Power_Scanner, "ScannerWCDMA"),
        ScannerCDMA(62, Power_Scanner, "ScannerCDMA"),
        ScannerCW(63, Power_Scanner, "ScannerCW"),
        ScannerPilot(64, Power_Scanner, "ScannerPilot"),
        ScannerSperum(65, Power_Scanner, "ScannerSperum"),


        //----测试任务----
        MOC(12, Power_Task, "InitiativeCall"),        //MOC测试业务可用
        MTC(40, Power_Task, "PassivityCall"),        //MTC测试业务可用
        FTPUpload(13, Power_Task, "FTPUpload"),            //FTP Upload测试业务可用
        FTPDownload(14, Power_Task, "FTPDownload"),            //FTP Download测试业务可用
        Ping(15, Power_Task, "Ping"),                //Ping测试业务可用
        HttpPage(16, Power_Task, "Http,HttpRefurbish"),        //HTTP Page(Logon/Refresh)测试业务可用
        HttpDownlaod(17, Power_Task, "HttpDownload"),        //HTTP Download测试业务可用
        HttpUpload(18, Power_Task, "HttpUpload"),            //HTTP Upload测试业务可用
        WapPage(19, Power_Task, "WapLogin,WapRefurbish"),    //WAP Page(Logon/Refresh)测试业务可用
        WapDownload(20, Power_Task, "WapDownload"),            //WAP Download测试业务可用
        Email(21, Power_Task, "EmailPop3,EmailSmtp,EmailSmtpAndPOP"),    //SMS测试业务可用
        SMS(22, Power_Task, "SMSIncept,SMSSend,SMSSendReceive"),    //SMS测试业务可用
        MMS(23, Power_Task, "MMSIncept,MMSSend,MMSSendReceive"),    //MMS测试业务可用
        Idle(24, Power_Task, "EmptyTask"),            //Idle测试业务可用
        ParallelService(25, Power_Task, "MultiRAB"),            //Parallel Service(并发)测试业务可用
        PPPDial(26, Power_Task, "PPPDial"),                //PPP Dial测试业务可用
        VideoStreaming(27, Power_Task, "Stream"),                //Video Streaming测试业务可用
        PESQMOS(29, Power_Task, "MOS"),                    //MOS测试业务可用
        Attach(31, Power_Task, "Attach"),                //Attach测试业务可用
        PDP(32, Power_Task, "PDP"),                    //PDP测试业务可用
        VideoPhone(33, Power_Task, "InitiativeVideoCall,PassivityVideoCall"),    //Video Phone测试业务可用
        Fetion(34, Power_Task, "Fection"),                //Fetion测试业务可用
        WeiBo(35, Power_Task, "WeiBo"),                //WeiBo测试业务可用
        VoIP(36, Power_Task, "VOIP"),                //VoIP测试业务可用
        VideoPlay(37, Power_Task, "HTTPVS"),                //Video Play测试业务可用
        SpeedTest(38, Power_Task, "SpeedTest"),            //SpeedTest测试业务可用
        DNSLookup(39, Power_Task, "DNSLookUp"),            //DNS Lookup测试业务可用
        Facebook(41, Power_Task, "Facebook"),            //Facebook测试业务可用
        CSFBLTEVoice(43, Power_Task, "CSFBLTEVoice"),        //CSFB LTE Voice测试业务可用
        iPerf(44, Power_Task, "Iperf"),                //iPerf测试业务可用
        TraceRoute(45, Power_Task, "TraceRoute"),            //Trace Route测试业务可用
        Skype(46, Power_Task, "Skype"),                //Skype测试业务可用
        MobiletoLandMOS(47, Power_Task, "Phone2Fixed"),            //Mobile to Land MOS测试业务可用
        POLQAMOS(48, Power_Task, "POLQA"),                //POLQA MOS测试业务可用
        Autotest(49, Power_Task, "Autotest"),            //Autotest测试业务可用
        AutotestCount(50, Power_Task, "AutotestCount"),        //Autotest测试业务数量
        MultiftpUpload(73, Power_Task, "MultiftpUpload"),        //MultiftpUpload测试业务数量
        MultiftpDownload(74, Power_Task, "MultiftpDownload"),    //MultiftpDownload测试业务数量
        PBM(80, Power_Task, "PBM"),                    //PBM测试业务数量
        WlanAP(98, Power_Task, "WlanAP"),                //WlanAP
        WlanLogin(99, Power_Task, "WlanLogin"),            //Wlan登陆
        WlanEteAuth(100, Power_Task, "WlanEteAuth"),            //WlanEteAuth
        WeChat(113, Power_Task, "WeChat"),            //WlanEteAuth
        Line(114, Power_Task, "Line"),            //WlanEteAuth
        WhatsApp(115, Power_Task, "WhatsApp"),            //WlanEteAuth
        Twitter(116, Power_Task, "Twitter"),            //WlanEteAuth
        Instagram(117, Power_Task, "Instagram"),            //WlanEteAuth
        UDP(123, Power_Task, "UDP"),            //UDP
        OpenSignal(128, Power_Task, "OpenSignal"),
        MultiHttpDown(129, Power_Task, "MultiHttpDownload"),
        WeCallMoc(131, Power_Task, "WeCallMoc"),
        WeCallMtc(132, Power_Task, "WeCallMtc"),
        SkypeChat(133, Power_Task, "SkypeChat"),
        SinaWeibo(134, Power_Task, "SinaWeibo"),
        QQ(135, Power_Task, "QQ"),
        WhatsAppChat(136, Power_Task, "WhatsAppChat"),
        WhatsAppMoc(137, Power_Task, "WhatsAppMoc"),
        WhatsAppMtc(138, Power_Task, "WhatsAppMtc"),
        Fackbook2(139, Power_Task, "Facebook_Ott"),
        Instagram2(140, Power_Task, "Instagram_Ott"),
        ENDC(141, Power_Net, "ENDC"),

        UnKnown;

        //与license生成工具的ID对应(上面的ID-1)
        private final int serialId;
        //权限分组类型ID
        private final int groupId;
        //权限关键字名称
        private final String powerStr;

        PowerList() {
            this.serialId = -1;
            this.groupId = -1;
            this.powerStr = "";
        }

        PowerList(int serialId, int groupId, String powerStr) {
            this.serialId = serialId;
            this.groupId = groupId;
            this.powerStr = powerStr;
        }

        public int getSerialId() {
            return this.serialId;
        }

        public int getGroutId() {
            return this.groupId;
        }

        public String getPowerStr() {
            return powerStr;
        }

        public static PowerList getPowerById(int id) {
            PowerList[] powerList = PowerList.values();
            for (PowerList power : powerList) {
                if (power.getSerialId() == id) {
                    return power;
                }
            }

            return UnKnown;
        }
    }

    private String getPowerStr(byte[] aa, byte[] vid) {
        String version = "201401";
        byte[] bb = null;
        byte[] cc;
        if (aa == null)
            return null;
        if (aa[0] == 91) {
            int equals = 0;
            int rightE = 0;
            for (int i = 0; i < aa.length; i++) {
                if (aa[i] == 61) {
                    equals = i;
                }

                if (aa[i] == 93) {
                    rightE = i;
                    break;
                }
            }
            cc = new byte[rightE - equals - 1];
            for (int i = 0; i < cc.length; i++) {
                cc[i] = aa[i + equals + 1];
            }

            bb = new byte[aa.length - rightE - 1];
            for (int i = 0; i < bb.length; i++) {
                bb[i] = aa[i + rightE + 1];
            }

            version = (new String(cc));
        }

        LogUtil.w(TAG, "--LVersion:" + version);

        if ("201501".equals(version)) {
            return getPowerStr201501(bb, vid);
        } else if ("2016001".equals(version)) {
            return getPowerStr2016001(bb, vid);
        } else if ("2016003".equals(version)) {
            return getPowerStr2016003(bb, vid);
        } else if ("2017003".equals(version)) {
            return getPowerStr2017003(bb, vid);
        } else if ("201506".equals(version)) {
            return getPowerStr201506(bb, vid);
        } else {
            //return getPOwerStr201401(aa,vid);
            return null;
        }
    }

    private String getPowerStr201501(byte[] bb, byte[] iim) {
        for (int i = 0; i < bb.length; i++) {
            bb[i] ^= iim[i % iim.length];
        }

        for (int i = 0; i < bb.length; i++) {
            int b1 = bb[i] << 4 & 0xf0;    //左移4位和 11110000与运算 低位变高位
            int b2 = bb[i] >> 4 & 0x0f;    //右移4位和 00001111与运算 高位变低位
            //System.out.println(i + "-" + (b1 + b2));
            bb[i] = (byte) (b1 + b2);

            bb[i] ^= 0xcc;
        }

        for (int i = 0; i < bb.length; i++) {
            bb[i] = convc_r(bb[i], "30714652");
        }

        return (new String(bb));
    }

    /**
     /**
     * nbytelen = bb.length
     * @param bb = szText 密文
     * @param iim = szLicense 设备ID
     * @return 解码数据
     */
    private String getPowerStr201506(byte[] bb, byte[] iim) {
         byte[] constr = "=($E%f3~UH~+^2*7.}.Gx@,&*^%".getBytes();
        byte[] strtmp = new byte[constr.length];
        for (int i = 0; i < constr.length; i++) {
            strtmp[i] = convc_ff(constr[i],"35746102");
        }

        for (int i = 0; i < bb.length; i++) {
            bb[i] = convc_r(bb[i],"50762413");
        }

        for (int i = 0; i < bb.length; i++) {
            bb[i] ^= strtmp[i % constr.length] ^ 0xF7;
            bb[i] ^= constr[i % constr.length] ^ 0x66;
        }
        return (new String(bb));
    }

    private String getPowerStr2016001(byte[] bb, byte[] iim) {
        for (int i = 0; i < bb.length; i++) {
            bb[i] ^= iim[i % iim.length];
        }

        for (int i = 0; i < bb.length; i++) {
            int b1 = bb[i] << 4 & 0xf0;    //左移4位和 11110000与运算 低位变高位
            int b2 = bb[i] >> 4 & 0x0f;    //右移4位和 00001111与运算 高位变低位
            //System.out.println(i + "-" + (b1 + b2));
            bb[i] = (byte) (b1 + b2);

            bb[i] ^= 0xdc;
        }

        for (int i = 0; i < bb.length; i++) {
            bb[i] = convc_r(bb[i], "50324176");
        }


        return (new String(bb));
    }

    private String getPowerStr2016003(byte[] bb, byte[] iim) {
        for (int i = 0; i < bb.length; i++) {
            bb[i] ^= iim[(i * 2) % iim.length];
        }

        for (int i = 0; i < bb.length; i++) {
            //bb[i] = (byte) ((bb[i] << 5) | (bb[i] >> 5) | ((bb[i] << 1)&16) | ((bb[i] >> 1)&8));
            int b1 = bb[i] << 5 & 0xe0;
            int b2 = bb[i] >> 5 & 0x07;
            int b3 = bb[i] << 1 & 0x10;
            int b4 = bb[i] >> 1 & 0x08;
            bb[i] = (byte) (b1 + b2 + b3 + b4);
            bb[i] ^= 0xea;
        }

        for (int i = 0; i < bb.length; i++) {
            bb[i] = convc_r(bb[i], "70632541");
        }

        for (int i = 0; i < bb.length; i++) {
            //bb[i] = (byte) ((bb[i] << 4) | (bb[i] >>4));
            int b1 = bb[i] << 4 & 0xf0;    //左移4位和 11110000与运算 低位变高位
            int b2 = bb[i] >> 4 & 0x0f;    //右移4位和 00001111与运算 高位变低位
            bb[i] = (byte) (b1 + b2);

            bb[i] ^= 0xdc;
        }

        return (new String(bb));
    }

    /**
     * @param bb  密文
     * @param iim 设备ID
     * @return 解码数据
     */
    private String getPowerStr2017003(byte[] bb, byte[] iim) {
        int nbytelen = bb.length;
        for (int i = 0; i < nbytelen; i++) {
            if (i < nbytelen / 3 || i > nbytelen * 2 / 3) {
                byte c = bb[i];
                bb[i] = bb[nbytelen - 1 - i];
                bb[nbytelen - 1 - i] = c;
            }
        }
        for (int i = 0; i < nbytelen; i++) {
            bb[i] ^= iim[(i * 3) % iim.length];
        }
        byte[] constr = "-=$E%f4~UH~+^9*6.}.Gx@,".getBytes();

        for (int i = 0; i < nbytelen; i++) {
            bb[i] ^= constr[i % constr.length];
            //bb[i] = (byte) ((bb[i] << 5) | (bb[i] >> 5) | ((bb[i] << 1)&~0xEF) | ((bb[i] >> 1&~0xF7));
            int b1 = bb[i] << 5;
            int b2 = bb[i] >> 5;
            int b3 = bb[i] << 1 & ~0xEF;
            int b4 = bb[i] >> 1 & ~0xF7;
            bb[i] = (byte) (b1 + b2 + b3 + b4);
            bb[i] ^= 0xf3;
        }

        for (int i = 0; i < nbytelen; i++) {
            bb[i] = convc_r(bb[i], "17024365");
        }

        for (int i = 0; i < nbytelen; i++) {
            //bb[i] = (byte) ((bb[i] << 2) | (bb[i] >>6));
            int b1 = bb[i] << 2;
            int b2 = bb[i] >> 6;
            bb[i] = (byte) (b1 + b2);
            bb[i] ^= 0x8b;
            bb[i] = Integer.valueOf(String.valueOf(bb[i]), 8).byteValue();
        }

        return (new String(bb));
    }


    private byte convc_ff(byte data,String convcStr) {
        byte[] strSEQ = convcStr.getBytes();
        byte ret_char = 0;
        for (int i = 0; i < strSEQ.length; i++) {
            int index = strSEQ[i] - 48;
            int movenum = index - i;
            byte mask = (byte) (1 << (strSEQ.length - i - 1));
            byte temp;
            if (movenum >= 0) {
                temp = (byte)(mask & (data << movenum));
            } else {
                temp = (byte)(mask & (data >> Math.abs(movenum)));
            }
            ret_char = (byte) (ret_char | temp);
        }

        return ret_char;
    }

    private byte convc_r(byte data, String convcStr) {
        byte[] strSEQ = convcStr.getBytes();
        byte ret_char = 0;
        for (int i = 0; i < strSEQ.length; i++) {
            int index = strSEQ[i] - 48;
            int movenum = index - i;

            byte mask = (byte) (1 << (strSEQ.length - index - 1));
            byte temp;

            if (movenum >= 0) {
                temp = (byte) (mask & (data >> movenum));
            } else {
                temp = (byte) (mask & (data << Math.abs(movenum)));
            }

            ret_char = (byte) (ret_char | temp);
        }
        return ret_char;
    }

    /**
     * 检查用户权限
     */
    public int checkUserPower(Context context, String devId) {
        int result;
        byte[] verKey = UtilsMethod.getVersionKey(UtilsMethod.jem(PD));
        TypeConver.ksB67dHyili23(verKey, new String(verKey), verKey.length);

        String imei = MyPhoneState.getInstance().getMyDeviceId(context);
        String lImei = TypeConver.dIuVlic53R(imei);

        if (!lImei.equals("ErrorFlagFile")) {
            LocalInfoCheck localInfo = new LocalInfoCheck(context, lImei, imei);


            if (localInfo.checkInfo()) {
                byte[] autoByte = ConfigRoutine.getInstance().getAutoValue(context);
                if (TypeConver.tcBKlmm0u23f3(autoByte, autoByte.length, devId, 110)) {
                    LogUtil.w(TAG, "--tKl0u98fdmm--A:" + (!ConfigRoutine.getInstance().checkGoOnType(context))
                            + "--b:" + ConfigRoutine.getInstance().checkLineCheckLic(context)
                            + "--c:" + (MyPhoneState.getInstance().isNetworkAvirable(context) && !appModel.isPowerByNet())
                            + "--d:" + appModel.isPowerByNet());
                    //当前次启动未执行过网络鉴权动作
                    if (!appModel.isPowerByNet()) {
                        //如果上次验证时间超过七天,或者上次校验时间被人为修改过或时间前置,
                        //当前次网络校验的返回值必须为文件下载成功,否则鉴权失败
                        if (ConfigRoutine.getInstance().checkLineCheckLic(context)
                                || !ConfigRoutine.getInstance().checkGoOnType(context)) {
                            //强制打开网络,超时15秒
                            result = checkLicenseExists(context, true);
                            //如果当前不是文件存在状态,校验失败
                            if (result != WalkCommonPara.POWER_FTP_FILE_EXISTS) {
                                return returnForSendBro(context, result);
                            }
                        }
                        //如果当前为网络状态为打开,那么只有返回状态为远程权限文件不存在时才鉴权失败,否则做当前做本地文件校验
                        else if (MyPhoneState.getInstance().isNetworkAvirable(context)) {
                            result = checkLicenseExists(context, false);
                            //如果网络检验结果为文件不存在,校验失败
                            if (result == WalkCommonPara.POWER_FTP_FILE_NOT_EXISTS) {
                                return returnForSendBro(context, result);
                            }
                        }

                    }

                    autoByte = ConfigRoutine.getInstance().getAutoValue(context);
                    String actTime = TypeConver.PnBv0Y6nxz9uW1(291, autoByte, autoByte.length, devId) + " 23:59:59";
                    LogUtil.w(TAG, "--checkTime:" + actTime);
                    if (checkTimeLimit(context, actTime)) {

//                        LogUtil.d(TAG, "Enter checkTimeLimit");
                        //String paraType = UtilsCMethod.GetPowerParam(filePath, devId,0);
                        //if(paraType.equals(LicenseType)){
//                        LogUtil.d(TAG, "Enter LicenseType:");
                        String appPower = getPowerStr(TypeConver.AhBSdh80eK0x2(autoByte, autoByte.length, 0x6003, devId), devId.getBytes());
//                        LogUtil.w(TAG, "--appPower:" + Arrays.toString(appPower.toCharArray()));

                        if (appPower == null) {
                            result = WalkCommonPara.POWER_INIT_USERPOWER_FAILD;
                        } else if (initPowerList(appPower)) {
                            int activeTime = checkRemainTime(actTime);
                            LogUtil.d(TAG, "============activeTime 2:" + activeTime + "============");
                            appModel.setActiveDate(actTime);
                            appModel.setActiveTime(activeTime);
                            //初始化权限列表成功
                            appModel.setCheckPowerSuccess(true);
                            result = WalkCommonPara.POWER_LINCESE_SUCCESS;
                        } else {
                            result = WalkCommonPara.POWER_INIT_USERPOWER_FAILD;
                        }

						/*}else{
                            result = WalkCommonPara.POWER_POWER_GROUP_FAILD;
						}*/
                    } else {
                        result = WalkCommonPara.POWER_LICENSE_TIME_OUT;
                    }
                } else {
                    result = WalkCommonPara.POWER_CONTENT_CHECK_FAILD;
                }
            } else {
                result = WalkCommonPara.POWER_LOCAL_INFO_CHACKFAILD;
            }
        } else {
            result = WalkCommonPara.POWER_LOCAL_INFO_BUILDFAILD;
        }
        LogUtil.d(TAG, "--Enter checkPW:" + result);
        context.sendBroadcast(new Intent(ProgressActivity.BASE_PROGRESS_FINISH));
        return result;

    }

    /**
     * 检查权限文件是否存在
     *
     * @param context 上下文
     * @param coerce  是否强制打开网络
     * @return 是否存在
     */
    private int checkLicenseExists(Context context, boolean coerce) {
        try {
            if (inServerOpenNet(context, coerce)) {
                if (APNOperate.getInstance(context).checkNetWorkIsAvailable()) {
                    //return checkFtpFileExist(context);
                    return downloadFtpFile(context);
                }
                return WalkCommonPara.POWER_FTP_NET_DISCONNECTED;
            }
            return WalkCommonPara.POWER_CHECK_POWER_OUTSERVICE;
        } catch (Exception e) {
            e.printStackTrace();
            return WalkCommonPara.POWER_FTP_CONNECTED_FAILD;
        }
    }

    /**
     * 检查FTP权限文件是否存在
     *
     * @param context 上下文
     * @return 是否存在
     */
    public static int checkFtpFileExist(Context context) {
        try {
            String path = UtilsMethod.jem(BuildPower.UL)
                    + File.separator + UtilsMethod.jem(BuildPower.PD)
                    + File.separator + MyPhoneState.getInstance().getMyDeviceId(context)
                    + UtilsMethod.jem(BuildPower.PL);

            if(null!=HttpsUtil.getSSLResult(context, path)){
                return WalkCommonPara.POWER_FTP_FILE_EXISTS;
            }
        } catch (FileNotFoundException fe) {
            return WalkCommonPara.POWER_FTP_FILE_NOT_EXISTS;
        } catch (Exception e) {
            return WalkCommonPara.POWER_FTP_CONNECTED_FAILD;
        }
        return WalkCommonPara.POWER_FTP_FILE_NOT_EXISTS;
    }

    /**
     * 下载FTP端权限文件
     *
     * @param context 上下文
     * @return 文件是否存在
     */
    private static int downloadFtpFile(Context context) {
        //FtpOperate ftpClient = new FtpOperate(context);
        int result;
        try {
            String path = UtilsMethod.jem(BuildPower.UL)
                    + File.separator + UtilsMethod.jem(BuildPower.PD)
                    + File.separator + MyPhoneState.getInstance().getMyDeviceId(context)
                    + UtilsMethod.jem(BuildPower.PL);

            String authPaht = UtilsMethod.jem(BuildPower.UL)
                    + File.separator + UtilsMethod.jem(BuildPower.PD)
                    + File.separator + MyPhoneState.getInstance().getMyDeviceId(context)
                    + File.separator + UtilsMethod.jem(BuildPower.PX);

            String powerStr = HttpsUtil.getSSLResult(context, path);
            String authStr = HttpsUtil.getSSLResult(context, authPaht);

            new CheckAbnormal().checkNormalState(context);
            if (!StringUtil.isNullOrEmpty(powerStr)) {

                ConfigRoutine.getInstance().setAuthValueStr(context, authStr);
                ConfigRoutine.getInstance().setAutoValue(context, powerStr);
                ConfigRoutine.getInstance().setGoOnType(context);
                ConfigRoutine.getInstance().setLineCheckLi(context);
                getTimeByNet(context);
                ApplicationModel.getInstance().setPowerByNet(true);

                result = WalkCommonPara.POWER_FTP_FILE_EXISTS;
            } else {
                result = WalkCommonPara.POWER_FTP_CONNECTED_FAILD;
            }

        } catch (FileNotFoundException fe) {
            result = WalkCommonPara.POWER_FTP_FILE_NOT_EXISTS;
            LogUtil.w(TAG, "--downloadFtpFile File Not exists--");
        } catch (Exception e) {
            result = WalkCommonPara.POWER_FTP_CONNECTED_FAILD;
            LogUtil.w(TAG, "downloadFtpFile", e);
        }

        return result;
    }

    /**
     * 检查当前是否有信号及数据是否连接，如果无连接是否强制打开连接的
     *
     * @param context 上下文
     * @param coerce  是否强制打开网络
     * @return 是否连接网络
     * @throws InterruptedException 中断异常
     */
    private boolean inServerOpenNet(Context context, boolean coerce) throws InterruptedException {
        MyPhoneState phoneState = MyPhoneState.getInstance();
        phoneState.listenPhoneState(context);
        Thread.sleep(100);

        LogUtil.w(TAG, "---isNetworkAvirable:" + phoneState.isNetworkAvirable(context) + "--isServiceAlive:" + phoneState.isServiceAlive());
        if (phoneState.isNetworkAvirable(context) || phoneState.isServiceAlive()) {
            if (APNOperate.getInstance(context).checkNetWorkIsAvailable() || coerce) {
                Intent showProgress = new Intent(context, ProgressActivity.class);
                showProgress.putExtra(ProgressActivity.EXTRA_MESSAGE_ID, R.string.str_waitting);
                showProgress.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                showProgress.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(showProgress);
            }
            long startActivityTimes = System.currentTimeMillis();

            if (!APNOperate.getInstance(context).checkNetWorkIsAvailable() && coerce) {
                APNOperate.getInstance(context).setMobileDataEnabled(true, "", true, 1000 * 15);

                //此处小于1000表示可能因打脱网开网络失败,需等1秒让上面的Activity注册接受消息完成
                if (System.currentTimeMillis() - startActivityTimes < 500) {
                    Thread.sleep(500);
                }
            } else if (APNOperate.getInstance(context).checkNetWorkIsAvailable()) {
                Thread.sleep(500);
            }

            return true;
        }
        return false;
    }

    private int returnForSendBro(Context context, int result) {
        context.sendBroadcast(new Intent(ProgressActivity.BASE_PROGRESS_FINISH));
        return result;
    }

    /*初始化权限列表*/
    private boolean initPowerList(String appPower) {
        try {
            ArrayList<String> appList = new ArrayList<>();
            ArrayList<String> netList = new ArrayList<>();
            ArrayList<String> taskList = new ArrayList<>();
            appModel.getLicenseKeyIDS().clear();
            char[] appPowers = appPower.toCharArray();
            for (int i = 0; i < appPowers.length; i++) {
                if (appPowers[i] == '1') {
                    PowerList power = PowerList.getPowerById(i + 1);
                    String[] powerStr = power.getPowerStr().split(",");
                    switch (power.getGroutId()) {
                        case Power_App:
                            addKeyToArray(appList, powerStr);
                            break;
                        case Power_Net:
                            addKeyToArray(netList, powerStr);
                            break;
                        case Power_Task:
                            addKeyToArray(taskList, powerStr);
                            break;
                    }
                    appModel.getLicenseKeyIDS().add(power.getSerialId());
                }
            }

            appModel.addAppList(appList.toArray(new String[0]));
            appModel.addNetList(netList.toArray(new String[0]));
            appModel.addTaskList(taskList.toArray(new String[0]));
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 将关键字列表添加到指定队列中,且保证不重复
     */
    private void addKeyToArray(ArrayList<String> array, String[] keys) {
        for (String key : keys) {
            if (!array.contains(key)) {
                array.add(key);
            }
        }
    }

    /* 检查当前license有效剩余天数 */
    private int checkRemainTime(String timeLiimit) {
        SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        //Date currentDate = new Date();
        int day = 0;
        try {
            long timeLimit = sdt.parse(timeLiimit).getTime();
            long nowTime = System.currentTimeMillis();
            day = (int) ((timeLimit - nowTime) / 3600000) / 24; // 共计小时数
            //System.out.println("共" + day + "天 ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return day;
    }

    private static long getTimeByNet(Context context) {
        try {
            long ts = UtilsMethod.getNetTimeByBjtime();
            if (ts < 31507200000L) {
                ts = UtilsMethod.getNetTimeByNTP();
            }

            if (ts >= 31507200000L) {    //&& ts > ConfigRoutine.getInstance().getNetTimes(context)
                ConfigRoutine.getInstance().setNetTimes(context, ts);
            } else if (ts < 31507200000L
                    && System.currentTimeMillis() > ConfigRoutine.getInstance().getNetTimes(context)) {
                LogUtil.w(TAG, "--getTimeByNet Disable,Use LocalTime--");

                ts = System.currentTimeMillis();
                ConfigRoutine.getInstance().setNetTimes(context, ts);
            } else {
                LogUtil.w(TAG, "--getTimeByNet Disable--");
                ts = -1;
            }
            return ts;
        } catch (Exception e) {
            LogUtil.w(TAG, "getTimeByNet", e);
        }

        return -1;
    }

    /*检查当前系统时间是否超出试用期限*/
    private boolean checkTimeLimit(Context context, String timeLiimit) {
        try {
            long saveTime;
            long currTime;
            if (ConfigRoutine.getInstance().checkGoOnType(context)) {
                long netTime = ConfigRoutine.getInstance().getNetTimes(context);
                LogUtil.w(TAG, "---===checkTime:" + UtilsMethod.sdfyMdhms.format(netTime));
                if (netTime < ConfigRoutine.limitLTime) {
                    if (inServerOpenNet(context, true)) {
                        saveTime = getTimeByNet(context);
                        context.sendBroadcast(new Intent(ProgressActivity.BASE_PROGRESS_FINISH));
                    } else {
                        return false;
                    }
                    if (saveTime > ConfigRoutine.limitLTime && saveTime - System.currentTimeMillis() > 1000 * TBTime) { //如超半个小时
                        UtilsMethod.setTime(saveTime, context);
                    }
                } else {
                    saveTime = netTime;
                    if (saveTime - System.currentTimeMillis() > 1000 * TBTime) { //如超半个小时
                        if (inServerOpenNet(context, true)) {
                            saveTime = getTimeByNet(context);
                            context.sendBroadcast(new Intent(ProgressActivity.BASE_PROGRESS_FINISH));
                        } else {
                            return false;
                        }
                        if (saveTime > ConfigRoutine.limitLTime) {
                            UtilsMethod.setTime(saveTime, context);
                        }
                    }
                }

                currTime = System.currentTimeMillis();
                if (saveTime > ConfigRoutine.limitLTime && currTime - saveTime > (-TBTime * 1000)) {    //2013-01-01 00:00:00
                    ConfigRoutine.getInstance().setNetTimes(context, currTime);
                } else {
                    return false;
                }
            } else {
                currTime = System.currentTimeMillis();
            }

            if (currTime < UtilsMethod.sdFormatss.parse(timeLiimit).getTime()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
