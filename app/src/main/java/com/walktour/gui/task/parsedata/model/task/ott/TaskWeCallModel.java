package com.walktour.gui.task.parsedata.model.task.ott;

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
public class TaskWeCallModel extends TaskModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3129727774129337885L;
	@SerializedName("wxCallConfig")
	private WxCallConfig wxCallConfig =new WxCallConfig();
	public TaskWeCallModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
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

		testTask.append("contactName =" + wxCallConfig.getContactName() + "\r\n");
		testTask.append("callNumber =" + wxCallConfig.getCalledNumber() + "\r\n");
		testTask.append("connectTime =" + wxCallConfig.getConnection() + "\r\n");
		testTask.append("keepTime =" + wxCallConfig.getDuration() + "\r\n");
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
	
	public WxCallConfig getTestConfig(){
		return wxCallConfig;
	}

    /**
     * 是否双向交替测试
     * @return
     */
    public boolean isAlternateTest() {
        return wxCallConfig.isAlternateTest();
    }

    public void setAlternateTest(boolean isAlternateTest){
        wxCallConfig.setAlternateTest(isAlternateTest);
    }

    public boolean isMultiTest() {
        return wxCallConfig.isMultiTest();
    }

    public void setMultiTest(boolean multiTest) {
        wxCallConfig.setMultiTest(multiTest);
    }

    public String getMultiCycleDataName() {
        return wxCallConfig.getMultiCycleDataName();
    }

    public void setMultiCycleDataName(String multiCycleDataName) {
        wxCallConfig.setMultiCycleDataName(multiCycleDataName);
    }

    public int getCycleInterval() {
        return wxCallConfig.getCycleInterval();
    }

    public void setCycleInterval(int cycleInterval) {
        wxCallConfig.setCycleInterval(cycleInterval);
    }

    public int getCycleTimes() {
        return wxCallConfig.getCycleTimes();
    }

    public void setCycleTimes(int cycleTimes) {
        wxCallConfig.setCycleTimes(cycleTimes);
    }
	
	public boolean isRealtimeCalculation() {
		return wxCallConfig.isRealtimeCalculation();
	}

	public void setRealtimeCalculation(boolean realtimeCalculation) {
		wxCallConfig.setRealtimeCalculation(realtimeCalculation);
	}
	public String getCallNumber() {
		return wxCallConfig.getCalledNumber();
	}

	public void setCallNumber(String callNumber) {
		wxCallConfig.setCalledNumber(callNumber);
	}

	public int getConnectTime() {
		return wxCallConfig.getConnection();
	}

	public void setConnectTime(int connectTime) {
		wxCallConfig.setConnection(connectTime);
	}

	public int getKeepTime() {
		return wxCallConfig.getDuration();
	}

	public void setKeepTime(int keepTime) {
		wxCallConfig.setDuration(keepTime);
	}

	public int getMosTest() {
		if(wxCallConfig.isMosTest())
			return 1;
		return 0;
	}

	public void setMosTest(int callMOSServer) {
		if(callMOSServer==1){
			wxCallConfig.setMosTest(true);
		}else{
			wxCallConfig.setMosTest(false);
		}
	}

	/**
	 * 0为PESQ算分,1为POLQA算分
	 * @return
	 */
	public String getCallMosCountStr(){
		return wxCallConfig.getMosAlgorithm();
	}

	public int getCallMOSCount() {
		if(wxCallConfig.getMosAlgorithm().equals(WxCallConfig.MOSAlgorithm_PESQ))
			return 0;
		return 1;
	}

	public void setCallMOSCount(int callMOSCount) {
		if(callMOSCount==0){
			wxCallConfig.setMosAlgorithm(WxCallConfig.MOSAlgorithm_PESQ);
		}else{
			wxCallConfig.setMosAlgorithm(WxCallConfig.MOSAlgorithm_POLQA);
		}
	}

	public int getCallMOSTestType() {
		if(wxCallConfig.getTestType().equals(WxCallConfig.TESTTYPE_MOBILE_LAND)){
			return TaskModel.TASKSTATUS_1;
		}
		return TaskModel.TASKSTATUS_0;
	}

	public void setCallMOSTestType(int callMOSTestType) { 
		if(callMOSTestType==TaskModel.TASKSTATUS_1){
			wxCallConfig.setTestType(WxCallConfig.TESTTYPE_MOBILE_LAND);
		}else{
			wxCallConfig.setTestType(WxCallConfig.TESTTYPE_MOC_ONLY);
		}
	}

	public int getCallMode() {
		if(wxCallConfig.getCallType().equals(WxCallConfig.CALLTYPE_VOICE)){
			return 0;
		}
		return 1;
	}

	public void setCallMode(int callMode) {
		if(callMode==0){
			wxCallConfig.setCallType(WxCallConfig.CALLTYPE_VOICE);
		}else{
			wxCallConfig.setCallType(WxCallConfig.CALLTYPE_VIDEO);
		}
	}

	/**
	 * 是否长呼,1表示长呼.0表示正常模式长
	 * 
	 * @return
	 */
	public int getCallLength() {
		return wxCallConfig.isLongCall()?1:0;
	}

	public void setCallLength(int callLength) {
		if(callLength==0){
			wxCallConfig.setLongCall(false);
		}else
			wxCallConfig.setLongCall(true);
	}


	/**
	 * 样本文件类型:0 NB 8k;1 WB 16k; SWB 48k;
	 * 
	 * @return
	 */
	public int getPolqaSample() {
		if(wxCallConfig.getSampleType().equals(WxCallConfig.SampleType_NB_8k)){
			return 0;
		}else if(wxCallConfig.getSampleType().equals(WxCallConfig.SampleType_WB_16k)){
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
			wxCallConfig.setSampleType(WxCallConfig.SampleType_NB_8k);
		}else if(polqaSample==1){
			wxCallConfig.setSampleType(WxCallConfig.SampleType_WB_16k);
		}else{
			wxCallConfig.setSampleType(WxCallConfig.SampleType_SWB_48k);
		}
	}

	/**
	 * 数分类型:0 NB; 1 SWB
	 * 
	 * @return
	 */
	public int getPolqaCalc() {
		if(wxCallConfig.getCalcMode().equals(WxCallConfig.CalcMode_NB)){
			return 0;
		}
		return 1;
	}

	public void setPolqaCalc(int polqaCalc) {
		if(polqaCalc==0){
			wxCallConfig.setCalcMode(WxCallConfig.CalcMode_NB);
		}else{
			wxCallConfig.setCalcMode(WxCallConfig.CalcMode_SWB);
		}
	}
	
	
	
	/** 是否主被叫联合测试 */
	@Override
	public boolean isUnitTest() {
		if (wxCallConfig.getTestType().equals(WxCallConfig.TESTTYPE_MOBILE_MOBILE)) {
			return true;
		}
		return false;

	}

	/** 设置是否主被叫联合测试 */
	@Override
	public void setUnitTest(boolean isUnitTest) {
		if (isUnitTest) {
			wxCallConfig.setTestType(WxCallConfig.TESTTYPE_MOBILE_MOBILE);
		} else {
			wxCallConfig.setTestType(WxCallConfig.TESTTYPE_MOC_ONLY);
		}

	}

    public String getContactName() {
        return wxCallConfig.getContactName();
    }

    public void setContactName(String contactName) {
        wxCallConfig.setContactName(contactName);
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
				if (tagName.equals("WxCallConfig")) { 
					wxCallConfig.parseXmlMocTest(parser);
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
		if(null!= wxCallConfig)
			wxCallConfig.writeXml(serializer);
	}
	
	@Override
	public String getServerTaskType() {
		return getTaskType();
	}

	public int getDisConnect(){
	    return 0;
    }
	
}
