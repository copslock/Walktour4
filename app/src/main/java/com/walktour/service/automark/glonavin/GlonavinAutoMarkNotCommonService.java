package com.walktour.service.automark.glonavin;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.dialog.BasicDialogActivity;
import com.walktour.gui.R;
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
import java.util.UUID;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/7/27
 * @describe 格纳微自动打点服务(可以回调楼层高度)
 */

public class GlonavinAutoMarkNotCommonService extends Service {

    private static final String TAG = "GlonavinAutoMarkNotCommonService";
    /**
     * 蓝牙适配器
     */
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();
    /**
     * 服务端UUID
     */
    private static final UUID SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    /**
     * 读模块返回数据所用的UUID
     */
    private static final UUID READ_CHARACTERISTIC_UUID = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    /**
     * 写数据到模块所用的UUID
     */
    private static final UUID WRITE_CHARACTERISTIC_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");

    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mRCharacteristic;
    private BluetoothGattCharacteristic mWCharacteristic;
    /**
     * 选择连接的目标模块地址
     */
    private String mSelectedModuleAddress;

    private GlonavinPoint prePoint;
    boolean isOpenElevatorRecognition;//是否电梯
    /**
     * 是否已唤醒模块
     */
    private boolean hasWaked = false;
    /**
     * 是否更新模块
     */
    private boolean hasUpdate = false;

    /**
     * 是否初始化完成
     */
    private boolean hasInit = false;

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "-------onCreate---------");
        super.onCreate();
        enableBle();
        isOpenElevatorRecognition = (AutoMarkConstant.markScene == MarkScene.LIFT);
        AutoMarkConstant.floors.clear();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "-------onStartCommand---------");
        mSelectedModuleAddress = intent.getStringExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE);
        connectModule(mSelectedModuleAddress);
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 开始监听蓝牙模块回调的点
     */
    private void startMonitor() {

        LogUtil.i(TAG, "-----------startMonitor----------------");
        int currentFloor = AutoMarkConstant.currentFloor;
        int firstFloorHeight = (int) (AutoMarkConstant.firstFloorHeight * 10);
        int FloorHeight = (int) (AutoMarkConstant.FloorHeight * 10);
        if (!hasWaked) {
            if (mWCharacteristic != null) {
                LogUtil.d(TAG, "是否电梯识别:" + isOpenElevatorRecognition);
                mWCharacteristic.setValue(GlonavinUtils.getWakeModuleCommand(true, isOpenElevatorRecognition, GlonavinUtils.LongDistanceReviseMode.CLOSE,
                        (byte) currentFloor, (byte) firstFloorHeight, (byte) FloorHeight)); //为characteristic赋值
                boolean isWriteSuccess = mBluetoothGatt.writeCharacteristic(mWCharacteristic);
                LogUtil.d(TAG, "wake BLE success:" + isWriteSuccess);
            } else {
                LogUtil.d(TAG, "mRCharacteristic null ,wake BLE failed");
            }
            hasWaked = true;
        }
    }

    void upDateMonitor() {
        int currentFloor = AutoMarkConstant.currentFloor;
        if (hasWaked && !hasUpdate) {
            if (mWCharacteristic != null) {
                LogUtil.d(TAG, "是否电梯识别:" + isOpenElevatorRecognition);
                mWCharacteristic.setValue(GlonavinUtils.getUpdateModuleParamCommand(
                        (byte) currentFloor, true,
                        GlonavinUtils.LongDistanceReviseMode.CLOSE, isOpenElevatorRecognition)); //为characteristic赋值
                boolean isWriteSuccess = mBluetoothGatt.writeCharacteristic(mWCharacteristic);
                LogUtil.d(TAG, "Update BLE success:" + isWriteSuccess);
            } else {
                LogUtil.d(TAG, "mRCharacteristic null ,Update BLE failed");
            }
            hasUpdate = true;
        }
    }

    /**
     * 停止监听蓝牙模块回调的点
     */
    private void stopMonitor() {
        LogUtil.d(TAG, "write data to BLE");
        if (mWCharacteristic != null) {
            mWCharacteristic.setValue(GlonavinUtils.getSleepModuleCommand()); //为characteristic赋值
            boolean isWriteSuccess = mBluetoothGatt.writeCharacteristic(mWCharacteristic);
            LogUtil.d(TAG, "sleep BLE success:" + isWriteSuccess);
        } else {
            LogUtil.d(TAG, "mRCharacteristic null ,sleep BLE failed");
        }
    }


    public class MyBinder extends Binder {
        public GlonavinAutoMarkNotCommonService getGlonavinAutoMarkService() {
            return GlonavinAutoMarkNotCommonService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "-------onBind-------");
        return new MyBinder();
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "-------onDestroy---------");
        GlonavinDataManager.getInstance().reset();
        stopMonitor();
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        hasWaked = false;
        stopSelf();
        super.onDestroy();
    }

    public void enableBle() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(enableBtIntent);
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
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
//       return mParameterSet.getGpsEventColor(this,-78);

    }

    public void connectModule(String address) {
        if (!TextUtils.isEmpty(address)) {
            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            if (null != device) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothGatt = device.connectGatt(GlonavinAutoMarkNotCommonService.this, false, mGattCallback);
                    }
                }).start();
            } else {
                // TODO: 2018/5/11 连接设备出错
                LogUtil.e(TAG, "连接设备出错 device为空");
            }
        } else {
            LogUtil.d(TAG, "----connectModule " + mSelectedModuleAddress + ", 地址为空，连接失败-----------");
        }
    }
    void showFaileDialog() {
        // 上报事件提示
        Intent intentDialog = new Intent(this, BasicDialogActivity.class);
        intentDialog.putExtra("title", this.getString(R.string.str_tip));
        intentDialog.putExtra("message", this.getString(R.string.toast_buletooth_connect_faile));
        intentDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentDialog);
    }
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        byte[] poiBytes = new byte[36];
        int preIndex = 0;

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            LogUtil.d(TAG, "--------onConnectionStateChange, newState: " + newState + "------");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                LogUtil.d(TAG, "连接成功");
                // Attempts to discover services after successful connection.
                //有时候发现服务不回调,需延时 https://stackoverflow.com/questions/41434555/onservicesdiscovered-never-called-while-connecting-to-gatt-server#comment70285228_41526267
                try {
                    Thread.sleep(600);
                    LogUtil.i(TAG, "Attempting to start service discovery:"
                            + gatt.discoverServices());
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //连接断开
                LogUtil.d(TAG, "断开连接");
                showFaileDialog();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            //当服务发现之后回调这里
            LogUtil.d(TAG, "--- onServicesDiscovered:status:" + status + " ----");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = mBluetoothGatt.getService(SERVICE_UUID); //通过厂家给的UUID获取BluetoothGattService
                if (service != null) {
                    mRCharacteristic = service.getCharacteristic(READ_CHARACTERISTIC_UUID);
                    mWCharacteristic = service.getCharacteristic(WRITE_CHARACTERISTIC_UUID);
                    mBluetoothGatt.readCharacteristic(mRCharacteristic);
                    mBluetoothGatt.setCharacteristicNotification(mRCharacteristic, true);
                    //在通过上面的设置返回为true之后还要进行下面的操作，才能订阅到数据的上传。
                    if (mBluetoothGatt.setCharacteristicNotification(mRCharacteristic, true)) {
                        for (BluetoothGattDescriptor dp : mRCharacteristic.getDescriptors()) {
                            if (dp != null) {
                                if ((mRCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                                    dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                } else if ((mRCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                                    dp.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                                }
                                mBluetoothGatt.writeDescriptor(dp);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            LogUtil.d(TAG, "---onCharacteristicRead,status:" + status + "----");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            //判断UUID是否相等
            if ((READ_CHARACTERISTIC_UUID.toString()).equals(characteristic.getUuid().toString())) {
                byte[] value = characteristic.getValue();

                if (value[0] == 36 && value[1] == -89) {//0x24 和 0xA7
                    EventBus.getDefault().post(getString(R.string.glonavin_auto_mark_need_init));
                    LogUtil.d(TAG, getString(R.string.glonavin_auto_mark_need_init));
                }
                if (value[0] == 36 && value[1] == -90) { //0x24 和 0xA6
                    hasInit = true;
                    EventBus.getDefault().post(getString(R.string.glonavin_auto_mark_init_success));
                    LogUtil.d(TAG, getString(R.string.glonavin_auto_mark_init_success));
                    upDateMonitor();
                }
                if (value[0]==36&&value[1]==113){
                  LogUtil.d(TAG,"电梯楼层开启是否成功(1代表成功):"+ value[3]);
                }
                if (hasInit) {
                    //回调位置信息
                    if (value[0] == 36 && value[1] == -47) {
                        for (int i = 0; i < value.length; i++) {
                            poiBytes[i] = value[i];
                        }
                        preIndex = value.length;
                        return;
                    }
                    for (int i = 0; i < value.length; i++) {
                        poiBytes[preIndex] = value[i];
                        preIndex++;
                    }

                    if (poiBytes[0] == 36 && poiBytes[1] == -47 && poiBytes[35] != 0) {
                        LogUtil.d(TAG, "--------开始吐点-------");
                        boolean isValid = GlonavinUtils.checkPosBytesValid(poiBytes);
                        if (isValid) {
                            prePoint = GlonavinUtils.getGlonavinPoint(poiBytes);
                            LogUtil.d(TAG, "point有效点:" + prePoint);
//                        if (isOpenElevatorRecognition) {//是电梯的话，就保持0
//                            prePoint.setX(0);
//                            prePoint.setY(0);
//                        }
                            prePoint.setColor(getColor());
                            if (prePoint.getCurrentFloor() >= 230) {//如果楼层数据溢出
                                prePoint.setCurrentFloor(0);
                            }
                            float x = (float) Math.sqrt(Math.pow(prePoint.getX(), 2) + Math.pow(prePoint.getY(), 2));//X:就是距离
                            float y = prePoint.getZ();//y就是高度

                            PointStatus ps = new PointStatus();
                            ps.setStatus(PointStatus.POINT_STATUS_EFFECTIVE);
                            ps.setPoint(new PointF(x, y));
                            MapFactory.getMapData().getPointStatusStack().push(ps);
                            MapFactory.getMapData().getGlonavinPointStack().push(prePoint);
                            EventBus.getDefault().post(prePoint);//非粘性EventBus
                            /**
                             * 写进RCU，为了回放
                             */
                            long time = System.currentTimeMillis();
                            int secondTime = (int) (time / 1000);
                            int flag = 0x30007;

                            EventBytes.Builder(GlonavinAutoMarkNotCommonService.this).addInteger(0)//int GPSPointType; 0：陀螺仪；2：手工打点；3：取消手工打点
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
                            LogUtil.d(TAG, "x:" + x);
                            LogUtil.d(TAG, "y:" + y);
//                        if(!hasUpdate){
//                            startMonitor();//参数更新
//                        }
                        }
                    }
                    poiBytes = new byte[36];
                    preIndex = 0;
                }
            }
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            LogUtil.d(TAG, "---onDescriptorWrites status:" + status + "----");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //延迟一秒再发命令给设备规避部分手机oppo发送命令无效问题
                    startMonitor();
                }
            }, 1000);
        }
    };

}
