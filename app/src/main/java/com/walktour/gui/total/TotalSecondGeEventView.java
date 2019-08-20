package com.walktour.gui.total;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalEvent;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;

import java.util.HashMap;

public class TotalSecondGeEventView extends BasicTotalView {
    private boolean isRegisterReceiver = false;

    public TotalSecondGeEventView(Context context) {
        super(context);
    }

    public TotalSecondGeEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        CreateTable(canvas);
        CreateTableData(canvas, TotalDataByGSM.getInstance().getEvent());
    }


    /**
     * 创建表格
     *
     * @param bm 要创建表格的位图
     * @return 输出位图
     */
    protected void CreateTable(Canvas cv) {
        int width = this.getWidth();
        float startx = 1;
        float starty = 0;
        float stopx = 0;
        float stopy = 0;
        float tablewidth = width;
        int tableRows = 14;    //行数
        int tableCols = 3;    //列数
        float colsWidth = tablewidth / tableCols;        //列宽
        cv.drawLine(startx, marginSize, width - marginSize, marginSize, linePaint);
        cv.drawLine(startx, rowHeight * tableRows, width - marginSize, rowHeight * tableRows, linePaint);
        cv.drawLine(startx, marginSize, startx, rowHeight * tableRows, linePaint);
        cv.drawLine(width - marginSize, marginSize, width - marginSize, rowHeight * tableRows, linePaint);

        for (int i = 0; i < tableRows - 1; i++) {
            startx = 1;
            starty = rowHeight * (i + 1);
            stopx = width - marginSize;
            stopy = rowHeight * (i + 1);
            cv.drawLine(startx, starty, stopx, stopy, linePaint);
        }
        for (int i = 0; i < tableCols - 2; i++) {
            startx = colsWidth * (i + 2);
            starty = marginSize;
            stopx = colsWidth * (i + 2);
            stopy = rowHeight * tableRows;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);
        }

        float rowUpBit = (rowHeight - textSize) / 2;        //指定行上升位数,为行高-字体高度 再除2

        String paraname;
        paraname = getContext().getString(R.string.total_value);//"尝试次数";
        cv.drawText(paraname, colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2, rowHeight * 1 - rowUpBit, fontPaint);

        paraname = getContext().getString(R.string.total_e_lacTrys);            //切换次数
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 2 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_e_lacSuccs);        //切换成功次数
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 3 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_e_lacRate);    //"成功次数";
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 4 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_rau_try);                //LAU 位置更新次数
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 5 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_rau_success);            //LAU 位置更新成功次数
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 6 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_rau_successrate);        //LAU 位置更新成功率
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 7 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_avg_rau);                //平均RAU间隔时间(Min)
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 8 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_e_rau_avg_range);                //平均RAU间隔时间(Min)
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 9 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_avg_area_rechoose);            //RAU 路由更新成功次数
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 10 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_avg_area_rechoose_range);        //RAU 路由更新成功率数
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 11 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_e_lte_handover_request);                //平均RAU间隔时间(Min)
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 12 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_e_lte_handover_succs);                //Rau平均时间间格
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 13 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_e_lte_handover_succsRate);            //小区重选次数
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 14 - rowUpBit, fontPaint);

        cv.save();
        cv.restore();
    }

    /**
     * 创建表格数据
     *
     * @param bm   要创建表格的位图
     * @param data 表格数据
     * @return 输出位图
     */
    protected void CreateTableData(Canvas cv, HashMap<String, Long> hMap) {
        int width = this.getWidth();
        float tablewidth = width;
        int tableCols = 3;    //列数
        float colsWidth = tablewidth / tableCols;                                        //列宽
        float rowUpBit = (rowHeight - textSize) / 2;                                //指定行上升位数,为行高-字体高度 再除2

        String value;

        value = "" + TotalDataByGSM.getHashMapValue(hMap, TotalEvent._lacTry.name());
        cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 2 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapValue(hMap, TotalEvent._lacSuccs.name());
        cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 3 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._lacSuccs.name(),
                TotalEvent._lacTry.name(), 100, "%");
        cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 4 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapValue(hMap, TotalEvent._rauTrys.name());
        cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 5 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapValue(hMap, TotalEvent._rauSuccs.name());
        cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 6 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._rauSuccs.name(),
                TotalEvent._rauTrys.name(), 100, "%");
        cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 7 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._rauTrys.name(),    //平均RAU间隔时间(Min)
                TotalEvent._testTimeLong.name(), 1, "");
        cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 8 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._rauTrys.name(),    //平均RAU间隔距离(km)
                TotalEvent._testMileage.name(), 1, "");
        cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 9 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._sectionReChooses.name(), //平均小区重选间隔时间(s)
                TotalEvent._testTimeLong.name(), 1, "");
        cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 10 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._sectionReChooses.name(), //平均小区重选间隔距离(km)
                TotalEvent._testMileage.name(), 1, "");
        cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 11 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapValue(hMap, TotalEvent._lteHandOverReq.name());
        cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 12 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapValue(hMap, TotalEvent._lteHandOverSucss.name());
        cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 13 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._lteHandOverSucss.name(),
                TotalEvent._lteHandOverReq.name(), 100, "%");
        cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 14 - rowUpBit, paramPaint);
        cv.save();

        cv.restore();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter(); //注册一个消息过滤器
        filter.addAction(TotalDataByGSM.TotalTaskDataChanged);
        filter.addAction(TotalDataByGSM.TotalParaDataChanged);
        getContext().registerReceiver(mIntentReceiver, filter, null, null);
        isRegisterReceiver = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            if (isRegisterReceiver) {
                getContext().unregisterReceiver(mIntentReceiver); //反注册消息过滤器
                isRegisterReceiver = false;
            }
        } catch (java.lang.IllegalArgumentException e) {
            LogUtil.w("IllegalArgumentException:", e.toString());
        }
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
