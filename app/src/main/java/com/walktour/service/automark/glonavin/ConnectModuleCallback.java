package com.walktour.service.automark.glonavin;

/**
 * Created by Yi.Lin on 2018/4/10.
 * <p>
 * 连接定位模块回调
 */

public interface ConnectModuleCallback {

    /**
     * 连接结果
     *
     * @param isSuccess 是否连接成功
     */
    void onConnectResult(boolean isSuccess);

}
