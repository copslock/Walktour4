package com.walktour.gui.singlestation.net.model.testplan;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi.lin on 2017/9/22.
 * <p>
 * 导入平台基站时平台下发的Attach类型的测试计划
 */

public class AttachTestPlan extends BaseTestPlan {

    @SerializedName("BaudRate")
    private int baudRate;
    @SerializedName("Duration")
    private int duration;
    @SerializedName("Inteval")
    private int interval;
    @SerializedName("Timeout")
    private int timeout;

    @Override
    public String toString() {
        return "AttachTestPlan{" +
                "baudRate=" + baudRate +
                ", duration=" + duration +
                ", interval=" + interval +
                ", timeout=" + timeout +
                '}';
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
