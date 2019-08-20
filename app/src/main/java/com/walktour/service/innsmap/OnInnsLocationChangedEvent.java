package com.walktour.service.innsmap;

import com.innsmap.InnsMap.location.bean.INNSMapLocation;

/**
 * Created by Yi.Lin on 2018/2/1.
 * 当寅时定位点发生变化时发送的事件
 */

public class OnInnsLocationChangedEvent {
    private INNSMapLocation location;

    public OnInnsLocationChangedEvent(INNSMapLocation location) {
        this.location = location;
    }

    public INNSMapLocation getLocation() {
        return location;
    }

    public void setLocation(INNSMapLocation location) {
        this.location = location;
    }
}
