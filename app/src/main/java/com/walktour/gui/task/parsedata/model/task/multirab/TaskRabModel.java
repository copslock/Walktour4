package com.walktour.gui.task.parsedata.model.task.multirab;

import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * 并发业务处理
 * 
 * @author weirong.fan
 *
 */
public class TaskRabModel extends TaskModel {

	private static final long serialVersionUID = -5306890279923476276L;

	public TaskRabModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setTaskType(WalkStruct.TaskType.MultiRAB.toString());
	}
 
	private int intervalTime; // 间隔时间 

	private String rabRule = ""; // 并发规则，ipad下发用

	private String refTask = ""; // ipad下发用

	private int refServiceIndex = 0; // ipad下发用

	private ArrayList<TaskModel> taskModela = new ArrayList<TaskModel>(); // 保存子任务队列

	private ParallelServiceTestConfig parallelServiceTestConfig=new ParallelServiceTestConfig();
	private NetworkConnectionSetting networkConnectionSetting=new NetworkConnectionSetting();

	/**
	 * 获得当前测试计划写入RCU文件的字符串 
	 * 
	 * @author zhihui.lian
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());

		testTask.append("IntervalTime =" + intervalTime + "\r\n");
		testTask.append("VoiceDelay =" + this.getVoiceDelay() + "\r\n");
		testTask.append("DetailPlan" + "\r\n");
		for (Object model : taskModela) {
			testTask.append(TaskTestObject.getTestPlanInfo((TaskModel) model));
		}

		return testTask.toString();
	}
	@Override
	public int getTypeProperty() {
		if(networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_WLAN))
			return WalkCommonPara.TypeProperty_Wlan;
		else if(networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_PPPNB)){
			return WalkCommonPara.TypeProperty_Ppp;
		}
		return WalkCommonPara.TypeProperty_Net;
	}



	@Override
	public void setTypeProperty(int typeProperty) {
		if(typeProperty==WalkCommonPara.TypeProperty_Wlan){
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_WLAN);
		} else if(typeProperty==WalkCommonPara.TypeProperty_Ppp){
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_PPPNB);
		}else{
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_PPP);
		}
	}
	/***
	 * 数据业务复写这两个方法，以适配历史的业务
	 */
	@Override
	public int getDisConnect() {
		return networkConnectionSetting.getDisConnect();
	}

	public void setDisConnect(int disConnect) {
		networkConnectionSetting.setDisConnect(disConnect);
	}

	public int getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
	}

	public ArrayList<TaskModel> getTaskModel() {
		taskModela.clear();
		taskModela.addAll(parallelServiceTestConfig.getTaskList());
		return this.taskModela;
	}

	@SuppressWarnings("unchecked")
	public void setTaskModelList(ArrayList<TaskModel> taskModel) {
		this.taskModela = (ArrayList<TaskModel>) taskModel.clone();
	}

	/**
	 * 固化并发子业务列表
	 */
	public void addSubTaskModels(ArrayList<TaskModel> taskModel){
		parallelServiceTestConfig.getTaskList().clear();
		parallelServiceTestConfig.getTaskList().addAll(taskModel);
	}
	
	public ArrayList<TaskModel> addTaskList(TaskModel taskModel) {
		taskModela.add(taskModel);
		parallelServiceTestConfig.getTaskList().add(taskModel);
		return taskModela;
	}

	/**
	 * @return the voiceDelay
	 */
	public int getVoiceDelay() {
		return parallelServiceTestConfig.getVoiceAheadData();
	}

	/**
	 * @param voiceDelay
	 *            the voiceDelay to set
	 */
	public void setVoiceDelay(int voiceDelay) {
		parallelServiceTestConfig.setVoiceAheadData(voiceDelay);
	}

	/**
	 * 获得单次并发总超时时间 单位秒
	 * 
	 * @return
	 */
	public int getSingleParallelTimeout() {
		return parallelServiceTestConfig.getTotalTimeout();
	}

	public void setSingleParallelTimeout(int singleParallelTimeout) {
		parallelServiceTestConfig.setTotalTimeout(singleParallelTimeout);
	}

	// 以下属性是DTLog文件中用到的
	private String startLable = "";
	private String endLable = "";

	private int firstServiceEndType = 0;
	private int firstServiceEndDelay = 0;
	private int secondServiceStartType = 0;
	private int secondServiceStartDelay = 10;
	private int secondServiceEndType = 0;
	private int secondServiceEndDelay = 0;

	public String getStartLable() {
		return startLable;
	}

	public void setStartLable(String startLable) {
		this.startLable = startLable;
	}

	public String getEndLable() {
		return endLable;
	}

	public void setEndLable(String endLable) {
		this.endLable = endLable;
	}

	public int getFirstServiceEndType() {
		return firstServiceEndType;
	}

	public void setFirstServiceEndType(int firstServiceEndType) {
		this.firstServiceEndType = firstServiceEndType;
	}

	public int getFirstServiceEndDelay() {
		return firstServiceEndDelay;
	}

	public void setFirstServiceEndDelay(int firstServiceEndDelay) {
		this.firstServiceEndDelay = firstServiceEndDelay;
	}

	public int getSecondServiceStartType() {
		return secondServiceStartType;
	}

	public void setSecondServiceStartType(int secondServiceStartType) {
		this.secondServiceStartType = secondServiceStartType;
	}

	public int getSecondServiceStartDelay() {
		return secondServiceStartDelay;
	}

	public void setSecondServiceStartDelay(int secondServiceStartDelay) {
		this.secondServiceStartDelay = secondServiceStartDelay;
	}

	public int getSecondServiceEndType() {
		return secondServiceEndType;
	}

	public void setSecondServiceEndType(int secondServiceEndType) {
		this.secondServiceEndType = secondServiceEndType;
	}

	public int getSecondServiceEndDelay() {
		return secondServiceEndDelay;
	}

	public void setSecondServiceEndDelay(int secondServiceEndDelay) {
		this.secondServiceEndDelay = secondServiceEndDelay;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getRabRule() {
		return rabRule;
	}

	public void setRabRule(String rabRule) {
		this.rabRule = rabRule;
	}

	public String getRefTask() {
		return refTask;
	}

	public void setRefTask(String refTask) {
		this.refTask = refTask;
	}

	public int getRefServiceIndex() {
		return refServiceIndex;
	}

	public void setRefServiceIndex(int refServiceIndex) {
		this.refServiceIndex = refServiceIndex;
	}

	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
	}
 
	public ParallelServiceTestConfig getParallelServiceTestConfig() {
		return parallelServiceTestConfig;
	}

	public void parseXml(XmlPullParser parser, List<TaskModel> tasks, Map<String, String> map) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("ParallelServiceTestConfig")) { 
					parallelServiceTestConfig.parseXml(parser);
				} else if (tagName.equals("NetworkConnectionSetting")) { 
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attName = parser.getAttributeName(i);
						String attValue = parser.getAttributeValue(i);
						if (attName.equals("IsAvailable")) {
							networkConnectionSetting.setAvailable(stringToBool(attValue));
						}
					}
					networkConnectionSetting.parseXml(parser);

				} else {// 解析公共属性
					parsrXmlPublic(parser, map);
				}

				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("TaskConfig")) {
					tasks.add(this);
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	@Override
	public void writeXml(XmlSerializer serializer) throws Exception {
		super.writeXml(serializer);
		if (null != parallelServiceTestConfig)
			parallelServiceTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.MultiRAB.getXmlTaskType();
	}
}
