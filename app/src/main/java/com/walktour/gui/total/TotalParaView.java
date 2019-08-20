package com.walktour.gui.total;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalDataInterface;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ParamInfo;
import com.walktour.control.config.ParamItem;
import com.walktour.control.config.ParamTotalInfo;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.framework.view.ViewSizeLinstener;
import com.walktour.gui.R;
import com.walktour.model.TotalMeasureModel;

import java.util.HashMap;
import java.util.List;

public class TotalParaView extends BasicTotalView {
    private static String tag = "TotalParaView";
    
    private static int currentPositon = 0;
    
    private boolean isRegisterReceiver = false;
    
    private ViewSizeLinstener viewSizeLinstener;
    
    DisplayMetrics metric = new DisplayMetrics();
    
    public TotalParaView(Context context) {
        super(context);
        ((Activity) context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(metric);
        
    }
    
    public TotalParaView(Context context,ViewSizeLinstener viewSizeLinstener) {
        super(context);
        ((Activity) context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(metric);
        this.viewSizeLinstener = viewSizeLinstener;
    }
    
    public TotalParaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((Activity) context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(metric);
    }
    
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        CreateTable(canvas);
        CreateTableData(canvas, TotalDataByGSM.getInstance().getMeasuePara());
    }
    
    /**
     * 创建表格
     * 
     * @param bm
     *            要创建表格的位图
     * @return 输出位图
     */
    protected Bitmap CreateTable(Canvas cv) {
        int width = this.getWidth();
        float startx = 1;
        float starty = 1;
        float stopx = 0;
        float stopy = 0;
        float tablewidth = width;
        int tableRows = 8; // 行数
        int tableCols = 5; // 列数
        
        float colsWidth = tablewidth / tableCols; // 列宽
        
        float rowUpBit = (rowHeight - textSize) / 2;
        
        cv.drawLine(startx,
                marginSize,
                width - marginSize,
                marginSize,
                linePaint);
        cv.drawLine(startx,
                rowHeight * tableRows,
                width - marginSize,
                rowHeight * tableRows,
                linePaint);
        cv.drawLine(startx,
                marginSize,
                startx,
                rowHeight * tableRows,
                linePaint);
        cv.drawLine(width - marginSize, marginSize, width - marginSize, rowHeight
                * tableRows, linePaint);
        
        for (int i = 0; i < tableRows - 1; i++) {
            startx = 1;
            starty = rowHeight * (i + 1);
            stopx = width  - marginSize;
            stopy = rowHeight * (i + 1);
            cv.drawLine(startx, starty, stopx, stopy, linePaint);
        }
        for (int i = 1; i < tableCols - 1; i++) {
            startx = colsWidth * (i + 1);
            starty = 1 + rowHeight * 1;
            stopx = colsWidth * (i + 1);
            stopy = rowHeight * tableRows;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);
        }
        startx = 0;
        starty = rowHeight * 10;
        
        stopx = 0;
        stopy = this.getHeight() - 15 * metric.density;
        
        float yCoordinateHeight = stopy - starty;
        // cv.drawLine(startx, starty, stopx, stopy, fontPaint);
        
        startx = 0;
        starty = this.getHeight() - 15 * metric.density;
        
        stopx = this.getWidth() - 20;
        stopy = this.getHeight() - 15 * metric.density;
        
        cv.drawLine(startx, starty, stopx, stopy, linePaint);
        float rectWidth = 30 * metric.density; // 柱状宽度
        float space = (this.getWidth() - 40 -rectWidth * 5)/4;
        
        List<ParamInfo> list = ParamTotalInfo.getInstance().getParamList();
        
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.param_view_line_color));
        paint.setStrokeWidth(1f);
        if (list.size() > 0) {
            List<ParamItem> paramItemList = list.get(currentPositon).paramItemList;
            HashMap<String, Long> map = TotalDataByGSM.getInstance().getPara();
            if(paramItemList.size() > 5){
                space = (this.getWidth() - 40 -rectWidth * paramItemList.size())/(paramItemList.size() -1);
            }
            for (int i = 0; i < paramItemList.size(); i++) {
                String s = TotalDataInterface.getHashMapMultiple(map,
                        paramItemList.get(i).itemname,
                        list.get(currentPositon).paramName,
                        100,
                        "");
                if (s != null && !s.equals("")) {
                    paramItemList.get(i).percent = Float.parseFloat(s);
                } else {
                    paramItemList.get(i).percent = 0;
                }
                // LogUtil.w(tag,
                // "----paramItemList.get(i).percent="+paramItemList.get(i).percent);
                float left = startx + 15 + i * (rectWidth + space);
                float top = stopy - (paramItemList.get(i).percent / 100)
                        * yCoordinateHeight;
                float right = left + rectWidth;
                float bottom = stopy;
                paint.setColor(paramItemList.get(i).color);
                cv.drawRect(left, top, right, bottom, paint);
                paint.setTextSize(fontPaint.getTextSize());
                float bottom_textX = right - rectWidth / 2
                        - paint.measureText(paramItemList.get(i).showName)
                        / 2;
                String percent = paramItemList.get(i).percent + "%";
                cv.drawText(paramItemList.get(i).showName,
                        bottom_textX,
                        bottom + 20 + rowUpBit,
                        paint);
                paint.setColor(getResources().getColor(R.color.app_main_text_color));
                float percent_textX = right - rectWidth / 2
                        - paint.measureText(percent) / 2;
                cv.drawText(percent, percent_textX, top - 7, paint);
            }
        }
        
        String paraname;
        paraname = "GSM Cell Info";// 表头
        cv.drawText(paraname,
                (tablewidth - fontPaint.measureText(paraname)) / 2,
                rowHeight * 1 - rowUpBit,
                fontPaint);
        paraname = getContext().getString(R.string.total_para);// "参数名称";
        cv.drawText("GSM Cell Info",
                colsWidth * 0
                        + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 2 - rowUpBit,
                fontPaint);
        paraname = getContext().getString(R.string.total_max);// "最大";
        cv.drawText(paraname,
                colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname))
                        / 2,
                rowHeight * 2 - rowUpBit,
                fontPaint);
        paraname = getContext().getString(R.string.total_min);// "最小";
        cv.drawText(paraname,
                colsWidth * 3 + (colsWidth - fontPaint.measureText(paraname))
                        / 2,
                rowHeight * 2 - rowUpBit,
                fontPaint);
        paraname = getContext().getString(R.string.total_average);// "平均";
        cv.drawText(paraname,
                colsWidth * 4 + (colsWidth - fontPaint.measureText(paraname))
                        / 2,
                rowHeight * 2 - rowUpBit,
                fontPaint);
        paraname = getContext().getString(R.string.total_rxQual)
                + getContext().getString(R.string.total_full);// "RxQualFull";
        cv.drawText(paraname,
                colsWidth * 0
                        + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 3 - rowUpBit,
                fontPaint);
        paraname = getContext().getString(R.string.total_rxQual)
                + getContext().getString(R.string.total_sub);// "RxQualSub";
        cv.drawText(paraname,
                colsWidth * 0
                        + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 4 - rowUpBit,
                fontPaint);
        paraname = getContext().getString(R.string.total_rxLev)
                + getContext().getString(R.string.total_full) + "(dBm)";// "RxLevFull";
        cv.drawText(paraname,
                colsWidth * 0
                        + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 5 - rowUpBit,
                fontPaint);
        paraname = getContext().getString(R.string.total_rxLev)
                + getContext().getString(R.string.total_sub) + "(dBm)";// "RxLevBub";
        cv.drawText(paraname,
                colsWidth * 0
                        + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 6 - rowUpBit,
                fontPaint);
        paraname = getContext().getString(R.string.total_txPower) + "(dBm)";
        cv.drawText(paraname,
                colsWidth * 0
                        + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 7 - rowUpBit,
                fontPaint);
        paraname = getContext().getString(R.string.total_ta);
        cv.drawText(paraname,
                colsWidth * 0
                        + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 8 - rowUpBit,
                fontPaint);
        
        cv.save();
        cv.restore();
        return null;
    }
    
    /**
     * 创建表格数据
     * 
     * @param bm
     *            要创建表格的位图
     * @param data
     *            表格数据
     * @return 输出位图
     */
    protected Bitmap CreateTableData(Canvas cv,
            HashMap<String, TotalMeasureModel> hMap) {
        int width = this.getWidth();
        float tablewidth = width;
        int tableCols = 5; // 列数
        float colsWidth = tablewidth / tableCols; // 列宽
        float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
        
        String value;
        TotalMeasureModel data = TotalDataByGSM.getHashMapMeasure(hMap,
                TotalStruct.TotalMeasurePara._rxQualFull.name());
        value = "" + (data.getMaxValue() != -9999 ? data.getMaxValue() : "");
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 3 - rowUpBit,
                paramPaint);
        value = "" + (data.getMinValue() != -9999 ? data.getMinValue() : "");
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 3 - rowUpBit,
                paramPaint);
        value = ""
                + (data.getKeySum() == -9999 ? ""
                        : UtilsMethod.decFormat.format(data.getKeySum()
                                * 1f
                                / (data.getKeyCounts() != 0 ? data.getKeyCounts()
                                        : 1)));
        cv.drawText(value,
                colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 3 - rowUpBit,
                paramPaint);
        
        data = TotalDataByGSM.getHashMapMeasure(hMap,
                TotalStruct.TotalMeasurePara._rxQualSub.name());
        value = "" + (data.getMaxValue() != -9999 ? data.getMaxValue() : "");
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 4 - rowUpBit,
                paramPaint);
        value = "" + (data.getMinValue() != -9999 ? data.getMinValue() : "");
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 4 - rowUpBit,
                paramPaint);
        value = ""
                + (data.getKeySum() == -9999 ? ""
                        : UtilsMethod.decFormat.format(data.getKeySum()
                                * 1f
                                / (data.getKeyCounts() != 0 ? data.getKeyCounts()
                                        : 1)));
        cv.drawText(value,
                colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 4 - rowUpBit,
                paramPaint);
        
        data = TotalDataByGSM.getHashMapMeasure(hMap,
                TotalStruct.TotalMeasurePara._rxLevFull.name());
        value = "" + (data.getMaxValue() != -9999 ? data.getMaxValue() : "");
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 5 - rowUpBit,
                paramPaint);
        value = "" + (data.getMinValue() != -9999 ? data.getMinValue() : "");
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 5 - rowUpBit,
                paramPaint);
        value = ""
                + (data.getKeySum() == -9999 ? ""
                        : UtilsMethod.decFormat.format(data.getKeySum()
                                * 1f
                                / (data.getKeyCounts() != 0 ? data.getKeyCounts()
                                        : 1)));
        cv.drawText(value,
                colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 5 - rowUpBit,
                paramPaint);
        
        data = TotalDataByGSM.getHashMapMeasure(hMap,
                TotalStruct.TotalMeasurePara._rxLevSub.name());
        value = "" + (data.getMaxValue() != -9999 ? data.getMaxValue() : "");
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 6 - rowUpBit,
                paramPaint);
        value = "" + (data.getMinValue() != -9999 ? data.getMinValue() : "");
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 6 - rowUpBit,
                paramPaint);
        value = ""
                + (data.getKeySum() == -9999 ? ""
                        : UtilsMethod.decFormat.format(data.getKeySum()
                                * 1f
                                / (data.getKeyCounts() != 0 ? data.getKeyCounts()
                                        : 1)));
        cv.drawText(value,
                colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 6 - rowUpBit,
                paramPaint);
        
        data = TotalDataByGSM.getHashMapMeasure(hMap,
                TotalStruct.TotalMeasurePara._txPower.name());
        value = "" + (data.getMaxValue() != -9999 ? data.getMaxValue() : "");
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 7 - rowUpBit,
                paramPaint);
        value = "" + (data.getMinValue() != -9999 ? data.getMinValue() : "");
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 7 - rowUpBit,
                paramPaint);
        value = ""
                + (data.getKeySum() == -9999 ? ""
                        : UtilsMethod.decFormat.format(data.getKeySum()
                                * 1f
                                / (data.getKeyCounts() != 0 ? data.getKeyCounts()
                                        : 1)));
        cv.drawText(value,
                colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 7 - rowUpBit,
                paramPaint);
        
        data = TotalDataByGSM.getHashMapMeasure(hMap,
                TotalStruct.TotalMeasurePara._ta.name());
        value = "" + (data.getMaxValue() != -9999 ? data.getMaxValue() : "");
        cv.drawText(value,
                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 8 - rowUpBit,
                paramPaint);
        value = "" + (data.getMinValue() != -9999 ? data.getMinValue() : "");
        cv.drawText(value,
                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 8 - rowUpBit,
                paramPaint);
        value = ""
                + (data.getKeySum() == -9999 ? ""
                        : UtilsMethod.decFormat.format(data.getKeySum()
                                * 1f
                                / (data.getKeyCounts() != 0 ? data.getKeyCounts()
                                        : 1)));
        cv.drawText(value,
                colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
                rowHeight * 8 - rowUpBit,
                paramPaint);
        
        cv.save();
        cv.restore();
        return null;
        
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter(); // 注册一个消息过滤器
        filter.addAction(WalkMessage.TotalParaSelect);
        filter.addAction(TotalDataByGSM.TotalTaskDataChanged);
        filter.addAction(TotalDataByGSM.TotalParaDataChanged);
        filter.addAction(TotalDataByGSM.TotalResultToPicture);
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
            String action = intent.getAction();
            if (action.equals(WalkMessage.TotalParaSelect)) {
                try {
                    currentPositon = intent.getExtras().getInt("item_position");
                    invalidate();
                } catch (Exception e) {
                }
            } else if (intent.getAction()
                    .equals(TotalDataByGSM.TotalResultToPicture)) {
                String path = intent.getStringExtra(TotalDataByGSM.TotalSaveFilePath)
                        + "-Para.jpg";
                LogUtil.w(tag, "--save current to file---" + path);
                TotalParaView.this.buildDrawingCache();
                UtilsMethod.SaveBitmapToFile(TotalParaView.this.getDrawingCache(),
                        path);
            } else {
                invalidate();
            }
        }
    };
    
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewSizeLinstener.onViewSizeChange((int)(rowHeight + 0.5), w);
    };
    
}
