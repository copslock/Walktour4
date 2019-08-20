package com.walktour.gui.task.activity.scannertsma.ennnnum;

/**
 * @author jinfeng.xie
 * @data 2019/2/1
 */
public enum NetType {
//    1：GSM；2，WCDMA；3：CDMA；4：EVDO；5：LTE； 7：TDSCDMA；
//            10：CW；14：ACD；15：CWScan；16：NB-IoT；18：NR；

    GSM(1,"GSM"),
    WCDMA(2,"WCDMA"),
    CDMA(3,"CDMA"),
    EVDO(4,"EVDO"),
    LTE(5,"LTE"),
    TDSCDMA(7,"TDSCDMA"),
    CW(10,"CW"),
    ACD(14,"ACD"),
    CWScan(15,"CWScan"),
    NB_IoT(16,"NB-IoT"),
    NR(18,"NR");

    private int netType;
    private String name;
    NetType(int netType,String name){
        this.netType=netType;
        this.name=name;
    }
    public static NetType getNetTypeByCode(int code){
        for(NetType xxxEnum : values()){
            if(xxxEnum.getNetType() == code){
                return xxxEnum;
            }
        }
        throw new RuntimeException("没有找到对应的枚举");
    }
    public int getNetType() {
        return netType;
    }

    public void setNetType(int netType) {
        this.netType = netType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NetType{" +
                "netType=" + netType +
                ", name='" + name + '\'' +
                '}';
    }
}
