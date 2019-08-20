package com.walktour.service.automark.glonavin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.dingli.watcher.jni.GNVControllerJNI;
import com.dingli.watcher.model.GNVRoomPoint;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.map.MapView;
import com.walktour.gui.map.PointStatus;
import com.walktour.service.automark.constant.AutoMarkConstant;
import com.walktour.service.automark.constant.MarkScene;
import com.walktour.service.automark.glonavin.bean.GlonavinPoint;
import com.walktour.service.automark.glonavin.eventbus.OnBubblingValidPointEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Yi.Lin on 2018/4/9.
 * 格纳微自动打点服务
 */

public class Glonavin3in1AutoMarkService extends BaseGlonavin3in1Service {

    private static final String TAG = "Glonavin3in1AutoMarkService";

    private LocationClient mLocClient;

    /**
     * 是否开始吐出有效点
     */
    private boolean isBubblingValidPoint;

    private double mLatitude;
    private double mLongitude;

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "-------onCreate---------");
        super.onCreate();
    }

    @Override
    void detailData(int mode, int isStop, byte[] data) {
        LogUtil.d(TAG, "GNVDidGotUpdate");
//mode 模式 0：室内测试 1：地铁测试 2：加速度获取测试
        //iStop  是否结束测试，这个有时后不返回，建议 在 StopTest后 延时2秒断开连接
        switch (mode) {
            case 0: {
                GNVRoomPoint point = new GNVRoomPoint();
                GNVControllerJNI.ParseRoomPoints(data, point);
                LogUtil.d(TAG, "point:" + point);
                detalPoint(point);
                break;
            }
            case 1: {
//                GNVMetroGPS gps = new GNVMetroGPS();
//                ParseMetroGPS(data,gps);
                break;
            }
            case 2: {
//                GNVAcceleration acc = new GNVAcceleration();
//                ParseAccValue(data,acc);
                break;
            }
            default:
                break;
        }
    }

    private GlonavinPoint mPrePoint;//上一个点
    private GlonavinPoint mPreRealPoint;//上一个点
    private float dAngle;

    /*處理有效点*/
    public void detalPoint(GNVRoomPoint point) {
        LogUtil.d(TAG, "--------开始吐点-------");
        float x = point.getPoxX();
        float y = point.getPoxY();
        float angle = point.getBow();
        //过滤点
                        /*if (Math.abs(x) < 0.01 && Math.abs(y) < 0.01) {
                            return;
                        }*/
        GlonavinDataManager instance = GlonavinDataManager.getInstance();
        if (!isBubblingValidPoint) {
            instance.setHasStartedBubblingValidPoint(true);
            EventBus.getDefault().postSticky(new OnBubblingValidPointEvent());
            LogUtil.i(TAG, "------发送开始吐点事件，通知界面弹出确定起始点和方向对话框-----------");
            isBubblingValidPoint = true;
        }
        //确认已经设置过方向后才开始打点
        if (instance.isHasDirectionSet()) {
            //比例尺（像素/米）
            final SharePreferencesUtil sharePreferencesUtil = SharePreferencesUtil.getInstance(WalktourApplication.getAppContext());
            final String indoorMapPath = sharePreferencesUtil.getString(MapView.SP_INDOOR_MAP_PATH);
            //比例尺（像素/米）
            float plottingScale = sharePreferencesUtil.getFloat(indoorMapPath, 1.0f);//像素/米
            if (null == mPrePoint) {
                dAngle = angle - GlonavinDataManager.getInstance().getInitialAngle();
                GlonavinPoint startP = GlonavinDataManager.getInstance().getStartPoint();
                mPreRealPoint = new GlonavinPoint(startP.getX(), startP.getY(), (float) Math.PI);
                //赋值
                instance.setHasPointDrew(true);
                //新结构的插点第一个点必须插入一个X,Y,Z坐标都为0的点
                writeGPSBeforeFirstPoint();
            } else {
                float dx = x - mPrePoint.getX();
                float dy = y - mPrePoint.getY();
                float disReal = (float) (Math.sqrt(dx * dx + dy * dy) * plottingScale);
                mPreRealPoint = GlonavinUtils.nextPointWithDistance(mPreRealPoint, angle - dAngle, disReal);
            }

            mPrePoint = new GlonavinPoint(x, y, angle - dAngle);

            PointStatus ps = new PointStatus();
            ps.setStatus(PointStatus.POINT_STATUS_EFFECTIVE);
            ps.setPoint(new PointF(mPreRealPoint.getX(), mPreRealPoint.getY()));
            MapFactory.getMapData().getPointStatusStack().push(ps);

            Bitmap map = MapFactory.getMapData().getMap();
            int sampleSize = MapFactory.getMapData().getSampleSize();
            double pointX = (mPreRealPoint.getX()) * sampleSize;
            double pointY = (map.getHeight() - mPreRealPoint.getY()) * sampleSize;
            long time = System.currentTimeMillis();
            int secondTime = (int) (time / 1000);
            int flag = 0x30007;
            float northShift = (float) (pointY / plottingScale);
            float eastShift = (float) (pointX / plottingScale);
            LogUtil.d(TAG, "northShift:" + northShift + ", eastShift:" + eastShift);
            EventBytes.Builder(Glonavin3in1AutoMarkService.this).addInteger(0)//int GPSPointType; 0：陀螺仪；2：手工打点；3：取消手工打点
                    .addInteger(secondTime)//unsigned int Second;
                    .addInteger((int) (time - secondTime * 1000) * 1000)//unsigned int uSecond;
                    .addDouble(mLongitude)//double dLon;  定点经度（建筑物等）
                    .addDouble(mLatitude)//double dLat;  定点纬度（建筑物等）
                    .addSingle(0)//float Altitude;	定点海拔（建筑物等）
                    .addSingle(northShift)//float Altitude;	float NorthShift; 南北偏移距离（单位：米）
                    .addSingle(eastShift)//float Altitude;	float EastShift;  东西偏移距离（单位：米）
                    .addSingle(0)//float Altitude;  float HeightShift; 上下偏移距离（单位：米）
                    .addSingle(0)//float Altitude;	float Angle;  角度
                    .writeToRcu(flag);

        } else {
            LogUtil.i(TAG, "还未设置方向");
        }
    }
    /**
     * 新结构的插电第一个点必须插入一个X,Y,Z坐标都为0的点
     */
    private void writeGPSBeforeFirstPoint() {
        long time = System.currentTimeMillis();
        int secondTime = (int) (time / 1000);
        int flag = 0x30007;
        EventBytes.Builder(Glonavin3in1AutoMarkService.this).addInteger(0)//int GPSPointType; 0：陀螺仪；2：手工打点；3：取消手工打点
                .addInteger(secondTime)//unsigned int Second;
                .addInteger((int) (time - secondTime * 1000) * 1000)//unsigned int uSecond;
                .addDouble(mLongitude)//double dLon;  定点经度（建筑物等）
                .addDouble(mLatitude)//double dLat;  定点纬度（建筑物等）
                .addSingle(0)//float Altitude;	定点海拔（建筑物等）
                .addSingle(0)//float NorthShift; 南北偏移距离（单位：米）
                .addSingle(0)//float EastShift;  东西偏移距离（单位：米）
                .addSingle(0)//float HeightShift; 上下偏移距离（单位：米）
                .addSingle(0)//float Angle;  角度
                .writeToRcu(flag);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "-------onStartCommand---------");
        initLocation();
        mLocClient.start();
        mLocClient.requestLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    void startMonitor() {
      GNVControllerJNI.StartRoomTest(gnvController,0,0,0,0,0,0,1);
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "-------onDestroy---------");
        super.onDestroy();
    }

    private void initLocation() {
        mLocClient = new LocationClient(WalktourApplication.getAppContext());
        mLocClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                mLocClient.stop();
                if (location == null) {
//                    ToastUtil.showLong(WalktourApplication.getAppContext(), "定位失败");
                    return;
                }
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                LogUtil.e(TAG, "---latitude:" + mLatitude + ",longitude:" + mLongitude + "---------");
                if (mLatitude == 0
                        && mLongitude == 0) {
//                    ToastUtil.showLong(WalktourApplication.getAppContext(), "定位失败");
                    return;
                }
//                ToastUtil.showLong(WalktourApplication.getAppContext(), "定位成功");
            }
        });
        LocationClientOption locationOption = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        mLocClient.setLocOption(locationOption);

    }

}
