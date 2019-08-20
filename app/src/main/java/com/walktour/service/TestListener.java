package com.walktour.service;

import com.walktour.gui.map.GenericPara;

import java.util.Vector;

public interface TestListener {
	public void OnEventChange(String event);
	public void OnDataChanged(Vector<GenericPara> dataList);
	public void onChartDataChanged(Vector<GenericPara> chartList);
}
