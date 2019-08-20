package com.walktour.model;

/**
 * 存放CSFB异常产生时，当前通话过程中每个事件节点的详细信息
 * @author tangwq
 *
 */
public class CsfbFaildEventModel {
	
	//当前事件节点显示类型，0：表示不显示，1：表示置灰；2：表示有效节点
	private int		nodeShowType= 0;
	//当前事件显示属性	1：普通事件，2：成功事件，3：失败，4：其它
	private int 	eventType	= 1;
	//事件产生时间
	private String 	eventTimes	= "--:--:--";
	//事件ID
	private int 	eventCode	= -9999;
	//事件内容
	private String 	eventMsg	= "";
	//当前事件对应的信令内容
	private String 	signalMsg	= "";
	//当前事件对应的信令方向
	private String 	direction	= "";
	//当前网络显示内容
	private String	currentNet	= "";
	//当前网络对应的参数值,如:BCCH:38350@@BSIC:7@@LAC:-6.12dBm;显示时以@@符分割
	private String 	paramValues			= "";
	//与上一事件的时延
	private String 	delayByLastEvent 	= "";
	//断开原因描述
	private String 	disconnectReasion	= "";
	
	/**
	 * 增加采样点，查询层三详细解码
	 */
	private String l3DetailStr = "";
	


	/**
	 * 实例化异常节点时需传入是否显示，及当前事件ID，事件描述等
	 * @param nodeShowType
	 * @param eventCode
	 * @param eventMsg
	 */
	public CsfbFaildEventModel(int nodeShowType,int eventType,int eventCode,String eventMsg){
		this.nodeShowType 	= nodeShowType;
		this.eventType		= eventType;
		this.eventCode 		= eventCode;
		this.eventMsg 		= eventMsg;
	}
	
	@Override
	public String toString(){
		return "nodeShowType:" + nodeShowType + ";eventTimes:" + eventTimes + ";eventCode:" 
				+ eventCode + ";eventMsg:" + eventMsg + ";signalMsg:" + signalMsg + ";currentNet:" 
				+ currentNet + ";paramValues:" + paramValues + ";delayByLastEvent:" + delayByLastEvent
				+ ";disconnectReasion:" + disconnectReasion;
	}
	
	/**
	 * 当前事件节点显示类型
	 * 0：表示不显示，1：表示置灰；2：表示有效节点
	 * */
	public int getNodeShowType() {
		return nodeShowType;
	}
	public void setNodeShowType(int isEnable) {
		this.nodeShowType = isEnable;
	}
	
	/**当前事件显示属性	1：普通事件，2：成功事件，3：失败，4：其它*/
	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	/**
	 * 事件产生时间
	 * 显示时以hh:MM:ss格式化显示
	 * */
	public String getEventTimes() {
		return eventTimes;
	}
	public void setEventTimes(String eventTimes) {
		this.eventTimes = eventTimes;
	}
	
	/**事件ID*/
	public int getEventCode() {
		return eventCode;
	}
	public void setEventCode(int eventCode) {
		this.eventCode = eventCode;
	}
	
	/**事件内容*/
	public String getEventMsg() {
		return eventMsg;
	}
	public void setEventMsg(String eventMsg) {
		this.eventMsg = eventMsg;
	}
	
	/**当前事件对应的信令内容*/
	public String getSignalMsg() {
		return signalMsg;
	}
	public void setSignalMsg(String signalMsg) {
		this.signalMsg = signalMsg;
	}
	
	/**当前网络显示内容*/
	public String getCurrentNet() {
		return currentNet;
	}
	public void setCurrentNet(String currentNet) {
		this.currentNet = currentNet;
	}
	
	/**
	 * 当前网络对应的参数值
	 * 如:BCCH:38350@@BSIC:7@@LAC:-6.12dBm
	 * 显示时以@@符分割
	 * */
	public String getParamValues() {
		return paramValues;
	}
	public void setParamValues(String paramValues) {
		this.paramValues = paramValues;
	}
	
	/**与上一事件的时延*/
	public String getDelayByLastEvent() {
		return delayByLastEvent;
	}
	public void setDelayByLastEvent(String delayByLastEvent) {
		this.delayByLastEvent = delayByLastEvent;
	}
	
	/**断开原因描述*/
	public String getDisconnectReasion() {
		return disconnectReasion;
	}

	public void setDisconnectReasion(String disconnectReasion) {
		this.disconnectReasion = disconnectReasion;
	}

	public  String getDirection() {
		return direction;
	}

	public  void setDirection(String direction) {
		this.direction = direction;
	}
	
	public String getL3DetailStr() {
		return l3DetailStr;
	}

	public void setL3DetailStr(String l3DetailStr) {
		this.l3DetailStr = l3DetailStr;
	}
	
}
