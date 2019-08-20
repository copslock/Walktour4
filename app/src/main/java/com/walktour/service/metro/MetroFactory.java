package com.walktour.service.metro;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dingli.watcher.model.MetroGPS;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.FtpOperate;
import com.walktour.Utils.FtpTranserStatus.DownloadStatus;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.AssetsWriter;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.service.metro.model.MetroCity;
import com.walktour.service.metro.model.MetroRoute;
import com.walktour.service.metro.model.MetroStation;
import com.walktour.service.metro.utils.MetroUtil;

import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 地铁线路工厂类
 *
 * @author jianchao.wang
 */
public class MetroFactory {
    /**
     * 日志标识
     */
    private static final String TAG = "MetroFactory";
    /**
     * FTP服务器IP
     */
    private static final String[] FTP_SERVER_IPS = {"61.143.60.84", "112.91.151.37", "172.16.23.217"};
    /**
     * FTP服务器端口
     */
    private static final int[] FTP_SERVER_PORTS = {64001, 64001, 21};
    /**
     * FTP服务器用户
     */
    private static final String FTP_SERVER_USER = "dinglicom";
    /**
     * FTP服务器密码
     */
    private static final String FTP_SERVER_PASSWORD = "p@sswOrd";
    /**
     * 选择的城市文件名称
     */
    private static final String SELECT_CITY_NAME = "select_metro_city_name";
    /**
     * 选择的城市路线ID
     */
    private static final String SELECT_ROUTE_ID = "select_metro_route_id";
    /**
     * 选择的城市路线起始和到达站点
     */
    private static final String SELECT_ROUTE_STATIONIDS = "select_metro_route_stationids";
    /**
     * 唯一实例
     */
    private static MetroFactory sInstance;
    /**
     * 所有城市列表
     */
    private List<MetroCity> mCities = new ArrayList<MetroCity>();
    /**
     * 城市映射表<城市名称，城市对象>
     */
    private Map<String, MetroCity> mCityMap = new HashMap<String, MetroCity>();
    /**
     * 路线映射表<路线名称，路线对象>
     */
    private Map<String, MetroRoute> mRouteMap = new HashMap<String, MetroRoute>();
    /**
     * 地铁线路的存放路径
     */
    public File mBaseFile;
    /**
     * 当前选择的城市
     */
    private MetroCity mCurrentCity;
    /**
     * 当前选择的线路
     */
    private MetroRoute mCurrentRoute;
    /**
     * 当前选择的线路的起始站点列表
     */
    private List<MetroStation> mCurrentStations = new ArrayList<MetroStation>();
    /**
     * ftp工具类
     */
    private FtpOperate mFtpUtil;
    /**
     * 服务器上的最新版本
     */
    private FTPFile mRemoteFile = null;
    /**
     * 是否正在测试业务
     */
    private boolean isRuning = false;
    /**
     * 是否自动打点模式
     */
    private boolean isAutoMark = false;
    /**
     * 是否有自动测试的模拟数据
     */
    private boolean hasAutoTestData = false;
    /**
     * 自动测试的模拟数据
     */
    private List<Float> mAutoTestDatas = new ArrayList<Float>();
    /**
     * 当前站的站点序号
     */
    private int mCurrentStationIndex = -1;
    /**
     * 实时采集的加速度值
     */
    private List<String> mAccelerationsList = new ArrayList<String>();
    /**
     * 实时采集的加速度值存放文件
     */
    private static final String ACCELERATION_FILE_NAME = "acceleration.csv";
    /**
     * 数据资源文件名
     */
    private static final String ASSETS_FILE_NAME = "Metro_20160828.zip";
    /**
     * 回放选择的城市名称
     */
    private String mReplayCityName;
    /**
     * 回放选择的路线名称
     */
    private String mReplayRouteName;
    /**
     * 底层库工具类
     */
    private MetroUtil mUtil;
    /**
     * 日期格式
     */
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    /**
     * 写加速度值文件
     */
    private WriteFileThread mWriteFileThread;
    /**
     * 属性对象名称
     */
    private String mSharedPrefsName;
    /**
     * 地铁最新发布包MD5, 例如:7A0768E1065DDE7C4B50029159B76827
     */

    private MetroFactory(Context context) {
        this.mUtil = MetroUtil.getInstance();
        this.mFtpUtil = new FtpOperate(context);
        this.mBaseFile = new File(AppFilePathUtil.getInstance().createSDCardBaseDirectory("metro"));
        this.mSharedPrefsName = context.getPackageName() + "_metro";
        this.checkIsFirstTime(context);
        this.init(context);
    }

    /**
     * 返回唯一实例
     *
     * @return 唯一实例
     */
    public static MetroFactory getInstance(Context context) {
        if (sInstance == null)
            sInstance = new MetroFactory(context);
        return sInstance;
    }

    /**
     * 读取自动化测试模拟数据
     */
    private void readAutoTestData() {
        this.hasAutoTestData = false;
        this.mAutoTestDatas.clear();
        File file = new File(this.mBaseFile.getAbsolutePath() + File.separator + "test");
        if (!file.exists()) {
            file.mkdirs();
            return;
        }
        if (file.isFile())
            return;
        if (file.listFiles().length > 0) {
            file = file.listFiles()[0];
            if (file.isFile()) {
                InputStreamReader read = null;
                BufferedReader bufferedReader = null;
                try {
                    read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                    bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while ((lineTxt = bufferedReader.readLine()) != null) {
                        String[] ls = lineTxt.split(",");
                        for (int i = 0; i < ls.length; i++) {
                            if (StringUtil.isDouble(ls[i])) {
                                float value = Float.parseFloat(ls[i].trim());
                                this.mAutoTestDatas.add(value);
                                break;
                            }
                        }
                    }
                    if (!this.mAutoTestDatas.isEmpty())
                        this.hasAutoTestData = true;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (read != null) {
                            read.close();
                            read = null;
                        }
                        if (bufferedReader != null) {
                            bufferedReader.close();
                            bufferedReader = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 获取模拟的加速度
     *
     * @param pos 数据位置
     * @return
     */
    public float getTestAcceleration(int pos) {
        if (pos >= 0 && pos < this.mAutoTestDatas.size()) {
            return this.mAutoTestDatas.get(pos);
        }
        return 0;
    }

    /**
     * 保存加速度值到文件中
     *
     * @param acceleration 重力加速度值
     */
    public void saveAccelerationToFile(float acceleration) {
        LogUtil.d(TAG, "-----saveAccelerationToFile-----" + acceleration);
        StringBuilder sb = new StringBuilder();
        sb.append(this.mFormat.format(new Date())).append(",").append(acceleration);
        this.mAccelerationsList.add(sb.toString());
    }

    /**
     * 检测当前是否是第一次调用地铁项目
     *
     * @param context 上下文
     */
    private void checkIsFirstTime(Context context) {
        if (this.mBaseFile.exists() && this.mBaseFile.listFiles().length > 0)
            return;
        String desFile = this.mBaseFile.getAbsolutePath() + File.separator + ASSETS_FILE_NAME;
        AssetsWriter writer = new AssetsWriter(context, "files/"+ASSETS_FILE_NAME, new File(desFile), false);
        writer.writeBinFile();
        try {
            ZipUtil.unzip(desFile, this.mBaseFile.getAbsolutePath(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化数据
     *
     * @param context 上下文
     */
    public void init(Context context) {
        this.mCities.clear();
        this.mCityMap.clear();
        this.mCurrentCity = null;
        this.mCurrentRoute = null;
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String parentPath = this.mBaseFile.getAbsolutePath() + File.separator + "Metro" + File.separator;
        if (language.endsWith("zh"))
            parentPath += "cn";
        else
            parentPath += "en";
        File parentFile = new File(parentPath);
        if (!parentFile.exists() || parentFile.isFile())
            return;
        for (File file : parentFile.listFiles()) {
            if (file.isFile()) {
                MetroCity city = new MetroCity();
                if (file.getName().endsWith("xml"))
                    city.setName(file.getName().substring(0, file.getName().length() - 4));
                else if (file.getName().endsWith("json"))
                    city.setName(file.getName().substring(0, file.getName().length() - 5));
                city.setFilePath(file.getAbsolutePath());
                this.mCities.add(city);
                this.mCityMap.put(city.getName(), city);
            }
        }
        SharedPreferences preference = context.getSharedPreferences(this.mSharedPrefsName, Context.MODE_PRIVATE);
        String selectCityName = preference.getString(SELECT_CITY_NAME, "");
        if (this.mCityMap.containsKey(selectCityName)) {
            MetroCity city = this.mCityMap.get(selectCityName);
            this.setCurrentCity(context, city, false);
            long selectRouteId = preference.getLong(SELECT_ROUTE_ID, 0);
            MetroRoute route = city.getRoute(selectRouteId);
            if (route != null) {
                String selectRouteStationIds = preference.getString(SELECT_ROUTE_STATIONIDS, "");
                if (!StringUtil.isNullOrEmpty(selectRouteStationIds)) {
                    String[] stationIds = selectRouteStationIds.split("_");
                    for (MetroStation station : route.getStations()) {
                        if (station.getId() == Long.parseLong(stationIds[0]))
                            route.setStartStation(station);
                        else if (station.getId() == Long.parseLong(stationIds[1]))
                            route.setEndStation(station);
                    }
                }
                this.setCurrentRoute(context, route, false);
            }
        }
    }

    /**
     * 读取json格式的城市文件数据
     *
     * @param city 城市对象
     */
    private void readCityJsonData(MetroCity city) {
        try {
            List<MetroRoute> routes = new ArrayList<MetroRoute>();
            JSONTokener jsonParser = new JSONTokener(FileUtil.getStringFromFile(new File(city.getFilePath())));
            JSONArray routeArray = (JSONArray) jsonParser.nextValue();
            for (int i = 0; i < routeArray.length(); i++) {
                JSONObject routeObj = (JSONObject) routeArray.get(i);
                MetroRoute route = new MetroRoute();
                route.setName(routeObj.getString("name"));
                route.setId(routeObj.getLong("index"));
                JSONArray stationArray = routeObj.getJSONArray("stations");
                List<MetroStation> stations = new ArrayList<MetroStation>();
                for (int j = 0; j < stationArray.length(); j++) {
                    JSONObject stationObj = (JSONObject) stationArray.get(j);
                    MetroStation station = new MetroStation();
                    station.setId(Integer.parseInt(stationObj.getString("indexPosition")));
                    station.setName(stationObj.getString("stationName"));
                    MyLatLng latlng = new MyLatLng(Double.parseDouble(stationObj.getString("stationLat")),
                            Double.parseDouble(stationObj.getString("stationLong")));
                    station.setLatLng(latlng);
                    stations.add(station);
                    route.getKml().add(latlng);
                }
                route.setStations(stations);
                routes.add(route);
            }
            city.setRoutes(routes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取xml格式的城市文件数据
     *
     * @param city 城市对象
     */
    private void readCityXmlData(MetroCity city) {
        MetroUtil.getInstance().init(city);
        MetroUtil.getInstance().readCityData();
        MetroUtil.getInstance().uninit();
    }

    public List<MetroCity> getCities() {
        return mCities;
    }

    /**
     * 获取指定城市名称的城市对象
     *
     * @param cityName 城市名称
     * @return
     */
    public MetroCity getCityByName(String cityName) {
        if (this.mCityMap.containsKey(cityName))
            return this.mCityMap.get(cityName);
        return null;
    }

    /**
     * 下载服务器端的文件
     */
    public boolean downloadFile() {
        if (this.mRemoteFile == null)
            return false;
        try {
            String zipFilePath = this.mBaseFile.getAbsolutePath() + File.separator + this.mRemoteFile.getName();
            DownloadStatus downStatus = this.mFtpUtil.download("/Metro/" + this.mRemoteFile.getName(), zipFilePath, false,
                    false);
            if (DownloadStatus.Download_From_Break_Success == downStatus
                    || DownloadStatus.Download_New_Success == downStatus) {
                this.deleteOldFiles();
                //服务端压缩文件为GBK格式，所以此处传入GBK解决解压缩出来乱码问题
                ZipUtil.unzip(zipFilePath, this.mBaseFile.getAbsolutePath(), "GBK", false);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                this.mFtpUtil.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 删除旧版本文件
     */
    private void deleteOldFiles() {
        File[] files = this.mBaseFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".zip")) {
                continue;
            }
            this.deleteFile(files[i]);
        }
    }

    /**
     * 删除文件
     *
     * @param file 文件
     */
    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                this.deleteFile(files[i]);
            }
        }
        file.delete();
    }

    /**
     * 判断服务器端的文件是否有更新
     */
    public boolean checkFileHasNewVersion() {
        File localFile = null;
        for (File file : this.mBaseFile.listFiles()) {
            if (file.getName().startsWith("Metro_") && file.getName().endsWith(".zip")) {
                localFile = file;
                break;
            }
        }
        for (int i = 0; i < FTP_SERVER_IPS.length; i++) {
            String ip = FTP_SERVER_IPS[i];
            int port = FTP_SERVER_PORTS[i];
            try {
                boolean flag = this.mFtpUtil.connect(ip, port, FTP_SERVER_USER, FTP_SERVER_PASSWORD, 5 * 1000);
                if (flag) {
                    FTPFile[] files = this.mFtpUtil.listFiles("/Metro", false);
                    int lastData = 0;
                    for (FTPFile file : files) {
                        if (file.getName().startsWith("Metro_") && file.getName().endsWith(".zip")) {
                            try {
                                int data = Integer.parseInt(file.getName().substring(6, 14));
                                if (data > lastData) {
                                    this.mRemoteFile = file;
                                    lastData = data;
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (this.mRemoteFile != null) {
            if (localFile == null)
                return true;
            try {
                int remoteData = Integer.parseInt(this.mRemoteFile.getName().substring(6, 14));
                int localData = Integer.parseInt(localFile.getName().substring(6, 14));
                if (remoteData > localData) {
                    localFile.delete();
                    return true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        try {
            this.mFtpUtil.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前的城市
     *
     * @param isReplay 当前是否回放中
     * @return
     */
    public MetroCity getCurrentCity(boolean isReplay) {
        if (isReplay) {
            if (this.mCityMap.containsKey(this.mReplayCityName)) {
                return this.mCityMap.get(this.mReplayCityName);
            }
            return null;
        }
        return mCurrentCity;
    }

    /**
     * 设置当前选择的城市
     *
     * @param context     上下文
     * @param currentCity 当前城市
     * @param save        是否保存到数据中
     */
    public void setCurrentCity(Context context, MetroCity currentCity, boolean save) {
        if (this.mCurrentCity != null && !this.mCurrentCity.equals(currentCity)) {
            if (this.mCurrentRoute != null) {
                this.mCurrentRoute.setStartStation(null);
                this.mCurrentRoute.setEndStation(null);
                this.mCurrentRoute.initStationState();
                this.mCurrentRoute = null;
            }
        }
        this.mCurrentCity = currentCity;
        if (save) {
            SharedPreferences preference = context.getSharedPreferences(this.mSharedPrefsName, Context.MODE_PRIVATE);
            Editor editor = preference.edit();
            editor.putString(SELECT_CITY_NAME, this.mCurrentCity.getName());
            editor.apply();
        }
        this.readCityData(this.mCurrentCity);
    }

    /**
     * 读取城市数据
     *
     * @param city
     */
    private void readCityData(MetroCity city) {
        if (!city.getRoutes().isEmpty())
            return;
        if (city.getFilePath().endsWith("json"))
            this.readCityJsonData(city);
        else
            this.readCityXmlData(city);
        for (MetroRoute route : city.getRoutes()) {
            this.mRouteMap.put(route.getRouteFilterDesc(), route);
        }
    }

    /**
     * 获取当前的路线
     *
     * @param isReplay 当前是否回放中
     * @return
     */
    public MetroRoute getCurrentRoute(boolean isReplay) {
        if (isReplay) {
            if (this.mRouteMap.containsKey(this.mReplayRouteName)) {
                return this.mRouteMap.get(this.mReplayRouteName);
            }
            return null;
        }
        return mCurrentRoute;
    }

    /**
     * 设置当前选择的路线
     *
     * @param context      上下文
     * @param currentRoute 当前路线
     * @param save         是否保存到数据中
     */
    public void setCurrentRoute(Context context, MetroRoute currentRoute, boolean save) {
        if (this.mCurrentRoute != null && !this.mCurrentRoute.equals(currentRoute)) {
            this.mCurrentRoute.setStartStation(null);
            this.mCurrentRoute.setEndStation(null);
            this.mCurrentRoute.initStationState();
        }
        this.mCurrentRoute = currentRoute;
        if (save) {
            SharedPreferences preference = context.getSharedPreferences(this.mSharedPrefsName, Context.MODE_PRIVATE);
            Editor editor = preference.edit();
            editor.putLong(SELECT_ROUTE_ID, this.mCurrentRoute.getId());
            editor.putString(SELECT_ROUTE_STATIONIDS,
                    this.mCurrentRoute.getStartStation().getId() + "_" + this.mCurrentRoute.getEndStation().getId());
            editor.commit();
        }
        this.mCurrentRoute.initStationState();
        this.mCurrentStations.clear();
        List<MetroStation> list = this.mCurrentRoute.getStations();
        for (int i = 0; i < list.size(); i++) {
            MetroStation station = list.get(i);
            switch (station.getState()) {
                case MetroStation.STATE_CAN_SELECT:
                case MetroStation.STATE_START:
                case MetroStation.STATE_END:
                    this.mCurrentStations.add(station);
                    break;
            }
        }
        this.mCurrentStationIndex = -1;
    }

    public boolean isRuning() {
        return isRuning;
    }

    /**
     * 获得当前所在的地铁站
     *
     * @return
     */
    public MetroStation getCurrentStation() {
        if (this.mCurrentStationIndex < 0 && !this.mCurrentStations.isEmpty()) {
            this.mCurrentStationIndex = 0;
            if (!this.isAutoMark) {
                MetroStation station = this.mCurrentStations.get(this.mCurrentStationIndex);
                station.setReach(true);
            }
        }
        if (this.mCurrentStationIndex >= 0) {
            MetroStation station = this.mCurrentStations.get(this.mCurrentStationIndex);
            return station;
        }
        return null;
    }

    /**
     * 到达当前站点
     *
     * @param isHand 是否手动到站
     */
    public void reachStation(boolean isHand) {
        MetroStation station = this.getCurrentStation();
        if (station != null && !station.isReach()) {
            LogUtil.d(TAG, "----reachStation----station:" + station.getName() + "----isHand:" + isHand);
            if (isHand)
                this.mUtil.reachStation(station);
            else
                station.setReach(true);
        }
    }

    /**
     * 从当前站点启动
     *
     * @param isHand 是否手动启动
     */
    public void startStation(boolean isHand) {
        MetroStation station = this.getCurrentStation();
        if (station != null && station.isReach()) {
            LogUtil.d(TAG, "----startStation----station:" + station.getName() + "----isHand:" + isHand);
            if (isHand)
                this.mUtil.startStation(station);
            this.mCurrentStationIndex++;
            if (this.mCurrentStationIndex >= this.mCurrentStations.size()) {
                this.mCurrentStationIndex = this.mCurrentStations.size() - 1;
                return;
            }
            station.setReach(false);
        }
    }

    public String getReplayCityName() {
        return mReplayCityName;
    }

    /**
     * 设置回放的城市
     *
     * @param replayCityName 回放的城市名称
     */
    public void setReplayCityName(String replayCityName) {
        this.mReplayCityName = replayCityName;
        if (!StringUtil.isNullOrEmpty(this.mReplayCityName)) {
            if (this.mCityMap.containsKey(this.mReplayCityName)) {
                MetroCity city = this.mCityMap.get(this.mReplayCityName);
                this.readCityData(city);
            }
        }
    }

    public String getReplayRouteName() {
        return mReplayRouteName;
    }

    public void setReplayRouteName(String replayRouteName) {
        mReplayRouteName = replayRouteName;
    }

    /**
     * 开始测试
     */
    public void startTest() {
        this.isAutoMark = MapFactory.getMapData().isAutoMark();
        LogUtil.d(TAG, "----startTest----isAutoMark:" + this.isAutoMark);
        this.readAutoTestData();
        this.isRuning = true;
        this.mCurrentStationIndex = -1;
        this.mUtil.init(this.mCurrentCity);
        this.mUtil.startTest(this.mCurrentRoute, isAutoMark);
        if (this.mWriteFileThread == null) {
            this.mWriteFileThread = new WriteFileThread();
            this.mWriteFileThread.start();
        }
    }

    /**
     * 开始测试
     */
    public void startGNVTest() {
        this.isAutoMark = MapFactory.getMapData().isAutoMark();
        this.mCurrentStationIndex = -1;
        LogUtil.d(TAG, "----startTest----isAutoMark:" + this.isAutoMark);
        this.isRuning = true;
    }

    /**
     * 开始测试
     */
    public void setStationIndex(int mCurrentStationIndex) {
        this.mCurrentStationIndex = mCurrentStationIndex;
    }
    /**
     * 停止测试
     */
    public void stopTest() {
        LogUtil.d(TAG, "----stopTest----isAutoMark:" + this.isAutoMark);
        this.isRuning = false;
        this.mUtil.stopTest();
        this.mUtil.uninit();
        if (this.mWriteFileThread != null) {
            this.mWriteFileThread.stopThread();
            this.mWriteFileThread = null;
        }
    }

    /**
     * 获取当前可以取到的GPS点数
     *
     * @return
     */
    public int getCurrentGPSCount() {
        return this.mUtil.getCurrentGPSCount();
    }

    /**
     * 获取当前底层库返回的GPS点
     *
     * @return
     */
    public MetroGPS getCurrentGPS() {
        return this.mUtil.getCurrentGPS();
    }

    /**
     * 设置当前的设备加速度
     *
     * @param acceleration 加速度值
     */
    public void setAcceleration(double acceleration) {
        this.mUtil.setAcceleration(acceleration);
    }

    /**
     * 是否最后一个站点
     *
     * @param station 站点对象
     * @return
     */
    public boolean isLastStation(MetroStation station) {
        if (this.mCurrentStations.isEmpty())
            return false;
        if (this.mCurrentStations.get(this.mCurrentStations.size() - 1).equals(station))
            return true;
        return false;
    }

    public boolean hasAutoTestData() {
        return hasAutoTestData;
    }

    /**
     * 写加速度文件线程
     *
     * @author jianchao.wang
     */
    private class WriteFileThread extends Thread {
        /**
         * 是否停止执行
         */
        private boolean isStop = false;
        /**
         * 间隔时间
         */
        private static final int INTEVER_TIME = 1000;
        /**
         * 日期格式
         */
        private SimpleDateFormat mFileNameFormat = new SimpleDateFormat("yyMMdd-HHmmss", Locale.getDefault());

        @Override
        public void run() {
            File file = new File(mBaseFile.getAbsolutePath() + File.separator + "acceleration");
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(file.getAbsolutePath() + File.separator + this.mFileNameFormat.format(new Date()) + "-"
                    + ACCELERATION_FILE_NAME);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                }
            }
            BufferedWriter writer = null;
            FileWriter fileWriter = null;

            try {
                fileWriter = new FileWriter(file, true);
                writer = new BufferedWriter(fileWriter);
                while (!isStop) {
                    try {
                        Thread.sleep(INTEVER_TIME);
                    } catch (InterruptedException e) {
                        break;
                    }
                    while (!mAccelerationsList.isEmpty()) {
                        writer.write(mAccelerationsList.remove(0));
                        writer.newLine();
                        writer.flush();
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                }
            }
        }

        /**
         * 停止线程
         */
        public void stopThread() {
            this.isStop = true;
            this.interrupt();
        }

    }
}
