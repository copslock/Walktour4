package com.walktour.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.GpsAdaptChange;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhone;
import com.walktour.control.config.ConfigAlarm;
import com.walktour.control.config.ParameterSetting;
import com.walktour.gui.R;
import com.walktour.gui.map.googlemap.GoogleMapMainActivity;
import com.walktour.model.MapEvent;

import org.andnav.osm.util.GeoPoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GpsService extends Service {
    private static final String TAG = "GPSService";

    private static boolean isFirstGps;

    GpsInfo gpsInfo = null;

    ApplicationModel appModel = null;

    GpsAdaptChange gpsAdapt = null;

    ParameterSetting parasetSetting = null;

    LocationManager locationManager = null;

    /**
     * 记录是否在此处强制打开的GPS，如果是的话，在GPS释放的时候强制关闭GPS开关
     */
    private static boolean isEnforcementOpen = false;

    // 告警提示相关
    private NotificationManager mNotificationManager;// 通知管理器

    private SharedPreferences mSharedPreferences;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        isFirstGps = false;
        appModel = ApplicationModel.getInstance();
        gpsInfo = GpsInfo.getInstance();
        parasetSetting = ParameterSetting.getInstance();
        gpsAdapt = GpsAdaptChange.getInstance(appModel.getScreenWidth(), appModel.getScreenHeight());

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        /*
         * // 查找到服务信息 Criteria criteria = new Criteria();
         * criteria.setAccuracy(Criteria.ACCURACY_COARSE); //位置解析的精度,Criteria.
         * ACCURACY_FINE，精确模式； Criteria. ACCURACY_COARSE，模糊模式
         * criteria.setAltitudeRequired(true); //是否提供海拔高度信息，是或否
         * criteria.setBearingRequired(true); //是否提供方向信息，是或否
         * criteria.setCostAllowed(true); //是否允许运营商计费，是或否
         * criteria.setPowerRequirement(Criteria.POWER_LOW); //电池消耗，无、低、中、高，参数
         * Criteria. NO_REQUIREMENT, Criteria. POWER_LOW, Criteria. POWER_MEDIUM, or
         * Criteria. POWER_HIGH criteria.setSpeedRequired(true); //是否提供速度信息，是或否 //
         * 低功耗 String provider = locationManager.getBestProvider(criteria, true);
         */
        // 获取GPS信息
        String provider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(provider, 1000, 0, getListener());
        // 注册卫星状态信息回调
        locationManager.addGpsStatusListener(statusListener);
        LogUtil.w(TAG, "--->onCreate");
        // 生成通知管理器
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.w(TAG, "---tonDestroy---");
        if (isEnforcementOpen) {
            LogUtil.w(TAG, "---to Enforcement close Gps switch---");
            isEnforcementOpen = false;
            new MyPhone(getApplicationContext()).checkGpsProvider(false);
        }
        if (isFirstGps) {
            showGpsStatus(getString(R.string.str_gps_status_close));
            isFirstGps = false;
        }
        if (gpsInfo.gpsListener != null) {
            locationManager.removeUpdates(getListener());
        }
        if (gpsInfo.getLocation() != null) {
            gpsInfo.setLocation(null);
        }
    }

    /**
     * GPS采样取值计时器，当该值大于2时表示需要记入队列中，初始值为3表示第一个点需要记入队列
     */
    private int intervalGpsTime = 3;
    private long mLastGpsTime = 0;

    private LocationListener getListener() {
        LogUtil.w(TAG, "--->getListener");
        if (gpsInfo.gpsListener == null) {
            LogUtil.w(TAG, "--->gpsInfo.gpsListener=null");
            gpsInfo.gpsListener = new LocationListener() {
                // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
                public void onLocationChanged(Location location) {
                    LogUtil.w(TAG, "-mLastGpsTime:"+mLastGpsTime);
                    if (System.currentTimeMillis() - mLastGpsTime < 1000)
                        return;
                    mLastGpsTime = System.currentTimeMillis();
                    try {
                        if (!isFirstGps) {
                            showGpsStatus(getString(R.string.str_gps_status_connect));
                            isFirstGps = true;
                        }
                        gpsInfo.setLocation(location);
                        LogUtil.d(TAG,
                                "-----LocationListener---lat:" + location.getLatitude() + "---lng:" + location.getLongitude()+"---speed"+location.getSpeed());
                        if (appModel.isTestJobIsRun() && !appModel.isTestStoping()) {
                            // 当前GPS参数值不为无效值时才需要添加到GPS队列中 event.value !=
                            // TraceInfoData.disableValue &&
                            intervalGpsTime++;
                            if (intervalGpsTime > 3) {
                                intervalGpsTime = 0;

                                // TraceInfoInterface.traceData.addGpsLocas(event);
                            }
                            // 当经纬度改变的时候计算与上一个点的距离
                            // twq20131111,里程值计算，放到DataSetBuilder类的addGpsInfo方法中触发计算，该方法的经纬度为写到数据集取出来的点值
                            // TraceInfoInterface.traceData.theLocationChange(location.getLongitude(),location.getLatitude());

                            if (location.getLongitude() == 0 && location.getLatitude() == 0) {
                                LogUtil.w(TAG, "---the location is zero--");
                                return;
                            }

                            if (appModel.getRuningScene() != SceneType.HighSpeedRail&&appModel.getRuningScene() != SceneType.Metro ) {// 高铁通过广播接收另外处理
                                long time = System.currentTimeMillis();
                                int secondTime = (int) (time / 1000);
                                int flag = 0x30002;
                                EventBytes.Builder(getBaseContext()).addInteger(1).addInteger(secondTime)
                                        .addInteger((int) (time - secondTime * 1000) * 1000).addDouble(location.getLatitude())
                                        .addDouble(location.getLongitude()).addSingle(location.getSpeed()).addSingle(0)
                                        .addSingle((float) location.getAltitude()).addInteger(3).addSingle(0).writeGPSToRcu(flag);
                            }
                            // }
                            // 非测试状态下 ,轨迹导航
                        } else {
                            if (mSharedPreferences.getInt(GoogleMapMainActivity.AUTO_FOLLOW_MODE, 0) == 2) {
                                GeoPoint geopoint = new GeoPoint((int) (location.getLatitude() * 1e6),
                                        (int) (location.getLongitude() * 1e6));
                                MapEvent event = new MapEvent();
                                event.setLongitude(geopoint.getLongitudeE6());
                                event.setLatitude(geopoint.getLatitudeE6());
                                TraceInfoInterface.traceData.addGpsLocas(event);
                            }
                        }
                        Intent gpsIntent = new Intent(GpsInfo.gpsLocationChanged);
                        sendBroadcast(gpsIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Provider被disable时触发此函数，比如GPS被关闭
                public void onProviderDisabled(String provider) {
                    LogUtil.w(TAG, "--->onProviderDisabled");
                    gpsInfo.setLocation(null);

                    /*
                     * // 弹出对话框，提示用户当前GPS/A-GPS没有使能 Intent gpsIntent = new
                     * Intent(GpsInfo.gpsProviderDisabled); sendBroadcast(gpsIntent);
                     */
                    // twq20120328此处不再发送提示或打开GPS开关的消息，而是强制打开GPS，释放GPS时判断如果本应用强制打开的，则强制关闭
                    LogUtil.w(TAG, "---to Enforcement open Gps switch---");
                    isEnforcementOpen = true;
                    new MyPhone(GpsService.this).checkGpsProvider(true);
                }

                // Provider被enable时触发此函数，比如GPS被打开
                public void onProviderEnabled(String provider) {
                    LogUtil.w(TAG, "--->onProviderEnabled:" + provider);
                }

                // Provider的转态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    LogUtil.w(TAG, "--->onStatusChanged"+status);
//                    LogUtil.w(TAG, "--->onStatusChanged: " + provider + status);
//                    if (status == GpsStatus.GPS_EVENT_STOPPED) {
//                        gpsInfo.setLocation(null);
//                        // twq20120411 发送GPS丢失消息，告警服务捕获此消息决定是否显示
//                        sendBroadcast(new Intent(WalkMessage.ACTION_GPS_STATE_LOST));
//                    }
                }
            };
        }
        return gpsInfo.gpsListener;
    }

    /**
     * 卫星状态监听器
     */
    private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) { // GPS状态变化时的回调，如卫星数
            GpsStatus status = locationManager.getGpsStatus(null); // 取当前状态
            updateGpsStatus(event, status);
        }
    };

    private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>();

    private void updateGpsStatus(int event, GpsStatus status) {
        if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            numSatelliteList.clear();
            int usedStatellite = 0;
            int count = 0;

            int maxSatellites = status.getMaxSatellites();
            Iterator<GpsSatellite> it = status.getSatellites().iterator();
            while (it.hasNext() && count <= maxSatellites) {
                GpsSatellite s = it.next();
                if (s.usedInFix()) {
                    usedStatellite++;
                }
                numSatelliteList.add(s);
                count++;
            }
            gpsInfo.setNumSatelliteList(numSatelliteList);
            gpsInfo.setUsedStatellite(usedStatellite);
        }
    }

    @SuppressWarnings("deprecation")
    private void showGpsStatus(String status) {
        boolean[] choiced = ConfigAlarm.getInstance().getChoiced();
        if (choiced[4]) { // 在config_alarm.xml中，排在第5位即序号4的是GSP状态告警，如果该值为真，那么当GPS搜星成功获断开时发出告警
            /*
             * Intent eventIntent= new Intent();
             * eventIntent.setAction(WalkMessage.testEventUpdate);
             * eventIntent.putExtra("eventValue", status);
             * ShowInfo.data.addEventPara(status); sendBroadcast(eventIntent);
             *
             * //同时更新自动测试页面中的事件
             * FleetEvent.Eventer.getInstance().addEvent(getApplicationContext(),
             * status);
             */
            // notification //通知图标, 状态栏显示的通知文本提示,通知产生的时间
            Notification.Builder notification = new Notification.Builder(this);
            notification.setTicker(status);
            notification.setSmallIcon(R.mipmap.walktour);
            notification.setWhen(System.currentTimeMillis());
            // Intent 点击该通知后要跳转的Activity
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, GpsService.class), 0);
            // must set this for content view, or will throw a exception
            // 如果想要更新一个通知，只需要在设置好notification之后，再次调用
            // setLatestEventInfo(),然后重新发送一次通知即可，即再次调用notify()。
            notification.setAutoCancel(true);
            notification.setContentIntent(contentIntent);
            notification.setContentTitle(getString(R.string.sys_alarm));
            notification.setContentText(status);
            mNotificationManager.notify(R.string.service_started, notification.build());
        }
    }

}
