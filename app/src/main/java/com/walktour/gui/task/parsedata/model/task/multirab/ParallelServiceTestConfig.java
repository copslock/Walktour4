package com.walktour.gui.task.parsedata.model.task.multirab;

import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.base.TaskConfig;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.attach.TaskAttachModel;
import com.walktour.gui.task.parsedata.model.task.dnslookup.TaskDNSLookUpModel;
import com.walktour.gui.task.parsedata.model.task.email.receive.TaskEmailPop3Model;
import com.walktour.gui.task.parsedata.model.task.email.send.TaskEmailSmtpModel;
import com.walktour.gui.task.parsedata.model.task.email.sendreceive.TaskEmailSmtpPop3Model;
import com.walktour.gui.task.parsedata.model.task.facebook.TaskFaceBookModel;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;
import com.walktour.gui.task.parsedata.model.task.http.page.TaskHttpPageModel;
import com.walktour.gui.task.parsedata.model.task.http.upload.TaskHttpUploadModel;
import com.walktour.gui.task.parsedata.model.task.idle.TaskEmptyModel;
import com.walktour.gui.task.parsedata.model.task.iperf.TaskIperfModel;
import com.walktour.gui.task.parsedata.model.task.mms.receive.TaskMmsReceiveModel;
import com.walktour.gui.task.parsedata.model.task.mms.send.TaskMmsSendModel;
import com.walktour.gui.task.parsedata.model.task.mms.sendreceive.TaskMmsSendReceiveModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.model.task.mtc.TaskPassivityCallModel;
import com.walktour.gui.task.parsedata.model.task.multiftp.download.TaskMultiftpDownloadModel;
import com.walktour.gui.task.parsedata.model.task.multiftp.upload.TaskMultiftpUploadModel;
import com.walktour.gui.task.parsedata.model.task.multihttp.download.TaskMultiHttpDownModel;
import com.walktour.gui.task.parsedata.model.task.opensignal.TaskOpenSignalModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskWeCallModel;
import com.walktour.gui.task.parsedata.model.task.pbm.TaskPBMModel;
import com.walktour.gui.task.parsedata.model.task.pdpactive.TaskPdpModel;
import com.walktour.gui.task.parsedata.model.task.ping.TaskPingModel;
import com.walktour.gui.task.parsedata.model.task.reboot.TaskRebootModel;
import com.walktour.gui.task.parsedata.model.task.sms.receive.TaskSmsReceiveModel;
import com.walktour.gui.task.parsedata.model.task.sms.send.TaskSmsSendModel;
import com.walktour.gui.task.parsedata.model.task.sms.sendreceive.TaskSmsSendReceiveModel;
import com.walktour.gui.task.parsedata.model.task.speedtest.TaskSpeedTestModel;
import com.walktour.gui.task.parsedata.model.task.traceroute.TaskTraceRouteModel;
import com.walktour.gui.task.parsedata.model.task.udp.TaskUDPModel;
import com.walktour.gui.task.parsedata.model.task.videoplay.TaskVideoPlayModel;
import com.walktour.gui.task.parsedata.model.task.videostreaming.TaskStreamModel;
import com.walktour.gui.task.parsedata.model.task.wap.TaskWapPageModel;
import com.walktour.gui.task.parsedata.model.task.wechat.TaskWeChatModel;
import com.walktour.gui.task.parsedata.model.task.weibo.TaskWeiBoModel;
import com.walktour.gui.task.parsedata.model.task.wlan.ap.TaskWlanApModel;
import com.walktour.gui.task.parsedata.model.task.wlan.eteauth.TaskWlanEteAuthModel;
import com.walktour.gui.task.parsedata.model.task.wlan.login.TaskWlanLoginModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/***
 * 并发业务测试配置
 * 
 * @author weirong.fan
 * 
 */
public class ParallelServiceTestConfig extends TaskBase {
	private static final long serialVersionUID = 3729791653253034310L;
	/** 并发启动延时 0 无 */
	public static final int RAB_STATE_MODEL_NORMAL = 0;
	/** 并发启动延时 1 相对时间 */
	public static final int RAB_STATE_MODEL_RELATIVE_TIME = 1;
	/** 并发启动延时 2 绝对时间 */
	public static final int RAB_STATE_MODEL_ABSOLUTELY_TIME = 2;
	/** 并发启动延时 3 按服务状态 */
	public static final int RAB_STATE_MODEL_EVENT_STATE = 3;
	/** 启动方式:无 **/
	public static final String TESTSTARTMODE_Default = "Default";
	/** 启动方式:按相对时间 **/
	public static final String TESTSTARTMODE_By_Delay = "By Delay";
	/** 启动方式:按绝对时间 **/
	public static final String TESTSTARTMODE_By_Time = "By Time";
	/** 启动方式:按状态 **/
	public static final String TESTSTARTMODE_By_Status = "By Status";
	/** 启动状态:参考业务开始 **/
	public static final String REFERENCESTATUS_Start_Referencing_Task = "Start Referencing Task";
	/** 启动状态:参考业务成功 **/
	public static final String REFERENCESTATUS_Referencing_Task_Succeeded = "Referencing Task Succeeded";
	/** 启动状态:参考业务结束 **/
	public static final String REFERENCESTATUS_Referencing_Task_Completed = "Referencing Task Completed";
	/** 单次并发总超时密码 **/
	private int totalTimeout;
	/** 语音提前数据业务时长 **/
	private int voiceAheadData;
	/** 测试启动方式 ***/
	private String testStartMode = TESTSTARTMODE_Default;
	/** 参考任务 **/
	private String referenceTask = "";
	/** 启动状态 **/
	private String referenceStatus = REFERENCESTATUS_Start_Referencing_Task;
	private List<TaskModel> taskList = new LinkedList<TaskModel>();
	public int getTotalTimeout() {
		return totalTimeout;
	}
	public void setTotalTimeout(int totalTimeout) {
		this.totalTimeout = totalTimeout;
	}
	public int getVoiceAheadData() {
		return voiceAheadData;
	}
	public void setVoiceAheadData(int voiceAheadData) {
		this.voiceAheadData = voiceAheadData;
	}
	public List<TaskModel> getTaskList() {
		return taskList;
	}
	public String getTestStartMode() {
		return testStartMode;
	}
	public void setTestStartMode(String testStartMode) {
		this.testStartMode = testStartMode;
	}
	public String getReferenceTask() {
		return referenceTask;
	}
	public void setReferenceTask(String referenceTask) {
		this.referenceTask = referenceTask;
	}
	public String getReferenceStatus() {
		return referenceStatus;
	}
	public void setReferenceStatus(String referenceStatus) {
		this.referenceStatus = referenceStatus;
	}
	public int getRabStartMode() {
		if (this.getTestStartMode().equals(TESTSTARTMODE_Default)) {
			return RAB_STATE_MODEL_NORMAL;
		} else if (this.getTestStartMode().equals(TESTSTARTMODE_By_Delay)) {
			return RAB_STATE_MODEL_RELATIVE_TIME;
		} else if (this.getTestStartMode().equals(TESTSTARTMODE_By_Time)) {
			return RAB_STATE_MODEL_ABSOLUTELY_TIME;
		} else if (this.getTestStartMode().equals(TESTSTARTMODE_By_Status)) {
			return RAB_STATE_MODEL_EVENT_STATE;
		}
		return RAB_STATE_MODEL_NORMAL;
	}
	public void setRabStartMode(int rabStartMode) {
		switch (rabStartMode) {
		case RAB_STATE_MODEL_NORMAL:
			this.setTestStartMode(TESTSTARTMODE_Default);
			break;
		case RAB_STATE_MODEL_RELATIVE_TIME:
			this.setTestStartMode(TESTSTARTMODE_By_Delay);
			break;
		case RAB_STATE_MODEL_ABSOLUTELY_TIME:
			this.setTestStartMode(TESTSTARTMODE_By_Time);
			break;
		case RAB_STATE_MODEL_EVENT_STATE:
			this.setTestStartMode(TESTSTARTMODE_By_Status);
			break;
		default:
			this.setTestStartMode(TESTSTARTMODE_Default);
			break;
		}
	}
	public String getReferenceService() {
		return this.getReferenceTask();
	}
	public void setReferenceService(String referenceService) {
		this.setReferenceTask(referenceService);
	}
	public int getStartState() {
		if (this.getReferenceStatus().equals(REFERENCESTATUS_Start_Referencing_Task)) {
			return 0;
		} else if (this.getReferenceStatus().equals(REFERENCESTATUS_Referencing_Task_Succeeded)) {
			return 1;
		} else if (this.getReferenceStatus().equals(REFERENCESTATUS_Referencing_Task_Completed)) {
			return 2;
		}
		return 0;
	}
	public void setStartState(int startState) {
		switch (startState) {
		case 0:
			this.setReferenceStatus(REFERENCESTATUS_Start_Referencing_Task);
			break;
		case 1:
			this.setReferenceStatus(REFERENCESTATUS_Referencing_Task_Succeeded);
			break;
		case 2:
			this.setReferenceStatus(REFERENCESTATUS_Referencing_Task_Completed);
			break;
		default:
			this.setReferenceStatus(REFERENCESTATUS_Start_Referencing_Task);
			break;
		}
	}
	public void parseXml(XmlPullParser parser) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("TotalTimeout")) {
					this.setTotalTimeout(stringToInt(parser.nextText()) / 1000);
				} else if (tagName.equals("VoiceAheadData")) {
					this.setVoiceAheadData(stringToInt(parser.nextText()) / 1000);
				} else if (tagName.equals("ParallelControlRule")) {// 启动方式
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attName = parser.getAttributeName(i);
						if (attName.equals("TestStartMode")) {
							this.setTestStartMode(parser.getAttributeValue(i));
						}
					}
				} else if (tagName.equals("ReferenceTask")) {
					this.setReferenceTask(parser.nextText());
				} else if (tagName.equals("ReferenceStatus")) {
					this.setReferenceStatus(parser.nextText());
				}
				// 任务列表
				else if (tagName.equals("TaskList")) {
					parseXmlTaskConfig(parser, taskList);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("ParallelServiceTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	/***
	 * 解析测试任务计划配置
	 *
	 * @param parser 解析器
	 * @param tasks 测试任务
	 * @throws Exception 异常信息
	 */
	private void parseXmlTaskConfig(XmlPullParser parser, List<TaskModel> tasks) throws Exception {
		boolean isCheck = false;
		int eventType = parser.getEventType();
		String tagName = "";
		Map<String, String> map = new HashMap<String, String>();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("TaskConfig")) {
					isCheck = false;
					map.clear();
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						if (parser.getAttributeName(i).equals("IsCheck")) {
							isCheck = stringToBool(parser.getAttributeValue(i));
							break;
						}
					}
				} else if (tagName.equals("TaskType")) {
					String taskType = parser.nextText();
					if (getString(taskType).equalsIgnoreCase("Idle") || getString(taskType).equalsIgnoreCase("空闲测试")) {// Idle测试
						TaskEmptyModel model = new TaskEmptyModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("MOC")) { // MOC测试
						TaskInitiativeCallModel model = new TaskInitiativeCallModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("MTC")) { // MTC测试
						TaskPassivityCallModel model = new TaskPassivityCallModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (taskType.equals("FTP Upload")) { // FTP Upload测试
						TaskFtpModel model = new TaskFtpModel(WalkStruct.TaskType.FTPUpload.name());
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (taskType.equals("FTP Download")) { // FTP
						TaskFtpModel model = new TaskFtpModel(WalkStruct.TaskType.FTPDownload.name());
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("Ping")) { // Ping测试
						TaskPingModel model = new TaskPingModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("HTTPPage")) { // HTTP
						TaskHttpPageModel model = new TaskHttpPageModel(WalkStruct.TaskType.Http.name());
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
						switch (model.getHttpTestMode()) {
						case TaskHttpPageModel.LOGIN:
							model.setTaskType(WalkStruct.TaskType.Http.toString());
							break;
						case TaskHttpPageModel.REFRESH:
							model.setTaskType(WalkStruct.TaskType.HttpRefurbish.toString());
							break;
						default:
							break;
						}
					} else if (taskType.equals("HTTPDownload")) { // HTTP 下载
						TaskHttpPageModel model = new TaskHttpPageModel(WalkStruct.TaskType.HttpDownload.name());
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("HTTPUpload")) { // HTTP
						// Upload测试
						TaskHttpUploadModel model = new TaskHttpUploadModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("WAPPage")) { // WAP
																					// Page测试
						TaskWapPageModel model = new TaskWapPageModel(WalkStruct.TaskType.WapLogin.name());
						if (model.getWapPageTestConfig().getMode().equalsIgnoreCase(TaskWapPageModel.MODE_LOGIN)) {
							model.setTaskType(WalkStruct.TaskType.WapLogin.name());
						} else if (model.getWapPageTestConfig().getMode()
								.equalsIgnoreCase(TaskWapPageModel.MODE_REFRESH)) {
							model.setTaskType(WalkStruct.TaskType.WapRefurbish.name());
						}
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("WAPDownload")) { // WAP
						// Download测试
						TaskWapPageModel model = new TaskWapPageModel(WalkStruct.TaskType.WapDownload.name());
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("EmailSend")) { // Email
																					// Send测试
						TaskEmailSmtpModel model = new TaskEmailSmtpModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("EmailReceive")) { // Email
						// Receive测试
						TaskEmailPop3Model model = new TaskEmailPop3Model();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("EmailSelf")) { // Email
						// Self-Reception测试
						TaskEmailSmtpPop3Model model = new TaskEmailSmtpPop3Model();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("VideoPlay")) { // Video
																					// Play测试
						TaskVideoPlayModel model = new TaskVideoPlayModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("VideoStreaming")) { // Video
						// Streaming测试
						TaskStreamModel model = new TaskStreamModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("Speedtest")) { // Speedtest测试
						TaskSpeedTestModel model = new TaskSpeedTestModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("Traceroute")) { // Trace
						// Route测试
						TaskTraceRouteModel model = new TaskTraceRouteModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("SMSSend")) { // SMS
																					// Send测试
						TaskSmsSendModel model = new TaskSmsSendModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("SMSReceive")) { // SMS
						// Receive测试
						TaskSmsReceiveModel model = new TaskSmsReceiveModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("SMSSelf")) { // SMS
						// Self-Reception测试
						TaskSmsSendReceiveModel model = new TaskSmsSendReceiveModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("PBM")) { // PBM测试
						TaskPBMModel model = new TaskPBMModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("MultiRAB")) { // Multi
																					// RAB测试
						TaskRabModel model = new TaskRabModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("Multi-FTPDownload")) { // Multi
						// FTP
						// Download测试
						TaskMultiftpDownloadModel model = new TaskMultiftpDownloadModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("Multi-FTPUpload")) { // Multi
						// FTP
						// Upload测试
						TaskMultiftpUploadModel model = new TaskMultiftpUploadModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("Facebook")) { // Facebook
						TaskFaceBookModel model = new TaskFaceBookModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("WLANAP关联")) { // WLAN
																					// AP
																					// 关联
						TaskWlanApModel model = new TaskWlanApModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("WLANWebAuth认证")) { // WLAN
						// Web
						// Auth
						// 认证
						TaskWlanLoginModel model = new TaskWlanLoginModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equalsIgnoreCase("WLANETEAUTH认证")) { // WLAN
						// ETE
						// AUTH
						// 认证
						TaskWlanEteAuthModel model = new TaskWlanEteAuthModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equals("Weibo")) { // weibo业务测试
						TaskWeiBoModel model = new TaskWeiBoModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equals("PDPActive")) { // PDP
						// Active业务测试
						TaskWeiBoModel model = new TaskWeiBoModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equals("MMSSend")) { // MMS
																		// Send业务测试
						TaskMmsSendModel model = new TaskMmsSendModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equals("MMSReceive")) { // MMS
						// Receive业务测试
						TaskMmsReceiveModel model = new TaskMmsReceiveModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equals("MMSSelf")) { // MMS
																		// Self业务测试
						TaskMmsSendReceiveModel model = new TaskMmsSendReceiveModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equals("iPerf")) { // iPerf业务测试
						TaskIperfModel model = new TaskIperfModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equals("DNSLookup")) { // DNS
																			// Lookup业务测试
						TaskDNSLookUpModel model = new TaskDNSLookUpModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (getString(taskType).equals("Attach")) { // Attach业务测试
						TaskAttachModel model = new TaskAttachModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
//					} else if (getString(taskType).equals("WeChat")) { // 微信业务测试
//						TaskWeChatModel model = new TaskWeChatModel();
//						model.setCheck(isCheck);
//						model.parseXml(parser, tasks, map);
					}else if(taskType.equals("UDP")){ //UDP业务业务测试
						TaskUDPModel model = new TaskUDPModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					}else if(taskType.equals("REBOOT")){ //REBOOT业务测试
						TaskRebootModel model = new TaskRebootModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					}else if(taskType.equals("OpenSignal")){ //OpenSignal业务测试
						TaskOpenSignalModel model = new TaskOpenSignalModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					}else if(taskType.equals("MultiHttpDownload")){ //MultiHttpDownload业务测试
						TaskMultiHttpDownModel model = new TaskMultiHttpDownModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					}else if (taskType.equals(WalkStruct.TaskType.WeCallMoc.getXmlTaskType())
                            ||taskType.equals(WalkStruct.TaskType.WeCallMtc.getXmlTaskType())){
                        TaskWeCallModel model = new TaskWeCallModel();
                        model.setTaskType(taskType);
                        model.setCheck(isCheck);
                        model.parseXml(parser, tasks, map);
                    } else if(taskType.equals(WalkStruct.TaskType.WeChat.getXmlTaskType())
							||taskType.equals(WalkStruct.TaskType.QQ.getXmlTaskType())
							||taskType.equals(WalkStruct.TaskType.WhatsAppChat.getXmlTaskType())
							||taskType.equals(WalkStruct.TaskType.WhatsAppMoc.getXmlTaskType())
							||taskType.equals(WalkStruct.TaskType.WhatsAppMtc.getXmlTaskType())
							||taskType.equals(WalkStruct.TaskType.SinaWeibo.getXmlTaskType())
							||taskType.equals(WalkStruct.TaskType.SkypeChat.getXmlTaskType())
                            ||taskType.equals(WalkStruct.TaskType.Facebook_Ott.name())
                            ||taskType.equals(WalkStruct.TaskType.Instagram_Ott.name())){ //MultipleAppTest业务业务测试
						TaskMultipleAppTestModel model = new TaskMultipleAppTestModel();
						model.setTaskType(taskType);
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					}
				} else {// TaskConfig前几个属性乱序的特殊处理
					if (tagName.equals("TaskType")) {
						map.put("TaskType", parser.nextText());
					} else if (tagName.equals("TaskID")) {
						map.put("TaskID", parser.nextText());
					} else if (tagName.equals("TaskSequence")) {
						map.put("TaskSequence", parser.nextText());
					} else if (tagName.equals("TaskName")) {
						map.put("TaskName", parser.nextText());
					} else if (tagName.equals("TaskRepeatCount")) {
						map.put("TaskRepeatCount", parser.nextText());
					} else if (tagName.equals("Interval")) {
						map.put("Interval", parser.nextText());
					} else if (tagName.equals("Infinite")) {
						map.put("Infinite", parser.nextText());
					} else if (tagName.equals("TaskStatus")) {
						map.put("TaskStatus", parser.nextText());
					}else if (tagName.equals("ParallelStartAfterDelay")) {
						map.put("ParallelStartAfterDelay", parser.nextText());
					}else if (tagName.equals("ParallelStartAtTime")) {
						map.put("ParallelStartAtTime", parser.nextText());
					}else if (tagName.equals("ParallelStartConditon")) {
						for (int i = 0; i < parser.getAttributeCount(); i++) {
							String attName = parser.getAttributeName(i);
							if (attName.equals("IsAvailable")) {
								map.put("IsAvailable", parser.getAttributeValue(i)); 
							}
						} 
						
					}
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("TaskList")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "ParallelServiceTestConfig");
		this.writeTag(serializer, "TotalTimeout", this.totalTimeout * 1000);
		this.writeTag(serializer, "VoiceAheadData", this.voiceAheadData * 1000);
		serializer.startTag(null, "ParallelControlRule");
		this.writeAttribute(serializer, "TestStartMode", this.testStartMode);
		this.writeTag(serializer, "ReferenceTask", this.referenceTask);
		this.writeTag(serializer, "ReferenceStatus", this.referenceStatus);
		serializer.endTag(null, "ParallelControlRule");
		serializer.startTag(null, "TaskList");
		for (TaskConfig taskConfig : taskList) {
			serializer.startTag(null, "TaskConfig");
			serializer.attribute(null, "IsCheck", boolToText(taskConfig.isCheck()));
			if (taskConfig instanceof TaskEmptyModel) {// Idle测试
				TaskEmptyModel model = (TaskEmptyModel) (taskConfig);
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskInitiativeCallModel) { // MOC测试
				TaskInitiativeCallModel model = (TaskInitiativeCallModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskPassivityCallModel) { // MTC测试
				TaskPassivityCallModel model = (TaskPassivityCallModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskFtpModel) {
				// FTP
				// Download和
				// Upload测试
				TaskFtpModel model = (TaskFtpModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskPingModel) { // Ping测试
				TaskPingModel model = (TaskPingModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskHttpPageModel) { // HTTP Page测试
				TaskHttpPageModel model = (TaskHttpPageModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskHttpUploadModel) { // HTTP
				// Upload测试
				TaskHttpUploadModel model = (TaskHttpUploadModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskWapPageModel) { // WAP Page测试
				TaskWapPageModel model = (TaskWapPageModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskEmailSmtpModel) { // Email
																	// Send测试
				TaskEmailSmtpModel model = (TaskEmailSmtpModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskEmailPop3Model) { // Email
				// Receive测试
				TaskEmailPop3Model model = (TaskEmailPop3Model) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskEmailSmtpPop3Model) { // Email
				// Self-Reception测试
				TaskEmailSmtpPop3Model model = (TaskEmailSmtpPop3Model) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskVideoPlayModel) { // Video
																	// Play测试
				TaskVideoPlayModel model = (TaskVideoPlayModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskStreamModel) { // Video
																// Streaming测试
				TaskStreamModel model = (TaskStreamModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskSpeedTestModel) { // Speedtest测试
				TaskSpeedTestModel model = (TaskSpeedTestModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskTraceRouteModel) { // Trace
				// Route测试
				TaskTraceRouteModel model = (TaskTraceRouteModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskSmsSendModel) { // SMS Send测试
				TaskSmsSendModel model = (TaskSmsSendModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskSmsReceiveModel) { // SMS
				// Receive测试
				TaskSmsReceiveModel model = (TaskSmsReceiveModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskSmsSendReceiveModel) { // SMS
				// Self-Reception测试
				TaskSmsSendReceiveModel model = (TaskSmsSendReceiveModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskPBMModel) { // PBM测试
				TaskPBMModel model = (TaskPBMModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskRabModel) { // Multi RAB测试
				TaskRabModel model = (TaskRabModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskMultiftpDownloadModel) { // Multi
				// FTP
				// Download测试
				TaskMultiftpDownloadModel model = (TaskMultiftpDownloadModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskMultiftpUploadModel) { // Multi
				// FTP
				// Upload测试
				TaskMultiftpUploadModel model = (TaskMultiftpUploadModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskFaceBookModel) { // Facebook
				TaskFaceBookModel model = (TaskFaceBookModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskWlanApModel) { // WLAN AP 关联
				TaskWlanApModel model = (TaskWlanApModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskWlanLoginModel) { // WLAN Web
																	// Auth 认证
				TaskWlanLoginModel model = (TaskWlanLoginModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskWlanEteAuthModel) { // WLAN ETE
																		// AUTH
																		// 认证
				TaskWlanEteAuthModel model = (TaskWlanEteAuthModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskRabModel) { // 并发业务
				TaskRabModel model = (TaskRabModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskWeiBoModel) { // weibo业务测试
				TaskWeiBoModel model = (TaskWeiBoModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskPdpModel) { // PDP
				// Active业务测试
				TaskPdpModel model = (TaskPdpModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskMmsSendModel) { // MMS Send业务测试
				TaskMmsSendModel model = (TaskMmsSendModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskMmsReceiveModel) { // MMS
				// Receive业务测试
				TaskMmsReceiveModel model = (TaskMmsReceiveModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskMmsSendReceiveModel) { // MMS
																		// Self业务测试
				TaskMmsSendReceiveModel model = (TaskMmsSendReceiveModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskIperfModel) {// iPerf业务测试
				TaskIperfModel model = (TaskIperfModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskDNSLookUpModel) { // DNS
																	// Lookup业务测试
				TaskDNSLookUpModel model = (TaskDNSLookUpModel) taskConfig;
				model.writeXml(serializer);
			} else if (taskConfig instanceof TaskAttachModel) { // Attach 业务测试
				TaskAttachModel model = (TaskAttachModel) taskConfig;
				model.writeXml(serializer);
			} else if(taskConfig instanceof TaskWeChatModel){ //微信
				TaskWeChatModel model = (TaskWeChatModel)taskConfig;
				model.writeXml(serializer);
			}else if(taskConfig instanceof TaskUDPModel){ //UDP业务
				TaskUDPModel model = (TaskUDPModel)taskConfig;
				model.writeXml(serializer);
			}else if(taskConfig instanceof TaskRebootModel){ //REBOOT业务
				TaskRebootModel model = (TaskRebootModel)taskConfig;
				model.writeXml(serializer);
			}else if(taskConfig instanceof TaskOpenSignalModel){ //Opensignal业务
				TaskOpenSignalModel model = (TaskOpenSignalModel)taskConfig;
				model.writeXml(serializer);
			}else if(taskConfig instanceof TaskMultiHttpDownModel){ //MultiHttpDownload业务
				TaskMultiHttpDownModel model = (TaskMultiHttpDownModel)taskConfig;
				model.writeXml(serializer);
			}else if(taskConfig instanceof TaskMultipleAppTestModel){ //MultipleAppTest业务
				TaskMultipleAppTestModel model = (TaskMultipleAppTestModel)taskConfig;
				model.writeXml(serializer);
			}else if (taskConfig instanceof TaskWeCallModel){
                TaskWeCallModel model = (TaskWeCallModel)taskConfig;
                model.writeXml(serializer);
            }
			serializer.endTag(null, "TaskConfig");
		}
		serializer.endTag(null, "TaskList");
		serializer.endTag(null, "ParallelServiceTestConfig");
	}
	private String getString(String str) {
		if (null == str || "".equals(str))
			return "";
		return str.replaceAll(" ", "");
	}
}
