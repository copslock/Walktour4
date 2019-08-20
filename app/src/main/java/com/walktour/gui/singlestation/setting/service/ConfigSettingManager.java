package com.walktour.gui.singlestation.setting.service;


import android.content.Context;
import android.util.Xml;

import com.walktour.Utils.TotalStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.setting.model.SettingModel;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luojun on 2017/7/3.
 * 当前使用XML配置形式,后续改为数据库进行配置
 */
public class ConfigSettingManager {
    /**
     * 日志标识
     */
    private static final String TAG = "ConfigSetting";
    /**
     * 缓存从XML中读取到的列表
     */
    private List<SettingModel> mSettingModelList = new ArrayList<>();
    /**
     * 唯一实例
     */
    private static ConfigSettingManager sInstance;

    private ConfigSettingManager(Context context) {
        try {
            readXmlFile(context.getAssets().open("config/config_singlestation_threshold_setting.xml"));
        } catch (Exception e) {
            LogUtil.e(TAG, TAG + "-- Exception --" + e.toString());
        }
    }

    /**
     * 返回唯一实例
     *
     * @param context 上下文
     * @return 唯一实例
     */
    public static ConfigSettingManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ConfigSettingManager(context);
        }
        return sInstance;
    }

    /**
     * 解析xml文件
     *
     * @param xml xml文件输入流
     * @throws Exception 异常
     */
    private void readXmlFile(InputStream xml) throws Exception {
        if (null == xml)
            return;

        XmlPullParser pullParser = Xml.newPullParser();

        pullParser.setInput(xml, "UTF-8");

        int stationType = SingleStationDaoManager.STATION_TYPE_INDOOR;
        int sceneType = SingleStationDaoManager.SCENE_TYPE_COVERAGE;
        String strAttValue;
        int event = pullParser.getEventType();

        LogUtil.e(TAG, TAG + "-- Event" + event);
        while (event != XmlPullParser.END_DOCUMENT) {
            String nodeName = pullParser.getName();
//            LogUtil.e(TAG, TAG + "-- getName -- " + nodeName);
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG: {
                    switch (nodeName) {
                        case "Type": {
                            strAttValue = pullParser.getAttributeValue(0);
                            switch (strAttValue) {
                                case "indoor":
                                    stationType = SingleStationDaoManager.STATION_TYPE_INDOOR;
                                    break;
                                case "outdoor":
                                    stationType = SingleStationDaoManager.STATION_TYPE_OUTDOOR;
                                    break;
                            }
                            break;
                        }
                        case "Scene": {
                            strAttValue = pullParser.getAttributeValue(0);
                            switch (strAttValue) {
                                case "handover_gate":
                                    sceneType = SingleStationDaoManager.SCENE_TYPE_HANDOVER;
                                    break;
                                case "handover_indoor":
                                    sceneType = SingleStationDaoManager.SCENE_TYPE_HANDOVER;
                                    break;
                                case "handover_park":
                                    sceneType = SingleStationDaoManager.SCENE_TYPE_HANDOVER;
                                    break;
                                case "leakage":
                                    sceneType = SingleStationDaoManager.SCENE_TYPE_SIGNAL_LEAKAGE;
                                    break;
                                case "performance":
                                    sceneType = SingleStationDaoManager.SCENE_TYPE_PERFORMANCE;
                                    break;
                               /* case "park":
                                    sceneType = SingleStationDaoManager.SCENE_TYPE_PARK;
                                    break;*/
                                default:
                                    sceneType = SingleStationDaoManager.SCENE_TYPE_COVERAGE;
                                    break;
                            }
                            break;
                        }
                        case "Threshold": {
                            SettingModel settingModel = new SettingModel();
                            settingModel.setStationType(stationType);
                            settingModel.setSceneType(sceneType);
                            for (int iLp = 0; iLp < pullParser.getAttributeCount(); iLp++) {
                                String strAttName = pullParser.getAttributeName(iLp);
                                strAttValue = pullParser.getAttributeValue(iLp);

                                switch (strAttName) {
                                    case "Task":
                                        settingModel.setTestTask(strAttValue);
                                        break;
                                    case "KPI":
                                        settingModel.setThresholdKey(this.getThresholdKey(strAttValue));
                                        break;
                                    case "operator":
                                        settingModel.setOperator(strAttValue);
                                        break;
                                    case "value":
                                        settingModel.setThresholdValue(Float.parseFloat(strAttValue));
                                        break;
                                    case "unit":
                                        settingModel.setThresholdUnit(strAttValue);
                                        break;
                                }
                            }
                            mSettingModelList.add(settingModel);
                        }
                        break;
                    }
                }
                break;
                case XmlPullParser.END_TAG:
                    switch (nodeName) {
                        case "Type":
                            stationType = SingleStationDaoManager.STATION_TYPE_INDOOR;
                            break;
                        case "Scene":
                            sceneType = SingleStationDaoManager.SCENE_TYPE_COVERAGE;
                            break;
                    }

                    break;
            }
            event = pullParser.next(); //下一个标签
        }
    }

    /**
     * @return 获取从xml中读取到的节点信息
     */
    public List<SettingModel> getSettingModelList() {
        return this.mSettingModelList;
    }

    /**
     * 获取实际的阈值key
     *
     * @param threshold 配置文件的阈值key
     * @return 实际的阈值key
     */
    private String getThresholdKey(String threshold) {
        String thresholdKey;
        switch (threshold) {
            case "RS Coverage Ratio":
                thresholdKey = TotalStruct.TotalSingleStation._RSCoverMileage.name();
                break;
            case "Attempt Handover Times":
                thresholdKey = TotalStruct.TotalSingleStation._FTPAttemptHandoverTimes.name();
                break;
            case "Handover Success Ratio":
                thresholdKey = TotalStruct.TotalSingleStation._FTPHandoverSuccessRate.name();
                break;
            case "PCI Sample":
                thresholdKey = TotalStruct.TotalSingleStation._PCISample.name();
                break;
            case "RSRP":
                thresholdKey = TotalStruct.TotalSingleStation._RSRPAverage.name();
                break;
            case "Attempt Times":
                thresholdKey = TotalStruct.TotalSingleStation._tryTimes.name();
                break;
            case "Success Ratio":
                thresholdKey = TotalStruct.TotalSingleStation._successRate.name();
                break;
            case "Average Rate":
                thresholdKey = TotalStruct.TotalSingleStation._SpeedAverage.name();
                break;
            case "Attampt Call Times(CSFB)":
                thresholdKey = TotalStruct.TotalSingleStation._csfb_tryTimes.name();
                break;
            case "Call Sucess Ratio(CSFB)":
                thresholdKey = TotalStruct.TotalSingleStation._csfb_successRate.name();
                break;
            case "Handover Success Ratio(eSRVCC)":
                thresholdKey = TotalStruct.TotalSingleStation._volte_eSRVCC.name();
                break;
            case "Call Sucess Ratio(Volte)":
                thresholdKey = TotalStruct.TotalSingleStation._volte_successRate.name();
                break;
            case "Call Established Sucess Ratio(CSFB)":
                thresholdKey = TotalStruct.TotalSingleStation._csfb_establishedRate.name();
                break;
            case "Handover Success Times(eSRVCC)":
                thresholdKey = TotalStruct.TotalSingleStation._volte_eSRVCC.name();
                break;
            case "Call Return Delay(CSFB)":
                thresholdKey = TotalStruct.TotalSingleStation._csfb_returnDelay.name();
                break;
            case "Call Connect Delay(CSFB)":
                thresholdKey = TotalStruct.TotalSingleStation._csfb_connectDelay.name();
                break;
            case "Call Return Success Ratio(CSFB)":
                thresholdKey = TotalStruct.TotalSingleStation._csfb_returnSuccessRate.name();
                break;
            default:
                thresholdKey = "";
                break;
        }
        return thresholdKey;
    }
}
