package com.walktour.gui.newmap2.util;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;

import java.math.BigDecimal;

/**
 * @author zhicheng.chen
 * @date 2018/6/12
 */
public class BaiduMapUtil {

    /**
     * gps坐标转baidu坐标
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public static LatLng convert(double latitude, double longitude) {
        CoordinateConverter conver = new CoordinateConverter();
        conver.coord(new LatLng(latitude, longitude));
        conver.from(CoordinateConverter.CoordType.GPS);
        LatLng ll = conver.convert();
        return ll;
    }


    /**
     * 将百度坐标转变成火星坐标
     *
     * @param ll 百度坐标（百度地图坐标）
     * @return 火星坐标(高德 、 腾讯地图等)
     */
    public static double[] baidu2Gaode(LatLng ll) {
        double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
        double x = ll.longitude - 0.0065, y = ll.latitude - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double[] gps = {dataDigit(6, z * Math.sin(theta)),dataDigit(6, z * Math.cos(theta))};
        return gps;
//        return new LatLng(dataDigit(6, z * Math.sin(theta)), dataDigit(6, z * Math.cos(theta)));
    }

    /**
     * 对double类型数据保留小数点后多少位
     * 高德地图转码返回的就是 小数点后6位，为了统一封装一下
     *
     * @param digit 位数
     * @param in    输入
     * @return 保留小数位后的数
     */
    static double dataDigit(int digit, double in) {
        return new BigDecimal(in).setScale(digit, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 判断该点是不是在屏幕里面
     *
     * @param map
     * @param p
     * @return
     */
    public static boolean checkPointInScreenBound(BaiduMap map, LatLng p) {

        if (map == null || map.getMapStatus() == null) {
            return false;
        }

        LatLngBounds bound = map.getMapStatus().bound;

        return bound.contains(p);
    }

    /**
     * 获取两点之间的距离
     *
     * @param p1
     * @param p2
     * @return
     */
    public static String getDistanceStr(LatLng p1, LatLng p2) {
        double distance = DistanceUtil.getDistance(p1, p2);
        String ret;
        if (distance > 1000) {
            ret = String.format("%.2fKM", (float) distance / 1000);
        } else {
            ret = String.format("%.2fM", distance);
        }
        return ret;
    }
}
