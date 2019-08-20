package com.walktour.gui.newmap.basestation;

import com.walktour.gui.newmap2.filter.IParamFilter;

/**
 * Created by Yi.Lin on 2018/11/7.
 * 导入基站筛选设置
 */

public class ImportBaseStationFilterManager {

    private static ImportBaseStationFilterManager sInstance;

    private boolean mIsFilter;
    private IParamFilter mFilter;

    private ImportBaseStationFilterManager() {
        //no-op
    }

    public static ImportBaseStationFilterManager getInstance() {
        if (null == sInstance) {
            synchronized (ImportBaseStationFilterManager.class) {
                if (null == sInstance) {
                    sInstance = new ImportBaseStationFilterManager();
                }
            }
        }
        return sInstance;
    }

    public void setFilterStrategy(IParamFilter filter) {
        this.mFilter = filter;
    }

    public IParamFilter getFilterStrategy() {
        return this.mFilter;
    }

    public void setIsFilter(boolean filter) {
        this.mIsFilter = filter;
    }

    /**
     * 是否筛选
     * @return
     */
    public boolean isFilter() {
        return mIsFilter;
    }
}
