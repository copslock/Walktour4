package com.dinglicom.dataset.model;

/**
 * 从数据集出来的数据变化的接口
 * @author qihang.li
 */
public interface OnDataChangeListener {
	/**
	 * 事件改变
	 * @param model 新增的事件
	 * @param eventCount 当前事件总数
	 */
	void onEventChange(EventModel model,int eventCount);
	
	/**
	 * 查询事件的状态变化
	 * @param state 查询事件的状态: 0,正在查询，1,结束查询
	 */
	void onEventQueryStateChange(int state);
	
	
	/**
	 * 参数统计值更新处理
	 * @param paramVaue
	 */
	void onParamTotalChange(String paramId,String paramVaue);
}
