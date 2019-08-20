package com.walktour.gui.task.parsedata.model.task.moc;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class MOCTestConfig  extends TaskBase{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6968205048452217154L;
	public static final String TESTTYPE_MOC_ONLY = "MOC Only";
	public static final String TESTTYPE_MOBILE_MOBILE = "Mobile to Mobile Sync";
	public static final String TESTTYPE_MOBILE_LAND = "Mobile to Land";
	
	public static final String MOSAlgorithm_PESQ="PESQ";
	public static final String MOSAlgorithm_POLQA="POLQA";
	
	public static final String CALLTYPE_VOICE="Voice Call";
	public static final String CALLTYPE_VIDEO="Video Call";
	
	public static final String SampleType_NB_8k="NB 8k";
	public static final String SampleType_WB_16k="WB 16k";
	public static final String SampleType_SWB_48k="SWB 48k";
	
	public static final String CalcMode_NB="NB";
	public static final String CalcMode_SWB="SWB";

	@SerializedName("mtcDevicePort")
	private int mtcDevicePort = 0;
	/**是否进行长呼**/
	@SerializedName("longCall")
	private boolean longCall = false;

	@SerializedName("mosDeviceVer")
	private String mosDeviceVer = "";
	@SerializedName("sampleType")
	private String sampleType =SampleType_SWB_48k;

	@SerializedName("callingNumber")
	private String callingNumber = "";

	@SerializedName("callType")
	private String callType = CALLTYPE_VOICE;
	@SerializedName("calcMode")
	private String calcMode =CalcMode_NB;
	@SerializedName("lowMOSThreshold")
	private String lowMOSThreshold = "";
	@SerializedName("cdmaAMRRate")
	private String cdmaAMRRate = "";
	@SerializedName("umtsAMRRate")
	private String umtsAMRRate = "";
	@SerializedName("calledNumber")
	private String calledNumber = "";
	/**是否进行MOS测试**/
	@SerializedName("mosTest")
	private boolean mosTest = false;
	@SerializedName("connection")
	private int connection = 0;
	/** 持续时间 */
	@SerializedName("duration")
	private int duration = 0;
	@SerializedName("testType")
	private String testType = "";
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

	/**MOC 算法 **/
	@SerializedName("mosAlgorithm")
	private String mosAlgorithm = MOSAlgorithm_PESQ;
	@SerializedName("useRandomTimeDial")
	private UseRandomTimeDial useRandomTimeDial=new UseRandomTimeDial();
	@SerializedName("redialActionSetting")
	private RedialActionSetting redialActionSetting=new RedialActionSetting();


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

	public int getMtcDevicePort() {
		return mtcDevicePort;
	}

	public void setMtcDevicePort(int mtcDevicePort) {
		this.mtcDevicePort = mtcDevicePort;
	}

	public boolean isLongCall() {
		return longCall;
	}

	public void setLongCall(boolean longCall) {
		this.longCall = longCall;
	}

	public String getMosDeviceVer() {
		return mosDeviceVer;
	}

	public void setMosDeviceVer(String mosDeviceVer) {
		this.mosDeviceVer = mosDeviceVer;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public String getCallingNumber() {
		return callingNumber;
	}

	public void setCallingNumber(String callingNumber) {
		this.callingNumber = callingNumber;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
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

	public String getCdmaAMRRate() {
		return cdmaAMRRate;
	}

	public void setCdmaAMRRate(String cdmaAMRRate) {
		this.cdmaAMRRate = cdmaAMRRate;
	}

	public String getUmtsAMRRate() {
		return umtsAMRRate;
	}

	public void setUmtsAMRRate(String umtsAMRRate) {
		this.umtsAMRRate = umtsAMRRate;
	}

	public String getCalledNumber() {
		return calledNumber;
	}

	public void setCalledNumber(String calledNumber) {
		this.calledNumber = calledNumber;
	}

	public boolean isMosTest() {
		return mosTest;
	}

	public void setMosTest(boolean mosTest) {
		this.mosTest = mosTest;
	}

	public int getConnection() {
		return connection;
	}

	public void setConnection(int connection) {
		this.connection = connection;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public String getMosAlgorithm() {
		return mosAlgorithm;
	}

	public void setMosAlgorithm(String mosAlgorithm) {
		this.mosAlgorithm = mosAlgorithm;
	}

	public UseRandomTimeDial getUseRandomTimeDial() {
		return useRandomTimeDial;
	}

 

	public RedialActionSetting getRedialActionSetting() {
		return redialActionSetting;
	}

 

	public void parseXmlMocTest(XmlPullParser parser) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("MTCDevicePort")) {
					this.setMtcDevicePort(stringToInt(parser.nextText()));
				} else if (tagName.equals("LongCall")) {
					this.setLongCall(stringToBool(parser.nextText()));
				} else if (tagName.equals("MOSDeviceVer")) {
					this.setMosDeviceVer(parser.nextText());
				} else if (tagName.equals("SampleType")) {
					this.setSampleType(parser.nextText());
				} else if (tagName.equals("CallingNumber")) {
					this.setCallingNumber(parser.nextText());
				} else if (tagName.equals("CallType")) {
					this.setCallType(parser.nextText());
				} else if (tagName.equals("RealtimeCalculation")) {
					this.setRealtimeCalculation(stringToBool(parser.nextText()));
				}else if (tagName.equals("CalcMode")) {
					this.setCalcMode(parser.nextText());
				} else if (tagName.equals("LowMOSThreshold")) {
					this.setLowMOSThreshold(parser.nextText());
				} else if (tagName.equals("CDMAAMRRate")) {
					this.setCdmaAMRRate(parser.nextText());
				} else if (tagName.equals("UMTSAMRRate")) {
					this.setUmtsAMRRate(parser.nextText());
				} else if (tagName.equals("CalledNumber")) {
					this.setCalledNumber(parser.nextText());
				} else if (tagName.equals("MOSTest")) {
					this.setMosTest(stringToBool(parser.nextText()));
				} else if (tagName.equals("Connection")) {
					this.setConnection(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("Duration")) {
					this.setDuration(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("TestType")) {
					this.setTestType(parser.nextText());
				} else if (tagName.equals("MOSAlgorithm")) {
					this.setMosAlgorithm(parser.nextText());
				} else if (tagName.equals("UseRandomTimeDial")) { 
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						if (parser.getAttributeName(i).equals("IsAvailable")) {
							useRandomTimeDial.setAvailable(stringToBool(parser.getAttributeValue(i)));
						}
					}
					useRandomTimeDial.parseXmlMocTest(parser);
				} else if (tagName.equals("RedialActionSetting")) { 
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						if (parser.getAttributeName(i).equals("IsAvailable")) {
							redialActionSetting.setAvailable(stringToBool(parser.getAttributeValue(i)));
						}
					}
					redialActionSetting.parseXmlMocTest(parser);
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
				if (tagName.equals("MOCTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "MOCTestConfig");
		
		this.writeTag(serializer, "MTCDevicePort",this.mtcDevicePort);
		this.writeTag(serializer, "LongCall",this.longCall);
		this.writeTag(serializer, "MOSDeviceVer",this.mosDeviceVer);
		this.writeTag(serializer, "SampleType",this.sampleType);
		this.writeTag(serializer, "CallingNumber",this.callingNumber);
		this.writeTag(serializer, "CallType",this.callType);
		this.writeTag(serializer, "CalcMode",this.calcMode);
		this.writeTag(serializer, "RealtimeCalculation",this.realtimeCalculation);
		this.writeTag(serializer, "LowMOSThreshold",this.lowMOSThreshold);
		this.writeTag(serializer, "CDMAAMRRate",this.cdmaAMRRate);
		this.writeTag(serializer, "UMTSAMRRate",this.umtsAMRRate);
		this.writeTag(serializer, "CalledNumber",this.calledNumber);
		this.writeTag(serializer, "MOSTest",this.mosTest);
		this.writeTag(serializer, "Connection",this.connection*1000);
		this.writeTag(serializer, "Duration",this.duration*1000);
		this.writeTag(serializer, "TestType",this.testType);
		this.writeTag(serializer, "MOSAlgorithm",this.mosAlgorithm);
        this.writeTag(serializer, "isAlternateTest", this.isAlternateTest);
        this.writeTag(serializer, "isMultiTest", this.isMultiTest);
        this.writeTag(serializer, "multiCycleDataName", this.multiCycleDataName);
        this.writeTag(serializer, "cycleInterval", this.cycleInterval);
        this.writeTag(serializer, "cycleTimes", this.cycleTimes);
		if (null != useRandomTimeDial) {
			useRandomTimeDial.writeXml(serializer);
		}

		if (null != redialActionSetting) {
			redialActionSetting.writeXml(serializer);
		}

		serializer.endTag(null, "MOCTestConfig");
	}
}
