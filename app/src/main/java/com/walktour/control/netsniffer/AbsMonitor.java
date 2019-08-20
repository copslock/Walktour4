/*
 * 文件名: AbsMonitor.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: IMonitor接口的抽象类
 * 创建人: 黄广府
 * 创建时间:2012-8-30
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.control.netsniffer;

/**
 * IMonitor接口的抽象类<BR>
 * [功能详细描述]
 * @author 实现了IMonitor的start和stop两个主要方法，并能够记录当前的状态, 可避免重复运行(start)，或未运行便先中止(stop)
 * @version [WalkTour Client V100R001C03, 2012-8-30]
 */
public abstract class AbsMonitor implements IMonitor {
    
    private boolean running;
    
    public AbsMonitor() {
        super();
        this.running = false;
    }
    
    /**
     * 开始运行
     * 
     * @return
     */
    public final synchronized void start() {
        if (running)
            return;
        running = true;
        localStart();
    }
    
    /**
     * 结束运行
     * 
     * @return
     */
    public final synchronized void stop() {
        if (!running)
            return;
        running = false;
        localStop();
    }
    
    /**
     * 给子类实现的本地开始方法
     * 
     * @return
     */
    protected abstract void localStart();
    
    /**
     * 给子类实现的本地结束方法
     * 
     * @return
     */
    protected abstract void localStop();
    
    /**
     * 当前对象是否正在执行
     * 
     * @return
     */
    public boolean isRunning() {
        return running;
    }
    
}
