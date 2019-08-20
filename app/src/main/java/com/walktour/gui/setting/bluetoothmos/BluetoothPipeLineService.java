package com.walktour.gui.setting.bluetoothmos;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.walktour.bluetooth.IBluetoothCommunication;

/**
 * 蓝牙通信管道
 *
 * @author zhicheng.chen
 * @date 2019/3/31
 */
public class BluetoothPipeLineService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    IBluetoothCommunication.Stub mBinder = new IBluetoothCommunication.Stub() {
        @Override
        public void sendMessage(String message) throws RemoteException {
            BluetoothPipeLine.getInstance().sendData(message.getBytes());
        }

        @Override
        public void close() throws RemoteException {
            BluetoothPipeLine.getInstance().close();
        }
    };
}
