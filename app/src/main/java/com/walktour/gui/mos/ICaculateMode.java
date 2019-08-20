package com.walktour.gui.mos;

import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

/**
 * @author zhicheng.chen
 * @date 2019/4/12
 */
public interface ICaculateMode {
    void setMosDevice(BluetoothMOSDevice device);

    void setRcuFile(String rcuFileName);

    void setModel(TaskModelWrapper wrapper);

    void start();

    void stop();

    boolean isStop();
}
