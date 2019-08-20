package com.walktour.gui.task.parsedata.model.task.mtc;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.moc.MOCTestConfig;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

/**
 * 语音被叫对象
 * 
 * @author jianchao.wang
 *
 */
public class TaskPassivityCallModel extends TaskModel { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -899790527791198038L;
	/** 开启MOS测试模式 */
	public static final int MOS_ON = 1;
	/** 关闭MOS测试模式 */
	public static final int MOS_OFF = 0;
	/** 是否并行数据业务,0:无;1:WAP */
	@SerializedName("parallelData")
	private int parallelData;
	@SerializedName("mtcTestConfig")
	private MTCTestConfig mtcTestConfig = new MTCTestConfig();
	public TaskPassivityCallModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Voice);
		setTaskType(WalkStruct.TaskType.PassivityCall.toString());
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

		testTask.append("isUnitTest =" + this.isUnitTest() + "\r\n");
		testTask.append("callMOSServer =" + this.getCallMOSServer() + "\r\n");
		testTask.append("parallelData =" + parallelData + "\r\n");
		testTask.append("polqaSample =" + this.getPolqaSample() + "\r\n");
        testTask.append("polqaCalc =" + this.getPolqaCalc() + "\r\n");
        testTask.append("isAlternateTest =" + this.isAlternateTest() + "\r\n");
        testTask.append("isMultiTest =" + this.isMultiTest() + "\r\n");
        testTask.append("multiCycleDataName =" + this.getMultiCycleDataName() + "\r\n");
        testTask.append("cycleInterval =" + this.getCycleInterval() + "\r\n");
        testTask.append("cycleTimes =" + this.getCycleTimes() + "\r\n");
		return testTask.toString();
	}

    public MTCTestConfig getTestConfig(){
        return mtcTestConfig;
    }

    public boolean isAlternateTest() {
        return mtcTestConfig.isAlternateTest();
    }

    public void setAlternateTest(boolean isAlternateTest){
        mtcTestConfig.setAlternateTest(isAlternateTest);
    }

    public boolean isMultiTest() {
        return mtcTestConfig.isMultiTest();
    }

    public void setMultiTest(boolean multiTest) {
        mtcTestConfig.setMultiTest(multiTest);
    }

    public String getMultiCycleDataName() {
        return mtcTestConfig.getMultiCycleDataName();
    }

    public void setMultiCycleDataName(String multiCycleDataName) {
        mtcTestConfig.setMultiCycleDataName(multiCycleDataName);
    }

    public int getCycleInterval() {
        return mtcTestConfig.getCycleInterval();
    }

    public void setCycleInterval(int cycleInterval) {
        mtcTestConfig.setCycleInterval(cycleInterval);
    }

    public int getCycleTimes() {
        return mtcTestConfig.getCycleTimes();
    }

    public void setCycleTimes(int cycleTimes) {
        mtcTestConfig.setCycleTimes(cycleTimes);
    }

    public boolean isRealtimeCalculation() {
        return mtcTestConfig.isRealtimeCalculation();
    }

    public void setRealtimeCalculation(boolean realtimeCalculation) {
        mtcTestConfig.setRealtimeCalculation(realtimeCalculation);
    }

	public int getCallMOSServer() {
		return mtcTestConfig.getMosTest() ? MOS_ON : MOS_OFF;
	}

	public void setCallMOSServer(int callMOSServer) {
		mtcTestConfig.setMosTest(callMOSServer == MOS_ON);
	}

	public int getCallMOSCount() {
		if (mtcTestConfig.getMosAlgorithm().equals(MTCTestConfig.MOSALGORITHM_PESQ)) {
			return 0;
		} else if (mtcTestConfig.getMosAlgorithm().equals(MTCTestConfig.MOSALGORITHM_POLQA)) {
			return 1;
		} else
			return 0;
	}

	/**
	 * 0为PESQ算分,1为POLQA算分
	 * @return
	 */
	public String getCallMosCountStr(){
		return getCallMOSCount() == 1 ? "POLQA" : "PESQ";
	}

	public void setCallMOSCount(int callMOSCount) {
		if (callMOSCount == 0) {
			mtcTestConfig.setMosAlgorithm(MTCTestConfig.MOSALGORITHM_PESQ);
		} else if (callMOSCount == 1) {
			mtcTestConfig.setMosAlgorithm(MTCTestConfig.MOSALGORITHM_POLQA);
		} else
			mtcTestConfig.setMosAlgorithm(MTCTestConfig.MOSALGORITHM_PESQ);

	}

	public int getParallelData() {
		return parallelData;
	}

	public void setParallelData(int parallelData) {
		this.parallelData = parallelData;
	}

	/** 是否主被叫联合测试 */
	@Override
	public boolean isUnitTest() {
		if (mtcTestConfig.getTestType().equals(MTCTestConfig.TESTTYPE_MTC_ONLY)) {
			return false;
		}
		return true;

	}

	/** 设置是否主被叫联合测试 */
	@Override
	public void setUnitTest(boolean isUnitTest) {
		if (isUnitTest) {
			mtcTestConfig.setTestType(MTCTestConfig.TESTTYPE_MOBILE_MOBILE);
		} else {
			mtcTestConfig.setTestType(MTCTestConfig.TESTTYPE_MTC_ONLY);
		}

	}

	public int getCallMode() {
		if (mtcTestConfig.getCallType().equals(MTCTestConfig.CALLTYPE_VOICE)) {
			return 0;
		}
		return 1;
	}

	public void setCallMode(int callMode) {
		if (callMode == 0) {
			mtcTestConfig.setCallType(MTCTestConfig.CALLTYPE_VOICE);
		} else if (callMode == 1) {
			mtcTestConfig.setCallType(MTCTestConfig.CALLTYPE_VOIDE0);
		} else
			mtcTestConfig.setCallType(MTCTestConfig.CALLTYPE_VOICE);
	}

	public int getPolqaSample() {
		if (mtcTestConfig.getSampleType().equals(MTCTestConfig.SAMPLETYPE_0))
			return 0;
		else if (mtcTestConfig.getSampleType().equals(MTCTestConfig.SAMPLETYPE_1))
			return 1;
		else if (mtcTestConfig.getSampleType().equals(MTCTestConfig.SAMPLETYPE_2))
			return 2;
		return 0;
	}

	/**
	 * 如果当前为PESQ算分直接返回 8000
	 * 否则按POQAL设置返回 0 NB 8k;1 WB 16k; SWB 48k;
	 * 
	 * @return
	 */
	public int getSampleRate(){
		if(getCallMOSCount() == 0){
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
		if (polqaSample == 0)
			mtcTestConfig.setSampleType(MTCTestConfig.SAMPLETYPE_0);
		else if (polqaSample == 1)
			mtcTestConfig.setSampleType(MTCTestConfig.SAMPLETYPE_1);
		else if (polqaSample == 2)
			mtcTestConfig.setSampleType(MTCTestConfig.SAMPLETYPE_2);
		else
			mtcTestConfig.setSampleType(MTCTestConfig.SAMPLETYPE_0);
	}

    /**
     * 数分类型:0 NB; 1 SWB
     *
     * @return
     */
    public int getPolqaCalc() {
        if(mtcTestConfig.getCalcMode().equals(MOCTestConfig.CalcMode_NB)){
            return 0;
        }
        return 1;
    }

	public void setPolqaCalc(int polqaCalc){
        if(polqaCalc==0){
            mtcTestConfig.setCalcMode(MOCTestConfig.CalcMode_NB);
        }else{
            mtcTestConfig.setCalcMode(MOCTestConfig.CalcMode_SWB);
        }
    }

	public MTCTestConfig getMtcTestConfig() {
		return mtcTestConfig;
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
				if (tagName.equals("MTCTestConfig")) {
					mtcTestConfig.parseXmlMtcTest(parser);
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
		if (null != mtcTestConfig) {
			mtcTestConfig.writeXml(serializer);
		}
	}

	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.PassivityCall.getXmlTaskType();
	}
}
