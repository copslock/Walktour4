package com.walktour.service.automark.glonavin;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;

import com.dingli.watcher.jni.GNVControllerJNI;
import com.dingli.watcher.model.GNVRoomPoint;
import com.walktour.Utils.EventBytes;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.map.PointStatus;
import com.walktour.model.AlarmModel;
import com.walktour.model.MapEvent;
import com.walktour.service.automark.constant.AutoMarkConstant;
import com.walktour.service.automark.constant.MarkScene;
import com.walktour.service.automark.glonavin.bean.GlonavinPoint;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/7/27
 * @describe 格纳微自动打点服务(可以回调楼层高度)
 */

public class Glonavin3in1AutoMarkNotCommonService extends BaseGlonavin3in1Service {

    private static final String TAG = "Glonavin3in1AutoMarkNotCommonService";


    private GlonavinPoint currentPoint;

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "-------onCreate---------");
        super.onCreate();
        AutoMarkConstant.floors.clear();
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

    void detalPoint(GNVRoomPoint point) {
        //回调位置信息
        LogUtil.d(TAG, "--------开始吐点-------");
        currentPoint = point.chancetoGlonavinPointModel();
        LogUtil.d(TAG, "point有效点:" + currentPoint);
        currentPoint.setColor(getColor());
        if (currentPoint.getCurrentFloor() >= 230) {//如果楼层数据溢出
            currentPoint.setCurrentFloor(0);
        }
        float x = (float) Math.sqrt(Math.pow(currentPoint.getX(), 2) + Math.pow(currentPoint.getY(), 2));//X:就是距离
        float y = currentPoint.getZ();//y就是高度

        PointStatus ps = new PointStatus();
        ps.setStatus(PointStatus.POINT_STATUS_EFFECTIVE);
        ps.setPoint(new PointF(x, y));
        MapFactory.getMapData().getPointStatusStack().push(ps);
        MapFactory.getMapData().getGlonavinPointStack().push(currentPoint);
        EventBus.getDefault().post(currentPoint);//非粘性EventBus
        /**
         * 写进RCU，为了回放
         */
        long time = System.currentTimeMillis();
        int secondTime = (int) (time / 1000);
        int flag = 0x30007;

        EventBytes.Builder(Glonavin3in1AutoMarkNotCommonService.this).addInteger(0)//int GPSPointType; 0：陀螺仪；2：手工打点；3：取消手工打点
                .addInteger(secondTime)//unsigned int Second;
                .addInteger((int) (time - secondTime * 1000) * 1000)//unsigned int uSecond;
                .addDouble(0)//double dLon;  定点经度（建筑物等）
                .addDouble(0)//double dLat;  定点纬度（建筑物等）
                .addSingle(0)//float Altitude;	定点海拔（建筑物等）
                .addSingle(x)//float Altitude;	float NorthShift; 南北偏移距离（单位：米）
                .addSingle(y)//float Altitude;	float EastShift;  东西偏移距离（单位：米）
                .addSingle(0)//float Altitude;  float HeightShift; 上下偏移距离（单位：米）
                .addSingle(0)//float Altitude;	float Angle;  角度
                .writeToRcu(flag);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "-------onStartCommand---------");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    void startMonitor() {
        LogUtil.i(TAG, "-----------startMonitor----------------");
        int currentFloor = AutoMarkConstant.currentFloor;
        int firstFloorHeight = (int) (AutoMarkConstant.firstFloorHeight);
        int FloorHeight = (int) (AutoMarkConstant.FloorHeight);
        int LiftCheck = (AutoMarkConstant.markScene == MarkScene.LIFT) ? 1 : 0;
        int isSuceess = GNVControllerJNI.StartRoomTest(gnvController,
                1, LiftCheck, 0, FloorHeight, firstFloorHeight, currentFloor, 1);
        LogUtil.d(TAG, "isSuceess:" + isSuceess + "     currentFloor:" + currentFloor + "     firstFloorHeight:" + firstFloorHeight + "     FloorHeight:" + FloorHeight + "     LiftCheck:" + LiftCheck);
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "-------onDestroy---------");
        super.onDestroy();
    }

    private int getColor() {
        List<AlarmModel> alarmList = new ArrayList<>(MapFactory.getMapData().getEventQueue());
        int color = Color.GRAY;
        if (alarmList.size() > 0) {
            MapEvent mapEvent = alarmList.get(alarmList.size() - 1).getMapEvent();
            if (mapEvent != null) {
                color = mapEvent.getColor();
            }
        }
        return color;
    }


}
