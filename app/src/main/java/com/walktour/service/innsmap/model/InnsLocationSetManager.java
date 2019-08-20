package com.walktour.service.innsmap.model;

import java.util.ArrayList;

/**
 * Created by Yi.Lin on 2017/12/15.
 * <p>
 * 寅时自动打点点集合管理类
 */

public class InnsLocationSetManager {

    /**
     * 单例对象
     */
    private volatile static InnsLocationSetManager sManager;

    /**
     * 点集合容器
     */
    private static ArrayList<LocationWithMeasParameter> sLocationList;

    /**
     * 获取单例对象
     *
     * @return InnsLocationSetManager对象
     */
    public static InnsLocationSetManager getInstance() {
        if (null == sManager) {
            synchronized (InnsLocationSetManager.class) {
                if (null == sManager) {
                    sManager = new InnsLocationSetManager();
                }
            }
        }
        return sManager;
    }

    /**
     * 添加点
     *
     * @param location
     */
    public void addLocation(LocationWithMeasParameter location) {
        sLocationList.add(location);
    }

    /**
     * 清空定位点集合
     */
    public void clear(){
        sLocationList.clear();
    }

    /**
     * 移除点
     * @param locationIndex 点的索引
     */
    public void removeLocation(int locationIndex) {
        if (locationIndex < sLocationList.size()) {
            sLocationList.remove(locationIndex);
        }
    }

    /**
     * 移除点
     * @param location 移除点的对象
     */
    public void removeLocation(LocationWithMeasParameter location){
        sLocationList.remove(location);
    }


    public ArrayList<LocationWithMeasParameter> getLocationList() {
        return sLocationList;
    }

    public void setLocationList(ArrayList<LocationWithMeasParameter> locationList) {
        sLocationList = locationList;
    }

    /**
     * 私有化构造器
     */
    private InnsLocationSetManager() {
        //no-instance
        if (null == sLocationList) {
            sLocationList = new ArrayList<>();
        }
    }

}
