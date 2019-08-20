package com.walktour.gui.map;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.LayoutChangeListener;
import com.walktour.framework.view.ScrollLayout;
import com.walktour.gui.R;

/**
 * LTE-CA 参数界面
 * @author zhihui.lian
 */
public class LteCa extends BasicActivity {
    
    private ScrollLayout scrollLayout;
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_lte_ca_view);
        
        scrollLayout = (ScrollLayout) findViewById(R.id.srooll_layout);
        LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        
        View lteCaView1= new LteCaView(this, 1);
        View lteCaView2= new LteCaView(this, 2);
        View lteCaView3= new LteCaView(this, 3);
        View lteCaView4= new LteCaView(this, 4);
        scrollLayout.addView(lteCaView1, layoutParams);
        scrollLayout.addView(lteCaView2, layoutParams);
        scrollLayout.addView(lteCaView3, layoutParams);
        scrollLayout.addView(lteCaView4, layoutParams);
        
        scrollLayout.addChangeListener(new LayoutChangeListener() {
			@Override
			public void doChange(int lastIndex, int currentIndex) {
				(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_4)).setBackgroundResource(R.drawable.darkdot);
				switch (currentIndex) {
				case 0:
					(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.lightdot);
					break;
				case 1:
					(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.lightdot);
					break;
				case 2:
					(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.lightdot);
					break;
				case 3:
					(initImageView(R.id.switch_4)).setBackgroundResource(R.drawable.lightdot);
					break;
				default:
					(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.lightdot);
					break;
				}

			}
		});
    }

	@Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
	
}
