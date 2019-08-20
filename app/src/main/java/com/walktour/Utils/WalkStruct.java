package com.walktour.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.dinglicom.dataset.model.DataSetEvent;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;

import java.util.ArrayList;

@SuppressLint("InlinedApi")
public class WalkStruct {
    /**
     * 应用类型
     *
     * @author tangwq
     */
    public static enum AppType {
        OperationTest,                //业务测试
        AutomatismTest,            //自动测试
        BackgroundMonitor,        //后台监控
        MutilyTester,                //多路测试
        IndoorTest,                //室内专项
        ScannerTest,                //扫频仪测试
        PioneerTester,                //Pioneer测试
        InfoMonitor,                //参数监控
        DTTest,
        CQTTest,
        Metro,                        //高铁权限
        HighSpeedRail,            //高铁权限
        InnsMap,                            //寅时室内测试
        PolqaType                   //使用内置PolqaLicense文件打分权限
    }

    public static enum SceneType {
        Manual(1001),
        Auto(1002),
        MultiTest(1003),
        BTU(1004),
        Anhui(1005),
        Huawei(1006),
        Fujian(1007),
        SingleSite(1008),
        MultiATU(1009),
        ATU(1010),
        Scanner(1011),
        Wone(1012),
        Metro(1013),
        HighSpeedRail(1014),
        Perception(1015);


        private int sceneId;

        SceneType(int typeId) {
            sceneId = typeId;
        }

        public int getSceneTypeId() {
            return sceneId;
        }

        public static SceneType get(int id) {
            for (SceneType type : values()) {
                if (type.sceneId == id)
                    return type;
            }
            return null;
        }
    }

    public static enum FileType {
        RCU(2001, "1", "rcu"),
        DDIB(2002, "3", "ddib"),
        DCF(2003, "5", "dcf"),
        DTLOG(2004, "2", "lte.dgz"),
        PCAP(2005, "4", "pcap"),
        ORGRCU(2006, "6", "org.rcu"),
        FloorPlan(2007, "", "png"),    //楼层平面图
        TXT(2009, "", "txt"),
        JSON(2010, "", "json"),
        LOCUSJPEG(2011, "", "jpeg"),
        CU(2013, "7", "cu"),
        ECTI(2016, "8", "eCTI"),
        OTSPARAM(2017, "17", "otsparam"),
        LTE(2012, "", "lte"),
        WLAN(2014, "", "lol"),
        MOSZIP(2015, "", "moszip"),
        MIXZIP(2018, "", "mixzip"),
        UNKNOW(2000, "", "unknow");

        private final int fileType;
        private final String ipackTypeId;
        private final String extendName;

        FileType(int typeId, String ipackTypeId, String extName) {
            fileType = typeId;
            this.ipackTypeId = ipackTypeId;
            this.extendName = extName;
        }

        /**
         * 获得文件类型
         */
        public int getFileTypeId() {
            return fileType;
        }

        /**
         * 获得往IPACK传输的文件类型ID
         *
         * @return
         */
        public String getIpackTypeId() {
            return ipackTypeId;
        }

        /**
         * 获得文件类型扩展名+.
         */
        public String getExtendName() {
            return "." + extendName;
        }

        /**
         * 文件类型名称
         */
        public String getFileTypeName() {
            return extendName;
        }

        /**
         * 根据文件ID获得对应文件类型
         *
         * @param fileId
         * @return
         */
        public static FileType getFileType(int fileId) {
            for (FileType fileType : values()) {
                if (fileType.getFileTypeId() == fileId) {
                    return fileType;
                }
            }

            return UNKNOW;
        }

        /**
         * 根据文件扩展类型名获得对应的文件类型
         *
         * @param typeName
         * @return
         */
        public static FileType getFileTypeByName(String typeName) {
            for (FileType fileType : values()) {
                if (fileType.getFileTypeName().equals(typeName)) {
                    return fileType;
                }
            }

            return UNKNOW;
        }
    }

    /**
     * 测试任务类型
     *
     * @author tangwq
     * @param0 任务所属类型，０：其它类型，１：语音，２：ＮＥＴ数据，３：ＷＡＰ数据   4:短信类型
     * @param1 任务类型显示名称
     * @param2 获得写入ＲＣＵ事件结构中的类型
     * @param3 《RCU事件存储结构》中定义的PPP拨号测试业务类型
     */
    public static enum TaskType {
        FleetConnect(2, 3000, "fleet", -1, 0, ""),
        EmptyTask(0, 3001, "empty", TestKind.tkIdle.getKind(), 0, "Idle"),             //空测试
        InitiativeCall(1, 3002, "call", TestKind.tkDial.getKind(), 0, "MOC"),             //语音主叫
        PassivityCall(1, 3003, "call", TestKind.tkDial.getKind(), 0, "MTC"),             //语音被叫
        FTPDownload(2, 3004, "ftpD", TestKind.tkFTPDownload.getKind(), RcuEventCommand.TEST_TYPE_FTP_DOWNLOAD, "FTP Download"),//Ftp下载
        FTPUpload(2, 3005, "ftpU", TestKind.tkFTPUpload.getKind(), RcuEventCommand.TEST_TYPE_FTP_UPLOAD, "FTP Upload"),    //Ftp上传
        Http(2, 3006, "httpL", TestKind.tkHttpPage.getKind(), RcuEventCommand.TEST_TYPE_HTTP_PAGE, "HTTP Page"),        //HTTP仅剩下面三种，此处暂保留
        HttpRefurbish(2, 3006, "httpR", TestKind.tkHttpPage.getKind(), RcuEventCommand.TEST_TYPE_HTTP_PAGE, "HTTP Page"),    //HTTP刷新
        HttpDownload(2, 3007, "httpD", TestKind.tkHTTP.getKind(), RcuEventCommand.TEST_TYPE_HTTP, "HTTP Download"),    //HTTP下载
        HttpUpload(2, 3008, "httpU", TestKind.tkHttpUp.getKind(), RcuEventCommand.TEST_TYPE_HTTP_UP, "HTTP Upload"),        //Http上传
        HTTPVS(2, 3009, "httpvs", TestKind.tkHttpVS.getKind(), RcuEventCommand.TEST_TYPE_HTTPVS, "Video Play"),            //Video Play(HTTPVS) 不能再并发里头做测试
        Stream(2, 3009, "VideoStream", TestKind.tkVideoStream.getKind(), RcuEventCommand.TEST_TYPE_Video_Streaming, "Video Streaming"),    //流媒体
        Ping(2, 3010, "ping", TestKind.tkPing.getKind(), RcuEventCommand.TEST_TYPE_Ping, "Ping"),        //Ping
        EmailPop3(2, 3011, "email", TestKind.tkEMailDownload.getKind(), RcuEventCommand.TEST_TYPE_Email_POP3, "Email Receive"),    //邮件接收
        EmailSmtp(2, 3011, "email", TestKind.tkEMailUpload.getKind(), RcuEventCommand.TEST_TYPE_Email_SMTP, "Email Send"),    //邮件发送
        EmailSmtpAndPOP(2, 3011, "email", TestKind.tkEMailUpload.getKind(), RcuEventCommand.TEST_TYPE_Email_POP3, "Email Self"),    //邮件自发自收
        PBM(2, 3012, "pbm", TestKind.tkPBM.getKind(), RcuEventCommand.TEST_TYPE_PBM, "PBM"),                    //PBM
        MultiftpDownload(2, 3013, "MFTPDownload", TestKind.tkMultFtpDownload.getKind(), RcuEventCommand.TEST_TYPE_MultFtpDownLoad, "Multi-FTP Download"),//FTP多路并发下载
        MultiftpUpload(2, 3014, "MFTPUpload", TestKind.tkMultFtpUpload.getKind(), RcuEventCommand.TEST_TYPE_MultFtpUpload, "Multi-FTP Upload"),    //FTP多路并发上行
        MultiRAB(2, 3015, "multirab", TestKind.tkMultFtpDownload.getKind(), RcuEventCommand.TEST_TYPE_MultiDataService, "Parallel Service"),//并发业务 默认为数据类型
        SpeedTest(2, 3016, "speedtest", TestKind.tkSpeedTest.getKind(), RcuEventCommand.TEST_TYPE_SpeedTest, "Speedtest"),        //SpeedTest
        DNSLookUp(2, 3017, "DnsLookUp", TestKind.tkIdle.getKind(), RcuEventCommand.TEST_TYPE_DNSLookUp, "DNS Lookup"),        //DNSLookUp
        Facebook(2, 3018, "facebook", TestKind.tkfacebook.getKind(), RcuEventCommand.TEST_TYPE_FACEBOOK, "Facebook"),        //Facebook
        Iperf(2, 3019, "iperf", TestKind.tkIperf.getKind(), RcuEventCommand.TEST_TYPE_Iperf, "iPerf"),            //Iperf业务
        TraceRoute(2, 3020, "traceroute", TestKind.tkTraceRoute.getKind(), RcuEventCommand.TEST_TYPE_TraceRoute, "Trace Route"),        //TraceRoute
        Attach(0, 3021, "attch", TestKind.tkAttach.getKind(), RcuEventCommand.TEST_TYPE_Attach, "Attach"),        //Attach
        PDP(0, 3022, "pdpactive", TestKind.tkPDPActive.getKind(), RcuEventCommand.TEST_TYPE_PDP, "PDP Active"),            //PDP
        WapLogin(3, 3023, "wap", TestKind.tkWAP.getKind(), RcuEventCommand.TEST_TYPE_WAP, "WAP Page"),            //Wap登陆
        WapRefurbish(3, 3024, "wap", TestKind.tkWAP.getKind(), RcuEventCommand.TEST_TYPE_WAP, "WAP Page"),            //Wap刷新
        WapDownload(3, 3025, "wap", TestKind.tkWAPDL.getKind(), RcuEventCommand.TEST_TYPE_WAP_Download, "WAP Download"),//Wap下载
        SMSIncept(4, 3026, "smsI", TestKind.tkSMS.getKind(), 0, "SMS Receive"),                                        //接收短信
        SMSSend(4, 3026, "smsS", TestKind.tkSMS.getKind(), 0, "SMS Send"),                                        //发送短信
        SMSSendReceive(4, 3026, "smsSr", TestKind.tkSMS.getKind(), 0, "SMS Self"),                                        //自收短信
        MMSIncept(3, 3027, "mms", TestKind.tkMMS.getKind(), RcuEventCommand.TEST_TYPE_MMS_Retrieval, "MMS Receive"),//接收彩信
        MMSSend(3, 3027, "mms", TestKind.tkMMS.getKind(), RcuEventCommand.TEST_TYPE_MMS_Send, "MMS Send"),    //发送彩信
        MMSSendReceive(3, 3027, "mms", TestKind.tkMMS.getKind(), RcuEventCommand.TEST_TYPE_MMS_Send, "MMS Self"),    //发送彩信
        WeiBo(2, 3028, "weibo", TestKind.tkWeiBo.getKind(), RcuEventCommand.TEST_TYPE_WEIBO, "Weibo"),                //WeiBo
        Fection(2, 3030, "fection", TestKind.tkFetion.getKind(), RcuEventCommand.TEST_TYPE_Fetion, ""),            //飞信-之前用于控制MOS
        InitiativeVideoCall(1, 3000, "call", TestKind.tkVideoCall.getKind(), 0, ""),            //视频主叫
        PassivityVideoCall(1, 3000, "call", TestKind.tkVideoCall.getKind(), 0, ""),                //视频被叫
        WlanLogin(0, 3031, "WLAN Logon", TestKind.tkWLanAPCoverage.getKind(), 0, "WLAN Web Auth 认证"),                                        //Wlan登录
        WlanAP(0, 3032, "WLAN AP", TestKind.tkWLanAPCoverage.getKind(), 0, "WLAN AP 关联"),                                        //Wlan AP关联
        WlanEteAuth(0, 3033, "WLAN Ete Auth", TestKind.tkWLanAPCoverage.getKind(), 0, "WLAN ETE AUTH 认证"),                                        //Wlan ETE AUTH
        WeChat(2, 3034, "WeChat", TestKind.tkWeChat.getKind(), RcuEventCommand.TEST_TYPE_WECHAT, "WeChat"),            //微信
        Line(2, 3035, "Line", TestKind.tkFetion.getKind(), RcuEventCommand.TEST_TYPE_Fetion, "Line"),                //Line
        Twitter(2, 3037, "Twitter", TestKind.tkFetion.getKind(), RcuEventCommand.TEST_TYPE_Fetion, "Twitter"),            //Twitter
        Instagram(2, 3038, "Instagram", TestKind.tkFetion.getKind(), RcuEventCommand.TEST_TYPE_Fetion, "Instagram"),        //Instagram
        UDP(2,3039,"UDP",TestKind.tkUDP.getKind(), RcuEventCommand.TEST_TYPE_UDP, "UDP"),
        REBOOT(2,3042,"REBOOT",TestKind.tkREBOOT.getKind(), RcuEventCommand.TEST_TYPE_REBOOT, "REBOOT"),//开关机业务测试
        OpenSignal(2,3040,"OpenSignal",TestKind.tkOpenSignal.getKind(), RcuEventCommand.TEST_TYPE_OpenSignal, "OpenSignal"),//开关机业务测试
        MultiHttpDownload(2,3041,"MultiHttpDownload",TestKind.tkMultiHttpDownload.getKind(), RcuEventCommand.TEST_TYPE_MultiHttpDownload, "MultiHttpDownload"),//开关机业务测试
        WeCallMoc(2,3043,"WeCallMoc",TestKind.tkWeCallMoc.getKind(), RcuEventCommand.TEST_TYPE_WeCallMoc, "WeCallMoc"),//Ott 业务测试
        WeCallMtc(2,3044,"WeCallMtc",TestKind.tkWeCallMtc.getKind(), RcuEventCommand.TEST_TYPE_WeCallMtc, "WeCallMtc"),//Ott 业务测试
        SkypeChat(2,3045,"SkypeChat",TestKind.tkSkypeChat.getKind(), RcuEventCommand.TEST_TYPE_SkypeChat, "SkypeChat"),//Ott 业务测试
        SinaWeibo(2,3046,"SinaWeibo",TestKind.tkSinaWeibo.getKind(), RcuEventCommand.TEST_TYPE_SinaWeibo, "SinaWeibo"),//Ott 业务测试
        QQ(2,3047,"QQ",TestKind.tkQQ.getKind(), RcuEventCommand.TEST_TYPE_QQ, "QQ"),//Ott 业务测试
        WhatsAppChat(2,3048,"WhatsAppChat",TestKind.tkWhatsAppChat.getKind(), RcuEventCommand.TEST_TYPE_WHATSAPP, "WhatsAppChat"),//Ott 业务测试
        WhatsAppMoc(2,3049,"WhatsAppMoc",TestKind.tkWhatsAppMoc.getKind(), RcuEventCommand.TEST_TYPE_WHATSAPP_Moc, "WhatsAppMoc"),//Ott 业务测试
        WhatsAppMtc(2,3050,"WhatsAppMtc",TestKind.tkWhatsAppMtc.getKind(), RcuEventCommand.TEST_TYPE_WHATSAPP_Mtc, "WhatsAppMtc"),//Ott 业务测试
        Facebook_Ott(2,3051,"Facebook_Ott",TestKind.tkFacebook2.getKind(), RcuEventCommand.TEST_TYPE_FACKBOOK_OTT, "Facebook(OTT)"),//Ott 业务测试
        Instagram_Ott(2,3052,"Instagram_Ott",TestKind.tkInstagram2.getKind(), RcuEventCommand.TEST_TYPE_INSTAGRAM_OTT, "Instagram(OTT)"),//Ott 业务测试
        IDT(2,3053,"IDT",TestKind.tkIDT.getKind(), RcuEventCommand.TEST_TYPE_IDT, "IDT"),//Ott 业务测试
        MOS(0, 3000, "mos", TestKind.tkMosSelfTest.getKind(), 0, ""),        //MOS权限
        POLQA(0, 3000, "mos", TestKind.tkMosSelfTest.getKind(), 0, ""),        //MOS POLQA权限
        Phone2Fixed(0, 3000, "mos", TestKind.tkMosSelfTest.getKind(), 0, ""),        //MOS 手机－固定端权限
        WalktourUpload(2, 3000, "walktourupload", TestKind.tkIdle.getKind(), 0, ""),                //数据上传
        VOIP(2, 3000, "voip", TestKind.tkVOIP.getKind(), 0, "Voip"),             //语音
        Default(0, 3000, "default", TestKind.tkIdle.getKind(), 0, "");                                            //注意这里的参数必须用具体数值，不能引用NON\VOICE\DATA\WAP

        public static int NON = 0;
        public static int VOICE = 1;
        public static int DATA = 2;
        public static int WAP = 3;
        private int dataType;
        private int testKind;
        private int testType;    //拨号时写入RCU事件中的测试类型
        private String typeName = "";
        private int testTypeId;
        private String xmlTaskType;

        private TaskType(int type, int typeId, String name, int kind, int tType, String xmlTaskType) {
            dataType = type;
            typeName = name;
            testKind = kind;
            testType = tType;
            testTypeId = typeId;
            this.xmlTaskType = xmlTaskType;

        }

        /**
         * @return the typeName
         */
        public String getTypeName() {
            return typeName;
        }

        /**
         * 反加数据库存储中的类型ID
         *
         * @return
         */
        public int getTestTypeId() {
            return testTypeId;
        }

        /**
         * 是否数据测试
         */
        public boolean isDataTest() {
            return dataType == DATA || dataType == WAP;
        }

        public int getDataType() {
            return dataType;
        }

        /**
         * 获得Pioneer testKind类型
         */
        public int getTestKind() {
            return testKind;
        }

        /**
         * 数据业务PPP拨号时获得当前业务的测试类型,由<<RCU事件存储结构>>中定义
         */
        public int getTestType() {
            return testType;
        }

        /**
         * 真正写入通用测试计划的测试类型
         *
         * @return
         */
        public String getXmlTaskType() {
            return xmlTaskType;
        }

        /**
         * 显示在界面的任务名
         */
        public String getShowName(Context context) {
            String[] keys = context.getResources().getStringArray(R.array.task_key);
            String[] showNames = context.getResources().getStringArray(R.array.task_beta1);
            for (int i = 0; i < keys.length; i++) {
                if (keys[i].equals(this.name())) {
                    return showNames[i];
                }
            }
            LogUtil.w("getShowName", "can't not find " + this.name());
            return "unknown";
        }
    }

    public static class TaskTypeIDs{
        public static final int InitiativeCall = 1,PassivityCall = 2,PBM=3,
                Ping = 4,Attach = 5 ,PDP = 6 , FTPUpload = 7,FTPDownload = 8,MultiftpUpload=9,TaskMultiftpUploadModel = 10,
                MultiftpDownload = 11,EmailPop3 = 12,EmailSmtp = 13,SMSIncept=14,SMSSend = 15,SMSSendReceive=16,
                MMSIncept=17,MMSSend=18,MMSSendReceive=19,WapRefurbish = 20,WapLogin = 21,WapDownload = 22,Http = 23,
                HttpRefurbish = 24,HttpDownload=25, WlanLogin=26,Stream = 27,DNSLookUp=28,SpeedTest=29,HttpUpload = 30,
                HTTPVS = 31,Facebook=32,TraceRoute=33,Iperf=34,WeiBo=35,WeChat=36,UDP = 37,REBOOT = 38,OPENSIGNAL = 39,MULTIHTTPDOWNLOAD = 40,
                WeCallMoc = 41,WeCallMtc=42,SkypeChat=43,SinaWeibo=44,QQ=45, WhatsAppChat =46, WhatsAppMoc =47,WhatsAppMtc = 48,Facebook_Ott = 49,Instagram_Ott = 50;
    }
    /**
     * 测试过程中的异常监控事件
     *
     * @author Tangwq
     */
    public static enum Abnormal {
        ET_MO_Drop(DataSetEvent.ET_MO_Drop, "40030174", "Outgoing Dropped Call"),
        ET_MO_Block(DataSetEvent.ET_MO_Block, "40030176", "Outgoing Blocked Call"),
        ET_MT_Drop(DataSetEvent.ET_MT_Drop, "40030184", "Incoming Dropped Call"),
        ET_MT_Block(DataSetEvent.ET_MT_Block, "40030185", "Incoming Blocked Call"),
        ET_MO_CSFB_Failure(DataSetEvent.ET_MO_CSFB_Failure, "4003031A", "Originating CSFB Failure"),
        ET_MO_CSFB_Abnormal(DataSetEvent.ET_MO_CSFB_Abnormal, "40030337", "Originating CSFB Abnormal"),
        ET_MT_CSFB_Failure(DataSetEvent.ET_MT_CSFB_Failure, "40030320", "Terminating CSFB Failure"),
        ET_MT_CSFB_Abnormal(DataSetEvent.ET_MT_CSFB_Abnormal, "40030338", "Terminating CSFB Abnormal"),
        ET_ReturnToLTE_Failure(DataSetEvent.ET_ReturnToLTE_Failure, "4003032C", "Return Back To LTE Failure"),
        ET_Handover_From_WCDMA_Failure(DataSetEvent.ET_Handover_From_WCDMA_Failure, "400300B2", "CS Handover Failure(WCDMA -> GSM)"),
        ET_Handover_To_WCDMA_Failure(DataSetEvent.ET_Handover_To_WCDMA_Failure, "40030082", "CS Handover Failure(GSM -> WCDMA)"),
        ET_Handover_From_TD_Failure(DataSetEvent.ET_Handover_From_TD_Failure, "400300B5", "CS Handover Failure(TD-SCDMA -> GSM)"),
        ET_Handover_To_TD_Failure(DataSetEvent.ET_Handover_To_TD_Failure, "40030085", "CS Handover Failure(GSM -> TD-SCDMA)"),
        ET_PS_Handover_From_WCDMA_Failure(DataSetEvent.ET_PS_Handover_From_WCDMA_Failure, "400300C2", "PS Handover Failure(WCDMA -> GSM)"),
        ET_PS_Handover_To_WCDMA_Failure(DataSetEvent.ET_PS_Handover_To_WCDMA_Failure, "400300D2", "PS Handover Failure(GSM ->WCDMA )"),
        ET_PS_Handover_From_TD_Failure(DataSetEvent.ET_PS_Handover_From_TD_Failure, "400300C5", "PS Handover Failure(TD-SCDMA -> GSM)"),
        ET_PS_Handover_To_TD_Failure(DataSetEvent.ET_PS_Handover_To_TD_Failure, "400300D5", "PS Handover Failure(GSM -> TD-SCDMA)"),
        ET_LTE_Handover_To_WCDMA_Failure(DataSetEvent.ET_LTE_Handover_To_WCDMA_Failure, "40030102", "LTE Handover To WCDMA Failure"),
        ET_LTE_Handover_To_TD_Failure(DataSetEvent.ET_LTE_Handover_To_TD_Failure, "40030105", "LTE Handover To TD-SCDMA Failure"),
        ET_LTEHandoverFailure(DataSetEvent.ET_LTEHandoverFailure, "40030302", "LTE Handover Failure"),
        ET_FTP_DL_Drop(DataSetEvent.ET_FTP_DL_Drop, "40000084", "FTP Download Drop"),
        ET_FTP_UP_Droped(DataSetEvent.ET_FTP_UP_Droped, "40000094", "FTP Upload Drop"),
        ET_HttpPageDrop(DataSetEvent.ET_HttpPageDrop, "40000F05", "HTTP Page Drop"),
        ET_HTTP_Drop(DataSetEvent.ET_HTTP_Drop, "400000A4", "HTTP Download Drop"),
        ET_HttpUpload_Drop(DataSetEvent.ET_HttpUpload_Drop, "40001006", "HTTP Upload Drop"),
        ET_HttpVideo_Drop(DataSetEvent.ET_HttpVideo_Drop, "40001016", "Video Play Drop"),
        ET_Ping_NFail(DataSetEvent.ET_Ping_NFail, "40000193", "Ping Failure"),
        ET_Email_SMTP_Drop(DataSetEvent.ET_Email_SMTP_Drop, "400000C4", "Email Send Drop"),
        ET_Email_POP3_Drop(DataSetEvent.ET_Email_POP3_Drop, "400000B4", "Email Receive Drop"),
        Unknow(0, "40000000", "Unknow");

        private final int eventId;
        private final String abnormalId;
        private final String abnormalStr;

        Abnormal(int eventId, String abnormalId, String abnormalStr) {
            this.eventId = eventId;
            this.abnormalId = abnormalId;
            this.abnormalStr = abnormalStr;
        }

        /**
         * 获得异常RCU事件ID
         */
        public int getEventId() {
            return eventId;
        }

        /**
         * 获得异常RCU事件对应数据库编码为400+异常ID结构
         */
        public String getAbnormalId() {
            return abnormalId;
        }

        /**
         * 获得异常事件描述
         */
        public String getAbnormalStr() {
            return abnormalStr;
        }

        /**
         * 获得取要监控的事件异常列表
         */
        public static ArrayList<Integer> getAbnormalList() {
            ArrayList<Integer> abnormalList = new ArrayList<Integer>();
            for (Abnormal abnormal : Abnormal.values()) {
                abnormalList.add(abnormal.getEventId());
            }
            return abnormalList;
        }
    }

    public static enum TestType {
        DT(5001),
        CQT(5002);

        private int testType;

        TestType(int typeId) {
            testType = typeId;
        }

        public int getTestTypeId() {
            return testType;
        }
    }

    /**
     * 当前网络类型状态相关属性及取值ID
     */
    public static enum CurrentNetState {
        WCDMA(UnifyParaID.NET_WCDMA, UnifyParaID.CURRENT_STATE_WCDMA, 3, "WCDMA", 0x02, 6001),
        GSM(UnifyParaID.NET_GSM, UnifyParaID.CURRENT_STATE_GSM, 2, "GSM", 0x01, 6002),
        CDMA(UnifyParaID.NET_CDMA_EVDO, UnifyParaID.CURRENT_STATE_CDMA, 2, "CDMA", 0x08, 6003),
        TDSCDMA(UnifyParaID.NET_TDSCDMA, UnifyParaID.CURRENT_STATE_TDSCDMA, 3, "TD-SCDMA", 0x04, 6005),
        LTE(UnifyParaID.NET_LTE, UnifyParaID.CURRENT_STATE_LTE, 4, "LTE", 0x10, 6006),
        NBIoT(UnifyParaID.NET_NB_IoT, UnifyParaID.CURRENT_STATE_NB_IOT, 5, "NBIoT", 0x30, 6008),
        CatM(UnifyParaID.NET_CAT_M, UnifyParaID.CURRENT_STATE_CAT_M, 5, "CatM", 0x100, 6009),
        ENDC(UnifyParaID.NET_ENDC, UnifyParaID.CURRENT_STATE_ENDC, 4, "ENDC", 0x200, 6010),
        NoService(UnifyParaID.NET_NO_SERVICE, 0, 0, "NoService", 0x80, 6007),
        Unknown(UnifyParaID.NET_UNKONWN, "Unknown", 0x20);

        private final int netId;        //Walktour中定义的当前网络ID值
        private final int netStateId;    //网络状态ID,数据集中的ID表示
        private final int general;        //第几代网络
        private final String netName;    //网络名字
        private final int netType;        //网络类型
        private final int netTypeId;    //数据库字典表中存放的网络值

        private CurrentNetState(int currentNetId, String netNameP, int netTypeP) {
            netId = currentNetId;
            netStateId = 0;
            general = 0;
            netName = netNameP;
            netType = netTypeP;
            netTypeId = 6000;
        }

        private CurrentNetState(int currentNetId, int currentNetStateId, int g, String netNameP, int netTypeP, int typeId) {
            netId = currentNetId;
            netStateId = currentNetStateId;
            general = g;
            netName = netNameP;
            netType = netTypeP;
            netTypeId = typeId;
        }

        /**
         * 获得当前网络的ID值
         */
        public int getCurrentNetId() {
            return netId;
        }

        /**
         * 获得当前网络取网络状态的ID值
         */
        public int getCurrentNetStateId() {
            return netStateId;
        }

        /**
         * 当前网络名字
         */
        public String getNetTypeName() {
            return netName;
        }

        /**
         * 获得网络类型
         */
        public int getNetType() {
            return netType;
        }

        /**
         * 根据当前网络类型ID获得对应的网络类型结构
         */
        public static CurrentNetState getNetStateById(int netId) {
            CurrentNetState[] netStates = CurrentNetState.values();
            for (CurrentNetState netState : netStates) {
                if (netState.getCurrentNetId() == netId) {
                    return netState;
                }
            }
            return Unknown;
        }

        public static CurrentNetState getNetTypeId(int netId) {
            CurrentNetState[] netStates = CurrentNetState.values();
            for (CurrentNetState netState : netStates) {
                if (netState.getNetTypeId() == netId) {
                    return netState;
                }
            }
            return Unknown;
        }

        /**
         * @return 几G网络
         */
        public int getGeneral() {
            return general;
        }

        /**
         * 数据库字典表中存放的网络值
         *
         * @return
         */
        public int getNetTypeId() {
            return netTypeId;
        }
    }

    /**
     * 电信设置项枚举信息
     *
     * @author Tangwq
     */
    public static enum TelecomSetting {
        Normal(0, 0),
        G4Only(1, 4),
        G3Only(2, 3);

        private final int selectIndex;
        private final int networkGeneral;

        private TelecomSetting(int index, int geranal) {
            this.selectIndex = index;
            this.networkGeneral = geranal;
        }

        /**
         * 获得设置项选中序号
         *
         * @return
         */
        public int getSelectIndex() {
            return selectIndex;
        }

        /**
         * 当前选择的模式
         *
         * @return
         */
        public int getNetWorkGeneral() {
            return networkGeneral;
        }

        /**
         * 根据选设序号返回当前设指定网下切类型
         *
         * @param index
         * @return
         */
        public static TelecomSetting getTelecomSetByIndex(int index) {
            for (TelecomSetting set : values()) {
                if (set.getSelectIndex() == index) {
                    return set;
                }
            }

            return Normal;
        }
    }

    /**
     * 查看页界的当类型
     *
     * @author tangwq
     */
    public enum ShowInfoType {
        Map,                        //一级地图界面
        OfflineMap,                    //地图
        OnlineMap,                    //地图
        OtherMap,                    //其他地图
        GoogleMap,                    //Google地图
        BaiduMap,                    //百度地图
        AMap,                       //高德地图
        InnsMap,                    //寅时地图
        AutoMark,               //外置陀螺仪自动打点
        Info,                        //信息
        Scanner,                    //扫频仪显示
        Param,                    //参数
        Base,                        //基站
        Chart,                        //图表
        ChartYw,                    //数据业务相关图表
        Normal(NetType.Normal),
        Gsm(2, NetType.GSM),    //Gsm信息
        NBIoT(NetType.NBIoT),
        CatM(NetType.CatM),
        CtoI,                        //CtoI
        Edge(NetType.GSM),        //Edge Gprs
        Umts(1, NetType.WCDMA),    //Umts wcdma
        Hspa(NetType.WCDMA),    //Hspa wcdma
        HspaPlus(NetType.WCDMA),//Hspa wcdma
        Cdma(3, NetType.CDMA),    //cdma
        EvDo(4, NetType.EVDO),    //evdo cdma
        Data,         //数据业务信息
        Total,        //实时统计
        Event,         //事件
        L3Msg,         //层3信息
        AlarmMsg,     //告警页面
        WLAN(9, NetType.WiFi), // Wlan相关，如AP属性、AP列表、AP曲线图、信道分布图
        WCDMA(1, NetType.WCDMA),
        TDSCDMA(5, NetType.TDSCDMA),//TD-SCDMA
        TDHspaPlus(NetType.TDSCDMA),//TDhspa
        Enforce,      //强制
        Playback,     //数据回放
        ShowTotal,    //显示统计功能
        PauseTest,    //暂停测试
        LockFrequency,//锁定网络
        LockCAFrequency,//锁定网络
        VideoPlay,    //媒体业务
        LTE(6, NetType.LTE),
        LTEFDD(NetType.LTE),
        LTETDD(NetType.LTE),
        LTECA(NetType.LTE),
        FDD3CaD(NetType.LTE),    //FDD3载波下载权限
        TDD3CaD(NetType.LTE),    //TDD3载波下载权限
        FDD2CaU(NetType.LTE),    //FDD2载波上行权限
        TDD2CaU(NetType.LTE),    //TDD2载波上行权限
        FDD800M(NetType.LTE),    //FDD800M 0x7F06001C 值为5或26且没有LTE FDD 800M
        LTE4T4R(NetType.LTE),
        ENDC(NetType.ENDC),
        CSFBAnalysis,    //CSFB异常分析
        VoLTEAnalyse,    //VoLTE异常分析
        LTEDataAnalysis,//LTE Data异常分析
        GMCCProj,        //超级用户系统设置权限
        CreateBin,        //创建Bin文件
        OpenDataSetLog,    //打开数据集日志选项
        LogcatAllLog,        //Logcat全日志开关
        CheckOutOfService,//检查脱网
        BluetoothSync,    //蓝牙同步权限
        Atu,            //ATU权限
        Btu,            //BTU平台权限
        DCF,            //DCF权限
        CU,                //联通特有数据CU权限
        eCTI,            //eCTI权限
        OTS,            //OTS权限
        WOnePro,        //WONE项目权限
        AHWorkOrder,    //安徽电信工单项目模块
        HWWorkOrder,    //华为工单项目模块
        FJWorkOrder,    //福建投诉工单项目模块
        SingleStation,    //单站验证
        TCPIPCapture,    //TCP/IP抓包权限
        DontSaveFile,    //不存保存文件
        BaiduSpecialCount,//百度云转用帐号
        VoLTE,   //通用Volte权限，国内单指移动
        UnicomVoLTE,      //联通Volte权限
        TelecomVoLTE,     //电信Volte权限
        VoLTEVideo,    //Volte视频
        SAMSUNGVoLTE,    //三星VoLTE权限
        TcpIpPcap,
        TelecomSwitch,    //电信下切权限
        Default;

        private final int nettype;
        private final NetType showGroup;

        private ShowInfoType(int type) {
            this.nettype = type;
            this.showGroup = NetType.Normal;
        }

        private ShowInfoType(NetType netGroup) {
            this.nettype = 0;
            this.showGroup = netGroup;
        }

        private ShowInfoType(int type, NetType netGroup) {
            this.nettype = type;
            this.showGroup = netGroup;
        }

        private ShowInfoType() {
            this.nettype = 0;
            this.showGroup = NetType.Normal;
        }

        /**
         * 获得当前类型的网络ID，该值用于决定当前页面是否有权限显示
         * 如果当前没有GSM权限，则GSM相应的页面不显示
         */
        public int getNetType() {
            return this.nettype;
        }

        /**
         * 获得`界面分组类型
         */
        public NetType getNetGroup() {
            return this.showGroup;
        }
    }

    /**
     * 统计页面显示类型
     *
     * @author tangwq
     */
    public static enum ShowTotalType {
        Voice,
        Ftp,
        Wap,
        Mms,
        Sms,
        Http,
        Ping,
        Email,
        Para,
        ParaC2,
        Event,
        SecondGeQuality,
        SecondGeData,
        SecondGePara,
        SecondGeEvent,
        vs,
        Default
    }

    /**
     * 扫频仪测试任务类型
     *
     * @author zhihui.lian
     */
    public static enum ScannerTaskType {
        CW(),
        Pilot(),
        Spectrum(),
        Colorcode(),
        Default();


        /**
         * 显示在界面的任务名
         */
        public String getShowName(Context context) {
            String[] keys = context.getResources().getStringArray(R.array.sc_task_key);
            String[] showNames = context.getResources().getStringArray(R.array.sc_task_beta1);
            for (int i = 0; i < keys.length; i++) {
                if (keys[i].equals(this.name())) {
                    return showNames[i];
                }
            }
            return "unknown";
        }
    }


    /**
     * 数据任务窗口参数名
     *
     * @author tangwq
     */
    public static enum DataTaskValue {
        TaskRepeat,    //当前任务的重复次数
        TaskOrder,    //当前任务的序号(从1开始)，即当前测试在测试任务重复次数中的序号
        FtpDlAllSize,    //下载总大小
        FtpUlAllSize,    //上传总大小
        FtpDlCurrentSize,//当前上传大小
        FtpUlCurrentSize,//当前下载大小
        FtpDlThrput,    //下载速度
        FtpUlThrput,    //上传速度
        FtpDlMeanRate,    //下载平均值
        FtpUlMeanRate,    //上传平均值
        FtpDlProgress,    //下传进度
        FtpUlProgress,    //上载进度
        ActiveThreadNum,//当前活动线程数
        PeakValue,        //峰值
        useTimes,        //时长
        PingDelay,        //ping 延迟
        HttpDlThrput,    //http 下载大小
        WapDlThrput,    //Wap 下载大小
        Pop3Thrput,        //发送邮件大小
        SmtpThrput,        //接收邮件大小

        //仪表盘关键字枚举
        BordLeftTitle,        //仪表盘左标题
        BordRightTile,        //仪表盘右标题
        BordCurrentSpeed,    //仪表盘当前显示速率
        BordPoints,        //仪表盘瞬时速率
        BordProgress,        //仪表盘当前进度
        IsWlanTest,

        //PBM参数
        PBMULBandwidth,        //上行带宽
        PBMDLBandwidth        //下行带宽
    }

    /**
     * 数据业务测试过程中产生的事件名称
     *
     * @author tangwq
     */
    public static enum DataTaskEvent {
        Start_Test("Start Test"),
        Test_Finished("Test Finished"),
        Stop_Logging("Stop Logging"),
        Task_Stop("Test Stopped"),
        ALL_LAST_DATA("all last data"),
        PPP_Dial_Start("PPP Dial Start"),
        PPP_Dial_Success("PPP Dial Success"),
        PPP_Dial_Failure("PPP Dial Failure"),
        PPP_Hungup("PPP Dial Hangup"),
        WiFi_ConnectStart("WiFi AP ConnectStart"),
        WiFi_ConnectSuccess("WiFi AP ConnectSuccess"),
        WiFi_ConnectFailure("WiFi AP ConnectFailed"),
        //ftp event
        FTP_TYPE_UPLOAD("FTP Upload"),
        FTP_TYPE_DOWNLOAD("FTP Download"),
        FTP_CONNECT_BEGIN("Connect"),
        FTP_CONNECT_SUCCESS("Connect Success"),
        FTP_CONNECT_FAILED("Connect Failure"),
        FTP_LOGIN_SUCCESS("Connect Success"),
        FTP_LOGIN_FAILED("Connect Failure"),
        FTP_RETR_OR_STOR("ftp retr or stor"),
        FTP_DL_RETR("Send RETR"),
        FTP_UL_STOR("Send STOR"),
        FTP_FILE_SIZE("Ftp File Size"),
        FTP_FIRST_DATA("First Data"),
        FTP_CUR_DATA("ftp cur data"),
        FTP_LAST_DATA("Last Data"),
        FTP_DROP("Drop"),
        FTP_FILE_NOT_FOUND("File Not Found"),
        FTP_PARSE_URL_ERROR("ftp rarse url error"),
        FTP_REQUEST("Ftp Request"),
        FTP_RESPONSE("Ftp Response"),
        FTP_MKD_ERROR("Make Directory Error"),
        FTP_CWD_ERROR("No Such File Or Directory"),
        FTP_MALLOC_NULL("Ftp Malloc Null"),
        FTP_SERVER_NORESPONSE("Server No Response"),
        FTP_FILE_SIZE_ZERO("Upload Or Download File Error"),
        FTP_DEFAULT_EVENT("Faild Event"),
        FTP_SocketDisconnected("Disconnect"),

        //wap event
        WAP_TYPE_LOGIN("WAP Logon"),
        WAP_TYPE_REFRESH("WAP Refresh"),
        WAP_TYPE_KjavaDown("KJava"),
        WAP_TYPE_PictureDown("Picture"),
        WAP_TYPE_RingDown("Ring"),
        WAP_DROP("Drop"),
        WAP_Ref_First_Request("WAP Get URL Request"),
        WAP_Ref_First_Success("WAP Get URL Success"),
        WAP_Ref_First_Failure("WAP Get URL Failure"),
        CONNECT_GATEWAY_BEGIN("Connect Gateway Request"),
        CONNECT_GATEWAY_SUCCESS("Connect Gateway Success"),
        CONNECT_GATEWAY_FAILED("Connect Gateway Failure"),
        SEND_REQUEST_BEGIN("Request"),
        SEND_REQUEST_SUCCESS("Request Success"),
        SEND_REQUEST_FAILED("Request Failure"),
        SEND_CUR_DATA("send cur data"),
        RECV_FIRST_DATA("Reply"),
        RECV_CUR_DATA("recv cur data"),
        RECV_LAST_DATA("Finish"),
        RECV_FAILED("recv failed"),
        RECV_DATA("recv data"),
        URL_REDIRECT("url redirect"),
        HYPERLINK_URL("hyperlink url"),
        IMG_URL("img url"),
        HTTP_REDIRECT("http redirect"),
        REDIRECT_END("redirect end"),
        WIFI_LOGIN_BEGIN("wifi login begin"),
        WIFI_LOGIN_SUCCESS("wifi login success"),
        WIFI_LOGIN_FAILED("wifi login failed"),
        WIFI_LOGOUT_BEGIN("wifi logout begin"),
        WIFI_LOGOUT_SUCCESS("wifi logout success"),
        WIFI_LOGOUT_FAILED("wifi logout failed"),

        //Ping Event
        PING_TYPE_START("Ping Start"),
        PING_TYPE_SUCCESS("Ping Success:Delay(%d ms)"),
        PING_TYPE_FAILURE("Ping Failure:Delay(%d ms)"),
        //PING_TYPE_HUNGUP("PPP Hangup")


        SMTP_TYPE_CONN("SMTP Connect"),
        SMTP_TYPE_SEND_MAIL_CMD("Email Upload Send Mail:%s(KByte)"),
        SMTP_TYPE_FIRST_DATA("Email Upload First Data"),
        SMTP_TYPE_LAST_DATA("Email Upload Last Data:%.3f(s),Mean Rate:%s (KB/S),transmit Size:%s KBytes"),
        SMTP_TYPE_DROP("Email Upload Drop:%.3f(s),Mean Rate:%s (KB/S),transmit Size:%s KBytes,%s"),
        SMTP_TYPE_EOM_ACK("SMTP Disconnect"),

        POP3_TYPE_CONN("POP3 Connect"),
        POP3_TYPE_SEND_RETR_CMD("Email Download Send RETR %s(KByte)"),
        POP3_TYPE_FIRST_DATA("Email Download First Data"),
        POP3_TYPE_LAST_DATA("Email Download Last Data:%.3f(s),Mean Rate:%s (KB/S),transmit Size:%s KBytes"),
        POP3_TYPE_DROP("Email Download Drop:%.3f(s),Mean Rate:%s (KB/S),transmit Size:%s KBytes,%s"),
        POP3_TYPE_FINISH("POP3 Disconnect"),

        //DNS LookUp事件
        DNS_LOOKUP_START("DNS Lookup Start"),
        DNS_LOOKUP_SUCCESS("DNS Lookup Success:%s,Delay %d(ms)"),
        DNS_LOOKUP_FAILURE("DNS Lookup Failure:Delay %d(ms),Reason:%s"),

        WLAN_DHCP_START("Wlan DHCP Start"),
        WLAN_DHCP_SUCCESS("Wlan DHCP Success"),
        WLAN_DHCP_FAILURE("Wlan DHCP Failure"),
        WLAN_DHCP_DISCONNECT("Wlan DHCP Disconnect"),

        //Speed Test
        SPEEDTEST_SockConnecting("Speedtest Connect Server"),
        SPEEDTEST_ConnectSockSucc("Speedtest Connect Server Success"),
        SPEEDTEST_ConnectSockFailed("Speedtest Connect Server Failure"),
        SPEEDTEST_Ping_Strart("Speedtest Ping Start"),
        SPEEDTEST_Ping_Suc("Speedtest Ping Success:Delay %d(ms)"),
        SPEEDTEST_DL_Strart("Speedtest Download Start"),
        SPEEDTEST_DL_Suc("Speedtest Download Success:Delay %.2f(s),Mean Rate:%.2f (KB/S),Transmit Size:%.2f KBytes"),
        SPEEDTEST_UL_Start("Speedtest Upload Start"),
        SPEEDTEST_UL_Suc("Speedtest Upload Success:Delay %.2f(s),Mean Rate:%.2f (KB/S),Transmit Size:%.2f KBytes"),
        SPEEDTEST_SocketDisconnected("Speedtest Disconnect Server"),
        SPEEDTEST_FAILED("Speedtest Failure"),
        SPEEDTEST_PING_FAILURE("Speedtest Ping Failure:%s"),
        SPEEDTEST_DOWNLOAD_FAILURE("Speedtest Download Failure:%s"),
        SPEEDTEST_UPLOAD_FAILURE("Speedtest Upload Failure:%s"),

        //VS
        STREAM_REQUEST("Video Stream Start"),
        STREAM_REQUEST_SUCCESS("Video Stream Request Success"),
        STREAM_REQUEST_FAILURE("Video Stream Request Failure"),
        RECEPTION_OF_FIRST_DATA_PACKET("Video Stream First Data"),
        STREAM_REPRODUCTION_START("Video Stream Reproduction Start"),
        STREAM_REPRODUCTION_END("Video Stream Reproduction End"),
        STREAM_END("Video Stream Finished"),
        STREAM_DROP("Video Stream Drop"),

        STREAMING_REQUEST("Streaming Request"),
        STREAMING_FIRST_DATA("Streaming First Data:Delay:%dms, File Duration:%ds, Video Width-Height:%d*%d, Video FPS:%d, Total BitRate:%.2fkbps"),//, WebSite Type:%s, Media Quality:%s"),
        STREAMING_REQUEST_FAILURE("Streaming Request Failure:%dms,Reason:%s"),
        STREAMING_REPRODUCTION_START("Streaming Reproduction Start:%dms"),
        STREAMING_REPRODUCTION_START_FAILURE("Streaming Reproduction Start Failure:%dms,Reason:%s"),
        STREAMING_REBUFFERING_START("Streaming Rebuffering Start"),
        STREAMING_REBUFFERING_END("Streaming Rebuffering End:%dms"),
        STREAMING_LAST_DATA("Streaming Last Data:Receive Size:%.2fKbytes,ReBuffer Count:%d, Rebuffer Time:%dms, VMOS:%.2f, A-V DeSync Rate:%d%%, LostVideoPacket Rate:%.2f%%, AvgVideoPacketGap:%dms, AvgVideoPacketJitter:%d"),
        STREAMING_DROP("Streaming Drop:Receive Size:%.2fKbytes,ReBuffer Count:%d, Rebuffer Time:%dms, VMOS:%.2f, A-V DeSync Rate:%d%%, LostVideoPacket Rate:%.2f%%, AvgVideoPacketGap:%dms, AvgVideoPacketJitter:%d, Reason:%s"),
        STREAMING_PLAY_FINISHED("Streaming Play Finished"),


        //HttpUpload
        HTTP_UP_START("HTTP Upload Start"),
        HTTP_UP_LOGIN_START("HTTP Upload Login Start"),
        HTTP_UP_LOGIN_SUCCESS("HTTP Upload Login Success:Delay(%d ms)"),
        HTTP_UP_LOGIN_FAILED("HTTP Upload Login Failure"),
        HTTP_UP_SEND_START("HTTP Upload Send Start"),
        HTTP_UP_FIRST_DATA("HTTP Upload First Data"),
        HTTP_UP_LAST_DATA("HTTP Upload Last Data:Delay(%ds),Mean Rate:%.2f(KB/S),transmit Size:%.2fKBytes"),
        HTTP_UP_FAILED("HTTP Upload Failure:%s"),
        HTTP_UP_DROP("HTTP Upload Drop:Delay(%ds),Mean Rate:%.2f(KB/S),transmit Size:%.2f KBytes,Reason:%s"),


        //Video Play
        VIDEO_PLAY_REQUEST("Video Play Request"),
        VIDEO_PLAY_FIRST_DATA("Video Play First Data: Delay:%dms"),
        VIDEO_PLAY_REQUEST_FAILURE("Video Play Request Failure:%dms,Reason:%s"),
        VIDEO_PLAY_REPRODUCTION_START("Video Play Reproduction Start: Delay:%dms"),
        VIDEO_PLAY_KPI_REPORT("Video Play Kpi Report: Delay:%dms,File Duration:%s,Video Width-Height:%d*%d,Video FPS:%d,Total BitRate:%.2fkbps,%s"),
        VIDEO_PLAY_REPRODUCTION_START_FAILURE("Video Play Reproduction Start Failure:%dms,Reason:%s"),
        VIDEO_PLAY_REBUFFERING_START("Video Play Rebuffering Start"),
        VIDEO_PLAY_REBUFFERING_END("Video Play Rebuffering End:%dms"),
        VIDEO_PLAY_LAST_DATA("Video Play Last Data: Receive Size:%.2fKbytes,ReBuffer Count:%d,Rebuffer Time:%dms,VMOS%.2f,A-V DeSync Rate:%d%%"),
        VIDEO_PLAY_DROP("Video Play Drop: Receive Size:%.2fKbytes,ReBuffer Count:%d,Rebuffer Time:%dms,VMOS:%.2f,A-V DeSync Rate:%d%%,Reason:%s"),
        VIDEO_PLAY_PLAY_FINISHED("Video Play Play Finished"),
        VIDEO_PLAY_SEGMENT_REPORT("Video Play Segment Report"),


        //FaceBook
        FACEBOOK_TEST_START("Facebook Test Start:UserName:%s,Password:%s,SendText:%s,PictureQuality:%d"),
        FACEBOOK_ACTION_START("Facebook Action Start:ActionType:%d,ContentText:%s,ContentSize:%d"),
        FACEBOOK_ACTION_SUCCESS("Facebook Action Success:ActionType:%d,Delay:%dms,UpBytes:%dbytes,DownBytes:%dbytes,ClickDelay:%dms"),
        FACEBOOK_ACTION_FAILURE("Facebook Action Failure:ActionType:%d,Delay:%dms,ReasonCode:%d,Description:%s"),
        FACEBOOK_TEST_SUCCESS("Facebook Test Success"),
        FACEBOOK_TEST_FAILURE("Facebook Test Failure:Code:%s,Reason:%s"),

        Route_DNS_Lookup_Start("DNS Lookup Start"),
        Route_DNS_Lookup_Success("DNS Lookup Success:IP:%s,%dms"),
        Route_DNS_Lookup_Failure("DNS Lookup Failure:%dms,Reason:%s"),
        Route_Start("Trace Route Start"),
        Route_PointReply("%d-Reply:%s, %dms"),
        Route_PointTimeOut("%d-Timeout:%s"),
        Route_Success("Trace Route Success:%d hops,Delay(%.2fms),%d hops Timeout,%d hops Unknown"),
        Route_Fail("Trace Route Failure"),
        Route_ReslvError("Trace Route Address Reslv Error");

        private final String event;

        private DataTaskEvent(String ev) {
            this.event = ev;
        }

        public String toString() {
            return event;
        }
    }

    /**
     * Trace口吐数据时结构体的命名
     *
     * @author tangwq
     */
    public static enum TraceStructType {
        //layer 3
        flashL3Msg,
        flashL3AndCallEvent,    //需要刷新L3及发送呼叫EVENT事件 暂用于统一解码twq20110814
        pdpDeactiveRequest,        //pdp测试的Deacive请求
        pdpDeactiveAccept,        //pdp测试的Deacive请求成功
        pdpActiveRequest,        //pdp测试的Active请求
        pdpActiveAccept,        //pdp测试的Active请求成功
        attachRequest,
        attachAccept,
        attachReject,
        attachComlete,
        detachRequest,
        detachAccept,
        Default
    }

    /**
     * 呼叫过程中通过信令产生的事件信息
     *
     * @author tangwq
     */
    public static enum CallEventMessage {
        CSFB_Originating_Call_Request,    //CSFB主叫起呼
        CSFB_Terminating_Call_Request,    //CSFB被叫起呼
        LTE_CSFB_RRC_Release,            //CSFB RRC释放
        LTE_to_WCDMA_Coverage,            //回落到WCDMA覆盖
        LTE_to_TDCDMA_Coverage,            //回落到TDSCDMA覆盖
        LTE_to_GSM_Coverage,            //回落到GSM覆盖
        CSFB_Return_to_LTE,                //返回LTE网络

        Outgoing_Call_Attempt,        //主叫发起 0x2C00 以空口层三消息“channel request和CM service request”两条信令为发起呼叫的开始，缺一不可
        Outgoing_Call_Alerting,        //主叫振铃 0x2C01 当收到"CC Alerting"消息，并且当前处于主叫状态，认为发生了主叫振铃事件。
        Outgoing_Call_Connected,    //主叫接通 0x2C02 收到"Connect"，或"CC Connect Acknowledge"等接通消息，认为主叫接通。
        Outgoing_Call_Failure,        //主叫失败 0x2C03 以空口层三消息中没有“Connect”或“Connect Acknowledge”信令中的任何一条
        Incoming_Call_Attempt,        //被叫响应 0x2C04 当收到RR Paging Response时发生了被叫响应
        Incoming_Call_Alerting,        //被叫振铃 0x2C05 当收到"CC Alerting"消息，并且当前处于被叫状态，认为发生了被叫振铃事件。
        Incoming_Call_Connected,    //被叫接通 0x2C06 收到"Connect"，或"CC Connect Acknowledge"等接通消息，认为被叫接通
        Incoming_Call_Failure,        //被叫失败 0x2C07 当发生被叫响应后，没有接通，就进入空闲状态，或者收到"Disconnect"或者"Release Complete"、"Connection Release"，" Connection Release Complete"、"Connection Release-CCCH"等信道释放消息，认为
        Call_Complete,                //呼叫结束 0x2C08 手机发起或收到“Disconnect”或“Channel Release”中的任何一条消息即为正常释放
        Call_Drop,
        Drop_Call,                    //掉话 	  0x2C09 当“Disconnect”或“Channel Release”两条消息都未出现而由专用模式转为空闲模式时，计为一次掉话（如通话时间不足规定时长，出现释放，要求通过层3信令解码判断原因）
        Out_Of_Service,             //手机脱网
        CallingPoorCov,                //弱覆盖			下面这三个面异常是在应用层中判断产生
        CallingFLT,                    //前向链路干扰
        CallingEdgeCov,                //边缘覆盖
        BadQuality,                    //连续质量差
        Data_Attempt,                //数据业务交互开始
        PhoneStateIdle,                //当前手机状态为Idle
        HandOff,                    //切换
        Default
    }

    /**
     * 服务器操作的类型
     */
    public static enum ServerOperateType {

        idle,
        //		connectFtp,				//连接FTP服务器
//		uploadPcapFile,			//上传pcap数据到Fleet平台
        uploadTestFile,            //上传测试数据
        uploadIndoorFile,        //上传室内测试数据
        uploadAutoTestFile,        //自动测试完成后上传测试数据
        uploadGPSOnce,            //一次上传GPS信息
        uploadGPSConstantly,    //连续上传GPS信息
        downAutoTest,            //自动测试下载测试计划
        downManual,                //任务列表中下载测试计划
        downManualForce,        //任务列表中下载测试计划，已经是最新时也强制更新
        syncTime,                //同步时间
        sendEvent,                //发送事件
        logout,
        stopUpload,            //停止指定文件上传
        uploadParamsReport //上传实时参数到平台
    }

    ;

    /**
     * 获得网络类型相关参数
     *
     * @author tangwq
     */
    public static enum NetType {
        Normal(0),
        WCDMA(1),
        GSM(2),
        EVDO(3),
        CDMA(4),
        TDSCDMA(5),
        LTE(6),
        LTETDD(7),
        WiFi(9),
        NBIoT(8),
        CatM(10),
        ENDC(11),
        ScannerWCDMA(21),
        ScannerCDMA(23),
        ScannerTDSCDMA(25),
        ScannerLTE(26);

        private final int type;

        private NetType(int ev) {
            this.type = ev;
        }

        public int getNetType() {
            return type;
        }

        public static NetType getNetTypeByID(int id) {
            NetType[] reasons = NetType.values();
            for (NetType s : reasons) {
                if (id == s.getNetType()) {
                    return s;
                }
            }
            return Normal;
        }
    }

    public static enum DocType {
        Default, //默认无类型
        FloorMap,
        FloorView,
        BuildView,
        RCU
    }

    /**
     * Umpc服务器登陆状态
     *
     * @author tangwq
     */
    public static enum UMPCConnectStatus {
        Default,            //无效状态

        //当前块的状态为旧WIFI模式所有,新模式都不用
        WifiConnecting,        //Wifi 连接中
        WifiConnected,        //Wifi 连接成功
        WifiConnectFaild,    //Wifi 连接失败
        UmpcConnecting,        //UMPC Socket连接中
        UmpcConnected,        //UMPC Socket连接成功
        UmpcLogined,        //UMPC 登陆成功
        UmpcLoginFaild,        //UMPC 登陆失败
        UmpcConnectFaild,    //UMPC Socket连接失败
        UmpcDisconnecting,    //UMPC Socket断开中
        UmpcDisconnected,    //UMPC Socket断开成功
        UmpcServiceClose,    //UMPC 服务关闭

        //新模式中用到的状态
        ServereCreating,    //服务端创建中
        ServerCreated,        //服务端创建成功
        TerminalConnected,    //IPAD端连接成功
        TerminalConnectFaild,//IPAD连接失败
        TerminalLoginSucces,//终端登陆成功
        TerminalLoginFaild    //终端登陆失败
    }

    /**
     * UMPC回传数据类型
     *
     * @author tangwq
     */
    public static enum UMPCEventType {
        Event('E'),            //事件
        Alarm('A'),            //告警
        Single('M'),        //信令
        RealTimeData('R'),    //实时参数
        NetGSM('G'),        //GSM参数信息
        NetTDSCDMA('T'),    //TD参数信息
        NetWCDMA('W'),        //WCDMA参数信息
        NetCDMA('C'),        //CDMA参数信息
        NetLTE('L'),        //LTE参数信息
        NetEVDO('V'),       //EVDO参数
        Scanner('S');       //SCANNER参数

        private final char type;

        private UMPCEventType(char c) {
            this.type = c;
        }

        public char getUMPCEvnetType() {
            return type;
        }
    }


    /**
     * 振铃类型
     *
     * @author tangwq
     */
    public static enum AlarmType {
        //信令类告警
        ALARMTYPE_NONE(0),
        ALARMTYPE_CALL_FIAL(1),        //连续呼叫失败 连续3次 连续触发 　
        ALARMTYPE_PPP_FIAL(2),            //连续拨号失败 连续3次 连续触发 　
        ALARMTYPE_LOGIN_FIAL(3),        //连续登陆失败 连续3次 连续触发 　
        ALARMTYPE_OUT_NET(4),            //脱网 连续10s脱网 10s
        //设备类告警
        ALARMTYPE_DEVICE_LOST(5),        //设备丢失 心跳包丢失 10s 是
        ALARMTYPE_POWER_LOW(0x1001),            //电量不足 低于20%电量 60s 　
        ALARMTYPE_POWER_ALMOSTNONE(0x1002),    //电量严重短缺// 低于10%电量 60s 是
        ALARMTYPE_STORAGE_RARE(0x1003),        //存储空间低 低于10M空间 60s 　
        ALARMTYPE_STORAGE_ALMOSTNONE(0x1004),//存储空间严重不足 低于5M空间 60s 是
        ALARMTYPE_MOBILE_OFFLINE(0x1005),             //手机离线
        ALARMTYPE_TEMPERATURE_HIGH(0x1006),        //手机温度过高
        ALARMTYPE_TRATTIC_EXCEED(0x1007);        //流量超标

        private final int type;

        private AlarmType(int alarmtype) {
            type = alarmtype;
        }

        private AlarmType() {
            type = 0;
        }

        public int getAlarmType() {
            return type;
        }
    }

    /**
     * 扫频仪连接状态
     */
    public static enum ScannerConnectStatus {
        Default,                //无效状态
        ScannerLogined,            //Scnaner 登陆成功
        ScannerDisConnect,        //Scanner 断开成功
        ScannerConnectFail,        //Scanner 连接失败
        ScannerStart            //Scanner 开始测试
    }


    public enum TestKind {
        tkDial(0),             //Dial
        tkAttach(1),           //Attach
        tkPDPActive(2),        //PDP
        tkPPP(3),              //pppDial
        tkPing(4),             //ping
        tkFTPDownload(5),      //ftpDownLoad
        tkFTPUpload(6),        //ftpUpLoad
        tkHTTP(7),             //HTTP Download
        tkHttpPage(8),         //HTTP Page
        tkEMailDownload(9),    //收邮件
        tkEMailUpload(10),     //发邮件
        tkTFTP(11),
        tkSMS(12),             //SMS
        tkMMS(13),             //MMS
        tkWAP(14),             //Wap Page
        tkWAPDL(15),           //WAP Download
        tkFetion(16),          //fection
        tkMEmail(17),
        tkPushMail(18),
        tkVideoCall(19),       //video
        tkVideoStream(20),     //VideoStreaming
        tkMosSelfTest(21),
        tkMosSelfCheck(22),
        tkMBMS(23),
        tkIdle(24), //Idle
        tkCMMB(25),
        tkWAPVS(26),
        tkVerifyPopedom(27),
        tkTraceRoute(28),
        tkDHCP(29),
        tkDialed(30),
        tkBSSearch(31),
        tkGPSOne(32),
        tkIperf(33),
        tkQChat(34),
        tkWebDisconnect(35),
        tkHiSiliconConnect(36),
        tkWLanAPCoverage(37),
        tkMultFtpDownload(38),
        tkMultFtpUpload(39),
        tkPower(40),
        tkVOIP(41),
        tkWeiBo(42),       //WeiBo
        tkDelayTest(43),
        tkHttpFix(44),
        tkPassivegpsOne(45),
        tkNumberFilter(46),
        tkHttpUp(47),
        tkSpeedTest(48),
        tkHttpVS(49),
        tkfacebook(50),
        tkPBM(51),
        tkWeChat(52),
        tkUDP(58),
        tkREBOOT(54),
        tkOpenSignal(57),
        tkMultiHttpDownload(55),
        tkWeCallMoc(59),
        tkWeCallMtc(60),
        tkSkypeChat(61),
        tkSinaWeibo(62),
        tkQQ(63),
        tkWhatsAppChat(64),
        tkWhatsAppMoc(65),
        tkWhatsAppMtc(66),
        tkFacebook2(67),
        tkInstagram2(68),
        tkIDT(69);

        private final int kind;

        private TestKind(int taskKind) {
            kind = taskKind;
        }

        public int getKind() {
            return kind;
        }

        public static TestKind getTestKind(int kind){
            for(TestKind testKind:values()){
                if(testKind.kind == kind)
                    return testKind;
            }
            return null;
        }
    }

    public static enum Alarm {
        NORMAL(0, "NORMAL"),
        DEVICE_STOP_TEST(1, "Stop Test"),
        DEVICE_INTERRUPT_TEST(1, "Interrupt Test"),
        DEVICE_POWER_LOW(1, "Low Power"),
        DEVICE_SIMCARD(1, "No Sim Card"),
        DEVICE_TEMP_HIGH(1, "Device temperature is too high"),
        DEVICE_STORGE_LOW(1, "Storage Space is little"),
        DEVICE_STORGE_INVALID(1, "Storge is invalid"),
        DEVICE_GPS_UNAVAILABLE(1, "GPS Unavailable"),
        DEVICE_LOST(1, "Device Lost"),
        MO_MOS_POWER_LOW(1, "MO MicroMOS Low Power"),
        MT_MOS_POWER_LOW(1, "MT MicroMOS Low Power"),
        //-----Handover是指业务状态(数据或语音)下进行2G、3G网间切换-----------------------------------
        NET_GSM_HANDOVER_SUCCESS(2, DataSetEvent.ET_GSM_Hard_Handover_Success, "GSM Handover Success", R.drawable.handover_success),
        NET_GSM_HANDOVER_FAIL(2, DataSetEvent.ET_GSM_Hard_Handover_Failure, "GSM Handover Failure", R.drawable.handover_fail),
        NET_WCDMA_HARD_HANDOVER_SUCCESS(2, DataSetEvent.ET_WCDMA_Hard_Handover_Success, "WCDMA Hard Handover Success", R.drawable.handover_success),
        NET_WCDMA_HARD_HANDOVER_FAIL(2, DataSetEvent.ET_WCDMA_Hard_Handover_Failure, "WCDMA Hard Handover Failure", R.drawable.handover_fail),
        NET_WCDMA_SOFT_HANDOVER_SUCCESS(2, DataSetEvent.ET_WCDMA_Soft_Handover_Success, "WCDMA Soft Handover Success", R.drawable.handover_success),
        NET_WCDMA_SOFT_HANDOVER_FAIL(2, DataSetEvent.ET_WCDMA_Soft_Handover_Failure, "WCDMA Soft Handover Failure", R.drawable.handover_fail),
        NET_TD_HARD_HANDOVER_SUCCESS(2, DataSetEvent.ET_TD_Hard_Handover_Success, "TD-SCDMA Hard Handover Success", "TDSCDMA Hard Handover Success", R.drawable.handover_success),
        NET_TD_HARD_HANDOVER_FAIL(2, DataSetEvent.ET_TD_Hard_Handover_Failure, "TD-SCDMA Hard Handover Failure", "TDSCDMA Hard Handover Failure", R.drawable.handover_fail),
        NET_LTE_HANDOVER_SUCCESS(2, DataSetEvent.ET_LTEHandoverSuccess, "LTE Handover Success", R.drawable.handover_success),
        NET_LTE_HANDOVER_FAIL(2, DataSetEvent.ET_LTEHandoverFailure, "LTE Hard Handover Failure", R.drawable.handover_fail),
        //通话中切网
        NET_W2G_CS_HANDOVER_SUCCESS(2, DataSetEvent.ET_Handover_From_WCDMA_Success, "WCDMA->GSM CS Handover Success", "WCDMA to GSM CS Handover Success", R.drawable.umts2gsm_success),
        NET_W2G_CS_HANDOVER_FAIL(2, DataSetEvent.ET_Handover_From_WCDMA_Failure, "WCDMA->GSM CS Handover Failure", "WCDMA to GSM CS Handover Failure", R.drawable.umts2gsm_fail),
        NET_G2W_CS_HANDOVER_SUCCESS(2, DataSetEvent.ET_Handover_To_WCDMA_Success, "GSM->WCDMA CS Handover Success", "GSM to WCDMA CS Handover Success", R.drawable.gsm2umts_success),
        NET_G2W_CS_HANDOVER_FAIL(2, DataSetEvent.ET_Handover_To_WCDMA_Failure, "GSM->WCDMA CS Handover Failure", "GSM to WCDMA CS Handover Failure", R.drawable.gsm2umts_fail),
        NET_T2G_CS_HANDOVER_SUCCESS(2, DataSetEvent.ET_Handover_From_TD_Success, "TD-SCDMA->GSM CS Handover Success", "TD to GSM CS Handover Success", R.drawable.umts2gsm_success),
        NET_T2G_CS_HANDOVER_FAIL(2, DataSetEvent.ET_Handover_From_TD_Failure, "TD-SCDMA->GSM CS Handover Failure", "TD to GSM CS Handover Failure", R.drawable.umts2gsm_fail),
        NET_G2T_CS_HANDOVER_SUCCESS(2, DataSetEvent.ET_Handover_To_TD_Success, "GSM->TD-SCDMA CS Handover Success", "GSM to TD CS Handover Success", R.drawable.gsm2umts_success),
        NET_G2T_CS_HANDOVER_FAIL(2, DataSetEvent.ET_Handover_To_TD_Failure, "GSM->TD-SCDMA CS Handover Failure", "GSM to TD CS Handover Failure", R.drawable.gsm2umts_fail),
        //数据中切网
        NET_W2G_PS_HANDOVER_SUCCESS(2, DataSetEvent.ET_PS_Handover_From_WCDMA_Success, "WCDMA->GSM PS Handover Success", "WCDMA to GSM PS Handover Success", R.drawable.umts2gsm_success),
        NET_W2G_PS_HANDOVER_FAIL(2, DataSetEvent.ET_PS_Handover_From_WCDMA_Failure, "WCDMA->GSM PS Handover Failure", "WCDMA to GSM PS Handover Failure", R.drawable.umts2gsm_fail),
        NET_G2W_PS_HANDOVER_SUCCESS(2, DataSetEvent.ET_PS_Handover_To_WCDMA_Success, "GSM->WCDMA PS Handover Success", "GSM to WCDMA PS Handover Success", R.drawable.gsm2umts_success),
        NET_G2W_PS_HANDOVER_FAIL(2, DataSetEvent.ET_PS_Handover_To_WCDMA_Failure, "GSM->WCDMA PS Handover Failure", "GSM to WCDMA PS Handover Failure", R.drawable.gsm2umts_fail),
        NET_T2G_PS_HANDOVER_SUCCESS(2, DataSetEvent.ET_PS_Handover_From_TD_Success, "TD-SCDMA->GSM PS Handover Success", "TDSCDMA to GSM PS Handover Success", R.drawable.umts2gsm_success),
        NET_T2G_PS_HANDOVER_FAIL(2, DataSetEvent.ET_PS_Handover_From_TD_Failure, "TD-SCDMA->GSM PS Handover Failure", "TDSCDMA to GSM PS Handover Failure", R.drawable.umts2gsm_fail),
        NET_G2T_PS_HANDOVER_SUCCESS(2, DataSetEvent.ET_PS_Handover_To_TD_Success, "GSM->TD-SCDMA PS Handover Success", "GSM to TDSCDMA PS Handover Success", R.drawable.gsm2umts_success),
        NET_G2T_PS_HANDOVER_FAIL(2, DataSetEvent.ET_PS_Handover_To_TD_Failure, "GSM->TD-SCDMA PS Handover Failure", "GSM to TDSCDMA PS Handover Failure", R.drawable.gsm2umts_fail),
        NET_L2W_HANDOVER_SUCCESS(2, DataSetEvent.ET_LTE_Handover_To_WCDMA_Success, "LTE->WCDMA Handover Success", "LTE to WCDMA Handover Success", R.drawable.handover_success),
        NET_L2W_HANDOVER_FAIL(2, DataSetEvent.ET_LTE_Handover_To_WCDMA_Failure, "LTE->WCDMA Handover Failure", "LTE to WCDMA Handover Failure", R.drawable.handover_fail),
        //---------Cell Reselection 小区重选是指在Idle状态下(无语音无数据)发生的小区切换---------
        NET_GSM_CELL_RESELECTION(2, DataSetEvent.ET_Intra_GSM_CellReselect, "GSM Cell Reselection", R.drawable.cell_reselection),
        NET_WCDMA_CELL_RESELECTION(2, DataSetEvent.ET_Intra_WCDMA_CellReselect, "WCDMA Cell Reselection", R.drawable.cell_reselection),
        NET_TD_CELL_RESELECTION(2, DataSetEvent.ET_Intra_TD_CellReselect, "TD-SCDMA Cell Reselection", R.drawable.cell_reselection),
        NET_LTE_CELL_RESELECTION(2, DataSetEvent.ET_Intra_TDDLTE_CellReselect, "LTE Cell Reselection", R.drawable.cell_reselection),
        NET_G2W_CELL_RESELECTION(2, DataSetEvent.ET_Inter_CellReselect_GSM_To_WCDMA_Complete, "GSM->WCDMA Cell Reselection", "GSM to WCDMA Cell Reselection", R.drawable.cell_reselection),
        NET_W2G_CELL_RESELECTION(2, DataSetEvent.ET_Inter_CellReselect_WCDMA_To_GSM_Complete, "WCDMA->GSM Cell Reselection", "WCDMA to GSM Cell Reselection", R.drawable.cell_reselection),
        NET_G2T_CELL_RESELECTION(2, DataSetEvent.ET_Inter_CellReselect_GSM_To_TD_Complete, "GSM->TD-SCDMA Cell Reselection", "GSM to TDSCDMA Cell Reselection", R.drawable.cell_reselection),
        NET_T2G_CELL_RESELECTION(2, DataSetEvent.ET_Inter_CellReselect_TD_To_GSM_Complete, "TD-SCDMA->GSM Cell Reselection", "TDSCDMA to GSM Cell Reselection", R.drawable.cell_reselection),
        NET_L2W_CELL_RESELECTION(2, DataSetEvent.ET_Inter_CellReselect_LTE_To_WCDMA_Complete, "LTE->WCDMA Cell Reselection", "LTE to WCDMA Cell Reselection", R.drawable.cell_reselection),
        NET_W2L_CELL_RESELECTION(2, DataSetEvent.ET_Inter_CellReselect_WCDMA_To_LTE_Complete, "WCDMA->LTE Cell Reselection", "WCDMA to LTE Cell Reselection", R.drawable.cell_reselection),
        NET_L2T_CELL_RESELECTION(2, DataSetEvent.ET_Inter_CellReselect_LTE_To_TD_Complete, "LTE->TD-SCDMA Cell Reselection", "LTE to TDSCDMA Cell Reselection", R.drawable.cell_reselection),
        NET_T2L_CELL_RESELECTION(2, DataSetEvent.ET_Inter_CellReselect_TD_To_LTE_Complete, "TD-SCDMA->LTE Cell Reselection", "TDSCDMA to LTE Cell Reselection", R.drawable.cell_reselection),
        //TODO 以下两个暂时未能在数据集中找到
        NET_L2G_CELL_RESELECTION(2, "LTE->GSM Cell Reselection", "LTE to GSM Cell Reselection", R.drawable.cell_reselection),
        NET_G2L_CELL_RESELECTION(2, "GSM->LTE Cell Reselection", "GSM to LTE Cell Reselection", R.drawable.cell_reselection),
        NET_OUT_OF_SERVICE(2, "Out of Service", R.drawable.out_of_service),
        /*业务告警*/
        TEST_OUTGOING_CALL_ATTEMPT(3, DataSetEvent.ET_MO_Attempt, "Outgoing Call Attempt", R.drawable.outgoing_call_attempt),
        TEST_OUTGOING_CALL_SETUP(3, DataSetEvent.ET_MO_Connect, "Outgoing Call Setup", R.drawable.call_setup),
        TEST_OUTGOING_BLOCKED_CALL(3, DataSetEvent.ET_MO_Block, "Outgoing Blocked Call", R.drawable.outgoing_blocked_call),
        TEST_OUTGOING_DROPPED_CALL(3, DataSetEvent.ET_MO_Drop, "Outgoing Dropped Call", R.drawable.outgoing_dropped_call),
        TEST_INCOMING_CALL_ATTEMPT(3, DataSetEvent.ET_MT_Attempt, "Incoming Call Attempt", R.drawable.incoming_call_attempt),
        TEST_INCOMING_CALL_SETUP(3, DataSetEvent.ET_MT_Connect, "Incoming Call Setup", R.drawable.call_setup),
        TEST_INCOMING_BLOCKED_CALL(3, DataSetEvent.ET_MT_Block, "Incoming Blocked Call", R.drawable.incoming_blocked_call),
        TEST_INCOMING_DROPPED_CALL(3, DataSetEvent.ET_MT_Drop, "Incoming Dropped Call", R.drawable.incoming_dropped_call),
        TEST_FTP_DOWNLOAD_DROP(3, RcuEventCommand.FTP_DL_Drop, "FTP Download Drop", R.drawable.ftp_dl_drop),
        TEST_FTP_DOWNLOAD_LASTDATA(3, RcuEventCommand.FTP_DL_LastData, "FTP Download LastData", R.drawable.ftp_dl_lastdata),
        TEST_FTP_UPLOAD_DROP(3, RcuEventCommand.FTP_UL_Drop, "FTP Upload Drop", R.drawable.ftp_ul_drop),
        TEST_FTP_UPLOAD_LASTDATA(3, RcuEventCommand.FTP_UL_LastData, "FTP Upload Last Data", R.drawable.ftp_ul_lastdata),
        TEST_PING_FAIL(3, RcuEventCommand.Ping_Failure, "Ping Failure", R.drawable.ping_fail),
        TEST_DNS_LOOKUP_FAIL(3, RcuEventCommand.DNS_LOOKUP_FAILURE, "DNS Lookup Failure", R.drawable.dns_fail),
        TEST_SPEEDTEST_FAIL(3, RcuEventCommand.SPEEDTEST_FAIL, "Speed Test Failure", R.drawable.speedtest_fail),
        TEST_HTTP_PAGE_Drop(3, DataSetEvent.ET_HttpPageDrop, "Http Page Drop", R.drawable.http_page_fail),
        TEST_HTTP_UPLOAD_DROP(3, RcuEventCommand.HTTP_UP_DROP, "Http Upload Drop", R.drawable.http_ul_drop),
        TEST_HTTP_DOWNLOAD_DROP(3, RcuEventCommand.HTTP_Down_Drop, "Http Download Drop", R.drawable.http_dl_drop),
        TEST_EMAIL_UPLOAD_DROP(3, RcuEventCommand.EMAIL_SMTP_DROP, "Email Upload Drop", R.drawable.email_ul_drop),
        TEST_EMAIL_DOWNLOAD_DROP(3, RcuEventCommand.EMAIL_POP3_DROP, "Email Download Drop", R.drawable.email_dl_drop),
        TEST_WAP_DROP(3, DataSetEvent.ET_WAP_Drop, "Wap Test Drop", R.drawable.wap_page_drop),
        TEST_WAP_DOWNLOAD_DROP(3, RcuEventCommand.WAPDL_Drop, "Wap Download Drop", R.drawable.wap_dl_drop),
        TEST_VIDEO_STREAMING_DROP(3, RcuEventCommand.Streaming_Drop, "Video Streaming Drop", R.drawable.video_drop),
        TEST_VIDEO_PLAY_DROP(3, RcuEventCommand.VIDEOPLAY_DROP, "Video Play Drop", R.drawable.video_drop),
        TEST_SMS_SEND_FAIL(3, DataSetEvent.ET_SMS_Send_Failure, "SMS Send Failure", R.drawable.sms_send_fail),
        TEST_SMS_RECEIVE_FAIL(3, DataSetEvent.ET_SMS_Recv_Failure, "SMS Receive Failure", R.drawable.sms_receive_fail),
        TEST_MMS_SEND_FAIL(3, DataSetEvent.ET_MMS_Send_Failure, "MMS Send Failure", R.drawable.mms_send_fail),
        TEST_MMS_RECEIVE_FAIL(3, DataSetEvent.ET_MMS_Recv_Failure, "MMS Receive Failure", R.drawable.mms_recv_fail),
        TEST_PBM_FAIL(3, DataSetEvent.ET_PBM_Failure, "PBM Failure", R.drawable.pbm_receive_fail),

        //2013.9.25 这些事件暂时屏蔽，到时处理为自定义事件
        //TEST_FTP_DOWNLOAD_THROUGHPUT_LOW(3,"FTP Download Throughput is Low",R.drawable.ftp_dl_low),
        //TEST_FTP_UPLOAD_THROUGHPUT_LOW(3,"FTP Upload Throughput is Low",R.drawable.ftp_ul_low),
        //NET_LOW_COVERAGE(2,"Low Coverage",R.drawable.low_coverage),///////
        //NET_POOR_QUALITY(2,"Poor Quality",R.drawable.poor_quality),
        CUSTOM_EVENT(4),
        FILTER_EVENT(5);
        private final int type;    //告警类型:设备、网络、测试
        private final String msg;    //告警显示的名称
        private final String ttx;    //声音告警的发音字符串
        private final int rcuId;    //业务告警事件的rcuId,语音业务 除外大部分有FLAG对应的事件可以使用
        private final int drawable;    //图标
        public final static int TYPE_DEVICE = 1;
        public final static int TYPE_NETWORK = 2;
        public final static int TYPE_TEST = 3;
        public final static int TYPE_CUSTOM = 4;
        public final static int TYPE_FILTER_EVENT = 5;
        public final static int NULL_RCUID = -999;

        /**
         * @param type 类型
         */
        private Alarm(int type) {
            this.type = type;
            this.msg = "";
            this.ttx = "";
            this.drawable = R.drawable.empty;
            this.rcuId = NULL_RCUID;
        }

        /**
         * @param type 类型
         * @param msg  信息
         */
        private Alarm(int type, String msg) {
            this.type = type;
            this.msg = msg;
            this.ttx = msg;
            this.drawable = R.drawable.empty;
            this.rcuId = NULL_RCUID;
        }

        /**
         * @param type     类型
         * @param msg      信息
         * @param drawable 图标
         */
        private Alarm(int type, String msg, int drawable) {
            this.type = type;
            this.msg = msg;
            this.ttx = msg;
            this.drawable = drawable;
            this.rcuId = NULL_RCUID;
        }

        /**
         * @param type     类型
         * @param msg      信息
         * @param ttx      语音
         * @param drawable 图标
         */
        private Alarm(int type, String msg, String ttx, int drawable) {
            this.type = type;
            this.msg = msg;
            this.ttx = ttx;
            this.drawable = drawable;
            this.rcuId = NULL_RCUID;
        }

        /**
         * @param type     类型
         * @param rcuId    RCU事件ID
         * @param msg      信息
         * @param ttx      语音
         * @param drawable 图标
         */
        private Alarm(int type, int rcuId, String msg, String ttx, int drawable) {
            this.type = type;
            this.rcuId = rcuId;
            this.msg = msg;
            this.ttx = ttx;
            this.drawable = drawable;
        }

        /**
         * @param type     类型
         * @param rcuId    RCU事件ID
         * @param msg      信息
         * @param drawable 图标
         */
        private Alarm(int type, int rcuId, String msg, int drawable) {
            this.type = type;
            this.msg = msg;
            this.ttx = msg;
            this.drawable = drawable;
            this.rcuId = rcuId;
        }

        public int getRcuId() {
            return rcuId;
        }

        /**
         * @return 告警显示内容
         */
        public String getMsg() {
            int netType = Deviceinfo.getInstance().getNettype();
            String str = msg;

            //2013.8.28 暂时先把WCDMA字眼替换为TD-SCDMA，集成数据集后告警判断流程得修改
            if (netType == Deviceinfo.NETTYPE_TD || netType == Deviceinfo.NETTYPE_TDD_LTE) {
                if (str.contains("WCDMA")) {
                    str = str.replace("WCDMA", "TD-SCDMA");
                }
            }
            return str;
        }

        /**
         * @return 告警声的发音
         */
        public String getTtx() {
            return ttx;
        }


        public int getDrawable() {
            return drawable > 0 ? drawable : R.drawable.empty;
        }

        /**
         * @return 告警的类型：设备、网络、测试
         */
        public int getType() {
            return type;
        }

        /**
         * @return 告警的描述名，设备告警显示的取自R.array.sys_alarm
         */
        public String getDescription(Context context) {
            switch (this) {
                case DEVICE_POWER_LOW:
                    return context.getResources().getStringArray(R.array.sys_alarm)[0];
                case DEVICE_TEMP_HIGH:
                    return context.getResources().getStringArray(R.array.sys_alarm)[1];
                case DEVICE_STORGE_LOW:
                    return context.getResources().getStringArray(R.array.sys_alarm)[2];
                case DEVICE_STORGE_INVALID:
                    return context.getResources().getStringArray(R.array.sys_alarm)[3];
                case DEVICE_GPS_UNAVAILABLE:
                    return context.getResources().getStringArray(R.array.sys_alarm)[4];
                case DEVICE_LOST:
                    return context.getResources().getStringArray(R.array.sys_alarm)[5];
                case DEVICE_STOP_TEST:
                    return context.getResources().getStringArray(R.array.sys_alarm)[6];
                case DEVICE_INTERRUPT_TEST:
                    return context.getResources().getStringArray(R.array.sys_alarm)[7];
                case DEVICE_SIMCARD:
                    return context.getResources().getStringArray(R.array.sys_alarm)[8];
                case MO_MOS_POWER_LOW:
                    return context.getResources().getStringArray(R.array.sys_alarm)[9];
                case MT_MOS_POWER_LOW:
                    return context.getResources().getStringArray(R.array.sys_alarm)[10];
                default:
                    return this.getMsg();
            }
        }

        /**
         * @return 所有设备告警
         */
        public static ArrayList<Alarm> getDeviceAlarms() {
            ArrayList<Alarm> result = new ArrayList<Alarm>();
            for (int i = 0; i < values().length; i++) {
                if (values()[i].type == 1) {
                    result.add(values()[i]);
                }
            }
            return result;
        }

        /**
         * @return 所有网络告警
         */
        public static ArrayList<Alarm> getNetworkAlarms() {
            ArrayList<Alarm> result = new ArrayList<Alarm>();
            for (int i = 0; i < values().length; i++) {
                if (values()[i].type == 2) {
                    result.add(values()[i]);
                }
            }
            return result;
        }

        /**
         * @return 所有业务告警
         */
        public static ArrayList<Alarm> getTestAlarms() {
            ArrayList<Alarm> result = new ArrayList<Alarm>();
            for (int i = 0; i < values().length; i++) {
                if (values()[i].type == 3) {
                    result.add(values()[i]);
                }
            }
            return result;
        }

        /**
         * @param name
         * @return 是否包含了指定名字name的类型
         */
        public static boolean contains(String name) {
            for (int i = 0; i < values().length; i++) {
                if (values()[i].name().equals(name)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 根据RCU事件ID判断该事件是否属于告警
         *
         * @param eventFlag
         * @return
         */
        public static boolean isAlarm(int eventFlag) {
            for (int i = 0; i < values().length; i++) {
                if (values()[i].rcuId == eventFlag) {
                    return true;
                }
            }

            return false;
        }

        /**
         * 根据RCU事件ID判断该事件是否属于告警
         *
         * @param eventFlag
         * @return
         */
        public static Alarm getAlarm(int eventFlag) {
            for (int i = 0; i < values().length; i++) {
                if (values()[i].rcuId == eventFlag) {
                    return values()[i];
                }
            }

            return null;
        }
    }

    /**
     * Scanner测试网络类型，0: GSM850, 1: GSM900, 2: GSM1800, 3: GSM1900, 4: UMTS850,
     * 5: UMTS900, 6: UMTS1900, 7: UMTS2110, 8: CDMA800, 9: CDMA1900, 10:
     * EVDO800, 11: TDSCDMA2010, 12: LTE   10633、10688、10713
     */
    public static enum ScannerNetworkType {
        GSM850(0),
        GSM900(1),
        GSM1800(2),
        GSM1900(3),
        UMTS850(4),
        UMTS900(5),
        UMTS1900(6),
        UMTS2110(7),
        CDMA800(8),
        CDMA1900(9),
        EVDO800(10),
        TDSCDMA2010(11),
        LTE(12);

        private final int networkType;

        private ScannerNetworkType(int networkType) {
            this.networkType = networkType;
        }

        public int getNetworkType() {
            return networkType;
        }
    }

    public static enum NetworkTypeShow {
        NETWORK_TYPE_GPRS(TelephonyManager.NETWORK_TYPE_GPRS, "GSM", "G"),
        NETWORK_TYPE_EDGE(TelephonyManager.NETWORK_TYPE_EDGE, "GSM", "G"),
        NETWORK_TYPE_UMTS(TelephonyManager.NETWORK_TYPE_UMTS, "UMTS", "W"),
        NETWORK_TYPE_CDMA(TelephonyManager.NETWORK_TYPE_CDMA, "CDMA", "C"),
        NETWORK_TYPE_EVDO_0(TelephonyManager.NETWORK_TYPE_EVDO_0, "EVDO", "V"),
        NETWORK_TYPE_EVDO_A(TelephonyManager.NETWORK_TYPE_EVDO_A, "EVDO", "V"),
        NETWORK_TYPE_1xRTT(TelephonyManager.NETWORK_TYPE_1xRTT, "CDMA", "C"),
        NETWORK_TYPE_HSDPA(TelephonyManager.NETWORK_TYPE_HSDPA, "UMTS", "W"),
        NETWORK_TYPE_HSUPA(TelephonyManager.NETWORK_TYPE_HSUPA, "UMTS", "W"),
        NETWORK_TYPE_HSPA(TelephonyManager.NETWORK_TYPE_HSPA, "UMTS", "W"),
        NETWORK_TYPE_IDEN(TelephonyManager.NETWORK_TYPE_IDEN, "UMTS", "W"),
        NETWORK_TYPE_EVDO_B(TelephonyManager.NETWORK_TYPE_EVDO_B, "EVDO", "V"),
        NETWORK_TYPE_LTE(TelephonyManager.NETWORK_TYPE_LTE, "LTE", "L"),
        NETWORK_TYPE_EHRPD(TelephonyManager.NETWORK_TYPE_EHRPD, "EHRPD", "W"),
        NETWORK_TYPE_HSPAP(TelephonyManager.NETWORK_TYPE_HSPAP, "HSPAP", "W"),
        NETWORK_TYPE_TDA(16, "TDSCDAM", "T"),
        NETWORK_TYPE_TDB(17, "TDSCDAM", "T"),
        NETWORK_UNKNOWN(-1, "UNKNOWN", "U");


        private final int netId;
        private final String netWorkName;
        private final String netForIpadTag;

        private NetworkTypeShow(int netid, String networkname, String netforipadname) {
            this.netId = netid;
            this.netWorkName = networkname;
            this.netForIpadTag = netforipadname;
        }

        /**
         * 获得网络类型ID
         */
        public int getNetWorkId() {
            return this.netId;
        }

        /**
         * 获得得当前网络名称
         */
        public String getNetWorkName() {
            return this.netWorkName;
        }

        /**
         * 获得当前网络往IPDA端发送的标签信息
         */
        public String getNetForIpadTag() {
            return this.netForIpadTag;
        }

        /**
         * 通过网络ID获得对应的网络对象
         */
        public static NetworkTypeShow getNetWorkByNetID(int netid) {
            NetworkTypeShow[] networks = NetworkTypeShow.values();
            for (NetworkTypeShow network : networks) {
                if (network.getNetWorkId() == netid) {
                    return network;
                }
            }
            return NETWORK_UNKNOWN;
        }
    }


    /*
     * 并发按状态的业务事件ID对应枚举类
	 */
    public static enum RabByStateServiceId {
        MOC(TaskType.InitiativeCall.name(), 0x30170, 0x30172, 0x30173),
        MTC(TaskType.PassivityCall.name(), 0x30180, 0x30182, 0x30183),
        FTPUpload(TaskType.FTPUpload.name(), 0x0090, 0x0092, 0x0093),
        FTPDownload(TaskType.FTPDownload.name(), 0x0080, 0x0082, 0x0083),
        VideoPlay(TaskType.HTTPVS.name(), 0x1010, 0x1011, 0x1015),
        SMSSend(TaskType.SMSSend.name(), 0x00D0, 0x10003),
        HTTPPage(TaskType.Http.name(), 0x0F0C, 0x0F03, 0x0F04),
        HTTPUpload(TaskType.HttpUpload.name(), 0x1000, 0x1005, 0x1007),
        HTTPDown(TaskType.HttpDownload.name(), 0x00AB, 0x00A2, 0x00A3),
        EmailSend(TaskType.EmailSmtp.name(), 0x00C7, 0x00C2, 0x00C3);

        private String taskType;        //测试任务类型
        private int[] serviceID;        //任务事件ID对应表

        private RabByStateServiceId(String taskType, int... serviceID) {
            this.taskType = taskType;
            this.serviceID = serviceID;
        }

        public String getTaskType() {
            return taskType;
        }

        public int[] getServiceID() {
            return serviceID;
        }

        /**
         * 根据保存ID获取下标
         *
         * @param taskType
         * @param serviceID
         * @return
         */
        public static int getIndexByServiceEventID(String taskType, int serviceID) {
            RabByStateServiceId[] rabByStateServiceIds = RabByStateServiceId.values();
            int index = 0;
            for (RabByStateServiceId rabByStateServiceId : rabByStateServiceIds) {
                if (rabByStateServiceId.getTaskType().equals(taskType)) {
                    for (int i = 0; i < rabByStateServiceId.getServiceID().length; i++) {
                        System.out.println("----" + rabByStateServiceId.getServiceID()[i]);
                        if (rabByStateServiceId.getServiceID()[i] == serviceID) {
                            index = i;
                        }
                    }
                    break;
                }
            }
            return index;
        }


        /**
         * 根据业务以及下标 获取对应id
         *
         * @param taskType
         * @param index
         * @return
         */
        public static int getEventIDByServiceIndex(String taskType, int index) {
            RabByStateServiceId[] rabByStateServiceIds = RabByStateServiceId.values();
            int eventID = 0;
            for (RabByStateServiceId rabByStateServiceId : rabByStateServiceIds) {
                if (rabByStateServiceId.getTaskType().equals(taskType)) {
                    try {
                        eventID = rabByStateServiceId.getServiceID()[index];
                    } catch (IndexOutOfBoundsException e) {
                        eventID = rabByStateServiceId.getServiceID()[index - 1];
                    }
                    break;
                }
            }
            return eventID;
        }


        /**
         * 根据taskType获取业务下标
         */
        public static int getTaskIndexByService(String taskType) {
            RabByStateServiceId[] rabByStateServiceIds = RabByStateServiceId.values();
            int i = 0;
            for (RabByStateServiceId rabByStateServiceId : rabByStateServiceIds) {
                if (rabByStateServiceId.getTaskType().equals(taskType)) {
                    return i;
                }
                i++;
            }
            return 0;
        }


        /**
         * 取任务名数组
         */
        public static String[] getRabTaskName() {
            RabByStateServiceId[] rabByStateServiceIds = RabByStateServiceId.values();
            String[] rabTaskArray = new String[rabByStateServiceIds.length];
            for (int i = 0; i < rabByStateServiceIds.length; i++) {
                rabTaskArray[i] = rabByStateServiceIds[i].toString();
            }
            return rabTaskArray;
        }

        /**
         * 根据业务取参考业务ID对应中文名
         */
        public static String[] getRabTaskNameByID(String taskName, Context context) {
            String[] rabTaskArray = new String[RabByStateServiceId.valueOf(taskName).getServiceID().length];
            for (int i = 0; i < RabByStateServiceId.valueOf(taskName).getServiceID().length; i++) {
                rabTaskArray[i] = context.getResources().getStringArray(R.array.taskrab_byTask_array)[i];
            }
            return rabTaskArray;
        }

    }

}
