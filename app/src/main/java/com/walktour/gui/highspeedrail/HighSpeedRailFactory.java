package com.walktour.gui.highspeedrail;

import android.content.Context;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.service.metro.model.MetroCity;
import com.walktour.service.metro.model.MetroRoute;
import com.walktour.service.metro.model.MetroStation;
import com.walktour.service.metro.utils.MetroUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/8/2
 * @describe 高铁工厂类
 */
public class HighSpeedRailFactory {
    /**
     * 日志标识
     */
    private static final String TAG = "HighSpeedRailFactory";
    /**
     * 选择的城市文件名称
     */
    private static final String SELECT_HS_NAME = "select_hs_city_name";
    /**
     * 选择的城市路线ID
     */
    private static final String SELECT_ROUTE_ID = "select_hs_route_id";
    /**
     * 选择的城市路线起始和到达站点
     */
    private static final String SELECT_ROUTE_STATIONIDS = "select_metro_route_stationids";
    /**
     * 唯一实例
     */
    private static HighSpeedRailFactory sInstance;
    /**
     * 所有城市列表
     */
    private List<MetroCity> mCities = new ArrayList<MetroCity>();
    /**
     * 数据资源文件名
     */
    private static final String ASSETS_FILE_NAME = "hs_5476AB43E52296CDFF7884D37EBE8FC2.zip";
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
     * 属性对象名称
     */
    private SharePreferencesUtil sharePreferencesUtil;

    private Context mContext;

    private HighSpeedRailFactory(Context context) {
        this.mContext = context;
        this.mBaseFile = new File(AppFilePathUtil.getInstance().createSDCardBaseDirectory("highspeedrail"));
        this.sharePreferencesUtil = SharePreferencesUtil.getInstance(context);
        this.checkIsFirstTime(context);
        this.init(context);
    }

    /**
     * 返回唯一实例
     *
     * @return 唯一实例
     */
    public static HighSpeedRailFactory getInstance(Context context) {
        if (sInstance == null)
            sInstance = new HighSpeedRailFactory(context);
        return sInstance;
    }


    /**
     * 检测当前是否是第一次调用高铁项目
     *
     * @param context 上下文
     */
    private void checkIsFirstTime(Context context) {
        if (this.mBaseFile.exists() && this.mBaseFile.listFiles().length > 0)
            return;
        copyFile();
    }

    /***
     * 拷贝本地文件作为默认线路
     */
    private void copyFile() {
        if (sharePreferencesUtil.getInteger(WalkMessage.KEY_HIGHSPEEDRAIL_VERSION) == 0) {// 没有拷贝文件到默认SD卡
            copyAssetDataToSD("files/" + ASSETS_FILE_NAME, AppFilePathUtil.getInstance().getSDCardBaseDirectory(HighSpeedRailCommons.High_SPEED_RAIL_PATH) + ASSETS_FILE_NAME);
            try {
                ZipUtil.unzip(AppFilePathUtil.getInstance().getSDCardBaseDirectory(HighSpeedRailCommons.High_SPEED_RAIL_PATH) + ASSETS_FILE_NAME, AppFilePathUtil.getInstance().getSDCardBaseDirectory(HighSpeedRailCommons.High_SPEED_RAIL_PATH), "GBK", true);
                String name = ASSETS_FILE_NAME.substring(0, ASSETS_FILE_NAME.lastIndexOf("."));
                String[] names = name.split("_");
                if (names.length >= 2) {
                    sharePreferencesUtil.saveInteger(WalkMessage.KEY_HIGHSPEEDRAIL_VERSION,
                            Integer.parseInt(names[1]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 拷贝Asset目录下的文件到指定目录下
     *
     * @param fileName
     *            源文件名
     * @param strOutFileName
     *            目标文件，包含路径
     * @throws IOException
     */
    private void copyAssetDataToSD(String fileName, String strOutFileName) {
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            myOutput = new FileOutputStream(strOutFileName);

            myInput = mContext.getAssets().open(fileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (null != myInput) {
                try {
                    myInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myInput = null;
            }
            if (null != myOutput) {
                try {
                    myOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myOutput = null;
            }
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
        String parentPath = this.mBaseFile.getAbsolutePath() + File.separator;//Walktour\highspeedrail\
        if (language.endsWith("zh"))
            parentPath += "cn";
        else
            parentPath += "en";
        File parentFile = new File(parentPath);//Walktour\highspeedrail\cn
        if (!parentFile.exists() || parentFile.isFile())
            return;
        for (File file : parentFile.listFiles()) {
            if (file.isDirectory()) {
                MetroCity city = new MetroCity();
                city.setName(file.getName());
                city.setFilePath(file.getAbsolutePath()+File.separator+file.getName());
                this.mCities.add(city);
                this.mCityMap.put(city.getName(), city);
            }
        }
        String selectCityName = sharePreferencesUtil.getString(SELECT_HS_NAME, "");
        if (this.mCityMap.containsKey(selectCityName)) {
            MetroCity city = this.mCityMap.get(selectCityName);
            this.setCurrentCity(context, city, true);
            long selectRouteId = sharePreferencesUtil.getLong(SELECT_ROUTE_ID, 0l);
            MetroRoute route = city.getRoute(selectRouteId);
            if (route != null) {
                String selectRouteStationIds = sharePreferencesUtil.getString(SELECT_ROUTE_STATIONIDS, "");
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
     * 设置当前选择的城市
     *
     * @param context     上下文
     * @param currentCity 当前城市
     * @param save        是否保存到数据中
     */
    public void setCurrentCity(Context context, MetroCity currentCity, boolean save) {
        if (this.mCurrentCity != null && !this.mCurrentCity.equals(currentCity)) {
            if (this.mCurrentRoute != null) {
                this.mCurrentRoute = null;
            }
        }
        this.mCurrentCity = currentCity;
        if (save) {
            sharePreferencesUtil.saveString(SELECT_HS_NAME, this.mCurrentCity.getName());
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
            this.readCityXmlData(city);
        for (MetroRoute route : city.getRoutes()) {
            this.mRouteMap.put(route.getRouteFilterDesc(), route);
        }
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
            sharePreferencesUtil.saveLong(SELECT_ROUTE_ID, this.mCurrentRoute.getId());
            sharePreferencesUtil.saveString(SELECT_ROUTE_STATIONIDS,
                    this.mCurrentRoute.getStartStation().getId() + "_" + this.mCurrentRoute.getEndStation().getId());
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
    }


}
