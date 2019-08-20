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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.dingli.watcher.jni.GNVControllerJNI;
import com.dingli.watcher.jni.GNVManager;
import com.dingli.watcher.model.GNVRoomPoint;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.dialog.BasicDialogActivity;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.map.MapView;
import com.walktour.gui.map.PointStatus;
import com.walktour.service.automark.glonavin.bean.GlonavinPoint;
import com.walktour.service.automark.glonavin.eventbus.OnBubblingValidPointEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Yi.Lin on 2018/4/9.
 * 格纳微自动打点服务
 */

public class GlonavinAutoMarkService extends Service implements IBle {
    GNVManager gnvManager;

    private static final String TAG = "GlonavinAutoMarkService";
    /**
     * 蓝牙适配器
     */
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();
    /**
     * 每次搜索时长10秒
     */
    private static final int SCAN_DURATION = 6 * 1000;
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
    /**
     * 蓝牙模块名字关键字
     */
    private static final String DEV_NAME_PREFIX = "FootSensor";

    private BluetoothGatt mBluetoothGatt;
    /**
     * 搜索到的可用蓝牙模块
     */
    private ArrayList<String> mFoundModules;
    private BluetoothGattCharacteristic mRCharacteristic;
    private BluetoothGattCharacteristic mWCharacteristic;

    /**
     * 选择连接的目标模块地址
     */
    private String mSelectedModuleAddress;

    /**
     * 是否开始吐出有效点
     */
    private boolean isBubblingValidPoint;

    /**
     * 连接模块回调
     */
    private ConnectModuleCallback mConnectModuleCallback;

    /**
     * 是否已注册广播接受器
     */
    private boolean hasRegisteredBroadcast = false;


    /**
     * 是否已唤醒模块
     */
    private double mLatitude;
    private double mLongitude;
    private int gnvController;//so库回调过来的controller

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "-------onCreate---------");
        super.onCreate();
        enableBle();
        gnvManager = new GNVManager() {
            @Override
            public void GNVShouldWriteData(byte[] data) {
                LogUtil.d(TAG,"data："+ Arrays.toString(data));
                if (data!=null){
                    mWCharacteristic.setValue(data); //为characteristic赋值
                    boolean isWriteSuccess = mBluetoothGatt.writeCharacteristic(mWCharacteristic);
                    LogUtil.d(TAG, "wake GNVShouldWriteData success:" + isWriteSuccess);
                }
            }

            @Override
            public void GNVDidGotUpdate(int mode, int isStop, byte[] data) {
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
        };
        GNVControllerJNI.setManager(gnvManager);
        gnvController = GNVControllerJNI.CreateController();
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
            EventBytes.Builder(GlonavinAutoMarkService.this).addInteger(0)//int GPSPointType; 0：陀螺仪；2：手工打点；3：取消手工打点
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "-------onStartCommand---------");
        SDKInitializer.initialize(WalktourApplication.getAppContext());
        initLocation();
        mLocClient.start();
        mLocClient.requestLocation();
        if (!hasRegisteredBroadcast) {
            registerBroadcastReceiver();
        }

        mSelectedModuleAddress = intent.getStringExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE);
        connectModule(mSelectedModuleAddress, new ConnectModuleCallback() {
            @Override
            public void onConnectResult(boolean isSuccess) {
                LogUtil.d(TAG, "----connectModule " + mSelectedModuleAddress + ", isSuccess: " + isSuccess + "-----------");
            }
        });

        return super.onStartCommand(intent, flags, startId);
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
//                    startMonitor();
                    break;
                case WalkMessage.AUTOMARK_STOP_MARK:
                    stopMonitor();
                    break;
                case WalkMessage.GLONAVIN_AUTOMARK_START_MARK:
//                    startMonitor();
                    break;
            }
        }
    };


    /**
     * 开始监听蓝牙模块回调的点
     */
    private void startMonitor() {
        LogUtil.i(TAG, "-----------startMonitor----------------");
        GNVControllerJNI.StartRoomTest(gnvController,0,0,0,0,0,0,1);

    }

    /**
     * 停止监听蓝牙模块回调的点
     */
    private void stopMonitor() {
        GNVControllerJNI.StopTest(gnvController);
        GNVControllerJNI.FreeController(gnvController);
        GNVControllerJNI.setManager(null);
    }


    public class MyBinder extends Binder {
        public GlonavinAutoMarkService getGlonavinAutoMarkService() {
            return GlonavinAutoMarkService.this;
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
        if (hasRegisteredBroadcast) {
            this.unregisterReceiver(mBroadcastReceiver);
            hasRegisteredBroadcast = false;
        }
        GlonavinDataManager.getInstance().reset();
        stopMonitor();
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        stopSelf();
        super.onDestroy();
    }

    @Override
    public void enableBle() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(enableBtIntent);
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    @Override
    public void scanModule(final OnScanModuleCompleteListener listener) {
        LogUtil.d(TAG, "---------scanModule--------");
        if (null != mBluetoothAdapter) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                if (null == mFoundModules) {
                    mFoundModules = new ArrayList<>();
                } else {
                    mFoundModules.clear();
                }
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //10秒后关闭扫描蓝牙
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        listener.onComplete(mFoundModules);
                    }
                }, SCAN_DURATION);
            }
        }
    }


    @Override
    public void connectModule(String address, ConnectModuleCallback callback) {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mConnectModuleCallback = callback;
        if (!TextUtils.isEmpty(address)) {
            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            if (null != device) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothGatt = device.connectGatt(GlonavinAutoMarkService.this, false, mGattCallback);
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
                GNVControllerJNI.RecieveData(gnvController,value);
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

    private LocationClient mLocClient;

    /**
     * 新结构的插电第一个点必须插入一个X,Y,Z坐标都为0的点
     */
    private void writeGPSBeforeFirstPoint() {
        long time = System.currentTimeMillis();
        int secondTime = (int) (time / 1000);
        int flag = 0x30007;
        EventBytes.Builder(GlonavinAutoMarkService.this).addInteger(0)//int GPSPointType; 0：陀螺仪；2：手工打点；3：取消手工打点
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

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            String devName = device.getName();
            String devAddress = device.getAddress();
            LogUtil.d(TAG, devName + ", " + devAddress);
            if (!TextUtils.isEmpty(devName) && devName.contains(DEV_NAME_PREFIX) && !mFoundModules.contains(devAddress)) {
                mFoundModules.add(devAddress);
            }
        }
    };

}
