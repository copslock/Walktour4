package com.walktour.service.automark.glonavin;

import android.annotation.SuppressLint;
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.baidu.mapapi.SDKInitializer;
import com.dingli.watcher.jni.GNVControllerJNI;
import com.dingli.watcher.jni.GNVManager;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.dialog.BasicDialogActivity;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.service.automark.constant.AutoMarkConstant;
import com.walktour.service.automark.constant.MarkScene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Xie.jinfeng on 2019/4/11.
 * 格纳微三合一服务（室内，高铁，地铁）
 */

public abstract class BaseGlonavin3in1Service extends Service  {

    private static final String TAG = "Glonavin3in1AutoMarkService";
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


    /**
     * 连接模块回调
     */
    private ConnectModuleCallback mConnectModuleCallback;

    /**
     * 是否已注册广播接受器
     */
    private boolean hasRegisteredBroadcast = false;

    public int gnvController;//so库回调过来的controller
    private GNVManager gnvManager;

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "-------onCreate---------");
        super.onCreate();
        enableBle();
        gnvManager = new GNVManager() {
            @Override
            public void GNVShouldWriteData(final byte[] data) {
                LogUtil.d(TAG,"data："+ Arrays.toString(data));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10);
                            if (data!=null){
                                mWCharacteristic.setValue(data); //为characteristic赋值
                                boolean isWriteSuccess = mBluetoothGatt.writeCharacteristic(mWCharacteristic);
                                LogUtil.d(TAG, "写入" + (isWriteSuccess?"成功":"失败"));
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void GNVDidGotUpdate(int mode, int isStop, byte[] data) {
                detailData(mode,isStop,data);
            }
        };
        GNVControllerJNI.setManager(gnvManager);
        gnvController = GNVControllerJNI.CreateController();
    }
    abstract void detailData(int mode, int isStop, byte[] data);


    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "-------onStartCommand---------");
        SDKInitializer.initialize(WalktourApplication.getAppContext());
        if (!hasRegisteredBroadcast) {
            registerBroadcastReceiver();
        }
        if (intent!=null){
            mSelectedModuleAddress = intent.getStringExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE);
        }
        connectModule(mSelectedModuleAddress, new ConnectModuleCallback() {
            @Override
            public void onConnectResult(boolean isSuccess) {
                LogUtil.d(TAG, "----connectModule " + mSelectedModuleAddress + ", isSuccess: " + isSuccess + "-----------");
            }
        });

        return super.onStartCommand(intent,  Service.START_REDELIVER_INTENT, startId);
    }

    /**
     * 注册广播接收器
     */
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.AUTOMARK_START_MARK);
        filter.addAction(WalkMessage.AUTOMARK_STOP_MARK);
        filter.addAction(WalkMessage.AUTOMARK_FIRST_POINT);
        filter.addAction(WalkMessage.GLONAVIN_AUTOMARK_START_MARK);
        this.registerReceiver(mBroadcastReceiver, filter);
        this.hasRegisteredBroadcast = true;
    }

    /**
     * 广播接收器
     */
    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.d(TAG, "----GlonavinAutoMarkService onReceive: " + action + "------");
            switch (action) {
                case WalkMessage.AUTOMARK_START_MARK:
                    break;
                case WalkMessage.AUTOMARK_STOP_MARK:
                    stopMonitor();
                    break;
                case WalkMessage.GLONAVIN_AUTOMARK_START_MARK:
                    break;
            }
        }
    };


    /**
     * 开始监听蓝牙模块回调的点
     */
    abstract void startMonitor();


    /**
     * 停止监听蓝牙模块回调的点
     */
    private void stopMonitor() {
        GNVControllerJNI.StopTest(gnvController);
//        GNVControllerJNI.FreeController(gnvController);
        GNVControllerJNI.setManager(null);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "-------onBind-------");
        return null;
    }

    @Override
    public void onDestroy() {

        LogUtil.d(TAG, "-------onDestroy---------");
        if (hasRegisteredBroadcast) {
            this.unregisterReceiver(mBroadcastReceiver);
            hasRegisteredBroadcast = false;
        }
        GlonavinDataManager.getInstance().reset();
        stopMonitor();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    stopSelf();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        if (mBluetoothGatt!=null){
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
        super.onDestroy();
    }

    public void enableBle() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(enableBtIntent);
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

/*
* 连接蓝牙设备*/
    public void connectModule(String address, ConnectModuleCallback callback) {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mConnectModuleCallback = callback;
        if (!TextUtils.isEmpty(address)) {
            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            if (null != device) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothGatt = device.connectGatt(BaseGlonavin3in1Service.this, false, mGattCallback);
                    }
                }).start();
            } else {
                // TODO: 2018/5/11 连接设备出错
                LogUtil.e(TAG, "连接设备出错 device为空");
            }
        } else {
            mConnectModuleCallback.onConnectResult(false);
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


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            LogUtil.d(TAG, "--------onConnectionStateChange, newState: " + newState + "------");
            if (mConnectModuleCallback != null) {
                mConnectModuleCallback.onConnectResult(newState == BluetoothProfile.STATE_CONNECTED);
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                LogUtil.d(TAG, "连接成功");
                //连接成功
                gatt.discoverServices();//开始发现设备的服务
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
                LogUtil.d(TAG,"蓝牙收到数据为："+Arrays.toString(value));
                GNVControllerJNI.RecieveData(gnvController,value);
            }
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            LogUtil.d(TAG, "---onDescriptorWrites status:" + status + "----");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //延迟一秒
                    LogUtil.d(TAG,"onDescriptorWrite");
                    startMonitor();
                }
            }, 2000);
        }
    };




    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            String devName = device.getName();
            String devAddress = device.getAddress();
            LogUtil.d(TAG, devName + ", " + devAddress);
        }
    };

}
