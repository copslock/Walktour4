package com.walktour.gui.task.parsedata.model.task.moc;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

/**
 * 语音主叫对象
 * 
 * @author jianchao.wang
 *
 */
public class TaskInitiativeCallModel extends TaskModel { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 3129727774129337885L;
	@SerializedName("mocTestConfig")
	private MOCTestConfig mocTestConfig=new MOCTestConfig();
	public TaskInitiativeCallModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Voice);
		setTaskType(WalkStruct.TaskType.InitiativeCall.toString());
	}

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @author tangwq
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());

		testTask.append("callNumber =" + mocTestConfig.getCalledNumber() + "\r\n");
		testTask.append("connectTime =" + mocTestConfig.getConnection() + "\r\n");
		testTask.append("keepTime =" + mocTestConfig.getDuration() + "\r\n");
		testTask.append("callMOSServer =" + this.getMosTest() + "\r\n");
		testTask.append("isUnitTest =" + isUnitTest + "\r\n");
		testTask.append("polqaSample =" +this.getPolqaSample() + "\r\n");
		testTask.append("polqaCalc =" + this.getPolqaCalc() + "\r\n");
		testTask.append("isAlternateTest =" + this.isAlternateTest() + "\r\n");
		testTask.append("isMultiTest =" + this.isMultiTest() + "\r\n");
		testTask.append("multiCycleDataName =" + this.getMultiCycleDataName() + "\r\n");
		testTask.append("cycleInterval =" + this.getCycleInterval() + "\r\n");
		testTask.append("cycleTimes =" + this.getCycleTimes() + "\r\n");
		return testTask.toString();
	}
	
	public MOCTestConfig getTestConfig(){
		return mocTestConfig;
	}

    /**
     * 是否双向交替测试
     * @return
     */
    public boolean isAlternateTest() {
        return mocTestConfig.isAlternateTest();
    }

    public void setAlternateTest(boolean isAlternateTest){
        mocTestConfig.setAlternateTest(isAlternateTest);
    }

    public boolean isMultiTest() {
        return mocTestConfig.isMultiTest();
    }

    public void setMultiTest(boolean multiTest) {
        mocTestConfig.setMultiTest(multiTest);
    }

    public String getMultiCycleDataName() {
        return mocTestConfig.getMultiCycleDataName();
    }

    public void setMultiCycleDataName(String multiCycleDataName) {
        mocTestConfig.setMultiCycleDataName(multiCycleDataName);
    }

    public int getCycleInterval() {
        return mocTestConfig.getCycleInterval();
    }

    public void setCycleInterval(int cycleInterval) {
        mocTestConfig.setCycleInterval(cycleInterval);
    }

    public int getCycleTimes() {
        return mocTestConfig.getCycleTimes();
    }

    public void setCycleTimes(int cycleTimes) {
        mocTestConfig.setCycleTimes(cycleTimes);
    }
	
	public boolean isRealtimeCalculation() {
		return mocTestConfig.isRealtimeCalculation();
	}

	public void setRealtimeCalculation(boolean realtimeCalculation) {
		mocTestConfig.setRealtimeCalculation(realtimeCalculation);
	}
	public String getCallNumber() {
		return mocTestConfig.getCalledNumber();
	}

	public void setCallNumber(String callNumber) {
		mocTestConfig.setCalledNumber(callNumber);
	}

	public int getConnectTime() {
		return mocTestConfig.getConnection();
	}

	public void setConnectTime(int connectTime) {
		mocTestConfig.setConnection(connectTime);
	}

	public int getKeepTime() {
		return mocTestConfig.getDuration();
	}

	public void setKeepTime(int keepTime) {
		mocTestConfig.setDuration(keepTime);
	}

	public int getMosTest() {
		if(mocTestConfig.isMosTest())
			return 1;
		return 0;
	}

	public void setMosTest(int callMOSServer) {
		if(callMOSServer==1){
			mocTestConfig.setMosTest(true);
		}else{
			mocTestConfig.setMosTest(false);
		}
	}

	/**
	 * 0为PESQ算分,1为POLQA算分
	 * @return
	 */
	public String getCallMosCountStr(){
		return mocTestConfig.getMosAlgorithm();
	}

	public int getCallMOSCount() {
		if(mocTestConfig.getMosAlgorithm().equals(MOCTestConfig.MOSAlgorithm_PESQ))
			return 0;
		return 1;
	}

	public void setCallMOSCount(int callMOSCount) {
		if(callMOSCount==0){
			mocTestConfig.setMosAlgorithm(MOCTestConfig.MOSAlgorithm_PESQ);
		}else{
			mocTestConfig.setMosAlgorithm(MOCTestConfig.MOSAlgorithm_POLQA);
		}
	}

	public int getCallMOSTestType() {
		if(mocTestConfig.getTestType().equals(MOCTestConfig.TESTTYPE_MOBILE_LAND)){
			return TaskModel.TASKSTATUS_1;
		}
		return TaskModel.TASKSTATUS_0;
	}

	public void setCallMOSTestType(int callMOSTestType) { 
		if(callMOSTestType==TaskModel.TASKSTATUS_1){
			mocTestConfig.setTestType(MOCTestConfig.TESTTYPE_MOBILE_LAND);
		}else{
			mocTestConfig.setTestType(MOCTestConfig.TESTTYPE_MOC_ONLY);
		}
	}

	public int getCallMode() {
		if(mocTestConfig.getCallType().equals(MOCTestConfig.CALLTYPE_VOICE)){
			return 0;
		}
		return 1;
	}

	public void setCallMode(int callMode) {
		if(callMode==0){
			mocTestConfig.setCallType(MOCTestConfig.CALLTYPE_VOICE);
		}else{
			mocTestConfig.setCallType(MOCTestConfig.CALLTYPE_VIDEO);
		}
	}

	/**
	 * 是否长呼,1表示长呼.0表示正常模式长
	 * 
	 * @return
	 */
	public int getCallLength() {
		return mocTestConfig.isLongCall()?1:0;
	}

	public void setCallLength(int callLength) {
		if(callLength==0){
			mocTestConfig.setLongCall(false);
		}else
			mocTestConfig.setLongCall(true);
	}


	/**
	 * 样本文件类型:0 NB 8k;1 WB 16k; SWB 48k;
	 * 
	 * @return
	 */
	public int getPolqaSample() {
		if(mocTestConfig.getSampleType().equals(MOCTestConfig.SampleType_NB_8k)){
			return 0;
		}else if(mocTestConfig.getSampleType().equals(MOCTestConfig.SampleType_WB_16k)){
			return 1;
		}else{
			return 2;
		}
	}

	/**
	 * 如果当前为PESQ算分直接返回 8000
	 * 否则按POQAL设置返回 0 NB 8k;1 WB 16k; SWB 48k;
	 * 
	 * @return
	 */
	public int getSampleRate(){
		if(this.getCallMOSCount() == 0){
			return 8000;
		}
		switch(getPolqaSample()){
		case 1:
			return 16000;
		case 2:
			return 48000;
		default:
			return 8000;
		}
	}

	public void setPolqaSample(int polqaSample) {
		if(polqaSample==0){
			mocTestConfig.setSampleType(MOCTestConfig.SampleType_NB_8k);
		}else if(polqaSample==1){
			mocTestConfig.setSampleType(MOCTestConfig.SampleType_WB_16k);
		}else{
			mocTestConfig.setSampleType(MOCTestConfig.SampleType_SWB_48k);
		}
	}

	/**
	 * 数分类型:0 NB; 1 SWB
	 * 
	 * @return
	 */
	public int getPolqaCalc() {
		if(mocTestConfig.getCalcMode().equals(MOCTestConfig.CalcMode_NB)){
			return 0;
		}
		return 1;
	}

	public void setPolqaCalc(int polqaCalc) {
		if(polqaCalc==0){
			mocTestConfig.setCalcMode(MOCTestConfig.CalcMode_NB);
		}else{
			mocTestConfig.setCalcMode(MOCTestConfig.CalcMode_SWB);
		}
	}

	public MOCTestConfig getMocTestConfig() {
		return mocTestConfig;
	}
	/** 是否主被叫联合测试 */
	@Override
	public boolean isUnitTest() {
		if (mocTestConfig.getTestType().equals(MOCTestConfig.TESTTYPE_MOBILE_MOBILE)) {
			return true;
		}
		return false;

	}

	/** 设置是否主被叫联合测试 */
	@Override
	public void setUnitTest(boolean isUnitTest) {
		if (isUnitTest) {
			mocTestConfig.setTestType(MOCTestConfig.TESTTYPE_MOBILE_MOBILE);
		} else {
			mocTestConfig.setTestType(MOCTestConfig.TESTTYPE_MOC_ONLY);
		}

	}
 
	
	/**
	 * 解析Moc 测试
	 * 
	 * @param parser
	 * @throws Exception
	 */

	public void parseXml(XmlPullParser parser,List<TaskModel> tasks,Map<String,String> map) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("MOCTestConfig")) { 
					mocTestConfig.parseXmlMocTest(parser);
				} else{
					super.parsrXmlPublic(parser,map);
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
	public void writeXml(XmlSerializer serializer)  throws Exception {
		super.writeXml(serializer);
		if(null!=mocTestConfig)
			mocTestConfig.writeXml(serializer);
	}
	
	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.InitiativeCall.getXmlTaskType();
	}
	
}
