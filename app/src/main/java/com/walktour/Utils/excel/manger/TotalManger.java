package com.walktour.Utils.excel.manger;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DateUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.excel.ExcelUtil;
import com.walktour.Utils.excel.model.TotalExcelBean;
import com.walktour.Utils.excel.model.WorkTimeExcelBean;
import com.walktour.base.util.LogUtil;
import com.walktour.model.TotalMeasureModel;

import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成试试事件的数据分析报表。
 * 联通集团招标任务242
 * @author jinfeng.xie
 */
public class TotalManger {
    private static final String TAG = "TotalManger";
    private static TotalManger instance;
    private ExcelUtil excelUtil;
    private Context mContext;
    ArrayList<TotalExcelBean> beans=new ArrayList<>();
    /**
     * 获取设备信息单例
     * @return
     */
    public static synchronized TotalManger getInstance(Context context){
        if(instance == null){
            instance = new TotalManger(context);
        }
        return instance;
    }

    private TotalManger(Context context) {
        mContext = context;
        excelUtil = ExcelUtil.getInstance(mContext);
        LogUtil.d(TAG, "onCreate");
    }

    /*
     * 统计进去Excel*/
    public void totalIntoExcel() {
       getDatas();
       IntoExcel();
    }
    private void getDatas() {
        getUnifyTimes();
        getSpecialTimes();
        getPara();
        getMeasuePara();
        getEvent();
    }
    private void IntoExcel() {
        excelUtil.onExportTotal(beans);
    }
    /**
     * 返回业务测试次数的相关信息
     * @return
     */
    private void getUnifyTimes(){
        HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
        addBean(hMap);
    }

    /**
     *
     * [获得HTTP，PING,自定义事件等特别统计业务的统计对象]<BR>
     * [功能详细描述]
     * @return
     */
    private void getSpecialTimes(){
        Map<String, Map<String, Map<String, Long>>> hMap = TotalDataByGSM.getInstance().getSpecialTimes();
        TotalExcelBean bean =null;
        for(Map.Entry<String, Map<String, Map<String, Long>>> entry: hMap.entrySet()) {
            for(Map.Entry<String, Map<String, Long>> entryIn: entry.getValue().entrySet()) {
                for (Map.Entry<String, Long> entryIn2:entryIn.getValue().entrySet() ){
                    bean=new TotalExcelBean();
                    bean.setKey(""+entryIn2.getKey());
                    bean.setValue(""+entryIn2.getValue());
                    beans.add(bean);
                }
            }
        }
    }
    /**
     * 返回网络质量的相关参数信息
     * @return
     */
    private void getPara(){
        HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getPara();
        addBean(hMap);
    }

    /**
     * 返回网络测量参数信息
     * @return
     */
    private void getMeasuePara(){
        HashMap<String, TotalMeasureModel> hMap = TotalDataByGSM.getInstance().getMeasuePara();
        addBean(hMap);
    }

    /**
     * 返回事件统计结果
     * @return
     */
    private void getEvent(){
        HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getEvent();
        addBean(hMap);
    }

    private <T> void addBean(HashMap<String, T> hMap){
        TotalExcelBean bean =null;
        for(Map.Entry<String, T> entry: hMap.entrySet()) {
            bean=new TotalExcelBean();
            bean.setKey(""+entry.getKey());
            bean.setValue(""+entry.getValue());
            beans.add(bean);
        }
    }

}
