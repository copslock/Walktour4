package com.walktour.gui.newmap2.bean;

import android.graphics.Point;

/**
 * @author zhicheng.chen
 * @date 2018/11/21
 */
public class StationEvent {
    public static final int CLEAR = 0;
    public static final int SELECT = 1;
    /**
     * 框选框过滤
     */
    public static final int RECT_FILTER = 2;
    /**
     * 参数过滤
     */
    public static final int PARAM_FILTER = 3;
    public Point[] points;
    public int type;
}
