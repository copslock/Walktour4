package com.walktour.gui.task.parsedata.model.task.mtc;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class MTCTestConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -8731870527807400709L;
	public static final String SAMPLETYPE_0 = "NB 8k";
	public static final String SAMPLETYPE_1 = "WB 16k";
	public static final String SAMPLETYPE_2 = "SWB 48k";

	public static final String MOSALGORITHM_PESQ = "PESQ";
	public static final String MOSALGORITHM_POLQA = "POLQA";

	public static final String TESTTYPE_MTC_ONLY = "MTC Only";
	public static final String TESTTYPE_MOBILE_MOBILE = "Mobile to Mobile Sync";

	public static final String CALLTYPE_VOICE="Voice Call";
	public static final String CALLTYPE_VOIDE0="Video Call";
	@SerializedName("calcMode")
	private String calcMode;
	@SerializedName("lowMOSThreshold")
	private String lowMOSThreshold;
	/** POLQA放音样本类型, 样本文件类型:0 NB 8k;1 WB 16k; SWB 48k; 仅当MOSAlgorithm=POLQA有效 */
	@SerializedName("sampleType")
	private String sampleType = SAMPLETYPE_2;
	/** 0为PESQ算分,1为POLQA算分 PESQ/POLQA */
	@SerializedName("mosAlgorithm")
	private String mosAlgorithm = MOSALGORITHM_PESQ;
	/** 是否进行语音评估测试 True：进行MOS测试 False：不进行MOS测试； 默认False */
	@SerializedName("mosTest")
	private boolean mosTest = false;
	/**是否主被叫联合测试  0：MTC Only(单被叫)； 1：MO-MT(主被叫联合)； */
	@SerializedName("testType")
	private String testType = TESTTYPE_MTC_ONLY;
	/**客户端新增**/
	@SerializedName("callType")
	private String callType=CALLTYPE_VOICE;

    /**是否实时算分 false:否 true-是**/
    @SerializedName("realtimeCalculation")
    private boolean realtimeCalculation=true;

    /**是否双向交替测试*/
    @SerializedName("isAlternateTest")
    private boolean isAlternateTest = false;

    @SerializedName("isMultiTest")
    private boolean isMultiTest;

    @SerializedName("multiCycleDataName")
    private String multiCycleDataName;

    @SerializedName("cycleInterval")
    private int cycleInterval;

    @SerializedName("cycleTimes")
    private int cycleTimes;

    public boolean isMultiTest() {
        return isMultiTest;
    }

    public void setMultiTest(boolean multiTest) {
        isMultiTest = multiTest;
    }

    public String getMultiCycleDataName() {
        return multiCycleDataName;
    }

    public void setMultiCycleDataName(String multiCycleDataName) {
        this.multiCycleDataName = multiCycleDataName;
    }

    public int getCycleInterval() {
        return cycleInterval;
    }

    public void setCycleInterval(int cycleInterval) {
        this.cycleInterval = cycleInterval;
    }

    public int getCycleTimes() {
        return cycleTimes;
    }

    public void setCycleTimes(int cycleTimes) {
        this.cycleTimes = cycleTimes;
    }

    public boolean isAlternateTest() {
        return isAlternateTest;
    }

    public void setAlternateTest(boolean isAlternateTest){
        this.isAlternateTest = isAlternateTest;
    }

    public boolean isRealtimeCalculation() {
        return realtimeCalculation;
    }

    public void setRealtimeCalculation(boolean realtimeCalculation) {
        this.realtimeCalculation = realtimeCalculation;
    }

	public String getCalcMode() {
		return calcMode;
	}

	public void setCalcMode(String calcMode) {
		this.calcMode = calcMode;
	}

	public String getLowMOSThreshold() {
		return lowMOSThreshold;
	}

	public void setLowMOSThreshold(String lowMOSThreshold) {
		this.lowMOSThreshold = lowMOSThreshold;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public String getMosAlgorithm() {
		return mosAlgorithm;
	}

	public void setMosAlgorithm(String mosAlgorithm) {
		this.mosAlgorithm = mosAlgorithm;
	}

	public boolean getMosTest() {
		return mosTest;
	}

	public void setMosTest(boolean mosTest) {
		this.mosTest = mosTest;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public void parseXmlMtcTest(XmlPullParser parser) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("CalcMode")) {
					this.setCalcMode(parser.nextText());
				} else if (tagName.equals("LowMOSThreshold")) {
					this.setLowMOSThreshold(parser.nextText());
				} else if (tagName.equals("SampleType")) {
					this.setSampleType(parser.nextText());
				} else if (tagName.equals("MOSAlgorithm")) {
					this.setMosAlgorithm(parser.nextText());
				} else if (tagName.equals("MOSTest")) {
					this.setMosTest(stringToBool(parser.nextText()));
				} else if (tagName.equals("TestType")) {
					this.setTestType(parser.nextText());
				}else if (tagName.equals("CallType")) {
					this.setCallType(parser.nextText());
				}else if (tagName.equals("isAlternateTest")) {
                    this.setAlternateTest(stringToBool(parser.nextText()));
                }else if (tagName.equals("isMultiTest")) {
                    this.setMultiTest(stringToBool(parser.nextText()));
                }else if (tagName.equals("multiCycleDataName")) {
                    this.setMultiCycleDataName(parser.nextText());
                }else if (tagName.equals("cycleInterval")) {
                    this.setCycleInterval(stringToInt(parser.nextText()));
                }else if (tagName.equals("cycleTimes")) {
                    this.setCycleTimes(stringToInt(parser.nextText()));
                }
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("MTCTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "MTCTestConfig");
		this.writeTag(serializer, "CalcMode", this.calcMode);
		this.writeTag(serializer, "CallType", this.callType);
		this.writeTag(serializer, "LowMOSThreshold", this.lowMOSThreshold);
		this.writeTag(serializer, "SampleType", this.sampleType);
		this.writeTag(serializer, "MOSAlgorithm", this.mosAlgorithm);
		this.writeTag(serializer, "MOSTest", boolToText(this.mosTest));
		this.writeTag(serializer, "TestType", this.testType);
		this.writeTag(serializer, "isAlternateTest", this.isAlternateTest);
        this.writeTag(serializer, "isMultiTest", this.isMultiTest);
        this.writeTag(serializer, "multiCycleDataName", this.multiCycleDataName);
        this.writeTag(serializer, "cycleInterval", this.cycleInterval);
        this.writeTag(serializer, "cycleTimes", this.cycleTimes);
		serializer.endTag(null, "MTCTestConfig");

	}
}
