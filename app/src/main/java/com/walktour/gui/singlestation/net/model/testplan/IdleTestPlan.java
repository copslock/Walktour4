package com.walktour.gui.singlestation.net.model.testplan;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi.lin on 2017/9/22.
 * <p>
 * 导入平台基站时平台下发的Idle类型的测试计划
 */

public class IdleTestPlan extends BaseTestPlan {
    @SerializedName("CollectData")
    private boolean collectData;//true
    @SerializedName("Duration")
    private int duration;//300000

    @Override
    public String toString() {
        return "IdleTestPlan{" +
                "collectData=" + collectData +
                ", duration=" + duration +
                '}';
    }

    public boolean isCollectData() {
        return collectData;
    }

    public void setCollectData(boolean collectData) {
        this.collectData = collectData;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
