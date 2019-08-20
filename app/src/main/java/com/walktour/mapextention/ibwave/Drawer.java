package com.walktour.mapextention.ibwave;

import android.graphics.Canvas;

import com.jhlabs.map.proj.Projection;

public abstract class Drawer {
	public abstract void Draw(Projection aProj, TabMap aMap, Canvas canvas, ViewPort aViewPort);
}
