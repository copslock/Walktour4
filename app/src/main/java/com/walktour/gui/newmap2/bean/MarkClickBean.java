package com.walktour.gui.newmap2.bean;

import com.walktour.gui.newmap2.overlay.OverlayType;

public class MarkClickBean {
    private Object obj;
    private OverlayType overlayType= OverlayType.Alarm;
    private boolean isCurrentPoint=false;//是否当前的坐标点
    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public OverlayType getOverlayType() {
        return overlayType;
    }

    public void setOverlayType(OverlayType overlayType) {
        this.overlayType = overlayType;
    }

    public boolean isCurrentPoint() {
        return isCurrentPoint;
    }

    public void setCurrentPoint(boolean currentPoint) {
        isCurrentPoint = currentPoint;
    }
}
