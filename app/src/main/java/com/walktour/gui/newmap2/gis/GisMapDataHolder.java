package com.walktour.gui.newmap2.gis;

import com.walktour.framework.database.model.BaseStation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhicheng.chen
 * @date 2018/11/20
 */
public class GisMapDataHolder {

    private static GisMapDataHolder Instance;
    private List<BaseStation> mdata = new ArrayList<>();

    public static GisMapDataHolder get() {
        if (Instance == null) {
            synchronized (GisMapDataHolder.class) {
                if (Instance == null) {
                    Instance = new GisMapDataHolder();
                }
            }
        }
        return Instance;
    }

    public void setData(List<BaseStation> data) {
        if (data != null) {
            mdata.clear();
            mdata.addAll(data);
        }
    }

    public List<BaseStation> getData() {
        return mdata;
    }

    public void clearAllData() {
        mdata.clear();
        Instance = null;
    }
}
