package com.walktour.mapextention.ibwave;

import com.jhlabs.map.java.Rectangle2D;
import com.jhlabs.map.proj.Projection;

public abstract class TabMap {
	private String mName = "";
	private Drawer mDrawer;

	public Drawer getmDrawer() {
		return mDrawer;
	}

	public void setmDrawer(Drawer mDrawer) {
		this.mDrawer = mDrawer;
	}

	public Rectangle2D.Double getDataScope() {
		Rectangle2D.Double ds = new Rectangle2D.Double(0, 0, 0, 0);
		return ds;
	}

	public abstract void recalcDataScope(Projection aProj);

	public String getLayerName() {
		return mName;
	}

	public void setLayerName(String Value) {
		mName = Value;
	}
}
