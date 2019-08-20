package com.walktour.model;

import java.util.ArrayList;
import java.util.List;

/**
 * VoLTE异常分析结果类
 * @author zhihui.lian
 *
 */
public class VoLteFaildModel {

	//呼叫类型主叫（MO），被叫（MT） 调用分析枚举
	private int callType;
	//异常原因码
	private int  reasonCode;
	//事件队列
	private List<VoLteEventModel> eventList = new ArrayList<VoLteEventModel>();
	
	
	public int getCallType() {
		return callType;
	}
	public void setCallType(int callType) {
		this.callType = callType;
	}
	public int getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(int reasonCode) {
		this.reasonCode = reasonCode;
	}
	public List<VoLteEventModel> getEventList() {
		return eventList;
	}
	public void setEventList(List<VoLteEventModel> eventList) {
		this.eventList = eventList;
	}
}
