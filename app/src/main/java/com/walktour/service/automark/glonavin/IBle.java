package com.walktour.service.automark.glonavin;

/**
 * Created by Yi.Lin on 2018/4/9.
 */

public interface IBle {


    /**
     * 使手机蓝牙操作可用
     */
    void enableBle();

    /**
     * 搜索蓝牙模块
     */
    void scanModule(OnScanModuleCompleteListener listener);


    /**
     * 连接蓝牙模块
     * @param address 蓝牙模块地址
     * @param callback 连接是否成功回调
     */
    void connectModule(String address,ConnectModuleCallback callback);
}
