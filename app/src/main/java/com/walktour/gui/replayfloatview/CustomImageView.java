package com.walktour.gui.replayfloatview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageButton;

import com.walktour.gui.R;



/**
 * 
 * @author zhihui.lian
 *	自定义ImageView
 */
public class CustomImageView extends ImageButton implements OnGestureListener{

	private GestureDetector  mGestureDetector;
	
	private ViewLinstenser buttonLinstenser;

	public CustomImageView(Context context) {
		super(context);
	}
	
	public CustomImageView(Context context,ViewLinstenser buttonLinstenser) {
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
    public CustomImageView(Context context,AttributeSet paramAttributeSet){  
        super(context,paramAttributeSet);  
        mGestureDetector = new GestureDetector(getContext(), this);
    }  
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			this.setBackgroundResource(R.drawable.bg2);
		}else if(event.getAction() == MotionEvent.ACTION_UP){
			this.setBackgroundResource(R.drawable.bg1);
		}
		mGestureDetector.onTouchEvent(event);
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
		buttonLinstenser.onSroll(e1, e2);
		return true;
	}
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		if (isClickable()) {
			super.performClick();
		}
		return false;
	}
	
	
	
}
