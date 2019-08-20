package com.walktour.gui.task.activity.scannertsma.model;

/**
 * @author jinfeng.xie
 * @data 2019/1/30
 */

import android.text.TextUtils;

import com.dingli.seegull.SeeGullFlags;
import com.innsmap.InnsMap.location.bean.P;
import com.walktour.Utils.StringUtil;
import com.walktour.gui.R;
import com.walktour.gui.data.model.NetworkType;
import com.walktour.gui.workorder.hw.model.TestSchema;

import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 频点范围对应值
 * 利用枚举操作
 */
public enum ChannelRange {

//    GSM450(TestSchemaType.GSM,"GSM-450" ,0x101,0x101,		"259-293",""),
//    GSM480(TestSchemaType.GSM,"GSM-480" ,	0x102,0x102,	"306-340"	,""),
//    GSM750(TestSchemaType.GSM, "GSM-750"	,0x103,0x103,"438-511",""),
//    GSM850(TestSchemaType.GSM, "GSM-850",0x104,0x104,	"128-251",""),
//    PGSM900(TestSchemaType.GSM,"P-GSM-900" ,0x105,0x105,"1-124",""),
//    EGSM900(TestSchemaType.GSM, "E-GSM-900",0x105,0x105,"975-1023,0-124",""),
//    RGSM900(TestSchemaType.GSM, "R-GSM-900"	, 0x105,0x105,"955-1023,0-124",""),
//    DCS1800(TestSchemaType.GSM, "DCS-1800"	,0x108,0x108,	"512-885",""),
//
//    C800M(TestSchemaType.CDMA,"800M",0x203,0x203,"1-799,991-1023",""),
//    C1900M(TestSchemaType.CDMA,"1900M"	,0x207,0x207,"0-1199",""),
//    TACS(TestSchemaType.CDMA,"TACS",	0x204,0x204,"0-1000,1329-2047",""),
//     JTACS(TestSchemaType.CDMA,"JTACS",	0x205,0x205,"1-799,801-1039,1041-1199,1201-1600",""),
//      KoreanPCS(TestSchemaType.CDMA,"Korean PCS"	,0x206,0x206,"0-599",""),
//     NMT450(TestSchemaType.CDMA,"NMT-450",0x201,0x201,	"1-300；539-871；1039-1473；1792-2016",""),
//       IMT2000(TestSchemaType.CDMA,"IMT-2000"	,0x208,0x208,"0-1199",""),
//      C700M(TestSchemaType.CDMA,"700M"		"0-359",""),
//    C1800M(TestSchemaType.CDMA,"1800M"		"0-1499",""),
//    C900M(TestSchemaType.CDMA,"900M",0x204,0x204,		"0-699",""),
//    Secondary800M(TestSchemaType.CDMA,"Secondary 800M"	,0x202,0x202,"0-719；720-919",""),




    G850(TestSchemaType.GSM, "850", 0x0100, 0x0101, "127-252", ""),
    G900(TestSchemaType.GSM, "E-GSM900", 0x0600, 0x0601, "0-125,974-1023", ""),
    G1800(TestSchemaType.GSM, "1800", 0x0700, 0x0701, "511-886", ""),
    G1900(TestSchemaType.GSM, "1900", 0x0200, 0x0201, "511-811", ""),

    C450(TestSchemaType.CDMA, "450MHz NMT", 0x2F00, 0x2F01, "27-374,1069-1443,2017,2018", ""),
    C800(TestSchemaType.CDMA, "800MHz cellular", 0x0100, 0x0101, "990-1023,1-780,1044-1323", ""),
    C1900(TestSchemaType.CDMA, "1900", 0x0200, 0x0201, "13-1187", ""),

    E450(TestSchemaType.EVDO, "450MHz NMT", 0x2F00, 0x2F01, "27-374,1069-1443,2017,2018", ""),
    E800(TestSchemaType.EVDO, "800MHz cellular", 0x0100, 0x0101, "990-1023,1-780,1044-1323", ""),
    E1900(TestSchemaType.EVDO, "1900", 0x0200, 0x0201, "13-1187", ""),

    W850(TestSchemaType.WCDMA, "V_850", 0x0100, 0x0101, "4355-4460,1005-1109", "4130-4235,780-884"),
    W900(TestSchemaType.WCDMA, "VIII_900", 0x0600, 0x0601, "2935-3090", "2710-2865"),
    W1900(TestSchemaType.WCDMA, "II_1900", 0x0200, 0x0202, "9660-9940,410-689", "9260-9540,10-289"),
    W1700(TestSchemaType.WCDMA, "IX_1700", 0x07F8, 0x07F9, "9235-9389", "8760-8914"),
    W2100(TestSchemaType.WCDMA, "I_2100", 0x0300, 0x0301, "10560-10840", ""),

    T2000(TestSchemaType.TDSCDMA, "Chinese 2000 (A)", 0x2703, 0x2703, "10054-10121", ""),
    T18TD(TestSchemaType.TDSCDMA, "1.8TD(F)", 0x1F03, 0x1F03, "9404-9596", ""),

    LBand1(TestSchemaType.LTE, "Band1", 0x0300, 0x0301, "", ""),
    LBand2(TestSchemaType.LTE, "Band2", 0x0200, 0x0201, "", ""),
    LBand3(TestSchemaType.LTE, "Band3", 0x0700, 0x0701, "", ""),
    LBand5(TestSchemaType.LTE, "Band5", 0x0100, 0x0101, "", ""),
    LBand7(TestSchemaType.LTE, "Band7", 0x1200, 0x1201, "", ""),
    LBand9(TestSchemaType.LTE, "Band9", 0x07F8, 0x07F9, "", ""),
    LBand12(TestSchemaType.LTE, "Band12", 0x1100, 0x1101, "", ""),
    LBand17(TestSchemaType.LTE, "Band17", 0x11F8, 0x11F9, "", ""),
    LBand20(TestSchemaType.LTE, "Band20", 0x1A00, 0x1A01, "", ""),

    LBand35(TestSchemaType.LTE, "Band35", 0x2803, 0x2803, "", ""),
    LBand38(TestSchemaType.LTE, "Band38", 0x1C03, 0x1C03, "", ""),
    LBand39(TestSchemaType.LTE, "Band39", 0x1F03, 0x1F03, "", ""),
    LBand40(TestSchemaType.LTE, "Band40", 0x1D03, 0x1D03, "", ""),
    LBand41(TestSchemaType.LTE, "Band41", 0x1E03, 0x1E03, "", ""),
    LBand42(TestSchemaType.LTE, "Band42", 0x2303, 0x2303, "", ""),
    LBand43(TestSchemaType.LTE, "Band43", 0x2403, 0x2403, "", ""),
    LBand44(TestSchemaType.LTE, "Band44", 0x2503, 0x2503, "", "");

    private int mNetType;                //标识区分网络
    private String mBandTypeName;        //频段类型名字
    private String mChannelRangeDL;        //下行频点支持范围
    private String mChannelRangeUL;        //上行频点支持范围
    private int mBandCodeDL;        //下行频段代号
    private int mBandCodeUL;        //上行频段代号


    ChannelRange(int netType, String bandTypeName, int bandCodeDL, int bandCodeUL, String channelRangeDL, String channelRangeUL) {
        this.mNetType = netType;
        this.mBandTypeName = bandTypeName;
        this.mChannelRangeDL = channelRangeDL;
        if (StringUtil.isNullOrEmpty(channelRangeUL)) {
            this.mChannelRangeUL = channelRangeDL;
        } else {
            this.mChannelRangeUL = channelRangeUL;
        }
        this.mBandCodeDL = bandCodeDL;
        this.mBandCodeUL = bandCodeUL;
    }

    /**
     * 获取频段名字数组
     *
     * @param netType
     * @param isUL     是否上行链路
     * @param firstRow 第一行显示的选项
     * @return
     */
    public static String[] getBandStrArray(int netType, boolean isUL, String firstRow) {
        Set<Integer> netTypeSet = new HashSet<Integer>();
        netTypeSet.add(netType);
        List<String> bandStrList = new ArrayList<String>();
        bandStrList.add("--" + firstRow + "--");
        for (ChannelRange channel : ChannelRange.values()) {
            if (netTypeSet.contains(channel.mNetType)) {
                bandStrList.add(channel.mBandTypeName);
            }
        }
        return bandStrList.toArray(new String[bandStrList.size()]);
    }

    /**
     * 获取bandcode数组
     *
     * @param netType      网络类型
     * @param isUL         是否上行链路
     * @return
     */
    public static int[] getBandCodeArray( int netType, boolean isUL) {
        Set<Integer> netTypeSet = new HashSet<Integer>();
        netTypeSet.add(netType);
        List<Integer> bandCodeList = new ArrayList<Integer>();

        for (ChannelRange channel : ChannelRange.values()) {
            if (netTypeSet.contains(channel.mNetType)) {
                bandCodeList.add(isUL ? channel.mBandCodeUL : channel.mBandCodeDL);
            }
        }
        int[] bandCodes = new int[bandCodeList.size()];
        for (int i = 0; i < bandCodes.length; i++) {
            bandCodes[i] = bandCodeList.get(i);
        }
        return bandCodes;
    }

    /**
     * 获取频点范围数组集合
     *
     * @param netType 网络类型
     * @param isUL    是否上行链路
     * @return
     */
    public static List<String> getBandRangeList(int netType, boolean isUL) {
        Set<Integer> netTypeSet = new HashSet<Integer>();
        netTypeSet.add(netType);
        List<String> bandRangeList = new ArrayList<String>();
        for (ChannelRange channel : ChannelRange.values()) {
            if (netTypeSet.contains(channel.mNetType)) {
                    String channel1 = isUL?channel.mChannelRangeUL:channel.mChannelRangeDL;
                    if (!TextUtils.isEmpty(channel1)) {
                        bandRangeList.add(channel1);
                    }
            }
        }
        return bandRangeList;
    }
}
