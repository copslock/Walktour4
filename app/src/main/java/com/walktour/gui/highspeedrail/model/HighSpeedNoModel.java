package com.walktour.gui.highspeedrail.model;

import com.walktour.service.metro.model.MetroRoute;

import java.io.Serializable;


/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/8/30
 * @describe 高铁班次
 */
public class HighSpeedNoModel implements Serializable {
    public String noName;//班次名字
    public String noPath;//班次的路径
    public MetroRoute routes;
    public String parentPath;//线路的kml路径

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getNoName() {
        return noName;
    }

    public void setNoName(String noName) {
        this.noName = noName;
    }

    public String getNoPath() {
        return noPath;
    }

    public void setNoPath(String noPath) {
        this.noPath = noPath;
    }

    public MetroRoute getRoutes() {
        return routes;
    }

    public void setRoutes(MetroRoute routes) {
        this.routes = routes;
    }

    @Override
    public String toString() {
        return "HighSpeedNoModel{" +
                "noName='" + noName + '\'' +
                ", noPath='" + noPath + '\'' +
                ", routes=" + routes +
                ", parentPath='" + parentPath + '\'' +
                '}';
    }
}
