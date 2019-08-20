package com.walktour.service.bluetoothmos;

import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

interface IBluetoothMOSServiceCallback{
    
    /**
     * 处理消息
	 * @param what
	 *          消息类型
	 * @param message
	 *          消息内容
     */
	void handleMessage(int what,String message);

	/**
	 * 返回查找到的蓝牙设备对象
	 */
	void handleDevice(in BluetoothMOSDevice device);

	void onFinishDiscoverBluetoothDevices(in List<BluetoothMOSDevice> discoveredDevices);
	
}