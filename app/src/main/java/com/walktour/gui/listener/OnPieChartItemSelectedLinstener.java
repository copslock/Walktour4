package com.walktour.gui.listener;

import com.walktour.gui.analysis.csfb.CsfbPieChartView;

/**
 * 饼状图的块被点击的消息监听
 * zhihui.lian
 */

public interface OnPieChartItemSelectedLinstener {

	/**
	 * 回调
	 * 
	 */
	void onPieChartItemSelected(CsfbPieChartView view, int position, String colorRgb, float size, float rate, boolean isFreePart, float rotateTime);
	
	/**
	 * 点击中心圆监听
	 */
	void onCircleOnClick();
}
