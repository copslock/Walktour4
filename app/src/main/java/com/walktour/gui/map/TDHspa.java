package com.walktour.gui.map;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.LayoutChangeListener;
import com.walktour.framework.view.ScrollLayout;
import com.walktour.gui.R;

public class TDHspa extends BasicActivity {
	 private ScrollLayout scrollLayout;
	    
		public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.map_tdhspaview);
	        
	        scrollLayout = (ScrollLayout) findViewById(R.id.srooll_layout);
	        LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	        
	        View hspaView1= new TdHspaView(this, 1);
	        View hspaView2= new TdHspaView(this, 2);
	        View hspaView3= new TdHspaView(this, 3);			//暂时屏蔽tdhspa第三页,时隙图
	        scrollLayout.addView(hspaView1, layoutParams);
	        scrollLayout.addView(hspaView2, layoutParams);
	        scrollLayout.addView(hspaView3, layoutParams);
	        
	        scrollLayout.addChangeListener(new LayoutChangeListener() {
	            @Override
	            public void doChange(int lastIndex, int currentIndex) {
	                if(currentIndex == 0){
	                    (initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.lightdot);
	                    (initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.darkdot);
	                    (initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.darkdot);
	                }else if(currentIndex == 1){
	                    (initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.darkdot);
	                    (initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.lightdot);
	                    (initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.darkdot);
	                }else if(currentIndex == 2){
	                	(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.darkdot);
	                    (initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.darkdot);
	                    (initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.lightdot);
	                }
	                
	            }
	        });
	    }

		@Override
	    protected void onSaveInstanceState(Bundle outState) {
	        //No call for super(). Bug on API Level > 11.
	    }
}
