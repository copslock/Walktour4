package com.walktour.service.metro;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dingli.watcher.model.MetroGPS;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.FtpOperate;
import com.walktour.Utils.FtpTranserStatus.DownloadStatus;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalktourConst;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.AssetsWriter;
import com.walktour.gui.highspeedrail.model.HighSpeedLineModel;
import com.walktour.gui.highspeedrail.model.HighSpeedNoModel;
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
 * 高铁线路工厂类
 *
 * @author xiejinfeng
 */
public class HsFactory {
    /**
     * 日志标识
     */
    private static final String TAG = "HsFactory";

    private ApplicationModel appModel = null;
    /**
     * 唯一实例
     */
    private static HsFactory sInstance;
    /**
     * 当前选择的线路的起始站点列表
     */
    private List<MetroStation> mCurrentStations = new ArrayList<MetroStation>();
    /**
     * 当前站的站点序号
     */
    private int mCurrentStationIndex = -1;
    private int startStationIndex=0;
    private int endStationIndex=0;
    /**
     * 底层库工具类
     */
    private MetroUtil mUtil;

    /**
     * 记录高铁线路的对象
     */
    private HighSpeedNoModel mCurrentHS;

    /**
     * 地铁最新发布包MD5, 例如:7A0768E1065DDE7C4B50029159B76827
     */


    private HsFactory(Context context) {
        this.mUtil = MetroUtil.getInstance();
        this.appModel=ApplicationModel.getInstance();

    }

    /**
     * 返回唯一实例
     *
     * @return 唯一实例
     */
    public static HsFactory getInstance(Context context) {
        if (sInstance == null)
            sInstance = new HsFactory(context);
        return sInstance;
    }

    /**
     * 初始化数据
     *
     * @param context 上下文
     */
    public void init(Context context) {
        mCurrentHS = SharePreferencesUtil.getInstance(context).getObjectFromShare(WalktourConst.CURRENT_HS_NO, HighSpeedNoModel.class);
        if (mCurrentHS != null) {
            {
                if (mCurrentHS.getRoutes()==null){
                    return;
                }
                List<MetroStation> list = mCurrentHS.getRoutes().getStations();
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
                LogUtil.d(TAG, "" + mCurrentHS);
            }
        }
    }

            /**
             * 获取当前的路线
             *
             * @param isReplay 当前是否回放中
             * @return
             */
            public MetroRoute getCurrentRoute ( boolean isReplay){
                if (mCurrentHS == null || mCurrentHS.getRoutes() == null) {
                    return null;
                } else {
                    return mCurrentHS.getRoutes();
                }
            }


            public boolean isRuning () {
                return appModel.isTestJobIsRun() && appModel.getRuningScene() == WalkStruct.SceneType.HighSpeedRail;
            }
    /**
     * 获得当前所在的地铁站
     *
     * @return
     */
    public MetroStation getCurrentStation() {
        if (this.mCurrentStationIndex < 0 && !this.mCurrentStations.isEmpty()) {
            this.mCurrentStationIndex = 0;
                MetroStation station = this.mCurrentStations.get(this.mCurrentStationIndex);
                station.setReach(true);
        }
        if (this.mCurrentStationIndex >= 0) {
            MetroStation station = this.mCurrentStations.get(this.mCurrentStationIndex);
            LogUtil.d(TAG,"station："+station);
            return station;
        }

        return null;
    }

            /**
             * 到达当前站点
             *
             * @param isHand 是否手动到站
             */
            public void reachStation ( boolean isHand){
                MetroStation station = this.getCurrentStation();
                if (station != null && !station.isReach()) {
                    LogUtil.d(TAG, "----reachStation----station:" + station.getName() + "----isHand:" + isHand);
                    if (isHand)
                        this.mUtil.reachStation(station);
                    else
                        station.setReach(true);
                }
            }

            /**`
             * 从当前站点启动
             *
             * @param isHand 是否手动启动
             */
            public void startStation ( boolean isHand){
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

            /**
             * 是否最后一个站点
             *
             * @param station 站点对象
             * @return
             */
            public boolean isLastStation (MetroStation station){
                if (this.mCurrentStations.isEmpty())
                    return false;
                if (this.mCurrentStations.get(this.mCurrentStations.size() - 1).equals(station))
                    return true;
                return false;
            }

            /**
             * 开始测试
             */
            public void startTest () {

            }

            /**
             * 停止测试
             */
            public void stopTest () {

            }

}
