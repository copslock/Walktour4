/**
 * 
 */
package com.walktour.gui.map;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * @author zhengmin E-mail:min.zheng@dinglicom
 * @version 创建时间：2010-7-29 下午05:33:02
 * 类说明
 */
/**
 * @author admin
 *
 */
public class CustomButton extends TextView{
	private Context con;
	public static final String HIDE_MENU = "hide_menu";
	/**
	 * @param context
	 * @param attrs
	 */
	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub 
        this.con = context; 
        this.setOnClickListener(new OnClickListener() { 
            public void onClick(View v) {           
            	Intent intent= new Intent(); 
    			intent.setAction(HIDE_MENU); 
    			con.sendBroadcast(intent); 
    			
            } 
    }); 
 
	}

}
