package com.walktour.Utils.excel.manger;

import android.app.ActivityManager;
import android.content.Context;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DateUtil;
import com.walktour.Utils.excel.ExcelUtil;
import com.walktour.Utils.excel.model.WorkTimeExcelBean;
import com.walktour.base.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持以毫秒为单位记录仪表自出厂以来的累计使用时长。
 * 联通集团招标任务240
 * @author jinfeng.xie
 * @data 2019/3/19
 */
public class CountTimeManger {
    private static final String TAG = "CountTimeManger";
    private static CountTimeManger instance;
    private ApplicationModel appModel;
    private ActivityManager mActivityManager;
    private long startTime;
    private ExcelUtil excelUtil;
    private Context mContext;
    /**
     * 获取设备信息单例
     * @return
     */
    public static synchronized CountTimeManger getInstance(Context context){
        if(instance == null){
            instance = new CountTimeManger(context);
        }
        return instance;
    }

    private CountTimeManger(Context context) {
        mContext = context;
        appModel = ApplicationModel.getInstance();
        excelUtil = ExcelUtil.getInstance(mContext);
        LogUtil.d(TAG, "onCreate");
    }

    public void setStartTime(){
        startTime = System.currentTimeMillis();
    }
    /*
     * 统计进去Excel*/
    private void countIntoExcel() {
        if (startTime==0){
            return;
        }
        long endTime = System.currentTimeMillis();
        String startDate = DateUtil.formatToStandard(startTime);
        List<WorkTimeExcelBean> dates = excelUtil.onImportWorkTime();
        if (dates == null) {
            dates = new ArrayList<>();
        }
        for (int i = 0; i < dates.size(); i++) {
            WorkTimeExcelBean bean = dates.get(i);
            if (startDate.equals(bean.getDate())) {
                long time = Long.parseLong(bean.getTime()) + endTime - startTime;
                bean.setTime("" + time);
                bean.setCount(""+(Integer.parseInt(bean.getCount())+1));
                excelUtil.onExportWorkTime(dates);
                return;
            }
        }
        WorkTimeExcelBean bean = new WorkTimeExcelBean();
        bean.setDate(startDate);
        bean.setTime("" + (endTime - startTime));
        bean.setCount(""+1);
        dates.add(bean);
        excelUtil.onExportWorkTime(dates);
    }

    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        countIntoExcel();
    }
}
