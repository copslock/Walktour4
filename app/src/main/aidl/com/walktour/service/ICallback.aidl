package com.walktour.service;   
import java.util.Map;
import com.walktour.model.TotalSpecialModel;
interface ICallback {
	void OnEventChange(String event);
	void onCallTestStop(in Map stopResult);
	void OnDataChanged(in Map dataList);
	void onChartDataChanged(in Map chartList);
	Map  callMainProcess(int callType);
	void onParaChanged(int callType,in Map paraValue);
	int getNetWorkType(boolean isPBM);
}