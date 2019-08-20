package com.walktour.gui.total;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalVS;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.tableview.BaseTableView;
import com.walktour.framework.view.tableview.Table;
import com.walktour.gui.R;

import java.util.HashMap;

/**
 * VS业务统计展现页面
 *
 * @author jianchao.wang
 */
public class TotalVSView extends BaseTableView {
    /**
     * 日志标识
     */
    private static final String TAG = "TotalVSView";
    /**
     * 统计表标识
     */
    private String mTableTag = "vs";
    /**
     * 表格行数
     */
    private static final int TABLE_ROWS = 14;
    /**
     * 表格列数
     */
    private static final int TABLE_COLS = 3;
    /**
     * 图片保存名称
     */
    private static final String SAVE_PIC_NAME = "-VS.jpg";

    public TotalVSView(Context context) {
        super(context, TAG, SAVE_PIC_NAME);
    }

    public TotalVSView(Context context, AttributeSet attrs) {
        super(context, attrs, TAG, SAVE_PIC_NAME);
    }

    @Override
    protected void createTables() {
        if (super.hasTable(this.mTableTag))
            return;
        Table table = super.createTable(this.mTableTag, TABLE_ROWS, TABLE_COLS);
        table.setTitle(super.getString(R.string.strean_KPI), super.mTextColor);
        table.mergeCols(1, 0, 1);
        super.setTableCells(this.mTableTag, 1, 0, true, super.mTextColor, 1, true);
    }

    @Override
    protected String[][] getTableTexts(String tag) {
        String[][] texts = new String[TABLE_ROWS - 1][1];
        texts[0][0] = super.getString(R.string.total_vs_kpi_attempts);
        texts[1][0] = super.getString(R.string.total_vs_kpi_successAttempts);
        texts[2][0] = super.getString(R.string.total_vs_kpi_successAttemptRats);
        texts[3][0] = super.getString(R.string.total_vs_kpi_meanAccessTime);
        texts[4][0] = super.getString(R.string.total_vs_kpi_startReproductions);
        texts[5][0] = super.getString(R.string.total_vs_kpi_reproductionFailureRats);
        texts[6][0] = super.getString(R.string.total_vs_kpi_reproductionDelay);
        texts[7][0] = super.getString(R.string.total_vs_kpi_playEnds);
        texts[8][0] = super.getString(R.string.total_vs_kpi_playDrops);
        texts[9][0] = super.getString(R.string.total_vs_kpi_playDropRats);
        texts[10][0] = super.getString(R.string.total_vs_kpi_reBufferCount);
        texts[11][0] = super.getString(R.string.total_vs_kpi_reBufferFailureRats);
        texts[12][0] = super.getString(R.string.total_vs_kpi_meanReBufferTime);
        return texts;
    }

    @Override
    protected void setTablesDatas() {
        super.setTableCells(this.mTableTag, 1, 2, false, super.mValueColor, 1, true);
    }

    @Override
    protected String[][] getTableValues(String tag) {
        HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
        String[][] values = new String[TABLE_ROWS - 1][1];
        values[0][0] = TotalDataByGSM.getHashMapValue(hMap, TotalVS._vsTrys.name());
        values[1][0] = TotalDataByGSM.getHashMapValue(hMap, TotalVS._vsSuccs.name());
        values[2][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVS._vsSuccs.name(),
                TotalVS._vsTrys.name(), 100, "%");
        values[3][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVS._vsConnectTime.name(),
                TotalVS._vsSuccs.name(), 1, "ms");
        values[4][0] = TotalDataByGSM.getHashMapValue(hMap, TotalVS._vsReproductionStart.name());
        if (hMap.containsKey(TotalVS._vsTrys.name())) {
            long val = (hMap.containsKey(TotalVS._vsSuccs.name()) ? hMap.get(TotalVS._vsSuccs.name()) : 0)
                    - (hMap.containsKey(TotalVS._vsReproductionStart.name()) ? hMap.get(TotalVS._vsReproductionStart.name()) : 0);
            LogUtil.w(VIEW_LOG_TAG, "------>" + val + "," + hMap.get(TotalVS._vsSuccs.name()));
            values[5][0] = TotalDataByGSM.getIntMultiple(val,
                    (hMap.containsKey(TotalVS._vsSuccs.name()) ? hMap.get(TotalVS._vsSuccs.name()) : 1), 100, "%");
        }
        values[6][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVS._vsReproductionDaily.name(),
                TotalVS._vsReproductionStart.name(), 1, "ms");
        values[7][0] = TotalDataByGSM.getHashMapValue(hMap, TotalVS._vsPlayEnd.name());
        values[8][0] = TotalDataByGSM.getHashMapValue(hMap, TotalVS._vsDrop.name());
        values[9][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVS._vsDrop.name(),
                TotalVS._vsReproductionStart.name(), 100, "%");
        values[10][0] = TotalDataByGSM.getHashMapValue(hMap, TotalVS._vsReBuffers.name());
        values[11][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVS._vsReBufferFailure.name(),
                TotalVS._vsReBuffers.name(), 100, "%");
        values[12][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVS._vsRebufferTime.name(),
                TotalVS._vsReBufferSuccess.name(), 1, "ms");
        return values;
    }

}
