package com.walktour.mapextention.ibwave;

import android.graphics.RectF;

/**
 * An object bounded on an Axis-Aligned Bounding Box.
 * @author Colonel32
 * @author cnvandev
 */
interface IBoundedObject {
	public RectF getBounds();
}

public class BoundedObject implements IBoundedObject{
	
	private RectF mBounds = new RectF();
	
	public BoundedObject(RectF bound){
		mBounds.set(bound);
	}
	
	public RectF getBounds()
	{
		return mBounds;
	}
	
	public void setmBounds(RectF value)
	{
		mBounds.set(value);
	}
}
