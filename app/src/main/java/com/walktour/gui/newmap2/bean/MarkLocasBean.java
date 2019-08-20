package com.walktour.gui.newmap2.bean;

import com.walktour.model.LocusParamInfo;
import com.walktour.model.MapEvent;

/**
 * @date on 2018/6/22
 * @describe
 * @author jinfeng.xie
 * @version 1.0.0
 *
 */
public class MarkLocasBean {
    MapEvent mapEvent;
    LocusParamInfo locusParamInfo;

    @Override
    public String toString() {
        return "MarkLocasBean{" +
                "mapEvent=" + mapEvent +
                ", locusParamInfo=" + locusParamInfo +
                '}';
    }

    public MapEvent getMapEvent() {
        return mapEvent;
    }

    public void setMapEvent(MapEvent mapEvent) {
        this.mapEvent = mapEvent;
    }

    public LocusParamInfo getLocusParamInfo() {
        return locusParamInfo;
    }

    public void setLocusParamInfo(LocusParamInfo locusParamInfo) {
        this.locusParamInfo = locusParamInfo;
    }
}
