package com.walktour.service.innsmap.model;

import com.innsmap.InnsMap.location.bean.INNSMapLocation;

/**
 * Created by Yi.Lin on 2017/12/13.
 *
 * 用于存储 寅时定位点拼接测试参数结果
 *
 */

public class LocationWithMeasParameter {

    /**
     * 寅时定位点对象
     */
    private INNSMapLocation innsMapLocation;
    /**
     * 对应的测试参数
     */
    private String measParameter;
    /**
     * 定位点产生时间
     */
    private long time;

    public LocationWithMeasParameter(INNSMapLocation innsMapLocation, String measParameter, long time) {
        this.innsMapLocation = innsMapLocation;
        this.measParameter = measParameter;
        this.time = time;
    }

    @Override
    public String toString() {
        return "LocationWithMeasParameter{" +
                "innsMapLocation=" + innsMapLocation +
                ", measParameter='" + measParameter + '\'' +
                ", time=" + time +
                '}';
    }

    public INNSMapLocation getInnsMapLocation() {
        return innsMapLocation;
    }

    public void setInnsMapLocation(INNSMapLocation innsMapLocation) {
        this.innsMapLocation = innsMapLocation;
    }

    public String getMeasParameter() {
        return measParameter;
    }

    public void setMeasParameter(String measParameter) {
        this.measParameter = measParameter;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
