package com.walktour.gui.map.googlemap.reflection;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class RGestureDetectorCupcake extends GestureDetector {
	OnExGestureListener mListener;

	public RGestureDetectorCupcake(Context context, OnExGestureListener listener) {
		super(context, listener);
		mListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		
        switch (action & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_UP:
        	mListener.onUp(ev);
        }

        return super.onTouchEvent(ev);
	}
	
}
