package com.walktour.service.automark.constant;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/7/25
 * @describe 外置陀螺仪打点模式
 */
public enum MarkScene {
    COMMON(0, "普通"), STAIRS(1, "楼梯"), LIFT(2, "电梯");
    private int value;
    private String name;
    MarkScene(int value, String name) {
        this.value=value;
        this.name=name;
    }
}
