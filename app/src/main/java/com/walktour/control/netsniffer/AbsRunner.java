/*
 * 文件名: AbsRunner.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 可运行的抽象类
 * 创建人: 黄广府
 * 创建时间:2012-8-30
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.control.netsniffer;

import android.content.Context;

import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;

/**
 * 可运行的抽象类<BR>
 * 该类拥有start和stop两个主要方法，并能够记录当前的状态, 可避免重复运行(start)，或未运行便先中止(stop)
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-30]
 */
public abstract class AbsRunner {
	
	private final String TAG = "AbsRunner";
	private String runningName = "";
    private boolean running;

    public AbsRunner() {
        super();
        this.running = false;
        this.runningName = "";
    }

    /**
     * 开始运行
     * 
     * @return
     */
    public final synchronized boolean start(final Context ctx, final String pacpName) {
    	if (running && pacpName.equals(runningName)){
        	LogUtil.w(TAG,"--AbsRunner is Running byName:" + pacpName);
            return false;
        }
        
        //开线程启动抓包
        new Thread(new Runnable() {
			@Override
			public void run() {
				//如果当前状态为运行中,先调用停止当前抓包
				if(running){
					LogUtil.w(TAG, "--befer start pcap stop byName:" + runningName);
					localStop();
					UtilsMethod.ThreadSleep(500);
				}
				
				runningName = pacpName;
				running = true;
				localStart(ctx, pacpName);
			}
		}).start();
		return running;
    }

    /**
     * 结束运行
     * 
     * @return
     */
    public final synchronized boolean stop() {
        if (!running)
            return false;
        running = false;
        return localStop();
    }

    /**
     * 给子类实现的本地开始方法
     * 
     * @return
     */
    protected abstract boolean localStart(final Context ctx, String pacpName);

    /**
     * 给子类实现的本地结束方法
     * 
     * @return
     */
    protected abstract boolean localStop();

    /**
     * 当前对象是否正在执行
     * 
     * @return
     */
    public boolean isRunning() {
        return running;
    }

}
