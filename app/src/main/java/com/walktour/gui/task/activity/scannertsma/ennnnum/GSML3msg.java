package com.walktour.gui.task.activity.scannertsma.ennnnum;

/**
 * @author jinfeng.xie
 * @data 2019/6/3
 */
public enum GSML3msg {
    SITYPE_1          (0x19),        //!< System Information Type 1
    SITYPE_2          (0x1A),
    SITYPE_2_BIS      (0x02),
    SITYPE_2_N        (0x45),
    SITYPE_2_QUATER   (0x07),
    SITYPE_2_TER      (0x03),
    SITYPE_3          (0x1B),
    SITYPE_4          (0x1C),
    SITYPE_7          (0x1F),
    SITYPE_8          (0x18),
    SITYPE_9          (0x04),
    SITYPE_13         (0x00),
    SITYPE_13_ALT     (0x44),
    SITYPE_15         (0x43),
    SITYPE_16         (0x3D),
    SITYPE_17         (0x3E),
    SITYPE_18         (0x40),
    SITYPE_19         (0x41),
    SITYPE_20         (0x42),
    SITYPE_21         (0x46),
    SITYPE_22         (0x47),
    UNKNOWN        (0xFFFFFFFF);

    private long code;
    GSML3msg(long code) {
        this.code=code;
    }

    public long getCode(){
        return code;
    }


    public static GSML3msg getNetTypeByCode(int code){
        for(GSML3msg xxxEnum : values()){
            if(xxxEnum.code == code){
                return xxxEnum;
            }
        }
        throw new RuntimeException("没有找到对应的枚举");
    }

}
