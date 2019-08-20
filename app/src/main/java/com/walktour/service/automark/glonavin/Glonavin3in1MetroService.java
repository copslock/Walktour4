package com.walktour.service.automark.glonavin;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Looper;
import android.widget.Toast;

import com.dingli.watcher.jni.GNVControllerJNI;
import com.dingli.watcher.model.GNVAcceleration;
import com.dingli.watcher.model.GNVMetroGPS;
import com.dingli.watcher.model.MetroGPS;
import com.walktour.Utils.EventBytes;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.ui.dialog.BasicDialogActivity;
import com.walktour.gui.R;
import com.walktour.gui.map.MapFactory;
import com.walktour.service.metro.MetroFactory;
import com.walktour.service.metro.MetroTestService;
import com.walktour.service.metro.model.MetroCity;
import com.walktour.service.metro.model.MetroRoute;
import com.walktour.service.metro.model.MetroStation;
import com.walktour.service.metro.utils.MetroUtil;

/**
 * @author jinfeng.xie
 * @data 2019/4/12
 */
public class Glonavin3in1MetroService extends BaseGlonavin3in1Service {
    private static final String TAG = "Glonavin3in1MetroService";
    /**
     * 地铁工厂类
     */
    private MetroFactory mFactory;
    private int validSum=0;

    @Override
    public void onCreate() {
        LogUtil.d(TAG,"onCreate");
        super.onCreate();
        this.mFactory = MetroFactory.getInstance(this);
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG,"onDestroy");
        super.onDestroy();
        this.mFactory.stopTest();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG,"onStartCommand");
        this.mFactory.startGNVTest();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    void detailData(int mode, int isStop, byte[] data) {
//mode 模式 0：室内测试 1：地铁测试 2：加速度获取测试
        //iStop  是否结束测试，这个有时后不返回，建议 在 StopTest后 延时2秒断开连接
        switch (mode) {
            case 0: {
//                GNVRoomPoint point = new GNVRoomPoint();
//                GNVControllerJNI.ParseRoomPoints(data, point);

                break;
            }
            case 1: {
                GNVMetroGPS gps = new GNVMetroGPS();
                GNVControllerJNI.ParseMetroGPS(data,gps);

                if (gps.getValidFlag()==1){
//                    toast(gps);
                    detalPoint(gps);
                    validSum++;
                    if (validSum==1){
                        showFinishDialog();
                    }
                }
                break;
            }
            case 2: {
//                GNVAcceleration acc = new GNVAcceleration();
//                GNVControllerJNI.ParseAccValue(data,acc);
//                LogUtil.d(TAG, "point:" + acc);
//                detalPoint(acc);
                break;
            }
            default:
                break;
        }
    }
    void showFinishDialog() {
        // 上报事件提示
        Intent intentDialog = new Intent(this, BasicDialogActivity.class);
        intentDialog.putExtra("title", this.getString(R.string.str_tip));
        intentDialog.putExtra("message", this.getString(R.string.glonavin_auto_mark_init_success));
        intentDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentDialog);
    }
    private static final long SHORT_DELAY = 2000; // 2 seconds
    private long currtTime;
    //保证2秒土司一次
    void toast(GNVMetroGPS gps){
        if (System.currentTimeMillis()-currtTime>SHORT_DELAY){
            Looper.prepare();
            Toast.makeText(this,"当前精度为："+gps.getLat(),Toast.LENGTH_SHORT).show();
            Looper.loop();
            currtTime=System.currentTimeMillis();
        }
    }
    private void detalPoint(GNVMetroGPS acc) {

        checkIsStationGPS(acc);
        MetroGPS gps = acc.chanceToMetroGPSModel();
        this.write(gps);
        if (gps != null) {
            Intent intent = new Intent(MetroTestService.GPS_LOCATION_CHANGED);
            intent.putExtra("lon", gps.Lon);
            intent.putExtra("lat", gps.Lat);
            sendBroadcast(intent);
        }
    }
    /**
     * 判断是否是站点GPS，设置当前的下一个站点
     *
     * @param info gps信息
     */
    private boolean checkIsStationGPS(GNVMetroGPS info) {
        LogUtil.d(TAG,"有效的gps:"+info.getStationID()+"启停状态,"+info.getPosType());
        //位置信息类(0:静止[0表示站点位置信息],1:减速,2:匀速,3:加速,4:启动[非0表示两站之间的位置信息])
//        if (info.getPosType() < 0)
//            return false;
//        if (info.getPosType() > 0) {
//            MetroStation station = mFactory.getCurrentStation();
//            if (station == null)
//                return false;
//            if (station.isReach()&&info.getstation.getIndex()){
//                mFactory.startStation(false);
//                LogUtil.e(TAG,"已启动");
//            }
//
//        } else if (info.getPosType()==0){// 如果当前收到第一个补点，且当前的站是已到站，则设置从当前站出发
//            MetroStation station = mFactory.getCurrentStation();
//            if (station == null)
//                return false;
//            if (info.getStationID() - 1 == station.getIndex()&&!station.isReach()) {
//                mFactory.reachStation(false);
//                LogUtil.e(TAG,"已到站");
//                return true;
//            }
//
//        }
        mFactory.setStationIndex(info.getStationID());
        return false;
    }
    /**
     * 写数据到文件
     *
     * @param gps 经纬度
     */
    private void write(MetroGPS gps) {
//        if (gps.Atype < 0)
//            return;
        LogUtil.d(TAG, "-----write-----lat:" + gps.Lat + "-----lon:" + gps.Lon+"------speed:"+gps.Speed);
        float altitude = 0;
        // 由于数据集的业务逻辑限制，带时间的的GPS点如果插入过密或者移动距离过大则会导致GPS点无效，当前的处理是使用了数据集的bug，以后根据需要再更改
        int secondTime = 0;
        int usecondTime = 0;
        int flag = 0x30002;
        EventBytes.Builder(getBaseContext()).addInteger(1).addInteger(secondTime).addInteger(usecondTime)
                .addDouble(gps.Lat).addDouble(gps.Lon).addSingle(gps.Speed).addSingle(0).addSingle(altitude).addInteger(3)
                .addSingle(0).writeGPSToRcu(flag);
//            MapEvent event = new MapEvent();
//            event.setLongitude(gps.Lon);
//            event.setLatitude(gps.Lat);
//            TraceInfoInterface.traceData.addGpsLocas(event);
    }
    @Override
    void startMonitor() {
        String cityPath="";
        int lineIndex=-1;
        int startStatationIndex=-1;
        int endStatationIndex=-1;
        MetroCity currentCity = mFactory.getCurrentCity(false);
        if (currentCity!=null){
            cityPath=currentCity.getFilePath();
        }
        MetroRoute currentRoute = mFactory.getCurrentRoute(false);
        if (currentRoute!=null){
            lineIndex= (int) currentRoute.getId();
            startStatationIndex=  currentRoute.getStartStation().getIndex();
            endStatationIndex=  currentRoute.getEndStation().getIndex();
        }
        LogUtil.d(TAG,"cityPath:"+cityPath);
        LogUtil.d(TAG,"lineIndex:"+lineIndex);
        LogUtil.d(TAG,"startStatationIndex:"+startStatationIndex);
        LogUtil.d(TAG,"endStatationIndex:"+endStatationIndex);
        GNVControllerJNI.StartMetrolTest(gnvController,  cityPath,lineIndex,startStatationIndex,endStatationIndex,1);
    }
}
