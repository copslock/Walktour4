/*
 * 文件名: MarkPoint.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2012-8-10
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.gui.map.googlemap.kml;

import com.walktour.model.LocusParamInfo;

import org.andnav.osm.util.GeoPoint;

import java.util.HashMap;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-10] 
 */
public class MarkPoint {
    
    /**
     * 经纬度
     */
    public GeoPoint GeoPoint;
    
    /**
     * 轨迹点颜色
     */
    private int color;
    
    /**
     * 详细描述
     */
    private String desc;
    
    /**
     * 参数名称
     */
    private String paramName;
    
    /**
     * 参数值
     */
    private double paramValue;
    
    /**
     * 开始采样点
     */
    private int beginPointIndex;
    
    /**
     * 结束采样点
     */
    private int endPointIndex;
    
    private HashMap<String, LocusParamInfo> paramHm = new HashMap<String, LocusParamInfo>();

    /**
     * @return the geoPoint
     */
    public GeoPoint getGeoPoint() {
        return GeoPoint;
    }

    /**
     * @param geoPoint the geoPoint to set
     */
    public void setGeoPoint(GeoPoint geoPoint) {
        GeoPoint = geoPoint;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @return the color
     */
    public int getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(int color) {
        this.color = color;
    }

//    /**
//     * @return the eventType
//     */
//    public int getEventType() {
//        return eventType;
//    }
//
//    /**
//     * @param eventType the eventType to set
//     */
//    public void setEventType(int eventType) {
//        this.eventType = eventType;
//    }

    /**
     * @return the paramName
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * @param paramName the paramName to set
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    /**
     * @return the paramValue
     */
    public double getParamValue() {
        return paramValue;
    }

    /**
     * @param paramValue the paramValue to set
     */
    public void setParamValue(double paramValue) {
        this.paramValue = paramValue;
    }

    /**
     * @return the beginPointIndex
     */
    public int getBeginPointIndex() {
        return beginPointIndex;
    }

    /**
     * @param beginPointIndex the beginPointIndex to set
     */
    public void setBeginPointIndex(int beginPointIndex) {
        this.beginPointIndex = beginPointIndex;
    }

    /**
     * @return the endPointIndex
     */
    public int getEndPointIndex() {
        return endPointIndex;
    }

    /**
     * @param endPointIndex the endPointIndex to set
     */
    public void setEndPointIndex(int endPointIndex) {
        this.endPointIndex = endPointIndex;
    }

    /**
     * @return the paramHm
     */
    public HashMap<String, LocusParamInfo> getParamHm() {
        return paramHm;
    }

    /**
     * @param paramHm the paramHm to set
     */
    public void setParamHm(HashMap<String, LocusParamInfo> paramHm) {
        this.paramHm = paramHm;
    }

    
}
