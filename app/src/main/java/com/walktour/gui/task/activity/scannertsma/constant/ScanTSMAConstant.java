package com.walktour.gui.task.activity.scannertsma.constant;

/**
 * @author jinfeng.xie
 * @data 2019/6/3
 */
public class ScanTSMAConstant {

    //protocol
    public static class ProtocolCodes {
        public final static int	PROTOCOL_GSM	=	0x0001;
        public final static int	PROTOCOL_IS_95_CDMA	=	0x0003;
        public final static int	PROTOCOL_3GPP_WCDMA	=	0x0004;
        public final static int	PROTOCOL_IS_2000_CDMA=	0x0005;
        public final static int	PROTOCOL_IS_856_EVDO=	0x0006;
        public final static int	PROTOCOL_TDSCDMA	=	0x0008;
        public final static int	PROTOCOL_LTE	=	0x000A;
        public final static int	PROTOCOL_TD_LTE	=	0x000B;
        public final static int	PROTOCOL_WiFi	=	0x000C;
    }
    public static class Spinner{
        public static String[] spScanType=new String[]{"TopN","User list"};
        public static String[] spRssiAntennaPorts=new String[]{"RF1","RF2"};
        public static String[] spWbAntennaPorts=new String[]{"RF1","RF2","RF1&RF2"};
        public static String[] spRssiMode=new String[]{"NORMAL","TDD_UL","TDD_DL","DL_ALL_TS","TDD_GP"};
        public static String[] spWbTestType=new String[]{"Not Test","WB","WB+SubBand"};
        public static String[] spWbShowType=new String[]{"Max Power Cell","All Cell"};
        public static String[] spMimoType=new String[]{"Not Test","2*2"};
        public static String[] spChannelZhenType=new String[]{"FDD","TDD"};
        public static String[] spChannelBandwidth=new String[]{"Auto","1.4MHz","3MHz","5MHz","10MHz","15MHz","20MHz"};
        public static String[] spNbAntennaPorts=new String[]{"RF1","RF2"};
        public static String[] spNbOutputType=new String[]{"Real Time","Buffer"};
        public static String[] spL3msgAntennaPorts=new String[]{"RF1","RF2"};

        public static String[] SP_SPECTURM_DETECTOR_TYPE =new String[]{"Peak","RMS","MinPeak","Avg"};
        public static String[] SP_SPECTURM_BLIND_REQELEMENT =new String[]{"Bank 1", "Bank 2", "Bank 3", "Bank 4", "Bank 5", "Bank 6", "Bank 7", "Bank 8", "Bank 9", "Bank 10",
                "Bank 11", "Bank 12", "Bank 13", "Bank 14", "Bank 15", "Bank 16", "Bank 17", "Bank 18", "Bank 19", "Bank 20",
                "Bank 21", "Bank 22", "Bank 23", "Bank 24", "Bank 25", "Bank 26", "Bank 27", "Bank 28", "Bank 29", "Bank 30",
                "Bank 31", "Bank 32", "Bank 33", "Bank 34", "Bank 35", "Bank 36", "Bank 37", "Bank 38", "Bank 39", "Bank 40",
                "Bank 41", "Bank 42", "Bank 43", "Bank 44"};

        
        public static String[] blindSpType=new String[]{"smiple", "smart"};
        public static String[] blindSpSensitivity=new String[]{"Excellent at a fair spee", "Good and fast", "fair and very fast"};
        public static String[] blindSpMinBandwith=new String[]{"1.5", "3", "5", "10", "15", "20"};
        public static String[] infoSpScanType=new String[]{"Blind", "ColorCode", "Pilot", "CW", "Spectrum"};
    }
}
