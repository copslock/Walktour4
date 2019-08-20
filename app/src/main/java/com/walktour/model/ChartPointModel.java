/*
 * 文件名: ChartPointModel.java
 * 版    权：  Copyright Dingli. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-10-27
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.model;

import java.util.List;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-10-27] 
 */
public class ChartPointModel {
    
    private String value;
    
    private int pointIndex;
    
    private List<AlarmModel> alarms;

    
    
    /**
     * [构造简要说明]
     * @param value
     * @param pointIndex
     */
    public ChartPointModel(String value, int pointIndex,List<AlarmModel> alarms) {
        super();
        this.value = value;
        this.pointIndex = pointIndex;
        this.alarms = alarms;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the pointIndex
     */
    public int getPointIndex() {
        return pointIndex;
    }

    /**
     * @param pointIndex the pointIndex to set
     */
    public void setPointIndex(int pointIndex) {
        this.pointIndex = pointIndex;
    }

    /**
     * @return the alarms
     */
    public List<AlarmModel> getAlarms() {
        return alarms;
    }

    /**
     * @param alarms the alarms to set
     */
    public void setAlarms(List<AlarmModel> alarms) {
        this.alarms = alarms;
    }

    
    
    
}
