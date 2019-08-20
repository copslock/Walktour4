package com.walktour.gui.highspeedrail.model;

import java.io.Serializable;
import java.util.List;

/**
 * @date on 2018/8/30
 * @describe 高铁选择的对象
 * @author jinfeng.xie
 * @version 1.0.0
 *
 */
public class HighSpeedLineModel implements Serializable {
    public String hsPath;
    public String hsname;
    public List<HighSpeedNoModel> noModels;

    public String getHsPath() {
        return hsPath;
    }

    public void setHsPath(String hsPath) {
        this.hsPath = hsPath;
    }

    public String getHsname() {
        return hsname;
    }

    public void setHsname(String hsname) {
        this.hsname = hsname;
    }

    public List<HighSpeedNoModel> getNoModels() {
        return noModels;
    }

    public void setNoModels(List<HighSpeedNoModel> noModels) {
        this.noModels = noModels;
    }

    @Override
    public String toString() {
        return "HighSpeedLineModel{" +
                "hsPath='" + hsPath + '\'' +
                ", hsname='" + hsname + '\'' +
                ", noModels=" + noModels +
                '}';
    }
}
