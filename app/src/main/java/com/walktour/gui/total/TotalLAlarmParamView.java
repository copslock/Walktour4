package com.walktour.gui.total;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.model.AlarmModel;

import java.util.HashMap;
import java.util.List;

public class TotalLAlarmParamView extends BasicTotalView {
    private static String tag = "TotalLAlarmParamView";
    private boolean isRegisterReceiver = false;
    DisplayMetrics metric = new DisplayMetrics();
    NewMapFactory mapFactory;
    Context context;
    private HashMap<String, Integer> tableParms;

    public TotalLAlarmParamView(Context context) {
        super(context);

        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(metric);
        mapFactory = NewMapFactory.getInstance();
        this.context = context;
    }

    public TotalLAlarmParamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDrawingCacheEnabled(false);
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(metric);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        CreateTable(canvas);
        CreateTableData(canvas);
    }

    /**
     * 创建表格
     *
     * @return 输出位图
     */
    protected Bitmap CreateTable(Canvas cv) {
        //初始化数据，HashMap
        List<AlarmModel> alarmList = mapFactory.getAlarmList();
        tableParms = new HashMap<>();
        for (int i = 0; i < alarmList.size(); i++) {
            if (tableParms.containsKey(alarmList.get(i).getDescription(context))) {//如果有，总数则加1
                int totalNum = tableParms.get(alarmList.get(i).getDescription(context));
                totalNum++;
                tableParms.put(alarmList.get(i).getDescription(context), totalNum);
            } else {
                tableParms.put(alarmList.get(i).getDescription(context), 1);//如果没含有，总数则为1
            }
        }

        int width = this.getWidth();
        float startx = 1;
        float starty = 0;
        float stopx = 0;
        float stopy = 0;
        float tablewidth = width;
        int tableRows = tableParms.size()+2; // 行数
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
        paraname = "自定义事件统计";
        cv.drawText(paraname, (colsWidth * 3 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 1 - rowUpBit, fontPaint);

        /* 第二行第一个 */
        paraname = getResources().getString(R.string.alarm_name);// 名称
        cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 2 - rowUpBit, fontPaint);
        /* 第二行第二个 */
        paraname = getResources().getString(R.string.table_alarm_number);// 总数
        cv.drawText(paraname,
                colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2,
                rowHeight * 2 - rowUpBit, fontPaint);

        cv.save();
        cv.restore();
        return null;
    }

    /**
     * 创建表格数据
     *
     * @return 输出位图
     */
    protected Bitmap CreateTableData(Canvas cv) {
        int width = this.getWidth();
        float tablewidth = width;
        int tableCols = 3; // 列数
        float colsWidth = tablewidth / tableCols; // 列宽
        float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2


        int row=3;//第几行
        for (String key : tableParms.keySet()) {
            String value=""+tableParms.get(key);
            /* 名称 */
            cv.drawText(key, colsWidth * 0 + (colsWidth * 2 - paramPaint.measureText(key)) / 2,
                    rowHeight * row - rowUpBit, paramPaint);
            /* 总数 */
            cv.drawText(value,
                    colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
                    rowHeight * row - rowUpBit, paramPaint);
            row++;
        }


        cv.save();
        cv.restore();
        return null;

    }

    /**
     * 处理无效值情况<BR>
     * [功能详细描述]
     *
     * @param value
     * @return
     */
    private String handlesInvalid(long value) {
        if (value != -9999) {
            return String.valueOf(value);
        }
        return "";
    }


    /**
     * 处理平均值<BR>
     * [功能详细描述]
     *
     * @param sum
     * @param count
     * @return
     */
    private String processAverage(long sum, long count) {
        return sum == -9999 ? "" : UtilsMethod.decFormat
                .format(sum
                        * 1f
                        / (count != 0 ? count : 1));
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
        } catch (IllegalArgumentException e) {
            LogUtil.w("IllegalArgumentException:", e.toString());
        }
    }

    /**
     * 消息处理
     */
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(
                    TotalDataByGSM.TotalResultToPicture)) {
                String path = intent
                        .getStringExtra(TotalDataByGSM.TotalSaveFilePath)
                        + "-Para.jpg";
                LogUtil.w(tag, "--save current to file---" + path);
                TotalLAlarmParamView.this.buildDrawingCache();
                UtilsMethod.SaveBitmapToFile(
                        TotalLAlarmParamView.this.getDrawingCache(), path);
            } else {
                invalidate();
            }
        }
    };

}
