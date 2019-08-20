/**
 * 
 */
package com.dinglicom.dataset.model;

import com.walktour.model.MapEvent;

/**
 * @author lifangjie
 *
 */
public class ModuleInfo implements Cloneable {
    
    public int portID;
    
    public String deviceName; //模块名称，比如SAMSUNG SIII i9308D，参见DataSet_SP.xml定义
    
    public int moduleType; //设备芯片类型定义，如"0x9107"，参见Pilot.Chip文件
    
    public int networkType; //td gsm,wcdma
    
    public int currentViewID; //当前显示在哪个页面上，参数，信令，事件,这个信息需要从外部来设置
    
    public String devicePort; //option                     //trace端口名	
    
    public int baudrate; //option		            //波特率，默认值152000
    
    public int rxInterval; //option				        //获取数据周期，默认值20ms
    
    public int lastMsgPointIndex = -1;
    
    public int lastEventIndex = -1;
    
    public int lastTotalIndex = -1;
    
    /**
     * 记录最后一个ChartView采样点
     */
    public int lastChartIndex = -1;
    /**
     * 记录上一个GPS点对应的采样点
     */
    public int lastGPSIndex = -1;
    /**记录上个GPS点产生时间*/
    public long 	lastGpsTimes = -1;
    /**
     * 记录上一个分布页面所采询到的采样点
     */
    public int lastDistributionIndex = -1;
    /**最后一个参数查询的采样点值，用于参数查询线程中确当定当前采样点是否有变化而判断串信令是否有效*/
    public int lastTraceIndex	= -1;
    /**记录最后一条有效信令时间*/
    public long lastTraceTime	= System.currentTimeMillis();
    /**串口异常重启次数*/
    public int traceRebootTime  = 0;
    /**
     * 回放方向 ,1为正向播放,0为方向播放
     */
    public int playbackDirection = 0;
    
    public int playbackStartPointIndex;
    
    /**
     * 保存上一个GPS点信息
     * 该对象用于处理当一个GPS点产生时是否需要执行补充点的操作
     */
    public MapEvent lastMapEvent 	= null;
    /**两个GPS点之间间隔多长时间需要补点*/
    public long	fillGpsInterval		= 5000;
    /**两个GPS点之间间隔超过多长时间不再补点*/
    public long fillGpsLimitTime	= 120000;
    
    /**
     * 重置所有采样点坐标<BR>
     * [功能详细描述]
     */
    public void resetPonintIndex(){
        lastMsgPointIndex = -1;
        lastEventIndex = -1;
        lastTotalIndex = -1;
        lastGPSIndex = -1;
        lastGpsTimes = -1;
        lastDistributionIndex = -1;
        lastTraceIndex = -1;
        lastTraceTime  = System.currentTimeMillis();
    }
    
    //public DataBox dataBox=new DataBox();
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
