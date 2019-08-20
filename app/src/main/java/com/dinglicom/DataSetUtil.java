package com.dinglicom;

/**
 *  类功能：主要包含一些数据集操作过程中的一些公用方法
 * 
 * @author tangwq
 *
 */
public class DataSetUtil {

    /**
     * 将从数据集获得的结果处理成String[][]的结果数组方式
     * 其中String[i][0]存储的是数据的结果值，
     * String[i][1]存储的是相应位置的采样点序号
     *
     * @param value 值的格式："value@@value@@...",获得"pointIndex,value@@pointIndex,value@@..."
     * @return
     */
    public static String[][] splitValues(String value) {
        String[] values = value.split("@@");
        String[][] result = new String[values.length][2];

        for (int i = 0; i < values.length; i++) {
            String[] vs = values[i].split(",");

            //根据值结果，如果长度为2,数据0为序号，否则数组0为值
            if (vs.length == 2) {
                result[i][0] = vs[1];
                result[i][1] = vs[0];
            } else {
                result[i][0] = vs[0];
                //result[i][1] = "";
            }
        }

        return result;
    }

    /**
     * 获取两个时间间隔
     *
     * @param begin
     * @param end
     * @return
     */
    public static String parseTimeStr(long begin, long end) {
        String totalTime = "00:00";
        try {
            long between = end - begin;
            parseTimeStr(between);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalTime;
    }

    /**
     * 获取两个时间间隔
     *
     * @return
     */
    public static String parseTimeStr(long between) {
        String totalTime = "00:00";
        try {
            long hour = (between / (60 * 60 * 1000));
            long min = ((between / (60 * 1000)) - hour * 60);
            long sec = (between / 1000 - hour * 60 * 60 - min * 60);

            String hourStr = hour + "";

            String minStr = min + "";
            if (minStr.length() < 2) {
                minStr = "0" + minStr;
            }

            String secStr = sec + "";
            if (secStr.length() < 2) {
                secStr = "0" + secStr;
            }

            if (hourStr.length() == 1) {
                totalTime = minStr + ":" + secStr;
            } else {
                totalTime = hour + ":" + minStr + ":" + secStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalTime;
    }
}
