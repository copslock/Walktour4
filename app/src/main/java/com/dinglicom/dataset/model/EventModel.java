package com.dinglicom.dataset.model;

/**
 * icon_event_1：包括RCU事件和非RCU事件
 * @date 2013.08.30
 * @author qihang.li
 */
public class EventModel {
	
	/**
	 * 原有的事件显示标准事件，测试时显示　
	 */
	public static final int TYPE_STANDER = 0;
	/**
	 * 数据集出来的事件，每个都有RCU ID对应　
	 */
	public static final int TYPE_RCU = 1;
	/**
	 * 标签事件　
	 */
	public static final int TYPE_TAG = 2 ;
	/**
	 * 自定义事件　
	 */
	public static final int TYPE_DEFINE = 3 ;
	
	
	/**
	 * 事件发生的时间
	 */
	private long time = 0;
	/**
	 * 要显示的事件字符串，可以是直接从数据集取到的，如Call Established 
	 * 也可以是自定义的非RCU事件如
	 */
	private String eventStr = "";
	/**
	 * 事件的RCU ID，有部分事件只显示在界面，没有对应RCU ID的事件，这个值 为0
	 */
	private int rcuId = -1 ;
	/**
	 * 数据集出来的事件序号
	 */
	private int eventIndex = -1;
	/**
	 * 从数据集出来的采样点序号,查事件的附属
	 */
	private int pointIndex = -1;
	/**
	 * 显示的附属结构
	 */
	private String descDisplay = "";
	
	private boolean error = false;
	
	private boolean isAlarm = false;
	
	private boolean showOnMap = false;
	
	private boolean showOnChart = false;
	
	private boolean showOnTotal = false;
	
	/**
	 * 自定义事件的时延
	 */
	private int customDelay = 0;
	private String customEventName = "";
	
	/**
	 * 事件类型　
	 */
	private int type = 0;
	
	private String iconDrawablePath = "";

	private boolean isNeedMark;//针对筛选结果，是否需要标记不同颜色

	public boolean isNeedMark() {
		return isNeedMark;
	}

	public void setNeedMark(boolean needMark) {
		isNeedMark = needMark;
	}
	
	/**
	 * icon_event_1
	 * @param time 时间
	 * @param eventStr 要显示的字符串,带String.formart格式
	 */
	public EventModel(long time ,String eventStr,int type){
		this.time = time;
		this.eventStr = eventStr;
		if( eventStr.toLowerCase().contains("drop") 
				|| eventStr.toLowerCase().contains("fail") 
				|| eventStr.toLowerCase().contains("block") ){
			error = true;
		}
		this.type = type;
	}
	public long getTime() {
		return time;
	}
	public int getRcuId() {
		return rcuId;
	}
	public void setRcuId(int rcuId) {
		this.rcuId = rcuId;
	}
	public int getEventIndex() {
		return eventIndex;
	}
	public void setEventIndex(int eventIndex) {
		this.eventIndex = eventIndex;
	}
	public int getPointIndex() {
		return pointIndex;
	}
	public void setPointIndex(int index) {
		this.pointIndex = index;
	}
	/**
	 * @return 完整的事件显示内容，包括附属结构
	 */
	public String getEventStr() {
		String str =  eventStr;
		
		//附属结构
		if( this.descDisplay.trim().length()>0 ){
			str += "\n"+this.descDisplay;
		}
		
		return str;
	}
	
	/**
	 * 给附属结构赋值
	 */
	public void setDescStr(String desc){
		this.descDisplay = desc;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public boolean isAlarm() {
		return isAlarm;
	}
	public void setAlarm(boolean isAlarm) {
		this.isAlarm = isAlarm;
	}
	public boolean isShowOnMap() {
		return showOnMap;
	}
	public void setShowOnMap(boolean showOnMap) {
		this.showOnMap = showOnMap;
	}
	public boolean isShowOnChart() {
		return showOnChart;
	}
	public void setShowOnChart(boolean showOnChart) {
		this.showOnChart = showOnChart;
	}
	public boolean isShowOnTotal() {;
		return showOnTotal;
	}
	public void setShowOnTotal(boolean showOnTotal) {
		this.showOnTotal = showOnTotal;
	}
	public int getCustomDelay() {
		return customDelay;
	}
	public void setCustomDelay(int customDelay) {
		this.customDelay = customDelay;
	}
	public String getCustomEventName() {
		return customEventName;
	}
	public void setCustomEventName(String customEventName) {
		this.customEventName = customEventName;
	}
	public String getIconDrawablePath() {
		return iconDrawablePath;
	}
	public void setIconDrawablePath(String iconDrawablePath) {
		this.iconDrawablePath = iconDrawablePath;
	}

	@Override
	public String toString() {
		return "EventModel{" +
				"time=" + time +
				", eventStr='" + eventStr + '\'' +
				", rcuId=" + rcuId +
				", eventIndex=" + eventIndex +
				", pointIndex=" + pointIndex +
				", descDisplay='" + descDisplay + '\'' +
				", error=" + error +
				", isAlarm=" + isAlarm +
				", showOnMap=" + showOnMap +
				", showOnChart=" + showOnChart +
				", showOnTotal=" + showOnTotal +
				", customDelay=" + customDelay +
				", customEventName='" + customEventName + '\'' +
				", type=" + type +
				", iconDrawablePath='" + iconDrawablePath + '\'' +
				'}';
	}
}
