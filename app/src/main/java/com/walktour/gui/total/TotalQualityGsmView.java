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

public class TotalQualityGsmView extends BasicTotalView {

    private boolean isRegisterReceiver = false;

    public TotalQualityGsmView(Context context) {
        super(context);
    }

    public TotalQualityGsmView(Context context, AttributeSet attrs) {
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
        int tableRows = 5; // 行数
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
        paraname = getContext().getString(R.string.info_gsm);// "MO";
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

        paraname = getContext().getString(R.string.total_call_voicequality);// "尝试次数";
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 2 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_cover_percent1);// "覆盖率1";
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 3 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_cover_percent2);// "覆盖率2";
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 4 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_switch_SuccsRate);// 切换成功率
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 5 - rowUpBit, fontPaint);
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

        value = "" + getG2VoiceQuality(paras, 1); // "MO话音质量";
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 2 - rowUpBit, paramPaint);
        value = "" + getG2VoiceQuality(paras, 2); // "MT话音质量";
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 2 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getHashMapMultiple(paras,
                TotalDial._moRxLev1s.name(),
                TotalDial._moTotalRxLevs.name(), 100, "%"); // "MO覆盖率1";
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 3 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getHashMapMultiple(paras,
                TotalDial._mtRxLev1s.name(),
                TotalDial._mtTotalRxLevs.name(), 100, "%"); // "MT覆盖率1";
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 3 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getHashMapMultiple(paras,
                TotalDial._moRxLev2s.name(),
                TotalDial._moTotalRxLevs.name(), 100, "%"); // "MO覆盖率2";
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 4 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getHashMapMultiple(paras,
                TotalDial._mtRxLev2s.name(),
                TotalDial._mtTotalRxLevs.name(), 100, "%"); // "MT覆盖率2";
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 4 - rowUpBit, paramPaint);

        value = ""
                + TotalDataByGSM.getHashMapMultiple(TotalDataByGSM.getInstance().getEvent(),
                TotalEvent._moGsmSwitchSuccs.name(),
                TotalEvent._moGsmSwitch.name(), 100, "%"); // "mo切换成功率";
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 5 - rowUpBit, paramPaint);
        value = ""
                + TotalDataByGSM.getHashMapMultiple(TotalDataByGSM.getInstance().getEvent(),
                TotalEvent._mtGsmSwitchSuccs.name(),
                TotalEvent._mtGsmSwitch.name(), 100, "%"); // "mt切换成功率";
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 5 - rowUpBit, paramPaint);

        cv.save();
        cv.restore();
        return null;

    }

    /**
     * 计划2G网络下的语音通话质量
     *
     * @param paras
     * @param callType 1 MO 2，MT
     * @return
     */
    private String getG2VoiceQuality(HashMap<String, Long> paras,
                                     int callType) {
        String grate1 = "";
        String grate2 = "";
        String grateT = "";
        if (callType == 1) {
            grate1 = TotalDataByGSM.getHashMapValue(paras,
                    TotalDial._moRxQual1s.name());
            grate2 = TotalDataByGSM.getHashMapValue(paras,
                    TotalDial._moRxQual2s.name());
            grateT = TotalDataByGSM.getHashMapValue(paras,
                    TotalDial._moTotalRxQuals.name());
        } else {
            grate1 = TotalDataByGSM.getHashMapValue(paras,
                    TotalDial._mtRxQual1s.name());
            grate2 = TotalDataByGSM.getHashMapValue(paras,
                    TotalDial._mtRxQual2s.name());
            grateT = TotalDataByGSM.getHashMapValue(paras,
                    TotalDial._mtTotalRxQuals.name());
        }
        if ((!grate1.equals("") || !grate2.equals("")) && !grateT.equals("")) {
            return TotalDataByGSM.getIntMultiple(
                    (grate1.equals("") ? 0 : Long.parseLong(grate1))
                            + (grate2.equals("") ? 0
                            : Long.parseLong(grate2) * 0.7f),
                    Long.parseLong(grateT), 100, "%");
        } else {
            return "";
        }
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
