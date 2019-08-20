package com.walktour.gui.task.activity.scannertsma.ennnnum;

/**
 * @author jinfeng.xie
 * @data 2019/6/3
 */
public enum LTEL3msg {

    UNDEFINED( 0),  //!< Undefined), used for not initialized fields
    MIB      ( 10),  //!< MIB    is PDU 10   If necessary), this PDU will be acquired automatically.
    SIB1     ( 11),  //!< SIB  1 is PDU 11
    SIB2     ( 12),  //!< SIB  2 is PDU 12
    SIB3     ( 13),  //!< SIB  3 is PDU 13
    SIB4     ( 14),  //!< SIB  4 is PDU 14
    SIB5     ( 15),  //!< SIB  5 is PDU 15
    SIB6     ( 16),  //!< SIB  6 is PDU 16
    SIB7     ( 17),  //!< SIB  7 is PDU 17
    SIB8     ( 18),  //!< SIB  8 is PDU 18
    SIB9     ( 19),  //!< SIB  9 is PDU 19
    SIB10    ( 20),  //!< SIB 10 is PDU 20
    SIB11    ( 21),  //!< SIB 11 is PDU 21
    SIB12    ( 22),  //!< SIB 12 is PDU 22
    SIB13    ( 23),  //!< SIB 13 is PDU 23
    SIB14    ( 24),  //!< SIB 14 is PDU 24
    SIB15    ( 25),  //!< SIB 15 is PDU 25
    SIB16    ( 26),  //!< SIB 16 is PDU 26
    SIB17    ( 27),  //!< SIB 17 is PDU 27
    SIB18    ( 28),  //!< SIB 18 is PDU 28
    SIB19    ( 29),  //!< SIB 19 is PDU 29
    SIB20    ( 201), //!< SIB 20 is PDU 201
    SIB21    ( 202), //!< SIB 21 is PDU 202
    SIB24    ( 205), //!< SIB 24 is PDU 205
    SIB25    ( 206), //!< SIB 25 is PDU 206
    SIB26    ( 207); //!< SIB 26 is PDU 207

    private long code;
    LTEL3msg(long code) {
        this.code=code;
    }
    public long getCode(){
        return code;
    }


    public static LTEL3msg getLTEL3msgByCode(int code){
        for(LTEL3msg xxxEnum : values()){
            if(xxxEnum.code == code){
                return xxxEnum;
            }
        }
        throw new RuntimeException("没有找到对应的枚举");
    }
}
