package com.walktour.gui.highspeedrail;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.IBinder;

import com.dingli.watcher.model.MetroGPS;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.gui.highspeedrail.model.HighSpeedLineModel;
import com.walktour.gui.highspeedrail.model.HighSpeedNoModel;
import com.walktour.service.metro.HsFactory;
import com.walktour.service.metro.model.MetroStation;
import com.walktour.service.metro.utils.MetroUtil;

/***
 * 高铁测试服务,主要调用库处理gps数据
 *
 * @author weirong.fan
 *
 */
public class HighSpeedGpsService extends Service {

    public static final String GPS_LOCATION_CHANGED = "com.walktour.service.metro.HighSpeedGpsService.gpsLocationChanged";
    @SuppressLint("HandlerLeak")

    /**
     * TAG
     **/
    private String TAG = HighSpeedGpsService.class.getSimpleName();
    private boolean isCollecting = false;

    /**
     * 全局对象
     **/
    private ApplicationModel appModel = ApplicationModel.getInstance();

    /**
     * 配置文件
     */
    private SharePreferencesUtil sharePreferencesUtil;

    private HsFactory mFactory;

    private MetroGPS mLastGps = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sharePreferencesUtil = SharePreferencesUtil.getInstance(this);
        mFactory =HsFactory.getInstance(this);
        IntentFilter broadCaseIntent = new IntentFilter();
        broadCaseIntent.addAction(GpsInfo.gpsLocationChanged);
        this.registerReceiver(gpsLocationChangeReceiver, broadCaseIntent);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LogUtil.w(TAG, "释放高铁接口库开始.");
            MetroUtil.getInstance().stopTest();
            MetroUtil.getInstance().uninit();
            isCollecting = false;
            this.unregisterReceiver(gpsLocationChangeReceiver);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            LogUtil.w(TAG, "释放高铁接口库完成.");
        }
    }

    /**
     * 当GPS状态为打开时,获取GPS消息
     */
    private final BroadcastReceiver gpsLocationChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GpsInfo.gpsLocationChanged) && appModel.isTestJobIsRun()
                    && !appModel.isTestStoping()) {
                LogUtil.e(TAG,"GService.isFileG:"+GService.isFileG);
                if (isCollecting && !GService.isFileG) {
                    Location location = GpsInfo.getInstance().getLocation();
                    if (null != location) {
                        if (0 != location.getSpeed()) {
//							ToastUtil.showToastShort(context, "当前速度是:"+location.getSpeed()*3.6f+"");
                        }
                        LogUtil.w(TAG, "[Lat=" + location.getLatitude() + ",Lon=" + location.getLongitude() + ",Speed=" + location.getSpeed() * 3.6f + "]");

                        MetroGPS gpsInfo = new MetroGPS();
                        gpsInfo.Lat = location.getLatitude();
                        gpsInfo.Lon = location.getLongitude();
                        gpsInfo.Speed = location.getSpeed() * 3.6f;
                        gpsInfo.Altitude = (float) location.getAltitude();
                        gpsInfo.Atype = 0;
                        MetroUtil.getInstance().setOrgGPS(gpsInfo);
                    }
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //获取是否采用新的高铁测试模式
        boolean isExtent = ConfigRoutine.getInstance().isHsExternalGPS(this);

        if (!isCollecting) {
            if (isExtent) {//新的模式
                //获取线路

                    HighSpeedNoModel train = SharePreferencesUtil.getInstance(this).getObjectFromShare(WalktourConst.CURRENT_HS_NO, HighSpeedNoModel.class);
                    if (train != null) {
                        MetroUtil.getInstance().initWithMode(MetroUtil.RTFillGPS_Mode_RailwayPro);
                        HighSpeedLineModel highSpeedLineModel=new HighSpeedLineModel();
                        highSpeedLineModel.setHsPath(train.getParentPath());
                        MetroUtil.getInstance().startHighSpeedTest(highSpeedLineModel, train, train.getRoutes(), true);
                        isCollecting = true;
                    }
            } else { //旧的模式
                //获取线路
                HighSpeedLineModel line = SharePreferencesUtil.getInstance(this).getObjectFromShare(WalktourConst.CURRENT_HS, HighSpeedLineModel.class);
//                String fileName = line.hsname;//徐兰高铁(GXL)（西安北-兰州西）   文件夹里的
                LogUtil.d(TAG,"HighSpeedLineModel:"+line);
                if (line != null) {
                    MetroUtil.getInstance().initWithMode(MetroUtil.RTFillGPS_Mode_Railway);
                    MetroUtil.getInstance().startHighSpeedTest(line, null, null, false);
                    isCollecting = true;
                }
            }
            if (isCollecting) {
                LogUtil.d(TAG,"高铁GPS读取线程开启");
                new ExexThread().start();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 写数据到文件
     *
     * @param gps 经纬度
     */
    private void write(MetroGPS gps) {

        if (gps != null) {
            if (gps.Atype < 0)
                return;
            LogUtil.d(TAG, "-----write-----lat:" + gps.Lat + "-----lon:" + gps.Lon);
            float altitude = 0;
            // 由于数据集的业务逻辑限制，带时间的的GPS点如果插入过密或者移动距离过大则会导致GPS点无效，当前的处理是使用了数据集的bug，以后根据需要再更改
            int secondTime = 0;
            int usecondTime = 0;
            int flag = 0x30002;
            EventBytes.Builder(getBaseContext()).addInteger(1).addInteger(secondTime).addInteger(usecondTime)
                    .addDouble(gps.Lat).addDouble(gps.Lon).addSingle(gps.Speed).addSingle(0).addSingle(altitude).addInteger(3)
                    .addSingle(0).writeGPSToRcu(flag);
            mLastGps = gps;
        }
        if (mLastGps != null) {
            Intent intent = new Intent(GPS_LOCATION_CHANGED);
            intent.putExtra("lon", mLastGps.Lon);
            intent.putExtra("lat", mLastGps.Lat);
            sendBroadcast(intent);
        }
    }

    private class ExexThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                while (isCollecting) {

                    if (isCollecting) {
                        LogUtil.w(TAG, "MetroJNI.GetRTFillGPSPointCount() start");
                        int count = MetroUtil.getInstance().getCurrentGPSCount();
                        LogUtil.w(TAG, "MetroJNI.GetRTFillGPSPointCount() finish");
                        LogUtil.w(TAG, "Count=" + count);
                        //预定休眠1秒
                        int persleep = 1000;
                        //当个数点超过时，均匀分割休眠时间
                        if (count > 0) {
                            /**
                             *如果出现1个点以上的话，300毫秒写一次
                             */
                            if (count>1){
                                persleep=300;
                            }else {
                                persleep=1000;
                            }
                            for (int i = 0; i < count && isCollecting; i++) {
                                LogUtil.w(TAG, "MetroJNI.ReadOneRTFillGPSPoint() start");
                                MetroGPS gpsInfo = MetroUtil.getInstance().getCurrentGPS();
                                write(gpsInfo);
                                checkIsStationGPS(gpsInfo);
                                sleep(persleep);
                            }
                        } else {
                            write(null);
                            sleep(persleep);
                        }
                    }
                }

            } catch (Exception e) {
                LogUtil.w(TAG, e.getMessage());
                e.printStackTrace();
            }
        }

    }
    /**
     * 判断是否是站点GPS，设置当前的下一个站点
     *
     * @param info gps信息
     */
    private boolean checkIsStationGPS(MetroGPS info) {
        if (info.Atype < 0)
            return false;
        if (info.Atype > 0) {
            MetroStation station = mFactory.getCurrentStation();
            if (station == null)
                return false;
            if (info.Atype - 1 == station.getIndex()) {
                info.Lon = station.getLatLng().longitude;
                info.Lat = station.getLatLng().latitude;
                mFactory.reachStation(false);
                LogUtil.e(TAG,"已到站");
                return true;
            }
        } else {// 如果当前收到第一个补点，且当前的站是已到站，则设置从当前站出发
            MetroStation station = mFactory.getCurrentStation();
            if (station.isReach()){
                mFactory.startStation(false);
                LogUtil.e(TAG,"已启动");
            }
        }
        return false;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
