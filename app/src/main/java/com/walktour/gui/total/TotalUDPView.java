package com.walktour.gui.total;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.SDCardUtil;
import com.walktour.framework.view.tableview.BaseTableView;
import com.walktour.framework.view.tableview.Table;
import com.walktour.gui.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * 统计PBM
 */
public class TotalUDPView extends BaseTableView {

    /**
     * 日志标识
     */
    private static final String TAG = "TotalUDPView";
    /***
     * 广播接收
     */
    public static final String ACTION = "com.walktour.gui.total.udp.test";
    /**
     * 统计表标识
     */
    private String mTableTag = "udp";
    /**
     * 表格行数
     */
    private static final int TABLE_ROWS = 15;
    /**
     * 表格列数
     */
    private static final int TABLE_COLS = 3;
    /**
     * 图片保存名称
     */
    private static final String SAVE_PIC_NAME = "-UDP.jpg";

    String SDCARDPATH = "";

    /**
     * 是否注册广播监听器
     */
    private boolean isRegisterReceiverUDP = false;

    private String[][] values = null;

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        try {
            values = new String[TABLE_ROWS - 2][TABLE_COLS - 1];
            getContext().registerReceiver(udpIntentReceiver, createUDPIntentFilter(), null, null);
            isRegisterReceiverUDP = true;
        } catch (Exception e) {
            LogUtil.w(TAG, e.toString());
        }
    }

    /**
     * 注册广播接听器的过滤设置
     *
     * @param receiver 广播接听器
     */
    protected IntentFilter createUDPIntentFilter()
    {
        IntentFilter filter = new IntentFilter(); // 注册一个消息过滤器
        filter.addAction(TotalUDPView.ACTION);
        return filter;
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        try {
            if (isRegisterReceiverUDP) {
                getContext().unregisterReceiver(udpIntentReceiver); // 反注册消息过滤器
                isRegisterReceiverUDP = false;
            }
        } catch (Exception e) {
            LogUtil.w(TAG, e.toString());
        }
    }

    public TotalUDPView(Context context)
    {
        super(context, TAG, SAVE_PIC_NAME);
    }

    public TotalUDPView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TAG, SAVE_PIC_NAME);
    }

    @Override
    protected void createTables()
    {
        if (super.hasTable(this.mTableTag))
            return;
        Table table = super.createTable(this.mTableTag, TABLE_ROWS, TABLE_COLS);
        table.setTitle(super.getString(R.string.total_udp), super.mTextColor);
        SDCARDPATH = SDCardUtil.getSDCardPath();
        super.setTableCells(this.mTableTag, 1, 0, true, super.mTextColor, 1, true);
    }

    @Override
    protected String[][] getTableTexts(String tag)
    {
        String[][] texts = new String[TABLE_ROWS - 1][TABLE_COLS];
        texts[0][1] = super.getString(R.string.total_upload);
        texts[0][2] = super.getString(R.string.total_download);
        texts[1][0] = super.getString(R.string.total_attemptCount);
        texts[2][0] = super.getString(R.string.total_SuccessCounts);
        texts[3][0] = super.getString(R.string.total_DropRate);
        texts[5][0] = super.getString(R.string.total_ftp_appAverageRate);
        texts[4][0] = super.getString(R.string.total_SuccessRate);
        texts[6][0] = super.getString(R.string.total_udp_phy);
        texts[7][0] = super.getString(R.string.total_udp_phy_avg);
        texts[8][0] = super.getString(R.string.total_udp_mac);
        texts[9][0] = super.getString(R.string.total_udp_mac_avg);
        texts[10][0] = super.getString(R.string.total_udp_rlc);
        texts[11][0] = super.getString(R.string.total_udp_rlc_avg);
        texts[12][0] = super.getString(R.string.total_udp_pdcp);
        texts[13][0] = super.getString(R.string.total_udp_pdcp_avg);
        return texts;
    }

    @Override
    protected void setTablesDatas()
    {
        super.setTableCells(this.mTableTag, 2, 1, false, super.mValueColor, 1, true);
    }

    @Override
    protected String[][] getTableValues(String tag)
    {
        HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
        //尝试次数
        values[0][0] = TotalDataByGSM.getHashMapValue(hMap, TotalStruct.TotalUDP._upTryTimes.name
                ());// 上行尝试次数
        values[0][1] = TotalDataByGSM.getHashMapValue(hMap, TotalStruct.TotalUDP._downTryTimes
                .name());// 下行尝试次数
        //成功次数
        values[1][0] = TotalDataByGSM.getHashMapValue(hMap, TotalStruct.TotalUDP._upSuccessTimes
                .name());// 上行成功次数
        values[1][1] = TotalDataByGSM.getHashMapValue(hMap, TotalStruct.TotalUDP
                ._downSuccessTimes.name());// 下行成功次数
        //掉线率
        String value = "" + TotalDataByGSM.getHashMapMultiple(hMap,
                TotalStruct.TotalUDP._upFailTimes.name(),
                TotalStruct.TotalUDP._upTryTimes.name(),
                100, "%");
        values[2][0] = value;
        value = "" + TotalDataByGSM.getHashMapMultiple(hMap,
                TotalStruct.TotalUDP._downFailTimes.name(),
                TotalStruct.TotalUDP._downTryTimes.name(),
                100, "%");
        values[2][1] = value;
        //应用层平均速率
        values[4][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalUDP
                        ._upAverageSpeed.name(),
                TotalStruct.TotalUDP._upSuccessTimes.name(), 0.001f, "");// 上行带宽
        values[4][1] = TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalUDP
                        ._downAverageSpeed.name(),
                TotalStruct.TotalUDP._downSuccessTimes.name(), 0.001f, "");// 下行带宽
        //成功率
        value = "" + TotalDataByGSM.getHashMapMultiple(hMap,
                TotalStruct.TotalUDP._upSuccessTimes.name(),
                TotalStruct.TotalUDP._upTryTimes.name(),
                100, "%");
        values[3][0] = value;
        value = "" + TotalDataByGSM.getHashMapMultiple(hMap,
                TotalStruct.TotalUDP._downSuccessTimes.name(),
                TotalStruct.TotalUDP._downTryTimes.name(),
                100, "%");
        values[3][1] = value;
        //RLC
        //RLC累计平均
        values[5][0] = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalUDP
                        ._upAverageSpeedRLCTotal.name(), TotalStruct.TotalUDP._upSuccessTimes
                        .name(),
                0.001f, "");
        values[5][1] = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalUDP
                        ._downAverageSpeedRLCTotal.name(), TotalStruct.TotalUDP._downSuccessTimes
                        .name(),
                0.001f, "");
        //RLC单次平均
//        values[6][0] = getVX2(SDCARDPATH + "Walktour/data/1.txt");
//        values[6][1] = getVX2(SDCARDPATH + "Walktour/data/4.txt");
        //MAC
        //MAC累计平均
        values[7][0] = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalUDP
                        ._upAverageSpeedMACTotal.name(), TotalStruct.TotalUDP._upSuccessTimes
                        .name(),
                0.001f, "");
        values[7][1] = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalUDP
                        ._downAverageSpeedMACTotal.name(), TotalStruct.TotalUDP._downSuccessTimes
                        .name(),
                0.001f, "");
        //MAC单次平均
//        values[8][0] = getVX2(SDCARDPATH + "Walktour/data/2.txt");
//        values[8][1] = getVX2(SDCARDPATH + "Walktour/data/5.txt");
        //PHY
        //PHY累计平均
        values[9][0] = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalUDP
                        ._upAverageSpeedPHYTotal.name(), TotalStruct.TotalUDP._upSuccessTimes
                        .name(),
                0.001f, "");
        values[9][1] = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalUDP
                        ._downAverageSpeedPHYTotal.name(), TotalStruct.TotalUDP._downSuccessTimes
                        .name(),
                0.001f, "");
        //PHY单次平均
//        values[10][0] = getVX2(SDCARDPATH + "Walktour/data/3.txt");
//        values[10][1] = getVX2(SDCARDPATH + "Walktour/data/6.txt");
        //PDCP
        //PDCP累计平均
        values[11][0] = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalUDP
                        ._upAverageSpeedPDCPTotal.name(), TotalStruct.TotalUDP._upSuccessTimes
                        .name(),
                0.001f, "");
        values[11][1] = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalUDP
                        ._downAverageSpeedPDCPTotal.name(), TotalStruct.TotalUDP
                        ._downSuccessTimes.name()
                , 0.001f, "");
        //PDCP单次平均
//        values[12][0] = getVX2(SDCARDPATH + "WalkTourour/data/7.txt");
//        values[12][1] = getVX2(SDCARDPATH + "Walktour/data/8.txt");
        return values;
    }


    /**
     * 消息处理
     */
    private final BroadcastReceiver udpIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(TotalUDPView.ACTION)) {
                int direct = intent.getIntExtra("direct", 0);
                if (direct == 0) {//上行
                    long val = intent.getLongExtra("Walktour/data/1.txt", 0);
                    values[6][0] = formatVal(val / 1000.00f) + "";
                    val = intent.getLongExtra("Walktour/data/2.txt", 0);
                    values[8][0] = formatVal(val / 1000.00f) + "";
                    val = intent.getLongExtra("Walktour/data/3.txt", 0);
                    values[10][0] = formatVal(val / 1000.00f) + "";
                    val = intent.getLongExtra("Walktour/data/4.txt", 0);
                    values[12][0] = formatVal(val / 1000.00f) + "";
                    values[6][1] = "";
                    values[8][1] = "";
                    values[10][1] = "";
                    values[12][1] = "";
                } else if (direct == 1) {//下行
                    long val = intent.getLongExtra("Walktour/data/1.txt", 0);
                    values[6][1] = formatVal(val / 1000.00f) + "";
                    val = intent.getLongExtra("Walktour/data/2.txt", 0);
                    values[8][1] = formatVal(val / 1000.00f) + "";
                    val = intent.getLongExtra("Walktour/data/3.txt", 0);
                    values[10][1] = formatVal(val / 1000.00f) + "";
                    val = intent.getLongExtra("Walktour/data/4.txt", 0);
                    values[12][1] = formatVal(val / 1000.00f) + "";
                    values[6][0] = "";
                    values[8][0] = "";
                    values[10][0] = "";
                    values[12][0] = "";
                }
            }
        }
    };

    /**
     * 格式化字符串
     *
     * @param num
     * @return
     */
    private String formatVal(float num)
    {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(num);
    }
}
