package com.walktour.externalinterface.vivo;
import com.walktour.externalinterface.vivo.IVivoCallback;
/**
* 获取Walktour 测试任务执行对外接口
**/
interface IWalktourVivo {
    /***
    *
    * 获取app版本号
    * */
    String getAppversion();
    /**
    * 加载测试任务
    * type=1,fileInfo表示为文件的xml内容,解析xml内容得到测试任务
    * type=2,fileInfo表示为文件的json内容,解析json内容得到测试任务
    */
    int loadTask(int type,String fileInfo);
    /***
    * 清除测试任务
    ***/
    int clearTask();
    /**
    * 开始测试
    * isSavLog--是否保存数据
    * isEnablePcap--是否允许抓包
    */
    int startTest(boolean isSavLog,boolean isEnablePcap);
    /***
    * 停止测试
    */
    int stopTest();
    /**
    * 是否在测试中
    **/
    boolean isTesting();
    //注册回调接口
    void registerCallback(IVivoCallback cb);
    //取消回调注册
    void unregisterCallback(IVivoCallback cb);

}
