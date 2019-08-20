/**
 *
 */
package com.walktour.gui.newmap2.util;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.widget.EditText;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteRailwayItem;
import com.walktour.gui.newmap.model.MyLatLng;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/6/7
 * @describe TODO
 */
public class GaodeMapUtil {
    /**
     * 判断edittext是否null
     */
    public static String checkEditText(EditText editText) {
        if (editText != null && editText.getText() != null
                && !(editText.getText().toString().trim().equals(""))) {
            return editText.getText().toString().trim();
        } else {
            return "";
        }
    }

    public static Spanned stringToSpan(String src) {
        return src == null ? null : Html.fromHtml(src.replace("\n", "<br />"));
    }

    public static String colorFont(String src, String color) {
        StringBuffer strBuf = new StringBuffer();

        strBuf.append("<font color=").append(color).append(">").append(src)
                .append("</font>");
        return strBuf.toString();
    }

    public static String makeHtmlNewLine() {
        return "<br />";
    }

    public static String makeHtmlSpace(int number) {
        final String space = "&nbsp;";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < number; i++) {
            result.append(space);
        }
        return result.toString();
    }

    public static String getFriendlyLength(int lenMeter) {
        if (lenMeter > 10000) // 10 km
        {
            int dis = lenMeter / 1000;
            return dis + ChString.Kilometer;
        }

        if (lenMeter > 1000) {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            return dstr + ChString.Kilometer;
        }

        if (lenMeter > 100) {
            int dis = lenMeter / 50 * 50;
            return dis + ChString.Meter;
        }

        int dis = lenMeter / 10 * 10;
        if (dis == 0) {
            dis = 10;
        }

        return dis + ChString.Meter;
    }

    public static boolean IsEmptyOrNullString(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    /**
     * 把MyLatLon对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(MyLatLng myLatLng) {
        return new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude());
    }

    /**
     * 根据类型 转换 坐标
     */
    public static LatLng convertToGaode(Context context, LatLng sourceLatLng, CoordinateConverter.CoordType coord) {
        CoordinateConverter converter = new CoordinateConverter(context);
        // CoordType.GPS 待转换坐标类型
        converter.from(coord);
        // sourceLatLng待转换坐标点
        converter.coord(sourceLatLng);
        // 执行转换操作
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    /**
     * 根据类型 转换 坐标
     */
    public static LatLng convertToGaode(Context context, MyLatLng sourceLatLng, CoordinateConverter.CoordType coord) {
        CoordinateConverter converter = new CoordinateConverter(context);
        // CoordType.GPS 待转换坐标类型
        converter.from(coord);
        // sourceLatLng待转换坐标点
        converter.coord(new LatLng(sourceLatLng.latitude,sourceLatLng.longitude));
        // 执行转换操作
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    /**
     * 根据类型 转换 坐标
     */
    public static NaviLatLng convertToGaodeNavi(Context context, MyLatLng sourceLatLng, CoordinateConverter.CoordType coord) {
        LatLng latLng=convertToGaode(context,sourceLatLng,coord);
        if (latLng==null){
            return null;
        }
        NaviLatLng naviLatLng=new NaviLatLng(latLng.latitude,latLng.longitude);
        return naviLatLng;
    }

    /**
     * 把集合体的LatLonPoint转化为集合体的LatLng
     */
    public static ArrayList<LatLng> convertArrList(List<LatLonPoint> shapes) {
        ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
        for (LatLonPoint point : shapes) {
            LatLng latLngTemp = GaodeMapUtil.convertToLatLng(point);
            lineShapes.add(latLngTemp);
        }
        return lineShapes;
    }

    /**
     * 求两个经纬度的中点
     *
     * @param l1
     * @param l2
     * @return
     */
    public static LatLng getMidLatLng(LatLng l1, LatLng l2) {
        return new LatLng((l1.latitude + l2.latitude) / 2, (l1.longitude + l2.longitude) / 2);
    }

    /**
     * long类型时间格式化
     */
    public static String convertToTime(long time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return df.format(date);
    }

    public static final String HtmlBlack = "#000000";
    public static final String HtmlGray = "#808080";

    public static String getFriendlyTime(int second) {
        if (second > 3600) {
            int hour = second / 3600;
            int miniate = (second % 3600) / 60;
            return hour + "小时" + miniate + "分钟";
        }
        if (second >= 60) {
            int miniate = second / 60;
            return miniate + "分钟";
        }
        return second + "秒";
    }


    public static String getBusPathTitle(BusPath busPath) {
        if (busPath == null) {
            return String.valueOf("");
        }
        List<BusStep> busSetps = busPath.getSteps();
        if (busSetps == null) {
            return String.valueOf("");
        }
        StringBuffer sb = new StringBuffer();
        for (BusStep busStep : busSetps) {
            StringBuffer title = new StringBuffer();
            if (busStep.getBusLines().size() > 0) {
                for (RouteBusLineItem busline : busStep.getBusLines()) {
                    if (busline == null) {
                        continue;
                    }

                    String buslineName = getSimpleBusLineName(busline.getBusLineName());
                    title.append(buslineName);
                    title.append(" / ");
                }
//					RouteBusLineItem busline = busStep.getBusLines().get(0);

                sb.append(title.substring(0, title.length() - 3));
                sb.append(" > ");
            }
            if (busStep.getRailway() != null) {
                RouteRailwayItem railway = busStep.getRailway();
                sb.append(railway.getTrip() + "(" + railway.getDeparturestop().getName()
                        + " - " + railway.getArrivalstop().getName() + ")");
                sb.append(" > ");
            }
        }
        return sb.substring(0, sb.length() - 3);
    }

    public static String getBusPathDes(BusPath busPath) {
        if (busPath == null) {
            return String.valueOf("");
        }
        long second = busPath.getDuration();
        String time = getFriendlyTime((int) second);
        float subDistance = busPath.getDistance();
        String subDis = getFriendlyLength((int) subDistance);
        float walkDistance = busPath.getWalkDistance();
        String walkDis = getFriendlyLength((int) walkDistance);
        return String.valueOf(time + " | " + subDis + " | 步行" + walkDis);
    }

    public static String getSimpleBusLineName(String busLineName) {
        if (busLineName == null) {
            return String.valueOf("");
        }
        return busLineName.replaceAll("\\(.*?\\)", "");
    }

    /**
     * 一个float的长度，转化成String
     */
    public static String getDistance(LatLng start, LatLng end) {
        float distance = AMapUtils.calculateLineDistance(start, end);
        String ret;
        if (distance > 1000) {
            ret = String.format("%.2fKM", (float) distance / 1000);
        } else {
            ret = String.format("%.2fM", distance);
        }
        return ret;
    }
    /**
     * 检查该基站是否在范围内
     *
     * @param p
     * @return
     */
    public static boolean checkPointInScreenBound(AMap aMap,LatLng p) {
        if (aMap == null || p == null){
            return false;
        }
        LatLngBounds bounds = aMap.getProjection().getVisibleRegion().latLngBounds;
        return bounds.contains(p);
    }
}
