package com.walktour.externalinterface.vivo;

/**
* 接口回调
**/
interface IVivoCallback {
    /**
    * 通知测试已经开始
    **/
    void notifyTestStarted(String msg);

    /**
    * 通知测试已经结束
    **/
    void notifyTestFinished(String msg);

    /***
    * 回调给客户端的MOS值
    **/
    void notifyMosValue(String value);
}