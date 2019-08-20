package com.walktour.model;

/***
 *  * 存储层3信令的相关信息
 * 包括：flag: -1：无效，1：有效
 * 		 ID： 信令ID
 * 		 value：参数值，
 * 		 l3Msg：表示的层3信息
 */
public class TdL3Model {
    public static final String TdL3Model_DOWN = "0";//下行指令
    public static final String TdL3Model_UP = "1";//上行指令
    public static final String TdL3Model_MMS = "2";//终端指令
    private int flag;
    private long id;
    private int value;
    private String l3Msg = "";
    private long time = 0;
    /**
     * 0--下行(DL)
     * 1--上行(UL)
     * 2--终端指令(MS)
     */
    private String direction = "";
    private int pointIndex;     //数据集当前采样点
    /**
     * 显示的颜色
     */
    private int color;
    /**
     * 每条层三信令对应一个num值，用于调用底层库获得详细层三信令
     */
    private String num;

    private boolean isNeedMark;//针对筛选结果，是否需要标记不同颜色

    public boolean isNeedMark() {
        return isNeedMark;
    }

    public void setNeedMark(boolean needMark) {
        isNeedMark = needMark;
    }

    public TdL3Model() {
        l3Msg = "";
    }

    public synchronized long getId() {
        return id;
    }

    public synchronized void setId(long id) {
        this.id = id;
    }

    public synchronized int getValue() {
        return value;
    }

    public synchronized void setValue(int value) {
        this.value = value;
    }

    public synchronized String getL3Msg() {
        if (l3Msg == null || l3Msg.equals("Null"))
            return "";
        return l3Msg;
    }

    public synchronized void setL3Msg(String l3Msg) {
        this.l3Msg = l3Msg;
    }

    public synchronized int getFlag() {
        return flag;
    }

    public synchronized void setFlag(int flag) {
        this.flag = flag;
    }

    /**
     * @return the num
     */
    public String getNum() {
        return num;
    }

    /**
     * @param num the num to set
     */
    public void setNum(String num) {
        this.num = num;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public int getPointIndex() {
        return pointIndex;
    }

    public void setPointIndex(int pointIndex) {
        this.pointIndex = pointIndex;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


}
