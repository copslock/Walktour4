package com.walktour.service.automark.glonavin;

import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;

import com.dingli.watcher.jni.GNVControllerJNI;
import com.dingli.watcher.model.GNVAcceleration;
import com.dingli.watcher.model.GNVRoomPoint;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.ToastUtil;
import com.walktour.framework.ui.dialog.BasicDialogActivity;
import com.walktour.gui.R;
import com.walktour.service.metro.utils.MetroUtil;

/**
 * @author jinfeng.xie
 * @data 2019/4/12
 */
public class Glonavin3in1GService extends BaseGlonavin3in1Service {
    private static final String TAG = "Glonavin3in1GService";
    private int validSum=0;//有效值的数量
    @Override
    public void onCreate() {
        LogUtil.d(TAG,"onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    void detailData(int mode, int isStop, byte[] data) {
        LogUtil.d(TAG, "GNVDidGotUpdate:&& mode="+mode);
//mode 模式 0：室内测试 1：地铁测试 2：加速度获取测试
        //iStop  是否结束测试，这个有时后不返回，建议 在 StopTest后 延时2秒断开连接
        switch (mode) {
            case 0: {
//                GNVRoomPoint point = new GNVRoomPoint();
//                GNVControllerJNI.ParseRoomPoints(data, point);

                break;
            }
            case 1: {


//                GNVMetroGPS gps = new GNVMetroGPS();
//                ParseMetroGPS(data,gps);
                break;
            }
            case 2: {
                GNVAcceleration acc = new GNVAcceleration();
                GNVControllerJNI.ParseAccValue(data,acc);

                if (acc.getValidFlag()==1){
//                    toast(acc);
                    detalPoint(acc);
                    validSum++;
                    if (validSum==1){
                        showFinishDialog();
                    }
                }

                break;
            }
            default:
                break;
        }
    }
//    private static final long SHORT_DELAY = 2000; // 2 seconds
//    //保证2秒土司一次
//    void toast(GNVAcceleration acc){
//        if (System.currentTimeMillis()-currtTime>SHORT_DELAY){
//            Looper.prepare();
//            Toast.makeText(this,"当前加速度为："+acc.getAcceleration(),Toast.LENGTH_SHORT).show();
//            Looper.loop();
//            currtTime=System.currentTimeMillis();
//        }
//    }
    void showFinishDialog() {
        // 上报事件提示
        Intent intentDialog = new Intent(this, BasicDialogActivity.class);
        intentDialog.putExtra("title", this.getString(R.string.str_tip));
        intentDialog.putExtra("message", this.getString(R.string.glonavin_auto_mark_init_success));
        intentDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentDialog);
    }

    private void detalPoint(GNVAcceleration acc) {
        if (validSum%5==0){
            LogUtil.d(TAG, "acc:" + acc);
            MetroUtil.getInstance().setAcceleration(acc.Acceleration,acc.getStatusFlag());
        }
    }

    @Override
    void startMonitor() {
        LogUtil.d(TAG,"startMonitor");
        GNVControllerJNI.StartAccTest(gnvController,1);
    }
}
