package com.walktour.gui.newmap2.bean;

import java.io.Serializable;

/**
 * 导航路线
 * @author zhicheng.chen
 * @date 2018/12/19
 */
public class RounteBean<T> implements Serializable{

    public static final int LAGLNG_BAIDU = 0;
    public static final int LAGLNG_GAODE = 1;
    public static final int LAGLNG_GPS = 2;

    public String name;
    public String adress;
    public double lat;
    public double lng;
    public int latLngType;//经纬度类型
    public T info;
}
