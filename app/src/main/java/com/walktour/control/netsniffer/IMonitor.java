/*
 * 文件名: IMonitor.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 所有监控器的接口
 * 创建人: 黄广府
 * 创建时间:2012-8-30
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.control.netsniffer;


/**
 * 所有监控器的接口<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-30]
 */
public interface IMonitor {
    /**
     * 监控启动
     */
    void start();

    /**
     * 监控关闭
     */
    void stop();

}
