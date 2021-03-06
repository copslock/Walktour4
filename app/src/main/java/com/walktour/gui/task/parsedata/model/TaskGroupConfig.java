package com.walktour.gui.task.parsedata.model;

import com.walktour.Utils.FileUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
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
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
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

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ?????????????????????
 *
 * @author weirong.fan
 *
 */
public class TaskGroupConfig extends TaskBase implements Comparator<TaskGroupConfig> {

	/**
	 *
	 */
	private static final long serialVersionUID = -7009560937428371796L;
	/** 0=????????????????????? */
	public static final int GROUPSTATUS_0 = 0;
	/** 1=???????????????????????? **/
	public static final int GROUPSTATUS_1 = 1;
	/** 2=???????????????????????? **/
	public static final int GROUPSTATUS_2 = 2;

	/** ???????????? */
	private boolean isCheck = false;
	/** ????????????????????? */
	private int groupRepeatCount = 1;
	private String groupID = "1";
	private int groupSequence = 1;
	/** ??????????????????????????? */
	private TimeDuration timeDuration = new TimeDuration();;
	/** ????????? */
	private String groupName = "";

	/**
	 * ???????????????
	 */
	private int groupInterval;

	/** 0=???????????????????????? 1=??????????????????????????? 2=??????????????????????????? **/
	private int groupStatus = GROUPSTATUS_2;
	/** ?????????????????? */
	private List<TaskModel> tasks = new LinkedList<TaskModel>();

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public int getGroupRepeatCount() {
		return groupRepeatCount;
	}

	public void setGroupRepeatCount(int groupRepeatCount) {
		this.groupRepeatCount = groupRepeatCount;
	}

	public TimeDuration getTimeDuration() {
		return timeDuration;
	}

	public int getGroupInterval() {
		return groupInterval;
	}

	public void setGroupInterval(int groupInterval) {
		this.groupInterval = groupInterval;
	}

	public List<TaskModel> getTasks() {
		TaskModel compator = new TaskModel();
		Collections.sort(tasks, compator);
		return tasks;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public int getGroupSequence() {
		return groupSequence;
	}

	public void setGroupSequence(int groupSequence) {
		this.groupSequence = groupSequence;
	}



	public int getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(int groupStatus) {
		this.groupStatus = groupStatus;
	}

	/***
	 * ?????? TaskGroupConfig
	 *
	 * @param parser
	 * @throws Exception
	 */
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
				if (tagName.equals("GroupRepeatCount")) {
					this.setGroupRepeatCount(stringToInt(parser.nextText()));
				} else if (tagName.equals("GroupInterval")) {
					this.setGroupInterval(stringToInt(parser.nextText())*1000);
				} else if (tagName.equals("GroupName")) {
					this.setGroupName(parser.nextText());
				} else if (tagName.equals("GroupID")) {
					this.setGroupID(parser.nextText());
				} else if (tagName.equals("GroupSequence")) {
					this.setGroupSequence(stringToInt(parser.nextText()));
				} else if (tagName.equals("GroupStatus")) {
					this.setGroupStatus(stringToInt(parser.nextText()));
				} else if (tagName.equals("TimeDuration")) {
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						if (parser.getAttributeName(i).equals("IsCheck")) {
							timeDuration.setCheck(stringToBool(parser.getAttributeValue(i)));
						}
					}
					timeDuration.parseXml(parser);
				} else if (tagName.equals("Tasks")) {
					parseXmlTaskConfig(parser, tasks);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("TaskGroupConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	/**
	 * ??????????????????????????????
	 *
	 * @param parser ?????????
	 * @param tasks ????????????
	 * @throws Exception ??????
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
					map.clear();
					isCheck = false;
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						if (parser.getAttributeName(i).equals("IsCheck")) {
							isCheck = stringToBool(parser.getAttributeValue(i));
							break;
						}
					}
				} else if (tagName.equals("TaskType")) {
					String taskType = parser.nextText();
					if (taskType.equals("Idle") || taskType.equals("????????????")) {// Idle??????
						TaskEmptyModel model = new TaskEmptyModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals(WalkStruct.TaskType.InitiativeCall.getXmlTaskType())) { // MOC??????
						TaskInitiativeCallModel model = new TaskInitiativeCallModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					} else if (taskType.equals(WalkStruct.TaskType.PassivityCall.getXmlTaskType())) { // MTC??????
						TaskPassivityCallModel model = new TaskPassivityCallModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals(WalkStruct.TaskType.FTPUpload.getXmlTaskType())) { // FTP Upload??????
						TaskFtpModel model = new TaskFtpModel(WalkStruct.TaskType.FTPUpload.name());
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals(WalkStruct.TaskType.FTPDownload.getXmlTaskType())) { // FTP
						TaskFtpModel model = new TaskFtpModel(WalkStruct.TaskType.FTPDownload.name());
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals(WalkStruct.TaskType.Ping.getXmlTaskType())) { // Ping??????
						TaskPingModel model = new TaskPingModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("HTTP Page")) { // HTTP Page??????
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

					} else if (taskType.equals(WalkStruct.TaskType.HttpDownload.getXmlTaskType())) { // HTTP ??????
						TaskHttpPageModel model = new TaskHttpPageModel(WalkStruct.TaskType.HttpDownload.name());
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals(WalkStruct.TaskType.HttpUpload.getXmlTaskType())) { // HTTP
																	// Upload??????
						TaskHttpUploadModel model = new TaskHttpUploadModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("WAP Page")) { // WAP Page??????
						TaskWapPageModel model = new TaskWapPageModel(WalkStruct.TaskType.WapLogin.name());

						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
						if (model.getWapPageTestConfig().getMode().equalsIgnoreCase(TaskWapPageModel.MODE_LOGIN)) {
							model.setTaskType(WalkStruct.TaskType.WapLogin.name());
						} else if (model.getWapPageTestConfig().getMode()
								.equalsIgnoreCase(TaskWapPageModel.MODE_REFRESH)) {
							model.setTaskType(WalkStruct.TaskType.WapRefurbish.name());
						}

					} else if (taskType.equals("WAP Download")) { // WAP
						TaskWapPageModel model = new TaskWapPageModel(WalkStruct.TaskType.WapDownload.name());
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("Email Send")) { // Email Send??????
						TaskEmailSmtpModel model = new TaskEmailSmtpModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("Email Receive")) { // Email
																	// Receive??????
						TaskEmailPop3Model model = new TaskEmailPop3Model();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("Email Self")) { // Email
																			// Self-Reception??????
						TaskEmailSmtpPop3Model model = new TaskEmailSmtpPop3Model();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("Video Play")) { // Video Play??????
						TaskVideoPlayModel model = new TaskVideoPlayModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("Video Streaming")) { // Video
																		// Streaming??????
						TaskStreamModel model = new TaskStreamModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("Speedtest")) { // Speedtest??????
						TaskSpeedTestModel model = new TaskSpeedTestModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("Trace Route")) { // Trace
																// Route??????
						TaskTraceRouteModel model = new TaskTraceRouteModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("SMS Send")) { // SMS Send??????
						TaskSmsSendModel model = new TaskSmsSendModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("SMS Receive")) { // SMS
																	// Receive??????
						TaskSmsReceiveModel model = new TaskSmsReceiveModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("SMS Self")) { // SMS
																		// Self-Reception??????
						TaskSmsSendReceiveModel model = new TaskSmsSendReceiveModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("PBM")) { // PBM??????
						TaskPBMModel model = new TaskPBMModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("Multi RAB")) { // Multi RAB??????
						TaskRabModel model = new TaskRabModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals(WalkStruct.TaskType.MultiftpDownload.getXmlTaskType())) { // Multi
																		// FTP
																		// Download??????
						TaskMultiftpDownloadModel model = new TaskMultiftpDownloadModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals(WalkStruct.TaskType.MultiftpUpload.getXmlTaskType())) { // Multi
																		// FTP
																		// Upload??????
						TaskMultiftpUploadModel model = new TaskMultiftpUploadModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("Facebook")) { // Facebook
						TaskFaceBookModel model = new TaskFaceBookModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("WLAN AP ??????")) { // WLAN AP ??????
						TaskWlanApModel model = new TaskWlanApModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("WLAN Web Auth ??????")) { // WLAN
																		// Web
																		// Auth
																		// ??????
						TaskWlanLoginModel model = new TaskWlanLoginModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("WLAN ETE AUTH ??????")) { // WLAN
																		// ETE
																		// AUTH
																		// ??????
						TaskWlanEteAuthModel model = new TaskWlanEteAuthModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("Parallel Servie")||taskType.equals("Parallel Service")) { // ??????????????????
						TaskRabModel model = new TaskRabModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("Weibo")) { // weibo????????????
						TaskWeiBoModel model = new TaskWeiBoModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("PDP Active")) { // PDP
																// Active????????????
						TaskPdpModel model = new TaskPdpModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("MMS Send")) { // MMS Send????????????
						TaskMmsSendModel model = new TaskMmsSendModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("MMS Receive")) { // MMS
																	// Receive????????????
						TaskMmsReceiveModel model = new TaskMmsReceiveModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("MMS Self")) { // MMS Self????????????
						TaskMmsSendReceiveModel model = new TaskMmsSendReceiveModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("iPerf")) { // iPerf????????????
						TaskIperfModel model = new TaskIperfModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("DNS Lookup")) { // DNS
																// Lookup????????????DNS Lookup
						TaskDNSLookUpModel model = new TaskDNSLookUpModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);

					} else if (taskType.equals("Attach")) { // Attach????????????
						TaskAttachModel model = new TaskAttachModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
//					} else if(taskType.equals("WeChat")){ //??????????????????
//						TaskWeChatModel model = new TaskWeChatModel();
//						model.setCheck(isCheck);
//						model.parseXml(parser, tasks, map);
					}else if(taskType.equals("UDP")){ //UDP??????????????????
						TaskUDPModel model = new TaskUDPModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					}else if(taskType.equals("REBOOT")){ //REBOOT????????????
						TaskRebootModel model = new TaskRebootModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					}else if(taskType.equals("OpenSignal")){ //OpenSignal????????????
						TaskOpenSignalModel model = new TaskOpenSignalModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					}else if(taskType.equals("MultiHttpDownload")){ //MultiHttpDownload????????????
						TaskMultiHttpDownModel model = new TaskMultiHttpDownModel();
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					}else if (WalkStruct.TaskType.WeCallMoc.getXmlTaskType().equals(taskType)
                            ||WalkStruct.TaskType.WeCallMtc.getXmlTaskType().equals(taskType)){
                        TaskWeCallModel model = new TaskWeCallModel();
                        model.setTaskType(taskType);
                        model.setCheck(isCheck);
                        model.parseXml(parser, tasks, map);
                    } else if(WalkStruct.TaskType.WeChat.getXmlTaskType().equals(taskType)
							||WalkStruct.TaskType.QQ.getXmlTaskType().equals(taskType)
							||WalkStruct.TaskType.SinaWeibo.getXmlTaskType().equals(taskType)
							||WalkStruct.TaskType.WhatsAppChat.getXmlTaskType().equals(taskType)
							||WalkStruct.TaskType.WhatsAppMoc.getXmlTaskType().equals(taskType)
							||WalkStruct.TaskType.WhatsAppMtc.getXmlTaskType().equals(taskType)
							||WalkStruct.TaskType.SkypeChat.getXmlTaskType().equals(taskType)
                            ||WalkStruct.TaskType.Facebook_Ott.name().equals(taskType)
                            ||WalkStruct.TaskType.Instagram_Ott.name().equals(taskType)){ //MultipleAppTest??????????????????
						TaskMultipleAppTestModel model = new TaskMultipleAppTestModel();
						model.setTaskType(taskType);
						model.setCheck(isCheck);
						model.parseXml(parser, tasks, map);
					}
				} else {// TaskConfig????????????????????????????????????
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
				if (tagName.equals("Tasks")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		this.timeDuration.writeXml(serializer);
		this.writeTag(serializer, "GroupRepeatCount", this.groupRepeatCount);
		this.writeTag(serializer, "GroupID", this.groupID);
		this.writeTag(serializer, "GroupSequence", this.groupSequence);
		this.writeTag(serializer, "GroupStatus", this.groupStatus);
		this.writeTag(serializer, "GroupName", this.groupName);
		serializer.startTag(null, "Tasks");
		serializer.flush();
		File xmlFile=new File(TaskListDispose.getInstance().getFileName());
		StringBuffer sb=new StringBuffer();
		sb.setLength(0);
		for (TaskModel taskModel : tasks) {
			sb.setLength(0);
			sb.append(FileUtil.getStringFromFile(xmlFile));
			serializer.startTag(null, "TaskConfig");
			this.writeAttribute(serializer, "IsCheck", taskModel.isCheck());
			if (taskModel instanceof TaskEmptyModel) {// Idle??????
				TaskEmptyModel model = (TaskEmptyModel) (taskModel);
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskInitiativeCallModel) { // MOC??????
				TaskInitiativeCallModel model = (TaskInitiativeCallModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskPassivityCallModel) { // MTC??????
				TaskPassivityCallModel model = (TaskPassivityCallModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskFtpModel) { // FTP // Upload??????
				TaskFtpModel model = (TaskFtpModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskPingModel) { // Ping??????
				TaskPingModel model = (TaskPingModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskHttpPageModel) { // HTTP Page??????
				TaskHttpPageModel model = (TaskHttpPageModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskHttpUploadModel) { // HTTP
				// Upload??????
				TaskHttpUploadModel model = (TaskHttpUploadModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskWapPageModel) { // WAP Page??????
				TaskWapPageModel model = (TaskWapPageModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskEmailSmtpModel) { // Email
																	// Send??????
				TaskEmailSmtpModel model = (TaskEmailSmtpModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskEmailPop3Model) { // Email
				// Receive??????
				TaskEmailPop3Model model = (TaskEmailPop3Model) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskEmailSmtpPop3Model) { // Email
				// Self-Reception??????
				TaskEmailSmtpPop3Model model = (TaskEmailSmtpPop3Model) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskVideoPlayModel) { // Video
																	// Play??????
				TaskVideoPlayModel model = (TaskVideoPlayModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskStreamModel) { // Video
																// Streaming??????
				TaskStreamModel model = (TaskStreamModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskSpeedTestModel) { // Speedtest??????
				TaskSpeedTestModel model = (TaskSpeedTestModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskTraceRouteModel) { // Trace
				// Route??????
				TaskTraceRouteModel model = (TaskTraceRouteModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskSmsSendModel) { // SMS Send??????
				TaskSmsSendModel model = (TaskSmsSendModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskSmsReceiveModel) { // SMS
				// Receive??????
				TaskSmsReceiveModel model = (TaskSmsReceiveModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskSmsSendReceiveModel) { // SMS
				// Self-Reception??????
				TaskSmsSendReceiveModel model = (TaskSmsSendReceiveModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskPBMModel) { // PBM??????
				TaskPBMModel model = (TaskPBMModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskRabModel) { // Multi RAB??????
				TaskRabModel model = (TaskRabModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskMultiftpDownloadModel) { // Multi
				// FTP
				// Download??????
				TaskMultiftpDownloadModel model = (TaskMultiftpDownloadModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskMultiftpUploadModel) { // Multi
				// FTP
				// Upload??????
				TaskMultiftpUploadModel model = (TaskMultiftpUploadModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskFaceBookModel) { // Facebook
				TaskFaceBookModel model = (TaskFaceBookModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskWlanApModel) { // WLAN AP ??????
				TaskWlanApModel model = (TaskWlanApModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskWlanLoginModel) { // WLAN Web
																	// Auth ??????
				TaskWlanLoginModel model = (TaskWlanLoginModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskWlanEteAuthModel) { // WLAN ETE
																		// AUTH
																		// ??????
				TaskWlanEteAuthModel model = (TaskWlanEteAuthModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskRabModel) { // ??????????????????
				TaskRabModel model = (TaskRabModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskWeiBoModel) { // weibo????????????
				TaskWeiBoModel model = (TaskWeiBoModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskPdpModel) { // PDP
				// Active????????????
				TaskPdpModel model = (TaskPdpModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskMmsSendModel) { // MMS Send????????????
				TaskMmsSendModel model = (TaskMmsSendModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskMmsReceiveModel) { // MMS
				// Receive????????????
				TaskMmsReceiveModel model = (TaskMmsReceiveModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskMmsSendReceiveModel) { // MMS
																		// Self????????????
				TaskMmsSendReceiveModel model = (TaskMmsSendReceiveModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskIperfModel) {// iPerf????????????
				TaskIperfModel model = (TaskIperfModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskDNSLookUpModel) { // DNS Lookup????????????
				TaskDNSLookUpModel model = (TaskDNSLookUpModel) taskModel;
				model.writeXml(serializer);
			} else if (taskModel instanceof TaskAttachModel) { // Attach????????????
				TaskAttachModel model = (TaskAttachModel) taskModel;
				model.writeXml(serializer);
			} else if(taskModel instanceof TaskWeChatModel){ //??????
				TaskWeChatModel model = (TaskWeChatModel)taskModel;
				model.writeXml(serializer);
			}else if(taskModel instanceof TaskUDPModel){ //UDP??????
				TaskUDPModel model = (TaskUDPModel)taskModel;
				model.writeXml(serializer);
			}else if(taskModel instanceof TaskRebootModel){ //REBOOT??????
				TaskRebootModel model = (TaskRebootModel)taskModel;
				model.writeXml(serializer);
			}else if(taskModel instanceof TaskOpenSignalModel){ //Opensignal??????
				TaskOpenSignalModel model = (TaskOpenSignalModel)taskModel;
				model.writeXml(serializer);
			}else if(taskModel instanceof TaskMultiHttpDownModel){ //MultiHttpDownload??????
				TaskMultiHttpDownModel model = (TaskMultiHttpDownModel)taskModel;
				model.writeXml(serializer);
			}else if(taskModel instanceof TaskMultipleAppTestModel){ //MultipleAppTest??????
				TaskMultipleAppTestModel model = (TaskMultipleAppTestModel)taskModel;
				model.writeXml(serializer);
			}else if (taskModel instanceof TaskWeCallModel){
                TaskWeCallModel model = (TaskWeCallModel)taskModel;
                model.writeXml(serializer);
            }
			serializer.endTag(null, "TaskConfig");
			//???????????????????????????????????????,?????????xmlDescription??????
			serializer.flush();
			taskModel.setXmlDescription(FileUtil.getStringFromFile(xmlFile).substring(sb.length()));
		}
		xmlFile=null;
		sb=null;
		serializer.endTag(null, "Tasks");
	}

	/**
	 * ??????????????????
	 */
	@Override
	public int compare(TaskGroupConfig lhs, TaskGroupConfig rhs) {
		if (lhs.getGroupSequence() > rhs.getGroupSequence())
			return 1;
		else if (lhs.getGroupSequence() < rhs.getGroupSequence())
			return -1;
		return 0;
	}

}
