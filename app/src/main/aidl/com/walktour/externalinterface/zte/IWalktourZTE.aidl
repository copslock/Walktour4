package com.walktour.externalinterface.zte;
import com.walktour.externalinterface.zte.ITaskCallback;

/**
* Walktour对外中兴接口
**/
interface IWalktourZTE {
      /**
       * 功能：
       * 启动scs采集数据
       * logmask:掩码；isEnablePcap：是否开启抓包
       * 返回值：
       * -1000：默认值
       * 0：启动采集
       * -1：key不存在
       * -10：文件错误
       * -11：文件路径错误
       * -52：启动错误
       * -113：已经启动过了
       * -111：非su权限
       * -110：停止失败，测试未启动
       * -50：LogMask错误
       * -2：连接错误，重启软件
       * -114：服务未启动
       */
      int startLogging(String key, String logmask, boolean isEnablePcap);
      //停止采集
      int stopLogging(String key);

      //采集是否启动
      boolean isLogging(String key);

      //启动业务测试
      int startTest(String key);
      //停止业务测试
      int stopTest(String key);

      //是否运行测试中
      boolean isTesting(String key);

      //采集的时长
      int getLoggingTime(String key);

      //采集的文件数据大小
      double getDataSize(String key);

      //采集文件数目
      int getLogFileCount(String key);

      //得到数据存储位置
      String getLogFileDir(String key);
      //获得app版本号
      int getVersion(String key);
      //控制是否启用http通讯功能
      boolean controlCommunication(String key, boolean isEnable);
      /**
       * 功能：
       * 加载任务文件，fileName：任务json文件的路径
      */
      int loadTaskFile(String key, String fileName);

      //清除任务文件
      int clearTaskFile(String key);
      //开启TCP抓包
      int startTCPCapture(String key);

      //停止TCP抓包
      int stopTCPCapture(String key);
      /**
       * 功能：
       * 返回抓包状态
       *
       * @return boolean
       * true=正在抓包;false=没抓包
       */
      boolean isTCPTesting(String key);
      /**
       * 获取scs授权信息，到期日期和是否授权（0未授权 1授权）
       *
       * @return
       */
      String getLicenseInfo(String key);
      /**
       * 通知scs哪些文件已经上传完成
       *
       * @param tskExtInf 任务的拓展信息
       * @param fileName  文件名称
       */
      void uploadFileFinished(String tskExtInf,String fileName);
      //注册回调接口
      void registerCallback(ITaskCallback cb);
      //取消回调注册
      void unregisterCallback(ITaskCallback cb);


}
