package com.walktour.gui.replayfloatview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.SeekBar;



/**
 * 
 * @author zhihui.lian
 *	自定义滑动条
 */
public class CustomSeekBar extends SeekBar implements OnGestureListener{

	private GestureDetector  mGestureDetector;
	
	private ViewLinstenser buttonLinstenser;

	public CustomSeekBar(Context context) {
		super(context);
	}
	
	public CustomSeekBar(Context context,ViewLinstenser buttonLinstenser) {
		super(context);
		this.buttonLinstenser = buttonLinstenser;
		// TODO Auto-generated constructor stub
		  mGestureDetector = new GestureDetector(getContext(), this);
	}
	
	public void setImageViewLinstenser(Context context,ViewLinstenser buttonLinstenser){
		this.buttonLinstenser = buttonLinstenser;
		  mGestureDetector = new GestureDetector(getContext(), this);
	}
	
	
	
	
	/** 
     * 该构造方法在静态引入XML文件中是必须的 
     * @param context 
     * @param paramAttributeSet 
     */  
    public CustomSeekBar(Context context,AttributeSet paramAttributeSet){  
        super(context,paramAttributeSet);  
        mGestureDetector = new GestureDetector(getContext(), this);
    }  
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		System.out.println("onTouchEvent");
		mGestureDetector.onTouchEvent(event);
		return true;
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
	System.out.println("onSingleTapUp");
		super.onTouchEvent(e);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		System.out.println("-----------");
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
//		 final int FLING_MIN_DISTANCE = 100, 
//				 FLING_MIN_VELOCITY = 200;  
//	        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(distanceX) > FLING_MIN_VELOCITY) {  
//	            // Fling left  
//	            Log.i("MyGesture", "Fling left");  
//	            Toast.makeText(getContext(), "Fling Left", Toast.LENGTH_SHORT).show();  
//	        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(distanceX) > FLING_MIN_VELOCITY) {  
//	            // Fling right  
//	            Log.i("MyGesture", "Fling right");  
//	            Toast.makeText(getContext(), "Fling Right", Toast.LENGTH_SHORT).show();  
//	           
//	        } else if(e2.getY()-e1.getY()>FLING_MIN_DISTANCE && Math.abs(distanceY)>FLING_MIN_VELOCITY) {
//	            // Fling down  
//	            Log.i("MyGesture", "Fling down");  
//	            Toast.makeText(getContext(), "Fling down", Toast.LENGTH_SHORT).show();
//	            buttonLinstenser.onSroll(e1, e2);
//	        } else if(e1.getY()-e2.getY()>FLING_MIN_DISTANCE && Math.abs(distanceY)>FLING_MIN_VELOCITY) {
//	            // Fling up  
//	            Log.i("MyGesture", "Fling up");  
//	            buttonLinstenser.onSroll(e1, e2);
//	            Toast.makeText(getContext(), "Fling up", Toast.LENGTH_SHORT).show();
//	        }  
		 float y1 = e1.getY(), y2 = e2.getY();  
		 float x1 = e1.getX(), x2 = e2.getX();
	        if ( (y1 -y2 > 15 ) || (y1 - y2 < -15 ) && 
	        		((x1 - x2 > 100) || (x1 - x2 < -100))
	        		) { 
	        	buttonLinstenser.onSroll(e1, e2);
	        		
	        	return true;
	        }else if((e1.getX() > e2.getX()) || (e2.getX() > e1.getX())){
	        	if(y1 -y2 < 100){
	        		super.onTouchEvent(e2);
	        	}
	        	return true;
	        }
	        return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}
	
	
	
}
