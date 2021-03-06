package com.walktour.gui.map.googlemap.reflection;

//import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class RGestureDetectorFroyo extends GestureDetector {
	OnExGestureListener mListener;

	//@SuppressLint("NewApi")
	public RGestureDetectorFroyo(Context context, OnExGestureListener listener, Handler handler, boolean ignoreMultitouch) {
		super(context, listener, handler, ignoreMultitouch);
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
