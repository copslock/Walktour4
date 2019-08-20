package com.walktour.gui.total;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.framework.view.tableview.BaseTableView;
import com.walktour.framework.view.tableview.Table;
import com.walktour.gui.R;

import java.util.HashMap;

/**
 * 统计PBM
 */
public class TotalMultiHttpDownloadView extends BaseTableView {
    /**
     * 日志标识
     */
    private static final String TAG = "TotalMultiHttpDownloadView";
    /**
     * 统计表标识
     */
    private String mTableTag = "MultiHttpDownload";
    /**
     * 表格行数
     */
    private static final int TABLE_ROWS = 4;
    /**
     * 表格列数
     */
    private static final int TABLE_COLS = 2;
    /**
     * 图片保存名称
     */
    private static final String SAVE_PIC_NAME = "-MultiHttpDownload.jpg";
    public TotalMultiHttpDownloadView(Context context) {
        super(context, TAG, SAVE_PIC_NAME);
    }

    public TotalMultiHttpDownloadView(Context context, AttributeSet attrs) {
        super(context, attrs, TAG, SAVE_PIC_NAME);
    }

    @Override
    protected void createTables() {
        if (super.hasTable(this.mTableTag))
            return;
        Table table = super.createTable(this.mTableTag, TABLE_ROWS, TABLE_COLS);
        table.setTitle(super.getString(R.string.total_multihttpdownload), super.mTextColor);
        super.setTableCells(this.mTableTag, 1, 0, true, super.mTextColor, 1, true);
    }

    @Override
    protected String[][] getTableTexts(String tag) {
        String[][] texts = new String[TABLE_ROWS - 1][TABLE_COLS];
        texts[0][1] = super.getString(R.string.total_download);
        texts[1][0] = super.getString(R.string.total_attemptCount);
        texts[2][0] = super.getString(R.string.total_SuccessCounts);
        return texts;
    }

    @Override
    protected void setTablesDatas() {
        super.setTableCells(this.mTableTag, 2, 1, false, super.mValueColor, 1, true);
    }

    @Override
    protected String[][] getTableValues(String tag) {
        HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
        String[][] values = new String[TABLE_ROWS - 2][TABLE_COLS - 1];
        //尝试次数
        values[0][0] = TotalDataByGSM.getHashMapValue(hMap, TotalStruct.TotalMultiHttpDownload._TryTimes.name());// 尝试次数
        //成功次数
        values[1][0] = TotalDataByGSM.getHashMapValue(hMap, TotalStruct.TotalMultiHttpDownload._SuccessTimes.name());// 成功次数
        return values;
    }
}
