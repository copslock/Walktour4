package com.walktour.gui.newmap2.overlay;

/**
 * @author zhicheng.chen
 * @date 2018/6/6
 */
public enum OverlayType {
    Null(-1), Alarm(0), BaseStation(1), LocasPoint(2), MetroRoute(3), CellLink(4), RangingLink(5), MifMap(6), HeatMap(7), GisStation(8),HsRoute(9);
    private int mId;

    OverlayType(int id) {
        this.mId = id;
    }

    public static OverlayType get(int id) {
        for (OverlayType type : values()) {
            if (type.getId() == id)
                return type;
        }
        return null;
    }

    public int getId() {
        return mId;
    }
}
