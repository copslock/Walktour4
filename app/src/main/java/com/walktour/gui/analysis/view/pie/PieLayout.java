package com.walktour.gui.analysis.view.pie;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

/***
 * 饼图
 * 
 * @author weirong.fan
 *
 */
public class PieLayout extends FrameLayout{
	
	private PieView pie;
	private LineView lineText;

	public PieLayout(Context context, AttributeSet attrs,int width,int height) {
		super(context, attrs);
		pie = new PieView(context, attrs,width,height);
		lineText = new LineView(context, attrs,width,height);
		addView(pie, LayoutParams.MATCH_PARENT);
		lineText.setBackgroundColor(0x00000000);
		addView(lineText, LayoutParams.MATCH_PARENT);
		this.setLayoutParams(new LinearLayout.LayoutParams(width,height));
		
	}
	public void setWidthAndHeight(int width,int height,boolean isRorate){
		pie.setWidth(width, height,isRorate);
		lineText.setWidth(width, height);
		this.setLayoutParams(new LinearLayout.LayoutParams(width,height));
		this.invalidate();
	}
	public void initi(List<EntyExpenses> list){
		pie.initi(list, lineText);
		lineText.initi(list, pie);
	}

	public PieView getPie() {
		return pie;
	}
	
}
