package com.walktour.service.innsmap.model;

/**
 * Created by Yi.Lin on 2018/2/2.
 * 用于寅时测试接收ping业务传过来的测试数据bean
 */

public class PingData {
    public static final int RESULT_SUCCEED = 1, RESULT_FAILED = 0;
    private int result;
    private int repeat;
    private long time;//样本点采集时间(ms)
    private String IP;// 对端IP
    private int size;
    private int delay;
    private int TTL;
    private String cause;

    @Override
    public String toString() {
        return "PingData{" +
                "result=" + result +
                ", repeat=" + repeat +
                ", time=" + time +
                ", IP='" + IP + '\'' +
                ", size=" + size +
                ", delay=" + delay +
                ", TTL=" + TTL +
                ", cause='" + cause + '\'' +
                '}';
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getTTL() {
        return TTL;
    }

    public void setTTL(int TTL) {
        this.TTL = TTL;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
