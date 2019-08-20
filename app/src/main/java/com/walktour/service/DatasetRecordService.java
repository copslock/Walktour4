package com.walktour.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.dinglicom.data.control.BuildTestRecord;
import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.HttpServer;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigDataAcquisition;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.gui.WalkTour;
import com.walktour.gui.map.MapFactory;

/***
 * 启动数据集
 */
public class DatasetRecordService extends Service {
    private static final String TAG = "DatasetRecordService";
    private DatasetManager datasetMgr;
    private boolean isStart = false;

    @Override
    public void onCreate()
    {
        datasetMgr = DatasetManager.getInstance(this);
        regeditBroadcast();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        LogUtil.w(TAG, "--onStartCommand");
        if (!isStart) {
            isStart = true;
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    boolean flag = false;
                    while (!flag) {
                        flag = datasetMgr.startDataSet(true);
                        try {
                            LogUtil.w(TAG, flag ? "startDataSet is success." : "startDataSet is failure.");
                            if (flag) {
                                if (Deviceinfo.getInstance().isVivo()) {
                                    try {
                                        MyPhoneState.getInstance().startAirplane(DatasetRecordService.this);
                                        Thread.sleep(2000);
                                        MyPhoneState.getInstance().closeAirplane(DatasetRecordService.this);
                                        Thread.sleep(3000);
                                        Deviceinfo.getInstance().setAir(false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Thread.sleep(2000);
                                }
                            } else {
                                Thread.sleep(2000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (flag) {//数据集启动完成
                        Intent intent = new Intent();
                        intent.setAction(WalkMessage.ACTION_STARTDATASET_INIT_FINISH);
                        sendBroadcast(intent);

                    }
                }
            }).start();
        }
        runForegroundService();
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressWarnings("deprecation")
    private void runForegroundService()
    {
        Notification.Builder notification = new Notification.Builder(this);
        notification.setTicker(getString(R.string.app_name));
        notification.setSmallIcon(R.drawable.walktour38);
        notification.setWhen(System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, WalkTour.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setContentIntent(pendingIntent);
        notification.setContentTitle(getString(R.string.main_testing));
        notification.setContentText(getString(R.string.main_tostoptest));
        //使用 startForeground ，如果 id 为 0 ，那么 notification 将不会显示
        startForeground(0, notification.build());
    }

    private void regeditBroadcast()
    {
        // 注册创建接受测试开始结束时的相关事件
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.NOTIFY_TESTJOBDONE); // 测试结束
        filter.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE); // 中断测试
        filter.addAction(HttpServer.OtsFileCreate);
        filter.addAction(HttpServer.OtsStopFile);
        filter.addAction(HttpServer.OtsFileExport);
        this.registerReceiver(mIntentReceiver, filter, null, null);
    }

    /**
     * 接收消息处理
     */
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent)
        {
            try {
                String action = intent.getAction();

                if (intent.getAction().equals(WalkMessage.NOTIFY_TESTJOBDONE)
                        || intent.getAction().equals(WalkMessage.NOTIFY_INTERRUPTJOBDONE)) {
                    MapFactory.getMapData().getHistoryList().clear();
                } else if (intent.getAction().equals(HttpServer.OtsFileCreate)) {
                    operateFileThread(intent.getExtras().getString("FileName"));
                } else if (intent.getAction().equals(HttpServer.OtsStopFile)) {
                    Log.i(TAG, "=========" + "come Broadcast()");
                    operateFileThread("");
                } else if (intent.getAction().equals(HttpServer.OtsFileExport)) {
                    Log.i(TAG, "=========" + "come OtsFileExport");
                    fileExportThread(intent);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    };

    /**
     * 操作文件保存与关闭 由于进程句柄的问题，放在HttpServer容易导致库崩溃，所以改成此方法
     *
     * @param filename
     */
    private void operateFileThread(final String filename)
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                if (filename != null && filename.length() != 0) {
                    datasetMgr.configDecodeProperty(ConfigDataAcquisition.getInstance().getl3msg()); // 配置采样配置周期
                    // false为OTS采集
                    BuildTestRecord testRecord = new BuildTestRecord();
                    datasetMgr.createFile(filename, false, false, "", 0, null, testRecord, -1, null, false);
                } else {
                    Log.i(TAG, "========" + "close ots File");
                    datasetMgr.closeFile();
                }
            }
        }).start();
    }

    /**
     * 文件导出
     *
     * @param intent
     */
    private void fileExportThread(final Intent intent)
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    datasetMgr.openPlayback(DatasetManager.PORT_4, intent.getStringExtra("FileName"));
                    Log.i(TAG, "=============totalPoint--" + datasetMgr.getTotalExportPointCount(DatasetManager.PORT_4) + "--"
                            + intent.getStringExtra("FileName"));
                    datasetMgr.customExportFile(
                            intent.getStringArrayListExtra("customSavePath"),
                            intent.getStringArrayListExtra("headMsgList"),
                            intent.getBooleanExtra("division_enable", false) == true ? (intent.getIntExtra("division_size", 512) * 1000)
                                    : Integer.MAX_VALUE, intent.getIntegerArrayListExtra("configList"), 0, datasetMgr
                                    .getTotalExportPointCount(DatasetManager.PORT_4), null, 0); // 指定分割大小，从配置文件获取
                    datasetMgr.closePlayback(DatasetManager.PORT_4);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try {
            this.stopForeground(true);
            datasetMgr.closeDataSet();
            Log.d(TAG, "DataRecordService onDestroy");
            // 反注册创建接受测试开始结束时的相关事件
            if (null != mIntentReceiver) {
                this.unregisterReceiver(mIntentReceiver);
                mIntentReceiver = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

}
