package com.walktour.service.metro.utils;

import com.dingli.watcher.jni.MetroJNI;
import com.dingli.watcher.model.MetroGPS;
import com.dingli.watcher.model.MetroRunParamInfo;
import com.walktour.Utils.ApplicationModel;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.highspeedrail.model.HighSpeedLineModel;
import com.walktour.gui.highspeedrail.model.HighSpeedNoModel;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.service.metro.model.MetroCity;
import com.walktour.service.metro.model.MetroRoute;
import com.walktour.service.metro.model.MetroStation;

import java.util.ArrayList;
import java.util.List;

/**
 * 地铁业务和业务库交互的工具类
 *
 * @author jianchao.wang
 */
public class MetroUtil {
    /**
     * 日志标识
     */
    private static final String TAG = "MetroUtil";
    /**
     * 唯一实例
     */
    private static MetroUtil sInstance;
    /**
     * 业务日志路径
     */
    private static String LOG_PATH = "";

// 一些设置的KEY
    /**
     * 速度加成
     */
    private static final int ConfigDoublePropertyKey_RTFillGPS_SpeedAddRate = 0;
    /**
     * 地铁判断停止最小阈值
     */
    private static final int ConfigDoublePropertyKey_RTFillGPS_MinStopThreshold = 1;
    /**
     * 地铁判断停止的最大阀值,默认:0.5
     */
    private static final int ConfigDoublePropertyKey_RTFillGPS_MaxStopThreshold = 2;
    /**
     * 地铁判断起动的最小阀值,默认:0.5
     */
    private static final int ConfigDoublePropertyKey_RTFillGPS_MinStartThreshold = 3;
    /**
     * 地铁判断减速的最大阀值,默认: -0.2
     */
    private static final int ConfigDoublePropertyKey_RTFillGPS_MaxReduceThreshold = 4;
    /**
     * 地铁加速度补偿校正的最小阀值,默认: -0.4
     */
    private static final int ConfigDoublePropertyKey_RTFillGPS_MinRepairAdjustThreshold = 5;
    /**
     * 地铁加速度补偿校正的最大阀值,默认: 0.4
     */
    private static final int ConfigDoublePropertyKey_RTFillGPS_MaxRepairAdjustThreshold = 6;

    //修改原始点GPS坐标对于原始点GPS速度增加的比率,默认:1.3
    private static final int ConfigDoublePropertyKey_RTFillGPS_ModifyGPSAddRate = 11;
    //补点与原始点GPS最大间隔距离,默认:150m
    private static final int ConfigDoublePropertyKey_RTFillGPS_MaxGPSIntervalLen = 12;
    //高铁补点后出现的真实点离kml轨迹线的最大距离
    private static final int ConfigDoublePropertyKey_RTFillGPS_MaxPointLineLength = 13;
    //补点间隔
    private static final int ConfigDoublePropertyKey_RTFillGPS_DefaultFillGPSIntervalLen = 14;

    /**
     * 判到站参考两站运行时间比率,默认:0.7; 等于0表示判到站无参考时间限制
     */
    private static final int ConfigDoublePropertyKey_RTFillGPS_ReferenceTSRunTimeMSRate = 16;
    /**
     * 判到站参考最小瞬时速度m/s,默认:-9999; 小于等于-9999表示判到站无最小瞬时速度限制
     */
    private static final int ConfigDoublePropertyKey_RTFillGPS_RefMinInstantSpeedMperS = 18;
    /**
     * 最小距离
     */
    private static final int ConfigDoublePropertyKey_RTFillGPS_GPSPreferenceMaxDriftLen = 19;

    /**
     * 配置属性key:配置日志文件的存储路径
     */
    private static final int ConfigPropertyKey_String_LogFilePath = 1002;
    /**
     * 是否写自身日志信息 (APropertyValue =0: 不写日志, 非0: 写日志),默认:0
     */
    private static final int ConfigIntPropertyKey_RTFillGPS_IsWriteSelfLog = 10060;
    /**  */
    private static final int ConfigIntPropertyKey_RTFillGPS_IntervalTimeMS = 10061;
    /**
     * 停车等待时间
     */
    private static final int ConfigIntPropertyKey_RTFillGPS_MetroStopStationWaitTimeMS = 10062;
    /**
     * 陀螺仪信息间隔
     */
    private static final int ConfigIntPropertyKey_RTFillGPS_SendMetroRunParamIntervalTimeMS = 10064;
    /**
     * 持续时间阈值
     */
    private static final int ConfigIntPropertyKey_RTFillGPS_ContinueCheckTimeMS = 10065;
    /**
     * 持续陀螺仪数据个数
     */
    private static final int ConfigIntPropertyKey_RTFillGPS_StationContinueCheckCount = 10066;
    /**
     * 配置属性key:地铁测试模式(1:自动模式,2:手动模式),默认:1
     */
    private static final int ConfigIntPropertyKey_RTFillGPS_MetroTestModel = 10067;
    /**
     * 配置属性key:地铁在自动模式下计算运行距离的方式(1:加速度方式,2:匀速方式),默认:1
     */
    private static final int ConfigIntPropertyKey_RTFillGPS_MetroCalcRunDistanceMode = 10068;
    /**
     * 地铁到站大于最大空白距离时最大连续补点个数(用于连续补点)
     */
    private static final int ConfigIntPropertyKey_RTFillGPS_StationMaxContinueFillGPSCount = 10071;
    //初始过滤加速度个数
    private static final int ConfigIntPropertyKey_RTFillGPS_IniFilterAccCount = 10072;
    //启停模式方式（0：原方式，1：L3三合一，格纳微输入启停状态的）
//    private static final int ConfigIntPropertyKey_RTFillGPS_MSSMR_AccDealMode = 10075;

    //地铁补点模式
    public static final int RTFillGPS_Mode_Subway = 0;
    //高铁GPS入参补点模式
    public static final int RTFillGPS_Mode_Railway = 1;
    //高铁GPS + 陀螺仪 扩展模式
    public static final int RTFillGPS_Mode_RailwayPro = 2;
    /**
     * 属性key:线路标识ID或站点标识ID
     */
    private static final int CNST_DMMSDataManager_KeyDefine_ID = 1;
    /**
     * 属性key:线路开始时间
     */
    private static final int CNST_DMMSDataManager_KeyDefine_StartTime = 4;
    /**
     * 属性key:线路结尾时间
     */
    private static final int CNST_DMMSDataManager_KeyDefine_EndTime = 5;
    /**
     * 属性key:线路名称或站点名称
     */
    private static final int CNST_DMMSDataManager_KeyDefine_Name = 2;
    /**
     * 属性key:站点时刻表首班
     */
    private static final int CNST_DMMSDataManager_KeyDefine_StationTime = 8;
    /**
     * 属性key:经度
     */
    private static final int CNST_DMMSDataManager_KeyDefine_Longitude = 6;
    /**
     * 属性key:纬度
     */
    private static final int CNST_DMMSDataManager_KeyDefine_Latitude = 7;
//瞬时速度计算方式(0: 距离算平均速度(原方式), 1: 加速度计算), 默认:0
private static final int  ConfigIntPropertyKey_RTFillGPS_InstantSpeedCalcType = 10073;
//距离算平均速度的速度最大限制, 默认:360
private static final int ConfigIntPropertyKey_RTFillGPS_CalcAvgSpeedMaxLimit = 10074;


    //一些测试用到的阈值
    private static double JY901_MIN_START_VALUE_HS = 0.02;
    private static double JY901_MAX_REDUCE_VALUE_HS = -0.02;
    private static double JY901_MIN_STOP_VALUE_HS = -0.02;
    private static double JY901_MAX_STOP_VALUE_HS = 0.02;
    //格纳微测试用到的阈值
    private static double GNV_MIN_START_VALUE_HS = 0.2;
    private static double GNV_MAX_REDUCE_VALUE_HS = -0.2;
    private static double GNV_MIN_STOP_VALUE_HS = -0.1;
    private static double GNV_MAX_STOP_VALUE_HS = 0.1;
    private static double GNV_MIN_ADJUST_VALUE = 0.0;
    private static double GNV_MAX_ADJUST_VALUE = 0.0;

    //一些测试用到的阈值
    private static double JY901_MIN_START_VALUE_METRO = 0.05;
    private static double JY901_MAX_REDUCE_VALUE_METRO = -0.05;
    private static double JY901_MIN_STOP_VALUE_METRO = -0.05;
    private static double JY901_MAX_STOP_VALUE_METRO = 0.05;

    private static double JY901_MIN_ADJUST_VALUE = 0.0;
    private static double JY901_MAX_ADJUST_VALUE = 0.0;
    private static double JY901_MAX_DRIF_LEN_VALUE = 100.0;

    private static double JY901_RUN_TIME_RATE_VALUE = 0.9;
    private static double JY901_SPEED_RATE_VALUE = 1.0;


    /*************************/
    //属性
    /*************************/
    /**
     * 解密密钥
     */
    private static final String ASecretKey = null;
    /**
     * ASecretKey的解密密钥
     */
    private static final String ASymmetricKey = null;
    /**
     * jni句柄
     */
    private int mHandler = -9999;
    /**
     * 当前解析的城市信息
     */
    private MetroCity mCity = null;
    /**
     * 当前是否正在测试中
     */
    private boolean isRunTest = false;
    /**
     * 当前是否已初始化
     */
    private boolean isInit = false;
    /**
     * 是否自动打点模式
     */
    private boolean isAutoMark = true;

    private int n_mode = RTFillGPS_Mode_Subway;

    private MetroUtil() {
        LOG_PATH = AppFilePathUtil.getInstance().getSDCardBaseDirectory("liblog");
    }

    /**
     * 返回唯一实例
     *
     * @return 唯一实例
     */
    public static MetroUtil getInstance() {
        if (sInstance == null)
            sInstance = new MetroUtil();
        return sInstance;
    }

    /**
     * 初始化
     *
     * @param city 城市对象
     */
    public void init(MetroCity city) {
        this.initWithMode(RTFillGPS_Mode_Subway);
        int flag = MetroJNI.ImportGPSAssitantMapFile(this.mHandler, city.getFilePath(), ASecretKey, ASymmetricKey);
        LogUtil.d(TAG, "-----init-----city:" + city.getName() + ";handler:" + this.mHandler + ";flag:" + flag + "-----");
        this.mCity = city;
    }

    /**
     * 读取城市文件的路线信息
     */
    public void readCityData() {
        if (this.mCity == null)
            return;
        LogUtil.d(TAG, "-----readCityData-----");
        int routeCount = MetroJNI.RTGetMetroLineCount(this.mHandler);
        List<MetroRoute> routes = new ArrayList<>();
        for (int routeIndex = 0; routeIndex < routeCount; routeIndex++) {
            MetroRoute route = new MetroRoute();
            route.setIndex(routeIndex);
            route.setId(MetroJNI.RTGetMetroLineIntKeyInfo(this.mHandler, routeIndex, CNST_DMMSDataManager_KeyDefine_ID));
            route.setStartTime(
                    MetroJNI.RTGetMetroLineIntKeyInfo(this.mHandler, routeIndex, CNST_DMMSDataManager_KeyDefine_StartTime));
            route.setEndTime(
                    MetroJNI.RTGetMetroLineIntKeyInfo(this.mHandler, routeIndex, CNST_DMMSDataManager_KeyDefine_EndTime));
            String routeName = MetroJNI.RTGetMetroLineStringKeyInfo(this.mHandler, routeIndex,
                    CNST_DMMSDataManager_KeyDefine_Name);
            routeName = routeName.substring(0, routeName.indexOf("("));
            route.setName(routeName);
            List<MetroStation> stations = new ArrayList<>();
            int stationCount = MetroJNI.RTGetMetroLineStationCount(this.mHandler, routeIndex);
            List<MyLatLng> kml = new ArrayList<>();
            for (int stationIndex = 0; stationIndex < stationCount; stationIndex++) {
                MetroStation station = new MetroStation();
                station.setIndex(stationIndex);
                station.setId(MetroJNI.RTGetMetroLineStationIntKeyInfo(this.mHandler, routeIndex, stationIndex,
                        CNST_DMMSDataManager_KeyDefine_ID));
                station.setFirstTime(MetroJNI.RTGetMetroLineStationIntKeyInfo(this.mHandler, routeIndex, stationIndex,
                        CNST_DMMSDataManager_KeyDefine_StationTime));
                station.setName(MetroJNI.RTGetMetroLineStationStringKeyInfo(this.mHandler, routeIndex, stationIndex,
                        CNST_DMMSDataManager_KeyDefine_Name));
                double longitude = MetroJNI.RTGetMetroLineStationDoubleKeyInfo(this.mHandler, routeIndex, stationIndex,
                        CNST_DMMSDataManager_KeyDefine_Longitude);
                double latitude = MetroJNI.RTGetMetroLineStationDoubleKeyInfo(this.mHandler, routeIndex, stationIndex,
                        CNST_DMMSDataManager_KeyDefine_Latitude);
                station.setLatLng(new MyLatLng(latitude, longitude));
                stations.add(station);
                if (stationIndex > 0) {
                    int count = MetroJNI.RTGetMetroLineStationSubItemCount(this.mHandler, routeIndex, stationIndex - 1,
                            stationIndex);
                    double lastLongitude = 0;
                    double lastLatitude = 0;
                    for (int index = 0; index < count; index++) {
                        longitude = MetroJNI.RTGetMetroLineStationSubItemDoubleKeyInfo(this.mHandler, routeIndex, stationIndex - 1,
                                stationIndex, index, CNST_DMMSDataManager_KeyDefine_Longitude);
                        latitude = MetroJNI.RTGetMetroLineStationSubItemDoubleKeyInfo(this.mHandler, routeIndex, stationIndex - 1,
                                stationIndex, index, CNST_DMMSDataManager_KeyDefine_Latitude);
                        if (longitude != lastLongitude && latitude != lastLatitude) {
                            lastLongitude = longitude;
                            lastLatitude = latitude;
                            kml.add(new MyLatLng(latitude, longitude));
                        }
                    }
                }
                kml.add(station.getLatLng());
            }
            route.setKml(kml);
            route.setStations(stations);
            routes.add(route);
        }
        this.mCity.setRoutes(routes);
    }

    /**
     * 开始测试线路
     *
     * @param route      要测试的线路
     * @param isAutoMark 是否自动打点
     */
    public void startTest(MetroRoute route, boolean isAutoMark) {
        if (this.isRunTest || route.getStartStation() == null || route.getEndStation() == null)
            return;
        LogUtil.d(TAG, "-----startTest-----route:" + route.getName());
        this.isAutoMark = isAutoMark;
        int testType = isAutoMark ? 1 : 2;
        MetroJNI.ConfigIntProperty(this.mHandler, ConfigIntPropertyKey_RTFillGPS_MetroTestModel, testType);
//        if (ApplicationModel.getInstance().getGlonavinType()==1){
//            MetroJNI.ConfigIntProperty(this.mHandler, ConfigIntPropertyKey_RTFillGPS_MSSMR_AccDealMode, 1);
//        }else {
//            MetroJNI.ConfigIntProperty(this.mHandler, ConfigIntPropertyKey_RTFillGPS_MSSMR_AccDealMode, 0);
//        }
        MetroJNI.SendMetroTestLineInfo(this.mHandler, route.getIndex(), route.getStartStation().getIndex(),
                route.getEndStation().getIndex(), -9999);
        MetroJNI.StartRTFillGPSPro(this.mHandler);
        this.isRunTest = true;
    }


    /**
     * 当前到达指定站点
     *
     * @param station 指定站点
     */
    public void reachStation(MetroStation station) {
        if (!this.isRunTest)
            return;
        LogUtil.d(TAG, "-----reachStation-----station:" + station.getName());
        MetroGPS info = new MetroGPS();
        info.Lat = station.getLatLng().latitude;
        info.Lon = station.getLatLng().longitude;
        MetroJNI.SendOriGPSInfo(this.mHandler, info,0);
    }

    /**
     * 从指定站点出发
     *
     * @param station 站点
     */
    public void startStation(MetroStation station) {
        if (!this.isRunTest)
            return;
        LogUtil.d(TAG, "-----startStation-----station:" + station.getName());
        MetroGPS info = new MetroGPS();
        info.Lat = station.getLatLng().latitude;
        info.Lon = station.getLatLng().longitude;
        MetroJNI.MetroRunFromStation(this.mHandler, info);
    }

    /**
     * 获取当前可以取到的GPS点数
     *
     * @return GPS点数
     */
    public int getCurrentGPSCount() {
        if (!this.isRunTest)
            return 0;
        int count = MetroJNI.GetRTFillGPSPointCount(this.mHandler);
        LogUtil.d(TAG, "-----getCurrentGPSCount-----" + count);
        return count;
    }

    /**
     * 获取当前的经纬度信息
     *
     * @return 当前的经纬度
     */
    public MetroGPS getCurrentGPS() {
        if (!this.isRunTest)
            return null;
        MetroGPS info = new MetroGPS();
        MetroJNI.ReadOneRTFillGPSPoint(this.mHandler, info);
        LogUtil.d(TAG, "-----getCurrentGPS-----lat:" + info.Lat + ",lon:" + info.Lon + ",type:" + info.Atype);
        return info;
    }

    /**
     * 设置当前的设备加速度
     *
     * @param acceleration 加速度值
     */
    public void setAcceleration(double acceleration) {
        if (!this.isAutoMark || !this.isRunTest||isInit==false)
            return;
        LogUtil.d(TAG, "-----setAcceleration-----acceleration:" + acceleration);
        MetroRunParamInfo info = new MetroRunParamInfo();
        info.Acceleration = acceleration;
        info.RunStatus=0;
        MetroJNI.SendMetroRunParamInfo(this.mHandler, info);
    }
    /**
     * 设置当前的设备加速度
     *
     * @param acceleration 加速度值
     */
    public void setAcceleration(double acceleration,int GNWStatus) {
        if (!this.isAutoMark || !this.isRunTest||isInit==false)
            return;
        LogUtil.d(TAG, "-----setAcceleration-----acceleration:" + acceleration);
        MetroRunParamInfo info = new MetroRunParamInfo();
        info.Acceleration = acceleration;
        info.GNWStatus=GNWStatus;
        MetroJNI.SendMetroRunParamInfo(this.mHandler, info);
    }
    /**
     * 设置当前的经纬度
     *
     */
    public void setOrgGPS(MetroGPS gps) {
        if (!this.isAutoMark || !this.isRunTest)
            return;
        LogUtil.d(TAG, "-----MetroGPS-----:" + gps);
        if (n_mode==RTFillGPS_Mode_RailwayPro){
            MetroJNI.SendOriGPSInfo(mHandler,gps,-1);
        }else {
            MetroJNI.SendOriGPSInfo(mHandler,gps,0);
        }
    }


    /**
     * 停止当前测试
     */
    public void stopTest() {
        if (!this.isRunTest)
            return;
        LogUtil.d(TAG, "-----stopTest-----");
        MetroJNI.EndRTFillGPSPro(this.mHandler);
        this.isRunTest = false;
    }

    /**
     * 反初始化
     */
    public void uninit() {
        if (!this.isInit)
            return;
        LogUtil.d(TAG, "-----uninit-----handler:" + this.mHandler);
        MetroJNI.FreeDMRealTimeFillGPS(this.mHandler);
        this.mCity = null;
        this.mHandler = -9999;
        isInit=false;
    }


    /**
     * 初始化
     *
     * @param mode 高铁线路
     *             城市对象
     */
    public void initWithMode(int mode) {
        if (mode != this.n_mode) {
            this.uninit();
        }
        if (this.isInit)
            return;
        this.n_mode = mode;
        this.mHandler = MetroJNI.CreateDMRealTimeFillGPS(this.n_mode);
        MetroJNI.ConfigStringProperty(this.mHandler, ConfigPropertyKey_String_LogFilePath, LOG_PATH);
        MetroJNI.ConfigIntProperty(this.mHandler, ConfigIntPropertyKey_RTFillGPS_IsWriteSelfLog, 1);
        if (mode == RTFillGPS_Mode_Railway) {
            //修改原始点GPS坐标对于原始点GPS速度增加的比率,默认:1.3
            MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_ModifyGPSAddRate, 1.3);
            //补点与原始点GPS最大间隔距离,默认:150m
            MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MaxGPSIntervalLen, 150);
        } else if (mode == RTFillGPS_Mode_RailwayPro) {
            if (ApplicationModel.getInstance().isGlonavinTest()&&ApplicationModel.getInstance().getGlonavinType()==1){
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_ReferenceTSRunTimeMSRate, JY901_RUN_TIME_RATE_VALUE);
                //判到起步
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MinStartThreshold, GNV_MIN_START_VALUE_HS);
                //判到减速
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MaxReduceThreshold, GNV_MAX_REDUCE_VALUE_HS);
                //最小停止
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MinStopThreshold, GNV_MIN_STOP_VALUE_HS);
                //最大停止
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MaxStopThreshold, GNV_MAX_STOP_VALUE_HS);
                //加速度累计
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_SpeedAddRate, JY901_SPEED_RATE_VALUE);
                //最小对齐
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MinRepairAdjustThreshold, GNV_MIN_ADJUST_VALUE);
                //最大对齐
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MaxRepairAdjustThreshold, GNV_MAX_ADJUST_VALUE);
                //地铁在自动模式下计算运行距离的方式(1:加速度方式,2:匀速方式),默认:1
                MetroJNI.ConfigIntProperty(this.mHandler, ConfigIntPropertyKey_RTFillGPS_MetroCalcRunDistanceMode, 2);
            }else {
                //
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_ReferenceTSRunTimeMSRate, JY901_RUN_TIME_RATE_VALUE);
                //判到起步
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MinStartThreshold, JY901_MIN_START_VALUE_HS);
                //判到减速
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MaxReduceThreshold, JY901_MAX_REDUCE_VALUE_HS);
                //最小停止
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MinStopThreshold, JY901_MIN_STOP_VALUE_HS);
                //最大停止
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MaxStopThreshold, JY901_MAX_STOP_VALUE_HS);
                //加速度累计
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_SpeedAddRate, JY901_SPEED_RATE_VALUE);
                //最小对齐
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MinRepairAdjustThreshold, JY901_MIN_ADJUST_VALUE);
                //最大对齐
                MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MaxRepairAdjustThreshold, JY901_MAX_ADJUST_VALUE);
                //地铁在自动模式下计算运行距离的方式(1:加速度方式,2:匀速方式),默认:1
                MetroJNI.ConfigIntProperty(this.mHandler, ConfigIntPropertyKey_RTFillGPS_MetroCalcRunDistanceMode, 2);
            }
//            if (ApplicationModel.getInstance().getGlonavinType()==1){
//                MetroJNI.ConfigIntProperty(this.mHandler, ConfigIntPropertyKey_RTFillGPS_MSSMR_AccDealMode, 1);
//            }else {
//                MetroJNI.ConfigIntProperty(this.mHandler, ConfigIntPropertyKey_RTFillGPS_MSSMR_AccDealMode, 0);
//            }
        } else {
            //地铁在自动模式下计算运行距离的方式(1:加速度方式,2:匀速方式),默认:1
            MetroJNI.ConfigIntProperty(this.mHandler, ConfigIntPropertyKey_RTFillGPS_MetroCalcRunDistanceMode, 2);
            MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_RefMinInstantSpeedMperS, -9999);
            MetroJNI.ConfigIntProperty(this.mHandler, ConfigIntPropertyKey_RTFillGPS_StationMaxContinueFillGPSCount, 50);
            MetroJNI.ConfigIntProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_DefaultFillGPSIntervalLen, 10);
            //判到起步
            MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MinStartThreshold, JY901_MIN_START_VALUE_METRO);
            //判到减速
            MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MaxReduceThreshold, JY901_MAX_REDUCE_VALUE_METRO);
            //最小停止
            MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MinStopThreshold, JY901_MIN_STOP_VALUE_METRO);
            //最大停止
            MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MaxStopThreshold, JY901_MAX_STOP_VALUE_METRO);
            //加速度累计
            MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_SpeedAddRate, JY901_SPEED_RATE_VALUE);
            //最小对齐
            MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MinRepairAdjustThreshold, JY901_MIN_ADJUST_VALUE);
            //最大对齐
            MetroJNI.ConfigDoubleProperty(this.mHandler, ConfigDoublePropertyKey_RTFillGPS_MaxRepairAdjustThreshold, JY901_MAX_ADJUST_VALUE);
        }

        LogUtil.d(TAG, "-----init-----RTFillMode:" + mode + ";handle:" + this.mHandler);
        this.isInit = true;
    }

    /**
     * 读取城市文件的路线信息
     */
    public boolean readHSRTrainData(HighSpeedNoModel train) {
        if (train == null)
            return false;
        //判断是否已经有缓存
        if (train.routes != null) {
            return true;
        }
        //导入xml
        int flag = MetroJNI.ImportGPSAssitantMapFile(this.mHandler, train.noPath, ASecretKey, ASymmetricKey);
        LogUtil.d(TAG, "-----readHSRTrainData-----");
        int routeCount = MetroJNI.RTGetMetroLineCount(this.mHandler);
        if (routeCount > 0) {
            int routeIndex = 0;
            MetroRoute route = new MetroRoute();
            route.setIndex(0);
            route.setId(MetroJNI.RTGetMetroLineIntKeyInfo(this.mHandler, routeIndex, CNST_DMMSDataManager_KeyDefine_ID));
            route.setStartTime(
                    MetroJNI.RTGetMetroLineIntKeyInfo(this.mHandler, routeIndex, CNST_DMMSDataManager_KeyDefine_StartTime));
            route.setEndTime(
                    MetroJNI.RTGetMetroLineIntKeyInfo(this.mHandler, routeIndex, CNST_DMMSDataManager_KeyDefine_EndTime));
//            String routeName = MetroJNI.RTGetMetroLineStringKeyInfo(this.mHandler, routeIndex,
//                    CNST_DMMSDataManager_KeyDefine_Name);
//            routeName = routeName.substring(0, routeName.indexOf("("));
            route.setName("");
            List<MetroStation> stations = new ArrayList<>();
            int stationCount = MetroJNI.RTGetMetroLineStationCount(this.mHandler, routeIndex);
            List<MyLatLng> kml = new ArrayList<>();
            for (int stationIndex = 0; stationIndex < stationCount; stationIndex++) {
                MetroStation station = new MetroStation();
                station.setIndex(stationIndex);
                station.setId(MetroJNI.RTGetMetroLineStationIntKeyInfo(this.mHandler, routeIndex, stationIndex,
                        CNST_DMMSDataManager_KeyDefine_ID));
                station.setFirstTime(MetroJNI.RTGetMetroLineStationIntKeyInfo(this.mHandler, routeIndex, stationIndex,
                        CNST_DMMSDataManager_KeyDefine_StationTime));
                station.setName(MetroJNI.RTGetMetroLineStationStringKeyInfo(this.mHandler, routeIndex, stationIndex,
                        CNST_DMMSDataManager_KeyDefine_Name));
                double longitude = MetroJNI.RTGetMetroLineStationDoubleKeyInfo(this.mHandler, routeIndex, stationIndex,
                        CNST_DMMSDataManager_KeyDefine_Longitude);
                double latitude = MetroJNI.RTGetMetroLineStationDoubleKeyInfo(this.mHandler, routeIndex, stationIndex,
                        CNST_DMMSDataManager_KeyDefine_Latitude);
                station.setLatLng(new MyLatLng(latitude, longitude));
                stations.add(station);
                if (stationIndex > 0) {
                    int count = MetroJNI.RTGetMetroLineStationSubItemCount(this.mHandler, routeIndex, stationIndex - 1,
                            stationIndex);
                    double lastLongitude = 0;
                    double lastLatitude = 0;
                    for (int index = 0; index < count; index++) {
                        longitude = MetroJNI.RTGetMetroLineStationSubItemDoubleKeyInfo(this.mHandler, routeIndex, stationIndex - 1,
                                stationIndex, index, CNST_DMMSDataManager_KeyDefine_Longitude);
                        latitude = MetroJNI.RTGetMetroLineStationSubItemDoubleKeyInfo(this.mHandler, routeIndex, stationIndex - 1,
                                stationIndex, index, CNST_DMMSDataManager_KeyDefine_Latitude);
                        if (longitude != lastLongitude && latitude != lastLatitude) {
                            lastLongitude = longitude;
                            lastLatitude = latitude;
                            kml.add(new MyLatLng(latitude, longitude));
                        }
                    }
                }
                kml.add(station.getLatLng());
            }
            route.setKml(kml);
            route.setStations(stations);
            train.routes=route;
            return true;
        }
        return false;
    }

    public void startHighSpeedTest(HighSpeedLineModel line, HighSpeedNoModel train, MetroRoute route, boolean isAutoMark) {
        if (this.isRunTest)
            return;
        if (line == null) {
            return;
        }
        LogUtil.d(TAG, "-----startHighSpeedTest-----HighSpeedRailLine:" + line.hsPath);
        int flag = MetroJNI.ImportGPSAssitantMapFile(this.mHandler, line.hsPath, this.ASecretKey, this.ASymmetricKey);
        if (flag != 1)
            return;
        LogUtil.d(TAG, "-----import success-----HighSpeedRailLine:" + line.hsname);
        if (train != null&&route!=null) {
            flag = MetroJNI.ImportGPSAssitantMapFile(this.mHandler, train.noPath, this.ASecretKey, this.ASymmetricKey);
            if (flag != 1)
                return;
            LogUtil.d(TAG, "-----import success-----HighSpeedRailTrain:" + train.noName);

            this.isAutoMark = isAutoMark;
            int testType = isAutoMark ? 1 : 2;
            MetroJNI.ConfigIntProperty(this.mHandler, ConfigIntPropertyKey_RTFillGPS_MetroTestModel, testType);
            MetroJNI.SendMetroTestLineInfo(this.mHandler, route.getIndex(), route.getStartStation().getIndex(),
                    route.getEndStation().getIndex(), -9999);
            LogUtil.d(TAG, "-----startHighSpeedTest-----route:" + route.getName());
        }
        MetroJNI.StartRTFillGPSPro(this.mHandler);
        this.isRunTest = true;
    }


}
