package com.walktour.gui.total;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalDial;
import com.walktour.Utils.TotalStruct.TotalEvent;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;

import java.util.HashMap;

public class TotalQualityTdView extends BasicTotalView {
    private boolean isRegisterReceiver = false;

    public TotalQualityTdView(Context context) {
        super(context);
    }

    public TotalQualityTdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        CreateTable(canvas);
        CreateTableData(canvas, TotalDataByGSM.getInstance().getPara());
    }

    /**
     * 创建表格
     *
     * @param bm 要创建表格的位图
     * @return 输出位图
     */
    protected Bitmap CreateTable(Canvas cv) {
        int width = this.getWidth();
        float startx = 1;
        float starty = 0;
        float stopx = 0;
        float stopy = 0;
        float tablewidth = width;
        int tableRows = 11; // 行数
        int tableCols = 4; // 列数
        float colsWidth = tablewidth / tableCols; // 列宽

        cv.drawLine(startx, marginSize, width - marginSize, marginSize, linePaint);
        cv.drawLine(startx, rowHeight * tableRows, width - marginSize,
                rowHeight * tableRows, linePaint);
        cv.drawLine(startx, marginSize, startx, rowHeight * tableRows, linePaint);
        cv.drawLine(width - marginSize, marginSize, width - marginSize, rowHeight
                * tableRows, linePaint);

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

        float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
        String paraname;
        paraname = getContext().getString(R.string.info_tdscdma);// "MO";
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 1 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_call_mo);// "MO";
        cv.drawText(paraname,
                colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2,
                rowHeight * 1 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_call_mt);// "MT";
        cv.drawText(paraname,
                colsWidth * 3 + (colsWidth - fontPaint.measureText(paraname)) / 2,
                rowHeight * 1 - rowUpBit, fontPaint);

        paraname = getContext().getString(R.string.total_coverage_test_duration);// 测试时长
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 2 - rowUpBit, fontPaint);

        paraname = getContext().getString(R.string.total_call_voicequality);// 话音质量
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 3 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_td_cover_percent1);// 覆盖率1(RSCP>=-94&C/I >=-3
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 4 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_td_cover_percent2);// 覆盖率2(RSCP>=-85&C/I >=0）
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 5 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_coverage_mileage);// 覆盖里程(km)
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 6 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_coverage_total_mileage);// 总里程(km)
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 7 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_coverage_mileage_coverage_ratio);// 里程覆盖率
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 8 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_td_ET_Baton_Handover_succsRate);// 接力切换成功率(%)
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 9 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_td_ET_TD_Hard_Handover_succsRate);// 硬切换成功率(%)
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 10 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_td_Mileag_Operate_Rate);// 里程互操作比
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 11 - rowUpBit, fontPaint);
        cv.save();
        cv.restore();
        return null;
    }

    /**
     * 创建表格数据
     *
     * @param bm   要创建表格的位图
     * @param data 表格数据
     * @return 输出位图
     */
    protected Bitmap CreateTableData(Canvas cv, HashMap<String, Long> paras) {
        int width = this.getWidth();
        float tablewidth = width;
        int tableCols = 4; // 列数
        float colsWidth = tablewidth / tableCols; // 列宽
        float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
        String value;

        value = "" + TotalDataByGSM.getHashMapValue(paras, TotalDial._moTimeLongTD.name(), 1000f);        //MO测试时长
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 2 - rowUpBit, paramPaint);
        value = "" + TotalDataByGSM.getHashMapValue(paras, TotalDial._mtTimeLongTD.name(), 1000f);        //MT测试时长
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 2 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapMultiple(paras,                            // MO话音质量
                TotalDial._moTDBler03s.name(),
                TotalDial._moTDBlerCount.name(), 100, "%");
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 3 - rowUpBit, paramPaint);
        value = "" + TotalDataByGSM.getHashMapMultiple(paras,                            // "MT话音质量";
                TotalDial._mtTDBler03s.name(),
                TotalDial._mtTDBlerCount.name(), 100, "%");
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 3 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapMultiple(paras,                        //覆盖率1(RSCP>=-94&C/I >=-3)
                TotalDial._moPccpchRscp1.name(),
                TotalDial._moPccpchRscpCount.name(), 100, "%");
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 4 - rowUpBit, paramPaint);
        value = "" + TotalDataByGSM.getHashMapMultiple(paras,                        //覆盖率1(RSCP>=-94&C/I >=-3)
                TotalDial._mtPccpchRscp1.name(),
                TotalDial._mtPccpchRscpCount.name(), 100, "%");
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 4 - rowUpBit, paramPaint);

        value = "" + TotalDataByGSM.getHashMapMultiple(paras,                        //覆盖率2(RSCP>=-85&C/I >=0)
                TotalDial._moPccpchRscp2.name(),
                TotalDial._moPccpchRscpCount.name(), 100, "%");
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 5 - rowUpBit, paramPaint);
        value = "" + TotalDataByGSM.getHashMapMultiple(paras,                        //覆盖率2(RSCP>=-85&C/I >=0)
                TotalDial._mtPccpchRscp2.name(),
                TotalDial._mtPccpchRscpCount.name(), 100, "%");
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 5 - rowUpBit, paramPaint);

        value = ""
                + TotalDataByGSM.getHashMapValue(paras, TotalDial._moTdCoverMileage.name(), 10000);
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 6 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getHashMapValue(paras, TotalDial._mtTdCoverMileage.name(), 10000);
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 6 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getHashMapValue(paras, TotalDial._moTdTotalMileage.name(), 10000);
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 7 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getHashMapValue(paras, TotalDial._mtTdTotalMileage.name(), 10000);
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 7 - rowUpBit, paramPaint);

        value = ""
                + TotalDataByGSM.getHashMapMultiple(paras,
                TotalDial._moTdCoverMileage.name(),
                TotalDial._moTdTotalMileage.name(), 100, "%");
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 8 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getHashMapMultiple(paras,
                TotalDial._mtTdCoverMileage.name(),
                TotalDial._mtTdTotalMileage.name(), 100, "%");
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 8 - rowUpBit, paramPaint);

        value = ""
                + TotalDataByGSM.getHashMapMultiple(TotalDataByGSM.getInstance().getEvent(),
                TotalEvent._moTDBatonHandoverSuccess.name(),
                TotalEvent._moTDBatonHandoverRequest.name(), 100, "%");
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 9 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getHashMapMultiple(TotalDataByGSM.getInstance().getEvent(),
                TotalEvent._mtTDBatonHandoverSuccess.name(),
                TotalEvent._mtTDBatonHandoverRequest.name(), 100, "%"); // "mt切换成功率";
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 9 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getHashMapMultiple(TotalDataByGSM.getInstance().getEvent(),
                TotalEvent._moTDHardHandoverSuccess.name(),
                TotalEvent._moTDHardHandoverRequest.name(), 100, "%"); //
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 10 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getHashMapMultiple(TotalDataByGSM.getInstance().getEvent(),
                TotalEvent._mtTDHardHandoverSuccess.name(),
                TotalEvent._mtTDHardHandoverRequest.name(), 100, "%"); //
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 10 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getIntMultiple(
                (TotalDataByGSM.getHashMapVal(paras, TotalDial._moGsmCoverMileage.name())
                        + TotalDataByGSM.getHashMapVal(paras, TotalDial._moTdCoverMileage.name())
                ) * 1f
                ,
                (TotalDataByGSM.getHashMapVal(TotalDataByGSM.getInstance().getEvent(), TotalEvent._moReSetGSM2TD.name())
                        + TotalDataByGSM.getHashMapVal(TotalDataByGSM.getInstance().getEvent(), TotalEvent._moReSetTD2GSM.name())
                        + TotalDataByGSM.getHashMapVal(TotalDataByGSM.getInstance().getEvent(), TotalEvent._moHandOverGSM2TD.name())
                        + TotalDataByGSM.getHashMapVal(TotalDataByGSM.getInstance().getEvent(), TotalEvent._moHandOverTD2GSM.name())
                )
                ,
                1f, "");
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 11 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getIntMultiple(
                (TotalDataByGSM.getHashMapVal(paras, TotalDial._mtGsmCoverMileage.name())
                        + TotalDataByGSM.getHashMapVal(paras, TotalDial._mtTdCoverMileage.name())
                ) * 1f
                ,
                (TotalDataByGSM.getHashMapVal(TotalDataByGSM.getInstance().getEvent(), TotalEvent._mtReSetGSM2TD.name())
                        + TotalDataByGSM.getHashMapVal(TotalDataByGSM.getInstance().getEvent(), TotalEvent._mtReSetTD2GSM.name())
                        + TotalDataByGSM.getHashMapVal(TotalDataByGSM.getInstance().getEvent(), TotalEvent._mtHandOverGSM2TD.name())
                        + TotalDataByGSM.getHashMapVal(TotalDataByGSM.getInstance().getEvent(), TotalEvent._mtHandOverTD2GSM.name())
                )
                ,
                1f, "");
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 11 - rowUpBit, paramPaint);

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
        isRegisterReceiver = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            if (isRegisterReceiver) {
                getContext().unregisterReceiver(mIntentReceiver); // 反注册消息过滤器
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
