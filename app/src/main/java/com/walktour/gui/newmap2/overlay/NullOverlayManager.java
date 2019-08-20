package com.walktour.gui.newmap2.overlay;

import android.content.Context;

/**
 * @author zhicheng.chen
 * @date 2018/11/26
 * the manager is do nothing
 */
public class NullOverlayManager extends BaseOverlayManager {

    public NullOverlayManager(Context context) {
        super(context);
    }

    @Override
    public OverlayType getOverlayType() {
        return OverlayType.Null;
    }

    @Override
    public boolean addOverlay(Object... obj) {
        return false;
    }

    @Override
    public boolean clearOverlay() {
        return false;
    }
}
