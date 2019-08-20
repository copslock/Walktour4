package com.dingli.watcher.jni;


/**
 * @author jinfeng.xie
 * @data 2019/4/9
 */
public abstract class GNVManager {
    ///////////////////////////
    //回调函数
    //要发送的下行命令数据
    public abstract void GNVShouldWriteData(byte[] data);

    //接收到的数据更新
    public abstract void GNVDidGotUpdate(int mode,int isStop, byte[] data);

}
