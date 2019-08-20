package com.walktour.gui.singlestation.report.service;

import android.content.Context;

import com.walktour.Utils.DateUtils;
import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.CellInfo;
import com.walktour.gui.singlestation.dao.model.SceneInfo;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.StationInfoReport;
import com.walktour.gui.singlestation.dao.model.SurveyCellInfo;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;
import com.walktour.gui.singlestation.dao.model.TaskTestResult;
import com.walktour.gui.singlestation.dao.model.ThresholdTestResult;
import com.walktour.gui.singlestation.net.SingleStationRetrofitManager;
import com.walktour.gui.singlestation.report.model.StationInfoCallBack;
import com.walktour.gui.singlestation.report.model.StationInfoReportDownCallBack;
import com.walktour.report.utils.excel.ExcelUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/***
 * 已测试基站服务类
 */
public class TestedStationService {
    /**
     * 日志标识
     */
    private static final String TAG = "TestedStationService";
    /**
     * 数据库操作类
     */
    private SingleStationDaoManager mDaoManager;
    private AppFilePathUtil appFilePathUtil = AppFilePathUtil.getInstance();
    private Context context;

    public TestedStationService(Context context) {
        this.context = context;
        this.mDaoManager = SingleStationDaoManager.getInstance(context);
    }

    /***
     * 导出本地报表
     * @param context 上下文
     * @param stationInfoList 要导出的基站列表
     * @param callBack 回调类
     */
    public void exportLocalReport(Context context, List<StationInfo> stationInfoList, StationInfoCallBack callBack) {
        LogUtil.d(TAG, "----exportLocalReport----");
        //报表路径
        String sdcard_path = appFilePathUtil.getSDCardBaseDirectory(context.getString(R.string.path_singlestation),
                context.getString(R.string.path_singlestation_template_report));
        //模板路径
        String templatePath;
        String fileName;
        for (StationInfo stationInfo : stationInfoList) {
            Map<String, String> datas = this.fetcheExcelKeyValue(context, stationInfo);
            if (stationInfo.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR) {
                templatePath = sdcard_path + context.getString(R.string.path_singlestation_template) + "/cmcc_indoor"
                        + ".xls";
                fileName = sdcard_path + context.getString(R.string.path_singlestation_template_report_indoor) + "/"
                        + stationInfo.getName() + "_" + stationInfo.getAddress() + "_" + stationInfo.getDeviceType()
                        + DateUtils.getDateTimeToString() + "_indoor.xls";
            } else {
                templatePath = sdcard_path + context.getString(R.string.path_singlestation_template) +
                        "/cmcc_outdoor" + ".xls";
                fileName = sdcard_path + context.getString(R.string.path_singlestation_template_report_outdoor) + "/"
                        + stationInfo.getName() + "_" + stationInfo.getAddress() + "_" + stationInfo.getDeviceType()
                        + DateUtils.getDateTimeToString() + "_outdoor.xls";
            }
            ExcelUtils.replaceModel(datas, templatePath, fileName);
            stationInfo.setIsExportedReport(StationInfo.EXPORTED_REPORT_YES);
            this.mDaoManager.save(stationInfo);
            StationInfoReport report = new StationInfoReport();
            report.setStationId(stationInfo.getId());
            report.setType(StationInfoReport.TYPE_LOCAL);
            report.setReportPath(fileName);
            this.mDaoManager.save(report);
        }
//        this.fetchTestedStationInfo(callBack);
    }

    /***
     * 导出远程数据报表
     * @param context 上下文
     * @param stationInfoList 要导出的基站列表
     * @param callBack 回调类
     */
    public void exportRemoteReport(Context context, String ip, int port, List<StationInfo> stationInfoList, final
    StationInfoReportDownCallBack callBack) {
        //报表路径
        String sdcard_path = appFilePathUtil.getSDCardBaseDirectory(context.getString(R.string.path_singlestation),
                context.getString(R.string.path_singlestation_template_report));
        String fileName;
        boolean flag = false;
        for (final StationInfo stationInfo : stationInfoList) {
            fileName = "";
            if (stationInfo.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR) {
                fileName = sdcard_path + context.getString(R.string.path_singlestation_template_report_indoor) + File
                        .separator + "indoor_";
            } else {
                fileName = sdcard_path + context.getString(R.string.path_singlestation_template_report_outdoor) +
                        File.separator + "outdoor_";
            }
            fileName += stationInfo.getCode() + "_" + stationInfo.getSiteId() + "_" + DateUtils.getDateTimeToString()
                    + ".xls";
            final String fileNamex = fileName;
            SingleStationRetrofitManager retrofitManager = SingleStationRetrofitManager.getInstance(ip, port);
            retrofitManager.getStationReport(context, stationInfo.getSiteId() + "", fileName, new SimpleCallBack() {
                @Override
                public void onSuccess() {
                    stationInfo.setIsExportedReport(StationInfo.EXPORTED_REPORT_YES);
                    mDaoManager.save(stationInfo);
                    StationInfoReport report = new StationInfoReport();
                    report.setStationId(stationInfo.getId());
                    report.setType(StationInfoReport.TYPE_REMOTE);
                    report.setReportPath(fileNamex);
                    mDaoManager.save(report);
                    callBack.onSuccess("");
                }

                @Override
                public void onFailure(String message) {
                    callBack.onFailure(message);
                }
            });
        }
    }

    /***
     * 获取已测试基站信息,屏蔽本地基站
     * @param callBack 回调类
     */
    public void fetchTestedStationInfo(StationInfoCallBack callBack) {
        List<StationInfo> list = this.mDaoManager.getStationInfoList(StationInfo.FROM_TYPE_PLATFORM);
        Iterator<StationInfo> it = list.iterator();
        while (it.hasNext()) {
            StationInfo info = it.next();
//        保留测试失败和测试成功的基站,表明已经测试完成
            if (info.getTestStatus() == StationInfo.TEST_STATUS_FAULT || info.getTestStatus() == StationInfo
                    .TEST_STATUS_SUCCESS) {
                continue;
            } else {
                it.remove();
            }
        }
        callBack.onSuccess(list);
    }

    /**
     * 获取当前基站中所有的信息,以key-value的形式保存
     *
     * @param stationInfo 基站信息
     * @return 基站信息
     */
    private Map<String, String> fetcheExcelKeyValue(Context context, StationInfo stationInfo) {
        Map<String, String> datas = new HashMap<>();
        switch (stationInfo.getType()) {
            case SingleStationDaoManager.STATION_TYPE_INDOOR://室内
                initIndoorData(context, stationInfo, datas);
                break;
            case SingleStationDaoManager.STATION_TYPE_OUTDOOR://室外
                initOutdoorData(context, stationInfo, datas);
                break;
        }
        return datas;
    }

    /**
     * 初始化室内基站的数据
     */
    private void initIndoorData(Context context, StationInfo stationInfo, Map<String, String> datas) {
        //勘察基站信息表
        SurveyStationInfo surveyStationInfo = mDaoManager.getSurveyStationInfo(stationInfo.getId());
        List<SurveyCellInfo> surveyCellInfos = surveyStationInfo.getCellInfoList();
        //报告页
        // -------------------------------------
        //基站描述
        datas.put("#DL_SiteName", stationInfo.getName() + "");//基站名称
        datas.put("#DL_celldateSurveyDate", surveyStationInfo.getTestDate() + "");//测试日期
        datas.put("#DL_SiteId", stationInfo.getCode() + "");//
        datas.put("#DL_siteplan-city", surveyStationInfo.getCity() + "");
        datas.put("#DL_Address", surveyStationInfo.getAddress() + "");
        datas.put("#DL_siteplan-AdminRegion", surveyStationInfo.getDistrict() + "");
        datas.put("#DL_siteplan-DeviceType", surveyStationInfo.getDeviceType() + "");
        datas.put("#DL_siteplan-SiteType", surveyStationInfo.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR
                ? context.getString(R.string.single_station_type_indoor) : context.getString(R.string
                .single_station_type_outdoor));
        datas.put("#DL_siteplan-Scope", "");
        //基站参数工程
        datas.put("#DL_Longitude", stationInfo.getLongitude() + "");
        datas.put("#DL_Longitude_test", surveyStationInfo.getLongitude() + "");
        datas.put("#DL_Latitude", stationInfo.getLatitude() + "");
        datas.put("#DL_Latitude_test", surveyStationInfo.getLatitude() + "");
        datas.put("#DL_TACenodebid", stationInfo.getTAC() + "");
        datas.put("#DL_TACenodebid_test", surveyStationInfo.getTAC() + "");
        datas.put("#DL_TACenodebid_test_result", getResult(stationInfo.getTAC(), surveyStationInfo.getTAC()));
        datas.put("#DL_eNodeBID", stationInfo.getENodeBID() + "");
        datas.put("#DL_eNodeBID_test", surveyStationInfo.getENodeBID() + "");
        datas.put("#DL_TACenodebid_test_result", getResult(stationInfo.getENodeBID(), surveyStationInfo.getENodeBID()));
        //小区参数工程
        for (int i = 0; i < surveyCellInfos.size(); i++) {
            if (i == 0) {//只会存在一个小区
                SurveyCellInfo surveyCellInfo = surveyCellInfos.get(i);
                CellInfo cellInfo = null;
                for (CellInfo cellInfox : stationInfo.getCellInfoList()) {
                    if (surveyCellInfo.getCellId() == cellInfox.getCellId()) {
                        cellInfo = cellInfox;
                        break;
                    }
                }
                if (null != cellInfo) {
                    //小区名1
                    datas.put("#DL_CellPlan0", cellInfo.getCarrierSetup() + "");
                    datas.put("#DL_CellTest0_CarrierCount", surveyCellInfo.getCarrierSetup() + "");
                    datas.put("#DL_CellTest0_CarrierCount_result", getResult(cellInfo.getCarrierSetup(),
                            surveyCellInfo.getCarrierSetup()));//1
                    datas.put("#DL_CellID0", cellInfo.getCellId() + "");
                    datas.put("#DL_CellID0_test", surveyCellInfo.getCellId() + "");
                    datas.put("#DL_CellID0_test_result", getResult(cellInfo.getCellId(), surveyCellInfo.getCellId()))
                    ;//2
                    datas.put("#DL_PCI0", cellInfo.getPCI() + "");
                    datas.put("#DL_PCI0_test", surveyCellInfo.getPCI() + "");
                    datas.put("#DL_PCI0_test_result", getResult(cellInfo.getPCI(), surveyCellInfo.getPCI()));//3
                    datas.put("#DL_band0", cellInfo.getBand() + "");
                    datas.put("#DL_band0_test", surveyCellInfo.getBand() + "");
                    datas.put("#DL_band0_test_result", getResult(cellInfo.getBand(), surveyCellInfo.getBand()));//4
                    datas.put("#DL_point0", cellInfo.getFrequency() + "");
                    datas.put("#DL_point0_test", surveyCellInfo.getFrequency() + "");
                    datas.put("#DL_point0_test_result", getResult(cellInfo.getFrequency(), surveyCellInfo
                            .getFrequency()));//5
                    datas.put("#DL_Bandwidth0", cellInfo.getBandwidth() + "");
                    datas.put("#DL_Bandwidth0_test", surveyCellInfo.getBandwidth() + "");
                    datas.put("#DL_Bandwidth0_test_result", getResult(cellInfo.getBandwidth(), surveyCellInfo
                            .getBandwidth()));//6
                    datas.put("#DL_ZCRootSequence0", cellInfo.getRootSequence() + "");
                    datas.put("#DL_ZCRootSequence0_test", surveyCellInfo.getRootSequence() + "");
                    datas.put("#DL_ZCRootSequence0_test_result", getResult(cellInfo.getRootSequence(), surveyCellInfo
                            .getRootSequence()));//7
                    datas.put("#DL_Subframeratio0", cellInfo.getSubframeMatching() + "");
                    datas.put("#DL_Subframeratio0_test", surveyCellInfo.getSubframeMatching() + "");
                    datas.put("#DL_Subframeratio0_test_result", getResult(cellInfo.getSubframeMatching(),
                            surveyCellInfo.getSubframeMatching()));//8
                    datas.put("#DL_SpecialSubframeRatio0", cellInfo.getSpecialSubframeMatching() + "");
                    datas.put("#DL_SpecialSubframeRatio0_test", surveyCellInfo.getSpecialSubframeMatching() + "");
                    datas.put("#DL_SpecialSubframeRatio0_test_result", getResult(cellInfo.getSpecialSubframeMatching
                            (), surveyCellInfo.getSpecialSubframeMatching()));//9
                }
            }
        }
        //网络性能验收
        //头部
        datas.put("#DL_SiteName0", stationInfo.getName() + "");
        datas.put("#DL_SiteId0", stationInfo.getCode() + "");
        datas.put("#DL_celldateSurveyDate0", surveyStationInfo.getTestDate() + "");
        datas.put("#DL_celldateSurveyer0", surveyStationInfo.getTester() + "");
        datas.put("#DL_DeviceMode0", surveyStationInfo.getDeviceType() + "");
        datas.put("#DL_PhoneNumber0 ", surveyStationInfo.getTestPhone() + "");
        datas.put("#DL_Platform0", surveyStationInfo.getTestPlatform() + "");
        // -------------------------------------
        List<SceneInfo> scenes = mDaoManager.getSceneInfoList(stationInfo.getId(), SingleStationDaoManager
                .SCENE_TYPE_PERFORMANCE);
        for (int i = 0; i < scenes.size(); i++) {
            if (i == 0) {//小区1
                datas.put("#DL_cover0", "");
                datas.put("#DL_RSRPavg0", "");
                datas.put("#DL_SINRavg0", "");
                datas.put("#DL_SC/DC0", "");
                datas.put("#DL_FTPULavg0", "");
                datas.put("#DL_FTPDLavg0", "");
                datas.put("#DL_CSFB0_ConnectS", "");
                datas.put("#DL_CSFB0_ReturnD", "");
                datas.put("#DL_CSFB0_ConnectD", "");
                datas.put("#DL_CSFB0_ReturnS", "");
                datas.put("#DL_HandoverSuccessRate0", "");
                datas.put("#DL_VoLTESuccess0", "");
                try {
                    List<TaskTestResult> taskTestResultList = mDaoManager.getTaskTestResultList(scenes.get(i).getId());
                    for (TaskTestResult taskTestResult : taskTestResultList) {
                        datas.put("#DL_RSRPavg0", taskTestResult.getRsrpAverage() + "");
                        datas.put("#DL_SINRavg0", taskTestResult.getSinrAverage() + "");
                        if (taskTestResult.getTaskType().equals("MOC_CSFB")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_csfb_establishedRate")) {//CSFB接通率
                                    datas.put("#DL_CSFB0_ConnectS", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                                if (thresholdTestResult.getThresholdKey().equals("_csfb_returnDelay")) {//返回4G时延
                                    datas.put("#DL_CSFB0_ReturnD", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                                if (thresholdTestResult.getThresholdKey().equals("_csfb_connectDelay")) {//CSFB接通时延
                                    datas.put("#DL_CSFB0_ConnectD", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                                if (thresholdTestResult.getThresholdKey().equals("_csfb_returnSuccessRate")) {//返回4G成功率
                                    datas.put("#DL_CSFB0_ReturnS", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                            }
                        } else if (taskTestResult.getTaskType().equals("MOC_VOLTE")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_volte_successRate")) {//VOLTE语音全程呼叫成功率
                                    datas.put("#DL_VoLTESuccess0", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                                if (thresholdTestResult.getThresholdKey().equals("_volte_eSRVCC")) {//切换成功率(%)
                                    datas.put("#DL_HandoverSuccessRate0", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                            }
                        } else if (taskTestResult.getTaskType().equals("FTP_Upload")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_SpeedAverage")) {//平均上载速率(Mbps)
                                    datas.put("#DL_FTPULavg0", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                            }
                        } else if (taskTestResult.getTaskType().equals("FTP_Download")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_SpeedAverage")) {//平均下载速率(Mbps)
                                    datas.put("#DL_FTPDLavg0", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                            }
                        } else if (taskTestResult.getTaskType().equals("FTP_Download")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_FTPCoverMileage")) {//覆盖率（RSRP≥-105dBm且 RS-SINR ≥6dB)
                                    datas.put("#DL_cover0", thresholdTestResult.getRealValue() + thresholdTestResult
                                            .getThresholdUnit());
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    LogUtil.w(TAG, ex.getMessage());
                }
            }
        }
        //设计方案
        // -------------------------------------
        datas.put("#DL_DESIGNSKETCH", "");
        //室内小区切换测试
        // -------------------------------------
        datas.put("#DL_PCCPCHRSRP", "");
        datas.put("#DL_PCI", "");
        datas.put("#DL_handoverT", "");
        datas.put("#DL_handoverST", "");
        datas.put("#DL_SuccessRate", "");
        //外泄测试
        // -------------------------------------
        //小区1
        datas.put("#DL_EscapePicture0", "");
        datas.put("#DL_EscapeOutdoor0", "");
        datas.put("#DL_EscapeOutdoor0_test", "");
        datas.put("#DL_EscapeIndoor0", "");
        datas.put("#DL_EscapeIndoor0_test", "");
        datas.put("#DL_EscapeDifference0", "");
        datas.put("#DL_EscapeDifference0_test", "");
        datas.put("#DL_EscapeDist0", "");
        datas.put("#DL_EscapeDist0_test", "");
        datas.put("#DL_Escape≤-110Ratio0", "");
        datas.put("#DL_Escape≤-110Ratio0_test", "");
        datas.put("#DL_Escape>10Ratio0", "");
        datas.put("#DL_Escape>10Ratio0_test", "");
        //小区2
        datas.put("#DL_EscapePicture1", "");
        datas.put("#DL_EscapeOutdoor1", "");
        datas.put("#DL_EscapeOutdoor1_test", "");
        datas.put("#DL_EscapeIndoor1", "");
        datas.put("#DL_EscapeIndoor1_test", "");
        datas.put("#DL_EscapeDifference1", "");
        datas.put("#DL_EscapeDifference1_test", "");
        datas.put("#DL_EscapeDist1", "");
        datas.put("#DL_EscapeDist1_test", "");
        datas.put("#DL_Escape≤-110Ratio1", "");
        datas.put("#DL_Escape≤-110Ratio1_test", "");
        datas.put("#DL_Escape>10Ratio1", "");
        datas.put("#DL_Escape>10Ratio1_test", "");
        //小区3
        datas.put("#DL_EscapePicture2", "");
        datas.put("#DL_EscapeOutdoor2", "");
        datas.put("#DL_EscapeOutdoor2_test", "");
        datas.put("#DL_EscapeIndoor2", "");
        datas.put("#DL_EscapeIndoor2_test", "");
        datas.put("#DL_EscapeDifference2", "");
        datas.put("#DL_EscapeDifference2_test", "");
        datas.put("#DL_EscapeDist2", "");
        datas.put("#DL_EscapeDist2_test", "");
        datas.put("#DL_Escape≤-110Ratio2", "");
        datas.put("#DL_Escape≤-110Ratio2_test", "");
        datas.put("#DL_Escape>10Ratio2", "");
        datas.put("#DL_Escape>10Ratio2_test", "");
        //一楼室内外进出口切换测试(新增VOLTE测试）
        // -------------------------------------
        datas.put("#DL_VoLTERSRPPicture1", "");
        datas.put("#DL_VoLTEPCIPicture1", "");
        datas.put("#DL_VoLTERSRPPicture2", "");
        datas.put("#DL_VoLTEPCIPicture2", "");
        //地下停车场进出口切换测试(新增VOLTE测试）
        // -------------------------------------
        datas.put("#DL_cellarRSRPPicture1", "");
        datas.put("#DL_cellarPCIPicture1", "");
        datas.put("#DL_cellarVoLTERSRPPicture1", "");
        datas.put("#DL_cellarVoLTEPCIPicture1", "");
    }

    private String getResult(String val1, String val2) {
        if ((val1 == null && val2 == null) || (val1.equals("") && val2.equals(""))) {
            return "";
        }
        if (val1.equals(val2)) {
            return "是";
        }
        return "否";
    }

    private String getResult(int val1, int val2) {
        if (val1 == (val2)) {
            return "是";
        }
        return "否";
    }

    private String getResult(double val1, double val2) {
        if (val1 == (val2)) {
            return "是";
        }
        return "否";
    }

    /**
     * 初始化室外基站数据
     */
    private void initOutdoorData(Context context, StationInfo stationInfo, Map<String, String> datas) {
        SurveyStationInfo surveyStationInfo = mDaoManager.getSurveyStationInfo(stationInfo.getId());
        List<SurveyCellInfo> surveyCellInfos = surveyStationInfo.getCellInfoList();
        //报告页
        // -------------------------------------
        //基站描述
        datas.put("#DL_SiteName0", stationInfo.getName());
        datas.put("#DL_celldateSurveyDate0", surveyStationInfo.getTestDate() + "");
        datas.put("#DL_SiteId0", stationInfo.getCode() + "");
        datas.put("#DL_siteplan-city", surveyStationInfo.getCity() + "");
        datas.put("#DL_Address", surveyStationInfo.getAddress() + "");
        datas.put("#DL_siteplan-AdminRegion", surveyStationInfo.getDistrict() + "");
        datas.put("#DL_siteplan-DeviceType", surveyStationInfo.getDeviceType() + "");
        datas.put("#DL_siteplan-SiteType", surveyStationInfo.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR
                ? context.getString(R.string.single_station_type_indoor) : context.getString(R.string
                .single_station_type_outdoor) + "");
        datas.put("#DL_siteplan-SiteConfiguration", "");
        //参数及功能验证
        // -------------------------------------
        datas.put("#DL_Longitude", stationInfo.getLongitude() + "");
        datas.put("#DL_Longitude_test", surveyStationInfo.getLongitude() + "");
        datas.put("#DL_Latitude", stationInfo.getLatitude() + "");
        datas.put("#DL_Latitude_test", surveyStationInfo.getLatitude() + "");
        datas.put("#DL_TACenodebid", stationInfo.getTAC() + "");
        datas.put("#DL_TACenodebid_test", surveyStationInfo.getTAC() + "");
        datas.put("#DL_TAC_result", getResult(stationInfo.getTAC(), surveyStationInfo.getTAC()));
        datas.put("#DL_eNodeBID", stationInfo.getENodeBID() + "");
        datas.put("#DL_eNodeBID_test", surveyStationInfo.getENodeBID() + "");
        datas.put("#DL_eNodeBID_result", getResult(stationInfo.getENodeBID(), surveyStationInfo.getENodeBID()));
        //
        if (null != stationInfo.getCellInfoList() && stationInfo.getCellInfoList().size() >= 3) {
            datas.put("#DL_CellName0", "");
            datas.put("#DL_CellName1", "");
            datas.put("#DL_CellName2", "");
        }
        try {
            for (int i = 0; i < surveyCellInfos.size(); i++) {
                if (i == 0) {
                    SurveyCellInfo surveyCellInfo = surveyCellInfos.get(i);
                    CellInfo cellInfo = null;
                    for (CellInfo cellInfox : stationInfo.getCellInfoList()) {
                        if (surveyCellInfo.getCellId() == cellInfox.getCellId()) {
                            cellInfo = cellInfox;
                            break;
                        }
                    }
                    if (null != cellInfo) {
                        //小区名1
                        datas.put("#DL_CellPlan0", cellInfo.getCarrierSetup() + "");
                        datas.put("#DL_CellTest0_CarrierCount", surveyCellInfo.getCarrierSetup() + "");
                        datas.put("#DL_CellPlan0_result", getResult(cellInfo.getCarrierSetup(), surveyCellInfo
                                .getCarrierSetup()));//1
                        datas.put("#DL_CellID0", cellInfo.getCellId() + "");
                        datas.put("#DL_CellID0_test", surveyCellInfo.getCellId() + "");
                        datas.put("#DL_CellD0_result", getResult(cellInfo.getCellId(), surveyCellInfo.getCellId()));//2
                        datas.put("#DL_PCI0", cellInfo.getPCI() + "");
                        datas.put("#DL_PCI0_test", surveyCellInfo.getPCI() + "");
                        datas.put("#DL_PCI0_result", getResult(cellInfo.getPCI(), surveyCellInfo.getPCI()));//3
                        datas.put("#DL_band0", cellInfo.getBand() + "");
                        datas.put("#DL_band0_test", surveyCellInfo.getBand() + "");
                        datas.put("#DL_band0_result", getResult(cellInfo.getBand(), surveyCellInfo.getBand()));//4
                        datas.put("#DL_point0", cellInfo.getFrequency() + "");
                        datas.put("#DL_point0_test", surveyCellInfo.getFrequency() + "");
                        datas.put("#DL_point0_result", getResult(cellInfo.getFrequency(), surveyCellInfo.getFrequency
                                ()));//5
                        datas.put("#DL_Bandwidth0", cellInfo.getBandwidth() + "");
                        datas.put("#DL_Bandwidth0_test", surveyCellInfo.getBandwidth() + "");
                        datas.put("#DL_Bandwidth0_result", getResult(cellInfo.getBandwidth(), surveyCellInfo
                                .getBandwidth()));//6
                        datas.put("#DL_ZCRootSequence0", cellInfo.getRootSequence() + "");
                        datas.put("#DL_ZCRootSequence0_test", surveyCellInfo.getRootSequence() + "");
                        datas.put("#DL_ZCRootSequence0_result", getResult(cellInfo.getRootSequence(), surveyCellInfo
                                .getRootSequence()));//7
                        datas.put("#DL_Subframeratio0", cellInfo.getSubframeMatching() + "");
                        datas.put("#DL_Subframeratio0_test", surveyCellInfo.getSubframeMatching() + "");
                        datas.put("#DL_Subframeratio0_result", getResult(cellInfo.getSubframeMatching(),
                                surveyCellInfo.getSubframeMatching()));//8
                        datas.put("#DL_SpecialSubframeRatio0", cellInfo.getSpecialSubframeMatching() + "");
                        datas.put("#DL_SpecialSubframeRatio0_test", surveyCellInfo.getSpecialSubframeMatching() + "");
                        datas.put("#DL_SpecialSubframeRatio0_result", getResult(cellInfo.getSpecialSubframeMatching()
                                , surveyCellInfo.getSpecialSubframeMatching()));//9
                        datas.put("#DL_Longitude0", stationInfo.getLongitude() + "");
                        datas.put("#DL_Longitude0_test", surveyStationInfo.getLongitude() + "");
                        datas.put("#DL_Longitude0_result", getResult(stationInfo.getLongitude(), surveyStationInfo
                                .getLongitude()));//1
                        datas.put("#DL_Latitude0", stationInfo.getLatitude() + "");
                        datas.put("#DL_Latitude0_test", surveyStationInfo.getLatitude() + "");
                        datas.put("#DL_Latitude0_result", getResult(stationInfo.getLatitude(), surveyStationInfo
                                .getLatitude()));//2
                        datas.put("#DL_PDCCHOFDMSymbols0", cellInfo.getPDCCH() + "");
                        datas.put("#DL_PDCCHOFDMSymbols0_test", surveyCellInfo.getPDCCH() + "");
                        datas.put("#DL_PDCCHOFDMSymbols0_result", getResult(cellInfo.getPDCCH(), surveyCellInfo
                                .getPDCCH()));//3
                        datas.put("#DL_RsPower0", cellInfo.getRsPower() + "");
                        datas.put("#DL_RsPower0_test", surveyCellInfo.getRsPower() + "");
                        datas.put("#DL_RsPower0_result", getResult(cellInfo.getRsPower(), surveyCellInfo.getRsPower()
                        ));//4
                        datas.put("#DL_PA0", cellInfo.getPA() + "");
                        datas.put("#DL_PA0_test", surveyCellInfo.getPA() + "");
                        datas.put("#DL_PA0_result", getResult(cellInfo.getPA(), surveyCellInfo.getPA()));//5
                        datas.put("#DL_PB0", cellInfo.getPB() + "");
                        datas.put("#DL_PB0_test", surveyCellInfo.getPB() + "");
                        datas.put("#DL_PB0_result", getResult(cellInfo.getPB(), surveyCellInfo.getPB()));//6
                        datas.put("#DL_AntennaInstallationHeight0", cellInfo.getAerialHigh() + "");
                        datas.put("#DL_AntennaInstallationHeight0_test", surveyCellInfo.getAerialHigh() + "");
                        datas.put("#DL_AntennaInstallationHeight0_result", getResult(cellInfo.getAerialHigh(),
                                surveyCellInfo.getAerialHigh()));//7
                        datas.put("#DL_Azimuth0", cellInfo.getAzimuth() + "");
                        datas.put("#DL_Azimuth0_test", surveyCellInfo.getAzimuth() + "");
                        datas.put("#DL_Azimuth0_result", getResult(cellInfo.getAzimuth(), surveyCellInfo.getAzimuth()
                        ));//8
                        datas.put("#DL_TotalDowntiltAngle0", cellInfo.getDownAngle() + "");
                        datas.put("#DL_TotalDowntiltAngle0_test", surveyCellInfo.getDownAngle() + "");
                        datas.put("#DL_TotalDowntiltAngle0_result", getResult(cellInfo.getDownAngle(), surveyCellInfo
                                .getDownAngle()));//9
                        datas.put("#DL_ElectricalDowntilt0", cellInfo.getElectricDownAngle() + "");
                        datas.put("#DL_ElectricalDowntilt0_test", surveyCellInfo.getElectricDownAngle() + "");
                        datas.put("#DL_ElectricalDowntilt0_result", getResult(cellInfo.getElectricDownAngle(),
                                surveyCellInfo.getElectricDownAngle()));//10
                        datas.put("#DL_MechanicalDowntilt0", cellInfo.getMachineDownAngle() + "");
                        datas.put("#DL_MechanicalDowntilt0_test", surveyCellInfo.getMachineDownAngle() + "");
                        datas.put("#DL_MechanicalDowntilt0_result", getResult(cellInfo.getMachineDownAngle(),
                                surveyCellInfo.getMachineDownAngle()));//11
                        datas.put("#DL_V－PlaneHalfPowerBeamwidth0", cellInfo.getVerticalFalfPowerAngle() + "");
                        datas.put("#DL_V－PlaneHalfPowerBeamwidth0_test", surveyCellInfo.getVerticalFalfPowerAngle() +
                                "");
                        datas.put("#DL_V－PlaneHalfPowerBeamwidth0_result", getResult(cellInfo
                                .getVerticalFalfPowerAngle(), surveyCellInfo.getVerticalFalfPowerAngle()));//11
                        datas.put("#DL_H-PlaneHalfPowerBeamwidth0", cellInfo.getHorizontalFalfPowerAngle() + "");
                        datas.put("#DL_H-PlaneHalfPowerBeamwidth0_test", surveyCellInfo.getHorizontalFalfPowerAngle()
                                + "");
                        datas.put("#DL_H-PlaneHalfPowerBeamwidth0_result", getResult(cellInfo
                                .getHorizontalFalfPowerAngle(), surveyCellInfo.getHorizontalFalfPowerAngle()));//12
                        datas.put("#DL_AntennaManufacturer0", cellInfo.getAerialVender() + "");
                        datas.put("#DL_AntennaManufacturer0_test", surveyCellInfo.getAerialVender() + "");
                        datas.put("#DL_AntennaManufacturer0_result", getResult(cellInfo.getAerialVender(),
                                surveyCellInfo.getAerialVender()));//13
                        datas.put("#DL_AntennaTypes0", cellInfo.getAerialType() + "");
                        datas.put("#DL_AntennaTypes0_test", surveyCellInfo.getAerialType() + "");
                        datas.put("#DL_AntennaTypes0_result", getResult(cellInfo.getAerialType(), surveyCellInfo
                                .getAerialType()));//14
                    }
                } else if (i == 1) {
                    SurveyCellInfo surveyCellInfo = surveyCellInfos.get(i);
                    CellInfo cellInfo = null;
                    for (CellInfo cellInfox : stationInfo.getCellInfoList()) {
                        if (surveyCellInfo.getCellId() == cellInfox.getCellId()) {
                            cellInfo = cellInfox;
                            break;
                        }
                    }
                    if (null != cellInfo) {
                        //小区名2
                        datas.put("#DL_CellPlan1", cellInfo.getCarrierSetup() + "");
                        datas.put("#DL_CellTest1_CarrierCount", surveyCellInfo.getCarrierSetup() + "");
                        datas.put("#DL_CellPlan1_result", getResult(cellInfo.getCarrierSetup(), surveyCellInfo
                                .getCarrierSetup()));//1
                        datas.put("#DL_CellID1", cellInfo.getCellId() + "");
                        datas.put("#DL_CellID1_test", surveyCellInfo.getCellId() + "");
                        datas.put("#DL_CellD1_result", getResult(cellInfo.getCellId(), surveyCellInfo.getCellId()));//2
                        datas.put("#DL_PCI1", cellInfo.getPCI() + "");
                        datas.put("#DL_PCI1_test", surveyCellInfo.getPCI() + "");
                        datas.put("#DL_PCI1_result", getResult(cellInfo.getPCI(), surveyCellInfo.getPCI()));//3
                        datas.put("#DL_band1", cellInfo.getBand() + "");
                        datas.put("#DL_band1_test", surveyCellInfo.getBand() + "");
                        datas.put("#DL_band1_result", getResult(cellInfo.getBand(), surveyCellInfo.getBand()));//4
                        datas.put("#DL_point1", cellInfo.getFrequency() + "");
                        datas.put("#DL_point1_test", surveyCellInfo.getFrequency() + "");
                        datas.put("#DL_point1_result", getResult(cellInfo.getFrequency(), surveyCellInfo.getFrequency
                                ()));//5
                        datas.put("#DL_Bandwidth1", cellInfo.getBandwidth() + "");
                        datas.put("#DL_Bandwidth1_test", surveyCellInfo.getBandwidth() + "");
                        datas.put("#DL_Bandwidth1_result", getResult(cellInfo.getBandwidth(), surveyCellInfo
                                .getBandwidth()));//6
                        datas.put("#DL_ZCRootSequence1", cellInfo.getRootSequence() + "");
                        datas.put("#DL_ZCRootSequence1_test", surveyCellInfo.getRootSequence() + "");
                        datas.put("#DL_ZCRootSequence1_result", getResult(cellInfo.getRootSequence(), surveyCellInfo
                                .getRootSequence()));//7
                        datas.put("#DL_Subframeratio1", cellInfo.getSubframeMatching() + "");
                        datas.put("#DL_Subframeratio1_test", surveyCellInfo.getSubframeMatching() + "");
                        datas.put("#DL_Subframeratio1_result", getResult(cellInfo.getSubframeMatching(),
                                surveyCellInfo.getSubframeMatching()));//8
                        datas.put("#DL_SpecialSubframeRatio1", cellInfo.getSpecialSubframeMatching() + "");
                        datas.put("#DL_SpecialSubframeRatio1_test", surveyCellInfo.getSpecialSubframeMatching() + "");
                        datas.put("#DL_SpecialSubframeRatio1_result", getResult(cellInfo.getSpecialSubframeMatching()
                                , surveyCellInfo.getSpecialSubframeMatching()));//9
                        datas.put("#DL_Longitude1", stationInfo.getLongitude() + "");
                        datas.put("#DL_Longitude1_test", surveyStationInfo.getLongitude() + "");
                        datas.put("#DL_Longitude1_result", getResult(stationInfo.getLongitude(), surveyStationInfo
                                .getLongitude()));//1
                        datas.put("#DL_Latitude1", stationInfo.getLatitude() + "");
                        datas.put("#DL_Latitude1_test", surveyStationInfo.getLatitude() + "");
                        datas.put("#DL_Latitude1_result", getResult(stationInfo.getLatitude(), surveyStationInfo
                                .getLatitude()));//2
                        datas.put("#DL_PDCCHOFDMSymbols1", cellInfo.getPDCCH() + "");
                        datas.put("#DL_PDCCHOFDMSymbols1_test", surveyCellInfo.getPDCCH() + "");
                        datas.put("#DL_PDCCHOFDMSymbols1_result", getResult(cellInfo.getPDCCH(), surveyCellInfo
                                .getPDCCH()));//3
                        datas.put("#DL_RsPower1", cellInfo.getRsPower() + "");
                        datas.put("#DL_RsPower1_test", surveyCellInfo.getRsPower() + "");
                        datas.put("#DL_RsPower1_result", getResult(cellInfo.getRsPower(), surveyCellInfo.getRsPower()
                        ));//4
                        datas.put("#DL_PA1", cellInfo.getPA() + "");
                        datas.put("#DL_PA1_test", surveyCellInfo.getPA() + "");
                        datas.put("#DL_PA1_result", getResult(cellInfo.getPA(), surveyCellInfo.getPA()));//5
                        datas.put("#DL_PB1", cellInfo.getPB() + "");
                        datas.put("#DL_PB1_test", surveyCellInfo.getPB() + "");
                        datas.put("#DL_PB1_result", getResult(cellInfo.getPB(), surveyCellInfo.getPB()));//6
                        datas.put("#DL_AntennaInstallationHeight1", cellInfo.getAerialHigh() + "");
                        datas.put("#DL_AntennaInstallationHeight1_test", surveyCellInfo.getAerialHigh() + "");
                        datas.put("#DL_AntennaInstallationHeight1_result", getResult(cellInfo.getAerialHigh(),
                                surveyCellInfo.getAerialHigh()));//7
                        datas.put("#DL_Azimuth1", cellInfo.getAzimuth() + "");
                        datas.put("#DL_Azimuth1_test", surveyCellInfo.getAzimuth() + "");
                        datas.put("#DL_Azimuth1_result", getResult(cellInfo.getAzimuth(), surveyCellInfo.getAzimuth()
                        ));//8
                        datas.put("#DL_TotalDowntiltAngle1", cellInfo.getDownAngle() + "");
                        datas.put("#DL_TotalDowntiltAngle1_test", surveyCellInfo.getDownAngle() + "");
                        datas.put("#DL_TotalDowntiltAngle1_result", getResult(cellInfo.getDownAngle(), surveyCellInfo
                                .getDownAngle()));//9
                        datas.put("#DL_ElectricalDowntilt1", cellInfo.getElectricDownAngle() + "");
                        datas.put("#DL_ElectricalDowntilt1_test", surveyCellInfo.getElectricDownAngle() + "");
                        datas.put("#DL_ElectricalDowntilt1_result", getResult(cellInfo.getElectricDownAngle(),
                                surveyCellInfo.getElectricDownAngle()));//11
                        datas.put("#DL_MechanicalDowntilt1", cellInfo.getMachineDownAngle() + "");
                        datas.put("#DL_MechanicalDowntilt1_test", surveyCellInfo.getMachineDownAngle() + "");
                        datas.put("#DL_MechanicalDowntilt1_result", getResult(cellInfo.getMachineDownAngle(),
                                surveyCellInfo.getMachineDownAngle()));//11
                        datas.put("#DL_V－PlaneHalfPowerBeamwidth1", cellInfo.getVerticalFalfPowerAngle() + "");
                        datas.put("#DL_V－PlaneHalfPowerBeamwidth1_test", surveyCellInfo.getVerticalFalfPowerAngle() +
                                "");
                        datas.put("#DL_V－PlaneHalfPowerBeamwidth1_result", getResult(cellInfo
                                .getVerticalFalfPowerAngle(), surveyCellInfo.getVerticalFalfPowerAngle()));//11
                        datas.put("#DL_H-PlaneHalfPowerBeamwidth1", cellInfo.getHorizontalFalfPowerAngle() + "");
                        datas.put("#DL_H-PlaneHalfPowerBeamwidth1_test", surveyCellInfo.getHorizontalFalfPowerAngle()
                                + "");
                        datas.put("#DL_H-PlaneHalfPowerBeamwidth1_result", getResult(cellInfo
                                .getHorizontalFalfPowerAngle(), surveyCellInfo.getHorizontalFalfPowerAngle()));//12
                        datas.put("#DL_AntennaManufacturer1", cellInfo.getAerialVender() + "");
                        datas.put("#DL_AntennaManufacturer1_test", surveyCellInfo.getAerialVender() + "");
                        datas.put("#DL_AntennaManufacturer1_result", getResult(cellInfo.getAerialVender(),
                                surveyCellInfo.getAerialVender()));//13
                        datas.put("#DL_AntennaTypes1", cellInfo.getAerialType() + "");
                        datas.put("#DL_AntennaTypes1_test", surveyCellInfo.getAerialType() + "");
                        datas.put("#DL_AntennaTypes1_result", getResult(cellInfo.getAerialType(), surveyCellInfo
                                .getAerialType()));//14
                    }
                } else if (i == 2) {
                    SurveyCellInfo surveyCellInfo = surveyCellInfos.get(i);
                    CellInfo cellInfo = null;
                    for (CellInfo cellInfox : stationInfo.getCellInfoList()) {
                        if (surveyCellInfo.getCellId() == cellInfox.getCellId()) {
                            cellInfo = cellInfox;
                            break;
                        }
                    }
                    if (null != cellInfo) {
                        //小区名3
                        datas.put("#DL_CellPlan2", cellInfo.getCarrierSetup() + "");
                        datas.put("#DL_CellTest2_CarrierCount", surveyCellInfo.getCarrierSetup() + "");
                        datas.put("#DL_CellPlan2_result", getResult(cellInfo.getCarrierSetup(), surveyCellInfo
                                .getCarrierSetup()));//1
                        datas.put("#DL_CellID2", cellInfo.getCellId() + "");
                        datas.put("#DL_CellID2_test", surveyCellInfo.getCellId() + "");
                        datas.put("#DL_CellD2_result", getResult(cellInfo.getCellId(), surveyCellInfo.getCellId()));//2
                        datas.put("#DL_PCI2", cellInfo.getPCI() + "");
                        datas.put("#DL_PCI2_test", surveyCellInfo.getPCI() + "");
                        datas.put("#DL_PCI2_result", getResult(cellInfo.getPCI(), surveyCellInfo.getPCI()));//3
                        datas.put("#DL_band2", cellInfo.getBand() + "");
                        datas.put("#DL_band2_test", surveyCellInfo.getBand() + "");
                        datas.put("#DL_band2_result", getResult(cellInfo.getBand(), surveyCellInfo.getBand()));//4
                        datas.put("#DL_point2", cellInfo.getFrequency() + "");
                        datas.put("#DL_point2_test", surveyCellInfo.getFrequency() + "");
                        datas.put("#DL_point2_result", getResult(cellInfo.getFrequency(), surveyCellInfo.getFrequency
                                ()));//5
                        datas.put("#DL_Bandwidth2", cellInfo.getBandwidth() + "");
                        datas.put("#DL_Bandwidth2_test", surveyCellInfo.getBandwidth() + "");
                        datas.put("#DL_Bandwidth2_result", getResult(cellInfo.getBandwidth(), surveyCellInfo
                                .getBandwidth()));//6
                        datas.put("#DL_ZCRootSequence2", cellInfo.getRootSequence() + "");
                        datas.put("#DL_ZCRootSequence2_test", surveyCellInfo.getRootSequence() + "");
                        datas.put("#DL_ZCRootSequence2_result", getResult(cellInfo.getRootSequence(), surveyCellInfo
                                .getRootSequence()));//7
                        datas.put("#DL_Subframeratio2", cellInfo.getSubframeMatching() + "");
                        datas.put("#DL_Subframeratio2_test", surveyCellInfo.getSubframeMatching() + "");
                        datas.put("#DL_Subframeratio2_result", getResult(cellInfo.getSubframeMatching(),
                                surveyCellInfo.getSubframeMatching()));//8
                        datas.put("#DL_SpecialSubframeRatio2", cellInfo.getSpecialSubframeMatching() + "");
                        datas.put("#DL_SpecialSubframeRatio2_test", surveyCellInfo.getSpecialSubframeMatching() + "");
                        datas.put("#DL_SpecialSubframeRatio2_result", getResult(cellInfo.getSpecialSubframeMatching()
                                , surveyCellInfo.getSpecialSubframeMatching()));//9
                        datas.put("#DL_Longitude2", stationInfo.getLongitude() + "");
                        datas.put("#DL_Longitude2_test", surveyStationInfo.getLongitude() + "");
                        datas.put("#DL_Longitude2_result", getResult(stationInfo.getLongitude(), surveyStationInfo
                                .getLongitude()));//1
                        datas.put("#DL_Latitude2", stationInfo.getLatitude() + "");
                        datas.put("#DL_Latitude2_test", surveyStationInfo.getLatitude() + "");
                        datas.put("#DL_Latitude2_result", getResult(stationInfo.getLatitude(), surveyStationInfo
                                .getLatitude()));//2
                        datas.put("#DL_PDCCHOFDMSymbols2", cellInfo.getPDCCH() + "");
                        datas.put("#DL_PDCCHOFDMSymbols2_test", surveyCellInfo.getPDCCH() + "");
                        datas.put("#DL_PDCCHOFDMSymbols2_result", getResult(cellInfo.getPDCCH(), surveyCellInfo
                                .getPDCCH()));//3
                        datas.put("#DL_RsPower2", cellInfo.getRsPower() + "");
                        datas.put("#DL_RsPower2_test", surveyCellInfo.getRsPower() + "");
                        datas.put("#DL_RsPower2_result", getResult(cellInfo.getRsPower(), surveyCellInfo.getRsPower()
                        ));//4
                        datas.put("#DL_PA2", cellInfo.getPA() + "");
                        datas.put("#DL_PA2_test", surveyCellInfo.getPA() + "");
                        datas.put("#DL_PA2_result", getResult(cellInfo.getPA(), surveyCellInfo.getPA()));//5
                        datas.put("#DL_PB2", cellInfo.getPB() + "");
                        datas.put("#DL_PB2_test", surveyCellInfo.getPB() + "");
                        datas.put("#DL_PB2_result", getResult(cellInfo.getPB(), surveyCellInfo.getPB()));//6
                        datas.put("#DL_AntennaInstallationHeight2", cellInfo.getAerialHigh() + "");
                        datas.put("#DL_AntennaInstallationHeight2_test", surveyCellInfo.getAerialHigh() + "");
                        datas.put("#DL_AntennaInstallationHeight2_result", getResult(cellInfo.getAerialHigh(),
                                surveyCellInfo.getAerialHigh()));//7
                        datas.put("#DL_Azimuth2", cellInfo.getAzimuth() + "");
                        datas.put("#DL_Azimuth2_test", surveyCellInfo.getAzimuth() + "");
                        datas.put("#DL_Azimuth2_result", getResult(cellInfo.getAzimuth(), surveyCellInfo.getAzimuth()
                        ));//8
                        datas.put("#DL_TotalDowntiltAngle2", cellInfo.getDownAngle() + "");
                        datas.put("#DL_TotalDowntiltAngle2_test", surveyCellInfo.getDownAngle() + "");
                        datas.put("#DL_TotalDowntiltAngle2_result", getResult(cellInfo.getDownAngle(), surveyCellInfo
                                .getDownAngle()));//9
                        datas.put("#DL_ElectricalDowntilt2", cellInfo.getElectricDownAngle() + "");
                        datas.put("#DL_ElectricalDowntilt2_test", surveyCellInfo.getElectricDownAngle() + "");
                        datas.put("#DL_ElectricalDowntilt2_result", getResult(cellInfo.getElectricDownAngle(),
                                surveyCellInfo.getElectricDownAngle()));//12
                        datas.put("#DL_MechanicalDowntilt2", cellInfo.getMachineDownAngle() + "");
                        datas.put("#DL_MechanicalDowntilt2_test", surveyCellInfo.getMachineDownAngle() + "");
                        datas.put("#DL_MechanicalDowntilt2_result", getResult(cellInfo.getMachineDownAngle(),
                                surveyCellInfo.getMachineDownAngle()));//11
                        datas.put("#DL_V－PlaneHalfPowerBeamwidth2", cellInfo.getVerticalFalfPowerAngle() + "");
                        datas.put("#DL_V－PlaneHalfPowerBeamwidth2_test", surveyCellInfo.getVerticalFalfPowerAngle() +
                                "");
                        datas.put("#DL_V－PlaneHalfPowerBeamwidth2_result", getResult(cellInfo
                                .getVerticalFalfPowerAngle(), surveyCellInfo.getVerticalFalfPowerAngle()));//11
                        datas.put("#DL_H-PlaneHalfPowerBeamwidth2", cellInfo.getHorizontalFalfPowerAngle() + "");
                        datas.put("#DL_H-PlaneHalfPowerBeamwidth2_test", surveyCellInfo.getHorizontalFalfPowerAngle()
                                + "");
                        datas.put("#DL_H-PlaneHalfPowerBeamwidth2_result", getResult(cellInfo
                                .getHorizontalFalfPowerAngle(), surveyCellInfo.getHorizontalFalfPowerAngle()));//12
                        datas.put("#DL_AntennaManufacturer2", cellInfo.getAerialVender() + "");
                        datas.put("#DL_AntennaManufacturer2_test", surveyCellInfo.getAerialVender() + "");
                        datas.put("#DL_AntennaManufacturer2_result", getResult(cellInfo.getAerialVender(),
                                surveyCellInfo.getAerialVender()));//13
                        datas.put("#DL_AntennaTypes2", cellInfo.getAerialType() + "");
                        datas.put("#DL_AntennaTypes2_test", surveyCellInfo.getAerialType() + "");
                        datas.put("#DL_AntennaTypes2_result", getResult(cellInfo.getAerialType(), surveyCellInfo
                                .getAerialType()));//14
                    }
                }
            }
        } catch (Exception ex) {
            LogUtil.w(TAG, ex.getMessage());
        }
        //关键点拍照
        // -------------------------------------
        datas.put("#DL_celldateSurveyer", "");
        datas.put("#DL_celldateSurveyDate", "");
        datas.put("#DL_xiaoqufgaiquyutu", "");
        //站点经纬度
        //-------------------------------------
        datas.put("#DL_jingweidutu", "");
        //网络性能验收-DT
        //--------------------------------------
        datas.put("#DL_SiteName1", stationInfo.getName() + "");
        datas.put("#DL_SiteId1", stationInfo.getCode() + "");
        datas.put("#DL_celldateSurveyDate1", surveyStationInfo.getTestDate() + "");
        datas.put("#DL_celldateSurveyer0", surveyStationInfo.getTester() + "");
        datas.put("#DL_DeviceMode0", surveyStationInfo.getDeviceType() + "");
        datas.put("#DL_PhoneNumber0", surveyStationInfo.getTestPhone() + "");
        datas.put("#DL_Platform0", surveyStationInfo.getTestPlatform() + "");
        datas.put("#DL_RSRPMap0", "");
        datas.put("#DL_SINRMap0", "");
        datas.put("#DL_PCIMap0", "");
        datas.put("#DL_RSRPMap1", "");
        datas.put("#DL_SINRMap1", "");
        datas.put("#DL_PCIMap1", "");
        datas.put("#DL_RSRPMap2", "");
        datas.put("#DL_SINRMap2", "");
        datas.put("#DL_PCIMap2", "");
        //性能验收-CQT截图
        //--------------------------------------
        datas.put("#DL_SiteName2", stationInfo.getName() + "");
        datas.put("#DL_SiteId2", stationInfo.getCode() + "");
        datas.put("#DL_celldateSurveyDate2", surveyStationInfo.getTestDate() + "");
        datas.put("#DL_celldateSurveyer1", surveyStationInfo.getTester() + "");
        datas.put("#DL_DeviceMode1", surveyStationInfo.getDeviceType() + "");
        datas.put("#DL_PhoneNumber1", surveyStationInfo.getTestPhone() + "");
        datas.put("#DL_Platform1", surveyStationInfo.getTestPlatform() + "");
        List<SceneInfo> scenes = mDaoManager.getSceneInfoList(stationInfo.getId(), SingleStationDaoManager
                .SCENE_TYPE_PERFORMANCE);
        try {
            for (int i = 0; i < scenes.size(); i++) {
                if (i == 0) {//小区1
                    datas.put("#DL_RSRPavg0", "");
                    datas.put("#DL_SINRavg0", "");
                    datas.put("#DL_CSFB0", "");
                    datas.put("#DL_VoLTE0", "");
                    datas.put("#DL_FTPULavg0", "");
                    datas.put("#DL_FTPDLavg0", "");
                    List<TaskTestResult> taskTestResultList = mDaoManager.getTaskTestResultList(scenes.get(i).getId());
                    for (TaskTestResult taskTestResult : taskTestResultList) {
                        datas.put("#DL_RSRPavg0", taskTestResult.getRsrpAverage() + "");
                        datas.put("#DL_SINRavg0", taskTestResult.getSinrAverage() + "");
                        if (taskTestResult.getTaskType().equals("MOC_CSFB")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_csfb_establishedRate")) {
                                    datas.put("#DL_CSFB0", thresholdTestResult.getRealValue() + thresholdTestResult
                                            .getThresholdUnit());
                                }
                            }
                        } else if (taskTestResult.getTaskType().equals("MOC_VOLTE")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_volte_successRate")) {
                                    datas.put("#DL_VoLTE0", thresholdTestResult.getRealValue() + thresholdTestResult
                                            .getThresholdUnit());
                                }
                            }
                        } else if (taskTestResult.getTaskType().equals("FTP_Upload")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_SpeedAverage")) {
                                    datas.put("#DL_FTPULavg0", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                            }
                        } else if (taskTestResult.getTaskType().equals("FTP_Download")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_SpeedAverage")) {
                                    datas.put("#DL_FTPDLavg0", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                            }
                        }
                    }
                    datas.put("#DL_CQTdl_0", "");
                    datas.put("#DL_CQTUl_0", "");
                } else if (i == 1) {//小区2
                    datas.put("#DL_RSRPavg1", "");
                    datas.put("#DL_SINRavg1", "");
                    datas.put("#DL_CSFB1", "");
                    datas.put("#DL_VoLTE1", "");
                    datas.put("#DL_FTPULavg1", "");
                    datas.put("#DL_FTPDLavg1", "");
                    List<TaskTestResult> taskTestResultList = mDaoManager.getTaskTestResultList(scenes.get(i).getId());
                    for (TaskTestResult taskTestResult : taskTestResultList) {
                        datas.put("#DL_RSRPavg1", taskTestResult.getRsrpAverage() + "");
                        datas.put("#DL_SINRavg1", taskTestResult.getSinrAverage() + "");
                        if (taskTestResult.getTaskType().equals("MOC_CSFB")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_csfb_establishedRate")) {
                                    datas.put("#DL_CSFB1", thresholdTestResult.getRealValue() + thresholdTestResult
                                            .getThresholdUnit());
                                }
                            }
                        } else if (taskTestResult.getTaskType().equals("MOC_VOLTE")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_volte_successRate")) {
                                    datas.put("#DL_VoLTE1", thresholdTestResult.getRealValue() + thresholdTestResult
                                            .getThresholdUnit());
                                }
                            }
                        } else if (taskTestResult.getTaskType().equals("FTP_Upload")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_SpeedAverage")) {
                                    datas.put("#DL_FTPULavg1", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                            }
                        } else if (taskTestResult.getTaskType().equals("FTP_Download")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_SpeedAverage")) {
                                    datas.put("#DL_FTPDLavg1", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                            }
                        }
                    }
                    datas.put("#DL_CQTdl_1", "");
                    datas.put("#DL_CQTUl_1", "");
                } else if (i == 2) {//小区3
                    datas.put("#DL_RSRPavg2", "");
                    datas.put("#DL_SINRavg2", "");
                    datas.put("#DL_CSFB2", "");
                    datas.put("#DL_VoLTE2", "");
                    datas.put("#DL_FTPULavg2", "");
                    datas.put("#DL_FTPDLavg2", "");
                    List<TaskTestResult> taskTestResultList = mDaoManager.getTaskTestResultList(scenes.get(i).getId());
                    for (TaskTestResult taskTestResult : taskTestResultList) {
                        datas.put("#DL_RSRPavg2", taskTestResult.getRsrpAverage() + "");
                        datas.put("#DL_SINRavg2", taskTestResult.getSinrAverage() + "");
                        if (taskTestResult.getTaskType().equals("MOC_CSFB")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_csfb_establishedRate")) {
                                    datas.put("#DL_CSFB2", thresholdTestResult.getRealValue() + thresholdTestResult
                                            .getThresholdUnit());
                                }
                            }
                        } else if (taskTestResult.getTaskType().equals("MOC_VOLTE")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_volte_successRate")) {
                                    datas.put("#DL_VoLTE2", thresholdTestResult.getRealValue() + thresholdTestResult
                                            .getThresholdUnit());
                                }
                            }
                        } else if (taskTestResult.getTaskType().equals("FTP_Upload")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_SpeedAverage")) {
                                    datas.put("#DL_FTPULavg2", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                            }
                        } else if (taskTestResult.getTaskType().equals("FTP_Download")) {
                            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList
                                    ()) {
                                if (thresholdTestResult.getThresholdKey().equals("_SpeedAverage")) {
                                    datas.put("#DL_FTPDLavg2", thresholdTestResult.getRealValue() +
                                            thresholdTestResult.getThresholdUnit());
                                }
                            }
                        }
                    }
                    datas.put("#DL_CQTdl_2", "");
                    datas.put("#DL_CQTUl_2", "");
                }
            }
        } catch (Exception ex) {
            LogUtil.w(TAG, ex.getMessage());
        }
    }
}

