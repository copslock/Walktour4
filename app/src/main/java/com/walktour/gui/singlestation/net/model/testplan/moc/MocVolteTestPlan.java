package com.walktour.gui.singlestation.net.model.testplan.moc;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.singlestation.net.model.testplan.BaseTestPlan;

/**
 * Created by yi.lin on 2017/9/22.
 * <p>
 * 导入平台基站时平台下发的 Moc_VOLTE类型的测试计划
 */

public class MocVolteTestPlan extends BaseTestPlan {

    @SerializedName("ConnectionTime")
    private int connectionTime;//2000
    @SerializedName("DialNumber")
    private String dialNumber;//"10086"
    @SerializedName("Duration")
    private int duration;//90000
    @SerializedName("Inteval")
    private int interval;//15000

    @Override
    public String toString() {
        return "MocCSFBTestPlan{" +
                "connectionTime=" + connectionTime +
                ", dialNumber='" + dialNumber + '\'' +
                ", duration='" + duration + '\'' +
                ", interval='" + interval + '\'' +
                '}';
    }

    public int getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(int connectionTime) {
        this.connectionTime = connectionTime;
    }

    public String getDialNumber() {
        return dialNumber;
    }

    public void setDialNumber(String dialNumber) {
        this.dialNumber = dialNumber;
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

}
