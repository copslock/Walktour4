package com.walktour.gui.task.activity.scannertsma.ennnnum;

/**
 * @author jinfeng.xie
 * @data 2019/6/5
 */
public enum WifiType {
    NB(0),SCAN_TSMA(1);
    private int code;

    WifiType(int code){
        this.code=code;
    }
    public static WifiType getWifiTypeByCode(int code){
        for(WifiType xxxEnum : values()){
            if(xxxEnum.getCode() == code){
                return xxxEnum;
            }
        }
        throw new RuntimeException("没有找到对应的枚举");
    }

    public int getCode() {
        return code;
    }
}
