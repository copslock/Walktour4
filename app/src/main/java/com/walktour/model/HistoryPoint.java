/*
 * 文件名: HistoryPoint.java
 * 版    权：  Copyright Dingli. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-10-24
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.model;

import com.walktour.gui.map.PointStatus;

import java.util.Queue;

/**
 * 历史轨迹点<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-10-24] 
 */
public class HistoryPoint {
    
    public static final int HISTORY_ADD = 1;   //历史增加点
    public static final int HISTORY_DEL = 0;   //历史删除点
    
    private PointStatus pointStatus;
    
    private Queue<AlarmModel> alarmQueue;
    
    private int type;
    

    /**
     * [构造简要说明]
     * @param pointStatus
     * @param type
     */
    public HistoryPoint(PointStatus pointStatus, Queue<AlarmModel> alarmQueue,int type) {
        super();
        this.pointStatus = pointStatus;
        this.alarmQueue = alarmQueue;
        this.type = type;
    }

    /**
     * @return the pointStatus
     */
    public PointStatus getPointStatus() {
        return pointStatus;
    }

    /**
     * @param pointStatus the pointStatus to set
     */
    public void setPointStatus(PointStatus pointStatus) {
        this.pointStatus = pointStatus;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the alarmQueue
     */
    public Queue<AlarmModel> getAlarmQueue() {
        return alarmQueue;
    }

    /**
     * @param alarmQueue the alarmQueue to set
     */
    public void setAlarmQueue(Queue<AlarmModel> alarmQueue) {
        this.alarmQueue = alarmQueue;
    }
    
    
    
}
