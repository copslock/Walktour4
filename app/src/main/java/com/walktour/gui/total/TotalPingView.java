package com.walktour.gui.total;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TotalPingView extends BasicTotalView {
    private static String tag = "TotalPingView";
    
    private boolean isRegisterReceiver = false;
    
    float startx = 1;
    
    float starty = 0;
    
    float stopx = 0;
    
    float stopy = 0;
    
    int tableRows = 7; // 行数
    
    int tableCols = 5; // 列数
    
    public TotalPingView(Context context) {
        super(context);
    }
    
    public TotalPingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @SuppressWarnings("unchecked")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        startx = 1;
        starty = 0;
        stopy = 0;
        stopx = this.getWidth() - 1;
        
        float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2

        canvas.drawLine(startx, 0, 0, rowHeight, linePaint);
        canvas.drawLine(this.getWidth(), 0, this.getWidth(), rowHeight, linePaint);
        canvas.drawLine(startx, 0, this.getWidth(), 0, linePaint);

        canvas.drawText(getResources().getString(R.string.total_ping_title), (this.getWidth() - fontPaint.measureText(getResources().getString(R.string.total_ping_title))) / 2, rowHeight * 1 - rowUpBit, fontPaint);

        Map<String,Map<String,Map<String,Long>>> specialTimesHM = TotalDataByGSM.getInstance().getSpecialTimes();
        Iterator<Entry<String,Map<String,Map<String,Long>>>> iter = specialTimesHM.entrySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            Map.Entry<String,Map<String,Map<String,Long>>> entry = iter.next();
            String key = entry.getKey();
            //忽略http 及 pdp 及自定义事件
            if (key.contains("GSM") || key.equals("WCDMA") || key.equals("TDSCDMA")
            		|| key.contains("LTE") || key.contains("Other") || key.contains("EVDO")
            		|| key.contains("CDMA")) {
                Map<String,Map<String,Long>> pingDataHM = entry.getValue();
                starty = (rowHeight * i * tableRows) + (i * 10) + rowHeight;
                stopy = starty + rowHeight * tableRows;
                CreateTable(canvas);
                if(key.contains("LTE")&& ApplicationModel.getInstance().isNBTest()){
                    key="NBIot";
                }
                createTableData(canvas, key, pingDataHM);
                i++;
            }
        }
        if (i == 0) {
            starty = (rowHeight * i * tableRows) + (i * 10) + rowHeight;
            stopy = starty + rowHeight * tableRows;
            CreateTable(canvas);
        }
        
        //CreateTableData(canvas, TotalDataByGSM.getInstance().getUnifyTimes());
    }
    
    /**
     * 创建表格
     * 
     * @param cv
     *            要创建表格的位图
     * @return 输出位图
     */
    protected void CreateTable(Canvas cv) {
        int width = this.getWidth();
        float tablewidth = width;
        float colsWidth = tablewidth / tableCols; // 列宽
        
        //画四边 
        cv.drawLine(startx, starty, stopx, starty, linePaint);
        cv.drawLine(startx, stopy, stopx, stopy, linePaint);
        cv.drawLine(startx, starty, startx, stopy, linePaint);
        cv.drawLine(stopx, startx, stopx, stopy, linePaint);
        // 画横线
        for (int i = 0; i < tableRows; i++) {
            cv.drawLine(startx, starty + rowHeight * i, stopx, starty
                    + rowHeight * i, linePaint);
        }
        // 画竖线
        for (int i = 1; i < tableCols - 1; i++) {
            cv.drawLine(colsWidth * (i + 1),
                    starty,
                    colsWidth * (i + 1),
                    stopy,
                    linePaint);
        }
        
        float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
        /*		String values[] = new String[]{"TBF Close","TBF Open","Total"};
        		for (int i = 0; i < values.length; i++) {
                    cv.drawText(values[i],
                            colsWidth * (i+2) + (colsWidth - paint.measureText(values[i]))
                                    / 2, rowsHeight * 1 - rowUpBit + starty, paint);
                }*/
        String values[] = new String[] {
                getContext().getString(R.string.total_attemptCount),
                getContext().getString(R.string.total_SuccessCounts),
                getContext().getString(R.string.total_SuccessRate),
                getContext().getString(R.string.total_AverageDelay),
                getContext().getString(R.string.total_MaxDelay),
                getContext().getString(R.string.total_MinDelay)};
        for (int i = 0; i < values.length; i++) {
            cv.drawText(values[i],
                    colsWidth * 0
                            + (colsWidth * 2 - fontPaint.measureText(values[i]))
                            / 2,
                    rowHeight * (i + 2) - rowUpBit + starty,
                    fontPaint);
        }
        
        cv.save();
        cv.restore();
    }
    
    /**
     * 动态创建列表数据<BR>
     * [功能详细描述]
     * @param cv
     * @param pingDataHM
     */
    @SuppressWarnings("unchecked")
    private void createTableData(Canvas cv, String network, Map<String,Map<String,Long>> pingDataHM) {
        int width = this.getWidth();
        float colsWidth = width / tableCols; // 列宽
        float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
        HashMap<String, Long> value1HM = new HashMap<String, Long>(), value2HM = new HashMap<String, Long>();
        String title[] = new String[] { "Cell DCH", "Cell FACH", "Total" };
        if ("GSM".equals(network)) {
            title = new String[] { "TBF Close", "TBF Open", "Total" };
        } else if ("WCDMA".equals(network)) {
            title = new String[] { "Cell DCH", "Cell FACH", "Total" };
        }
        if (pingDataHM != null) {
            Iterator<Entry<String,Map<String,Long>>> iter = pingDataHM.entrySet().iterator();
            int k = 0;
            while (iter.hasNext() && k < 2) {
                Map.Entry<String,Map<String,Long>> entry = iter.next();
                String key = entry.getKey();
                if (k == 0) {
                    value1HM = (HashMap<String, Long>) entry.getValue();
                    title[0] = key;
                } else {
                    value2HM = (HashMap<String, Long>) entry.getValue();
                    title[1] = key;
                }
                k++;
            }
        }
        for (int i = 0; i < title.length; i++) {
            cv.drawText(title[i],
                    colsWidth * (i + 2) + (colsWidth - fontPaint.measureText(title[i])) / 2,
                    rowHeight * 1 - rowUpBit + starty,
                    fontPaint);
        }
        String datas[] = new String[]{
                TotalDataByGSM.getHashMapValue(value1HM, TotalStruct.TotalAppreciation._pingTry.name()),
                TotalDataByGSM.getHashMapValue(value2HM, TotalStruct.TotalAppreciation._pingTry.name()),
                String.valueOf(TotalDataByGSM.getHashMapValueSum(value1HM, value2HM, TotalStruct.TotalAppreciation._pingTry.name())),
                TotalDataByGSM.getHashMapValue(value1HM, TotalStruct.TotalAppreciation._pingSuccess.name()),
                TotalDataByGSM.getHashMapValue(value2HM, TotalStruct.TotalAppreciation._pingSuccess.name()),
                String.valueOf(TotalDataByGSM.getHashMapValueSum(value1HM, value2HM, TotalStruct.TotalAppreciation._pingSuccess.name())),
                TotalDataByGSM.getHashMapMultiple(value1HM, TotalStruct.TotalAppreciation._pingSuccess.name(), TotalStruct.TotalAppreciation._pingTry.name(), 100, "%"),
                TotalDataByGSM.getHashMapMultiple(value2HM, TotalStruct.TotalAppreciation._pingSuccess.name(), TotalStruct.TotalAppreciation._pingTry.name(), 100, "%"),
                String.valueOf(TotalDataByGSM.getHashMapMultipleSum(value1HM, value2HM, TotalStruct.TotalAppreciation._pingSuccess.name(), TotalStruct.TotalAppreciation._pingTry.name(), 100, "%")),
                TotalDataByGSM.getHashMapMultiple(value1HM, TotalStruct.TotalAppreciation._pingDelay.name(), TotalStruct.TotalAppreciation._pingSuccess.name(), 1, ""),
                TotalDataByGSM.getHashMapMultiple(value2HM, TotalStruct.TotalAppreciation._pingDelay.name(), TotalStruct.TotalAppreciation._pingSuccess.name(), 1, ""),
                TotalDataByGSM.getHashMapMultipleSum(value1HM, value2HM, TotalStruct.TotalAppreciation._pingDelay.name(), TotalStruct.TotalAppreciation._pingSuccess.name(), 1, ""),
                TotalDataByGSM.getHashMapValue(value1HM, TotalStruct.TotalAppreciation._pingDelayMax.name()),
                TotalDataByGSM.getHashMapValue(value2HM, TotalStruct.TotalAppreciation._pingDelayMax.name()),
                "-",
                TotalDataByGSM.getHashMapValue(value1HM, TotalStruct.TotalAppreciation._pingDelayMin.name()),
                TotalDataByGSM.getHashMapValue(value2HM, TotalStruct.TotalAppreciation._pingDelayMin.name()),
                "-"
        };
        cv.drawText(network,
                colsWidth * 0 + (colsWidth * 2 - paramPaint.measureText(network))
                        / 2,
                rowHeight * 1 - rowUpBit + starty,
                paramPaint);
        
        for (int i = 0, j = 2; i < datas.length; i++) {
            cv.drawText(datas[i], colsWidth * (i - (j - 2) * 3 + 2)
                    + (colsWidth - paramPaint.measureText(datas[i])) / 2, rowHeight
                    * j - rowUpBit + starty, paramPaint);
            if ((i + 1) % 3 == 0) {
                j++;
            }
        }
        
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter(); // 注册一个消息过滤器
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
    
    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }
    
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int height = 0;
    	 Map<String, Map<String,Map<String,Long>>> specialTimesHM = TotalDataByGSM.getInstance().getSpecialTimes();
         Iterator<Entry<String, Map<String,Map<String,Long>>>> iter = specialTimesHM.entrySet().iterator();
         int i = 0;
         while (iter.hasNext()) {
        	 Map.Entry<String, Map<String,Map<String,Long>>> entry = iter.next();
             String key = entry.getKey();
             if (key.contains("GSM") || key.equals("WCDMA") || key.equals("TDSCDMA")
             		|| key.contains("LTE") || key.contains("Other") || key.contains("EVDO")
             		|| key.contains("CDMA")) {
            	 i++;
             }
         }
         if (i != 0) {
             starty = (rowHeight * i * tableRows) + (i * 10) + rowHeight;
             height = (int)(starty + rowHeight * tableRows);
         }
    	if(height == 0){
    		height = (int) (rowHeight * tableRows) + 1;
    	}
    	
        setMeasuredDimension(measureWidth(widthMeasureSpec), height);
    }
    
    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        
        int result = 500;
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        }
        else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        
        return result;
    }
    
    
    
    /**
     * 消息处理
     */
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TotalDataByGSM.TotalResultToPicture)) {
                String path = intent.getStringExtra(TotalDataByGSM.TotalSaveFilePath)
                        + "-Ping.jpg";
                LogUtil.w(tag, "--save current to file---" + path);
                TotalPingView.this.buildDrawingCache();
                UtilsMethod.SaveBitmapToFile(TotalPingView.this.getDrawingCache(),
                        path);
            } else {
                invalidate();
            }
        }
    };

}
