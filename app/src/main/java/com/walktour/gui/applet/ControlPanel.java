package com.walktour.gui.applet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.walktour.gui.R;

/**
 * @class 方向控制键
 * @author qihang.li@dinglicom.com
 * */
public class ControlPanel extends RelativeLayout{
	public static  final int PANEL_WIDTH = 240;
	public static  final int PANEL_HEIGHT = 240;
	/**移动时需要移动的像素*/
	public final static int MOVE_PIX = 3; 
	/**操作方向键*/
	public final static String ACTION_CONTROL = "com.walktour.map.ControlPanelaction.control";
	/**打点*/
	public final static String ACTION_MARK = "com.walkour.map.ControlPanel.atcion.mark";
	public final static String KEY_CONTROL = "control";
	public final static String KEY_LEFT = "marginleft";
	public final static String KEY_TOP = "margintop";
	public final static int CONTROL_UP    = 1;
	public final static int CONTROL_DOWN  = 2;
	public final static int CONTROL_LEFT  = 3;
	public final static int CONTROL_RIGHT = 4;
	public final static int CONTROL_ENTER = 5;
	
	private Context context;
	private Button btnLeft;
	private Button btnRight;
	private Button btnUp;
	private Button btnDown;
	private Button btnEnter;
	
	public ControlPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context ;
		findView();
	}
	
	private void findView(){
		LayoutInflater factory = LayoutInflater.from(context);
		View view = factory.inflate( R.layout.controlpanel, null);
		this.addView(view);
		btnLeft = (Button)view.findViewById( R.id.ButtonLeft );
		btnRight = (Button)view.findViewById( R.id.ButtonRight );
		btnUp = (Button)view.findViewById( R.id.ButtonUp );
		btnDown = (Button)view.findViewById( R.id.ButtonDown );
		btnEnter = (Button)view.findViewById( R.id.ButtonEnter );
	}
	
	public void setButtonListener( OnClickListener onClickListener){
		this.btnDown.setOnClickListener(onClickListener);
		this.btnEnter.setOnClickListener( onClickListener );
		this.btnLeft.setOnClickListener(onClickListener);
		this.btnRight.setOnClickListener(onClickListener);
		this.btnUp.setOnClickListener(onClickListener);
	}
	
	public void setButtonPressListener(OnTouchListener onTouchListener){
		this.btnDown.setOnTouchListener(onTouchListener);
		this.btnEnter.setOnTouchListener(onTouchListener);
		this.btnLeft.setOnTouchListener(onTouchListener);
		this.btnRight.setOnTouchListener(onTouchListener);
		this.btnUp.setOnTouchListener(onTouchListener);
		this.btnUp.setOnTouchListener(onTouchListener);
	}
	
}