/**
 *
 */
package com.walktour.gui.total;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;

import java.util.HashMap;


/**
 * OTT統計
 * <p>
 * 2013-11-15 下午4:10:45
 *
 * @author jinfeng.xie
 * @version 1.0.0
 */
public class TotalOttView extends BasicTotalView {


    public TotalOttView(Context context) {
        super(context);
    }

    public TotalOttView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        CreateTable(canvas);
        CreateTableData(canvas, TotalDataByGSM.getInstance().getUnifyTimes());
    }

    /**
     * 创建表格
     *
     * @return 输出位图
     */
    protected Bitmap CreateTable(Canvas cv) {
        int width = this.getWidth();

        float startx = 1;
        float starty = 0;
        float stopx = 0;
        float stopy = 0;
        float tablewidth = width;
        int tableRows = 5; // 行数
        int tableCols = 3; // 列数
        float colsWidth = tablewidth / tableCols; // 列宽

        /* 画四边 */
        cv.drawLine(startx, marginSize, width - marginSize, marginSize, linePaint);
        cv.drawLine(startx, rowHeight * tableRows, width - marginSize, rowHeight * tableRows, linePaint);
        cv.drawLine(startx, marginSize, startx, rowHeight * tableRows, linePaint);
        cv.drawLine(width - marginSize, marginSize, width - marginSize, rowHeight * tableRows, linePaint);
        /* 画横线 */
        for (int i = 1; i < tableRows; i++) {
            startx = 1;
            starty = rowHeight * i;
            stopx = width - marginSize;
            stopy = rowHeight * i;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);
        }
        /* 画竖线 */
        // for(int i=0;i<tableCols - 1;i++){
        startx = colsWidth * 2;
        starty = rowHeight;
        stopx = colsWidth * 2;
        stopy = rowHeight * tableRows;
        cv.drawLine(startx, starty, stopx, stopy, linePaint);
        // }

        float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2

        String paraname;

        /* 第一行 */
        paraname = getContext().getString(R.string.total_ott);
        cv.drawText(paraname, (colsWidth * 3 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 1 - rowUpBit, fontPaint);

        paraname = getContext().getString(R.string.total_attemptCount);
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 2 - rowUpBit, fontPaint);

        paraname = getContext().getString(R.string.total_SuccessCounts);
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 3 - rowUpBit, fontPaint);

        paraname = getContext().getString(R.string.total_SuccessRate);
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 4 - rowUpBit, fontPaint);

        paraname = getContext().getString(R.string.total_AverageDelay);
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 5 - rowUpBit, fontPaint);

        cv.save();
        cv.restore();
        return null;
    }

    /**
     * 创建表格数据
     *
     * @param data 表格数据
     * @return 输出位图
     */
    protected Bitmap CreateTableData(Canvas cv, HashMap<String, Long> hMap) {
        int width = this.getWidth();
        float tablewidth = width;
        int tableCols = 3; // 列数
        float colsWidth = tablewidth / tableCols; // 列宽
        float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2

        String value;

        /* 第二行 */
        value = "" + TotalDataByGSM.getHashMapValue(hMap,
                TotalStruct.TotalAppreciation._OttTryTimes.name());// 发送尝试次数
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 2 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapValue(hMap,
                TotalStruct.TotalAppreciation._OttSuccessTimes.name());// 发送成功次数
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 3 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapMultiple(hMap,
                TotalStruct.TotalAppreciation._OttSuccessTimes.name(),
                TotalStruct.TotalAppreciation._OttTryTimes.name(), 100, "%");// 成功率
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 4 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapMultiple(hMap,
                TotalStruct.TotalAppreciation._OttDelayTimes.name(),
                TotalStruct.TotalAppreciation._OttTryTimes.name(), 1, "");// 平均时延
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 5 - rowUpBit, paramPaint);

        cv.save();
        cv.restore();
        return null;

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter(); // 注册一个消息过滤器
        filter.addAction(TotalDataByGSM.TotalTaskDataChanged);
        filter.addAction(TotalDataByGSM.TotalParaDataChanged);
        getContext().registerReceiver(mIntentReceiver, filter, null, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(mIntentReceiver); // 反注册消息过滤器
    }

    /**
     * 消息处理
     */
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            invalidate();
        }
    };
}
