package com.walktour.gui.highspeedrail;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.ToastUtil;
import com.walktour.framework.ui.dialog.BasicDialogActivity;
import com.walktour.gui.R;
import com.walktour.gui.highspeedrail.util.CSVFileReplay;
import com.walktour.service.metro.utils.MetroUtil;

import java.io.File;

public class GService extends Service {
    private static final String TAG = "GService";
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    /**
     * 蓝牙设备
     */
    private HsGpsDataManager hsDataManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;
    private File file;//这是模拟加速度的文件
    private CSVFileReplay csvFileReplay;//
    public static boolean isFileG;//是否使用文件模拟加速度
    public static String blueToothAddress = "";
    private final Handler mHandler = new Handler() {
        // 匿名内部类写法，实现接口Handler的一些方法
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case HsGpsDataManager.STATE_CONNECTED:
                            Log.d(TAG, "已连接:" + "address" + blueToothAddress);
                            break;
                        case HsGpsDataManager.STATE_CONNECTING:
                            Log.d(TAG, "连接中:" + "address" + blueToothAddress);
                            break;
                        case HsGpsDataManager.STATE_LISTEN:
                        case HsGpsDataManager.STATE_NONE:
                            Log.d(TAG, "断开了:");
                            ToastUtil.showLong(GService.this, "已经断开了");
                            stopSelf();
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    try {
                        float[] fData = msg.getData().getFloatArray("Data");
                        Log.d(TAG, "蓝牙获取的加速度数据:x加速度" + fData[0]);
                        MetroUtil.getInstance().setAcceleration(fData[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"), Toast.LENGTH_SHORT).show();
                    LogUtil.d(TAG, "" + msg.getData().getString("toast"));
                    showDimissDialog();
                    break;
            }
        }
    };

    void showDimissDialog() {
        if (ApplicationModel.getInstance().isTestJobIsRun()){
            // 上报事件提示
            Intent intentDialog = new Intent(this, BasicDialogActivity.class);
            intentDialog.putExtra("title", this.getString(R.string.str_tip));
            intentDialog.putExtra("message", this.getString(R.string.toast_gps_buletooth_no_connect));
            intentDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentDialog);
        }
    }

    @Override
    public void onCreate() {
        LogUtil.e(TAG, "onCreat");
        super.onCreate();
        ReadG();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (hsDataManager != null) {
            hsDataManager.stop();
        }
        if (isFileG && csvFileReplay != null) {
            csvFileReplay.stop();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * @date on
     * @describe 初始化蓝牙
     * @author jinfeng.xie
     */
    private void initBuleTooth() {
        if ("".equals(blueToothAddress)) {
            return;
        }
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
                //finish();
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) mBluetoothAdapter.enable();
            if (hsDataManager == null) {
                hsDataManager = new HsGpsDataManager(this, mHandler); // 用来管理蓝牙的连接
                hsDataManager.setRecord(false);
                device = mBluetoothAdapter.getRemoteDevice(blueToothAddress);// Get the BLuetoothDevice object
                hsDataManager.connect(device);// Attempt to connect to the device
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * @date on
     * @describe 读加速度
     * @author jinfeng.xie
     */
    private void ReadG() {
        file = new File(AppFilePathUtil.getInstance().getSDCardBaseDirectory("liblog") + "/DLMetrolGeoData.csv");
        boolean isFileGps = file.exists();
        if (isFileGps) {
            isFileG = true;
            initFileG();
        } else {
            isFileG = false;
            initBuleTooth();
        }
    }

    private void initFileG() {
        csvFileReplay = new CSVFileReplay(file.getAbsolutePath(), 100);
        csvFileReplay.setHandler(new CSVFileReplay.CSVReplayHandle() {
            @Override
            public void CSVReadRowData(String[] buffer) {
                if (buffer.length > 2) {
                    Log.e(TAG, "文件回放的数据" + Double.parseDouble(buffer[2]));
                    MetroUtil.getInstance().setAcceleration(Double.parseDouble(buffer[2]));
                }
            }
        });
        if (isFileG && csvFileReplay != null) {
            csvFileReplay.start();
        }
    }
}
