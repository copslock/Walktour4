package com.walktour.service.automark.constant;

import java.util.HashMap;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/7/25
 * @describe 自动打点常量
 */
public class AutoMarkConstant {
    public static MarkScene markScene = MarkScene.COMMON;//打点场景
    public static int currentFloor = 1;//当前楼层
    public static double firstFloorHeight = 0;//首层高
    public static double FloorHeight = 0;//层高


    public static HashMap<Integer, Float> floors = new HashMap<>();//记录的楼层

}
