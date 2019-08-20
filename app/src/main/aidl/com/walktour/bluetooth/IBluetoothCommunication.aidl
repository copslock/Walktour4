package com.walktour.bluetooth;

/**
* Walktour蓝牙通信接口
**/
interface IBluetoothCommunication {
      /**
       * 发送蓝牙消息
       */
      void sendMessage(String message);

    /**
     * 关闭蓝牙消息管道
     */
      void close();
}
